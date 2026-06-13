package app.chimahon.shared

import eu.kanade.tachiyomi.source.SourceRegistry
import tachiyomi.core.platform.storage.PlatformStorageDirectories

internal actual class ChimahonPlatformApkExtensionManager actual constructor(
    storageDirectories: PlatformStorageDirectories,
    sourceRegistry: SourceRegistry,
) {
    actual suspend fun reload(): List<ChimahonInstalledExtensionEntry> = emptyList()

    actual suspend fun install(
        extension: ChimahonRepoExtensionEntry,
        apkBytes: ByteArray,
    ): ChimahonInstalledExtensionEntry {
        error("Android APK extensions are currently supported by the desktop compatibility engine only.")
    }

    actual fun close() = Unit
}
