package com.example.authservice.Controller;

import com.example.authservice.Dto.response.MessageResponse;
import com.example.authservice.Service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PatchMapping("/users/{userId}/block")
    public ResponseEntity<MessageResponse> blockUser(@PathVariable UUID userId) {
        adminAuthService.blockUser(userId);
        return ResponseEntity.ok(new MessageResponse("User blocked successfully"));
    }

    @PatchMapping("/users/{userId}/unblock")
    public ResponseEntity<MessageResponse> unblockUser(@PathVariable UUID userId) {
        adminAuthService.unblockUser(userId);
        return ResponseEntity.ok(new MessageResponse("User unblocked successfully"));
    }
}