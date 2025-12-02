package vk.crud.client.payments.exceptions;

public class ClientBalanceNotFoundException extends RuntimeException {
    public ClientBalanceNotFoundException(String message) {
        super(message);
    }
}