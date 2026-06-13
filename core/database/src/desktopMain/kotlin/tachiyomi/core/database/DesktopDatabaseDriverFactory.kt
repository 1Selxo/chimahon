package tachiyomi.core.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import okio.FileSystem
import okio.Path

class DesktopDatabaseDriverFactory(
    private val databaseDirectory: Path,
    private val fileSystem: FileSystem = FileSystem.SYSTEM,
) : DatabaseDriverFactory {
    override fun create(
        schema: SqlSchema<QueryResult.Value<Unit>>,
        name: String,
    ): SqlDriver {
        fileSystem.createDirectories(databaseDirectory)
        val databasePath = databaseDirectory / name
        return JdbcSqliteDriver("jdbc:sqlite:$databasePath").also { driver ->
            initializeSqliteDriver(driver, schema)
            driver.execute(null, "PRAGMA foreign_keys = ON", 0, null).value
            driver.execute(null, "PRAGMA journal_mode = WAL", 0, null).value
            driver.execute(null, "PRAGMA synchronous = NORMAL", 0, null).value
        }
    }
}
