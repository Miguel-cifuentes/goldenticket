package com.example.goldenticket2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Event create/update payload")
public record EventRequest(
        @NotBlank @Schema(example = "Noche Llanera Golden") String name,
        @NotBlank @Schema(example = "Concierto y fiesta cultural en Villavicencio.") String description,
        @Future @NotNull @Schema(example = "2026-09-21T20:00:00") LocalDateTime date,
        @NotBlank @Schema(example = "Villavicencio, Meta - Club Social") String location,
        @NotNull @Schema(example = "75000") BigDecimal price,
        @Min(1) @Schema(example = "500") Integer capacity,
        @Schema(example = "https://cdn.example.com/event.jpg") String image,
        @Valid List<TicketTypeRequest> ticketTypes
) {
}