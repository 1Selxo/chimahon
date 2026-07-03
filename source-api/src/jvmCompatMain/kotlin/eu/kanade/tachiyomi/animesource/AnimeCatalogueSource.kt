package eu.kanade.tachiyomi.animesource

import eu.kanade.tachiyomi.animesource.model.AnimeFilterList
import eu.kanade.tachiyomi.animesource.model.AnimesPage
import eu.kanade.tachiyomi.animesource.model.SAnime
import rx.Observable

actual interface AnimeCatalogueSource : AnimeSource {

    actual override val lang: String

    actual val supportsLatest: Boolean

    @Suppress("DEPRECATION")
    actual suspend fun getPopularAnime(page: Int): AnimesPage

    @Suppress("DEPRECATION")
    actual suspend fun getSearchAnime(page: Int, query: String, filters: AnimeFilterList): AnimesPage

    @Suppress("DEPRECATION")
    actual suspend fun getLatestUpdates(page: Int): AnimesPage

    actual fun getFilterList(): AnimeFilterList

    /**
     * Whether the extension provides its own related anime request.
     */
    actual val supportsRelatedAnime: Boolean

    /**
     * Extensions can opt out of the app's title-search based recommendations.
     */
    actual val disableRelatedAnimeBySearch: Boolean

    /**
     * Disable showing any related anime.
     */
    actual val disableRelatedAnime: Boolean

    actual override suspend fun getRelatedAnimeList(
        anime: SAnime,
        exceptionHandler: (Throwable) -> Unit,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    )

    actual suspend fun getRelatedAnimeListByExtension(
        anime: SAnime,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    )

    actual suspend fun fetchRelatedAnimeList(anime: SAnime): List<SAnime>

    actual suspend fun getRelatedAnimeListBySearch(
        anime: SAnime,
        pushResults: suspend (relatedAnime: Pair<String, List<SAnime>>, completed: Boolean) -> Unit,
    )

    @Deprecated(
        "Use the non-RxJava API instead",
        ReplaceWith("getPopularAnime"),
    )
    fun fetchPopularAnime(page: Int): Observable<AnimesPage>

    @Deprecated(
        "Use the non-RxJava API instead",
        ReplaceWith("getSearchAnime"),
    )
    fun fetchSearchAnime(page: Int, query: String, filters: AnimeFilterList): Observable<AnimesPage>

    @Deprecated(
        "Use the non-RxJava API instead",
        ReplaceWith("getLatestUpdates"),
    )
    fun fetchLatestUpdates(page: Int): Observable<AnimesPage>
}
