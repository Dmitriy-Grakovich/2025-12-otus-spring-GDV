package ru.diasoft.spring.factory;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import ru.diasoft.spring.config.BaseConfig;
import ru.diasoft.spring.dao.CsvQuestionDaoImpl;

import java.util.Locale;

@Component
public class QuestionDaoFactory {
    
    private final BaseConfig baseConfig;
    private final MessageSource messageSource;
    
    public QuestionDaoFactory(BaseConfig baseConfig, MessageSource messageSource) {
        this.baseConfig = baseConfig;
        this.messageSource = messageSource;
    }

    
    public CsvQuestionDaoImpl createQuestionDao() {
        return new CsvQuestionDaoImpl(baseConfig, messageSource);
    }
}