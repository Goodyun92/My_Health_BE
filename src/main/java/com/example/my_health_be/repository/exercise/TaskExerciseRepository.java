package com.example.my_health_be.repository.exercise;

import com.example.my_health_be.domain.exercise.DailyExercise;
import com.example.my_health_be.domain.exercise.TaskExercise;
import com.example.my_health_be.dto.exercise.TaskExerciseReturnDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskExerciseRepository extends JpaRepository<TaskExercise,Long> {
    Optional<List<TaskExercise>> findByDailyExercise(DailyExercise dailyExercise);
}
