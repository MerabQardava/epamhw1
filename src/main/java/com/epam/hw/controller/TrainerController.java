package com.epam.hw.controller;

import com.epam.hw.dto.*;
import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.Trainer;
import com.epam.hw.service.TrainerService;
import com.epam.hw.storage.LoginResults;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trainer")
public class TrainerController {
    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService){
        this.trainerService=trainerService;
    }

    @PostMapping()
    public ResponseEntity<CreateUserReturnDTO> registerTrainer(@RequestBody @Valid TrainerRegistrationDTO dto){
        Trainer saved = trainerService.createTrainer(dto.firstName(),dto.lastName(),dto.specialization());

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateUserReturnDTO(
                saved.getUser().getUsername(),
                saved.getUser().getPassword()));
    }


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

    @PutMapping("/login/{username}")
    public ResponseEntity<String> changeLogin(@PathVariable String username,
                                              @RequestBody @Valid UserPasswordChangeDTO dto){
        try {
            Trainer trainer = trainerService.getTrainerByUsername(username);
            if(trainer == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer with username of "+username+" not found");
            }else if(!trainer.getUser().getPassword().equals(dto.oldPassword())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
            }else{
                trainerService.changePassword(username, dto.newPassword());
                return ResponseEntity.ok("Password changed successfully");
            }
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer with username of "+username+" not found");
        }

    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileDTO> getTrainerProfile(@PathVariable String username) {
        Trainer trainer = trainerService.getTrainerByUsername(username);
        if (trainer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

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



    @PutMapping("/{username}")
    public ResponseEntity<UpdateTrainerReturnDTO> updateTrainerProfile(@PathVariable String username,
                                                                       @RequestBody @Valid UpdateTrainerDTO dto){
        try{
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
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    @GetMapping("/{username}/unassigned")
    public ResponseEntity<Set<TrainersListDTO>> getUnassignedTrainers(@PathVariable String username){
        try{
            Set<TrainersListDTO> trainers = trainerService.getUnassignedTraineeTrainers(username).stream().map(
                    trainer -> new TrainersListDTO(trainer.getUser().getUsername(),
                            trainer.getUser().getFirstName(),
                            trainer.getUser().getLastName(),
                            trainer.getSpecializationId())
            ).collect(Collectors.toSet());

            return ResponseEntity.status(HttpStatus.OK).body(trainers);


        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    @PatchMapping("/{username}/toggle")
    public ResponseEntity<String> toggleActivity(@PathVariable String username){
        try{
            trainerService.toggleTrainerStatus(username);
            return ResponseEntity.status(HttpStatus.OK).body("Trainer status toggled successfully");
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer Not Found");
        }
    }

}
