package app.chimahon.shared

import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.SourceRegistry
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import okio.FileSystem
import platform.Foundation.NSDate
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
    actual val backgroundState: String = "Foreground scheduler; iOS background work follows system limits"
    actual val storageDirectories: PlatformStorageDirectories = IosPlatformStorageDirectories(APP_NAME)

    init {
        storageDirectories.ensureChimahonDirectories()
    }

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

    actual fun currentTimeMillis(): Long {
        return (NSDate().timeIntervalSince1970 * 1_000.0).toLong()
    }

    actual fun resolveExternalMangaUrl(source: CatalogueSource, manga: SManga): String? {
        return when (source) {
            is HttpSource -> runCatching { source.getMangaUrl(manga) }.getOrNull() ?: manga.url
            else -> manga.url
        }
    }

    actual fun openExternalUrl(url: String): Boolean {
        return ChimahonPlatformIntegration.openExternalUrl(url)
    }

    actual fun close() {
        apkExtensionManager.close()
        databaseDriver.close()
    }
}
