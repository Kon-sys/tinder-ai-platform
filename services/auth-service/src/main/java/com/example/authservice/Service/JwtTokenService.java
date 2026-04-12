package com.example.authservice.Service;

import com.example.authservice.Model.AuthUser;

public interface JwtTokenService {

    String generateAccessToken(AuthUser user);

    String generateRefreshToken(AuthUser user);

    boolean isTokenValid(String token);

    String extractLogin(String token);
}
