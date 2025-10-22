package space.sadcat.tasks.enums

import kotlinx.serialization.Serializable

@Serializable
enum class Status {
    BACKLOG,
    TODO,
    IN_PROGRESS,
    REVIEW,
    DONE
}