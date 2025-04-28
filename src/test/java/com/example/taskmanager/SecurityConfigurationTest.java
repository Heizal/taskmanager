package com.example.taskmanager;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    // Test: Public access to /api/auth/** should be permitted
    @Test
    public void whenUnauthenticated_thenAccessToAuthEndpointsShouldBePermitted() throws Exception {
        // Register endpoint test: Should allow unauthenticated access and successfully register a new user.
        mockMvc.perform(post("/api/auth/register")
                        .with(csrf()) // Add CSRF token for POST request
                        .contentType("application/json")
                        .content("{ \"username\": \"user9997\", \"email\": \"user9997@example.com\", \"password\": \"Password1\" , \"roleName\": \"USER\"}"))
                .andExpect(status().isOk()); // Expecting 200 OK for successful registration

        // Login endpoint test: Should allow unauthenticated access and respond accordingly.
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf()) // Add CSRF token for POST request
                        .contentType("application/json")
                        .content("{ \"email\": \"user9997@example.com\", \"password\": \"Password1\" }"))
                .andExpect(status().isOk()); // Expecting 200 OK if login is successful
    }

    //Test for invalid login credentials
    @Test
    public void whenUnauthenticated_thenInvalidCredentialsShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{ \"email\": \"wronguser@example.com\", \"password\": \"wrongpassword\" }"))
                .andExpect(status().isUnauthorized()) // Expecting 401 Unauthorized for invalid credentials
                .andExpect(content().string("Invalid credentials"));
    }


    // Test: Authenticated user with USER role can access GET /api/tasks/** endpoint
    @Test
    @WithMockUser(roles = "USER")
    public void whenAuthenticatedAsUser_thenCanAccessGetTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
    }

    // Test: Unauthenticated users should be forbidden from accessing /api/tasks/** endpoint
    @Test
    public void whenUnauthenticated_thenCannotAccessTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized()); // Expecting 401
    }

    // Test: Authenticated user with ADMIN role can POST to /api/tasks/** (create task)
    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenAuthenticatedAsAdmin_thenCanCreateTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .with(csrf())  // Add CSRF token for POST request
                        .contentType("application/json")
                        .content("{ \"title\": \"New Task\", \"description\": \"Task description\" }"))
                .andExpect(status().isOk());
    }

    // Test: Unauthenticated users cannot create tasks
    @Test
    public void whenUnauthenticated_thenCannotCreateTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .with(csrf())  // Add CSRF token for POST request
                        .contentType("application/json")
                        .content("{ \"title\": \"New Task\", \"description\": \"Task description\" }"))
                .andExpect(status().isUnauthorized()); //Expects 302 redirection

    }

    // Test: Users without the ADMIN role cannot DELETE to /api/users
    @Test
    @WithMockUser(roles = "USER")
    public void whenAuthenticatedAsUser_thenCannotDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/testuser")
                        .with(csrf()))  // Add CSRF token for DELETE request
                .andExpect(status().isForbidden()); // Expecting 403 Forbidden
    }
}

