package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    // Service dependency to handle business logic for tasks
    @Autowired
    private TaskService taskService;

    /**
     * Create a new task.
     * Method Type: POST
     * Access: Requires authenticated user
     *
     * @return The created task
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }


    /**
     * Retrieve task by id
     * Method Type: GET
     * Access: Requires authenticated user
     *
     * @return List of tasks by their id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    /**
     * Retrieve all tasks.
     * Method Type: GET
     * Access: Requires authenticated user
     *
     * @return List of all tasks in the system.
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    /**
     * Update an existing task.
     * Method Type: PUT
     * Access: Requires authenticated user
     *
     * @param id The ID of the task to be updated.
     * @param taskDetails Updated task data.
     * @return The updated task.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDetails));
    }

    /**
     * Delete a task.
     * Method Type: DELETE
     * Access: Requires authenticated user
     *
     * @param id The ID of the task to delete.
     * @return Response entity with ok status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Share a task.
     * Method Type: POST
     * Access: Requires authenticated user
     *
     * @param taskId The ID of the task to be shared.
     * @param username The user to receive the shared task.
     * @return The shared task.
     */
    @PostMapping("/{taskId}/share")
    public ResponseEntity<Task> shareTask(@PathVariable Long taskId, @RequestParam String username) {
        Task sharedTask = taskService.sharedTaskWithUser(taskId, username);
        return ResponseEntity.ok(sharedTask);
    }


    /**
     * Assign a task.
     * Method Type: POST
     * Access: Requires authenticated user
     *
     * @param taskId The ID of the task to be assigned.
     * @param username The user to receive the assigned task.
     * @param oidcUser The user assigning the task should be authenticated
     * @return The assigned task.
     */
    @PostMapping("/{taskId}/assign")
    public ResponseEntity<?> assignTask(@PathVariable Long taskId, @RequestParam String username,
                                        @AuthenticationPrincipal OidcUser oidcUser, Authentication authentication,
                                        HttpServletRequest request) throws Exception {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication is missing");
        }

        if (oidcUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
        Task assignedTask = taskService.assignTaskToUser(taskId, username, oidcUser, authentication, request);
        return ResponseEntity.ok(assignedTask);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String query) {
        List<Task> tasks = taskService.searchTasks(query);
        return ResponseEntity.ok(tasks);

    }

    @GetMapping("/filter")
    public ResponseEntity<List<Task>> filterTasks(
            @RequestParam (required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) String assignedTo
    ) {
        List<Task> filteredTasks = taskService.filterTasks(status, dueDate, assignedTo);
        return ResponseEntity.ok(filteredTasks);
    }
}
