package vk.crud.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vk.crud.model.Product;
import vk.crud.repo.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products API", description = "CRUD операции для управления продуктами")
public class ProductController {

    private ProductRepository productRepository;

    public ProductController(@Autowired ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    @Operation(summary = "Получить все продукты", description = "Возвращает список всех продуктов")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить продукт по ID", description = "Возвращает продукт по указанному идентификатору")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "Идентификатор продукта")
            @PathVariable("id") Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Получить продукты по категории", description = "Возвращает список продуктов указанной категории")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @Parameter(description = "Категория продукта")
            @PathVariable("category") String category) {
        List<Product> products = productRepository.findByCategory(category);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @Operation(summary = "Создать новый продукт", description = "Создает новый продукт с указанными данными")
    public ResponseEntity<Product> createProduct(
            @Parameter(description = "Данные продукта")
            @RequestBody Product product) {
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить продукт", description = "Обновляет данные продукта по указанному идентификатору")
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "Идентификатор продукта")
            @PathVariable("id") Long id,
            @Parameter(description = "Обновленные данные продукта")
            @RequestBody Product productDetails) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = optionalProduct.get();
        product.setName(productDetails.getName());
        product.setCategory(productDetails.getCategory());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());

        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить продукт", description = "Удаляет продукт по указанному идентификатору")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Идентификатор продукта")
            @PathVariable("id") Long id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Обновить остаток продукта", description = "Обновляет количество остатка продукта")
    public ResponseEntity<Product> updateStock(
            @Parameter(description = "Идентификатор продукта")
            @PathVariable("id") Long id,
            @Parameter(description = "Новое значение остатка")
            @RequestParam("stock") Integer stock) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = optionalProduct.get();
        product.setStock(stock);

        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}/price")
    @Operation(summary = "Обновить цену продукта", description = "Обновляет цену продукта")
    public ResponseEntity<Product> updatePrice(
            @Parameter(description = "Идентификатор продукта")
            @PathVariable("id") Long id,
            @Parameter(description = "Новое значение цены")
            @RequestParam("price") BigDecimal price) {
        Optional<Product> optionalProduct = productRepository.findById(id);

        if (optionalProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = optionalProduct.get();
        product.setPrice(price);

        Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(updatedProduct);
    }
}