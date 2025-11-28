package vk.crud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import vk.crud.client.payments.exceptions.ClientBalanceResponseErrorHandler;
import vk.crud.client.product.exceptions.ProductResponseErrorHandler;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate productRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ProductResponseErrorHandler());
        return restTemplate;
    }

    @Bean
    public RestTemplate clientBalanceRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ClientBalanceResponseErrorHandler());
        return restTemplate;
    }

}