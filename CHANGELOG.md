# Changelog

All notable changes to this project are documented in this file.

## [v8.0.0] - 2026-05-30

Eighth version of the Telecom Subscription Service.

- Added retry handling for billing-event consumption with exponential backoff.
- Added a dead-letter queue for poisoned billing messages.
- Added a dead-letter listener to observe failed billing events separately.
- Expanded the README architecture section to explain the API, resilience, messaging, and integration layers.
- Added tests for the retry-aware RabbitMQ configuration and billing listener validation.

## [v7.0.0] - 2026-05-30

Seventh version of the Telecom Subscription Service.

- Added RabbitMQ billing events for subscription lifecycle changes.
- Published `SUBSCRIPTION_CREATED`, `INVOICE_REQUESTED`, `PAYMENT_FAILED`, and `SUPPORT_TICKET_RAISED` events from the subscription flow.
- Added RabbitMQ exchange, queue, binding, publisher, and listener support.
- Updated the README with the billing event flow and local RabbitMQ setup notes.
- Added tests for the event publisher and the subscription service event flow.

## [v6.0.0] - 2026-05-29

Sixth version of the Telecom Subscription Service.

- Replaced the Hystrix-based user ticket fallback with Resilience4j circuit breaker, retry, timeout, and fallback support.
- Switched the support-ticket flow to an async Feign-backed path with fallback handling.
- Added Resilience4j configuration for the support ticket lookup use case.
- Removed Hystrix bootstrap and dashboard wiring from the application.
- Updated the README and added service-level tests for the new resilience path.

## [v5.0.0] - 2026-05-28

Fifth version of the Telecom Subscription Service.

- Added explicit subscription lifecycle states: `REQUESTED`, `ACTIVE`, `SUSPENDED`, `CANCELLED`, and `PAYMENT_FAILED`.
- Updated subscription creation to start in `REQUESTED` and move to `ACTIVE` after successful billing.
- Added lifecycle transition endpoints for activate, suspend, cancel, and payment-failed handling.
- Removed the redundant hardcoded billing call from the subscription controller.
- Updated the README with lifecycle examples and an updated flow diagram.

## [v4.0.0] - 2026-05-12

Fourth version of the Telecom Subscription Service.

- Added OpenFeign clients for billing and support service calls.
- Switched subscription creation to create invoices through the billing service client.
- Updated user ticket lookup to use the support service client.
- Kept Eureka client registration and Hystrix runtime support in place.
- Expanded the README with the new service-to-service flow and examples.

## [v3.0.0] - 2026-05-12

Third version of the Telecom Subscription Service.

- Added Eureka client registration and Hystrix dashboard support.
- Switched the application runtime to port `8080`.
- Added Hystrix fallback handling for the user ticket lookup flow.
- Updated subscription creation to call the billing endpoint after validating the user.
- Simplified `application.yml` to focus on discovery and resilience settings.

## [v2.0.0] - 2026-05-12

Second version of the Telecom Subscription Service.

- Added `/api/user/tickets/{userId}` for user ticket lookup.
- Updated subscription creation to validate the user first and then call the billing service.
- Expanded the README with API examples, flow notes, and project structure.
- Kept the existing user, account, and subscription CRUD flows intact.

## [v1.0.0] - 2026-05-12

Initial release of the Telecom Subscription Service.

- Added user management APIs for create, read, update, delete, and search by name/email.
- Added account management APIs tied to user records.
- Added subscription management APIs tied to user records.
- Documented the project structure, API flow, and example payloads in the README.
- Kept local MySQL configuration in `application.yml` for easy setup.
