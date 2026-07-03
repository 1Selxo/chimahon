package chimahon.desktop

import app.chimahon.shared.desktopshortcutsui.ChimahonDesktopShortcutModifierLabels
import app.chimahon.shared.desktopshortcutsui.ChimahonDesktopShortcutPlatform
import java.awt.Desktop
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.Window
import java.awt.desktop.OpenFilesHandler
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import javax.swing.JFileChooser
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
    val shortcutPlatform: ChimahonDesktopShortcutPlatform
        get() = when {
            isMacOs -> ChimahonDesktopShortcutPlatform.Mac
            isWindows -> ChimahonDesktopShortcutPlatform.Windows
            isChromeOs -> ChimahonDesktopShortcutPlatform.ChromeOs
            isLinux -> ChimahonDesktopShortcutPlatform.Linux
            else -> ChimahonDesktopShortcutPlatform.Unknown
        }
    val shortcutModifierLabels: ChimahonDesktopShortcutModifierLabels
        get() = ChimahonDesktopShortcutModifierLabels.forPlatform(shortcutPlatform)
    val platformShortcutLabels: DesktopPlatformShortcutLabels
        get() {
            val labels = shortcutModifierLabels
            val fullScreenShortcut = if (isMacOs) {
                "${labels.primary}+${labels.control}+F"
            } else {
                "F11"
            }
            return DesktopPlatformShortcutLabels(
                platform = shortcutPlatform,
                menuShortcut = labels.primary,
                control = labels.control,
                meta = labels.meta,
                alt = labels.alt,
                shift = labels.shift,
                fullScreen = fullScreenShortcut,
                readerZoomWheel = "${labels.primary}+Mouse wheel",
                readerChapterWheel = "${labels.shift}+Mouse wheel",
                readerPageScrollWheel = "${labels.alt}+Mouse wheel",
            )
        }

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

    fun revealDirectory(directory: DesktopDirectory): Boolean {
        ensureDirectories()
        return revealPath(pathFor(directory)) || openDirectory(directory)
    }

    fun openPathDialog(parent: Window): Boolean {
        ensureDirectories()
        if (GraphicsEnvironment.isHeadless()) return openDirectory(DesktopDirectory.Files)

        return runCatching {
            val chooser = JFileChooser(pathFor(DesktopDirectory.Downloads).toFile()).apply {
                dialogTitle = "Open file or folder"
                fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
                isMultiSelectionEnabled = true
            }
            if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) return@runCatching false
            openDroppedFiles(chooser.selectedFiles.toList())
        }.getOrDefault(false)
    }

    fun openDroppedFiles(files: List<File>): Boolean {
        val existing = files
            .mapNotNull { file -> runCatching { file.toPath().toAbsolutePath().normalize() }.getOrNull() }
            .filter(Files::exists)
            .distinct()
        if (existing.isEmpty()) return false

        if (existing.size == 1) {
            val target = existing.single()
            return if (Files.isDirectory(target)) {
                openPath(target)
            } else {
                revealPath(target) || openPath(target)
            }
        }

        val copiedPaths = copyText(existing.joinToString(System.lineSeparator()))
        val openedParent = existing.firstOrNull()?.parent?.let(::openPath) == true
        return openedParent || copiedPaths
    }

    fun openDroppedText(text: String): Boolean {
        val candidates = text
            .lineSequence()
            .map(String::trim)
            .filter(String::isNotBlank)
            .take(12)
            .toList()
        if (candidates.isEmpty()) return false

        candidates.forEach { candidate ->
            candidate.localPathOrNull()?.let { path ->
                if (openDroppedFiles(listOf(path.toFile()))) return true
            }
            candidate.uriOrNull()?.let { uri ->
                if (uri.scheme.equals("file", ignoreCase = true)) {
                    val path = runCatching { Path.of(uri) }.getOrNull()
                    if (path != null && openDroppedFiles(listOf(path.toFile()))) return true
                }
                if (uri.scheme.equals("http", ignoreCase = true) || uri.scheme.equals("https", ignoreCase = true)) {
                    if (browseUri(uri)) return true
                }
            }
        }

        return copyText(candidates.joinToString(System.lineSeparator()))
    }

    fun installOpenFileHandler(): AutoCloseable {
        if (!Desktop.isDesktopSupported()) return AutoCloseable {}

        val desktop = runCatching { Desktop.getDesktop() }.getOrNull() ?: return AutoCloseable {}
        if (!desktop.isSupported(Desktop.Action.APP_OPEN_FILE)) return AutoCloseable {}

        val handler = OpenFilesHandler { event ->
            openDroppedFiles(event.files)
        }
        return runCatching {
            desktop.setOpenFileHandler(handler)
            AutoCloseable {
                runCatching {
                    desktop.setOpenFileHandler(null)
                }
            }
        }.getOrElse {
            AutoCloseable {}
        }
    }

    fun copyDirectoryPath(directory: DesktopDirectory): Boolean {
        ensureDirectories()
        return copyText(pathFor(directory).toString())
    }

    fun copyStorageSummary(): Boolean {
        return copyText(storageSummary())
    }

    fun copyDiagnosticInfo(): Boolean {
        return copyText(diagnosticInfo())
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

    fun diagnosticInfo(): String {
        return buildString {
            appendLine(aboutSummary())
            appendLine()
            appendLine(storageSummary())
            appendLine()
            appendLine("Desktop integration")
            appendLine("Shortcut platform: ${shortcutPlatform.name}")
            appendLine("Menu shortcut: $menuShortcutLabel")
            appendLine("Full screen shortcut: ${platformShortcutLabels.fullScreen}")
            appendLine("Reader zoom wheel: ${platformShortcutLabels.readerZoomWheel}")
            appendLine("Desktop API: ${Desktop.isDesktopSupported()}")
            appendLine("Browse action: ${desktopActionSupported(Desktop.Action.BROWSE)}")
            appendLine("Open action: ${desktopActionSupported(Desktop.Action.OPEN)}")
            appendLine("Mail action: ${desktopActionSupported(Desktop.Action.MAIL)}")
            appendLine("Open-file handler: ${desktopActionSupported(Desktop.Action.APP_OPEN_FILE)}")
            appendLine("Headless: ${GraphicsEnvironment.isHeadless()}")
            appendLine("Working directory: ${Path.of("").toAbsolutePath().normalize()}")
            appendLine("User home: ${System.getProperty("user.home")}")
            appendLine("Temp root: ${System.getProperty("java.io.tmpdir")}")
        }.trimEnd()
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
        DesktopDirectory.entries.map(::pathFor).forEach { path ->
            runCatching {
                Files.createDirectories(path)
            }
        }
    }

    private fun pathFor(directory: DesktopDirectory): Path {
        return when (directory) {
            DesktopDirectory.Files -> directories.files
            DesktopDirectory.Database -> directories.database
            DesktopDirectory.Extensions -> directories.extensions
            DesktopDirectory.Cache -> directories.cache
            DesktopDirectory.Logs -> directories.logs
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
            appendLine("Downloads: ${directories.downloads}")
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
    Database("Database"),
    Extensions("Extensions"),
    Cache("Cache"),
    Logs("Logs"),
    Temporary("Temporary"),
}

internal data class DesktopPlatformShortcutLabels(
    val platform: ChimahonDesktopShortcutPlatform,
    val menuShortcut: String,
    val control: String,
    val meta: String,
    val alt: String,
    val shift: String,
    val fullScreen: String,
    val readerZoomWheel: String,
    val readerChapterWheel: String,
    val readerPageScrollWheel: String,
)

private data class DesktopAppDirectories(
    val files: Path,
    val database: Path,
    val extensions: Path,
    val cache: Path,
    val logs: Path,
    val temporary: Path,
    val downloads: Path,
) {
    companion object {
        fun resolve(appName: String): DesktopAppDirectories {
            val directoryName = appName.sanitizePathSegment()
            val userHome = Path.of(System.getProperty("user.home"))
            val files = defaultDataRoot(userHome).resolve(directoryName)
            val cache = defaultCacheRoot(userHome).resolve(directoryName)
            return DesktopAppDirectories(
                files = files,
                database = files.resolve("database"),
                extensions = files.resolve("apk-extensions"),
                cache = cache,
                logs = cache.resolve("logs"),
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
private val isChromeOs = osName.contains("chrome") || osName.contains("cros")
private val isLinux = osName.contains("linux") || osName.contains("nix") || osName.contains("nux")

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

private fun revealPath(path: Path): Boolean {
    if (!Files.exists(path)) return false
    if (Files.isDirectory(path)) return openPath(path)

    return when {
        isWindows -> launchCommand("explorer.exe", "/select,${path.toAbsolutePath()}")
        isMacOs -> launchCommand("open", "-R", path.toString())
        else -> path.parent?.let(::openPath) == true
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

private fun String.localPathOrNull(): Path? {
    val candidate = trim()
    if (candidate.isBlank()) return null
    if (candidate.startsWith("file:", ignoreCase = true)) {
        return runCatching { Path.of(URI(candidate)).toAbsolutePath().normalize() }
            .getOrNull()
            ?.takeIf(Files::exists)
    }

    return runCatching { Path.of(candidate.expandUserHome()).toAbsolutePath().normalize() }
        .getOrNull()
        ?.takeIf(Files::exists)
}

private fun String.uriOrNull(): URI? {
    val candidate = trim()
    if (candidate.isBlank() || candidate.any(Char::isWhitespace)) return null
    return runCatching {
        val uri = URI(candidate)
        if (uri.scheme.isNullOrBlank()) {
            if (candidate.looksLikeHost()) URI("https://$candidate") else null
        } else {
            uri
        }
    }.getOrNull()
}

private fun String.looksLikeHost(): Boolean {
    if (startsWith("/") || startsWith("#")) return false
    val host = substringBefore('/').substringBefore('?')
    return host.startsWith("www.", ignoreCase = true) ||
        (host.contains('.') && host.any(Char::isLetter))
}

private fun String.expandUserHome(): String {
    if (this == "~") return System.getProperty("user.home").orEmpty()
    if (startsWith("~/") || startsWith("~\\")) {
        return System.getProperty("user.home").orEmpty() + substring(1)
    }
    return this
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
    if (command.isEmpty()) return false
    return runCatching {
        val process = ProcessBuilder(*command)
            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
            .redirectError(ProcessBuilder.Redirect.DISCARD)
            .start()
        process.outputStream.close()
        if (process.waitFor(3, TimeUnit.SECONDS)) {
            process.exitValue() == 0
        } else {
            true
        }
    }.getOrDefault(false)
}

private fun launchCommandWithInput(
    input: String,
    vararg command: String,
): Boolean {
    if (command.isEmpty()) return false
    return runCatching {
        val process = ProcessBuilder(*command)
            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
            .redirectError(ProcessBuilder.Redirect.DISCARD)
            .start()
        process.outputStream.use { output ->
            output.write(input.toByteArray(StandardCharsets.UTF_8))
        }
        if (process.waitFor(3, TimeUnit.SECONDS)) {
            process.exitValue() == 0
        } else {
            process.destroyForcibly()
            false
        }
    }.getOrDefault(false)
}

private fun desktopActionSupported(action: Desktop.Action): Boolean {
    if (!Desktop.isDesktopSupported()) return false
    return runCatching {
        Desktop.getDesktop().isSupported(action)
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
