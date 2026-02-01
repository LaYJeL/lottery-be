# AGENTS.md - Lottery and Raffle Service Context

## 1. Project Overview and Business Logic

**Goal:** To create a reliable service where users complete tasks in order to participate in lotteries (where they can win money) or sweepstakes (where they can win prizes). Participation in sweepstakes or lotteries can be purchased.

### Main Domain Models
* **User:** A registered participant who has passed identity verification.
* **UserProfile:** User statistics including reputation, level, balance, and competition history.
* **Task:** An action (e.g., "Buy a product," "Subscribe to a channel," etc.) for which the user receives a reward in the form of profile reputation or a ticket to participate in a competition. The higher the reputation, the better the competitions the user can participate in.
* **Ticket/Participation:** Proof of participation in a specific drawing or lottery. Can be earned through tasks or purchased with real money.
* **Competition (Giveaway/Lottery):** An event with a specific start/end time, prize pool, and winning criteria.
* **Wallet:** User's financial account for deposits, withdrawals, and competition entry fees.
* **Transaction:** Record of all financial operations (deposits, withdrawals, ticket purchases).
* **Prize:** Reward (physical item, digital currency, etc.).

### Critical Business Rules ("Red Lines")
1. **Fairness:** The logic of selecting the winner must be proven random and protected from fraud, and must be accessible to all users.
2. **Integrity:** A user cannot participate more times than the number of tickets they have.
3. **Atomicity:** Prevention of "double spending" of tickets or duplication of rewards for tasks under high load.
4. **Audit Trail:** All financial transactions and winner selections must be logged and auditable.
5. **Balance Consistency:** User balance must never go negative; all balance operations must be atomic.

---

## 2. Technology Stack and Architecture

### Core Stack
* **Language:** Java 21+ (use Records for DTOs where appropriate)
* **Framework:** Spring Boot 3.x
* **Database:** PostgreSQL 16+ (for data integrity and ACID compliance)
* **User Management:** Keycloak (OAuth2/JWT authentication)
* **ORM:** Hibernate 6 with JPA specifications
* **Security:** Spring Security with OAuth2 Resource Server
* **Database Migrations:** Liquibase
* **Caching:** Caffeine (in-memory cache)
* **API Documentation:** SpringDoc OpenAPI (Swagger UI)
* **Testing:** JUnit 5, Mockito, Testcontainers

### Architectural Principles
* **Layered Architecture:** Controller → Service → Repository
* **Domain-Driven Design:** Organize code around business domains
* **Event-Driven:** Use Spring Events for cross-cutting concerns (gamification, notifications)

### Project Structure
```
src/main/java/com/game/lottery/
├── config/          # Configuration classes (@Configuration, @ConfigurationProperties)
├── controller/      # REST controllers (thin layer, validation only)
├── dto/             # Data Transfer Objects (API contracts)
├── enums/           # Business enumerations
├── event/           # Domain events and listeners
├── exception/       # Custom exceptions and GlobalExceptionHandler
├── mapper/          # MapStruct mapper interfaces
├── model/           # JPA entities
├── repository/      # Spring Data JPA repositories
├── security/        # Security filters and converters
└── service/         # Business logic layer
    └── gamification/  # Extracted helper classes for complex logic
```

---

## 3. Coding Standards and Guidelines

### 3.1 General Principles
* **KISS:** Keep implementations simple; avoid over-engineering
* **SOLID:** Follow Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
* **DRY:** Extract common logic into reusable components
* **Clean Code:** Meaningful names, small methods, no magic numbers

### 3.2 API Design
* **REST Conventions:** Use proper HTTP methods (GET, POST, PUT, DELETE)
* **Versioning:** Prefix all endpoints with `/api/v1/`
* **Pagination:** Use Spring's `Pageable` for list endpoints
* **Response Format:** Consistent error responses using `ErrorResponse` DTO
* **Validation:** Use Jakarta Bean Validation (`@Valid`, `@NotNull`, `@Min`, etc.) at controller level

### 3.3 Exception Handling (CRITICAL)
* **Never throw generic `RuntimeException`** - always use domain-specific exceptions
* **Exception Hierarchy:**
  - `*NotFoundException` → 404 (e.g., `UserNotFoundException`, `CompetitionNotFoundException`)
  - `*NotActiveException` → 400 (e.g., `CompetitionNotActiveException`)
  - `*AlreadyExistsException` → 409 (e.g., `AlreadyJoinedException`)
  - `InsufficientFundsException` → 400
  - `IdentityProviderException` → 503
