# API Routes

Note: JSON errors return `{ "error": "message" }`. With `db.enabled=false` in `application.yaml`, data is in-memory and resets on restart.

GET -> /api/health
*Health check; returns a short text message.*

Response (text):
```
Hello World! The API is responsive!
```

GET -> /api/v1/tasks
*List all tasks (JSON array).*

Response (200):
```json
[
  { "id": 1, "title": "Write docs", "status": "BACKLOG" },
  { "id": 2, "title": "Ship v1", "status": "IN_PROGRESS" }
]
```

GET -> /api/v1/tasks/{id}
*Get a single task by id; 400 if id invalid, 404 if missing.*

Response (200):
```json
{ "id": 1, "title": "Write docs", "status": "BACKLOG" }
```

POST -> /api/v1/tasks
*Create a task; returns 201 and Location header. Title must be non-empty. `status` defaults to BACKLOG if omitted.*
```json
{
  "title": "Write docs",
  "status": "TODO"
}
```

Response (201):
```json
{ "id": 3, "title": "Write docs", "status": "TODO" }
```

PUT -> /api/v1/tasks/{id}
*Update an existing task; title must be non-empty; 404 if missing.*
```json
{
  "title": "Write better docs",
  "status": "IN_PROGRESS"
}
```

Response (200):
```json
{ "id": 3, "title": "Write better docs", "status": "IN_PROGRESS" }
```

DELETE -> /api/v1/tasks/{id}
*Delete a task; returns 204 No Content, 404 if missing.*

Response:
```
Status: 204 No Content
```
