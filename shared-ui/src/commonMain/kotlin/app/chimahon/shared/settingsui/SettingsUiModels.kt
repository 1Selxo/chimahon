package app.chimahon.shared.settingsui

enum class ChimahonSettingsRoute(
    val key: String,
    val title: String,
    val subtitle: String,
) {
    Appearance(
        key = "appearance",
        title = "Appearance",
        subtitle = "Theme, navigation, display density",
    ),
    Library(
        key = "library",
        title = "Library",
        subtitle = "Display, badges, updates, gestures",
    ),
    Anime(
        key = "anime",
        title = "Anime",
        subtitle = "Library, episodes, player defaults",
    ),
    Reader(
        key = "reader",
        title = "Reader",
        subtitle = "Reading mode, controls, OCR, lookup",
    ),
    Downloads(
        key = "downloads",
        title = "Downloads",
        subtitle = "Storage, cleanup, queue behavior",
    ),
    Tracking(
        key = "tracking",
        title = "Tracking",
        subtitle = "Sync, progress, tracker automation",
    ),
    Browse(
        key = "browse",
        title = "Browse",
        subtitle = "Sources, extensions, repositories",
    ),
    Advanced(
        key = "advanced",
        title = "Advanced",
        subtitle = "Security, dictionary, data maintenance",
    ),
    Backup(
        key = "backup",
        title = "Backup and restore",
        subtitle = "Backup schedule, restore scope",
    ),
}

data class ChimahonSettingsUiState(
    val screens: List<ChimahonSettingsScreen>,
    val selectedRoute: ChimahonSettingsRoute = ChimahonSettingsRoute.Appearance,
) {
    val selectedScreen: ChimahonSettingsScreen?
        get() = screens.firstOrNull { it.route == selectedRoute }
}

data class ChimahonSettingsScreen(
    val route: ChimahonSettingsRoute,
    val title: String = route.title,
    val subtitle: String = route.subtitle,
    val sections: List<ChimahonSettingsSection> = emptyList(),
)

data class ChimahonSettingsSection(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val preferences: List<ChimahonPreference> = emptyList(),
)

enum class ChimahonSettingsEntryKind {
    Anime,
    LightNovel,
    Player,
    Desktop,
    Repository,
    RemoveRepository,
    Settings,
}

data class ChimahonSettingsEntryRow(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val kind: ChimahonSettingsEntryKind = ChimahonSettingsEntryKind.Settings,
    val settingsKey: String = key,
    val route: ChimahonSettingsRoute? = null,
    val value: String? = null,
    val actionLabel: String? = null,
    val enabled: Boolean = true,
    val destructive: Boolean = false,
) {
    fun asPreference(): ChimahonPreference {
        return if (route != null) {
            ChimahonPreference.NestedScreen(
                key = key,
                title = title,
                subtitle = subtitle,
                route = route,
                enabled = enabled,
            )
        } else {
            ChimahonPreference.Action(
                key = key,
                title = title,
                subtitle = subtitle ?: value,
                actionLabel = actionLabel,
                enabled = enabled,
                destructive = destructive,
            )
        }
    }
}

object ChimahonSettingsEntryRows {
    fun androidParityRows(
        includeAnime: Boolean = true,
        includeLightNovels: Boolean = true,
        includePlayer: Boolean = true,
        includeDesktop: Boolean = true,
        includeRepositories: Boolean = true,
        includeRemoveRepository: Boolean = false,
    ): List<ChimahonSettingsEntryRow> {
        return buildList {
            add(settings())
            if (includeAnime) add(anime())
            if (includePlayer) add(player())
            if (includeLightNovels) add(lightNovels())
            if (includeDesktop) add(desktop())
            if (includeRepositories) add(extensionRepositories())
            if (includeRemoveRepository) add(removeRepository())
        }
    }

    fun settings(): ChimahonSettingsEntryRow {
        return ChimahonSettingsEntryRow(
            key = "settings.root",
            title = "Settings",
            subtitle = "Open the full settings list.",
            route = ChimahonSettingsRoute.Appearance,
        )
    }

    fun anime(): ChimahonSettingsEntryRow {
        return ChimahonSettingsEntryRow(
            key = "animeLibrary.settings",
            title = "Anime settings",
            subtitle = "Library display, episode defaults, updates and player behavior.",
            kind = ChimahonSettingsEntryKind.Anime,
            settingsKey = "animeLibrary.displayMode",
            route = ChimahonSettingsRoute.Anime,
        )
    }

    fun lightNovels(): ChimahonSettingsEntryRow {
        return ChimahonSettingsEntryRow(
            key = "reader.novel",
            title = "Light novel settings",
            subtitle = "Typography, layout, lookup and progress controls.",
            kind = ChimahonSettingsEntryKind.LightNovel,
            settingsKey = "reader.continuousMode",
            route = ChimahonSettingsRoute.Reader,
        )
    }

    fun player(): ChimahonSettingsEntryRow {
        return ChimahonSettingsEntryRow(
            key = "player.settings",
            title = "Player settings",
            subtitle = "Playback, gestures, subtitles, audio and decoder options.",
            kind = ChimahonSettingsEntryKind.Player,
            settingsKey = "player.autoplayEnabled",
            route = ChimahonSettingsRoute.Anime,
        )
    }

