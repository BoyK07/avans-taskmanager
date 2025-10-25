package space.sadcat.tasks.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import space.sadcat.tasks.models.CreateTaskRequest
import space.sadcat.tasks.models.Task
import space.sadcat.tasks.models.UpdateTaskRequest

class InMemoryTaskRepository : TaskRepository {
    private val mutex = Mutex()
    private val store = LinkedHashMap<Long, Task>()
    private var seq = 0L

    override suspend fun all(): List<Task> = mutex.withLock { store.values.toList() }
    override suspend fun find(id: Long): Task? = mutex.withLock { store[id] }
    override suspend fun create(req: CreateTaskRequest): Task = mutex.withLock {
        val id = ++seq
        val t = Task(id, req.title, req.status)
        store[id] = t; t
    }
    override suspend fun update(id: Long, req: UpdateTaskRequest): Task? = mutex.withLock {
        val cur = store[id] ?: return null
        val upd = cur.copy(title = req.title, status = req.status)
        store[id] = upd; upd
    }
    override suspend fun delete(id: Long): Boolean = mutex.withLock { store.remove(id) != null }
}


