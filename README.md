# Virality Engine

This repository contains the Spring Boot microservice for the Virality Engine API. It acts as an API gateway and guardrail system managing distributed state and enforcing strict interaction limits using Redis.

## Tech Stack
- Java 17
- Spring Boot 3.x
- PostgreSQL
- Redis
- Spring Data Redis
- Spring Data JPA

## Setup Instructions

1. Navigate to the project root directory.
2. Spin up the necessary dependencies (Postgres & Redis) using Docker Compose:
   ```bash
   docker-compose up -d
   ```
3. Run the Spring Boot application using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

## Postman Collection
You can find the fully configured Postman collection (`virality-engine.postman_collection.json`) in the root folder. Import it directly into Postman to easily test the provided endpoints.

## Design Decisions & Thread Safety Guarantees

To guarantee reliable concurrency protection and 100% thread safety across the application—especially to handle extreme race conditions seamlessly (e.g., stopping 200 bots trying to comment at the exact same millisecond without leaking into DB)—the service implements rigid **Redis Atomic Operations** to block requests before any DB operations take place.

### 1. The Horizontal Cap (Guardrail Logic)
We utilize the Redis `INCR` (`opsForValue().increment()`) operation. Redis handles operations serially. Running a clustered node setup still leverages the atomicity of the `INCR` command at large scale. After the increment, the `InteractionService` evaluates the dynamically returned counter value. If `count > 100`, the bot comment logic halts immediately, raising a `RateLimitException` (Status: 429 Too Many Requests) and aborting the DB save entirely.

### 2. The Cooldown Lock
To restrict a specific bot from interacting with a specific human more than once via a 10-minute timeout limit, we leverage the atomic SET-if-Not-Exists (`SET NX`) feature in Redis via `opsForValue().setIfAbsent()`. This intrinsically guarantees zero race conditions as Redis strictly dictates that only the initial requesting thread performs and passes the assignment check. If it encounters a key populated already, it evaluates strictly to false, reverting to the 429 Too Many Requests response loop.

### 3. Application Statelessness
At the core of the service, all dynamic memory variables, interaction caps, rate limit trackers, and pending pushes rely 100% on the Redis node cache system. Java memory remains isolated from application state. We rely on PostgreSQL as the final source of truth; nonetheless, database interactions remain explicitly restricted directly by the Redis validations acting smoothly as the gatekeeper.
