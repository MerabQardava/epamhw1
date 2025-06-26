package com.epam.hw.controller;

import com.epam.hw.dto.*;
import com.epam.hw.entity.Trainee;
import com.epam.hw.service.TraineeService;
import com.epam.hw.storage.LoginResults;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Trainee Controller", description = "Endpoints for managing trainees")
@RestController
@RequestMapping("/trainee")
public class TraineeController {
    private final TraineeService traineeService;

    @Autowired
    public TraineeController(TraineeService traineeService){
        this.traineeService = traineeService;
    }


    @Operation(summary = "Register a new trainee and return generated credentials")
    @PostMapping()
    public ResponseEntity<CreateUserReturnDTO> registerTrainee(@RequestBody @Valid TraineeRegistrationDTO dto){
        Trainee saved = traineeService.createTrainee(dto.firstName(), dto.lastName(),LocalDate.parse(dto.dob()),dto.address());

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserReturnDTO(
                saved.getUser().getUsername(),
                saved.getUser().getPassword()));
    }


    @Operation(summary = "Login a trainee with username and password")
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

    @Operation(summary = "Change trainee password after verifying current credentials")
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

    @Operation(summary = "Fetch full profile of the trainee by username")
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


    @Operation(summary = "Delete a trainee by username")
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteTrainee(@PathVariable String username) {
        Trainee trainee = traineeService.getTraineeByUsername(username);
        if (trainee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee with username of " + username + " not found");
        }

        traineeService.deleteByUsername(username);
        return ResponseEntity.ok("Trainee with username of " + username + " deleted successfully");
    }

    @Operation(summary = "Update the profile of an existing trainee")
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


    @Operation(summary = "Toggle active/inactive status of the trainee")
    @PatchMapping("/{username}/toggle")
    public ResponseEntity<String> toggleActivity(@PathVariable String username){
        try{
            traineeService.toggleTraineeStatus(username);
            return ResponseEntity.status(HttpStatus.OK).body("Trainee status toggled successfully");
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee Not Found");
        }
    }


    @Operation(summary = "Update the list of trainers assigned to a trainee")
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainersListDTO>> updateTrainerList(
            @PathVariable String username,
            @RequestBody Set<String> trainerUsernames
    ){

        try {
            List<TrainersListDTO> updatedTrainers = traineeService.updateTraineeTrainers(username, trainerUsernames)
                    .stream()
                    .map(trainer -> new TrainersListDTO(
                            trainer.getUser().getUsername(),
                            trainer.getUser().getFirstName(),
                            trainer.getUser().getLastName(),
                            trainer.getSpecializationId()
                    )).collect(Collectors.toList());

            return ResponseEntity.ok(updatedTrainers);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Get filtered training sessions of a trainee by date range, trainer or type")
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainingDTO>> getTraineeTrainings(@PathVariable String username,
                                                                 @ModelAttribute GetTrainingOptionsDTO options
    ){
        try {
            Trainee trainee = traineeService.getTraineeByUsername(username);
            if (trainee == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<TrainingDTO> trainings = traineeService.getTraineeTrainings(
                            username,
                            options.startDate(),
                            options.endDate(),
                            options.trainerUsername(),
                            options.trainingTypeName()
                    ).stream()
                    .map(training -> new TrainingDTO(
                            training.getTrainingName(),
                            training.getDate(),
                            training.getTrainingType(),
                            training.getDuration(),
                            training.getTrainer().getUser().getUsername()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(trainings);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }




}
