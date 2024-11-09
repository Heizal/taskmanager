package com.example.taskmanager.controller;

import com.example.taskmanager.dto.LoginDto;
import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.RoleRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.JwtProvider;
import com.example.taskmanager.service.OAuthTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private OAuthTokenService oAuthTokenService;

    @GetMapping("/oauth2/callback")
    public ResponseEntity<?> handleOAuthCallback(@RequestParam String code) {
        // Use the authorization code to get tokens
        Map<String, String> tokenResponse = oAuthTokenService.exchangeCodeForTokens(code);

        // Return access token, refresh token, and their validity periods
        return ResponseEntity.ok(tokenResponse);
    }

    // New endpoint to refresh tokens
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        // Use the refresh token to get a new access token
        Map<String, String> tokenResponse = oAuthTokenService.refreshAccessToken(refreshToken);

        // Return new access token along with validity period
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            return new ResponseEntity<>("Default role not found!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Encrypt password and create the user
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // Set default roles (initialize role set if null)
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(userRole);

        userRepository.save(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(loginDto.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Verify password
            if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                // Assuming we have a way to generate a JWT token, for example using a JwtProvider service
                String accessToken = jwtProvider.generateToken(user.getUsername());
                String refreshToken = jwtProvider.generateRefreshToken(user.getUsername());

                // Return a JSON object instead of plain text
                Map<String, String> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("accessToken", accessToken);
                response.put("refreshToken", refreshToken);
                response.put("accessTokenValidity", String.valueOf(3600));
                response.put("refreshTokenValidity", String.valueOf(86400));

                return ResponseEntity.ok(response);  // Return JSON
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
