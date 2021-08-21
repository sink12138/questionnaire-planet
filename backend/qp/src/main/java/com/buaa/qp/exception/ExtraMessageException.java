package com.buaa.qp.exception;

public class ExtraMessageException extends Exception {
    private final String message;

    public ExtraMessageException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
