# AGENTS.md — BartExchangeAI

REST API бартерной биржи: обмены, группы, офферы, отзывы, рекомендации через Spring AI.
Java 21, Spring Boot 3.5.15, Gradle, пакет `com.example.bartexchangeai`.
Spring Framework намеренно на 6.2.19: в ветке 6.2 нет фикса для двух CVE (XsltView, SSE), по коду они не достижимы. Не тянуть Spring 7 и Boot 4 «заодно».
Проект портфолио-уровня: не тащить сюда enterprise-инфраструктуру без прямой просьбы.

## Команды

- `./gradlew build` — компиляция и все тесты. CI (`.github/workflows/ci.yml`) гоняет то же самое.
- Первый прогон на чистой машине ~7 минут (Gradle 8.8 и зависимости качаются), повторный — секунды. Долгий `build` — не повод считать его зависшим.
- `./gradlew test` — только тесты.
- `./gradlew dependencyCheckAnalyze` — OWASP-проверка зависимостей, сборка падает при CVSS ≥ 9.
- Актуальное состояние (25.11.2026): задача падает на обновлении базы NVD (`Value too long for column URL ... CVE-2026-6786`), это баг плагина и данных, а не проекта. Без `NVD_API_KEY` в CI обновление длится 40+ минут. Красный `dependencyCheck` проверяй по причине, прежде чем чинить код.
- `./gradlew bootRun --args='--spring.profiles.active=dev'` — запуск на H2, без внешней БД.
- `docker compose up -d` — PostgreSQL для прод-профиля.
- Прод-профиль требует `DB_PASSWORD` и `JWT_SECRET` из окружения.

## Слои

- `controller` — HTTP, валидация DTO, коды ответов. Бизнес-логики здесь нет.
- `service` — интерфейсы; `service/impl` — логика и транзакции.
- `repository` — Spring Data JPA; производные методы предпочтительнее `@Query`.
- `model/{user,group,offer,exchange}` — сущности, `dto` + `mapper` (MapStruct) — контракт API.
- `exception` + `GlobalExceptionHandler` — единый формат ошибок.
- `security` — `JwtAuthenticationFilter`, `JwtTokenProvider`, `CustomUserDetailsService`, `AuthorizationService`.
- `config` — конфигурация приложения; `WebController`/`DashboardController` — серверный Thymeleaf-дашборд.

## Правила по слоям

- Новый эндпоинт: DTO с `@Valid`/`@NotNull`/`@Size` → MapStruct-маппер → сервис → контроллер. Наружу отдаётся DTO, не сущность.
- Маппинг только через мапперы. Ручное копирование полей в сервисе не добавлять.
- Ошибки — через `GlobalExceptionHandler`: единый формат, без стектрейсов и внутренних сообщений в ответе.
- Списки — только `Pageable`. Лимиты в `application.properties`: default 20, max 50. Поднимать max без причины нельзя.
- Права проверять через `AuthorizationService`, а не `if` в контроллере. Чужой объект — 403 или 404, а не 200.
- Кэш Caffeine (10 минут): если меняешь данные, которые он держит, добавь инвалидацию. Иначе баг «данные старые».

## База и миграции

- dev: H2 in-memory, `ddl-auto=create-drop`, Liquibase выключен — схема из сущностей.
- prod: PostgreSQL, `ddl-auto=validate`, Liquibase включён, мастер — `db/changelog/db.changelog-master.yaml`.
- Схема меняется только новым файлом `db/changelog/011-*.yaml` плюс `include` в мастер. Файлы `001–010` не редактировать.
- Изменение сущности без changelog валит прод на `validate` — это всегда пара: сущность + миграция.

## Тесты

- JUnit 5 + Mockito + `spring-security-test`. 141 тест в 19 классах: контроллерные, сервисные, `RecommendationIntegrationTest` и smoke-тест контекста. Проверено 24.11.2026: 0 падений, 0 пропусков.
- На баг сначала падающий тест, потом фикс.
- Тесты не ходят в реальный OpenAI: вызовы Spring AI мокаются. Платный API в тестах не дёргать.

## Секреты

- Секреты только из окружения: `JWT_SECRET`, `OPENAI_API_KEY`, `DB_PASSWORD` (подстановки уже есть в `application*.properties`).
- Не вписывать их в `application*.properties`, не логировать, не выводить в отчёт.

## Границы

- Не коммитить, не пушить, не мержить без прямой просьбы.
- `JwtAuthenticationFilter` не отключать, `permitAll` на новые пути не добавлять «чтобы заработало».
- `dependencyCheck` не отключать, порог CVSS не поднимать, тесты не глушить.
- Версии, запинованные в `build.gradle` через `ext`, не откатывать: tomcat 10.1.59, jackson 2.21.7, postgresql 42.7.12, commons-lang3 3.18.0, log4j 2.25.5, Spring AI 1.0.7 — это закрытые CVE, а не вкусовщина.
- Пакеты и файлы не переименовывать ради «чистоты».
- `nul` в корне — артефакт Windows, в коммит не тащить.

## Перед «готово»

1. `./gradlew build` — зелёный.
2. Менял схему — подними прод-профиль через `docker compose up -d` и убедись, что Liquibase и `validate` проходят.
3. Что не проверял — сказать прямо.
