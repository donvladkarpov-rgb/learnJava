package vk.crud.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_seq_gen"
    )
    @SequenceGenerator(
            name = "user_seq_gen",
            sequenceName = "user_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 250)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Instant updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ClientProduct> products = new ArrayList<>();

    // Конструктор по умолчанию (ОБЯЗАТЕЛЬНО для JPA)
    public User() {
    }

    // Конструктор для создания новых объектов
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    // Конструктор для создания новых объектов
    public User(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
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

    // Сеттеры (только для тех полей, которые могут изменяться)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ClientProduct> getProducts() {
        return products;
    }

    public void setProducts(List<ClientProduct> products) {
        this.products = products;
    }

    // Хелпер-методы
    public void addProduct(ClientProduct product) {
        products.add(product);
        product.setUser(this);
    }

    public void removeProduct(ClientProduct product) {
        products.remove(product);
        product.setUser(null);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }

}