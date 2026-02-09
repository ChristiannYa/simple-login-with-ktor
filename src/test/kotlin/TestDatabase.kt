import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.testcontainers.containers.PostgreSQLContainer

object TestDatabase {
    private var postgres: PostgreSQLContainer<*>? = null

    fun setUp(): Database {
        // Create and start container if it doesn't exist
        if (postgres == null) {
            postgres = PostgreSQLContainer("postgres:15-alpine").apply { start() }
        }

        val database = Database.connect(
            url = postgres!!.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgres!!.username,
            password = postgres!!.password
        )

        // Run Flyway migrations
        postgres!!.configureFlyway().migrate()

        return database
    }

    // Clean up database after each test
    fun tearDown() {
        postgres?.configureFlyway()?.clean()
    }

    fun shutDown() {
        postgres?.stop()
        postgres = null
    }

    private fun PostgreSQLContainer<*>.configureFlyway(): Flyway {
        return Flyway
            .configure()
            .dataSource(this.jdbcUrl, this.username, this.password)
            .locations("classpath:db/migration")
            .cleanDisabled(false)
            .load()
    }
}