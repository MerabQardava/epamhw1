package spring_mod2.task1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spring_mod2.task1.DAO.TrainerDAO;
import spring_mod2.task1.Entities.Trainer;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TrainerDAOTest {
    private TrainerDAO trainerDAO;

    @BeforeEach
    void setUp() {
        trainerDAO = new TrainerDAO(new java.util.HashMap<>());
    }

    @Test
    void testSaveAndGetTrainee() {
        Trainer trainer = new Trainer("John","Doe", 2,1);
        trainerDAO.save(trainer);

        Trainer retrieved = trainerDAO.get(1);
        assertEquals(trainer, retrieved);
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer("Jane", "Doe", 2, 1);
        trainerDAO.save(trainer);
        assertEquals(trainer, trainerDAO.get(1));
        Trainer trainerUpdate = new Trainer("John", "Doe", 2, 1);
        trainerDAO.update(trainerUpdate);
        assertEquals(trainerUpdate, trainerDAO.get(1));
    }
}
