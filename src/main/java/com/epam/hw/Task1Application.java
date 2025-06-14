package com.epam.hw;

import com.epam.hw.entity.*;

import com.epam.hw.service.TraineeService;
import com.epam.hw.service.TrainingService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;

import java.time.LocalDate;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@EntityScan
public class Task1Application {

	public static void main(String[] args) {
		var context = SpringApplication.run(Task1Application.class, args);
		EntityManagerFactory emf = context.getBean(EntityManagerFactory.class);
		EntityManager em = emf.createEntityManager();

		TraineeService traineeService = context.getBean(TraineeService.class);
		TrainingService trainingService = context.getBean(TrainingService.class);


		User user = new User("John", "Doe");

		Trainee trainee = new Trainee( LocalDate.of(1990, 1, 1), "123 Mech St", user);

		System.out.println(traineeService.logIn("John.Doe","amongusi"));
		System.out.println(traineeService.getTraineeByUsername( "John.Doe"));
//		System.out.println(traineeService.getTraineeTrainings("John.Doe",
//				LocalDate.of( 2023, 10, 1),
//				LocalDate.of( 2023, 10, 31),
//				"Merab.Kardava","Java").size());




	}

}
