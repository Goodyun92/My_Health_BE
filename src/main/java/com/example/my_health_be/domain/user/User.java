package com.example.my_health_be.domain.user;

import com.example.my_health_be.domain.enums.Role;
import lombok.*;

import javax.persistence.*;

import java.util.List;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;    //아이디

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickName;    //닉네임

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

}
