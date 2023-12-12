package com.example.my_health_be.dto.intake;

import com.example.my_health_be.dto.exercise.TaskExerciseReturnDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyIntakeReturnDto {
    private String userName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Double targetCalorie;

    private Double currentCalorie;

    private List<TaskIntakeReturnDto> intakes;
}
