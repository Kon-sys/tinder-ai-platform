package com.example.authservice.Service;

import com.example.authservice.Dto.request.LoginRequest;
import com.example.authservice.Dto.request.RefreshTokenRequest;
import com.example.authservice.Dto.request.RegisterRequest;
import com.example.authservice.Dto.response.AuthResponse;

public interface AuthService {

    AuthResponse signUp(RegisterRequest request);

    AuthResponse signIn(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);
}