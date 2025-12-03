package vk.limit.web;

import org.springframework.web.bind.annotation.*;
import vk.limit.model.dto.TransactionInfo;
import vk.limit.service.UserLimitsTransactionService;

@RestController
@RequestMapping("/api/v1/limits/transactions")
public class UserLimitsTransactionController {

    private final UserLimitsTransactionService transactionService;

    public UserLimitsTransactionController(UserLimitsTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Получить текущий лимит пользователя
     */
    @GetMapping("/{username}/current-limit")
    public Long getCurrentLimit(@PathVariable String username) {
        return transactionService.getCurrentLimit(username);
    }

    /**
     * Установить лимит пользователя
     */
    @PutMapping("/{username}/limit")
    public Boolean setUserLimit(
            @PathVariable String username,
            @RequestParam Long newLimit) {
        return transactionService.setUserLimit(username, newLimit);
    }

    /**
     * Установить дефолтный лимит для всех пользователей
     */
    @PutMapping("/default-limit")
    public Boolean setDefaultLimitForAllUsers(
            @RequestParam Long defaultLimit) {
        return transactionService.setDefaultLimitForAllUsers(defaultLimit);
    }

    // ========== СУЩЕСТВУЮЩИЕ МЕТОДЫ ==========

    @PostMapping("/reserve")
    public String reserveLimit(
            @RequestParam String username,
            @RequestParam Long amount) {
        return transactionService.reserveLimit(username, amount);
    }

    @PostMapping("/confirm")
    public Boolean confirmLimitDeduction(
            @RequestParam String transactionId) {
        return transactionService.confirmLimitDeduction(transactionId);
    }

    @PostMapping("/rollback")
    public Boolean rollbackLimitDeduction(
            @RequestParam String transactionId) {
        return transactionService.rollbackLimitDeduction(transactionId);
    }

    @GetMapping("/status/{transactionId}")
    public TransactionInfo getTransactionStatus(
            @PathVariable String transactionId) {
        return transactionService.getTransactionInfo(transactionId);
    }

    @PostMapping("/cleanup")
    public Boolean cleanupExpiredTransactions() {
        return transactionService.cleanupExpiredTransactions();
    }
}