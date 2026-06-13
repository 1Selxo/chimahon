package tachiyomi.core.platform.storage

import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

class IosPlatformStorageDirectories(
    private val appName: String = "chimahon",
    appSupportRoot: Path? = null,
    cacheRoot: Path? = null,
    documentsRoot: Path? = null,
    temporaryRoot: Path? = null,
) : PlatformStorageDirectories {

    private val documentsRoot: Path = documentsRoot ?: userDirectory(NSDocumentDirectory)

    override val cacheDir: Path = (cacheRoot ?: userDirectory(NSCachesDirectory)).resolveDirectoryName(appName)

    override val filesDir: Path = (appSupportRoot ?: userDirectory(NSApplicationSupportDirectory))
        .resolveDirectoryName(appName)

    override val temporaryDir: Path = (temporaryRoot ?: NSTemporaryDirectory().toPath())
        .resolveDirectoryName(appName)

    override fun defaultDownloadsDir(appName: String): Path {
        return documentsRoot.resolveDirectoryName(appName)
    }

    override fun fileUri(path: Path): String {
        return NSURL.fileURLWithPath(path.toString()).absoluteString ?: "file://$path"
    }

    private fun userDirectory(directory: NSSearchPathDirectory): Path {
        val paths = NSSearchPathForDirectoriesInDomains(directory, NSUserDomainMask, true)
        val path = paths.firstOrNull() as? String
        return path?.toPath() ?: error("Unable to resolve iOS storage directory: $directory")
    }
}
