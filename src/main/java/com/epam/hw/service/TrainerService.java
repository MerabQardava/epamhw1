package com.epam.hw.service;

import com.epam.hw.dto.UpdateTrainerDTO;
import com.epam.hw.entity.*;
import com.epam.hw.repository.TrainerRepository;
import com.epam.hw.repository.TrainingRepository;
import com.epam.hw.repository.TrainingTypeRepository;
import com.epam.hw.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository,
                          UserRepository userRepository,
                          TrainingRepository trainingRepository) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
    }



    public Trainer getTrainerByUsername(String username) {
        logger.debug("Fetching trainer by username: {}", username);

        return trainerRepository.findByUser_Username(username).orElseThrow(() -> {
            logger.warn("No trainer found for username: {}", username);
            return new EntityNotFoundException("Trainer not found for username: " + username);
        });
    }

    public boolean changePassword(String username, String newPassword) {
        logger.info("Changing password for logged-in trainer.");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if(user.getTrainer()==null) {
            logger.warn("User {} is not a trainer, cannot change password.", username);
            throw new EntityNotFoundException("User is not a trainer: " + username);
        }

        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }

    public boolean toggleTrainerStatus(String username) {
        Trainer trainer = getTrainerByUsername(username);

        User user = trainer.getUser();
        boolean newStatus = !user.isActive();
        user.setActive(newStatus);

        userRepository.save(user);
        logger.info("Trainer status toggled to: {}", newStatus ? "ACTIVE" : "INACTIVE");
        return newStatus;
    }

    public Trainer updateTrainerProfile(String username,UpdateTrainerDTO dto) {

        Trainer trainer = getTrainerByUsername(username);


        User user = trainer.getUser();

        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());

        if(dto.isActive()){
            toggleTrainerStatus(username);
        }

        trainerRepository.save(trainer);

        return trainer;

    }

    public List<Training> getTrainerTrainings(String username, LocalDate from, LocalDate to, String trainerName) {
        logger.debug("Fetching trainings for trainer: {} from {} to {} (name filter: {})",
                username, from, to, trainerName);

        return trainingRepository.findTrainingsByTrainerCriteria(username, from, to, trainerName);
    }

    public List<Trainer> getUnassignedTraineeTrainers(String username) {
        logger.debug("Fetching unassigned trainers for trainee: {}", username);

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Trainee trainee = user.get().getTrainee();

            if (trainee == null) {
                logger.warn("No trainee found for user: {}", username);
                throw new EntityNotFoundException("Trainer not found: " + username);
            }

            List<Trainer> allTrainers = trainerRepository.findAll();
            List<Trainer> unassignedTrainers = new ArrayList<>(allTrainers);
            unassignedTrainers.removeAll(trainee.getTrainers());
            unassignedTrainers.removeIf(obj->!obj.getUser().isActive());

            logger.info("Unassigned trainers returned for trainee: {}", username);
            return unassignedTrainers;
        }

        logger.warn("No trainee found with username: {}", username);
        throw new EntityNotFoundException("Trainer not found: " + username);
    }


}
