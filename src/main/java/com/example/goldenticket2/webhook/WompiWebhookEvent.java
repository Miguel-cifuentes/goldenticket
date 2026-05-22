package com.example.goldenticket2.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WompiWebhookEvent(
        @JsonProperty("event")
        String event,
        Instant receivedAt,
        JsonNode payload
        @JsonProperty("timestamp")
                Long timestamp,
        @JsonProperty("data")
        WompiWebhookData data,
        @JsonProperty("signature")
        WompiWebhookSignature signature
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WompiWebhookData(
            @JsonProperty("transaction")
            WompiTransaction transaction
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WompiTransaction(
            @JsonProperty("id")
            String id,
            @JsonProperty("reference")
            String reference,
            @JsonProperty("status")
            String status,
            @JsonProperty("amount_in_cents")
            Long amountInCents,
            @JsonProperty("currency")
            String currency,
            @JsonProperty("payment_method_type")
            String paymentMethodType,
            @JsonProperty("customer_email")
            String customerEmail,
            @JsonProperty("created_at")
            String createdAt,
            @JsonProperty("finalized_at")
            String finalizedAt,
            @JsonProperty("raw")
            JsonNode raw
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record WompiWebhookSignature(
            @JsonProperty("properties")
            List<String> properties,
            @JsonProperty("checksum")
            String checksum
    ) {
    }
}
