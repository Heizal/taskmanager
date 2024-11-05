package com.example.taskmanager.repository.specification;

import com.example.taskmanager.model.Task;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification class for building dynamic queries for Task entities.
 * Allows filtering tasks based on various attributes such as status, due date, and assigned user.
 */
public class TaskSpecification implements Specification<Task> {
    private String status;
    private LocalDate dueDate;
    private String assignedTo;


    /**
     * Constructor for initializing filter criteria.
     *
     * @param status The status of the task (e.g., "TO_DO", "IN_PROGRESS", "DONE").
     * @param dueDate The due date to filter tasks that are due on a specific date.
     * @param assignedTo The username of the person the task is assigned to.
     */
    public TaskSpecification(String status, LocalDate dueDate, String assignedTo) {
        this.status = status;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
    }

    /**
     * Constructs a predicate for filtering tasks based on the provided criteria.
     * This method is called by the query executor to dynamically create SQL queries.
     *
     * @param root The root type in the query from which the entity attributes can be accessed.
     * @param query The criteria query.
     * @param criteriaBuilder The builder used to construct predicates.
     * @return A compound predicate composed of multiple criteria.
     */
    @Override
    public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        // List to store individual conditions
        List<Predicate> predicates = new ArrayList<>();

        // Filter by task status if provided
        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }

        // Filter by due date if provided
        if (dueDate != null) {
            predicates.add(criteriaBuilder.equal(root.get("dueDate"), dueDate));
        }

        // Filter by assigned user if provided
        if (assignedTo != null) {
            predicates.add(criteriaBuilder.equal(root.get("assignedTo").get("username"), assignedTo));
        }

        // Combine all predicates using 'AND' logic
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}

