package tachiyomi.core.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDatabaseDriverFactory(
    private val context: Context,
    private val openHelperFactory: SupportSQLiteOpenHelper.Factory,
    private val onOpen: (SupportSQLiteDatabase) -> Unit = {},
) : DatabaseDriverFactory {
    override fun create(
        schema: SqlSchema<QueryResult.Value<Unit>>,
        name: String,
    ): SqlDriver {
        return AndroidSqliteDriver(
            schema = schema,
            context = context,
            name = name,
            factory = openHelperFactory,
            callback = object : AndroidSqliteDriver.Callback(schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    onOpen(db)
                }
            },
        )
    }
}
