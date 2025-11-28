package vk.crud.model.dto.payments;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO для баланса клиента")
public class ClientBalanceDto {

    @Schema(description = "Идентификатор записи", example = "1")
    private Long id;

    @Schema(description = "Идентификатор клиента", example = "CLIENT_12345")
    private String clientId;

    @Schema(description = "Номер карты", example = "4111111111111111")
    private String cardNumber;

    @Schema(description = "Баланс средств", example = "1500.75")
    private BigDecimal balance;

    // Конструкторы
    public ClientBalanceDto() {
    }

    public ClientBalanceDto(Long id, String clientId, String cardNumber, BigDecimal balance) {
        this.id = id;
        this.clientId = clientId;
        this.cardNumber = cardNumber;
        this.balance = balance;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    // toString
    @Override
    public String toString() {
        return "ClientBalanceDto{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", balance=" + balance +
                '}';
    }

}