package com.example.taskmanager.service;

import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.repository.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
    public Task getTask(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
    }
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    public Task updateTask(Long id, Task taskDetails) {
        Task task = getTask(id);
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());
        task.setStatus(taskDetails.getStatus());
        return taskRepository.save(task);
    }

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

    public Task assignTaskToUser(Long taskId, String username) {
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found with username: " + username));

        task.setAssignedTo(user);
        return taskRepository.save(task);
    }

    public List<Task> searchTasks(String query) {
        return taskRepository.findByTitleContainingOrDescriptionContaining(query, query);
    }

    public List<Task> filterTasks(String status, LocalDate dueDate, String assignedTo) {
        return taskRepository.findAll(new TaskSpecification(status, dueDate, assignedTo));
    }

}
