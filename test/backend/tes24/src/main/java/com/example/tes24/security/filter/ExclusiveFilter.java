package com.example.tes24.security.filter;

import com.example.tes24.properties.AllowedUrlProperties;
import io.jsonwebtoken.lang.Assert;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
public class ExclusiveFilter extends OncePerRequestFilter {
    private final List<String> anonymous;

    public ExclusiveFilter(AllowedUrlProperties allowedUrlProperties) {
        Assert.notNull(allowedUrlProperties, "allowedUrlProperties must not be null");
        Assert.notNull(allowedUrlProperties.getAnonymous(), "anonymous must not be null");

        this.anonymous = allowedUrlProperties.getAnonymous();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (anonymous.contains(requestURI)) {
            SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(request.getRemoteHost(), new Object(),null ));
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank()) {
            SecurityContextHolder.getContext().setAuthentication(
                    new AnonymousAuthenticationToken(
                            "ANONYMOUS", "ANONYMOUS", List.of(new SimpleGrantedAuthority("ANONYMOUS"))));
            return true;
        }

        return false;
    }
}
