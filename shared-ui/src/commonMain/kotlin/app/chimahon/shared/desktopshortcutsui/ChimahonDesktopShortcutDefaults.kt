package app.chimahon.shared.desktopshortcutsui

object ChimahonDesktopShortcutDefaults {
    val areaOrder: List<ChimahonDesktopShortcutArea> = listOf(
        ChimahonDesktopShortcutArea.Navigation,
        ChimahonDesktopShortcutArea.Reader,
        ChimahonDesktopShortcutArea.Player,
        ChimahonDesktopShortcutArea.Browse,
        ChimahonDesktopShortcutArea.Library,
        ChimahonDesktopShortcutArea.Anime,
        ChimahonDesktopShortcutArea.LightNovel,
    )

    fun modifierLabels(
        platform: ChimahonDesktopShortcutPlatform,
    ): ChimahonDesktopShortcutModifierLabels {
        return ChimahonDesktopShortcutModifierLabels.forPlatform(platform)
    }

    fun conflictBadge(
        label: String = "Conflict",
        detail: String? = null,
        severity: ChimahonDesktopShortcutConflictSeverity =
            ChimahonDesktopShortcutConflictSeverity.Warning,
        conflictingShortcutIds: Set<String> = emptySet(),
    ): ChimahonDesktopShortcutConflictBadge {
        return ChimahonDesktopShortcutConflictBadge(
            label = label,
            detail = detail,
            severity = severity,
            conflictingShortcutIds = conflictingShortcutIds,
        )
    }
}
