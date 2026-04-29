package ru.diasoft.spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.diasoft.spring.dao.QuestionDao;
import ru.diasoft.spring.domain.Question;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionDao questionDao;
    @Override
    public List<Question> getAllQuestions(Locale locale) {
        return questionDao.getAllQuestions(locale);
    }
}
