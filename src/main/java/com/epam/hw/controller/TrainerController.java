package com.epam.hw.controller;

import com.epam.hw.dto.TrainerRegistrationDTO;
import com.epam.hw.entity.Trainer;
import com.epam.hw.service.TraineeService;
import com.epam.hw.service.TrainerService;
import com.epam.hw.storage.LoginResults;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/trainer")
public class TrainerController {
    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService traineeService){
        this.trainerService=traineeService;
    }

    @PostMapping()
    public ResponseEntity<Map<String,String>> registerTrainer(@RequestBody @Valid TrainerRegistrationDTO dto){
        Trainer saved = trainerService.createTrainer(dto.firstName(),dto.lastName(),dto.specialization());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "Username",saved.getUser().getUsername(),
                "Password",saved.getUser().getPassword()
        ));
    }


    @GetMapping("/login")
    public ResponseEntity<String> loginTrainee(@RequestParam String username,
                                               @RequestParam String password){

        LoginResults authenticated = trainerService.logIn(username, password);


        if(authenticated.equals(LoginResults.USER_NOT_FOUND)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Trainer with username of "+username+" not found");
        }else if(authenticated.equals(LoginResults.BAD_PASSWORD)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }

        return ResponseEntity.ok("Login successful");

    }

}
