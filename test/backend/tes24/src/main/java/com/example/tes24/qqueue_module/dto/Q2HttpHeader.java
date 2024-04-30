package com.example.tes24.qqueue_module.dto;

import com.example.tes24.qqueue_module.adapter.ContentType;

import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public final class Q2HttpHeader {
    static {
        instance = new Q2HttpHeader();
        reentrantLock = new ReentrantLock();
    }

    private static final Q2HttpHeader instance;
    private static final ReentrantLock reentrantLock;

    protected static final String DEFAULT_URL = "http://localhost:8080/tes24";
    protected static final int DEFAULT_TIMEOUT = 15000;
    protected static final String DEFAULT_METHOD = "POST";
    protected static final ContentType DEFAULT_CONTENT_TYPE = ContentType.JSON;

    private String url = DEFAULT_URL;
    private ContentType contentType = DEFAULT_CONTENT_TYPE;
    private Integer timeout = DEFAULT_TIMEOUT;
    private String method = DEFAULT_METHOD;

    public static Q2HttpHeader defaultQ2HttpHeader() {
        reentrantLock.lock();
        try {
            return instance;
        } finally {
            reentrantLock.unlock();
        }
    }

    public static Q2HttpHeaderBuilder builder() {
        return new Q2HttpHeaderBuilder();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Q2HttpHeader that = (Q2HttpHeader) object;
        return Objects.equals(url, that.url) && contentType == that.contentType && Objects.equals(timeout, that.timeout) && Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, contentType, timeout, method);
    }

    @Override
    public String toString() {
        return "Q2HttpHeader{" +
                "url='" + url + '\'' +
                ", contentType=" + contentType +
                ", timeout=" + timeout +
                ", method='" + method + '\'' +
                '}';
    }

    static class Q2HttpHeaderBuilder {
        private final Q2HttpHeader q2HttpHeader;
        private Q2HttpHeaderBuilder() {
            this.q2HttpHeader = new Q2HttpHeader();
        }

        public Q2HttpHeaderBuilder url(String url) {
            this.q2HttpHeader.url = url;
            return this;
        }

        public Q2HttpHeaderBuilder contentType(ContentType contentType) {
            this.q2HttpHeader.contentType = contentType;
            return this;
        }

        public Q2HttpHeaderBuilder timeout(Integer timeout) {
            this.q2HttpHeader.timeout = timeout;
            return this;
        }

        public Q2HttpHeaderBuilder method(String method) {
            this.q2HttpHeader.method = method;
            return this;
        }

        public Q2HttpHeader build() {
            return this.q2HttpHeader;
        }
    }

    public String getUrl() {
        return url;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public String getMethod() {
        return method;
    }

}
