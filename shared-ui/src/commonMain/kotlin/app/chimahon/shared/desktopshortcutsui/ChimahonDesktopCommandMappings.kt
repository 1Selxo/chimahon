package app.chimahon.shared.desktopshortcutsui

import app.chimahon.shared.ChimahonDesktopCommand

enum class ChimahonDesktopCommandInputKind(val title: String) {
    Keyboard("Keyboard"),
    MouseButton("Mouse button"),
    MouseWheel("Mouse wheel"),
    Touchpad("Touchpad"),
}

enum class ChimahonDesktopCommandFocusTarget(val title: String) {
    Global("Global"),
    CurrentScreen("Current screen"),
    BrowseContent("Browse content"),
    ReaderContent("Reader content"),
    PlayerSurface("Player surface"),
    LightNovelReader("Light novel reader"),
    TextSelection("Text selection"),
}

enum class ChimahonDesktopCommandScrollMode(val title: String) {
    None("No scroll behavior"),
    FocusedScrollable("Focused scrollable content"),
    ReaderPage("Reader page navigation"),
    ReaderChapter("Reader chapter navigation"),
    ReaderPageScroll("Reader page scroll"),
    BrowseGrid("Browse grid/list scroll"),
    PlayerSeek("Player seek"),
    LightNovelPage("LN page navigation"),
    LightNovelContinuous("LN continuous scroll"),
}

enum class ChimahonDesktopCommandZoomMode(val title: String) {
    None("No zoom behavior"),
    ReaderZoomIn("Reader zoom in"),
    ReaderZoomOut("Reader zoom out"),
    ReaderZoomReset("Reader zoom reset"),
    ReaderFitWidth("Reader fit width"),
    ReaderFitHeight("Reader fit height"),
    ReaderFitScreen("Reader fit screen"),
    ReaderToggleFit("Reader toggle fit"),
    LightNovelTextScale("LN text scale"),
}

data class ChimahonDesktopCommandFocusBehavior(
    val target: ChimahonDesktopCommandFocusTarget,
    val ignoreWhileTextInputFocused: Boolean = true,
    val keepContentFocusedAfterDispatch: Boolean = true,
    val consumesWhenPanelsOpen: Boolean = false,
)

data class ChimahonDesktopCommandScrollBehavior(
    val mode: ChimahonDesktopCommandScrollMode = ChimahonDesktopCommandScrollMode.None,
    val amountLabel: String? = null,
    val preservesPointerAnchor: Boolean = false,
)

data class ChimahonDesktopCommandZoomBehavior(
    val mode: ChimahonDesktopCommandZoomMode = ChimahonDesktopCommandZoomMode.None,
    val amountLabel: String? = null,
    val resetsScrollAnchor: Boolean = false,
)

data class ChimahonDesktopMouseCommandMapping(
    val label: String,
    val description: String,
    val inputKind: ChimahonDesktopCommandInputKind = ChimahonDesktopCommandInputKind.MouseButton,
    val modifiers: Set<ChimahonDesktopShortcutModifier> = emptySet(),
    val clickCount: Int = 1,
) {
    fun displayLabel(labels: ChimahonDesktopShortcutModifierLabels): String {
        val clickLabel = if (clickCount > 1) "$clickCount x $label" else label
        val modifierLabels = ChimahonDesktopShortcutModifierDisplayOrder
            .filter { it in modifiers }
            .map(labels::labelFor)
        return (modifierLabels + clickLabel).joinToString(labels.separator)
    }
}

data class ChimahonDesktopCommandMapping(
    val id: String,
    val title: String,
    val description: String,
    val command: ChimahonDesktopCommand? = null,
    val areas: Set<ChimahonDesktopShortcutArea>,
    val shortcuts: List<ChimahonDesktopShortcutChord> = emptyList(),
    val mouseMappings: List<ChimahonDesktopMouseCommandMapping> = emptyList(),
    val focusBehavior: ChimahonDesktopCommandFocusBehavior,
    val scrollBehavior: ChimahonDesktopCommandScrollBehavior = ChimahonDesktopCommandScrollBehavior(),
    val zoomBehavior: ChimahonDesktopCommandZoomBehavior = ChimahonDesktopCommandZoomBehavior(),
    val tags: Set<String> = emptySet(),
) {
    fun platformShortcutLabels(
        platform: ChimahonDesktopShortcutPlatform,
        labels: ChimahonDesktopShortcutModifierLabels =
            ChimahonDesktopShortcutModifierLabels.forPlatform(platform),
    ): ChimahonDesktopCommandShortcutLabels {
        return ChimahonDesktopCommandShortcutLabels(
            platform = platform,
            keyboard = shortcuts.map { it.displayLabel(labels = labels, platform = platform) },
            mouse = mouseMappings.map { it.displayLabel(labels) },
            focusBehavior = focusBehavior,
            scrollBehavior = scrollBehavior,
            zoomBehavior = zoomBehavior,
        )
    }
}

