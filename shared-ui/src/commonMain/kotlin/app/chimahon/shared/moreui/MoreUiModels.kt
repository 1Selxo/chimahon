package app.chimahon.shared.moreui

import androidx.compose.ui.graphics.Color

enum class ChimahonMoreIcon {
    About,
    Anime,
    Backup,
    Categories,
    Check,
    Chevron,
    Delete,
    Desktop,
    Download,
    Edit,
    Extension,
    Help,
    History,
    Info,
    Library,
    Link,
    Novel,
    Palette,
    Player,
    Queue,
    Reader,
    Repository,
    Restore,
    Search,
    Security,
    Settings,
    Storage,
    Sync,
    Theme,
    Warning,
}

enum class ChimahonMoreMenuEntryKind {
    Navigation,
    Toggle,
    Action,
    Settings,
    Repository,
    Destructive,
}

data class ChimahonMorePreferenceAction(
    val label: String,
    val enabled: Boolean = true,
    val loading: Boolean = false,
)

data class ChimahonMorePreferenceOption(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val enabled: Boolean = true,
)

data class ChimahonMoreMenuEntry(
    val key: String,
    val title: String,
    val subtitle: String,
    val icon: ChimahonMoreIcon,
    val kind: ChimahonMoreMenuEntryKind = ChimahonMoreMenuEntryKind.Navigation,
    val settingsKey: String? = null,
    val value: String? = null,
    val badge: String? = null,
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val warning: Boolean = false,
    val action: ChimahonMorePreferenceAction? = null,
) {
    val pills: List<ChimahonMoreStatusPill>
        get() = buildList {
            if (!badge.isNullOrBlank()) add(ChimahonMoreStatusPill(badge, active = selected, warning = warning))
            if (selected && badge.isNullOrBlank()) add(ChimahonMoreStatusPill("On", active = true))
        }
}

data class ChimahonMoreMenuSectionState(
    val key: String,
    val title: String,
    val entries: List<ChimahonMoreMenuEntry>,
)

data class ChimahonMoreInfoCardState(
    val title: String,
    val subtitle: String,
    val icon: ChimahonMoreIcon = ChimahonMoreIcon.Info,
    val action: ChimahonMorePreferenceAction? = null,
    val warning: Boolean = false,
    val selected: Boolean = false,
)

data class ChimahonMoreThemeSwatch(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val onBackground: Color,
    val selected: Boolean = false,
    val enabled: Boolean = true,
)

data class ChimahonMoreColorSwatch(
    val key: String,
    val title: String,
    val color: Color,
    val selected: Boolean = false,
    val enabled: Boolean = true,
)

data class ChimahonMoreNavigationTabState(
    val key: String,
    val title: String,
    val section: ChimahonMoreNavigationSection,
    val icon: ChimahonMoreIcon = ChimahonMoreIcon.Library,
    val enabled: Boolean = true,
)

enum class ChimahonMoreNavigationSection(val title: String) {
    Navbar("Navigation bar"),
    More("More"),
    Disabled("Disabled"),
}

data class ChimahonMoreBackupScopeItem(
    val key: String,
    val title: String,
    val subtitle: String,
    val icon: ChimahonMoreIcon = ChimahonMoreIcon.Backup,
    val included: Boolean,
    val countLabel: String? = null,
    val warning: Boolean = false,
)

data class ChimahonMoreBackupActionState(
    val title: String,
    val subtitle: String,
    val icon: ChimahonMoreIcon = ChimahonMoreIcon.Backup,
    val action: ChimahonMorePreferenceAction,
    val warning: Boolean = false,
)

data class ChimahonMoreAboutLink(
    val title: String,
    val subtitle: String,
    val url: String,
    val icon: ChimahonMoreIcon = ChimahonMoreIcon.Link,
)

data class ChimahonMoreHelpTopic(
    val title: String,
    val body: String,
    val icon: ChimahonMoreIcon = ChimahonMoreIcon.Help,
    val action: ChimahonMorePreferenceAction? = null,
)

data class ChimahonMoreStatusPill(
    val text: String,
    val active: Boolean = false,
    val warning: Boolean = false,
)

object ChimahonMoreMenuDefaults {
    fun androidParitySections(
        downloadedOnly: Boolean = false,
        incognitoMode: Boolean = false,
        downloadQueueLabel: String? = null,
        includeAnime: Boolean = true,
        includeLightNovels: Boolean = true,
        includePlayer: Boolean = true,
        includeDesktop: Boolean = true,
        includeRepositories: Boolean = true,
    ): List<ChimahonMoreMenuSectionState> {
        return listOf(
            ChimahonMoreMenuSectionState(
                key = "quick",
                title = "Quick toggles",
                entries = listOf(
                    downloadedOnly(downloadedOnly),
                    incognitoMode(incognitoMode),
                ),
            ),
            ChimahonMoreMenuSectionState(
                key = "library",
                title = "Library",
                entries = buildList {
                    add(downloadQueue(downloadQueueLabel))
                    add(categories())
                    if (includeAnime) add(anime())
                    if (includeLightNovels) add(lightNovels())
                },
            ),
            ChimahonMoreMenuSectionState(
                key = "tools",
                title = "Tools",
                entries = buildList {
                    add(history())
                    if (includePlayer) add(playerSettings())
                    if (includeDesktop) add(desktopSettings())
                    if (includeRepositories) add(extensionRepositories())
                },
            ),
            ChimahonMoreMenuSectionState(
                key = "settings",
                title = "Settings",
                entries = listOf(
                    settings(),
                    dataAndStorage(),
                    about(),
                ),
            ),
        ).filter { it.entries.isNotEmpty() }
    }

