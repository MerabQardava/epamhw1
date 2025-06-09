//package com.epam.hw.storage;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//import com.epam.hw.entity.Trainee;
//import com.epam.hw.entity.Trainer;
//import com.epam.hw.entity.Training;
//
//import java.time.LocalDate;
//import java.util.Map;
//
//
//@Component
//public class StorageInitializer implements BeanPostProcessor {
//
//    @Autowired
//    private Environment env;
//
//    private static final Logger logger = LoggerFactory.getLogger(StorageInitializer.class);
//
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        if ("traineeStorage".equals(beanName) && bean instanceof Map) {
//            Map<Integer, Trainee> storage = (Map<Integer, Trainee>) bean;
//
//            int index = 1;
//            while (true) {
//                String prefix = "trainee." + index;
//                String firstName = env.getProperty(prefix + ".firstName");
//                if (firstName == null) break;
//
//                String lastName = env.getProperty(prefix + ".lastName");
//                String address = env.getProperty(prefix + ".address");
//                Integer userId = Integer.valueOf(env.getProperty(prefix + ".userId"));
//                LocalDate dob = LocalDate.parse(env.getProperty(prefix + ".dateOfBirth"));
//
//                Trainee trainee = new Trainee(firstName, lastName, dob, address, userId);
//                storage.put(userId, trainee);
//
//                logger.debug("Loaded Trainee from properties: id={}, name={} {}", userId, firstName, lastName);
//
//                index++;
//            }
//        }else if ("trainerStorage".equals(beanName) && bean instanceof Map) {
//            Map<Integer, Trainer> storage = (Map<Integer, Trainer>) bean;
//
//            int index = 1;
//            while (true) {
//                String prefix = "trainer." + index;
//                String firstName = env.getProperty(prefix + ".firstName");
//                if (firstName == null) break;
//
//                String lastName = env.getProperty(prefix + ".lastName");
//                Integer specializationId =Integer.valueOf(env.getProperty(prefix + ".specializationId"));
//                Integer userId = Integer.valueOf(env.getProperty(prefix + ".userId"));
//                Trainer trainer = new Trainer(firstName, lastName, specializationId, userId);
//                storage.put(specializationId, trainer);
//
//                logger.debug("Loaded Trainer from properties: id={}, specializationId={}, name={} {}", userId, specializationId, firstName, lastName);
//                index++;
//            }
//
//        } else if ("trainingStorage".equals(beanName) && bean instanceof Map) {
//            Map<String, Training> storage = (Map<String, Training>) bean;
//
//            int index = 1;
//            while (true) {
//                String prefix = "training." + index;
//                String trainingName = env.getProperty(prefix + ".trainingName");
//                if (trainingName == null) break;
//
//                String trainingType= env.getProperty(prefix + ".trainingType");
//                String duration = env.getProperty(prefix + ".duration");
//                Integer traineeId = Integer.valueOf(env.getProperty(prefix + ".traineeId"));
//                Integer trainerId = Integer.valueOf(env.getProperty(prefix + ".trainerId"));
//                LocalDate data = LocalDate.parse(env.getProperty(prefix + ".date"));
//
//                Training training = new Training(traineeId,trainerId,trainingName,trainingType,data,duration);
//                storage.put(trainingName, training);
//
//                logger.debug("Loaded Training from properties: name={}, traineeId={}, trainerId={}", trainingName, traineeId, trainerId);
//
//                index++;
//            }
//            logger.info("Loaded {} Trainings from properties", index-1);
//
//        }
//        return bean;
//    }
//
//
//
//}
