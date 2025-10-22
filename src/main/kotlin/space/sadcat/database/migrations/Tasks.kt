package space.sadcat.database.migrations

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import space.sadcat.tasks.enums.Status

object Tasks : LongIdTable("tasks") {
    val title = varchar("title", 255)
    val status = enumerationByName("status", 32, Status::class)
}
