package com.example.brokage.application.error;

import com.example.brokage.domain.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final String GENERAL_ERROR = "Oops, Something went Wrong.";


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({
            BadCredentialsException.class,
            InternalAuthenticationServiceException.class
    })
    public ErrorResponse handleUnAuthenticatedError(Exception e) {
        log.warn("Failed to process - Forbidden", e);
        return new ErrorResponse(e);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({
            AccessDeniedException.class,
            InvalidUserCredentialsException.class,
            JwtServiceException.class
    })
    public ErrorResponse handleForbiddenError(Exception e) {
        log.warn("Failed to process - Forbidden", e);
        return new ErrorResponse(e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            IllegalArgumentException.class,
            InsufficientSizeException.class,
            InvalidOrderSideException.class,
            InvalidOrderStatusException.class
    })
    public ErrorResponse handleBadRequestDomainError(Exception e) {
        log.warn("Failed to process - Bad request", e);
        return new ErrorResponse(e);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidExceptionError(MethodArgumentNotValidException ex) {
        log.warn("Failed to process - Bad request", ex);
        String error = ex.getClass().getSimpleName();
        var details = ExceptionUtil.getInvalidFields(ex)
                .stream()
                .map(invalidField -> "Field '%s' %s".formatted(invalidField.field(), invalidField.message()))
                .collect(Collectors.joining(". ", "Request validation failed. ", "."));
        return new ErrorResponse(error, "Invalid request payload", details);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            MissingRequestHeaderException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            BindException.class,
            ConstraintViolationException.class
    })
    public ErrorResponse handleBadRequestGenericError(Exception e) {
        log.warn("Failed to process - Bad request", e);
        return new ErrorResponse(GENERAL_ERROR, e);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
            UserAlreadyExistsException.class
    })
    public ErrorResponse handleConflictError(Exception e) {
        log.warn("Failed to process - Conflict", e);
        return new ErrorResponse(e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleUnknownError(Exception e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(GENERAL_ERROR, e);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            RecordNotFoundException.class,
            UserNotExistsException.class
    })
    public ErrorResponse handleRecordNotFound(Exception e) {
        log.warn("Failed to process - Not Found", e);
        return new ErrorResponse(e);
    }
}
