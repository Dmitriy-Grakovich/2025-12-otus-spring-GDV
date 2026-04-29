package ru.diasoft.spring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@ConfigurationProperties(prefix = "base")
@Component
@Getter
@Setter
public class BaseConfig {
    private String csvResource; // Базовое имя файла (без локали и расширения)
    private String csvResourcePattern; // Шаблон с локалью
    private int limit;
    private String defaultLocale;
}
