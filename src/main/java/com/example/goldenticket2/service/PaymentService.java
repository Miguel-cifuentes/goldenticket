package com.example.goldenticket2.service;

import com.example.goldenticket2.dto.PaymentRequest;
import com.example.goldenticket2.dto.PaymentResponse;
import com.fasterxml.jackson.databind.JsonNode;

public interface PaymentService {
    PaymentResponse createPurchase(PaymentRequest request);

    PaymentResponse processWebhook(JsonNode payload);

    PaymentResponse verifyPayment(String transactionId);
}