package com.epam.hw.controller;


import com.epam.hw.entity.TrainingType;
import com.epam.hw.service.TrainingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
