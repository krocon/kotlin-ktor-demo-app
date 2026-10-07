package com.example.tasks

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * A tiny in-process pub/sub: routes publish events,
 * every connected WebSocket / SSE client collects them.
 */
class TaskEventBus {

    private val _events = MutableSharedFlow<TaskEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<TaskEvent> = _events.asSharedFlow()

    suspend fun publish(event: TaskEvent) {
        _events.emit(event)
    }
}
