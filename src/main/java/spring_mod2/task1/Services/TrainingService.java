package spring_mod2.task1.Services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring_mod2.task1.DAO.TrainingDAO;
import spring_mod2.task1.Entities.Training;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
public class TrainingService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

    TrainingDAO trainingDAO;
    private final Set<Integer> ids = new HashSet<>();

    @Autowired
    public TrainingService(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;

        ids.clear();

        trainingDAO.getAll().forEach(t -> {
            ids.add(t.getTraineeId());
        });

        logger.info("TrainingService initialized with {} trainee IDs", ids.size());
    }

    public Training create(Integer traineeId,Integer trainerId, String trainingName,String trainingType,String date,String duration) {
        Training training = new Training(traineeId,trainerId,trainingName,trainingType, LocalDate.parse(date),duration);
        trainingDAO.save(training);

        logger.info("Created Training with name={}", trainingName);
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
