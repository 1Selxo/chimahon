package app.chimahon.shared

import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.SourceRegistry
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import tachiyomi.core.database.DesktopDatabaseDriverFactory
import tachiyomi.core.platform.javascript.DesktopJavaScriptRuntimeFactory
import tachiyomi.core.platform.javascript.JavaScriptRuntimeFactory
import tachiyomi.core.platform.storage.DesktopPlatformStorageDirectories
import tachiyomi.core.platform.storage.PlatformStorageDirectories
import tachiyomi.data.Database
import tachiyomi.data.DatabaseHandler
import tachiyomi.data.DesktopDatabaseHandler
import java.awt.Desktop
import java.net.URI

internal actual class ChimahonPlatformServices actual constructor() {
    actual val platformName: String = "Desktop"
    actual val backgroundState: String = "Coroutine scheduler ready"
    actual val storageDirectories: PlatformStorageDirectories = DesktopPlatformStorageDirectories(APP_NAME)
    actual val sourceRegistry: SourceRegistry = SourceRegistry()
    actual val apkExtensionManager = ChimahonPlatformApkExtensionManager(storageDirectories, sourceRegistry)
    private val databaseDriver = DesktopDatabaseDriverFactory(
        databaseDirectory = storageDirectories.filesDir / DATABASE_DIRECTORY,
    ).create(Database.Schema, DATABASE_NAME)
    actual val database: Database = createDatabase(databaseDriver)
    actual val databaseHandler: DatabaseHandler = DesktopDatabaseHandler(database, databaseDriver)
    actual val javaScriptRuntimeFactory: JavaScriptRuntimeFactory = DesktopJavaScriptRuntimeFactory

    actual fun currentTimeMillis(): Long = System.currentTimeMillis()

    actual fun resolveExternalMangaUrl(source: CatalogueSource, manga: SManga): String? {
        return when (source) {
            is HttpSource -> runCatching { source.getMangaUrl(manga) }.getOrNull()
            else -> manga.url
        }
    }

    actual fun openExternalUrl(url: String): Boolean {
        val uri = runCatching { URI(url) }.getOrNull() ?: return false
        if (uri.scheme?.lowercase() !in setOf("http", "https")) return false
        if (!Desktop.isDesktopSupported()) return false

        return runCatching {
            val desktop = Desktop.getDesktop()
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri)
                true
            } else {
                false
            }
        }.getOrDefault(false)
    }

    actual fun close() {
        databaseDriver.close()
    }
}
