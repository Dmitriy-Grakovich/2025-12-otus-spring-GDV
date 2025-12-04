package ru.diasoft.spring.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@Configuration
public class AppConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Кэширование на 1 час
        return messageSource;
    }

    @Bean
    public Locale defaultLocale() {
        return Locale.getDefault(); // Локаль системы
    }
}