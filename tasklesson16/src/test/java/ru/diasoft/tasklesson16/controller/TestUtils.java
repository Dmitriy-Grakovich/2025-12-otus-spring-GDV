package ru.diasoft.tasklesson16.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class TestUtils {
    
    private TestUtils() {
        // Utility class
    }
    
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
