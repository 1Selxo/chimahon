package tachiyomi.core.extensions

import kotlinx.serialization.encodeToString
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

        val manifestJson = runtimeFactory.create().evaluate<String>(bootstrapScript(script))
        val manifest = json.decodeFromString<ScriptExtensionManifest>(manifestJson)
        validate(manifest)
        return LoadedScriptExtension(manifest, script)
    }

    private fun bootstrapScript(script: String): String {
        val encodedScript = json.encodeToString(script)
        return """
            (function () {
                var module = { exports: {} };
                var exports = module.exports;
                eval($encodedScript);
                var extension = module.exports;
                if (extension && extension.default) {
                    extension = extension.default;
                }
                if ((!extension || Object.keys(extension).length === 0) &&
                    typeof chimahonExtension !== "undefined") {
                    extension = chimahonExtension;
                }
                if (!extension) {
                    throw new Error("Extension did not export a manifest");
                }
                return JSON.stringify(extension.manifest || extension);
            })();
        """.trimIndent()
    }

    private fun validate(manifest: ScriptExtensionManifest) {
        require(manifest.id.isNotBlank()) { "Extension id cannot be blank" }
        require(manifest.id.matches(EXTENSION_ID_PATTERN)) {
            "Extension id must contain only letters, numbers, dots, dashes, or underscores"
        }
        require(manifest.name.isNotBlank()) { "Extension name cannot be blank" }
        require(manifest.version.isNotBlank()) { "Extension version cannot be blank" }
        require(manifest.sources.isNotEmpty()) { "Extension must declare at least one source" }
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
        val EXTENSION_ID_PATTERN = Regex("[A-Za-z0-9._-]+")
    }
}
