package com.epam.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;


import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.Trainer;
import com.epam.hw.entity.Training;
import com.epam.hw.entity.TrainingType;
import com.epam.hw.repository.TraineeRepository;
import com.epam.hw.repository.TrainerRepository;
import com.epam.hw.repository.TrainingRepository;
import com.epam.hw.repository.TrainingTypeRepository;
import org.mockito.ArgumentCaptor;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @Mock  TrainingRepository     trainingRepo;
    @Mock  TraineeRepository      traineeRepo;
    @Mock  TrainerRepository      trainerRepo;
    @Mock  TrainingTypeRepository trainingTypeRepo;

    @InjectMocks
    TrainingService trainingService;

    @Test
    void addTraining_success() {
        Integer traineeId = 1, trainerId = 2, typeId = 3;
        Trainee trainee     = new Trainee();
        Trainer trainer     = new Trainer();
        TrainingType type  = new TrainingType("Java");

        when(traineeRepo.findById(traineeId)).thenReturn(Optional.of(trainee));
        when(trainerRepo.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepo.findById(typeId)).thenReturn(Optional.of(type));


        when(trainingRepo.save(any(Training.class))).thenAnswer(inv -> {
            Training tr = inv.getArgument(0);
            tr.setId(10);
            return tr;
        });

        Training result = trainingService.addTraining(
                traineeId, trainerId, "Java", typeId,
                LocalDate.of(2025, 6, 15), 60);

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingRepo).save(captor.capture());
        Training saved = captor.getValue();

        assertEquals(trainee, saved.getTrainee());
        assertEquals(trainer, saved.getTrainer());
        assertEquals(type,   saved.getTrainingType());
        assertEquals("Java", saved.getTrainingName());
        assertEquals(60, saved.getDuration());
        assertEquals(10, result.getId());
    }

    @Test
    void addTraining_throwsWhenTraineeNotFound() {
        when(traineeRepo.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> trainingService.addTraining(
                        99, 1, "Any", 1,
                        LocalDate.now(), 30));

        assertEquals("Trainee not found", ex.getMessage());
        verify(trainingRepo, never()).save(any());
    }
}