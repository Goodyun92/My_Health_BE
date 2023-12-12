package com.example.my_health_be.repository.exercise;

import com.example.my_health_be.domain.exercise.DailyExercise;
import com.example.my_health_be.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyExerciseRepository extends JpaRepository<DailyExercise,Long> {
    Optional <DailyExercise> findByUserAndDate(User user, LocalDate date);

    List<DailyExercise> findByUserAndDateGreaterThanEqual(User user, LocalDate date);
}
