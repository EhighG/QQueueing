package com.example.tes24.qqueue_module.adapter;

import java.util.concurrent.locks.ReentrantLock;

public final class HttpHeaderInfo {
    private static final HttpHeaderInfo instance;
    private static final ReentrantLock reentrantLock;

    static {
        instance = new HttpHeaderInfo();
        reentrantLock = new ReentrantLock();
    }

    static final String DEFAULT_URL = "localhost";
    static final int DEFAULT_TIMEOUT = 15000;
    static final String DEFAULT_METHOD = "POST";
    static final ContentType DEFAULT_CONTENT_TYPE = ContentType.JSON;

    private String url = DEFAULT_URL;
    private ContentType contentType = DEFAULT_CONTENT_TYPE;
    private Integer timeout = DEFAULT_TIMEOUT;
    private String method = DEFAULT_METHOD;

    public static HttpHeaderInfo defaultHttpHeaderInfo() {
        reentrantLock.lock();
        try {
            return instance;
        } finally {
            reentrantLock.unlock();
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
