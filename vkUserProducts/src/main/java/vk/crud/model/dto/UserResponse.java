package vk.crud.model.dto;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ClientProductResponse> products;

    // Конструкторы
    public UserResponse() {}

    public UserResponse(Long id, String username, String email, Instant createdAt,
                        Instant updatedAt, List<ClientProductResponse> products) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.products = products;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<ClientProductResponse> getProducts() { return products; }
    public void setProducts(List<ClientProductResponse> products) { this.products = products; }

}