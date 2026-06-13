package app.chimahon.shared

import tachiyomi.core.platform.storage.DesktopPlatformStorageDirectories
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path

internal actual object ChimahonPlatformIntegration {
    private val storageDirectories = DesktopPlatformStorageDirectories(APP_NAME)

    actual fun openExternalUrl(url: String): Boolean {
        val uri = url.httpUriOrNull() ?: return false

        return performDesktopAction(Desktop.Action.BROWSE) { desktop ->
            desktop.browse(uri)
        } || launchPlatformCommand(uri.toString())
    }

    actual fun openPath(path: String): Boolean {
        val target = path.localPathOrNull() ?: return false
        if (!Files.exists(target)) return false

        return performDesktopAction(Desktop.Action.OPEN) { desktop ->
            desktop.open(target.toFile())
        } || launchPlatformCommand(target.toString())
    }

    actual fun revealPath(path: String): Boolean {
        val target = path.localPathOrNull() ?: return false
        if (!Files.exists(target)) return false
        if (Files.isDirectory(target)) return openPath(target.toString())

        return when {
            isWindows -> launchCommand("explorer.exe", "/select,$target")
            isMacOs -> launchCommand("open", "-R", target.toString())
            else -> target.parent?.let { openPath(it.toString()) } ?: false
        }
    }

    actual fun copyText(text: String): Boolean {
        return runCatching {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
            true
        }.getOrDefault(false)
    }

    actual fun shareText(text: String, title: String?): Boolean = false

    actual fun shareFile(path: String, title: String?): Boolean = false

    actual fun platformInfo(): ChimahonPlatformInfo {
        val osName = System.getProperty("os.name").orEmpty()
        val osVersion = System.getProperty("os.version").orEmpty()
        val implementationVersion = ChimahonPlatformIntegration::class.java
            .`package`
            ?.implementationVersion

        return ChimahonPlatformInfo(
            platformName = "Desktop",
            platformVersion = listOf(osName, osVersion).filter(String::isNotBlank).joinToString(" "),
            deviceModel = System.getProperty("os.arch").orEmpty(),
            appVersion = implementationVersion,
            buildNumber = System.getProperty("chimahon.build.number"),
        )
    }

    actual fun storagePaths(): ChimahonStoragePaths {
        return ChimahonStoragePaths(
            filesDir = storageDirectories.filesDir.toString(),
            cacheDir = storageDirectories.cacheDir.toString(),
            downloadsDir = storageDirectories.defaultDownloadsDir(APP_NAME).toString(),
            temporaryDir = storageDirectories.temporaryDir.toString(),
        )
    }
}

private val osName = System.getProperty("os.name").lowercase()
private val isWindows = osName.contains("win")
private val isMacOs = osName.contains("mac")

private fun String.httpUriOrNull(): URI? {
    val uri = runCatching { URI(this) }.getOrNull() ?: return null
    return uri.takeIf { it.scheme?.lowercase() in setOf("http", "https") }
}

private fun String.localPathOrNull(): Path? {
    return runCatching {
        val path = if (startsWith("file:", ignoreCase = true)) {
            Path.of(URI(this))
        } else {
            Path.of(this)
        }
        path.toAbsolutePath().normalize()
    }.getOrNull()
}

private inline fun performDesktopAction(
    action: Desktop.Action,
    operation: (Desktop) -> Unit,
): Boolean {
    if (!Desktop.isDesktopSupported()) return false

    return runCatching {
        val desktop = Desktop.getDesktop()
        if (!desktop.isSupported(action)) return false
        operation(desktop)
        true
    }.getOrDefault(false)
}

private fun launchPlatformCommand(target: String): Boolean {
    return when {
        isWindows -> launchCommand("explorer.exe", target)
        isMacOs -> launchCommand("open", target)
        else -> launchCommand("xdg-open", target)
    }
}

private fun launchCommand(vararg command: String): Boolean {
    return runCatching {
        ProcessBuilder(*command)
            .redirectErrorStream(true)
            .start()
        true
    }.getOrDefault(false)
}
