package tachiyomi.core.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

fun interface DatabaseDriverFactory {
    fun create(
        schema: SqlSchema<QueryResult.Value<Unit>>,
        name: String,
    ): SqlDriver
}

internal fun initializeSqliteDriver(
    driver: SqlDriver,
    schema: SqlSchema<QueryResult.Value<Unit>>,
) {
    val currentVersion = driver.executeQuery(
        identifier = null,
        sql = "PRAGMA user_version",
        mapper = { cursor ->
            val hasRow = cursor.next().value
            QueryResult.Value(if (hasRow) cursor.getLong(0) ?: 0L else 0L)
        },
        parameters = 0,
        binders = null,
    ).value

    when {
        currentVersion == 0L -> schema.create(driver).value
        currentVersion < schema.version -> schema.migrate(driver, currentVersion, schema.version).value
        currentVersion > schema.version -> {
            driver.close()
            error("Database version $currentVersion is newer than schema version ${schema.version}")
        }
    }

    if (currentVersion != schema.version) {
        driver.execute(
            identifier = null,
            sql = "PRAGMA user_version = ${schema.version}",
            parameters = 0,
            binders = null,
        ).value
    }
}
