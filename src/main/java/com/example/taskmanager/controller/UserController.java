package com.example.taskmanager.controller;
import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // User registration endpoint
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        // Check if user already exists by email or username
        if (userService.existsByEmail(registrationDto.getEmail())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        if (userService.existsByUsername(registrationDto.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken");
        }

        // Register the user
        userService.registerUser(registrationDto);
        return ResponseEntity.ok("User registered successfully");
    }

    // Get user by username endpoint (optional, depending on your needs)
    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        return ResponseEntity.ok(user);
    }

    // Update User Information
    @PutMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable String username, @RequestBody UserRegistrationDto updatedDto) {
        if (!userService.existsByUsername(username)) {
            return ResponseEntity.notFound().build();
        }
        userService.updateUser(username, updatedDto); // You'd need to implement this in UserService
        return ResponseEntity.ok("User updated successfully");
    }

    // Delete User
    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        if (!userService.existsByUsername(username)) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(username); // Implement this in UserService
        return ResponseEntity.ok("User deleted successfully");
    }

    //Get all users
    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers(); // Implement this in UserService
        return ResponseEntity.ok(users);
    }


}

