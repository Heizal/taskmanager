package com.example.taskmanager.repository;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.specification.TaskSpecification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findBySharedUsers_Id(Long userId);
    List<Task> findByTitleContainingOrDescriptionContaining(String title, String description);
    List<Task> findAll(TaskSpecification taskSpecification);
}

