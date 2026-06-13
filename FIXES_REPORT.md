# lottery-be — Звіт про виправлення та аналіз

**Дата:** 2026-06-13 · **Гілка:** main · **Модель:** Claude Fable 5
**Стек:** Java 21, Spring Boot 3, PostgreSQL, Keycloak, Hibernate, Liquibase.

> Усі зміни закомічено **і запушено** на `main`.

## Оточення (важливо для завтра)

- На машині не було JDK 21 (лише Android Studio JBR 17). Поставив **Temurin JDK 21**
  у `~/Software/jdk21` (без root). Запуск тестів:
  ```bash
  export JAVA_HOME=~/Software/android-dev/jdk
  ./gradlew test -Porg.gradle.java.installations.paths=$HOME/Software/jdk21
  ```
- **Базлайн тестів на свіжому клоні: 38 виконано, 11 впало.** З них **10 —
  інфраструктурні**: `@SpringBootTest`/`@DataJpaTest`/Testcontainers, яким
  потрібні **Docker + PostgreSQL** (недоступні в цьому середовищі) — падають на
  `Failed to load ApplicationContext` / `Previous attempts to find a Docker
  environment failed`. Це **не баги коду**, а відсутнє локальне оточення. Щоб їх
  ганяти — `docker-compose up -d` перед `./gradlew test`.
- Після виправлень: **38 виконано, 10 впало** (всі 10 — ті самі інфраструктурні).

## Виправлені баги

### Фінансовий — Подвійна витрата при `withdraw` (КРИТИЧНЕ)
**Файл:** `service/WalletService.java`.
`withdraw()` читав гаманець через неблокований `getOrCreateWallet()` →
`findByUserId`, потім перевіряв баланс і віднімав. Два паралельні зняття (або
зняття + вхід у конкурс) могли **обидва** пройти перевірку балансу й **обидва**
списати → lost update, **баланс у мінус / подвійна витрата**. Порушує власні
«червоні лінії» проекту **#3 Atomicity** і **#5 Balance Consistency**.
**Фікс:** `withdraw` читає гаманець під песимістичним локом
`findByUserIdWithLock` (PESSIMISTIC_WRITE) — точно як уже робить
`spendForCompetition`. Unit-тести оновлено (стаб лок-методу + перевірка, що лок
узято). **Верифіковано:** `WalletServiceTest` зелений.

### Тест — застарілий стаб у `CompetitionServiceTest` (СЕРЕДНЄ)
**Файл:** `test/.../CompetitionServiceTest.java`.
`getActiveCompetitions` після рефакторингу резолвить «entered» через батчевий
`findEnteredCompetitionIds` (фікс N+1), а тест стабав застарілий
`findByUser_UserId` → Mockito strict-stubbing валив тест
(`UnnecessaryStubbingException`). **Фікс:** стаб реально вживаного методу.
**Верифіковано:** клас зелений (1 з 11 падінь усунуто).

## Що перевірено й виглядає добре (відповідає red-lines)

- **Жодного `java.util.Random`** у логіці — `SecureRandom` у верифікації. ✓
- **Жодного `double`/`float`** для грошей — скрізь `BigDecimal`. ✓
- Порівняння балансу через `compareTo`, не `equals`. ✓
- `spendForCompetition` уже бере песимістичний лок. ✓
- `getOrCreateWallet` обробляє race створення гаманця (`DataIntegrityViolation`). ✓

## Ідеї для подальшого (потребують перевірки в Docker-оточенні)

1. **Idempotency-ключі для фінансових операцій** (CLAUDE.md 3.11) — `deposit`/
   `withdraw`/entry поки без `X-Idempotency-Key`; ретрай = дубль транзакції.
2. **`deposit` без локу** — менш критично (тільки створює PROCESSING-транзакцію,
   не змінює баланс), але варто звірити повний flow підтвердження депозиту.
3. **Rate limiting** (3.12) — на auth/фінансових ендпойнтах поки не видно.
4. **Інтеграційні тести в CI** — підняти Testcontainers, щоб 10 «червоних»
   стали зеленими й покривали саме конкурентність балансу (це найкраще місце
   довести фікс withdraw під реальним навантаженням).
5. **Аудит `@Transactional`-меж** — переконатися, що зовнішні виклики не
   всередині транзакцій (CLAUDE.md 3.16).

> Свідомо НЕ чіпав логіку розіграшу/випадковості (CLAUDE.md 4.2: без явного
> дозволу) і не робив неверифіковних змін фінансової логіки без можливості
> прогнати інтеграційні тести.
