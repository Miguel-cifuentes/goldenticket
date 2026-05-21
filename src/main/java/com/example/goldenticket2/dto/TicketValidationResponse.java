package com.example.goldenticket2.dto;

public record TicketValidationResponse(
        boolean valid,
        String message,
        TicketResponse ticket
) {
}
