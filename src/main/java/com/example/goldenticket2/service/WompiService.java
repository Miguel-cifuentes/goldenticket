package com.example.goldenticket2.service;

import com.example.goldenticket2.entity.Payment;
import com.fasterxml.jackson.databind.JsonNode;

public interface WompiService {
    String buildCheckoutUrl(Payment payment);

    JsonNode verifyTransaction(String transactionId);

    boolean isValidEventSignature(JsonNode payload);
}