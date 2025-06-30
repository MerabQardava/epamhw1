package com.epam.hw.controller;


import com.epam.hw.dto.CreateTrainingDTO;
import com.epam.hw.entity.TrainingType;
import com.epam.hw.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getTrainingTypesTest() {
        List<TrainingType> mockTypes = List.of(new TrainingType("Java"), new TrainingType("Spring"));
        when(trainingService.getTrainingTypes()).thenReturn(mockTypes);

        ResponseEntity<List<TrainingType>> response = trainingController.getTrainingTypes();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(trainingService).getTrainingTypes();
    }

    @Test
    void addTrainingTest() {
        CreateTrainingDTO dto = new CreateTrainingDTO(
                "Java Training",
                "Java",
                "2025-01-01",
                60
        );

        ResponseEntity<String> response = trainingController.addTraining("trainee.user","trainer.user",dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Training added successfully", response.getBody());

        verify(trainingService).addTraining(
                eq("trainee.user"),
                eq("trainer.user"),
                eq("Java Training"),
                eq("Java"),
                eq(java.time.LocalDate.of(2025, 1, 1)),
                eq(60)
        );
    }
}
