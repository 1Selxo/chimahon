package app.chimahon.shared.menuui

object ChimahonMenuPresets {
    fun screenOverflow(
        includeSearch: Boolean = true,
        includeFilter: Boolean = true,
        includeSort: Boolean = false,
        includeRefresh: Boolean = true,
        includeSettings: Boolean = true,
        includeHelp: Boolean = false,
    ): List<ChimahonMenuSection> {
        return buildList {
            add(
                ChimahonMenuSection(
                    actions = buildList {
                        if (includeSearch) add(screenAction(ChimahonMenuActionId.Search, "Search"))
                        if (includeFilter) add(screenAction(ChimahonMenuActionId.Filter, "Filter"))
                        if (includeSort) add(screenAction(ChimahonMenuActionId.Sort, "Sort"))
                        if (includeRefresh) add(screenAction(ChimahonMenuActionId.Refresh, "Refresh"))
                    },
                ),
            )
            add(
                ChimahonMenuSection(
                    actions = buildList {
                        if (includeSettings) add(screenAction(ChimahonMenuActionId.Settings, "Settings"))
                        if (includeHelp) add(screenAction(ChimahonMenuActionId.Help, "Help"))
                    },
                ),
            )
        }.withoutEmptySections()
    }

    fun mangaActions(
        favorite: Boolean,
        inLibrary: Boolean = true,
        unreadCount: Int? = null,
        downloaded: Boolean = false,
        webUrlAvailable: Boolean = true,
        trackingAvailable: Boolean = true,
        migrationAvailable: Boolean = true,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                actions = listOf(
                    mangaAction(
                        id = if (favorite) ChimahonMenuActionId.Unfavorite else ChimahonMenuActionId.Favorite,
                        label = if (favorite) "Remove favorite" else "Add to library",
                        supportingText = if (inLibrary) "Library entry" else "Not in library",
                    ),
                    mangaAction(
                        id = ChimahonMenuActionId.Categories,
                        label = "Edit categories",
                        enabled = inLibrary,
                    ),
                    mangaAction(
                        id = ChimahonMenuActionId.Track,
                        label = "Tracking",
                        enabled = trackingAvailable,
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Chapters",
                actions = listOf(
                    mangaAction(
                        id = ChimahonMenuActionId.MarkRead,
                        label = "Mark all as read",
                        badge = unreadCount?.takeIf { it > 0 }?.toString(),
                    ),
                    mangaAction(
                        id = ChimahonMenuActionId.MarkUnread,
                        label = "Mark all as unread",
                    ),
                    mangaAction(
                        id = ChimahonMenuActionId.Download,
                        label = if (downloaded) "Download missing chapters" else "Download chapters",
                    ),
                    mangaAction(
                        id = ChimahonMenuActionId.DeleteDownloads,
                        label = "Delete downloads",
                        enabled = downloaded,
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Source",
                actions = listOf(
                    mangaAction(
                        id = ChimahonMenuActionId.WebView,
                        label = "Open in WebView",
                        enabled = webUrlAvailable,
                    ),
                    mangaAction(
                        id = ChimahonMenuActionId.CopyLink,
                        label = "Copy source link",
                        enabled = webUrlAvailable,
                    ),
                    mangaAction(
                        id = ChimahonMenuActionId.Share,
                        label = "Share",
                        enabled = webUrlAvailable,
                    ),
                    mangaAction(
                        id = ChimahonMenuActionId.Migrate,
                        label = "Migrate",
                        enabled = migrationAvailable && inLibrary,
                    ),
                ),
            ),
            ChimahonMenuSection(
                actions = listOf(
                    mangaAction(
                        id = ChimahonMenuActionId.RemoveFromLibrary,
                        label = "Remove from library",
                        enabled = inLibrary,
                    ),
                ),
            ),
        ).withoutEmptySections()
    }

    fun mangaSelectionActions(
        selectedCount: Int,
        canDownload: Boolean = true,
        canDeleteDownloads: Boolean = true,
    ): List<ChimahonMenuSection> {
        val suffix = if (selectedCount == 1) "chapter" else "chapters"
        return listOf(
            ChimahonMenuSection(
                title = "$selectedCount selected",
                actions = listOf(
                    mangaAction(ChimahonMenuActionId.SelectAll, "Select all"),
                    mangaAction(ChimahonMenuActionId.MarkRead, "Mark read"),
                    mangaAction(ChimahonMenuActionId.MarkUnread, "Mark unread"),
                    mangaAction(ChimahonMenuActionId.MarkPreviousRead, "Mark previous as read"),
                    mangaAction(
                        id = ChimahonMenuActionId.Download,
                        label = "Download $suffix",
                        enabled = canDownload,
                    ),
                    mangaAction(
                        id = ChimahonMenuActionId.DeleteDownloads,
                        label = "Delete downloads",
                        enabled = canDeleteDownloads,
                    ),
                ),
            ),
        )
    }

    fun animeActions(
        favorite: Boolean,
        inLibrary: Boolean = true,
        unseenCount: Int? = null,
        downloaded: Boolean = false,
        webUrlAvailable: Boolean = true,
        trackingAvailable: Boolean = true,
        playerAvailable: Boolean = true,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                actions = listOf(
                    animeAction(
                        id = if (favorite) ChimahonMenuActionId.Unfavorite else ChimahonMenuActionId.Favorite,
                        label = if (favorite) "Remove favorite" else "Add to library",
                        supportingText = if (inLibrary) "Anime library entry" else "Not in library",
                    ),
                    animeAction(
                        id = ChimahonMenuActionId.PlayerSettings,
                        label = "Player settings",
                        enabled = playerAvailable,
                        payloadKey = "player.autoplayEnabled",
                    ),
                    animeAction(
                        id = ChimahonMenuActionId.Categories,
                        label = "Edit categories",
                        enabled = inLibrary,
                    ),
                    animeAction(
                        id = ChimahonMenuActionId.Track,
                        label = "Tracking",
                        enabled = trackingAvailable,
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Episodes",
                actions = listOf(
                    animeAction(
                        id = ChimahonMenuActionId.MarkSeen,
                        label = "Mark all as seen",
                        badge = unseenCount?.takeIf { it > 0 }?.toString(),
                    ),
                    animeAction(
                        id = ChimahonMenuActionId.MarkUnseen,
                        label = "Mark all as unseen",
                    ),
                    animeAction(
                        id = ChimahonMenuActionId.Download,
                        label = if (downloaded) "Download missing episodes" else "Download episodes",
                    ),
                    animeAction(
                        id = ChimahonMenuActionId.DeleteDownloads,
                        label = "Delete episode downloads",
                        enabled = downloaded,
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Source",
                actions = listOf(
                    animeAction(ChimahonMenuActionId.WebView, "Open in WebView", enabled = webUrlAvailable),
                    animeAction(ChimahonMenuActionId.CopyLink, "Copy anime link", enabled = webUrlAvailable),
                    animeAction(ChimahonMenuActionId.Share, "Share anime", enabled = webUrlAvailable),
                    animeAction(ChimahonMenuActionId.Migrate, "Migrate anime", enabled = inLibrary),
                ),
            ),
            ChimahonMenuSection(
                actions = listOf(
                    animeAction(
                        id = ChimahonMenuActionId.RemoveAnimeFromLibrary,
                        label = "Remove from library",
                        enabled = inLibrary,
                    ),
                ),
            ),
        ).withoutEmptySections()
    }

    fun animeSelectionActions(
        selectedCount: Int,
        canDownload: Boolean = true,
        canDeleteDownloads: Boolean = true,
    ): List<ChimahonMenuSection> {
        val suffix = if (selectedCount == 1) "episode" else "episodes"
        return listOf(
            ChimahonMenuSection(
                title = "$selectedCount selected",
                actions = listOf(
                    animeAction(ChimahonMenuActionId.SelectAll, "Select all"),
                    animeAction(ChimahonMenuActionId.MarkSeen, "Mark seen"),
                    animeAction(ChimahonMenuActionId.MarkUnseen, "Mark unseen"),
                    animeAction(ChimahonMenuActionId.MarkPreviousSeen, "Mark previous as seen"),
                    animeAction(
                        id = ChimahonMenuActionId.Download,
                        label = "Download $suffix",
                        enabled = canDownload,
                    ),
                    animeAction(
                        id = ChimahonMenuActionId.DeleteDownloads,
                        label = "Delete downloads",
                        enabled = canDeleteDownloads,
                    ),
                ),
            ),
        )
    }

    fun readerActions(
        ocrEnabled: Boolean = true,
        ocrActive: Boolean = false,
        hasPageImage: Boolean = true,
        pageFailed: Boolean = false,
        autoScrollRunning: Boolean = false,
        webUrlAvailable: Boolean = true,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                actions = listOf(
                    readerAction(ChimahonMenuActionId.ReaderSettings, "Reader settings"),
                    readerAction(ChimahonMenuActionId.Chapters, "Chapters"),
                    readerAction(ChimahonMenuActionId.Statistics, "Statistics"),
                ),
            ),
            ChimahonMenuSection(
                title = "Display",
                actions = listOf(
                    readerAction(ChimahonMenuActionId.ReadingMode, "Reading mode"),
                    readerAction(ChimahonMenuActionId.Orientation, "Orientation"),
                    readerAction(ChimahonMenuActionId.Crop, "Crop borders"),
                    readerAction(
                        id = ChimahonMenuActionId.ResetReaderView,
                        label = "Reset view",
                        enabled = hasPageImage,
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Page",
                actions = listOf(
                    readerAction(
                        id = ChimahonMenuActionId.OcrLookup,
                        label = if (ocrActive) "Hide OCR lookup" else "OCR lookup",
                        enabled = ocrEnabled,
                        checked = ocrActive,
                        role = ChimahonMenuActionRole.Toggle,
                    ),
                    readerAction(ChimahonMenuActionId.DictionaryLookup, "Dictionary lookup", enabled = ocrEnabled),
                    readerAction(
                        id = ChimahonMenuActionId.Retry,
                        label = "Retry page",
                        enabled = pageFailed,
                    ),
                    readerAction(
                        id = ChimahonMenuActionId.RetryAll,
                        label = "Retry failed pages",
                    ),
                    readerAction(
                        id = ChimahonMenuActionId.SetCover,
                        label = "Set as cover",
                        enabled = hasPageImage,
                    ),
                    readerAction(
                        id = ChimahonMenuActionId.SaveImage,
                        label = "Save image",
                        enabled = hasPageImage,
                    ),
                    readerAction(
                        id = ChimahonMenuActionId.SharePage,
                        label = "Share page",
                        enabled = hasPageImage,
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Automation",
                actions = listOf(
                    readerAction(
                        id = ChimahonMenuActionId.AutoScroll,
                        label = if (autoScrollRunning) "Pause autoscroll" else "Start autoscroll",
                        checked = autoScrollRunning,
                        role = ChimahonMenuActionRole.Toggle,
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Source",
                actions = listOf(
                    readerAction(ChimahonMenuActionId.WebView, "Open in WebView", enabled = webUrlAvailable),
                    readerAction(ChimahonMenuActionId.CopyLink, "Copy chapter link", enabled = webUrlAvailable),
                    readerAction(ChimahonMenuActionId.Share, "Share chapter", enabled = webUrlAvailable),
                ),
            ),
        ).withoutEmptySections()
    }

    fun novelActions(
        favorite: Boolean,
        inLibrary: Boolean = true,
        unreadCount: Int? = null,
        lookupAvailable: Boolean = true,
        syncAvailable: Boolean = true,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                actions = listOf(
                    novelAction(
                        id = if (favorite) ChimahonMenuActionId.Unfavorite else ChimahonMenuActionId.Favorite,
                        label = if (favorite) "Remove favorite" else "Add to library",
                        supportingText = if (inLibrary) "Light novel entry" else "Not in library",
                    ),
                    novelAction(
                        id = ChimahonMenuActionId.NovelTypography,
                        label = "Typography",
                        payloadKey = "reader.fontSize",
                    ),
                    novelAction(
                        id = ChimahonMenuActionId.NovelSettings,
                        label = "Novel reader settings",
                        payloadKey = "reader.continuousMode",
                    ),
                    novelAction(ChimahonMenuActionId.Categories, "Edit categories", enabled = inLibrary),
                ),
            ),
            ChimahonMenuSection(
                title = "Chapters",
                actions = listOf(
                    novelAction(
                        id = ChimahonMenuActionId.MarkRead,
                        label = "Mark all as read",
                        badge = unreadCount?.takeIf { it > 0 }?.toString(),
                    ),
                    novelAction(ChimahonMenuActionId.MarkUnread, "Mark all as unread"),
                    novelAction(
                        id = ChimahonMenuActionId.NovelLookup,
                        label = "Dictionary lookup",
                        enabled = lookupAvailable,
                        payloadKey = "dictionary.enabled",
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Sync",
                actions = listOf(
                    novelAction(ChimahonMenuActionId.Track, "Tracking", enabled = syncAvailable),
                    novelAction(ChimahonMenuActionId.NovelImport, "Import EPUB"),
                ),
            ),
            ChimahonMenuSection(
                actions = listOf(
                    novelAction(
                        id = ChimahonMenuActionId.RemoveNovelFromLibrary,
                        label = "Remove from library",
                        enabled = inLibrary,
                    ),
                ),
            ),
        ).withoutEmptySections()
    }

    fun novelSelectionActions(
        selectedCount: Int,
        canResetProgress: Boolean = true,
    ): List<ChimahonMenuSection> {
        val suffix = if (selectedCount == 1) "novel" else "novels"
        return listOf(
            ChimahonMenuSection(
                title = "$selectedCount selected",
                actions = listOf(
                    novelAction(ChimahonMenuActionId.SelectAll, "Select all"),
                    novelAction(ChimahonMenuActionId.ClearSelection, "Clear selection"),
                    novelAction(ChimahonMenuActionId.Categories, "Move $suffix to category"),
                    novelAction(ChimahonMenuActionId.MarkUnread, "Reset progress", enabled = canResetProgress),
                    novelAction(ChimahonMenuActionId.RemoveNovelFromLibrary, "Remove $suffix"),
                ),
            ),
        )
    }

    fun playerActions(
        paused: Boolean = false,
        fullscreen: Boolean = true,
        pictureInPictureAvailable: Boolean = false,
        pictureInPicture: Boolean = false,
        theaterMode: Boolean = false,
        castAvailable: Boolean = false,
        casting: Boolean = false,
        subtitleLookupEnabled: Boolean = false,
        videoOcrEnabled: Boolean = false,
        downloaded: Boolean = false,
        sourceUrlAvailable: Boolean = true,
        externalPlayerAvailable: Boolean = true,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                actions = listOf(
                    playerAction(
                        id = ChimahonMenuActionId.PlayerSettings,
                        label = "Player settings",
                        payloadKey = "player.autoplayEnabled",
                    ),
                    playerAction(ChimahonMenuActionId.Episodes, "Episodes"),
                    playerAction(
                        id = ChimahonMenuActionId.PlaybackSpeed,
                        label = "Playback speed",
                        payloadKey = "player.playerSpeed",
                    ),
                    playerAction(ChimahonMenuActionId.Quality, "Quality"),
                    playerAction(ChimahonMenuActionId.Hosters, "Hosters"),
                ),
            ),
            ChimahonMenuSection(
                title = "Tracks and filters",
                actions = listOf(
                    playerAction(
                        id = ChimahonMenuActionId.SubtitleSettings,
                        label = "Subtitle settings",
                        payloadKey = "player.subtitles.listMode",
                    ),
                    playerAction(ChimahonMenuActionId.Subtitles, "Subtitle tracks"),
                    playerAction(ChimahonMenuActionId.AudioTracks, "Audio tracks"),
                    playerAction(
                        id = ChimahonMenuActionId.AudioDelay,
                        label = "Audio delay",
                        payloadKey = "player.audio.delayMillis",
                    ),
                    playerAction(ChimahonMenuActionId.VideoFilters, "Video filters"),
                    playerAction(
                        id = ChimahonMenuActionId.VideoOcr,
                        label = if (videoOcrEnabled) "Capture video OCR" else "Video OCR",
                        checked = videoOcrEnabled,
                        role = ChimahonMenuActionRole.Toggle,
                    ),
                    playerAction(
                        id = ChimahonMenuActionId.DictionaryLookup,
                        label = "Subtitle lookup",
                        checked = subtitleLookupEnabled,
                        role = ChimahonMenuActionRole.Toggle,
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Window",
                actions = listOf(
                    playerAction(
                        id = ChimahonMenuActionId.TogglePictureInPicture,
                        label = if (pictureInPicture) "Exit picture-in-picture" else "Picture-in-picture",
                        checked = pictureInPicture,
                        enabled = pictureInPictureAvailable,
                        role = ChimahonMenuActionRole.Toggle,
                    ),
                    playerAction(
                        id = ChimahonMenuActionId.ToggleTheaterMode,
                        label = if (theaterMode) "Exit theater mode" else "Theater mode",
                        checked = theaterMode,
                        role = ChimahonMenuActionRole.Toggle,
                    ),
                    playerAction(
                        id = ChimahonMenuActionId.ToggleCast,
                        label = if (casting) "Stop casting" else "Cast",
                        checked = casting,
                        enabled = castAvailable,
                        role = ChimahonMenuActionRole.Toggle,
                    ),
                    playerAction(
                        id = ChimahonMenuActionId.OpenExternalPlayer,
                        label = "Open in external player",
                        enabled = externalPlayerAvailable,
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Source",
                actions = listOf(
                    playerAction(
                        id = ChimahonMenuActionId.Download,
                        label = if (downloaded) "Downloaded" else "Download episode",
                        selected = downloaded,
                    ),
                    playerAction(ChimahonMenuActionId.OpenInBrowser, "Open source", enabled = sourceUrlAvailable),
                    playerAction(ChimahonMenuActionId.CopyLink, "Copy source link", enabled = sourceUrlAvailable),
                    playerAction(ChimahonMenuActionId.Share, "Share episode", enabled = sourceUrlAvailable),
                    playerAction(
                        id = ChimahonMenuActionId.Retry,
                        label = if (paused) "Retry stream" else "Retry current stream",
                    ),
                ),
            ),
        ).withoutEmptySections()
    }

    fun sourceActions(
        pinned: Boolean = false,
        enabled: Boolean = true,
        extensionInstalled: Boolean = true,
        trusted: Boolean = true,
        hasSettings: Boolean = false,
        updateAvailable: Boolean = false,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                actions = listOf(
                    sourceAction(
                        id = if (pinned) ChimahonMenuActionId.UnpinSource else ChimahonMenuActionId.PinSource,
                        label = if (pinned) "Unpin source" else "Pin source",
                    ),
                    sourceAction(
                        id = if (enabled) ChimahonMenuActionId.DisableSource else ChimahonMenuActionId.EnableSource,
                        label = if (enabled) "Disable source" else "Enable source",
                        checked = enabled,
                        role = ChimahonMenuActionRole.Toggle,
                    ),
                    sourceAction(
                        id = ChimahonMenuActionId.SourceSettings,
                        label = "Source settings",
                        enabled = hasSettings,
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Extension",
                actions = listOf(
                    sourceAction(
                        id = ChimahonMenuActionId.UpdateExtension,
                        label = "Update extension",
                        enabled = updateAvailable,
                        badge = if (updateAvailable) "New" else null,
                    ),
                    sourceAction(
                        id = if (trusted) ChimahonMenuActionId.UntrustExtension else ChimahonMenuActionId.TrustExtension,
                        label = if (trusted) "Remove trust" else "Trust extension",
                        enabled = extensionInstalled,
                    ),
                    sourceAction(
                        id = if (extensionInstalled) {
                            ChimahonMenuActionId.UninstallExtension
                        } else {
                            ChimahonMenuActionId.InstallExtension
                        },
                        label = if (extensionInstalled) "Uninstall extension" else "Install extension",
                    ),
                ),
            ),
        ).withoutEmptySections()
    }

    fun repositorySettingsActions(
        repositoryCount: Int = 0,
        disabledCount: Int = 0,
        autoSyncEnabled: Boolean = true,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                title = "Repositories",
                actions = listOf(
                    repoAction(
                        id = ChimahonMenuActionId.ExtensionRepositories,
                        label = "Extension repositories",
                        badge = repositoryCount.takeIf { it > 0 }?.toString(),
                        payloadKey = "browse.disabledExtensionRepoUrls",
                    ),
                    repoAction(ChimahonMenuActionId.AddRepository, "Add repository"),
                    repoAction(ChimahonMenuActionId.RefreshRepository, "Refresh repositories"),
                    repoAction(
                        id = ChimahonMenuActionId.RepositorySettings,
                        label = "Repository settings",
                        supportingText = if (disabledCount > 0) "$disabledCount disabled" else null,
                        payloadKey = "browse.disabledExtensionRepoUrls",
                    ),
                ),
            ),
            ChimahonMenuSection(
                title = "Automation",
                actions = listOf(
                    settingsAction(
                        id = ChimahonMenuActionId.Settings,
                        label = "Auto-update repositories",
                        checked = autoSyncEnabled,
                        role = ChimahonMenuActionRole.Toggle,
                        payloadKey = "browse.autoUpdateExtensionRepos",
                    ),
                ),
            ),
        ).withoutEmptySections()
    }

    fun repositoryActions(
        selected: Boolean = false,
        reachable: Boolean = true,
        mutable: Boolean = true,
        removing: Boolean = false,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                actions = listOf(
                    repoAction(
                        id = ChimahonMenuActionId.SelectRepository,
                        label = if (selected) "Selected repository" else "Use repository",
                        selected = selected,
                        enabled = !selected && reachable,
                        role = ChimahonMenuActionRole.Radio,
                    ),
                    repoAction(ChimahonMenuActionId.RefreshRepository, "Refresh repository", enabled = reachable),
                    repoAction(ChimahonMenuActionId.CopyRepositoryUrl, "Copy repository URL"),
                ),
            ),
            ChimahonMenuSection(
                actions = listOf(
                    repoAction(ChimahonMenuActionId.EditRepository, "Edit repository", enabled = mutable),
                    repoAction(
                        id = ChimahonMenuActionId.RemoveRepository,
                        label = if (removing) "Removing repository" else "Remove repository",
                        enabled = mutable && !removing,
                        busy = removing,
                        supportingText = "Installed extensions stay installed.",
                        payloadKey = "browse.disabledExtensionRepoUrls",
                    ),
                ),
            ),
        ).withoutEmptySections()
    }

    fun removeRepositoryActions(
        repositoryName: String,
        removable: Boolean = true,
        removing: Boolean = false,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                title = "Remove repository?",
                actions = listOf(
                    repoAction(
                        id = ChimahonMenuActionId.RemoveRepository,
                        label = if (removing) "Removing $repositoryName" else "Remove $repositoryName",
                        enabled = removable && !removing,
                        busy = removing,
                        supportingText = "Installed extensions stay installed.",
                        payloadKey = "browse.disabledExtensionRepoUrls",
                    ),
                    repoAction(ChimahonMenuActionId.CopyRepositoryUrl, "Copy repository URL"),
                ),
            ),
        )
    }

    fun settingsActions(
        backupAvailable: Boolean = true,
        restoreAvailable: Boolean = true,
        dataActionsAvailable: Boolean = true,
        destructiveEnabled: Boolean = false,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                title = "Data",
                actions = listOf(
                    settingsAction(ChimahonMenuActionId.Backup, "Create backup", enabled = backupAvailable),
                    settingsAction(ChimahonMenuActionId.Restore, "Restore backup", enabled = restoreAvailable),
                    settingsAction(ChimahonMenuActionId.Import, "Import data", enabled = dataActionsAvailable),
                    settingsAction(ChimahonMenuActionId.Export, "Export data", enabled = dataActionsAvailable),
                    settingsAction(ChimahonMenuActionId.DataAndStorage, "Data and storage"),
                ),
            ),
            ChimahonMenuSection(
                title = "Appearance",
                actions = listOf(
                    settingsAction(ChimahonMenuActionId.Theme, "Theme"),
                ),
            ),
            ChimahonMenuSection(
                title = "Advanced",
                actions = listOf(
                    settingsAction(
                        id = ChimahonMenuActionId.ResetSettings,
                        label = "Reset settings",
                        enabled = destructiveEnabled,
                        supportingText = "Restore defaults for this section.",
                    ),
                    settingsAction(ChimahonMenuActionId.About, "About Chimahon"),
                ),
            ),
        ).withoutEmptySections()
    }

    fun settingsNavigationActions(
        includeAnime: Boolean = true,
        includeNovels: Boolean = true,
        includePlayer: Boolean = true,
        includeDesktop: Boolean = true,
        includeRepositories: Boolean = true,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                title = "Settings",
                actions = buildList {
                    add(
                        settingsAction(
                            id = ChimahonMenuActionId.Settings,
                            label = "Settings",
                            payloadKey = "appearance.themeMode",
                        ),
                    )
                    if (includeAnime) {
                        add(
                            settingsAction(
                                id = ChimahonMenuActionId.AnimeSettings,
                                label = "Anime settings",
                                payloadKey = "animeLibrary.displayMode",
                            ),
                        )
                    }
                    if (includePlayer) {
                        add(
                            settingsAction(
                                id = ChimahonMenuActionId.PlayerSettings,
                                label = "Player settings",
                                payloadKey = "player.autoplayEnabled",
                            ),
                        )
                    }
                    if (includeNovels) {
                        add(
                            settingsAction(
                                id = ChimahonMenuActionId.NovelSettings,
                                label = "Light novel settings",
                                payloadKey = "reader.continuousMode",
                            ),
                        )
                    }
                    if (includeDesktop) {
                        add(
                            desktopAction(
                                id = ChimahonMenuActionId.DesktopSettings,
                                label = "Desktop settings",
                                payloadKey = "appearance.tabletUiMode",
                            ),
                        )
                    }
                    if (includeRepositories) {
                        add(
                            repoAction(
                                id = ChimahonMenuActionId.ExtensionRepositories,
                                label = "Extension repositories",
                                payloadKey = "browse.disabledExtensionRepoUrls",
                            ),
                        )
                    }
                },
            ),
        ).withoutEmptySections()
    }