* **GlobalExceptionHandler:** All exceptions must be handled and return proper `ErrorResponse`

### 3.4 Database and Transactions
* **Migrations:** Always use Liquibase for schema changes; never use `ddl-auto: update` in production
* **Transactions:**
  - Use `@Transactional` for all service methods that modify data
  - Use `@Transactional(readOnly = true)` for read-only operations
  - Keep transactions short; avoid calling external services inside transactions
* **Locking:**
  - Use `@Version` for optimistic locking on entities with concurrent updates
  - Use `@Lock(LockModeType.PESSIMISTIC_WRITE)` for critical balance operations
* **N+1 Prevention:**
  - Use `@Query` with JOINs for complex queries
  - Prefer fetching IDs and then batch loading over lazy loading in loops
* **Indexing:** Add indexes for frequently queried columns (foreign keys, status fields)

### 3.5 Entity and DTO Mapping
* **Use MapStruct** for all entity-to-DTO conversions
* **Never expose entities directly** in API responses
* **Mapper Location:** `src/main/java/com/game/lottery/mapper/`
* **Naming Convention:** `EntityNameMapper` (e.g., `TaskMapper`, `CompetitionMapper`)

### 3.6 Security and Randomness (CRITICAL)
* **Randomness:** NEVER use `java.util.Random` for lottery logic. ALWAYS use `java.security.SecureRandom`
* **Authentication:** All endpoints except `/api/public/**` and `/swagger-ui/**` require JWT
* **Authorization:** Use `@PreAuthorize("hasRole('ADMIN')")` for admin-only endpoints
* **Secrets:** Never commit secrets to repository; use environment variables
* **Input Validation:** Validate all input at controller level; reject negative amounts, invalid UUIDs

### 3.7 Configuration Management
* **Externalize Configuration:** Use `@ConfigurationProperties` for grouped config
* **Environment Variables:** Support overriding via env vars (e.g., `${DB_PASSWORD:default}`)
* **Profiles:** Use `application-{profile}.yml` for environment-specific config
* **Sensitive Data:** Never log passwords, tokens, or PII

### 3.8 Logging and Observability
* **Logging Framework:** SLF4J with `@Slf4j` annotation
* **Log Levels:**
  - `ERROR`: Exceptions and failures requiring attention
  - `WARN`: Recoverable issues (e.g., invalid verification attempt)
  - `INFO`: Important business events (user registration, competition join)
  - `DEBUG`: Detailed flow for troubleshooting
* **Structured Logging:** Include userId, competitionId, transactionId in log context
* **Health Checks:** Expose `/actuator/health` for monitoring

### 3.9 Testing Standards
* **Coverage Requirements:**
  - Unit tests for all service methods
  - Integration tests for repository queries
  - Controller tests for validation and authorization
* **Test Naming:** `methodName_shouldExpectedBehavior_whenCondition`
* **Mocking:** Use `@Mock` and `@InjectMocks` for unit tests
* **Test Data:** Use builders and factories, not hardcoded values
* **Testcontainers:** Use for integration tests requiring PostgreSQL

### 3.10 Money and Currency Handling (CRITICAL)
* **Always use `BigDecimal`** for monetary values - NEVER use `double` or `float`
* **Precision:** Use `BigDecimal` with scale of 2 for currency (e.g., `setScale(2, RoundingMode.HALF_UP)`)
* **Comparisons:** Use `compareTo()` instead of `equals()` (BigDecimal equals considers scale)
* **Storage:** Store as `DECIMAL(19,2)` in PostgreSQL
* **Calculations:**
  - Always specify `MathContext` or `RoundingMode` in division operations
  - Avoid intermediate rounding; round only final results
* **Display:** Format for display at the presentation layer, not in business logic

### 3.11 Idempotency (CRITICAL)
* **All financial operations MUST be idempotent** - retrying the same request must not cause duplicate effects
* **Idempotency Keys:**
  - Accept `X-Idempotency-Key` header for POST/PUT requests that modify state
  - Store idempotency keys with TTL (e.g., 24 hours) in cache or database
  - Return cached response for duplicate requests
* **Transaction References:**
  - Generate unique `transactionId` before processing
  - Check for existing transaction before creating new one
