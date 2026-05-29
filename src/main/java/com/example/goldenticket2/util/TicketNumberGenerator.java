package com.example.goldenticket2.util;

import java.security.SecureRandom;

public final class TicketNumberGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private TicketNumberGenerator() {
    }

    public static String next() {
        StringBuilder builder = new StringBuilder("GT-");
        for (int i = 0; i < 12; i++) {
            builder.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return builder.toString();
    }
}
