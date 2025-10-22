# todo-manager

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need
  to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                               | Description                                                 |
|----------------------------------------------------|-------------------------------------------------------------|
| [Routing](https://start.ktor.io/p/routing-default) | Allows to define structured routes and associated handlers. |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                                    | Description                                                          |
|-----------------------------------------|----------------------------------------------------------------------|
| `./gradlew test`                        | Run the tests                                                        |
| `./gradlew build`                       | Build everything                                                     |
| `./gradlew buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `./gradlew buildImage`                  | Build the docker image to use with the fat JAR                       |
| `./gradlew publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `./gradlew run`                         | Run the server                                                       |
| `./gradlew runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```


## API Reference

### Base

- **Base URL**: `http://localhost:8080`
- **Prefix**: All API routes are under `/api`, versioned under `/api/v1`.
- **Content-Type**: `application/json`
- **Error shape**: All non-2xx errors return JSON:

```json
{ "error": "message" }
```

Unknown JSON fields are ignored by the server. JSON is pretty-printed in responses.

---

### Health

- **GET** `/api/health`
  - Returns plain text confirming the API is responsive.

Example response body (text):

```
Hello World! The API is responsive!
```

---

### Tasks

All task routes are under: `/api/v1/tasks`

#### Model: Task

```json
{
  "id": 1,
  "title": "Write docs",
  "status": "BACKLOG"
}
```

#### Enum: Status

One of: `BACKLOG`, `TODO`, `IN_PROGRESS`, `REVIEW`, `DONE`

---

#### List tasks

- **GET** `/api/v1/tasks`

Response 200 JSON:

```json
[
  { "id": 1, "title": "Write docs", "status": "BACKLOG" },
  { "id": 2, "title": "Ship v1", "status": "IN_PROGRESS" }
]
```

Errors:
- 500 → `{ "error": "Unexpected server error" }`

---

#### Get task by id

- **GET** `/api/v1/tasks/{id}`

Response 200 JSON:

```json
{ "id": 1, "title": "Write docs", "status": "BACKLOG" }
```

Errors:
- 400 (invalid id) → `{ "error": "Invalid id" }`
- 404 (missing) → `{ "error": "Task not found" }`

---

#### Create task

- **POST** `/api/v1/tasks`

Request JSON (raw):

```json
{
  "title": "Write docs",
  "status": "TODO"
}
```

Notes:
- `title` is required and must be non-empty
- `status` is optional; defaults to `BACKLOG` if omitted

Response 201 JSON:

```json
{
  "id": 3,
  "title": "Write docs",
  "status": "TODO"
}
```

Response headers:
- `Location: /api/tasks/{id}`

Errors:
- 400 (invalid JSON) → `{ "error": "Invalid JSON body" }`
- 400 (validation) → `{ "error": "Title cannot be empty" }`

---

#### Update task

- **PUT** `/api/v1/tasks/{id}`

Request JSON (raw):

```json
{
  "title": "Write better docs",
  "status": "IN_PROGRESS"
}
```

Notes:
- Both `title` and `status` are required
- `title` must be non-empty

Response 200 JSON:

```json
{
  "id": 3,
  "title": "Write better docs",
  "status": "IN_PROGRESS"
}
```

Errors:
- 400 (invalid id) → `{ "error": "Invalid id" }`
- 400 (invalid JSON) → `{ "error": "Invalid JSON body" }`
- 400 (validation) → `{ "error": "Title cannot be empty" }`
- 404 (missing) → `{ "error": "Task not found" }`

---

#### Delete task

- **DELETE** `/api/v1/tasks/{id}`

Response 204 No Content (no body)

Errors:
- 400 (invalid id) → `{ "error": "Invalid id" }`
- 404 (missing) → `{ "error": "Task not found" }`

