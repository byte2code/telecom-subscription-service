# Telecom Subscription Service

Spring Boot REST API for telecom user, account, and subscription management with Eureka registration, Hystrix support, and Feign-based downstream calls.

## Overview

This project manages telecom customer data in a single Spring Boot service. Version 4 keeps the existing user, account, and subscription CRUD flows, but changes the integration style: subscription creation now uses Feign to talk to the billing service, and user ticket retrieval is routed through a Feign client for the support service.

The project is useful for understanding CRUD APIs, JPA relationships, DTO mapping, service discovery, and declarative service-to-service communication.

## Concepts / Features Covered

- Spring Boot REST APIs
- Spring Data JPA entities and repositories
- One-to-one and one-to-many mapping
- DTO-based request handling
- User, account, and subscription CRUD
- Eureka client registration
- Hystrix dashboard and metrics exposure
- OpenFeign clients for billing and support calls
- Subscription creation with downstream invoice creation
- Ticket retrieval from support-service
- JSON serialization control with `@JsonIgnoreProperties`

## Tech Stack

- Java 17
- Spring Boot 2.7.13
- Spring Web
- Spring Data JPA
- Spring Cloud Netflix Eureka Client
- Spring Cloud Netflix Hystrix
- Spring Cloud OpenFeign
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

### Fetch user tickets

```bash
curl http://localhost:8080/api/user/tickets/1
```

Sample response:

```json
[]
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
2. Make sure the downstream billing and support services are available if you want the Feign calls to succeed.
3. Provide MySQL datasource settings in your local environment or profile, since this snapshot keeps discovery and resilience settings in `application.yml`.
4. Start the application with Maven or from your IDE.
5. Call the endpoints on port `8080`.

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
│   ├── feign/
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

    SubService --> BillingClient["billing-service Feign client"]
    SubService --> SupportClient["support-service Feign client"]
    UserAPI --> Tickets["/api/user/tickets/{userId}"]
    Tickets --> SupportClient
```

## Learning Highlights

- Using Eureka client registration in a Spring Boot app
- Replacing direct `RestTemplate` integration with Feign clients
- Adding separate downstream calls for billing and support
- Keeping user, account, and subscription CRUD in one service
- Managing JPA relationships while exposing DTO-friendly REST APIs

## Notes

- The application keeps `application.yml` focused on discovery and resilience settings.
- Local datasource settings are expected to be supplied outside this file.
- IDE files and build artifacts are intentionally excluded from version control.
