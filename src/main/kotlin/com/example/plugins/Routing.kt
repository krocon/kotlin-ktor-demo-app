package com.example.plugins

import com.example.live.liveRoutes
import com.example.tasks.TaskEventBus
import com.example.tasks.TaskRepository
import com.example.tasks.taskRoutes
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/** Registers all HTTP routes of the application. */
fun Application.configureRouting(taskRepository: TaskRepository, eventBus: TaskEventBus) {
    routing {
        get("/") {
            call.respondText(
                """
                <h1>Hello, Ktor!</h1>
                <p><a href="/live/">Open the live demo &rarr;</a></p>
                """.trimIndent(),
                ContentType.Text.Html,
            )
        }
        get("/health") {
            call.respond(mapOf("status" to "UP"))
        }
        taskRoutes(taskRepository, eventBus)
        liveRoutes(taskRepository, eventBus)

        // Browser demo page for the live endpoints: http://localhost:8080/live/
        staticResources("/live", "static")
    }
}
