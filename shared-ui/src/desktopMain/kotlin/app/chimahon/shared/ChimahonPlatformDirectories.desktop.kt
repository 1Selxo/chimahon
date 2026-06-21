package app.chimahon.shared

import tachiyomi.core.platform.storage.PlatformStorageDirectories
import java.nio.file.Files
import java.nio.file.Path as NioPath

internal fun PlatformStorageDirectories.ensureChimahonDirectories() {
    listOf(
        filesDir,
        cacheDir,
        temporaryDir,
        defaultDownloadsDir(APP_NAME),
    ).forEach { path ->
        runCatching {
            Files.createDirectories(NioPath.of(path.toString()))
        }
    }
}
