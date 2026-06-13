package app.chimahon.shared

internal data class ChimahonPlatformInfo(
    val platformName: String,
    val platformVersion: String,
    val deviceModel: String,
    val appVersion: String?,
    val buildNumber: String?,
)

internal data class ChimahonStoragePaths(
    val filesDir: String,
    val cacheDir: String,
    val downloadsDir: String,
    val temporaryDir: String,
)

internal expect object ChimahonPlatformIntegration {
    fun openExternalUrl(url: String): Boolean

    fun openPath(path: String): Boolean

    fun revealPath(path: String): Boolean

    fun copyText(text: String): Boolean

    fun shareText(text: String, title: String? = null): Boolean

    fun shareFile(path: String, title: String? = null): Boolean

    fun platformInfo(): ChimahonPlatformInfo

    fun storagePaths(): ChimahonStoragePaths
}
