package com.example.goldenticket2.dto;

import com.example.goldenticket2.entity.RoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Schema(description = "Data required to create a platform account")
public record RegisterRequest(
        @NotBlank @Schema(example = "Maria Fernanda Torres") String fullName,
        @Email @NotBlank @Schema(example = "maria@example.com") String email,
        @NotBlank @Size(min = 8) @Schema(example = "Secure123*") String password,
        @Schema(example = "[\"USER\"]") Set<RoleName> roles
) {
}
