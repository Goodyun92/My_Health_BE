package com.example.my_health_be.util;

import com.example.my_health_be.domain.enums.Gender;

public class CalorieUtil {

    public static Double calcIntakeTargetCalorie(Gender gender, Double weight, Double height, Integer age){
        double bmr;
        double TDEE;

        if (gender == Gender.MALE) {
            // 남성의 BMR 계산
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            // 여성의 BMR 계산
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }

        TDEE = bmr * 1.375;

        //목표 일일 칼로리 적자 : weight/10 * 100
        return TDEE - weight/10 * 100;
    }

    public static Double calcExerciseTargetCalorie(Double weight){
        //목표 일일 칼로리 적자 : weight/10 * 100

        return weight/10 * 100;
    }
}
