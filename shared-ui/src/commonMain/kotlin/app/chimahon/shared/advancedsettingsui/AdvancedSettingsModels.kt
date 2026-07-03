package app.chimahon.shared.advancedsettingsui

import androidx.compose.ui.graphics.Color

enum class ChimahonAdvancedSettingsRoute(
    val key: String,
    val title: String,
    val subtitle: String,
) {
    Overview(
        key = "overview",
        title = "Settings",
        subtitle = "Search and manage app settings",
    ),
    Appearance(
        key = "appearance",
        title = "Appearance",
        subtitle = "Theme, icons, navigation, density",
    ),
    BackupRestore(
        key = "backup_restore",
        title = "Backup and restore",
        subtitle = "Schedules, restore scope, export files",
    ),
    DataStorage(
        key = "data_storage",
        title = "Data and storage",
        subtitle = "Cache, downloads, database maintenance",
    ),
    SecurityPrivacy(
        key = "security_privacy",
        title = "Security and privacy",
        subtitle = "Incognito, analytics, extension safety",
    ),
    NavigationEditor(
        key = "navigation_editor",
        title = "Navigation editor",
        subtitle = "Choose tabs and desktop destinations",
    ),
    AnimeAndNovel(
        key = "anime_novel",
        title = "Anime and novels",
        subtitle = "Player, subtitles, LN reader defaults",
    ),
    Diagnostics(
        key = "diagnostics",
        title = "Diagnostics",
        subtitle = "Logs, health checks, platform details",
    ),
}

enum class ChimahonAdvancedSettingCategory(
    val key: String,
    val label: String,
) {
    Appearance("appearance", "Appearance"),
    Backup("backup", "Backup"),
    Browse("browse", "Browse"),
    Data("data", "Data"),
    Downloads("downloads", "Downloads"),
    Extensions("extensions", "Extensions"),
    Library("library", "Library"),
    Reader("reader", "Reader"),
    Player("player", "Player"),
    Anime("anime", "Anime"),
    Novel("novel", "Novels"),
    Navigation("navigation", "Navigation"),
    Security("security", "Security"),
    Privacy("privacy", "Privacy"),
    Diagnostics("diagnostics", "Diagnostics"),
}

enum class ChimahonAdvancedSettingTone {
    Neutral,
    Info,
    Success,
    Warning,
    Error,
}

enum class ChimahonAdvancedSettingIcon {
    Backup,
    Restore,
    Storage,
    Palette,
    Navigation,
    Security,
    Privacy,
    Diagnostics,
    Sync,
    Delete,
    Search,
    Visibility,
    Hidden,
    Settings,
    Download,
    Lock,
    Tune,
}

data class ChimahonAdvancedOption(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val enabled: Boolean = true,
)

data class ChimahonAdvancedProgress(
    val label: String,
    val fraction: Float? = null,
)

sealed class ChimahonAdvancedSettingItem {
    abstract val key: String
    abstract val title: String
    abstract val subtitle: String?
    abstract val icon: ChimahonAdvancedSettingIcon?
    abstract val category: ChimahonAdvancedSettingCategory
    abstract val tone: ChimahonAdvancedSettingTone
    abstract val enabled: Boolean
    abstract val tags: Set<String>

    data class Toggle(
        override val key: String,
        override val title: String,
        val checked: Boolean,
        override val subtitle: String? = null,
        override val icon: ChimahonAdvancedSettingIcon? = null,
        override val category: ChimahonAdvancedSettingCategory,
        override val tone: ChimahonAdvancedSettingTone = ChimahonAdvancedSettingTone.Neutral,
        override val enabled: Boolean = true,
        override val tags: Set<String> = emptySet(),
    ) : ChimahonAdvancedSettingItem()

    data class Choice(
        override val key: String,
        override val title: String,
        val selectedKey: String,
        val options: List<ChimahonAdvancedOption>,
        override val subtitle: String? = null,
        override val icon: ChimahonAdvancedSettingIcon? = null,
        override val category: ChimahonAdvancedSettingCategory,
        override val tone: ChimahonAdvancedSettingTone = ChimahonAdvancedSettingTone.Neutral,
        override val enabled: Boolean = true,
        override val tags: Set<String> = emptySet(),
    ) : ChimahonAdvancedSettingItem() {
        val selectedTitle: String
            get() = options.firstOrNull { it.key == selectedKey }?.title ?: selectedKey
    }

    data class MultiChoice(
        override val key: String,
        override val title: String,
        val selectedKeys: Set<String>,
        val options: List<ChimahonAdvancedOption>,
        override val subtitle: String? = null,
        override val icon: ChimahonAdvancedSettingIcon? = null,
        override val category: ChimahonAdvancedSettingCategory,
        override val tone: ChimahonAdvancedSettingTone = ChimahonAdvancedSettingTone.Neutral,
        override val enabled: Boolean = true,
        override val tags: Set<String> = emptySet(),
        val emptyLabel: String = "None",
    ) : ChimahonAdvancedSettingItem() {
        val selectedTitle: String
            get() {
                val selected = options.filter { it.key in selectedKeys }
                return selected.takeIf { it.isNotEmpty() }
                    ?.joinToString { option -> option.title }
                    ?: emptyLabel
            }
    }

