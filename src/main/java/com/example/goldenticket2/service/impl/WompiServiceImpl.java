package com.example.goldenticket2.service.impl;

import com.example.goldenticket2.entity.Payment;
import com.example.goldenticket2.service.WompiService;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class WompiServiceImpl implements WompiService {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.wompi.base-url}")
    private String baseUrl;

    @Value("${app.wompi.public-key}")
    private String publicKey;

    @Value("${app.wompi.private-key}")
    private String privateKey;

    @Value("${app.wompi.events-secret}")
    private String eventsSecret;

    @Value("${app.wompi.integrity-secret}")
    private String integritySecret;

    @Value("${app.wompi.redirect-url}")
    private String redirectUrl;

    @Override
    public String buildCheckoutUrl(Payment payment) {
        String signature = sha256(payment.getReference() + payment.getAmountInCents() + "COP" + integritySecret);

        return "https://checkout.wompi.co/p/?" +
                "public-key=" + encode(publicKey) +
                "&currency=COP" +
                "&amount-in-cents=" + payment.getAmountInCents() +
                "&reference=" + encode(payment.getReference()) +
                "&signature%3Aintegrity=" + signature +
                "&redirect-url=" + encode(redirectUrl);
    }

    @Override
    public JsonNode verifyTransaction(String transactionId) {
        return webClientBuilder.baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + privateKey)
                .build()
                .get()
                .uri("/transactions/{id}", transactionId)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    @Override
    public boolean isValidEventSignature(JsonNode payload) {
        JsonNode signature = payload.path("signature");
        JsonNode properties = signature.path("properties");
        String checksum = signature.path("checksum").asText("");
        Long timestamp = payload.path("timestamp").isMissingNode() ? null : payload.path("timestamp").asLong();
        if (!properties.isArray() || checksum.isBlank() || timestamp == null) {
            return false;
        }
        StringBuilder raw = new StringBuilder();
        for (JsonNode property : properties) {
            raw.append(resolveProperty(payload, property.asText()));
        }
        raw.append(timestamp).append(eventsSecret);
        return sha256(raw.toString()).equalsIgnoreCase(checksum);
    }

    private String resolveProperty(JsonNode payload, String dottedPath) {
        JsonNode current = payload;
        for (String part : dottedPath.split("\\.")) {
            current = current.path(part);
        }
        return current.isMissingNode() || current.isNull() ? "" : current.asText();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to calculate SHA-256 signature", ex);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

