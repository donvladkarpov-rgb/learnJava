package vk.crud.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "client_balance")
public class ClientBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_balance_seq")
    @SequenceGenerator(name = "client_balance_seq", sequenceName = "client_balance_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "client_id", nullable = false, unique = true, length = 50)
    private String clientId;

    @Column(name = "card_number", nullable = false, unique = true, length = 20)
    private String cardNumber;

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    // Конструкторы
    public ClientBalance() {
    }

    public ClientBalance(String clientId, String cardNumber, BigDecimal balance) {
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

    // Бизнес-методы
    public void addToBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void subtractFromBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    // equals и hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientBalance)) return false;

        ClientBalance that = (ClientBalance) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    // toString
    @Override
    public String toString() {
        return "ClientBalance{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", balance=" + balance +
                '}';
    }
}