package ru.diasoft.spring.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.diasoft.spring.dao.CsvQuestionDaoImpl;
import ru.diasoft.spring.dao.QuestionDao;
import ru.diasoft.spring.service.QuestionService;
import ru.diasoft.spring.service.QuestionServiceImpl;
import ru.diasoft.spring.service.TestService;
import ru.diasoft.spring.service.TestServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class SimpleIntegrationTest {

    @Test
    @DisplayName("Создание контекста Spring и получение бинов")
    void springContextCreation() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfiguration.class)) {

            TestService testService = context.getBean(TestService.class);
            QuestionService questionService = context.getBean(QuestionService.class);
            QuestionDao questionDao = context.getBean(QuestionDao.class);

            assertNotNull(testService);
            assertNotNull(questionService);
            assertNotNull(questionDao);

            assertInstanceOf(TestServiceImpl.class, testService);
            assertInstanceOf(QuestionServiceImpl.class, questionService);
            assertInstanceOf(CsvQuestionDaoImpl.class, questionDao);

            var questions = questionService.getAllQuestions();
            assertNotNull(questions);
            assertFalse(questions.isEmpty());

            System.out.println("Integration test passed! Loaded " + questions.size() + " questions");
        }
    }

    @Configuration
    static class TestConfiguration {

        @Bean
        public QuestionDao questionDao() {
            return new CsvQuestionDaoImpl("questions-test.csv");
        }

        @Bean
        public QuestionService questionService(QuestionDao questionDao) {
            return new QuestionServiceImpl(questionDao);
        }

        @Bean
        public TestService testService(QuestionService questionService) {
            return new TestServiceImpl(questionService, "2");
        }
    }
}