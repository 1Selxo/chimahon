package tachiyomi.core.common.storage

import android.content.Context
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.core.platform.storage.AndroidPlatformStorageDirectories
import tachiyomi.core.platform.storage.PlatformStorageDirectories
import tachiyomi.i18n.MR
import java.io.File

class AndroidStorageFolderProvider(
    private val context: Context,
    private val directories: PlatformStorageDirectories = AndroidPlatformStorageDirectories(context),
) : FolderProvider {

    /**
     * This will return File: /storage/emulated/0/<app_name>
     */
    override fun directory(): File {
        return File(defaultDownloadsDir().toString())
    }

    /**
     * This will return: file:///storage/emulated/0/<app_name>
     */
    override fun path(): String {
        return directories.fileUri(defaultDownloadsDir())
    }

    private fun defaultDownloadsDir() = directories.defaultDownloadsDir(
        appName = context.stringResource(MR.strings.app_name),
    )
}