data class ChimahonDesktopCommandShortcutLabels(
    val platform: ChimahonDesktopShortcutPlatform,
    val keyboard: List<String>,
    val mouse: List<String>,
    val focusBehavior: ChimahonDesktopCommandFocusBehavior,
    val scrollBehavior: ChimahonDesktopCommandScrollBehavior,
    val zoomBehavior: ChimahonDesktopCommandZoomBehavior,
) {
    val all: List<String>
        get() = keyboard + mouse
}

data class ChimahonDesktopCommandMappingSection(
    val id: String,
    val title: String,
    val subtitle: String,
    val areas: Set<ChimahonDesktopShortcutArea>,
    val mappings: List<ChimahonDesktopCommandMapping>,
)

fun chimahonDesktopCommandMappingSections(): List<ChimahonDesktopCommandMappingSection> {
    val navigation = ChimahonDesktopShortcutArea.Navigation
    val reader = ChimahonDesktopShortcutArea.Reader
    val player = ChimahonDesktopShortcutArea.Player
    val browse = ChimahonDesktopShortcutArea.Browse
    val library = ChimahonDesktopShortcutArea.Library
    val anime = ChimahonDesktopShortcutArea.Anime
    val lightNovel = ChimahonDesktopShortcutArea.LightNovel

    return listOf(
        ChimahonDesktopCommandMappingSection(
            id = "navigation",
            title = "Navigation",
            subtitle = "Global commands that move between Chimahon surfaces",
            areas = setOf(navigation, library, browse, anime, lightNovel),
            mappings = listOf(
                commandMapping(
                    id = "nav.back",
                    title = "Back",
                    description = "Close the active panel, reader/player surface, or go back one screen.",
                    command = ChimahonDesktopCommand.Back,
                    areas = setOf(navigation, library, browse, anime, lightNovel),
                    shortcuts = listOf(shortcut("Esc"), shortcut("Left", alt)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.Global,
                    tags = setOf("close", "escape", "panel"),
                ),
                commandMapping(
                    id = "nav.search",
                    title = "Search",
                    description = "Focus search for the current library, browse, or history screen.",
                    command = ChimahonDesktopCommand.Search,
                    areas = setOf(navigation, library, browse, anime, lightNovel),
                    shortcuts = listOf(shortcut("F", primary)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.CurrentScreen,
                    tags = setOf("focus", "find"),
                ),
                commandMapping(
                    id = "nav.filters",
                    title = "Toggle filters",
                    description = "Show or hide filters for the current list or browse source.",
                    command = ChimahonDesktopCommand.ToggleFilters,
                    areas = setOf(navigation, library, browse, anime, lightNovel),
                    shortcuts = listOf(shortcut("F", primary, shift)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.CurrentScreen,
                    tags = setOf("filter", "sort"),
                ),
                commandMapping(
                    id = "nav.refresh",
                    title = "Refresh",
                    description = "Refresh the active library, source, history, or settings surface.",
                    command = ChimahonDesktopCommand.Refresh,
                    areas = setOf(navigation, library, browse, anime, lightNovel),
                    shortcuts = listOf(shortcut("F5"), shortcut("R", primary)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.CurrentScreen,
                    scrollMode = ChimahonDesktopCommandScrollMode.FocusedScrollable,
                    tags = setOf("reload", "sync"),
                ),
                commandMapping(
                    id = "nav.library",
                    title = "Manga library",
                    description = "Open the manga library tab.",
                    command = ChimahonDesktopCommand.Library,
                    areas = setOf(navigation, library),
                    shortcuts = listOf(shortcut("1", primary), shortcut("L", primary)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.Global,
                ),
                commandMapping(
                    id = "nav.novels",
                    title = "Light novels",
                    description = "Open the light novel library tab.",
                    command = ChimahonDesktopCommand.Novels,
                    areas = setOf(navigation, lightNovel),
                    shortcuts = listOf(shortcut("N", primary)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.Global,
                ),
                commandMapping(
                    id = "nav.updates",
                    title = "Updates",
                    description = "Open manga updates.",
                    command = ChimahonDesktopCommand.Updates,
                    areas = setOf(navigation, library),
                    shortcuts = listOf(shortcut("2", primary), shortcut("U", primary)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.Global,
                ),
                commandMapping(
                    id = "nav.history",
                    title = "History",
                    description = "Open reading history.",
                    command = ChimahonDesktopCommand.History,
                    areas = setOf(navigation, library),
                    shortcuts = listOf(shortcut("3", primary), shortcut("H", primary)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.Global,
                ),
                commandMapping(
                    id = "nav.more",
                    title = "More",
                    description = "Open More and settings-adjacent tools.",
                    command = ChimahonDesktopCommand.More,
                    areas = setOf(navigation),
                    shortcuts = listOf(shortcut("5", primary), shortcut("M", primary)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.Global,
                ),
                commandMapping(
                    id = "nav.settings",
                    title = "Settings",
                    description = "Open app settings.",
                    command = ChimahonDesktopCommand.Settings,
                    areas = setOf(navigation),
                    shortcuts = listOf(shortcut(",", primary)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.Global,
                ),
                commandMapping(
                    id = "nav.downloads",
                    title = "Download queue",
                    description = "Open manga download queue.",
                    command = ChimahonDesktopCommand.DownloadQueue,
                    areas = setOf(navigation, library, browse),
                    shortcuts = listOf(shortcut("D", primary)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.Global,
                ),
            ),
        ),
        ChimahonDesktopCommandMappingSection(
            id = "browse",
            title = "Browse",
            subtitle = "Source, extension, feed, and migration navigation",
            areas = setOf(browse, anime),
            mappings = listOf(
                commandMapping(
                    id = "browse.sources",
                    title = "Browse manga sources",
                    description = "Open source browsing.",
                    command = ChimahonDesktopCommand.BrowseSources,
                    areas = setOf(browse),
                    shortcuts = listOf(shortcut("4", primary), shortcut("B", primary)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.BrowseContent,
                    scrollMode = ChimahonDesktopCommandScrollMode.BrowseGrid,
                ),
                commandMapping(
                    id = "browse.extensions",
                    title = "Browse manga extensions",
                    description = "Open extension repositories and installed extensions.",
                    command = ChimahonDesktopCommand.BrowseExtensions,
                    areas = setOf(browse),
                    shortcuts = listOf(shortcut("E", primary)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.BrowseContent,
                    scrollMode = ChimahonDesktopCommandScrollMode.BrowseGrid,
                ),
                commandMapping(
                    id = "browse.feed",
                    title = "Browse feed",
                    description = "Open the browse feed.",
                    command = ChimahonDesktopCommand.BrowseFeed,
                    areas = setOf(browse),
                    shortcuts = listOf(shortcut("B", primary, shift)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.BrowseContent,
                    scrollMode = ChimahonDesktopCommandScrollMode.BrowseGrid,
                ),
                commandMapping(
                    id = "browse.migrate",
                    title = "Migrate",
                    description = "Open source migration.",
                    command = ChimahonDesktopCommand.BrowseMigrate,
                    areas = setOf(browse),
                    shortcuts = listOf(shortcut("M", primary, shift)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.BrowseContent,
                    scrollMode = ChimahonDesktopCommandScrollMode.BrowseGrid,
                ),
                commandMapping(
                    id = "browse.anime.sources",
                    title = "Browse anime sources",
                    description = "Open anime source browsing.",
                    command = ChimahonDesktopCommand.BrowseAnimeSources,
                    areas = setOf(browse, anime),
                    shortcuts = listOf(shortcut("A", primary, alt, shift)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.BrowseContent,
                    scrollMode = ChimahonDesktopCommandScrollMode.BrowseGrid,
                ),
                commandMapping(
                    id = "browse.anime.extensions",
                    title = "Browse anime extensions",
                    description = "Open anime extension repositories.",
                    command = ChimahonDesktopCommand.BrowseAnimeExtensions,
                    areas = setOf(browse, anime),
                    shortcuts = listOf(shortcut("E", primary, alt, shift)),
                    focusTarget = ChimahonDesktopCommandFocusTarget.BrowseContent,
                    scrollMode = ChimahonDesktopCommandScrollMode.BrowseGrid,
                ),
            ),
        ),
        ChimahonDesktopCommandMappingSection(
            id = "reader",
            title = "Reader",
            subtitle = "Manga reader keyboard, mouse, wheel, focus, page, and zoom controls",
            areas = setOf(reader),
            mappings = readerMappings(reader),
        ),
        ChimahonDesktopCommandMappingSection(
            id = "ln-reader",
            title = "Light novel reader",
            subtitle = "Keyboard and wheel behavior for paged and continuous LN reading",
            areas = setOf(lightNovel),
            mappings = lightNovelReaderMappings(lightNovel),
        ),
        ChimahonDesktopCommandMappingSection(
            id = "anime-player",
            title = "Anime and player",
            subtitle = "Anime navigation plus video player command parity",
            areas = setOf(anime, player),
            mappings = animeAndPlayerMappings(anime, player),
        ),
    )
}

fun chimahonDesktopCommandMappings(): List<ChimahonDesktopCommandMapping> {
    return chimahonDesktopCommandMappingSections().flatMap { it.mappings }
}

fun chimahonDesktopShortcutSections(
    sections: List<ChimahonDesktopCommandMappingSection> = chimahonDesktopCommandMappingSections(),
): List<ChimahonDesktopShortcutSectionModel> {
    return sections.map { section ->
        ChimahonDesktopShortcutSectionModel(
            id = section.id,
            title = section.title,
            subtitle = section.subtitle,
            areas = section.areas,
            rows = section.mappings.map(ChimahonDesktopCommandMapping::toShortcutRow),
        )
    }
}

fun ChimahonDesktopCommandMapping.toShortcutRow(): ChimahonDesktopShortcutRowModel {
    return ChimahonDesktopShortcutRowModel(
        id = id,
        title = title,
        description = description,
        shortcut = shortcuts.firstOrNull(),
        alternateShortcuts = shortcuts.drop(1),
        areas = areas,
        tags = tags +
            areas.map { it.title } +
            focusBehavior.target.title +
            scrollBehavior.mode.title +
            zoomBehavior.mode.title +
            mouseMappings.map { it.label },
    )
}

private fun readerMappings(reader: ChimahonDesktopShortcutArea): List<ChimahonDesktopCommandMapping> {
    val readerFocus = ChimahonDesktopCommandFocusTarget.ReaderContent
    return listOf(
        commandMapping(
            id = "reader.previous.page",
            title = "Previous page",
            description = "Move to the previous page or scroll segment.",
            command = ChimahonDesktopCommand.ReaderPreviousPage,
            areas = setOf(reader),
            shortcuts = listOf(
                shortcut("Page Up"),
                shortcut("Left"),
                shortcut("Up"),
                shortcut("Space", shift),
                shortcut("Left", primary, alt),
            ),
            mouseMappings = listOf(
                mouse("Back mouse button", "Previous page"),
                wheel("Wheel up", "Previous page"),
            ),
            focusTarget = readerFocus,
            scrollMode = ChimahonDesktopCommandScrollMode.ReaderPage,
        ),
        commandMapping(
            id = "reader.next.page",
            title = "Next page",
            description = "Move to the next page or scroll segment.",
            command = ChimahonDesktopCommand.ReaderNextPage,
            areas = setOf(reader),
            shortcuts = listOf(
                shortcut("Page Down"),
                shortcut("Right"),
                shortcut("Down"),
                shortcut("Space"),
                shortcut("Right", primary, alt),
            ),
            mouseMappings = listOf(
                mouse("Forward mouse button", "Next page"),
                wheel("Wheel down", "Next page"),
            ),
            focusTarget = readerFocus,
            scrollMode = ChimahonDesktopCommandScrollMode.ReaderPage,
        ),
        commandMapping(
            id = "reader.first.page",
            title = "First page",
            description = "Jump to page 1.",
            command = ChimahonDesktopCommand.ReaderFirstPage,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("Home"), shortcut("Home", primary, alt)),
            focusTarget = readerFocus,
            scrollMode = ChimahonDesktopCommandScrollMode.ReaderPage,
        ),
        commandMapping(
            id = "reader.last.page",
            title = "Last page",
            description = "Jump to the final page.",
            command = ChimahonDesktopCommand.ReaderLastPage,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("End"), shortcut("End", primary, alt)),
            focusTarget = readerFocus,
            scrollMode = ChimahonDesktopCommandScrollMode.ReaderPage,
        ),
        commandMapping(
            id = "reader.previous.chapter",
            title = "Previous chapter",
            description = "Move to the previous chapter.",
            command = ChimahonDesktopCommand.ReaderPreviousChapter,
            areas = setOf(reader),
            shortcuts = listOf(
                shortcut("Page Up", shift),
                shortcut("Left", shift),
                shortcut("Left", primary, alt, shift),
            ),
            mouseMappings = listOf(
                mouse("Back mouse button", "Previous chapter", shift),
                wheel("Wheel up", "Previous chapter", shift),
            ),
            focusTarget = readerFocus,
            scrollMode = ChimahonDesktopCommandScrollMode.ReaderChapter,
        ),
        commandMapping(
            id = "reader.next.chapter",
            title = "Next chapter",
            description = "Move to the next chapter.",
            command = ChimahonDesktopCommand.ReaderNextChapter,
            areas = setOf(reader),
            shortcuts = listOf(
                shortcut("Page Down", shift),
                shortcut("Right", shift),
                shortcut("Right", primary, alt, shift),
            ),
            mouseMappings = listOf(
                mouse("Forward mouse button", "Next chapter", shift),
                wheel("Wheel down", "Next chapter", shift),
            ),
            focusTarget = readerFocus,
            scrollMode = ChimahonDesktopCommandScrollMode.ReaderChapter,
        ),
        commandMapping(
            id = "reader.controls",
            title = "Toggle controls",
            description = "Show or hide reader controls and page actions without leaving reader focus.",
            command = ChimahonDesktopCommand.ReaderToggleControls,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("Enter"), shortcut("Enter", primary, alt)),
            mouseMappings = listOf(mouse("Middle click", "Toggle controls")),
            focusTarget = readerFocus,
            consumesWhenPanelsOpen = true,
            tags = setOf("hud", "toolbar"),
        ),
        commandMapping(
            id = "reader.mode",
            title = "Cycle reader mode",
            description = "Switch paged, webtoon, and continuous modes.",
            command = ChimahonDesktopCommand.ReaderCycleMode,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("M"), shortcut("M", primary, alt)),
            focusTarget = readerFocus,
            tags = setOf("paged", "webtoon"),
        ),
        commandMapping(
            id = "reader.settings",
            title = "Reader settings",
            description = "Open fit, layout, zoom, and behavior settings.",
            command = ChimahonDesktopCommand.ReaderOpenSettings,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("S"), shortcut(",", primary, alt)),
            focusTarget = readerFocus,
            consumesWhenPanelsOpen = true,
        ),
        commandMapping(
            id = "reader.chapters",
            title = "Chapter list",
            description = "Open chapter drawer while preserving reader context.",
            command = ChimahonDesktopCommand.ReaderOpenChapters,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("C"), shortcut("C", primary, alt)),
            focusTarget = readerFocus,
            consumesWhenPanelsOpen = true,
        ),
        commandMapping(
            id = "reader.stats",
            title = "Reader stats",
            description = "Toggle the statistics overlay.",
            command = ChimahonDesktopCommand.ReaderToggleStats,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("I"), shortcut("I", primary, alt)),
            focusTarget = readerFocus,
        ),
        commandMapping(
            id = "reader.ocr",
            title = "OCR lookup",
            description = "Toggle OCR boxes and lookup controls.",
            command = ChimahonDesktopCommand.ReaderToggleOcrLookup,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("G"), shortcut("G", primary, alt)),
            focusTarget = ChimahonDesktopCommandFocusTarget.TextSelection,
            consumesWhenPanelsOpen = true,
            tags = setOf("lookup", "glens"),
        ),
        commandMapping(
            id = "reader.crop",
            title = "Crop borders",
            description = "Toggle crop borders.",
            command = ChimahonDesktopCommand.ReaderToggleCrop,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("F"), shortcut("F", primary, alt)),
            focusTarget = readerFocus,
        ),
        commandMapping(
            id = "reader.orientation",
            title = "Cycle orientation",
            description = "Cycle reader orientation.",
            command = ChimahonDesktopCommand.ReaderCycleOrientation,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("T"), shortcut("T", primary, alt)),
            focusTarget = readerFocus,
        ),
        commandMapping(
            id = "reader.layout",
            title = "Cycle page layout",
            description = "Cycle single, double, and split page layout.",
            command = ChimahonDesktopCommand.ReaderCyclePageLayout,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("L"), shortcut("L", primary, alt)),
            focusTarget = readerFocus,
        ),
        commandMapping(
            id = "reader.double.page.shift",
            title = "Shift double pages",
            description = "Shift the double-page pairing.",
            command = ChimahonDesktopCommand.ReaderShiftDoublePages,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("P"), shortcut("P", primary, alt)),
            focusTarget = readerFocus,
        ),
        commandMapping(
            id = "reader.zoom.in",
            title = "Zoom in",
            description = "Increase reader zoom around the current pointer or viewport anchor.",
            command = ChimahonDesktopCommand.ReaderZoomIn,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("=", primary), shortcut("+", primary), shortcut("Num +", primary)),
            mouseMappings = listOf(wheel("Wheel up", "Zoom in", primary)),
            focusTarget = readerFocus,
            zoomMode = ChimahonDesktopCommandZoomMode.ReaderZoomIn,
            zoomAmount = "step in",
        ),
        commandMapping(
            id = "reader.zoom.out",
            title = "Zoom out",
            description = "Decrease reader zoom around the current pointer or viewport anchor.",
            command = ChimahonDesktopCommand.ReaderZoomOut,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("-", primary), shortcut("Num -", primary)),
            mouseMappings = listOf(wheel("Wheel down", "Zoom out", primary)),
            focusTarget = readerFocus,
            zoomMode = ChimahonDesktopCommandZoomMode.ReaderZoomOut,
            zoomAmount = "step out",
        ),
        commandMapping(
            id = "reader.zoom.reset",
            title = "Reset zoom",
            description = "Reset reader zoom.",
            command = ChimahonDesktopCommand.ReaderResetZoom,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("0", primary)),
            focusTarget = readerFocus,
            zoomMode = ChimahonDesktopCommandZoomMode.ReaderZoomReset,
            resetScrollAnchor = true,
        ),
        commandMapping(
            id = "reader.fit.width",
            title = "Fit width",
            description = "Fit page to viewport width.",
            command = ChimahonDesktopCommand.ReaderFitWidth,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("W", primary, alt), shortcut("1", primary, alt)),
            focusTarget = readerFocus,
            zoomMode = ChimahonDesktopCommandZoomMode.ReaderFitWidth,
            resetScrollAnchor = true,
        ),
        commandMapping(
            id = "reader.fit.height",
            title = "Fit height",
            description = "Fit page to viewport height.",
            command = ChimahonDesktopCommand.ReaderFitHeight,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("H", primary, alt), shortcut("2", primary, alt)),
            focusTarget = readerFocus,
            zoomMode = ChimahonDesktopCommandZoomMode.ReaderFitHeight,
            resetScrollAnchor = true,
        ),
        commandMapping(
            id = "reader.fit.screen",
            title = "Fit screen",
            description = "Fit the full page to screen.",
            command = ChimahonDesktopCommand.ReaderFitScreen,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("0", primary, alt)),
            focusTarget = readerFocus,
            zoomMode = ChimahonDesktopCommandZoomMode.ReaderFitScreen,
            resetScrollAnchor = true,
        ),
        commandMapping(
            id = "reader.fit.toggle",
            title = "Toggle fit width/screen",
            description = "Toggle between fit-width and fit-screen behavior.",
            command = ChimahonDesktopCommand.ReaderToggleFitWidthOrScreen,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("Z")),
            mouseMappings = listOf(mouse("Left click", "Toggle fit width/screen", clickCount = 2)),
            focusTarget = readerFocus,
            zoomMode = ChimahonDesktopCommandZoomMode.ReaderToggleFit,
            resetScrollAnchor = true,
        ),
        commandMapping(
            id = "reader.scroll.up",
            title = "Scroll up",
            description = "Scroll the reader viewport up; falls back to previous page in paged mode.",
            areas = setOf(reader),
            shortcuts = listOf(shortcut("K"), shortcut("Up", shift)),
            mouseMappings = listOf(wheel("Wheel up", "Page scroll", alt)),
            focusTarget = readerFocus,
            scrollMode = ChimahonDesktopCommandScrollMode.ReaderPageScroll,
            scrollAmount = "line/page segment",
        ),
        commandMapping(
            id = "reader.scroll.down",
            title = "Scroll down",
            description = "Scroll the reader viewport down; falls back to next page in paged mode.",
            areas = setOf(reader),
            shortcuts = listOf(shortcut("J"), shortcut("Down", shift)),
            mouseMappings = listOf(wheel("Wheel down", "Page scroll", alt)),
            focusTarget = readerFocus,
            scrollMode = ChimahonDesktopCommandScrollMode.ReaderPageScroll,
            scrollAmount = "line/page segment",
        ),
        commandMapping(
            id = "reader.bookmark",
            title = "Bookmark chapter",
            description = "Toggle chapter bookmark.",
            command = ChimahonDesktopCommand.ReaderBookmarkChapter,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("B"), shortcut("B", primary, alt)),
            focusTarget = readerFocus,
            consumesWhenPanelsOpen = true,
        ),
        commandMapping(
            id = "reader.download",
            title = "Download chapter",
            description = "Queue the current chapter for download.",
            command = ChimahonDesktopCommand.ReaderDownloadChapter,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("D"), shortcut("D", primary, alt)),
            focusTarget = readerFocus,
            consumesWhenPanelsOpen = true,
        ),
        commandMapping(
            id = "reader.mark.read",
            title = "Mark read",
            description = "Toggle chapter read state.",
            command = ChimahonDesktopCommand.ReaderMarkChapterRead,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("R"), shortcut("R", primary, alt)),
            focusTarget = readerFocus,
            consumesWhenPanelsOpen = true,
        ),
        commandMapping(
            id = "reader.open.url",
            title = "Open chapter URL",
            description = "Open the current chapter URL externally.",
            command = ChimahonDesktopCommand.ReaderOpenChapterUrl,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("O"), shortcut("O", primary, alt)),
            focusTarget = readerFocus,
            consumesWhenPanelsOpen = true,
        ),
        commandMapping(
            id = "reader.share",
            title = "Share chapter URL",
            description = "Share or copy the current chapter URL.",
            command = ChimahonDesktopCommand.ReaderShareChapter,
            areas = setOf(reader),
            shortcuts = listOf(shortcut("S", primary, alt, shift)),
            focusTarget = readerFocus,
            consumesWhenPanelsOpen = true,
        ),
    )
}