    data class Slider(
        override val key: String,
        override val title: String,
        val value: Float,
        val valueRange: ClosedFloatingPointRange<Float>,
        val steps: Int = 0,
        val valueLabel: String = value.toAdvancedReadableLabel(),
        override val subtitle: String? = null,
        override val icon: ChimahonAdvancedSettingIcon? = null,
        override val category: ChimahonAdvancedSettingCategory,
        override val tone: ChimahonAdvancedSettingTone = ChimahonAdvancedSettingTone.Neutral,
        override val enabled: Boolean = true,
        override val tags: Set<String> = emptySet(),
    ) : ChimahonAdvancedSettingItem()

    data class Action(
        override val key: String,
        override val title: String,
        val actionLabel: String,
        override val subtitle: String? = null,
        override val icon: ChimahonAdvancedSettingIcon? = null,
        override val category: ChimahonAdvancedSettingCategory,
        override val tone: ChimahonAdvancedSettingTone = ChimahonAdvancedSettingTone.Neutral,
        override val enabled: Boolean = true,
        override val tags: Set<String> = emptySet(),
        val progress: ChimahonAdvancedProgress? = null,
    ) : ChimahonAdvancedSettingItem()

    data class Info(
        override val key: String,
        override val title: String,
        val value: String? = null,
        override val subtitle: String? = null,
        override val icon: ChimahonAdvancedSettingIcon? = null,
        override val category: ChimahonAdvancedSettingCategory,
        override val tone: ChimahonAdvancedSettingTone = ChimahonAdvancedSettingTone.Info,
        override val enabled: Boolean = true,
        override val tags: Set<String> = emptySet(),
    ) : ChimahonAdvancedSettingItem()

    data class Link(
        override val key: String,
        override val title: String,
        val route: ChimahonAdvancedSettingsRoute,
        override val subtitle: String? = null,
        override val icon: ChimahonAdvancedSettingIcon? = null,
        override val category: ChimahonAdvancedSettingCategory,
        override val tone: ChimahonAdvancedSettingTone = ChimahonAdvancedSettingTone.Neutral,
        override val enabled: Boolean = true,
        override val tags: Set<String> = emptySet(),
    ) : ChimahonAdvancedSettingItem()
}

data class ChimahonAdvancedSettingsGroup(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val items: List<ChimahonAdvancedSettingItem> = emptyList(),
)

data class ChimahonAdvancedSettingsPage(
    val route: ChimahonAdvancedSettingsRoute,
    val title: String = route.title,
    val subtitle: String = route.subtitle,
    val groups: List<ChimahonAdvancedSettingsGroup> = emptyList(),
)

data class ChimahonAdvancedSettingsState(
    val pages: List<ChimahonAdvancedSettingsPage>,
    val selectedRoute: ChimahonAdvancedSettingsRoute = ChimahonAdvancedSettingsRoute.Overview,
    val query: String = "",
    val activeCategories: Set<ChimahonAdvancedSettingCategory> = emptySet(),
) {
    val selectedPage: ChimahonAdvancedSettingsPage?
        get() = pages.firstOrNull { it.route == selectedRoute }

    val visiblePages: List<ChimahonAdvancedSettingsPage>
        get() = pages.mapNotNull { page ->
            val groups = page.groups.mapNotNull { group ->
                val items = group.items.filter { item ->
                    item.matches(query = query, activeCategories = activeCategories)
                }
                if (items.isEmpty()) null else group.copy(items = items)
            }
            if (groups.isEmpty()) null else page.copy(groups = groups)
        }
}

data class ChimahonBackupScopeItem(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val selected: Boolean = true,
    val required: Boolean = false,
)

data class ChimahonBackupRestoreState(
    val lastBackupLabel: String? = null,
    val nextBackupLabel: String? = null,
    val automaticBackupsEnabled: Boolean = false,
    val includePrivateData: Boolean = false,
    val scopes: List<ChimahonBackupScopeItem> = emptyList(),
    val restorePreview: List<ChimahonDiagnosticRow> = emptyList(),
    val inProgress: ChimahonAdvancedProgress? = null,
)

data class ChimahonMaintenanceRow(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val sizeLabel: String? = null,
    val actionLabel: String,
    val icon: ChimahonAdvancedSettingIcon = ChimahonAdvancedSettingIcon.Storage,
    val tone: ChimahonAdvancedSettingTone = ChimahonAdvancedSettingTone.Neutral,
    val enabled: Boolean = true,
)

