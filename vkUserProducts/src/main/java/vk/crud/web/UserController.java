package vk.crud.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vk.crud.model.dto.*;
import vk.crud.model.ClientProduct;
import vk.crud.model.User;
import vk.crud.model.dto.mappers.DtoMapper;
import vk.crud.service.ClientProductService;
import vk.crud.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "API для управления пользователями и их продуктами")
public class UserController {

    private final UserService userService;
    private final ClientProductService clientProductService;

    public UserController(UserService userService, ClientProductService clientProductService) {
        this.userService = userService;
        this.clientProductService = clientProductService;
    }

    // ========== CRUD операции для User ==========

    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public List<UserResponse> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return DtoMapper.toUserResponseList(users);
    }

    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(u -> ResponseEntity.ok(DtoMapper.toUserResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать нового пользователя")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Parameter(description = "Данные пользователя", required = true)
            @Valid @RequestBody UserRequest userRequest) {
        User user = DtoMapper.toUserEntity(userRequest);
        User savedUser = userService.saveUser(user);
        UserResponse response = DtoMapper.toUserResponse(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Обновить пользователя")
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId,
            @Parameter(description = "Обновленные данные пользователя", required = true)
            @Valid @RequestBody UserRequest userRequest) {

        User userDetails = DtoMapper.toUserEntity(userRequest);
        Optional<User> updatedUser = userService.updateUser(userId, userDetails);
        return updatedUser.map(u -> ResponseEntity.ok(DtoMapper.toUserResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId) {
        if (userService.getUserById(userId).isPresent()) {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ========== CRUD операции для ClientProduct ==========

    @Operation(summary = "Получить все продукты пользователя")
    @GetMapping("/{userId}/products")
    public List<ClientProductResponse> getUserProducts(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId) {
        List<ClientProduct> products = clientProductService.getProductsByUserId(userId);
        return DtoMapper.toClientProductResponseList(products);
    }

    @Operation(summary = "Получить конкретный продукт пользователя")
    @GetMapping("/{userId}/products/{productId}")
    public ResponseEntity<ClientProductResponse> getUserProduct(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId,
            @Parameter(description = "ID продукта", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("productId") Long productId) {

        Optional<ClientProduct> product = clientProductService.getProductByIdAndUserId(productId, userId);
        return product.map(p -> ResponseEntity.ok(DtoMapper.toClientProductResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать новый продукт для пользователя")
    @PostMapping("/{userId}/products")
    public ResponseEntity<ClientProductResponse> createUserProduct(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId,
            @Parameter(description = "Данные продукта", required = true)
            @Valid @RequestBody ClientProductRequest productRequest) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        ClientProduct product = DtoMapper.toClientProductEntity(productRequest);
        product.setUser(user);

        ClientProduct savedProduct = clientProductService.saveProduct(product);
        ClientProductResponse response = DtoMapper.toClientProductResponse(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Обновить продукт пользователя")
    @PutMapping("/{userId}/products/{productId}")
    public ResponseEntity<ClientProductResponse> updateUserProduct(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId,
            @Parameter(description = "ID продукта", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("productId") Long productId,
            @Parameter(description = "Обновленные данные продукта", required = true)
            @Valid @RequestBody ClientProductRequest productRequest) {

        ClientProduct productDetails = DtoMapper.toClientProductEntity(productRequest);
        Optional<ClientProduct> updatedProduct = clientProductService.updateProduct(productId, userId, productDetails);
        return updatedProduct.map(p -> ResponseEntity.ok(DtoMapper.toClientProductResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Удалить продукт пользователя")
    @DeleteMapping("/{userId}/products/{productId}")
    public ResponseEntity<Void> deleteUserProduct(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId,
            @Parameter(description = "ID продукта", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("productId") Long productId) {

        if (clientProductService.getProductByIdAndUserId(productId, userId).isPresent()) {
            clientProductService.deleteProduct(productId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ========== Дополнительные операции ==========

    @Operation(summary = "Получить пользователя по email")
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(description = "Email пользователя", example = "user@example.com", required = true, in = ParameterIn.PATH)
            @PathVariable("email") String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(u -> ResponseEntity.ok(DtoMapper.toUserResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить пользователя по username")
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(
            @Parameter(description = "Username пользователя", example = "john_doe", required = true, in = ParameterIn.PATH)
            @PathVariable("username") String username) {
        Optional<User> user = userService.getUserByUsername(username);
        return user.map(u -> ResponseEntity.ok(DtoMapper.toUserResponse(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить все продукты")
    @GetMapping("/products/all")
    public List<ClientProductResponse> getAllProducts() {
        List<ClientProduct> products = clientProductService.getAllProducts();
        return DtoMapper.toClientProductResponseList(products);
    }

    @Operation(summary = "Получить продукты по типу")
    @GetMapping("/products/type/{productType}")
    public List<ClientProductResponse> getProductsByType(
            @Parameter(description = "Тип продукта", example = "SAVINGS", required = true, in = ParameterIn.PATH)
            @PathVariable("productType") String productType) {
        List<ClientProduct> products = clientProductService.getProductsByType(productType);
        return DtoMapper.toClientProductResponseList(products);
    }

    // ========== Дополнительные методы для удобства ==========

    @Operation(summary = "Создать пользователя с продуктами")
    @PostMapping("/with-products")
    public ResponseEntity<UserResponse> createUserWithProducts(
            @Parameter(description = "Данные пользователя с продуктами", required = true)
            @Valid @RequestBody UserWithProductsRequest request) {

        // Создаем пользователя
        User user = DtoMapper.toUserEntity(request.getUser());
        User savedUser = userService.saveUser(user);

        // Создаем продукты если они есть
        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            for (ClientProductRequest productRequest : request.getProducts()) {
                ClientProduct product = DtoMapper.toClientProductEntity(productRequest);
                product.setUser(savedUser);
                clientProductService.saveProduct(product);
            }
        }

        // Обновляем пользователя чтобы получить продукты
        User updatedUser = userService.getUserById(savedUser.getId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve created user"));

        UserResponse response = DtoMapper.toUserResponse(updatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}