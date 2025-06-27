package com.epam.hw.controller;

import com.epam.hw.dto.*;
import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.Trainer;
import com.epam.hw.service.TrainerService;
import com.epam.hw.storage.LoginResults;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Trainer Controller", description = "Endpoints for managing trainers")
@RestController
@RequestMapping("/trainer")
public class TrainerController {
    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService){
        this.trainerService=trainerService;
    }

    @Operation(summary = "Register a new trainer and return generated credentials")
    @PostMapping()
    public ResponseEntity<CreateUserReturnDTO> registerTrainer(@RequestBody @Valid TrainerRegistrationDTO dto){
        Trainer saved = trainerService.createTrainer(dto.firstName(),dto.lastName(),dto.specialization());

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserReturnDTO(
                saved.getUser().getUsername(),
                saved.getUser().getPassword()));
    }

    @Operation(summary = "Login a trainer using username and password")
    @GetMapping("/login")
    public ResponseEntity<String> loginTrainer(@RequestParam String username,
                                               @RequestParam String password){

        LoginResults authenticated = trainerService.logIn(username, password);


        if(authenticated.equals(LoginResults.USER_NOT_FOUND)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Trainer with username of "+username+" not found");
        }else if(authenticated.equals(LoginResults.BAD_PASSWORD)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }

        return ResponseEntity.ok("Login successful");

    }


    @Operation(summary = "Change trainer password after verifying old password")
    @PutMapping("/login/{username}")
    public ResponseEntity<String> changeLogin(@PathVariable String username,
                                              @RequestBody @Valid UserPasswordChangeDTO dto){

        Trainer trainer = trainerService.getTrainerByUsername(username);
        if (!trainer.getUser().getPassword().equals(dto.oldPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }

        trainerService.changePassword(username, dto.newPassword());
        return ResponseEntity.ok("Password changed successfully");

    }


    @Operation(summary = "Get full profile of a trainer by username")
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileDTO> getTrainerProfile(@PathVariable String username) {
        Trainer trainer = trainerService.getTrainerByUsername(username);

        Set<TraineesListDTO> traineesSet = trainer.getTrainees().stream()
                .map(trainee -> new TraineesListDTO(
                        trainee.getUser().getUsername(),
                        trainee.getUser().getFirstName(),
                        trainee.getUser().getLastName()
                )).collect(Collectors.toSet());

        TrainerProfileDTO profileDTO = new TrainerProfileDTO(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecializationId(),
                trainer.getUser().isActive(),
                traineesSet
        );
        return ResponseEntity.ok(profileDTO);
    }


    @Operation(summary = "Update profile details of a trainer")
    @PutMapping("/{username}")
    public ResponseEntity<UpdateTrainerReturnDTO> updateTrainerProfile(@PathVariable String username,
                                                                       @RequestBody @Valid UpdateTrainerDTO dto){

            Trainer updatedTrainer = trainerService.updateTrainerProfile(username,new UpdateTrainerDTO(
                    dto.firstName(),
                    dto.lastName(),
                    dto.specialization(),
                    dto.isActive()
            ));

            Set<TraineesListDTO> traineesSet = updatedTrainer.getTrainees().stream()
                    .map(trainer -> new TraineesListDTO(
                            trainer.getUser().getUsername(),
                            trainer.getUser().getFirstName(),
                            trainer.getUser().getLastName()
                    )).collect(Collectors.toSet());

            return ResponseEntity.ok(new UpdateTrainerReturnDTO(
                    updatedTrainer.getUser().getUsername(),
                    updatedTrainer.getUser().getFirstName(),
                    updatedTrainer.getUser().getLastName(),
                    updatedTrainer.getSpecializationId(),
                    updatedTrainer.getUser().isActive(),
                    traineesSet
            ));


    }

    @Operation(summary = "Get list of trainers not yet assigned to the given trainee")
    @GetMapping("/{username}/unassigned")
    public ResponseEntity<Set<TrainersListDTO>> getUnassignedTrainers(@PathVariable String username){

            Set<TrainersListDTO> trainers = trainerService.getUnassignedTraineeTrainers(username).stream().map(
                    trainer -> new TrainersListDTO(trainer.getUser().getUsername(),
                            trainer.getUser().getFirstName(),
                            trainer.getUser().getLastName(),
                            trainer.getSpecializationId())
            ).collect(Collectors.toSet());

            return ResponseEntity.status(HttpStatus.OK).body(trainers);

    }

    @Operation(summary = "Toggle active/inactive status of a trainer")
    @PatchMapping("/{username}/toggle")
    public ResponseEntity<String> toggleActivity(@PathVariable String username){
            trainerService.toggleTrainerStatus(username);
            return ResponseEntity.status(HttpStatus.OK).body("Trainer status toggled successfully");
    }

    @Operation(summary = "Get list of trainings conducted by the trainer, optionally filtered by date and trainee")
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingDTO>> getTrainerTrainings(@PathVariable String username,
                                                                 @ModelAttribute GetTrainerTrainingOptionsDTO options
    ){
            Trainer trainer = trainerService.getTrainerByUsername(username);

            List<TrainerTrainingDTO> trainings = trainerService.getTrainerTrainings(
                            username,
                            options.startDate(),
                            options.endDate(),
                            options.traineeUsername()
                    ).stream()
                    .map(training -> new TrainerTrainingDTO(
                            training.getTrainingName(),
                            training.getDate(),
                            training.getTrainingType(),
                            training.getDuration(),
                            training.getTrainee().getUser().getUsername()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(trainings);

    }

}
