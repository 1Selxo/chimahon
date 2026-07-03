package eu.kanade.tachiyomi.animesource

import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.source.sourceApiLogError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

actual interface AnimeCatalogueSource : AnimeSource {

    actual override val lang: String

    actual val supportsLatest: Boolean

    actual suspend fun getPopularAnime(page: Int): AnimesPage

    actual suspend fun getSearchAnime(page: Int, query: String, filters: AnimeFilterList): AnimesPage

    actual suspend fun getLatestUpdates(page: Int): AnimesPage

    actual fun getFilterList(): AnimeFilterList

    actual val supportsRelatedAnime: Boolean get() = false

    actual val disableRelatedAnimeBySearch: Boolean get() = false

    actual val disableRelatedAnime: Boolean get() = false

    actual override suspend fun getRelatedAnimeList(
        anime: SAnime,
        exceptionHandler: (Throwable) -> Unit,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    ) {
        val handler = CoroutineExceptionHandler { _, e -> exceptionHandler(e) }
        if (!disableRelatedAnime) {
            supervisorScope {
                if (supportsRelatedAnime) launch(handler) { getRelatedAnimeListByExtension(anime, pushResults) }
                if (!disableRelatedAnimeBySearch) launch(handler) { getRelatedAnimeListBySearch(anime, pushResults) }
            }
        }
    }

    actual suspend fun getRelatedAnimeListByExtension(
        anime: SAnime,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    ) {
        runCatching { fetchRelatedAnimeList(anime) }
            .onSuccess { if (it.isNotEmpty()) pushResults(Pair("", it), false) }
            .onFailure { e ->
                sourceApiLogError("getRelatedAnimeListByExtension failed", e)
            }
    }

    actual suspend fun fetchRelatedAnimeList(anime: SAnime): List<SAnime> =
        throw UnsupportedOperationException("Unsupported!")

    actual suspend fun getRelatedAnimeListBySearch(
        anime: SAnime,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    ) {
        val words = linkedSetOf(anime.title)
        anime.title.stripKeywordForRelatedAnime()
            .filterNot { word -> words.any { it.equals(word, ignoreCase = true) } }
            .onEach { words.add(it) }
        if (words.isEmpty()) return

        coroutineScope {
            val filterList = getFilterList()
            words.map { keyword ->
                launch {
                    runCatching {
                        getSearchAnime(1, keyword.sanitizeRelatedQuery(), filterList).animes
                    }
                        .onSuccess { if (it.isNotEmpty()) pushResults(Pair(keyword, it), false) }
                        .onFailure { e ->
                            sourceApiLogError("getRelatedAnimeListBySearch failed", e)
                        }
                }
            }
        }
    }
}

private fun String.stripKeywordForRelatedAnime(): List<String> {
    val regexWhitespace = Regex("\\s+")
    val regexSpecialCharacters = Regex("([!~#$%^&*+_|/\\\\,?:;'\"<>(){}\\[\\]]|\\s-|-\\s|\\s\\.|\\.\\s])")
    val regexNumberOnly = Regex("^\\d+$")

    return replace(regexSpecialCharacters, " ")
        .split(regexWhitespace)
        .map {
            it.replace(regexNumberOnly, "")
                .lowercase()
        }
        .filter { it.length > 1 }
}

private fun String.sanitizeRelatedQuery(): String {
    return trim()
        .trim(' ', '-', '_', ',', ':')
        .replace('\u2018', '\'')
        .replace('\u2019', '\'')
        .replace('\u201C', '"')
        .replace('\u201D', '"')
        .replace('\u2013', '-')
        .replace('\u2014', '-')
        .replace("\u2026", "...")
}