private fun lightNovelReaderMappings(lightNovel: ChimahonDesktopShortcutArea): List<ChimahonDesktopCommandMapping> {
    val focus = ChimahonDesktopCommandFocusTarget.LightNovelReader
    return listOf(
        commandMapping(
            id = "ln.previous",
            title = "Previous page/chapter",
            description = "Move backward in the LN reader, using page or chapter semantics for the active mode.",
            areas = setOf(lightNovel),
            shortcuts = listOf(shortcut("Left"), shortcut("Page Up"), shortcut("Space", shift)),
            mouseMappings = listOf(wheel("Wheel up", "Previous page or scroll up")),
            focusTarget = focus,
            scrollMode = ChimahonDesktopCommandScrollMode.LightNovelPage,
        ),
        commandMapping(
            id = "ln.next",
            title = "Next page/chapter",
            description = "Move forward in the LN reader, using page or chapter semantics for the active mode.",
            areas = setOf(lightNovel),
            shortcuts = listOf(shortcut("Right"), shortcut("Page Down"), shortcut("Space")),
            mouseMappings = listOf(wheel("Wheel down", "Next page or scroll down")),
            focusTarget = focus,
            scrollMode = ChimahonDesktopCommandScrollMode.LightNovelPage,
        ),
        commandMapping(
            id = "ln.toggle.hud",
            title = "Toggle HUD",
            description = "Show or hide LN reader controls while keeping reading focus.",
            areas = setOf(lightNovel),
            shortcuts = listOf(shortcut("Enter")),
            focusTarget = focus,
            consumesWhenPanelsOpen = true,
            tags = setOf("toolbar"),
        ),
        commandMapping(
            id = "ln.chapters",
            title = "Chapter drawer",
            description = "Open the LN chapter drawer.",
            areas = setOf(lightNovel),
            shortcuts = listOf(shortcut("C")),
            focusTarget = focus,
            consumesWhenPanelsOpen = true,
        ),
        commandMapping(
            id = "ln.typography",
            title = "Typography",
            description = "Open text, spacing, theme, and layout controls.",
            areas = setOf(lightNovel),
            shortcuts = listOf(shortcut("S"), shortcut(",", primary, alt)),
            mouseMappings = listOf(wheel("Wheel up/down", "Scale text", primary)),
            focusTarget = focus,
            zoomMode = ChimahonDesktopCommandZoomMode.LightNovelTextScale,
            zoomAmount = "font size step",
            consumesWhenPanelsOpen = true,
        ),
        commandMapping(
            id = "ln.direction",
            title = "Reading direction",
            description = "Cycle default, RTL, LTR, and vertical reading directions.",
            areas = setOf(lightNovel),
            shortcuts = listOf(shortcut("T")),
            focusTarget = focus,
        ),
        commandMapping(
            id = "ln.mode",
            title = "Paged/continuous mode",
            description = "Toggle paged and continuous LN reading behavior.",
            areas = setOf(lightNovel),
            shortcuts = listOf(shortcut("M")),
            focusTarget = focus,
            scrollMode = ChimahonDesktopCommandScrollMode.LightNovelContinuous,
        ),
        commandMapping(
            id = "ln.lookup",
            title = "Lookup selected text",
            description = "Create a lookup from selected text without leaving the reader.",
            areas = setOf(lightNovel),
            shortcuts = listOf(shortcut("G")),
            focusTarget = ChimahonDesktopCommandFocusTarget.TextSelection,
            consumesWhenPanelsOpen = true,
            tags = setOf("sasayaki", "dictionary"),
        ),
    )
}