    fun desktopActions(
        showCommandPalette: Boolean = true,
        showKeyboardShortcuts: Boolean = true,
        showSettings: Boolean = true,
    ): List<ChimahonMenuSection> {
        return listOf(
            ChimahonMenuSection(
                title = "Desktop",
                actions = buildList {
                    if (showCommandPalette) {
                        add(desktopAction(ChimahonMenuActionId.CommandPalette, "Command palette", shortcut = "Ctrl+K"))
                    }
                    if (showKeyboardShortcuts) {
                        add(desktopAction(ChimahonMenuActionId.DesktopShortcuts, "Keyboard shortcuts", shortcut = "?"))
                    }
                    if (showSettings) {
                        add(
                            desktopAction(
                                id = ChimahonMenuActionId.DesktopSettings,
                                label = "Desktop settings",
                                payloadKey = "appearance.tabletUiMode",
                            ),
                        )
                    }
                },
            ),
        ).withoutEmptySections()
    }
}

fun screenAction(
    id: ChimahonMenuActionId,
    label: String,
    enabled: Boolean = true,
    checked: Boolean = false,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    badge: String? = null,
    supportingText: String? = null,
): ChimahonMenuAction {
    return menuAction(
        id = id,
        area = ChimahonMenuActionArea.Screen,
        label = label,
        enabled = enabled,
        checked = checked,
        role = role,
        badge = badge,
        supportingText = supportingText,
    )
}

