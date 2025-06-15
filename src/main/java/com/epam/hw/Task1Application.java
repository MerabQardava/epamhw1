package com.epam.hw;

import com.epam.hw.entity.*;

import com.epam.hw.service.TraineeService;
import com.epam.hw.service.TrainerService;
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
		TrainerService trainerService = context.getBean(TrainerService.class);

////		trainerService.createTrainer("Mecho","Mechitashvili","Java");
		trainerService.LogIn("Mecho.Mechitashvili1","78ce676cba");
////		System.out.println(trainerService.getTrainerByUsername("Merab.Kardava"));
//		trainerService.toggleTrainerStatus();
		System.out.println(trainerService.getUnassignedTraineeTrainers("John.Doe"));
//		System.out.println(traineeService.logIn("John.Doe","amongusi"));
//		traineeService.addTrainerToTrainee("John.Doe","Mecho.Mechitashvili1");







	}

}
