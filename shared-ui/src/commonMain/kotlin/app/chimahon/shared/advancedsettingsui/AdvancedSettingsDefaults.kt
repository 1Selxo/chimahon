package app.chimahon.shared.advancedsettingsui

import androidx.compose.ui.graphics.Color

object ChimahonAdvancedSettingsDefaults {
    fun state(): ChimahonAdvancedSettingsState {
        return ChimahonAdvancedSettingsState(
            pages = pages(),
            selectedRoute = ChimahonAdvancedSettingsRoute.Overview,
        )
    }

    fun pages(): List<ChimahonAdvancedSettingsPage> {
        return listOf(
            overviewPage(),
            appearancePage(),
            backupPage(),
            dataStoragePage(),
            securityPrivacyPage(),
            navigationPage(),
            animeAndNovelPage(),
            diagnosticsPage(),
        )
    }

    fun backupState(): ChimahonBackupRestoreState {
        return ChimahonBackupRestoreState(
            lastBackupLabel = "No backup yet",
            nextBackupLabel = "Manual",
            automaticBackupsEnabled = false,
            includePrivateData = false,
            scopes = listOf(
                ChimahonBackupScopeItem(
                    key = "library",
                    title = "Library entries",
                    subtitle = "Manga, anime, light novels, categories, progress",
                    required = true,
                ),
                ChimahonBackupScopeItem(
                    key = "settings",
                    title = "Settings",
                    subtitle = "Reader, player, browse, navigation and appearance",
                    required = true,
                ),
                ChimahonBackupScopeItem(
                    key = "tracking",
                    title = "Tracking",
                    subtitle = "Tracker links and pending sync state",
                ),
                ChimahonBackupScopeItem(
                    key = "extensions",
                    title = "Extensions and repositories",
                    subtitle = "Installed source metadata and repo list",
                ),
                ChimahonBackupScopeItem(
                    key = "downloads",
                    title = "Download index",
                    subtitle = "Chapter and episode download metadata",
                    selected = false,
                ),
            ),
        )
    }

    fun maintenanceRows(): List<ChimahonMaintenanceRow> {
        return listOf(
            ChimahonMaintenanceRow(
                key = "clear_image_cache",
                title = "Clear image cache",
                subtitle = "Covers, thumbnails and temporary reader pages",
                sizeLabel = "Cache",
                actionLabel = "Clear",
                icon = ChimahonAdvancedSettingIcon.Storage,
            ),
            ChimahonMaintenanceRow(
                key = "clear_webview_cache",
                title = "Clear extension web cache",
                subtitle = "Cookies, scraper sessions and temporary source data",
                sizeLabel = "Web",
                actionLabel = "Clear",
                icon = ChimahonAdvancedSettingIcon.Hidden,
            ),
            ChimahonMaintenanceRow(
                key = "repair_database",
                title = "Repair database",
                subtitle = "Run integrity checks and remove orphan rows",
                actionLabel = "Run",
                icon = ChimahonAdvancedSettingIcon.Storage,
                tone = ChimahonAdvancedSettingTone.Warning,
            ),
            ChimahonMaintenanceRow(
                key = "reindex_library",
                title = "Reindex library",
                subtitle = "Refresh search keys for manga, anime and novels",
                actionLabel = "Start",
                icon = ChimahonAdvancedSettingIcon.Sync,
            ),
        )
    }

    fun themeSwatches(): List<ChimahonThemeSwatch> {
        return listOf(
            ChimahonThemeSwatch(
                key = "default",
                title = "Default",
                subtitle = "System colors",
                selected = true,
                primary = Color(0xFF6750A4),
                secondary = Color(0xFF625B71),
                background = Color(0xFFFFFBFE),
            ),
            ChimahonThemeSwatch(
                key = "midnight_dusk",
                title = "Midnight dusk",
                subtitle = "Dark with muted violet",
                primary = Color(0xFFD0BCFF),
                secondary = Color(0xFFCCC2DC),
                background = Color(0xFF141218),
            ),
            ChimahonThemeSwatch(
                key = "forest",
                title = "Forest",
                subtitle = "Green accent",
                primary = Color(0xFF006D3B),
                secondary = Color(0xFF4D6357),
                background = Color(0xFFFBFDF7),
            ),
            ChimahonThemeSwatch(
                key = "ocean",
                title = "Ocean",
                subtitle = "Blue accent",
                primary = Color(0xFF0061A4),
                secondary = Color(0xFF535F70),
                background = Color(0xFFFCFCFF),
            ),
            ChimahonThemeSwatch(
                key = "amoled",
                title = "AMOLED",
                subtitle = "Pure black surfaces",
                primary = Color(0xFFB69DF8),
                secondary = Color(0xFFC9C2D0),
                background = Color(0xFF000000),
            ),
        )
    }

