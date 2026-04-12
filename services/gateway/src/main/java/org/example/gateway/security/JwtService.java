package org.example.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getLoginFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Expired JwtException", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JwtException", e);
        } catch (MalformedJwtException e) {
            log.error("Malformed JwtException", e);
        } catch (SecurityException e) {
            log.error("Security JwtException", e);
        } catch (Exception e) {
            log.error("Invalid token", e);
        }
        return false;
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}