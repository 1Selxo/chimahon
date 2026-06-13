package app.chimahon.shared

import eu.kanade.tachiyomi.source.SourceRegistry
import tachiyomi.core.platform.storage.PlatformStorageDirectories

internal data class ChimahonApkExtensionManagerStatus(
    val isSupported: Boolean,
    val installedExtensionCount: Int,
    val registeredSourceCount: Int,
    val errors: List<String> = emptyList(),
)

internal expect class ChimahonPlatformApkExtensionManager(
    storageDirectories: PlatformStorageDirectories,
    sourceRegistry: SourceRegistry,
) {
    suspend fun reload(): List<ChimahonInstalledExtensionEntry>

    suspend fun install(
        extension: ChimahonRepoExtensionEntry,
        apkBytes: ByteArray,
    ): ChimahonInstalledExtensionEntry

    suspend fun uninstall(packageId: String): Boolean

    fun status(): ChimahonApkExtensionManagerStatus

    fun close()
}
