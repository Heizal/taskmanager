package com.example.taskmanager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

   /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf(csrf -> csrf.disable()) // Disable CSRF (you may want to enable it in production)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow preflight requests
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/tasks/**").permitAll()
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/oauth2/authorization/google").permitAll() // Allow OAuth2 login
                        .anyRequest().authenticated()) // All other endpoints require authentication
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google") // Login page endpoint
                        .defaultSuccessUrl("/api/tasks", true) // Redirect after successful login
                        .failureUrl("/login?error=true") // Redirect on login failure
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .oidcUserService(oidcUserService()))) // Configure user info retrieval
                .userDetailsService(customUserDetailsService); // Custom UserDetailsService
        return http.build(); // Return the security filter chain
    }*/
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
               .cors()
               .and()
               .csrf(csrf -> csrf.disable()) // Disable CSRF if this is a pure REST API
               .authorizeHttpRequests(auth -> auth
                       .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                       .requestMatchers("/api/auth/register", "/api/auth/login", "/oauth2/authorization/google").permitAll() // Publicly accessible
                       .requestMatchers( "/api/email/send").authenticated() // Requires authentication
                       .requestMatchers("/api/tasks/**").authenticated()
                       .requestMatchers("/api/users/**").hasRole("ADMIN") // Requires ADMIN role
                       .anyRequest().authenticated()) // All other endpoints require authentication
               .oauth2Login(oauth2 -> oauth2
                       .loginPage("/oauth2/authorization/google")
                       .defaultSuccessUrl("/api/tasks", true)
                       .failureUrl("/login?error=true")
                       .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                               .oidcUserService(oidcUserService())))
               .userDetailsService(customUserDetailsService)
               .sessionManagement(session -> session
                       .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                       .maximumSessions(1));
       return http.build();
   }

    @Bean
    public OidcUserService oidcUserService() {
        return new OidcUserService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository(OAuth2AuthorizedClientService authorizedClientService) {
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService);
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                                 OAuth2AuthorizedClientRepository authorizedClientRepository) {
        return new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
    }
}

