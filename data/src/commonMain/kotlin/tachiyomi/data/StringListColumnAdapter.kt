package tachiyomi.data

import app.cash.sqldelight.ColumnAdapter

private const val LIST_OF_STRINGS_SEPARATOR = ", "

object StringListColumnAdapter : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> {
        return if (databaseValue.isEmpty()) {
            emptyList()
        } else {
            databaseValue.split(LIST_OF_STRINGS_SEPARATOR)
        }
    }

    override fun encode(value: List<String>): String {
        return value.joinToString(separator = LIST_OF_STRINGS_SEPARATOR)
    }
}
