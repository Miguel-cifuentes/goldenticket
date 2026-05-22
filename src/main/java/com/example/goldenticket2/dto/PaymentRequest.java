package com.example.goldenticket2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to create a ticket purchase and payment")
public record PaymentRequest(
        @NotNull @Schema(example = "1") Long eventId,
        @NotNull @Schema(example = "1") Long ticketTypeId,
        @Min(1) @Schema(example = "2") Integer quantity
) {
}
