package space.sadcat.tasks.models

import kotlinx.serialization.Serializable
import space.sadcat.tasks.enums.Status

@Serializable
data class Task(
    val id: Long,
    val title: String,
    val status: Status = Status.BACKLOG
)

@Serializable
data class CreateTaskRequest(
    val title: String,
    val status: Status = Status.BACKLOG,
)

@Serializable
data class UpdateTaskRequest(
    val title: String,
    val status: Status,
)
