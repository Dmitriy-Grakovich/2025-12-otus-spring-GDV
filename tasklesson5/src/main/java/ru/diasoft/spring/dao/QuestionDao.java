package ru.diasoft.spring.dao;

import ru.diasoft.spring.domain.Question;

import java.util.List;
import java.util.Locale;

public interface QuestionDao {
    List<Question> getAllQuestions(Locale locale);
}
