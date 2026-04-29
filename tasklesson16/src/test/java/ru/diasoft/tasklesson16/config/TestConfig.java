package ru.diasoft.tasklesson16.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan("ru.diasoft.tasklesson16.controller.mapper")
public class TestConfig {
    // Конфигурация для unit-тестов контроллеров (@WebMvcTest)
    // Для интеграционных тестов (@SpringBootTest) используется основной SecurityConfig
}