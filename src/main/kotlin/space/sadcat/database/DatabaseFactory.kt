package space.sadcat.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.ApplicationEnvironment
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

// Optional Flyway; you included it in Gradle
import org.flywaydb.core.Flyway

object DatabaseFactory {

    fun init(env: ApplicationEnvironment) {
        val cfg = env.config.config("db")

        val hikari = HikariConfig().apply {
            driverClassName = cfg.property("driver").getString()
            jdbcUrl = cfg.property("jdbcUrl").getString()
            cfg.propertyOrNull("user")?.getString()?.let { username = it }
            cfg.propertyOrNull("password")?.getString()?.let { password = it }
            maximumPoolSize = cfg.propertyOrNull("maximumPoolSize")?.getString()?.toInt() ?: 5

            // sensible defaults
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(hikari)

        // If Flyway migrations exist (classpath:db/migration), run them.
        // Safe to leave as-is even if there are no migration files yet.
        runFlywayIfAvailable(dataSource)

        // Connect Exposed to the pool
        Database.connect(dataSource)

        // Optional: enforce explicit commit behavior
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_REPEATABLE_READ
    }

    private fun runFlywayIfAvailable(dataSource: javax.sql.DataSource) {
        try {
            val flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration") // put SQL files here in Step 7 (optional)
                .baselineOnMigrate(true)
                .load()

            flyway.migrate()
        } catch (_: Throwable) {
            // No migrations found or Flyway misconfigured — ignore for now.
            // You can remove this try/catch if you want startup to fail on migration errors.
        }
    }
}
