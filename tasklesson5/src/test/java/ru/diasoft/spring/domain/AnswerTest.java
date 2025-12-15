package ru.diasoft.spring.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnswerTest {

    @Test
    @DisplayName("Должен корректно создавать ответ")
    void answerCreation() {
        Answer answer = new Answer("Test Answer", true);
        
        assertEquals("Test Answer", answer.getValue());
        assertTrue(answer.isCorrect());
    }

    @Test
    @DisplayName("toString должен возвращать значение ответа")
    void toString_ReturnsValue() {
        Answer answer = new Answer("Test Answer", true);
        
        assertEquals("Test Answer", answer.toString());
    }
}