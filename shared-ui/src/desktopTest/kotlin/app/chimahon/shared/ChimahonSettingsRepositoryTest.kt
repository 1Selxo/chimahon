package app.chimahon.shared

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChimahonSettingsRepositoryTest {

    @Test
    fun emptyStoreUsesBackwardCompatibleDefaults() = runBlocking {
        val repository = ChimahonSettingsRepository(InMemoryPlatformSettingsStore())

        val settings = repository.loadSettings()

        assertEquals(ChimahonSettings(), settings)
    }

    @Test
    fun persistsEverySettingsGroupAcrossRepositoryInstances() = runBlocking {
        val store = InMemoryPlatformSettingsStore()
        val repository = ChimahonSettingsRepository(store)
        val appearance = ChimahonAppearanceSettings(
            themeMode = ChimahonThemeMode.Dark,
            appTheme = ChimahonAppTheme.Tako,
            colorTheme = ChimahonColorTheme.Teal,
            appIcon = ChimahonAppIcon.Mihon,
            amoled = true,
            fontScalePercent = 115,
            compactNavigation = true,
            relativeDates = false,
            showDescriptionImages = false,
        )
        val reader = ChimahonReaderSettings(
            mode = ChimahonReaderMode.RightToLeft,
            scale = ChimahonReaderScale.FitScreen,
            canvas = ChimahonReaderCanvas.White,
            pureBlackBackground = false,
            showPageStrip = false,
            keepControlsVisible = false,
            tapZonesEnabled = false,
            smallerTapZones = true,
            invertTapZones = ChimahonTapZoneInvert.Both,
            volumeKeysEnabled = true,
            volumeKeysInverted = true,
            longTapEnabled = false,
            keepScreenOn = true,
            cropBorders = true,
            pageTransitions = false,
            showPageNumber = false,
            verticalWriting = false,
            continuousMode = true,
            fontSize = 22.5,
            lineHeight = 1.9,
            horizontalPadding = 14.5,
            verticalPadding = 16.5,
            avoidPageBreak = false,
            justifyText = true,
            characterSpacing = 0.25,
            paragraphSpacing = 1.5,
            hideFurigana = true,
            showTitle = false,
            showCharacters = false,
            showPercentage = false,
            showProgressTop = false,
            showReadingSpeed = false,
            showReadingTime = false,
            tapZonePercent = 35,
            chapterSwipeDistance = 128,
        )
        val library = ChimahonLibrarySettings(
            displayMode = ChimahonLibraryDisplayMode.List,
            showCategoryTabs = false,
            showUnreadBadges = false,
            showDownloadedBadges = false,
            showLanguageBadges = true,
            showContinueButtons = false,
            sort = ChimahonLibrarySort.LastUpdate,
            sortAscending = false,
            downloadedFilter = ChimahonFilterMode.Include,
            unreadFilter = ChimahonFilterMode.Exclude,
            startedFilter = ChimahonFilterMode.Include,
            bookmarkedFilter = ChimahonFilterMode.Exclude,
            completedFilter = ChimahonFilterMode.Include,
            autoUpdateIntervalHours = 12,
            updateOnlyOnWifi = false,
            showUpdateCount = false,
            updateNotificationsEnabled = false,
        )
        val downloads = ChimahonDownloadPreferences(
            wifiOnly = false,
            saveAsCbz = false,
            splitTallImages = false,
            autoDownloadWhileReadingCount = 3,
            removeAfterReadSlots = 2,
            removeAfterMarkedRead = true,
            removeBookmarkedChapters = true,
            downloadNewChapters = true,
            downloadNewUnreadOnly = true,
            parallelSourceDownloads = 7,
            parallelPageDownloads = 9,
        )
        val browse = ChimahonBrowseSettings(
            showNsfwSources = false,
            hideLibraryEntries = true,
            autoLoadMore = false,
            extensionUpdateNotificationsEnabled = false,
        )
        val security = ChimahonSecuritySettings(
            secureScreenMode = ChimahonSecureScreenMode.Always,
            hideNotificationContent = true,
            requireAuthentication = true,
            lockAfterMinutes = 15,
            protectDownloads = true,
        )

        repository.saveAppearanceSettings(appearance)
        repository.saveReaderSettings(reader)
        repository.saveLibrarySettings(library)
        repository.saveDownloadSettings(downloads)
        repository.saveBrowseSettings(browse)
        repository.saveSecuritySettings(security)
        repository.setDownloadedOnly(true)
        repository.setIncognitoMode(true)

        val settings = ChimahonSettingsRepository(store).loadSettings()

        assertEquals(appearance, settings.appearance)
        assertEquals(reader, settings.reader)
        assertEquals(library, settings.library)
        assertEquals(downloads, settings.downloads)
        assertEquals(browse, settings.browse)
        assertEquals(security, settings.security)
        assertTrue(settings.appMode.downloadedOnly)
        assertTrue(settings.appMode.incognitoMode)
        assertTrue(repository.isIncognitoModeEnabled())
    }

    @Test
    fun unknownEnumValuesFallBackToDefaults() = runBlocking {
        val store = InMemoryPlatformSettingsStore(
            mutableMapOf(
                "__APP_STATE_chimahon_reader_mode" to "Paged",
                "__APP_STATE_chimahon_reader_scale" to "ActualSize",
                "__APP_STATE_chimahon_reader_canvas" to "Sepia",
                "__APP_STATE_chimahon_reader_pure_black_background" to "always",
                "__APP_STATE_chimahon_app_icon" to "Triangle",
                "__APP_STATE_chimahon_reader_page_strip" to "sometimes",
                "__APP_STATE_chimahon_reader_controls" to "",
                "__APP_STATE_chimahon_reader_invert_tap_zones" to "Diagonal",
                "__APP_STATE_chimahon_reader_volume_keys" to "enabled",
                "__APP_STATE_chimahon_library_display_mode" to "Shelf",
                "__APP_STATE_chimahon_library_category_tabs" to "1",
                "__APP_STATE_chimahon_library_unread_badges" to "TRUE",
                "__APP_STATE_chimahon_library_continue_buttons" to "yes",
                "__APP_STATE_chimahon_library_sort" to "Popularity",
                "__APP_STATE_chimahon_library_filter_downloaded" to "Only",
                "__APP_STATE_chimahon_library_update_interval" to "hourly",
                "__APP_STATE_chimahon_download_remove_after_read" to "soon",
                "__APP_STATE_chimahon_download_parallel_sources" to "many",
                "__APP_STATE_chimahon_browse_show_nsfw" to "sometimes",
                "__APP_STATE_chimahon_security_secure_screen" to "WhenLocked",
                "__APP_STATE_chimahon_security_lock_after_minutes" to "later",
            ),
        )
        val repository = ChimahonSettingsRepository(store)

        val settings = repository.loadSettings()

        assertEquals(ChimahonSettings(), settings)
    }
}
