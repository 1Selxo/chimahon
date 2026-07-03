package eu.kanade.tachiyomi.animesource

expect interface AnimeSourceFactory {
    fun createSources(): List<AnimeSource>
}
