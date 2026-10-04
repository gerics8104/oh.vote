package hu.oh.vote.service.szavazas.util;

import java.security.SecureRandom;

public final class SzavazasIdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private SzavazasIdGenerator() {
    }

    public static String generate() {
        char first = (char) ('A' + RANDOM.nextInt(26));
        char second = (char) ('A' + RANDOM.nextInt(26));
        int number = RANDOM.nextInt(10_000);
        return "%c%c%04d".formatted(first, second, number);
    }
}