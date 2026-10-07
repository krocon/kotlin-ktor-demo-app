package com.example.tasks

import com.example.module
import com.example.plugins.ErrorResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskRoutesTest {

    /** Starts the app with an empty repository and returns a JSON-aware client. */
    private fun ApplicationTestBuilder.setup(
        repository: TaskRepository = InMemoryTaskRepository(),
    ): HttpClient {
        application { module(repository) }
        return createClient {
            install(ContentNegotiation) { json() }
        }
    }

    @Test
    fun `root returns hello`() = testApplication {
        val client = setup()

        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, Ktor!", response.bodyAsText())
    }

    @Test
    fun `health check is UP`() = testApplication {
        val client = setup()

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(mapOf("status" to "UP"), response.body<Map<String, String>>())
    }

    @Test
    fun `create and fetch a task`() = testApplication {
        val client = setup()

        val created = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(CreateTaskRequest(title = "Write tests"))
        }
        assertEquals(HttpStatusCode.Created, created.status)
        assertEquals("/tasks/1", created.headers[HttpHeaders.Location])

        val task = client.get("/tasks/1").body<Task>()
        assertEquals(Task(id = 1, title = "Write tests", done = false), task)
    }

    @Test
    fun `list tasks filtered by done`() = testApplication {
        val repository = InMemoryTaskRepository()
        repository.create("Open task")
        val closed = repository.create("Closed task")
        repository.update(closed.id, UpdateTaskRequest(done = true))
        val client = setup(repository)

        val doneTasks = client.get("/tasks?done=true").body<List<Task>>()

        assertEquals(listOf("Closed task"), doneTasks.map { it.title })
    }

    @Test
    fun `patch marks a task as done`() = testApplication {
        val repository = InMemoryTaskRepository()
        val task = repository.create("Ship it")
        val client = setup(repository)

        val response = client.patch("/tasks/${task.id}") {
            contentType(ContentType.Application.Json)
            setBody(UpdateTaskRequest(done = true))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.body<Task>().done)
    }

    @Test
    fun `delete removes a task`() = testApplication {
        val repository = InMemoryTaskRepository()
        val task = repository.create("Temporary")
        val client = setup(repository)

        assertEquals(HttpStatusCode.NoContent, client.delete("/tasks/${task.id}").status)
        assertEquals(HttpStatusCode.NotFound, client.get("/tasks/${task.id}").status)
    }

    @Test
    fun `blank title is rejected`() = testApplication {
        val client = setup()

        val response = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(CreateTaskRequest(title = "   "))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("title must not be blank", response.body<ErrorResponse>().message)
    }

    @Test
    fun `unknown task returns 404`() = testApplication {
        val client = setup()

        val response = client.get("/tasks/42")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Task 42 not found", response.body<ErrorResponse>().message)
    }

    @Test
    fun `non-numeric id returns 400`() = testApplication {
        val client = setup()

        assertEquals(HttpStatusCode.BadRequest, client.get("/tasks/abc").status)
    }
}
