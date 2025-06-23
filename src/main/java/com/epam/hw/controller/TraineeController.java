package com.epam.hw.controller;

import com.epam.hw.dto.*;
import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.User;
import com.epam.hw.service.TraineeService;
import com.epam.hw.storage.LoginResults;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trainee")
public class TraineeController {
    private final TraineeService traineeService;

    @Autowired
    public TraineeController(TraineeService traineeService){
        this.traineeService = traineeService;
    }

    @PostMapping()
    public ResponseEntity<CreateUserReturnDTO> registerTrainee(@RequestBody @Valid TraineeRegistrationDTO dto){
        Trainee saved = traineeService.createTrainee(dto.firstName(), dto.lastName(),LocalDate.parse(dto.dob()),dto.address());

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserReturnDTO(
                saved.getUser().getUsername(),
                saved.getUser().getPassword()));
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

    @PutMapping("/login/{username}")
    public ResponseEntity<String> changeLogin(@PathVariable String username,
                                              @RequestBody @Valid UserPasswordChangeDTO dto){
        try {
            Trainee trainee = traineeService.getTraineeByUsername(username);
            if(trainee == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee with username of "+username+" not found");
            }else if(!trainee.getUser().getPassword().equals(dto.oldPassword())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
            }else{
                traineeService.changePassword(username, dto.newPassword());
                return ResponseEntity.ok("Password changed successfully");
            }
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee with username of "+username+" not found");
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileDTO> getTraineeProfile(@PathVariable String username) {
        Trainee trainee = traineeService.getTraineeByUsername(username);
        if (trainee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Set<TrainersListDTO> trainersSet = trainee.getTrainers().stream()
                .map(trainer -> new TrainersListDTO(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecializationId()
                )).collect(Collectors.toSet());

        TraineeProfileDTO profileDTO = new TraineeProfileDTO(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth().toString(),
                trainee.getAddress(),
                trainee.getUser().isActive(),
                trainersSet
        );
        return ResponseEntity.ok(profileDTO);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteTrainee(@PathVariable String username) {
        Trainee trainee = traineeService.getTraineeByUsername(username);
        if (trainee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee with username of " + username + " not found");
        }

        traineeService.deleteByUsername(username);
        return ResponseEntity.ok("Trainee with username of " + username + " deleted successfully");
    }

    @PutMapping("/{username}")
    public ResponseEntity<UpdateTraineeReturnDTO> updateTraineeProfile(@PathVariable String username,
                                                                 @RequestBody @Valid UpdateTraineeDTO dto){
        try{
            Trainee updatedTrainee = traineeService.updateTraineeProfile(username,new UpdateTraineeDTO(
                    dto.firstName(),
                    dto.lastName(),
                    dto.dob(),
                    dto.address(),
                    dto.isActive()
            ));

            Set<TrainersListDTO> trainersSet = updatedTrainee.getTrainers().stream()
                    .map(trainer -> new TrainersListDTO(
                            trainer.getUser().getUsername(),
                            trainer.getUser().getFirstName(),
                            trainer.getUser().getLastName(),
                            trainer.getSpecializationId()
                    )).collect(Collectors.toSet());

            return ResponseEntity.ok(new UpdateTraineeReturnDTO(
                    updatedTrainee.getUser().getUsername(),
                    updatedTrainee.getUser().getFirstName(),
                    updatedTrainee.getUser().getLastName(),
                    updatedTrainee.getDateOfBirth().toString(),
                    updatedTrainee.getAddress(),
                    updatedTrainee.getUser().isActive(),
                    trainersSet
            ));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    @PatchMapping("/{username}/toggle")
    public ResponseEntity<String> toggleActivity(@PathVariable String username){
        try{
            traineeService.toggleTraineeStatus(username);
            return ResponseEntity.status(HttpStatus.OK).body("Trainee status toggled successfully");
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee Not Found");
        }
    }




}
