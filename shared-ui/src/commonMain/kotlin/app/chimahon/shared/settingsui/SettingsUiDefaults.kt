package app.chimahon.shared.settingsui

import app.chimahon.shared.ChimahonAppModeSettings
import app.chimahon.shared.ChimahonAppearanceSettings
import app.chimahon.shared.ChimahonAppIcon
import app.chimahon.shared.ChimahonAppTheme
import app.chimahon.shared.ChimahonAnimeLibrarySettings
import app.chimahon.shared.ChimahonAudioChannels
import app.chimahon.shared.ChimahonAutoTrackOnMarkRead
import app.chimahon.shared.ChimahonBrowseSettings
import app.chimahon.shared.ChimahonBrowseSourceDisplayMode
import app.chimahon.shared.ChimahonBrowseSourceFilterMode
import app.chimahon.shared.ChimahonBrowseTab
import app.chimahon.shared.ChimahonColorTheme
import app.chimahon.shared.ChimahonDataSettings
import app.chimahon.shared.ChimahonDictionaryOcrEngine
import app.chimahon.shared.ChimahonDictionaryPopupMode
import app.chimahon.shared.ChimahonDictionaryRecursiveLookupMode
import app.chimahon.shared.ChimahonDictionarySettings
import app.chimahon.shared.ChimahonDictionaryThemeMode
import app.chimahon.shared.ChimahonDownloadCleanupPolicy
import app.chimahon.shared.ChimahonDownloadEncryption
import app.chimahon.shared.ChimahonDownloadOrder
import app.chimahon.shared.ChimahonDownloadPreferences
import app.chimahon.shared.ChimahonDownloadStorageLocation
import app.chimahon.shared.ChimahonDualPageMode
import app.chimahon.shared.ChimahonEmptyCategoryVisibility
import app.chimahon.shared.ChimahonEpisodeSwipeAction
import app.chimahon.shared.ChimahonExtensionFilterMode
import app.chimahon.shared.ChimahonExtensionSortMode
import app.chimahon.shared.ChimahonFilterMode
import app.chimahon.shared.ChimahonInstalledSourceOrder
import app.chimahon.shared.ChimahonLibraryContinueButtonMode
import app.chimahon.shared.ChimahonLibraryCoverRatio
import app.chimahon.shared.ChimahonLibraryDisplayMode
import app.chimahon.shared.ChimahonLibraryGroup
import app.chimahon.shared.ChimahonLibraryLayoutBehavior
import app.chimahon.shared.ChimahonLibrarySettings
import app.chimahon.shared.ChimahonLibrarySort
import app.chimahon.shared.ChimahonLibraryUpdateGroupMode
import app.chimahon.shared.ChimahonMaterialPaletteStyle
import app.chimahon.shared.ChimahonPlayerAspect
import app.chimahon.shared.ChimahonPlayerDebanding
import app.chimahon.shared.ChimahonPlayerGestureAction
import app.chimahon.shared.ChimahonPlayerOrientation
import app.chimahon.shared.ChimahonPlayerSettings
import app.chimahon.shared.ChimahonReaderCanvas
import app.chimahon.shared.ChimahonReaderColorFilterMode
import app.chimahon.shared.ChimahonReaderFlashColor
import app.chimahon.shared.ChimahonReaderHideThreshold
import app.chimahon.shared.ChimahonReaderKeyboardScheme
import app.chimahon.shared.ChimahonReaderLandscapeZoomType
import app.chimahon.shared.ChimahonReaderMode
import app.chimahon.shared.ChimahonReaderMouseWheelAction
import app.chimahon.shared.ChimahonReaderNavigationMode
import app.chimahon.shared.ChimahonReaderOrientation
import app.chimahon.shared.ChimahonReaderScale
import app.chimahon.shared.ChimahonReaderSettings
import app.chimahon.shared.ChimahonReaderStatisticsAutostartMode
import app.chimahon.shared.ChimahonReaderTapNavigationLayout
import app.chimahon.shared.ChimahonReaderTextTheme
import app.chimahon.shared.ChimahonReaderZoomStart
import app.chimahon.shared.ChimahonRestoreMissingSourceBehavior
import app.chimahon.shared.ChimahonSecureScreenMode
import app.chimahon.shared.ChimahonSecuritySettings
import app.chimahon.shared.ChimahonSettings
import app.chimahon.shared.ChimahonStartScreen
import app.chimahon.shared.ChimahonSubtitleBorderStyle
import app.chimahon.shared.ChimahonSubtitleJustification
import app.chimahon.shared.ChimahonSubtitleListMode
import app.chimahon.shared.ChimahonTabletUiMode
import app.chimahon.shared.ChimahonTapZoneInvert
import app.chimahon.shared.ChimahonThemeMode
import app.chimahon.shared.ChimahonTrackingSettings
import app.chimahon.shared.ChimahonWebtoonScaleType
import app.chimahon.shared.anime.ChimahonAnimeEpisodeDisplayMode
import app.chimahon.shared.anime.ChimahonAnimeEpisodeSortMode

fun ChimahonSettings.toSettingsUiState(
    selectedRoute: ChimahonSettingsRoute = ChimahonSettingsRoute.Appearance,
): ChimahonSettingsUiState {
    return ChimahonSettingsUiState(
        selectedRoute = selectedRoute,
        screens = listOf(
            appearance.toAppearanceSettingsScreen(),
            library.toLibrarySettingsScreen(),
            toAnimeSettingsScreen(),
            reader.toReaderSettingsScreen(),
            downloads.toDownloadSettingsScreen(),
            tracking.toTrackingSettingsScreen(),
            browse.toBrowseSettingsScreen(),
            toAdvancedSettingsScreen(),
            data.toBackupSettingsScreen(),
        ),
    )
}

fun ChimahonAppearanceSettings.toAppearanceSettingsScreen(): ChimahonSettingsScreen {
    return ChimahonSettingsScreen(
        route = ChimahonSettingsRoute.Appearance,
        sections = listOf(
            section("appearance.theme", "Theme") {
                listOf(
                    listPreference(
                        key = "appearance.themeMode",
                        title = "Theme mode",
                        selectedKey = themeMode.name,
                        options = ChimahonThemeMode.entries.options { it.title },
                    ),
                    listPreference(
                        key = "appearance.appTheme",
                        title = "App theme",
                        subtitle = "Matches Mihon/Tachiyomi theme presets.",
                        selectedKey = appTheme.name,
                        options = ChimahonAppTheme.entries.options { it.title },
                    ),
                    listPreference(
                        key = "appearance.colorTheme",
                        title = "Color theme",
                        selectedKey = colorTheme.name,
                        options = ChimahonColorTheme.entries.options { it.title },
                    ),
                    listPreference(
                        key = "appearance.customThemeStyle",
                        title = "Custom theme style",
                        selectedKey = customThemeStyle.name,
                        options = ChimahonMaterialPaletteStyle.entries.options { it.title },
                        enabled = appTheme == ChimahonAppTheme.Custom,
                    ),
                    switchPreference("appearance.amoled", "Pure black dark mode", amoled),
                )
            },
            section("appearance.interface", "Interface") {
                listOf(
                    listPreference(
                        key = "appearance.tabletUiMode",
                        title = "Tablet UI",
                        selectedKey = tabletUiMode.name,
                        options = ChimahonTabletUiMode.entries.options { it.title },
                    ),
                    listPreference(
                        key = "appearance.startScreen",
                        title = "Start screen",
                        selectedKey = startScreen.name,
                        options = ChimahonStartScreen.entries.options { it.title },
                    ),
                    listPreference(
                        key = "appearance.dateFormat",
                        title = "Date format",
                        selectedKey = dateFormat.name,
                        options = app.chimahon.shared.ChimahonDateFormat.entries.options { it.title },
                    ),
                    listPreference(
                        key = "appearance.appIcon",
                        title = "App icon",
                        selectedKey = appIcon.name,
                        options = ChimahonAppIcon.entries.options { it.title },
                    ),
                    sliderPreference(
                        key = "appearance.fontScalePercent",
                        title = "Font scale",
                        value = fontScalePercent.toFloat(),
                        range = 85f..130f,
                        steps = 8,
                        label = "$fontScalePercent%",
                    ),
                    switchPreference("appearance.compactNavigation", "Compact navigation rail", compactNavigation),
                    switchPreference("appearance.bottomBarLabels", "Show bottom navigation labels", bottomBarLabels),
                    switchPreference("appearance.showNavigationBadges", "Show navigation badges", showNavigationBadges),
                    switchPreference("appearance.showTopBarSubtitle", "Show top bar subtitles", showTopBarSubtitle),
                )
            },
            section("appearance.mangaInfo", "Manga details") {
                listOf(
                    switchPreference("appearance.showDescriptionImages", "Show description images", showDescriptionImages),
                    switchPreference(
                        key = "appearance.mangaInfoCoverBasedTheme",
                        title = "Use cover colors",
                        checked = mangaInfoCoverBasedTheme,
                    ),
                    listPreference(
                        key = "appearance.mangaInfoCoverBasedStyle",
                        title = "Cover color style",
                        selectedKey = mangaInfoCoverBasedStyle.name,
                        options = ChimahonMaterialPaletteStyle.entries.options { it.title },
                        enabled = mangaInfoCoverBasedTheme,
                    ),
                    switchPreference("appearance.usePanoramaCoverMangaInfo", "Panorama cover header", usePanoramaCoverMangaInfo),
                    switchPreference("appearance.topAlignCover", "Top-align cover", topAlignCover),
                    switchPreference("appearance.usePanoramaCoverFlow", "Panorama library cover flow", usePanoramaCoverFlow),
                )
            },
        ),
    )
}

