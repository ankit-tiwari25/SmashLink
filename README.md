# 🔗 SmashLink — URL Shortener

> Smash long URLs into short, sharp links in seconds.

SmashLink is a production-grade URL shortening service built with Java and Spring Boot, featuring JWT authentication, Redis caching, email notifications, API rate limiting, and distributed tracing.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Database | MySQL + Spring Data JPA |
| Cache | Redis |
| Security | Spring Security + JWT (JJWT) |
| Email | JavaMailSender (Gmail SMTP) |
| Rate Limiting | Bucket4j |
| Tracing | Micrometer + Zipkin (Brave) |
| Build Tool | Maven |
| Utilities | Lombok, SLF4J |

---

## ✨ Features

- **User Registration & Login** — Secure registration with BCrypt password encoding and JWT-based authentication
- **Role-Based Access Control** — `ROLE_USER` and `ROLE_ADMIN` with protected endpoints
- **Admin Management** — View, deactivate, lock, and delete users; view all URLs per user
- **URL Shortening** — Base62 encoding on DB auto-increment ID guarantees zero-collision short codes
- **Hit Count Tracking** — Tracks every redirect hit with configurable hit limits
- **URL Expiry** — Set expiry dates on short URLs
- **Email Notifications** — Observer pattern triggers emails on hit limit nearing, exhausted, expiry nearing, and expired events
- **Redis Caching** — 30-minute TTL cache on URL lookups for fast redirects
- **API Rate Limiting** — 10 requests/minute per IP and per user via Bucket4j
- **Distributed Tracing** — Zipkin tracing with `traceId` and `spanId` in every log line

---

## 📁 Project Structure

```
smashlink/
├── src/main/java/com/smashlink/
│   ├── SmashLinkApplication.java
│   ├── admin/
│   │   └── controller/AdminController.java
│   ├── auth/
│   │   ├── controller/AuthController.java
│   │   └── dto/
│   ├── config/
│   │   └── RedisConfig.java
│   ├── exception/
│   │   ├── AppException.java
│   │   └── GlobalExceptionHandler.java
│   ├── notification/
│   │   ├── event/
│   │   ├── observer/
│   │   ├── publisher/
│   │   └── service/EmailService.java
│   ├── ratelimit/
│   │   ├── RateLimitConfig.java
│   │   └── RateLimitFilter.java
│   ├── security/
│   │   ├── CustomUserDetailsService.java
│   │   ├── JwtAuthFilter.java
│   │   ├── JwtUtil.java
│   │   └── SecurityConfig.java
│   ├── url/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/Url.java
│   │   ├── repository/UrlRepository.java
│   │   └── service/
│   ├── user/
│   │   ├── controller/UserController.java
│   │   ├── dto/
│   │   ├── entity/User.java
│   │   ├── enums/
│   │   ├── repository/UserRepository.java
│   │   └── service/
│   └── util/
│       └── Base62Util.java
└── src/main/resources/
    └── application.properties
```

---

## ⚙️ Setup & Installation

### Prerequisites

- Java 21
- Maven
- MySQL
- Redis
- Gmail account with App Password

### 1. Clone the repository

```bash
git clone https://github.com/your-username/smashlink.git
cd smashlink
```

### 2. Create MySQL database

```sql
CREATE DATABASE smashlink_db;
```

### 3. Configure `application.properties`

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/smashlink_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

# JWT
jwt.secret=your_64_character_secret_key_here
jwt.expiry.ms=3600000

# Admin Registration Secret
admin.registration.secret=your_admin_secret

# Gmail SMTP
spring.mail.username=your_gmail@gmail.com
spring.mail.password=your_16_char_app_password
```

### 4. Start Redis locally

```bash
# Via WSL (Windows)
sudo service redis-server start

# Verify
redis-cli ping  # should return PONG
```

### 5. Start Zipkin

```bash
curl -sSL https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar
```

Zipkin UI: `http://localhost:9411`

### 6. Run the application

```bash
mvn spring-boot:run
```

App runs at: `http://localhost:8080`

---

## 🔑 Gmail App Password Setup

