package spring_mod2.task1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import spring_mod2.task1.DAO.TraineeDAO;
import spring_mod2.task1.Entities.Trainee;
import spring_mod2.task1.Entities.Trainer;
import spring_mod2.task1.Entities.Training;
import spring_mod2.task1.Services.TraineeService;
import spring_mod2.task1.Services.TrainingService;

import java.util.HashMap;
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
