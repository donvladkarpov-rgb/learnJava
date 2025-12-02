package vk.crud.service;

import vk.crud.model.dto.ProductDto;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(Long id);
    List<ProductDto> getProductsByCategory(String category);
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(Long id, ProductDto productDetails);
    void deleteProduct(Long id);
    ProductDto updateStock(Long id, Integer stock);
    ProductDto updatePrice(Long id, BigDecimal price);
}