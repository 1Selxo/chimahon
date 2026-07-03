package app.chimahon.shared.desktopshortcutsui

enum class ChimahonDesktopShortcutArea(val title: String) {
    Navigation("Navigation"),
    Reader("Reader"),
    Player("Player"),
    Browse("Browse"),
    Library("Library"),
    Anime("Anime"),
    LightNovel("LN"),
}

enum class ChimahonDesktopShortcutPlatform {
    Windows,
    Mac,
    Linux,
    ChromeOs,
    Unknown,
}

enum class ChimahonDesktopShortcutModifier {
    Primary,
    Control,
    Meta,
    Alt,
    Shift,
    Function,
}

data class ChimahonDesktopShortcutModifierLabels(
    val primary: String,
    val control: String,
    val meta: String,
    val alt: String = "Alt",
    val shift: String = "Shift",
    val function: String = "Fn",
    val separator: String = "+",
    val chordSeparator: String = " then ",
) {
    fun labelFor(modifier: ChimahonDesktopShortcutModifier): String {
        return when (modifier) {
            ChimahonDesktopShortcutModifier.Primary -> primary
            ChimahonDesktopShortcutModifier.Control -> control
            ChimahonDesktopShortcutModifier.Meta -> meta
            ChimahonDesktopShortcutModifier.Alt -> alt
            ChimahonDesktopShortcutModifier.Shift -> shift
            ChimahonDesktopShortcutModifier.Function -> function
        }
    }

    companion object {
        fun forPlatform(platform: ChimahonDesktopShortcutPlatform): ChimahonDesktopShortcutModifierLabels {
            return when (platform) {
                ChimahonDesktopShortcutPlatform.Mac -> ChimahonDesktopShortcutModifierLabels(
                    primary = "Cmd",
                    control = "Ctrl",
                    meta = "Cmd",
                    alt = "Opt",
                )
                ChimahonDesktopShortcutPlatform.Linux -> ChimahonDesktopShortcutModifierLabels(
                    primary = "Ctrl",
                    control = "Ctrl",
                    meta = "Super",
                )
                ChimahonDesktopShortcutPlatform.ChromeOs -> ChimahonDesktopShortcutModifierLabels(
                    primary = "Ctrl",
                    control = "Ctrl",
                    meta = "Search",
                )
                ChimahonDesktopShortcutPlatform.Windows,
                ChimahonDesktopShortcutPlatform.Unknown,
                -> ChimahonDesktopShortcutModifierLabels(
                    primary = "Ctrl",
                    control = "Ctrl",
                    meta = "Win",
                )
            }
        }
    }
}

data class ChimahonDesktopShortcutKeyStroke(
    val key: String,
    val modifiers: Set<ChimahonDesktopShortcutModifier> = emptySet(),
    val platformKeyLabels: Map<ChimahonDesktopShortcutPlatform, String> = emptyMap(),
) {
    fun displayLabel(
        labels: ChimahonDesktopShortcutModifierLabels,
        platform: ChimahonDesktopShortcutPlatform = ChimahonDesktopShortcutPlatform.Unknown,
    ): String {
        val keyLabel = platformKeyLabels[platform] ?: key
        val modifierLabels = ChimahonDesktopShortcutModifierDisplayOrder
            .filter { it in modifiers }
            .map(labels::labelFor)
        return (modifierLabels + keyLabel).joinToString(labels.separator)
    }
}

data class ChimahonDesktopShortcutChord(
    val strokes: List<ChimahonDesktopShortcutKeyStroke>,
) {
    fun displayLabel(
        labels: ChimahonDesktopShortcutModifierLabels,
        platform: ChimahonDesktopShortcutPlatform = ChimahonDesktopShortcutPlatform.Unknown,
    ): String {
        return strokes.joinToString(labels.chordSeparator) { stroke ->
            stroke.displayLabel(labels = labels, platform = platform)
        }
    }

    companion object {
        fun single(
            key: String,
            vararg modifiers: ChimahonDesktopShortcutModifier,
        ): ChimahonDesktopShortcutChord {
            return ChimahonDesktopShortcutChord(
                strokes = listOf(
                    ChimahonDesktopShortcutKeyStroke(
                        key = key,
                        modifiers = modifiers.toSet(),
                    ),
                ),
            )
        }
    }
}

fun ChimahonDesktopShortcutChord.displayLabel(
    platform: ChimahonDesktopShortcutPlatform,
): String {
    return displayLabel(
        labels = ChimahonDesktopShortcutModifierLabels.forPlatform(platform),
        platform = platform,
    )
}

enum class ChimahonDesktopShortcutConflictSeverity(val title: String) {
    Info("Info"),
    Warning("Warning"),
    Error("Conflict"),
}

data class ChimahonDesktopShortcutConflictBadge(
    val label: String,
    val severity: ChimahonDesktopShortcutConflictSeverity = ChimahonDesktopShortcutConflictSeverity.Warning,
    val detail: String? = null,
    val conflictingShortcutIds: Set<String> = emptySet(),
)

data class ChimahonDesktopShortcutRowModel(
    val id: String,
    val title: String,
    val description: String? = null,
    val shortcut: ChimahonDesktopShortcutChord? = null,
    val alternateShortcuts: List<ChimahonDesktopShortcutChord> = emptyList(),
    val areas: Set<ChimahonDesktopShortcutArea> = emptySet(),
    val enabled: Boolean = true,
    val conflictBadges: List<ChimahonDesktopShortcutConflictBadge> = emptyList(),
    val tags: Set<String> = emptySet(),
) {
    val shortcuts: List<ChimahonDesktopShortcutChord>
        get() = listOfNotNull(shortcut) + alternateShortcuts
}

