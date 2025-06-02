package com.epam.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import com.epam.hw.entity.Trainee;

import java.util.Map;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Task1Application {

	public static void main(String[] args) {
		var context = SpringApplication.run(Task1Application.class, args);
		Map<String, Trainee> traineeStorage = context.getBean("traineeStorage", Map.class);

		System.out.println(traineeStorage.toString());

	}
}