fun mangaAction(
    id: ChimahonMenuActionId,
    label: String,
    enabled: Boolean = true,
    checked: Boolean = false,
    selected: Boolean = false,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    badge: String? = null,
    supportingText: String? = null,
): ChimahonMenuAction {
    return menuAction(
        id = id,
        area = ChimahonMenuActionArea.Manga,
        label = label,
        enabled = enabled,
        checked = checked,
        selected = selected,
        role = role,
        badge = badge,
        supportingText = supportingText,
    )
}

fun animeAction(
    id: ChimahonMenuActionId,
    label: String,
    enabled: Boolean = true,
    checked: Boolean = false,
    selected: Boolean = false,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    badge: String? = null,
    supportingText: String? = null,
    payloadKey: String? = null,
): ChimahonMenuAction {
    return menuAction(
        id = id,
        area = ChimahonMenuActionArea.Anime,
        label = label,
        enabled = enabled,
        checked = checked,
        selected = selected,
        role = role,
        badge = badge,
        supportingText = supportingText,
        payloadKey = payloadKey,
    )
}

fun readerAction(
    id: ChimahonMenuActionId,
    label: String,
    enabled: Boolean = true,
    checked: Boolean = false,
    selected: Boolean = false,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    badge: String? = null,
    supportingText: String? = null,
    shortcut: String? = null,
): ChimahonMenuAction {
    return menuAction(
        id = id,
        area = ChimahonMenuActionArea.Reader,
        label = label,
        enabled = enabled,
        checked = checked,
        selected = selected,
        role = role,
        badge = badge,
        supportingText = supportingText,
        shortcut = shortcut,
    )
}

