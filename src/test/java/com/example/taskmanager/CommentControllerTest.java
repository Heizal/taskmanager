package com.example.taskmanager;


import com.example.taskmanager.controller.CommentController;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.Comment;
import com.example.taskmanager.service.CommentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    //Test: Create comment
    @Test
    public void testCreateComment_Success() throws Exception {
        Long taskId = 1L;
        String username = "user1";
        String content = "This is a comment";

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent(content);

        //Return created comment
        Mockito.when(commentService.createComment(taskId, username, content)).thenReturn(comment);

        mockMvc.perform(post("/api/tasks/" + taskId + "/comments")
                        .param("username", username)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content));
    }

    //Test: Get comment by task id
    @Test
    public void testGetCommentsByTaskId_Success() throws Exception {
        Long taskId = 1L;

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("This is a comment");

        // Mocking CommentService to return a list of comments
        Mockito.when(commentService.getCommentsByTaskId(taskId)).thenReturn(Collections.singletonList(comment));

        mockMvc.perform(get("/api/tasks/" + taskId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("This is a comment"));
    }

    //Test: create comment for nonexistent task
    @Test
    public void testCreateComment_NonExistentTask() throws Exception {
        Long nonExistentTaskId = 99L;
        String username = "user1";
        String content = "This is a comment";

        // Mocking CommentService to throw exception when task doesn't exist
        Mockito.when(commentService.createComment(nonExistentTaskId, username, content))
                .thenThrow(new ResourceNotFoundException("Task not found with id: " + nonExistentTaskId));

        mockMvc.perform(post("/api/tasks/" + nonExistentTaskId + "/comments")
                        .param("username", username)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
