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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@Transactional
public class TraineeService {
    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    private Auth auth;

    @Autowired
    public TraineeService(TraineeRepository traineeRepository,
                          UserRepository userRepository,
                          TrainerRepository trainerRepository,
                          TrainingRepository trainingRepository,
                            Auth auth
    ) {
        this.trainingRepository=trainingRepository;
        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.auth = auth;


    }

    private void isLoggedIn(){
        if (auth.getLoggedInUser() == null || auth.getLoggedInUser().getTrainee() == null) {
            throw new IllegalStateException("No trainee is logged in.");
        }
    }

    public Trainee getTraineeByUsername(String username) {
        isLoggedIn();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getTrainee();
        }
        return null;
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
        return auth.logIn(username, password);
    }

    public boolean logOut() {
        return auth.logOut();
    }

    public boolean changePassword(String newPassword) {
        isLoggedIn();
        User user = auth.getLoggedInUser();
        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }

    public boolean updateTraineeProfile(Trainee updatedTraineeData) {
        isLoggedIn();


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
        return true;
    }

    public boolean toggleTraineeStatus() {
        isLoggedIn();

        User user = auth.getLoggedInUser();
        user.setActive(!user.isActive());
        userRepository.save(user);

        return user.isActive();
    }

    public boolean deleteByUsername(String username) {
        isLoggedIn();

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
        isLoggedIn();

        Trainee trainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

        Set<Trainer> newTrainers = new HashSet<>(trainerRepository.findAllById(newTrainerIds));

        trainee.getTrainers().stream()
                .filter(t -> !newTrainers.contains(t))
                .forEach(trainee::removeTrainer);

        newTrainers.forEach(trainee::addTrainer);

        return true;
    }

    public void addTrainerToTrainee(Integer traineeId, Integer trainerId) {
        isLoggedIn();

        Trainee trainee  = traineeRepository.findById(traineeId).orElseThrow();
        Trainer trainer  = trainerRepository.findById(trainerId).orElseThrow();

        trainee.addTrainer(trainer);
    }

    public void removeTrainerFromTrainee(Integer traineeId, Integer trainerId) {
        isLoggedIn();

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

        return trainingRepository.findTrainingsByTraineeCriteria(
                username, from, to, trainerName, trainingTypeName);
    }




}