    fun desktop(): ChimahonSettingsEntryRow {
        return ChimahonSettingsEntryRow(
            key = "desktop.settings",
            title = "Desktop settings",
            subtitle = "Large-screen navigation, keyboard shortcuts and reader controls.",
            kind = ChimahonSettingsEntryKind.Desktop,
            settingsKey = "appearance.tabletUiMode",
            route = ChimahonSettingsRoute.Appearance,
        )
    }

    fun extensionRepositories(
        count: Int? = null,
        disabledCount: Int = 0,
    ): ChimahonSettingsEntryRow {
        val value = count?.let { total ->
            if (disabledCount > 0) "$total total, $disabledCount disabled" else "$total total"
        }
        return ChimahonSettingsEntryRow(
            key = "browse.extensionRepositories",
            title = "Extension repositories",
            subtitle = "Add, sync, disable or remove source repositories.",
            kind = ChimahonSettingsEntryKind.Repository,
            settingsKey = "browse.disabledExtensionRepoUrls",
            route = ChimahonSettingsRoute.Browse,
            value = value,
        )
    }

    fun removeRepository(
        repositoryName: String? = null,
        enabled: Boolean = true,
    ): ChimahonSettingsEntryRow {
        val target = repositoryName?.takeIf { it.isNotBlank() } ?: "repository"
        return ChimahonSettingsEntryRow(
            key = "repository.remove",
            title = "Remove repository",
            subtitle = "Remove $target from extension repositories. Installed extensions stay installed.",
            kind = ChimahonSettingsEntryKind.RemoveRepository,
            settingsKey = "browse.disabledExtensionRepoUrls",
            actionLabel = "Remove",
            enabled = enabled,
            destructive = true,
        )
    }
}

fun Iterable<ChimahonSettingsEntryRow>.asPreferences(): List<ChimahonPreference> {
    return map { it.asPreference() }
}

data class ChimahonPreferenceOption(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val enabled: Boolean = true,
)

data class ChimahonSliderSpec(
    val value: Float,
    val valueRange: ClosedFloatingPointRange<Float>,
    val steps: Int = 0,
    val valueLabel: String = value.toReadableSliderLabel(),
)

enum class ChimahonPreferenceTone {
    Info,
    Warning,
    Error,
    Success,
}

sealed class ChimahonPreference {
    abstract val key: String
    abstract val title: String
    abstract val subtitle: String?
    abstract val enabled: Boolean

    data class Switch(
        override val key: String,
        override val title: String,
        val checked: Boolean,
        override val subtitle: String? = null,
        override val enabled: Boolean = true,
    ) : ChimahonPreference()

    data class Slider(
        override val key: String,
        override val title: String,
        val spec: ChimahonSliderSpec,
        override val subtitle: String? = null,
        override val enabled: Boolean = true,
    ) : ChimahonPreference()

    data class ListSelect(
        override val key: String,
        override val title: String,
        val selectedKey: String,
        val options: List<ChimahonPreferenceOption>,
        override val subtitle: String? = null,
        override val enabled: Boolean = true,
    ) : ChimahonPreference() {
        val selectedTitle: String
            get() = options.firstOrNull { it.key == selectedKey }?.title ?: selectedKey
    }

    data class MultiSelect(
        override val key: String,
        override val title: String,
        val selectedKeys: Set<String>,
        val options: List<ChimahonPreferenceOption>,
        override val subtitle: String? = null,
        override val enabled: Boolean = true,
        val emptyLabel: String = "None",
    ) : ChimahonPreference() {
        val selectedTitle: String
            get() {
                val selected = options.filter { it.key in selectedKeys }
                return selected.takeIf { it.isNotEmpty() }
                    ?.joinToString { it.title }
                    ?: emptyLabel
            }
    }

    data class Info(
        override val key: String,
        override val title: String,
        override val subtitle: String? = null,
        val tone: ChimahonPreferenceTone = ChimahonPreferenceTone.Info,
        override val enabled: Boolean = true,
    ) : ChimahonPreference()

    data class Action(
        override val key: String,
        override val title: String,
        override val subtitle: String? = null,
        val actionLabel: String? = null,
        override val enabled: Boolean = true,
        val destructive: Boolean = false,
    ) : ChimahonPreference()

    data class NestedScreen(
        override val key: String,
        override val title: String,
        val route: ChimahonSettingsRoute,
        override val subtitle: String? = null,
        override val enabled: Boolean = true,
    ) : ChimahonPreference()
}

sealed class ChimahonSettingsUiEvent {
    data class Navigate(val route: ChimahonSettingsRoute) : ChimahonSettingsUiEvent()
    data class ToggleChanged(val key: String, val checked: Boolean) : ChimahonSettingsUiEvent()
    data class SliderChanged(val key: String, val value: Float) : ChimahonSettingsUiEvent()
    data class OptionSelected(val key: String, val optionKey: String) : ChimahonSettingsUiEvent()
    data class MultiOptionToggled(
        val key: String,
        val optionKey: String,
        val selected: Boolean,
    ) : ChimahonSettingsUiEvent()

    data class ActionClicked(val key: String) : ChimahonSettingsUiEvent()
}

internal fun Float.toReadableSliderLabel(): String {
    val rounded = kotlin.math.round(this * 10f) / 10f
    return if (rounded % 1f == 0f) {
        rounded.toInt().toString()
    } else {
        rounded.toString()
    }
}
