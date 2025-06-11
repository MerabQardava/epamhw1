package com.epam.hw.service;

import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.Trainer;
import com.epam.hw.entity.User;
import com.epam.hw.repository.TraineeRepository;

import com.epam.hw.repository.TrainerRepository;
import com.epam.hw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Service
public class TraineeService {
    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;

    private Trainee loggedInTrainee = null;

    @Autowired
    public TraineeService(TraineeRepository traineeRepository, UserRepository userRepository,TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;

    }

    public void createTrainee(Trainee trainee) {
        String baseUsername = trainee.getUser().getUsername();
        int num = 1;

        while (userRepository.findByUsername(trainee.getUser().getUsername()).isPresent()) {
            trainee.getUser().setUsername(baseUsername + num);
            num++;
        }

        traineeRepository.save(trainee);
    }

    public boolean logIn(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(password)) {
                if (user.getTrainee() != null) {
                    loggedInTrainee = user.getTrainee();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean logOut() {
        if (loggedInTrainee == null) {
            throw new IllegalStateException("No trainee is logged in.");
        }
        loggedInTrainee = null;
        return true;
    }

    public boolean changePassword(String newPassword) {
        if (loggedInTrainee == null) {
            throw new IllegalStateException("No trainee is logged in.");
        }
        User user = loggedInTrainee.getUser();
        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }

    public boolean updateTraineeProfile(Trainee updatedTraineeData) {
        if (loggedInTrainee == null) {
            throw new IllegalStateException("No trainee is logged in.");
        }


        User currentUser = loggedInTrainee.getUser();
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
        }


        if (updatedUser != null) {
            currentUser.setFirstName(updatedUser.getFirstName());
            currentUser.setLastName(updatedUser.getLastName());
            currentUser.setPassword(updatedUser.getPassword());
            currentUser.setActive(updatedUser.isActive());
        }

        loggedInTrainee.setDateOfBirth(updatedTraineeData.getDateOfBirth());
        loggedInTrainee.setAddress(updatedTraineeData.getAddress());


        userRepository.save(currentUser);
        traineeRepository.save(loggedInTrainee);
        return true;
    }

    public boolean toggleTraineeStatus() {
        if (loggedInTrainee == null) {
            throw new IllegalStateException("No trainee is logged in.");
        }
        User user = loggedInTrainee.getUser();
        user.setActive(!user.isActive());
        userRepository.save(user);
        loggedInTrainee.setUser(user);
        return user.isActive();
    }

    public boolean deleteByUsername(String username) {
        if (loggedInTrainee == null) {
            throw new IllegalStateException("No trainee is logged in.");
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Trainee trainee = user.getTrainee();
            if (trainee != null) {
                traineeRepository.delete(trainee);
            }
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    public boolean updateTraineeTrainers(Integer traineeId, Set<Integer> newTrainerIds) {
        if (loggedInTrainee == null) {
            throw new IllegalStateException("No trainee is logged in.");
        }

        Optional<Trainee> optTrainee = traineeRepository.findById(traineeId);
        if (optTrainee.isEmpty()) {
            return false;
        }

        Trainee trainee = optTrainee.get();

        Set<Trainer> trainers = new HashSet<>(trainerRepository.findAllById(newTrainerIds));

        trainee.setTrainers(trainers);

        traineeRepository.save(trainee);

        return true;
    }

    public void addTrainerToTrainee(Integer traineeId, Integer trainerId) {
        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow();
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow();
        trainee.getTrainers().add(trainer);
        traineeRepository.save(trainee);
    }

    public void removeTrainerFromTrainee(Integer traineeId, Integer trainerId) {
        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow();
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow();
        trainee.getTrainers().remove(trainer);
        traineeRepository.save(trainee);
    }

    //TODO Get Trainee Trainings List by trainee username and criteria (from date, to date, trainer
    //name, training type).




}