data class ChimahonDesktopShortcutSectionModel(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val areas: Set<ChimahonDesktopShortcutArea> = emptySet(),
    val rows: List<ChimahonDesktopShortcutRowModel> = emptyList(),
)

data class ChimahonDesktopShortcutCheatsheetState(
    val title: String = "Keyboard shortcuts",
    val subtitle: String? = "Reader, player, browse, library, anime, and LN controls",
    val sections: List<ChimahonDesktopShortcutSectionModel> = emptyList(),
    val query: String = "",
    val selectedArea: ChimahonDesktopShortcutArea? = null,
    val platform: ChimahonDesktopShortcutPlatform = ChimahonDesktopShortcutPlatform.Unknown,
    val modifierLabels: ChimahonDesktopShortcutModifierLabels =
        ChimahonDesktopShortcutModifierLabels.forPlatform(platform),
    val emptyTitle: String = "No shortcuts found",
    val emptySubtitle: String = "Try another search or clear the selected area.",
)

val ChimahonDesktopShortcutModifierDisplayOrder: List<ChimahonDesktopShortcutModifier> = listOf(
    ChimahonDesktopShortcutModifier.Primary,
    ChimahonDesktopShortcutModifier.Control,
    ChimahonDesktopShortcutModifier.Meta,
    ChimahonDesktopShortcutModifier.Alt,
    ChimahonDesktopShortcutModifier.Shift,
    ChimahonDesktopShortcutModifier.Function,
)

fun chimahonDesktopShortcutKeyStroke(
    key: String,
    vararg modifiers: ChimahonDesktopShortcutModifier,
): ChimahonDesktopShortcutKeyStroke {
    return ChimahonDesktopShortcutKeyStroke(key = key, modifiers = modifiers.toSet())
}

fun chimahonDesktopShortcutChord(
    key: String,
    vararg modifiers: ChimahonDesktopShortcutModifier,
): ChimahonDesktopShortcutChord {
    return ChimahonDesktopShortcutChord.single(key, *modifiers)
}

fun List<ChimahonDesktopShortcutSectionModel>.filterChimahonDesktopShortcutSections(
    query: String,
    area: ChimahonDesktopShortcutArea? = null,
    platform: ChimahonDesktopShortcutPlatform = ChimahonDesktopShortcutPlatform.Unknown,
    modifierLabels: ChimahonDesktopShortcutModifierLabels =
        ChimahonDesktopShortcutModifierLabels.forPlatform(platform),
): List<ChimahonDesktopShortcutSectionModel> {
    val normalizedQuery = query.trim()
    return mapNotNull { section ->
        if (!section.areas.appliesTo(area)) return@mapNotNull null
        val sectionMatchesQuery = section.matchesQuery(normalizedQuery)
        val rows = section.rows.filter { row ->
            row.appliesTo(area = area, sectionAreas = section.areas) &&
                (
                    normalizedQuery.isBlank() ||
                        sectionMatchesQuery ||
                        row.matchesQuery(
                            query = normalizedQuery,
                            platform = platform,
                            modifierLabels = modifierLabels,
                        )
                    )
        }
        if (rows.isEmpty()) null else section.copy(rows = rows)
    }
}

fun ChimahonDesktopShortcutCheatsheetState.availableAreas(): List<ChimahonDesktopShortcutArea> {
    val areas = sections
        .flatMap { section -> section.areas + section.rows.flatMap { it.areas } }
        .toSet()
    return ChimahonDesktopShortcutArea.values().filter { it in areas }
}

private fun Set<ChimahonDesktopShortcutArea>.appliesTo(area: ChimahonDesktopShortcutArea?): Boolean {
    return area == null || isEmpty() || area in this
}

private fun ChimahonDesktopShortcutRowModel.appliesTo(
    area: ChimahonDesktopShortcutArea?,
    sectionAreas: Set<ChimahonDesktopShortcutArea>,
): Boolean {
    return when {
        area == null -> true
        areas.isNotEmpty() -> area in areas
        sectionAreas.isNotEmpty() -> area in sectionAreas
        else -> true
    }
}

private fun ChimahonDesktopShortcutSectionModel.matchesQuery(query: String): Boolean {
    return query.isNotBlank() &&
        (
            title.contains(query, ignoreCase = true) ||
                subtitle.orEmpty().contains(query, ignoreCase = true) ||
                areas.any { it.title.contains(query, ignoreCase = true) }
            )
}

private fun ChimahonDesktopShortcutRowModel.matchesQuery(
    query: String,
    platform: ChimahonDesktopShortcutPlatform,
    modifierLabels: ChimahonDesktopShortcutModifierLabels,
): Boolean {
    if (query.isBlank()) return true
    return title.contains(query, ignoreCase = true) ||
        description.orEmpty().contains(query, ignoreCase = true) ||
        tags.any { it.contains(query, ignoreCase = true) } ||
        areas.any { it.title.contains(query, ignoreCase = true) } ||
        conflictBadges.any { badge ->
            badge.label.contains(query, ignoreCase = true) ||
                badge.detail.orEmpty().contains(query, ignoreCase = true)
        } ||
        shortcuts.any { shortcut ->
            shortcut.displayLabel(labels = modifierLabels, platform = platform)
                .contains(query, ignoreCase = true)
        }
}
