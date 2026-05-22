package com.example.goldenticket2.service.impl;

import com.example.goldenticket2.dto.TicketResponse;
import com.example.goldenticket2.dto.TicketValidationResponse;
import com.example.goldenticket2.entity.Ticket;
import com.example.goldenticket2.entity.TicketStatus;
import com.example.goldenticket2.entity.User;
import com.example.goldenticket2.mapper.TicketMapper;
import com.example.goldenticket2.repository.TicketRepository;
import com.example.goldenticket2.service.TicketService;
import com.example.goldenticket2.util.SecurityUtils;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> myTickets() {
        User user = SecurityUtils.currentUser();
        return ticketRepository.findByOwnerId(user.getId()).stream()
                .map(ticketMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TicketValidationResponse validateQr(String qrCode) {
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElse(null);
        if (ticket == null) {
            return new TicketValidationResponse(false, "Ticket does not exist", null);
        }
        if (ticket.getStatus() == TicketStatus.USED) {
            return new TicketValidationResponse(false, "Ticket was already used", ticketMapper.toResponse(ticket));
        }
        if (ticket.getStatus() != TicketStatus.ISSUED) {
            return new TicketValidationResponse(false, "Ticket is not active", ticketMapper.toResponse(ticket));
        }
        ticket.setStatus(TicketStatus.USED);
        ticket.setUsedAt(Instant.now());
        return new TicketValidationResponse(true, "Ticket validated successfully", ticketMapper.toResponse(ticket));
    }
}
