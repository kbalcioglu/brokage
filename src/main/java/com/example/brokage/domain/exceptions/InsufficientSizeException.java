package com.example.brokage.domain.exceptions;

public class InsufficientSizeException extends RuntimeException {

    public InsufficientSizeException(String message) {
        super(message);
    }

    public InsufficientSizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
