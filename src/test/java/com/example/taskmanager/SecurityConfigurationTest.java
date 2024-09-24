package com.example.taskmanager;

import com.example.taskmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // Test: Public access to /api/auth/** should be permitted
    @Test
    public void whenUnauthenticated_thenAccessToAuthEndpointsShouldBePermitted() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                        // Add CSRF token for POST request
                        .contentType("application/json")
                        .content("{ \"username\": \"user1\", \"email\": \"user1@example.com\", \"password\": \"password\" }"))
                .andExpect(status().isOk());
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

    // Test: Users without the ADMIN role cannot POST to /api/tasks
    @Test
    @WithMockUser(roles = "USER")
    public void whenAuthenticatedAsUser_thenCannotCreateTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .with(csrf())  // Add CSRF token for POST request
                        .contentType("application/json")
                        .content("{ \"title\": \"New Task\", \"description\": \"Task description\" }"))
                .andExpect(status().isForbidden()); // User should be forbidden (403)
    }

    // Test: Unauthenticated users should be forbidden from POST to /api/tasks
    @Test
    public void whenUnauthenticated_thenCannotCreateTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .with(csrf())  // Add CSRF token for POST request
                        .contentType("application/json")
                        .content("{ \"title\": \"New Task\", \"description\": \"Task description\" }"))
                .andExpect(status().isUnauthorized()); // Expecting 401
    }
}

