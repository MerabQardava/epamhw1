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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

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
        logger.debug("Attempting to add training for traineeId={}, trainerId={}, trainingTypeId={}, date={}, duration={}",
                traineeId, trainerId, trainingTypeId, date, duration);

        Trainee trainee = traineeRepo.findById(traineeId).orElseThrow(() -> {
            logger.warn("Trainee not found with ID: {}", traineeId);
            return new RuntimeException("Trainee not found");
        });

        Trainer trainer = trainerRepo.findById(trainerId).orElseThrow(() -> {
            logger.warn("Trainer not found with ID: {}", trainerId);
            return new RuntimeException("Trainer not found");
        });

        TrainingType trainingType = trainingTypeRepo.findById(trainingTypeId).orElseThrow(() -> {
            logger.warn("TrainingType not found with ID: {}", trainingTypeId);
            return new RuntimeException("TrainingType not found");
        });

        Training training = new Training(trainee, trainer, trainingName, trainingType, date, duration);
        Training savedTraining = trainingRepo.save(training);

        logger.info("Training created with ID: {} for trainee: {}, trainer: {}", savedTraining.getId(), traineeId, trainerId);
        return savedTraining;
    }
}
