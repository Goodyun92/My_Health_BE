package com.example.my_health_be.dto.user;

import com.example.my_health_be.domain.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserProfileRequest {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birth;

    private Gender gender;

    private Double height;

    private Double weight;
}
