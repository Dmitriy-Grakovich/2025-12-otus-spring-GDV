package ru.diasoft.spring.service;

import ru.diasoft.spring.domain.Question;

import java.util.List;
import java.util.Locale;

public interface QuestionService {

        List<Question> getAllQuestions(Locale locale);

}
