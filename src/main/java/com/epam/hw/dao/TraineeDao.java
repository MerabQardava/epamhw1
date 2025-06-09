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
    private Integer id = 1;

    @Autowired
    public TraineeDao(Map<Integer, Trainee> traineeStorage) {
        this.storage = traineeStorage;
    }


    public void save(Trainee trainee) {
        logger.info("Saving trainee with ID: {}", trainee.getId());
        while(storage.containsKey(id)){
            id++;
        }
        trainee.setId(id);
        trainee.getUser().setUsername(getUniqueUsername(trainee));
        storage.put(id, trainee);
        logger.debug("Trainee saved: {}", trainee);
        id++;
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
        int id = trainee.getId();
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

    private String getUniqueUsername(Trainee trainee) {
        final String baseUsername = trainee.getUser().getUsername();
        int counter = 1;

        if (storage.values().stream().noneMatch(user -> user.getUser().getUsername().equals(baseUsername))) {
            return baseUsername;
        }

        while (true) {
            final String candidateUsername = baseUsername + counter;
            if (storage.values().stream().noneMatch(user -> user.getUser().getUsername().equals(candidateUsername))) {
                return candidateUsername;
            }
            counter++;
        }
    }

}

