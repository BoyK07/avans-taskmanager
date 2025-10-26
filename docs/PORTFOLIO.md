# 🧩 Individueel Portfolio Kotlin – [Jouw Naam]

> 📅 Deadline: 26 oktober 2025  
> 🎯 Doel: aantonen dat ik OO- en FP-concepten in Kotlin beheers via een eigen project (geen Hyperskill)

---

## 1️⃣ Projectbeschrijving
**Titel:** Takenbeheer API (Ktor)

**Beschrijving:**  
Deze applicatie is een RESTful API voor het beheren van taken. Gebouwd met Ktor 3 en Kotlin 2, met JSON-serialisatie via kotlinx.serialization. Taken hebben een `title` en een `status` (zoals `TODO`, `IN_PROGRESS`, `DONE`). De API ondersteunt CRUD-operaties, valideert invoer (bijv. geen lege titels), en antwoordt consequent met JSON-foutberichten. De data-opslaglaag is gescheiden via een `TaskRepository`-interface met twee implementaties: een thread-safe in-memory variant voor testen en een SQL-variant op basis van JetBrains Exposed voor productie. Hierdoor schakelt de applicatie eenvoudig tussen in-memory en database-gestuurde opslag via configuratie.

---

## 2️⃣ Architectuur & Ontwerp

### 2.1 UML Class Diagram (tekstueel overzicht)

```
space.sadcat
├── Application.module
├── Routing.configureRouting
├── http
│   ├── ErrorResponse
│   ├── ApplicationCall.badRequest / notFound
│   └── ApplicationCall.withValidBody<T>(validate)
├── tasks
│   ├── enums
│   │   └── Status
│   ├── models
│   │   ├── Task (data class)
│   │   ├── CreateTaskRequest (data class)
│   │   └── UpdateTaskRequest (data class)
│   ├── repository
│   │   ├── TaskRepository (interface)
│   │   ├── InMemoryTaskRepository (implements TaskRepository)
│   │   └── SqlTaskRepository (implements TaskRepository)
│   └── routes.v1
│       └── taskRoutes(repo: TaskRepository)
└── database
    ├── db.DatabaseFactory (object)
    └── migrations.Tasks (object, Exposed table)
```

### 2.2 OO-structuur
- **Modellen:** `Task`, `CreateTaskRequest`, `UpdateTaskRequest` beschrijven het domein en worden geserialiseerd naar/van JSON.
- **Repository-laag (Abstraction):** `TaskRepository` definieert de CRUD-API; `InMemoryTaskRepository` en `SqlTaskRepository` implementeren deze. De applicatie kiest dynamisch de implementatie op basis van config (`db.enabled`).
- **Routing/Service-laag:** `taskRoutes` bevat de HTTP-endpoints en orkestreert validatie, repository-calls en HTTP-responses.
- **HTTP-hulpfuncties:** Uniforme foutafhandeling en validatie via `withValidBody` en `badRequest/notFound` helpers.
- **Database:** `DatabaseFactory` initialiseert de connectiepool (Hikari) en maakt tabellen aan (`Tasks` via Exposed).

**Waarom zo gestructureerd?**  
De scheiding in lagen maakt de code onderhoudbaar en testbaar. Door de repository te abstraheren zijn routes en businesslogica onafhankelijk van de opslagtechniek. Scope functions en higher-order functions verminderen boilerplate in configuratie en validatie.

---

## 3️⃣ Toepassing van OO-concepten

### 3.1 Encapsulation & Abstraction
```kotlin
// Encapsulation via private state + Mutex
class InMemoryTaskRepository : TaskRepository {
    private val mutex = Mutex()
    private val store = LinkedHashMap<Long, Task>()
    private var seq = 0L
    // ... CRUD-methoden met withLock(...)
}

// Abstraction via interface
interface TaskRepository {
    suspend fun all(): List<Task>
    suspend fun find(id: Long): Task?
    suspend fun create(req: CreateTaskRequest): Task
    suspend fun update(id: Long, req: UpdateTaskRequest): Task?
    suspend fun delete(id: Long): Boolean
}
```
**Toelichting:** interne staat (`store`, `seq`) is afgeschermd; de interface abstraheert de opslaglaag.

