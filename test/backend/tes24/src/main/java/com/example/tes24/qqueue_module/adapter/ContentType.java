package com.example.tes24.qqueue_module.adapter;

public enum ContentType {
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
