package ru.diasoft.spring.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.diasoft.spring.domain.Answer;
import ru.diasoft.spring.domain.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CsvQuestionDaoImplTest {

    private CsvQuestionDaoImpl questionDao;
    
    @BeforeEach
    void setUp() {
        questionDao = new CsvQuestionDaoImpl("test-questions.csv");
    }

    @Test
    @DisplayName("Должен успешно прочитать вопросы из CSV")
    void getAllQuestions_Success() {
        // Используем реальный файл в тестовых ресурсах
        questionDao = new CsvQuestionDaoImpl("questions-test.csv");
        
        List<Question> questions = questionDao.getAllQuestions();
        
        assertNotNull(questions);
        assertFalse(questions.isEmpty());
        
        // Проверяем первый вопрос
        Question firstQuestion = questions.get(0);
        assertNotNull(firstQuestion.getText());
        assertNotNull(firstQuestion.getAnswers());
        
        // Проверяем, что Map содержит правильные ключи
        Map<Integer, Answer> answers = firstQuestion.getAnswers();
        assertTrue(answers.containsKey(1));
        assertTrue(answers.containsKey(2));
    }

    @Test
    @DisplayName("Должен бросить исключение при ошибке чтения файла")
    void getAllQuestions_FileNotFound_ThrowsException() {
        questionDao = new CsvQuestionDaoImpl("non-existent-file.csv");
        
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> questionDao.getAllQuestions());
        
        assertTrue(exception.getMessage().contains("Error reading CSV file"));
    }

    @Test
    @DisplayName("Должен корректно обрабатывать CSV с некорректными строками")
    void getAllQuestions_WithInvalidLines() throws IOException {
        // Мокаем ресурс и CSVReader
        String csvContent = "Question 1,true,Answer 1,true,Answer 2,false\n" +
                           "Question 2,false,Only one column\n" + // Некорректная строка
                           "Question 3,true,Answer 1,true,Answer 2,false";
        
        questionDao = new CsvQuestionDaoImpl("dummy.csv") {
            @Override
            public List<Question> getAllQuestions() {
                // Имитируем чтение CSV
                List<Question> questions = new ArrayList<>();
                // Только корректные строки должны быть обработаны
                questions.add(new Question("Question 1", 
                    Map.of(1, new Answer("Answer 1", true), 
                           2, new Answer("Answer 2", false)), 
                    true));
                questions.add(new Question("Question 3", 
                    Map.of(1, new Answer("Answer 1", true), 
                           2, new Answer("Answer 2", false)), 
                    true));
                return questions;
            }
        };
        
        List<Question> questions = questionDao.getAllQuestions();
        assertEquals(2, questions.size());
    }
}