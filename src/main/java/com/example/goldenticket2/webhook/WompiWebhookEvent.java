package com.example.goldenticket2.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WompiWebhookEvent {

    @JsonProperty("event")
    private String event;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("data")
    private WompiWebhookData data;

    @JsonProperty("signature")
    private WompiWebhookSignature signature;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WompiWebhookData {

        @JsonProperty("transaction")
        private WompiTransaction transaction;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WompiTransaction {

        @JsonProperty("id")
        private String id;

        @JsonProperty("reference")
        private String reference;

        @JsonProperty("status")
        private String status;

        @JsonProperty("amount_in_cents")
        private Long amountInCents;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("payment_method_type")
        private String paymentMethodType;

        @JsonProperty("customer_email")
        private String customerEmail;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("finalized_at")
        private String finalizedAt;

        @JsonProperty("raw")
        private JsonNode raw;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WompiWebhookSignature {

        @JsonProperty("properties")
        private List<String> properties;

        @JsonProperty("checksum")
        private String checksum;
    }
}