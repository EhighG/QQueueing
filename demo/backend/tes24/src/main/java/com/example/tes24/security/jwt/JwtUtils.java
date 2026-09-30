package com.example.tes24.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    private final Long accessExpiration;
    private final Long refreshExpiration;
    private final SecretKey secretKey;

    private JwtUtils(@Value("${jwt.key}") String key,
                     @Value("${jwt.access-expiration}") long accessExpiration,
                     @Value("${jwt.refresh-expiration}") long refreshExpiration ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String createAccessToken(String id) {
        return createToken(id, accessExpiration);
    }
    public String createAccessToken(Long id) {
        return createToken(String.valueOf(id), accessExpiration);
    }

    public String createRefreshToken(String id) {
        return createToken(id, refreshExpiration);
    }
    public String createRefreshToken(Long id) {
        return createToken(String.valueOf(id), refreshExpiration);
    }

    public String createToken(String id, Long expiration) {
        var now = Instant.now();
        var expire = now.plus(expiration, ChronoUnit.MINUTES);

        return Jwts.builder()
                .claims()
                .add("id", id)
                .and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(expire))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(this.secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token, SecretKey key) throws JwtException, IllegalArgumentException {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String getId(String token) {
        return parseClaims(token, this.secretKey).get("id", String.class);
    }
}
