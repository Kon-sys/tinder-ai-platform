package com.example.authservice.Service.impl;

import com.example.authservice.Dto.request.LoginRequest;
import com.example.authservice.Dto.request.RegisterRequest;
import com.example.authservice.Dto.response.AuthResponse;
import com.example.authservice.Exception.AuthException;
import com.example.authservice.Model.AuthUser;
import com.example.authservice.Model.RefreshToken;
import com.example.authservice.Model.enums.Role;
import com.example.authservice.Repository.AuthUserRepository;
import com.example.authservice.Repository.RefreshTokenRepository;
import com.example.authservice.Service.AuthService;
import com.example.authservice.Service.JwtTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.authservice.Dto.request.RefreshTokenRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    @Transactional
    public AuthResponse signUp(RegisterRequest request) {
        String login = request.getLogin().trim();

        if (authUserRepository.existsByLogin(login)) {
            throw new AuthException("Login already exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AuthException("Passwords do not match");
        }

        AuthUser user = AuthUser.builder()
                .login(login)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        AuthUser savedUser = authUserRepository.save(user);

        return buildJwtToken(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse signIn(LoginRequest request) {
        AuthUser user = authUserRepository.findByLogin(request.getLogin().trim())
                .orElseThrow(() -> new AuthException("Invalid login or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid login or password");
        }

        refreshTokenRepository.deleteAllByUser(user);

        return buildJwtToken(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthException("Refresh token not found"));

        if (Boolean.TRUE.equals(storedToken.getRevoked())) {
            throw new AuthException("Refresh token revoked");
        }

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthException("Refresh token expired");
        }

        if (!jwtTokenService.isTokenValid(request.getRefreshToken())) {
            throw new AuthException("Invalid refresh token");
        }

        AuthUser user = storedToken.getUser();

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        return buildJwtToken(user);
    }

    private AuthResponse buildAuthResponse(AuthUser user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .login(user.getLogin())
                .role(user.getRole().name())
                .build();
    }


    private AuthResponse buildJwtToken(AuthUser user) {
        String newAccessToken = jwtTokenService.generateAccessToken(user);
        String newRefreshToken = jwtTokenService.generateRefreshToken(user);

        RefreshToken newStoredToken = RefreshToken.builder()
                .token(newRefreshToken)
                .user(user)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshExpirationMs)))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newStoredToken);

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }
}
