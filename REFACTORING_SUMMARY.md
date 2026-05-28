# Lottery-BE Refactoring Summary

## Overview

This document summarizes the refactoring performed on the lottery-be Spring Boot application. The refactoring focused on improving code quality, maintainability, and addressing potential issues.

**Date:** 2026-02-01
**Scope:** Critical + High Priority Issues

---

## 1. Exception Handling Standardization

### Problem
21+ instances of generic `RuntimeException` being thrown instead of domain-specific exceptions, leading to inconsistent error responses and difficult client-side error handling.

### Solution
Created custom exception classes and updated `GlobalExceptionHandler` with appropriate HTTP status codes.

### New Exception Classes

| Exception | HTTP Status | Use Case |
|-----------|-------------|----------|
| `TaskNotFoundException` | 404 | Task not found in admin operations |
| `TaskProgressNotFoundException` | 404 | User's task progress not found |
| `TaskNotClaimableException` | 400 | Task not completed or already claimed |
| `IdentityProviderException` | 503 | Keycloak/identity provider errors |
| `WalletNotFoundException` | 404 | Wallet creation/lookup failures |

### Files Modified
- `src/main/java/com/game/lottery/exception/GlobalExceptionHandler.java`
- `src/main/java/com/game/lottery/service/GamificationService.java`
- `src/main/java/com/game/lottery/service/WalletService.java`
- `src/main/java/com/game/lottery/service/CompetitionService.java`
- `src/main/java/com/game/lottery/service/UserManagementService.java`
- `src/main/java/com/game/lottery/controller/AdminTaskController.java`

---

## 2. MapStruct Integration

### Problem
Repetitive manual DTO-to-Entity mapping code scattered across 4+ locations (~100+ lines of duplicated code).

### Solution
Integrated MapStruct library for compile-time generated mappers.

### New Mapper Interfaces

| Mapper | Purpose |
|--------|---------|
| `TaskMapper` | Task <-> TaskDto (bidirectional for CRUD) |
| `TransactionMapper` | Transaction -> TransactionDto (with Instant to LocalDateTime conversion) |
| `CompetitionMapper` | Competition -> CompetitionDto (with isEntered parameter) |
| `CompetitionEntryMapper` | CompetitionEntry -> CompetitionEntryDto (nested object mapping) |
| `PaymentMethodMapper` | PaymentMethod -> PaymentMethodDto |

### Files Modified
- `build.gradle.kts` - Added MapStruct dependencies
- `src/main/java/com/game/lottery/controller/AdminTaskController.java`
- `src/main/java/com/game/lottery/service/WalletService.java`
- `src/main/java/com/game/lottery/service/CompetitionService.java`

### Dependencies Added
```kotlin
val mapstructVersion = "1.5.5.Final"
implementation("org.mapstruct:mapstruct:$mapstructVersion")
annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
```

---

## 3. WalletService Race Condition Fix

### Problem
`spendForCompetition()` had non-atomic balance updates vulnerable to race conditions under high concurrency.

### Solution
Implemented pessimistic locking for balance-modifying operations.

