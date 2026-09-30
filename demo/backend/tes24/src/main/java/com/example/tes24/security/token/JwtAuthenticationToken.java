package com.example.tes24.security.token;

import io.jsonwebtoken.lang.Assert;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private Object principal;
    private String credentials;

    public static JwtAuthenticationToken unauthenticated(String credentials) {
        return new JwtAuthenticationToken(credentials);
    }

    public static JwtAuthenticationToken authenticated(Object principal, String credentials,
                                                       Collection<? extends GrantedAuthority> authorities) {
        return new JwtAuthenticationToken(principal, credentials, authorities);
    }
    private JwtAuthenticationToken(String credentials) {
        super(null);
        this.principal = null;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }

    private JwtAuthenticationToken(Object principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public String getToken() {
        return this.credentials;
    }
}
