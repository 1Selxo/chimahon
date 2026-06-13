package app.chimahon.shared

import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIPasteboard
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import tachiyomi.core.platform.storage.IosPlatformStorageDirectories

internal actual object ChimahonPlatformIntegration {
    private val storageDirectories = IosPlatformStorageDirectories(APP_NAME)

    actual fun openExternalUrl(url: String): Boolean {
        val nativeUrl = url.httpUrlOrNull() ?: return false
        return UIApplication.sharedApplication.open(nativeUrl)
    }

    actual fun openPath(path: String): Boolean {
        if (!NSFileManager.defaultManager.fileExistsAtPath(path)) return false
        return UIApplication.sharedApplication.open(NSURL.fileURLWithPath(path))
    }

    actual fun revealPath(path: String): Boolean = false

    actual fun copyText(text: String): Boolean {
        UIPasteboard.generalPasteboard.string = text
        return true
    }

    actual fun shareText(text: String, title: String?): Boolean {
        return presentShareSheet(listOf(text), title)
    }

    actual fun shareFile(path: String, title: String?): Boolean {
        if (!NSFileManager.defaultManager.fileExistsAtPath(path)) return false
        return presentShareSheet(listOf(NSURL.fileURLWithPath(path)), title)
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
        return ChimahonStoragePaths(
            filesDir = storageDirectories.filesDir.toString(),
            cacheDir = storageDirectories.cacheDir.toString(),
            downloadsDir = storageDirectories.defaultDownloadsDir(APP_NAME).toString(),
            temporaryDir = storageDirectories.temporaryDir.toString(),
        )
    }
}

private fun String.httpUrlOrNull(): NSURL? {
    val url = NSURL.URLWithString(this) ?: return null
    return url.takeIf { it.scheme?.lowercase() in setOf("http", "https") }
}

private fun UIApplication.open(url: NSURL): Boolean {
    if (!canOpenURL(url)) return false
    openURL(url, options = emptyMap<Any?, Any>(), completionHandler = null)
    return true
}

private fun presentShareSheet(items: List<*>, title: String?): Boolean {
    val presenter = activeViewController() ?: return false
    val activityController = UIActivityViewController(
        activityItems = items,
        applicationActivities = null,
    )
    if (!title.isNullOrBlank()) {
        activityController.setValue(title, forKey = "subject")
    }
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

    var controller = rootController
    while (controller?.presentedViewController != null) {
        controller = controller.presentedViewController
    }
    return controller
}
