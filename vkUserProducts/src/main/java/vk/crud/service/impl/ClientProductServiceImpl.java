package vk.crud.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vk.crud.model.ClientProduct;
import vk.crud.repo.ClientProductRepository;
import vk.crud.service.ClientProductService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientProductServiceImpl implements ClientProductService {

    private final ClientProductRepository clientProductRepository;

    @Autowired
    public ClientProductServiceImpl(ClientProductRepository clientProductRepository) {
        this.clientProductRepository = clientProductRepository;
    }

    @Override
    public List<ClientProduct> getAllProducts() {
        return clientProductRepository.findAll();
    }

    @Override
    public List<ClientProduct> getProductsByUserId(Long userId) {
        return clientProductRepository.findByUserId(userId);
    }

    @Override
    public Optional<ClientProduct> getProductByIdAndUserId(Long productId, Long userId) {
        return clientProductRepository.findByIdAndUserId(productId, userId);
    }

    @Override
    public List<ClientProduct> getProductsByType(String productType) {
        return clientProductRepository.findByProductType(productType);
    }

    @Override
    public ClientProduct saveProduct(ClientProduct product) {
        return clientProductRepository.save(product);
    }

    @Override
    public Optional<ClientProduct> updateProduct(Long productId, Long userId, ClientProduct productDetails) {
        return clientProductRepository.findByIdAndUserId(productId, userId)
                .map(existingProduct -> {
                    // Обновляем только те поля, которые должны быть изменяемыми
                    if (productDetails.getAccountNumber() != null) {
                        existingProduct.setAccountNumber(productDetails.getAccountNumber());
                    }
                    if (productDetails.getBalance() != null) {
                        existingProduct.setBalance(productDetails.getBalance());
                    }
                    if (productDetails.getProductType() != null) {
                        existingProduct.setProductType(productDetails.getProductType());
                    }
                    // User не обновляем, так как продукт привязан к конкретному пользователю
                    return clientProductRepository.save(existingProduct);
                });
    }

    @Override
    public void deleteProduct(Long id) {
        clientProductRepository.deleteById(id);
    }

    // Дополнительные методы для специфичной бизнес-логики
    public List<ClientProduct> getProductsByTypeAndUserId(String productType, Long userId) {
        return clientProductRepository.findByProductTypeAndUserId(productType, userId);
    }

    public Optional<ClientProduct> getProductByAccountNumber(String accountNumber) {
        return clientProductRepository.findByAccountNumber(accountNumber);
    }

    public boolean productExistsByAccountNumber(String accountNumber) {
        return clientProductRepository.existsByAccountNumber(accountNumber);
    }

    // Старые методы для обратной совместимости (можно удалить со временем)
    public ClientProduct createProduct(ClientProduct product) {
        return saveProduct(product);
    }

    public ClientProduct updateProduct(ClientProduct product) {
        return saveProduct(product);
    }

    public Optional<ClientProduct> getProductById(Long id) {
        return clientProductRepository.findById(id);
    }
}