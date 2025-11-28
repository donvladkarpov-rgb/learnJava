package vk.crud.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vk.crud.model.dto.ClientBalanceDto;
import vk.crud.service.ClientBalanceService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/client-balances")
@Tag(name = "Client Balance API", description = "Управление балансами клиентов")
public class ClientBalanceController {

    private final ClientBalanceService clientBalanceService;

    public ClientBalanceController(@Autowired ClientBalanceService clientBalanceService) {
        this.clientBalanceService = clientBalanceService;
    }

    @GetMapping
    @Operation(summary = "Получить все балансы клиентов")
    public ResponseEntity<List<ClientBalanceDto>> getAllBalances() {
        return ResponseEntity.ok(clientBalanceService.getAllBalances());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить баланс по ID")
    public ResponseEntity<ClientBalanceDto> getBalanceById(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id) {
        return clientBalanceService.getBalanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Получить баланс по ID клиента")
    public ResponseEntity<ClientBalanceDto> getBalanceByClientId(
            @Parameter(description = "ID клиента", example = "CLIENT_12345", required = true)
            @PathVariable("clientId") String clientId) {
        return clientBalanceService.getBalanceByClientId(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/card/{cardNumber}")
    @Operation(summary = "Получить баланс по номеру карты")
    public ResponseEntity<ClientBalanceDto> getBalanceByCardNumber(
            @Parameter(description = "Номер карты", example = "4111111111111111", required = true)
            @PathVariable("cardNumber") String cardNumber) {
        return clientBalanceService.getBalanceByCardNumber(cardNumber)
                .map(ResponseEntity::ok)
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

        return clientBalanceService.createBalance(clientId, cardNumber, balance)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
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

        return clientBalanceService.updateBalance(id, clientId, cardNumber, balance)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/deposit")
    @Operation(summary = "Пополнить баланс")
    public ResponseEntity<ClientBalanceDto> depositBalance(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Сумма пополнения", example = "500.00", required = true)
            @RequestParam("amount") BigDecimal amount) {

        return clientBalanceService.depositBalance(id, amount)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/withdraw")
    @Operation(summary = "Списать с баланса")
    public ResponseEntity<ClientBalanceDto> withdrawBalance(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Сумма списания", example = "200.50", required = true)
            @RequestParam("amount") BigDecimal amount) {

        return clientBalanceService.withdrawBalance(id, amount)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить баланс клиента")
    public ResponseEntity<Void> deleteBalance(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id) {
        return clientBalanceService.deleteBalance(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
