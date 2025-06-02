package com.epam.hw.entity;

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