    fun downloadedOnly(selected: Boolean): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "appMode.downloadedOnly",
            title = "Downloaded only",
            subtitle = "Hide entries that need a network connection.",
            icon = ChimahonMoreIcon.Download,
            kind = ChimahonMoreMenuEntryKind.Toggle,
            settingsKey = "appMode.downloadedOnly",
            selected = selected,
        )
    }

    fun incognitoMode(selected: Boolean): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "appMode.incognitoMode",
            title = "Incognito mode",
            subtitle = "Pause history updates while reading or watching.",
            icon = ChimahonMoreIcon.Security,
            kind = ChimahonMoreMenuEntryKind.Toggle,
            settingsKey = "appMode.incognitoMode",
            selected = selected,
        )
    }

    fun downloadQueue(label: String? = null): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "downloads.queue",
            title = "Download queue",
            subtitle = "Manga chapters and anime episodes waiting to download.",
            icon = ChimahonMoreIcon.Queue,
            value = label,
        )
    }

    fun categories(): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "library.categories",
            title = "Categories",
            subtitle = "Manage manga, anime and light novel categories.",
            icon = ChimahonMoreIcon.Categories,
        )
    }

    fun anime(): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "anime.library",
            title = "Anime",
            subtitle = "Anime library, updates, history and source browsing.",
            icon = ChimahonMoreIcon.Anime,
            kind = ChimahonMoreMenuEntryKind.Navigation,
            settingsKey = "animeLibrary.displayMode",
        )
    }

    fun lightNovels(): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "novels.library",
            title = "Light novels",
            subtitle = "Local EPUBs, novel sources and text reader settings.",
            icon = ChimahonMoreIcon.Novel,
            settingsKey = "reader.continuousMode",
        )
    }

    fun history(): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "history",
            title = "History",
            subtitle = "Recently read, watched and opened entries.",
            icon = ChimahonMoreIcon.History,
        )
    }

    fun playerSettings(): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "player.settings",
            title = "Player settings",
            subtitle = "Playback, subtitles, audio, gestures and decoder options.",
            icon = ChimahonMoreIcon.Player,
            kind = ChimahonMoreMenuEntryKind.Settings,
            settingsKey = "player.autoplayEnabled",
        )
    }

    fun desktopSettings(): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "desktop.settings",
            title = "Desktop settings",
            subtitle = "Large-screen navigation, shortcuts and reader controls.",
            icon = ChimahonMoreIcon.Desktop,
            kind = ChimahonMoreMenuEntryKind.Settings,
            settingsKey = "appearance.tabletUiMode",
        )
    }

    fun extensionRepositories(
        countLabel: String? = null,
        warning: Boolean = false,
    ): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "browse.extensionRepositories",
            title = "Extension repositories",
            subtitle = "Add, sync, disable or remove source repositories.",
            icon = ChimahonMoreIcon.Repository,
            kind = ChimahonMoreMenuEntryKind.Repository,
            settingsKey = "browse.disabledExtensionRepoUrls",
            badge = countLabel,
            warning = warning,
        )
    }

    fun removeRepository(repositoryName: String? = null): ChimahonMoreMenuEntry {
        val target = repositoryName?.takeIf { it.isNotBlank() } ?: "repository"
        return ChimahonMoreMenuEntry(
            key = "repository.remove",
            title = "Remove repository",
            subtitle = "Remove $target. Installed extensions stay installed.",
            icon = ChimahonMoreIcon.Delete,
            kind = ChimahonMoreMenuEntryKind.Destructive,
            settingsKey = "browse.disabledExtensionRepoUrls",
            warning = true,
            action = ChimahonMorePreferenceAction("Remove"),
        )
    }

    fun settings(): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "settings",
            title = "Settings",
            subtitle = "Configure app behavior and appearance.",
            icon = ChimahonMoreIcon.Settings,
            kind = ChimahonMoreMenuEntryKind.Settings,
        )
    }

    fun dataAndStorage(): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "data.storage",
            title = "Data and storage",
            subtitle = "Backups, cache, database maintenance and downloads.",
            icon = ChimahonMoreIcon.Storage,
            kind = ChimahonMoreMenuEntryKind.Settings,
            settingsKey = "data.includeSettingsInBackups",
        )
    }

    fun about(): ChimahonMoreMenuEntry {
        return ChimahonMoreMenuEntry(
            key = "about",
            title = "About Chimahon",
            subtitle = "Version, licenses and project information.",
            icon = ChimahonMoreIcon.About,
        )
    }
}
