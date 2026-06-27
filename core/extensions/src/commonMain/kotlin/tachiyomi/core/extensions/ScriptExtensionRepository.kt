package tachiyomi.core.extensions

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

data class ScriptExtensionRepositoryEntry(
    val repoBaseUrl: String,
    val id: String,
    val name: String,
    val version: String,
    val artifactUrl: String,
    val packageName: String = id,
    val packageType: ScriptExtensionPackageType = ScriptExtensionPackageType.JavaScript,
    val language: String = "",
    val sourceCount: Int = 0,
    val isNsfw: Boolean = false,
    val apkName: String? = null,
    val iconUrl: String? = null,
)

class ScriptExtensionRepositoryParser(
    private val json: Json = Json {
        ignoreUnknownKeys = true
    },
) {
    fun catalogUrls(baseUrl: String): List<String> = repoCatalogUrls(baseUrl)

    fun parse(
        repoBaseUrl: String,
        payload: String,
    ): List<ScriptExtensionRepositoryEntry> {
        return parseRepoExtensions(
            json = json,
            repoBaseUrl = repoBaseUrl,
            payload = payload,
        )
    }
}

fun repoCatalogUrls(baseUrl: String): List<String> {
    val normalized = normalizeExtensionRepoBaseUrl(baseUrl)
    return listOf(
        "$normalized/chimahon.json",
        "$normalized/index.min.json",
        "$normalized/index.json",
    )
}

fun parseRepoExtensions(
    json: Json,
    repoBaseUrl: String,
    payload: String,
): List<ScriptExtensionRepositoryEntry> {
    val baseUrl = normalizeExtensionRepoBaseUrl(repoBaseUrl)
    val root = json.parseToJsonElement(payload)
    val entries = when (root) {
        is JsonArray -> root
        is JsonObject -> {
            root.arrayValue("extensions", "packages", "items", "data")
                ?: JsonArray(emptyList())
        }
        else -> JsonArray(emptyList())
    }

    return entries.mapNotNull { element ->
        val entry = element as? JsonObject ?: return@mapNotNull null
        entry.toRepositoryEntry(baseUrl)
    }
}

private fun JsonObject.toRepositoryEntry(repoBaseUrl: String): ScriptExtensionRepositoryEntry? {
    val apkName = stringValue("apk", "apkName", "apk_name")
    val scriptPath = stringValue(
        "scriptUrl",
        "script_url",
        "downloadUrl",
        "download_url",
        "artifactUrl",
        "artifact_url",
        "url",
        "script",
        "js",
    )

    return when {
        apkName != null -> toApkRepositoryEntry(repoBaseUrl, apkName)
        scriptPath != null && scriptPath.looksLikeJavaScriptArtifact() -> toScriptRepositoryEntry(repoBaseUrl, scriptPath)
        else -> null
    }
}

private fun JsonObject.toScriptRepositoryEntry(
    repoBaseUrl: String,
    scriptPath: String,
): ScriptExtensionRepositoryEntry {
    val artifactUrl = resolveRepoUrl(repoBaseUrl, scriptPath)
    val fallbackId = artifactUrl.substringAfterLast("/")
        .substringBefore("?")
        .substringBefore("#")
        .substringBeforeLast(".")
        .ifBlank { "script-extension" }
    val id = stringValue("id", "pkg", "package", "packageName", "pkgName") ?: fallbackId
    val sourceCount = sourceCount()
    val language = stringValue("lang", "language") ?: commonSourceLanguage()
    return ScriptExtensionRepositoryEntry(
        repoBaseUrl = repoBaseUrl,
        id = id,
        name = stringValue("name", "displayName", "display_name") ?: fallbackId,
        version = stringValue("version", "versionName", "version_name", "code", "versionCode") ?: "remote",
        artifactUrl = artifactUrl,
        packageName = stringValue("packageName", "pkgName", "pkg", "package") ?: id,
        packageType = ScriptExtensionPackageType.JavaScript,
        language = language,
        sourceCount = sourceCount,
        isNsfw = booleanValue("isNsfw", "is_nsfw", "nsfw") ?: sourcesNsfw(),
        iconUrl = iconUrl(repoBaseUrl),
    )
}