data class ChimahonThemeSwatch(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val selected: Boolean = false,
    val enabled: Boolean = true,
    val primary: Color,
    val secondary: Color,
    val background: Color,
)

enum class ChimahonNavigationDestination {
    Hidden,
    BottomBar,
    Rail,
    More,
}

data class ChimahonNavigationTabItem(
    val key: String,
    val title: String,
    val subtitle: String? = null,
    val icon: ChimahonAdvancedSettingIcon = ChimahonAdvancedSettingIcon.Navigation,
    val destination: ChimahonNavigationDestination,
    val enabled: Boolean = true,
    val canMoveUp: Boolean = true,
    val canMoveDown: Boolean = true,
)

data class ChimahonSecurityToggle(
    val key: String,
    val title: String,
    val checked: Boolean,
    val subtitle: String? = null,
    val icon: ChimahonAdvancedSettingIcon = ChimahonAdvancedSettingIcon.Security,
    val tone: ChimahonAdvancedSettingTone = ChimahonAdvancedSettingTone.Neutral,
    val enabled: Boolean = true,
)

enum class ChimahonDiagnosticStatus {
    Healthy,
    Warning,
    Error,
    Unknown,
}

data class ChimahonDiagnosticRow(
    val key: String,
    val title: String,
    val value: String,
    val subtitle: String? = null,
    val status: ChimahonDiagnosticStatus = ChimahonDiagnosticStatus.Unknown,
    val actionLabel: String? = null,
)

sealed class ChimahonAdvancedSettingsEvent {
    data class Navigate(val route: ChimahonAdvancedSettingsRoute) : ChimahonAdvancedSettingsEvent()
    data class SearchChanged(val query: String) : ChimahonAdvancedSettingsEvent()
    data class CategoryToggled(
        val category: ChimahonAdvancedSettingCategory,
        val selected: Boolean,
    ) : ChimahonAdvancedSettingsEvent()

    data class ToggleChanged(val key: String, val checked: Boolean) : ChimahonAdvancedSettingsEvent()
    data class ChoiceSelected(val key: String, val optionKey: String) : ChimahonAdvancedSettingsEvent()
    data class MultiChoiceToggled(
        val key: String,
        val optionKey: String,
        val selected: Boolean,
    ) : ChimahonAdvancedSettingsEvent()

    data class SliderChanged(val key: String, val value: Float) : ChimahonAdvancedSettingsEvent()
    data class ActionClicked(val key: String) : ChimahonAdvancedSettingsEvent()
    data class BackupScopeChanged(val key: String, val selected: Boolean) : ChimahonAdvancedSettingsEvent()
    data class ThemeSelected(val key: String) : ChimahonAdvancedSettingsEvent()
    data class NavigationDestinationChanged(
        val key: String,
        val destination: ChimahonNavigationDestination,
    ) : ChimahonAdvancedSettingsEvent()

    data class NavigationMoveRequested(val key: String, val up: Boolean) : ChimahonAdvancedSettingsEvent()
    data class SecurityToggleChanged(val key: String, val checked: Boolean) : ChimahonAdvancedSettingsEvent()
    data class DiagnosticActionClicked(val key: String) : ChimahonAdvancedSettingsEvent()
}

fun ChimahonAdvancedSettingItem.matches(
    query: String,
    activeCategories: Set<ChimahonAdvancedSettingCategory>,
): Boolean {
    if (activeCategories.isNotEmpty() && category !in activeCategories) {
        return false
    }
    val normalizedQuery = query.trim().lowercase()
    if (normalizedQuery.isBlank()) {
        return true
    }
    val haystack = buildList {
        add(key)
        add(title)
        subtitle?.let(::add)
        add(category.label)
        addAll(tags)
        when (this@matches) {
            is ChimahonAdvancedSettingItem.Action -> add(actionLabel)
            is ChimahonAdvancedSettingItem.Choice -> {
                add(selectedTitle)
                options.forEach {
                    add(it.title)
                    it.subtitle?.let(::add)
                }
            }
            is ChimahonAdvancedSettingItem.Info -> value?.let(::add)
            is ChimahonAdvancedSettingItem.Link -> {
                add(route.title)
                add(route.subtitle)
            }
            is ChimahonAdvancedSettingItem.MultiChoice -> {
                add(selectedTitle)
                options.forEach {
                    add(it.title)
                    it.subtitle?.let(::add)
                }
            }
            is ChimahonAdvancedSettingItem.Slider -> add(valueLabel)
            is ChimahonAdvancedSettingItem.Toggle -> add(if (checked) "enabled" else "disabled")
        }
    }.joinToString(separator = " ").lowercase()
    return normalizedQuery in haystack
}

internal fun Float.toAdvancedReadableLabel(): String {
    val rounded = kotlin.math.round(this * 10f) / 10f
    return if (rounded % 1f == 0f) {
        rounded.toInt().toString()
    } else {
        rounded.toString()
    }
}