fun ChimahonLibrarySettings.toLibrarySettingsScreen(): ChimahonSettingsScreen {
    return ChimahonSettingsScreen(
        route = ChimahonSettingsRoute.Library,
        sections = listOf(
            section("library.display", "Display") {
                listOf(
                    listPreference(
                        key = "library.displayMode",
                        title = "Display mode",
                        selectedKey = displayMode.name,
                        options = ChimahonLibraryDisplayMode.entries.options { it.name.readableEnumLabel() },
                    ),
                    sliderPreference(
                        key = "library.gridColumnsPortrait",
                        title = "Portrait grid columns",
                        subtitle = "0 keeps automatic sizing.",
                        value = gridColumnsPortrait.toFloat(),
                        range = 0f..12f,
                        steps = 11,
                        label = if (gridColumnsPortrait == 0) "Auto" else gridColumnsPortrait.toString(),
                    ),
                    sliderPreference(
                        key = "library.gridColumnsLandscape",
                        title = "Landscape grid columns",
                        subtitle = "0 keeps automatic sizing.",
                        value = gridColumnsLandscape.toFloat(),
                        range = 0f..16f,
                        steps = 15,
                        label = if (gridColumnsLandscape == 0) "Auto" else gridColumnsLandscape.toString(),
                    ),
                    listPreference(
                        key = "library.coverAspectRatio",
                        title = "Cover ratio",
                        selectedKey = coverAspectRatio.name,
                        options = ChimahonLibraryCoverRatio.entries.options { it.name.readableEnumLabel() },
                    ),
                    listPreference(
                        key = "library.layoutBehavior",
                        title = "Layout behavior",
                        selectedKey = layoutBehavior.name,
                        options = ChimahonLibraryLayoutBehavior.entries.options { it.title },
                    ),
                    switchPreference("library.categorizedDisplaySettings", "Per-category display settings", categorizedDisplaySettings),
                    switchPreference("library.showCategoryTabs", "Show category tabs", showCategoryTabs),
                    switchPreference("library.rememberCategorySelection", "Remember selected category", rememberCategorySelection),
                    listPreference(
                        key = "library.emptyCategoryVisibility",
                        title = "Empty categories",
                        selectedKey = emptyCategoryVisibility.name,
                        options = ChimahonEmptyCategoryVisibility.entries.options { it.title },
                    ),
                )
            },
            section("library.badges", "Badges and metadata") {
                listOf(
                    switchPreference("library.showUnreadBadges", "Unread badges", showUnreadBadges),
                    switchPreference("library.showDownloadedBadges", "Downloaded badges", showDownloadedBadges),
                    switchPreference("library.showLocalBadges", "Local badges", showLocalBadges),
                    switchPreference("library.showLanguageBadges", "Language badges", showLanguageBadges),
                    switchPreference("library.showSourceBadges", "Source badges", showSourceBadges),
                    switchPreference("library.showTrackingBadges", "Tracking badges", showTrackingBadges),
                    switchPreference("library.showCategoryItemCount", "Category item count", showCategoryItemCount),
                    switchPreference("library.showLatestChapter", "Latest chapter in list", showLatestChapter),
                    switchPreference("library.showLastReadAt", "Last read date", showLastReadAt),
                    switchPreference("library.showContinueButtons", "Continue reading buttons", showContinueButtons),
                    listPreference(
                        key = "library.continueButtonMode",
                        title = "Continue button mode",
                        selectedKey = continueButtonMode.name,
                        options = ChimahonLibraryContinueButtonMode.entries.options { it.title },
                        enabled = showContinueButtons,
                    ),
                )
            },
            section("library.filters", "Sort and filters") {
                listOf(
                    listPreference(
                        key = "library.sort",
                        title = "Default sort",
                        selectedKey = sort.name,
                        options = ChimahonLibrarySort.entries.options { it.name.readableEnumLabel() },
                    ),
                    switchPreference("library.sortAscending", "Ascending sort", sortAscending),
                    listPreference("library.downloadedFilter", "Downloaded filter", downloadedFilter.name, ChimahonFilterMode.entries.options { it.name }),
                    listPreference("library.unreadFilter", "Unread filter", unreadFilter.name, ChimahonFilterMode.entries.options { it.name }),
                    listPreference("library.startedFilter", "Started filter", startedFilter.name, ChimahonFilterMode.entries.options { it.name }),
                    listPreference("library.bookmarkedFilter", "Bookmarked filter", bookmarkedFilter.name, ChimahonFilterMode.entries.options { it.name }),
                    listPreference("library.completedFilter", "Completed filter", completedFilter.name, ChimahonFilterMode.entries.options { it.name }),
                    listPreference("library.trackedFilter", "Tracked filter", trackedFilter.name, ChimahonFilterMode.entries.options { it.name }),
                )
            },
            section("library.updates", "Updates") {
                listOf(
                    sliderPreference(
                        key = "library.autoUpdateIntervalHours",
                        title = "Automatic updates",
                        value = autoUpdateIntervalHours.toFloat(),
                        range = 0f..168f,
                        steps = 27,
                        label = if (autoUpdateIntervalHours == 0) "Off" else "${autoUpdateIntervalHours}h",
                    ),
                    switchPreference("library.updateOnlyOnWifi", "Update only on Wi-Fi", updateOnlyOnWifi),
                    multiPreference("library.updateRestrictions", "Update restrictions", updateRestrictions, commonRestrictionOptions()),
                    listPreference(
                        key = "library.updateGroupMode",
                        title = "Update categories",
                        selectedKey = updateGroupMode.name,
                        options = ChimahonLibraryUpdateGroupMode.entries.options { it.title },
                    ),
                    switchPreference("library.autoUpdateMetadata", "Refresh metadata on update", autoUpdateMetadata),
                    multiPreference("library.smartUpdateRestrictions", "Smart update restrictions", smartUpdateRestrictions, smartUpdateOptions()),
                    switchPreference("library.showUpdateCount", "Show update count", showUpdateCount),
                    switchPreference("library.updateNotificationsEnabled", "Update notifications", updateNotificationsEnabled),
                    switchPreference("library.showUpdatingProgressBanner", "Updating progress banner", showUpdatingProgressBanner),
                )
            },
            section("library.gestures", "Gestures and advanced") {
                listOf(
                    listPreference(
                        key = "library.swipeToStartAction",
                        title = "Swipe start action",
                        selectedKey = swipeToStartAction.name,
                        options = app.chimahon.shared.ChimahonChapterSwipeAction.entries.options { it.title },
                    ),
                    listPreference(
                        key = "library.swipeToEndAction",
                        title = "Swipe end action",
                        selectedKey = swipeToEndAction.name,
                        options = app.chimahon.shared.ChimahonChapterSwipeAction.entries.options { it.title },
                    ),
                    multiPreference("library.duplicateReadChapterHandling", "Duplicate read handling", duplicateReadChapterHandling, duplicateChapterOptions()),
                    switchPreference("library.hideMissingChapters", "Hide missing chapters", hideMissingChapters),
                    switchPreference("library.showEmptyCategoriesSearch", "Show empty categories in search", showEmptyCategoriesSearch),
                    switchPreference("library.fetchMetadataOnAdd", "Fetch metadata on add", fetchMetadataOnAdd),
                    switchPreference("library.fetchChaptersOnAdd", "Fetch chapters on add", fetchChaptersOnAdd),
                    switchPreference("library.updateMangaTitles", "Update manga titles", updateMangaTitles),
                    switchPreference("library.disallowNonAsciiFilenames", "Disallow non-ASCII filenames", disallowNonAsciiFilenames),
                )
            },
        ),
    )
}

fun ChimahonSettings.toAnimeSettingsScreen(): ChimahonSettingsScreen {
    return ChimahonSettingsScreen(
        route = ChimahonSettingsRoute.Anime,
        sections = listOf(
            animeLibrary.toAnimeLibraryDisplaySection(),
            animeLibrary.toAnimeLibraryBadgeSection(),
            animeLibrary.toAnimeLibraryFilterSection(),
            animeLibrary.toAnimeEpisodeDefaultsSection(),
            animeLibrary.toAnimeUpdateGestureSection(),
            player.toAnimePlayerBehaviorSection(),
            player.toAnimeAutoplaySection(),
            player.toAnimePlayerGestureSection(),
            player.toAnimeExternalSelectionSection(),
            player.toAnimeSubtitleSection(),
            player.toAnimeAudioDecoderSection(),
            tracking.toAnimeTrackingSection(),
        ),
    )
}

private fun ChimahonAnimeLibrarySettings.toAnimeLibraryDisplaySection(): ChimahonSettingsSection {
    return section("animeLibrary.display", "Library display") {
        listOf(
            listPreference(
                key = "animeLibrary.displayMode",
                title = "Display mode",
                selectedKey = displayMode.name,
                options = ChimahonLibraryDisplayMode.entries.options { it.name.readableEnumLabel() },
            ),
            sliderPreference(
                key = "animeLibrary.gridColumnsPortrait",
                title = "Portrait grid columns",
                subtitle = "0 keeps automatic sizing.",
                value = gridColumnsPortrait.toFloat(),
                range = 0f..12f,
                steps = 11,
                label = if (gridColumnsPortrait == 0) "Auto" else gridColumnsPortrait.toString(),
            ),
            sliderPreference(
                key = "animeLibrary.gridColumnsLandscape",
                title = "Landscape grid columns",
                subtitle = "0 keeps automatic sizing.",
                value = gridColumnsLandscape.toFloat(),
                range = 0f..16f,
                steps = 15,
                label = if (gridColumnsLandscape == 0) "Auto" else gridColumnsLandscape.toString(),
            ),
            infoPreference(
                key = "animeLibrary.defaultCategoryId",
                title = "Default category",
                subtitle = defaultCategoryId.animeDefaultCategoryLabel(),
            ),
            switchPreference(
                key = "animeLibrary.categorizedDisplaySettings",
                title = "Per-category display settings",
                checked = categorizedDisplaySettings,
                subtitle = "Remember anime display, sort, and filters per category.",
            ),
            switchPreference("animeLibrary.showCategoryTabs", "Show category tabs", showCategoryTabs),
            switchPreference(
                key = "animeLibrary.showCategoryItemCount",
                title = "Category item count",
                checked = showCategoryItemCount,
                enabled = showCategoryTabs,
            ),
            listPreference(
                key = "animeLibrary.groupBy",
                title = "Group by",
                selectedKey = groupBy.name,
                options = ChimahonLibraryGroup.entries.options { it.title },
            ),
        )
    }
}

private fun ChimahonAnimeLibrarySettings.toAnimeLibraryBadgeSection(): ChimahonSettingsSection {
    return section("animeLibrary.badges", "Badges and metadata") {
        listOf(
            switchPreference("animeLibrary.showUnseenBadges", "Unseen badges", showUnseenBadges),
            switchPreference("animeLibrary.showDownloadedBadges", "Downloaded badges", showDownloadedBadges),
            switchPreference("animeLibrary.showLocalBadges", "Local badges", showLocalBadges),
            switchPreference("animeLibrary.showLanguageBadges", "Language badges", showLanguageBadges),
            switchPreference(
                key = "animeLibrary.showContinueWatchingButtons",
                title = "Continue watching buttons",
                checked = showContinueWatchingButtons,
                subtitle = "Resume the next unseen or started episode from library entries.",
            ),
        )
    }
}

private fun ChimahonAnimeLibrarySettings.toAnimeLibraryFilterSection(): ChimahonSettingsSection {
    return section("animeLibrary.filters", "Library sort and filters") {
        listOf(
            listPreference(
                key = "animeLibrary.sort",
                title = "Default sort",
                selectedKey = sort.name,
                options = ChimahonLibrarySort.entries.options { it.name.readableEnumLabel() },
            ),
            switchPreference("animeLibrary.sortAscending", "Ascending sort", sortAscending),
            listPreference(
                "animeLibrary.downloadedFilter",
                "Downloaded filter",
                downloadedFilter.name,
                ChimahonFilterMode.entries.options { it.filterSettingsTitle() },
            ),
            listPreference(
                "animeLibrary.unseenFilter",
                "Unseen filter",
                unseenFilter.name,
                ChimahonFilterMode.entries.options { it.filterSettingsTitle() },
            ),
            listPreference(
                "animeLibrary.startedFilter",
                "Started filter",
                startedFilter.name,
                ChimahonFilterMode.entries.options { it.filterSettingsTitle() },
            ),
            listPreference(
                "animeLibrary.bookmarkedFilter",
                "Bookmarked filter",
                bookmarkedFilter.name,
                ChimahonFilterMode.entries.options { it.filterSettingsTitle() },
            ),
            listPreference(
                "animeLibrary.completedFilter",
                "Completed filter",
                completedFilter.name,
                ChimahonFilterMode.entries.options { it.filterSettingsTitle() },
            ),
            listPreference(
                "animeLibrary.fillerFilter",
                "Filler filter",
                fillerFilter.name,
                ChimahonFilterMode.entries.options { it.filterSettingsTitle() },
            ),
            listPreference(
                "animeLibrary.trackedFilter",
                "Tracked filter",
                trackedFilter.name,
                ChimahonFilterMode.entries.options { it.filterSettingsTitle() },
            ),
        )
    }
}

