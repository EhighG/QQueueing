package com.example.tes24.qqueue_module.exception;

public class HttpURLConnectionBuildException extends RuntimeException {
    public HttpURLConnectionBuildException(String message) {
        super(message);
    }

    public HttpURLConnectionBuildException(String message, Throwable cause) {
        super(message, cause);
    }
    public HttpURLConnectionBuildException(Throwable cause) {
        super(cause);
    }
}
