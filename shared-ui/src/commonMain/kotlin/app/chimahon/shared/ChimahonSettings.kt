package app.chimahon.shared

import tachiyomi.core.platform.settings.PlatformSettingsStore
import tachiyomi.core.platform.settings.readBoolean
import tachiyomi.core.platform.settings.readInt
import tachiyomi.core.platform.settings.writeBoolean
import tachiyomi.core.platform.settings.writeInt

data class ChimahonSettings(
    val appearance: ChimahonAppearanceSettings = ChimahonAppearanceSettings(),
    val reader: ChimahonReaderSettings = ChimahonReaderSettings(),
    val library: ChimahonLibrarySettings = ChimahonLibrarySettings(),
    val downloads: ChimahonDownloadPreferences = ChimahonDownloadPreferences(),
    val browse: ChimahonBrowseSettings = ChimahonBrowseSettings(),
    val security: ChimahonSecuritySettings = ChimahonSecuritySettings(),
    val appMode: ChimahonAppModeSettings = ChimahonAppModeSettings(),
)

data class ChimahonAppearanceSettings(
    val themeMode: ChimahonThemeMode = ChimahonThemeMode.System,
    val appTheme: ChimahonAppTheme = ChimahonAppTheme.Default,
    val colorTheme: ChimahonColorTheme = ChimahonColorTheme.Default,
    val amoled: Boolean = false,
    val fontScalePercent: Int = 100,
    val compactNavigation: Boolean = false,
    val relativeDates: Boolean = true,
    val showDescriptionImages: Boolean = true,
)

enum class ChimahonThemeMode {
    System,
    Light,
    Dark,
}

enum class ChimahonAppTheme(val title: String) {
    Default("Default"),
    Catppuccin("Catppuccin"),
    Cloudflare("Cloudflare"),
    CottonCandy("Cotton Candy"),
    Doom("Doom"),
    GreenApple("Green Apple"),
    Lavender("Lavender"),
    Matrix("Matrix"),
    MidnightDusk("Midnight Dusk"),
    Mocha("Mocha"),
    Monochrome("Monochrome"),
    Nord("Nord"),
    Sapphire("Sapphire"),
    StrawberryDaiquiri("Strawberry Daiquiri"),
    Tako("Tako"),
    TealTurquoise("Teal Turquoise"),
    TidalWave("Tidal Wave"),
    YinYang("Yin & Yang"),
    Yotsuba("Yotsuba"),
}

enum class ChimahonColorTheme(val title: String) {
    Default("Default"),
    Dynamic("Dynamic"),
    Blue("Blue"),
    Green("Green"),
    Orange("Orange"),
    Pink("Pink"),
    Purple("Purple"),
    Red("Red"),
    Teal("Teal"),
}

data class ChimahonReaderSettings(
    val mode: ChimahonReaderMode = ChimahonReaderMode.Webtoon,
    val scale: ChimahonReaderScale = ChimahonReaderScale.FitWidth,
    val canvas: ChimahonReaderCanvas = ChimahonReaderCanvas.Black,
    val orientation: ChimahonReaderOrientation = ChimahonReaderOrientation.Free,
    val dualPageMode: ChimahonDualPageMode = ChimahonDualPageMode.Off,
    val splitWidePages: Boolean = false,
    val colorFilterEnabled: Boolean = false,
    val grayscale: Boolean = false,
    val invertColors: Boolean = false,
    val brightness: Int = 0,
    val navigationMode: ChimahonReaderNavigationMode = ChimahonReaderNavigationMode.Automatic,
    val showPageStrip: Boolean = true,
    val keepControlsVisible: Boolean = true,
    val tapZonesEnabled: Boolean = true,
    val smallerTapZones: Boolean = false,
    val invertTapZones: ChimahonTapZoneInvert = ChimahonTapZoneInvert.None,
    val swipeNavigationEnabled: Boolean = true,
    val doubleTapToZoom: Boolean = true,
    val volumeKeysEnabled: Boolean = false,
    val volumeKeysInverted: Boolean = false,
    val longTapEnabled: Boolean = true,
    val keepScreenOn: Boolean = false,
    val cropBorders: Boolean = false,
    val pageTransitions: Boolean = true,
    val showPageNumber: Boolean = true,
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
    FitHeight,
}

enum class ChimahonReaderCanvas {
    Black,
    Gray,
    White,
}

enum class ChimahonReaderOrientation {
    Free,
    Portrait,
    Landscape,
    ReversePortrait,
    ReverseLandscape,
}

enum class ChimahonDualPageMode {
    Off,
    Automatic,
    Always,
}

enum class ChimahonReaderNavigationMode {
    Automatic,
    LeftToRight,
    RightToLeft,
    Vertical,
}

enum class ChimahonTapZoneInvert {
    None,
    Horizontal,
    Vertical,
    Both,
}

