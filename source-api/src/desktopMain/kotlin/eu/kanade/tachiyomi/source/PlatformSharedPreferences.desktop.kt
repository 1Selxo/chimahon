package eu.kanade.tachiyomi.source

actual interface PlatformSharedPreferences

private object EmptyPlatformSharedPreferences : PlatformSharedPreferences

actual fun platformSourcePreferences(key: String): PlatformSharedPreferences {
    return EmptyPlatformSharedPreferences
}
