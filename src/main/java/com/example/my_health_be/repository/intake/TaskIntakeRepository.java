package com.example.my_health_be.repository.intake;

import com.example.my_health_be.domain.intake.DailyIntake;
import com.example.my_health_be.domain.intake.TaskIntake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskIntakeRepository extends JpaRepository<TaskIntake,Long> {
    Optional<List<TaskIntake>> findByDailyIntake(DailyIntake dailyIntake);
}
