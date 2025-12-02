package vk.crud.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vk.crud.model.dto.ProductDto;
import vk.crud.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products API", description = "CRUD операции для управления продуктами")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Получить все продукты", description = "Возвращает список всех продуктов")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить продукт по ID", description = "Возвращает продукт по указанному идентификатору")
    public ResponseEntity<ProductDto> getProductById(
            @Parameter(description = "Идентификатор продукта")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Получить продукты по категории", description = "Возвращает список продуктов указанной категории")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(
            @Parameter(description = "Категория продукта")
            @PathVariable("category") String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @PostMapping
    @Operation(summary = "Создать новый продукт", description = "Создает новый продукт с указанными данными")
    public ResponseEntity<ProductDto> createProduct(
            @Parameter(description = "Данные продукта")
            @RequestBody ProductDto productDto) {
        ProductDto savedProduct = productService.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить продукт", description = "Обновляет данные продукта по указанному идентификатору")
    public ResponseEntity<ProductDto> updateProduct(
            @Parameter(description = "Идентификатор продукта")
            @PathVariable("id") Long id,
            @Parameter(description = "Обновленные данные продукта")
            @RequestBody ProductDto productDetails) {
        ProductDto updatedProduct = productService.updateProduct(id, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить продукт", description = "Удаляет продукт по указанному идентификатору")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Идентификатор продукта")
            @PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Обновить остаток продукта", description = "Обновляет количество остатка продукта")
    public ResponseEntity<ProductDto> updateStock(
            @Parameter(description = "Идентификатор продукта")
            @PathVariable("id") Long id,
            @Parameter(description = "Новое значение остатка")
            @RequestParam("stock") Integer stock) {
        ProductDto updatedProduct = productService.updateStock(id, stock);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}/price")
    @Operation(summary = "Обновить цену продукта", description = "Обновляет цену продукта")
    public ResponseEntity<ProductDto> updatePrice(
            @Parameter(description = "Идентификатор продукта")
            @PathVariable("id") Long id,
            @Parameter(description = "Новое значение цены")
            @RequestParam("price") BigDecimal price) {
        ProductDto updatedProduct = productService.updatePrice(id, price);
        return ResponseEntity.ok(updatedProduct);
    }

}