fun novelAction(
    id: ChimahonMenuActionId,
    label: String,
    enabled: Boolean = true,
    checked: Boolean = false,
    selected: Boolean = false,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    badge: String? = null,
    supportingText: String? = null,
    payloadKey: String? = null,
): ChimahonMenuAction {
    return menuAction(
        id = id,
        area = ChimahonMenuActionArea.Novel,
        label = label,
        enabled = enabled,
        checked = checked,
        selected = selected,
        role = role,
        badge = badge,
        supportingText = supportingText,
        payloadKey = payloadKey,
    )
}

fun playerAction(
    id: ChimahonMenuActionId,
    label: String,
    enabled: Boolean = true,
    checked: Boolean = false,
    selected: Boolean = false,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    badge: String? = null,
    supportingText: String? = null,
    shortcut: String? = null,
    payloadKey: String? = null,
): ChimahonMenuAction {
    return menuAction(
        id = id,
        area = ChimahonMenuActionArea.Player,
        label = label,
        enabled = enabled,
        checked = checked,
        selected = selected,
        role = role,
        badge = badge,
        supportingText = supportingText,
        shortcut = shortcut,
        payloadKey = payloadKey,
    )
}

fun sourceAction(
    id: ChimahonMenuActionId,
    label: String,
    enabled: Boolean = true,
    checked: Boolean = false,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    badge: String? = null,
    supportingText: String? = null,
): ChimahonMenuAction {
    return menuAction(
        id = id,
        area = ChimahonMenuActionArea.Source,
        label = label,
        enabled = enabled,
        checked = checked,
        role = role,
        badge = badge,
        supportingText = supportingText,
    )
}

