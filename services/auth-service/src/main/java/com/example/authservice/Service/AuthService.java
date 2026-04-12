package com.example.authservice.Service;

import com.example.authservice.Dto.request.LoginRequest;
import com.example.authservice.Dto.request.LogoutRequest;
import com.example.authservice.Dto.request.RefreshTokenRequest;
import com.example.authservice.Dto.request.RegisterRequest;
import com.example.authservice.Dto.response.AuthResponse;
import com.example.authservice.Dto.response.UserMeResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(LogoutRequest request);

    UserMeResponse getCurrentUser(UUID userId);
}