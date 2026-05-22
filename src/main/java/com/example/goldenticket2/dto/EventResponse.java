package com.example.goldenticket2.dto;

import com.example.goldenticket2.entity.EventStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EventResponse(
        Long id,
        String name,
        String description,
        LocalDateTime date,
        String location,
        BigDecimal price,
        Integer capacity,
        Integer availableCapacity,
        String image,
        EventStatus status,
        Long organizerId,
        String organizerName,
        List<TicketTypeResponse> ticketTypes
) {
}