private fun ChimahonAnimeLibrarySettings.toAnimeEpisodeDefaultsSection(): ChimahonSettingsSection {
    return section("animeLibrary.episodes", "Episode defaults") {
        listOf(
            listPreference(
                key = "animeLibrary.episodeSort",
                title = "Episode sort",
                selectedKey = episodeSort.name,
                options = ChimahonAnimeEpisodeSortMode.entries.options { it.settingsTitle() },
            ),
            switchPreference(
                key = "animeLibrary.episodeSortDescending",
                title = "Descending episode sort",
                checked = episodeSortDescending,
                subtitle = "Matches Android-like source episode ordering by default.",
            ),
            listPreference(
                key = "animeLibrary.episodeDisplayMode",
                title = "Episode display",
                selectedKey = episodeDisplayMode.name,
                options = ChimahonAnimeEpisodeDisplayMode.entries.options { it.settingsTitle() },
            ),
            listPreference(
                "animeLibrary.episodeUnseenFilter",
                "Unseen filter",
                episodeUnseenFilter.name,
                ChimahonFilterMode.entries.options { it.filterSettingsTitle() },
            ),
            listPreference(
                "animeLibrary.episodeDownloadedFilter",
                "Downloaded filter",
                episodeDownloadedFilter.name,
                ChimahonFilterMode.entries.options { it.filterSettingsTitle() },
            ),
            listPreference(
                "animeLibrary.episodeBookmarkedFilter",
                "Bookmarked filter",
                episodeBookmarkedFilter.name,
                ChimahonFilterMode.entries.options { it.filterSettingsTitle() },
            ),
            listPreference(
                "animeLibrary.episodeFillerFilter",
                "Filler filter",
                episodeFillerFilter.name,
                ChimahonFilterMode.entries.options { it.filterSettingsTitle() },
            ),
        )
    }
}

private fun ChimahonAnimeLibrarySettings.toAnimeUpdateGestureSection(): ChimahonSettingsSection {
    return section("animeLibrary.updates", "Updates and gestures") {
        listOf(
            multiPreference(
                key = "animeLibrary.updateRestrictions",
                title = "Update restrictions",
                selected = updateRestrictions,
                options = animeUpdateRestrictionOptions(),
            ),
            listPreference(
                key = "animeLibrary.swipeToStartAction",
                title = "Episode swipe start",
                selectedKey = swipeToStartAction.name,
                options = ChimahonEpisodeSwipeAction.entries.options { it.title },
            ),
            listPreference(
                key = "animeLibrary.swipeToEndAction",
                title = "Episode swipe end",
                selectedKey = swipeToEndAction.name,
                options = ChimahonEpisodeSwipeAction.entries.options { it.title },
            ),
        )
    }
}

private fun ChimahonPlayerSettings.toAnimePlayerBehaviorSection(): ChimahonSettingsSection {
    return section("animePlayer.behavior", "Player behavior") {
        listOf(
            switchPreference(
                key = "player.preserveWatchingPosition",
                title = "Preserve watching position",
                checked = preserveWatchingPosition,
            ),
            sliderPreference(
                key = "player.progressPreference",
                title = "Mark seen threshold",
                value = progressPreference.toFloat(),
                range = 0.5f..1f,
                steps = 49,
                label = "${(progressPreference * 100.0).toInt()}%",
            ),
            listPreference(
                key = "player.defaultOrientation",
                title = "Default orientation",
                selectedKey = defaultOrientation.name,
                options = ChimahonPlayerOrientation.entries.options { it.title },
            ),
            listPreference(
                key = "player.aspect",
                title = "Aspect ratio",
                selectedKey = aspect.name,
                options = ChimahonPlayerAspect.entries.options { it.title },
            ),
            listPreference(
                key = "player.playerSpeed",
                title = "Playback speed",
                selectedKey = playerSpeed.speedKey(),
                options = playerSpeedOptions(playerSpeed, speedPresets),
            ),
            switchPreference("player.invertDuration", "Invert duration display", invertDuration),
            switchPreference("player.fullscreen", "Fullscreen", fullscreen),
            switchPreference("player.hideControls", "Hide controls by default", hideControls),
            switchPreference("player.displayVolumePercent", "Display volume percent", displayVolumePercent),
            switchPreference("player.showCurrentEpisode", "Show current episode", showCurrentEpisode),
            switchPreference("player.showLoadingCircle", "Show loading indicator", showLoadingCircle),
            switchPreference("player.allowGesturesInPanels", "Allow gestures in panels", allowGesturesInPanels),
            switchPreference("player.rememberBrightness", "Remember brightness", rememberBrightness),
            switchPreference("player.rememberVolume", "Remember volume", rememberVolume),
            switchPreference("player.showFailedHosters", "Show failed hosters", showFailedHosters),
            switchPreference("player.showEmptyHosters", "Show empty hosters", showEmptyHosters),
            switchPreference("player.showSystemStatusBar", "Show system status bar", showSystemStatusBar),
            switchPreference("player.reduceMotion", "Reduce player motion", reduceMotion),
            sliderPreference(
                "player.controlsHideDelayMillis",
                "Controls hide delay",
                controlsHideDelayMillis.toFloat(),
                500f..10000f,
                18,
                "${controlsHideDelayMillis}ms",
            ),
            sliderPreference(
                "player.panelOpacityPercent",
                "Panel opacity",
                panelOpacityPercent.toFloat(),
                0f..100f,
                19,
                "$panelOpacityPercent%",
            ),
        )
    }
}

private fun ChimahonPlayerSettings.toAnimeAutoplaySection(): ChimahonSettingsSection {
    return section("animePlayer.autoplay", "Auto-next and skip") {
        listOf(
            switchPreference(
                key = "player.autoplayEnabled",
                title = "Auto-next episodes",
                checked = autoplayEnabled,
                subtitle = "Continue to the next episode when playback ends.",
            ),
            switchPreference("player.skipIntroEnabled", "Skip intro controls", skipIntroEnabled),
            switchPreference("player.autoSkipIntro", "Auto-skip intro", autoSkipIntro, enabled = skipIntroEnabled),
            switchPreference(
                "player.netflixStyleSkipIntro",
                "Netflix-style skip button",
                netflixStyleSkipIntro,
                enabled = skipIntroEnabled,
            ),
            sliderPreference(
                "player.skipIntroWaitSeconds",
                "Skip intro wait",
                skipIntroWaitSeconds.toFloat(),
                0f..30f,
                29,
                "${skipIntroWaitSeconds}s",
                enabled = skipIntroEnabled,
            ),
            switchPreference("player.aniSkipEnabled", "AniSkip integration", aniSkipEnabled),
            switchPreference(
                "player.disableAniSkipOnChapters",
                "Disable AniSkip when chapters exist",
                disableAniSkipOnChapters,
                enabled = aniSkipEnabled,
            ),
            switchPreference("player.pipEnabled", "Picture-in-picture", pipEnabled),
            switchPreference("player.pipEpisodeToasts", "PiP episode toasts", pipEpisodeToasts, enabled = pipEnabled),
            switchPreference("player.pipOnExit", "Enter PiP on exit", pipOnExit, enabled = pipEnabled),
            switchPreference(
                "player.pipReplaceWithPrevious",
                "Replace PiP with previous player",
                pipReplaceWithPrevious,
                enabled = pipEnabled,
            ),
            switchPreference("player.castEnabled", "Cast controls", castEnabled),
        )
    }
}

private fun ChimahonPlayerSettings.toAnimePlayerGestureSection(): ChimahonSettingsSection {
    return section("animePlayer.gestures", "Player gestures") {
        listOf(
            switchPreference(
                "player.gestures.volumeBrightnessGestures",
                "Volume and brightness gestures",
                gestures.volumeBrightnessGestures,
            ),
            switchPreference(
                "player.gestures.swapVolumeAndBrightness",
                "Swap volume and brightness sides",
                gestures.swapVolumeAndBrightness,
                enabled = gestures.volumeBrightnessGestures,
            ),
            switchPreference("player.gestures.horizontalSeekGesture", "Horizontal seek gesture", gestures.horizontalSeekGesture),
            switchPreference("player.gestures.showSeekBar", "Show seek bar while seeking", gestures.showSeekBar),
            switchPreference("player.gestures.smoothSeek", "Smooth seek", gestures.smoothSeek),
            sliderPreference(
                "player.gestures.defaultIntroLengthSeconds",
                "Default intro length",
                gestures.defaultIntroLengthSeconds.toFloat(),
                0f..180f,
                35,
                "${gestures.defaultIntroLengthSeconds}s",
            ),
            sliderPreference(
                "player.gestures.skipLengthSeconds",
                "Seek step",
                gestures.skipLengthSeconds.toFloat(),
                5f..90f,
                16,
                "${gestures.skipLengthSeconds}s",
            ),
            listPreference(
                "player.gestures.leftDoubleTap",
                "Left double tap",
                gestures.leftDoubleTap.name,
                ChimahonPlayerGestureAction.entries.options { it.title },
            ),
            listPreference(
                "player.gestures.centerDoubleTap",
                "Center double tap",
                gestures.centerDoubleTap.name,
                ChimahonPlayerGestureAction.entries.options { it.title },
            ),
            listPreference(
                "player.gestures.rightDoubleTap",
                "Right double tap",
                gestures.rightDoubleTap.name,
                ChimahonPlayerGestureAction.entries.options { it.title },
            ),
            listPreference(
                "player.gestures.mediaPrevious",
                "Media previous",
                gestures.mediaPrevious.name,
                ChimahonPlayerGestureAction.entries.options { it.title },
            ),
            listPreference(
                "player.gestures.mediaPlayPause",
                "Media play/pause",
                gestures.mediaPlayPause.name,
                ChimahonPlayerGestureAction.entries.options { it.title },
            ),
            listPreference(
                "player.gestures.mediaNext",
                "Media next",
                gestures.mediaNext.name,
                ChimahonPlayerGestureAction.entries.options { it.title },
            ),
        )
    }
}

private fun ChimahonPlayerSettings.toAnimeExternalSelectionSection(): ChimahonSettingsSection {
    return section("animePlayer.external", "External player and selection") {
        listOf(
            switchPreference("player.alwaysUseExternalPlayer", "Always use external player", alwaysUseExternalPlayer),
            infoPreference(
                key = "player.externalPlayerPackage",
                title = "External player package",
                subtitle = externalPlayerPackage.blankSettingsValue("No package selected"),
            ),
            switchPreference("player.selections.rememberQuality", "Remember quality", selections.rememberQuality),
            switchPreference(
                "player.selections.rememberSubtitleTracks",
                "Remember subtitle tracks",
                selections.rememberSubtitleTracks,
            ),
            switchPreference(
                "player.selections.restoreAddedSubtitleTracks",
                "Restore added subtitle tracks",
                selections.restoreAddedSubtitleTracks,
                enabled = selections.rememberSubtitleTracks,
            ),
            infoPreference(
                "player.selections.preferredHosterKey",
                "Preferred hoster",
                selections.preferredHosterKey.blankSettingsValue("No preferred hoster"),
            ),
            infoPreference(
                "player.selections.preferredVideoKey",
                "Preferred video",
                selections.preferredVideoKey.blankSettingsValue("No preferred video"),
            ),
            infoPreference(
                "player.selections.primarySubtitleKey",
                "Primary subtitle",
                selections.primarySubtitleKey.blankSettingsValue("No primary subtitle"),
            ),
            infoPreference(
                "player.selections.secondarySubtitleKey",
                "Secondary subtitle",
                selections.secondarySubtitleKey.blankSettingsValue("No secondary subtitle"),
            ),
        )
    }
}

