package tachiyomi.core.platform.storage

import okio.Path

interface PlatformStorageDirectories {
    val cacheDir: Path
    val filesDir: Path
    val temporaryDir: Path

    fun defaultDownloadsDir(appName: String): Path

    fun fileUri(path: Path): String
}

fun Path.resolveDirectoryName(name: String): Path {
    return this / sanitizePathSegment(name)
}

internal fun sanitizePathSegment(name: String): String {
    val sanitized = name
        .trim()
        .map { char ->
            when (char) {
                '/', '\\', ':', '*', '?', '"', '<', '>', '|' -> '_'
                else -> char
            }
        }
        .joinToString("")
        .trim('.')

    return sanitized.ifBlank { "app" }
}
