package spring_mod2.task1.Entities;

import org.springframework.stereotype.Component;

@Component
public class TrainingType {
    private String trainingTypeName;


    public String getTrainingTypeName() {
        return trainingTypeName;
    }

    public void setTrainingTypeName(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }
}
