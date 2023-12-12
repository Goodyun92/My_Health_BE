package com.example.my_health_be.repository.user;

import com.example.my_health_be.domain.user.User;
import com.example.my_health_be.domain.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile,Long> {
    Optional<UserProfile> findByUser(User user);
}
