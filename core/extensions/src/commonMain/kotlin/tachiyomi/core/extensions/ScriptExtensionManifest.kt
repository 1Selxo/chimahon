package tachiyomi.core.extensions

import kotlinx.serialization.Serializable

@Serializable
data class ScriptExtensionManifest(
    val id: String,
    val name: String,
    val version: String,
    val sources: List<ScriptSourceManifest>,
)

@Serializable
data class ScriptSourceManifest(
    val id: Long,
    val name: String,
    val language: String,
    val baseUrl: String,
    val isNsfw: Boolean = false,
    val supportsLatest: Boolean = false,
)

data class LoadedScriptExtension(
    val manifest: ScriptExtensionManifest,
    val script: String,
)
