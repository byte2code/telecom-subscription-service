# Changelog

All notable changes to this project are documented in this file.

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
