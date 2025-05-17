package com.example.taskmanager.service;


import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.Comment;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.CommentRepository;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Comment createComment(Long taskId, String username, String content, Long parentCommendId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User user = userService.findByUsername(username);
        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        //Threading implementation
        if (parentCommendId != null) {
            Comment parentComment = commentRepository.findById(parentCommendId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        return commentRepository.save(comment);
    }

    //Fetch comments for a task
    public List<Comment> getCommentsByTaskId(Long taskId, List<Comment> allComments) {
        return allComments.stream()
                .filter(comment -> comment.getTask().getId().equals(taskId))
                .toList();
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
