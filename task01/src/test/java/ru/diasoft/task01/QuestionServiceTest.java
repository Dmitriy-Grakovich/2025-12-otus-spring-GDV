package ru.diasoft.task01;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.diasoft.task01.dao.QuestionDao;
import ru.diasoft.task01.domain.Answer;
import ru.diasoft.task01.domain.Question;
import ru.diasoft.task01.service.QuestionService;
import ru.diasoft.task01.service.QuestionServiceImpl;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionDao questionDao;

    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        questionService = new QuestionServiceImpl(questionDao);
    }

    @Test
    void getAllQuestionsShouldReturnCorrectNumberOfQuestions() {
        // Arrange
        List<Question> expectedQuestions = Arrays.asList(
            new Question("Test question 1", 
                Arrays.asList(new Answer("Answer 1", true)), false),
            new Question("Test question 2", 
                Arrays.asList(new Answer("Answer 2", false)), false)
        );
        
        when(questionDao.getAllQuestions()).thenReturn(expectedQuestions);

        // Act
        List<Question> actualQuestions = questionService.getAllQuestions();

        // Assert
        assertEquals(2, actualQuestions.size());
        assertEquals(expectedQuestions, actualQuestions);
    }
}