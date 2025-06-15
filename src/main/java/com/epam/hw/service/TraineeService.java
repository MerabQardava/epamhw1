package com.epam.hw.service;

import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.Trainer;
import com.epam.hw.entity.Training;
import com.epam.hw.entity.User;
import com.epam.hw.repository.TraineeRepository;
import com.epam.hw.repository.TrainerRepository;
import com.epam.hw.repository.TrainingRepository;
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
public class TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final Auth auth;

    @Autowired
    public TraineeService(TraineeRepository traineeRepository,
                          UserRepository userRepository,
                          TrainerRepository trainerRepository,
                          TrainingRepository trainingRepository,
                          Auth auth) {
        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.auth = auth;
    }

    private void isLoggedIn() {
        if (auth.getLoggedInUser() == null || auth.getLoggedInUser().getTrainee() == null) {
            logger.warn("Unauthorized access attempt – no trainee is logged in.");
            throw new IllegalStateException("No trainee is logged in.");
        }
    }

    public Trainee getTraineeByUsername(String username) {
        isLoggedIn();
        logger.debug("Fetching trainee by username: {}", username);

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            logger.info("Trainee found for username: {}", username);
            return optionalUser.get().getTrainee();
        }
        logger.warn("No user found with username: {}", username);
        return null;
    }

    public void createTrainee(String firstName,String lastName,LocalDate dateOfBirth,String address) {
        User user = new User(firstName,lastName);

        String baseUsername = user.getUsername();
        int num = 1;

        logger.debug("Creating new trainee with base username: {}", baseUsername);

        while (userRepository.findByUsername(user.getUsername()).isPresent()) {
            user.setUsername(baseUsername + num);
            num++;
        }

        Trainee trainee = new Trainee(dateOfBirth,address,user);

        traineeRepository.save(trainee);
        logger.info("Trainee created with username: {}", trainee.getUser().getUsername());
    }

    public boolean logIn(String username, String password) {
        logger.info("Attempting login for username: {}", username);
        return auth.logIn(username, password);
    }

    public boolean logOut() {
        logger.info("Logging out current user.");
        return auth.logOut();
    }

    public boolean changePassword(String newPassword) {
        isLoggedIn();
        logger.info("Changing password for logged-in trainee.");
        User user = auth.getLoggedInUser();
        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }

    public boolean updateTraineeProfile(Trainee updatedTraineeData) {
        isLoggedIn();

        logger.info("Updating profile for logged-in trainee.");
        User currentUser = auth.getLoggedInUser();
        User updatedUser = updatedTraineeData.getUser();

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
            logger.debug("Username updated to: {}", newUsername);
        }

        if (updatedUser != null) {
            currentUser.setFirstName(updatedUser.getFirstName());
            currentUser.setLastName(updatedUser.getLastName());
            currentUser.setPassword(updatedUser.getPassword());
            currentUser.setActive(updatedUser.isActive());
        }

        auth.getLoggedInUser().getTrainee().setDateOfBirth(updatedTraineeData.getDateOfBirth());
        auth.getLoggedInUser().getTrainee().setAddress(updatedTraineeData.getAddress());

        userRepository.save(currentUser);
        traineeRepository.save(auth.getLoggedInUser().getTrainee());

        logger.info("Trainee profile updated.");
        return true;
    }

    public boolean toggleTraineeStatus() {
        isLoggedIn();
        User user = auth.getLoggedInUser();
        boolean newStatus = !user.isActive();
        user.setActive(newStatus);
        userRepository.save(user);
        logger.info("Trainee status toggled to: {}", newStatus ? "ACTIVE" : "INACTIVE");
        return newStatus;
    }

    public boolean deleteByUsername(String username) {
        isLoggedIn();
        logger.warn("Deleting user with username: {}", username);

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Trainee trainee = user.getTrainee();
            if (trainee != null) {
                traineeRepository.delete(trainee);
                logger.debug("Deleted trainee entity for user: {}", username);
            }
            userRepository.delete(user);
            logger.info("User deleted: {}", username);
            return true;
        }

        logger.warn("Delete failed – no user found with username: {}", username);
        return false;
    }

    public boolean updateTraineeTrainers(Integer traineeId, Set<Integer> newTrainerIds) {
        isLoggedIn();
        logger.info("Updating trainers for traineeId: {}", traineeId);

        Trainee trainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        Set<Trainer> newTrainers = new HashSet<>(trainerRepository.findAllById(newTrainerIds));

        trainee.getTrainers().stream()
                .filter(t -> !newTrainers.contains(t))
                .forEach(trainee::removeTrainer);

        newTrainers.forEach(trainee::addTrainer);

        logger.debug("Trainer list updated for traineeId: {}", traineeId);
        return true;
    }

    public void addTrainerToTrainee(Integer traineeId, Integer trainerId) {
        isLoggedIn();
        logger.info("Assigning trainer {} to trainee {}", trainerId, traineeId);

        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow();
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow();

        trainee.addTrainer(trainer);
    }

    public void removeTrainerFromTrainee(Integer traineeId, Integer trainerId) {
        isLoggedIn();
        logger.info("Removing trainer {} from trainee {}", trainerId, traineeId);

        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow();
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow();

        trainee.removeTrainer(trainer);
    }

    public List<Training> getTraineeTrainings(
            String username,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingTypeName) {
        isLoggedIn();

        logger.debug("Fetching trainings for trainee: {} from {} to {}, trainerName={}, trainingType={}",
                username, from, to, trainerName, trainingTypeName);

        return trainingRepository.findTrainingsByTraineeCriteria(
                username, from, to, trainerName, trainingTypeName);
    }
}
