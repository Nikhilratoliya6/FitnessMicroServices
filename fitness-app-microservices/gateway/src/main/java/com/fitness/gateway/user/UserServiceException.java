package com.fitness.gateway.user;

public class UserServiceException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;

    public UserServiceException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
