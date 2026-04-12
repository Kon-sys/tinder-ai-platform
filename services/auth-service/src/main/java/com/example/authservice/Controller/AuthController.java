package com.example.authservice.Controller;

import com.example.authservice.Dto.request.LoginRequest;
import com.example.authservice.Dto.request.RefreshTokenRequest;
import com.example.authservice.Dto.request.RegisterRequest;
import com.example.authservice.Dto.response.AuthResponse;
import com.example.authservice.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody RegisterRequest request) {
        System.out.println("Произошел запрос к регистрации");
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.signIn(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}