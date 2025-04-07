package com.example.brokage.domain.exceptions;

public class InvalidUserCredentialsException extends RuntimeException {

    public InvalidUserCredentialsException(String message) {
        super(message);
    }

    public InvalidUserCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}