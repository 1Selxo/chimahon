package tachiyomi.core.extensions

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tachiyomi.core.platform.javascript.DesktopJavaScriptRuntimeFactory

class ScriptExtensionInvokerTest {
    private val loader = ScriptExtensionLoader(DesktopJavaScriptRuntimeFactory)
    private val invoker = ScriptExtensionInvoker(DesktopJavaScriptRuntimeFactory)

    @Test
    fun invokesRequestAndParserMethods() = runBlocking {
        val extension = loader.load(TEST_EXTENSION)
        val arguments = buildJsonObject {
            put("page", 3)
        }

        val request = invoker.invoke(
            extension = extension,
            sourceId = 42,
            method = "popularMangaRequest",
            arguments = arguments,
            deserializer = ScriptHttpRequest.serializer(),
        )
        assertEquals("https://example.org/popular?page=3", request.url)

        val page = invoker.invoke(
            extension = extension,
            sourceId = 42,
            method = "popularMangaParse",
            arguments = arguments,
            deserializer = ScriptMangasPage.serializer(),
        )
        assertEquals("Example 3", page.mangas.single().title)
        assertEquals(true, page.hasNextPage)
    }

    private companion object {
        val TEST_EXTENSION = """
            module.exports = {
                manifest: {
                    id: "org.chimahon.invoker",
                    name: "Invoker",
                    version: "1.0.0",
                    sources: [{
                        id: 42,
                        name: "Example",
                        language: "en",
                        baseUrl: "https://example.org"
                    }]
                },
                sources: {
                    "42": {
                        popularMangaRequest: function (args) {
                            return { url: "https://example.org/popular?page=" + args.page };
                        },
                        popularMangaParse: function (args) {
                            return {
                                mangas: [{
                                    url: "/manga/" + args.page,
                                    title: "Example " + args.page
                                }],
                                hasNextPage: true
                            };
                        }
                    }
                }
            };
        """.trimIndent()
    }
}
