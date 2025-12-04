package ru.diasoft.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.diasoft.spring.domain.Answer;
import ru.diasoft.spring.domain.Question;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


@Service
public class TestServiceImpl implements TestService {

    private final Integer result;
    private final QuestionService questionService;

    @Autowired
    public TestServiceImpl(QuestionService questionService, @Value("${result}") String resultSt) {
        this.result = Integer.parseInt(resultSt.trim());
        this.questionService = questionService;
    }

    @Override
    public void runTest() {
        List<Question> questions = questionService.getAllQuestions();

        System.out.println("=== Welcome to Student Testing Application ===\n");
        System.out.println("Total questions: " + questions.size() + "\n");

        Scanner scanner = new Scanner(System.in);
        System.out.println("enter your full name");
        String fullName = scanner.nextLine();
        System.out.println();
        int countIsCorrect = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            System.out.println("Question " + (i + 1) + ":");
            System.out.println(question);

            if (question.isIsMultiChoice()) {
                System.out.print("Enter the number(s) of correct answer(s), separated by comma: ");
            } else {
                System.out.print("Enter your answer: ");
            }

            String userAnswer = scanner.nextLine();
            if (checkAnswer(userAnswer, question)) {
                countIsCorrect++;
            }

            System.out.println();
        }
        if (countIsCorrect >= result) {
            System.out.println(fullName + "result = " + countIsCorrect + "\n" + "=== Test completed! ===");
        } else {
            System.out.println("Get ready and come back again");
        }
        scanner.close();
    }

    boolean checkAnswer(String userAnswer, Question question) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }

        Map<Integer, Answer> answers = question.getAnswers();

        return Arrays.stream(userAnswer.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .mapToInt(s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        return -1; // неверный формат
                    }
                })
                .filter(i -> i >= 0 && i < answers.size()) // проверка диапазона
                .allMatch(i -> answers.get(i).isCorrect());
    }
}
