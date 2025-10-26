package space.sadcat.tasks.repository

import kotlin.properties.Delegates
import space.sadcat.tasks.enums.Status
import space.sadcat.tasks.models.CreateTaskRequest
import space.sadcat.tasks.models.Task

// Abstract base demonstrating abstraction, encapsulation, init, companion, delegated properties, overloading.
abstract class BaseTaskRepository {

    // delegated property example
    protected val createdAt: Long by lazy { System.currentTimeMillis() }

    init {
        // explicit this and basic control flow
        check(this.createdAt > 0)
    }

    protected fun normalizeTitle(title: String): String = title.trim().ifEmpty { "Untitled" }

    // Overloading: build Task either from raw params or from request
    protected fun buildTask(id: Long, title: String, status: Status): Task =
        Task(id = id, title = normalizeTitle(title), status = status)

    protected fun buildTask(id: Long, req: CreateTaskRequest): Task =
        buildTask(id = id, title = req.title, status = req.status)

    // Type check and safe cast helper
    protected fun castToTaskOrNull(any: Any?): Task? = any as? Task

    companion object {
        // delegated property: observable counter
        @Volatile
        private var createdCount: Int by Delegates.observable(0) { _, _, _ -> }

        fun increaseCreateCount(): Unit { createdCount += 1 }
    }
}

// Nothing type function
internal fun fail(message: String): Nothing = throw IllegalStateException(message)


