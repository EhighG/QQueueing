package com.example.tes24.qqueue_module.http;

public enum ContentType {
    TEXT_PLAIN("text/plain;charset=UTF-8"),
    X_FORM_URL_ENCODED("application/x-www-form-urlencoded"),
    JSON("application/json");

    private String typeValue;

    ContentType(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeValue() {
        return typeValue;
    }
}
