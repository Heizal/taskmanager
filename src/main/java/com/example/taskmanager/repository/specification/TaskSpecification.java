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

public class TaskSpecification implements Specification<Task> {
    private String status;
    private LocalDate dueDate;
    private String assignedTo;

    public TaskSpecification(String status, LocalDate dueDate, String assignedTo) {
        this.status = status;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
    }

    @Override
    public Predicate toPredicate(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }
        if (dueDate != null) {
            predicates.add(criteriaBuilder.equal(root.get("dueDate"), dueDate));
        }
        if (assignedTo != null) {
            predicates.add(criteriaBuilder.equal(root.get("assignedUser").get("username"), assignedTo));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}

