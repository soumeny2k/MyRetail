package com.sample.myretail.exception;

public class ProductException extends RuntimeException {
    private int status;
    private String message;

    public ProductException(String message) {
        this.message = message;
    }

    public ProductException(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
