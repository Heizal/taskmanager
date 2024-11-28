# Project Name: TaskManager Backend

## Overview

**TaskManager** is a backend application designed to help product owners and developers keep track of ongoing tasks within a tech team. It is similar to **JIRA** but focuses more on providing visibility into the tasks developers are working on and enabling efficient communication between product owners and developers.

This backend serves as the central hub for managing tasks, assigning responsibilities, sharing tasks across the team, and tracking updates. It is built using **Spring Boot** and leverages **OAuth** for authentication, ensuring a secure way to handle user access.

#### Note that this is a backend-only project, intended for integration with any compatible frontend or API testing tool like **Postman**.

### Key Features
- **Task Management**: Create, update, delete, assign, and comment on tasks.
- **Role-Based Access Control**: Secure actions based on user roles.
- **Task Sharing and Comments**: Developers can share tasks and communicate via comments to improve collaboration.
- **OAuth Authentication**: Secure user authentication with **JWT** or **Google Authentication**
- **Email Notifications**: Send email alerts to users for task assignments.

## Technologies Used
- **Java 17**
- **Spring Boot 3**
- **PostgreSQL** for data persistence
- **OAuth2.0** for user authentication
- **JavaMailSender** for email notifications
- **JUnit** for testing

## Getting Started

### Prerequisites
- **Java 17**
- **Maven**
- **PostgreSQL**
- **An OAuth 2.0 client ID for Google Authentication** (configured in Google Cloud Console)

### Installation Instructions
- Find the installation instructions in the [INSTALLATION.md](docs/INSTALLATION.md)

### Using the API
- Find a complete user guide in the [USER_GUIDE.md](docs/USER_GUIDE.md)


## System Architecture
- A full conceptual guide with the system architecture can be found in the [CONCEPTUAL_GUIDE.md](docs/CONCEPTUAL_GUIDE.md)

## Threat model and security measures
- A comprehensive threat model of the backend system including the security measures taken can be found in [THREAT_MODEL.md](docs/THREAT_MODEL.md)

## Contributing
Contributions are welcome! To contribute:
- Fork the repository.
- Create a new branch (`git checkout -b feature-branch`).
- Commit your changes (`git commit -m 'Add some feature'`).
- Push to the branch (`git push origin feature-branch`).
- Open a pull request.

Feel free to reach out with any questions or feedback!

