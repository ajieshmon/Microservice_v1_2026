package com.example.gatewayservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private final String secret;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username, String role) {

        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setId(jti)   // 🔥 JTI added
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateRefreshToken(String username) {

        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setId(jti)   // 🔥 JTI added
                .setSubject(username)
                .claim("type", "REFRESH")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}