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
    @Autowired
    private TaskService taskService;


    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{taskId}/share")
    public ResponseEntity<Task> shareTask(@PathVariable Long taskId, @RequestParam String username) {
        Task sharedTask = taskService.sharedTaskWithUser(taskId, username);
        return ResponseEntity.ok(sharedTask);
    }

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
