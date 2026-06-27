package eu.kanade.tachiyomi.animesource

import eu.kanade.tachiyomi.source.PlatformSharedPreferences
import eu.kanade.tachiyomi.source.platformSourcePreferences

interface ConfigurableAnimeSource : AnimeSource {

    fun getSourcePreferences(): PlatformSharedPreferences =
        platformSourcePreferences(preferenceKey())

    fun setupPreferenceScreen(screen: PreferenceScreen)
}

fun ConfigurableAnimeSource.preferenceKey(): String = "source_$id"

fun ConfigurableAnimeSource.sourcePreferences(): PlatformSharedPreferences =
    platformSourcePreferences(preferenceKey())
