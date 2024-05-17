package com.example.tes24.qqueueing.exception;

public class InstantiateFailureException extends RuntimeException {
    public InstantiateFailureException(String message) {
        super(message);
    }
    public InstantiateFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
