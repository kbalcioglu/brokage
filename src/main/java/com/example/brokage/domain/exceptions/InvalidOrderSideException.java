package com.example.brokage.domain.exceptions;

public class InvalidOrderSideException extends RuntimeException {

    public InvalidOrderSideException(String message) {
        super(message);
    }
}
