package vk.crud.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vk.crud.client.ProductRestClient;
import vk.crud.model.ClientBalance;
import vk.crud.model.dto.ClientBalanceDto;
import vk.crud.model.dto.prodact.ProductDto;
import vk.crud.repo.ClientBalanceRepository;
import vk.crud.service.ClientBalanceService;
import vk.crud.web.exceptions.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientBalanceServiceImpl implements ClientBalanceService {

    private final ClientBalanceRepository clientBalanceRepository;
    private final ProductRestClient productRestClient;

    public ClientBalanceServiceImpl(ClientBalanceRepository clientBalanceRepository, ProductRestClient productRestClient) {
        this.clientBalanceRepository = clientBalanceRepository;
        this.productRestClient = productRestClient;
    }

    private ClientBalanceDto convertToDto(ClientBalance balance) {
        return new ClientBalanceDto(
                balance.getId(),
                balance.getClientId(),
                balance.getCardNumber(),
                balance.getBalance()
        );
    }

    @Override
    public List<ClientBalanceDto> getAllBalances() {
        return clientBalanceRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ClientBalanceDto> getBalanceById(Long id) {
        return clientBalanceRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public Optional<ClientBalanceDto> getBalanceByClientId(String clientId, Long productId, Long quantity) {
        ProductDto productDto = productRestClient.getProductById(productId);
        if (quantity > productDto.getStock()) throw new ResourceNotFoundException("Нет товара!");
        return clientBalanceRepository.findByClientId(clientId)
                .map(this::convertToDto);
    }

    @Override
    public Optional<ClientBalanceDto> getBalanceByCardNumber(String cardNumber) {
        return clientBalanceRepository.findByCardNumber(cardNumber)
                .map(this::convertToDto);
    }

    @Override
    public Optional<ClientBalanceDto> createBalance(String clientId, String cardNumber, BigDecimal balance) {
        if (clientBalanceRepository.existsByClientId(clientId) || clientBalanceRepository.existsByCardNumber(cardNumber)) {
            return Optional.empty(); // Конфликт
        }

        ClientBalance newBalance = new ClientBalance(clientId, cardNumber, balance);
        ClientBalance saved = clientBalanceRepository.save(newBalance);
        return Optional.of(convertToDto(saved));
    }

    @Override
    public Optional<ClientBalanceDto> updateBalance(Long id, String clientId, String cardNumber, BigDecimal balance) {
        return clientBalanceRepository.findById(id)
                .map(existing -> {
                    existing.setClientId(clientId);
                    existing.setCardNumber(cardNumber);
                    existing.setBalance(balance);
                    ClientBalance updated = clientBalanceRepository.save(existing);
                    return convertToDto(updated);
                });
    }

    @Override
    public Optional<ClientBalanceDto> depositBalance(Long id, BigDecimal amount) {
        return clientBalanceRepository.findById(id)
                .map(balance -> {
                    balance.addToBalance(amount);
                    ClientBalance updated = clientBalanceRepository.save(balance);
                    return convertToDto(updated);
                });
    }

    @Override
    public Optional<ClientBalanceDto> withdrawBalance(Long id, BigDecimal amount) {
        return clientBalanceRepository.findById(id)
                .filter(balance -> balance.hasSufficientBalance(amount))
                .map(balance -> {
                    balance.subtractFromBalance(amount);
                    ClientBalance updated = clientBalanceRepository.save(balance);
                    return convertToDto(updated);
                });
    }

    @Override
    public boolean deleteBalance(Long id) {
        if (clientBalanceRepository.existsById(id)) {
            clientBalanceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
