package com.example.my_health_be.repository;

import com.example.my_health_be.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByNickName(String nickName);
}
