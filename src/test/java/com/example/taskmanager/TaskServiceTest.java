/*
UNIT TESTS FOR TASK SERVICE CRUD OPERATIONS
createTask_ShouldReturnSavedTask: Tests if a saved task is retrieved after creation.
getTask_ShouldReturnTask: Tests if a task is correctly retrieved by its ID.
getTask_ShouldThrowException_WhenTaskNotFound: Tests if a ResourceNotFoundException is thrown when a task is not found.
getAllTasks_ShouldReturnAllTasks: Tests if all tasks are correctly retrieved.
updateTask_ShouldReturnUpdatedTask: Tests if a task is correctly updated with new details.
deleteTask_ShouldDeleteTask: Tests if a task is correctly deleted.
*/

package com.example.taskmanager;

import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_ShouldReturnSavedTask() {
        Task task = new Task();
        task.setTitle("Test Task");

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task savedTask = taskService.createTask(task);
        assertNotNull(savedTask);
        assertEquals("Test Task", savedTask.getTitle());
        verify(taskRepository).save(task);

    }

    @Test
    void getTask_ShouldReturnTask() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTask(taskId);

        assertNotNull(foundTask);
        assertEquals(taskId, foundTask.getId());
        assertEquals("Test Task", foundTask.getTitle());
        verify(taskRepository).findById(taskId);
    }

    @Test
    void getTask_ShouldThrowException_WhenTaskNotFound() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTask(taskId));
        verify(taskRepository).findById(taskId);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        List<Task> tasks = Arrays.asList(
                new Task(1L, "Task 1", "Description 1", null, "TODO", null, null, new HashSet<>()),
                new Task(2L, "Task 2", "Description 2", null, "OPEN", null, null, new HashSet<>())
        );

        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> foundTasks = taskService.getAllTasks();

        assertNotNull(foundTasks);
        assertEquals(2, foundTasks.size());
        verify(taskRepository).findAll();
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() {
        Long taskId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Task existingTask = new Task(taskId, "Old Title", "Old Description", null, "TODO", null, null, new HashSet<>());
        Task updatedDetails = new Task(taskId, "New Title", "New Description", null, "IN_PROGRESS", null, null, new HashSet<>());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedDetails);

        Task updatedTask = taskService.updateTask(taskId, updatedDetails);

        assertNotNull(updatedTask);
        assertEquals("New Title", updatedTask.getTitle());
        assertEquals("New Description", updatedTask.getDescription());
        assertEquals("IN_PROGRESS", updatedTask.getStatus());
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        Long taskId = 1L;
        Task task = new Task(taskId, "Test Task", "Test Description", null, "TODO", null, null, new HashSet<>());

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(taskId);

        verify(taskRepository).findById(taskId);
        verify(taskRepository).delete(task);
    }
}
