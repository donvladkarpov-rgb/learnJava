package vk.crud.client.product.exceptions;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError()) {
            if (response.getStatusCode().equals(NOT_FOUND)) {
                throw new ProductNotFoundException("Product not found");
            } else {
                super.handleError(response);
            }
        } else {
            super.handleError(response);
        }
    }
}