    fun navigationTabs(): List<ChimahonNavigationTabItem> {
        return listOf(
            ChimahonNavigationTabItem(
                key = "library",
                title = "Library",
                subtitle = "Manga and saved reading lists",
                destination = ChimahonNavigationDestination.BottomBar,
            ),
            ChimahonNavigationTabItem(
                key = "anime",
                title = "Anime",
                subtitle = "Anime library and player queue",
                destination = ChimahonNavigationDestination.BottomBar,
                icon = ChimahonAdvancedSettingIcon.Visibility,
            ),
            ChimahonNavigationTabItem(
                key = "novels",
                title = "Novels",
                subtitle = "Light novels and text reader",
                destination = ChimahonNavigationDestination.Rail,
                icon = ChimahonAdvancedSettingIcon.Tune,
            ),
            ChimahonNavigationTabItem(
                key = "updates",
                title = "Updates",
                subtitle = "Chapter, episode and novel updates",
                destination = ChimahonNavigationDestination.Rail,
            ),
            ChimahonNavigationTabItem(
                key = "history",
                title = "History",
                subtitle = "Reading and watching history",
                destination = ChimahonNavigationDestination.Rail,
            ),
            ChimahonNavigationTabItem(
                key = "browse",
                title = "Browse",
                subtitle = "Sources, extensions and migration",
                destination = ChimahonNavigationDestination.BottomBar,
            ),
            ChimahonNavigationTabItem(
                key = "more",
                title = "More",
                subtitle = "Settings, backups and diagnostics",
                destination = ChimahonNavigationDestination.BottomBar,
                canMoveDown = false,
            ),
        )
    }

    fun securityToggles(): List<ChimahonSecurityToggle> {
        return listOf(
            ChimahonSecurityToggle(
                key = "incognito_mode",
                title = "Incognito mode",
                subtitle = "Pause history updates while reading or watching",
                checked = false,
                icon = ChimahonAdvancedSettingIcon.Hidden,
            ),
            ChimahonSecurityToggle(
                key = "downloaded_only",
                title = "Downloaded only",
                subtitle = "Hide network-backed entries while offline",
                checked = false,
                icon = ChimahonAdvancedSettingIcon.Download,
            ),
            ChimahonSecurityToggle(
                key = "extension_integrity",
                title = "Require extension integrity checks",
                subtitle = "Warn before running changed third-party source code",
                checked = true,
                icon = ChimahonAdvancedSettingIcon.Security,
                tone = ChimahonAdvancedSettingTone.Warning,
            ),
            ChimahonSecurityToggle(
                key = "private_backup",
                title = "Encrypt private backup sections",
                subtitle = "Protect tracker tokens, cookies and repository credentials",
                checked = true,
                icon = ChimahonAdvancedSettingIcon.Lock,
            ),
        )
    }

    fun diagnostics(): List<ChimahonDiagnosticRow> {
        return listOf(
            ChimahonDiagnosticRow(
                key = "database",
                title = "SQLDelight database",
                value = "Ready",
                subtitle = "Shared schema loaded",
                status = ChimahonDiagnosticStatus.Healthy,
            ),
            ChimahonDiagnosticRow(
                key = "extensions",
                title = "Extension engine",
                value = "Ready",
                subtitle = "Desktop script runtime available",
                status = ChimahonDiagnosticStatus.Healthy,
            ),
            ChimahonDiagnosticRow(
                key = "ocr",
                title = "GLens OCR",
                value = "Platform client",
                subtitle = "OCR availability is checked per device",
                status = ChimahonDiagnosticStatus.Unknown,
                actionLabel = "Check",
            ),
            ChimahonDiagnosticRow(
                key = "downloads",
                title = "Download worker",
                value = "Idle",
                subtitle = "Background queue bridge",
                status = ChimahonDiagnosticStatus.Unknown,
            ),
        )
    }

