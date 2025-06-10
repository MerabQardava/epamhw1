package com.epam.hw;

import com.epam.hw.entity.*;

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

		try {
			em.getTransaction().begin();

			User user = new User("Merab", "Kardava");
			User user2= new User("Giorgi", "mech");
			Trainee trainee = new Trainee(LocalDate.of(1999, 1, 1), "Tbilisi", user2);
			TrainingType trainingType = new TrainingType("Java");
			Trainer trainer = new Trainer(trainingType,user);

			Training training = new Training(trainee, trainer, "Java Basics", trainingType, LocalDate.of(2023, 10, 1), "2 hours");

			em.persist(user2);
			em.persist(user);
			em.persist(trainee);
			em.persist(trainingType);
			em.persist(trainer);
			em.persist(training);

			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			em.close();
			emf.close();
		}
	}

}
