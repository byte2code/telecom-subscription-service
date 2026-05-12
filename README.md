# Telecom Subscription Service

Spring Boot REST API for telecom user, account, and subscription management with Eureka registration and Hystrix-based fallback support.

## Overview

This project manages telecom customer data in one Spring Boot service. The earlier versions focused on CRUD for users, accounts, and subscriptions. Version 3 adds service-discovery and resilience support, so the application now registers with Eureka, exposes Hystrix monitoring, and wraps the user ticket lookup in a fallback flow.

The project is useful for understanding CRUD APIs, JPA relationships, DTO mapping, service discovery, and circuit-breaker style integration through `RestTemplate` and Hystrix.

## Concepts / Features Covered

- Spring Boot REST APIs
- Spring Data JPA entities and repositories
- One-to-one and one-to-many mapping
- DTO-based request handling
- User, account, and subscription CRUD
- User ticket lookup endpoint
- Hystrix fallback for ticket retrieval
- Subscription creation with downstream billing call
- Eureka client registration
- Hystrix dashboard and metrics exposure
- JSON serialization control with `@JsonIgnoreProperties`

## Tech Stack

- Java 17
- Spring Boot 2.7.13
- Spring Web
- Spring Data JPA
- Spring Cloud Netflix Eureka Client
- Spring Cloud Netflix Hystrix
- Hystrix Dashboard
- RestTemplate
- MySQL
- Lombok
- Maven

## API Endpoints

### User APIs

- `GET /api/user`
- `GET /api/user/{id}`
- `GET /api/user/name/{name}`
- `GET /api/user/email/{email}`
- `GET /api/user/tickets/{userId}`
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
- `DELETE /api/subscription/{id}`

## Example Requests

### Create a user

```bash
curl -X POST http://localhost:8080/api/user \
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

### Create a subscription

```bash
curl -X POST http://localhost:8080/api/subscription \
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

Fallback response:

```json
{
  "message": "Subscription temporarily unavailable (fallback)"
}
```

### Fetch user tickets

```bash
curl http://localhost:8080/api/user/tickets/1
```

Fallback response:

```json
[
  {
    "message": "Tickets unavailable (fallback)"
  }
]
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

1. Start your Eureka server on `http://localhost:8761`.
2. Provide MySQL datasource settings in your local environment or profile, since this snapshot keeps discovery and Hystrix settings in `application.yml`.
3. Start the application with Maven or from your IDE.
4. Call the endpoints on port `8080`.

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
│   ├── BillingDtos/
│   ├── SupportDtos/
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

    Eureka["Eureka Server :8761"] --- App["SubscriptionService :8080"]
    App --> UserService["UserService"]
    App --> AccountService["AccountService"]
    App --> SubService["SubscriptionService"]

    UserService --> UserTickets["/tickets/{userId}"]
    UserTickets --> Fallback["Hystrix fallback"]
    SubService --> Billing["Billing call"]
```

## Learning Highlights

- Using Eureka client registration in a Spring Boot app
- Adding Hystrix fallback behavior for unstable remote calls
- Keeping user, account, and subscription CRUD in one service
- Adding a separate ticket lookup path for users
- Managing JPA relationships while exposing DTO-friendly REST APIs

## Notes

- The application keeps `application.yml` focused on discovery and resilience settings.
- Local datasource settings are expected to be supplied outside this file.
- IDE files and build artifacts are intentionally excluded from version control.
