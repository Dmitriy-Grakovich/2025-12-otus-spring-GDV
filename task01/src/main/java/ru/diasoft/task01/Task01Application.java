package ru.diasoft.task01;


import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.diasoft.task01.service.TestService;

public class Task01Application {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context =
				new ClassPathXmlApplicationContext("/spring-context.xml");

		TestService testService = context.getBean(TestService.class);
		testService.runTest();

		context.close();
	}

}
