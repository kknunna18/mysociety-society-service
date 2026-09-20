# MySociety Society Service

Spring Boot 3 / Java 21 service for society administration, buildings, units, resident memberships, vehicles, and tenant-scoped settings. It uses the shared PostgreSQL schema `mysociety`, validates rather than migrates that schema, and never accesses Identity Service user records.

## Run

1. Create the `mysociety` PostgreSQL database and apply `database/migrations/V1__society_schema.sql` as described in [`database/README.md`](database/README.md).
2. Copy `.env.example` values into your environment. `JWT_SECRET` must be a private HS256 secret of at least 32 bytes and `JWT_ISSUER` must be `mysociety-identity`.
3. Run `gradlew.bat bootRun --args='--spring.profiles.active=local'` on Windows, or `./gradlew bootRun` elsewhere.

The API listens on `http://localhost:8082/api/v1`; OpenAPI is at `/api/v1/openapi` and Swagger UI at `/api/v1/swagger-ui.html`. Health probes and Prometheus metrics are exposed through `/api/v1/actuator`.

## Security

All business APIs require an HS256 JWT. The service uses `sub` as the external resident/user UUID, `society_id` as the authoritative tenant ID, and optional `roles` and `permissions` claims for authorities. Request body tenant IDs are not accepted. Every tenant-owned lookup constrains `society_id`; do not log JWTs, credentials, or personal contact details.

## Build and test

`gradlew.bat clean build` runs unit, web, and architecture tests. Tests do not require Docker. Testcontainers PostgreSQL is available for integration tests when Docker is present.
MySociety Society Service
