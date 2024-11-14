# Threat model and security measures.

## Overview
To create this threat model I was inspired by the STRIDE Threat model. First I lay out the security threat, the endpoints that could be affected and finally security measures I implemented to mitigate against these threats


### 1. Spoofing Identity

**Threat:** Attackers pretending to be someone else to gain unauthorized access.

- #### **End points affected**
  - Login: `/api/auth/login`
  - OAuth Authorization: `/oauth2/authorization/google`
- #### **Security measures**
  - **JWT Tokens**: Signed JWTs are used to verify the identity of users. Tokens are signed using RS256 with a private key, and the public key is used to verify the signature—preventing anyone from faking a valid JWT without the private key.
  - **OAuth2 Authentication:** Users can authenticate via trusted providers like Google.

### 2. Tampering with Data

**Threat:** Malicious actors attempt to modify data while its being transferred or when its in the Database

- #### **Endpoints affected**

  - Task endpoints: `/api/tasks`
  - Comment endpoints: `/api/tasks/comments`
  - Collaboration endpoints: `/api/tasks/{{taskId}}/share`

- #### Security measures

  - **HTTPS/SSL**: All communication between the client and server is done over HTTPS. This encrypts the data, preventing attackers from intercepting and altering it in transit.
  - For a user to access any of these endpoints they have to authenticate themselves. JWTs ensure the integrity of each request as any modification to a token invalidates its signature which would block out an attacker

### 3. Repudiation

**Threat:** Users denying their actions, e.g., claiming they didn’t create/delete or assign a task.

- #### **Endpoints affected**

    - All endpoints that involve data being modified.

- #### Security measures

    - **Log user actions**: Use of **JWT tokens** allows us to track actions taken by a specific user. Since the tokens include user information, we can verify and log each action made by an authenticated user.
    
### 4. Information Disclosure

**Threat:** Unauthorized access to sensitive data.

- #### **Endpoints affected**

  - Database: Personal information stored in the database
  - Auth endpoints
  - User endpoints: `/api/users`

- #### Security measures

  - **Role Based Access Control**: Sensitive endpoints, like those for managing user roles `/api/users/**`, are only accessible by **ADMIN** users.
  - **Encryption with HTTPS**: With **TLS**, any data that is transmitted between the user and the server with any third parties is encrypted preventing man in the middle attacks.
  - **Password encryption in the database**: Passwords are hashed using **BCrypt** before being saved to the database

### 5. Denial of Service

**Threat:** Flooding the server with requests to exhaust resources, making the service unavailable.

- #### **Endpoints affected**

  - Auth endpoints: `/api/auth/login`
  - Task endpoints: `/api/tasks`

- #### Security measures

  - **Token Expiry**: **Short-lived JWTs** _(15 minutes)_ ensure that even if many tokens are issued, they expire soon, reducing the load associated with verification.
  - **COULD DO (Didnt do):** Rate limiting on sensitive endpoints like auth endpoints
