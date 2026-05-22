package com.example.goldenticket2.dto;

import com.example.goldenticket2.entity.PaymentStatus;
import com.example.goldenticket2.entity.PurchaseStatus;
import java.math.BigDecimal;

public record PaymentResponse(
        Long purchaseId,
        Long paymentId,
        String reference,
        String wompiTransactionId,
        PaymentStatus paymentStatus,
        PurchaseStatus purchaseStatus,
        BigDecimal amount,
        Long amountInCents,
        String checkoutUrl
) {
}
