package vk.crud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import vk.crud.client.exceptions.ProductResponseErrorHandler;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate productRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ProductResponseErrorHandler());
        return restTemplate;
    }


}