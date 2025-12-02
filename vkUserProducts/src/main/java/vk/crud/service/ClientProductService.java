package vk.crud.service;

import vk.crud.model.dto.ClientProductRequest;
import vk.crud.model.dto.ClientProductResponse;

import java.util.List;

public interface ClientProductService {
    List<ClientProductResponse> getAllProducts();
    List<ClientProductResponse> getProductsByUserId(Long userId);
    ClientProductResponse getProductByIdAndUserId(Long productId, Long userId);
    List<ClientProductResponse> getProductsByType(String productType);
    ClientProductResponse createProduct(Long userId, ClientProductRequest productRequest);
    ClientProductResponse updateProduct(Long productId, Long userId, ClientProductRequest productRequest);
    void deleteProduct(Long id);

    // Дополнительные методы
    List<ClientProductResponse> getProductsByTypeAndUserId(String productType, Long userId);
    ClientProductResponse getProductByAccountNumber(String accountNumber);
    boolean productExistsByAccountNumber(String accountNumber);
}