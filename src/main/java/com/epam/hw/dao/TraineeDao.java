package com.epam.hw.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.epam.hw.entity.Trainee;

import java.util.*;

@Repository
public class TraineeDao {
    private final Map<Integer, Trainee> storage;
    private static final Logger logger = LoggerFactory.getLogger(TraineeDao.class);
    @Autowired
    public TraineeDao(Map<Integer, Trainee> traineeStorage) {
        this.storage = traineeStorage;
    }


    public void save(Trainee trainee) {
        logger.info("Saving trainee with ID: {}", trainee.getUserId());
        storage.put(trainee.getUserId(), trainee);
        logger.debug("Trainee saved: {}", trainee);
    }

    public void delete(int id) {
        if (storage.containsKey(id)) {
            logger.info("Deleting trainee with ID: {}", id);
            storage.remove(id);
        } else {
            logger.warn("Cannot delete - trainee with ID {} does not exist", id);
        }
    }
    public void update(Trainee trainee) {
        int id = trainee.getUserId();
        if (!storage.containsKey(id)) {
            logger.warn("Cannot update - trainee with ID {} does not exist", id);
            return;
        }
        logger.info("Updating trainee with ID: {}", id);
        storage.put(id, trainee);
        logger.debug("Trainee updated: {}", trainee);
    }

    public Trainee get(int id) {
        logger.debug("Retrieving trainee with ID: {}", id);
        Trainee trainee = storage.get(id);
        if (trainee == null) {
            logger.warn("Trainee with ID {} not found", id);
        }
        return trainee;
    }

    public List<Trainee> getAll() {
        return new ArrayList<>(storage.values());
    }
}

