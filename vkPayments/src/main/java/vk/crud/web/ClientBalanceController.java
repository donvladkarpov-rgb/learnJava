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
import vk.crud.web.exceptions.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/client-balances")
@Tag(name = "Client Balance API", description = "Управление балансами клиентов")
public class ClientBalanceController {

    private final ClientBalanceService clientBalanceService;

    public ClientBalanceController(@Autowired ClientBalanceService clientBalanceService) {
        this.clientBalanceService = clientBalanceService;
    }

    @GetMapping
    @Operation(summary = "Получить все балансы клиентов")
    public List<ClientBalanceDto> getAllBalances() {
        return clientBalanceService.getAllBalances();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить баланс по ID")
    public ClientBalanceDto getBalanceById(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id) {
        return clientBalanceService.getBalanceById(id)
                .orElseThrow(()->new ResourceNotFoundException("Баланс клиента не найден!"));
    }

    @GetMapping("/client/{clientId}/{productId}/{quantity}")
    @Operation(summary = "Получить баланс по ID клиента")
    public ClientBalanceDto getBalanceByClientId(
            @Parameter(description = "ID клиента", example = "CLIENT_12345", required = true)
            @PathVariable("clientId") String clientId,
            @Parameter(description = "ID товара", example = "12345", required = true)
            @PathVariable("productId") Long productId,
            @Parameter(description = "Количество товара", example = "12345", required = true)
            @PathVariable("quantity") Long quantity
            ) {
        return clientBalanceService.getBalanceByClientId(clientId, productId, quantity)
                .orElseThrow(()->new ResourceNotFoundException("Баланс клиента не найден!"));
    }

    @GetMapping("/card/{cardNumber}")
    @Operation(summary = "Получить баланс по номеру карты")
    public ClientBalanceDto getBalanceByCardNumber(
            @Parameter(description = "Номер карты", example = "4111111111111111", required = true)
            @PathVariable("cardNumber") String cardNumber) {
        return clientBalanceService.getBalanceByCardNumber(cardNumber)
                .orElseThrow(()->new ResourceNotFoundException("Баланс клиента не найден!"));
    }

    @PostMapping
    @Operation(summary = "Создать новый баланс клиента")
    public ClientBalanceDto createBalance(
            @Parameter(description = "ID клиента", example = "CLIENT_12345", required = true)
            @RequestParam("clientId") String clientId,
            @Parameter(description = "Номер карты", example = "4111111111111111", required = true)
            @RequestParam("cardNumber") String cardNumber,
            @Parameter(description = "Начальный баланс", example = "1000.00")
            @RequestParam(value = "balance", defaultValue = "0.00") BigDecimal balance) {

        return clientBalanceService.createBalance(clientId, cardNumber, balance)
                .orElseThrow(()->new ResourceNotFoundException("Баланс клиента не найден!"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить баланс клиента")
    public ClientBalanceDto updateBalance(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "ID клиента", example = "CLIENT_12345", required = true)
            @RequestParam("clientId") String clientId,
            @Parameter(description = "Номер карты", example = "4111111111111111", required = true)
            @RequestParam("cardNumber") String cardNumber,
            @Parameter(description = "Баланс", example = "1500.75", required = true)
            @RequestParam("balance") BigDecimal balance) {

        return clientBalanceService.updateBalance(id, clientId, cardNumber, balance)
                .orElseThrow(()->new ResourceNotFoundException("Баланс клиента не найден!"));
    }

    @PatchMapping("/{id}/deposit")
    @Operation(summary = "Пополнить баланс")
    public ClientBalanceDto depositBalance(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Сумма пополнения", example = "500.00", required = true)
            @RequestParam("amount") BigDecimal amount) {

        return clientBalanceService.depositBalance(id, amount)
                .orElseThrow(()->new ResourceNotFoundException("Баланс клиента не найден!"));
    }

    @PatchMapping("/{id}/withdraw")
    @Operation(summary = "Списать с баланса")
    public ClientBalanceDto withdrawBalance(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Сумма списания", example = "200.50", required = true)
            @RequestParam("amount") BigDecimal amount) {

        return clientBalanceService.withdrawBalance(id, amount)
                .orElseThrow(()->new ResourceNotFoundException("Баланс клиента не найден!"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить баланс клиента")
    public void deleteBalance(
            @Parameter(description = "ID записи баланса", example = "1", required = true)
            @PathVariable("id") Long id) {
        clientBalanceService.deleteBalance(id);
    }
}
