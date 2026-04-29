package ru.diasoft.spring.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import ru.diasoft.spring.config.BaseConfig;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoImplTest {

    @Mock
    private BaseConfig baseConfig;

    @Mock
    private MessageSource messageSource;

    private CsvQuestionDaoImpl dao;

    @BeforeEach
    void setUp() {
        when(baseConfig.getCsvResourcePattern()).thenReturn("questions_{locale}.csv");
        dao = new CsvQuestionDaoImpl(baseConfig, messageSource);
    }



    @Test
    void getAllQuestions_WithLocale_UpdatesLocale() {
        // Given
        Locale russian = new Locale("ru");

        // When
        dao.getAllQuestions(russian);

        // Then
        assertEquals(russian, dao.getCurrentLocale());
    }

    @Test
    void getAllQuestions_WithNullLocale_KeepsCurrentLocale() {
        // Given
        Locale current = dao.getCurrentLocale();

        // When
        dao.getAllQuestions(null);

        // Then
        assertEquals(current, dao.getCurrentLocale());
    }


}