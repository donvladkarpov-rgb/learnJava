package vk.crud.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ClientProductDto {

    private Long id;
    private Integer quantity;
    private String accountNumber;
    private BigDecimal balance;
    private String productType;
    private Long userId; // вместо полного User — только ID (чтобы избежать циклических ссылок)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Конструктор по умолчанию
    public ClientProductDto() {}

    // Полный конструктор
    public ClientProductDto(Long id, String accountNumber, BigDecimal balance, String productType, Long userId, LocalDateTime createdAt, LocalDateTime updatedAt, Integer quantity) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.productType = productType;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.quantity = quantity;
    }

    // Геттеры
    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getProductType() {
        return productType;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ClientProductDto{" +
                "id=" + id +
                ", quantity='" + quantity + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", productType='" + productType + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}