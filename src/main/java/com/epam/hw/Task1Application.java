package com.epam.hw;

import com.epam.hw.entity.Trainer;
import com.epam.hw.entity.TrainingType;
import com.epam.hw.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import com.epam.hw.entity.Trainee;

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
//			Trainee trainee = new Trainee(LocalDate.of(1999, 1, 1), "Tbilisi", user);
			TrainingType trainingType = new TrainingType("Java");
			Trainer trainer = new Trainer(trainingType,user);

			em.persist(trainingType);
			em.persist(user);
			em.persist(trainer);

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
