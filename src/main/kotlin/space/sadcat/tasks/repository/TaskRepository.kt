package space.sadcat.tasks.repository

import space.sadcat.tasks.models.*

interface TaskRepository {
    suspend fun all(): List<Task>
    suspend fun find(id: Long): Task?
    suspend fun create(req: CreateTaskRequest): Task
    suspend fun update(id: Long, req: UpdateTaskRequest): Task?
    suspend fun delete(id: Long): Boolean
}
