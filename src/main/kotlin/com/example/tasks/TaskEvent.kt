package com.example.tasks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Something that happened to a task. Sent to live clients as JSON, e.g.
 * {"type":"created","task":{"id":4,"title":"Try Ktor","done":false}}
 */
@Serializable
sealed interface TaskEvent {

    @Serializable
    @SerialName("created")
    data class Created(val task: Task) : TaskEvent

    @Serializable
    @SerialName("updated")
    data class Updated(val task: Task) : TaskEvent

    @Serializable
    @SerialName("deleted")
    data class Deleted(val id: Long) : TaskEvent
}

/** Name of the event as used in the "type" field and as SSE event name. */
val TaskEvent.type: String
    get() = when (this) {
        is TaskEvent.Created -> "created"
        is TaskEvent.Updated -> "updated"
        is TaskEvent.Deleted -> "deleted"
    }

fun TaskEvent.toJson(): String = Json.encodeToString(TaskEvent.serializer(), this)
