package com.example.taskmanager;

import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.RoleRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Test registerUser
    @Test
    void registerUser_ShouldReturnSavedUser(){
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setUsername("testuser");
        userRegistrationDto.setEmail("testuser@example.com");
        userRegistrationDto.setPassword("password");

        Role role = new Role();
        role.setName("ROLE_USER");

        User user = new User();
        user.setUsername("testuser");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.registerUser(userRegistrationDto);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
    }

    //Test find UserByUsername
    @Test
    void findUserByUsername_ShouldReturnUser_WhenUserExists(){
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User foundUser = userService.findByUsername("testuser");

        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());

    }

    //Test if a user exists by email
    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists(){
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(true);
        boolean emailExists = userService.existsByEmail("testuser@example.com");
        assertTrue(emailExists);
    }

    //Test if user exists by username
    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean usernameExists = userService.existsByUsername("testuser");

        assertTrue(usernameExists);
    }
}