### 3.2 Inheritance & Polymorphism
```kotlin
// Polymorfisme via interface-referentie
val repo: TaskRepository = if (dbEnabled) SqlTaskRepository() else InMemoryTaskRepository()
configureRouting(repo) // routes werken tegen het abstracte type
```
**Toelichting:** de routes gebruiken alleen `TaskRepository`; concrete implementatie kan wisselen zonder aanpassingen.

### 3.3 Interfaces & Abstract Classes
```kotlin
// Interface (contract) + meerdere implementaties
interface TaskRepository { /* ... */ }
class InMemoryTaskRepository : TaskRepository { /* ... */ }
class SqlTaskRepository : TaskRepository { /* ... */ }
```
**Toelichting:** interface dwingt consistentie af; elke implementatie focust op zijn verantwoordelijkheden.

### 3.4 Overriding, Data Classes, Object Singletons, Generics, Exception Handling
```kotlin
// Data classes: immutabele value objects
@Serializable
data class Task(val id: Long, val title: String, val status: Status = Status.BACKLOG)

// Overriding: repository-methoden implementeren het interfacecontract
override suspend fun update(id: Long, req: UpdateTaskRequest): Task? = mutex.withLock { /* ... */ }

// Object singletons: infrastructuur-componenten
object DatabaseFactory { fun init(env: ApplicationEnvironment) { /* ... */ } }

// Generics + reified type + higher-order validate
suspend inline fun <reified T : Any> ApplicationCall.withValidBody(
    crossinline validate: (T) -> String?
): T? { /* ... */ }

// Exception handling: uniform JSON via StatusPages en helpers
install(StatusPages) {
    exception<Throwable> { call, _ -> call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Unexpected server error")) }
}
```
**Toelichting:** data classes voor domein, `object` voor singletons, inline reified generics voor type-veilige request parsing, en centrale exception-afhandeling.

---

## 4️⃣ Toepassing van FP-concepten

### 4.1 Lambda’s & Higher-Order Functions
```kotlin
// Higher-order validatie in de HTTP-laag
val req = call.withValidBody<CreateTaskRequest> {
    if (it.title.isBlank()) "Title cannot be empty" else null
}

// Exposed: transaction wrapper als higher-order
private suspend fun <T> dbQuery(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }
```
**Toelichting:** functies ontvangen lambdas om gedrag (validatie/DB-actie) te parametriseren.

### 4.2 Scope Functions (`let`, `apply`, `run`, `also`, `with`)
```kotlin
// apply: configuratie zonder tijdelijke variabelen
val hikari = HikariConfig().apply {
    driverClassName = cfg.property("driver").getString()
    jdbcUrl = cfg.property("jdbcUrl").getString()
}

// also: tussentijdse side-effect (Location header) en chaining
val created = repo.create(req).also {
    call.response.headers.append(HttpHeaders.Location, "/api/v1/tasks/${it.id}")
}
```
**Waarom deze keuzes:** `apply` voor DSL-achtige configuratie; `also` voor duidelijke, beperkte side-effects.

### 4.3 Function Types & Immutability
- `withValidBody(validate: (T) -> String?)` gebruikt een expliciet function type voor validatie.  
- Domeinobjecten zijn immutabel; updates gaan via `copy(...)` op `Task`.  
- De in-memory store is beschermd met een `Mutex` en gebruikt pure transformaties binnen een kritieke sectie.

---

## 5️⃣ Testen

### 5.1 Unit Tests
```kotlin
@Test fun create_find_update_delete() = runTest {
    val repo: TaskRepository = InMemoryTaskRepository()
    val c = repo.create(CreateTaskRequest("A", status = Status.TODO))
    assertEquals("A", c.title)
    assertNotNull(repo.find(c.id))
    val u = repo.update(c.id, UpdateTaskRequest("B", Status.DONE))
    assertEquals("B", u?.title); assertEquals(Status.DONE, u?.status)
    assertTrue(repo.delete(c.id)); assertNull(repo.find(c.id))
}
```
**Toelichting:** CRUD-gedrag van de repository wordt gecontroleerd.

