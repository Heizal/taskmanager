package com.example.taskmanager.service;

import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.repository.specification.TaskSpecification;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    /**
     * Create a new task and save it to the database.
     * Validates the input to ensure no required fields are missing.
     *
     * @param task Data Transfer Object containing task details.
     * @return The newly created task entity.
     */
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    /**
     * Retrieve a task from the repository.
     *
     * @return List of all tasks by id in the database.
     * @throws ResourceNotFoundException if task is not found by id.
     */
    public Task getTask(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
    }

    /**
     * Retrieve all tasks from the repository.
     *
     * @return List of all tasks in the database.
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Update an existing task in the system.
     * Checks if the task exists, and applies the update if found.
     *
     * @param id The ID of the task to be updated.
     * @param taskDetails Data Transfer Object with updated task details.
     * @return The updated task entity.
     */
    public Task updateTask(Long id, Task taskDetails) {
        Task task = getTask(id);
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());
        task.setStatus(taskDetails.getStatus());
        return taskRepository.save(task);
    }

    /**
     * Delete a task from the system.
     *
     * @param id The ID of the task to be deleted.
     */
    public void deleteTask(Long id) {
        Task task = getTask(id);
        taskRepository.delete(task);
    }

    public Task sharedTaskWithUser(Long taskId, String username) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new ResourceNotFoundException("Task not found with id"));

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found"));

        //Add user to set of sharedUsers
        task.getSharedUsers().add(user);
        //Save task
        return taskRepository.save(task);
    }

    public Task assignTaskToUser(Long taskId, String username, OidcUser oidcUser, Authentication authentication, HttpServletRequest request) throws Exception {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found with username: " + username));

        task.setAssignedTo(user);

        Task savedTask = taskRepository.save(task);
        System.out.println("Task assigned to user and saved: " + savedTask);

        // Fetch the OAuth2 access token for the logged-in user
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                "google", oidcUser.getName());
        if (authorizedClient == null) {
            throw new IllegalStateException("No authorized client found for the user");
        }
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        // Send email notification using Gmail API
        emailService.sendEmailWithGmailApi(
                oidcUser.getEmail(), // 'from' email
                user.getEmail(),      // 'to' email
                "Task Assigned",
                "You have been assigned a task: " + task.getTitle(),
                accessToken           // OAuth2 access token
        );
        return savedTask;
    }


    public List<Task> searchTasks(String query) {
        return taskRepository.findByTitleContainingOrDescriptionContaining(query, query);
    }

    public List<Task> filterTasks(String status, LocalDate dueDate, String assignedTo) {
        TaskSpecification specification = new TaskSpecification(status, dueDate, assignedTo);
        return taskRepository.findAll(specification);
    }

}
