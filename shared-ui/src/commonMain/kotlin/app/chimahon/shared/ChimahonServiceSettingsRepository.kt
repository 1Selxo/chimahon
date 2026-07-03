package app.chimahon.shared

import tachiyomi.core.platform.settings.PlatformSettingsStore
import tachiyomi.core.platform.settings.readBoolean
import tachiyomi.core.platform.settings.readInt
import tachiyomi.core.platform.settings.writeBoolean
import tachiyomi.core.platform.settings.writeInt

internal data class ChimahonServiceParitySettings(
    val libraryDisplayMode: ChimahonLibraryDisplayMode = ChimahonLibraryDisplayMode.ComfortableGrid,
    val libraryContinueButtonMode: ChimahonLibraryContinueButtonMode =
        ChimahonLibraryContinueButtonMode.Unread,
    val libraryShowLocalBadges: Boolean = true,
    val libraryShowSourceBadges: Boolean = false,
    val libraryShowTrackingBadges: Boolean = false,
    val libraryRememberCategorySelection: Boolean = true,
    val browseDefaultTab: ChimahonBrowseTab = ChimahonBrowseTab.Sources,
    val browseSourceFilterMode: ChimahonBrowseSourceFilterMode = ChimahonBrowseSourceFilterMode.All,
    val browseExtensionFilterMode: ChimahonExtensionFilterMode = ChimahonExtensionFilterMode.All,
    val browseGlobalSearchPinnedOnly: Boolean = false,
    val browseHideUnavailableSources: Boolean = false,
    val readerKeyboardShortcutsEnabled: Boolean = true,
    val readerKeyboardScheme: ChimahonReaderKeyboardScheme =
        ChimahonReaderKeyboardScheme.AndroidCompatible,
    val readerMouseWheelAction: ChimahonReaderMouseWheelAction =
        ChimahonReaderMouseWheelAction.Scroll,
    val readerMouseWheelSensitivityPercent: Int = 100,
    val readerDesktopTapNavigation: Boolean = true,
    val readerDesktopPageButtons: Boolean = true,
    val readerHideCursorWhileReading: Boolean = true,
    val readerTextTheme: ChimahonReaderTextTheme = ChimahonReaderTextTheme.System,
    val readerSystemLightSepia: Boolean = false,
    val readerUiTheme: ChimahonReaderTextTheme = ChimahonReaderTextTheme.System,
    val readerCustomBackgroundColor: Int = 0xFFF2E2C9.toInt(),
    val readerCustomTextColor: Int = 0xFF000000.toInt(),
    val readerCustomInfoColor: Int = 0xFF444444.toInt(),
    val readerCustomThemes: List<ChimahonReaderCustomTheme> = emptyList(),
    val readerSelectedFont: String = "System",
    val readerLayoutAdvanced: Boolean = false,
    val readerStatisticsEnabled: Boolean = true,
    val readerStatisticsAutostartMode: ChimahonReaderStatisticsAutostartMode =
        ChimahonReaderStatisticsAutostartMode.On,
    val readerLookupPopupWidth: Int = 300,
    val readerLookupPopupHeight: Int = 200,
    val readerLookupPopupFullWidth: Boolean = false,
    val readerLookupPopupSwipeToDismiss: Boolean = true,
    val readerLookupPopupSwipeThreshold: Int = 50,
    val readerLookupMaxResults: Int = 10,
    val readerLookupScanLength: Int = 50,
    val dictionaryThemeMode: ChimahonDictionaryThemeMode = ChimahonDictionaryThemeMode.System,
    val dictionaryCustomColor: Int = 0,
    val dictionaryFontFamily: String = "",
    val dictionaryCustomCss: String = "",
    val dictionaryVideoOcrSentenceAudioPaddingSeconds: Int = 3,
    val dictionaryWordAudioEnabled: Boolean = true,
    val dictionaryWordAudioAutoplay: Boolean = false,
    val dictionaryWordAudioSources: String = "[]",
    val dictionaryWordAudioLocalPath: String = "",
    val dictionaryWordAudioLocalUri: String = "",
    val dictionaryWordAudioLocalEnabled: Boolean = false,
    val downloadStorageLocation: ChimahonDownloadStorageLocation =
        ChimahonDownloadStorageLocation.AppManaged,
    val downloadCleanupPolicy: ChimahonDownloadCleanupPolicy =
        ChimahonDownloadCleanupPolicy.AfterRead,
    val downloadOrder: ChimahonDownloadOrder = ChimahonDownloadOrder.Ascending,
    val downloadPauseWhenMetered: Boolean = true,
    val downloadRequireCharging: Boolean = false,
    val downloadPauseWhenBatteryLow: Boolean = false,
    val downloadMaxRetryCount: Int = 3,
    val downloadPreserveQueueOnExit: Boolean = true,
    val securityHideHistoryInIncognito: Boolean = true,
    val securityHideDownloadsInIncognito: Boolean = true,
    val securityClearCookiesOnExit: Boolean = false,
    val securityClearSearchHistoryOnExit: Boolean = false,
    val dataAutoBackupEnabled: Boolean = false,
    val dataBackupWifiOnly: Boolean = true,
    val dataBackupWhileChargingOnly: Boolean = false,
    val dataIncludeDownloadsInBackups: Boolean = false,
    val dataBackupRetentionCount: Int = 3,
)

