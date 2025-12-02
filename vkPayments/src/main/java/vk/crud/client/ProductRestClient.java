package vk.crud.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vk.crud.config.properties.RestTemplateProperties;
import vk.crud.model.dto.prodact.ProductDto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@EnableConfigurationProperties(RestTemplateProperties.class)
public class ProductRestClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final RestTemplateProperties prop;

    public ProductRestClient(@Qualifier("productRestTemplate") RestTemplate restTemplate,
                             RestTemplateProperties prop) {
        this.restTemplate = restTemplate;
        this.prop = prop;
        this.baseUrl = prop.getBaseUrl();
    }

    public List<ProductDto> getAllProducts() {
        ProductDto[] products = restTemplate.getForObject(baseUrl, ProductDto[].class);
        return products != null ? Arrays.asList(products) : List.of();
    }

    public ProductDto getProductById(Long id) {
        return restTemplate.getForObject(baseUrl + "/" + id, ProductDto.class);
        // Если 404 → ProductNotFoundException (автоматически)
    }

    public List<ProductDto> getProductsByCategory(String category) {
        ProductDto[] products = restTemplate.getForObject(baseUrl + "/category/" + category, ProductDto[].class);
        return products != null ? Arrays.asList(products) : List.of();
    }

    public ProductDto createProduct(ProductDto product) {
        return restTemplate.postForObject(baseUrl, product, ProductDto.class);
    }

    public ProductDto updateProduct(Long id, ProductDto productDetails) {
        restTemplate.put(baseUrl + "/" + id, productDetails);
        return getProductById(id); // или можно использовать exchange + возвращать из PUT
    }

    public void deleteProduct(Long id) {
        restTemplate.delete(baseUrl + "/" + id);
        // Если 404 → ProductDtoNotFoundException
    }

    public ProductDto updateStock(Long id, Integer stock) {
        String url = baseUrl + "/" + id + "/stock?stock=" + stock;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(headers), ProductDto.class)
                .getBody();
    }

    public ProductDto updatePrice(Long id, BigDecimal price) {
        String url = baseUrl + "/" + id + "/price?price=" + price;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(headers), ProductDto.class)
                .getBody();
    }
}