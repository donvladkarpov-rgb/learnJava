package vk.crud.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import vk.crud.model.ClientProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientProductRepository extends JpaRepository<ClientProduct, Long> {

    // Найти все продукты по userId
    @EntityGraph(attributePaths = {"user"})
    List<ClientProduct> findByUserId(Long userId);

    // Найти продукт по productId и userId
    @EntityGraph(attributePaths = {"user"})
    Optional<ClientProduct> findByIdAndUserId(Long id, Long userId);

    // Найти продукты по типу и userId
    @EntityGraph(attributePaths = {"user"})
    List<ClientProduct> findByProductTypeAndUserId(String productType, Long userId);

    // Найти продукты с балансом больше указанного
    @EntityGraph(attributePaths = {"user"})
    List<ClientProduct> findByBalanceGreaterThanAndUserId(BigDecimal balance, Long userId);

    // Найти по номеру счета
    @EntityGraph(attributePaths = {"user"})
    Optional<ClientProduct> findByAccountNumber(String accountNumber);

    // Запрос с Entity Graph для eager loading пользователя
    @EntityGraph(attributePaths = {"user"})
    Optional<ClientProduct> findWithUserByIdAndUserId(Long id, Long userId);

    // Entity Graph для всех продуктов пользователя с информацией о пользователе
    @EntityGraph(attributePaths = {"user"})
    List<ClientProduct> findWithUserByUserId(Long userId);

    // Проверить существование продукта по номеру счета
    @EntityGraph(attributePaths = {"user"})
    boolean existsByAccountNumber(String accountNumber);

    // Найти продукты по типу продукта
    @EntityGraph(attributePaths = {"user"})
    List<ClientProduct> findByProductType(String productType);

}