    private fun overviewPage(): ChimahonAdvancedSettingsPage {
        return ChimahonAdvancedSettingsPage(
            route = ChimahonAdvancedSettingsRoute.Overview,
            groups = listOf(
                ChimahonAdvancedSettingsGroup(
                    key = "main",
                    title = "Settings categories",
                    items = listOf(
                        routeLink(ChimahonAdvancedSettingsRoute.Appearance, ChimahonAdvancedSettingIcon.Palette),
                        routeLink(ChimahonAdvancedSettingsRoute.BackupRestore, ChimahonAdvancedSettingIcon.Backup),
                        routeLink(ChimahonAdvancedSettingsRoute.DataStorage, ChimahonAdvancedSettingIcon.Storage),
                        routeLink(ChimahonAdvancedSettingsRoute.SecurityPrivacy, ChimahonAdvancedSettingIcon.Security),
                        routeLink(ChimahonAdvancedSettingsRoute.NavigationEditor, ChimahonAdvancedSettingIcon.Navigation),
                        routeLink(ChimahonAdvancedSettingsRoute.AnimeAndNovel, ChimahonAdvancedSettingIcon.Visibility),
                        routeLink(ChimahonAdvancedSettingsRoute.Diagnostics, ChimahonAdvancedSettingIcon.Diagnostics),
                    ),
                ),
            ),
        )
    }

