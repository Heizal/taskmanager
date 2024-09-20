package com.example.taskmanager.service;

import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.model.User;

public interface UserService {
    User registerUser(UserRegistrationDto registrationDto);
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}
