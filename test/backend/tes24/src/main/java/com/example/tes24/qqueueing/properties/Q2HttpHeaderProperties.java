package com.example.tes24.qqueueing.properties;

import com.example.tes24.qqueueing.channel.Q2HttpHeader;

import java.util.Map;

public class Q2HttpHeaderProperties {
    public Map<String, Q2HttpHeader> q2HttpHeaderMap;

    public Q2HttpHeader getDefaultQ2HttpHeader() {
        return getQ2HttpHeader("default");
    }

    public Q2HttpHeader getQ2HttpHeader(String name) {
        return q2HttpHeaderMap.get(name);
    }

    @Override
    public String toString() {
        return "Q2HttpHeaderProperties{" +
                "q2HttpHeaderMap=" + q2HttpHeaderMap +
                '}';
    }
}
