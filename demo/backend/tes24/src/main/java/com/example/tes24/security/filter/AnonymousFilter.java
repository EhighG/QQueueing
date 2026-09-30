package com.example.tes24.security.filter;

import com.example.tes24.properties.AllowedUrlProperties;
import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.security.servlet.AntPathRequestMatcherProvider;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
public class AnonymousFilter extends OncePerRequestFilter {
    private final List<String> allowedUrls;
    private final List<String> allowedPatterns;
    private final List<String> anonymous;
    private final AntPathRequestMatcherProvider antPathRequestMatcherProvider = new AntPathRequestMatcherProvider(s -> s);

    public AnonymousFilter(AllowedUrlProperties allowedUrlProperties) {
        Assert.notNull(allowedUrlProperties, "allowedUrlProperties must not be null");
        Assert.notNull(allowedUrlProperties.getAnonymous(), "anonymous must not be null");

        this.allowedUrls = allowedUrlProperties.getUrls();
        this.allowedPatterns = allowedUrlProperties.getPatterns();
        this.anonymous = allowedUrlProperties.getAnonymous();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (allowedUrls.contains(requestURI) || anonymous.contains(requestURI) || isMatched(request)) {
            SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(request.getRemoteHost(), new Object(), List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
        }

        filterChain.doFilter(request, response);
    }

    private boolean isMatched(HttpServletRequest request) {
        return allowedPatterns.stream().anyMatch(pattern -> antPathRequestMatcherProvider.getRequestMatcher(pattern).matches(request));
    }
}
