package com.epam.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.epam.hw.dao.TraineeDao;
import com.epam.hw.entity.Trainee;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeDaoTest {
    private TraineeDao traineeDAO;

    @BeforeEach
    void setUp() {
        traineeDAO = new TraineeDao(new java.util.HashMap<>());
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
