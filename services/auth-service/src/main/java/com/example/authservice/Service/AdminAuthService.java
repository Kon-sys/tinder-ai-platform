package com.example.authservice.Service;

import java.util.UUID;
import com.example.authservice.Dto.response.AdminUserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminAuthService {

    void blockUser(UUID userId);

    void unblockUser(UUID userId);

    AdminUserResponse getUserById(UUID userId);

    List<AdminUserResponse> getAllUsers();
}