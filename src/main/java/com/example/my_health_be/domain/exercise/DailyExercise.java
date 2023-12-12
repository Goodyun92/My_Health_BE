package com.example.my_health_be.domain.exercise;

import com.example.my_health_be.domain.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(
        name = "daily_exercise",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "date"})
        }
)
public class DailyExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Column(nullable = false)
    private Double targetCalorie;

    @Column(nullable = false)
    private Double currentCalorie;
}
