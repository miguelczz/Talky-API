# Talky - Backend

This repository contains the backend portion of **Talky**, built with **Java**, **Spring Boot**, and **PostgreSQL**.
Its main function is to manage the assistant's business logic, including persistence of users, conversations,
and academic content, as well as centralizing security (Cognito + Spring Security) and external integrations (n8n, OpenAI).
It also provides advanced mechanisms such as abuse control and versioned migrations.

---

## Architecture & Technologies

- **Java 17+**
- **Spring Boot** (REST API)
- **Spring Security with OAuth2 Resource Server** (Cognito JWT validation)
- **Spring Data JPA** (entity and repository management)
- **PostgreSQL** as the main database
- **Flyway** for version control and migrations
- **Bucket4j** for rate limiting (abuse prevention)
- **WireMock** in tests to mock n8n/OpenAI

---

## Core Entities

### Users and Sessions
- **User** → basic user info (Cognito sub, email, name, role)
- **Conversation** → conversation thread linked to a user
- **Message** → messages within a conversation, with metadata support (JSONB)

### Academic Content
- **GlossaryTerm** → glossary terms and definitions
- **Lesson** → lessons organized by level/order
- **Exam** → exams linked to a lesson
- **Question** → exam questions (options stored as JSONB)
- **UserExamResult** → a user's exam results

---

## Core Endpoints

- `POST /api/v1/conversations` → creates a conversation
- `GET /api/v1/conversations` → lists the user's conversations
- `GET /api/v1/conversations/{id}` → conversation detail
- `POST /api/v1/conversations/{id}/messages` → sends a message and receives a response
- `GET /api/v1/glossary` → lists glossary terms
- `GET /api/v1/lessons` → lists lessons
- `GET /api/v1/exams/{id}` → exam detail with questions
- `POST /api/v1/exams/{id}/submit` → records an exam result

---

## External Integrations

- **AWS Cognito**: user management and JWT token validation in the backend.
- **n8n**: flow orchestration; information is received and sent via webhooks.
- **OpenAI** (planned): possible direct connection to enable token-by-token streaming.

---

## Running the Project

Clone the repository:

```bash
git clone https://github.com/miguelczz/talky-API.git
cd talky-API
```

Build and run with Maven:

```bash
mvn clean install
mvn spring-boot:run
```

The backend will start at:

```
http://localhost:8080
```

---

## Security Notes

- User validation is handled with **Cognito JWT** through Spring Security.
- Configure database credentials and external service credentials via **environment variables** or a secure configuration file.
- Use an appropriate `.gitignore` to avoid committing sensitive information (keys, passwords, etc.).
