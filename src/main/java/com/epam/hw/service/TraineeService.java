package com.epam.hw.service;

import com.epam.hw.dto.UpdateTraineeDTO;
import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.Trainer;
import com.epam.hw.entity.Training;
import com.epam.hw.entity.User;
import com.epam.hw.repository.TraineeRepository;
import com.epam.hw.repository.TrainerRepository;
import com.epam.hw.repository.TrainingRepository;
import com.epam.hw.repository.UserRepository;
import com.epam.hw.storage.Auth;
import com.epam.hw.storage.LoginResults;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.TypeCollector;
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
            logger.info("User found for username: {}", username);
            return optionalUser.get().getTrainee();
        }
        logger.warn("No user found with username: {}", username);
        return null;
    }

    public Trainee createTrainee(String firstName,String lastName,LocalDate dateOfBirth,String address) {
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
        return trainee;
    }

    public LoginResults logIn(String username, String password) {
        logger.info("Attempting login for username: {}", username);

        Optional<Trainee> traineeOpt = traineeRepository.findByUser_Username(username);
        if (traineeOpt.isEmpty()) {
            logger.info("Trainee not found: {}", username);
            return LoginResults.USER_NOT_FOUND;
        }

        boolean ok = auth.logIn(username, password);
        return ok ? LoginResults.SUCCESS : LoginResults.BAD_PASSWORD;
    }

    public boolean logOut() {
        logger.info("Logging out current user.");
        return auth.logOut();
    }

    public boolean changePassword(String username,String newPassword) {
        isLoggedIn();
        logger.info("Changing password for logged-in trainee.");
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if(user.getTrainee()==null) {
            logger.warn("User {} is not a trainee, cannot change password.", username);
            throw new EntityNotFoundException("User is not a trainee: " + username);
        }

        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }

    public Trainee updateTraineeProfile(String username,UpdateTraineeDTO updatedTraineeData) {
        isLoggedIn();

        logger.info("Updating profile for logged-in trainee.");

        Trainee trainee = getTraineeByUsername(username);
        if (trainee == null) {
            logger.warn("Trainee not found for username: {}",username);
            throw new EntityNotFoundException("Trainee not found: " + username);
        }

        User currentUser = trainee.getUser();

        currentUser.setFirstName(updatedTraineeData.firstName());
        currentUser.setLastName(updatedTraineeData.lastName());

        if(updatedTraineeData.isActive()){
            toggleTraineeStatus(username);
        }

        if (updatedTraineeData.dob() != null) {
            trainee.setDateOfBirth(LocalDate.parse(updatedTraineeData.dob()));
        }

        if (updatedTraineeData.address() != null) {
            trainee.setAddress(updatedTraineeData.address());
        }


        traineeRepository.save(trainee);

        return trainee;


    }

    public boolean toggleTraineeStatus(String username) {
        isLoggedIn();
        Trainee trainee = traineeRepository.findByUser_Username(username).orElseThrow(
                () -> new EntityNotFoundException("Trainee not found: " + username));

        User user = trainee.getUser();
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

    public Set<Trainer> updateTraineeTrainers(String username, Set<String> trainersList) {
        isLoggedIn();
        logger.info("Updating trainers for traineeId: {}", username);

        Trainee trainee = traineeRepository.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        Set<Trainer> newTrainers = new HashSet<>(trainerRepository.findAllByUser_UsernameIn(trainersList));

        trainee.getTrainers().removeIf(t -> !newTrainers.contains(t));
        newTrainers.forEach(trainee::addTrainer);

        logger.debug("Trainer list updated for traineeId: {}", username);
        return trainee.getTrainers();

    }

    public void addTrainerToTrainee(String traineeUsername, String trainerUsername) {
        isLoggedIn();
        logger.info("Assigning trainer {} to trainee {}", trainerUsername, traineeUsername);

        Trainee trainee = traineeRepository.findByUser_Username(traineeUsername).orElseThrow();
        Trainer trainer = trainerRepository.findByUser_Username(trainerUsername).orElseThrow();

        trainee.addTrainer(trainer);
    }

    public void removeTrainerFromTrainee(String traineeUsername, String trainerUsername) {
        isLoggedIn();
        logger.info("Removing trainer {} from trainee {}", traineeUsername, trainerUsername);

        Trainee trainee = traineeRepository.findByUser_Username(traineeUsername).orElseThrow();
        Trainer trainer = trainerRepository.findByUser_Username(trainerUsername).orElseThrow();

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
