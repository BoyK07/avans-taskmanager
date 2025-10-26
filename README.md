# Todo Manager (Ktor)

A concise Ktor REST API for managing tasks. It demonstrates repository abstraction (SQL and in-memory), robust JSON handling, and a wide range of Kotlin language features required for assessment.

## Tech stack
- Kotlin, Ktor
- kotlinx.serialization (JSON)
- Exposed + HikariCP (SQL)
- JUnit (tests)

## Build & Run
```bash
./gradlew build
./gradlew run
```

## Test
```bash
./gradlew test
```

## Configuration
- File: `src/main/resources/application.yaml`
- Toggle DB usage with `db.enabled` (true = SQL via Exposed/Hikari, false = in-memory)

## API
- Base URL: `http://localhost:8080`
- Prefix: `/api`, version: `/api/v1`
- Content-Type: `application/json`
- Error shape:
```json
{ "error": "message" }
```

### Health
- GET `/api/health` → plain text confirming API responsiveness

### Tasks
- GET `/api/v1/tasks`
- GET `/api/v1/tasks/{id}`
- POST `/api/v1/tasks`
- PUT `/api/v1/tasks/{id}`
- DELETE `/api/v1/tasks/{id}`

Models
```json
{ "id": 1, "title": "Write docs", "status": "BACKLOG" }
```
Status enum: `BACKLOG|TODO|IN_PROGRESS|REVIEW|DONE`

## Project structure
- `Application.kt` — entry point, plugins (ContentNegotiation, StatusPages, CORS), repo selection
- `Routing.kt` — `/api` routes, `/api/v1` wiring
- `space/sadcat/tasks/routes/v1/TaskRoutes.kt` — task handlers
- `space/sadcat/tasks/models` — `Task`, `CreateTaskRequest`, `UpdateTaskRequest`
- `space/sadcat/tasks/repository`
  - `TaskRepository` (interface)
  - `InMemoryTaskRepository` (mutex-backed map)
  - `SqlTaskRepository` (Exposed CRUD)
  - `BaseTaskRepository` (abstract base with utilities)
- `space/sadcat/database` — `DatabaseFactory`, migrations (`Tasks`)
- `space/sadcat/http/HttpHelpers.kt` — extension helpers and validation HOFs

## Kotlin concepts demonstrated (where)
- Program entry point, packages, imports: `Application.kt` (`main`, `module`)
- Variables (`val`/`var`) and basic types: repositories, routes
- Special types: `Any` (reified in helpers), `Unit` (explicit/implicit), `Nothing` (`fail` in `BaseTaskRepository.kt`)
- Null safety: nullable types, safe calls `?.`, Elvis `?:` (e.g. `db.enabled` in `Application.kt`)
- Control flow: early return, `when`, guards
- Type checks and safe casts: `as?` in base; `when (any)` in health route
- Collections: `List`, `Map` (repos); `MutableList`, `Set` (health)
- OO concepts:
  - Abstraction & encapsulation: `TaskRepository`, `BaseTaskRepository` (protected members)
  - Inheritance/overriding: `InMemoryTaskRepository`/`SqlTaskRepository` extend base
  - Abstract class: `BaseTaskRepository`
  - Interfaces: `TaskRepository`
  - Overloading: `buildTask(id, title, status)` vs `buildTask(id, req)`
  - Polymorphism: swapping repo implementation behind the interface
- Class, constructors, init-blocks: primary constructors throughout; `init` in base; explicit `this`
- Equality: structural vs referential illustrated in `InMemoryTaskRepository.update`
- Data class: `Task` vs regular classes
- Visibility & property: `private`, `protected`, delegated properties (`by lazy`, `Delegates.observable`)
- Member vs top-level functions: route handlers vs helpers in `HttpHelpers.kt`
- Default & named arguments: defaults in signatures, named in calls
- Extension functions: `ApplicationCall.badRequest`, `notFound`, `paramLong`
- Top-level function: `configureRouting`, helpers
- Companion object: in `BaseTaskRepository`
- Object declaration (singleton): `DatabaseFactory`
- Generics: `inline reified` in `HttpHelpers.kt`
- Exception handling: try/catch in helpers, `StatusPages` global handler
- FP: function types, higher-order functions (validation), trailing lambdas, implicit `it`, returning from lambda, lambda with receiver (Ktor DSL), scope functions (`apply`, `let`, `also`)

## Notes
- Unknown JSON fields are ignored; responses are pretty-printed.
- CORS is enabled (permissive) for demo purposes.
