# BartExchangeAI

REST API for a barter exchange platform built with Spring Boot.

## Tech Stack

- **Java 21**, **Spring Boot 3.5**
- **Spring Security** + JWT authentication
- **Spring Data JPA** + Hibernate
- **PostgreSQL** (prod) / **H2** (dev)
- **Liquibase** for database migrations
- **MapStruct** for DTO mapping
- **Swagger/OpenAPI** (springdoc)
- **JUnit 5** + **Mockito** (116 tests)
- **Docker Compose** for local PostgreSQL

## Features

- User management with rating system
- Offer listings with categories and groups
- Exchange workflow (create, complete, cancel)
- Messaging between exchange participants
- Review system with average rating calculation
- JWT-based authentication (register/login)
- Paginated API responses
- Global error handling with structured error responses
- Swagger UI for API exploration

## Getting Started

### Prerequisites

- Java 21+
- Gradle 8+ (or use included wrapper)

### Run with H2 (development)

```bash
./gradlew bootRun
```

The app starts at `http://localhost:8080` with an in-memory H2 database and sample data.

### Run with PostgreSQL (production)

```bash
# Start PostgreSQL
docker compose up -d

# Run the app with prod profile
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### Run Tests

```bash
./gradlew test
```

## API Endpoints

| Resource    | URL                  | Methods                          |
|-------------|----------------------|----------------------------------|
| Auth        | `/api/auth`          | POST register, login             |
| Users       | `/api/users`         | GET, POST, PUT, DELETE           |
| Categories  | `/api/categories`    | GET, POST, PUT, DELETE           |
| Offers      | `/api/offers`        | GET, POST, PUT, DELETE + search  |
| Exchanges   | `/api/exchanges`     | GET, POST, PUT, DELETE + complete/cancel |
| Messages    | `/api/messages`      | GET, POST, DELETE                |
| Reviews     | `/api/reviews`       | GET, POST, PUT, DELETE + average |
| Groups      | `/api/groups`        | GET, POST, PUT, DELETE + search  |

### Authentication

GET endpoints are public. POST/PUT/DELETE require a JWT token:

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","email":"user1@test.com","password":"password123"}'

# Use the returned token
curl -H "Authorization: Bearer <token>" \
  -X POST http://localhost:8080/api/offers ...
```

### Swagger UI

Available at: `http://localhost:8080/swagger-ui.html`

## Project Structure

```
src/main/java/com/example/bartexchangeai/
├── config/          # SecurityConfig, OpenApiConfig, DataInitializer
├── controller/      # REST controllers (8 + AuthController)
├── dto/             # Data Transfer Objects + auth DTOs + ErrorResponse
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── mapper/          # MapStruct mappers
├── model/           # JPA entities (User, Offer, Exchange, Message, Review, Category, Group)
├── repository/      # Spring Data JPA repositories
├── security/        # JWT provider, filter, UserDetailsService
└── service/         # Service interfaces + implementations
```

## Dev Credentials (H2 profile)

| User    | Password      | Role  |
|---------|---------------|-------|
| alice   | password123   | USER  |
| bob     | password123   | USER  |
| charlie | password123   | ADMIN |

H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, user: `sa`)
