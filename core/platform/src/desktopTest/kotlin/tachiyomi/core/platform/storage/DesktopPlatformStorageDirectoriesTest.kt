package tachiyomi.core.platform.storage

import okio.Path.Companion.toPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DesktopPlatformStorageDirectoriesTest {

    @Test
    fun defaultDirectoriesAreScopedToAppName() {
        val directories = DesktopPlatformStorageDirectories(
            appName = "Chimahon",
            userHome = "/Users/tester".toPath(),
            tempRoot = "/tmp".toPath(),
            appDataRoot = "/data".toPath(),
            cacheRoot = "/cache".toPath(),
        )

        assertEquals("/data/Chimahon".toPath(), directories.filesDir)
        assertEquals("/cache/Chimahon".toPath(), directories.cacheDir)
        assertEquals("/tmp/Chimahon".toPath(), directories.temporaryDir)
        assertEquals("/Users/tester/Downloads/Chimahon".toPath(), directories.defaultDownloadsDir("Chimahon"))
    }

    @Test
    fun defaultDirectoriesSanitizeAppNameForPathSegments() {
        val directories = DesktopPlatformStorageDirectories(
            appName = "Chi:ma/hon?",
            userHome = "/Users/tester".toPath(),
            tempRoot = "/tmp".toPath(),
            appDataRoot = "/data".toPath(),
            cacheRoot = "/cache".toPath(),
        )

        assertEquals("/data/Chi_ma_hon_".toPath(), directories.filesDir)
        assertTrue(directories.fileUri(directories.filesDir).startsWith("file:/"))
    }
}
