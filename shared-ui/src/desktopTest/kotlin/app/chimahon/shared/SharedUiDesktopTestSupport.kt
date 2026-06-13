package app.chimahon.shared

import app.cash.sqldelight.db.SqlDriver
import eu.kanade.tachiyomi.source.SourceRegistry
import kotlinx.coroutines.sync.Mutex
import okio.Path.Companion.toPath
import sun.misc.Unsafe
import tachiyomi.core.database.DesktopDatabaseDriverFactory
import tachiyomi.data.Database
import tachiyomi.data.DatabaseHandler
import tachiyomi.data.DesktopDatabaseHandler
import java.lang.reflect.Field
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator
import kotlin.time.TimeMark

internal class SharedUiTestDatabase(
    private val root: Path,
    val driver: SqlDriver,
    val database: Database,
    val handler: DesktopDatabaseHandler,
) : AutoCloseable {
    override fun close() {
        driver.close()
        root.deleteTestDirectoryRecursively()
    }
}

internal fun createSharedUiTestDatabase(name: String): SharedUiTestDatabase {
    val root = Files.createTempDirectory(name)
    val driver = DesktopDatabaseDriverFactory(root.toString().toPath())
        .create(Database.Schema, "test.db")
    val database = createDatabase(driver)
    return SharedUiTestDatabase(
        root = root,
        driver = driver,
        database = database,
        handler = DesktopDatabaseHandler(database, driver),
    )
}

internal fun chimahonServiceForTest(
    databaseHandler: DatabaseHandler? = null,
    sourceRegistry: SourceRegistry = SourceRegistry(),
): ChimahonSharedAppServices {
    val platformServices = unsafeAllocate<ChimahonPlatformServices>().apply {
        forceSetField("sourceRegistry", sourceRegistry)
        if (databaseHandler != null) {
            forceSetField("databaseHandler", databaseHandler)
        }
    }
    return unsafeAllocate<ChimahonSharedAppServices>().apply {
        forceSetField("platformServices", platformServices)
        forceSetField("readerProgressMutex", Mutex())
        forceSetField("readerProgressMarks", mutableMapOf<Long, TimeMark>())
    }
}

internal fun invokePrivateChimahonAppFunction(
    name: String,
    parameterTypes: List<Class<*>>,
    vararg args: Any?,
): Any? {
    val method = Class.forName("app.chimahon.shared.ChimahonAppKt")
        .getDeclaredMethod(name, *parameterTypes.toTypedArray())
    method.isAccessible = true
    return method.invoke(null, *args)
}

private fun Path.deleteTestDirectoryRecursively() {
    if (Files.notExists(this)) return
    Files.walk(this).use { paths ->
        paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
    }
}

private val testUnsafe: Unsafe by lazy {
    Unsafe::class.java.getDeclaredField("theUnsafe").run {
        isAccessible = true
        get(null) as Unsafe
    }
}

private inline fun <reified T : Any> unsafeAllocate(): T {
    @Suppress("UNCHECKED_CAST")
    return testUnsafe.allocateInstance(T::class.java) as T
}

private fun Any.forceSetField(name: String, value: Any?) {
    val field = javaClass.findField(name)
    testUnsafe.putObject(this, testUnsafe.objectFieldOffset(field), value)
}

private tailrec fun Class<*>.findField(name: String): Field {
    return runCatching { getDeclaredField(name) }
        .getOrElse {
            superclass?.findField(name) ?: throw NoSuchFieldException("${this.name}.$name")
        }
        .also { it.isAccessible = true }
}
