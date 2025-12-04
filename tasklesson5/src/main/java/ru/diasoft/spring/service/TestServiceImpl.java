package ru.diasoft.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.diasoft.spring.config.BaseConfig;
import ru.diasoft.spring.domain.Answer;
import ru.diasoft.spring.domain.Question;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class TestServiceImpl implements TestService {

    private final Integer result;
    private final QuestionService questionService;
    private final MessageSource messageSource;
    private final BaseConfig baseConfig;
    private Locale currentLocale;

    @Autowired
    public TestServiceImpl(QuestionService questionService,
                           BaseConfig baseConfig,
                           MessageSource messageSource) {
        this.result = baseConfig.getLimit();
        this.questionService = questionService;
        this.messageSource = messageSource;
        this.baseConfig = baseConfig;

    }

    @Override
    public void runTest() {
        selectLanguage();

        List<Question> questions = questionService.getAllQuestions(currentLocale);

        printMessage("test.welcome");
        printMessage("test.total.questions", questions.size());
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        printMessage("test.enter.name");
        String fullName = scanner.nextLine();
        System.out.println();

        int countIsCorrect = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);

            printMessage("test.question.number", i + 1);
            System.out.println(question);

            if (question.isIsMultiChoice()) {
                printMessage("test.multi.choice.prompt");
            } else {
                printMessage("test.single.choice.prompt");
            }

            String userAnswer = scanner.nextLine();
            if (checkAnswer(userAnswer, question)) {
                countIsCorrect++;
            }

            System.out.println();
        }

        if (countIsCorrect >= result) {
            printMessage("test.result.success", fullName, countIsCorrect);
            printMessage("test.completed");
        } else {
            printMessage("test.result.failure");
        }

        scanner.close();
    }
    private void selectLanguage() {
    Scanner scanner = new Scanner(System.in);

        System.out.println("Please select language / Пожалуйста, выберите язык:");
        System.out.println("1. English");
        System.out.println("2. Русский");

        System.out.print("Your choice (1-2): ");

    String choice = scanner.nextLine().trim();

        switch (choice) {
        case "1":
            currentLocale = Locale.ENGLISH;
            break;
        case "2":
            currentLocale = new Locale("ru");
            break;

        default:
            System.out.println("Invalid choice, using English");
            currentLocale = Locale.ENGLISH;
    }

    // Здесь нужно обновить questionService с новой локалью
    // Для этого может потребоваться переделать QuestionService
}

    boolean checkAnswer(String userAnswer, Question question) {
        if (userAnswer == null || userAnswer.isBlank()) {
            return false;
        }

        Map<Integer, Answer> answers = question.getAnswers();

        // Получаем множество правильных индексов
        Set<Integer> correctIndices = answers.entrySet().stream()
                .filter(entry -> entry.getValue().isCorrect())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // Если нет правильных ответов
        if (correctIndices.isEmpty()) {
            return false;
        }

        // Получаем множество ответов пользователя
        Set<Integer> userIndices = Arrays.stream(userAnswer.split(","))
                .map(String::trim)
                .map(part -> {
                    try {
                        int index = Integer.parseInt(part);
                        return index > 0 ? index : -1;
                    } catch (NumberFormatException e) {
                        return -1;
                    }
                })
                .filter(index -> index != -1)
                .collect(Collectors.toSet());

        return userIndices.equals(correctIndices);
    }

    // Вспомогательный метод для локализованного вывода
    private void printMessage(String messageCode, Object... args) {
        String message = messageSource.getMessage(messageCode, args, currentLocale);
        System.out.println(message);
    }



}