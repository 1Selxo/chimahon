package eu.kanade.tachiyomi.animesource

actual interface AnimeSourceFactory {
    actual fun createSources(): List<AnimeSource>
}
