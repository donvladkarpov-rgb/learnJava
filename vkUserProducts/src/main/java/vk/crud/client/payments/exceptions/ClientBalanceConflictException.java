package vk.crud.client.payments.exceptions;

public class ClientBalanceConflictException extends RuntimeException {
    public ClientBalanceConflictException(String message) {
        super(message);
    }
}