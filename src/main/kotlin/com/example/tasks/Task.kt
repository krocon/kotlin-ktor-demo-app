package com.example.tasks

import kotlinx.serialization.Serializable

/** A single to-do item. */
@Serializable
data class Task(
    val id: Long,
    val title: String,
    val done: Boolean = false,
)

/** Body of POST /tasks. */
@Serializable
data class CreateTaskRequest(
    val title: String,
)

/** Body of PATCH /tasks/{id}. Only fields that are set get updated. */
@Serializable
data class UpdateTaskRequest(
    val title: String? = null,
    val done: Boolean? = null,
)
