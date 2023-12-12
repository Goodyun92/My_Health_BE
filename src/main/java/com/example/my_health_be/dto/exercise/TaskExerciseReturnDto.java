package com.example.my_health_be.dto.exercise;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskExerciseReturnDto {
    private Long id;

    private String content;

    private Double calorie;
}
