package com.fitness.gateway.exception;

public class ErrorResponse {
    private final String errorCode;
    private final String message;
    private final int status;

    public ErrorResponse(String errorCode, String message, int status) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
