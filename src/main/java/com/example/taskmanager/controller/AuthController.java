package com.example.taskmanager.controller;

import com.example.taskmanager.dto.LoginDto;
import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.RoleRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.JwtProvider;
import com.example.taskmanager.service.OAuthTokenService;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtProvider jwtProvider;
    private final OAuthTokenService oAuthTokenService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, RoleRepository roleRepository,
                          JwtProvider jwtProvider, OAuthTokenService oAuthTokenService,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtProvider = jwtProvider;
        this.oAuthTokenService = oAuthTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/oauth2/callback")
    public ResponseEntity<?> handleOAuthCallback(@RequestParam String code) {
        // Use the authorization code to get tokens
        Map<String, String> tokenResponse = oAuthTokenService.exchangeCodeForTokens(code);

        // Return access token, refresh token, and their validity periods
        return ResponseEntity.ok(tokenResponse);
    }

    // endpoint to refresh tokens
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        //Validate the refresh token using the public key
        boolean isValid = jwtProvider.validateToken(refreshToken);

        if (isValid){
            //If valid generate a new access token
            String username = jwtProvider.extractUsername(refreshToken);

            //Find by username
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                String newAccessToken = jwtProvider.generateToken(user);

                Map<String, String> tokenResponse = new HashMap<>();
                tokenResponse.put("accessToken", newAccessToken);
                tokenResponse.put("accessTokenValidity", String.valueOf(3600));

                return ResponseEntity.ok(tokenResponse);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // Validate and get the role name
        String roleName = registrationDto.getRoleName();
        if (roleName == null || roleName.trim().isEmpty()) {
            return new ResponseEntity<>("Role is required for registration!", HttpStatus.BAD_REQUEST);
        }

        // Convert role name to uppercase to match expected values (e.g., "USER", "ADMIN")
        Role selectedRole = roleRepository.findByName(roleName.toUpperCase());
        if (selectedRole == null) {
            return new ResponseEntity<>("Role not found!", HttpStatus.BAD_REQUEST);
        }

        // Encrypt password and create the user
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        // Set the selected role (initialize role set if null)
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(selectedRole);

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
                // Generate JWT tokens
                String accessToken = jwtProvider.generateToken(user);
                String refreshToken = jwtProvider.generateRefreshToken(user);

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
