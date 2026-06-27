package chimahon.desktop

import java.awt.Desktop
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.Window
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.JTextArea

internal object DesktopPlatformAffordances {
    private const val appName = "chimahon"
    private const val displayName = "Chimahon"
    private const val repositoryUrl = "https://github.com/sohilsayed/chimahon"
    private const val releasesUrl = "$repositoryUrl/releases/latest"
    private const val issuesUrl = "$repositoryUrl/issues"
    private const val discordUrl = "https://discord.gg/Ak2sW9Nvr9"
    private val directories = DesktopAppDirectories.resolve(appName)
    val menuShortcutUsesMeta: Boolean
        get() = isMacOs
    val menuShortcutLabel: String
        get() = if (isMacOs) "Cmd" else "Ctrl"

    fun configureRuntime() {
        setDefaultProperty("apple.awt.application.name", displayName)
        setDefaultProperty("apple.awt.application.appearance", "system")
        setDefaultProperty("apple.laf.useScreenMenuBar", "true")
        setDefaultProperty("awt.useSystemAAFontSettings", "on")
        setDefaultProperty("swing.aatext", "true")
        setDefaultProperty("sun.awt.application.name", displayName)
        setDefaultProperty("com.apple.mrj.application.apple.menu.about.name", displayName)
        ensureDirectories()
    }

    fun configureWindow(window: Window) {
        window.minimumSize = Dimension(900, 580)
    }

    fun toggleFullScreen(window: Window): Boolean {
        val device = window.graphicsConfiguration?.device
            ?: GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice

        return runCatching {
            if (!device.isFullScreenSupported) return@runCatching false
            device.fullScreenWindow = if (device.fullScreenWindow == window) null else window
            true
        }.getOrDefault(false)
    }

    fun openDirectory(directory: DesktopDirectory): Boolean {
        ensureDirectories()
        return openPath(pathFor(directory))
    }

    fun copyDirectoryPath(directory: DesktopDirectory): Boolean {
        ensureDirectories()
        return copyText(pathFor(directory).toString())
    }

    fun copyStorageSummary(): Boolean {
        return copyText(storageSummary())
    }

    fun copyDiagnosticInfo(): Boolean {
        return copyText(
            buildString {
                appendLine(aboutSummary())
                appendLine()
                appendLine(storageSummary())
            }.trimEnd(),
        )
    }

    fun shareStorageSummary(): Boolean {
        return shareText(
            text = storageSummary(),
            title = "$displayName storage paths",
        )
    }

    fun copyTextToClipboard(text: String): Boolean {
        return copyText(text)
    }

    fun shareText(
        text: String,
        title: String = displayName,
    ): Boolean {
        val body = text.trim()
        if (body.isBlank()) return false
        return openMailDraft(title, body) || copyText(body)
    }

    fun showTextDialog(
        parent: Window,
        title: String,
        text: String,
    ): Boolean {
        if (GraphicsEnvironment.isHeadless()) return copyText(text)

        return runCatching {
            val textArea = JTextArea(text).apply {
                isEditable = false
                lineWrap = false
                rows = 24
                columns = 58
                caretPosition = 0
            }
            JOptionPane.showMessageDialog(
                parent,
                JScrollPane(textArea),
                title,
                JOptionPane.INFORMATION_MESSAGE,
            )
            true
        }.getOrDefault(false) || copyText(text)
    }

    fun showAboutDialog(parent: Window): Boolean {
        return showTextDialog(
            parent = parent,
            title = "About $displayName",
            text = aboutSummary(),
        )
    }

    fun openProjectWebsite(): Boolean {
        return browseUri(URI(repositoryUrl))
    }

    fun openLatestReleases(): Boolean {
        return browseUri(URI(releasesUrl))
    }

    fun openIssueTracker(): Boolean {
        return browseUri(URI(issuesUrl))
    }

    fun openDiscord(): Boolean {
        return browseUri(URI(discordUrl))
    }

