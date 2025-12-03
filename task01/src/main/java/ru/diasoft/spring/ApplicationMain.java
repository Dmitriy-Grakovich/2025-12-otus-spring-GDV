package ru.diasoft.spring;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.diasoft.spring.service.TestService;

@ComponentScan
@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationMain {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ApplicationMain.class);

        TestService testService = context.getBean(TestService.class);
        testService.runTest();

        context.close();
    }

}
