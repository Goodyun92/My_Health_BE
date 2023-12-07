package com.example.my_health_be.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class UserInbodyRequest {
    private Double bmi;
    private Double skeletalMuscle;
    private Double fatPer;
}