internal class ChimahonServiceSettingsRepository(
    private val settingsStore: PlatformSettingsStore,
) {
    suspend fun load(): ChimahonServiceSettings {
        return ChimahonServiceSettings(
            ui = loadUiSettings(),
            downloads = loadDownloadSettings(),
        )
    }

    suspend fun loadUiSettings(): ChimahonUiSettings {
        return ChimahonUiSettings(
            keepReaderControlsVisible = settingsStore.readBoolean(KEEP_READER_CONTROLS_VISIBLE_KEY),
            rightToLeftByDefault = settingsStore.readBoolean(RIGHT_TO_LEFT_BY_DEFAULT_KEY),
            showUnreadBadges = settingsStore.readBoolean(SHOW_UNREAD_BADGES_KEY, true),
            showCategoryTabs = settingsStore.readBoolean(SHOW_CATEGORY_TABS_KEY, true),
            showHiddenCategories = settingsStore.readBoolean(SHOW_HIDDEN_CATEGORIES_KEY),
        )
    }

    suspend fun saveUiSettings(settings: ChimahonUiSettings): ChimahonUiSettings {
        settingsStore.writeBoolean(KEEP_READER_CONTROLS_VISIBLE_KEY, settings.keepReaderControlsVisible)
        settingsStore.writeBoolean(RIGHT_TO_LEFT_BY_DEFAULT_KEY, settings.rightToLeftByDefault)
        settingsStore.writeBoolean(SHOW_UNREAD_BADGES_KEY, settings.showUnreadBadges)
        settingsStore.writeBoolean(SHOW_CATEGORY_TABS_KEY, settings.showCategoryTabs)
        settingsStore.writeBoolean(SHOW_HIDDEN_CATEGORIES_KEY, settings.showHiddenCategories)
        return settings
    }

    suspend fun loadDownloadSettings(): ChimahonDownloadSettings {
        return ChimahonDownloadSettings(
            downloadOnlyOverWifi = settingsStore.readBoolean(DOWNLOAD_ONLY_OVER_WIFI_KEY, true),
            autoDownloadNewChapters = settingsStore.readBoolean(AUTO_DOWNLOAD_NEW_CHAPTERS_KEY),
            autoDownloadUnreadOnly = settingsStore.readBoolean(AUTO_DOWNLOAD_UNREAD_ONLY_KEY, true),
            parallelDownloads = settingsStore.readInt(PARALLEL_DOWNLOADS_KEY, 2)
                .coerceIn(MIN_PARALLEL_DOWNLOADS, MAX_PARALLEL_DOWNLOADS),
        )
    }

    suspend fun saveDownloadSettings(settings: ChimahonDownloadSettings): ChimahonDownloadSettings {
        val normalized = settings.copy(
            parallelDownloads = settings.parallelDownloads
                .coerceIn(MIN_PARALLEL_DOWNLOADS, MAX_PARALLEL_DOWNLOADS),
        )
        settingsStore.writeBoolean(DOWNLOAD_ONLY_OVER_WIFI_KEY, normalized.downloadOnlyOverWifi)
        settingsStore.writeBoolean(AUTO_DOWNLOAD_NEW_CHAPTERS_KEY, normalized.autoDownloadNewChapters)
        settingsStore.writeBoolean(AUTO_DOWNLOAD_UNREAD_ONLY_KEY, normalized.autoDownloadUnreadOnly)
        settingsStore.writeInt(PARALLEL_DOWNLOADS_KEY, normalized.parallelDownloads)
        return normalized
    }

    suspend fun loadParitySettings(): ChimahonServiceParitySettings {
        return ChimahonServiceParitySettings(
            libraryDisplayMode = readEnum(
                LIBRARY_DISPLAY_MODE_KEY,
                ChimahonLibraryDisplayMode.ComfortableGrid,
            ),
            libraryContinueButtonMode = readEnum(
                LIBRARY_CONTINUE_BUTTON_MODE_KEY,
                ChimahonLibraryContinueButtonMode.Unread,
            ),
            libraryShowLocalBadges = settingsStore.readBoolean(
                LIBRARY_LOCAL_BADGES_KEY,
                defaultValue = true,
            ),
            libraryShowSourceBadges = settingsStore.readBoolean(LIBRARY_SOURCE_BADGES_KEY),
            libraryShowTrackingBadges = settingsStore.readBoolean(LIBRARY_TRACKING_BADGES_KEY),
            libraryRememberCategorySelection = settingsStore.readBoolean(
                LIBRARY_REMEMBER_CATEGORY_SELECTION_KEY,
                defaultValue = true,
            ),
            browseDefaultTab = readEnum(BROWSE_DEFAULT_TAB_KEY, ChimahonBrowseTab.Sources),
            browseSourceFilterMode = readEnum(
                BROWSE_SOURCE_FILTER_MODE_KEY,
                ChimahonBrowseSourceFilterMode.All,
            ),
            browseExtensionFilterMode = readEnum(
                BROWSE_EXTENSION_FILTER_MODE_KEY,
                ChimahonExtensionFilterMode.All,
            ),
            browseGlobalSearchPinnedOnly = settingsStore.readBoolean(
                BROWSE_GLOBAL_SEARCH_PINNED_ONLY_KEY,
            ),
            browseHideUnavailableSources = settingsStore.readBoolean(
                BROWSE_HIDE_UNAVAILABLE_SOURCES_KEY,
            ),
            readerKeyboardShortcutsEnabled = settingsStore.readBoolean(
                READER_KEYBOARD_SHORTCUTS_KEY,
                defaultValue = true,
            ),
            readerKeyboardScheme = readEnum(
                READER_KEYBOARD_SCHEME_KEY,
                ChimahonReaderKeyboardScheme.AndroidCompatible,
            ),
            readerMouseWheelAction = readEnum(
                READER_MOUSE_WHEEL_ACTION_KEY,
                ChimahonReaderMouseWheelAction.Scroll,
            ),
            readerMouseWheelSensitivityPercent = settingsStore.readInt(
                READER_MOUSE_WHEEL_SENSITIVITY_KEY,
                defaultValue = 100,
            ).coerceIn(MIN_MOUSE_WHEEL_SENSITIVITY, MAX_MOUSE_WHEEL_SENSITIVITY),
            readerDesktopTapNavigation = settingsStore.readBoolean(
                READER_DESKTOP_TAP_NAVIGATION_KEY,
                defaultValue = true,
            ),
            readerDesktopPageButtons = settingsStore.readBoolean(
                READER_DESKTOP_PAGE_BUTTONS_KEY,
                defaultValue = true,
            ),
            readerHideCursorWhileReading = settingsStore.readBoolean(
                READER_HIDE_CURSOR_WHILE_READING_KEY,
                defaultValue = true,
            ),
            readerTextTheme = readEnum(READER_TEXT_THEME_KEY, ChimahonReaderTextTheme.System),
            readerSystemLightSepia = settingsStore.readBoolean(READER_SYSTEM_LIGHT_SEPIA_KEY),
            readerUiTheme = readEnum(READER_UI_THEME_KEY, ChimahonReaderTextTheme.System),
            readerCustomBackgroundColor = settingsStore.readInt(
                READER_CUSTOM_BACKGROUND_COLOR_KEY,
                defaultValue = 0xFFF2E2C9.toInt(),
            ),
            readerCustomTextColor = settingsStore.readInt(
                READER_CUSTOM_TEXT_COLOR_KEY,
                defaultValue = 0xFF000000.toInt(),
            ),
            readerCustomInfoColor = settingsStore.readInt(
                READER_CUSTOM_INFO_COLOR_KEY,
                defaultValue = 0xFF444444.toInt(),
            ),
            readerCustomThemes = parseReaderCustomThemes(settingsStore.readString(READER_CUSTOM_THEMES_KEY)),
            readerSelectedFont = settingsStore.readString(READER_SELECTED_FONT_KEY) ?: "System",
            readerLayoutAdvanced = settingsStore.readBoolean(READER_LAYOUT_ADVANCED_KEY),
            readerStatisticsEnabled = settingsStore.readBoolean(
                READER_STATISTICS_ENABLED_KEY,
                defaultValue = true,
            ),
            readerStatisticsAutostartMode = readEnum(
                READER_STATISTICS_AUTOSTART_MODE_KEY,
                ChimahonReaderStatisticsAutostartMode.On,
            ),
            readerLookupPopupWidth = settingsStore.readInt(
                READER_LOOKUP_POPUP_WIDTH_KEY,
                defaultValue = 300,
            ),
            readerLookupPopupHeight = settingsStore.readInt(
                READER_LOOKUP_POPUP_HEIGHT_KEY,
                defaultValue = 200,
            ),
            readerLookupPopupFullWidth = settingsStore.readBoolean(READER_LOOKUP_POPUP_FULL_WIDTH_KEY),
            readerLookupPopupSwipeToDismiss = settingsStore.readBoolean(
                READER_LOOKUP_POPUP_SWIPE_TO_DISMISS_KEY,
                defaultValue = true,
            ),
            readerLookupPopupSwipeThreshold = settingsStore.readInt(
                READER_LOOKUP_POPUP_SWIPE_THRESHOLD_KEY,
                defaultValue = 50,
            ),
            readerLookupMaxResults = settingsStore.readInt(
                READER_LOOKUP_MAX_RESULTS_KEY,
                defaultValue = 10,
            ),
            readerLookupScanLength = settingsStore.readInt(
                READER_LOOKUP_SCAN_LENGTH_KEY,
                defaultValue = 50,
            ),
            dictionaryThemeMode = readEnum(
                DICTIONARY_THEME_MODE_KEY,
                ChimahonDictionaryThemeMode.System,
            ),
            dictionaryCustomColor = settingsStore.readInt(DICTIONARY_CUSTOM_COLOR_KEY),
            dictionaryFontFamily = settingsStore.readString(DICTIONARY_FONT_FAMILY_KEY) ?: "",
            dictionaryCustomCss = settingsStore.readString(DICTIONARY_CUSTOM_CSS_KEY) ?: "",
            dictionaryVideoOcrSentenceAudioPaddingSeconds = settingsStore.readInt(
                DICTIONARY_VIDEO_OCR_AUDIO_PADDING_SECONDS_KEY,
                defaultValue = 3,
            ),
            dictionaryWordAudioEnabled = settingsStore.readBoolean(
                DICTIONARY_WORD_AUDIO_ENABLED_KEY,
                defaultValue = true,
            ),
            dictionaryWordAudioAutoplay = settingsStore.readBoolean(DICTIONARY_WORD_AUDIO_AUTOPLAY_KEY),
            dictionaryWordAudioSources = settingsStore.readString(DICTIONARY_WORD_AUDIO_SOURCES_KEY) ?: "[]",
            dictionaryWordAudioLocalPath = settingsStore.readString(
                DICTIONARY_WORD_AUDIO_LOCAL_PATH_KEY,
            ) ?: "",
            dictionaryWordAudioLocalUri = settingsStore.readString(
                DICTIONARY_WORD_AUDIO_LOCAL_URI_KEY,
            ) ?: "",
            dictionaryWordAudioLocalEnabled = settingsStore.readBoolean(
                DICTIONARY_WORD_AUDIO_LOCAL_ENABLED_KEY,
            ),
            downloadStorageLocation = readEnum(
                DOWNLOAD_STORAGE_LOCATION_KEY,
                ChimahonDownloadStorageLocation.AppManaged,
            ),
            downloadCleanupPolicy = readEnum(
                DOWNLOAD_CLEANUP_POLICY_KEY,
                ChimahonDownloadCleanupPolicy.AfterRead,
            ),
            downloadOrder = readEnum(DOWNLOAD_ORDER_KEY, ChimahonDownloadOrder.Ascending),
            downloadPauseWhenMetered = settingsStore.readBoolean(
                DOWNLOAD_PAUSE_WHEN_METERED_KEY,
                defaultValue = true,
            ),
            downloadRequireCharging = settingsStore.readBoolean(DOWNLOAD_REQUIRE_CHARGING_KEY),
            downloadPauseWhenBatteryLow = settingsStore.readBoolean(DOWNLOAD_PAUSE_BATTERY_LOW_KEY),
            downloadMaxRetryCount = settingsStore.readInt(
                DOWNLOAD_MAX_RETRY_COUNT_KEY,
                defaultValue = 3,
            ).coerceIn(MIN_RETRY_COUNT, MAX_RETRY_COUNT),
            downloadPreserveQueueOnExit = settingsStore.readBoolean(
                DOWNLOAD_PRESERVE_QUEUE_ON_EXIT_KEY,
                defaultValue = true,
            ),
            securityHideHistoryInIncognito = settingsStore.readBoolean(
                SECURITY_HIDE_HISTORY_IN_INCOGNITO_KEY,
                defaultValue = true,
            ),
            securityHideDownloadsInIncognito = settingsStore.readBoolean(
                SECURITY_HIDE_DOWNLOADS_IN_INCOGNITO_KEY,
                defaultValue = true,
            ),
            securityClearCookiesOnExit = settingsStore.readBoolean(SECURITY_CLEAR_COOKIES_ON_EXIT_KEY),
            securityClearSearchHistoryOnExit = settingsStore.readBoolean(
                SECURITY_CLEAR_SEARCH_HISTORY_ON_EXIT_KEY,
            ),
            dataAutoBackupEnabled = settingsStore.readBoolean(DATA_AUTO_BACKUP_ENABLED_KEY),
            dataBackupWifiOnly = settingsStore.readBoolean(DATA_BACKUP_WIFI_ONLY_KEY, defaultValue = true),
            dataBackupWhileChargingOnly = settingsStore.readBoolean(DATA_BACKUP_CHARGING_ONLY_KEY),
            dataIncludeDownloadsInBackups = settingsStore.readBoolean(DATA_BACKUP_INCLUDE_DOWNLOADS_KEY),
            dataBackupRetentionCount = settingsStore.readInt(
                DATA_BACKUP_RETENTION_COUNT_KEY,
                defaultValue = 3,
            ).coerceIn(MIN_BACKUP_RETENTION, MAX_BACKUP_RETENTION),
        )
    }

    suspend fun saveParitySettings(settings: ChimahonServiceParitySettings): ChimahonServiceParitySettings {
        val normalized = settings.copy(
            readerMouseWheelSensitivityPercent = settings.readerMouseWheelSensitivityPercent
                .coerceIn(MIN_MOUSE_WHEEL_SENSITIVITY, MAX_MOUSE_WHEEL_SENSITIVITY),
            downloadMaxRetryCount = settings.downloadMaxRetryCount.coerceIn(
                MIN_RETRY_COUNT,
                MAX_RETRY_COUNT,
            ),
            dataBackupRetentionCount = settings.dataBackupRetentionCount.coerceIn(
                MIN_BACKUP_RETENTION,
                MAX_BACKUP_RETENTION,
            ),
        )
        settingsStore.writeString(LIBRARY_DISPLAY_MODE_KEY, normalized.libraryDisplayMode.name)
        settingsStore.writeString(
            LIBRARY_CONTINUE_BUTTON_MODE_KEY,
            normalized.libraryContinueButtonMode.name,
        )
        settingsStore.writeBoolean(LIBRARY_LOCAL_BADGES_KEY, normalized.libraryShowLocalBadges)
        settingsStore.writeBoolean(LIBRARY_SOURCE_BADGES_KEY, normalized.libraryShowSourceBadges)
        settingsStore.writeBoolean(LIBRARY_TRACKING_BADGES_KEY, normalized.libraryShowTrackingBadges)
        settingsStore.writeBoolean(
            LIBRARY_REMEMBER_CATEGORY_SELECTION_KEY,
            normalized.libraryRememberCategorySelection,
        )
        settingsStore.writeString(BROWSE_DEFAULT_TAB_KEY, normalized.browseDefaultTab.name)
        settingsStore.writeString(BROWSE_SOURCE_FILTER_MODE_KEY, normalized.browseSourceFilterMode.name)
        settingsStore.writeString(
            BROWSE_EXTENSION_FILTER_MODE_KEY,
            normalized.browseExtensionFilterMode.name,
        )
        settingsStore.writeBoolean(
            BROWSE_GLOBAL_SEARCH_PINNED_ONLY_KEY,
            normalized.browseGlobalSearchPinnedOnly,
        )
        settingsStore.writeBoolean(
            BROWSE_HIDE_UNAVAILABLE_SOURCES_KEY,
            normalized.browseHideUnavailableSources,
        )
        settingsStore.writeBoolean(
            READER_KEYBOARD_SHORTCUTS_KEY,
            normalized.readerKeyboardShortcutsEnabled,
        )
        settingsStore.writeString(READER_KEYBOARD_SCHEME_KEY, normalized.readerKeyboardScheme.name)
        settingsStore.writeString(READER_MOUSE_WHEEL_ACTION_KEY, normalized.readerMouseWheelAction.name)
        settingsStore.writeInt(
            READER_MOUSE_WHEEL_SENSITIVITY_KEY,
            normalized.readerMouseWheelSensitivityPercent,
        )
        settingsStore.writeBoolean(
            READER_DESKTOP_TAP_NAVIGATION_KEY,
            normalized.readerDesktopTapNavigation,
        )
        settingsStore.writeBoolean(READER_DESKTOP_PAGE_BUTTONS_KEY, normalized.readerDesktopPageButtons)
        settingsStore.writeBoolean(
            READER_HIDE_CURSOR_WHILE_READING_KEY,
            normalized.readerHideCursorWhileReading,
        )
        settingsStore.writeString(READER_TEXT_THEME_KEY, normalized.readerTextTheme.name)
        settingsStore.writeBoolean(READER_SYSTEM_LIGHT_SEPIA_KEY, normalized.readerSystemLightSepia)
        settingsStore.writeString(READER_UI_THEME_KEY, normalized.readerUiTheme.name)
        settingsStore.writeInt(
            READER_CUSTOM_BACKGROUND_COLOR_KEY,
            normalized.readerCustomBackgroundColor,
        )
        settingsStore.writeInt(READER_CUSTOM_TEXT_COLOR_KEY, normalized.readerCustomTextColor)
        settingsStore.writeInt(READER_CUSTOM_INFO_COLOR_KEY, normalized.readerCustomInfoColor)
        settingsStore.writeString(
            READER_CUSTOM_THEMES_KEY,
            serializeReaderCustomThemes(normalized.readerCustomThemes),
        )
        settingsStore.writeString(READER_SELECTED_FONT_KEY, normalized.readerSelectedFont)
        settingsStore.writeBoolean(READER_LAYOUT_ADVANCED_KEY, normalized.readerLayoutAdvanced)
        settingsStore.writeBoolean(READER_STATISTICS_ENABLED_KEY, normalized.readerStatisticsEnabled)
        settingsStore.writeString(
            READER_STATISTICS_AUTOSTART_MODE_KEY,
            normalized.readerStatisticsAutostartMode.name,
        )
        settingsStore.writeInt(READER_LOOKUP_POPUP_WIDTH_KEY, normalized.readerLookupPopupWidth)
        settingsStore.writeInt(READER_LOOKUP_POPUP_HEIGHT_KEY, normalized.readerLookupPopupHeight)
        settingsStore.writeBoolean(
            READER_LOOKUP_POPUP_FULL_WIDTH_KEY,
            normalized.readerLookupPopupFullWidth,
        )
        settingsStore.writeBoolean(
            READER_LOOKUP_POPUP_SWIPE_TO_DISMISS_KEY,
            normalized.readerLookupPopupSwipeToDismiss,
        )
        settingsStore.writeInt(
            READER_LOOKUP_POPUP_SWIPE_THRESHOLD_KEY,
            normalized.readerLookupPopupSwipeThreshold,
        )
        settingsStore.writeInt(READER_LOOKUP_MAX_RESULTS_KEY, normalized.readerLookupMaxResults)
        settingsStore.writeInt(READER_LOOKUP_SCAN_LENGTH_KEY, normalized.readerLookupScanLength)
        settingsStore.writeString(DICTIONARY_THEME_MODE_KEY, normalized.dictionaryThemeMode.name)
        settingsStore.writeInt(DICTIONARY_CUSTOM_COLOR_KEY, normalized.dictionaryCustomColor)
        settingsStore.writeString(DICTIONARY_FONT_FAMILY_KEY, normalized.dictionaryFontFamily)
        settingsStore.writeString(DICTIONARY_CUSTOM_CSS_KEY, normalized.dictionaryCustomCss)
        settingsStore.writeInt(
            DICTIONARY_VIDEO_OCR_AUDIO_PADDING_SECONDS_KEY,
            normalized.dictionaryVideoOcrSentenceAudioPaddingSeconds,
        )
        settingsStore.writeBoolean(DICTIONARY_WORD_AUDIO_ENABLED_KEY, normalized.dictionaryWordAudioEnabled)
        settingsStore.writeBoolean(DICTIONARY_WORD_AUDIO_AUTOPLAY_KEY, normalized.dictionaryWordAudioAutoplay)
        settingsStore.writeString(DICTIONARY_WORD_AUDIO_SOURCES_KEY, normalized.dictionaryWordAudioSources)
        settingsStore.writeString(DICTIONARY_WORD_AUDIO_LOCAL_PATH_KEY, normalized.dictionaryWordAudioLocalPath)
        settingsStore.writeString(DICTIONARY_WORD_AUDIO_LOCAL_URI_KEY, normalized.dictionaryWordAudioLocalUri)
        settingsStore.writeBoolean(
            DICTIONARY_WORD_AUDIO_LOCAL_ENABLED_KEY,
            normalized.dictionaryWordAudioLocalEnabled,
        )
        settingsStore.writeString(DOWNLOAD_STORAGE_LOCATION_KEY, normalized.downloadStorageLocation.name)
        settingsStore.writeString(DOWNLOAD_CLEANUP_POLICY_KEY, normalized.downloadCleanupPolicy.name)
        settingsStore.writeString(DOWNLOAD_ORDER_KEY, normalized.downloadOrder.name)
        settingsStore.writeBoolean(DOWNLOAD_PAUSE_WHEN_METERED_KEY, normalized.downloadPauseWhenMetered)
        settingsStore.writeBoolean(DOWNLOAD_REQUIRE_CHARGING_KEY, normalized.downloadRequireCharging)
        settingsStore.writeBoolean(DOWNLOAD_PAUSE_BATTERY_LOW_KEY, normalized.downloadPauseWhenBatteryLow)
        settingsStore.writeInt(DOWNLOAD_MAX_RETRY_COUNT_KEY, normalized.downloadMaxRetryCount)
        settingsStore.writeBoolean(
            DOWNLOAD_PRESERVE_QUEUE_ON_EXIT_KEY,
            normalized.downloadPreserveQueueOnExit,
        )
        settingsStore.writeBoolean(
            SECURITY_HIDE_HISTORY_IN_INCOGNITO_KEY,
            normalized.securityHideHistoryInIncognito,
        )
        settingsStore.writeBoolean(
            SECURITY_HIDE_DOWNLOADS_IN_INCOGNITO_KEY,
            normalized.securityHideDownloadsInIncognito,
        )
        settingsStore.writeBoolean(SECURITY_CLEAR_COOKIES_ON_EXIT_KEY, normalized.securityClearCookiesOnExit)
        settingsStore.writeBoolean(
            SECURITY_CLEAR_SEARCH_HISTORY_ON_EXIT_KEY,
            normalized.securityClearSearchHistoryOnExit,
        )
        settingsStore.writeBoolean(DATA_AUTO_BACKUP_ENABLED_KEY, normalized.dataAutoBackupEnabled)
        settingsStore.writeBoolean(DATA_BACKUP_WIFI_ONLY_KEY, normalized.dataBackupWifiOnly)
        settingsStore.writeBoolean(DATA_BACKUP_CHARGING_ONLY_KEY, normalized.dataBackupWhileChargingOnly)
        settingsStore.writeBoolean(
            DATA_BACKUP_INCLUDE_DOWNLOADS_KEY,
            normalized.dataIncludeDownloadsInBackups,
        )
        settingsStore.writeInt(DATA_BACKUP_RETENTION_COUNT_KEY, normalized.dataBackupRetentionCount)
        return normalized
    }

    private suspend inline fun <reified T : Enum<T>> readEnum(
        key: String,
        defaultValue: T,
    ): T {
        val stored = settingsStore.readString(key) ?: return defaultValue
        return enumValues<T>().firstOrNull { it.name == stored } ?: defaultValue
    }

    private fun parseReaderCustomThemes(serialized: String?): List<ChimahonReaderCustomTheme> {
        if (serialized.isNullOrBlank()) return emptyList()

        return serialized.split("|")
            .mapNotNull { encoded ->
                val parts = encoded.split("~", limit = 2)
                if (parts.size != 2) return@mapNotNull null

                val colors = parts[1].split(",")
                val backgroundColor = colors.getOrNull(0)?.toIntOrNull() ?: return@mapNotNull null
                val textColor = colors.getOrNull(1)?.toIntOrNull() ?: return@mapNotNull null
                ChimahonReaderCustomTheme(
                    name = parts[0],
                    backgroundColor = backgroundColor,
                    textColor = textColor,
                )
            }
            .distinct()
    }

    private fun serializeReaderCustomThemes(themes: List<ChimahonReaderCustomTheme>): String {
        return themes
            .distinct()
            .takeLast(MAX_READER_CUSTOM_THEMES)
            .joinToString("|") { theme ->
                "${theme.name}~${theme.backgroundColor},${theme.textColor}"
            }
    }

    private companion object {
        const val KEEP_READER_CONTROLS_VISIBLE_KEY = "__APP_STATE_keep_reader_controls_visible"
        const val RIGHT_TO_LEFT_BY_DEFAULT_KEY = "__APP_STATE_right_to_left_by_default"
        const val SHOW_UNREAD_BADGES_KEY = "__APP_STATE_show_unread_badges"
        const val SHOW_CATEGORY_TABS_KEY = "__APP_STATE_show_category_tabs"
        const val SHOW_HIDDEN_CATEGORIES_KEY = "__APP_STATE_show_hidden_categories"
        const val DOWNLOAD_ONLY_OVER_WIFI_KEY = "__APP_STATE_download_only_over_wifi"
        const val AUTO_DOWNLOAD_NEW_CHAPTERS_KEY = "__APP_STATE_auto_download_new_chapters"
        const val AUTO_DOWNLOAD_UNREAD_ONLY_KEY = "__APP_STATE_auto_download_unread_only"
        const val PARALLEL_DOWNLOADS_KEY = "__APP_STATE_parallel_downloads"
        const val LIBRARY_DISPLAY_MODE_KEY = "__APP_STATE_chimahon_library_display_mode"
        const val LIBRARY_CONTINUE_BUTTON_MODE_KEY =
            "__APP_STATE_chimahon_library_continue_button_mode"
        const val LIBRARY_LOCAL_BADGES_KEY = "__APP_STATE_chimahon_library_local_badges"
        const val LIBRARY_SOURCE_BADGES_KEY = "__APP_STATE_chimahon_library_source_badges"
        const val LIBRARY_TRACKING_BADGES_KEY = "__APP_STATE_chimahon_library_tracking_badges"
        const val LIBRARY_REMEMBER_CATEGORY_SELECTION_KEY =
            "__APP_STATE_chimahon_library_remember_category_selection"
        const val BROWSE_DEFAULT_TAB_KEY = "__APP_STATE_chimahon_browse_default_tab"
        const val BROWSE_SOURCE_FILTER_MODE_KEY =
            "__APP_STATE_chimahon_browse_source_filter_mode"
        const val BROWSE_EXTENSION_FILTER_MODE_KEY =
            "__APP_STATE_chimahon_browse_extension_filter_mode"
        const val BROWSE_GLOBAL_SEARCH_PINNED_ONLY_KEY =
            "__APP_STATE_chimahon_browse_global_search_pinned_only"
        const val BROWSE_HIDE_UNAVAILABLE_SOURCES_KEY =
            "__APP_STATE_chimahon_browse_hide_unavailable_sources"
        const val READER_KEYBOARD_SHORTCUTS_KEY =
            "__APP_STATE_chimahon_reader_keyboard_shortcuts"
        const val READER_KEYBOARD_SCHEME_KEY = "__APP_STATE_chimahon_reader_keyboard_scheme"
        const val READER_MOUSE_WHEEL_ACTION_KEY =
            "__APP_STATE_chimahon_reader_mouse_wheel_action"
        const val READER_MOUSE_WHEEL_SENSITIVITY_KEY =
            "__APP_STATE_chimahon_reader_mouse_wheel_sensitivity"
        const val READER_DESKTOP_TAP_NAVIGATION_KEY =
            "__APP_STATE_chimahon_reader_desktop_tap_navigation"
        const val READER_DESKTOP_PAGE_BUTTONS_KEY =
            "__APP_STATE_chimahon_reader_desktop_page_buttons"
        const val READER_HIDE_CURSOR_WHILE_READING_KEY =
            "__APP_STATE_chimahon_reader_hide_cursor_while_reading"
        const val READER_TEXT_THEME_KEY = "__APP_STATE_chimahon_reader_text_theme"
        const val READER_SYSTEM_LIGHT_SEPIA_KEY =
            "__APP_STATE_chimahon_reader_system_light_sepia"
        const val READER_UI_THEME_KEY = "__APP_STATE_chimahon_reader_ui_theme"
        const val READER_CUSTOM_BACKGROUND_COLOR_KEY =
            "__APP_STATE_chimahon_reader_custom_background_color"
        const val READER_CUSTOM_TEXT_COLOR_KEY =
            "__APP_STATE_chimahon_reader_custom_text_color"
        const val READER_CUSTOM_INFO_COLOR_KEY =
            "__APP_STATE_chimahon_reader_custom_info_color"
        const val READER_CUSTOM_THEMES_KEY = "__APP_STATE_chimahon_reader_custom_themes"
        const val READER_SELECTED_FONT_KEY = "__APP_STATE_chimahon_reader_selected_font"
        const val READER_LAYOUT_ADVANCED_KEY = "__APP_STATE_chimahon_reader_layout_advanced"
        const val READER_STATISTICS_ENABLED_KEY =
            "__APP_STATE_chimahon_reader_statistics_enabled"
        const val READER_STATISTICS_AUTOSTART_MODE_KEY =
            "__APP_STATE_chimahon_reader_statistics_autostart_mode"
        const val READER_LOOKUP_POPUP_WIDTH_KEY =
            "__APP_STATE_chimahon_reader_lookup_popup_width"
        const val READER_LOOKUP_POPUP_HEIGHT_KEY =
            "__APP_STATE_chimahon_reader_lookup_popup_height"
        const val READER_LOOKUP_POPUP_FULL_WIDTH_KEY =
            "__APP_STATE_chimahon_reader_lookup_popup_full_width"
        const val READER_LOOKUP_POPUP_SWIPE_TO_DISMISS_KEY =
            "__APP_STATE_chimahon_reader_lookup_popup_swipe_to_dismiss"
        const val READER_LOOKUP_POPUP_SWIPE_THRESHOLD_KEY =
            "__APP_STATE_chimahon_reader_lookup_popup_swipe_threshold"
        const val READER_LOOKUP_MAX_RESULTS_KEY =
            "__APP_STATE_chimahon_reader_lookup_max_results"
        const val READER_LOOKUP_SCAN_LENGTH_KEY =
            "__APP_STATE_chimahon_reader_lookup_scan_length"
        const val DICTIONARY_THEME_MODE_KEY = "__APP_STATE_chimahon_dictionary_theme_mode"
        const val DICTIONARY_CUSTOM_COLOR_KEY = "__APP_STATE_chimahon_dictionary_custom_color"
        const val DICTIONARY_FONT_FAMILY_KEY = "__APP_STATE_chimahon_dictionary_font_family"
        const val DICTIONARY_CUSTOM_CSS_KEY = "__APP_STATE_chimahon_dictionary_custom_css"
        const val DICTIONARY_VIDEO_OCR_AUDIO_PADDING_SECONDS_KEY =
            "__APP_STATE_chimahon_dictionary_video_ocr_audio_padding_seconds"
        const val DICTIONARY_WORD_AUDIO_ENABLED_KEY =
            "__APP_STATE_chimahon_dictionary_word_audio_enabled"
        const val DICTIONARY_WORD_AUDIO_AUTOPLAY_KEY =
            "__APP_STATE_chimahon_dictionary_word_audio_autoplay"
        const val DICTIONARY_WORD_AUDIO_SOURCES_KEY =
            "__APP_STATE_chimahon_dictionary_word_audio_sources"
        const val DICTIONARY_WORD_AUDIO_LOCAL_PATH_KEY =
            "__APP_STATE_chimahon_dictionary_word_audio_local_path"
        const val DICTIONARY_WORD_AUDIO_LOCAL_URI_KEY =
            "__APP_STATE_chimahon_dictionary_word_audio_local_uri"
        const val DICTIONARY_WORD_AUDIO_LOCAL_ENABLED_KEY =
            "__APP_STATE_chimahon_dictionary_word_audio_local_enabled"
        const val MAX_READER_CUSTOM_THEMES = 24
        const val DOWNLOAD_STORAGE_LOCATION_KEY =
            "__APP_STATE_chimahon_download_storage_location"
        const val DOWNLOAD_CLEANUP_POLICY_KEY = "__APP_STATE_chimahon_download_cleanup_policy"
        const val DOWNLOAD_ORDER_KEY = "__APP_STATE_chimahon_download_order"
        const val DOWNLOAD_PAUSE_WHEN_METERED_KEY =
            "__APP_STATE_chimahon_download_pause_when_metered"
        const val DOWNLOAD_REQUIRE_CHARGING_KEY =
            "__APP_STATE_chimahon_download_require_charging"
        const val DOWNLOAD_PAUSE_BATTERY_LOW_KEY =
            "__APP_STATE_chimahon_download_pause_battery_low"
        const val DOWNLOAD_MAX_RETRY_COUNT_KEY = "__APP_STATE_chimahon_download_max_retry_count"
        const val DOWNLOAD_PRESERVE_QUEUE_ON_EXIT_KEY =
            "__APP_STATE_chimahon_download_preserve_queue_on_exit"
        const val SECURITY_HIDE_HISTORY_IN_INCOGNITO_KEY =
            "__APP_STATE_chimahon_security_hide_history_in_incognito"
        const val SECURITY_HIDE_DOWNLOADS_IN_INCOGNITO_KEY =
            "__APP_STATE_chimahon_security_hide_downloads_in_incognito"
        const val SECURITY_CLEAR_COOKIES_ON_EXIT_KEY =
            "__APP_STATE_chimahon_security_clear_cookies_on_exit"
        const val SECURITY_CLEAR_SEARCH_HISTORY_ON_EXIT_KEY =
            "__APP_STATE_chimahon_security_clear_search_history_on_exit"
        const val DATA_AUTO_BACKUP_ENABLED_KEY =
            "__APP_STATE_chimahon_data_auto_backup_enabled"
        const val DATA_BACKUP_WIFI_ONLY_KEY = "__APP_STATE_chimahon_data_backup_wifi_only"
        const val DATA_BACKUP_CHARGING_ONLY_KEY =
            "__APP_STATE_chimahon_data_backup_charging_only"
        const val DATA_BACKUP_INCLUDE_DOWNLOADS_KEY =
            "__APP_STATE_chimahon_data_backup_include_downloads"
        const val DATA_BACKUP_RETENTION_COUNT_KEY =
            "__APP_STATE_chimahon_data_backup_retention_count"
        const val MIN_PARALLEL_DOWNLOADS = 1
        const val MAX_PARALLEL_DOWNLOADS = 8
        const val MIN_MOUSE_WHEEL_SENSITIVITY = 25
        const val MAX_MOUSE_WHEEL_SENSITIVITY = 300
        const val MIN_RETRY_COUNT = 0
        const val MAX_RETRY_COUNT = 10
        const val MIN_BACKUP_RETENTION = 1
        const val MAX_BACKUP_RETENTION = 20
    }
}
