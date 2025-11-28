package vk.crud.client.payments;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vk.crud.model.dto.payments.ClientBalanceDto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class ClientBalanceRestClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ClientBalanceRestClient(
            @Qualifier("clientBalanceRestTemplate") RestTemplate clientBalanceRestTemplate,
            @Value("${client-balance.client.base-url}") String baseUrl) {
        this.restTemplate = clientBalanceRestTemplate;
        this.baseUrl = baseUrl;
    }

    public List<ClientBalanceDto> getAllBalances() {
        ClientBalanceDto[] balances = restTemplate.getForObject(baseUrl, ClientBalanceDto[].class);
        return balances != null ? Arrays.asList(balances) : List.of();
    }

    public ClientBalanceDto getBalanceById(Long id) {
        return restTemplate.getForObject(baseUrl + "/" + id, ClientBalanceDto.class);
    }

    public ClientBalanceDto getBalanceByClientId(String clientId) {
        return restTemplate.getForObject(baseUrl + "/client/" + clientId, ClientBalanceDto.class);
    }

    public ClientBalanceDto getBalanceByCardNumber(String cardNumber) {
        return restTemplate.getForObject(baseUrl + "/card/" + cardNumber, ClientBalanceDto.class);
    }

    public ClientBalanceDto createBalance(String clientId, String cardNumber, BigDecimal balance) {
        String url = String.format("%s?clientId=%s&cardNumber=%s&balance=%s",
                baseUrl, clientId, cardNumber, balance);
        return restTemplate.postForObject(url, null, ClientBalanceDto.class);
    }

    public ClientBalanceDto updateBalance(Long id, String clientId, String cardNumber, BigDecimal balance) {
        String url = String.format("%s/%d?clientId=%s&cardNumber=%s&balance=%s",
                baseUrl, id, clientId, cardNumber, balance);
        restTemplate.put(url, null);
        return getBalanceById(id);
    }

    public ClientBalanceDto depositBalance(Long id, BigDecimal amount) {
        String url = String.format("%s/%d/deposit?amount=%s", baseUrl, id, amount);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(headers), ClientBalanceDto.class)
                .getBody();
    }

    public ClientBalanceDto withdrawBalance(Long id, BigDecimal amount) {
        String url = String.format("%s/%d/withdraw?amount=%s", baseUrl, id, amount);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(headers), ClientBalanceDto.class)
                .getBody();
    }

    public void deleteBalance(Long id) {
        restTemplate.delete(baseUrl + "/" + id);
    }
}