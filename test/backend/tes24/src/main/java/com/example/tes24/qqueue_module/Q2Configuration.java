package com.example.tes24.qqueue_module;

import com.example.tes24.qqueue_module.http.Q2HttpHeaderProperties;

import java.util.Objects;

public class Q2Configuration {
    Q2HttpHeaderProperties defaultHeader;
    Q2HttpHeaderProperties monitorHeader;

    public Q2HttpHeaderProperties getDefaultHeader() {
        return defaultHeader;
    }

    public void setDefaultHeader(Q2HttpHeaderProperties defaultHeader) {
        this.defaultHeader = defaultHeader;
    }

    public Q2HttpHeaderProperties getMonitorHeader() {
        return monitorHeader;
    }

    public void setMonitorHeader(Q2HttpHeaderProperties monitorHeader) {
        this.monitorHeader = monitorHeader;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Q2Configuration that = (Q2Configuration) object;
        return Objects.equals(defaultHeader, that.defaultHeader) && Objects.equals(monitorHeader, that.monitorHeader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultHeader, monitorHeader);
    }

    @Override
    public String toString() {
        return "Q2Configuration{" +
                "defaultHeader=" + defaultHeader +
                ", monitorHeader=" + monitorHeader +
                '}';
    }
}
