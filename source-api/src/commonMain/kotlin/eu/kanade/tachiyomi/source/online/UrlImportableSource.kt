package eu.kanade.tachiyomi.source.online

import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.model.PlatformUri
import eu.kanade.tachiyomi.source.model.pathQueryAndFragment

interface UrlImportableSource : Source {
    val matchingHosts: List<String>

    fun matchesUri(uri: PlatformUri): Boolean {
        return uri.getHost().orEmpty().lowercase() in matchingHosts
    }

    fun mapUrlToChapterUrl(uri: PlatformUri): String? = null

    suspend fun mapChapterUrlToMangaUrl(uri: PlatformUri): String? = null

    // This method is allowed to block for IO if necessary
    suspend fun mapUrlToMangaUrl(uri: PlatformUri): String?

    fun cleanMangaUrl(url: String): String = url.pathQueryAndFragment()

    fun cleanChapterUrl(url: String): String = url.pathQueryAndFragment()
}
