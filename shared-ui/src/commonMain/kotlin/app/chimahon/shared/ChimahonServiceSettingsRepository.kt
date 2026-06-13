package app.chimahon.shared

import tachiyomi.core.platform.settings.PlatformSettingsStore
import tachiyomi.core.platform.settings.readBoolean
import tachiyomi.core.platform.settings.readInt
import tachiyomi.core.platform.settings.writeBoolean
import tachiyomi.core.platform.settings.writeInt

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
        const val MIN_PARALLEL_DOWNLOADS = 1
        const val MAX_PARALLEL_DOWNLOADS = 8
    }
}
