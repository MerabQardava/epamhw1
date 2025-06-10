package com.epam.hw.repository;

import com.epam.hw.entity.Trainee;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

@Registered
public interface TraineeRepository extends JpaRepository<Trainee, Integer> {

}
