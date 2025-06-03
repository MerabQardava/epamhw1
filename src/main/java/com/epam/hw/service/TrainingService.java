package com.epam.hw.service;


import com.epam.hw.dto.TrainingDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.epam.hw.dao.TrainingDao;
import com.epam.hw.entity.Training;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    TrainingDao trainingDAO;

    @Autowired
    public TrainingService(TrainingDao trainingDAO) {
        this.trainingDAO = trainingDAO;


        logger.info("TrainingService initialized");
    }

    public Training create(TrainingDto trainingDTO) {
        Training training = new Training(
                trainingDTO.traineeId(),
                trainingDTO.trainerId(),
                trainingDTO.trainingName(),
                trainingDTO.trainingType(),
                LocalDate.parse(trainingDTO.date()),
                trainingDTO.duration()
        );
        trainingDAO.save(training);

        logger.info("Created Training with name={}", training.getTrainingName());
        return training;
    }

    public Training get(String trainingName) {
        logger.debug("Fetching Training with name={}", trainingName);
        Training training = trainingDAO.get(trainingName);
        if (training == null) {
            logger.warn("Training with name={} not found", trainingName);
        }
        return training;
    }



}
