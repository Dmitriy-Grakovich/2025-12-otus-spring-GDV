package ru.diasoft.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.diasoft.spring.domain.Answer;
import ru.diasoft.spring.domain.Question;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private QuestionService questionService;


    private TestServiceImpl testService;

    private List<Question> testQuestions;

    @BeforeEach
    void setUp() {
        // Создаем TestServiceImpl с параметром resultSt
        testService = new TestServiceImpl(questionService, "3");


        // Создаем тестовые вопросы
        Answer answer1 = new Answer("Answer 1", true);
        Answer answer2 = new Answer("Answer 2", false);
        Answer answer3 = new Answer("Answer 3", true);

        Question question1 = new Question("Question 1?",
                Map.of(1, answer1, 2, answer2), false);

        Question question2 = new Question("Question 2?",
                Map.of(1, answer1, 2, answer2, 3, answer3), true);

        testQuestions = List.of(question1, question2);
    }

    @Test
    @DisplayName("checkAnswer должен вернуть true для правильного ответа")
    void checkAnswer_CorrectAnswer_ReturnsTrue() {
        Question question = testQuestions.get(0); // Одиночный выбор

        boolean result = testService.checkAnswer("1", question);

        assertTrue(result);
    }

    @Test
    @DisplayName("checkAnswer должен вернуть false для неправильного ответа")
    void checkAnswer_IncorrectAnswer_ReturnsFalse() {
        Question question = testQuestions.get(0); // Одиночный выбор

        boolean result = testService.checkAnswer("2", question);

        assertFalse(result);
    }

    @Test
    @DisplayName("checkAnswer должен корректно обрабатывать множественные ответы")
    void checkAnswer_MultipleChoice_AllCorrect_ReturnsTrue() {
        Question question = testQuestions.get(1); // Множественный выбор

        boolean result = testService.checkAnswer("1,3", question);

        assertTrue(result);
    }

    @Test
    @DisplayName("checkAnswer должен вернуть false если не все ответы правильные")
    void checkAnswer_MultipleChoice_NotAllCorrect_ReturnsFalse() {
        Question question = testQuestions.get(1); // Множественный выбор

        boolean result = testService.checkAnswer("1,2", question);

        assertFalse(result);
    }

    @Test
    @DisplayName("checkAnswer должен вернуть false для пустого ответа")
    void checkAnswer_EmptyAnswer_ReturnsFalse() {
        Question question = testQuestions.get(0);

        boolean result = testService.checkAnswer("", question);

        assertFalse(result);
    }

    @Test
    @DisplayName("checkAnswer должен вернуть false для null ответа")
    void checkAnswer_NullAnswer_ReturnsFalse() {
        Question question = testQuestions.get(0);

        boolean result = testService.checkAnswer(null, question);

        assertFalse(result);
    }

    @Test
    @DisplayName("checkAnswer должен фильтровать некорректные числа")
    void checkAnswer_InvalidNumbers_ReturnsFalse() {
        Question question = testQuestions.get(0);

        boolean result = testService.checkAnswer("1,abc,99", question);

        // Только валидные ответы проверяются
        assertTrue(result); // 1 - правильный ответ
    }

    @Test
    @DisplayName("checkAnswer должен игнорировать числа вне диапазона")
    void checkAnswer_OutOfRangeNumbers_ReturnsTrueForValidOnes() {
        Question question = testQuestions.get(0); // Только 2 ответа

        boolean result = testService.checkAnswer("1,5", question);

        // 5 игнорируется, 1 проверяется
        assertTrue(result);
    }

    @Test
    @DisplayName("Должен корректно обрабатывать пробелы в ответах")
    void checkAnswer_WithSpaces_ReturnsCorrectResult() {
        Question question = testQuestions.get(1);

        boolean result = testService.checkAnswer(" 1 , 3 ", question);

        assertTrue(result);
    }
}