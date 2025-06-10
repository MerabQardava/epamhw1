package com.epam.hw.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Trainee trainee;

    @ManyToOne
    private Trainer trainer;

    @Column(nullable = false)
    private String trainingName;

    @ManyToOne
    private TrainingType trainingType;

    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private String duration;

    public Training(Trainee traineeId, Trainer trainerId, String trainingName, TrainingType trainingType, LocalDate date, String duration) {
        this.trainee = traineeId;
        this.trainer = trainerId;
        this.trainingName = trainingName;
        this.trainingType= trainingType;
        this.date = date;
        this.duration = duration;
    }


    @Override
    public String toString() {
        return "Training{" +
                "traineeId=" + trainee +
                ", trainerId=" + trainer +
                ", trainingName='" + trainingName + '\'' +
                ", trainingType='" + trainingType + '\'' +
                ", date=" + date +
                ", duration='" + duration + '\'' +
                '}';
    }
}
