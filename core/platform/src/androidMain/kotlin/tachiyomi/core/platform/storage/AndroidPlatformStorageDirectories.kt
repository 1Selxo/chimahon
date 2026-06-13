package tachiyomi.core.platform.storage

import android.content.Context
import android.net.Uri
import android.os.Environment
import okio.Path
import okio.Path.Companion.toPath
import java.io.File

class AndroidPlatformStorageDirectories(
    private val context: Context,
) : PlatformStorageDirectories {

    override val cacheDir: Path
        get() = context.cacheDir.absolutePath.toPath()

    override val filesDir: Path
        get() = context.filesDir.absolutePath.toPath()

    override val temporaryDir: Path
        get() = (context.externalCacheDir ?: context.cacheDir)
            .resolve("tmp")
            .absolutePath
            .toPath()

    override fun defaultDownloadsDir(appName: String): Path {
        return Environment
            .getExternalStorageDirectory()
            .absolutePath
            .toPath()
            .resolveDirectoryName(appName)
    }

    override fun fileUri(path: Path): String {
        return Uri.fromFile(File(path.toString())).toString()
    }
}
