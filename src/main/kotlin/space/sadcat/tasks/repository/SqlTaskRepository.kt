package space.sadcat.tasks.repository

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import space.sadcat.database.migrations.Tasks
import space.sadcat.tasks.models.CreateTaskRequest
import space.sadcat.tasks.models.Task
import space.sadcat.tasks.models.UpdateTaskRequest

class SqlTaskRepository : BaseTaskRepository(), TaskRepository {

    private fun toTask(row: ResultRow) = Task(
        id = row[Tasks.id].value,
        title = row[Tasks.title],
        status = row[Tasks.status]
    )

    override suspend fun all(): List<Task> = dbQuery {
        Tasks.selectAll().orderBy(Tasks.id to SortOrder.ASC).map(::toTask)
    }

    override suspend fun find(id: Long): Task? = dbQuery {
        Tasks.selectAll().where(Tasks.id eq id).singleOrNull()?.let(::toTask)
    }

    override suspend fun create(req: CreateTaskRequest): Task = dbQuery {
        val newId = Tasks.insertAndGetId {
            it[title] = normalizeTitle(req.title)
            it[status] = req.status
        }.value
        Tasks.selectAll().where(Tasks.id eq newId).single().let(::toTask)
    }

    override suspend fun update(id: Long, req: UpdateTaskRequest): Task? = dbQuery {
        val updatedRows = Tasks.update({ Tasks.id eq id }) {
            it[title] = req.title
            it[status] = req.status
        }
        if (updatedRows == 0) null
        else Tasks.selectAll().where(Tasks.id eq id).single().let(::toTask)
    }

    override suspend fun delete(id: Long): Boolean = dbQuery {
        Tasks.deleteWhere { Tasks.id eq id } > 0
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}


