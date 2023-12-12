package com.example.my_health_be.dto.intake;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskIntakeReturnDto {
    private Long id;

    private String content;

    private Double calorie;
}