private fun animeAndPlayerMappings(
    anime: ChimahonDesktopShortcutArea,
    player: ChimahonDesktopShortcutArea,
): List<ChimahonDesktopCommandMapping> {
    return listOf(
        commandMapping(
            id = "anime.library",
            title = "Anime library",
            description = "Open anime library.",
            command = ChimahonDesktopCommand.AnimeLibrary,
            areas = setOf(anime),
            shortcuts = listOf(shortcut("A", primary, alt)),
            focusTarget = ChimahonDesktopCommandFocusTarget.Global,
        ),
        commandMapping(
            id = "anime.updates",
            title = "Anime updates",
            description = "Open anime updates.",
            command = ChimahonDesktopCommand.AnimeUpdates,
            areas = setOf(anime),
            shortcuts = listOf(shortcut("U", primary, alt)),
            focusTarget = ChimahonDesktopCommandFocusTarget.Global,
        ),
        commandMapping(
            id = "anime.history",
            title = "Anime history",
            description = "Open anime watch history.",
            command = ChimahonDesktopCommand.AnimeHistory,
            areas = setOf(anime),
            shortcuts = listOf(shortcut("H", primary, alt)),
            focusTarget = ChimahonDesktopCommandFocusTarget.Global,
        ),
        commandMapping(
            id = "anime.downloads",
            title = "Anime download queue",
            description = "Open anime downloads.",
            command = ChimahonDesktopCommand.AnimeDownloadQueue,
            areas = setOf(anime),
            shortcuts = listOf(shortcut("Q", primary, alt, shift)),
            focusTarget = ChimahonDesktopCommandFocusTarget.Global,
        ),
        commandMapping(
            id = "anime.settings",
            title = "Anime settings",
            description = "Open anime settings.",
            command = ChimahonDesktopCommand.AnimeSettings,
            areas = setOf(anime),
            shortcuts = listOf(shortcut(",", primary, alt, shift)),
            focusTarget = ChimahonDesktopCommandFocusTarget.Global,
        ),
        commandMapping(
            id = "player.play.pause",
            title = "Play/pause",
            description = "Toggle playback while the anime player is active.",
            command = ChimahonDesktopCommand.PlayerTogglePlayback,
            areas = setOf(player),
            shortcuts = listOf(shortcut("Space", primary, shift)),
            mouseMappings = listOf(mouse("Left click", "Toggle playback", clickCount = 2)),
            focusTarget = ChimahonDesktopCommandFocusTarget.PlayerSurface,
        ),
        commandMapping(
            id = "player.seek.backward",
            title = "Seek backward",
            description = "Jump backward in the current video.",
            command = ChimahonDesktopCommand.PlayerSeekBackward,
            areas = setOf(player),
            shortcuts = listOf(shortcut("J", primary, shift), shortcut("Left", primary, shift)),
            mouseMappings = listOf(wheel("Wheel down", "Seek backward", shift)),
            focusTarget = ChimahonDesktopCommandFocusTarget.PlayerSurface,
            scrollMode = ChimahonDesktopCommandScrollMode.PlayerSeek,
            scrollAmount = "seek step",
        ),
        commandMapping(
            id = "player.seek.forward",
            title = "Seek forward",
            description = "Jump forward in the current video.",
            command = ChimahonDesktopCommand.PlayerSeekForward,
            areas = setOf(player),
            shortcuts = listOf(shortcut("L", primary, shift), shortcut("Right", primary, shift)),
            mouseMappings = listOf(wheel("Wheel up", "Seek forward", shift)),
            focusTarget = ChimahonDesktopCommandFocusTarget.PlayerSurface,
            scrollMode = ChimahonDesktopCommandScrollMode.PlayerSeek,
            scrollAmount = "seek step",
        ),
        commandMapping(
            id = "player.previous.episode",
            title = "Previous episode",
            description = "Go to the previous episode.",
            command = ChimahonDesktopCommand.PlayerPreviousEpisode,
            areas = setOf(player),
            shortcuts = listOf(shortcut("[", primary, shift)),
            focusTarget = ChimahonDesktopCommandFocusTarget.PlayerSurface,
        ),
        commandMapping(
            id = "player.next.episode",
            title = "Next episode",
            description = "Go to the next episode.",
            command = ChimahonDesktopCommand.PlayerNextEpisode,
            areas = setOf(player),
            shortcuts = listOf(shortcut("]", primary, shift)),
            focusTarget = ChimahonDesktopCommandFocusTarget.PlayerSurface,
        ),
        commandMapping(
            id = "player.subtitles",
            title = "Subtitle settings",
            description = "Open subtitle controls.",
            command = ChimahonDesktopCommand.PlayerSubtitleSettings,
            areas = setOf(player),
            shortcuts = listOf(shortcut("S", primary, shift)),
            focusTarget = ChimahonDesktopCommandFocusTarget.PlayerSurface,
        ),
        commandMapping(
            id = "player.audio.delay",
            title = "Audio delay",
            description = "Open audio delay controls.",
            command = ChimahonDesktopCommand.PlayerAudioDelay,
            areas = setOf(player),
            shortcuts = listOf(shortcut("Y", primary, shift)),
            focusTarget = ChimahonDesktopCommandFocusTarget.PlayerSurface,
        ),
        commandMapping(
            id = "player.video.filters",
            title = "Video filters",
            description = "Open video filter controls.",
            command = ChimahonDesktopCommand.PlayerVideoFilters,
            areas = setOf(player),
            shortcuts = listOf(shortcut("V", primary, shift)),
            focusTarget = ChimahonDesktopCommandFocusTarget.PlayerSurface,
        ),
        commandMapping(
            id = "player.settings",
            title = "Player settings",
            description = "Open player settings.",
            command = ChimahonDesktopCommand.PlayerSettings,
            areas = setOf(player),
            shortcuts = listOf(shortcut(",", primary, shift)),
            focusTarget = ChimahonDesktopCommandFocusTarget.PlayerSurface,
        ),
    )
}

