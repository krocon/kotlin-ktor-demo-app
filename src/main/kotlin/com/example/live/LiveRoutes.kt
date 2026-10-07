package com.example.live

import com.example.tasks.TaskEvent
import com.example.tasks.TaskEventBus
import com.example.tasks.TaskRepository
import com.example.tasks.toJson
import com.example.tasks.type
import io.ktor.server.routing.Route
import io.ktor.server.sse.sse
import io.ktor.server.websocket.webSocket
import io.ktor.sse.ServerSentEvent
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch

/**
 * Live endpoints:
 *
 *   WS  /ws/tasks      two-way: receive all task events, send a text frame to create a task
 *   SSE /tasks/events  one-way: receive all task events as Server-Sent Events
 */
fun Route.liveRoutes(repository: TaskRepository, eventBus: TaskEventBus) {

    webSocket("/ws/tasks") {
        // Server -> client: forward every task event as a JSON text frame.
        val forwarding = launch(start = CoroutineStart.UNDISPATCHED) {
            eventBus.events.collect { event -> send(Frame.Text(event.toJson())) }
        }

        // Client -> server: every text frame becomes a new task.
        try {
            for (frame in incoming) {
                if (frame !is Frame.Text) continue
                val title = frame.readText().trim()
                if (title.isNotEmpty()) {
                    val task = repository.create(title)
                    eventBus.publish(TaskEvent.Created(task))
                }
            }
        } finally {
            forwarding.cancel()
        }
    }

    sse("/tasks/events") {
        eventBus.events.collect { event ->
            send(ServerSentEvent(data = event.toJson(), event = event.type))
        }
    }
}
