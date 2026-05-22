package com.example.goldenticket2.service;

import com.example.goldenticket2.entity.Ticket;

public interface PdfService {
    String generateTicketPdf(Ticket ticket);
}