private fun JsonObject.toApkRepositoryEntry(
    repoBaseUrl: String,
    apkName: String,
): ScriptExtensionRepositoryEntry? {
    val packageName = stringValue("pkg", "package", "id", "packageName", "pkgName") ?: return null
    val name = stringValue("name", "displayName", "display_name")
        ?.substringAfter("Tachiyomi: ")
        ?.substringAfter("Mihon: ")
        ?: packageName.substringAfterLast(".")
    return ScriptExtensionRepositoryEntry(
        repoBaseUrl = repoBaseUrl,
        id = packageName,
        name = name,
        version = stringValue("version", "versionName", "version_name", "code", "versionCode") ?: "remote",
        artifactUrl = resolveRepoUrl(repoBaseUrl, "apk/$apkName"),
        packageName = packageName,
        packageType = ScriptExtensionPackageType.AndroidApk,
        language = stringValue("lang", "language").orEmpty(),
        sourceCount = sourceCount(),
        isNsfw = booleanValue("isNsfw", "is_nsfw", "nsfw") ?: false,
        apkName = apkName,
        iconUrl = iconUrl(repoBaseUrl, packageName),
    )
}

private fun normalizeExtensionRepoBaseUrl(input: String): String {
    val cleaned = input.trim().trimEnd('/')
    return cleaned
        .removeSuffix("/chimahon.json")
        .removeSuffix("/index.min.json")
        .removeSuffix("/index.json")
        .trimEnd('/')
}

private fun resolveRepoUrl(baseUrl: String, value: String): String {
    val normalized = value.trim()
    if (normalized.startsWith("https://") || normalized.startsWith("http://")) return normalized
    if (normalized.startsWith("/")) {
        val scheme = baseUrl.substringBefore("://", "https")
        val host = baseUrl.substringAfter("://", baseUrl).substringBefore("/")
        return "$scheme://$host$normalized"
    }
    return "${baseUrl.trimEnd('/')}/${normalized.trimStart('/')}"
}

private fun JsonObject.arrayValue(vararg keys: String): JsonArray? {
    return keys.firstNotNullOfOrNull { key -> this[key] as? JsonArray }
}

private fun JsonObject.stringValue(vararg keys: String): String? {
    return keys.firstNotNullOfOrNull { key ->
        this[key]?.primitiveContentOrNull()?.takeIf(String::isNotBlank)
    }
}

private fun JsonObject.intValue(vararg keys: String): Int? {
    return keys.firstNotNullOfOrNull { key ->
        this[key]?.primitiveContentOrNull()?.toIntOrNull()
    }
}

private fun JsonObject.booleanValue(vararg keys: String): Boolean? {
    return keys.firstNotNullOfOrNull { key ->
        val value = this[key] ?: return@firstNotNullOfOrNull null
        when (val primitive = value as? JsonPrimitive) {
            null -> null
            else -> when (primitive.content.lowercase()) {
                "1", "true", "yes", "y" -> true
                "0", "false", "no", "n" -> false
                else -> primitive.content.toIntOrNull()?.let { it != 0 }
            }
        }
    }
}

private fun JsonObject.sourceCount(): Int {
    return intValue("sourceCount", "source_count")
        ?: when (val sources = this["sources"]) {
            is JsonArray -> sources.size
            is JsonObject -> sources.size
            else -> 0
        }
}

private fun JsonObject.commonSourceLanguage(): String {
    val sourceLanguages = when (val sources = this["sources"]) {
        is JsonArray -> sources.mapNotNull { source ->
            (source as? JsonObject)?.stringValue("lang", "language")
        }
        is JsonObject -> sources.values.mapNotNull { source ->
            (source as? JsonObject)?.stringValue("lang", "language")
        }
        else -> emptyList()
    }
    return sourceLanguages.distinct().singleOrNull().orEmpty()
}

private fun JsonObject.sourcesNsfw(): Boolean {
    return when (val sources = this["sources"]) {
        is JsonArray -> sources.any { source ->
            (source as? JsonObject)?.booleanValue("isNsfw", "is_nsfw", "nsfw") == true
        }
        is JsonObject -> sources.values.any { source ->
            (source as? JsonObject)?.booleanValue("isNsfw", "is_nsfw", "nsfw") == true
        }
        else -> false
    }
}

private fun JsonObject.iconUrl(
    repoBaseUrl: String,
    packageName: String? = stringValue("pkg", "package", "id", "packageName", "pkgName"),
): String? {
    stringValue("iconUrl", "icon_url", "icon")?.let { icon ->
        return resolveRepoUrl(repoBaseUrl, icon)
    }
    return packageName?.let { resolveRepoUrl(repoBaseUrl, "icon/$it.png") }
}

private fun JsonElement.primitiveContentOrNull(): String? {
    return runCatching { jsonPrimitive.content }.getOrNull()
}

private fun String.looksLikeJavaScriptArtifact(): Boolean {
    val withoutQuery = substringBefore("?").substringBefore("#")
    return startsWith("https://") ||
        startsWith("http://") ||
        withoutQuery.endsWith(".js", ignoreCase = true)
}
