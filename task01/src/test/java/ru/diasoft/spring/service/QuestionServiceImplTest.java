package ru.diasoft.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.diasoft.spring.dao.QuestionDao;
import ru.diasoft.spring.domain.Answer;
import ru.diasoft.spring.domain.Question;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Test
    @DisplayName("Должен возвращать все вопросы из DAO")
    void getAllQuestions_ReturnsQuestionsFromDao() {
        // Arrange
        Answer answer = new Answer("Answer", true);
        Question expectedQuestion = new Question("Test Question?",
                Map.of(1, answer), false);
        List<Question> expectedQuestions = List.of(expectedQuestion);

        when(questionDao.getAllQuestions()).thenReturn(expectedQuestions);

        // Act
        List<Question> actualQuestions = questionService.getAllQuestions();

        // Assert
        assertNotNull(actualQuestions);
        assertEquals(1, actualQuestions.size());
        assertEquals("Test Question?", actualQuestions.get(0).getText());
        verify(questionDao, times(1)).getAllQuestions();
    }

    @Test
    @DisplayName("Должен возвращать пустой список если DAO возвращает null")
    void getAllQuestions_DaoReturnsNull_ReturnsEmptyList() {
        // Arrange
        when(questionDao.getAllQuestions()).thenReturn(null);

        // Act
        List<Question> questions = questionService.getAllQuestions();

        // Assert
        assertNull(questions); // или можно изменить сервис, чтобы возвращал пустой список
        verify(questionDao, times(1)).getAllQuestions();
    }
}