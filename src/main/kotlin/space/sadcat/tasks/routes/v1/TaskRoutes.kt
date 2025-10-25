package space.sadcat.tasks.routes.v1

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*
import space.sadcat.tasks.repository.TaskRepository
import space.sadcat.tasks.models.*
import space.sadcat.http.*

fun Route.taskRoutes(repo: TaskRepository) = route("/tasks") {

    get {
        call.respond(repo.all())
    }

    get("{id}") {
        val id = call.paramLong("id") ?: return@get call.badRequest("Invalid id")
        val task = repo.find(id) ?: return@get call.notFound("Task not found")
        call.respond(task)
    }

    post {
        val req = call.withValidBody<CreateTaskRequest> {
            if (it.title.isBlank()) "Title cannot be empty" else null
        } ?: return@post

        val created = repo.create(req).also {
            call.response.headers.append(HttpHeaders.Location, "/api/v1/tasks/${it.id}")
        }
        call.respond(HttpStatusCode.Created, created)
    }

    put("{id}") {
        val id = call.paramLong("id") ?: return@put call.badRequest("Invalid id")
        val req = call.withValidBody<UpdateTaskRequest> {
            if (it.title.isBlank()) "Title cannot be empty" else null
        } ?: return@put

        val updated = repo.update(id, req) ?: return@put call.notFound("Task not found")
        call.respond(updated)
    }

    delete("{id}") {
        val id = call.paramLong("id") ?: return@delete call.badRequest("Invalid id")
        val ok = repo.delete(id)
        if (!ok) return@delete call.notFound("Task not found")
        call.respond(HttpStatusCode.NoContent)
    }
}
