package ru.diasoft.spring.dao;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import ru.diasoft.spring.config.BaseConfig;
import ru.diasoft.spring.domain.Answer;
import ru.diasoft.spring.domain.Question;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


@Repository

public class CsvQuestionDaoImpl implements QuestionDao {


    private final BaseConfig baseConfig;
    private final MessageSource messageSource;
    private Locale locale;

    public CsvQuestionDaoImpl(BaseConfig baseConfig, MessageSource messageSource) {
        this.baseConfig = baseConfig;
        this.messageSource = messageSource;
        this.locale = Locale.getDefault(); // Текущая локаль системы
    }


    @Override
    public List<Question> getAllQuestions(Locale locale) {
        if (locale != null){
            this.locale = locale;
        }
        String csvFileName = getLocalizedCsvFileName();
        System.out.println("Loading questions from: " + csvFileName);

        return loadQuestionsFromCsv(csvFileName);
    }
    private String getLocalizedCsvFileName() {
        String pattern = baseConfig.getCsvResourcePattern();
        System.out.println("pattern + " + pattern);
        if (pattern == null || pattern.isEmpty()) {
            // Если шаблон не задан, используем базовое имя с локалью
            return baseConfig.getCsvResource() + "_" + locale.getLanguage() + ".csv";
        }

        // Заменяем {locale} на текущую локаль
        return pattern.replace("{locale}", locale.getLanguage());
    }
    private List<Question> loadQuestionsFromCsv(String csvFileName) {
        List<Question> questions = new ArrayList<>();

        Resource resource = new ClassPathResource(csvFileName);

        // Если файл с локалью не найден, пробуем загрузить дефолтный
        if (!resource.exists()) {
            System.out.println("Localized file not found: " + csvFileName +
                    ", trying default: " + baseConfig.getCsvResource() + ".csv");
            resource = new ClassPathResource(baseConfig.getCsvResource() + ".csv");

            if (!resource.exists()) {
                throw new RuntimeException("CSV file not found: " + csvFileName +
                        " or " + baseConfig.getCsvResource() + ".csv");
            }
        }

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
            throw new RuntimeException("Error reading CSV file: " + csvFileName, e);
        }

        return questions;
    }

    // Альтернативная версия с fallback на дефолтную локаль
    public List<Question> getAllQuestionsWithFallback() {
        List<String> localesToTry = Arrays.asList(
                locale.getLanguage(), // Текущая локаль
                baseConfig.getDefaultLocale(), // Дефолтная из конфига
                "en" // Английский как последний fallback
        );

        for (String loc : localesToTry) {
            String fileName = baseConfig.getCsvResourcePattern()
                    .replace("{locale}", loc);

            Resource resource = new ClassPathResource(fileName);
            if (resource.exists()) {
                System.out.println("Found questions file for locale: " + loc);
                return loadQuestionsFromCsv(fileName);
            }
        }

        throw new RuntimeException("No questions file found for any locale");
    }

    // Геттер для тестов
    public Locale getCurrentLocale() {
        return locale;
    }
}
