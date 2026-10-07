package com.example.live

import com.example.module
import com.example.tasks.InMemoryTaskRepository
import com.example.tasks.Task
import com.example.tasks.TaskEvent
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.delete
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class LiveRoutesTest {

    private fun Frame.toEvent(): TaskEvent =
        Json.decodeFromString(TaskEvent.serializer(), (this as Frame.Text).readText())

    @Test
    fun `text frame creates a task and is broadcast back`() = testApplication {
        application { module(InMemoryTaskRepository()) }
        val client = createClient { install(WebSockets) }

        client.webSocket("/ws/tasks") {
            send(Frame.Text("Created via WebSocket"))

            val event = incoming.receive().toEvent()

            assertEquals(TaskEvent.Created(Task(id = 1, title = "Created via WebSocket")), event)
        }
    }

    @Test
    fun `REST delete is pushed to WebSocket clients`() = testApplication {
        application { module(InMemoryTaskRepository()) }
        val wsClient = createClient { install(WebSockets) }

        wsClient.webSocket("/ws/tasks") {
            // Creating a task first guarantees the socket is subscribed before we delete.
            send(Frame.Text("Delete me"))
            val created = incoming.receive().toEvent() as TaskEvent.Created

            client.delete("/tasks/${created.task.id}")

            assertEquals(TaskEvent.Deleted(created.task.id), incoming.receive().toEvent())
        }
    }
}
