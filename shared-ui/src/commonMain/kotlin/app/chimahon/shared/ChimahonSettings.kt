package app.chimahon.shared

import tachiyomi.core.platform.settings.PlatformSettingsStore
import tachiyomi.core.platform.settings.readBoolean
import tachiyomi.core.platform.settings.writeBoolean

data class ChimahonSettings(
    val reader: ChimahonReaderSettings = ChimahonReaderSettings(),
    val appMode: ChimahonAppModeSettings = ChimahonAppModeSettings(),
)

data class ChimahonReaderSettings(
    val mode: ChimahonReaderMode = ChimahonReaderMode.Webtoon,
    val scale: ChimahonReaderScale = ChimahonReaderScale.FitWidth,
    val canvas: ChimahonReaderCanvas = ChimahonReaderCanvas.Black,
)

enum class ChimahonReaderMode {
    Webtoon,
    Vertical,
    LeftToRight,
    RightToLeft,
}

enum class ChimahonReaderScale {
    FitScreen,
    FitWidth,
}

enum class ChimahonReaderCanvas {
    Black,
    Gray,
    White,
}

data class ChimahonAppModeSettings(
    val downloadedOnly: Boolean = false,
    val incognitoMode: Boolean = false,
)

internal class ChimahonSettingsRepository(
    private val settingsStore: PlatformSettingsStore,
) {

    suspend fun loadSettings(): ChimahonSettings {
        return ChimahonSettings(
            reader = loadReaderSettings(),
            appMode = loadAppModeSettings(),
        )
    }

    suspend fun loadReaderSettings(): ChimahonReaderSettings {
        return ChimahonReaderSettings(
            mode = readEnum(READER_MODE_KEY, ChimahonReaderMode.Webtoon),
            scale = readEnum(READER_SCALE_KEY, ChimahonReaderScale.FitWidth),
            canvas = readEnum(READER_CANVAS_KEY, ChimahonReaderCanvas.Black),
        )
    }

    suspend fun saveReaderSettings(settings: ChimahonReaderSettings): ChimahonReaderSettings {
        settingsStore.writeString(READER_MODE_KEY, settings.mode.name)
        settingsStore.writeString(READER_SCALE_KEY, settings.scale.name)
        settingsStore.writeString(READER_CANVAS_KEY, settings.canvas.name)
        return settings
    }

    suspend fun loadAppModeSettings(): ChimahonAppModeSettings {
        return ChimahonAppModeSettings(
            downloadedOnly = settingsStore.readBoolean(DOWNLOADED_ONLY_KEY),
            incognitoMode = settingsStore.readBoolean(INCOGNITO_MODE_KEY),
        )
    }

    suspend fun setDownloadedOnly(enabled: Boolean): ChimahonAppModeSettings {
        settingsStore.writeBoolean(DOWNLOADED_ONLY_KEY, enabled)
        return loadAppModeSettings()
    }

    suspend fun setIncognitoMode(enabled: Boolean): ChimahonAppModeSettings {
        settingsStore.writeBoolean(INCOGNITO_MODE_KEY, enabled)
        return loadAppModeSettings()
    }

    suspend fun isIncognitoModeEnabled(): Boolean {
        return settingsStore.readBoolean(INCOGNITO_MODE_KEY)
    }

    private suspend inline fun <reified T : Enum<T>> readEnum(
        key: String,
        defaultValue: T,
    ): T {
        val stored = settingsStore.readString(key) ?: return defaultValue
        return enumValues<T>().firstOrNull { it.name == stored } ?: defaultValue
    }

    private companion object {
        const val READER_MODE_KEY = "__APP_STATE_chimahon_reader_mode"
        const val READER_SCALE_KEY = "__APP_STATE_chimahon_reader_scale"
        const val READER_CANVAS_KEY = "__APP_STATE_chimahon_reader_canvas"
        const val DOWNLOADED_ONLY_KEY = "__APP_STATE_pref_downloaded_only"
        const val INCOGNITO_MODE_KEY = "__APP_STATE_incognito_mode"
    }
}
