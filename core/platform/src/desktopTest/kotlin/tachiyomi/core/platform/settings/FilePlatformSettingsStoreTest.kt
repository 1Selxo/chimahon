package tachiyomi.core.platform.settings

import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator

class FilePlatformSettingsStoreTest {

    @Test
    fun `settings survive reload and escaped values`() = runTest {
        val root = Files.createTempDirectory("chimahon-settings-store-test")
        try {
            val settingsFile = root.resolve("settings.tsv").toString().toPath()
            val store = FilePlatformSettingsStore(settingsFile)

            store.writeString("reader_mode", "RightToLeft")
            store.writeString("escaped\tkey", "line one\nline two\\tail")
            store.writeBoolean("incognito", true)
            store.writeInt("font_size", 18)

            val reloaded = FilePlatformSettingsStore(settingsFile)

            assertEquals("RightToLeft", reloaded.readString("reader_mode"))
            assertEquals("line one\nline two\\tail", reloaded.readString("escaped\tkey"))
            assertTrue(reloaded.readBoolean("incognito"))
            assertEquals(18, reloaded.readInt("font_size"))
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun `missing and invalid values use defaults`() = runTest {
        val root = Files.createTempDirectory("chimahon-settings-store-default-test")
        try {
            val settingsFile = root.resolve("settings.tsv").toString().toPath()
            val store = FilePlatformSettingsStore(settingsFile)

            store.writeString("incognito", "maybe")
            store.writeString("font_size", "large")

            assertFalse(store.readBoolean("incognito"))
            assertEquals(22, store.readInt("font_size", 22))
            assertNull(store.readString("missing"))
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun `remove and clear persist`() = runTest {
        val root = Files.createTempDirectory("chimahon-settings-store-remove-test")
        try {
            val settingsFile = root.resolve("settings.tsv").toString().toPath()
            val store = FilePlatformSettingsStore(settingsFile)

            store.writeString("one", "1")
            store.writeString("two", "2")
            store.remove("one")

            assertEquals(mapOf("two" to "2"), FilePlatformSettingsStore(settingsFile).snapshot())

            store.clear()

            assertEquals(emptyMap<String, String>(), FilePlatformSettingsStore(settingsFile).snapshot())
        } finally {
            root.deleteRecursively()
        }
    }

    private fun Path.deleteRecursively() {
        if (Files.notExists(this)) return
        Files.walk(this).use { paths ->
            paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
        }
    }
}
