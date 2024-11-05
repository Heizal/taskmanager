package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    /**
     * Find tasks shared with a specific user by the user's ID.
     *
     * @param userId The ID of the user with whom the tasks are shared.
     * @return List of tasks that are shared with the specified user.
     */
    List<Task> findBySharedUsers_Id(Long userId);

    /**
     * Find tasks where the title or description contains the specified keywords.
     * This allows for basic text search functionality.
     *
     * @param title The keyword to search in the title.
     * @param description The keyword to search in the description.
     * @return List of tasks matching the search criteria.
     */
    List<Task> findByTitleContainingOrDescriptionContaining(String title, String description);

    /**
     * Find all tasks that match the provided specification.
     * Specifications allow for dynamic and complex filtering of tasks.
     *
     * @param spec The specification defining filter criteria.
     * @return List of tasks matching the specification.
     */
    List<Task> findAll(Specification<Task> spec);

}

