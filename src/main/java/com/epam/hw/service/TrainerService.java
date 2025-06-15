package com.epam.hw.service;

import com.epam.hw.entity.*;
import com.epam.hw.repository.TrainerRepository;
import com.epam.hw.repository.TrainingRepository;
import com.epam.hw.repository.TrainingTypeRepository;
import com.epam.hw.repository.UserRepository;
import com.epam.hw.storage.Auth;
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
    private final Auth auth;
    private final TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public TrainerService(TrainerRepository trainerRepository,
                          UserRepository userRepository,
                          Auth auth,
                          TrainingRepository trainingRepository, TrainingTypeRepository trainingTypeRepository) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.auth = auth;
        this.trainingRepository = trainingRepository;
        this.trainingTypeRepository = trainingTypeRepository;
    }

    private void isLoggedIn() {
        if (auth.getLoggedInUser() == null || auth.getLoggedInUser().getTrainer() == null) {
            logger.warn("Unauthorized access attempt – no trainer is logged in.");
            throw new IllegalStateException("No trainer is logged in.");
        }
    }

    public Trainer createTrainer(String firstName,String lastName,String trainingTypeName) {

        Optional<TrainingType> trainingTypeOptional = trainingTypeRepository.findByTrainingTypeName(trainingTypeName);
        if (trainingTypeOptional.isEmpty()) {
            throw new IllegalStateException("Training type " + trainingTypeName + " does not exist.");
        }

        User user = new User(firstName,lastName);

        String baseUsername = user.getUsername();
        int num = 1;

        while (userRepository.findByUsername(user.getUsername()).isPresent()) {
            user.setUsername(baseUsername + num);
            num++;
        }

        Trainer trainer = new Trainer(trainingTypeOptional.get(), user);

        trainerRepository.save(trainer);
        logger.info("Trainer created with username: {}", trainer.getUser().getUsername());
        return trainer;
    }

    public boolean LogIn(String username, String password) {
        logger.info("Trainer login attempt: {}", username);
        return auth.logIn(username, password);
    }

    public boolean LogOut() {
        logger.info("Trainer logout attempt.");
        return auth.logOut();
    }

    public Trainer getTrainerByUsername(String username) {
        isLoggedIn();
        logger.debug("Fetching trainer by username: {}", username);

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            logger.info("Trainer found for username: {}", username);
            return optionalUser.get().getTrainer();
        }

        logger.warn("No trainer found for username: {}", username);
        return null;
    }

    public boolean changePassword(String newPassword) {
        isLoggedIn();
        User user = auth.getLoggedInUser();
        user.setPassword(newPassword);
        userRepository.save(user);
        logger.info("Password changed for trainer: {}", user.getUsername());
        return true;
    }

    public boolean toggleTrainerStatus() {
        isLoggedIn();
        User user = auth.getLoggedInUser();
        boolean newStatus = !user.isActive();
        user.setActive(newStatus);
        userRepository.save(user);
        logger.info("Trainer {} status toggled to: {}", user.getUsername(), newStatus ? "ACTIVE" : "INACTIVE");
        return newStatus;
    }

    public boolean updateTraineeProfile(Trainer updatedTrainerData) {
        isLoggedIn();

        User currentUser = auth.getLoggedInUser();
        User updatedUser = updatedTrainerData.getUser();

        if (updatedUser != null && updatedUser.getUsername() != null &&
                !updatedUser.getUsername().equals(currentUser.getUsername())) {

            String baseUsername = updatedUser.getUsername();
            String newUsername = baseUsername;
            int num = 1;

            while (userRepository.findByUsername(newUsername).isPresent()) {
                newUsername = baseUsername + num;
                num++;
            }

            currentUser.setUsername(newUsername);
            logger.debug("Trainer username updated to: {}", newUsername);
        }

        if (updatedUser != null) {
            currentUser.setFirstName(updatedUser.getFirstName());
            currentUser.setLastName(updatedUser.getLastName());
            currentUser.setPassword(updatedUser.getPassword());
            currentUser.setActive(updatedUser.isActive());
        }

        auth.getLoggedInUser().getTrainer().setSpecializationId(updatedTrainerData.getSpecializationId());

        userRepository.save(currentUser);
        trainerRepository.save(auth.getLoggedInUser().getTrainer());

        logger.info("Trainer profile updated: {}", currentUser.getUsername());
        return true;
    }

    public List<Training> getTrainerTrainings(String username, LocalDate from, LocalDate to, String trainerName) {
        isLoggedIn();
        logger.debug("Fetching trainings for trainer: {} from {} to {} (name filter: {})",
                username, from, to, trainerName);

        return trainingRepository.findTrainingsByTrainerCriteria(username, from, to, trainerName);
    }

    public List<Trainer> getUnassignedTraineeTrainers(String username) {
        isLoggedIn();
        logger.debug("Fetching unassigned trainers for trainee: {}", username);

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            Trainee trainee = user.get().getTrainee();
            if (trainee == null) {
                logger.warn("No trainee found for user: {}", username);
                return Collections.emptyList();
            }

            List<Trainer> allTrainers = trainerRepository.findAll();
            List<Trainer> unassignedTrainers = new ArrayList<>(allTrainers);
            unassignedTrainers.removeAll(trainee.getTrainers());

            logger.info("Unassigned trainers returned for trainee: {}", username);
            return unassignedTrainers;
        }

        logger.warn("No user found with username: {}", username);
        return Collections.emptyList();
    }
}