fun repoAction(
    id: ChimahonMenuActionId,
    label: String,
    enabled: Boolean = true,
    selected: Boolean = false,
    busy: Boolean = false,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    badge: String? = null,
    supportingText: String? = null,
    payloadKey: String? = null,
): ChimahonMenuAction {
    return menuAction(
        id = id,
        area = ChimahonMenuActionArea.Repository,
        label = label,
        enabled = enabled,
        selected = selected,
        busy = busy,
        role = role,
        badge = badge,
        supportingText = supportingText,
        payloadKey = payloadKey,
    )
}

fun settingsAction(
    id: ChimahonMenuActionId,
    label: String,
    enabled: Boolean = true,
    checked: Boolean = false,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    badge: String? = null,
    supportingText: String? = null,
    payloadKey: String? = null,
): ChimahonMenuAction {
    return menuAction(
        id = id,
        area = ChimahonMenuActionArea.Settings,
        label = label,
        enabled = enabled,
        checked = checked,
        role = role,
        badge = badge,
        supportingText = supportingText,
        payloadKey = payloadKey,
    )
}

fun desktopAction(
    id: ChimahonMenuActionId,
    label: String,
    enabled: Boolean = true,
    checked: Boolean = false,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    badge: String? = null,
    supportingText: String? = null,
    shortcut: String? = null,
    payloadKey: String? = null,
): ChimahonMenuAction {
    return menuAction(
        id = id,
        area = ChimahonMenuActionArea.Desktop,
        label = label,
        enabled = enabled,
        checked = checked,
        role = role,
        badge = badge,
        supportingText = supportingText,
        shortcut = shortcut,
        payloadKey = payloadKey,
    )
}

fun menuAction(
    id: ChimahonMenuActionId,
    area: ChimahonMenuActionArea,
    label: String,
    enabled: Boolean = true,
    checked: Boolean = false,
    selected: Boolean = false,
    busy: Boolean = false,
    role: ChimahonMenuActionRole = ChimahonMenuActionRole.Normal,
    tone: ChimahonMenuActionTone = id.defaultMenuTone(),
    icon: ChimahonMenuIcon? = id.defaultMenuIcon(),
    badge: String? = null,
    supportingText: String? = null,
    shortcut: String? = null,
    payloadKey: String? = null,
): ChimahonMenuAction {
    return ChimahonMenuAction(
        id = id,
        label = label,
        area = area,
        icon = icon,
        supportingText = supportingText,
        enabled = enabled,
        checked = checked,
        selected = selected,
        busy = busy,
        badge = badge,
        shortcut = shortcut,
        role = role,
        tone = tone,
        payloadKey = payloadKey,
    )
}

fun List<ChimahonMenuSection>.withoutEmptySections(): List<ChimahonMenuSection> {
    return filter { it.visibleActions.isNotEmpty() }
}
