package com.example.authservice.Dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    @Size(min = 5, max = 50)
    private String login;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    private String confirmPassword;

}