data class ChimahonLibrarySettings(
    val displayMode: ChimahonLibraryDisplayMode = ChimahonLibraryDisplayMode.ComfortableGrid,
    val gridColumnsPortrait: Int = 0,
    val gridColumnsLandscape: Int = 0,
    val coverAspectRatio: ChimahonLibraryCoverRatio = ChimahonLibraryCoverRatio.Automatic,
    val showCategoryTabs: Boolean = true,
    val showUnreadBadges: Boolean = true,
    val showDownloadedBadges: Boolean = true,
    val showLanguageBadges: Boolean = false,
    val showContinueButtons: Boolean = true,
    val sort: ChimahonLibrarySort = ChimahonLibrarySort.Alphabetical,
    val sortAscending: Boolean = true,
    val downloadedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val unreadFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val startedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val bookmarkedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val completedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val trackedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val autoUpdateIntervalHours: Int = 0,
    val updateOnlyOnWifi: Boolean = true,
    val showUpdateCount: Boolean = true,
    val updateNotificationsEnabled: Boolean = true,
)

enum class ChimahonLibraryDisplayMode {
    ComfortableGrid,
    CompactGrid,
    List,
}

enum class ChimahonLibraryCoverRatio {
    Automatic,
    Square,
    ThreeToFour,
    TwoToThree,
    Original,
}

enum class ChimahonLibrarySort {
    Alphabetical,
    LastRead,
    LastUpdate,
    UnreadCount,
    TotalChapters,
    LatestChapter,
    ChapterFetchDate,
    DateAdded,
    Random,
}

enum class ChimahonFilterMode {
    Any,
    Include,
    Exclude,
}

data class ChimahonDownloadPreferences(
    val wifiOnly: Boolean = true,
    val saveAsCbz: Boolean = true,
    val splitTallImages: Boolean = true,
    val autoDownloadWhileReadingCount: Int = 0,
    val removeAfterReadSlots: Int = -1,
    val removeAfterMarkedRead: Boolean = false,
    val removeBookmarkedChapters: Boolean = false,
    val downloadNewChapters: Boolean = false,
    val downloadNewUnreadOnly: Boolean = false,
    val parallelSourceDownloads: Int = 5,
    val parallelPageDownloads: Int = 5,
)

data class ChimahonBrowseSettings(
    val showNsfwSources: Boolean = true,
    val hideLibraryEntries: Boolean = false,
    val autoLoadMore: Boolean = true,
    val enabledLanguages: List<String> = emptyList(),
    val sourceDisplayMode: ChimahonBrowseSourceDisplayMode = ChimahonBrowseSourceDisplayMode.List,
    val groupSourcesByLanguage: Boolean = true,
    val showSourceLanguage: Boolean = true,
    val extensionUpdateNotificationsEnabled: Boolean = true,
)

enum class ChimahonBrowseSourceDisplayMode {
    List,
    CompactList,
    Grid,
}

data class ChimahonSecuritySettings(
    val secureScreenMode: ChimahonSecureScreenMode = ChimahonSecureScreenMode.Incognito,
    val hideNotificationContent: Boolean = false,
    val requireAuthentication: Boolean = false,
    val lockAfterMinutes: Int = 0,
    val lockOnAppExit: Boolean = false,
    val incognitoModeByDefault: Boolean = false,
    val protectDownloads: Boolean = false,
)

