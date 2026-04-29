package ru.diasoft.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import ru.diasoft.spring.config.BaseConfig;
import ru.diasoft.spring.domain.Answer;
import ru.diasoft.spring.domain.Question;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplCheckAnswerTest {

    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        // Используем mock QuestionService
        QuestionService mockQuestionService = mock(QuestionService.class);
        BaseConfig baseConfig = mock(BaseConfig.class);
        MessageSource messageSource = mock(MessageSource.class);
        baseConfig.setLimit(3);
        testService = new TestServiceImpl(mockQuestionService, baseConfig,messageSource);
    }

    @Test
    @DisplayName("checkAnswer возвращает true для правильного одиночного ответа")
    void checkAnswer_WithCorrectSingleAnswer_ReturnsTrue() {
        // Given
        Map<Integer, Answer> answers = new HashMap<>();
        answers.put(1, new Answer("Correct", true));
        answers.put(2, new Answer("Wrong", false));
        Question question = new Question("Test?", answers, false);
        
        // When & Then
        assertTrue(testService.checkAnswer("1", question));
    }

    @Test
    @DisplayName("checkAnswer возвращает false для неправильного одиночного ответа")
    void checkAnswer_WithIncorrectSingleAnswer_ReturnsFalse() {
        // Given
        Map<Integer, Answer> answers = new HashMap<>();
        answers.put(1, new Answer("Correct", true));
        answers.put(2, new Answer("Wrong", false));
        Question question = new Question("Test?", answers, false);
        
        // When & Then
        assertFalse(testService.checkAnswer("2", question));
    }

    @Test
    @DisplayName("checkAnswer возвращает true для всех правильных множественных ответов")
    void checkAnswer_WithAllCorrectMultiAnswers_ReturnsTrue() {
        // Given
        Map<Integer, Answer> answers = new HashMap<>();
        answers.put(1, new Answer("Correct A", true));
        answers.put(2, new Answer("Wrong B", false));
        answers.put(3, new Answer("Correct C", true));
        Question question = new Question("Test?", answers, true);
        
        // When & Then
        assertTrue(testService.checkAnswer("1,3", question));
    }

    @Test
    @DisplayName("checkAnswer возвращает false если выбраны не все правильные ответы")
    void checkAnswer_WithNotAllCorrectAnswers_ReturnsFalse() {
        // Given
        Map<Integer, Answer> answers = new HashMap<>();
        answers.put(1, new Answer("Correct A", true));
        answers.put(2, new Answer("Wrong B", false));
        answers.put(3, new Answer("Correct C", true));
        Question question = new Question("Test?", answers, true);
        
        // When & Then
        assertFalse(testService.checkAnswer("1", question));
        assertFalse(testService.checkAnswer("3", question));
    }

    @Test
    @DisplayName("checkAnswer возвращает false если выбран лишний неправильный ответ")
    void checkAnswer_WithExtraIncorrectAnswer_ReturnsFalse() {
        // Given
        Map<Integer, Answer> answers = new HashMap<>();
        answers.put(1, new Answer("Correct A", true));
        answers.put(2, new Answer("Wrong B", false));
        answers.put(3, new Answer("Correct C", true));
        Question question = new Question("Test?", answers, true);
        
        // When & Then
        assertFalse(testService.checkAnswer("1,2,3", question));
    }

    @Test
    @DisplayName("checkAnswer корректно обрабатывает пробелы в ответе")
    void checkAnswer_WithSpaces_ReturnsCorrectResult() {
        // Given
        Map<Integer, Answer> answers = new HashMap<>();
        answers.put(1, new Answer("Correct A", true));
        answers.put(2, new Answer("Wrong B", false));
        answers.put(3, new Answer("Correct C", true));
        Question question = new Question("Test?", answers, true);
        
        // When & Then
        assertTrue(testService.checkAnswer(" 1 , 3 ", question));
        assertTrue(testService.checkAnswer(" 1, 3", question));
        assertTrue(testService.checkAnswer("1 ,3 ", question));
    }

    @Test
    @DisplayName("checkAnswer игнорирует пустые элементы после split")
    void checkAnswer_WithEmptyElements_ReturnsCorrectResult() {
        // Given
        Map<Integer, Answer> answers = new HashMap<>();
        answers.put(1, new Answer("Correct A", true));
        answers.put(2, new Answer("Wrong B", false));
        answers.put(3, new Answer("Correct C", true));
        Question question = new Question("Test?", answers, true);
        
        // When & Then
        assertTrue(testService.checkAnswer("1,,3", question));
        assertTrue(testService.checkAnswer(",1,3,", question));
    }


}