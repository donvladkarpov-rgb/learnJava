package vk.limit.web;

import vk.limit.service.UserLimitsTransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/limits/transactions")
public class UserLimitsTransactionController {

    private final UserLimitsTransactionService transactionService;

    public UserLimitsTransactionController(UserLimitsTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Получить текущий лимит пользователя
     */
    @GetMapping("/{username}/current-limit")
    public Map<String, Object> getCurrentLimit(@PathVariable String username) {
        Long currentLimit = transactionService.getCurrentLimit(username);

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("currentLimit", currentLimit);
        response.put("status", "SUCCESS");
        response.put("message", "Current limit retrieved successfully");

        return response;
    }

    /**
     * Установить лимит пользователя
     */
    @PutMapping("/{username}/limit")
    public Map<String, Object> setUserLimit(
            @PathVariable String username,
            @RequestParam Long newLimit) {

        transactionService.setUserLimit(username, newLimit);

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("newLimit", newLimit);
        response.put("status", "SUCCESS");
        response.put("message", "User limit updated successfully");

        return response;
    }

    /**
     * Установить дефолтный лимит для всех пользователей
     */
    @PutMapping("/default-limit")
    public Map<String, Object> setDefaultLimitForAllUsers(
            @RequestParam Long defaultLimit) {

        transactionService.setDefaultLimitForAllUsers(defaultLimit);

        Map<String, Object> response = new HashMap<>();
        response.put("defaultLimit", defaultLimit);
        response.put("status", "SUCCESS");
        response.put("message", "Default limit set for all users successfully");

        return response;
    }

    // ========== СУЩЕСТВУЮЩИЕ МЕТОДЫ ==========

    @PostMapping("/reserve")
    public Map<String, Object> reserveLimit(
            @RequestParam String username,
            @RequestParam Long amount) {

        String transactionId = transactionService.reserveLimit(username, amount);

        Map<String, Object> response = new HashMap<>();
        response.put("transactionId", transactionId);
        response.put("status", "RESERVED");
        response.put("username", username);
        response.put("amount", amount);
        response.put("message", "Limit reserved successfully");

        return response;
    }

    @PostMapping("/confirm")
    public Map<String, Object> confirmLimitDeduction(
            @RequestParam String transactionId) {

        transactionService.confirmLimitDeduction(transactionId);

        Map<String, Object> response = new HashMap<>();
        response.put("transactionId", transactionId);
        response.put("status", "CONFIRMED");
        response.put("message", "Transaction confirmed successfully");

        return response;
    }

    @PostMapping("/rollback")
    public Map<String, Object> rollbackLimitDeduction(
            @RequestParam String transactionId) {

        transactionService.rollbackLimitDeduction(transactionId);

        Map<String, Object> response = new HashMap<>();
        response.put("transactionId", transactionId);
        response.put("status", "ROLLED_BACK");
        response.put("message", "Transaction rolled back successfully");

        return response;
    }

    @GetMapping("/status/{transactionId}")
    public Map<String, Object> getTransactionStatus(
            @PathVariable String transactionId) {

        UserLimitsTransactionService.TransactionInfo transactionInfo =
                transactionService.getTransactionInfo(transactionId);

        Map<String, Object> response = new HashMap<>();

        if (transactionInfo == null) {
            response.put("transactionId", transactionId);
            response.put("status", "NOT_FOUND");
            response.put("message", "Transaction not found");
        } else {
            response.put("transactionId", transactionInfo.getTransactionId());
            response.put("username", transactionInfo.getUsername());
            response.put("amount", transactionInfo.getAmount());
            response.put("previousLimit", transactionInfo.getPreviousLimit());
            response.put("createdAt", transactionInfo.getCreatedAt());
            response.put("status", transactionInfo.getStatus());

            String message;
            switch (transactionInfo.getStatus()) {
                case "PENDING":
                    message = "Transaction is pending confirmation";
                    break;
                case "EXPIRED":
                    message = "Transaction has expired";
                    break;
                default:
                    message = "Transaction status: " + transactionInfo.getStatus();
            }
            response.put("message", message);
        }

        return response;
    }

    @PostMapping("/cleanup")
    public Map<String, Object> cleanupExpiredTransactions() {

        transactionService.cleanupExpiredTransactions();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "Expired transactions cleanup completed");

        return response;
    }
}