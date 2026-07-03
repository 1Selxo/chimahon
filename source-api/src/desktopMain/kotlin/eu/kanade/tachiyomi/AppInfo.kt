package eu.kanade.tachiyomi

/**
 * Desktop host information exposed to binary-compatible Tachiyomi extensions.
 */
@Suppress("UNUSED")
object AppInfo {
    fun getVersionCode(): Int = 1

    fun getVersionName(): String = "Chimahon Desktop"

    fun getSupportedImageMimeTypes(): List<String> = listOf(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp",
    )
}