private fun ChimahonPlayerSettings.toAnimeSubtitleSection(): ChimahonSettingsSection {
    return section("animePlayer.subtitles", "Subtitles") {
        listOf(
            listPreference(
                "player.subtitles.listMode",
                "Subtitle list mode",
                subtitles.listMode.name,
                ChimahonSubtitleListMode.entries.options { it.title },
            ),
            infoPreference(
                "player.subtitles.font",
                "Subtitle font",
                subtitles.font.blankSettingsValue("Sans Serif"),
            ),
            sliderPreference(
                "player.subtitles.fontSize",
                "Subtitle font size",
                subtitles.fontSize.toFloat(),
                20f..100f,
                79,
                subtitles.fontSize.toString(),
            ),
            sliderPreference(
                "player.subtitles.fontScale",
                "Subtitle font scale",
                subtitles.fontScale.toFloat(),
                0.5f..2f,
                29,
                "${(subtitles.fontScale * 100.0).toInt()}%",
            ),
            sliderPreference(
                "player.subtitles.borderSize",
                "Subtitle border size",
                subtitles.borderSize.toFloat(),
                0f..12f,
                11,
                subtitles.borderSize.toString(),
            ),
            listPreference(
                "player.subtitles.borderStyle",
                "Subtitle border style",
                subtitles.borderStyle.name,
                ChimahonSubtitleBorderStyle.entries.options { it.title },
            ),
            sliderPreference(
                "player.subtitles.shadowOffset",
                "Subtitle shadow offset",
                subtitles.shadowOffset.toFloat(),
                0f..12f,
                11,
                subtitles.shadowOffset.toString(),
            ),
            sliderPreference(
                "player.subtitles.positionPercent",
                "Subtitle position",
                subtitles.positionPercent.toFloat(),
                0f..100f,
                19,
                "${subtitles.positionPercent}%",
            ),
            listPreference(
                "player.subtitles.justification",
                "Subtitle alignment",
                subtitles.justification.name,
                ChimahonSubtitleJustification.entries.options { it.title },
            ),
            switchPreference("player.subtitles.bold", "Bold subtitles", subtitles.bold),
            switchPreference("player.subtitles.italic", "Italic subtitles", subtitles.italic),
            switchPreference("player.subtitles.overrideAss", "Override ASS subtitles", subtitles.overrideAss),
            switchPreference("player.subtitles.screenshotSubtitles", "Screenshot subtitles", subtitles.screenshotSubtitles),
            sliderPreference(
                "player.subtitles.delayMillis",
                "Subtitle delay",
                subtitles.delayMillis.toFloat(),
                -5000f..5000f,
                39,
                "${subtitles.delayMillis}ms",
            ),
            sliderPreference(
                "player.subtitles.speed",
                "Subtitle speed",
                subtitles.speed.toFloat(),
                0.5f..2f,
                29,
                "${subtitles.speed.shortNumber()}x",
            ),
            sliderPreference(
                "player.subtitles.secondaryDelayMillis",
                "Secondary subtitle delay",
                subtitles.secondaryDelayMillis.toFloat(),
                -5000f..5000f,
                39,
                "${subtitles.secondaryDelayMillis}ms",
            ),
            infoPreference(
                "player.subtitles.preferredLanguages",
                "Preferred subtitle languages",
                subtitles.preferredLanguages.blankSettingsValue("No preferred languages"),
            ),
            infoPreference(
                "player.subtitles.whitelist",
                "Subtitle whitelist",
                subtitles.whitelist.blankSettingsValue("No preferred track names"),
            ),
            infoPreference(
                "player.subtitles.blacklist",
                "Subtitle blacklist",
                subtitles.blacklist.blankSettingsValue("No blocked track names"),
            ),
            switchPreference(
                "player.subtitles.regexRemoveSpeakerNames",
                "Remove speaker names",
                subtitles.regexRemoveSpeakerNames,
            ),
            switchPreference("player.subtitles.regexMergeMultiline", "Merge multiline captions", subtitles.regexMergeMultiline),
            switchPreference(
                "player.subtitles.regexRemoveBracketedText",
                "Remove bracketed text",
                subtitles.regexRemoveBracketedText,
            ),
            switchPreference(
                "player.subtitles.regexRemoveUppercaseLines",
                "Remove uppercase lines",
                subtitles.regexRemoveUppercaseLines,
            ),
            switchPreference("player.subtitles.regexRemoveMusicSymbols", "Remove music symbols", subtitles.regexRemoveMusicSymbols),
            switchPreference(
                "player.subtitles.regexRemoveCurlyBracedText",
                "Remove braced text",
                subtitles.regexRemoveCurlyBracedText,
            ),
            switchPreference("player.subtitles.regexCustomEnabled", "Custom regex cleanup", subtitles.regexCustomEnabled),
            infoPreference(
                "player.subtitles.regexCustomPattern",
                "Custom regex pattern",
                subtitles.regexCustomPattern.blankSettingsValue("No custom pattern"),
            ),
            infoPreference(
                "player.subtitles.jimakuTitle",
                "Jimaku title",
                subtitles.jimakuTitle.blankSettingsValue("Use anime title"),
            ),
            infoPreference(
                "player.subtitles.jimakuApiKey",
                "Jimaku API key",
                subtitles.jimakuApiKey.maskedSettingsValue("No API key"),
            ),
        )
    }
}

private fun ChimahonPlayerSettings.toAnimeAudioDecoderSection(): ChimahonSettingsSection {
    return section("animePlayer.audioDecoder", "Audio and decoder") {
        listOf(
            infoPreference(
                "player.audio.preferredLanguages",
                "Preferred audio languages",
                audio.preferredLanguages.blankSettingsValue("No preferred languages"),
            ),
            switchPreference("player.audio.pitchCorrection", "Pitch correction", audio.pitchCorrection),
            listPreference(
                "player.audio.channels",
                "Audio channels",
                audio.channels.name,
                ChimahonAudioChannels.entries.options { it.title },
            ),
            sliderPreference(
                "player.audio.volumeBoostCap",
                "Volume boost cap",
                audio.volumeBoostCap.toFloat(),
                0f..200f,
                39,
                "${audio.volumeBoostCap}%",
            ),
            sliderPreference(
                "player.audio.delayMillis",
                "Audio delay",
                audio.delayMillis.toFloat(),
                -5000f..5000f,
                39,
                "${audio.delayMillis}ms",
            ),
            switchPreference("player.decoder.tryHardwareDecoding", "Try hardware decoding", decoder.tryHardwareDecoding),
            switchPreference("player.decoder.gpuNext", "GPU Next", decoder.gpuNext),
            listPreference(
                "player.decoder.debanding",
                "Debanding",
                decoder.debanding.name,
                ChimahonPlayerDebanding.entries.options { it.title },
            ),
            switchPreference("player.decoder.useYuv420p", "Use YUV420P", decoder.useYuv420p),
            sliderPreference(
                "player.decoder.brightnessFilter",
                "Brightness filter",
                decoder.brightnessFilter.toFloat(),
                -100f..100f,
                39,
                decoder.brightnessFilter.toString(),
            ),
            sliderPreference(
                "player.decoder.saturationFilter",
                "Saturation filter",
                decoder.saturationFilter.toFloat(),
                -100f..100f,
                39,
                decoder.saturationFilter.toString(),
            ),
            sliderPreference(
                "player.decoder.contrastFilter",
                "Contrast filter",
                decoder.contrastFilter.toFloat(),
                -100f..100f,
                39,
                decoder.contrastFilter.toString(),
            ),
            sliderPreference(
                "player.decoder.gammaFilter",
                "Gamma filter",
                decoder.gammaFilter.toFloat(),
                -100f..100f,
                39,
                decoder.gammaFilter.toString(),
            ),
            sliderPreference(
                "player.decoder.hueFilter",
                "Hue filter",
                decoder.hueFilter.toFloat(),
                -100f..100f,
                39,
                decoder.hueFilter.toString(),
            ),
            switchPreference("player.advanced.mpvScriptsEnabled", "MPV scripts", advanced.mpvScriptsEnabled),
            infoPreference(
                "player.advanced.statisticsPage",
                "Statistics page",
                advanced.statisticsPage.toString(),
            ),
        )
    }
}

private fun ChimahonTrackingSettings.toAnimeTrackingSection(): ChimahonSettingsSection {
    return section("animePlayer.tracking", "History and tracking sync") {
        listOf(
            switchPreference("tracking.trackOnAddToLibrary", "Track when adding to library", trackOnAddToLibrary),
            switchPreference("tracking.autoSyncEnabled", "Automatic tracking sync", autoSyncEnabled),
            sliderPreference(
                "tracking.updateIntervalHours",
                "Sync interval",
                updateIntervalHours.toFloat(),
                1f..168f,
                166,
                "${updateIntervalHours}h",
                enabled = autoSyncEnabled,
            ),
            switchPreference(
                "tracking.syncLibraryEntriesOnly",
                "Sync library entries only",
                syncLibraryEntriesOnly,
                enabled = autoSyncEnabled,
            ),
            listPreference(
                "tracking.autoUpdateOnMarkRead",
                "Update tracker on seen",
                autoUpdateOnMarkRead.name,
                ChimahonAutoTrackOnMarkRead.entries.options { it.title },
            ),
            switchPreference(
                "tracking.autoSyncProgressFromTrackers",
                "Pull progress from trackers",
                autoSyncProgressFromTrackers,
                enabled = autoSyncEnabled,
            ),
            switchPreference(
                "tracking.resolveUsingSourceMetadata",
                "Resolve using source metadata",
                resolveUsingSourceMetadata,
                enabled = autoSyncEnabled,
            ),
            multiPreference(
                "tracking.syncRestrictions",
                "Sync restrictions",
                syncRestrictions,
                commonRestrictionOptions(),
                enabled = autoSyncEnabled,
            ),
            multiPreference(
                "tracking.syncIncludedCategories",
                "Included anime categories",
                syncIncludedCategories,
                animeCategoryPlaceholderOptions(),
                enabled = autoSyncEnabled,
            ),
            multiPreference(
                "tracking.syncExcludedCategories",
                "Excluded anime categories",
                syncExcludedCategories,
                animeCategoryPlaceholderOptions(),
                enabled = autoSyncEnabled,
            ),
        )
    }
}

