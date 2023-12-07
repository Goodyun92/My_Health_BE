package com.example.my_health_be.domain.user;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "inbody")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Inbody {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Double bmi;

    @Column(nullable = true)
    private Double skeletalMuscle;

    @Column(nullable = true)
    private Double fatPer;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
