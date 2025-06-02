package com.epam.hw.storage;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.Trainer;
import com.epam.hw.entity.Training;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Storage {

    @Bean
    public Map<Integer, Trainee> traineeStorage() {
        return new HashMap<>();
    };
    @Bean
    public Map<Integer, Trainer> trainerStorage() {
        return new HashMap<>();
    };
    @Bean
    public Map<String, Training> trainingStorage() {
        return new HashMap<>();
    };

}
