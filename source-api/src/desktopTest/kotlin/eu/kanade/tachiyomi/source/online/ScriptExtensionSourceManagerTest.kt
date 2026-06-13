package eu.kanade.tachiyomi.source.online

import eu.kanade.tachiyomi.source.SourceRegistry
import kotlinx.coroutines.runBlocking
import okio.Path
import okio.Path.Companion.toOkioPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import tachiyomi.core.extensions.ScriptExtensionInvoker
import tachiyomi.core.extensions.ScriptExtensionLoader
import tachiyomi.core.extensions.ScriptExtensionStore
import tachiyomi.core.platform.javascript.DesktopJavaScriptRuntimeFactory
import tachiyomi.core.platform.storage.PlatformStorageDirectories
import java.nio.file.Path as NioPath

class ScriptExtensionSourceManagerTest {
    @TempDir
    lateinit var temporaryDirectory: NioPath

    @Test
    fun installsRegistersAndUninstallsScriptSources() = runBlocking {
        val registry = SourceRegistry()
        val manager = createManager(registry)

        val extension = manager.install(TEST_EXTENSION)

        assertEquals("org.chimahon.manager", extension.manifest.id)
        assertEquals("Managed Source", registry.get(88)?.name)
        assertTrue(manager.uninstall("org.chimahon.manager"))
        assertNull(registry.get(88))
    }

    private fun createManager(registry: SourceRegistry): ScriptExtensionSourceManager {
        val loader = ScriptExtensionLoader(DesktopJavaScriptRuntimeFactory)
        return ScriptExtensionSourceManager(
            store = ScriptExtensionStore(
                storageDirectories = ManagerTestStorageDirectories(temporaryDirectory.toOkioPath()),
                loader = loader,
            ),
            invoker = ScriptExtensionInvoker(DesktopJavaScriptRuntimeFactory),
            registry = registry,
        )
    }

    private companion object {
        val TEST_EXTENSION = """
            module.exports = {
                manifest: {
                    id: "org.chimahon.manager",
                    name: "Managed",
                    version: "1.0.0",
                    sources: [{
                        id: 88,
                        name: "Managed Source",
                        language: "en",
                        baseUrl: "https://example.org"
                    }]
                },
                sources: {}
            };
        """.trimIndent()
    }
}

private class ManagerTestStorageDirectories(
    override val filesDir: Path,
) : PlatformStorageDirectories {
    override val cacheDir: Path = filesDir / "cache"
    override val temporaryDir: Path = filesDir / "temp"

    override fun defaultDownloadsDir(appName: String): Path = filesDir / "downloads" / appName

    override fun fileUri(path: Path): String = path.toString()
}
