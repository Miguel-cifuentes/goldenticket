package com.example.goldenticket2.controller;

import com.example.goldenticket2.dto.TicketResponse;
import com.example.goldenticket2.dto.TicketValidationResponse;
import com.example.goldenticket2.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/my")
    @Operation(summary = "List tickets owned by the authenticated user")
    public ResponseEntity<List<TicketResponse>> myTickets() {
        return ResponseEntity.ok(ticketService.myTickets());
    }

    @PostMapping("/validate/{qrCode}")
    @Operation(summary = "Validate and mark a QR ticket as used")
    public ResponseEntity<TicketValidationResponse> validate(@PathVariable String qrCode) {
        return ResponseEntity.ok(ticketService.validateQr(qrCode));
    }
}
