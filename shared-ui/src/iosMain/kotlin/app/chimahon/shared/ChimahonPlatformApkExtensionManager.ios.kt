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
        error("Android APK extensions cannot run on iOS. Install a JavaScript extension instead.")
    }

    actual suspend fun uninstall(packageId: String): Boolean = false

    actual fun status(): ChimahonApkExtensionManagerStatus {
        return ChimahonApkExtensionManagerStatus(
            isSupported = false,
            installedExtensionCount = 0,
            registeredSourceCount = 0,
        )
    }

    actual fun close() = Unit
}
