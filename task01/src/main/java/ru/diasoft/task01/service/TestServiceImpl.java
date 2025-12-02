package ru.diasoft.task01.service;

import lombok.AllArgsConstructor;
import ru.diasoft.task01.domain.Question;

import java.util.List;
import java.util.Scanner;

@AllArgsConstructor
public class TestServiceImpl implements TestService {
    private final QuestionService questionService;
    @Override
    public void runTest() {
        List<Question> questions = questionService.getAllQuestions();

        System.out.println("=== Welcome to Student Testing Application ===\n");
        System.out.println("Total questions: " + questions.size() + "\n");

        Scanner scanner = new Scanner(System.in);

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
            System.out.println();
        }

        System.out.println("=== Test completed! ===");
        scanner.close();
    }
}