1. Go to [myaccount.google.com](https://myaccount.google.com)
2. Search **"App Passwords"**
3. Create one for **Mail**
4. Paste the 16-character password in `spring.mail.password`

---

## 📡 API Reference

### Auth

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/login` | Public | Login and get JWT token |
| POST | `/api/auth/admin/register` | Public + Secret | Register an admin account |

### Users

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/users/register` | Public | Register a new user |

### URLs

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/urls/shorten` | User | Shorten a URL |
| GET | `/api/urls/my-urls` | User | Get all URLs (paginated) |
| GET | `/api/urls/{shortCode}/stats` | Owner / Admin | Get URL stats |
| DELETE | `/api/urls/{shortCode}` | Owner / Admin | Delete a short URL |
| GET | `/redirect/{shortCode}` | Public | Redirect to original URL |

### Admin

| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/admin/users` | Admin | Get all users (paginated) |
| DELETE | `/api/admin/users/{id}` | Admin | Delete a user |
| PATCH | `/api/admin/users/{id}/status` | Admin | Deactivate or lock a user |
| GET | `/api/admin/users/{id}/urls` | Admin | View all URLs of a user |

---

## 🔐 Authentication

All protected endpoints require a JWT token in the `Authorization` header:

```
Authorization: Bearer <your_token>
```

Get a token by calling `POST /api/auth/login`.

---

## 📧 Email Notifications

SmashLink sends email alerts using the **Observer Design Pattern**:

| Event | Trigger |
|---|---|
| Hit Limit Nearing | When 80% of hit limit is reached |
| Hit Limit Exhausted | When hit count reaches the limit |
| URL Expiry Nearing | When URL expires within 24 hours |
| URL Expired | When an expired URL is accessed |

The 80% threshold is configurable:
```properties
notification.hit.limit.threshold=80
```

---

## ⚡ Redis Caching

Short URL lookups are cached in Redis with a 30-minute TTL:

```
Request → Check Redis
            ├── HIT  → return cached URL + update hit count in DB
            └── MISS → query DB → store in Redis → return URL
```

Cache is evicted automatically on URL deletion.

---

## 🚦 Rate Limiting

Powered by **Bucket4j** — 10 requests per minute per IP and per user:

| Endpoint | Rate Limited |
|---|---|
| `POST /api/auth/login` | ✅ |
| `POST /api/users/register` | ✅ |
| `POST /api/urls/shorten` | ✅ |
| `GET /redirect/{shortCode}` | ✅ |

On breach, returns `429 Too Many Requests` with `Retry-After` header.

---

## 🔍 Distributed Tracing

Every log line includes `traceId` and `spanId`:

```
2026-03-21 10:00:01 [traceId=3f2a1b4c] [spanId=7d8e9f0a] INFO  UrlServiceImpl - Cache HIT — shortCode: aaaaaab
```

View full request traces at `http://localhost:9411` (Zipkin UI).

---

## 🏗️ Design Patterns Used

**Observer Pattern** — Email notification system. `UrlEventPublisher` acts as the subject and notifies all registered `UrlEventObserver` implementations. Adding new notification channels (SMS, Slack) requires zero changes to existing code — just implement `UrlEventObserver` and annotate with `@Component`.

**Repository Pattern** — Clean separation between data access and business logic via Spring Data JPA repositories.

**DTO Pattern** — Request and response DTOs ensure the entity layer is never exposed directly to the API layer.

---

## 📦 Build

```bash
# Run tests
mvn test

# Package
mvn clean package

# Run JAR
java -jar target/smashlink-0.0.1-SNAPSHOT.jar
```

---

## 📌 Project Phases

| Phase | Feature |
|---|---|
| 1 | User Registration (Entity, Repository, Service, Controller) |
| 2 | JWT Authentication + Spring Security |
| 3 | Admin Management + Secured Admin Registration |
| 4 | URL Shortening Core + Base62 Encoding |
| 5 | Email Notifications with Observer Pattern |
| 6 | Redis Caching |
| 7 | API Rate Limiting with Bucket4j |
| 8 | SLF4J Logging + Zipkin Distributed Tracing |

---
