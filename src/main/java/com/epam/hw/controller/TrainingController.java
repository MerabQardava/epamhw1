package com.epam.hw.controller;


import com.epam.hw.dto.CreateTrainingDTO;
import com.epam.hw.entity.TrainingType;
import com.epam.hw.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/training")
public class TrainingController {
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService){
        this.trainingService=trainingService;
    }

    @GetMapping
    public ResponseEntity<List<TrainingType>> getTrainingTypes(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(trainingService.getTrainingTypes());

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<String> addTraining(@RequestBody @Valid CreateTrainingDTO dto) {
        try {
            trainingService.addTraining(dto.traineeUsername(), dto.trainerUsername(), dto.trainingName(),
                    dto.trainingTypeName(), LocalDate.parse(dto.date()), dto.duration());
            return ResponseEntity.status(HttpStatus.CREATED).body("Training added successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding training: " + e.getMessage());
        }

    }

}
