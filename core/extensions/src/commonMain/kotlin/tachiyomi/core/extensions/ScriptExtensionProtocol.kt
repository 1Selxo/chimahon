package tachiyomi.core.extensions

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ScriptHttpRequest(
    val url: String,
    val method: String = "GET",
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null,
    val contentType: String? = null,
)

@Serializable
data class ScriptHttpResponse(
    val status: Int,
    val headers: Map<String, List<String>>,
    val body: String,
    val finalUrl: String,
)

@Serializable
data class ScriptParseInput(
    val arguments: JsonObject,
    val response: ScriptHttpResponse,
)

@Serializable
data class ScriptManga(
    val url: String,
    val title: String,
    val artist: String? = null,
    val author: String? = null,
    val description: String? = null,
    val genre: String? = null,
    val status: Int = 0,
    val thumbnailUrl: String? = null,
    val initialized: Boolean = false,
)

@Serializable
data class ScriptMangasPage(
    val mangas: List<ScriptManga>,
    val hasNextPage: Boolean,
)

@Serializable
data class ScriptChapter(
    val url: String,
    val name: String,
    val dateUpload: Long = 0,
    val chapterNumber: Float = -1f,
    val scanlator: String? = null,
)

@Serializable
data class ScriptPage(
    val index: Int,
    val url: String = "",
    val imageUrl: String? = null,
)
