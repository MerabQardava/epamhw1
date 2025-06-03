package com.epam.hw.dto;

public record TrainingDto(
        Integer traineeId,
        Integer trainerId,
        String trainingName,
        String trainingType,
        String date,
        String duration
) {}