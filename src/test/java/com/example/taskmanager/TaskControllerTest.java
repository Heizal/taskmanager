package com.example.taskmanager;


import com.example.taskmanager.controller.TaskController;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    //Test: get all tasks
    @Test
    public void testGetAllTasks() throws Exception {
        Task task1 = new Task(1L, "Task 1", "Description 1", null, "OPEN", null, null, new HashSet<>(),new HashSet<>() );
        Task task2 = new Task(2L, "Task 2", "Description 2", null, "OPEN", null, null, new HashSet<>(), new HashSet<>());


        Mockito.when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));

    }

    //Test: create task
    @Test
    public void testCreateTask() throws Exception {
        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setDescription("Task Description");

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle("New Task");
        savedTask.setDescription("Task Description");

        Mockito.when(taskService.createTask(Mockito.any(Task.class))).thenReturn(savedTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"title\": \"New Task\", \"description\": \"Task Description\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Task Description"));


    }

    //Test: Task sharing with existent user
    @Test
    @WithMockUser(username = "user1", roles = "USER")
    public void testShareTask() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");

        Mockito.when(taskService.sharedTaskWithUser(1L, "user2")).thenReturn(task);

        mockMvc.perform(post("/api/tasks/1/share")
                .param("username", "user2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    //Test: Task assignment
    /*@Test
    public void testAssignTaskToUser_Success() throws Exception {
        Long taskId = 1L;
        String username = "user1";
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Sample Task");
        task.setDescription("Sample Task Description");

        //Create mock OidcUser object
        OidcUser mockOidcUser = Mockito.mock(OidcUser.class);
        Mockito.when(mockOidcUser.getName()).thenReturn("mockUser");

        // Create a mock Authentication object
        Authentication mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(mockAuth.getName()).thenReturn(mockOidcUser.getName());

        // Create a mock HttpServletRequest object
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);

        //Return assigned task
        Mockito.when(taskService.assignTaskToUser(taskId, username, mockOidcUser, mockAuth,mockRequest))
                .thenReturn(task);
        mockMvc.perform(post("/api/tasks/" + taskId + "/assign")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Sample Task"));

    }


    //Test: Task assigned to nonexistent user
    @Test
    public void testAssignTaskToNonExistentUser() throws Exception {
        Long taskId = 1L;
        String nonExistentUsername = "nonexistentuser";
        //Create mock OidcUser object
        OidcUser mockOidcUser = Mockito.mock(OidcUser.class);
        Mockito.when(mockOidcUser.getName()).thenReturn("mockUser");

        // Create a mock Authentication object
        Authentication mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(mockAuth.getName()).thenReturn(mockOidcUser.getName());

        // Create a mock HttpServletRequest object
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);

        // Mocking TaskService to throw exception when user doesn't exist
        Mockito.when(taskService.assignTaskToUser(taskId, nonExistentUsername, mockOidcUser, mockAuth,mockRequest))
                .thenThrow(new ResourceNotFoundException("User not found with username: " + nonExistentUsername));

        mockMvc.perform(post("/api/tasks/" + taskId + "/assign")
                        .param("username", nonExistentUsername)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    //Test: Verify that error message works
    @Test
    public void testAssignNonExistentTask() throws Exception {
        Long nonExistentTaskId = 99L;
        String username = "user1";
        //Create mock OidcUser object
        OidcUser mockOidcUser = Mockito.mock(OidcUser.class);
        Mockito.when(mockOidcUser.getName()).thenReturn("mockUser");

        // Create a mock Authentication object
        Authentication mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(mockAuth.getName()).thenReturn(mockOidcUser.getName());

        // Create a mock HttpServletRequest object
        HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);

        // Mocking TaskService to throw exception when task doesn't exist
        Mockito.when(taskService.assignTaskToUser(nonExistentTaskId, username, mockOidcUser, mockAuth,mockRequest))
                .thenThrow(new ResourceNotFoundException("Task not found with id: " + nonExistentTaskId));

        mockMvc.perform(post("/api/tasks/" + nonExistentTaskId + "/assign")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }*/
}
