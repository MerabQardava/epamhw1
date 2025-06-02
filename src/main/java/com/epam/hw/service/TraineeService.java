package com.epam.hw.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.epam.hw.dao.TraineeDao;
import com.epam.hw.entity.Trainee;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private final TraineeDao traineeDAO;
    private final Set<String> existingUsernames = new HashSet<>();
    private final Set<Integer> ids = new HashSet<>();
    private static int idSequence = 1;

    @Autowired
    public TraineeService(TraineeDao traineeDAO) {
        this.traineeDAO = traineeDAO;
        refreshUsernames();

        logger.info("TraineeService initialized with {} usernames and {} ids", existingUsernames.size(), ids.size());
    }

    private void refreshUsernames() {
        existingUsernames.clear();
        ids.clear();

        traineeDAO.getAll().forEach(t -> {
            existingUsernames.add(t.getUsername());
            ids.add(t.getUserId());
        });
    }

    public Trainee create(String firstName, String lastName,String dob, String address) {
        String baseUsername = firstName + "." + lastName;
        String username = generateUniqueUsername(baseUsername);
        int id = idSequence++;

        while(ids.contains(id)) {
            id = idSequence++;
        }

        Trainee trainee = new Trainee(firstName, lastName, LocalDate.parse(dob), address,id);
        trainee.setUsername(username);

        traineeDAO.save(trainee);
        existingUsernames.add(username);
        logger.info("Created Trainee with id={} and username={}", id, username);
        return trainee;
    }

    private String generateUniqueUsername(String base) {
        String candidate = base;
        int counter = 1;
        while (existingUsernames.contains(candidate)) {
            candidate = base + counter++;
        }
        logger.debug("Generated unique username: {}", candidate);
        return candidate;
    }

    public Trainee get(int id) {
        logger.debug("Fetching Trainee with id={}", id);
        Trainee trainee = traineeDAO.get(id);
        if (trainee == null) {
            System.out.println("mecheti");
            logger.warn("Trainee with id={} not found", id);
        }
        return trainee;
    }



    public void update(Trainee trainee) {
        logger.info("Updating Trainee with id={}", trainee.getUserId());
        traineeDAO.update(trainee);
        logger.debug("Updated Trainee: {}", trainee);
    }

    public void delete(int id) {
        logger.info("Deleting Trainee with id={}", id);
        Trainee removed = traineeDAO.get(id);
        if (removed != null) {
            existingUsernames.remove(removed.getUsername());
            traineeDAO.delete(id);
            logger.info("Deleted Trainee with id={}", id);
        } else {
            logger.warn("Trainee with id={} not found for deletion", id);
        }
    }
}
