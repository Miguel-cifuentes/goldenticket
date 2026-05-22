package com.example.goldenticket2.service;

import com.example.goldenticket2.dto.TicketResponse;
import com.example.goldenticket2.dto.TicketValidationResponse;
import java.util.List;

public interface TicketService {
    List<TicketResponse> myTickets();

    TicketValidationResponse validateQr(String qrCode);
}
