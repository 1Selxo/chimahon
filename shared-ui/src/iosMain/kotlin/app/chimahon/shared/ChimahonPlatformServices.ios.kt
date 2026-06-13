package app.chimahon.shared

import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.SourceRegistry
import eu.kanade.tachiyomi.source.model.SManga
import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import platform.posix.time
import tachiyomi.core.database.NativeDatabaseDriverFactory
import tachiyomi.core.platform.javascript.IosJavaScriptRuntimeFactory
import tachiyomi.core.platform.javascript.JavaScriptRuntimeFactory
import tachiyomi.core.platform.storage.IosPlatformStorageDirectories
import tachiyomi.core.platform.storage.PlatformStorageDirectories
import tachiyomi.data.Database
import tachiyomi.data.DatabaseHandler
import tachiyomi.data.NativeDatabaseHandler

internal actual class ChimahonPlatformServices actual constructor() {
    actual val platformName: String = "iOS"
    actual val backgroundState: String = "BGTaskScheduler bridge ready"
    actual val storageDirectories: PlatformStorageDirectories = IosPlatformStorageDirectories(APP_NAME)
    actual val sourceRegistry: SourceRegistry = SourceRegistry()
    actual val apkExtensionManager = ChimahonPlatformApkExtensionManager(storageDirectories, sourceRegistry)
    private val databaseDirectory = storageDirectories.filesDir / DATABASE_DIRECTORY
    private val databaseDriver = NativeDatabaseDriverFactory().create(
        schema = Database.Schema,
        name = run {
            FileSystem.SYSTEM.createDirectories(databaseDirectory)
            (databaseDirectory / DATABASE_NAME).toString()
        },
    )
    actual val database: Database = createDatabase(databaseDriver)
    actual val databaseHandler: DatabaseHandler = NativeDatabaseHandler(database, databaseDriver)
    actual val javaScriptRuntimeFactory: JavaScriptRuntimeFactory = IosJavaScriptRuntimeFactory

    @OptIn(ExperimentalForeignApi::class)
    actual fun currentTimeMillis(): Long {
        return time(null) * 1_000L
    }

    actual fun resolveExternalMangaUrl(source: CatalogueSource, manga: SManga): String? = manga.url

    actual fun openExternalUrl(url: String): Boolean {
        return ChimahonPlatformIntegration.openExternalUrl(url)
    }

    actual fun close() {
        databaseDriver.close()
    }
}
