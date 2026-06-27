package tachiyomi.core.extensions

import kotlinx.serialization.json.Json
import tachiyomi.core.platform.javascript.JavaScriptRuntimeFactory

class ScriptExtensionLoader(
    private val runtimeFactory: JavaScriptRuntimeFactory,
    private val json: Json = Json {
        ignoreUnknownKeys = true
    },
) {
    suspend fun load(script: String): LoadedScriptExtension {
        require(script.isNotBlank()) { "Extension script cannot be blank" }

        val manifestJson = runtimeFactory.create().evaluate<String>(
            ScriptExtensionJavaScriptBridge.manifestScript(script, json),
        )
        val manifest = json.decodeFromString<ScriptExtensionManifest>(manifestJson).normalize()
        validate(manifest)
        return LoadedScriptExtension(manifest, script)
    }

    private fun ScriptExtensionManifest.normalize(): ScriptExtensionManifest {
        val normalizedSources = sources.map { source ->
            source.copy(
                language = source.language.ifBlank { language.ifBlank { DEFAULT_LANGUAGE } },
                baseUrl = source.baseUrl.trim(),
            )
        }
        return copy(
            sources = normalizedSources,
            packageName = packageName.ifBlank { id },
            language = language.ifBlank { normalizedSources.commonLanguage() },
            sourceCount = sourceCount.coerceAtLeast(normalizedSources.size),
            isNsfw = isNsfw || normalizedSources.any(ScriptSourceManifest::isNsfw),
        )
    }

    private fun validate(manifest: ScriptExtensionManifest) {
        require(manifest.id.isNotBlank()) { "Extension id cannot be blank" }
        require(manifest.id.matches(EXTENSION_ID_PATTERN)) {
            "Extension id must contain only letters, numbers, dots, dashes, or underscores"
        }
        require(manifest.name.isNotBlank()) { "Extension name cannot be blank" }
        require(manifest.version.isNotBlank()) { "Extension version cannot be blank" }
        require(manifest.sources.isNotEmpty()) { "Extension must declare at least one source" }
        require(manifest.sourceCount >= manifest.sources.size) {
            "Extension source count cannot be smaller than declared sources"
        }
        require(manifest.sources.map(ScriptSourceManifest::id).distinct().size == manifest.sources.size) {
            "Extension source ids must be unique"
        }
        manifest.sources.forEach { source ->
            require(source.name.isNotBlank()) { "Source name cannot be blank" }
            require(source.language.isNotBlank()) { "Source language cannot be blank" }
            require(source.baseUrl.startsWith("https://") || source.baseUrl.startsWith("http://")) {
                "Source base URL must use HTTP or HTTPS"
            }
        }
    }

    private companion object {
        const val DEFAULT_LANGUAGE = "all"
        val EXTENSION_ID_PATTERN = Regex("[A-Za-z0-9._-]+")
    }
}
