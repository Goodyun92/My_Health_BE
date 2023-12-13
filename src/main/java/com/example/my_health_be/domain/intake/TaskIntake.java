package com.example.my_health_be.domain.intake;

import lombok.*;

import javax.persistence.*;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table( name = "task_intake")
public class TaskIntake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "daily_intake_id", referencedColumnName = "id")
    private DailyIntake dailyIntake;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Double calorie;
}