enum class ChimahonSecureScreenMode {
    Always,
    Incognito,
    Never,
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
            appearance = loadAppearanceSettings(),
            reader = loadReaderSettings(),
            library = loadLibrarySettings(),
            downloads = loadDownloadSettings(),
            browse = loadBrowseSettings(),
            security = loadSecuritySettings(),
            appMode = loadAppModeSettings(),
        )
    }

    suspend fun loadAppearanceSettings(): ChimahonAppearanceSettings {
        return ChimahonAppearanceSettings(
            themeMode = readEnum(APPEARANCE_THEME_MODE_KEY, ChimahonThemeMode.System),
            appTheme = readEnum(APPEARANCE_APP_THEME_KEY, ChimahonAppTheme.Default),
            colorTheme = readEnum(APPEARANCE_COLOR_THEME_KEY, ChimahonColorTheme.Default),
            amoled = settingsStore.readBoolean(APPEARANCE_AMOLED_KEY),
            fontScalePercent = settingsStore.readInt(
                APPEARANCE_FONT_SCALE_PERCENT_KEY,
                defaultValue = 100,
            ),
            compactNavigation = settingsStore.readBoolean(APPEARANCE_COMPACT_NAVIGATION_KEY),
            relativeDates = settingsStore.readBoolean(APPEARANCE_RELATIVE_DATES_KEY, defaultValue = true),
            showDescriptionImages = settingsStore.readBoolean(
                APPEARANCE_DESCRIPTION_IMAGES_KEY,
                defaultValue = true,
            ),
        )
    }

    suspend fun saveAppearanceSettings(
        settings: ChimahonAppearanceSettings,
    ): ChimahonAppearanceSettings {
        settingsStore.writeString(APPEARANCE_THEME_MODE_KEY, settings.themeMode.name)
        settingsStore.writeString(APPEARANCE_APP_THEME_KEY, settings.appTheme.name)
        settingsStore.writeString(APPEARANCE_COLOR_THEME_KEY, settings.colorTheme.name)
        settingsStore.writeBoolean(APPEARANCE_AMOLED_KEY, settings.amoled)
        settingsStore.writeInt(APPEARANCE_FONT_SCALE_PERCENT_KEY, settings.fontScalePercent)
        settingsStore.writeBoolean(APPEARANCE_COMPACT_NAVIGATION_KEY, settings.compactNavigation)
        settingsStore.writeBoolean(APPEARANCE_RELATIVE_DATES_KEY, settings.relativeDates)
        settingsStore.writeBoolean(APPEARANCE_DESCRIPTION_IMAGES_KEY, settings.showDescriptionImages)
        return settings
    }

    suspend fun loadReaderSettings(): ChimahonReaderSettings {
        return ChimahonReaderSettings(
            mode = readEnum(READER_MODE_KEY, ChimahonReaderMode.Webtoon),
            scale = readEnum(READER_SCALE_KEY, ChimahonReaderScale.FitWidth),
            canvas = readEnum(READER_CANVAS_KEY, ChimahonReaderCanvas.Black),
            orientation = readEnum(READER_ORIENTATION_KEY, ChimahonReaderOrientation.Free),
            dualPageMode = readEnum(READER_DUAL_PAGE_MODE_KEY, ChimahonDualPageMode.Off),
            splitWidePages = settingsStore.readBoolean(READER_SPLIT_WIDE_PAGES_KEY),
            colorFilterEnabled = settingsStore.readBoolean(READER_COLOR_FILTER_ENABLED_KEY),
            grayscale = settingsStore.readBoolean(READER_GRAYSCALE_KEY),
            invertColors = settingsStore.readBoolean(READER_INVERT_COLORS_KEY),
            brightness = settingsStore.readInt(READER_BRIGHTNESS_KEY),
            navigationMode = readEnum(
                READER_NAVIGATION_MODE_KEY,
                ChimahonReaderNavigationMode.Automatic,
            ),
            showPageStrip = settingsStore.readBoolean(READER_PAGE_STRIP_KEY, defaultValue = true),
            keepControlsVisible = settingsStore.readBoolean(READER_CONTROLS_KEY, defaultValue = true),
            tapZonesEnabled = settingsStore.readBoolean(READER_TAP_ZONES_KEY, defaultValue = true),
            smallerTapZones = settingsStore.readBoolean(READER_SMALLER_TAP_ZONES_KEY),
            invertTapZones = readEnum(READER_INVERT_TAP_ZONES_KEY, ChimahonTapZoneInvert.None),
            swipeNavigationEnabled = settingsStore.readBoolean(
                READER_SWIPE_NAVIGATION_KEY,
                defaultValue = true,
            ),
            doubleTapToZoom = settingsStore.readBoolean(
                READER_DOUBLE_TAP_ZOOM_KEY,
                defaultValue = true,
            ),
            volumeKeysEnabled = settingsStore.readBoolean(READER_VOLUME_KEYS_KEY),
            volumeKeysInverted = settingsStore.readBoolean(READER_VOLUME_KEYS_INVERTED_KEY),
            longTapEnabled = settingsStore.readBoolean(READER_LONG_TAP_KEY, defaultValue = true),
            keepScreenOn = settingsStore.readBoolean(READER_KEEP_SCREEN_ON_KEY),
            cropBorders = settingsStore.readBoolean(READER_CROP_BORDERS_KEY),
            pageTransitions = settingsStore.readBoolean(READER_PAGE_TRANSITIONS_KEY, defaultValue = true),
            showPageNumber = settingsStore.readBoolean(READER_SHOW_PAGE_NUMBER_KEY, defaultValue = true),
        )
    }

    suspend fun saveReaderSettings(settings: ChimahonReaderSettings): ChimahonReaderSettings {
        settingsStore.writeString(READER_MODE_KEY, settings.mode.name)
        settingsStore.writeString(READER_SCALE_KEY, settings.scale.name)
        settingsStore.writeString(READER_CANVAS_KEY, settings.canvas.name)
        settingsStore.writeString(READER_ORIENTATION_KEY, settings.orientation.name)
        settingsStore.writeString(READER_DUAL_PAGE_MODE_KEY, settings.dualPageMode.name)
        settingsStore.writeBoolean(READER_SPLIT_WIDE_PAGES_KEY, settings.splitWidePages)
        settingsStore.writeBoolean(READER_COLOR_FILTER_ENABLED_KEY, settings.colorFilterEnabled)
        settingsStore.writeBoolean(READER_GRAYSCALE_KEY, settings.grayscale)
        settingsStore.writeBoolean(READER_INVERT_COLORS_KEY, settings.invertColors)
        settingsStore.writeInt(READER_BRIGHTNESS_KEY, settings.brightness)
        settingsStore.writeString(READER_NAVIGATION_MODE_KEY, settings.navigationMode.name)
        settingsStore.writeBoolean(READER_PAGE_STRIP_KEY, settings.showPageStrip)
        settingsStore.writeBoolean(READER_CONTROLS_KEY, settings.keepControlsVisible)
        settingsStore.writeBoolean(READER_TAP_ZONES_KEY, settings.tapZonesEnabled)
        settingsStore.writeBoolean(READER_SMALLER_TAP_ZONES_KEY, settings.smallerTapZones)
        settingsStore.writeString(READER_INVERT_TAP_ZONES_KEY, settings.invertTapZones.name)
        settingsStore.writeBoolean(READER_SWIPE_NAVIGATION_KEY, settings.swipeNavigationEnabled)
        settingsStore.writeBoolean(READER_DOUBLE_TAP_ZOOM_KEY, settings.doubleTapToZoom)
        settingsStore.writeBoolean(READER_VOLUME_KEYS_KEY, settings.volumeKeysEnabled)
        settingsStore.writeBoolean(READER_VOLUME_KEYS_INVERTED_KEY, settings.volumeKeysInverted)
        settingsStore.writeBoolean(READER_LONG_TAP_KEY, settings.longTapEnabled)
        settingsStore.writeBoolean(READER_KEEP_SCREEN_ON_KEY, settings.keepScreenOn)
        settingsStore.writeBoolean(READER_CROP_BORDERS_KEY, settings.cropBorders)
        settingsStore.writeBoolean(READER_PAGE_TRANSITIONS_KEY, settings.pageTransitions)
        settingsStore.writeBoolean(READER_SHOW_PAGE_NUMBER_KEY, settings.showPageNumber)
        return settings
    }

    suspend fun loadLibrarySettings(): ChimahonLibrarySettings {
        return ChimahonLibrarySettings(
            displayMode = readEnum(
                LIBRARY_DISPLAY_MODE_KEY,
                ChimahonLibraryDisplayMode.ComfortableGrid,
            ),
            gridColumnsPortrait = settingsStore.readInt(LIBRARY_GRID_COLUMNS_PORTRAIT_KEY),
            gridColumnsLandscape = settingsStore.readInt(LIBRARY_GRID_COLUMNS_LANDSCAPE_KEY),
            coverAspectRatio = readEnum(
                LIBRARY_COVER_ASPECT_RATIO_KEY,
                ChimahonLibraryCoverRatio.Automatic,
            ),
            showCategoryTabs = settingsStore.readBoolean(
                LIBRARY_CATEGORY_TABS_KEY,
                defaultValue = true,
            ),
            showUnreadBadges = settingsStore.readBoolean(
                LIBRARY_UNREAD_BADGES_KEY,
                defaultValue = true,
            ),
            showDownloadedBadges = settingsStore.readBoolean(
                LIBRARY_DOWNLOADED_BADGES_KEY,
                defaultValue = true,
            ),
            showLanguageBadges = settingsStore.readBoolean(LIBRARY_LANGUAGE_BADGES_KEY),
            showContinueButtons = settingsStore.readBoolean(
                LIBRARY_CONTINUE_BUTTONS_KEY,
                defaultValue = true,
            ),
            sort = readEnum(LIBRARY_SORT_KEY, ChimahonLibrarySort.Alphabetical),
            sortAscending = settingsStore.readBoolean(LIBRARY_SORT_ASCENDING_KEY, defaultValue = true),
            downloadedFilter = readEnum(LIBRARY_FILTER_DOWNLOADED_KEY, ChimahonFilterMode.Any),
            unreadFilter = readEnum(LIBRARY_FILTER_UNREAD_KEY, ChimahonFilterMode.Any),
            startedFilter = readEnum(LIBRARY_FILTER_STARTED_KEY, ChimahonFilterMode.Any),
            bookmarkedFilter = readEnum(LIBRARY_FILTER_BOOKMARKED_KEY, ChimahonFilterMode.Any),
            completedFilter = readEnum(LIBRARY_FILTER_COMPLETED_KEY, ChimahonFilterMode.Any),
            trackedFilter = readEnum(LIBRARY_FILTER_TRACKED_KEY, ChimahonFilterMode.Any),
            autoUpdateIntervalHours = settingsStore.readInt(LIBRARY_UPDATE_INTERVAL_KEY),
            updateOnlyOnWifi = settingsStore.readBoolean(LIBRARY_UPDATE_WIFI_ONLY_KEY, defaultValue = true),
            showUpdateCount = settingsStore.readBoolean(LIBRARY_SHOW_UPDATE_COUNT_KEY, defaultValue = true),
            updateNotificationsEnabled = settingsStore.readBoolean(
                LIBRARY_UPDATE_NOTIFICATIONS_KEY,
                defaultValue = true,
            ),
        )
    }

    suspend fun saveLibrarySettings(settings: ChimahonLibrarySettings): ChimahonLibrarySettings {
        settingsStore.writeString(LIBRARY_DISPLAY_MODE_KEY, settings.displayMode.name)
        settingsStore.writeInt(LIBRARY_GRID_COLUMNS_PORTRAIT_KEY, settings.gridColumnsPortrait)
        settingsStore.writeInt(LIBRARY_GRID_COLUMNS_LANDSCAPE_KEY, settings.gridColumnsLandscape)
        settingsStore.writeString(LIBRARY_COVER_ASPECT_RATIO_KEY, settings.coverAspectRatio.name)
        settingsStore.writeBoolean(LIBRARY_CATEGORY_TABS_KEY, settings.showCategoryTabs)
        settingsStore.writeBoolean(LIBRARY_UNREAD_BADGES_KEY, settings.showUnreadBadges)
        settingsStore.writeBoolean(LIBRARY_DOWNLOADED_BADGES_KEY, settings.showDownloadedBadges)
        settingsStore.writeBoolean(LIBRARY_LANGUAGE_BADGES_KEY, settings.showLanguageBadges)
        settingsStore.writeBoolean(LIBRARY_CONTINUE_BUTTONS_KEY, settings.showContinueButtons)
        settingsStore.writeString(LIBRARY_SORT_KEY, settings.sort.name)
        settingsStore.writeBoolean(LIBRARY_SORT_ASCENDING_KEY, settings.sortAscending)
        settingsStore.writeString(LIBRARY_FILTER_DOWNLOADED_KEY, settings.downloadedFilter.name)
        settingsStore.writeString(LIBRARY_FILTER_UNREAD_KEY, settings.unreadFilter.name)
        settingsStore.writeString(LIBRARY_FILTER_STARTED_KEY, settings.startedFilter.name)
        settingsStore.writeString(LIBRARY_FILTER_BOOKMARKED_KEY, settings.bookmarkedFilter.name)
        settingsStore.writeString(LIBRARY_FILTER_COMPLETED_KEY, settings.completedFilter.name)
        settingsStore.writeString(LIBRARY_FILTER_TRACKED_KEY, settings.trackedFilter.name)
        settingsStore.writeInt(LIBRARY_UPDATE_INTERVAL_KEY, settings.autoUpdateIntervalHours)
        settingsStore.writeBoolean(LIBRARY_UPDATE_WIFI_ONLY_KEY, settings.updateOnlyOnWifi)
        settingsStore.writeBoolean(LIBRARY_SHOW_UPDATE_COUNT_KEY, settings.showUpdateCount)
        settingsStore.writeBoolean(LIBRARY_UPDATE_NOTIFICATIONS_KEY, settings.updateNotificationsEnabled)
        return settings
    }

    suspend fun loadDownloadSettings(): ChimahonDownloadPreferences {
        return ChimahonDownloadPreferences(
            wifiOnly = settingsStore.readBoolean(DOWNLOAD_WIFI_ONLY_KEY, defaultValue = true),
            saveAsCbz = settingsStore.readBoolean(DOWNLOAD_SAVE_AS_CBZ_KEY, defaultValue = true),
            splitTallImages = settingsStore.readBoolean(DOWNLOAD_SPLIT_TALL_IMAGES_KEY, defaultValue = true),
            autoDownloadWhileReadingCount = settingsStore.readInt(DOWNLOAD_WHILE_READING_KEY),
            removeAfterReadSlots = settingsStore.readInt(DOWNLOAD_REMOVE_AFTER_READ_KEY, defaultValue = -1),
            removeAfterMarkedRead = settingsStore.readBoolean(DOWNLOAD_REMOVE_AFTER_MARKED_READ_KEY),
            removeBookmarkedChapters = settingsStore.readBoolean(DOWNLOAD_REMOVE_BOOKMARKED_KEY),
            downloadNewChapters = settingsStore.readBoolean(DOWNLOAD_NEW_CHAPTERS_KEY),
            downloadNewUnreadOnly = settingsStore.readBoolean(DOWNLOAD_NEW_UNREAD_ONLY_KEY),
            parallelSourceDownloads = settingsStore.readInt(
                DOWNLOAD_PARALLEL_SOURCES_KEY,
                defaultValue = 5,
            ),
            parallelPageDownloads = settingsStore.readInt(
                DOWNLOAD_PARALLEL_PAGES_KEY,
                defaultValue = 5,
            ),
        )
    }

    suspend fun saveDownloadSettings(
        settings: ChimahonDownloadPreferences,
    ): ChimahonDownloadPreferences {
        settingsStore.writeBoolean(DOWNLOAD_WIFI_ONLY_KEY, settings.wifiOnly)
        settingsStore.writeBoolean(DOWNLOAD_SAVE_AS_CBZ_KEY, settings.saveAsCbz)
        settingsStore.writeBoolean(DOWNLOAD_SPLIT_TALL_IMAGES_KEY, settings.splitTallImages)
        settingsStore.writeInt(DOWNLOAD_WHILE_READING_KEY, settings.autoDownloadWhileReadingCount)
        settingsStore.writeInt(DOWNLOAD_REMOVE_AFTER_READ_KEY, settings.removeAfterReadSlots)
        settingsStore.writeBoolean(DOWNLOAD_REMOVE_AFTER_MARKED_READ_KEY, settings.removeAfterMarkedRead)
        settingsStore.writeBoolean(DOWNLOAD_REMOVE_BOOKMARKED_KEY, settings.removeBookmarkedChapters)
        settingsStore.writeBoolean(DOWNLOAD_NEW_CHAPTERS_KEY, settings.downloadNewChapters)
        settingsStore.writeBoolean(DOWNLOAD_NEW_UNREAD_ONLY_KEY, settings.downloadNewUnreadOnly)
        settingsStore.writeInt(DOWNLOAD_PARALLEL_SOURCES_KEY, settings.parallelSourceDownloads)
        settingsStore.writeInt(DOWNLOAD_PARALLEL_PAGES_KEY, settings.parallelPageDownloads)
        return settings
    }

    suspend fun loadBrowseSettings(): ChimahonBrowseSettings {
        return ChimahonBrowseSettings(
            showNsfwSources = settingsStore.readBoolean(BROWSE_SHOW_NSFW_KEY, defaultValue = true),
            hideLibraryEntries = settingsStore.readBoolean(BROWSE_HIDE_LIBRARY_ENTRIES_KEY),
            autoLoadMore = settingsStore.readBoolean(BROWSE_AUTO_LOAD_MORE_KEY, defaultValue = true),
            enabledLanguages = settingsStore.readString(BROWSE_ENABLED_LANGUAGES_KEY)
                ?.split(',')
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                .orEmpty(),
            sourceDisplayMode = readEnum(
                BROWSE_SOURCE_DISPLAY_MODE_KEY,
                ChimahonBrowseSourceDisplayMode.List,
            ),
            groupSourcesByLanguage = settingsStore.readBoolean(
                BROWSE_GROUP_SOURCES_BY_LANGUAGE_KEY,
                defaultValue = true,
            ),
            showSourceLanguage = settingsStore.readBoolean(
                BROWSE_SHOW_SOURCE_LANGUAGE_KEY,
                defaultValue = true,
            ),
            extensionUpdateNotificationsEnabled = settingsStore.readBoolean(
                BROWSE_EXTENSION_UPDATE_NOTIFICATIONS_KEY,
                defaultValue = true,
            ),
        )
    }

    suspend fun saveBrowseSettings(settings: ChimahonBrowseSettings): ChimahonBrowseSettings {
        settingsStore.writeBoolean(BROWSE_SHOW_NSFW_KEY, settings.showNsfwSources)
        settingsStore.writeBoolean(BROWSE_HIDE_LIBRARY_ENTRIES_KEY, settings.hideLibraryEntries)
        settingsStore.writeBoolean(BROWSE_AUTO_LOAD_MORE_KEY, settings.autoLoadMore)
        settingsStore.writeString(
            BROWSE_ENABLED_LANGUAGES_KEY,
            settings.enabledLanguages.joinToString(","),
        )
        settingsStore.writeString(BROWSE_SOURCE_DISPLAY_MODE_KEY, settings.sourceDisplayMode.name)
        settingsStore.writeBoolean(
            BROWSE_GROUP_SOURCES_BY_LANGUAGE_KEY,
            settings.groupSourcesByLanguage,
        )
        settingsStore.writeBoolean(BROWSE_SHOW_SOURCE_LANGUAGE_KEY, settings.showSourceLanguage)
        settingsStore.writeBoolean(
            BROWSE_EXTENSION_UPDATE_NOTIFICATIONS_KEY,
            settings.extensionUpdateNotificationsEnabled,
        )
        return settings
    }

    suspend fun loadSecuritySettings(): ChimahonSecuritySettings {
        return ChimahonSecuritySettings(
            secureScreenMode = readEnum(SECURITY_SECURE_SCREEN_KEY, ChimahonSecureScreenMode.Incognito),
            hideNotificationContent = settingsStore.readBoolean(SECURITY_HIDE_NOTIFICATION_CONTENT_KEY),
            requireAuthentication = settingsStore.readBoolean(SECURITY_REQUIRE_AUTHENTICATION_KEY),
            lockAfterMinutes = settingsStore.readInt(SECURITY_LOCK_AFTER_MINUTES_KEY),
            lockOnAppExit = settingsStore.readBoolean(SECURITY_LOCK_ON_APP_EXIT_KEY),
            incognitoModeByDefault = settingsStore.readBoolean(
                SECURITY_INCOGNITO_BY_DEFAULT_KEY,
            ),
            protectDownloads = settingsStore.readBoolean(SECURITY_PROTECT_DOWNLOADS_KEY),
        )
    }

    suspend fun saveSecuritySettings(settings: ChimahonSecuritySettings): ChimahonSecuritySettings {
        settingsStore.writeString(SECURITY_SECURE_SCREEN_KEY, settings.secureScreenMode.name)
        settingsStore.writeBoolean(
            SECURITY_HIDE_NOTIFICATION_CONTENT_KEY,
            settings.hideNotificationContent,
        )
        settingsStore.writeBoolean(SECURITY_REQUIRE_AUTHENTICATION_KEY, settings.requireAuthentication)
        settingsStore.writeInt(SECURITY_LOCK_AFTER_MINUTES_KEY, settings.lockAfterMinutes)
        settingsStore.writeBoolean(SECURITY_LOCK_ON_APP_EXIT_KEY, settings.lockOnAppExit)
        settingsStore.writeBoolean(
            SECURITY_INCOGNITO_BY_DEFAULT_KEY,
            settings.incognitoModeByDefault,
        )
        settingsStore.writeBoolean(SECURITY_PROTECT_DOWNLOADS_KEY, settings.protectDownloads)
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
        const val APPEARANCE_THEME_MODE_KEY = "__APP_STATE_chimahon_theme_mode"
        const val APPEARANCE_APP_THEME_KEY = "__APP_STATE_chimahon_app_theme"
        const val APPEARANCE_COLOR_THEME_KEY = "__APP_STATE_chimahon_color_theme"
        const val APPEARANCE_AMOLED_KEY = "__APP_STATE_chimahon_theme_amoled"
        const val APPEARANCE_FONT_SCALE_PERCENT_KEY = "__APP_STATE_chimahon_font_scale_percent"
        const val APPEARANCE_COMPACT_NAVIGATION_KEY = "__APP_STATE_chimahon_compact_navigation"
        const val APPEARANCE_RELATIVE_DATES_KEY = "__APP_STATE_chimahon_relative_dates"
        const val APPEARANCE_DESCRIPTION_IMAGES_KEY = "__APP_STATE_chimahon_description_images"
        const val READER_MODE_KEY = "__APP_STATE_chimahon_reader_mode"
        const val READER_SCALE_KEY = "__APP_STATE_chimahon_reader_scale"
        const val READER_CANVAS_KEY = "__APP_STATE_chimahon_reader_canvas"
        const val READER_ORIENTATION_KEY = "__APP_STATE_chimahon_reader_orientation"
        const val READER_DUAL_PAGE_MODE_KEY = "__APP_STATE_chimahon_reader_dual_page_mode"
        const val READER_SPLIT_WIDE_PAGES_KEY = "__APP_STATE_chimahon_reader_split_wide_pages"
        const val READER_COLOR_FILTER_ENABLED_KEY =
            "__APP_STATE_chimahon_reader_color_filter_enabled"
        const val READER_GRAYSCALE_KEY = "__APP_STATE_chimahon_reader_grayscale"
        const val READER_INVERT_COLORS_KEY = "__APP_STATE_chimahon_reader_invert_colors"
        const val READER_BRIGHTNESS_KEY = "__APP_STATE_chimahon_reader_brightness"
        const val READER_NAVIGATION_MODE_KEY = "__APP_STATE_chimahon_reader_navigation_mode"
        const val READER_PAGE_STRIP_KEY = "__APP_STATE_chimahon_reader_page_strip"
        const val READER_CONTROLS_KEY = "__APP_STATE_chimahon_reader_controls"
        const val READER_TAP_ZONES_KEY = "__APP_STATE_chimahon_reader_tap_zones"
        const val READER_SMALLER_TAP_ZONES_KEY = "__APP_STATE_chimahon_reader_smaller_tap_zones"
        const val READER_INVERT_TAP_ZONES_KEY = "__APP_STATE_chimahon_reader_invert_tap_zones"
        const val READER_SWIPE_NAVIGATION_KEY = "__APP_STATE_chimahon_reader_swipe_navigation"
        const val READER_DOUBLE_TAP_ZOOM_KEY = "__APP_STATE_chimahon_reader_double_tap_zoom"
        const val READER_VOLUME_KEYS_KEY = "__APP_STATE_chimahon_reader_volume_keys"
        const val READER_VOLUME_KEYS_INVERTED_KEY = "__APP_STATE_chimahon_reader_volume_keys_inverted"
        const val READER_LONG_TAP_KEY = "__APP_STATE_chimahon_reader_long_tap"
        const val READER_KEEP_SCREEN_ON_KEY = "__APP_STATE_chimahon_reader_keep_screen_on"
        const val READER_CROP_BORDERS_KEY = "__APP_STATE_chimahon_reader_crop_borders"
        const val READER_PAGE_TRANSITIONS_KEY = "__APP_STATE_chimahon_reader_page_transitions"
        const val READER_SHOW_PAGE_NUMBER_KEY = "__APP_STATE_chimahon_reader_show_page_number"
        const val LIBRARY_DISPLAY_MODE_KEY = "__APP_STATE_chimahon_library_display_mode"
        const val LIBRARY_GRID_COLUMNS_PORTRAIT_KEY =
            "__APP_STATE_chimahon_library_grid_columns_portrait"
        const val LIBRARY_GRID_COLUMNS_LANDSCAPE_KEY =
            "__APP_STATE_chimahon_library_grid_columns_landscape"
        const val LIBRARY_COVER_ASPECT_RATIO_KEY =
            "__APP_STATE_chimahon_library_cover_aspect_ratio"
        const val LIBRARY_CATEGORY_TABS_KEY = "__APP_STATE_chimahon_library_category_tabs"
        const val LIBRARY_UNREAD_BADGES_KEY = "__APP_STATE_chimahon_library_unread_badges"
        const val LIBRARY_DOWNLOADED_BADGES_KEY = "__APP_STATE_chimahon_library_downloaded_badges"
        const val LIBRARY_LANGUAGE_BADGES_KEY = "__APP_STATE_chimahon_library_language_badges"
        const val LIBRARY_CONTINUE_BUTTONS_KEY = "__APP_STATE_chimahon_library_continue_buttons"
        const val LIBRARY_SORT_KEY = "__APP_STATE_chimahon_library_sort"
        const val LIBRARY_SORT_ASCENDING_KEY = "__APP_STATE_chimahon_library_sort_ascending"
        const val LIBRARY_FILTER_DOWNLOADED_KEY = "__APP_STATE_chimahon_library_filter_downloaded"
        const val LIBRARY_FILTER_UNREAD_KEY = "__APP_STATE_chimahon_library_filter_unread"
        const val LIBRARY_FILTER_STARTED_KEY = "__APP_STATE_chimahon_library_filter_started"
        const val LIBRARY_FILTER_BOOKMARKED_KEY = "__APP_STATE_chimahon_library_filter_bookmarked"
        const val LIBRARY_FILTER_COMPLETED_KEY = "__APP_STATE_chimahon_library_filter_completed"
        const val LIBRARY_FILTER_TRACKED_KEY = "__APP_STATE_chimahon_library_filter_tracked"
        const val LIBRARY_UPDATE_INTERVAL_KEY = "__APP_STATE_chimahon_library_update_interval"
        const val LIBRARY_UPDATE_WIFI_ONLY_KEY = "__APP_STATE_chimahon_library_update_wifi_only"
        const val LIBRARY_SHOW_UPDATE_COUNT_KEY = "__APP_STATE_chimahon_library_show_update_count"
        const val LIBRARY_UPDATE_NOTIFICATIONS_KEY = "__APP_STATE_chimahon_library_update_notifications"
        const val DOWNLOAD_WIFI_ONLY_KEY = "__APP_STATE_chimahon_download_wifi_only"
        const val DOWNLOAD_SAVE_AS_CBZ_KEY = "__APP_STATE_chimahon_download_save_as_cbz"
        const val DOWNLOAD_SPLIT_TALL_IMAGES_KEY = "__APP_STATE_chimahon_download_split_tall_images"
        const val DOWNLOAD_WHILE_READING_KEY = "__APP_STATE_chimahon_download_while_reading"
        const val DOWNLOAD_REMOVE_AFTER_READ_KEY = "__APP_STATE_chimahon_download_remove_after_read"
        const val DOWNLOAD_REMOVE_AFTER_MARKED_READ_KEY = "__APP_STATE_chimahon_download_remove_after_marked_read"
        const val DOWNLOAD_REMOVE_BOOKMARKED_KEY = "__APP_STATE_chimahon_download_remove_bookmarked"
        const val DOWNLOAD_NEW_CHAPTERS_KEY = "__APP_STATE_chimahon_download_new_chapters"
        const val DOWNLOAD_NEW_UNREAD_ONLY_KEY = "__APP_STATE_chimahon_download_new_unread_only"
        const val DOWNLOAD_PARALLEL_SOURCES_KEY = "__APP_STATE_chimahon_download_parallel_sources"
        const val DOWNLOAD_PARALLEL_PAGES_KEY = "__APP_STATE_chimahon_download_parallel_pages"
        const val BROWSE_SHOW_NSFW_KEY = "__APP_STATE_chimahon_browse_show_nsfw"
        const val BROWSE_HIDE_LIBRARY_ENTRIES_KEY = "__APP_STATE_chimahon_browse_hide_library_entries"
        const val BROWSE_AUTO_LOAD_MORE_KEY = "__APP_STATE_chimahon_browse_auto_load_more"
        const val BROWSE_ENABLED_LANGUAGES_KEY = "__APP_STATE_chimahon_browse_enabled_languages"
        const val BROWSE_SOURCE_DISPLAY_MODE_KEY =
            "__APP_STATE_chimahon_browse_source_display_mode"
        const val BROWSE_GROUP_SOURCES_BY_LANGUAGE_KEY =
            "__APP_STATE_chimahon_browse_group_sources_by_language"
        const val BROWSE_SHOW_SOURCE_LANGUAGE_KEY =
            "__APP_STATE_chimahon_browse_show_source_language"
        const val BROWSE_EXTENSION_UPDATE_NOTIFICATIONS_KEY =
            "__APP_STATE_chimahon_browse_extension_update_notifications"
        const val SECURITY_SECURE_SCREEN_KEY = "__APP_STATE_chimahon_security_secure_screen"
        const val SECURITY_HIDE_NOTIFICATION_CONTENT_KEY =
            "__APP_STATE_chimahon_security_hide_notification_content"
        const val SECURITY_REQUIRE_AUTHENTICATION_KEY =
            "__APP_STATE_chimahon_security_require_authentication"
        const val SECURITY_LOCK_AFTER_MINUTES_KEY = "__APP_STATE_chimahon_security_lock_after_minutes"
        const val SECURITY_LOCK_ON_APP_EXIT_KEY = "__APP_STATE_chimahon_security_lock_on_app_exit"
        const val SECURITY_INCOGNITO_BY_DEFAULT_KEY =
            "__APP_STATE_chimahon_security_incognito_by_default"
        const val SECURITY_PROTECT_DOWNLOADS_KEY = "__APP_STATE_chimahon_security_protect_downloads"
        const val DOWNLOADED_ONLY_KEY = "__APP_STATE_pref_downloaded_only"
        const val INCOGNITO_MODE_KEY = "__APP_STATE_incognito_mode"
    }
}
