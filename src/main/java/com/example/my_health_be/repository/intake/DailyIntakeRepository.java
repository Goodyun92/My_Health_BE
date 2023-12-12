package com.example.my_health_be.repository.intake;

import com.example.my_health_be.domain.exercise.DailyExercise;
import com.example.my_health_be.domain.intake.DailyIntake;
import com.example.my_health_be.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyIntakeRepository extends JpaRepository<DailyIntake, Long> {
    Optional<DailyIntake> findByUserAndDate(User user, LocalDate date);

    List<DailyIntake> findByUserAndDateGreaterThanEqual(User user, LocalDate date);
}
