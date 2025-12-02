package vk.crud.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vk.crud.client.payments.ClientBalanceRestClient;
import vk.crud.model.dto.*;
import vk.crud.service.BayService;
import vk.crud.service.ClientProductService;
import vk.crud.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "API для управления пользователями и их продуктами")
public class UserController {

    private final UserService userService;
    private final ClientProductService clientProductService;
    private final BayService bayService;

    public UserController(UserService userService,
                          ClientProductService clientProductService,
                          BayService bayService) {
        this.userService = userService;
        this.clientProductService = clientProductService;
        this.bayService = bayService;
    }

    // ========== User CRUD ==========

    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{userId}")
    public UserResponse getUserById(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @Operation(summary = "Создать нового пользователя")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Parameter(description = "Данные пользователя", required = true)
            @Valid @RequestBody UserRequest userRequest) {
        UserResponse response = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Обновить пользователя")
    @PutMapping("/{userId}")
    public UserResponse updateUser(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId,
            @Parameter(description = "Обновленные данные пользователя", required = true)
            @Valid @RequestBody UserRequest userRequest) {
        return userService.updateUser(userId, userRequest);
    }

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // ========== ClientProduct CRUD ==========

    @Operation(summary = "Получить все продукты пользователя")
    @GetMapping("/{userId}/products")
    public List<ClientProductResponse> getUserProducts(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId) {
        return clientProductService.getProductsByUserId(userId);
    }

    @Operation(summary = "Получить конкретный продукт пользователя")
    @GetMapping("/{userId}/products/{productId}")
    public ClientProductResponse getUserProduct(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId,
            @Parameter(description = "ID продукта", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("productId") Long productId) {
        return clientProductService.getProductByIdAndUserId(productId, userId);
    }

    @Operation(summary = "Создать новый продукт для пользователя")
    @PostMapping("/{userId}/products")
    public ResponseEntity<ClientProductResponse> createUserProduct(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId,
            @Parameter(description = "Данные продукта", required = true)
            @Valid @RequestBody ClientProductRequest productRequest) {
        ClientProductResponse response = clientProductService.createProduct(userId, productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Обновить продукт пользователя")
    @PutMapping("/{userId}/products/{productId}")
    public ClientProductResponse updateUserProduct(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId,
            @Parameter(description = "ID продукта", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("productId") Long productId,
            @Parameter(description = "Обновленные данные продукта", required = true)
            @Valid @RequestBody ClientProductRequest productRequest) {
        return clientProductService.updateProduct(productId, userId, productRequest);
    }

    @Operation(summary = "Удалить продукт пользователя")
    @DeleteMapping("/{userId}/products/{productId}")
    public ResponseEntity<Void> deleteUserProduct(
            @Parameter(description = "ID пользователя", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("userId") Long userId,
            @Parameter(description = "ID продукта", example = "1", required = true, in = ParameterIn.PATH)
            @PathVariable("productId") Long productId) {
        clientProductService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    // ========== Дополнительные операции ==========

    @Operation(summary = "Получить пользователя по email")
    @GetMapping("/email/{email}")
    public UserResponse getUserByEmail(
            @Parameter(description = "Email пользователя", example = "user@example.com", required = true, in = ParameterIn.PATH)
            @PathVariable("email") String email) {
        return userService.getUserByEmail(email);
    }

    @Operation(summary = "Получить пользователя по username")
    @GetMapping("/username/{username}")
    public UserResponse getUserByUsername(
            @Parameter(description = "Username пользователя", example = "john_doe", required = true, in = ParameterIn.PATH)
            @PathVariable("username") String username) {
        return userService.getUserByUsername(username);
    }

    @Operation(summary = "Получить все продукты")
    @GetMapping("/products/all")
    public List<ClientProductResponse> getAllProducts() {
        return clientProductService.getAllProducts();
    }

    @Operation(summary = "Получить продукты по типу")
    @GetMapping("/products/type/{productType}")
    public List<ClientProductResponse> getProductsByType(
            @Parameter(description = "Тип продукта", example = "SAVINGS", required = true, in = ParameterIn.PATH)
            @PathVariable("productType") String productType) {
        return clientProductService.getProductsByType(productType);
    }

    @Operation(summary = "Создать пользователя с продуктами")
    @PostMapping("/with-products")
    public ResponseEntity<UserResponse> createUserWithProducts(
            @Parameter(description = "Данные пользователя с продуктами", required = true)
            @Valid @RequestBody UserWithProductsRequest request) {

        UserResponse userResponse = userService.createUser(request.getUser());

        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            for (ClientProductRequest productReq : request.getProducts()) {
                clientProductService.createProduct(userResponse.getId(), productReq);
            }
        }

        // Перечитываем пользователя с продуктами (если lazy loading)
        UserResponse updatedUser = userService.getUserById(userResponse.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedUser);
    }

    @Operation(summary = "Купить продукты")
    @PostMapping("/bay-products")
    public ResponseEntity<UserDto> bayProduct(
            @Parameter(description = "Данные пользователя с продуктами", required = true)
            @Valid @RequestBody UserDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bayService.bay(request));
    }


}