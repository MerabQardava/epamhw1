//package com.epam.hw.service;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import com.epam.hw.dao.TraineeDao;
//import com.epam.hw.entity.Trainee;
//
//
//import java.time.LocalDate;
//import java.util.HashSet;
//import java.util.Set;
//
//@Service
//public class TraineeService {
//    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
//    private final TraineeDao traineeDAO;
//
//
//    @Autowired
//    public TraineeService(TraineeDao traineeDAO) {
//        this.traineeDAO = traineeDAO;
//
//        logger.info("TraineeService initialized");
//    }
//
//
//    public Trainee create(String firstName, String lastName,String dob, String address) {
//        String baseUsername = firstName + "." + lastName;
//
//        Trainee trainee = new Trainee(firstName, lastName, LocalDate.parse(dob), address);
//
//        traineeDAO.save(trainee);
//        logger.info("Created Trainee");
//        return trainee;
//    }
//
//
//    public Trainee get(int id) {
//        logger.debug("Fetching Trainee with id={}", id);
//        Trainee trainee = traineeDAO.get(id);
//        if (trainee == null) {
//            System.out.println("mecheti");
//            logger.warn("Trainee with id={} not found", id);
//        }
//        return trainee;
//    }
//
//
//
//    public void update(Trainee trainee) {
//        logger.info("Updating Trainee with id={}", trainee.getId());
//        traineeDAO.update(trainee);
//        logger.debug("Updated Trainee: {}", trainee);
//    }
//
//    public void delete(int id) {
//        logger.info("Deleting Trainee with id={}", id);
//        Trainee removed = traineeDAO.get(id);
//        if (removed != null) {
//            traineeDAO.delete(id);
//            logger.info("Deleted Trainee with id={}", id);
//        } else {
//            logger.warn("Trainee with id={} not found for deletion", id);
//        }
//    }
//}
