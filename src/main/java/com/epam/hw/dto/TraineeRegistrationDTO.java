package com.epam.hw.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TraineeRegistrationDTO(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String dob,
        String address
) {}