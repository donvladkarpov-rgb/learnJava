package vk.crud.web;

import vk.crud.model.ClientBalance;
import vk.crud.model.dto.ClientBalanceDto;
import vk.crud.repo.ClientBalanceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client-balances")
@Tag(name = "Client Balance API", description = "Управление балансами клиентов")
public class ClientBalanceController {

    @Autowired
    private ClientBalanceRepository clientBalanceRepository;

    @GetMapping
    @Operation(summary = "Получить все балансы клиентов")
    public ResponseEntity<List<ClientBalanceDto>> getAllBalances() {
        List<ClientBalanceDto> balances = clientBalanceRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(balances);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить баланс по ID")
    public ResponseEntity<ClientBalanceDto> getBalanceById(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id) {
        Optional<ClientBalance> balance = clientBalanceRepository.findById(id);
        return balance.map(b -> ResponseEntity.ok(convertToDto(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Получить баланс по ID клиента")
    public ResponseEntity<ClientBalanceDto> getBalanceByClientId(
            @Parameter(description = "ID клиента", example = "CLIENT_12345", required = true)
            @PathVariable("clientId") String clientId) {
        Optional<ClientBalance> balance = clientBalanceRepository.findByClientId(clientId);
        return balance.map(b -> ResponseEntity.ok(convertToDto(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/card/{cardNumber}")
    @Operation(summary = "Получить баланс по номеру карты")
    public ResponseEntity<ClientBalanceDto> getBalanceByCardNumber(
            @Parameter(description = "Номер карты", example = "4111111111111111", required = true)
            @PathVariable("cardNumber") String cardNumber) {
        Optional<ClientBalance> balance = clientBalanceRepository.findByCardNumber(cardNumber);
        return balance.map(b -> ResponseEntity.ok(convertToDto(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать новый баланс клиента")
    public ResponseEntity<ClientBalanceDto> createBalance(
            @Parameter(description = "ID клиента", example = "CLIENT_12345", required = true)
            @RequestParam("clientId") String clientId,
            @Parameter(description = "Номер карты", example = "4111111111111111", required = true)
            @RequestParam("cardNumber") String cardNumber,
            @Parameter(description = "Начальный баланс", example = "1000.00")
            @RequestParam(value = "balance", defaultValue = "0.00") BigDecimal balance) {

        if (clientBalanceRepository.existsByClientId(clientId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (clientBalanceRepository.existsByCardNumber(cardNumber)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        ClientBalance newBalance = new ClientBalance(clientId, cardNumber, balance);
        ClientBalance savedBalance = clientBalanceRepository.save(newBalance);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedBalance));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить баланс клиента")
    public ResponseEntity<ClientBalanceDto> updateBalance(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "ID клиента", example = "CLIENT_12345", required = true)
            @RequestParam("clientId") String clientId,
            @Parameter(description = "Номер карты", example = "4111111111111111", required = true)
            @RequestParam("cardNumber") String cardNumber,
            @Parameter(description = "Баланс", example = "1500.75", required = true)
            @RequestParam("balance") BigDecimal balance) {

        Optional<ClientBalance> optionalBalance = clientBalanceRepository.findById(id);
        if (optionalBalance.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ClientBalance existingBalance = optionalBalance.get();
        existingBalance.setClientId(clientId);
        existingBalance.setCardNumber(cardNumber);
        existingBalance.setBalance(balance);

        ClientBalance updatedBalance = clientBalanceRepository.save(existingBalance);
        return ResponseEntity.ok(convertToDto(updatedBalance));
    }

    @PatchMapping("/{id}/deposit")
    @Operation(summary = "Пополнить баланс")
    public ResponseEntity<ClientBalanceDto> depositBalance(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Сумма пополнения", example = "500.00", required = true)
            @RequestParam("amount") BigDecimal amount) {

        Optional<ClientBalance> optionalBalance = clientBalanceRepository.findById(id);
        if (optionalBalance.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ClientBalance balance = optionalBalance.get();
        balance.addToBalance(amount);

        ClientBalance updatedBalance = clientBalanceRepository.save(balance);
        return ResponseEntity.ok(convertToDto(updatedBalance));
    }

    @PatchMapping("/{id}/withdraw")
    @Operation(summary = "Списать с баланса")
    public ResponseEntity<ClientBalanceDto> withdrawBalance(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Сумма списания", example = "200.50", required = true)
            @RequestParam("amount") BigDecimal amount) {

        Optional<ClientBalance> optionalBalance = clientBalanceRepository.findById(id);
        if (optionalBalance.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ClientBalance balance = optionalBalance.get();
        if (!balance.hasSufficientBalance(amount)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        balance.subtractFromBalance(amount);
        ClientBalance updatedBalance = clientBalanceRepository.save(balance);
        return ResponseEntity.ok(convertToDto(updatedBalance));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить баланс клиента")
    public ResponseEntity<Void> deleteBalance(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id) {
        if (!clientBalanceRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        clientBalanceRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ClientBalanceDto convertToDto(ClientBalance balance) {
        return new ClientBalanceDto(
                balance.getId(),
                balance.getClientId(),
                balance.getCardNumber(),
                balance.getBalance()
        );
    }
}