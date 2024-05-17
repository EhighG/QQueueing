package com.example.tes24.qqueueing.configuration;

import com.example.tes24.qqueueing.annotation.Q2AutoConfiguration;
import com.example.tes24.qqueueing.annotation.Q2Bean;
import com.example.tes24.qqueueing.channel.Q2HttpHeader;
import com.example.tes24.qqueueing.properties.Q2HttpHeaderProperties;

@Q2AutoConfiguration
public class Q2HttpHeaderConfiguration {
    @Q2Bean
    public Q2HttpHeader defaultQ2HttpHeader(Q2HttpHeaderProperties q2HttpHeaderProperties) {
        return q2HttpHeaderProperties.getDefaultQ2HttpHeader();
    }
}
