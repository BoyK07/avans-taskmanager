package space.sadcat.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import space.sadcat.database.migrations.*
import java.sql.Connection

object DatabaseFactory {

    fun init(env: ApplicationEnvironment) {
        val cfg = env.config.config("db")

        val hikari = HikariConfig().apply {
            driverClassName = cfg.property("driver").getString()
            jdbcUrl = cfg.property("jdbcUrl").getString()
            cfg.propertyOrNull("user")?.getString()?.let { username = it }
            cfg.propertyOrNull("password")?.getString()?.let { password = it }
            maximumPoolSize = cfg.propertyOrNull("maximumPoolSize")?.getString()?.toInt() ?: 5
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(hikari)
        Database.connect(dataSource)
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ

        transaction {
            SchemaUtils.createMissingTablesAndColumns(Tasks)
        }
    }
}
