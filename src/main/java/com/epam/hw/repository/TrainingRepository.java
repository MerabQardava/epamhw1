package com.epam.hw.repository;

import com.epam.hw.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Integer> {

        @Query("""
        SELECT tr
        FROM Training tr
        JOIN tr.trainee t
        JOIN tr.trainer trn
        JOIN trn.user u
        WHERE u.username = :trainerUsername
        AND t.user.username = :traineeUsername
        AND tr.date BETWEEN :from AND :to
        AND tr.trainingType.trainingTypeName = :trainingTypeName
        """)
        List<Training> findTrainingsByTraineeCriteria(
                @Param("traineeUsername") String traineeUsername,
                @Param("from") LocalDate from,
                @Param("to") LocalDate to,
                @Param("trainerUsername") String trainerUsername,
                @Param("trainingTypeName") String trainingTypeName
        );

        @Query("""
        SELECT tr
        FROM Training tr
        JOIN tr.trainee t
        JOIN tr.trainer trn
        JOIN trn.user u
        WHERE u.username = :trainerUsername
        AND t.user.username = :traineeUsername
        AND tr.date BETWEEN :from AND :to
        """)
        List<Training> findTrainingsByTrainerCriteria(
                @Param("trainerUsername") String trainerUsername,
                @Param("from") LocalDate from,
                @Param("to") LocalDate to,
                @Param("traineeUsername") String traineeUsername
        );
}

