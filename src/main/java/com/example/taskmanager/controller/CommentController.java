package com.example.taskmanager.controller;
import com.example.taskmanager.model.Comment;
import com.example.taskmanager.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class CommentController {

    @Autowired
    private CommentService commentService;

    //Post a new comment
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long taskId, @RequestParam String username, @RequestParam String content, @RequestParam(required = false) Long parentCommentId) {
        Comment newComment = commentService.createComment(taskId, username, content, parentCommentId);
        //Instead of writing ResponseEntity<Comment>.ok(newComment)
        //It infers the type from the method signature
        return ResponseEntity.ok(newComment);
    }

    //Get all comments for a task
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<Comment>> getCommentsById(@PathVariable Long taskId) {
        List<Comment> allComments = commentService.getAllComments();
        List<Comment> comments = commentService.getCommentsByTaskId(taskId, allComments);
        return ResponseEntity.ok(comments);
    }

}
