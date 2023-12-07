package com.example.my_health_be.repository;

import com.example.my_health_be.domain.user.Inbody;
import com.example.my_health_be.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InbodyRepository extends JpaRepository<Inbody,Long> {
    Optional<Inbody> findByUser(User user);

}