fun ChimahonReaderSettings.toReaderSettingsScreen(): ChimahonSettingsScreen {
    return ChimahonSettingsScreen(
        route = ChimahonSettingsRoute.Reader,
        sections = listOf(
            section("reader.display", "Display") {
                listOf(
                    listPreference("reader.mode", "Reading mode", mode.name, ChimahonReaderMode.entries.options { it.name.readableEnumLabel() }),
                    listPreference("reader.scale", "Scale type", scale.name, ChimahonReaderScale.entries.options { it.name.readableEnumLabel() }),
                    listPreference("reader.canvas", "Background color", canvas.name, ChimahonReaderCanvas.entries.options { it.name.readableEnumLabel() }),
                    switchPreference("reader.pureBlackBackground", "Pure black background", pureBlackBackground),
                    listPreference("reader.textTheme", "Novel text theme", textTheme.name, ChimahonReaderTextTheme.entries.options { it.title }),
                    listPreference("reader.readerUiTheme", "Reader UI theme", readerUiTheme.name, ChimahonReaderTextTheme.entries.options { it.title }),
                    switchPreference("reader.systemLightSepia", "Use sepia in system light theme", systemLightSepia),
                    listPreference("reader.orientation", "Orientation", orientation.name, ChimahonReaderOrientation.entries.options { it.name.readableEnumLabel() }),
                    switchPreference("reader.fullscreen", "Fullscreen", fullscreen),
                    switchPreference("reader.keepScreenOn", "Keep screen on", keepScreenOn),
                    switchPreference("reader.drawUnderCutout", "Draw under display cutout", drawUnderCutout),
                )
            },
            section("reader.paged", "Paged reader") {
                listOf(
                    listPreference("reader.dualPageMode", "Dual page mode", dualPageMode.name, ChimahonDualPageMode.entries.options { it.name.readableEnumLabel() }),
                    switchPreference("reader.splitWidePages", "Split wide pages", splitWidePages),
                    switchPreference("reader.rotateWidePagesToFit", "Rotate wide pages to fit", rotateWidePagesToFit),
                    switchPreference("reader.invertWidePageRotation", "Invert wide page rotation", invertWidePageRotation),
                    switchPreference("reader.invertDoublePages", "Invert double pages", invertDoublePages),
                    sliderPreference("reader.centerMarginDp", "Center margin", centerMarginDp.toFloat(), 0f..64f, 15, "${centerMarginDp}dp"),
                    listPreference("reader.pagedZoomStart", "Zoom start position", pagedZoomStart.name, ChimahonReaderZoomStart.entries.options { it.title }),
                    switchPreference("reader.landscapeZoom", "Landscape zoom", landscapeZoom),
                    listPreference("reader.landscapeZoomType", "Landscape zoom type", landscapeZoomType.name, ChimahonReaderLandscapeZoomType.entries.options { it.title }),
                    switchPreference("reader.pagedDisableZoomIn", "Disable zoom in", pagedDisableZoomIn),
                )
            },
            section("reader.webtoon", "Webtoon reader") {
                listOf(
                    listPreference("reader.webtoonScaleType", "Webtoon scale", webtoonScaleType.name, ChimahonWebtoonScaleType.entries.options { it.title }),
                    switchPreference("reader.smartLongStripGapScale", "Smart long-strip gap scaling", smartLongStripGapScale),
                    sliderPreference("reader.webtoonSidePaddingPercent", "Side padding", webtoonSidePaddingPercent.toFloat(), 0f..50f, 9, "$webtoonSidePaddingPercent%"),
                    listPreference("reader.webtoonReaderHideThreshold", "Hide controls threshold", webtoonReaderHideThreshold.name, ChimahonReaderHideThreshold.entries.options { it.title }),
                    switchPreference("reader.webtoonPinchToZoom", "Pinch to zoom", webtoonPinchToZoom),
                    switchPreference("reader.webtoonDisableZoomOut", "Disable zoom out", webtoonDisableZoomOut),
                    switchPreference("reader.continuousVerticalTappingByPage", "Tap by page in vertical mode", continuousVerticalTappingByPage),
                    switchPreference("reader.rotateWidePagesToFitWebtoon", "Rotate wide pages in webtoon", rotateWidePagesToFitWebtoon),
                    switchPreference("reader.invertWidePageRotationWebtoon", "Invert webtoon wide page rotation", invertWidePageRotationWebtoon),
                )
            },
            section("reader.controls", "Controls") {
                listOf(
                    listPreference("reader.navigationMode", "Navigation mode", navigationMode.name, ChimahonReaderNavigationMode.entries.options { it.name.readableEnumLabel() }),
                    switchPreference("reader.showPageStrip", "Show page strip", showPageStrip),
                    switchPreference("reader.forceHorizontalSeekbar", "Force horizontal seekbar", forceHorizontalSeekbar),
                    switchPreference("reader.landscapeVerticalSeekbar", "Vertical seekbar in landscape", landscapeVerticalSeekbar),
                    switchPreference("reader.leftVerticalSeekbar", "Left vertical seekbar", leftVerticalSeekbar),
                    switchPreference("reader.keepControlsVisible", "Keep controls visible", keepControlsVisible),
                    switchPreference("reader.tapZonesEnabled", "Tap zones", tapZonesEnabled),
                    listPreference("reader.tapNavigationLayout", "Tap navigation layout", tapNavigationLayout.name, ChimahonReaderTapNavigationLayout.entries.options { it.title }),
                    sliderPreference("reader.tapZonePercent", "Tap zone size", tapZonePercent.toFloat(), 8f..40f, 31, "$tapZonePercent%"),
                    switchPreference("reader.smallerTapZones", "Smaller tap zones", smallerTapZones),
                    listPreference("reader.invertTapZones", "Invert tap zones", invertTapZones.name, ChimahonTapZoneInvert.entries.options { it.name }),
                    switchPreference("reader.swipeNavigationEnabled", "Swipe navigation", swipeNavigationEnabled),
                    switchPreference("reader.doubleTapToZoom", "Double tap to zoom", doubleTapToZoom),
                    sliderPreference("reader.doubleTapAnimationSpeedMillis", "Double tap animation speed", doubleTapAnimationSpeedMillis.toFloat(), 0f..1000f, 19, "${doubleTapAnimationSpeedMillis}ms"),
                    switchPreference("reader.longTapEnabled", "Long tap menu", longTapEnabled),
                    switchPreference("reader.readWithLongTap", "Mark read with long tap", readWithLongTap),
                    multiPreference("reader.bottomButtons", "Bottom action buttons", bottomButtons, readerBottomButtonOptions()),
                )
            },
            section("reader.desktop", "Desktop controls") {
                listOf(
                    switchPreference("reader.keyboardShortcutsEnabled", "Keyboard shortcuts", keyboardShortcutsEnabled),
                    listPreference("reader.keyboardScheme", "Keyboard scheme", keyboardScheme.name, ChimahonReaderKeyboardScheme.entries.options { it.title }),
                    listPreference("reader.mouseWheelAction", "Mouse wheel action", mouseWheelAction.name, ChimahonReaderMouseWheelAction.entries.options { it.title }),
                    sliderPreference("reader.mouseWheelSensitivityPercent", "Mouse wheel sensitivity", mouseWheelSensitivityPercent.toFloat(), 25f..250f, 44, "$mouseWheelSensitivityPercent%"),
                    switchPreference("reader.invertMouseWheel", "Invert mouse wheel", invertMouseWheel),
                    switchPreference("reader.trackpadGesturesEnabled", "Trackpad gestures", trackpadGesturesEnabled),
                    switchPreference("reader.desktopTapNavigation", "Mouse tap navigation", desktopTapNavigation),
                    switchPreference("reader.desktopPageButtons", "Desktop page buttons", desktopPageButtons),
                    switchPreference("reader.desktopReaderMenu", "Desktop reader menu", desktopReaderMenu),
                    switchPreference("reader.hideCursorWhileReading", "Hide cursor while reading", hideCursorWhileReading),
                    sliderPreference("reader.cursorHideDelayMillis", "Cursor hide delay", cursorHideDelayMillis.toFloat(), 300f..5000f, 46, "${cursorHideDelayMillis}ms"),
                )
            },
            section("reader.image", "Image and color") {
                listOf(
                    switchPreference("reader.customBrightnessEnabled", "Custom brightness", customBrightnessEnabled),
                    sliderPreference("reader.customBrightnessValue", "Custom brightness value", customBrightnessValue.toFloat(), -75f..75f, 29, customBrightnessValue.toString(), customBrightnessEnabled),
                    switchPreference("reader.colorFilterEnabled", "Color filter", colorFilterEnabled),
                    sliderPreference("reader.colorFilterValue", "Color filter value", colorFilterValue.toFloat(), -100f..100f, 39, colorFilterValue.toString(), colorFilterEnabled),
                    listPreference("reader.colorFilterMode", "Color filter blend mode", colorFilterMode.name, ChimahonReaderColorFilterMode.entries.options { it.title }, enabled = colorFilterEnabled),
                    switchPreference("reader.grayscale", "Grayscale", grayscale),
                    switchPreference("reader.invertColors", "Invert colors", invertColors),
                    switchPreference("reader.cropBorders", "Crop borders", cropBorders),
                    switchPreference("reader.pageTransitions", "Page transitions", pageTransitions),
                    switchPreference("reader.flashOnPageChange", "Flash on page change", flashOnPageChange),
                    sliderPreference("reader.flashDurationMillis", "Flash duration", flashDurationMillis.toFloat(), 100f..2000f, 18, "${flashDurationMillis}ms", flashOnPageChange),
                    sliderPreference("reader.flashPageInterval", "Flash page interval", flashPageInterval.toFloat(), 1f..12f, 10, flashPageInterval.toString(), flashOnPageChange),
                    listPreference("reader.flashColor", "Flash color", flashColor.name, ChimahonReaderFlashColor.entries.options { it.name.readableEnumLabel() }, enabled = flashOnPageChange),
                )
            },
            section("reader.novel", "Novel reader") {
                listOf(
                    switchPreference("reader.verticalWriting", "Vertical writing", verticalWriting),
                    switchPreference("reader.continuousMode", "Continuous novel mode", continuousMode),
                    sliderPreference("reader.fontSize", "Font size", fontSize.toFloat(), 12f..72f, 119, "${fontSize.shortNumber()}px"),
                    sliderPreference("reader.lineHeight", "Line height", lineHeight.toFloat(), 1.0f..2.5f, 29, lineHeight.shortNumber()),
                    sliderPreference("reader.horizontalPadding", "Horizontal padding", horizontalPadding.toFloat(), 0f..50f, 99, "${horizontalPadding.shortNumber()}%"),
                    sliderPreference("reader.verticalPadding", "Vertical padding", verticalPadding.toFloat(), 0f..50f, 99, "${verticalPadding.shortNumber()}%"),
                    switchPreference("reader.avoidPageBreak", "Avoid page breaks", avoidPageBreak),
                    switchPreference("reader.justifyText", "Justify text", justifyText),
                    switchPreference("reader.layoutAdvanced", "Advanced layout controls", layoutAdvanced),
                    sliderPreference("reader.characterSpacing", "Character spacing", characterSpacing.toFloat(), 0f..12f, 23, characterSpacing.shortNumber(), layoutAdvanced),
                    sliderPreference("reader.paragraphSpacing", "Paragraph spacing", paragraphSpacing.toFloat(), 0f..24f, 47, paragraphSpacing.shortNumber(), layoutAdvanced),
                    switchPreference("reader.hideFurigana", "Hide furigana", hideFurigana),
                )
            },
            section("reader.lookup", "OCR and lookup") {
                listOf(
                    switchPreference("reader.ocrOutlineVisible", "Show OCR boxes", ocrOutlineVisible),
                    switchPreference("reader.ocrAutoOnDownload", "Run OCR after downloads", ocrAutoOnDownload),
                    sliderPreference("reader.lookupPopupWidth", "Lookup popup width", lookupPopupWidth.toFloat(), 220f..720f, 49, "${lookupPopupWidth}px"),
                    sliderPreference("reader.lookupPopupHeight", "Lookup popup height", lookupPopupHeight.toFloat(), 160f..720f, 55, "${lookupPopupHeight}px"),
                    switchPreference("reader.lookupPopupFullWidth", "Full-width lookup popup", lookupPopupFullWidth),
                    switchPreference("reader.lookupPopupSwipeToDismiss", "Swipe to dismiss lookup", lookupPopupSwipeToDismiss),
                    sliderPreference("reader.lookupPopupSwipeThreshold", "Lookup swipe threshold", lookupPopupSwipeThreshold.toFloat(), 20f..160f, 13, "${lookupPopupSwipeThreshold}px", lookupPopupSwipeToDismiss),
                    sliderPreference("reader.lookupMaxResults", "Maximum lookup results", lookupMaxResults.toFloat(), 1f..30f, 28, lookupMaxResults.toString()),
                    sliderPreference("reader.lookupScanLength", "Lookup scan length", lookupScanLength.toFloat(), 5f..120f, 22, lookupScanLength.toString()),
                )
            },
            section("reader.performance", "Performance and statistics") {
                listOf(
                    sliderPreference("reader.preloadSize", "Preload pages", preloadSize.toFloat(), 0f..24f, 23, preloadSize.toString()),
                    sliderPreference("reader.readerThreads", "Reader threads", readerThreads.toFloat(), 1f..8f, 6, readerThreads.toString()),
                    sliderPreference("reader.readerCacheSizeMb", "Reader cache size", readerCacheSizeMb.toFloat(), 64f..1024f, 14, "${readerCacheSizeMb}MB"),
                    switchPreference("reader.aggressivePageLoading", "Aggressive page loading", aggressivePageLoading),
                    switchPreference("reader.preserveReadingPosition", "Preserve reading position", preserveReadingPosition),
                    switchPreference("reader.useAutoWebtoon", "Use auto webtoon detection", useAutoWebtoon),
                    switchPreference("reader.statisticsEnabled", "Reading statistics", statisticsEnabled),
                    listPreference("reader.statisticsAutostartMode", "Statistics autostart", statisticsAutostartMode.name, ChimahonReaderStatisticsAutostartMode.entries.options { it.title }, enabled = statisticsEnabled),
                    switchPreference("reader.showReadingSpeed", "Show reading speed", showReadingSpeed, statisticsEnabled),
                    switchPreference("reader.showReadingTime", "Show reading time", showReadingTime, statisticsEnabled),
                )
            },
        ),
    )
}

