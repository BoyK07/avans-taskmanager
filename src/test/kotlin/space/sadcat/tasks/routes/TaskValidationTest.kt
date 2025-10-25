package space.sadcat.tasks.routes

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import space.sadcat.configureRouting
import space.sadcat.tasks.repository.InMemoryTaskRepository
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

class TaskValidationTest {
    @Test fun `POST blank title returns 400`() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            configureRouting(InMemoryTaskRepository())
        }
        val r = client.post("/api/v1/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"title":"", "status":"TODO"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, r.status)
    }

    @Test fun `GET unknown id returns 404`() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            configureRouting(InMemoryTaskRepository())
        }
        val r = client.get("/api/v1/tasks/9999")
        assertEquals(HttpStatusCode.NotFound, r.status)
    }
}


