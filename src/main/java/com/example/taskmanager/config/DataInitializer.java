package com.example.taskmanager.config;


import com.example.taskmanager.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initRoles(RoleService roleService) {
        return args -> {
            // Initialize default roles if they don't exist
            if (roleService.getRoleByName("ROLE_USER") == null) {
                roleService.createRole("ROLE_USER");
            }
            if (roleService.getRoleByName("ROLE_ADMIN") == null) {
                roleService.createRole("ROLE_ADMIN");
            }
        };
    }
}
