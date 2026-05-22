package com.example.goldenticket2.dto;

import com.example.goldenticket2.entity.TicketStatus;
import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String ticketNumber,
        String qrCode,
        TicketStatus status,
        String eventName,
        LocalDateTime eventDate,
        String location,
        String ownerName,
        String pdfPath
) {
}
