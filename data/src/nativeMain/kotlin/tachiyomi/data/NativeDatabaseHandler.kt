package tachiyomi.data

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class NativeDatabaseHandler(
    private val db: Database,
    private val driver: SqlDriver,
    private val queryDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : DatabaseHandler {
    override suspend fun <T> await(inTransaction: Boolean, block: suspend Database.() -> T): T {
        return dispatch(inTransaction, block)
    }

    override suspend fun <T : Any> awaitList(
        inTransaction: Boolean,
        block: suspend Database.() -> Query<T>,
    ): List<T> {
        return dispatch(inTransaction) { block(db).executeAsList() }
    }

    override suspend fun <T : Any> awaitListExecutable(
        inTransaction: Boolean,
        block: suspend Database.() -> ExecutableQuery<T>,
    ): List<T> {
        return dispatch(inTransaction) { block(db).executeAsList() }
    }

    override suspend fun <T : Any> awaitOne(
        inTransaction: Boolean,
        block: suspend Database.() -> Query<T>,
    ): T {
        return dispatch(inTransaction) { block(db).executeAsOne() }
    }

    override suspend fun <T : Any> awaitOneExecutable(
        inTransaction: Boolean,
        block: suspend Database.() -> ExecutableQuery<T>,
    ): T {
        return dispatch(inTransaction) { block(db).executeAsOne() }
    }

    override suspend fun <T : Any> awaitOneOrNull(
        inTransaction: Boolean,
        block: suspend Database.() -> Query<T>,
    ): T? {
        return dispatch(inTransaction) { block(db).executeAsOneOrNull() }
    }

    override suspend fun <T : Any> awaitOneOrNullExecutable(
        inTransaction: Boolean,
        block: suspend Database.() -> ExecutableQuery<T>,
    ): T? {
        return dispatch(inTransaction) { block(db).executeAsOneOrNull() }
    }

    override fun <T : Any> subscribeToList(block: Database.() -> Query<T>): Flow<List<T>> {
        return queryFlow(block(db), Query<T>::executeAsList)
    }

    override fun <T : Any> subscribeToOne(block: Database.() -> Query<T>): Flow<T> {
        return queryFlow(block(db), Query<T>::executeAsOne)
    }

    override fun <T : Any> subscribeToOneOrNull(block: Database.() -> Query<T>): Flow<T?> {
        return queryFlow(block(db), Query<T>::executeAsOneOrNull)
    }

    private suspend fun <T> dispatch(
        inTransaction: Boolean,
        block: suspend Database.() -> T,
    ): T {
        if (driver.currentTransaction() != null) return block(db)

        return withContext(queryDispatcher) {
            if (inTransaction) {
                db.transactionWithResult {
                    runBlocking {
                        block(db)
                    }
                }
            } else {
                block(db)
            }
        }
    }

    private fun <T : Any, R> queryFlow(
        query: Query<T>,
        execute: Query<T>.() -> R,
    ): Flow<R> {
        return callbackFlow {
            fun emitResult() {
                try {
                    trySend(query.execute())
                } catch (error: Throwable) {
                    close(error)
                }
            }

            val listener = Query.Listener(::emitResult)
            query.addListener(listener)
            emitResult()
            awaitClose {
                query.removeListener(listener)
            }
        }.flowOn(queryDispatcher)
    }
}
