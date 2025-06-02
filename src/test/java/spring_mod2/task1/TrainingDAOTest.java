package spring_mod2.task1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spring_mod2.task1.DAO.TrainerDAO;
import spring_mod2.task1.DAO.TrainingDAO;
import spring_mod2.task1.Entities.Trainee;
import spring_mod2.task1.Entities.Trainer;
import spring_mod2.task1.Entities.Training;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainingDAOTest {
    private TrainingDAO trainingDAO;

    @BeforeEach
    void setUp() {
        trainingDAO = new TrainingDAO(new java.util.HashMap<>());
    }

    @Test
    void testSaveAndGetTrainee() {
        Training training = new Training(1,2,"Learn Java","Java", LocalDate.parse("2022-02-04"),"30 days");
        trainingDAO.save(training);

        Training retrieved = trainingDAO.get("Learn Java");
        assertEquals(training, retrieved);
    }

    @Test
    void testUpdateTrainer() {
        Training training = new Training(1,2,"Learn Java","Java", LocalDate.parse("2022-02-04"),"30 days");
        trainingDAO.save(training);
        assertEquals(training, trainingDAO.get("Learn Java"));
        Training trainingUpdate = new Training(1,2,"Learn Java","Java", LocalDate.parse("2022-02-04"),"60 days");
        trainingDAO.update(trainingUpdate);
        assertEquals(trainingUpdate, trainingDAO.get("Learn Java"));
    }
}
