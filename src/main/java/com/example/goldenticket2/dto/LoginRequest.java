package com.example.goldenticket2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credentials used to authenticate a user")
public record LoginRequest(
        @Email @NotBlank @Schema(example = "admin@goldenticket.com") String email,
        @NotBlank @Schema(example = "Admin123*") String password
) {
}
