package com.example.tasks

/**
 * Storage abstraction for tasks.
 * Swap the implementation (e.g. for a database) without touching the routes.
 */
interface TaskRepository {
    suspend fun findAll(done: Boolean? = null): List<Task>
    suspend fun findById(id: Long): Task?
    suspend fun create(title: String): Task
    suspend fun update(id: Long, request: UpdateTaskRequest): Task?
    suspend fun delete(id: Long): Boolean
}
