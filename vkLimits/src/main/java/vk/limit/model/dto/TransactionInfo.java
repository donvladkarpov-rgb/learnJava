package vk.limit.model.dto;

import java.time.LocalDateTime;

/**
 * Класс для возврата информации о транзакции
 */
public class TransactionInfo {
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

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(createdAt);
    }
}
