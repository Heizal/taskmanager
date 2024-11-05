# Conceptual Documentation for Backend-Only Task Management System

## Overview
The **Backend-Only Task Management System** is a platform for managing and tracking tasks within development teams. Inspired by JIRA, it provides backend support without a frontend interface. The backend API integrates with various frontends or tools like Postman to manage tasks, ensuring transparency, trackability, and effective communication.

The system includes modular components for user management, task tracking, commenting, and email notifications, all secured by OAuth2 and JWT.

## Purpose
The system acts as a **central hub for task management** for development teams. It allows for task creation, assignment, updates, commenting, and tracking, reducing administrative overhead.

The intended audience includes:
- **Product Owners**: To view, assign, and track developer tasks and understand the progress of different projects.
- **Developers**: To create, update, and manage their assigned tasks, and to use the commenting feature to keep all stakeholders informed of task progress.

Users interact with the backend using API tools like Postman or by integrating it with a UI framework such as React or Angular.

## Core Features

### 1. Task Management
The core of the system is the ability to perform CRUD operations (Create, Read, Update, Delete) on tasks. Each task has the following attributes:
- **Title**: A brief identifier for the task.
- **Description**: Detailed information about the task.
- **Status**: Current stage (e.g., To Do, In Progress, Done).
- **Priority**: Urgency level (e.g., Low, Medium, High).
- **Assigned User**: The developer responsible for the task.
- **Shared Users**: Users collaborating on the task.

Tasks can be **assigned** to specific users or **shared** with multiple users to enable collaboration.
- **Assigning Tasks**: Product owners assign tasks to developers, ensuring each task has a clear owner.
- **Sharing Tasks**: Multiple users can access a task for better collaboration.

Tasks are stored in a **PostgreSQL** database, and the system is built with Spring Boot, ensuring scalability and efficiency.

### 2. Commenting System
Tasks have a commenting feature that allows efficient communication:
- **Threaded Comments**: Users can reply to comments, creating discussion threads.
- **User Visibility**: Only authenticated users can add comments, ensuring secure interactions.

The commenting system is integrated with tasks, enabling developers to provide updates or discuss blockers.

### 3. User Authentication and Authorization
The system features a secure authentication mechanism using **OAuth2** for login and **JWT tokens** for session management.
- **OAuth2 Integration**: Provides a secure way to authenticate users.
- **Role-Based Access Control (RBAC)**: Roles such as **Admin** or **User** control access to various operations.

### 4. Email Notification System
The **EmailController** and **EmailService** provide notifications for key activities:
- When a task is assigned to a user, they receive an email notification.
- When a comment is added, relevant users receive a notification.

### 5. Search and Filter Tasks
The backend offers powerful filtering using **TaskSpecification**:
- **Dynamic Queries**: Filters tasks based on multiple criteria (e.g., status, due date).
- **Search Functionality**: Supports searching by title or description.

### 6. Error Handling
The backend includes a **GlobalExceptionHandler** for consistent error messages:
- **ResourceNotFoundException**: Used when a task or user cannot be found.
- Ensures predictable and informative error handling.

## System Architecture
The system follows a **Service-Oriented Architecture (SOA)** with clear separation across different layers:

### 1. Controller Layer
- Handles HTTP requests and maps them to appropriate services.
- **Controllers** include:
  - **AuthController**: Handles user registration and authentication.
  - **TaskController**: Manages task operations.
  - **CommentController**: Manages comments.
  - **EmailController**: Sends notifications.

### 2. Service Layer
- Contains business logic.
- Services ensure business rules are applied before interacting with repositories.
- **JwtProvider** handles JWT token generation and validation.

### 3. Repository Layer
- Directly interacts with the **PostgreSQL Database** using **JPA**.
- Provides CRUD operations through **TaskRepository**, **CommentRepository**, and **UserRepository**.
- Supports advanced queries through **JpaSpecificationExecutor**.

### 4. Security Layer
- **SecurityConfig** manages access control.
- Role-based access rules restrict endpoint usage.
- **CustomUserDetailsService** provides user information for security configuration.

### 5. Database Layer
- **PostgreSQL Database** stores entities such as users, tasks, and comments.
- Relationships ensure efficient CRUD operations and consistency.

### Diagram
![System Overview](/SystemOverview.png)
- **User Interface**: Initiates HTTP requests using an API client like Postman.
- **Controllers**: Handle user requests and map them to services.
- **Services**: Execute business logic and interact with repositories.
- **Repositories**: Handle CRUD operations with the **Postgres Database**.
- **Security Layer**: Secures resources using OAuth2 and JWT.

## Use Cases

### 1. Product Owner Assigns a Task to a Developer
- A **Product Owner** creates and assigns a task to a developer via the API.
- **TaskService** adds the task to the database.
- **EmailService** sends an email notification to the assigned developer.

### 2. Developer Updates Task Status
- A **Developer** updates the task status (e.g., *In Progress* to *Done*).
- **TaskService** processes the update request.
- **GlobalExceptionHandler** handles errors like a non-existent task ID.

### 3. Commenting on a Task
- A developer adds a comment to a task.
- **CommentService** processes the comment and associates it with the task.
- Stakeholders receive email notifications about the new comment.

## Conclusion
The **Backend-Only Task Management System** provides an efficient infrastructure for managing tasks and communication within development teams. Its modular architecture supports scalability, security, and easy integration. Features like **OAuth2, PostgreSQL, and dynamic filtering** make it a robust backend solution for software development teams.

