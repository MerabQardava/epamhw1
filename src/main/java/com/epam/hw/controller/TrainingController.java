package com.epam.hw.controller;


import com.epam.hw.dto.CreateTrainingDTO;
import com.epam.hw.entity.TrainingType;
import com.epam.hw.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Training Controller", description = "Endpoints for managing trainings")
@RestController
@RequestMapping("/training")
@Slf4j
public class TrainingController {
    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService){
        this.trainingService=trainingService;
    }

    @Operation(summary = "Get list of all available training types")
    @GetMapping("/types")
    public ResponseEntity<List<TrainingType>> getTrainingTypes(){
        log.info("GET /training - Fetching all training types");
        List<TrainingType> types = trainingService.getTrainingTypes();
        log.info("Retrieved {} training types", types.size());
        return ResponseEntity.ok(types);
    }

    @Operation(summary = "Create a new training session between trainee and trainer")
    @PostMapping("/trainee/{traineeUsername}/trainer/{trainerUsername}")
    public ResponseEntity<String> addTraining(
            @PathVariable String traineeUsername,
            @PathVariable String trainerUsername,
            @RequestBody @Valid CreateTrainingDTO dto) {
        log.info("POST /training/trainee/{}/trainer/{} - Creating training: type={}, date={}, duration={}",
                traineeUsername, trainerUsername, dto.trainingTypeName(), dto.date(), dto.duration());

        trainingService.addTraining(traineeUsername, trainerUsername, dto.trainingName(),
                dto.trainingTypeName(), LocalDate.parse(dto.date()), dto.duration());

        log.info("Training created successfully for trainee {} and trainer {}", traineeUsername, trainerUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body("Training added successfully");
    }

}
