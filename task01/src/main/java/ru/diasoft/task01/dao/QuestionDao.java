package ru.diasoft.task01.dao;

import ru.diasoft.task01.domain.Question;

import java.util.List;

public interface QuestionDao {
    List<Question> getAllQuestions();
}
