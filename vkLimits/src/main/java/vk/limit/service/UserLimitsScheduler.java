package vk.limit.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserLimitsScheduler {

    private final UserLimitsTransactionService transactionService;

    @Value("${user.limits.scheduler.cron.rollback-and-reset:0 0 3 * * *}") // Каждый день в 3:00 по умолчанию
    private String rollbackAndResetCron;

    @Value("${user.limits.default.value:100000}")
    private Long defaultLimitValue;

    public UserLimitsScheduler(UserLimitsTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Шедуллер для отката всех оставшихся транзакций и установки дефолтного лимита
     * Крон выражение настраивается через проперти user.limits.scheduler.cron.rollback-and-reset
     * Дефолтное значение лимита настраивается через проперти user.limits.default.value
     */
    @Scheduled(cron = "${user.limits.scheduler.cron.rollback-and-reset:0 0 3 * * *}")
    public void rollbackAllTransactionsAndSetDefaultLimits() {
        try {
            // 1. Откатываем все просроченные транзакции через cleanup
            transactionService.cleanupExpiredTransactions();

            // 2. Устанавливаем дефолтный лимит для всех пользователей
            transactionService.setDefaultLimitForAllUsers(defaultLimitValue);

        } catch (Exception e) {
            // Логирование ошибки (в реальном проекте используйте логгер)
            System.err.println("Error in scheduler rollbackAllTransactionsAndSetDefaultLimits: " + e.getMessage());
        }
    }
}