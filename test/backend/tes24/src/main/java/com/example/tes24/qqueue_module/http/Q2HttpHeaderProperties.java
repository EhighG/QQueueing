package com.example.tes24.qqueue_module.http;

import java.util.Objects;

public class Q2HttpHeaderProperties {
    private String url;
    private String contentType;
    private Integer timeout;
    private String method;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
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

    @Override
    public String toString() {
        return "Q2HttpHeaderProperties{" +
                "url='" + url + '\'' +
                ", contentType='" + contentType + '\'' +
                ", timeout=" + timeout +
                ", method='" + method + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Q2HttpHeaderProperties that = (Q2HttpHeaderProperties) o;
        return Objects.equals(url, that.url) && Objects.equals(contentType, that.contentType) && Objects.equals(timeout, that.timeout) && Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, contentType, timeout, method);
    }
}
