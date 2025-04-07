package com.example.brokage.application.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String error;
    private final String cause;
    private final String details;

    /**
     * returns the message from exception on UI.
     */
    public ErrorResponse(Throwable e) {
        this(e, ExceptionUtil.getRootCause(e));
    }

    /**
     * returns the message from @message field on UI.
     */
    public ErrorResponse(String message, Throwable e) {
        this(e.getClass().getSimpleName(), ExceptionUtil.getRootCause(e).getLocalizedMessage(), message);
    }

    public ErrorResponse(Throwable e, Throwable rootCause) {
        this(rootCause.getClass().getSimpleName(), rootCause.getLocalizedMessage(), e.getLocalizedMessage());
    }

    record InvalidField(String field, String message) {
    }
}
