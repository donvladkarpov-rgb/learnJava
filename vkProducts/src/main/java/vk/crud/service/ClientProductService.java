package vk.crud.service;

import vk.crud.model.ClientProduct;
import java.util.List;
import java.util.Optional;

public interface ClientProductService {
    List<ClientProduct> getAllProducts();
    List<ClientProduct> getProductsByUserId(Long userId);
    Optional<ClientProduct> getProductByIdAndUserId(Long productId, Long userId);
    List<ClientProduct> getProductsByType(String productType);
    ClientProduct saveProduct(ClientProduct product);
    Optional<ClientProduct> updateProduct(Long productId, Long userId, ClientProduct productDetails);
    void deleteProduct(Long id);
}