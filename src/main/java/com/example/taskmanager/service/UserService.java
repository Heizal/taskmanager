package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.model.User;

import java.util.List;

public interface UserService {
    User registerUser(UserRegistrationDto registrationDto);
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    User updateUser(String username, UserRegistrationDto updatedDto);
    void deleteUser(String username);
    List<User> getAllUsers();
}
