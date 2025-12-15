package ru.diasoft.spring.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {
    
    // Отключаем Spring Shell в тестах
    @Bean
    @Primary
    public String disableSpringShell() {
        System.setProperty("spring.shell.interactive.enabled", "false");
        System.setProperty("spring.shell.command.script.enabled", "false");
        return "test-config";
    }
}