package space.sadcat.http

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String)

// Parse a Long path param or return null
fun ApplicationCall.paramLong(name: String): Long? = parameters[name]?.toLongOrNull()

// Uniform JSON errors
suspend fun ApplicationCall.badRequest(msg: String) =
    respond(HttpStatusCode.BadRequest, ErrorResponse(msg))

suspend fun ApplicationCall.notFound(msg: String) =
    respond(HttpStatusCode.NotFound, ErrorResponse(msg))

// Safe JSON receive with auto 400 on failure
suspend inline fun <reified T : Any> ApplicationCall.receiveOr400(): T? =
    runCatching { receive<T>() }.getOrElse {
        badRequest("Invalid JSON body")
        null
    }
