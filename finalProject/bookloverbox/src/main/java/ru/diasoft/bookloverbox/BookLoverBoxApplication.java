package ru.diasoft.bookloverbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class BookLoverBoxApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookLoverBoxApplication.class, args);
	}

}
