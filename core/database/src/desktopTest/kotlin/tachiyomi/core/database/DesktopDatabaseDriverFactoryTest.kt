package tachiyomi.core.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import okio.Path.Companion.toOkioPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class DesktopDatabaseDriverFactoryTest {
    @TempDir
    lateinit var temporaryDirectory: Path

    @Test
    fun createsAndMigratesPersistentDatabase() {
        val factory = DesktopDatabaseDriverFactory(temporaryDirectory.toOkioPath())

        factory.create(SchemaV1, "chimahon-test.db").use { driver ->
            driver.execute(
                identifier = null,
                sql = "INSERT INTO entries(id) VALUES (1)",
                parameters = 0,
                binders = null,
            ).value
        }

        factory.create(SchemaV2, "chimahon-test.db").use { driver ->
            driver.execute(
                identifier = null,
                sql = "UPDATE entries SET title = 'Migrated' WHERE id = 1",
                parameters = 0,
                binders = null,
            ).value

            val title = driver.executeQuery(
                identifier = null,
                sql = "SELECT title FROM entries WHERE id = 1",
                mapper = { cursor ->
                    cursor.next().value
                    QueryResult.Value(cursor.getString(0))
                },
                parameters = 0,
                binders = null,
            ).value
            assertEquals("Migrated", title)
        }
    }
}

private object SchemaV1 : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
        driver.execute(null, "CREATE TABLE entries(id INTEGER NOT NULL PRIMARY KEY)", 0, null).value
        return QueryResult.Value(Unit)
    }

    override fun migrate(
        driver: SqlDriver,
        oldVersion: Long,
        newVersion: Long,
        vararg callbacks: app.cash.sqldelight.db.AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Value(Unit)
}

private object SchemaV2 : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long = 2

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
        driver.execute(
            null,
            "CREATE TABLE entries(id INTEGER NOT NULL PRIMARY KEY, title TEXT)",
            0,
            null,
        ).value
        return QueryResult.Value(Unit)
    }

    override fun migrate(
        driver: SqlDriver,
        oldVersion: Long,
        newVersion: Long,
        vararg callbacks: app.cash.sqldelight.db.AfterVersion,
    ): QueryResult.Value<Unit> {
        if (oldVersion < 2 && newVersion >= 2) {
            driver.execute(null, "ALTER TABLE entries ADD COLUMN title TEXT", 0, null).value
        }
        return QueryResult.Value(Unit)
    }
}
