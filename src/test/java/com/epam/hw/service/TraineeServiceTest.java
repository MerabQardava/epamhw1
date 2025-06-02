package com.epam.hw.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.epam.hw.dao.TraineeDao;
import com.epam.hw.entity.Trainee;
import com.epam.hw.service.TraineeService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TraineeServiceTest {
    private TraineeDao traineeDAO;
    private TraineeService traineeService;

    @BeforeEach
    void setUp() {
        traineeDAO = Mockito.mock(TraineeDao.class);
        traineeService = new TraineeService(traineeDAO);
    }

    @Test
    public void testCreateTrainee() {
        Trainee trainee = traineeService.create("John","Doe","2024-02-01","tbilisi");

        assertNotNull(trainee);
        assertEquals("John", trainee.getFirstName());
        assertEquals("Doe", trainee.getLastName());
        assertEquals(LocalDate.parse("2024-02-01"), trainee.getDateOfBirth());
        assertTrue(trainee.getUsername().startsWith("John.Doe"));
        verify(traineeDAO,times(1)).save(any(Trainee.class));
    }

    @Test
    void testUpdateTrainee() {
        Trainee trainee = traineeService.create("John","Doe","2024-02-01","tbilisi");
        traineeService.update(trainee);

        verify(traineeDAO,times(1)).save(any(Trainee.class));
    }

    @Test
    void testGetTrainer() {
        Trainee trainee = traineeService.create("John","Doe","2024-02-01","tbilisi");
        when(traineeDAO.get(1)).thenReturn(trainee);

        Trainee result = traineeService.get(1);

        assertEquals(trainee, result);
        verify(traineeDAO, times(1)).get(1);
    }

    @Test
    void testDeleteTrainee() {
        Trainee trainee = traineeService.create("John","Doe","2024-02-01","tbilisi");
        when(traineeDAO.get(1)).thenReturn(trainee);

        traineeService.delete(1);

        verify(traineeDAO, times(1)).delete(1);
    }






}
