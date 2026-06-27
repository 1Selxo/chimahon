package app.chimahon.shared

import okio.Path
import okio.Path.Companion.toPath
import tachiyomi.core.platform.storage.DesktopPlatformStorageDirectories
import tachiyomi.core.platform.storage.PlatformStorageDirectories
import java.nio.file.Files
import java.nio.file.Path as NioPath

internal fun createChimahonDesktopStorageDirectories(): PlatformStorageDirectories {
    val userHome = desktopPathProperty("user.home", fallback = ".")
    val tempRoot = desktopPathProperty("java.io.tmpdir", fallback = ".")
    val primary = DesktopPlatformStorageDirectories(
        appName = APP_NAME,
        userHome = userHome,
        tempRoot = tempRoot,
    )
    if (primary.prepareChimahonDirectories().isSuccess) {
        return primary
    }

    return listOf(
        desktopStorageFallback(userHome / ".chimahon"),
        desktopStorageFallback(tempRoot / "chimahon"),
    ).firstOrNull { directories ->
        directories.prepareChimahonDirectories().isSuccess
    } ?: primary
}

internal fun PlatformStorageDirectories.ensureChimahonDirectories() {
    prepareChimahonDirectories()
}

private fun PlatformStorageDirectories.prepareChimahonDirectories(): Result<Unit> {
    return runCatching {
        chimahonDirectoryPaths().forEach { path ->
            Files.createDirectories(NioPath.of(path.toString()))
        }
    }
}

private fun PlatformStorageDirectories.chimahonDirectoryPaths(): List<Path> {
    return listOf(
        filesDir,
        filesDir / DATABASE_DIRECTORY,
        filesDir / DESKTOP_APK_EXTENSION_DIRECTORY,
        cacheDir,
        temporaryDir,
        defaultDownloadsDir(APP_NAME),
    )
}

private fun desktopStorageFallback(root: Path): PlatformStorageDirectories {
    return DesktopPlatformStorageDirectories(
        appName = APP_NAME,
        userHome = root,
        tempRoot = root / "tmp",
        appDataRoot = root / "data",
        cacheRoot = root / "cache",
    )
}

private fun desktopPathProperty(
    name: String,
    fallback: String,
): Path {
    return System.getProperty(name)
        ?.takeIf(String::isNotBlank)
        ?.toPath()
        ?: fallback.toPath()
}

private const val DESKTOP_APK_EXTENSION_DIRECTORY = "apk-extensions"