    private fun setDefaultProperty(
        key: String,
        value: String,
    ) {
        if (System.getProperty(key).isNullOrBlank()) {
            System.setProperty(key, value)
        }
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

    private fun pathFor(directory: DesktopDirectory): Path {
        return when (directory) {
            DesktopDirectory.Files -> directories.files
            DesktopDirectory.Cache -> directories.cache
            DesktopDirectory.Temporary -> directories.temporary
            DesktopDirectory.Downloads -> directories.downloads
        }
    }

    private fun aboutSummary(): String {
        return buildString {
            appendLine("$displayName ${appVersionLabel()}")
            appendLine("Native desktop shell")
            appendLine()
            appendLine("Project: $repositoryUrl")
            appendLine("Latest releases: $releasesUrl")
            appendLine("Issues: $issuesUrl")
            appendLine()
            appendLine("Java: ${System.getProperty("java.version")} (${System.getProperty("java.vendor")})")
            appendLine("Runtime: ${System.getProperty("java.runtime.name")}")
            appendLine("OS: ${System.getProperty("os.name")} ${System.getProperty("os.version")} (${System.getProperty("os.arch")})")
            appendLine("Data: ${directories.files}")
        }.trimEnd()
    }

    private fun appVersionLabel(): String {
        return System.getProperty("chimahon.desktop.version")
            ?.takeIf { it.isNotBlank() }
            ?.let { "v$it" }
            ?: "development build"
    }

    private fun storageSummary(): String {
        ensureDirectories()
        return buildString {
            appendLine("$displayName storage paths")
            DesktopDirectory.entries.forEach { directory ->
                appendLine("${directory.title}: ${pathFor(directory)}")
            }
        }.trimEnd()
    }
}

internal enum class DesktopDirectory(val title: String) {
    Downloads("Downloads"),
    Files("Data"),
    Cache("Cache"),
    Temporary("Temporary"),
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

private fun browseUri(uri: URI): Boolean {
    return runCatching {
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(uri)
                return true
            }
        }
        false
    }.getOrDefault(false) || when {
        isWindows -> launchCommand("rundll32.exe", "url.dll,FileProtocolHandler", uri.toString())
        isMacOs -> launchCommand("open", uri.toString())
        else -> launchCommand("xdg-open", uri.toString())
    }
}

private fun copyText(text: String): Boolean {
    return copyTextWithToolkit(text) || copyTextWithCommand(text)
}

private fun copyTextWithToolkit(text: String): Boolean {
    if (GraphicsEnvironment.isHeadless()) return false
    return runCatching {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
        true
    }.getOrDefault(false)
}

private fun copyTextWithCommand(text: String): Boolean {
    return when {
        isWindows -> launchCommandWithInput(text, "clip.exe")
        isMacOs -> launchCommandWithInput(text, "pbcopy")
        else -> launchCommandWithInput(text, "wl-copy") ||
            launchCommandWithInput(text, "xclip", "-selection", "clipboard") ||
            launchCommandWithInput(text, "xsel", "--clipboard", "--input")
    }
}

private fun openMailDraft(subject: String, body: String): Boolean {
    val mailto = URI("mailto:?subject=${subject.urlEncoded()}&body=${body.urlEncoded()}")
    return runCatching {
        if (!Desktop.isDesktopSupported()) return@runCatching false
        val desktop = Desktop.getDesktop()
        if (!desktop.isSupported(Desktop.Action.MAIL)) return@runCatching false
        desktop.mail(mailto)
        true
    }.getOrDefault(false) || when {
        isWindows -> launchCommand("rundll32.exe", "url.dll,FileProtocolHandler", mailto.toString())
        isMacOs -> launchCommand("open", mailto.toString())
        else -> launchCommand("xdg-email", "--subject", subject, "--body", body)
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

private fun launchCommandWithInput(
    input: String,
    vararg command: String,
): Boolean {
    return runCatching {
        val process = ProcessBuilder(*command)
            .redirectErrorStream(true)
            .start()
        process.outputStream.use { output ->
            output.write(input.toByteArray(StandardCharsets.UTF_8))
        }
        process.waitFor(3, TimeUnit.SECONDS) && process.exitValue() == 0
    }.getOrDefault(false)
}

private fun String.urlEncoded(): String {
    return URLEncoder
        .encode(this, StandardCharsets.UTF_8)
        .replace("+", "%20")
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
