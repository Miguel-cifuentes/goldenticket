package com.example.goldenticket2.dto;

import com.example.goldenticket2.entity.RoleName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(description = "JWT authentication response")
public record AuthResponse(
        Long userId,
        String fullName,
        String email,
        Set<RoleName> roles,
        String token,
        String tokenType
) {
}
