package com.example.authservice.Service.impl;

import com.example.authservice.Model.AuthUser;
import com.example.authservice.Service.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(AuthUser user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessExpirationMs);

        return Jwts.builder()
                .subject(user.getLogin())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    @Override
    public String generateRefreshToken(AuthUser user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .subject(user.getLogin())
                .claim("type", "refresh")
                .claim("userId", user.getId())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractLogin(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}