package com.example.taskmanager.repository;

import com.example.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find user by username
    Optional<User> findByUsername(String username);

    // Check if a user exists by username (for registration validation)
    boolean existsByUsername(String username);

    // Check if a user exists by email (for registration validation)
    boolean existsByEmail(String email);


}
