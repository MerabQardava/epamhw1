package spring_mod2.task1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spring_mod2.task1.DAO.TraineeDAO;
import spring_mod2.task1.Entities.Trainee;
import spring_mod2.task1.Entities.Trainer;
import spring_mod2.task1.Services.TraineeService;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeDAOTest {
    private TraineeDAO traineeDAO;

    @BeforeEach
    void setUp() {
        traineeDAO = new TraineeDAO(new java.util.HashMap<>());
    }

    @Test
    void testSaveAndGetTrainee() {
        Trainee trainee = new Trainee("John","Doe",LocalDate.parse("2024-02-04"),"Tbilisi,Georgia",1);
        traineeDAO.save(trainee);

        Trainee retrieved = traineeDAO.get(1);
        assertEquals(trainee, retrieved);
    }

    @Test
    void testDeleteTrainee() {
        Trainee trainee = new Trainee("John","Doe",LocalDate.parse("2024-02-04"),"Tbilisi,Georgia",1);
        traineeDAO.save(trainee);
        traineeDAO.delete(1);
        assertNull(traineeDAO.get(1));
    }

    @Test
    void testUpdateTrainer() {
        Trainee trainee = new Trainee("Jane","Doe",LocalDate.parse("2024-02-04"),"Tbilisi,Georgia",1);
        traineeDAO.save(trainee);
        assertEquals(trainee, traineeDAO.get(1));
        Trainee traineeUpdate = new Trainee("John","Doe",LocalDate.parse("2024-02-04"),"Tbilisi,Georgia",1);
        traineeDAO.update(traineeUpdate);
        assertEquals(traineeUpdate, traineeDAO.get(1));
    }

}
