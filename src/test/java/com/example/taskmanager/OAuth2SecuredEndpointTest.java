package com.example.taskmanager;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OAuth2SecuredEndpointTest {
    @Autowired
    private MockMvc mockMvc;

    // Test accessing a secured endpoint with valid OAuth2 authentication
    @Test
    @WithMockUser(roles = "USER", username = "user1")
    public void whenAuthenticatedWithOAuth_thenAccessSecuredEndpoint() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk());
    }

    // Test accessing a secured endpoint without any authentication
    @Test
    public void whenNotAuthenticated_thenAccessSecuredEndpointShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized()); // Expecting 401 if no authentication
    }
}
