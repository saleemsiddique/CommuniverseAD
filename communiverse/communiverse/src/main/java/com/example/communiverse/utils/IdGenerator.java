package com.example.communiverse.utils;

import java.util.UUID;

public class IdGenerator {
    public static String generateId() {
        return UUID.randomUUID().toString().split("-")[0];
    }
}