private fun commandMapping(
    id: String,
    title: String,
    description: String,
    areas: Set<ChimahonDesktopShortcutArea>,
    shortcuts: List<ChimahonDesktopShortcutChord>,
    focusTarget: ChimahonDesktopCommandFocusTarget,
    command: ChimahonDesktopCommand? = null,
    mouseMappings: List<ChimahonDesktopMouseCommandMapping> = emptyList(),
    scrollMode: ChimahonDesktopCommandScrollMode = ChimahonDesktopCommandScrollMode.None,
    scrollAmount: String? = null,
    zoomMode: ChimahonDesktopCommandZoomMode = ChimahonDesktopCommandZoomMode.None,
    zoomAmount: String? = null,
    resetScrollAnchor: Boolean = false,
    consumesWhenPanelsOpen: Boolean = false,
    tags: Set<String> = emptySet(),
): ChimahonDesktopCommandMapping {
    return ChimahonDesktopCommandMapping(
        id = id,
        title = title,
        description = description,
        command = command,
        areas = areas,
        shortcuts = shortcuts,
        mouseMappings = mouseMappings,
        focusBehavior = ChimahonDesktopCommandFocusBehavior(
            target = focusTarget,
            consumesWhenPanelsOpen = consumesWhenPanelsOpen,
        ),
        scrollBehavior = ChimahonDesktopCommandScrollBehavior(
            mode = scrollMode,
            amountLabel = scrollAmount,
            preservesPointerAnchor = zoomMode != ChimahonDesktopCommandZoomMode.None,
        ),
        zoomBehavior = ChimahonDesktopCommandZoomBehavior(
            mode = zoomMode,
            amountLabel = zoomAmount,
            resetsScrollAnchor = resetScrollAnchor,
        ),
        tags = tags,
    )
}

private fun shortcut(
    key: String,
    vararg modifiers: ChimahonDesktopShortcutModifier,
): ChimahonDesktopShortcutChord {
    return chimahonDesktopShortcutChord(key, *modifiers)
}

private fun mouse(
    label: String,
    description: String,
    vararg modifiers: ChimahonDesktopShortcutModifier,
    clickCount: Int = 1,
): ChimahonDesktopMouseCommandMapping {
    return ChimahonDesktopMouseCommandMapping(
        label = label,
        description = description,
        modifiers = modifiers.toSet(),
        clickCount = clickCount,
    )
}

private fun wheel(
    label: String,
    description: String,
    vararg modifiers: ChimahonDesktopShortcutModifier,
): ChimahonDesktopMouseCommandMapping {
    return ChimahonDesktopMouseCommandMapping(
        label = label,
        description = description,
        inputKind = ChimahonDesktopCommandInputKind.MouseWheel,
        modifiers = modifiers.toSet(),
    )
}

private val primary = ChimahonDesktopShortcutModifier.Primary
private val alt = ChimahonDesktopShortcutModifier.Alt
private val shift = ChimahonDesktopShortcutModifier.Shift
