package com.example.goldenticket2.mapper;

import com.example.goldenticket2.dto.TicketResponse;
import com.example.goldenticket2.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getQrCode(),
                ticket.getStatus(),
                ticket.getEvent().getName(),
                ticket.getEvent().getEventDate(),
                ticket.getEvent().getLocation(),
                ticket.getOwner().getFullName(),
                ticket.getPdfPath()
        );
    }
}
