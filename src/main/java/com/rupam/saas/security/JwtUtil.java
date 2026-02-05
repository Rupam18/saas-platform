package com.rupam.saas.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final String secret;
    private final long jwtExpiration;
    private final SecretKey key;

    public JwtUtil(
            @org.springframework.beans.factory.annotation.Value("${jwt.secret}") String secret,
            @org.springframework.beans.factory.annotation.Value("${jwt.expiration}") long jwtExpiration) {
        this.secret = secret;
        this.jwtExpiration = jwtExpiration;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 🔐 Generate JWT with role + companyId
    public String generateToken(String email, String role, Long companyId) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("companyId", companyId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String extractRole(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public Long extractCompanyId(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("companyId", Long.class);
    }
}
