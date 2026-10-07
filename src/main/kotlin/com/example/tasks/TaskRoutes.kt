package com.example.tasks

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

/**
 * REST endpoints for tasks. Every change is published to the [TaskEventBus]:
 *
 *   GET    /tasks            list all tasks (optional ?done=true|false)
 *   GET    /tasks/{id}       get one task
 *   POST   /tasks            create a task
 *   PATCH  /tasks/{id}       update title and/or done
 *   DELETE /tasks/{id}       delete a task
 */
fun Route.taskRoutes(repository: TaskRepository, eventBus: TaskEventBus) {
    route("/tasks") {

        get {
            val done = call.doneFilter()
            call.respond(repository.findAll(done))
        }

        get("/{id}") {
            val id = call.taskId()
            val task = repository.findById(id) ?: throw NotFoundException("Task $id not found")
            call.respond(task)
        }

        post {
            val request = call.receive<CreateTaskRequest>()
            val task = repository.create(request.title)
            eventBus.publish(TaskEvent.Created(task))
            call.response.header(HttpHeaders.Location, "/tasks/${task.id}")
            call.respond(HttpStatusCode.Created, task)
        }

        patch("/{id}") {
            val id = call.taskId()
            val request = call.receive<UpdateTaskRequest>()
            val task = repository.update(id, request) ?: throw NotFoundException("Task $id not found")
            eventBus.publish(TaskEvent.Updated(task))
            call.respond(task)
        }

        delete("/{id}") {
            val id = call.taskId()
            if (!repository.delete(id)) throw NotFoundException("Task $id not found")
            eventBus.publish(TaskEvent.Deleted(id))
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun ApplicationCall.taskId(): Long =
    parameters["id"]?.toLongOrNull() ?: throw BadRequestException("id must be a number")

private fun ApplicationCall.doneFilter(): Boolean? {
    val raw = request.queryParameters["done"] ?: return null
    return raw.toBooleanStrictOrNull() ?: throw BadRequestException("done must be true or false")
}
