Role: Senior Software Architect (Systems Design Expert)
Task: Create new, seperate application named panchayagnam-services, Redesign and refactor the provided Java Spring Boot Trading Application into a high-performance, maintainable microservices ecosystem.
Phase 1: Service Discovery & Connectivity:
Integrate Spring Cloud Eureka Server for dynamic service registration.
Configure the application to use Feign Clients or WebClient for inter-service communication, ensuring load balancing via Spring Cloud LoadBalancer.
Design the system to handle service failures gracefully using the Circuit Breaker Pattern (Resilience4j).

Phase 2: SOLID Principles Application:
Single Responsibility (SRP): Decompose bloated controllers and services. Separate "Order Validation" logic from "Database Persistence" and "Notification" logic.
Open/Closed (OCP): Use interfaces for payment gateways or brokerage connectors so new providers can be added without modifying existing code.
Dependency Inversion (DIP): Ensure high-level modules (Trade Execution) do not depend on low-level modules (SQL Repositories); both must depend on abstractions.

Phase 3: Design Patterns Implementation:
Strategy Pattern: Implement for different trading strategies (e.g., Scalping, Day Trading, Long-term).
Observer Pattern: Use for real-time price updates or notification triggers.
Factory Pattern: Use for creating different types of Financial Instruments (Stocks, Options, Crypto).
Builder Pattern: Use for complex objects like TradeOrder to ensure immutability and readability.

Phase 4: Data Design & Performance:
Implement the Database per Service pattern to ensure loose coupling.
Refactor repeated DB hits into optimized JOIN queries or Redis Caching for frequently accessed static data (e.g., Currency symbols, User profiles).

Phase 5: Inventory
Scan the entire source branch.
List every Controller, Service, Repository, Entity, Utility and every class found.
Create a "Migration Queue" based on this list.

Phase 6: Systematic Migration
Migrate the entire queue including all Repositories and Data Access Layers.
For Repositories: Refactor all raw queries into optimized JPQL/Criteria API or Named Queries to eliminate SQL injection.
For Entities/DTOs: Ensure data privacy masking is applied to sensitive fields.
For Business Logic: Ensure 100% of the logic from the old services is preserved, but refactored using SOLID principles and Java Streams.

Phase 7: Deep Parity Audit
Compare the old codebase against the new one.
Specifically check: "Did I skip the Repositories? Did I skip the User Role logic? Did I skip the Dashboard logic?"
If the answer is yes to any, migrate them immediately before reporting completion.

Phase 8: Build & Dry Run
Validate that the application compiles.
Perform a trace-run of the APIs: Ensure a request can flow from the Controller -> Service -> Repository and back without missing logic or mapping errors.
Status Report: Once finished, provide a table showing [Old Class Name] -> [New Class Location] to confirm 100% coverage.
Provide a sequence diagram logic (in text) showing a request hitting the API Gateway, authenticating, discovering the service via Eureka, and executing a transaction.