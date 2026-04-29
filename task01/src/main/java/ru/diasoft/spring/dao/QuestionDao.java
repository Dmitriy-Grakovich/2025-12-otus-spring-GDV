package ru.diasoft.spring.dao;

import ru.diasoft.spring.domain.Question;

import java.util.List;

public interface QuestionDao {
    List<Question> getAllQuestions();
}
