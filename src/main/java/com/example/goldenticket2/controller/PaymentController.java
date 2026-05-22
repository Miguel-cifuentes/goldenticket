package com.example.goldenticket2.controller;

import com.example.goldenticket2.dto.PaymentRequest;
import com.example.goldenticket2.dto.PaymentResponse;
import com.example.goldenticket2.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/purchase")
    @Operation(summary = "Create a purchase and Wompi Sandbox checkout URL")
    public ResponseEntity<PaymentResponse> purchase(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPurchase(request));
    }

    @PostMapping("/webhook")
    @Operation(summary = "Process signed Wompi webhook notifications")
    public ResponseEntity<PaymentResponse> webhook(@RequestBody JsonNode payload) {
        return ResponseEntity.ok(paymentService.processWebhook(payload));
    }

    @GetMapping("/verify/{transactionId}")
    @Operation(summary = "Verify a transaction directly with Wompi")
    public ResponseEntity<PaymentResponse> verify(@PathVariable String transactionId) {
        return ResponseEntity.ok(paymentService.verifyPayment(transactionId));
    }
}
