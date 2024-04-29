package com.qqueueing.main.waiting.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Enumeration;

@Slf4j
@Component
public class TargetApiConnector {

    private final RestTemplate restTemplate;
    private String TARGET_URL;

    public TargetApiConnector(RestTemplate restTemplate,
                              @Value("${servers.target.url}") String targetUrl) {
        this.restTemplate = restTemplate;
        TARGET_URL = targetUrl;
    }

    public ResponseEntity<String> forwardToTarget(HttpServletRequest request) {
        HttpEntity<String> httpEntity = new HttpEntity<>(getAllHeaders(request));
        return restTemplate.exchange(TARGET_URL, HttpMethod.GET, httpEntity, String.class);
    }

    private HttpHeaders getAllHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        Collections.list(headerNames)
                .forEach(key -> {
                    headers.add(key, request.getHeader(key));
                });
        return headers;
    }
}
