package eu.kanade.tachiyomi.animesource.online

import eu.kanade.tachiyomi.animesource.AnimeSource
import eu.kanade.tachiyomi.animesource.model.SAnime
import eu.kanade.tachiyomi.animesource.model.SEpisode

actual interface ResolvableAnimeSource : AnimeSource {

    actual fun getUriType(uri: String): UriType

    actual suspend fun getAnime(uri: String): SAnime?

    actual suspend fun getEpisode(uri: String): SEpisode?
}
