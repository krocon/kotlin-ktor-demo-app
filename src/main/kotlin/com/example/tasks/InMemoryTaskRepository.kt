package com.example.tasks

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/** Thread-safe, in-memory implementation. Data is lost on restart. */
class InMemoryTaskRepository : TaskRepository {

    private val tasks = ConcurrentHashMap<Long, Task>()
    private val nextId = AtomicLong(1)

    override suspend fun findAll(done: Boolean?): List<Task> =
        tasks.values
            .filter { done == null || it.done == done }
            .sortedBy { it.id }

    override suspend fun findById(id: Long): Task? = tasks[id]

    override suspend fun create(title: String): Task = add(title)

    private fun add(title: String): Task {
        val task = Task(id = nextId.getAndIncrement(), title = title.trim())
        tasks[task.id] = task
        return task
    }

    override suspend fun update(id: Long, request: UpdateTaskRequest): Task? =
        tasks.computeIfPresent(id) { _, existing ->
            existing.copy(
                title = request.title?.trim() ?: existing.title,
                done = request.done ?: existing.done,
            )
        }

    override suspend fun delete(id: Long): Boolean = tasks.remove(id) != null

    companion object {
        /** A repository pre-filled with a few tasks, handy for demos. */
        fun withSampleData(): InMemoryTaskRepository = InMemoryTaskRepository().apply {
            add("Learn Kotlin")
            add("Build a Ktor API")
            add("Record a one-minute video")
        }
    }
}