### Changes
1. Added pessimistic lock query to `WalletRepository`:
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT w FROM Wallet w WHERE w.user.userId = :userId")
Optional<Wallet> findByUserIdWithLock(@Param("userId") UUID userId);
```

2. Updated `spendForCompetition()` to use the locked query

### Files Modified
- `src/main/java/com/game/lottery/repository/WalletRepository.java`
- `src/main/java/com/game/lottery/service/WalletService.java`

---

## 4. GamificationService Breakdown

### Problem
GamificationService was 353 lines with multiple responsibilities, making it hard to test and maintain.

### Solution
Extracted helper classes following Single Responsibility Principle.

### New Helper Classes

```
src/main/java/com/game/lottery/service/gamification/
├── LevelProgressCalculator.java  - XP calculations, leveling math, tier determination
├── ConditionEvaluator.java       - Task condition parsing (TIME:, ROLE:, BIRTHDAY)
└── TaskCycleManager.java         - Cycle reset logic (DAILY, WEEKLY, ONE_TIME)
```

### Result
- GamificationService reduced from ~350 lines to ~150 lines
- Each helper class has a single, testable responsibility
- Easier to modify game balance without touching core logic

---

## 5. N+1 Query Fix in CompetitionService

### Problem
`getActiveCompetitions()` fetched ALL user's competition entries, then looped to check matches against paginated competitions.

### Solution
Added optimized repository method that queries only for entries in the current page's competitions.

### Changes
1. Added to `CompetitionEntryRepository`:
```java
@Query("SELECT e.competition.id FROM CompetitionEntry e
        WHERE e.user.userId = :userId AND e.competition.id IN :competitionIds")
List<UUID> findEnteredCompetitionIds(@Param("userId") UUID userId,
                                      @Param("competitionIds") List<UUID> competitionIds);
```

2. Updated `getActiveCompetitions()` to:
   - Fetch paginated competitions
   - Extract competition IDs
   - Single query for user's entries in those specific competitions

### Files Modified
- `src/main/java/com/game/lottery/repository/CompetitionEntryRepository.java`
- `src/main/java/com/game/lottery/service/CompetitionService.java`

---

## 6. Configuration Externalization

### Problem
Game balance values and environment-specific settings were hardcoded in services.

### Solution
Created `@ConfigurationProperties` classes and moved values to `application.yml`.

### New Configuration Classes

**GameBalanceConfig** (`game.balance.*`)
- `leveling-exponent`: 1.5
- `base-xp`: 100
- `max-level`: 99
- `verification-code-expiry-minutes`: 5
- `max-verification-attempts`: 3

**CorsConfig** (`app.cors.*`)
- `allowed-origins`: configurable via `CORS_ALLOWED_ORIGIN` env var
- `allowed-methods`: GET, POST, PUT, DELETE, OPTIONS
- `allowed-headers`: Authorization, Content-Type, X-Requested-With

### Files Modified
- `src/main/resources/application.yml`
- `src/main/java/com/game/lottery/config/SecurityConfig.java`
- `src/main/java/com/game/lottery/service/VerificationCodeService.java`
- `src/main/java/com/game/lottery/service/gamification/LevelProgressCalculator.java`

---

## 7. Security Cleanup

### Problem
`.env` file with database and Keycloak credentials was potentially committed to repository.

### Solution
- Added `.env` to `.gitignore`
- Created `.env.example` with placeholder values for documentation

### Files Modified
- `.gitignore`
- `.env.example` (new file)

---

## Files Summary

### New Files Created (12)

**Exceptions:**
- `src/main/java/com/game/lottery/exception/TaskNotFoundException.java`
- `src/main/java/com/game/lottery/exception/TaskProgressNotFoundException.java`
- `src/main/java/com/game/lottery/exception/TaskNotClaimableException.java`
- `src/main/java/com/game/lottery/exception/IdentityProviderException.java`
- `src/main/java/com/game/lottery/exception/WalletNotFoundException.java`

**Mappers:**
- `src/main/java/com/game/lottery/mapper/TaskMapper.java`
- `src/main/java/com/game/lottery/mapper/TransactionMapper.java`
- `src/main/java/com/game/lottery/mapper/CompetitionMapper.java`
- `src/main/java/com/game/lottery/mapper/CompetitionEntryMapper.java`
- `src/main/java/com/game/lottery/mapper/PaymentMethodMapper.java`

**Gamification Helpers:**
- `src/main/java/com/game/lottery/service/gamification/LevelProgressCalculator.java`
- `src/main/java/com/game/lottery/service/gamification/ConditionEvaluator.java`
- `src/main/java/com/game/lottery/service/gamification/TaskCycleManager.java`

**Configuration:**
- `src/main/java/com/game/lottery/config/GameBalanceConfig.java`
- `src/main/java/com/game/lottery/config/CorsConfig.java`
- `.env.example`

### Files Modified (15)

- `build.gradle.kts`
- `application.yml`
- `.gitignore`
- `GlobalExceptionHandler.java`
- `WalletRepository.java`
- `CompetitionEntryRepository.java`
- `WalletService.java`
- `GamificationService.java`
- `CompetitionService.java`
- `VerificationCodeService.java`
- `UserManagementService.java`
- `AdminTaskController.java`
- `SecurityConfig.java`
- `WalletServiceTest.java`
- `VerificationCodeServiceTest.java`
- `GamificationServiceTest.java`

---

## Verification

### Build
```bash
./gradlew build -x test
# BUILD SUCCESSFUL
```

### Tests
```bash
./gradlew test --tests "com.game.lottery.service.*"
# Unit tests pass (integration tests require database setup)
```

### Manual Testing
1. Start application: `./gradlew bootRun`
2. Verify Swagger UI: `http://localhost:8088/swagger-ui.html`
3. Test key endpoints (user profile, wallet operations, competitions)

---

## Future Recommendations

1. **Add integration tests** for the new mapper classes
2. **Complete ROLE: condition** implementation in `ConditionEvaluator`
3. **Add @Version optimistic locking** tests for concurrent wallet operations
4. **Consider adding** retry mechanism for `IdentityProviderException`
5. **Remove `.env`** from git history using `git filter-branch` or BFG Repo-Cleaner
