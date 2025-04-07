package com.example.brokage.application.error;

import lombok.experimental.UtilityClass;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

@UtilityClass
class ExceptionUtil {

    Throwable getRootCause(Throwable e) {
        Throwable result = e;
        Throwable cause;

        while (null != (cause = result.getCause()) && !(result.getClass().equals(cause.getClass()))) {
            result = cause;
        }
        return result;

    }

    List<ErrorResponse.InvalidField> getInvalidFields(MethodArgumentNotValidException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse.InvalidField(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
    }
}
