package com.epam.hw.service;

import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.Trainer;
import com.epam.hw.entity.Training;
import com.epam.hw.entity.TrainingType;
import com.epam.hw.repository.TraineeRepository;
import com.epam.hw.repository.TrainerRepository;
import com.epam.hw.repository.TrainingRepository;
import com.epam.hw.repository.TrainingTypeRepository;
import com.epam.hw.storage.Auth;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingRepository trainingRepo;
    private final TraineeRepository traineeRepo;
    private final TrainerRepository trainerRepo;
    private final TrainingTypeRepository trainingTypeRepo;

    private final Auth auth;

    public TrainingService(TrainingRepository trainingRepo,
                           TraineeRepository traineeRepo,
                           TrainerRepository trainerRepo,
                           TrainingTypeRepository trainingTypeRepo,
                           Auth auth) {
        this.trainingRepo = trainingRepo;
        this.traineeRepo = traineeRepo;
        this.trainerRepo = trainerRepo;
        this.trainingTypeRepo = trainingTypeRepo;
        this.auth=auth;
    }


    private void isLoggedIn() {
        if (auth.getLoggedInUser() == null) {
            logger.warn("Unauthorized access attempt – no user is logged in.");
            throw new IllegalStateException("No user is logged in.");
        }
    }
    @Transactional
    public Training addTraining(String traineeUsername, String trainerUsername, String trainingName, String trainingTypeName, LocalDate date, Integer duration) {
        isLoggedIn();

        logger.debug("Attempting to add training for trainee={}, trainer={}, trainingType={}, date={}, duration={}",
                traineeUsername, trainerUsername, trainingTypeName, date, duration);

        Trainee trainee = traineeRepo.findByUser_Username(traineeUsername).orElseThrow(() -> {
            logger.warn("Trainee not found with ID: {}", traineeUsername);
            return new EntityNotFoundException("Trainee not found");
        });

        Trainer trainer = trainerRepo.findByUser_Username(trainerUsername).orElseThrow(() -> {
            logger.warn("Trainer not found with ID: {}", trainerUsername);
            return new EntityNotFoundException("Trainer not found");
        });

        TrainingType trainingType = trainingTypeRepo.findByTrainingTypeName(trainingTypeName).orElseThrow(() -> {
            logger.warn("TrainingType not found with name: {}", trainingTypeName);
            return new EntityNotFoundException("TrainingType not found");
        });

        Training training = new Training(trainee, trainer, trainingName, trainingType, date, duration);
        Training savedTraining = trainingRepo.save(training);

        logger.info("Training created with ID: {} for trainee: {}, trainer: {}", savedTraining.getId(), traineeUsername, trainerUsername);
        return savedTraining;
    }

    public List<TrainingType> getTrainingTypes(){
        isLoggedIn();

        return trainingTypeRepo.findAll();

    }
}
