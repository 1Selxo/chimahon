package app.chimahon.shared

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ChimahonExtensionRepoParsingTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesScriptAndApkEntriesFromPackagesEnvelope() {
        val extensions = parseRepoExtensions(
            json = json,
            repoBaseUrl = "https://repo.example/catalog",
            payload = """
                {
                  "packages": [
                    {
                      "script_url": "scripts/reader.js",
                      "package": "org.example.reader",
                      "name": "Reader Script",
                      "version_name": "2.4.0"
                    },
                    {
                      "apk": "tachiyomi-en-library-v1.2.3.apk",
                      "pkg": "eu.kanade.tachiyomi.extension.en.library",
                      "name": "Mihon: Library",
                      "version": "1.2.3",
                      "lang": "en",
                      "nsfw": 1,
                      "sources": [{}, {}]
                    },
                    {
                      "url": "archive.zip",
                      "id": "ignored"
                    }
                  ]
                }
            """.trimIndent(),
        )

        assertEquals(2, extensions.size)
        extensions[0].let { script ->
            assertEquals("org.example.reader", script.id)
            assertEquals("Reader Script", script.name)
            assertEquals("2.4.0", script.version)
            assertEquals("https://repo.example/catalog/scripts/reader.js", script.artifactUrl)
            assertEquals(ChimahonExtensionPackageType.JavaScript, script.packageType)
        }
        extensions[1].let { apk ->
            assertEquals("eu.kanade.tachiyomi.extension.en.library", apk.id)
            assertEquals("Library", apk.name)
            assertEquals("https://repo.example/catalog/apk/tachiyomi-en-library-v1.2.3.apk", apk.artifactUrl)
            assertEquals(ChimahonExtensionPackageType.AndroidApk, apk.packageType)
            assertEquals("en", apk.language)
            assertEquals(2, apk.sourceCount)
            assertTrue(apk.isNsfw)
        }
    }

    @Test
    fun resolvesRootAndAbsoluteScriptUrlsWithFallbackMetadata() {
        val extensions = parseRepoExtensions(
            json = json,
            repoBaseUrl = "https://repo.example/nested/catalog",
            payload = """
                [
                  {"script": "/scripts/root.js"},
                  {"downloadUrl": "https://cdn.example/absolute.js", "version": "3"}
                ]
            """.trimIndent(),
        )

        assertEquals(listOf("root", "absolute"), extensions.map { it.id })
        assertEquals(listOf("root", "absolute"), extensions.map { it.name })
        assertEquals(
            listOf(
                "https://repo.example/scripts/root.js",
                "https://cdn.example/absolute.js",
            ),
            extensions.map { it.artifactUrl },
        )
        assertEquals(listOf("remote", "3"), extensions.map { it.version })
        assertTrue(extensions.all { it.packageType == ChimahonExtensionPackageType.JavaScript })
        assertFalse(extensions.any { it.isNsfw })
    }

    @Test
    fun unsupportedRepositoryShapesProduceNoExtensions() {
        assertEquals(
            emptyList(),
            parseRepoExtensions(
                json = json,
                repoBaseUrl = "https://repo.example",
                payload = """{"metadata":{"name":"No package list"}}""",
            ),
        )
        assertEquals(
            emptyList(),
            parseRepoExtensions(
                json = json,
                repoBaseUrl = "https://repo.example",
                payload = "\"not an extension list\"",
            ),
        )
    }
}
