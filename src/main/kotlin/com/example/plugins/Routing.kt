package com.example.plugins

import com.example.tasks.TaskRepository
import com.example.tasks.taskRoutes
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

/** Registers all HTTP routes of the application. */
fun Application.configureRouting(taskRepository: TaskRepository) {
    routing {
        get("/") {
            call.respondText("Hello, Ktor!")
        }
        get("/health") {
            call.respond(mapOf("status" to "UP"))
        }
        taskRoutes(taskRepository)
    }
}
