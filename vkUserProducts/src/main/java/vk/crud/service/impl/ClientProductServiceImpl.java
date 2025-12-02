package vk.crud.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vk.crud.model.ClientProduct;
import vk.crud.model.User;
import vk.crud.model.dto.ClientProductRequest;
import vk.crud.model.dto.ClientProductResponse;
import vk.crud.model.dto.UserResponse;
import vk.crud.repo.ClientProductRepository;
import vk.crud.service.ClientProductService;
import vk.crud.service.UserService;
import vk.crud.model.dto.mappers.DtoMapper;
import vk.crud.web.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientProductServiceImpl implements ClientProductService {

    private final ClientProductRepository clientProductRepository;
    private final UserService userService;

    public ClientProductServiceImpl(ClientProductRepository clientProductRepository, UserService userService) {
        this.clientProductRepository = clientProductRepository;
        this.userService = userService;
    }

    @Override
    public List<ClientProductResponse> getAllProducts() {
        return clientProductRepository.findAll().stream()
                .map(DtoMapper::toClientProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientProductResponse> getProductsByUserId(Long userId) {
        // Проверяем, что пользователь существует
        userService.getUserById(userId); // бросит исключение, если нет

        return clientProductRepository.findByUserId(userId).stream()
                .map(DtoMapper::toClientProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClientProductResponse getProductByIdAndUserId(Long productId, Long userId) {
        ClientProduct product = clientProductRepository.findByIdAndUserId(productId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId + " for user: " + userId));
        return DtoMapper.toClientProductResponse(product);
    }

    @Override
    public List<ClientProductResponse> getProductsByType(String productType) {
        return clientProductRepository.findByProductType(productType).stream()
                .map(DtoMapper::toClientProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClientProductResponse createProduct(Long userId, ClientProductRequest productRequest) {
        UserResponse userDto = userService.getUserById(userId); // ensure user exists
        User user = DtoMapper.toUserEntity(DtoMapper.toUserRequest(userDto));
        ClientProduct product = DtoMapper.toClientProductEntity(productRequest);
        user.addProduct(product);
        product.setUser(user);
        ClientProduct saved = clientProductRepository.save(product);
        return DtoMapper.toClientProductResponse(saved);
    }

    @Override
    public ClientProductResponse updateProduct(Long productId, Long userId, ClientProductRequest productRequest) {
        ClientProduct existing = clientProductRepository.findByIdAndUserId(productId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId + " for user: " + userId));

        // Обновляем только разрешённые поля
        if (productRequest.getAccountNumber() != null) {
            existing.setAccountNumber(productRequest.getAccountNumber());
        }
        if (productRequest.getBalance() != null) {
            existing.setBalance(productRequest.getBalance());
        }
        if (productRequest.getProductType() != null) {
            existing.setProductType(productRequest.getProductType());
        }

        ClientProduct updated = clientProductRepository.save(existing);
        return DtoMapper.toClientProductResponse(updated);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!clientProductRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        clientProductRepository.deleteById(id);
    }

    // Дополнительные методы
    @Override
    public List<ClientProductResponse> getProductsByTypeAndUserId(String productType, Long userId) {
        return clientProductRepository.findByProductTypeAndUserId(productType, userId).stream()
                .map(DtoMapper::toClientProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClientProductResponse getProductByAccountNumber(String accountNumber) {
        ClientProduct product = clientProductRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with account: " + accountNumber));
        return DtoMapper.toClientProductResponse(product);
    }

    @Override
    public boolean productExistsByAccountNumber(String accountNumber) {
        return clientProductRepository.existsByAccountNumber(accountNumber);
    }
}