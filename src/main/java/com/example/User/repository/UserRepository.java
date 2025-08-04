package com.example.User.repository;

import com.example.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserMobileNumber(String userMobileNumber);
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserId(Long userId);
}