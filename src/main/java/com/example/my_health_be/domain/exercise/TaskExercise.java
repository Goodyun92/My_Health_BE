package com.example.my_health_be.domain.exercise;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table( name = "task_exercise")
public class TaskExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "daily_exercise_id", referencedColumnName = "id")
    private DailyExercise dailyExercise;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Double calorie;
}
