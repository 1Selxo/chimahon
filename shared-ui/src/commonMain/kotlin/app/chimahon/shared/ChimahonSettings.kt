package app.chimahon.shared

import tachiyomi.core.platform.settings.PlatformSettingsStore
import tachiyomi.core.platform.settings.readBoolean
import tachiyomi.core.platform.settings.writeBoolean

data class ChimahonSettings(
    val reader: ChimahonReaderSettings = ChimahonReaderSettings(),
    val library: ChimahonLibrarySettings = ChimahonLibrarySettings(),
    val appMode: ChimahonAppModeSettings = ChimahonAppModeSettings(),
)

data class ChimahonReaderSettings(
    val mode: ChimahonReaderMode = ChimahonReaderMode.Webtoon,
    val scale: ChimahonReaderScale = ChimahonReaderScale.FitWidth,
    val canvas: ChimahonReaderCanvas = ChimahonReaderCanvas.Black,
    val showPageStrip: Boolean = true,
    val keepControlsVisible: Boolean = true,
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

data class ChimahonLibrarySettings(
    val displayMode: ChimahonLibraryDisplayMode = ChimahonLibraryDisplayMode.ComfortableGrid,
    val showCategoryTabs: Boolean = true,
    val showUnreadBadges: Boolean = true,
    val showContinueButtons: Boolean = true,
)

enum class ChimahonLibraryDisplayMode {
    ComfortableGrid,
    CompactGrid,
    List,
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
            library = loadLibrarySettings(),
            appMode = loadAppModeSettings(),
        )
    }

    suspend fun loadReaderSettings(): ChimahonReaderSettings {
        return ChimahonReaderSettings(
            mode = readEnum(READER_MODE_KEY, ChimahonReaderMode.Webtoon),
            scale = readEnum(READER_SCALE_KEY, ChimahonReaderScale.FitWidth),
            canvas = readEnum(READER_CANVAS_KEY, ChimahonReaderCanvas.Black),
            showPageStrip = settingsStore.readBoolean(READER_PAGE_STRIP_KEY, defaultValue = true),
            keepControlsVisible = settingsStore.readBoolean(READER_CONTROLS_KEY, defaultValue = true),
        )
    }

    suspend fun saveReaderSettings(settings: ChimahonReaderSettings): ChimahonReaderSettings {
        settingsStore.writeString(READER_MODE_KEY, settings.mode.name)
        settingsStore.writeString(READER_SCALE_KEY, settings.scale.name)
        settingsStore.writeString(READER_CANVAS_KEY, settings.canvas.name)
        settingsStore.writeBoolean(READER_PAGE_STRIP_KEY, settings.showPageStrip)
        settingsStore.writeBoolean(READER_CONTROLS_KEY, settings.keepControlsVisible)
        return settings
    }

    suspend fun loadLibrarySettings(): ChimahonLibrarySettings {
        return ChimahonLibrarySettings(
            displayMode = readEnum(
                LIBRARY_DISPLAY_MODE_KEY,
                ChimahonLibraryDisplayMode.ComfortableGrid,
            ),
            showCategoryTabs = settingsStore.readBoolean(
                LIBRARY_CATEGORY_TABS_KEY,
                defaultValue = true,
            ),
            showUnreadBadges = settingsStore.readBoolean(
                LIBRARY_UNREAD_BADGES_KEY,
                defaultValue = true,
            ),
            showContinueButtons = settingsStore.readBoolean(
                LIBRARY_CONTINUE_BUTTONS_KEY,
                defaultValue = true,
            ),
        )
    }

    suspend fun saveLibrarySettings(settings: ChimahonLibrarySettings): ChimahonLibrarySettings {
        settingsStore.writeString(LIBRARY_DISPLAY_MODE_KEY, settings.displayMode.name)
        settingsStore.writeBoolean(LIBRARY_CATEGORY_TABS_KEY, settings.showCategoryTabs)
        settingsStore.writeBoolean(LIBRARY_UNREAD_BADGES_KEY, settings.showUnreadBadges)
        settingsStore.writeBoolean(LIBRARY_CONTINUE_BUTTONS_KEY, settings.showContinueButtons)
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
        const val READER_PAGE_STRIP_KEY = "__APP_STATE_chimahon_reader_page_strip"
        const val READER_CONTROLS_KEY = "__APP_STATE_chimahon_reader_controls"
        const val LIBRARY_DISPLAY_MODE_KEY = "__APP_STATE_chimahon_library_display_mode"
        const val LIBRARY_CATEGORY_TABS_KEY = "__APP_STATE_chimahon_library_category_tabs"
        const val LIBRARY_UNREAD_BADGES_KEY = "__APP_STATE_chimahon_library_unread_badges"
        const val LIBRARY_CONTINUE_BUTTONS_KEY = "__APP_STATE_chimahon_library_continue_buttons"
        const val DOWNLOADED_ONLY_KEY = "__APP_STATE_pref_downloaded_only"
        const val INCOGNITO_MODE_KEY = "__APP_STATE_incognito_mode"
    }
}
