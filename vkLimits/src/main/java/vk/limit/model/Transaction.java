package vk.limit.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "transaction_id", length = 36)
    private String transactionId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "previous_limit", nullable = false)
    private Long previousLimit;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    // Обязательный конструктор без параметров (требуется JPA)
    public Transaction() {}

    // Конструктор для удобства
    public Transaction(String transactionId, String username, Long amount,
                       Long previousLimit, LocalDateTime createdAt, String status) {
        this.transactionId = transactionId;
        this.username = username;
        this.amount = amount;
        this.previousLimit = previousLimit;
        this.createdAt = createdAt;
        this.status = status;
    }

    // Геттеры
    public String getTransactionId() { return transactionId; }
    public String getUsername() { return username; }
    public Long getAmount() { return amount; }
    public Long getPreviousLimit() { return previousLimit; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }

    // Метод isExpired() не нужен в Entity (это логика DTO / сервиса)
}
