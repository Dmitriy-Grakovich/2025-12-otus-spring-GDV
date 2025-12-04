package ru.diasoft.spring.dao;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import ru.diasoft.spring.domain.Answer;
import ru.diasoft.spring.domain.Question;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository

public class CsvQuestionDaoImpl implements QuestionDao {


    private final String csvPath;

    public CsvQuestionDaoImpl(@Value("${csvResource}") String csvPath) {

        this.csvPath = csvPath;

    }

    @Override
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();

        Resource resource = new ClassPathResource(csvPath);
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(resource.getInputStream()))) {

            List<String[]> lines = reader.readAll();

            for (String[] tokens : lines) {
                if (tokens.length >= 3) {
                    String questionText = tokens[0];
                    boolean isMultipleChoice = Boolean.parseBoolean(tokens[1]);

                    Map<Integer, Answer> answers = new HashMap<>();
                    int number = 1;
                    for (int i = 2; i < tokens.length; i += 2) {
                        if (i + 1 < tokens.length) {
                            String answerText = tokens[i];
                            boolean isCorrect = Boolean.parseBoolean(tokens[i + 1]);
                            answers.put(number++, new Answer(answerText, isCorrect));
                        }
                    }

                    questions.add(new Question(questionText, answers, isMultipleChoice));
                }
            }

        } catch (IOException | CsvException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }

        return questions;
    }
}
