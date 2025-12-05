package ru.diasoft.spring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.command.annotation.CommandScan;
import org.springframework.test.context.TestPropertySource;
import ru.diasoft.spring.config.BaseConfig;
import ru.diasoft.spring.dao.QuestionDao;
import ru.diasoft.spring.service.QuestionService;
import ru.diasoft.spring.service.TestService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = Tasklesson5Application.class,
        properties = {
                "spring.shell.interactive.enabled=false",
                "spring.shell.command.script.enabled=false"
        }
)
@TestPropertySource(properties = {
        "spring.shell.command.enabled=false"
})
@DisplayName("Spring Boot Application Context Tests")
class ApplicationContextTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Контекст должен успешно запускаться")
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    @DisplayName("Все основные бины должны быть созданы")
    void allRequiredBeansArePresent() {
        // Проверяем основные сервисы
        assertNotNull(applicationContext.getBean(TestService.class));
        assertNotNull(applicationContext.getBean(QuestionService.class));
        assertNotNull(applicationContext.getBean(QuestionDao.class));
        assertNotNull(applicationContext.getBean(BaseConfig.class));
    }

    @Test
    @DisplayName("BaseConfig должен содержать правильные значения из application.yml")
    void baseConfigPropertiesAreLoaded() {
        BaseConfig baseConfig = applicationContext.getBean(BaseConfig.class);

        assertNotNull(baseConfig.getCsvResource());
        assertNotNull(baseConfig.getCsvResourcePattern());
        assertTrue(baseConfig.getLimit() > 0);
        assertNotNull(baseConfig.getDefaultLocale());
    }
}