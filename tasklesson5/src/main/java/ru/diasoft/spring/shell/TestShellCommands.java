package ru.diasoft.spring.shell;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.diasoft.spring.config.BaseConfig;
import ru.diasoft.spring.domain.Question;
import ru.diasoft.spring.service.QuestionService;
import ru.diasoft.spring.service.TestService;

import java.util.List;
import java.util.Locale;

@ShellComponent
public class TestShellCommands {
    
    private final TestService testService;
    private final QuestionService questionService;
    private final BaseConfig baseConfig;
    
    @Autowired
    public TestShellCommands(TestService testService, 
                             QuestionService questionService,
                             BaseConfig baseConfig) {
        this.testService = testService;
        this.questionService = questionService;
        this.baseConfig = baseConfig;
    }
    
    @ShellMethod(value = "Start the testing session", key = {"start", "test", "run"})
    public void startTest() {
        System.out.println("Starting test session...");
        System.out.println("========================");
        testService.runTest();
    }
    
    @ShellMethod(value = "List all available questions", key = {"questions", "list"})
    public void listQuestions(
            @ShellOption(value = {"--lang", "-l"}, 
                        defaultValue = "en", 
                        help = "Language: en or ru") String lang) {
        
        Locale locale = "ru".equalsIgnoreCase(lang) ? 
                new Locale("ru") : Locale.ENGLISH;
        
        List<Question> questions = questionService.getAllQuestions(locale);
        
        System.out.println("\n=== Questions (" + locale.getLanguage() + ") ===");
        System.out.println("Total: " + questions.size());
        System.out.println("Passing limit: " + baseConfig.getLimit());
        System.out.println("=======================\n");
        
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            System.out.println((i + 1) + ". " + question.getText());
            System.out.println("   Type: " + (question.isIsMultiChoice() ? 
                    "Multiple choice" : "Single choice"));
            
            question.getAnswers().forEach((key, answer) -> {
                String marker = answer.isCorrect() ? "✓" : " ";
                System.out.println("   [" + marker + "] " + key + ". " + answer.getValue());
            });
            System.out.println();
        }
    }
    
    @ShellMethod(value = "Show application configuration", key = {"config", "settings"})
    public void showConfig() {
        System.out.println("\n=== Application Configuration ===");
        System.out.println("CSV Resource: " + baseConfig.getCsvResource());
        System.out.println("CSV Pattern: " + baseConfig.getCsvResourcePattern());
        System.out.println("Passing Limit: " + baseConfig.getLimit());
        System.out.println("Default Locale: " + baseConfig.getDefaultLocale());
        System.out.println("===============================\n");
    }
    
    @ShellMethod(value = "Show available commands", key = {"help", "h", "?"})
    public void showHelp() {
        System.out.println("\n=== Available Commands ===");
        System.out.println("start                 - Start the testing session");
        System.out.println("questions [--lang en/ru] - List all questions");
        System.out.println("config                - Show application configuration");
        System.out.println("help                  - Show this help message");
        System.out.println("exit                  - Exit the application");
        System.out.println("\nExamples:");
        System.out.println("  questions --lang ru  - Show questions in Russian");
        System.out.println("  questions -l en     - Show questions in English");
        System.out.println("==========================\n");
    }
    
    @ShellMethod(value = "Exit the application", key = {"exit", "quit", "e"})
    public void exit() {
        System.out.println("Goodbye!");
        System.exit(0);
    }
}