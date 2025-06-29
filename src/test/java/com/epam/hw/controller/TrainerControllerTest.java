package com.epam.hw.controller;

import com.epam.hw.dto.*;
import com.epam.hw.entity.*;
import com.epam.hw.service.TrainerService;
import com.epam.hw.storage.LoginResults;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainerController.class)
public class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerService trainerService;

    @Test
    void testRegisterTrainer_success() throws Exception {
        TrainerRegistrationDTO dto = new TrainerRegistrationDTO("John", "Smith", "Java");

        User user = new User();
        user.setUsername("john.smith");
        user.setPassword("pass123");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        Mockito.when(trainerService.createTrainer(anyString(), anyString(), anyString()))
                .thenReturn(trainer);

        mockMvc.perform(post("/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.smith"))
                .andExpect(jsonPath("$.password").value("pass123"));
    }

    @Test
    void testLogin_success() throws Exception {
        Mockito.when(trainerService.logIn("john.smith", "pass123")).thenReturn(LoginResults.SUCCESS);

        mockMvc.perform(get("/trainer/login")
                        .param("username", "john.smith")
                        .param("password", "pass123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    void testLogin_badPassword() throws Exception {
        Mockito.when(trainerService.logIn("john.smith", "wrong")).thenReturn(LoginResults.BAD_PASSWORD);

        mockMvc.perform(get("/trainer/login")
                        .param("username", "john.smith")
                        .param("password", "wrong"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid Credentials"));
    }

    @Test
    void testLogin_userNotFound() throws Exception {
        Mockito.when(trainerService.logIn("ghost", "pass")).thenReturn(LoginResults.USER_NOT_FOUND);

        mockMvc.perform(get("/trainer/login")
                        .param("username", "ghost")
                        .param("password", "pass"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Trainer with username of ghost not found"));
    }

    @Test
    void testChangePassword_success() throws Exception {
        User user = new User();
        user.setPassword("oldPass");
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        Mockito.when(trainerService.getTrainerByUsername("john.smith")).thenReturn(trainer);

        UserPasswordChangeDTO dto = new UserPasswordChangeDTO("oldPass", "newPass");

        mockMvc.perform(put("/trainer/login/john.smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));
    }

    @Test
    void testChangePassword_invalidOldPassword() throws Exception {
        User user = new User();
        user.setPassword("correct");
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        Mockito.when(trainerService.getTrainerByUsername("john.smith")).thenReturn(trainer);

        UserPasswordChangeDTO dto = new UserPasswordChangeDTO("wrong", "newPass");

        mockMvc.perform(put("/trainer/login/john.smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid Credentials"));
    }

    @Test
    void testGetTrainerProfile_success() throws Exception {
        User trainerUser = new User();
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Smith");
        trainerUser.setActive(true);

        User traineeUser = new User();
        traineeUser.setUsername("trainee1");
        traineeUser.setFirstName("T1");
        traineeUser.setLastName("Last");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        TrainingType specialization = new TrainingType();
        specialization.setTrainingTypeName("Java");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecializationId(specialization);
        trainer.setTrainees(Set.of(trainee));

        Mockito.when(trainerService.getTrainerByUsername("john.smith")).thenReturn(trainer);

        mockMvc.perform(get("/trainer/john.smith"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.specialization.trainingTypeName").value("Java"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainees[0].username").value("trainee1"));
    }

    @Test
    void testUpdateTrainerProfile_success() throws Exception {
        UpdateTrainerDTO dto = new UpdateTrainerDTO("John", "Smith", "Java", true);
        User user = new User();
        user.setUsername("john.smith");
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setActive(true);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Java");

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecializationId(trainingType);

        Mockito.when(trainerService.updateTrainerProfile(eq("john.smith"), any(UpdateTrainerDTO.class)))
                .thenReturn(trainer);

        mockMvc.perform(put("/trainer/john.smith")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.smith"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testToggleTrainerStatus_success() throws Exception {
        mockMvc.perform(patch("/trainer/john.smith/toggle"))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainer status toggled successfully"));

        Mockito.verify(trainerService, times(1)).toggleTrainerStatus("john.smith");
    }

    @Test
    void testGetTrainerTrainings_success() throws Exception {
        User traineeUser = new User();
        traineeUser.setUsername("trainee1");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        User trainerUser = new User();
        trainerUser.setUsername("john.smith");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Java");

        Training training = new Training(trainee, trainer, "Inheritance", type, LocalDate.of(2024, 6, 10), 90);

        Mockito.when(trainerService.getTrainerByUsername("john.smith")).thenReturn(trainer);
        Mockito.when(trainerService.getTrainerTrainings(eq("john.smith"), any(), any(), any()))
                .thenReturn(List.of(training));

        mockMvc.perform(get("/trainer/john.smith/trainings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Inheritance"))
                .andExpect(jsonPath("$[0].date").value("2024-06-10"))
                .andExpect(jsonPath("$[0].trainingType.trainingTypeName").value("Java"))
                .andExpect(jsonPath("$[0].duration").value(90))
                .andExpect(jsonPath("$[0].traineeName").value("trainee1"));
    }
}