fun ChimahonDownloadPreferences.toDownloadSettingsScreen(): ChimahonSettingsScreen {
    return ChimahonSettingsScreen(
        route = ChimahonSettingsRoute.Downloads,
        sections = listOf(
            section("downloads.network", "Network and power") {
                listOf(
                    switchPreference("downloads.wifiOnly", "Wi-Fi only", wifiOnly),
                    switchPreference("downloads.pauseWhenMetered", "Pause on metered networks", pauseWhenMetered),
                    switchPreference("downloads.requireCharging", "Require charging", requireCharging),
                    switchPreference("downloads.pauseWhenBatteryLow", "Pause when battery is low", pauseWhenBatteryLow),
                )
            },
            section("downloads.storage", "Storage") {
                listOf(
                    listPreference("downloads.storageLocation", "Storage location", storageLocation.name, ChimahonDownloadStorageLocation.entries.options { it.title }),
                    infoPreference("downloads.customDirectory", "Custom directory", customDownloadDirectory.ifBlank { "Not selected" }),
                    switchPreference("downloads.saveAsCbz", "Save as CBZ", saveAsCbz),
                    switchPreference("downloads.splitTallImages", "Split tall images", splitTallImages),
                    switchPreference("downloads.createNoMediaFile", "Create .nomedia file", createNoMediaFile),
                    switchPreference("downloads.saveSourceMetadata", "Save source metadata", saveSourceMetadata),
                    switchPreference("downloads.useCategorySubfolders", "Use category subfolders", useCategorySubfolders),
                    switchPreference("downloads.includeChapterUrlHash", "Include chapter URL hash", includeChapterUrlHash),
                    switchPreference("downloads.deleteEmptyFolders", "Delete empty folders", deleteEmptyFolders),
                )
            },
            section("downloads.cleanup", "Cleanup") {
                listOf(
                    listPreference("downloads.cleanupPolicy", "Cleanup policy", cleanupPolicy.name, ChimahonDownloadCleanupPolicy.entries.options { it.title }),
                    sliderPreference("downloads.removeAfterReadSlots", "Remove after read slots", removeAfterReadSlots.toFloat(), -1f..20f, 20, if (removeAfterReadSlots < 0) "Disabled" else removeAfterReadSlots.toString()),
                    switchPreference("downloads.removeAfterMarkedRead", "Remove after marked read", removeAfterMarkedRead),
                    switchPreference("downloads.removeBookmarkedChapters", "Remove bookmarked chapters", removeBookmarkedChapters),
                    multiPreference("downloads.removeExcludedCategories", "Excluded cleanup categories", removeExcludedCategories, categoryPlaceholderOptions()),
                )
            },
            section("downloads.automation", "Automation") {
                listOf(
                    sliderPreference("downloads.autoDownloadWhileReadingCount", "Download ahead while reading", autoDownloadWhileReadingCount.toFloat(), 0f..10f, 9, autoDownloadWhileReadingCount.toString()),
                    switchPreference("downloads.downloadNewChapters", "Download new chapters", downloadNewChapters),
                    switchPreference("downloads.downloadNewUnreadOnly", "Only unread new chapters", downloadNewUnreadOnly, downloadNewChapters),
                    multiPreference("downloads.downloadNewIncludedCategories", "Included categories", downloadNewIncludedCategories, categoryPlaceholderOptions(), enabled = downloadNewChapters),
                    multiPreference("downloads.downloadNewExcludedCategories", "Excluded categories", downloadNewExcludedCategories, categoryPlaceholderOptions(), enabled = downloadNewChapters),
                    listPreference("downloads.downloadOrder", "Download order", downloadOrder.name, ChimahonDownloadOrder.entries.options { it.title }),
                )
            },
            section("downloads.queue", "Queue and retries") {
                listOf(
                    sliderPreference("downloads.parallelSourceDownloads", "Parallel source downloads", parallelSourceDownloads.toFloat(), 1f..12f, 10, parallelSourceDownloads.toString()),
                    sliderPreference("downloads.parallelPageDownloads", "Parallel page downloads", parallelPageDownloads.toFloat(), 1f..12f, 10, parallelPageDownloads.toString()),
                    sliderPreference("downloads.maxRetryCount", "Retry count", maxRetryCount.toFloat(), 0f..10f, 9, maxRetryCount.toString()),
                    switchPreference("downloads.retryFailedDownloads", "Retry failed downloads", retryFailedDownloads),
                    switchPreference("downloads.preserveQueueOnExit", "Preserve queue on exit", preserveQueueOnExit),
                    sliderPreference("downloads.downloadCacheRenewIntervalHours", "Cache renewal interval", downloadCacheRenewIntervalHours.toFloat(), 1f..72f, 70, "${downloadCacheRenewIntervalHours}h"),
                )
            },
        ),
    )
}

fun ChimahonTrackingSettings.toTrackingSettingsScreen(): ChimahonSettingsScreen {
    return ChimahonSettingsScreen(
        route = ChimahonSettingsRoute.Tracking,
        sections = listOf(
            section("tracking.general", "Tracking") {
                listOf(
                    switchPreference("tracking.trackOnAddToLibrary", "Track when adding to library", trackOnAddToLibrary),
                    switchPreference("tracking.autoSyncEnabled", "Automatic sync", autoSyncEnabled),
                    sliderPreference("tracking.updateIntervalHours", "Sync interval", updateIntervalHours.toFloat(), 1f..168f, 166, "${updateIntervalHours}h", autoSyncEnabled),
                    listPreference("tracking.autoUpdateOnMarkRead", "Update when marked read", autoUpdateOnMarkRead.name, ChimahonAutoTrackOnMarkRead.entries.options { it.title }),
                    switchPreference("tracking.syncLibraryEntriesOnly", "Sync library entries only", syncLibraryEntriesOnly),
                    switchPreference("tracking.autoSyncProgressFromTrackers", "Pull progress from trackers", autoSyncProgressFromTrackers),
                    switchPreference("tracking.resolveUsingSourceMetadata", "Resolve using source metadata", resolveUsingSourceMetadata),
                )
            },
            section("tracking.restrictions", "Sync restrictions") {
                listOf(
                    multiPreference("tracking.syncRestrictions", "Sync restrictions", syncRestrictions, commonRestrictionOptions()),
                    multiPreference("tracking.syncIncludedCategories", "Included categories", syncIncludedCategories, categoryPlaceholderOptions()),
                    multiPreference("tracking.syncExcludedCategories", "Excluded categories", syncExcludedCategories, categoryPlaceholderOptions()),
                )
            },
        ),
    )
}

