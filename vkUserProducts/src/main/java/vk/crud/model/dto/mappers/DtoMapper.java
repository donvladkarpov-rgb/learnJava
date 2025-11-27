package vk.crud.model.dto.mappers;

import vk.crud.model.User;
import vk.crud.model.ClientProduct;
import vk.crud.model.dto.ClientProductRequest;
import vk.crud.model.dto.ClientProductResponse;
import vk.crud.model.dto.UserRequest;
import vk.crud.model.dto.UserResponse;

import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {

    // Конвертация User Entity → UserResponse DTO
    public static UserResponse toUserResponse(User user) {
        if (user == null) return null;

        List<ClientProductResponse> productResponses = null;
        if (user.getProducts() != null) {
            productResponses = user.getProducts().stream()
                    .map(DtoMapper::toClientProductResponse)
                    .collect(Collectors.toList());
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                productResponses
        );
    }

    // Конвертация ClientProduct Entity → ClientProductResponse DTO
    public static ClientProductResponse toClientProductResponse(ClientProduct product) {
        if (product == null) return null;

        Long userId = null;
        String username = null;
        if (product.getUser() != null) {
            userId = product.getUser().getId();
            username = product.getUser().getUsername();
        }

        return new ClientProductResponse(
                product.getId(),
                product.getAccountNumber(),
                product.getBalance(),
                product.getProductType(),
                userId,
                username,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    // Конвертация UserRequest DTO → User Entity
    public static User toUserEntity(UserRequest userRequest) {
        if (userRequest == null) return null;

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        return user;
    }

    // Конвертация ClientProductRequest DTO → ClientProduct Entity
    public static ClientProduct toClientProductEntity(ClientProductRequest productRequest) {
        if (productRequest == null) return null;

        ClientProduct product = new ClientProduct();
        product.setAccountNumber(productRequest.getAccountNumber());
        product.setBalance(productRequest.getBalance());
        product.setProductType(productRequest.getProductType());
        return product;
    }

    // Конвертация списков
    public static List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream()
                .map(DtoMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public static List<ClientProductResponse> toClientProductResponseList(List<ClientProduct> products) {
        return products.stream()
                .map(DtoMapper::toClientProductResponse)
                .collect(Collectors.toList());
    }
}