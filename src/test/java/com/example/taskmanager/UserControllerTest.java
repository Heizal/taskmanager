package com.example.taskmanager;

import com.example.taskmanager.controller.UserController;
import com.example.taskmanager.dto.UserRegistrationDto;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    @WithMockUser(roles = "USER")
    public void testRegisterUser() throws Exception {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setUsername("testuser");
        userRegistrationDto.setEmail("test@example.com");
        userRegistrationDto.setPassword("password");

        // Mocking user service
        Mockito.when(userService.existsByEmail("test@example.com")).thenReturn(false);
        Mockito.when(userService.existsByUsername("testuser")).thenReturn(false);

        // Perform POST request to register user
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"testuser\", \"email\": \"test@example.com\", \"password\": \"password\" }"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testRegisterUser_EmailAlreadyExists() throws Exception {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setUsername("testuser");
        userRegistrationDto.setEmail("test@example.com");
        userRegistrationDto.setPassword("password");

        // Mocking user service to simulate an email already in use
        Mockito.when(userService.existsByEmail("test@example.com")).thenReturn(true);

        // Perform POST request to register user
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"username\": \"testuser\", \"email\": \"test@example.com\", \"password\": \"password\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already in use"));
    }

    //Retrieve user by username
    @Test
    public void testGetUserByUsername_Success() throws Exception {
        // Create a sample role
        Role role = new Role(1L, "ROLE_USER");

        // Create a sample user with roles
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = new User(1L, "testuser", "test@example.com", "password", roles);

        // Mocking user service to return the user when called
        Mockito.when(userService.findByUsername("testuser")).thenReturn(user);

        mockMvc.perform(get("/api/users/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"));
    }


    //Test user not found
    @Test
    public void testGetUserByUsername_NotFound() throws Exception {
        //Mock service to throw ResourceNotFoundException
        Mockito.when(userService.findByUsername("noexistent"))
                .thenThrow(new ResourceNotFoundException("\"User not found with username: nonexistent\""));

        //Perform a GET request for a non existent user
        mockMvc.perform(get("/api/users/nonexistent"))
                .andExpect(status().isNotFound()) // 404 error
                .andExpect(content().string("User not found with username: nonexistent"));


    }

}

