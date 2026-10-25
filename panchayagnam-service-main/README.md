# Panchayagnam Service

An automated multi-broker trading backend built with **Spring Boot 3.2** and **Java 17**.  
It lets you manage multiple demat accounts across different stock brokers, create trade groups, place bulk buy/sell orders, and monitor everything through audit trails — all from a single unified API.

---

## What This Application Does

| Feature | Description |
|---------|-------------|
| **Multi-Broker Demat Account Management** | Add, update, and delete demat accounts for Angel One, Motilal Oswal, and Upstox from a single place. |
| **Automated Token Refresh** | A scheduler runs every weekday at **8:00 AM** (with a retry at **8:30 AM**) to auto-login and refresh access tokens for all registered accounts before the market opens. Manual refresh is also available via API. |
| **Trade Groups** | Create groups of demat accounts + stocks. Place **bulk buy orders** across all members in one click, and later **bulk sell** all open positions for a group. |
| **Angel SmartConnect Integration** | Full trading API — place/modify/cancel orders, GTT rules, market data (LTP, candle, OI), holdings, positions, margin calculation, option greeks, gainers/losers, and more. |
| **Trade Audit Trail** | Every buy and sell order is audited. Query audit logs by group, client, demat account, or status. Get position tracking (OPEN/SOLD) and group-level summaries. |
| **Parent-Child Account Mapping** | Map admin (parent) demat accounts to child accounts for hierarchical management. |
| **User Authentication** | Register/login with JWT-based authentication for the EasyTrade frontend. |
| **Swagger UI** | Interactive API documentation with sample payloads for every endpoint. |

---

## Supported Brokers

| Broker | Enum Value | Status |
|--------|-----------|--------|
| **Angel One** (SmartConnect API) | `ANGELONE` | ✅ Fully integrated — orders, GTT, market data, holdings, positions, DIS, margin, option greeks |
| **Motilal Oswal** (Open API) | `MOTILAL` | ✅ Login/token integrated via SDK (`MOFSLOPENAPI_V3.1.jar`) |
| **Upstox** | `UPSTOX` | 🔧 Enum defined, integration in progress |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.2.3 |
| Language | Java 17 |
| Build | Maven |
| Database | MySQL (local/prod), H2 (dev/test) |
| ORM | Spring Data JPA / Hibernate |
| Auth | JWT (jjwt), BCrypt, Google Authenticator (TOTP) |
| HTTP Client | OkHttp 4.12, Apache HttpClient 5 |
| Logging | Log4j2 (async with LMAX Disruptor) |
| Caching | Caffeine |
| API Docs | springdoc-openapi (Swagger UI) |
| Scheduling | Spring `@Scheduled` + `@EnableAsync` |
| WebSocket | Java-WebSocket 1.5.4 |

---

## Environment Profiles

| Profile | Activate With | Port | Database | Use Case |
|---------|--------------|------|----------|----------|
| **`local`** | `--spring.profiles.active=local` | `8081` | MySQL (`localhost:3306/easy_trade`) | Local development with persistent data |
| **`dev`** | `--spring.profiles.active=dev` | default (`8080`) | H2 in-memory (`panchayagnam`) | Development with auto-reset DB, H2 console at `/h2-console` |
| **`h2`** | `--spring.profiles.active=h2` | `8082` | H2 in-memory (`testdb`) | Testing — scheduling pool minimized, SQL logging enabled |

---

## API Modules & Endpoints

### Swagger UI

```
http://localhost:<port>/panchayagnam
```

### API Groups

| Tag | Base Path | Description |
|-----|-----------|-------------|
| **EasyTrade Auth** | `/easyTrade` | User login & registration |
| **EasyTrade** | `/easyTradeApi` | Demat account CRUD, token refresh, group buy orders |
| **Angel SmartConnect** | `/api/smart` | Full Angel One trading API (30+ endpoints) |
| **Trade Groups** | `/api/groups` | Create groups, manage members/stocks, group buy/sell |
| **Trade Audit** | `/api/audit` | Buy/sell audit logs, positions, group summaries |
| **Demat Account Mapping** | `/api/demat/mapping` | Parent-child account relationships |

---

## Project Structure

```
src/main/java/com/py/
├── Application.java                  # Entry point (@EnableScheduling, @EnableAsync)
├── brokers/
│   ├── angel/smartconnect/           # Angel One SmartConnect client, DTOs, controller
│   └── motilal/                      # Motilal Oswal login service
├── common/
│   ├── config/                       # OpenAPI config, RestTemplate config
│   ├── dto/                          # ApiResponse wrapper
│   ├── enums/                        # BrokerType, AccountActionType
│   └── util/                         # Utility classes
├── demataccount/                     # Demat account entity, repository, service, controller
├── easytrade/                        # EasyTrade auth & main controller, user entity
├── tokenrefresh/                     # Token refresh scheduler, service, audit entity
├── tradeaudit/                       # Trade audit controller & service
└── tradegroup/                       # Trade group entity, group order/sell services
```

---

## How to Run

```bash
# Clone
git clone <repo-url>
cd panchayagnam-service

# Run with local profile (MySQL)
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Run with H2 in-memory (no DB setup needed)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/panchayagnam-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

### Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **MySQL 8.x** (only for `local` profile)

---

## Token Refresh Scheduler

Runs automatically on **weekdays** to keep all broker sessions alive:

| Time | Trigger | Purpose |
|------|---------|---------|
| **08:00 AM** (Mon–Fri) | `SCHEDULER` | Refresh all account tokens before market open |
| **08:30 AM** (Mon–Fri) | `SCHEDULER_RETRY` | Retry any accounts that failed at 8:00 AM |
| **On-demand** | `GET /easyTradeApi/tokens/refresh-now` | Manual trigger via API |

Audit logs for every refresh attempt are available at `GET /easyTradeApi/tokens/audit`.