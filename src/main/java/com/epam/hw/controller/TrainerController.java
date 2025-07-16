package com.epam.hw.controller;

import com.epam.hw.dto.*;
import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.Trainer;
import com.epam.hw.entity.User;
import com.epam.hw.monitoring.CustomMetricsService;
import com.epam.hw.service.TrainerService;
import com.epam.hw.service.UserService;
import com.epam.hw.storage.LoginResults;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TrainerController {
    private final TrainerService trainerService;
    private final CustomMetricsService metricsService;
    private final UserService userService;

    @Autowired
    public TrainerController(TrainerService trainerService, CustomMetricsService metricsService, UserService userService) {
        this.trainerService=trainerService;
        this.metricsService=metricsService;
        this.userService=userService;
    }

    @Operation(summary = "Register a new trainer and return generated credentials")
    @PostMapping()
    public ResponseEntity<CreateUserReturnDTO> registerTrainer(@RequestBody @Valid TrainerRegistrationDTO dto){
        metricsService.recordRequest("POST", "/trainer");
        Timer.Sample sample = Timer.start();
        log.info("POST /trainer - Registering new trainer: {} {}", dto.firstName(), dto.lastName());
        Trainer saved = userService.register(dto.firstName(),dto.lastName(),dto.specialization(), dto.password());
        log.info("Trainer registered with username: {}", saved.getUser().getUsername());
        sample.stop(metricsService.getRequestTimer());
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserReturnDTO(
                saved.getUser().getUsername(),
                saved.getUser().getPassword()));
    }

//    @Operation(summary = "Login a trainer using username and password")
//    @GetMapping("/login")
//    public ResponseEntity<String> loginTrainer(@RequestParam String username,
//                                               @RequestParam String password){
//        metricsService.recordRequest("GET", "/trainer/login");
//        Timer.Sample sample = Timer.start();
//        log.info("GET /trainer/login - Attempting login for: {}", username);
//        LoginResults authenticated = trainerService.logIn(username, password);
//
//
//        if(authenticated.equals(LoginResults.USER_NOT_FOUND)){
//            log.warn("Login failed: Trainer not found - {}", username);
//            sample.stop(metricsService.getRequestTimer());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Trainer with username of "+username+" not found");
//        }else if(authenticated.equals(LoginResults.BAD_PASSWORD)){
//            log.warn("Login failed: Invalid password - {}", username);
//            sample.stop(metricsService.getRequestTimer());
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
//        }
//        log.info("Login successful - {}", username);
//        sample.stop(metricsService.getRequestTimer());
//        return ResponseEntity.ok("Login successful");
//
//    }

    @Operation(summary = "Login a trainer using username and password")
    @GetMapping("/login")
    public ResponseEntity<String> loginTrainer(@RequestParam String username,
                                               @RequestParam String password){
        metricsService.recordRequest("GET", "/trainer/login");
        Timer.Sample sample = Timer.start();
        log.info("GET /trainer/login - Attempting login for: {}", username);
//        LoginResults authenticated = trainerService.logIn(username, password);

        String token = userService.verify(username, password);

        log.info("Login successful for {}", username);
        sample.stop(metricsService.getRequestTimer());
        return ResponseEntity.ok(token);

    }


    @Operation(summary = "Change trainer password after verifying old password")
    @PutMapping("/login/{username}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> changeLogin(@PathVariable String username,
                                              @RequestBody @Valid UserPasswordChangeDTO dto){
        metricsService.recordRequest("PUT", "/trainer/login/{username}");
        Timer.Sample sample = Timer.start();
        log.info("PUT /trainer/login/{} - Changing password", username);
        Trainer trainer = trainerService.getTrainerByUsername(username);

        if (!trainer.getUser().getPassword().equals(dto.oldPassword())) {
            log.warn("Password change failed for {}: Invalid old password", username);
            sample.stop(metricsService.getRequestTimer());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        }

        trainerService.changePassword(username, dto.newPassword());
        log.info("Password changed successfully for {}", username);
        sample.stop(metricsService.getRequestTimer());
        return ResponseEntity.ok("Password changed successfully");

    }


    @Operation(summary = "Get full profile of a trainer by username")
    @GetMapping("/{username}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TrainerProfileDTO> getTrainerProfile(@PathVariable String username) {
        metricsService.recordRequest("GET", "/trainer/{username}");
        Timer.Sample sample = Timer.start();
        log.info("GET /trainer/{} - Fetching profile", username);
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
        log.info("Fetched profile for trainer: {}", username);
        sample.stop(metricsService.getRequestTimer());
        return ResponseEntity.ok(profileDTO);
    }


    @Operation(summary = "Update profile details of a trainer")
    @PutMapping("/{username}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UpdateTrainerReturnDTO> updateTrainerProfile(@PathVariable String username,
                                                                       @RequestBody @Valid UpdateTrainerDTO dto){
        metricsService.recordRequest("PUT", "/trainer/{username}");
        Timer.Sample sample = Timer.start();
        log.info("PUT /trainer/{} - Updating profile", username);

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

        log.info("Updated profile for trainer: {}", username);
        sample.stop(metricsService.getRequestTimer());
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
    @GetMapping("/unassigned-trainers/{traineeUsername}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Set<TrainersListDTO>> getUnassignedTrainers(@PathVariable String traineeUsername){
        metricsService.recordRequest("GET", "/trainer/unassigned-trainers/{traineeUsername}");
        Timer.Sample sample = Timer.start();
        log.info("GET /trainer/unassigned-trainers/{} - Fetching unassigned trainers", traineeUsername);

        Set<TrainersListDTO> trainers = trainerService.getUnassignedTraineeTrainers(traineeUsername).stream().map(
                trainer -> new TrainersListDTO(trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecializationId())
        ).collect(Collectors.toSet());

        log.info("Fetched {} unassigned trainers for trainee: {}", trainers.size(), traineeUsername);
        sample.stop(metricsService.getRequestTimer());
        return ResponseEntity.status(HttpStatus.OK).body(trainers);
    }

    @Operation(summary = "Toggle active/inactive status of a trainer")
    @PatchMapping("/{username}/status")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> toggleActivity(@PathVariable String username){
        metricsService.recordRequest("PATCH", "/trainer/{username}/status");
        Timer.Sample sample = Timer.start();
        log.info("PATCH /trainer/{}/status - Toggling activity", username);
        trainerService.toggleTrainerStatus(username);
        log.info("Trainer status toggled: {}", username);
        sample.stop(metricsService.getRequestTimer());
        return ResponseEntity.status(HttpStatus.OK).body("Trainer status toggled successfully");
    }

    @Operation(summary = "Get list of trainings conducted by the trainer, optionally filtered by date and trainee")
    @GetMapping("/{username}/trainings")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<TrainerTrainingDTO>> getTrainerTrainings(@PathVariable String username,
                                                                 @ModelAttribute GetTrainerTrainingOptionsDTO options
    ){
        metricsService.recordRequest("GET", "/trainer/{username}/trainings");
        Timer.Sample sample = Timer.start();
        log.info("GET /trainer/{}/trainings - Fetching trainings with filters: {}", username, options);
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
        log.info("Fetched {} trainings for trainer: {}", trainings.size(), username);
        sample.stop(metricsService.getRequestTimer());
        return ResponseEntity.ok(trainings);

    }

}
