# Java KeyGuard Backend API

The ingestion and management gateway for the KeyGuard system. Built with Spring Boot, PostgreSQL, and JWT-based authentication.

## 1. Security Architecture
This API implements several "Secure-by-Design" features to protect the research data:
* **Hashed Persistence**: Analyst passwords are saved using **BCrypt** hashing with automatic salting.
* **Brute-Force Protection**: A 5-attempt threshold triggers a 15-minute account lockout.
* **Payload Integrity**: Verifies **HMAC-SHA256 signatures** and **AES-256** encrypted data from agents.
* **Anti-Replay**: Requests are validated against a 60-second "freshness" window using signed timestamps.

## 2. Setup & Installation
1. **Database**: Configure your PostgreSQL credentials in `src/main/resources/application.properties`.
2. **Initial Admin**: On first run, a `CommandLineRunner` will automatically seed a default admin user.
3. **Test Credentials**:
    * **Username**: `admin`
    * **Password**: `supersecretpassword`
4. **Environment Variables**:
    * `JWT_SECRET`: Secret string for signing authentication tokens.

## 3. Connecting an Agent
To authorize a new agent, add an entry to the `agents` table in the database. You must generate and provide:
1. A unique `agent_id` (UUID).
2. A random `hmac_secret`.
3. A random 32-character `aes_key`.

*Note: These three values must be copied exactly into the Agent's application.properties file.*

## 4. API Endpoints
* `POST /auth/login`: Authenticates analysts; returns a JWT.
* `POST /logs/upload`: Secure ingestion point for encrypted agent logs.
* `GET /logs`: Paginated log viewing for authorized analysts (JWT required).