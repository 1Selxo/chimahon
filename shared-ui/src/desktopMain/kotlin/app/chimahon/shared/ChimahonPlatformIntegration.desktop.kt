package app.chimahon.shared

import tachiyomi.core.platform.storage.PlatformStorageDirectories
import java.awt.Desktop
import java.awt.GraphicsEnvironment
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

internal actual object ChimahonPlatformIntegration {
    private val storageDirectories = createChimahonDesktopStorageDirectories()

    init {
        storageDirectories.ensureChimahonDirectories()
    }

    actual fun openExternalUrl(url: String): Boolean {
        val uri = url.externalUriOrNull() ?: return false
        val scheme = uri.scheme.lowercase()
        if (scheme == "mailto") {
            return performDesktopAction(Desktop.Action.MAIL) { desktop ->
                desktop.mail(uri)
            } || launchUrlCommand(uri.toString()) ||
                copyText(uri.toString())
        }

        return performDesktopAction(Desktop.Action.BROWSE) { desktop ->
            desktop.browse(uri)
        } || launchUrlCommand(uri.toString()) ||
            copyText(uri.toString())
    }

    actual fun openPath(path: String): Boolean {
        val target = path.localPathOrNull() ?: return false
        if (!Files.exists(target)) return false

        return performDesktopAction(Desktop.Action.OPEN) { desktop ->
            desktop.open(target.toFile())
        } || launchPathCommand(target.toString())
    }

    actual fun revealPath(path: String): Boolean {
        val target = path.localPathOrNull() ?: return false
        if (!Files.exists(target)) return false
        if (Files.isDirectory(target)) return openPath(target.toString())

        return when {
            isWindows -> launchCommand("explorer.exe", "/select,${target.toAbsolutePath()}")
            isMacOs -> launchCommand("open", "-R", target.toString())
            else -> revealPathWithFileManagerPortal(target) ||
                target.parent?.let { openPath(it.toString()) } == true
        }
    }

    actual fun copyText(text: String): Boolean {
        return copyTextWithToolkit(text) || copyTextWithCommand(text)
    }

    actual fun shareText(text: String, title: String?): Boolean {
        if (text.isBlank()) return false
        return openMailDraft(
            subject = title.shareTitle(),
            body = text,
        ) || copyText(text)
    }

    actual fun shareFile(path: String, title: String?): Boolean {
        val target = path.localPathOrNull() ?: return false
        if (!Files.isRegularFile(target)) return false

        val shared = when {
            isWindows -> launchCommand(
                "powershell.exe",
                "-NoProfile",
                "-NonInteractive",
                "-Command",
                "Start-Process -FilePath \$args[0] -Verb Share",
                target.toString(),
            )
            isMacOs -> launchCommand("open", "-a", "Mail", target.toString())
            else -> launchCommand(
                "xdg-email",
                "--subject",
                title.shareTitle(),
                "--attach",
                target.toString(),
            )
        }
        return shared || revealPath(target.toString()) || copyText(target.toString())
    }

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
        storageDirectories.ensureChimahonDirectories()
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
private val allowedExternalSchemes = setOf("http", "https", "mailto")

private fun String.externalUriOrNull(): URI? {
    val candidate = normalizedExternalUrlString() ?: return null
    val uri = runCatching { URI(candidate) }.getOrNull() ?: return null
    return uri.takeIf { it.scheme?.lowercase() in allowedExternalSchemes }
}

private fun String.localPathOrNull(): Path? {
    val candidate = trim()
    if (candidate.isBlank()) return null

    return candidate.localPathCandidates()
        .firstOrNull(Files::exists)
}

private fun String.normalizedExternalUrlString(): String? {
    val candidate = trim()
    if (candidate.isBlank() || candidate.any(Char::isWhitespace)) return null
    return when {
        candidate.hasUrlScheme() -> candidate
        candidate.contains("@") -> null
        candidate.looksLikeHost() -> "https://$candidate"
        else -> null
    }
}

private fun String.localPathCandidates(): List<Path> {
    if (startsWith("file:", ignoreCase = true)) {
        return runCatching {
            listOf(Path.of(URI(this)).toAbsolutePath().normalize())
        }.getOrDefault(emptyList())
    }

    val path = runCatching { Path.of(expandUserHome()) }.getOrNull() ?: return emptyList()
    val normalized = path.normalize()
    if (normalized.isAbsolute) return listOf(normalized)

    return buildList {
        add(normalized.toAbsolutePath().normalize())
        createChimahonDesktopStorageDirectories().storageRootPaths().forEach { root ->
            add(root.resolve(normalized).normalize())
        }
    }.distinct()
}

private fun String.hasUrlScheme(): Boolean {
    val colon = indexOf(':')
    if (colon <= 0) return false
    val scheme = take(colon)
    return scheme.first().isLetter() &&
        scheme.all { it.isLetterOrDigit() || it == '+' || it == '-' || it == '.' }
}

private fun String.looksLikeHost(): Boolean {
    if (startsWith("/") || startsWith("#")) return false
    val host = substringBefore('/').substringBefore('?')
    return host.startsWith("www.", ignoreCase = true) ||
        (host.contains('.') && host.any(Char::isLetter))
}

private fun PlatformStorageDirectories.storageRootPaths(): List<Path> {
    return listOf(
        filesDir,
        defaultDownloadsDir(APP_NAME),
        cacheDir,
        temporaryDir,
    ).mapNotNull { path ->
        runCatching { Path.of(path.toString()).toAbsolutePath().normalize() }.getOrNull()
    }
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

private fun revealPathWithFileManagerPortal(target: Path): Boolean {
    return launchCommand(
        "dbus-send",
        "--session",
        "--dest=org.freedesktop.FileManager1",
        "--type=method_call",
        "/org/freedesktop/FileManager1",
        "org.freedesktop.FileManager1.ShowItems",
        "array:string:${target.toUri()}",
        "string:",
    )
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
        isWindows -> launchCommandWithInput(text, "clip.exe") ||
            launchCommand(
                "powershell.exe",
                "-NoProfile",
                "-NonInteractive",
                "-Command",
                "Set-Clipboard -Value \$args[0]",
                text,
            )
        isMacOs -> launchCommandWithInput(text, "pbcopy")
        else -> launchCommandWithInput(text, "wl-copy") ||
            launchCommandWithInput(text, "xclip", "-selection", "clipboard") ||
            launchCommandWithInput(text, "xsel", "--clipboard", "--input")
    }
}

private fun launchUrlCommand(target: String): Boolean {
    return when {
        isWindows -> launchCommand("rundll32.exe", "url.dll,FileProtocolHandler", target)
        isMacOs -> launchCommand("open", target)
        else -> launchCommand("xdg-open", target) ||
            launchCommand("gio", "open", target)
    }
}

private fun launchPathCommand(target: String): Boolean {
    return when {
        isWindows -> launchCommand("explorer.exe", target)
        isMacOs -> launchCommand("open", target)
        else -> launchCommand("xdg-open", target) ||
            launchCommand("gio", "open", target)
    }
}

private fun openMailDraft(subject: String, body: String): Boolean {
    val mailto = URI("mailto:?subject=${subject.urlEncoded()}&body=${body.urlEncoded()}")
    return performDesktopAction(Desktop.Action.MAIL) { desktop ->
        desktop.mail(mailto)
    } || when {
        isWindows || isMacOs -> launchUrlCommand(mailto.toString())
        else -> launchCommand("xdg-email", "--subject", subject, "--body", body)
    }
}

private fun String?.shareTitle(): String {
    return this
        ?.trim()
        ?.takeIf(String::isNotBlank)
        ?: APP_NAME
}

private fun String.urlEncoded(): String {
    return URLEncoder
        .encode(this, StandardCharsets.UTF_8)
        .replace("+", "%20")
}

private fun String.expandUserHome(): String {
    if (this == "~") return System.getProperty("user.home").orEmpty()
    if (startsWith("~/") || startsWith("~\\")) {
        return System.getProperty("user.home").orEmpty() + substring(1)
    }
    return this
}

private fun launchCommand(vararg command: String): Boolean {
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
