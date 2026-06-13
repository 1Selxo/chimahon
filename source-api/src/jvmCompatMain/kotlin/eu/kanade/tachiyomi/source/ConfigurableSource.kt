package eu.kanade.tachiyomi.source

interface ConfigurableSource : Source {

    /**
     * Gets instance of [PlatformSharedPreferences] scoped to the specific source.
     *
     * @since extensions-lib 1.5
     */
    fun getSourcePreferences(): PlatformSharedPreferences =
        platformSourcePreferences(preferenceKey())

    fun setupPreferenceScreen(screen: PreferenceScreen)
}

fun ConfigurableSource.preferenceKey(): String = "source_$id"

// TODO: use getSourcePreferences once all extensions are on ext-lib 1.5
fun ConfigurableSource.sourcePreferences(): PlatformSharedPreferences =
    platformSourcePreferences(preferenceKey())

fun sourcePreferences(key: String): PlatformSharedPreferences =
    platformSourcePreferences(key)
