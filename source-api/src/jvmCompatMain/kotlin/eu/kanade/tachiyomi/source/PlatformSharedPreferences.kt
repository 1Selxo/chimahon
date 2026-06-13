package eu.kanade.tachiyomi.source

expect interface PlatformSharedPreferences

expect fun platformSourcePreferences(key: String): PlatformSharedPreferences
