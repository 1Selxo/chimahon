package eu.kanade.tachiyomi.animesource

import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.SAnime

expect interface AnimeCatalogueSource : AnimeSource {

    override val lang: String

    val supportsLatest: Boolean

    suspend fun getPopularAnime(page: Int): AnimesPage

    suspend fun getSearchAnime(page: Int, query: String, filters: AnimeFilterList): AnimesPage

    suspend fun getLatestUpdates(page: Int): AnimesPage

    fun getFilterList(): AnimeFilterList

    val supportsRelatedAnime: Boolean

    val disableRelatedAnimeBySearch: Boolean

    val disableRelatedAnime: Boolean

    override suspend fun getRelatedAnimeList(
        anime: SAnime,
        exceptionHandler: (Throwable) -> Unit,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    )

    suspend fun getRelatedAnimeListByExtension(
        anime: SAnime,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    )

    suspend fun fetchRelatedAnimeList(anime: SAnime): List<SAnime>

    suspend fun getRelatedAnimeListBySearch(
        anime: SAnime,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    )
}
