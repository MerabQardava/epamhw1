package com.epam.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.epam.hw.dao.TrainingDao;
import com.epam.hw.entity.Training;
import com.epam.hw.service.TrainingService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TrainingServiceTest {
    TrainingService trainingService;
    TrainingDao trainingDAO;

    @BeforeEach
    void setUp() {
        trainingDAO = mock(TrainingDao.class);
        trainingService = new TrainingService(trainingDAO);
    }


    @Test
    void testCreateTraining() {
        Training training = trainingService.create(1, 2, "Learn Java", "Java", "2025-02-03", "20days");

        assertNotNull(training);
        assertEquals("Learn Java",training.getTrainingName());
        assertEquals("Java",training.getTrainingType());
        assertEquals(LocalDate.parse("2025-02-03"), training.getDate());
        assertEquals("20days",training.getDuration());
        assertEquals(1,training.getTraineeId());
        assertEquals(2,training.getTrainerId());

        verify(trainingDAO, times(1)).save(any(Training.class));
    }

    @Test
    void testUpdateTraining() {
        Training training = trainingService.create(1, 2, "Learn Java", "Java", "2025-02-03", "20days");
        trainingDAO.update(training);
        verify(trainingDAO, times(1)).save(any(Training.class));
    }

    @Test
    void testGetTrainer() {
        Training training = trainingService.create(1, 2, "Learn Java", "Java", "2025-02-03", "20days");
        when(trainingDAO.get("Learn Java")).thenReturn(training);

        Training result = trainingService.get("Learn Java");

        assertEquals(training, result);
        verify(trainingDAO, times(1)).get("Learn Java");
    }


}
