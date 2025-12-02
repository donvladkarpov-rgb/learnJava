package vk.limit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vk.limit.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByUsername(String username);
    List<Transaction> findByStatus(String status);
    List<Transaction> findByCreatedAtAfter(java.time.LocalDateTime dateTime);

    Optional<Transaction> findByTransactionId(String transactionId);
}