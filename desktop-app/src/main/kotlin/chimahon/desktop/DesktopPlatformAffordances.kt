package chimahon.desktop

import java.awt.Desktop
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.Window
import java.awt.datatransfer.StringSelection
import java.nio.file.Files
import java.nio.file.Path

internal object DesktopPlatformAffordances {
    private const val appName = "chimahon"
    private val directories = DesktopAppDirectories.resolve(appName)
    val menuShortcutUsesMeta: Boolean
        get() = isMacOs

    fun configureRuntime() {
        System.setProperty("apple.awt.application.name", "Chimahon")
        System.setProperty("apple.laf.useScreenMenuBar", "true")
        System.setProperty("sun.awt.application.name", "Chimahon")
        ensureDirectories()
    }

    fun configureWindow(window: Window) {
        window.minimumSize = Dimension(900, 580)
    }

    fun openDirectory(directory: DesktopDirectory): Boolean {
        ensureDirectories()
        return openPath(
            when (directory) {
                DesktopDirectory.Files -> directories.files
                DesktopDirectory.Cache -> directories.cache
                DesktopDirectory.Downloads -> directories.downloads
            },
        )
    }

    fun copyDirectoryPath(directory: DesktopDirectory): Boolean {
        ensureDirectories()
        val path = when (directory) {
            DesktopDirectory.Files -> directories.files
            DesktopDirectory.Cache -> directories.cache
            DesktopDirectory.Downloads -> directories.downloads
        }
        return copyText(path.toString())
    }

    private fun ensureDirectories() {
        listOf(
            directories.files,
            directories.cache,
            directories.temporary,
            directories.downloads,
        ).forEach { path ->
            runCatching {
                Files.createDirectories(path)
            }
        }
    }
}

internal enum class DesktopDirectory {
    Files,
    Cache,
    Downloads,
}

private data class DesktopAppDirectories(
    val files: Path,
    val cache: Path,
    val temporary: Path,
    val downloads: Path,
) {
    companion object {
        fun resolve(appName: String): DesktopAppDirectories {
            val directoryName = appName.sanitizePathSegment()
            val userHome = Path.of(System.getProperty("user.home"))
            return DesktopAppDirectories(
                files = defaultDataRoot(userHome).resolve(directoryName),
                cache = defaultCacheRoot(userHome).resolve(directoryName),
                temporary = Path.of(System.getProperty("java.io.tmpdir")).resolve(directoryName),
                downloads = userHome.resolve("Downloads").resolve(directoryName),
            )
        }

        private fun defaultDataRoot(userHome: Path): Path {
            return when {
                isWindows -> System.getenv("APPDATA")?.let(Path::of)
                    ?: userHome.resolve("AppData").resolve("Roaming")
                isMacOs -> userHome.resolve("Library").resolve("Application Support")
                else -> System.getenv("XDG_DATA_HOME")?.let(Path::of)
                    ?: userHome.resolve(".local").resolve("share")
            }
        }

        private fun defaultCacheRoot(userHome: Path): Path {
            return when {
                isWindows -> System.getenv("LOCALAPPDATA")?.let(Path::of)
                    ?: userHome.resolve("AppData").resolve("Local")
                isMacOs -> userHome.resolve("Library").resolve("Caches")
                else -> System.getenv("XDG_CACHE_HOME")?.let(Path::of)
                    ?: userHome.resolve(".cache")
            }
        }
    }
}

private val osName = System.getProperty("os.name").lowercase()
private val isWindows = osName.contains("win")
private val isMacOs = osName.contains("mac")

private fun openPath(path: Path): Boolean {
    if (!Files.exists(path)) return false
    return runCatching {
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            if (desktop.isSupported(Desktop.Action.OPEN)) {
                desktop.open(path.toFile())
                return true
            }
        }
        false
    }.getOrDefault(false) || when {
        isWindows -> launchCommand("explorer.exe", path.toString())
        isMacOs -> launchCommand("open", path.toString())
        else -> launchCommand("xdg-open", path.toString())
    }
}

private fun copyText(text: String): Boolean {
    if (GraphicsEnvironment.isHeadless()) return false
    return runCatching {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
        true
    }.getOrDefault(false)
}

private fun launchCommand(vararg command: String): Boolean {
    return runCatching {
        ProcessBuilder(*command)
            .redirectErrorStream(true)
            .start()
        true
    }.getOrDefault(false)
}

private fun String.sanitizePathSegment(): String {
    val sanitized = trim()
        .map { character ->
            when (character) {
                '/', '\\', ':', '*', '?', '"', '<', '>', '|' -> '_'
                else -> character
            }
        }
        .joinToString("")
        .trim('.')
    return sanitized.ifBlank { "app" }
}
