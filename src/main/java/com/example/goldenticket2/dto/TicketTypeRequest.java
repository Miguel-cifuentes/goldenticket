package com.example.goldenticket2.dto;

import com.example.goldenticket2.entity.TicketCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TicketTypeRequest(
        @NotNull TicketCategory category,
        @NotNull BigDecimal price,
        @Min(0) Integer stock
) {
}
