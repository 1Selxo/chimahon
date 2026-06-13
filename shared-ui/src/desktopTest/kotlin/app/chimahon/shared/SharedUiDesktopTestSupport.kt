package app.chimahon.shared

import app.cash.sqldelight.db.SqlDriver
import eu.kanade.tachiyomi.source.SourceRegistry
import kotlinx.coroutines.sync.Mutex
import okio.Path.Companion.toPath
import sun.misc.Unsafe
import tachiyomi.core.database.DesktopDatabaseDriverFactory
import tachiyomi.core.platform.settings.PlatformSettingsStore
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
    settingsStore: PlatformSettingsStore = InMemoryPlatformSettingsStore(),
): ChimahonSharedAppServices {
    val platformServices = unsafeAllocate<ChimahonPlatformServices>().apply {
        forceSetField("sourceRegistry", sourceRegistry)
        if (databaseHandler != null) {
            forceSetField("databaseHandler", databaseHandler)
        }
    }
    return unsafeAllocate<ChimahonSharedAppServices>().apply {
        forceSetField("platformServices", platformServices)
        forceSetField("settingsRepository", ChimahonSettingsRepository(settingsStore))
        forceSetField("readerProgressMutex", Mutex())
        forceSetField("readerProgressMarks", mutableMapOf<Long, TimeMark>())
    }
}

internal class InMemoryPlatformSettingsStore(
    private val values: MutableMap<String, String> = mutableMapOf(),
) : PlatformSettingsStore {
    override suspend fun readString(key: String): String? = values[key]

    override suspend fun writeString(key: String, value: String) {
        values[key] = value
    }

    override suspend fun remove(key: String) {
        values.remove(key)
    }

    override suspend fun clear() {
        values.clear()
    }

    override suspend fun snapshot(): Map<String, String> = values.toMap()
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
