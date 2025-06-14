package com.epam.hw.service;

import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.Trainer;
import com.epam.hw.entity.Training;
import com.epam.hw.entity.TrainingType;
import com.epam.hw.repository.TraineeRepository;
import com.epam.hw.repository.TrainerRepository;
import com.epam.hw.repository.TrainingRepository;
import com.epam.hw.repository.TrainingTypeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class TrainingService {

    private final TrainingRepository trainingRepo;
    private final TraineeRepository traineeRepo;
    private final TrainerRepository trainerRepo;
    private final TrainingTypeRepository trainingTypeRepo;

    public TrainingService(TrainingRepository trainingRepo,
                           TraineeRepository traineeRepo,
                           TrainerRepository trainerRepo,
                           TrainingTypeRepository trainingTypeRepo) {
        this.trainingRepo = trainingRepo;
        this.traineeRepo = traineeRepo;
        this.trainerRepo = trainerRepo;
        this.trainingTypeRepo = trainingTypeRepo;
    }

    @Transactional
    public Training addTraining(Integer traineeId, Integer trainerId, String trainingName, Integer trainingTypeId, LocalDate date, Integer duration) {
        Trainee trainee = traineeRepo.findById(traineeId).orElseThrow(() -> new RuntimeException("Trainee not found"));
        Trainer trainer = trainerRepo.findById(trainerId).orElseThrow(() -> new RuntimeException("Trainer not found"));
        TrainingType trainingType = trainingTypeRepo.findById(trainingTypeId).orElseThrow(() -> new RuntimeException("TrainingType not found"));

        Training training = new Training(trainee, trainer, trainingName, trainingType, date, duration);
        return trainingRepo.save(training);
    }
}