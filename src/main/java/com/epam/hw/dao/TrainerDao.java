//package com.epam.hw.dao;
//
//import com.epam.hw.entity.Trainee;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Repository;
//import com.epam.hw.entity.Trainer;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Repository
//public class TrainerDao {
//    private static final Logger logger = LoggerFactory.getLogger(TrainerDao.class);
//    private Integer id = 1;
//    private final Map<Integer, Trainer> storage;
//
//    public TrainerDao(Map<Integer, Trainer> trainerStorage) {
//        this.storage = trainerStorage;
//    }
//
//    public void save(Trainer trainer) {
//        logger.info("Saving trainer with ID: {}", trainer.getUserId());
//        while(storage.containsKey(id)){
//            id++;
//        }
//        trainer.setUserId(id);
//        trainer.setUsername(getUniqueUsername(trainer));
//        storage.put(id, trainer);
//        logger.debug("Trainer saved: {}", trainer);
//        id++;
//    }
//
//    public Trainer get(int id) {
//        logger.debug("Retrieving trainer with ID: {}", id);
//        Trainer trainer = storage.get(id);
//        if (trainer == null) {
//            logger.warn("Trainer with ID {} not found", id);
//        }
//        return trainer;
//    }
//
//
//    public void update(Trainer trainer) {
//        int id = trainer.getUserId();
//        if (!storage.containsKey(id)) {
//            logger.warn("Cannot update - trainer with ID {} does not exist", id);
//            return;
//        }
//        logger.info("Updating trainer with ID: {}", id);
//        storage.put(id, trainer);
//        logger.debug("Trainer updated: {}", trainer);
//    }
//
//    private String getUniqueUsername(Trainer trainee) {
//        final String baseUsername = trainee.getUsername();
//        int counter = 1;
//
//        if (storage.values().stream().noneMatch(user -> user.getUsername().equals(baseUsername))) {
//            return baseUsername;
//        }
//
//        while (true) {
//            final String candidateUsername = baseUsername + counter;
//            if (storage.values().stream().noneMatch(user -> user.getUsername().equals(candidateUsername))) {
//                return candidateUsername;
//            }
//            counter++;
//        }
//    }
//
//}
//
//
//
