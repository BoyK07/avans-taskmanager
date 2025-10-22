package space.sadcat.tasks.routes.v1

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import space.sadcat.http.*
import space.sadcat.tasks.models.*
import space.sadcat.tasks.repository.TaskRepository

fun Route.taskRoutes() = route("/tasks") {
    // GET /api/tasks
    get {
        val tasks = TaskRepository.all()
        call.respond(HttpStatusCode.OK, tasks)
    }

    // GET /api/tasks/{id}
    get("{id}") {
        val id = call.paramLong("id") ?: return@get call.badRequest("Invalid id")
        val task = TaskRepository.find(id) ?: return@get call.notFound("Task not found")
        call.respond(task)
    }

    // POST /api/tasks
    post {
        val req = call.receiveOr400<CreateTaskRequest>() ?: return@post
        if (req.title.isBlank()) return@post call.badRequest("Title cannot be empty")

        val created = TaskRepository.create(req)
        call.response.headers.append(HttpHeaders.Location, "/api/tasks/${created.id}")
        call.respond(HttpStatusCode.Created, created)
    }

    // PUT /api/tasks/{id}
    put("{id}") {
        val id = call.paramLong("id") ?: return@put call.badRequest("Invalid id")
        val req = call.receiveOr400<UpdateTaskRequest>() ?: return@put
        if (req.title.isBlank()) return@put call.badRequest("Title cannot be empty")

        val updated = TaskRepository.update(id, req) ?: return@put call.notFound("Task not found")
        call.respond(updated)
    }

    // DELETE /api/tasks/{id}
    delete("{id}") {
        val id = call.paramLong("id") ?: return@delete call.badRequest("Invalid id")
        val ok = TaskRepository.delete(id)
        if (!ok) return@delete call.notFound("Task not found")
        call.respond(HttpStatusCode.NoContent)
    }
}
