package tachiyomi.core.platform.storage

import okio.Path
import okio.Path.Companion.toPath
import java.io.File

class DesktopPlatformStorageDirectories(
    private val appName: String = "chimahon",
    private val userHome: Path = System.getProperty("user.home").toPath(),
    private val tempRoot: Path = System.getProperty("java.io.tmpdir").toPath(),
    appDataRoot: Path? = null,
    cacheRoot: Path? = null,
) : PlatformStorageDirectories {

    override val cacheDir: Path = (cacheRoot ?: defaultCacheRoot()).resolveDirectoryName(appName)

    override val filesDir: Path = (appDataRoot ?: defaultDataRoot()).resolveDirectoryName(appName)

    override val temporaryDir: Path = tempRoot.resolveDirectoryName(appName)

    override fun defaultDownloadsDir(appName: String): Path {
        return (userHome / "Downloads").resolveDirectoryName(appName)
    }

    override fun fileUri(path: Path): String {
        return File(path.toString()).toURI().toString()
    }

    private fun defaultDataRoot(): Path {
        return when {
            isWindows -> (System.getenv("APPDATA")?.toPath() ?: userHome / "AppData" / "Roaming")
            isMacOs -> userHome / "Library" / "Application Support"
            else -> System.getenv("XDG_DATA_HOME")?.toPath() ?: userHome / ".local" / "share"
        }
    }

    private fun defaultCacheRoot(): Path {
        return when {
            isWindows -> (System.getenv("LOCALAPPDATA")?.toPath() ?: userHome / "AppData" / "Local")
            isMacOs -> userHome / "Library" / "Caches"
            else -> System.getenv("XDG_CACHE_HOME")?.toPath() ?: userHome / ".cache"
        }
    }

    private companion object {
        val osName: String = System.getProperty("os.name").lowercase()
        val isWindows: Boolean = osName.contains("win")
        val isMacOs: Boolean = osName.contains("mac")
    }
}
