# Installation Guide for TaskManager Backend

## Prerequisites

Ensure you have the following before installing TaskManager:

- **Java 17**: Install Java Development Kit (JDK) 17.
- **Maven**: A build automation tool to compile and run the project.
- **PostgreSQL**: Database for storing task management data.
- **OAuth 2.0 Client ID**: Create this in **Google Cloud Console** for user authentication.

## Installation Steps

### 1. Clone the Repository
Clone the TaskManager repository to your local machine:

```sh
$ git clone https://github.com/username/taskmanager-backend.git
$ cd taskmanager-backend
```

### 2. Configure Environment Variables

- Create a `.env` file in the root of your project to store environment variables.
- Add the following configurations:

  ```
  POSTGRES_URL=jdbc:postgresql://localhost:5432/taskmanagement
  POSTGRES_USER=your_postgres_user
  POSTGRES_PASSWORD=your_postgres_password
  GOOGLE_CLIENT_ID=your_google_client_id
  GOOGLE_CLIENT_SECRET=your_google_client_secret
  MAIL_USERNAME=your_email@gmail.com
  MAIL_PASSWORD=your_app_password
  ```

### 3. Set Up the Database

- Start **PostgreSQL** on your machine.
- Create a new database named `taskmanagement`:

  ```sql
  CREATE DATABASE taskmanagement;
  ```

- Update the `application.properties` file or use environment variables to configure the database connection details.

### 4. Run the Application

Run the application using **Maven**:

```sh
$ mvn spring-boot:run
```

### 5. Accessing the API

- By default, the server runs on port `8443`.
- Test the API using **Postman** or another API testing tool at [https://localhost:8443](https://localhost:8443).

## Troubleshooting

### Common Issues

- **Database Connection Error**: Verify that **PostgreSQL** is running and the credentials in `.env` are correct.
- **OAuth Issues**: Ensure `GOOGLE_CLIENT_ID` , `GOOGLE_CLIENT_SECRET` and `GOOGLE_REDIRECT_URI` are correctly configured in **Google Cloud Console**.
- **Port Conflicts**: If port `8443` is in use, change the port in the `application.properties` file or set the `server.port` environment variable.

### Useful Commands

- **Clean and Rebuild the Project**:
  ```sh
  $ mvn clean install
  ```
- **Run Tests**:
  ```sh
  $ mvn test
  ```

## Support

If you run into any issues, open an issue on [GitHub](https://github.com/username/taskmanager-backend/issues).

