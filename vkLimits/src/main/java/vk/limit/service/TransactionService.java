package vk.limit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vk.limit.model.mapper.UserLimitsMapper;
import vk.limit.web.exceptions.TransactionNotFoundException;
import vk.limit.model.Transaction;
import vk.limit.model.dto.TransactionInfo;
import vk.limit.repository.TransactionRepository;

import java.util.List;

import static vk.limit.model.mapper.UserLimitsMapper.toDto;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Сохраняет транзакцию
     */
    @Transactional
    public TransactionInfo saveTransaction(TransactionInfo transactionInfo) {
        Transaction entity = UserLimitsMapper.toEntity(transactionInfo);
        Transaction saved = transactionRepository.save(entity);
        return toDto(saved);
    }

    /**
     * Ищет транзакцию по ID
     */
    public TransactionInfo getTransaction(String transactionId) {
        Transaction entity = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found: " + transactionId));
        return toDto(entity);
    }

    public List<TransactionInfo> getAll() {
        return transactionRepository.findAll().stream().map(UserLimitsMapper::toDto).toList();
    }

    /**
     * Удаляет транзакцию по ID
     */
    @Transactional
    public void deleteTransaction(String transactionId) {
        transactionRepository.deleteById(transactionId);
    }
}