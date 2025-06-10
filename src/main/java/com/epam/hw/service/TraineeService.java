package com.epam.hw.service;

import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.User;
import com.epam.hw.repository.TraineeRepository;

import com.epam.hw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TraineeService {
    private final TraineeRepository traineeRepository;
    private final UserRepository userRepository;

    @Autowired
    public TraineeService(TraineeRepository traineeRepository, UserRepository userRepository) {
        this.traineeRepository = traineeRepository;
        this.userRepository = userRepository;

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


}
