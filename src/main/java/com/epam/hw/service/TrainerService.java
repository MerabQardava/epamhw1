package com.epam.hw.service;


import com.epam.hw.entity.Trainer;
import com.epam.hw.entity.Training;
import com.epam.hw.entity.User;
import com.epam.hw.repository.TrainerRepository;
import com.epam.hw.repository.TrainingRepository;
import com.epam.hw.repository.UserRepository;
import com.epam.hw.storage.Auth;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;

    private final Auth auth;


    @Autowired
    public TrainerService(TrainerRepository trainerRepository,
                          UserRepository userRepository,
                          Auth auth,
                          TrainingRepository trainingRepository
    ) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.auth = auth;
        this.trainingRepository = trainingRepository;
    }

    private void isLoggedIn(){
        if (auth.getLoggedInUser() == null || auth.getLoggedInUser().getTrainer() == null) {
            throw new IllegalStateException("No trainer is logged in.");
        }
    }

    public Trainer createTrainer(Trainer trainer) {
        String baseUsername = trainer.getUser().getUsername();
        int num = 1;

        while (userRepository.findByUsername(trainer.getUser().getUsername()).isPresent()) {
            trainer.getUser().setUsername(baseUsername + num);
            num++;
        }

        trainerRepository.save(trainer);
        return trainer;
    }

    public boolean LogIn(String username, String password) {
        return auth.logIn(username, password);
    }

    public boolean LogOut() {
        return auth.logOut();
    }

    public Trainer getTrainerByUsername(String username) {
        isLoggedIn();

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getTrainer();
        }
        return null;
    }

    public boolean changePassword(String newPassword) {
        isLoggedIn();
        User user = auth.getLoggedInUser();
        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }

    public boolean toggleTrainerStatus() {
        isLoggedIn();

        User user = auth.getLoggedInUser();
        user.setActive(!user.isActive());
        userRepository.save(user);

        return user.isActive();
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
        return true;
    }

    public List<Training> getTrainerTrainings(
            String username,
            LocalDate from,
            LocalDate to,
            String trainerName) {
        isLoggedIn();

        return trainingRepository.findTrainingsByTrainerCriteria(username, from, to, trainerName);
    }



    // TODO 17. Get trainers list that not assigned on trainee by trainee's username






}
