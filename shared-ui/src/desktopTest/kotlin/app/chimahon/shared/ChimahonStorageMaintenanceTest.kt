package app.chimahon.shared

import okio.FileSystem
import okio.Path.Companion.toPath
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChimahonStorageMaintenanceTest {
    @Test
    fun clearDirectoryContentsPreservesRootAndRemovesDescendants() {
        val root = Files.createTempDirectory("chimahon-cache-maintenance")
        val cache = root.resolve("cache")
        val nested = cache.resolve("images")
        Files.createDirectories(nested)
        Files.write(cache.resolve("index.bin"), byteArrayOf(1, 2, 3))
        Files.write(nested.resolve("cover.bin"), byteArrayOf(4, 5, 6, 7))
        val cachePath = cache.toString().toPath()

        try {
            val result = clearDirectoryContents(cachePath)

            assertTrue(result.failures.isEmpty())
            assertEquals(7L, result.bytesRemoved)
            assertEquals(2, result.filesRemoved)
            assertEquals(1, result.directoriesRemoved)
            assertTrue(Files.isDirectory(cache))
            assertEquals(emptyList(), FileSystem.SYSTEM.list(cachePath))
        } finally {
            FileSystem.SYSTEM.deleteRecursively(root.toString().toPath())
        }
    }

    @Test
    fun clearDirectoryContentsRefusesFilesystemRoot() {
        val root = "/".toPath()

        val result = clearDirectoryContents(root)

        assertEquals(1, result.failures.size)
        assertTrue(result.failures.single().reason.contains("filesystem root"))
    }
}
