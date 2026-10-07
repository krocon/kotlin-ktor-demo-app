package com.example.tasks

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InMemoryTaskRepositoryTest {

    private val repository = InMemoryTaskRepository()

    @Test
    fun `ids are assigned sequentially`() = runBlocking<Unit> {
        val first = repository.create("First")
        val second = repository.create("Second")

        assertEquals(1L, first.id)
        assertEquals(2L, second.id)
    }

    @Test
    fun `update only changes the given fields`() = runBlocking<Unit> {
        val task = repository.create("Original")

        val updated = repository.update(task.id, UpdateTaskRequest(done = true))

        assertEquals(Task(id = task.id, title = "Original", done = true), updated)
    }

    @Test
    fun `update of unknown id returns null`() = runBlocking<Unit> {
        assertNull(repository.update(99, UpdateTaskRequest(title = "Nope")))
    }

    @Test
    fun `delete returns whether a task was removed`() = runBlocking<Unit> {
        val task = repository.create("To delete")

        assertTrue(repository.delete(task.id))
        assertFalse(repository.delete(task.id))
    }
}
