package ru.diasoft.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.diasoft.spring.dao.QuestionDao;
import ru.diasoft.spring.domain.Answer;
import ru.diasoft.spring.domain.Question;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceImplTest {

    @Mock
    private QuestionDao questionDao;

    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        questionService = new QuestionServiceImpl(questionDao);
    }

    @Test
    void getAllQuestions_WithLocale_CallsDaoWithSameLocale() {
        // Given
        Locale locale = Locale.FRENCH;
        List<Question> expectedQuestions = List.of(createTestQuestion());
        when(questionDao.getAllQuestions(locale)).thenReturn(expectedQuestions);

        // When
        List<Question> result = questionService.getAllQuestions(locale);

        // Then
        assertEquals(expectedQuestions, result);
        verify(questionDao, times(1)).getAllQuestions(locale);
    }

    @Test
    void getAllQuestions_WithNullLocale_PassesNullToDao() {
        // Given
        List<Question> expectedQuestions = List.of(createTestQuestion());
        when(questionDao.getAllQuestions(null)).thenReturn(expectedQuestions);

        // When
        List<Question> result = questionService.getAllQuestions(null);

        // Then
        assertEquals(expectedQuestions, result);
        verify(questionDao, times(1)).getAllQuestions(null);
    }

    @Test
    void getAllQuestions_WithEnglishLocale_ReturnsEnglishQuestions() {
        // Given
        Locale english = Locale.ENGLISH;
        List<Question> englishQuestions = List.of(
                new Question("What is 2+2?", Map.of(1, new Answer("4", true)), false)
        );
        when(questionDao.getAllQuestions(english)).thenReturn(englishQuestions);

        // When
        List<Question> result = questionService.getAllQuestions(english);

        // Then
        assertEquals(1, result.size());
        assertEquals("What is 2+2?", result.get(0).getText());
    }

    @Test
    void getAllQuestions_WithRussianLocale_ReturnsRussianQuestions() {
        // Given
        Locale russian = new Locale("ru");
        List<Question> russianQuestions = List.of(
                new Question("Сколько будет 2+2?", Map.of(1, new Answer("4", true)), false)
        );
        when(questionDao.getAllQuestions(russian)).thenReturn(russianQuestions);

        // When
        List<Question> result = questionService.getAllQuestions(russian);

        // Then
        assertEquals(1, result.size());
        assertEquals("Сколько будет 2+2?", result.get(0).getText());
    }

    @Test
    void getAllQuestions_ReturnsEmptyList_WhenDaoReturnsEmpty() {
        // Given
        Locale locale = Locale.GERMAN;
        when(questionDao.getAllQuestions(locale)).thenReturn(Collections.emptyList());

        // When
        List<Question> result = questionService.getAllQuestions(locale);

        // Then
        assertTrue(result.isEmpty());
        verify(questionDao, times(1)).getAllQuestions(locale);
    }

    @Test
    void constructor_InjectsDaoDependency() {
        // When
        QuestionServiceImpl service = new QuestionServiceImpl(questionDao);

        // Then
        assertNotNull(service);
        // Можно проверить через рефлексию, что dao установлен
    }

    private Question createTestQuestion() {
        Map<Integer, Answer> answers = new HashMap<>();
        answers.put(1, new Answer("Correct", true));
        answers.put(2, new Answer("Wrong", false));
        return new Question("Test?", answers, false);
    }
}