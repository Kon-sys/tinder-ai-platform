package com.example.authservice.Repository;

import com.example.authservice.Model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByLogin(String login);

    boolean existsByLogin(String login);
}
