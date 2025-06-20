package com.epam.hw.controller;

import com.epam.hw.dto.TraineeRegistrationDTO;
import com.epam.hw.entity.Trainee;
import com.epam.hw.service.TraineeService;
import com.epam.hw.storage.LoginResults;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/trainee")
public class TraineeController {
    private final TraineeService traineeService;

    @Autowired
    public TraineeController(TraineeService traineeService){
        this.traineeService = traineeService;
    }

    @PostMapping()
    public ResponseEntity<Map<String, String>> registerTrainee(@RequestBody @Valid TraineeRegistrationDTO dto){
        Trainee saved = traineeService.createTrainee(dto.firstName(), dto.lastName(),LocalDate.parse(dto.dob()),dto.address());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "Username",saved.getUser().getUsername(),
                "Password",saved.getUser().getPassword()));
    }

    @GetMapping("/login")
    public ResponseEntity<String> loginTrainee(@RequestParam String username,
                                               @RequestParam String password){

        LoginResults authenticated = traineeService.logIn(username, password);


        if(authenticated.equals(LoginResults.USER_NOT_FOUND)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Trainee with username of "+username+" not found");
        }else if(authenticated.equals(LoginResults.BAD_PASSWORD)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }

        return ResponseEntity.ok("Login successful");

    }



}
