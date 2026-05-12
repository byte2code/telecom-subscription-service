# Telecom Subscription Service

Spring Boot REST API for managing telecom users, their accounts, and subscription plans.

## Overview

This project is a small service for telecom-style customer management. It keeps the core business flow in one application, with separate layers for users, accounts, and subscriptions. That makes it a good learning project for Spring Boot, JPA relationships, and CRUD-based API design.

## Concepts / Features Covered

- Spring Boot REST APIs
- Spring Data JPA with one-to-one and one-to-many relationships
- MySQL persistence
- DTO-based request handling
- CRUD operations for users, accounts, and subscriptions
- JSON response mapping with `@JsonIgnoreProperties`

## Tech Stack

- Java 17
- Spring Boot 2.7.13
- Spring Web
- Spring Data JPA
- MySQL
- Lombok
- Maven

## API Endpoints

### User APIs

- `GET /api/user`
- `GET /api/user/{id}`
- `GET /api/user/name/{name}`
- `GET /api/user/email/{email}`
- `POST /api/user`
- `PUT /api/user/{id}`
- `DELETE /api/user/{id}`

### Account APIs

- `GET /account`
- `GET /account/{id}`
- `GET /account/userId/{userId}`
- `POST /account`
- `PUT /account/{id}`
- `DELETE /account/{id}`

### Subscription APIs

- `GET /api/subscription`
- `GET /api/subscription/{id}`
- `GET /api/subscription/userId/{userId}`
- `POST /api/subscription`
- `PUT /api/subscription/{id}`
- `DELETE /api/subscription/{id}`

## Example Requests

### Create a user

```bash
curl -X POST http://localhost:8081/api/user \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Asha Patel",
    "email": "asha@example.com",
    "contact": 9876543210,
    "address": "Mumbai"
  }'
```

Expected response:

```json
{
  "message": "User created Successfully"
}
```

### Create an account

```bash
curl -X POST http://localhost:8081/account \
  -H "Content-Type: application/json" \
  -d '{
    "user": { "id": 1 },
    "balance": "2500",
    "details": "Primary billing account"
  }'
```

Expected response:

```json
{
  "message": "Account Created Successfully"
}
```

### Create a subscription

```bash
curl -X POST http://localhost:8081/api/subscription \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "price": 499,
    "planName": "Silver Plan",
    "planDetails": "Monthly calling and data pack"
  }'
```

Expected response:

```json
{
  "message": "Subscription Created Successfully"
}
```

## Sample Output

### Get all users

```json
[
  {
    "id": 1,
    "name": "Asha Patel",
    "email": "asha@example.com",
    "contact": 9876543210,
    "address": "Mumbai"
  }
]
```

### Get subscriptions for a user

```json
[
  {
    "id": 1,
    "price": 499,
    "planName": "Silver Plan",
    "planDetails": "Monthly calling and data pack"
  }
]
```

## How to Run

1. Create a MySQL database named `telecom_user_service`.
2. Update the username and password in `src/main/resources/application.yml` if your local MySQL setup is different.
3. Start the application with Maven or from your IDE.
4. Call the endpoints on port `8081`.

Example:

```bash
mvn spring-boot:run
```

## Project Structure

```text
SubscriptionService/
├── src/main/java/Telecom/SubscriptionService/
│   ├── controller/
│   ├── dto/
│   ├── model/
│   ├── repository/
│   ├── service/
│   └── SubscriptionServiceApplication.java
├── src/main/resources/application.yml
├── README.md
├── CHANGELOG.md
└── .gitignore
```

## Flow Diagram

```mermaid
flowchart LR
    Client[Client] --> UserAPI["/api/user"]
    Client --> AccountAPI["/account"]
    Client --> SubAPI["/api/subscription"]

    UserAPI --> UserService["UserService"]
    AccountAPI --> AccountService["AccountService"]
    SubAPI --> SubService["SubscriptionService"]

    UserService --> UserDB[(User table)]
    AccountService --> AccountDB[(Account table)]
    SubService --> SubDB[(Subscription table)]

    UserDB --- AccountDB
    UserDB --- SubDB
```

## Learning Highlights

- Modeling one-to-one and one-to-many JPA relationships
- Using DTOs to separate request payloads from persistence models
- Designing small CRUD services with clear REST endpoints
- Returning message-based responses for create/update/delete flows
- Practicing entity mapping and JSON serialization control

## Notes

- `application.yml` is kept in the repository for local configuration.
- IDE files and build artifacts are excluded from version control.
