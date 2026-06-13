package app.chimahon.shared

import kotlinx.serialization.json.Json
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KeiyoushiExtensionRepoTest {
    @Test
    fun parsesKeiyoushiApkCatalog() {
        val payload = URI("$KEIYOUSHI_REPO/index.min.json").toURL().readText()
        val extensions = parseRepoExtensions(
            json = Json { ignoreUnknownKeys = true },
            repoBaseUrl = KEIYOUSHI_REPO,
            payload = payload,
        )

        assertTrue(extensions.size > 1_000)
        val mangaDex = extensions.single {
            it.id == "eu.kanade.tachiyomi.extension.all.mangadex"
        }
        assertEquals(ChimahonExtensionPackageType.AndroidApk, mangaDex.packageType)
        assertEquals("MangaDex", mangaDex.name)
        assertTrue(mangaDex.artifactUrl.contains("/apk/tachiyomi-all.mangadex-v"))
        assertTrue(mangaDex.artifactUrl.endsWith(".apk"))
        assertTrue(mangaDex.sourceCount > 10)
    }

    @Test
    fun prefersKeiyoushiPackageIndexOverRepositoryMetadata() {
        assertEquals(
            listOf(
                "$KEIYOUSHI_REPO/chimahon.json",
                "$KEIYOUSHI_REPO/index.min.json",
                "$KEIYOUSHI_REPO/index.json",
            ),
            repoCatalogUrls(KEIYOUSHI_REPO),
        )
    }

    private companion object {
        const val KEIYOUSHI_REPO = "https://raw.githubusercontent.com/keiyoushi/extensions/repo"
    }
}
