package ru.diasoft.spring.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    @DisplayName("Должен корректно создавать вопрос")
    void questionCreation() {
        Answer answer1 = new Answer("Answer 1", true);
        Answer answer2 = new Answer("Answer 2", false);
        
        Question question = new Question("Test Question?", 
            Map.of(1, answer1, 2, answer2), false);
        
        assertEquals("Test Question?", question.getText());
        assertFalse(question.isIsMultiChoice());
        assertEquals(2, question.getAnswers().size());
        assertTrue(question.getAnswers().get(1).isCorrect());
        assertFalse(question.getAnswers().get(2).isCorrect());
    }

    @Test
    @DisplayName("toString должен возвращать форматированный текст")
    void toString_ReturnsFormattedText() {
        Answer answer1 = new Answer("Correct", true);
        Answer answer2 = new Answer("Wrong", false);
        
        Question question = new Question("What is 2+2?", 
            Map.of(1, answer1, 2, answer2), false);
        
        String result = question.toString();
        
        assertTrue(result.contains("What is 2+2?"));
        assertTrue(result.contains("1. Correct"));
        assertTrue(result.contains("2. Wrong"));
    }
}