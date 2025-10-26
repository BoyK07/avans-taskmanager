package space.sadcat

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import space.sadcat.tasks.routes.v1.taskRoutes
import space.sadcat.tasks.repository.TaskRepository


fun Application.configureRouting(tasksRepo: TaskRepository) {
    routing {
        route("/api") {
            get("/health") {
                val phrases = mutableListOf("Hello World!", "Hey there!", "Heyo!", "Good day!")
                phrases.add("All good!")
                val unique: Set<String> = phrases.toSet()

                val any: Any? = if (unique.isEmpty()) null else unique.first()
                val msg = when (any) {
                    null -> "No phrases"
                    is String -> any
                    else -> "Unknown type"
                }

                call.respondText("$msg The API is responsive!")
            }
            route("/v1") {
                taskRoutes(tasksRepo)
            }
        }
    }
}

