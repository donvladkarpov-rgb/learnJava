package vk.crud.repo;

import vk.crud.model.ClientBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ClientBalanceRepository extends JpaRepository<ClientBalance, Long> {

    Optional<ClientBalance> findByClientId(@Param("clientId") String clientId);

    Optional<ClientBalance> findByCardNumber(@Param("cardNumber") String cardNumber);

    boolean existsByClientId(@Param("clientId") String clientId);

    boolean existsByCardNumber(@Param("cardNumber") String cardNumber);

    @Query("SELECT cb.balance FROM ClientBalance cb WHERE cb.clientId = :clientId")
    Optional<BigDecimal> findBalanceByClientId(@Param("clientId") String clientId);
}