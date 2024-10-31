# Project Name: 📝 TaskManager Backend

## 🌟 Overview

**TaskManager** is a backend application designed to help product owners 👩‍💼👨‍💼 and developers 👩‍💻👨‍💻 keep track of ongoing tasks within a tech team. It is similar to **JIRA** but focuses more on providing 👀 visibility into the tasks developers are working on and enabling 💬 efficient communication between product owners and developers.

This backend serves as the central hub 🌀 for managing tasks, assigning responsibilities, sharing tasks across the team, and tracking updates. It is built using **Spring Boot** 🚀 and leverages **OAuth** 🔒 for authentication, ensuring a secure way to handle user access. 

#### Note that this is a backend-only project, intended for integration with any compatible frontend or API testing tool like **Postman** 📫.

### ✨ Key Features
- **🗂️ Task Management**: Create ➕, update ✏️, delete ❌, assign, and comment on tasks.
- **🔐 Role-Based Access Control**: Secure actions based on user roles.
- **🔗 Task Sharing and Comments**: Developers can share 🔄 tasks and communicate via comments to improve collaboration 🤝.
- **🔑 OAuth Authentication**: Secure user authentication with **Google OAuth2**.
- **📧 Email Notifications**: Send email alerts to users for task assignments.

## 🛠️ Technologies Used
- **☕ Java 17**
- **🖥️ Spring Boot 3**
- **🗃️ PostgreSQL** for data persistence
- **🔒 OAuth2.0** for user authentication
- **📧 JavaMailSender** for email notifications
- **🧪 JUnit** for testing

## 🚀 Getting Started

### ✅ Prerequisites
- **☕ Java 17**
- **🔧 Maven**
- **🗃️ PostgreSQL**
- **🔑 An OAuth 2.0 client ID** (configured in Google Cloud Console)

### 📥 Installation Instructions

1. **Clone the Repository**
   ```sh
   git clone https://github.com/username/taskmanager-backend.git
   cd taskmanager-backend
   ```

2. **🛠️ Configure Environment Variables**
  - Create a `.env` file to store your environment variables:
    - `POSTGRES_URL`
    - `POSTGRES_USER`
    - `POSTGRES_PASSWORD`
    - `GOOGLE_CLIENT_ID`
    - `GOOGLE_CLIENT_SECRET`
    - `MAIL_USERNAME`
    - `MAIL_PASSWORD`

3. **⚙️ Set Up the Database**
  - Ensure **PostgreSQL** is running on your machine 💻.
  - Create a database named `taskmanagement` 🗄️.
  - Update the database URL, username, and password in the `application.properties` file or use environment variables.

4. **🏃 Run the Application**
   ```sh
   mvn spring-boot:run
   ```

5. **🌐 Accessing the API**
  - By default, the server runs on port `8080` 🚪. You can access it via [http://localhost:8080](http://localhost:8080).

## 📖 API Overview

### 🔐 Authentication
- **POST /api/auth/login**: Login endpoint for users.
- **POST /api/auth/register**: Register a new user.

### 🗂️ Task Management
- **GET /api/tasks**: Get all tasks.
- **POST /api/tasks**: Create a new task ➕.
- **GET /api/tasks/{id}**: Get details of a specific task 🔍.
- **PUT /api/tasks/{id}**: Update a task ✏️.
- **DELETE /api/tasks/{id}**: Delete a task ❌.

### 🔗 Task Sharing and Assignment
- **POST /api/tasks/{taskId}/share**: Share a task with another user.
- **POST /api/tasks/{taskId}/assign**: Assign a task to a user 👥.

### 💬 Comments
- **POST /api/tasks/{taskId}/comments**: Add a comment to a task 💬.
- **GET /api/tasks/{taskId}/comments**: Get all comments for a specific task 📝.

## 🔒 Security
This backend uses **OAuth2** for authentication, integrating with **Google** as an identity provider. Access tokens 🔑 are used to verify user identity, and permissions are scoped to actions the user is authorized to perform.

## 🧪 Testing
- **🧪 Unit Tests**: We use **JUnit** to ensure the quality of individual components.
- **🔄 Integration Tests**: Cover the interaction between components such as the repository, service, and controller.
- **📝 How to Run Tests**:
  ```sh
  mvn test
  ```

## 🤝 Contributing
Contributions are welcome! 🌟 To contribute:
- Fork the repository 🍴.
- Create a new branch (`git checkout -b feature-branch`) 🌿.
- Commit your changes (`git commit -m 'Add some feature'`) 💾.
- Push to the branch (`git push origin feature-branch`) 🚀.
- Open a pull request 🔄.


Feel free to reach out with any questions or feedback! 💌

