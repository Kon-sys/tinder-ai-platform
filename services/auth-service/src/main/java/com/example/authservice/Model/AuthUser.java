package com.example.authservice.Model;

import jakarta.persistence.*;
import lombok.*;
import com.example.authservice.Model.enums.Role;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "auth_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_auth_user_login", columnNames = "login")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = Role.ROLE_USER;
        }

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}