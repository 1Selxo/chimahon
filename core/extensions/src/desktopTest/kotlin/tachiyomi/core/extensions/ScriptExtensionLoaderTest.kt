package tachiyomi.core.extensions

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import tachiyomi.core.platform.javascript.DesktopJavaScriptRuntimeFactory

class ScriptExtensionLoaderTest {
    private val loader = ScriptExtensionLoader(DesktopJavaScriptRuntimeFactory)

    @Test
    fun loadsCommonJsManifestWithSources() = runBlocking {
        val extension = loader.load(
            """
            module.exports = {
                manifest: {
                    id: "org.chimahon.demo",
                    name: "Demo",
                    version: "1.0.0",
                    sources: [{
                        id: 42,
                        name: "Demo Source",
                        language: "en",
                        baseUrl: "https://example.org"
                    }]
                }
            };
            """.trimIndent(),
        )

        assertEquals("org.chimahon.demo", extension.manifest.id)
        assertEquals(42L, extension.manifest.sources.single().id)
    }

    @Test
    fun rejectsDuplicateSourceIds() {
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                loader.load(
                    """
                    module.exports = {
                        id: "org.chimahon.invalid",
                        name: "Invalid",
                        version: "1.0.0",
                        sources: [
                            { id: 7, name: "One", language: "en", baseUrl: "https://one.example" },
                            { id: 7, name: "Two", language: "en", baseUrl: "https://two.example" }
                        ]
                    };
                    """.trimIndent(),
                )
            }
        }
    }
}