* **Competition Entry:** Check `CompetitionEntry` existence before creating (already implemented via unique constraint)

### 3.12 Rate Limiting
* **Why:** Prevent abuse, bot attacks, and ensure fair access
* **Implementation:** Use bucket4j or Spring Cloud Gateway rate limiting
* **Limits by Endpoint Type:**
  - Authentication endpoints: 5 requests/minute per IP
  - Financial operations (deposit/withdraw): 10 requests/minute per user
  - Competition entry: 30 requests/minute per user
  - Read operations: 100 requests/minute per user
* **Response:** Return `429 Too Many Requests` with `Retry-After` header

### 3.13 Date, Time, and Timezone Handling
* **Storage:** Always store timestamps as `TIMESTAMP WITH TIME ZONE` (PostgreSQL) / `Instant` (Java)
* **Internal Processing:** Use `Instant` or `ZonedDateTime` with explicit timezone
* **User Display:** Convert to user's timezone only at presentation layer
* **Competition Scheduling:**
  - Store `startTime` and `endTime` as `Instant`
  - Store timezone separately if user-specific display needed
* **Avoid:** `LocalDateTime` for timestamps that cross timezone boundaries
* **Daily/Weekly Tasks:** Use `ZonedDateTime` with user's timezone for cycle calculations

### 3.14 Scheduled Jobs and Draw Execution
* **Framework:** Use Spring `@Scheduled` with `@SchedulerLock` (ShedLock) for distributed environments
* **Draw Execution:**
  - Schedule job to check for competitions past `endTime`
  - Execute winner selection in isolated transaction
  - Log all randomness inputs and outputs for audit
* **Idempotent Jobs:** Jobs must be safe to run multiple times (check state before processing)
* **Error Handling:** Log failures, send alerts, but don't throw exceptions that break scheduler
* **Monitoring:** Expose metrics for job execution (last run time, success/failure count)

### 3.15 Caching Strategy
* **Framework:** Caffeine for in-memory caching
* **What to Cache:**
  - Static/rarely changing data (task definitions, competition rules)
  - User profile data (with short TTL, e.g., 5 minutes)
  - Competition listings (with short TTL)
* **What NOT to Cache:**
  - Wallet balances (always read from DB with lock)
  - Competition entry counts (consistency critical)
  - Anything affecting lottery fairness
* **Cache Invalidation:** Prefer TTL-based expiration; explicit invalidation for admin updates
* **Annotations:** Use `@Cacheable`, `@CacheEvict`, `@CachePut`

### 3.16 External Service Integration
* **Retry Policy:**
  - Use exponential backoff with jitter
  - Max 3 retries for transient failures
  - No retry for 4xx client errors
* **Circuit Breaker:** Use Resilience4j for external calls (payment gateways, Keycloak)
* **Timeouts:** Always set connection and read timeouts (e.g., 5s connect, 30s read)
* **Fallback:** Define graceful degradation (e.g., queue for later if payment gateway down)
* **Never call external services inside database transactions**

### 3.17 Audit and Compliance
* **Audit Trail Requirements:**
  - Log all balance changes with before/after values
  - Log all competition entries with timestamp and ticket source
  - Log winner selection with random seed and algorithm used
  - Retain audit logs for minimum 7 years (regulatory requirement)
* **Soft Delete:** Use `deletedAt` timestamp instead of hard delete for:
  - Users (legal requirements)
  - Transactions (financial audit)
  - Competition entries (dispute resolution)
* **Data Retention:** Define and enforce retention policies per entity type
* **GDPR:** Support data export and anonymization (not deletion) for user requests

---

## 4. Workflow Guidelines

### 4.1 When Implementing a New Feature
1. **Understand Requirements:** Clarify business rules and edge cases
2. **Database First:** Create Liquibase migration if schema changes needed
3. **Entity Layer:** Create or update JPA entities
4. **Repository Layer:** Add repository methods with proper queries
5. **Service Layer:** Implement business logic with proper transactions
6. **DTO and Mapper:** Create DTOs and MapStruct mappers
7. **Controller Layer:** Expose REST endpoints with validation
8. **Exception Handling:** Create custom exceptions if needed
9. **Testing:** Write unit and integration tests
10. **Verification:** Does this affect fairness or financial integrity?

### 4.2 When Refactoring
1. **Tests First:** Ensure existing tests pass before changes
2. **Small Steps:** Make incremental changes, commit frequently
3. **No Logic Changes:** Do not change lottery/random logic without explicit approval
4. **Backward Compatibility:** Consider API consumers when changing contracts
5. **Update Tests:** Add tests for new code paths

