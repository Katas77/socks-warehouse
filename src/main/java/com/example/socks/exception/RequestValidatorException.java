package com.example.socks.exception;

public class RequestValidatorException extends RuntimeException {
    public RequestValidatorException() {
    }
    public RequestValidatorException(Exception cause) {
        super(cause);
    }
    public RequestValidatorException(String message) {
        super(message);
    }
    public RequestValidatorException(String message, Exception cause) {
        super(message, cause);
    }

}
