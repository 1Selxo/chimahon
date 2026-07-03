package eu.kanade.tachiyomi.animesource

import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode

actual interface AnimeSource {

    actual val id: Long

    actual val name: String

    actual val lang: String
        get() = ""

    actual suspend fun getAnimeDetails(anime: SAnime): SAnime

    actual suspend fun getEpisodeList(anime: SAnime): List<SEpisode>

    actual suspend fun getSeasonList(anime: SAnime): List<SAnime> = emptyList()

    actual suspend fun getRelatedAnimeList(
        anime: SAnime,
        exceptionHandler: (Throwable) -> Unit,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    ) {
        getRelatedMangaList(anime, exceptionHandler, pushResults)
    }

    suspend fun getRelatedMangaList(
        anime: SAnime,
        exceptionHandler: (Throwable) -> Unit,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    ): Unit = throw UnsupportedOperationException()
}
