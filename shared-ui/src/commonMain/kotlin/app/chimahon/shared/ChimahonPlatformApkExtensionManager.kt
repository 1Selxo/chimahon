package app.chimahon.shared

import eu.kanade.tachiyomi.source.SourceRegistry
import tachiyomi.core.platform.storage.PlatformStorageDirectories

internal expect class ChimahonPlatformApkExtensionManager(
    storageDirectories: PlatformStorageDirectories,
    sourceRegistry: SourceRegistry,
) {
    suspend fun reload(): List<ChimahonInstalledExtensionEntry>

    suspend fun install(
        extension: ChimahonRepoExtensionEntry,
        apkBytes: ByteArray,
    ): ChimahonInstalledExtensionEntry

    fun close()
}
