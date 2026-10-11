# Tragefox API

[![Java 25](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-9.8.1-blue.svg)](https://gradle.org)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

Tragefox API is an enterprise-grade RESTful API backend service built with **Spring Boot 4.1.1**, **Java 25**, and **MongoDB**. It provides secure identity management, role-based access control (RBAC), stateless JWT authentication, account profile management, Base32 sequential ID generation, OpenAPI documentation, and automated container orchestration.

---

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture & Project Structure](#architecture--project-structure)
- [Prerequisites](#prerequisites)
- [Configuration & Profiles](#configuration--profiles)
- [Getting Started](#getting-started)
  - [1. Running Locally](#1-running-locally)
  - [2. Running with Docker Compose](#2-running-with-docker-compose)
- [API Reference](#api-reference)
  - [Default Super User](#default-super-user)
  - [Authentication Endpoints](#authentication-endpoints)
  - [Account Management Endpoints](#account-management-endpoints)
  - [Interactive Documentation](#interactive-documentation)
- [Testing & Quality Assurance](#testing--quality-assurance)
  - [Running Tests](#running-tests)
  - [Code Formatting](#code-formatting)
  - [Code Coverage (JaCoCo)](#code-coverage-jacoco)
  - [Static Analysis (Qodana)](#static-analysis-qodana)
- [Deployment](#deployment)
- [Contributing & Guidelines](#contributing--guidelines)

---

## Features

- **Java 25 & Modern Toolchains**: Configured with Java 25 toolchains for the latest language and performance enhancements.
- **Spring Boot 4.1.1 Architecture**: Modern modular starters, Jakarta EE annotations, and tools.jackson 3 integration.
- **Stateless JWT Security**: Custom JWT authentication filter (`io.jsonwebtoken 0.13.0`) with BCrypt password encoding.
- **Role-Based Access Control**: Granular access control supporting `SUPER_USER`, `APP_ADMIN`, `APP_MANAGER`, `APP_USER`, and `PASSWORD_RESET_CLAIM`.
- **Base32 Unique Identifier Generator**: Automated, collision-resistant Base32 ID generation listener for MongoDB document persistence.
- **Auditing Framework**: Automatic metadata auditing (`createdBy`, `createdAt`, `updatedBy`, `updatedAt`) managed by Spring Data MongoDB.
- **MapStruct 1.6.3 Mapping**: High-performance, compile-time object mapping with custom reusable meta-annotations.
- **Global Error Handling**: Uniform API error structure with standard status codes, error codes, and validation error messages.
- **Containerized Orchestration**: Production-ready multi-stage `Dockerfile` and `docker-compose.yml` with health checks.
- **Automated CI/CD**: GitHub Actions workflows for Gradle build validation, Testcontainers test runs, and JetBrains Qodana static code inspection.

---

## Technology Stack

| Category | Technology | Version | Description |
|---|---|---|---|
| **Language** | Java | 25 (LTS) | Core programming language via Gradle Toolchains |
| **Framework** | Spring Boot | 4.1.1 | Application framework & dependency management |
| **Build Tool** | Gradle | 9.8.1 | Build automation and dependency resolution |
| **Database** | MongoDB | 7.0 | Document-oriented database |
| **Driver** | Spring Data MongoDB | Managed | Reactive and synchronous document repositories |
| **Security** | Spring Security & JJWT | 0.13.0 | Stateless JWT authorization & BCrypt hashing |
| **Serialization** | Jackson 3 (`tools.jackson`) | Managed | High-speed JSON serialization & deserialization |
| **Object Mapper** | MapStruct | 1.6.3 | Compile-time type-safe bean mappers |
| **Boilerplate** | Project Lombok | Managed | Code generation for getters, setters, builders |
| **Documentation** | SpringDoc OpenAPI | 3.1.1 | OpenAPI 3.1 specification & Swagger UI |
| **Testing** | JUnit 5 & Mockito | Managed | Unit & integration testing |
| **Integration Tests**| Testcontainers | 2.0.5 | Ephemeral MongoDB containers for testing |
| **Code Formatter** | Spotless | 8.10.4 | Enforces Palantir Java formatting standards |
| **Code Coverage** | JaCoCo | 0.8.15 | Test coverage reporting & threshold verification |

---

## Architecture & Project Structure

The project follows clean architecture principles with a modular package layout:

```
tragefox-api/
├── .github/
│   └── workflows/
│       ├── gradle.yml               # CI build, test, and dependency submission
│       └── qodana.yml               # JetBrains Qodana static code analysis
├── gradle/
│   └── wrapper/                     # Gradle wrapper distribution files (v9.8.1)
├── src/
│   ├── main/
│   │   ├── java/com/tragefox/api/
│   │   │   ├── Application.java     # Main Spring Boot application entry point
│   │   │   ├── application/
│   │   │   │   ├── controller/      # REST endpoint controllers
│   │   │   │   └── service/         # Service interfaces, implementations & MapStruct mappers
│   │   │   ├── common/
│   │   │   │   ├── config/          # Audit, Mapper, and OpenAPI configuration
│   │   │   │   ├── entity/          # BaseEntity (auditing) & Base32 ID sequences
│   │   │   │   ├── identifier/      # Base32 ID generation service & Mongo listeners
│   │   │   │   └── mapper/          # BaseMapper, MapperFactory & MapStruct meta-annotations
│   │   │   ├── exception/           # Centralized exception handling & error models
│   │   │   └── security/            # Security configs, JWT filters, models & repos
│   │   └── resources/
│   │       ├── application.yml         # Base application configuration
│   │       ├── application-local.yml   # Local development profile settings
│   │       └── application-docker.yml  # Docker container profile settings
│   └── test/
│       ├── java/com/tragefox/api/   # Unit and integration test suites
│       └── resources/               # Test configuration profiles (application-test.yml)
├── .dockerignore                    # Build artifacts excluded from Docker context
├── Dockerfile                       # Multi-stage Dockerfile for Java 25
├── docker-compose.yml               # Local compose stack (MongoDB 7.0 + Application)
├── build.gradle                     # Gradle build script with plugins & dependencies
├── settings.gradle                  # Gradle project settings
└── qodana.yaml                      # JetBrains Qodana configuration
```

---

## Prerequisites

Before running or building the project locally, ensure you have:

- **JDK 25** (e.g. Amazon Corretto 25 or Eclipse Temurin 25)
- **Docker & Docker Compose** (version 24+ / Compose v2+)
- **Git**

---

## Configuration & Profiles

The application uses standard Spring Boot profile-based YAML configurations:

| Profile | File | Default Port | MongoDB Connection | Use Case |
|---|---|---|---|---|
| `local` *(default)* | `application-local.yml` | `8082` | `mongodb://localhost:27017/tragefox` | Local machine development |
| `docker` | `application-docker.yml` | `8080` | `mongodb://mongodb:27017/tragefox` | Docker Compose deployment |
| `test` | `application-test.yml` | `8081` | Dynamic Testcontainers | Automated test execution |

Environment variables can override any setting:

- `SERVER_PORT`: Port the application listens on.
- `SPRING_DATA_MONGODB_URI`: MongoDB connection string.
- `SPRING_PROFILES_ACTIVE`: Active profile name (e.g. `local`, `docker`).

---

## Getting Started

### 1. Running Locally

1. **Start MongoDB**:
   Ensure MongoDB is running locally on port `27017`:
   ```bash
   docker run -d --name local-mongo -p 27017:27017 mongo:7.0
   ```

2. **Build and Run the Application**:
   ```bash
   ./gradlew bootRun
   ```

3. The application will start on `http://localhost:8082` with the `local` profile.

---

### 2. Running with Docker Compose

Deploy the complete stack (MongoDB 7.0 + Tragefox API) in one command:

```bash
# Build the image and start all containers in the background
docker compose up -d --build

# View real-time logs
docker compose logs -f app

# Stop the containers
docker compose down
```

The application will be accessible at `http://localhost:8080`.

---

## API Reference

### Default Super User
On application startup, a default super user is automatically initialized if it does not already exist:

- **Username**: `admin`
- **Password**: `admin`
- **Role**: `SUPER_USER`

---

### Authentication Endpoints (`/api/v1/auth`)

| Method | Path | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/signup` | Public | Register a new user |
| `POST` | `/api/v1/auth/login` | Public | Authenticate user and obtain JWT token |
| `POST` | `/api/v1/auth/reset-password/{userName}` | Public | Trigger password reset |
| `PUT` | `/api/v1/auth/password` | Public | Update password using reset claim |
| `PUT` | `/api/v1/auth/update/{userName}` | Authenticated | Update user authentication details |
| `GET` | `/api/v1/auth/find/{userName}` | Authenticated | Retrieve user authentication details |
| `DELETE` | `/api/v1/auth/deactivate/{userName}`| Authenticated | Deactivate a user account |

#### Example Login Request:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"userName": "admin", "password": "admin"}'
```

#### Example Login Response:
```json
{
  "userName": "admin",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### Account Management Endpoints (`/api/v1/account`)

All account endpoints require a valid Bearer token in the `Authorization` header (`Bearer <token>`):

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/account` | Create account details with auto-generated identifier |
| `GET` | `/api/v1/account/{identifier}` | Retrieve account details by identifier |
| `PUT` | `/api/v1/account/{identifier}` | Update existing account details |
| `DELETE` | `/api/v1/account/{identifier}` | Deactivate account details |

---

### Interactive Documentation

Once the application is running, access the interactive OpenAPI documentation:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) (or port `8082` when running locally)
- **OpenAPI JSON**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## Testing & Quality Assurance

### Running Tests

The test suite includes unit tests and integration tests that use **Testcontainers** to automatically start an ephemeral MongoDB 7.0 container:

```bash
./gradlew test
```

### Code Formatting

The project enforces code formatting standards using **Spotless** with the **Palantir Java Format**:

```bash
# Verify formatting
./gradlew spotlessCheck

# Apply formatting automatically
./gradlew spotlessApply
```

### Code Coverage (JaCoCo)

Generate JaCoCo test execution reports and enforce bundle line coverage thresholds:

```bash
./gradlew test jacocoTestReport jacocoTestCoverageVerification
```

The HTML report will be generated at:
```
build/reports/jacoco/test/html/index.html
```

### Static Analysis (Qodana)

The project includes pre-configured **JetBrains Qodana** inspections using the free community JVM linter:
- Configured in `qodana.yaml`
- Automated via `.github/workflows/qodana.yml` on pull requests and pushes

---

## Deployment

### Docker Multi-Stage Build

The [`Dockerfile`](Dockerfile) is optimized for security and container caching:

1. **Builder Stage**: Uses `amazoncorretto:25`, installs build tools, caches Gradle dependencies, and compiles the bootable jar.
2. **Runtime Stage**: Copies solely the final jar into a minimal runtime image exposing port `8080`.

To build the standalone Docker image:

```bash
docker build -t tragefox-api:latest .
```

To run the container:

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATA_MONGODB_URI=mongodb://<mongo-host>:27017/tragefox \
  tragefox-api:latest
```

---

## Contributing & Guidelines

1. Ensure code conforms to formatting standards: run `./gradlew spotlessApply` before committing.
2. Write unit tests for new service methods and ensure high branch and instruction coverage.
3. Validate the entire build before pushing:
   ```bash
   ./gradlew clean check build
   ```
