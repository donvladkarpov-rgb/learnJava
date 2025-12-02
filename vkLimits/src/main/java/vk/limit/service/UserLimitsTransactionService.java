package vk.limit.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import vk.limit.model.UserLimits;
import vk.limit.model.UserLimitsRollback;
import vk.limit.model.dto.TransactionInfo;
import vk.limit.repository.UserLimitsRepository;
import vk.limit.repository.UserLimitsRollbackRepository;
import vk.limit.web.exceptions.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserLimitsTransactionService {

    private final UserLimitsRepository userLimitsRepository;
    private final UserLimitsRollbackRepository rollbackRepository;
    private final TransactionService transactionService;

    // Хранилище для временных транзакций (в продакшене можно заменить на Redis/BД)
    //private final Map<String, TransactionInfo> pendingTransactions = new HashMap<>();

    public UserLimitsTransactionService(
            UserLimitsRepository userLimitsRepository,
            UserLimitsRollbackRepository rollbackRepository,
            TransactionService transactionService) {
        this.userLimitsRepository = userLimitsRepository;
        this.rollbackRepository = rollbackRepository;
        this.transactionService = transactionService;
    }

    // ========== НОВЫЕ МЕТОДЫ ==========

    /**
     * Получить текущий лимит по пользователю
     *
     * @param username имя пользователя
     * @return текущее значение лимита
     */
    public Long getCurrentLimit(String username) {
        UserLimits userLimits = getUserLimits(username);
        return userLimits.getUserLimit();
    }

    /**
     * Установить лимит по пользователю
     *
     * @param username имя пользователя
     * @param newLimit новое значение лимита
     */
    @Transactional
    public boolean setUserLimit(String username, Long newLimit) {
        validateAmount(newLimit);

        UserLimits userLimits = getUserLimits(username);

        // Сохраняем текущее значение в rollback перед изменением
        Long currentLimit = userLimits.getUserLimit();
        saveOrUpdateRollback(userLimits, currentLimit);

        // Устанавливаем новый лимит
        userLimits.setUserLimit(newLimit);
        userLimitsRepository.save(userLimits);
        return true;
    }

    /**
     * Установить один дефолтный лимит по всем пользователям
     *
     * @param defaultLimit дефолтное значение лимита
     */
    @Transactional
    public boolean setDefaultLimitForAllUsers(Long defaultLimit) {
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
        return true;
    }

    // ========== СУЩЕСТВУЮЩИЕ МЕТОДЫ (остаются без изменений) ==========

    /**
     * Списание части лимита (резервирование)
     *
     * @param username имя пользователя
     * @param amount   сумма для списания
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
        TransactionInfo transaction = new TransactionInfo(transactionId, username, amount, currentLimit,
                LocalDateTime.now(), "new");
        transactionService.saveTransaction(transaction);

        return transactionId;
    }

    /**
     * Подтверждение списания лимита (финализация транзакции)
     *
     * @param transactionId идентификатор транзакции
     */
    @Transactional
    public boolean confirmLimitDeduction(String transactionId) {
        TransactionInfo transaction = getValidTransaction(transactionId);

        // Проверяем актуальность лимита
        UserLimits userLimits = getUserLimits(transaction.getUsername());

        // Обновляем значение в rollback на текущее (списанное) значение
        Long currentLimit = userLimits.getUserLimit();
        updateRollback(userLimits, currentLimit);

        // Удаляем транзакцию из pending
        transactionService.deleteTransaction(transactionId);
        return true;
    }

    /**
     * Откат списания лимита (возврат средств)
     *
     * @param transactionId идентификатор транзакции
     */
    @Transactional
    public boolean rollbackLimitDeduction(String transactionId) {
        TransactionInfo transaction = getValidTransaction(transactionId);

        UserLimits userLimits = getUserLimits(transaction.getUsername());

        // Возвращаем сумму на лимит
        Long currentLimit = userLimits.getUserLimit();
        Long restoredLimit = currentLimit + transaction.getAmount();
        userLimits.setUserLimit(restoredLimit);
        userLimitsRepository.save(userLimits);

        // Обновляем rollback с новым значением
        updateRollback(userLimits, currentLimit);

        // Удаляем транзакцию из pending
        transactionService.deleteTransaction(transactionId);
        return true;
    }

    /**
     * Проверка статуса транзакции
     *
     * @param transactionId идентификатор транзакции
     * @return статус транзакции
     */
    public String getTransactionStatus(String transactionId) {
        TransactionInfo transaction = transactionService.getTransaction(transactionId);


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
    public boolean cleanupExpiredTransactions() {
        transactionService.getAll().forEach(entry -> {
            if (entry.isExpired()) {
                try {
                    // Автоматический откат просроченной транзакции
                    rollbackExpiredTransaction(entry.getTransactionId(), entry);
                } catch (Exception e) {
                    // Логируем ошибку, но продолжаем очистку
                }
            }
        });
        return true;
    }

    // Вспомогательные методы

    private UserLimits getUserLimits(String username) {
        return userLimitsRepository.findByUserLimitsUser(username).orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
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

    private TransactionInfo getValidTransaction(String transactionId) {
        TransactionInfo transaction = transactionService.getTransaction(transactionId);

        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found: " + transactionId);
        }

        if (transaction.isExpired()) {
            transactionService.deleteTransaction(transactionId);
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

    private void rollbackExpiredTransaction(String transactionId, TransactionInfo transaction) {
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
     *
     * @param transactionId идентификатор транзакции
     * @return информация о транзакции или null если не найдена
     */
    public TransactionInfo getTransactionInfo(String transactionId) {
        TransactionInfo transaction = transactionService.getTransaction(transactionId);
        if (transaction == null) throw new ResourceNotFoundException("Транзакция не найдена!");
        return transaction;
    }

}