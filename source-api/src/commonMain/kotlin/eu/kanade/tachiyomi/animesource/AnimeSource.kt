package eu.kanade.tachiyomi.animesource

import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode

/**
 * Common anime source contract used by the KMP shell.
 *
 * JVM/Android actuals keep the legacy Rx fetch bridge for existing extensions,
 * while native actuals expose the suspend-only API expected by script/native
 * engines.
 */
expect interface AnimeSource {

    val id: Long

    val name: String

    val lang: String

    suspend fun getAnimeDetails(anime: SAnime): SAnime

    suspend fun getEpisodeList(anime: SAnime): List<SEpisode>

    suspend fun getSeasonList(anime: SAnime): List<SAnime>

    suspend fun getRelatedAnimeList(
        anime: SAnime,
        exceptionHandler: (Throwable) -> Unit,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    )
}
