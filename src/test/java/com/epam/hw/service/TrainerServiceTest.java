package com.epam.hw.service;

import com.epam.hw.entity.Trainee;
import com.epam.hw.entity.Trainer;
import com.epam.hw.entity.TrainingType;
import com.epam.hw.entity.User;
import com.epam.hw.repository.*;
import com.epam.hw.storage.Auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {
    @Mock
    TraineeRepository traineeRepo;
    @Mock
    UserRepository userRepo;
    @Mock
    TrainerRepository trainerRepo;
    @Mock
    TrainingRepository trainingRepo;
    @Mock
    Auth auth;
    @Mock
    TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    TrainerService trainerService;

    User loggedUser;
    Trainer loggedTrainer;

    @BeforeEach
    void setUp() {
        loggedUser = new User("Logged", "User");
        loggedTrainer = new Trainer();
        loggedUser.setTrainer(loggedTrainer);


        Mockito.lenient().when(auth.getLoggedInUser()).thenReturn(loggedUser);
    }


    @Test
    void createTrainerTest() {
        String first = "John", last = "Doe";

        when(userRepo.findByUsername("John.Doe"))
                .thenReturn(Optional.of(new User()));
        when(userRepo.findByUsername("John.Doe1"))
                .thenReturn(Optional.empty());

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);

        when(trainingTypeRepository.findByTrainingTypeName("Java"))
                .thenReturn(Optional.of(new TrainingType("Java")));
        trainerService.createTrainer(first, last, "Java");

        verify(trainerRepo).save(captor.capture());
        Trainer saved = captor.getValue();

        assertEquals("John.Doe1", saved.getUser().getUsername());
        assertEquals(first,       saved.getUser().getFirstName());
        assertEquals(last,        saved.getUser().getLastName());
        assertEquals("Java",saved.getSpecializationId().getTrainingTypeName());
    }

    @Test
    void getTraineeByUsernameTest() {
        User dbUser = new User("A","B");
        Trainer dbTrainer = new Trainer();
        dbUser.setTrainer(dbTrainer);

        when(userRepo.findByUsername("A.B"))
                .thenReturn(Optional.of(dbUser));

        Trainer result = trainerService.getTrainerByUsername("A.B");

        assertSame(dbTrainer, result);
    }

//    @Test
//    void testLoginSuccess() {
//        String username = "john.doe";
//        String password = "password123";
//
//        when(auth.logIn(username, password)).thenReturn(true);
//
//        boolean result = trainerService.logIn(username, password);
//
//        assertTrue(result);
//        verify(auth).logIn(username, password);
//    }

    @Test
    void testLogout() {
        when(auth.logOut()).thenReturn(true);

        boolean result = trainerService.LogOut();

        assertTrue(result);
        verify(auth).logOut();
    }

    @Test
    void changePasswordTest() {

        User dbUser = new User("A", "B");
        dbUser.setPassword("oldPassword");
        dbUser.setTrainer(new Trainer());

        when(auth.getLoggedInUser()).thenReturn(dbUser);

        String newPassword = "newPassword123";

        boolean result = trainerService.changePassword("A.B",newPassword);

        assertTrue(result);
        assertEquals(newPassword, dbUser.getPassword());

        verify(userRepo).save(dbUser);
    }


    @Test
    void toggleTrainerStatusTest(){

        boolean result = trainerService.toggleTrainerStatus();

        assertFalse(result);
        assertFalse(loggedUser.isActive());

        verify(userRepo).save(loggedUser);
    }

    @Test
    void updateTrainerProfile_updatesAllFieldsAndPersists() {
        TrainingType newType = new TrainingType("Java");
        User incomingUser = new User("NewFirst", "NewLast");
        incomingUser.setUsername("desired");   // desired username
        incomingUser.setPassword("newpw");
        incomingUser.setActive(false);

        Trainer incomingTrainer = new Trainer();
        incomingTrainer.setUser(incomingUser);
        incomingTrainer.setSpecializationId(newType);

        when(userRepo.findByUsername("desired"))
                .thenReturn(Optional.of(new User()));
        when(userRepo.findByUsername("desired1"))
                .thenReturn(Optional.empty());

        boolean result = trainerService.updateTraineeProfile(incomingTrainer);
        assertTrue(result);

        ArgumentCaptor<User> userCap = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userCap.capture());
        User savedUser = userCap.getValue();

        assertEquals("desired1", savedUser.getUsername());
        assertEquals("NewFirst", savedUser.getFirstName());
        assertEquals("NewLast",  savedUser.getLastName());
        assertEquals("newpw",    savedUser.getPassword());
        assertFalse(savedUser.isActive());

        verify(trainerRepo).save(loggedTrainer);
        assertEquals(newType, loggedTrainer.getSpecializationId());
    }


}
