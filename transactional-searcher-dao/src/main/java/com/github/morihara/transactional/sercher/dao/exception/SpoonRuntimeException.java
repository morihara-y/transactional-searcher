package com.github.morihara.transactional.sercher.dao.exception;

public class SpoonRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -2833452513564965658L;

    public SpoonRuntimeException(Throwable e) {
        super(e);
    }

    public SpoonRuntimeException(String message) {
        super(message);
    }

    public SpoonRuntimeException(String message, Throwable e) {
        super(message, e);
    }

    public SpoonRuntimeException() {
    }
}
