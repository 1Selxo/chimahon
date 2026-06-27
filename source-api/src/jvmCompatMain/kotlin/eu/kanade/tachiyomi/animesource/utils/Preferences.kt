package eu.kanade.tachiyomi.animesource.utils

import eu.kanade.tachiyomi.animesource.AnimeSource
import eu.kanade.tachiyomi.source.PlatformSharedPreferences
import eu.kanade.tachiyomi.source.platformSourcePreferences

fun preferencesKey(id: Long) = "source_$id"

fun AnimeSource.preferencesKey(): String = preferencesKey(id)

fun sourcePreferences(key: String): PlatformSharedPreferences =
    platformSourcePreferences(key)

fun AnimeSource.sourcePreferences(): PlatformSharedPreferences = sourcePreferences(preferencesKey())

fun sourcePreferences(id: Long): PlatformSharedPreferences = sourcePreferences(preferencesKey(id))
