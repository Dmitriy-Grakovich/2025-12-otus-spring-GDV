package ru.diasoft.task01.service;

import lombok.AllArgsConstructor;
import ru.diasoft.task01.dao.QuestionDao;
import ru.diasoft.task01.domain.Question;

import java.util.List;
@AllArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionDao questionDao;
    @Override
    public List<Question> getAllQuestions() {
        return questionDao.getAllQuestions();
    }
}
