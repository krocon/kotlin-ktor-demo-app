package com.example

import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureSockets
import com.example.plugins.configureStatusPages
import com.example.plugins.configureValidation
import com.example.tasks.InMemoryTaskRepository
import com.example.tasks.TaskEventBus
import com.example.tasks.TaskRepository
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

/**
 * Entry point: starts an embedded Netty server.
 * The port can be overridden with the PORT environment variable.
 */
fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

/**
 * The application module wires all plugins and routes together.
 * Dependencies are parameters so tests can pass in fresh instances.
 */
fun Application.module(
    taskRepository: TaskRepository = InMemoryTaskRepository.withSampleData(),
    eventBus: TaskEventBus = TaskEventBus(),
) {
    configureMonitoring()
    configureSerialization()
    configureStatusPages()
    configureValidation()
    configureSockets()
    configureRouting(taskRepository, eventBus)
}
