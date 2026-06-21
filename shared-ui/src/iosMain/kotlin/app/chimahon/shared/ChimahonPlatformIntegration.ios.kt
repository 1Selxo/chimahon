package app.chimahon.shared

import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIModalPresentationPopover
import platform.UIKit.UINavigationController
import platform.UIKit.UIPasteboard
import platform.UIKit.UITabBarController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
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
        val nativeUrl = trimmed.externalUrlOrNull() ?: return copyText(trimmed)
        return UIApplication.sharedApplication.open(nativeUrl) ||
            presentShareSheet(listOf(nativeUrl), title = null) ||
            copyText(nativeUrl.absoluteString ?: trimmed)
    }

    actual fun openPath(path: String): Boolean {
        val fileUrl = path.fileUrlOrNull() ?: return false
        return UIApplication.sharedApplication.open(fileUrl) ||
            presentShareSheet(listOf(fileUrl), title = null) ||
            copyUrl(fileUrl)
    }

    actual fun revealPath(path: String): Boolean {
        val fileUrl = path.fileUrlOrNull() ?: return false
        return presentShareSheet(listOf(fileUrl), title = "Open in Files") ||
            UIApplication.sharedApplication.open(fileUrl) ||
            copyUrl(fileUrl)
    }

    actual fun copyText(text: String): Boolean {
        return runCatching {
            UIPasteboard.generalPasteboard.string = text
            true
        }.getOrDefault(false)
    }

    actual fun shareText(text: String, title: String?): Boolean {
        if (text.isBlank()) return false
        return presentShareSheet(listOf(text), title) || copyText(text)
    }

    actual fun shareFile(path: String, title: String?): Boolean {
        val fileUrl = path.fileUrlOrNull() ?: return false
        return presentShareSheet(listOf(fileUrl), title) || copyUrl(fileUrl)
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
        val url = NSURL.URLWithString(candidate) ?: return null
        val filePath = url.path ?: return null
        return url.takeIf { NSFileManager.defaultManager.fileExistsAtPath(filePath) }
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

private fun String.pathCandidates(): List<String> {
    val normalized = replace('\\', '/')
    if (normalized == "~") return listOf(NSHomeDirectory())
    if (normalized.startsWith("~/")) return listOf(NSHomeDirectory() + normalized.drop(1))
    if (normalized.startsWith("/")) return listOf(normalized)
    if (normalized.hasUrlScheme()) return emptyList()

    val relativePath = normalized.trimStart('/')
    val roots = listOf(
        iosStorageDirectories.filesDir.toString(),
        iosStorageDirectories.defaultDownloadsDir(APP_NAME).toString(),
        iosStorageDirectories.cacheDir.toString(),
        iosStorageDirectories.temporaryDir.toString(),
    )
    return roots.map { root -> "$root/$relativePath" }
}

private fun UIApplication.open(url: NSURL): Boolean {
    if (!canOpenURL(url)) return false
    openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
    return true
}

private fun copyUrl(url: NSURL): Boolean {
    val text = url.path ?: url.absoluteString ?: return false
    return ChimahonPlatformIntegration.copyText(text)
}

private fun presentShareSheet(items: List<*>, title: String?): Boolean {
    if (items.isEmpty()) return false
    val presenter = activeViewController() ?: return false
    val activityController = UIActivityViewController(
        activityItems = items,
        applicationActivities = null,
    )
    activityController.title = title?.trim()?.takeIf(String::isNotBlank)
    activityController.modalPresentationStyle = UIModalPresentationPopover
    activityController.popoverPresentationController?.let { popover ->
        popover.sourceView = presenter.view
        popover.sourceRect = presenter.view.bounds
    }
    presenter.presentViewController(activityController, animated = true, completion = null)
    return true
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
