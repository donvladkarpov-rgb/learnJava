package vk.crud.client.payments.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

public class ClientBalanceResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError()) {
            HttpStatusCode statusCode = response.getStatusCode();
            if (statusCode.equals(NOT_FOUND)) {
                throw new ClientBalanceNotFoundException("Client balance not found");
            } else if (statusCode.equals(CONFLICT)) {
                throw new ClientBalanceConflictException("Client ID or card number already exists");
            } else if (statusCode.equals(BAD_REQUEST)) {// Предполагаем, что BAD_REQUEST при снятии = недостаточно средств
                throw new InsufficientFundsException("Insufficient funds or invalid request");
            } else {
                super.handleError(response);
            }
        } else {
            super.handleError(response);
        }
    }
}