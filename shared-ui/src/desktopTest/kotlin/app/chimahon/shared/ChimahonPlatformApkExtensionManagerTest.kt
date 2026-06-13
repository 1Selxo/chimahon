package app.chimahon.shared

import eu.kanade.tachiyomi.source.SourceRegistry
import kotlinx.coroutines.runBlocking
import okio.Path.Companion.toPath
import tachiyomi.core.platform.storage.DesktopPlatformStorageDirectories
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ChimahonPlatformApkExtensionManagerTest {
    @Test
    fun installsAndRunsKeiyoushiExtension() = runBlocking {
        val root = Files.createTempDirectory("chimahon-apk-extension-test")
        val storage = DesktopPlatformStorageDirectories(
            appName = "test",
            userHome = root.toString().toPath(),
            tempRoot = root.toString().toPath(),
            appDataRoot = root.toString().toPath(),
            cacheRoot = root.toString().toPath(),
        )
        val registry = SourceRegistry()
        val manager = ChimahonPlatformApkExtensionManager(storage, registry)
        try {
            val extension = ChimahonRepoExtensionEntry(
                repoBaseUrl = KEIYOUSHI_REPO,
                id = "eu.kanade.tachiyomi.extension.en.weebcentral",
                name = "Weeb Central",
                version = "1.4.22",
                artifactUrl = "$KEIYOUSHI_REPO/apk/tachiyomi-en.weebcentral-v1.4.22.apk",
                packageType = ChimahonExtensionPackageType.AndroidApk,
                language = "en",
                sourceCount = 1,
            )

            val installed = manager.install(
                extension = extension,
                apkBytes = URI(extension.artifactUrl).toURL().readBytes(),
            )

            assertEquals(extension.id, installed.id)
            assertEquals(1, installed.sourceCount)
            val source = registry.getCatalogueSources().singleOrNull()
            assertNotNull(source)
            assertEquals("Weeb Central", source.name)
            val popular = source.getPopularManga(1).mangas
            assertTrue(popular.isNotEmpty())
            val manga = source.getMangaDetails(popular.first())
            val chapters = source.getChapterList(manga)
            assertTrue(chapters.isNotEmpty())
            val pages = source.getPageList(chapters.first())
            assertTrue(pages.isNotEmpty())
            val imageBytes = fetchSourcePageImage(source, pages.first())
            assertTrue(imageBytes.size > 1_000)
        } finally {
            manager.close()
            root.deleteRecursively()
        }
    }

    private fun Path.deleteRecursively() {
        if (Files.notExists(this)) return
        Files.walk(this).use { paths ->
            paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
        }
    }

    private companion object {
        const val KEIYOUSHI_REPO = "https://raw.githubusercontent.com/keiyoushi/extensions/repo"
    }
}
