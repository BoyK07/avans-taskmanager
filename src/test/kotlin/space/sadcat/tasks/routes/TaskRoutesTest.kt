package space.sadcat.tasks.routes

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import space.sadcat.configureRouting
import space.sadcat.tasks.repository.InMemoryTaskRepository
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

class TaskRoutesTest {
    @Test
    fun `POST creates and GET lists`() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            configureRouting(InMemoryTaskRepository())
        }
        val r = client.post("/api/v1/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Test task","status":"TODO"}""")
        }
        assertEquals(HttpStatusCode.Created, r.status)
        val list = client.get("/api/v1/tasks")
        assertEquals(HttpStatusCode.OK, list.status)
        assertTrue(list.bodyAsText().contains("Test task"))
    }
}


