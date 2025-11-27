package vk.crud.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ClientProductRequest {

    @NotBlank(message = "Account number is required")
    @Size(min = 5, max = 50, message = "Account number must be between 5 and 50 characters")
    private String accountNumber;

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", message = "Balance must be greater than or equal to 0")
    private BigDecimal balance;

    @NotBlank(message = "Product type is required")
    @Size(max = 20, message = "Product type must not exceed 20 characters")
    private String productType;

    // Конструкторы
    public ClientProductRequest() {}

    public ClientProductRequest(String accountNumber, BigDecimal balance, String productType) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.productType = productType;
    }

    // Геттеры и сеттеры
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
}