```kotlin
@Test fun all_returns_in_insertion_order() = runTest {
    val repo: TaskRepository = InMemoryTaskRepository()
    repo.create(CreateTaskRequest("A", Status.TODO))
    repo.create(CreateTaskRequest("B", Status.TODO))
    repo.create(CreateTaskRequest("C", Status.TODO))
    assertEquals(listOf("A","B","C"), repo.all().map { it.title })
}
```
**Toelichting:** insertion order blijft behouden (LinkedHashMap).

```kotlin
@Test fun update_missing_and_delete_missing() = runTest {
    val repo: TaskRepository = InMemoryTaskRepository()
    assertNull(repo.update(999, UpdateTaskRequest("X", Status.DONE)))
    assertFalse(repo.delete(999))
}
```
**Toelichting:** randgevallen (niet-bestaande id) geven veilige resultaten.

### 5.2 Integratie Test
```kotlin
@Test fun post_creates_and_get_lists() = testApplication {
    application { install(ContentNegotiation) { json() }; configureRouting(InMemoryTaskRepository()) }
    val r = client.post("/api/v1/tasks") {
        contentType(ContentType.Application.Json)
        setBody("""{"title":"Test task","status":"TODO"}""")
    }
    assertEquals(HttpStatusCode.Created, r.status)
    val list = client.get("/api/v1/tasks")
    assertEquals(HttpStatusCode.OK, list.status)
}
```
**Toelichting:** test de samenwerking tussen routing, validatie en repository.

### 5.3 Testresultaten
- Uitvoerbaar via Gradle: `./gradlew test` (of `gradlew.bat test` op Windows).  
- Voeg hier desgewenst screenshots van geslaagde test runs toe.

---

## 6️⃣ Installatiehandleiding

1. Open het project in **IntelliJ IDEA** of gebruik de commandline.  
2. Vul `src/main/resources/application.yaml` indien nodig aan met DB-configuratie.  
3. Start de server:  
   - Windows: `gradlew.bat run`  
   - macOS/Linux: `./gradlew run`  
4. De API luistert op de geconfigureerde host/poort (zie `application.yaml`).  
5. Testen uitvoeren: `gradlew test`.

---

## 7️⃣ Persoonlijke Reflectie

- **Wat ging goed:** …  
- **Wat kon beter:** …  
- **Wat heb ik geleerd:** …  
- **Waar ben ik trots op:** …

---

## 8️⃣ Duurzaamheidsverantwoording (ISO 25010)

| Dimensie | Voorbeeld uit mijn project |
| --- | --- |
| **Maintainability** | Lagen-architectuur (routes, repo, models), duidelijke contracts via interfaces, tests. |
| **Reliability** | Centrale exception-afvang (StatusPages), consistente JSON-fouten, unit + integratietests. |
| **Performance efficiency** | DB-IO via `Dispatchers.IO`, Hikari connection pool, in-memory pad voor snelle tests. |
| **Security** | Inputvalidatie (`withValidBody`), CORS-configuratie, minimale surface area in responses. |
| **Portability** | Abstractie van opslag (in-memory/SQL), Ktor-config via YAML. |

**Reflectie:**  
De architectuur stimuleert herbruikbaarheid en onderhoudbaarheid. Door abstracties en tests is de code betrouwbaar; resources worden efficiënt gebruikt via pooling en IO-dispatching.

---

## 9️⃣ Checklist

| Onderdeel | ✔️ |
| --- | -- |
| Eigen Kotlin-project met meerdere klassen | ✅ |
| OO-concepten toegepast | ✅ |
| FP-concepten toegepast | ✅ |
| UML-diagram(tekst) toegevoegd | ✅ |
| Tests uitgevoerd en gedocumenteerd | ✅ |
| Reflectie toegevoegd | ☐ |
| Duurzaamheidsverantwoording opgenomen | ✅ |

---

> ✨ Doel van dit document: alles in één markdownbestand zodat het later eenvoudig omgezet kan worden naar Word of PDF (bijv. via pandoc of export in Cursor).

