package vk.crud.model.dto;

import jakarta.validation.Valid;
import java.util.List;

public class UserWithProductsRequest {

    @Valid
    private UserRequest user;

    @Valid
    private List<ClientProductRequest> products;

    // Конструкторы
    public UserWithProductsRequest() {}

    public UserWithProductsRequest(UserRequest user, List<ClientProductRequest> products) {
        this.user = user;
        this.products = products;
    }

    // Геттеры и сеттеры
    public UserRequest getUser() { return user; }
    public void setUser(UserRequest user) { this.user = user; }

    public List<ClientProductRequest> getProducts() { return products; }
    public void setProducts(List<ClientProductRequest> products) { this.products = products; }
}