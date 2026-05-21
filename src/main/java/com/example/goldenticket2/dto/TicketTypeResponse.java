package com.example.goldenticket2.dto;

import com.example.goldenticket2.entity.TicketCategory;
import java.math.BigDecimal;

public record TicketTypeResponse(
        Long id,
        TicketCategory category,
        BigDecimal price,
        Integer stock,
        Integer availableStock
) {
}
