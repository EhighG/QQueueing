package com.example.tes24.security.filter;

import com.example.tes24.security.token.JwtAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.StringTokenizer;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(header);
            if (stringTokenizer.hasMoreTokens()) {
                String type = stringTokenizer.nextToken();
                if (type != null && type.equals("Bearer") && stringTokenizer.hasMoreTokens()) {
                    String credentials = stringTokenizer.nextToken();
                    if (credentials != null) {
                        SecurityContextHolder.getContext().setAuthentication(attemptAuthenticate(credentials));
                    }
                }
            }
        } else {
            throw new BadCredentialsException("Can't authenticate.");
        }

        filterChain.doFilter(request, response);
    }

    protected Authentication attemptAuthenticate(String token) {
        Authentication authentication = JwtAuthenticationToken.unauthenticated(token);
        return authenticationManager.authenticate(authentication);
    }
}