fun ChimahonBrowseSettings.toBrowseSettingsScreen(): ChimahonSettingsScreen {
    return ChimahonSettingsScreen(
        route = ChimahonSettingsRoute.Browse,
        sections = listOf(
            section("browse.sources", "Sources") {
                listOf(
                    switchPreference("browse.showNsfwSources", "Show NSFW sources", showNsfwSources),
                    switchPreference("browse.hideLibraryEntries", "Hide entries already in library", hideLibraryEntries),
                    switchPreference("browse.autoLoadMore", "Auto-load more results", autoLoadMore),
                    listPreference("browse.defaultBrowseTab", "Default browse tab", defaultBrowseTab.name, ChimahonBrowseTab.entries.options { it.title }),
                    listPreference("browse.sourceDisplayMode", "Source display mode", sourceDisplayMode.name, ChimahonBrowseSourceDisplayMode.entries.options { it.name.readableEnumLabel() }),
                    listPreference("browse.sourceFilterMode", "Source filter", sourceFilterMode.name, ChimahonBrowseSourceFilterMode.entries.options { it.title }),
                    listPreference("browse.installedSourceOrder", "Installed source order", installedSourceOrder.name, ChimahonInstalledSourceOrder.entries.options { it.title }),
                    switchPreference("browse.groupSourcesByLanguage", "Group sources by language", groupSourcesByLanguage),
                    switchPreference("browse.showSourceLanguage", "Show source language", showSourceLanguage),
                    switchPreference("browse.showSourcePinBadges", "Show pinned badges", showSourcePinBadges),
                    switchPreference("browse.showSourceLastUsed", "Show last used", showSourceLastUsed),
                    switchPreference("browse.rememberSourceFilters", "Remember source filters", rememberSourceFilters),
                )
            },
            section("browse.extensions", "Extensions") {
                listOf(
                    listPreference("browse.extensionFilterMode", "Extension filter", extensionFilterMode.name, ChimahonExtensionFilterMode.entries.options { it.title }),
                    listPreference("browse.extensionSortMode", "Extension sort", extensionSortMode.name, ChimahonExtensionSortMode.entries.options { it.title }),
                    switchPreference("browse.extensionUpdateNotificationsEnabled", "Extension update notifications", extensionUpdateNotificationsEnabled),
                    switchPreference("browse.checkExtensionUpdatesOnStart", "Check extension updates on start", checkExtensionUpdatesOnStart),
                    switchPreference("browse.autoUpdateExtensionRepos", "Auto-update repositories", autoUpdateExtensionRepos),
                    switchPreference("browse.hideObsoleteExtensions", "Hide obsolete extensions", hideObsoleteExtensions),
                    switchPreference("browse.hideUnavailableSources", "Hide unavailable sources", hideUnavailableSources),
                    infoPreference("browse.disabledExtensionRepoUrls", "Disabled repositories", disabledExtensionRepoUrls.size.toString()),
                )
            },
            section("browse.globalSearch", "Global search and related") {
                listOf(
                    switchPreference("browse.globalSearchPinnedOnly", "Search pinned sources only", globalSearchPinnedOnly),
                    switchPreference("browse.includePinnedSourcesInGlobalSearch", "Include pinned sources in global search", includePinnedSourcesInGlobalSearch),
                    switchPreference("browse.relatedMangaRecommendations", "Related manga recommendations", relatedMangaRecommendations),
                    switchPreference("browse.expandRelatedMangaSections", "Expand related manga sections", expandRelatedMangaSections),
                    switchPreference("browse.relatedMangaInOverflow", "Related manga in overflow", relatedMangaInOverflow),
                    switchPreference("browse.showHomeInRelatedManga", "Show home in related manga", showHomeInRelatedManga),
                    switchPreference("browse.sourceCategoriesFilter", "Source categories filter", sourceCategoriesFilter),
                    switchPreference("browse.useNewSourceNavigation", "Use new source navigation", useNewSourceNavigation),
                    switchPreference("browse.allowLocalSourceHiddenFolders", "Allow hidden local folders", allowLocalSourceHiddenFolders),
                )
            },
            section("browse.feed", "Feed") {
                listOf(
                    switchPreference("browse.hideFeedTab", "Hide feed tab", hideFeedTab),
                    switchPreference("browse.feedTabInFront", "Put feed tab first", feedTabInFront),
                    switchPreference("browse.hideLibraryFeedEntries", "Hide library entries in feed", hideLibraryFeedEntries),
                )
            },
        ),
    )
}

fun ChimahonSettings.toAdvancedSettingsScreen(): ChimahonSettingsScreen {
    return ChimahonSettingsScreen(
        route = ChimahonSettingsRoute.Advanced,
        sections = listOf(
            appMode.toAppModeSection(),
            dictionary.toDictionarySection(),
            security.toSecuritySection(),
            data.toMaintenanceSection(),
        ),
    )
}

fun ChimahonDataSettings.toBackupSettingsScreen(): ChimahonSettingsScreen {
    return ChimahonSettingsScreen(
        route = ChimahonSettingsRoute.Backup,
        sections = listOf(
            section("backup.schedule", "Automatic backups") {
                listOf(
                    switchPreference("data.autoBackupEnabled", "Automatic backups", autoBackupEnabled),
                    sliderPreference("data.backupIntervalHours", "Backup interval", backupIntervalHours.toFloat(), 6f..336f, 54, "${backupIntervalHours}h", autoBackupEnabled),
                    infoPreference("data.backupDirectory", "Backup directory", backupDirectory.ifBlank { "Not selected" }),
                    switchPreference("data.backupWifiOnly", "Back up on Wi-Fi only", backupWifiOnly),
                    switchPreference("data.backupWhileChargingOnly", "Back up while charging only", backupWhileChargingOnly),
                    sliderPreference("data.backupRetentionCount", "Backup retention", backupRetentionCount.toFloat(), 1f..20f, 18, backupRetentionCount.toString()),
                    switchPreference("data.compressBackups", "Compress backups", compressBackups),
                )
            },
            section("backup.scope", "Backup scope") {
                listOf(
                    switchPreference("data.includeLibraryInBackups", "Library", includeLibraryInBackups),
                    switchPreference("data.includeCategoriesInBackups", "Categories", includeCategoriesInBackups),
                    switchPreference("data.includeTrackingInBackups", "Tracking", includeTrackingInBackups),
                    switchPreference("data.includeHistoryInBackups", "History", includeHistoryInBackups),
                    switchPreference("data.includeSettingsInBackups", "Settings", includeSettingsInBackups),
                    switchPreference("data.includeExtensionsInBackups", "Extensions", includeExtensionsInBackups),
                    switchPreference("data.includeExtensionReposInBackups", "Extension repositories", includeExtensionReposInBackups),
                    switchPreference("data.includeDownloadsInBackups", "Downloads", includeDownloadsInBackups),
                    switchPreference("data.includeAnimeInBackups", "Anime library", includeAnimeInBackups),
                )
            },
            section("backup.restore", "Restore") {
                listOf(
                    listPreference("data.restoreMissingSources", "Missing sources", restoreMissingSources.name, ChimahonRestoreMissingSourceBehavior.entries.options { it.title }),
                    switchPreference("data.restoreCategories", "Restore categories", restoreCategories),
                    switchPreference("data.restoreTracking", "Restore tracking", restoreTracking),
                    switchPreference("data.restoreHistory", "Restore history", restoreHistory),
                    switchPreference("data.restoreAppSettings", "Restore app settings", restoreAppSettings),
                    actionPreference("backup.create", "Create backup", "Export selected data now.", "Create"),
                    actionPreference("backup.restoreFile", "Restore backup", "Import a Chimahon backup file.", "Restore"),
                )
            },
        ),
    )
}

private fun ChimahonAppModeSettings.toAppModeSection(): ChimahonSettingsSection {
    return section("advanced.mode", "App mode") {
        listOf(
            switchPreference("appMode.downloadedOnly", "Downloaded only", downloadedOnly),
            switchPreference("appMode.incognitoMode", "Incognito mode", incognitoMode),
        )
    }
}

private fun ChimahonDictionarySettings.toDictionarySection(): ChimahonSettingsSection {
    return section("advanced.dictionary", "Dictionary and OCR") {
        listOf(
            switchPreference("dictionary.enabled", "Dictionary lookup", enabled),
            switchPreference("dictionary.ocrEnabled", "OCR lookup", ocrEnabled),
            multiPreference("dictionary.enabledLanguages", "Lookup languages", enabledLanguages, languageOptions()),
            listPreference("dictionary.ocrEngine", "OCR engine", ocrEngine.name, ChimahonDictionaryOcrEngine.entries.options { it.title }),
            sliderPreference("dictionary.popupWidth", "Popup width", popupWidth.toFloat(), 220f..720f, 49, "${popupWidth}px"),
            sliderPreference("dictionary.popupHeight", "Popup height", popupHeight.toFloat(), 220f..840f, 61, "${popupHeight}px"),
            sliderPreference("dictionary.fontSize", "Lookup font size", fontSize.toFloat(), 10f..28f, 17, "${fontSize}px"),
            sliderPreference("dictionary.ocrBoxScaleXPercent", "OCR box width scale", ocrBoxScaleXPercent.toFloat(), 50f..200f, 29, "$ocrBoxScaleXPercent%"),
            sliderPreference("dictionary.ocrBoxScaleYPercent", "OCR box height scale", ocrBoxScaleYPercent.toFloat(), 50f..200f, 29, "$ocrBoxScaleYPercent%"),
            sliderPreference("dictionary.ocrBoxOpacityPercent", "OCR box opacity", ocrBoxOpacityPercent.toFloat(), 0f..100f, 19, "$ocrBoxOpacityPercent%"),
            listPreference("dictionary.themeMode", "Dictionary theme", themeMode.name, ChimahonDictionaryThemeMode.entries.options { it.title }),
            listPreference("dictionary.popupMode", "Popup mode", popupMode.name, ChimahonDictionaryPopupMode.entries.options { it.title }),
            listPreference("dictionary.recursiveLookupMode", "Recursive lookup", recursiveLookupMode.name, ChimahonDictionaryRecursiveLookupMode.entries.options { it.title }),
            switchPreference("dictionary.eInkMode", "E-ink mode", eInkMode),
            switchPreference("dictionary.paginatedScrolling", "Paginated scrolling", paginatedScrolling),
            switchPreference("dictionary.groupTerms", "Group terms", groupTerms),
            switchPreference("dictionary.groupPitches", "Group pitches", groupPitches),
            switchPreference("dictionary.showNavigationButtons", "Show navigation buttons", showNavigationButtons),
            switchPreference("dictionary.showPitchDiagram", "Show pitch diagram", showPitchDiagram),
            switchPreference("dictionary.showPitchNumber", "Show pitch number", showPitchNumber),
            switchPreference("dictionary.showPitchText", "Show pitch text", showPitchText),
            switchPreference("dictionary.autoKanaConversion", "Automatic kana conversion", autoKanaConversion),
            switchPreference("dictionary.wordAudioEnabled", "Word audio", wordAudioEnabled),
            switchPreference("dictionary.wordAudioAutoplay", "Autoplay word audio", wordAudioAutoplay, wordAudioEnabled),
        )
    }
}

private fun ChimahonSecuritySettings.toSecuritySection(): ChimahonSettingsSection {
    return section("advanced.security", "Security and privacy") {
        listOf(
            listPreference("security.secureScreenMode", "Secure screen", secureScreenMode.name, ChimahonSecureScreenMode.entries.options { it.name.readableEnumLabel() }),
            switchPreference("security.hideNotificationContent", "Hide notification content", hideNotificationContent),
            switchPreference("security.crashReportsEnabled", "Crash reports", crashReportsEnabled),
            switchPreference("security.requireAuthentication", "Require authentication", requireAuthentication),
            sliderPreference("security.lockAfterMinutes", "Lock after", lockAfterMinutes.toFloat(), 0f..120f, 23, if (lockAfterMinutes == 0) "Immediately" else "${lockAfterMinutes}m", requireAuthentication),
            switchPreference("security.lockOnAppExit", "Lock on app exit", lockOnAppExit),
            switchPreference("security.incognitoModeByDefault", "Incognito by default", incognitoModeByDefault),
            switchPreference("security.protectDownloads", "Protect downloads", protectDownloads),
            listPreference("security.downloadEncryption", "Download encryption", downloadEncryption.name, ChimahonDownloadEncryption.entries.options { it.title }, enabled = protectDownloads),
            multiPreference("security.biometricLockDays", "Authentication days", biometricLockDays, weekdayOptions(), enabled = requireAuthentication),
            switchPreference("security.encryptDatabase", "Encrypt database", encryptDatabase),
            switchPreference("security.hideHistoryInIncognito", "Hide history in incognito", hideHistoryInIncognito),
            switchPreference("security.hideDownloadsInIncognito", "Hide downloads in incognito", hideDownloadsInIncognito),
            switchPreference("security.hideDiscordRpcInIncognito", "Hide Discord RPC in incognito", hideDiscordRpcInIncognito),
            switchPreference("security.clearSearchHistoryOnExit", "Clear search history on exit", clearSearchHistoryOnExit),
            switchPreference("security.clearCookiesOnExit", "Clear cookies on exit", clearCookiesOnExit),
            switchPreference("security.clearClipboardOnExit", "Clear clipboard on exit", clearClipboardOnExit),
            switchPreference("security.excludeDownloadsFromSystemBackups", "Exclude downloads from system backups", excludeDownloadsFromSystemBackups),
            switchPreference("security.secureLocalSourceFiles", "Secure local source files", secureLocalSourceFiles),
            switchPreference("security.requireAuthForSettings", "Require auth for settings", requireAuthForSettings),
            switchPreference("security.requireAuthForDownloads", "Require auth for downloads", requireAuthForDownloads),
            switchPreference("security.allowScreenshotsInReader", "Allow screenshots in reader", allowScreenshotsInReader),
        )
    }
}

