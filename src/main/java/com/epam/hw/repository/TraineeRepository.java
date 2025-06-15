package com.epam.hw.repository;

import com.epam.hw.entity.Trainee;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Registered
public interface TraineeRepository extends JpaRepository<Trainee, Integer> {

    Optional<Trainee> findByUser_Username(String username);

}
