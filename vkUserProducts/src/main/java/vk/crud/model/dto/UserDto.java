package vk.crud.model.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UserDto {

    private Long id;
    private String username;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ClientProductDto> products = new ArrayList<>();

    // Конструктор по умолчанию
    public UserDto() {}

    // Полный конструктор
    public UserDto(Long id, String username, String email, Instant createdAt, Instant updatedAt, List<ClientProductDto> products) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.products = products != null ? products : new ArrayList<>();
    }

    // Геттеры
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<ClientProductDto> getProducts() {
        return products;
    }

    // Сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setProducts(List<ClientProductDto> products) {
        this.products = products != null ? products : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", products=" + products.size() + " items" +
                '}';
    }
}