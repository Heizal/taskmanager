package com.example.taskmanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.security.PublicKey;

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

        // OAuth2 Resource Server Configuration
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        // Custom UserDetails Service
        http.userDetailsService(customUserDetailsService);

        // Session Management Configuration
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}

