package com.qqueueing.main.waiting.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@Component
public class TargetApiConnector {

    private final RestTemplate restTemplate;

    public TargetApiConnector(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> forward(String targetUrl, HttpServletRequest request) {
        HttpEntity<String> httpEntity = new HttpEntity<>(getAllHeaders(request));
        System.out.println("targetUrl = " + targetUrl);
        return restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, String.class);
    }

    public ResponseEntity<String> forwardToWaitingPage(String queuePageUrl, String targetUrl, HttpServletRequest request) {
//        HttpEntity<String> httpEntity = new HttpEntity<>(getAllHeaders(request));
//        String requestUrl = queuePageUrl + "?Target-URL=" + targetUrl;
//        System.out.println("main server -> next.js request url = " + requestUrl);
//        ResponseEntity<String> result = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class);
//        log.info("waitingPage result : " + result);
//        return result;
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        messageConverters.add(stringHttpMessageConverter);
        restTemplate.setMessageConverters(messageConverters);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpEntity<String> httpEntity = new HttpEntity<>(getAllHeaders(request));
        String requestUrl = queuePageUrl + "?Target-URL=" + targetUrl;
        System.out.println("main server -> next.js request url = " + requestUrl);
        ResponseEntity<String> result = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class);
        log.info("waitingPage result : " + result);

        return result;
    }

    private HttpHeaders getAllHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
//        Collections.list(headerNames)
//                .forEach(key -> {
//                    headers.add(key, request.getHeader(key));
//                    System.out.println("key = " + key);
//                    System.out.println("request.getHeader(key) = " + request.getHeader(key));
//                });
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return headers;
    }
}
