package com.epam.hw.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.epam.hw.dao.TrainerDao;
import com.epam.hw.entity.Trainer;
import com.epam.hw.service.TrainerService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceTest {

    private TrainerDao trainerDAO;
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerDAO = mock(TrainerDao.class);
//        when(trainerDAO.getAll()).thenReturn(Collections.emptyList());
        trainerService = new TrainerService(trainerDAO);
    }

    @Test
    void testCreateTrainer() {
        Trainer trainer = trainerService.create("John", "Doe", 1);

        assertNotNull(trainer);
        assertEquals("John", trainer.getFirstName());
        assertEquals("Doe", trainer.getLastName());
        assertTrue(trainer.getUsername().startsWith("John.Doe"));
        verify(trainerDAO, times(1)).save(any(Trainer.class));
    }

    @Test
    void testGetTrainer() {
        Trainer expected = new Trainer("John", "Doe", 1, 1);
        when(trainerDAO.get(1)).thenReturn(expected);

        Trainer result = trainerService.get(1);

        assertEquals(expected, result);
        verify(trainerDAO, times(1)).get(1);
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer("Jane", "Doe", 2, 2);
        trainerService.update(trainer);

        verify(trainerDAO, times(1)).update(trainer);
    }
}
