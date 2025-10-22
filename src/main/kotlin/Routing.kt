package space.sadcat

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import space.sadcat.tasks.routes.v1.taskRoutes

fun Application.configureRouting() {
    routing {
        route("/api") {
            get("/health") {
                val phrases = arrayOf("Hello World!", "Hey there!", "Heyo!", "Good day!")
                call.respondText("${phrases.random()} The API is responsive!")
            }
            route("/v1") {
                taskRoutes()
            }
        }
    }
}