    private fun appearancePage(): ChimahonAdvancedSettingsPage {
        return ChimahonAdvancedSettingsPage(
            route = ChimahonAdvancedSettingsRoute.Appearance,
            groups = listOf(
                ChimahonAdvancedSettingsGroup(
                    key = "theme",
                    title = "Theme",
                    subtitle = "Android-like appearance controls",
                    items = listOf(
                        ChimahonAdvancedSettingItem.Choice(
                            key = "theme_mode",
                            title = "Theme mode",
                            subtitle = "Follow system, light, dark or AMOLED",
                            selectedKey = "system",
                            options = listOf(
                                ChimahonAdvancedOption("system", "Follow system"),
                                ChimahonAdvancedOption("light", "Light"),
                                ChimahonAdvancedOption("dark", "Dark"),
                                ChimahonAdvancedOption("amoled", "Dark AMOLED"),
                            ),
                            icon = ChimahonAdvancedSettingIcon.Palette,
                            category = ChimahonAdvancedSettingCategory.Appearance,
                            tags = setOf("theme", "dark", "amoled"),
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "dynamic_color",
                            title = "Dynamic color",
                            subtitle = "Use platform accent colors when available",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Palette,
                            category = ChimahonAdvancedSettingCategory.Appearance,
                        ),
                        ChimahonAdvancedSettingItem.Slider(
                            key = "font_scale",
                            title = "Font scale",
                            subtitle = "Adjust UI text size",
                            value = 100f,
                            valueRange = 85f..130f,
                            steps = 8,
                            valueLabel = "100%",
                            icon = ChimahonAdvancedSettingIcon.Settings,
                            category = ChimahonAdvancedSettingCategory.Appearance,
                        ),
                    ),
                ),
                ChimahonAdvancedSettingsGroup(
                    key = "navigation",
                    title = "Navigation",
                    items = listOf(
                        routeLink(ChimahonAdvancedSettingsRoute.NavigationEditor, ChimahonAdvancedSettingIcon.Navigation),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "compact_navigation",
                            title = "Compact navigation",
                            subtitle = "Use smaller labels and icon containers on desktop",
                            checked = false,
                            icon = ChimahonAdvancedSettingIcon.Navigation,
                            category = ChimahonAdvancedSettingCategory.Navigation,
                        ),
                    ),
                ),
            ),
        )
    }

    private fun backupPage(): ChimahonAdvancedSettingsPage {
        return ChimahonAdvancedSettingsPage(
            route = ChimahonAdvancedSettingsRoute.BackupRestore,
            groups = listOf(
                ChimahonAdvancedSettingsGroup(
                    key = "backup",
                    title = "Backup",
                    items = listOf(
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "automatic_backups",
                            title = "Automatic backups",
                            subtitle = "Create scheduled backups in the selected folder",
                            checked = false,
                            icon = ChimahonAdvancedSettingIcon.Backup,
                            category = ChimahonAdvancedSettingCategory.Backup,
                        ),
                        ChimahonAdvancedSettingItem.Choice(
                            key = "backup_frequency",
                            title = "Backup frequency",
                            subtitle = "How often scheduled backups are created",
                            selectedKey = "weekly",
                            options = listOf(
                                ChimahonAdvancedOption("daily", "Daily"),
                                ChimahonAdvancedOption("weekly", "Weekly"),
                                ChimahonAdvancedOption("monthly", "Monthly"),
                            ),
                            icon = ChimahonAdvancedSettingIcon.Sync,
                            category = ChimahonAdvancedSettingCategory.Backup,
                        ),
                        ChimahonAdvancedSettingItem.Action(
                            key = "create_backup",
                            title = "Create backup now",
                            subtitle = "Export library, settings and source metadata",
                            actionLabel = "Create",
                            icon = ChimahonAdvancedSettingIcon.Backup,
                            category = ChimahonAdvancedSettingCategory.Backup,
                        ),
                        ChimahonAdvancedSettingItem.Action(
                            key = "restore_backup",
                            title = "Restore backup",
                            subtitle = "Preview and merge a backup file",
                            actionLabel = "Restore",
                            icon = ChimahonAdvancedSettingIcon.Restore,
                            category = ChimahonAdvancedSettingCategory.Backup,
                        ),
                    ),
                ),
            ),
        )
    }

    private fun dataStoragePage(): ChimahonAdvancedSettingsPage {
        return ChimahonAdvancedSettingsPage(
            route = ChimahonAdvancedSettingsRoute.DataStorage,
            groups = listOf(
                ChimahonAdvancedSettingsGroup(
                    key = "cache",
                    title = "Storage maintenance",
                    items = maintenanceRows().map { row ->
                        ChimahonAdvancedSettingItem.Action(
                            key = row.key,
                            title = row.title,
                            subtitle = row.subtitle,
                            actionLabel = row.actionLabel,
                            icon = row.icon,
                            category = ChimahonAdvancedSettingCategory.Data,
                            tone = row.tone,
                            enabled = row.enabled,
                            tags = setOf("storage", "cache", "cleanup"),
                        )
                    },
                ),
                ChimahonAdvancedSettingsGroup(
                    key = "repositories",
                    title = "Extension repositories",
                    subtitle = "Android-style repository management entries",
                    items = listOf(
                        ChimahonAdvancedSettingItem.Action(
                            key = "browse.extensionRepositories",
                            title = "Extension repositories",
                            subtitle = "Add, sync, disable or remove source repositories",
                            actionLabel = "Manage",
                            icon = ChimahonAdvancedSettingIcon.Storage,
                            category = ChimahonAdvancedSettingCategory.Extensions,
                            tags = setOf("browse", "extension", "repository", "repo"),
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "browse.autoUpdateExtensionRepos",
                            title = "Auto-update repositories",
                            subtitle = "Refresh extension repositories with source update checks",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Sync,
                            category = ChimahonAdvancedSettingCategory.Extensions,
                            tags = setOf("browse", "extension", "repository", "repo", "sync"),
                        ),
                        ChimahonAdvancedSettingItem.Info(
                            key = "browse.disabledExtensionRepoUrls",
                            title = "Disabled repositories",
                            value = "0",
                            subtitle = "Repositories hidden from extension discovery",
                            icon = ChimahonAdvancedSettingIcon.Visibility,
                            category = ChimahonAdvancedSettingCategory.Extensions,
                            tags = setOf("browse", "extension", "repository", "repo", "disabled"),
                        ),
                        ChimahonAdvancedSettingItem.Action(
                            key = "repository.remove",
                            title = "Remove repository",
                            subtitle = "Remove a repository without uninstalling its extensions",
                            actionLabel = "Remove",
                            icon = ChimahonAdvancedSettingIcon.Delete,
                            category = ChimahonAdvancedSettingCategory.Extensions,
                            tone = ChimahonAdvancedSettingTone.Error,
                            tags = setOf("browse.disabledExtensionRepoUrls", "remove", "repository", "repo"),
                        ),
                    ),
                ),
            ),
        )
    }

    private fun securityPrivacyPage(): ChimahonAdvancedSettingsPage {
        return ChimahonAdvancedSettingsPage(
            route = ChimahonAdvancedSettingsRoute.SecurityPrivacy,
            groups = listOf(
                ChimahonAdvancedSettingsGroup(
                    key = "security",
                    title = "Security and privacy",
                    items = securityToggles().map { toggle ->
                        ChimahonAdvancedSettingItem.Toggle(
                            key = toggle.key,
                            title = toggle.title,
                            subtitle = toggle.subtitle,
                            checked = toggle.checked,
                            icon = toggle.icon,
                            category = when (toggle.icon) {
                                ChimahonAdvancedSettingIcon.Hidden,
                                ChimahonAdvancedSettingIcon.Visibility,
                                ChimahonAdvancedSettingIcon.Privacy -> ChimahonAdvancedSettingCategory.Privacy
                                else -> ChimahonAdvancedSettingCategory.Security
                            },
                            tone = toggle.tone,
                            enabled = toggle.enabled,
                        )
                    },
                ),
            ),
        )
    }

    private fun navigationPage(): ChimahonAdvancedSettingsPage {
        return ChimahonAdvancedSettingsPage(
            route = ChimahonAdvancedSettingsRoute.NavigationEditor,
            groups = listOf(
                ChimahonAdvancedSettingsGroup(
                    key = "desktop",
                    title = "Desktop and tablet layout",
                    items = listOf(
                        ChimahonAdvancedSettingItem.Choice(
                            key = "appearance.tabletUiMode",
                            title = "Large screen navigation",
                            subtitle = "Use rail, bottom bar, or automatic layout",
                            selectedKey = "Automatic",
                            options = listOf(
                                ChimahonAdvancedOption("Automatic", "Automatic"),
                                ChimahonAdvancedOption("Always", "Always use tablet UI"),
                                ChimahonAdvancedOption("Landscape", "Landscape only"),
                                ChimahonAdvancedOption("Never", "Never use tablet UI"),
                            ),
                            icon = ChimahonAdvancedSettingIcon.Navigation,
                            category = ChimahonAdvancedSettingCategory.Navigation,
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "appearance.showNavigationBadges",
                            title = "Show navigation badges",
                            subtitle = "Display unread and update counters",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Visibility,
                            category = ChimahonAdvancedSettingCategory.Navigation,
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "reader.keyboardShortcutsEnabled",
                            title = "Reader keyboard shortcuts",
                            subtitle = "Use Android-compatible and desktop reader shortcuts",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Navigation,
                            category = ChimahonAdvancedSettingCategory.Navigation,
                            tags = setOf("desktop", "reader", "keyboard"),
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "reader.desktopTapNavigation",
                            title = "Mouse tap navigation",
                            subtitle = "Use desktop taps for page navigation",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Navigation,
                            category = ChimahonAdvancedSettingCategory.Navigation,
                            tags = setOf("desktop", "reader", "mouse"),
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "reader.desktopPageButtons",
                            title = "Desktop page buttons",
                            subtitle = "Show pointer-friendly reader page buttons",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Visibility,
                            category = ChimahonAdvancedSettingCategory.Navigation,
                            tags = setOf("desktop", "reader", "buttons"),
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "reader.desktopReaderMenu",
                            title = "Desktop reader menu",
                            subtitle = "Expose reader actions in the desktop menu",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Settings,
                            category = ChimahonAdvancedSettingCategory.Navigation,
                            tags = setOf("desktop", "reader", "menu"),
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "reader.trackpadGesturesEnabled",
                            title = "Trackpad gestures",
                            subtitle = "Enable trackpad scrolling and zoom gestures",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Navigation,
                            category = ChimahonAdvancedSettingCategory.Navigation,
                            tags = setOf("desktop", "reader", "trackpad"),
                        ),
                    ),
                ),
            ),
        )
    }

    private fun animeAndNovelPage(): ChimahonAdvancedSettingsPage {
        return ChimahonAdvancedSettingsPage(
            route = ChimahonAdvancedSettingsRoute.AnimeAndNovel,
            groups = listOf(
                ChimahonAdvancedSettingsGroup(
                    key = "anime",
                    title = "Anime",
                    subtitle = "Player and episode defaults",
                    items = listOf(
                        ChimahonAdvancedSettingItem.Choice(
                            key = "animeLibrary.episodeSort",
                            title = "Episode sort",
                            subtitle = "Default ordering for source episode lists",
                            selectedKey = "Source",
                            options = listOf(
                                ChimahonAdvancedOption("Source", "Source order"),
                                ChimahonAdvancedOption("EpisodeNumber", "Episode number"),
                                ChimahonAdvancedOption("UploadDate", "Upload date"),
                                ChimahonAdvancedOption("Alphabetical", "Alphabetical"),
                            ),
                            icon = ChimahonAdvancedSettingIcon.Visibility,
                            category = ChimahonAdvancedSettingCategory.Anime,
                            tags = setOf("anime", "episode", "source order"),
                        ),
                        ChimahonAdvancedSettingItem.Choice(
                            key = "animeLibrary.episodeDisplayMode",
                            title = "Episode display mode",
                            subtitle = "Choose source titles or episode numbers",
                            selectedKey = "Name",
                            options = listOf(
                                ChimahonAdvancedOption("Name", "Source title"),
                                ChimahonAdvancedOption("Number", "Episode number"),
                            ),
                            icon = ChimahonAdvancedSettingIcon.Visibility,
                            category = ChimahonAdvancedSettingCategory.Anime,
                            tags = setOf("anime", "episode", "display"),
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "player.autoplayEnabled",
                            title = "Autoplay next episode",
                            subtitle = "Continue from the player when an episode ends",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Sync,
                            category = ChimahonAdvancedSettingCategory.Player,
                            tags = setOf("anime", "player"),
                        ),
                        ChimahonAdvancedSettingItem.Choice(
                            key = "player.defaultOrientation",
                            title = "Default orientation",
                            subtitle = "Orientation used when the player opens",
                            selectedKey = "SensorLandscape",
                            options = listOf(
                                ChimahonAdvancedOption("Free", "Free"),
                                ChimahonAdvancedOption("Portrait", "Portrait"),
                                ChimahonAdvancedOption("Landscape", "Landscape"),
                                ChimahonAdvancedOption("SensorPortrait", "Sensor portrait"),
                                ChimahonAdvancedOption("SensorLandscape", "Sensor landscape"),
                                ChimahonAdvancedOption("ReverseLandscape", "Reverse landscape"),
                            ),
                            icon = ChimahonAdvancedSettingIcon.Tune,
                            category = ChimahonAdvancedSettingCategory.Player,
                            tags = setOf("anime", "player", "orientation"),
                        ),
                        ChimahonAdvancedSettingItem.Choice(
                            key = "player.aspect",
                            title = "Player aspect",
                            subtitle = "Default video scaling mode",
                            selectedKey = "Fit",
                            options = listOf(
                                ChimahonAdvancedOption("Fit", "Fit"),
                                ChimahonAdvancedOption("Crop", "Crop"),
                                ChimahonAdvancedOption("Stretch", "Stretch"),
                            ),
                            icon = ChimahonAdvancedSettingIcon.Visibility,
                            category = ChimahonAdvancedSettingCategory.Player,
                            tags = setOf("anime", "player", "aspect"),
                        ),
                        ChimahonAdvancedSettingItem.Slider(
                            key = "player.playerSpeed",
                            title = "Playback speed",
                            subtitle = "Default player speed",
                            value = 1f,
                            valueRange = 0.25f..4f,
                            steps = 14,
                            valueLabel = "1x",
                            icon = ChimahonAdvancedSettingIcon.Tune,
                            category = ChimahonAdvancedSettingCategory.Player,
                            tags = setOf("anime", "player", "speed"),
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "player.pipEnabled",
                            title = "Picture-in-picture",
                            subtitle = "Allow the player to enter PiP",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Visibility,
                            category = ChimahonAdvancedSettingCategory.Player,
                            tags = setOf("anime", "player", "pip"),
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "player.castEnabled",
                            title = "Cast controls",
                            subtitle = "Show casting actions when available",
                            checked = false,
                            icon = ChimahonAdvancedSettingIcon.Sync,
                            category = ChimahonAdvancedSettingCategory.Player,
                            tags = setOf("anime", "player", "cast"),
                        ),
                    ),
                ),
                ChimahonAdvancedSettingsGroup(
                    key = "novels",
                    title = "Light novels",
                    subtitle = "Reader and lookup defaults",
                    items = listOf(
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "reader.continuousMode",
                            title = "Novel reader mode",
                            subtitle = "Use continuous scrolling instead of paginated text",
                            checked = false,
                            icon = ChimahonAdvancedSettingIcon.Tune,
                            category = ChimahonAdvancedSettingCategory.Novel,
                            tags = setOf("novel", "light novel", "reader"),
                        ),
                        ChimahonAdvancedSettingItem.Slider(
                            key = "reader.fontSize",
                            title = "Novel font size",
                            subtitle = "Default text size for LN chapters",
                            value = 18f,
                            valueRange = 10f..36f,
                            steps = 25,
                            valueLabel = "18px",
                            icon = ChimahonAdvancedSettingIcon.Settings,
                            category = ChimahonAdvancedSettingCategory.Novel,
                            tags = setOf("novel", "light novel", "font"),
                        ),
                        ChimahonAdvancedSettingItem.Slider(
                            key = "reader.lineHeight",
                            title = "Novel line height",
                            subtitle = "Spacing between text lines",
                            value = 1.6f,
                            valueRange = 1.0f..2.0f,
                            steps = 9,
                            icon = ChimahonAdvancedSettingIcon.Settings,
                            category = ChimahonAdvancedSettingCategory.Novel,
                            tags = setOf("novel", "light novel", "line height"),
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "reader.verticalWriting",
                            title = "Vertical writing",
                            subtitle = "Prefer vertical text layout for supported novels",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Tune,
                            category = ChimahonAdvancedSettingCategory.Novel,
                            tags = setOf("novel", "light novel", "vertical"),
                        ),
                        ChimahonAdvancedSettingItem.Toggle(
                            key = "dictionary.enabled",
                            title = "Lookup selected text",
                            subtitle = "Show dictionary actions after text selection",
                            checked = true,
                            icon = ChimahonAdvancedSettingIcon.Search,
                            category = ChimahonAdvancedSettingCategory.Novel,
                            tags = setOf("novel", "dictionary", "lookup"),
                        ),
                    ),
                ),
            ),
        )
    }

    private fun diagnosticsPage(): ChimahonAdvancedSettingsPage {
        return ChimahonAdvancedSettingsPage(
            route = ChimahonAdvancedSettingsRoute.Diagnostics,
            groups = listOf(
                ChimahonAdvancedSettingsGroup(
                    key = "diagnostics",
                    title = "Diagnostics",
                    items = diagnostics().map { row ->
                        ChimahonAdvancedSettingItem.Info(
                            key = row.key,
                            title = row.title,
                            subtitle = row.subtitle,
                            value = row.value,
                            icon = ChimahonAdvancedSettingIcon.Diagnostics,
                            category = ChimahonAdvancedSettingCategory.Diagnostics,
                            tone = when (row.status) {
                                ChimahonDiagnosticStatus.Healthy -> ChimahonAdvancedSettingTone.Success
                                ChimahonDiagnosticStatus.Warning -> ChimahonAdvancedSettingTone.Warning
                                ChimahonDiagnosticStatus.Error -> ChimahonAdvancedSettingTone.Error
                                ChimahonDiagnosticStatus.Unknown -> ChimahonAdvancedSettingTone.Info
                            },
                        )
                    },
                ),
            ),
        )
    }

    private fun routeLink(
        route: ChimahonAdvancedSettingsRoute,
        icon: ChimahonAdvancedSettingIcon,
    ): ChimahonAdvancedSettingItem.Link {
        val category = when (route) {
            ChimahonAdvancedSettingsRoute.Appearance -> ChimahonAdvancedSettingCategory.Appearance
            ChimahonAdvancedSettingsRoute.BackupRestore -> ChimahonAdvancedSettingCategory.Backup
            ChimahonAdvancedSettingsRoute.DataStorage -> ChimahonAdvancedSettingCategory.Data
            ChimahonAdvancedSettingsRoute.SecurityPrivacy -> ChimahonAdvancedSettingCategory.Security
            ChimahonAdvancedSettingsRoute.NavigationEditor -> ChimahonAdvancedSettingCategory.Navigation
            ChimahonAdvancedSettingsRoute.AnimeAndNovel -> ChimahonAdvancedSettingCategory.Anime
            ChimahonAdvancedSettingsRoute.Diagnostics -> ChimahonAdvancedSettingCategory.Diagnostics
            ChimahonAdvancedSettingsRoute.Overview -> ChimahonAdvancedSettingCategory.Diagnostics
        }
        return ChimahonAdvancedSettingItem.Link(
            key = "route:${route.key}",
            title = route.title,
            subtitle = route.subtitle,
            route = route,
            icon = icon,
            category = category,
            tags = setOf(route.key, route.title),
        )
    }
}
