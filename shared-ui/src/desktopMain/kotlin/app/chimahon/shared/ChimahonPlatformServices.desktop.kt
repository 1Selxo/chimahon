package app.chimahon.shared

import eu.kanade.tachiyomi.source.CatalogueSource
import eu.kanade.tachiyomi.source.SourceRegistry
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import tachiyomi.core.database.DesktopDatabaseDriverFactory
import tachiyomi.core.platform.javascript.DesktopJavaScriptRuntimeFactory
import tachiyomi.core.platform.javascript.JavaScriptRuntimeFactory
import tachiyomi.core.platform.storage.PlatformStorageDirectories
import tachiyomi.data.Database
import tachiyomi.data.DatabaseHandler
import tachiyomi.data.DesktopDatabaseHandler
import tachiyomi.mi.data.AnimeDatabase

internal actual class ChimahonPlatformServices actual constructor() {
    actual val platformName: String = "Desktop"
    actual val backgroundState: String = "Coroutine scheduler ready"
    actual val storageDirectories: PlatformStorageDirectories = createChimahonDesktopStorageDirectories()

    init {
        storageDirectories.ensureChimahonDirectories()
    }

    actual val sourceRegistry: SourceRegistry = SourceRegistry()
    actual val apkExtensionManager = ChimahonPlatformApkExtensionManager(storageDirectories, sourceRegistry)
    private val databaseDriver = DesktopDatabaseDriverFactory(
        databaseDirectory = storageDirectories.filesDir / DATABASE_DIRECTORY,
    ).create(Database.Schema, DATABASE_NAME)
    private val animeDatabaseDriver = DesktopDatabaseDriverFactory(
        databaseDirectory = storageDirectories.filesDir / DATABASE_DIRECTORY,
    ).create(AnimeDatabase.Schema, ANIME_DATABASE_NAME)
    actual val database: Database = createDatabase(databaseDriver)
    actual val animeDatabase: AnimeDatabase = createAnimeDatabase(animeDatabaseDriver)
    actual val databaseHandler: DatabaseHandler = DesktopDatabaseHandler(database, databaseDriver)
    actual val javaScriptRuntimeFactory: JavaScriptRuntimeFactory = DesktopJavaScriptRuntimeFactory

    actual fun currentTimeMillis(): Long = System.currentTimeMillis()

    actual fun resolveExternalMangaUrl(source: CatalogueSource, manga: SManga): String? {
        return when (source) {
            is HttpSource -> runCatching { source.getMangaUrl(manga) }.getOrNull()
            else -> manga.url
        }
    }

    actual suspend fun recognizeReaderOcr(
        bytes: ByteArray,
        languageCode: String,
    ): List<ChimahonReaderOcrBlock> {
        return DesktopGlensOcrClient.recognize(bytes = bytes, languageCode = languageCode)
    }

    actual fun openExternalUrl(url: String): Boolean {
        return ChimahonPlatformIntegration.openExternalUrl(url)
    }

    actual fun close() {
        apkExtensionManager.close()
        databaseDriver.close()
        animeDatabaseDriver.close()
    }
}
