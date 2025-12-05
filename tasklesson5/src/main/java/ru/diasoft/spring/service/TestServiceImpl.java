package ru.diasoft.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.diasoft.spring.config.BaseConfig;
import ru.diasoft.spring.domain.Answer;
import ru.diasoft.spring.domain.Question;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {

    private final Integer result;
    private final QuestionService questionService;
    private final MessageSource messageSource;
    private final BaseConfig baseConfig;

    // Убираем автосвязывание Scanner, создаем его при запуске теста
    private Scanner scanner;
    private Locale currentLocale = Locale.ENGLISH;

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
        try {
            // Создаем Scanner для этого запуска
            scanner = new Scanner(System.in);

            selectLanguage();

            List<Question> questions = questionService.getAllQuestions(currentLocale);

            printMessage("test.welcome");
            printMessage("test.total.questions", questions.size());
            System.out.println();

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
                printMessage("test.result.failure", fullName, countIsCorrect, result);
            }

        } finally {
            // Очищаем scanner, но не закрываем System.in
            scanner = null;
        }
    }

    private void selectLanguage() {
        System.out.println("\n=== Language Selection ===");
        System.out.println("Please select language / Пожалуйста, выберите язык:");
        System.out.println("1. English");
        System.out.println("2. Русский");
        System.out.print("Your choice (1-2): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                currentLocale = Locale.ENGLISH;
                System.out.println("Selected: English");
                break;
            case "2":
                currentLocale = new Locale("ru");
                System.out.println("Выбран: Русский");
                break;
            default:
                System.out.println("Invalid choice, using English");
                currentLocale = Locale.ENGLISH;
        }
        System.out.println();
    }

    public boolean checkAnswer(String userAnswer, Question question) {
        if (userAnswer == null || userAnswer.isBlank()) {
            return false;
        }

        Map<Integer, Answer> answers = question.getAnswers();

        Set<Integer> correctIndices = answers.entrySet().stream()
                .filter(entry -> entry.getValue().isCorrect())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        if (correctIndices.isEmpty()) {
            return false;
        }

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

    private void printMessage(String messageCode, Object... args) {
        String message = messageSource.getMessage(messageCode, args, currentLocale);
        System.out.println(message);
    }

    // Геттер для текущей локали (может пригодиться)
    public Locale getCurrentLocale() {
        return currentLocale;
    }
}