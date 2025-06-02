package com.epam.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.epam.hw.dao.TrainingDao;
import com.epam.hw.entity.Training;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainingDaoTest {
    private TrainingDao trainingDAO;

    @BeforeEach
    void setUp() {
        trainingDAO = new TrainingDao(new java.util.HashMap<>());
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
