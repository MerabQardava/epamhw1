package com.epam.hw.service;

import com.epam.hw.entity.Trainer;
import com.epam.hw.repository.TrainerRepository;
import com.epam.hw.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class TrainerService {
    TrainerRepository trainerRepository;
    UserRepository userRepository;



    @Autowired
    public TrainerService(TrainerRepository trainerRepository,UserRepository userRepository) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
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




}
