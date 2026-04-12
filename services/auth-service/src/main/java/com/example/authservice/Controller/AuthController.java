package com.example.authservice.Controller;

import com.example.authservice.Dto.request.LoginRequest;
import com.example.authservice.Dto.request.LogoutRequest;
import com.example.authservice.Dto.request.RefreshTokenRequest;
import com.example.authservice.Dto.request.RegisterRequest;
import com.example.authservice.Dto.response.AuthResponse;
import com.example.authservice.Dto.response.MessageResponse;
import com.example.authservice.Dto.response.UserMeResponse;
import com.example.authservice.security.CustomUserDetails;
import com.example.authservice.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserMeResponse response = authService.getCurrentUser(userDetails.getId());
        return ResponseEntity.ok(response);
    }
}