package vk.crud.service;

import vk.crud.model.dto.ClientBalanceDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ClientBalanceService {

    List<ClientBalanceDto> getAllBalances();

    Optional<ClientBalanceDto> getBalanceById(Long id);

    Optional<ClientBalanceDto> getBalanceByClientId(String clientId);

    Optional<ClientBalanceDto> getBalanceByCardNumber(String cardNumber);

    Optional<ClientBalanceDto> createBalance(String clientId, String cardNumber, BigDecimal balance);

    Optional<ClientBalanceDto> updateBalance(Long id, String clientId, String cardNumber, BigDecimal balance);

    Optional<ClientBalanceDto> depositBalance(Long id, BigDecimal amount);

    Optional<ClientBalanceDto> withdrawBalance(Long id, BigDecimal amount);

    boolean deleteBalance(Long id);
}
