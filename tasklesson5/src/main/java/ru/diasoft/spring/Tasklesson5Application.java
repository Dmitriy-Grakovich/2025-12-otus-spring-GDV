package ru.diasoft.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.diasoft.spring.service.TestService;

@SpringBootApplication
public class Tasklesson5Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Tasklesson5Application.class, args);
		TestService testService = context.getBean(TestService.class);
		testService.runTest();

		context.close();
	}

}
