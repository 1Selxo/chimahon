package tachiyomi.core.extensions

import kotlinx.coroutines.runBlocking
import okio.Path
import okio.Path.Companion.toOkioPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tachiyomi.core.platform.javascript.DesktopJavaScriptRuntimeFactory
import tachiyomi.core.platform.storage.PlatformStorageDirectories
import java.nio.file.Path as NioPath

class ScriptExtensionStoreTest {
    @TempDir
    lateinit var temporaryDirectory: NioPath

    @Test
    fun installsLoadsAndUninstallsScripts() = runBlocking {
        val store = ScriptExtensionStore(
            storageDirectories = TestStorageDirectories(temporaryDirectory.toOkioPath()),
            loader = ScriptExtensionLoader(DesktopJavaScriptRuntimeFactory),
        )

        val installed = store.install(TEST_EXTENSION)
        assertEquals("org.chimahon.store", installed.manifest.id)
        assertEquals("org.chimahon.store", store.loadInstalled().single().manifest.id)
        assertTrue(store.uninstall("org.chimahon.store"))
        assertFalse(store.uninstall("org.chimahon.store"))
        assertTrue(store.loadInstalled().isEmpty())
    }

    private companion object {
        val TEST_EXTENSION = """
            module.exports = {
                manifest: {
                    id: "org.chimahon.store",
                    name: "Stored",
                    version: "1.0.0",
                    sources: [{
                        id: 9,
                        name: "Stored Source",
                        language: "en",
                        baseUrl: "https://example.org"
                    }]
                },
                sources: {}
            };
        """.trimIndent()
    }
}

private class TestStorageDirectories(
    override val filesDir: Path,
) : PlatformStorageDirectories {
    override val cacheDir: Path = filesDir / "cache"
    override val temporaryDir: Path = filesDir / "temp"

    override fun defaultDownloadsDir(appName: String): Path = filesDir / "downloads" / appName

    override fun fileUri(path: Path): String = path.toString()
}
