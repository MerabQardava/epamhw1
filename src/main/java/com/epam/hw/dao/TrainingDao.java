package com.epam.hw.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.epam.hw.entity.Training;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TrainingDao {
    private static final Logger logger = LoggerFactory.getLogger(TrainingDao.class);

    private final Map<String, Training> storage;

    public TrainingDao(Map<String, Training> trainingStorage) {
        this.storage = trainingStorage;
    }

    public void save(Training training) {
        logger.info("Saving training with name: {}", training.getTrainingName());
        storage.put(training.getTrainingName(), training);
        logger.debug("Training saved: {}", training);
    }

    public Training get(String trainingName) {
        logger.debug("Retrieving training with name: {}", trainingName);
        Training training = storage.get(trainingName);
        if (training == null) {
            logger.warn("Training with name '{}' not found", trainingName);
        }
        return training;
    }

    public List<Training> getAll() {
        return new ArrayList<>(storage.values());
    }

    public void update(Training training) {
        String name = training.getTrainingName();
        if (!storage.containsKey(name)) {
            logger.warn("Cannot update - training with name '{}' does not exist", name);
            return;
        }
        logger.info("Updating training with name: {}", name);
        storage.put(name, training);
        logger.debug("Training updated: {}", training);
    }

}



