package spring_mod2.task1.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring_mod2.task1.DAO.TrainerDAO;
import spring_mod2.task1.Entities.Trainee;
import spring_mod2.task1.Entities.Trainer;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
public class TrainerService {
    TrainerDAO trainerDAO;
    private final Set<Integer> ids = new HashSet<>();
    private final Set<String> existingUsernames = new HashSet<>();
    private static int idSequence = 1;

    public static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    public TrainerService(TrainerDAO trainerDAO) {
        this.trainerDAO = trainerDAO;

        existingUsernames.clear();
        ids.clear();

        trainerDAO.getAll().forEach(t -> {
            existingUsernames.add(t.getUsername());
            ids.add(t.getUserId());
        });

        logger.info("TrainerService initialized with {} usernames and {} ids", existingUsernames.size(), ids.size());
    }

    public Trainer create(String firstName, String lastName, Integer specId) {
        String baseUsername = firstName + "." + lastName;
        String username = generateUniqueUsername(baseUsername);
        int id = idSequence++;

        while(ids.contains(id)) {
            id = idSequence++;
        }

        Trainer trainer = new Trainer(firstName, lastName, specId,id);
        trainer.setUsername(username);

        trainerDAO.save(trainer);
        existingUsernames.add(username);

        logger.info("Created Trainer with id={} and username={}", id, username);
        return trainer;
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
