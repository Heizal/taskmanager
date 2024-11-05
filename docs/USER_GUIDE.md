# User Guide and Reference Documentation for TaskManager Backend

## Overview
This guide is designed to help product owners and developers get familiar with using TaskManager's RESTAPI.
## API Overview

### Authentication
- **POST /api/auth/login**: Log in users with OAuth credentials.
- **POST /api/auth/register**: Register a new user.

### Task Management
- **GET /api/tasks**: Retrieve all tasks.
- **POST /api/tasks**: Create a new task.
  - **Request Body**:
    ```json
    {
      "title": "New Task",
      "description": "Task description",
      "priority": "HIGH"
    }
    ```
  - **Response**: Returns the newly created task.
  - **Status Codes**:
    - **200 OK**: Task created successfully.
    - **400 Bad Request**: Missing or invalid fields.
- **GET /api/tasks/{id}**: Retrieve details of a specific task.
- **PUT /api/tasks/{id}**: Update a task.
  - **Request Body**:
    ```json
    {
      "title": "Updated Task Title",
      "status": "IN_PROGRESS",
      "description": "Updated description"
    }
    ```
  - **Response**: Returns the updated task.
  - **Status Codes**:
    - **200 OK**: Task updated successfully.
    - **404 Not Found**: Task not found.
- **DELETE /api/tasks/{id}**: Delete a task.
  - **Response**: Confirmation message upon successful deletion.
  - **Status Codes**:
    - **200 OK**: Task deleted successfully.
    - **404 Not Found**: Task not found.

### Task Sharing and Assignment
- **POST /api/tasks/{taskId}/share**: Share a task with another user.
  - **Request Body**:
    ```json
    {
      "username": "developer123"
    }
    ```
  - **Response**: The shared task.
  - **Status Codes**:
    - **200 OK**: Task shared successfully.
    - **404 Not Found**: Task or user not found.
- **POST /api/tasks/{taskId}/assign**: Assign a task to a user.
  - **Request Body**:
    ```json
    {
      "username": "developer123"
    }
    ```
  - **Response**: The assigned task.
  - **Status Codes**:
    - **200 OK**: Task assigned successfully.
    - **404 Not Found**: Task or user not found.

### Comments
- **POST /api/tasks/{taskId}/comments**: Add a comment to a task.
  - **Request Body**:
    ```json
    {
      "username": "developer123",
      "content": "Started working on this feature."
    }
    ```
  - **Response**: Returns the newly created comment.
  - **Status Codes**:
    - **200 OK**: Comment added successfully.
    - **404 Not Found**: Task not found.
- **GET /api/tasks/{taskId}/comments**: Retrieve all comments for a specific task.
  - **Response**: List of comments.
  - **Status Codes**:
    - **200 OK**: Comments retrieved successfully.
    - **404 Not Found**: Task not found.

### Error Handling
Here are common errors you may encounter while using TaskManager and their solutions:

- **401 Unauthorized**: Ensure you are logged in via the **OAuth** login endpoint and provide the correct access token for authorized endpoints.
- **403 Forbidden**: You do not have the required permissions to perform this action. Ensure your user role is correctly configured.
- **404 Not Found**: The resource (task, user, etc.) you are looking for doesn't exist.
- **500 Internal Server Error**: Check the logs for server-related issues. Most often caused by misconfigured database or missing environment variables.

### API Testing with Postman
- Import the provided **Postman Collection** (available in the `docs/` folder) to get started with testing each endpoint easily.
- Use **OAuth** authentication within Postman to test secured endpoints. Make sure you log in first to obtain an access token.