package com.epam.hw.storage;

import com.epam.hw.entity.TrainingType;
import com.epam.hw.repository.TrainingTypeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseInitializer {

    private final TrainingTypeRepository trainingTypeRepository;

    public DatabaseInitializer(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @PostConstruct
    public void init() {
        List<String> defaults = List.of("JavaScript", "Python", "C#", "Spring");

        for (String name : defaults) {
            boolean exists = trainingTypeRepository.existsByTrainingTypeName(name);
            if (!exists) {
                trainingTypeRepository.save(new TrainingType(name));
            }
        }
    }
}