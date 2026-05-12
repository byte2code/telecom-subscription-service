# Changelog

All notable changes to this project are documented in this file.

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
