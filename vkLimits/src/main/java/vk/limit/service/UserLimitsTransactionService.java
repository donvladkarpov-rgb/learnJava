package vk.limit.service;

import vk.limit.model.UserLimits;
import vk.limit.model.UserLimitsRollback;
import vk.limit.repository.UserLimitsRepository;
import vk.limit.repository.UserLimitsRollbackRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserLimitsTransactionService {

    private final UserLimitsRepository userLimitsRepository;
    private final UserLimitsRollbackRepository rollbackRepository;

    // Хранилище для временных транзакций (в продакшене можно заменить на Redis/BД)
    private final Map<String, TransactionRecord> pendingTransactions = new HashMap<>();

    public UserLimitsTransactionService(
            UserLimitsRepository userLimitsRepository,
            UserLimitsRollbackRepository rollbackRepository) {
        this.userLimitsRepository = userLimitsRepository;
        this.rollbackRepository = rollbackRepository;
    }

    // ========== НОВЫЕ МЕТОДЫ ==========

    /**
     * Получить текущий лимит по пользователю
     * @param username имя пользователя
     * @return текущее значение лимита
     */
    public Long getCurrentLimit(String username) {
        UserLimits userLimits = getUserLimits(username);
        return userLimits.getUserLimit();
    }

    /**
     * Установить лимит по пользователю
     * @param username имя пользователя
     * @param newLimit новое значение лимита
     */
    @Transactional
    public void setUserLimit(String username, Long newLimit) {
        validateAmount(newLimit);

        UserLimits userLimits = getUserLimits(username);

        // Сохраняем текущее значение в rollback перед изменением
        Long currentLimit = userLimits.getUserLimit();
        saveOrUpdateRollback(userLimits, currentLimit);

        // Устанавливаем новый лимит
        userLimits.setUserLimit(newLimit);
        userLimitsRepository.save(userLimits);
    }

    /**
     * Установить один дефолтный лимит по всем пользователям
     * @param defaultLimit дефолтное значение лимита
     */
    @Transactional
    public void setDefaultLimitForAllUsers(Long defaultLimit) {
        validateAmount(defaultLimit);

        List<UserLimits> allUsers = userLimitsRepository.findAll();

        for (UserLimits userLimits : allUsers) {
            // Сохраняем текущее значение в rollback перед изменением
            Long currentLimit = userLimits.getUserLimit();

            // Сохраняем в rollback
            Optional<UserLimitsRollback> existingRollback =
                    rollbackRepository.findById(userLimits.getUserLimitsId());

            if (existingRollback.isPresent()) {
                UserLimitsRollback rollback = existingRollback.get();
                rollback.setUserLimit(currentLimit);
                rollbackRepository.save(rollback);
            } else {
                UserLimitsRollback rollback = new UserLimitsRollback();
                rollback.setUserLimits(userLimits);
                rollback.setUserLimit(currentLimit);
                rollbackRepository.save(rollback);
            }

            // Устанавливаем новый лимит
            userLimits.setUserLimit(defaultLimit);
            userLimitsRepository.save(userLimits);
        }
    }

    // ========== СУЩЕСТВУЮЩИЕ МЕТОДЫ (остаются без изменений) ==========

    /**
     * Внутренний класс для хранения информации о транзакции
     */
    private static class TransactionRecord {
        private final String username;
        private final Long amount;
        private final Long previousLimit;
        private final LocalDateTime createdAt;

        public TransactionRecord(String username, Long amount, Long previousLimit) {
            this.username = username;
            this.amount = amount;
            this.previousLimit = previousLimit;
            this.createdAt = LocalDateTime.now();
        }

        public String getUsername() {
            return username;
        }

        public Long getAmount() {
            return amount;
        }

        public Long getPreviousLimit() {
            return previousLimit;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public boolean isExpired() {
            return createdAt.isBefore(LocalDateTime.now().minusHours(24));
        }
    }

    /**
     * Списание части лимита (резервирование)
     * @param username имя пользователя
     * @param amount сумма для списания
     * @return идентификатор транзакции для подтверждения или отката
     */
    @Transactional
    public String reserveLimit(String username, Long amount) {
        validateAmount(amount);

        UserLimits userLimits = getUserLimits(username);
        validateSufficientLimit(userLimits, amount);

        // Сохраняем текущее значение лимита для возможного отката
        Long currentLimit = userLimits.getUserLimit();
        saveOrUpdateRollback(userLimits, currentLimit);

        // Резервируем сумму (уменьшаем доступный лимит)
        Long newLimit = currentLimit - amount;
        userLimits.setUserLimit(newLimit);
        userLimitsRepository.save(userLimits);

        // Создаем запись о транзакции
        String transactionId = UUID.randomUUID().toString();
        TransactionRecord transaction = new TransactionRecord(username, amount, currentLimit);
        pendingTransactions.put(transactionId, transaction);

        return transactionId;
    }

    /**
     * Подтверждение списания лимита (финализация транзакции)
     * @param transactionId идентификатор транзакции
     */
    @Transactional
    public void confirmLimitDeduction(String transactionId) {
        TransactionRecord transaction = getValidTransaction(transactionId);

        // Проверяем актуальность лимита
        UserLimits userLimits = getUserLimits(transaction.getUsername());

        // Обновляем значение в rollback на текущее (списанное) значение
        Long currentLimit = userLimits.getUserLimit();
        updateRollback(userLimits, currentLimit);

        // Удаляем транзакцию из pending
        pendingTransactions.remove(transactionId);
    }

    /**
     * Откат списания лимита (возврат средств)
     * @param transactionId идентификатор транзакции
     */
    @Transactional
    public void rollbackLimitDeduction(String transactionId) {
        TransactionRecord transaction = getValidTransaction(transactionId);

        UserLimits userLimits = getUserLimits(transaction.getUsername());

        // Возвращаем сумму на лимит
        Long currentLimit = userLimits.getUserLimit();
        Long restoredLimit = currentLimit + transaction.getAmount();
        userLimits.setUserLimit(restoredLimit);
        userLimitsRepository.save(userLimits);

        // Обновляем rollback с новым значением
        updateRollback(userLimits, currentLimit);

        // Удаляем транзакцию из pending
        pendingTransactions.remove(transactionId);
    }

    /**
     * Проверка статуса транзакции
     * @param transactionId идентификатор транзакции
     * @return статус транзакции
     */
    public String getTransactionStatus(String transactionId) {
        TransactionRecord transaction = pendingTransactions.get(transactionId);

        if (transaction == null) {
            return "COMPLETED_OR_NOT_FOUND";
        }

        if (transaction.isExpired()) {
            return "EXPIRED";
        }

        return "PENDING";
    }

    /**
     * Очистка просроченных транзакций
     */
    @Transactional
    public void cleanupExpiredTransactions() {
        pendingTransactions.entrySet().removeIf(entry -> {
            TransactionRecord transaction = entry.getValue();

            if (transaction.isExpired()) {
                try {
                    // Автоматический откат просроченной транзакции
                    rollbackExpiredTransaction(entry.getKey(), transaction);
                } catch (Exception e) {
                    // Логируем ошибку, но продолжаем очистку
                }
                return true;
            }
            return false;
        });
    }

    // Вспомогательные методы

    private UserLimits getUserLimits(String username) {
        Optional<UserLimits> userLimitsOpt = userLimitsRepository.findByUserLimitsUser(username);
        if (!userLimitsOpt.isPresent()) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        return userLimitsOpt.get();
    }

    private void validateAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive: " + amount);
        }
    }

    private void validateSufficientLimit(UserLimits userLimits, Long amount) {
        if (userLimits.getUserLimit() < amount) {
            throw new IllegalStateException(
                    String.format("Insufficient limit for user %s: %d < %d",
                            userLimits.getUserLimitsUser(), userLimits.getUserLimit(), amount)
            );
        }
    }

    private TransactionRecord getValidTransaction(String transactionId) {
        TransactionRecord transaction = pendingTransactions.get(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found: " + transactionId);
        }

        if (transaction.isExpired()) {
            pendingTransactions.remove(transactionId);
            throw new IllegalStateException("Transaction expired: " + transactionId);
        }

        return transaction;
    }

    private void saveOrUpdateRollback(UserLimits userLimits, Long limitValue) {
        Optional<UserLimitsRollback> existingRollback =
                rollbackRepository.findById(userLimits.getUserLimitsId());

        if (existingRollback.isPresent()) {
            // Обновляем существующий rollback
            UserLimitsRollback rollback = existingRollback.get();
            rollback.setUserLimit(limitValue);
            rollbackRepository.save(rollback);
        } else {
            // Создаем новый rollback
            UserLimitsRollback rollback = new UserLimitsRollback();
            rollback.setUserLimits(userLimits);
            rollback.setUserLimit(limitValue);
            rollbackRepository.save(rollback);
        }
    }

    private void updateRollback(UserLimits userLimits, Long limitValue) {
        rollbackRepository.findById(userLimits.getUserLimitsId())
                .ifPresent(rollback -> {
                    rollback.setUserLimit(limitValue);
                    rollbackRepository.save(rollback);
                });
    }

    private void rollbackExpiredTransaction(String transactionId, TransactionRecord transaction) {
        try {
            UserLimits userLimits = getUserLimits(transaction.getUsername());

            // Возвращаем сумму
            Long currentLimit = userLimits.getUserLimit();
            Long restoredLimit = currentLimit + transaction.getAmount();
            userLimits.setUserLimit(restoredLimit);
            userLimitsRepository.save(userLimits);

            updateRollback(userLimits, currentLimit);
        } catch (Exception e) {
            // Продолжаем выполнение даже при ошибке отката
        }
    }

    /**
     * Получение информации о транзакции
     * @param transactionId идентификатор транзакции
     * @return информация о транзакции или null если не найдена
     */
    public TransactionInfo getTransactionInfo(String transactionId) {
        TransactionRecord transaction = pendingTransactions.get(transactionId);
        if (transaction == null) {
            return null;
        }

        return new TransactionInfo(
                transactionId,
                transaction.getUsername(),
                transaction.getAmount(),
                transaction.getPreviousLimit(),
                transaction.getCreatedAt(),
                getTransactionStatus(transactionId)
        );
    }

    /**
     * Класс для возврата информации о транзакции
     */
    public static class TransactionInfo {
        private final String transactionId;
        private final String username;
        private final Long amount;
        private final Long previousLimit;
        private final LocalDateTime createdAt;
        private final String status;

        public TransactionInfo(String transactionId, String username, Long amount,
                               Long previousLimit, LocalDateTime createdAt, String status) {
            this.transactionId = transactionId;
            this.username = username;
            this.amount = amount;
            this.previousLimit = previousLimit;
            this.createdAt = createdAt;
            this.status = status;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getUsername() {
            return username;
        }

        public Long getAmount() {
            return amount;
        }

        public Long getPreviousLimit() {
            return previousLimit;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public String getStatus() {
            return status;
        }
    }
}