# Lottery & Raffle Service

## 1. Project Overview & Business Logic

**Goal:** Build a robust service where users complete tasks to earn/buy participation in lottery draws or receive a prize.

### Core Domain Models
* **User:** A registered participant, that has passed person validation.
* **Task:** An activity (e.g., "Buy product", "Subscribe on channel", etc) that rewards the user.
* **Ticket/Entry:** A proof of participation in a specific Draw. Can be earned via Tasks or purchased.
* **Draw (Lottery):** An event with a specific start/end time, prize pool, and winning criteria.
* **Prize:** The reward (physical item, digital currency, etc.).

### Critical Business Rules
1.  **Fairness:** The winner selection logic must be demonstrably random and tamper-proof.
2.  **Integrity:** A user cannot participate more times than their tickets allow.
3.  **Concurrency:** Prevent "double-spending" of tickets or duplicate task rewards under high load.

## 2. Tech Stack & Architecture

* **Backend:** Java 21+, Spring Boot 3.x.
* **Database:** PostgreSQL.
* **User-Management:** Keycloak.
* **ORM:** Hibernate 6 (JPA).
* **Security:** Spring Security (OAuth2/JWT).
* **Architecture Pattern:** Layered Architecture (Controller -> Service -> Repository).
* **Testing:** JUnit 5, Mockito, and Testcontainers.

## 3. Setup & Running

### Prerequisites
- Java 21+
- Docker & Docker Compose

### Running the Application
1.  Start the infrastructure (PostgreSQL, Keycloak, etc.):
    ```bash
    docker-compose up -d
    ```
2.  Build and run the application:
    ```bash
    ./gradlew bootRun
    ```

### Running Tests
```bash
./gradlew test
```

## 4. Key Directives
- **Security:** `java.util.Random` is strictly forbidden for game logic. `java.security.SecureRandom` MUST be used.
- **Transactions:** Services modifying balances or tickets must use `@Transactional`.
- **Validation:** All inputs must be validated at the Controller level.
