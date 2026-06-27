package tachiyomi.core.extensions

import kotlinx.serialization.Serializable

@Serializable
data class ScriptExtensionManifest(
    val id: String,
    val name: String,
    val version: String,
    val sources: List<ScriptSourceManifest>,
    val packageName: String = id,
    val packageType: ScriptExtensionPackageType = ScriptExtensionPackageType.JavaScript,
    val language: String = sources.commonLanguage(),
    val sourceCount: Int = sources.size,
    val isNsfw: Boolean = sources.any(ScriptSourceManifest::isNsfw),
    val apkName: String? = null,
    val artifactUrl: String? = null,
    val iconUrl: String? = null,
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

@Serializable
enum class ScriptExtensionPackageType {
    JavaScript,
    AndroidApk,
}

data class LoadedScriptExtension(
    val manifest: ScriptExtensionManifest,
    val script: String,
)

internal fun List<ScriptSourceManifest>.commonLanguage(): String {
    val languages = map { source -> source.language }
        .filter(String::isNotBlank)
        .distinct()
    return languages.singleOrNull().orEmpty()
}
