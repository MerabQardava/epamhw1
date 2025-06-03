package com.epam.hw.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.epam.hw.dao.TrainerDao;
import com.epam.hw.entity.Trainer;

import java.util.HashSet;
import java.util.Set;

@Service
public class TrainerService {
    TrainerDao trainerDAO;

    public static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    public TrainerService(TrainerDao trainerDAO) {
        this.trainerDAO = trainerDAO;


        logger.info("TrainerService initialized");
    }

    public Trainer create(String firstName, String lastName, Integer specId) {


        Trainer trainer = new Trainer(firstName, lastName, specId);

        trainerDAO.save(trainer);

        logger.info("Created Trainer");
        return trainer;
    }


    public Trainer get(Integer id){
        logger.debug("Fetching Trainer with id={}", id);
        Trainer trainer = trainerDAO.get(id);
        if (trainer == null) {
            logger.warn("Trainer with id={} not found", id);
        }
        return trainer;
    }

    public void update(Trainer trainer) {
        logger.info("Updating Trainer with id={}", trainer.getUserId());
        trainerDAO.update(trainer);
        logger.debug("Updated Trainer: {}", trainer);
    }


}