### 4.3 When Fixing Bugs
1. **Reproduce:** Write a failing test that demonstrates the bug
2. **Fix:** Make minimal changes to fix the issue
3. **Verify:** Ensure the test passes and no regressions occur
4. **Root Cause:** Document why the bug occurred

---

## 5. Code Review Checklist

Before submitting code for review, verify:

**Exception & Error Handling:**
- [ ] No `RuntimeException` - using domain-specific exceptions
- [ ] No `java.util.Random` - using `SecureRandom` for any randomness

**Data & Transactions:**
- [ ] Transactions are properly scoped (`@Transactional`)
- [ ] Money values use `BigDecimal`, never `double`/`float`
- [ ] Balance operations use pessimistic locking
- [ ] No external service calls inside transactions

**API & Validation:**
- [ ] Input validation present at controller level
- [ ] DTOs used for API responses (not entities)
- [ ] MapStruct mappers for conversions
- [ ] Financial endpoints support idempotency keys

**Code Quality:**
- [ ] Unit tests written and passing
- [ ] No hardcoded secrets or credentials
- [ ] Logging added for important operations
- [ ] Audit logging for financial/lottery operations
- [ ] Database queries optimized (no N+1)
- [ ] Configuration externalized (no magic numbers)

**Time & Scheduling:**
- [ ] Timestamps stored as `Instant`, not `LocalDateTime`
- [ ] Timezone explicitly handled for user-facing dates

---

## 6. Environment Setup

### Development Credentials
> **Note:** These are for local development only. Production uses secure vault.

- **Keycloak Admin:** Use environment variables or `.env` file
- **Database:** See `.env.example` for required variables

### Running Locally
```bash
# Start infrastructure (PostgreSQL, Keycloak)
docker-compose up -d

# Run the application
./gradlew bootRun

# Access Swagger UI
open http://localhost:8088/swagger-ui.html
```

### Running Tests
```bash
# Unit tests
./gradlew test

# E2E tests (requires running app)
./gradlew e2eTest
```

---

## 7. Common Pitfalls to Avoid

| Pitfall | Correct Approach |
|---------|------------------|
| Using `Random` for lottery | Use `SecureRandom` |
| Throwing `RuntimeException` | Create domain-specific exception |
| Returning entity from controller | Return DTO via MapStruct |
| Manual DTO mapping in service | Use MapStruct mapper interface |
| Balance update without lock | Use pessimistic or optimistic locking |
| Hardcoded configuration | Use `@ConfigurationProperties` |
| N+1 queries in loops | Use batch queries or JOINs |
| Generic catch blocks | Catch specific exceptions |
| Logging sensitive data | Mask or omit PII/secrets |
| Skipping input validation | Use Bean Validation annotations |
| Using `double` for money | Use `BigDecimal` with proper scale |
| `LocalDateTime` for timestamps | Use `Instant` for storage, convert for display |
| External calls in transaction | Move external calls outside `@Transactional` |
| Retry without idempotency | Implement idempotency keys for mutations |
| Hard delete financial records | Use soft delete with `deletedAt` timestamp |
| Caching wallet balances | Always read balance from DB with lock |
| No timeout on HTTP clients | Set connect/read timeouts, use circuit breaker |
| `BigDecimal.equals()` for comparison | Use `compareTo() == 0` (equals checks scale) |

---

## 8. Glossary

| Term | Definition |
|------|------------|
| Competition | A lottery or giveaway event users can enter |
| Entry | A user's participation in a competition |
| Task | An action users complete for rewards |
| Reputation | Points earned from completing tasks |
| Level | User tier based on total reputation |
| Wallet | User's financial account within the system |
| Entry Fee | Amount charged to join a competition |
| Idempotency Key | Unique identifier ensuring an operation executes only once |
| Soft Delete | Marking record as deleted without physical removal |
| Circuit Breaker | Pattern preventing cascading failures from external services |
| Draw | The process of randomly selecting winner(s) from entries |
| Audit Trail | Immutable record of all significant system actions |
| SecureRandom | Cryptographically strong random number generator |
| Pessimistic Lock | Database lock acquired before reading, preventing concurrent modifications |
| Optimistic Lock | Version-based concurrency control, fails on conflict |
