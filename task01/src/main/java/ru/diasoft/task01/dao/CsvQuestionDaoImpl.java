package ru.diasoft.task01.dao;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.diasoft.task01.domain.Answer;
import ru.diasoft.task01.domain.Question;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

@AllArgsConstructor
public class CsvQuestionDaoImpl implements QuestionDao {
    private final Resource resource;



    @Override
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();

        try (CSVReader reader = new CSVReader(
                new InputStreamReader(resource.getInputStream()))) {

            List<String[]> lines = reader.readAll();

            for (String[] tokens : lines) {
                if (tokens.length >= 3) {
                    String questionText = tokens[0];
                    boolean isMultipleChoice = Boolean.parseBoolean(tokens[1]);

                    List<Answer> answers = new ArrayList<>();
                    for (int i = 2; i < tokens.length; i += 2) {
                        if (i + 1 < tokens.length) {
                            String answerText = tokens[i];
                            boolean isCorrect = Boolean.parseBoolean(tokens[i + 1]);
                            answers.add(new Answer(answerText, isCorrect));
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
