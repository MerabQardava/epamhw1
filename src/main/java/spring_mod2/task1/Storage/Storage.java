package spring_mod2.task1.Storage;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import spring_mod2.task1.Entities.Trainee;
import spring_mod2.task1.Entities.Trainer;
import spring_mod2.task1.Entities.Training;

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
