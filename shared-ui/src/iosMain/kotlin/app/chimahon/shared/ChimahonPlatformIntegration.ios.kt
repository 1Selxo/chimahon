package app.chimahon.shared

import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSThread
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UINavigationController
import platform.UIKit.UIPasteboard
import platform.UIKit.UITabBarController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_sync
import tachiyomi.core.platform.storage.IosPlatformStorageDirectories

private val iosStorageDirectories = IosPlatformStorageDirectories(APP_NAME)

internal actual object ChimahonPlatformIntegration {
    private val storageDirectories = iosStorageDirectories

    init {
        storageDirectories.ensureChimahonDirectories()
    }

    actual fun openExternalUrl(url: String): Boolean {
        val trimmed = url.trim()
        if (trimmed.isBlank()) return false
        val nativeUrl = trimmed.externalUrlOrNull()
        if (nativeUrl != null) {
            return openUrl(nativeUrl) ||
                presentShareSheet(listOf(nativeUrl), title = null) ||
                copyText(nativeUrl.absoluteString ?: trimmed)
        }

        val fileUrl = trimmed.fileUrlOrNull()
        return if (fileUrl != null) {
            openUrl(fileUrl) ||
                presentShareSheet(listOf(fileUrl), title = fileUrl.shareTitle()) ||
                copyUrl(fileUrl)
        } else {
            copyText(trimmed)
        }
    }

    actual fun openPath(path: String): Boolean {
        val fileUrl = path.fileUrlOrNull() ?: return false
        return openUrl(fileUrl) ||
            presentShareSheet(listOf(fileUrl), title = fileUrl.shareTitle()) ||
            copyUrl(fileUrl)
    }

    actual fun revealPath(path: String): Boolean {
        val fileUrl = path.fileUrlOrNull() ?: return false
        return presentShareSheet(listOf(fileUrl), title = "Open in Files") ||
            openUrl(fileUrl) ||
            copyUrl(fileUrl)
    }

    actual fun copyText(text: String): Boolean {
        return performOnMainThread {
            UIPasteboard.generalPasteboard.string = text
            true
        }
    }

    actual fun shareText(text: String, title: String?): Boolean {
        if (text.isBlank()) return false
        return presentShareSheet(listOf(text), title) || copyText(text)
    }

    actual fun shareFile(path: String, title: String?): Boolean {
        val fileUrl = path.fileUrlOrNull() ?: return false
        return presentShareSheet(listOf(fileUrl), title.shareTitleOrNull() ?: fileUrl.shareTitle()) ||
            openUrl(fileUrl) ||
            copyUrl(fileUrl)
    }

    actual fun platformInfo(): ChimahonPlatformInfo {
        val device = UIDevice.currentDevice
        val bundle = NSBundle.mainBundle

        return ChimahonPlatformInfo(
            platformName = device.systemName,
            platformVersion = device.systemVersion,
            deviceModel = device.model,
            appVersion = bundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String,
            buildNumber = bundle.objectForInfoDictionaryKey("CFBundleVersion") as? String,
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

private val allowedExternalSchemes = setOf(
    "http",
    "https",
    "mailto",
    "tel",
    "sms",
    "facetime",
    "facetime-audio",
    "maps",
    "itms-apps",
)

private fun String.externalUrlOrNull(): NSURL? {
    val candidate = normalizedExternalUrlString() ?: return null
    val url = NSURL.URLWithString(candidate) ?: return null
    return url.takeIf { it.scheme?.lowercase() in allowedExternalSchemes }
}

private fun String.fileUrlOrNull(): NSURL? {
    val candidate = trim()
    if (candidate.isBlank()) return null

    if (candidate.startsWith("file:", ignoreCase = true)) {
        return candidate.fileSchemeUrlOrNull()?.existingFileUrlOrNull()
    }

    return candidate.pathCandidates()
        .asSequence()
        .mapNotNull { path ->
            val url = NSURL.fileURLWithPath(path)
            url.takeIf { NSFileManager.defaultManager.fileExistsAtPath(path) }
        }
        .firstOrNull()
}

private fun String.normalizedExternalUrlString(): String? {
    val candidate = trim()
    if (candidate.isBlank() || candidate.any(Char::isWhitespace)) return null
    return when {
        candidate.startsWith("//") && candidate.drop(2).looksLikeHost() -> "https:$candidate"
        candidate.hasUrlScheme() -> candidate
        candidate.contains("@") -> null
        candidate.looksLikeHost() -> "https://$candidate"
        else -> null
    }
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

private fun String.fileSchemeUrlOrNull(): NSURL? {
    val parsedUrl = NSURL.URLWithString(this)
    val parsedPath = parsedUrl?.path?.takeIf(String::isNotBlank)
    if (parsedPath != null) return NSURL.fileURLWithPath(parsedPath)

    val rawPath = when {
        startsWith("file://", ignoreCase = true) -> drop("file://".length)
        startsWith("file:", ignoreCase = true) -> drop("file:".length)
        else -> null
    }?.substringBefore('#')
        ?.substringBefore('?')
        ?.takeIf(String::isNotBlank)

    return rawPath?.let { NSURL.fileURLWithPath(it) }
}

private fun NSURL.existingFileUrlOrNull(): NSURL? {
    val filePath = path ?: return null
    return takeIf { filePath.isNotBlank() && NSFileManager.defaultManager.fileExistsAtPath(filePath) }
}

private fun String.pathCandidates(): List<String> {
    val normalized = replace('\\', '/')
    if (normalized == "~") return listOf(NSHomeDirectory())
    if (normalized.startsWith("~/")) return listOf(NSHomeDirectory() + normalized.drop(1))
    if (normalized.startsWith("/")) return listOf(normalized)
    if (normalized.hasUrlScheme()) return emptyList()

    val relativePath = normalized.trimStart('/')
    if (relativePath.isBlank()) return emptyList()
    val roots = listOf(
        iosStorageDirectories.defaultDownloadsDir(APP_NAME).toString(),
        iosStorageDirectories.filesDir.toString(),
        iosStorageDirectories.cacheDir.toString(),
        iosStorageDirectories.temporaryDir.toString(),
        NSHomeDirectory(),
    )
    return roots.distinct().map { root -> "$root/$relativePath" }
}

private fun openUrl(url: NSURL): Boolean {
    return performOnMainThread {
        val application = UIApplication.sharedApplication
        if (!application.canOpenURL(url)) return@performOnMainThread false
        application.openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
        true
    }
}

private fun copyUrl(url: NSURL): Boolean {
    val text = url.path ?: url.absoluteString ?: return false
    return ChimahonPlatformIntegration.copyText(text)
}

private fun presentShareSheet(items: List<*>, title: String?): Boolean {
    val shareItems = items.filterNotNull()
    if (shareItems.isEmpty()) return false

    return performOnMainThread {
        val presenter = activeViewController() ?: return@performOnMainThread false
        val activityController = UIActivityViewController(
            activityItems = shareItems,
            applicationActivities = null,
        )
        presenter.presentViewController(activityController, animated = true, completion = null)
        true
    }
}

private inline fun performOnMainThread(crossinline action: () -> Boolean): Boolean {
    if (NSThread.isMainThread) {
        return runCatching { action() }.getOrDefault(false)
    }

    var result = false
    dispatch_sync(dispatch_get_main_queue()) {
        result = runCatching { action() }.getOrDefault(false)
    }
    return result
}

private fun String?.shareTitleOrNull(): String? {
    return this
        ?.trim()
        ?.takeIf(String::isNotBlank)
}

private fun NSURL.shareTitle(): String {
    return lastPathComponent
        ?.trim()
        ?.takeIf(String::isNotBlank)
        ?: APP_NAME
}

private fun activeViewController(): UIViewController? {
    val application = UIApplication.sharedApplication
    val rootController = application.windows
        .filterIsInstance<UIWindow>()
        .firstOrNull { it.keyWindow }
        ?.rootViewController
        ?: application.windows
            .filterIsInstance<UIWindow>()
            .firstOrNull()
            ?.rootViewController
        ?: application.keyWindow?.rootViewController

    var controller = rootController ?: return null
    while (true) {
        controller = controller.topVisibleController()
        val presented = controller.presentedViewController ?: return controller
        controller = presented
    }
}

private fun UIViewController.topVisibleController(): UIViewController {
    var controller: UIViewController = this
    while (true) {
        val child = when (controller) {
            is UINavigationController -> controller.visibleViewController
            is UITabBarController -> controller.selectedViewController
            else -> null
        }
        if (child == null || child == controller) return controller
        controller = child
    }
}
