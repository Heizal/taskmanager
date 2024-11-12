package com.example.taskmanager.security;

import com.example.taskmanager.repository.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PublicKey publicKey;
    private final OidcUserService oidcUserService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService customUserDetailsService, @Lazy PublicKey publicKey, @Lazy OidcUserService oidcUserService) {
        this.customUserDetailsService = customUserDetailsService;
        this.publicKey = publicKey;
        this.oidcUserService = oidcUserService;
    }

    // Define a bean for the public key
    @Bean
    public PublicKey publicKey() {
        try {
            // Load the public key from the PEM file
            String key = new String(Files.readAllBytes(Paths.get("src/main/resources/public_key.pem")))
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            // Decode and generate the public key
            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);

        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException("Failed to load public key", e);
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CORS and CSRF Configuration
        http.cors();
        http.csrf(csrf -> csrf.disable()); // Disable CSRF for stateless REST APIs

        // Authorize Requests
        http.authorizeRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow preflight requests
                .requestMatchers("/api/auth/login", "/api/auth/refresh-token").permitAll() // Publicly accessible endpoints
                .requestMatchers("/api/auth/register", "/oauth2/authorization/google", "/api/auth/oauth2/callback").permitAll() // OAuth-required endpoints
                .requestMatchers("/api/users/**").hasRole("ADMIN") // ADMIN-only endpoints
                .anyRequest().authenticated()); // All other endpoints require JWT authentication

        // OAuth2 Login Configuration
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/oauth2/authorization/google")
                .defaultSuccessUrl("/api/tasks", true)
                .failureUrl("/login?error=true")
                .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                        .oidcUserService(oidcUserService)));

        // Custom UserDetails Service
        http.userDetailsService(customUserDetailsService);

        // Session Management Configuration
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1));

        // Add JWT Authorization Filter
        http.addFilterBefore(new JwtAuthorizationFilter(publicKey), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

