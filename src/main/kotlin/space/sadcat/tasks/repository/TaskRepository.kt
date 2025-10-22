package space.sadcat.tasks.repository

import space.sadcat.tasks.models.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

// Temporary in-memory storage
object TaskRepository {
    private val seq = AtomicLong(0)
    private val items = ConcurrentHashMap<Long, Task>()

    fun all(): List<Task> = items.values.sortedBy { it.id }

    fun find(id: Long): Task? = items[id]

    fun create(req: CreateTaskRequest): Task {
        val id = seq.incrementAndGet()
        return Task(id = id, title = req.title, status = req.status).also {
            items[id] = it
        }
    }

    fun update(id: Long, req: UpdateTaskRequest): Task? {
        val current = items[id] ?: return null
        val updated = current.copy(title = req.title, status = req.status)
        items[id] = updated
        return updated
    }

    fun delete(id: Long): Boolean = items.remove(id) != null
}