private fun ChimahonDataSettings.toMaintenanceSection(): ChimahonSettingsSection {
    return section("advanced.maintenance", "Data maintenance") {
        listOf(
            switchPreference("data.clearChapterCacheOnExit", "Clear chapter cache on exit", clearChapterCacheOnExit),
            switchPreference("data.clearDatabaseHistoryOnLogout", "Clear database history on logout", clearDatabaseHistoryOnLogout),
            switchPreference("data.databaseMaintenanceOnStart", "Run database maintenance on start", databaseMaintenanceOnStart),
            actionPreference("advanced.clearChapterCache", "Clear chapter cache", "Remove temporary chapter files.", "Clear", destructive = true),
            actionPreference("advanced.clearCookies", "Clear cookies", "Remove source web cookies.", "Clear", destructive = true),
            actionPreference("advanced.reindexDownloads", "Reindex downloads", "Rebuild download metadata from disk.", "Run"),
        )
    }
}

private fun section(
    key: String,
    title: String,
    subtitle: String? = null,
    preferences: () -> List<ChimahonPreference>,
): ChimahonSettingsSection {
    return ChimahonSettingsSection(
        key = key,
        title = title,
        subtitle = subtitle,
        preferences = preferences(),
    )
}

private fun switchPreference(
    key: String,
    title: String,
    checked: Boolean,
    enabled: Boolean = true,
    subtitle: String? = null,
): ChimahonPreference.Switch {
    return ChimahonPreference.Switch(
        key = key,
        title = title,
        subtitle = subtitle,
        checked = checked,
        enabled = enabled,
    )
}

private fun sliderPreference(
    key: String,
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    label: String,
    enabled: Boolean = true,
    subtitle: String? = null,
): ChimahonPreference.Slider {
    return ChimahonPreference.Slider(
        key = key,
        title = title,
        subtitle = subtitle,
        enabled = enabled,
        spec = ChimahonSliderSpec(
            value = value,
            valueRange = range,
            steps = steps,
            valueLabel = label,
        ),
    )
}

private fun listPreference(
    key: String,
    title: String,
    selectedKey: String,
    options: List<ChimahonPreferenceOption>,
    enabled: Boolean = true,
    subtitle: String? = null,
): ChimahonPreference.ListSelect {
    return ChimahonPreference.ListSelect(
        key = key,
        title = title,
        subtitle = subtitle,
        selectedKey = selectedKey,
        options = options,
        enabled = enabled,
    )
}

private fun multiPreference(
    key: String,
    title: String,
    selected: Collection<String>,
    options: List<ChimahonPreferenceOption>,
    enabled: Boolean = true,
    subtitle: String? = null,
): ChimahonPreference.MultiSelect {
    return ChimahonPreference.MultiSelect(
        key = key,
        title = title,
        subtitle = subtitle,
        selectedKeys = selected.toSet(),
        options = options,
        enabled = enabled,
    )
}

private fun infoPreference(
    key: String,
    title: String,
    subtitle: String,
    tone: ChimahonPreferenceTone = ChimahonPreferenceTone.Info,
): ChimahonPreference.Info {
    return ChimahonPreference.Info(
        key = key,
        title = title,
        subtitle = subtitle,
        tone = tone,
    )
}

private fun actionPreference(
    key: String,
    title: String,
    subtitle: String,
    actionLabel: String,
    destructive: Boolean = false,
): ChimahonPreference.Action {
    return ChimahonPreference.Action(
        key = key,
        title = title,
        subtitle = subtitle,
        actionLabel = actionLabel,
        destructive = destructive,
    )
}

private fun <T : Enum<T>> Iterable<T>.options(title: (T) -> String): List<ChimahonPreferenceOption> {
    return map { value ->
        ChimahonPreferenceOption(
            key = value.name,
            title = title(value),
        )
    }
}

private fun commonRestrictionOptions(): List<ChimahonPreferenceOption> = listOf(
    ChimahonPreferenceOption("Wi-Fi only", "Wi-Fi only"),
    ChimahonPreferenceOption("Charging", "Charging"),
    ChimahonPreferenceOption("Battery not low", "Battery not low"),
    ChimahonPreferenceOption("Unmetered network", "Unmetered network"),
)

private fun smartUpdateOptions(): List<ChimahonPreferenceOption> = listOf(
    ChimahonPreferenceOption("Has unread chapters", "Has unread chapters"),
    ChimahonPreferenceOption("Started", "Started"),
    ChimahonPreferenceOption("Not completed", "Not completed"),
    ChimahonPreferenceOption("In release period", "In release period"),
)

private fun duplicateChapterOptions(): List<ChimahonPreferenceOption> = listOf(
    ChimahonPreferenceOption("Same scanlator", "Same scanlator"),
    ChimahonPreferenceOption("Same chapter number", "Same chapter number"),
    ChimahonPreferenceOption("Same title", "Same title"),
)

private fun categoryPlaceholderOptions(): List<ChimahonPreferenceOption> = listOf(
    ChimahonPreferenceOption("Default", "Default"),
    ChimahonPreferenceOption("Favorites", "Favorites"),
    ChimahonPreferenceOption("Completed", "Completed"),
    ChimahonPreferenceOption("Reading", "Reading"),
    ChimahonPreferenceOption("Plan to read", "Plan to read"),
)

private fun animeCategoryPlaceholderOptions(): List<ChimahonPreferenceOption> = listOf(
    ChimahonPreferenceOption("Default", "Default"),
    ChimahonPreferenceOption("Watching", "Watching"),
    ChimahonPreferenceOption("Completed", "Completed"),
    ChimahonPreferenceOption("On hold", "On hold"),
    ChimahonPreferenceOption("Dropped", "Dropped"),
    ChimahonPreferenceOption("Plan to watch", "Plan to watch"),
)

private fun animeUpdateRestrictionOptions(): List<ChimahonPreferenceOption> = listOf(
    ChimahonPreferenceOption("Outside release period", "Outside release period"),
    ChimahonPreferenceOption("Wi-Fi only", "Wi-Fi only"),
    ChimahonPreferenceOption("Charging", "Charging"),
    ChimahonPreferenceOption("Battery not low", "Battery not low"),
    ChimahonPreferenceOption("Unmetered network", "Unmetered network"),
)

private fun readerBottomButtonOptions(): List<ChimahonPreferenceOption> = listOf(
    ChimahonPreferenceOption("vc", "Vertical crop"),
    ChimahonPreferenceOption("wb", "Webtoon side padding"),
    ChimahonPreferenceOption("cbp", "Page crop borders"),
    ChimahonPreferenceOption("cbc", "Color filter"),
    ChimahonPreferenceOption("pl", "Page layout"),
    ChimahonPreferenceOption("ocr", "GLens OCR lookup"),
    ChimahonPreferenceOption("ms", "More settings"),
)

private fun languageOptions(): List<ChimahonPreferenceOption> = listOf(
    ChimahonPreferenceOption("ja", "Japanese"),
    ChimahonPreferenceOption("en", "English"),
    ChimahonPreferenceOption("ko", "Korean"),
    ChimahonPreferenceOption("zh", "Chinese"),
)

private fun weekdayOptions(): List<ChimahonPreferenceOption> = listOf(
    ChimahonPreferenceOption("Sunday", "Sunday"),
    ChimahonPreferenceOption("Monday", "Monday"),
    ChimahonPreferenceOption("Tuesday", "Tuesday"),
    ChimahonPreferenceOption("Wednesday", "Wednesday"),
    ChimahonPreferenceOption("Thursday", "Thursday"),
    ChimahonPreferenceOption("Friday", "Friday"),
    ChimahonPreferenceOption("Saturday", "Saturday"),
)

private fun String.readableEnumLabel(): String {
    if (isBlank()) return this
    return buildString(length + 4) {
        this@readableEnumLabel.forEachIndexed { index, char ->
            val previous = this@readableEnumLabel.getOrNull(index - 1)
            if (index > 0 && char.isUpperCase() && previous?.isLetterOrDigit() == true) {
                append(' ')
            }
            append(char)
        }
    }
}

private fun ChimahonFilterMode.filterSettingsTitle(): String {
    return when (this) {
        ChimahonFilterMode.Any -> "Any"
        ChimahonFilterMode.Include -> "Include"
        ChimahonFilterMode.Exclude -> "Exclude"
    }
}

private fun ChimahonAnimeEpisodeSortMode.settingsTitle(): String {
    return when (this) {
        ChimahonAnimeEpisodeSortMode.Source -> "Source order"
        ChimahonAnimeEpisodeSortMode.EpisodeNumber -> "Episode number"
        ChimahonAnimeEpisodeSortMode.UploadDate -> "Upload date"
        ChimahonAnimeEpisodeSortMode.Alphabetical -> "Alphabetical"
    }
}

private fun ChimahonAnimeEpisodeDisplayMode.settingsTitle(): String {
    return when (this) {
        ChimahonAnimeEpisodeDisplayMode.Name -> "Source title"
        ChimahonAnimeEpisodeDisplayMode.Number -> "Episode number"
    }
}

private fun Int.animeDefaultCategoryLabel(): String {
    return when (this) {
        -1 -> "Use the app default category"
        0 -> "Uncategorized"
        else -> "Category ID $this"
    }
}

private fun playerSpeedOptions(
    currentSpeed: Double,
    presets: List<String>,
): List<ChimahonPreferenceOption> {
    val speeds = (presets.mapNotNull { it.toDoubleOrNull() } + currentSpeed)
        .map { it.coerceIn(0.25, 4.0) }
        .distinct()
        .sorted()
    return speeds.map { speed ->
        ChimahonPreferenceOption(
            key = speed.speedKey(),
            title = "${speed.speedKey()}x",
        )
    }
}

private fun Double.speedKey(): String {
    return shortNumber()
}

private fun String.blankSettingsValue(fallback: String): String {
    return if (isBlank()) fallback else this
}

private fun String.maskedSettingsValue(fallback: String): String {
    return when {
        isBlank() -> fallback
        length <= 4 -> "Set"
        else -> "${take(2)}...${takeLast(2)}"
    }
}

private fun Double.shortNumber(): String {
    val rounded = kotlin.math.round(this * 10.0) / 10.0
    return if (rounded % 1.0 == 0.0) {
        rounded.toInt().toString()
    } else {
        rounded.toString()
    }
}
