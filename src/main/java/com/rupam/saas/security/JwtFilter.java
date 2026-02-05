package com.rupam.saas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SecretKey key;

    public JwtFilter(JwtUtil jwtUtil, @Value("${jwt.secret}") String secret) {
        this.jwtUtil = jwtUtil;
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // Use the key directly or delegate?
                // Since JwtUtil methods return individual fields and re-parse every time,
                // it's more efficient to parse once here if we need multiple fields.
                // However, duplication of parsing logic is bad.
                // Let's stick to using the key consistent with JwtUtil.

                var claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                // 🔥 SaaS magic
                Long companyId = claims.get("companyId", Long.class);
                request.setAttribute("companyId", companyId);

                var authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + role));

                var auth = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                // If token is invalid, we just don't set authentication.
                // Spring Security will handle 403 if endpoint requires auth.
                // Or we can explicitly set status/error here if strictly required.
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
