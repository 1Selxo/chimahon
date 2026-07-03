package chimahon.desktop.input

import app.chimahon.shared.ChimahonDesktopCommand
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

enum class DesktopCommandInputArea {
    Navigation,
    Browse,
    Reader,
    AnimePlayer,
    LightNovelReader,
}

enum class DesktopCommandFocusPolicy {
    Global,
    ActiveScreen,
    BrowseContent,
    ReaderContent,
    PlayerSurface,
    LightNovelReader,
    TextSelection,
}

enum class DesktopCommandScrollPolicy {
    None,
    FocusedScrollable,
    ReaderPage,
    ReaderChapter,
    ReaderPageScroll,
    BrowseGrid,
    PlayerSeek,
    LightNovelPage,
    LightNovelContinuous,
}

enum class DesktopCommandZoomPolicy {
    None,
    ReaderZoomIn,
    ReaderZoomOut,
    ReaderZoomReset,
    ReaderFitWidth,
    ReaderFitHeight,
    ReaderFitScreen,
    ReaderToggleFit,
    LightNovelTextScale,
}

data class DesktopCommandInputBehavior(
    val focusPolicy: DesktopCommandFocusPolicy,
    val scrollPolicy: DesktopCommandScrollPolicy = DesktopCommandScrollPolicy.None,
    val zoomPolicy: DesktopCommandZoomPolicy = DesktopCommandZoomPolicy.None,
    val ignoreWhileTextInputFocused: Boolean = true,
    val consumesWhenPanelsOpen: Boolean = false,
    val keepContentFocusedAfterDispatch: Boolean = true,
)

data class DesktopCommandShortcutLabels(
    val menuShortcut: String,
    val ctrl: String = "Ctrl",
    val meta: String = "Meta",
    val alt: String = "Alt",
    val shift: String = "Shift",
    val separator: String = "+",
)

data class DesktopCommandKeyMapping(
    val keyCode: Int,
    val modifiers: DesktopReaderShortcutModifiers = DesktopReaderShortcutModifiers(),
    val repeatable: Boolean = true,
) {
    fun label(labels: DesktopCommandShortcutLabels): String {
        return buildList {
            if (modifiers.menuShortcut) add(labels.menuShortcut)
            if (modifiers.ctrl) add(labels.ctrl)
            if (modifiers.meta) add(labels.meta)
            if (modifiers.alt) add(labels.alt)
            if (modifiers.shift) add(labels.shift)
            add(keyCode.desktopCommandKeyLabel())
        }.joinToString(labels.separator)
    }
}

data class DesktopCommandMouseButtonMapping(
    val button: Int,
    val modifiers: DesktopReaderShortcutModifiers = DesktopReaderShortcutModifiers(),
    val clickCount: Int = 1,
) {
    fun label(labels: DesktopCommandShortcutLabels): String {
        val buttonLabel = when (button) {
            MouseEvent.BUTTON1 -> "Left click"
            MouseEvent.BUTTON2 -> "Middle click"
            MouseEvent.BUTTON3 -> "Right click"
            DesktopReaderInputDefaults.BackMouseButton -> "Back mouse button"
            DesktopReaderInputDefaults.ForwardMouseButton -> "Forward mouse button"
            else -> "Mouse button $button"
        }
        val clickLabel = if (clickCount > 1) "$clickCount x $buttonLabel" else buttonLabel
        return buildList {
            if (modifiers.menuShortcut) add(labels.menuShortcut)
            if (modifiers.ctrl) add(labels.ctrl)
            if (modifiers.meta) add(labels.meta)
            if (modifiers.alt) add(labels.alt)
            if (modifiers.shift) add(labels.shift)
            add(clickLabel)
        }.joinToString(labels.separator)
    }
}

data class DesktopCommandWheelMapping(
    val negativeActionTitle: String,
    val positiveActionTitle: String,
    val modifiers: DesktopReaderShortcutModifiers = DesktopReaderShortcutModifiers(),
    val scrollPolicy: DesktopCommandScrollPolicy = DesktopCommandScrollPolicy.FocusedScrollable,
    val zoomPolicy: DesktopCommandZoomPolicy = DesktopCommandZoomPolicy.None,
) {
    fun label(labels: DesktopCommandShortcutLabels): String {
        val modifierLabel = buildList {
            if (modifiers.menuShortcut) add(labels.menuShortcut)
            if (modifiers.ctrl) add(labels.ctrl)
            if (modifiers.meta) add(labels.meta)
            if (modifiers.alt) add(labels.alt)
            if (modifiers.shift) add(labels.shift)
        }.joinToString(labels.separator)
        val wheelLabel = "Mouse wheel"
        return if (modifierLabel.isBlank()) wheelLabel else "$modifierLabel${labels.separator}$wheelLabel"
    }
}

data class DesktopCommandInputMapping(
    val id: String,
    val title: String,
    val command: ChimahonDesktopCommand? = null,
    val areas: Set<DesktopCommandInputArea>,
    val keyMappings: List<DesktopCommandKeyMapping> = emptyList(),
    val mouseButtonMappings: List<DesktopCommandMouseButtonMapping> = emptyList(),
    val wheelMappings: List<DesktopCommandWheelMapping> = emptyList(),
    val behavior: DesktopCommandInputBehavior,
) {
    fun shortcutLabels(labels: DesktopCommandShortcutLabels): List<String> {
        return keyMappings.map { it.label(labels) } +
            mouseButtonMappings.map { it.label(labels) } +
            wheelMappings.map { it.label(labels) }
    }
}

object DesktopCommandInputDefaults {
    fun defaultMappings(): List<DesktopCommandInputMapping> {
        return navigationMappings() + browseMappings() + readerMappings() +
            lightNovelReaderMappings() + animePlayerMappings()
    }

    fun mappingsFor(area: DesktopCommandInputArea): List<DesktopCommandInputMapping> {
        return defaultMappings().filter { area in it.areas }
    }

    fun commandMappings(): List<DesktopCommandInputMapping> {
        return defaultMappings().filter { it.command != null }
    }

    private fun navigationMappings(): List<DesktopCommandInputMapping> {
        val navigation = setOf(DesktopCommandInputArea.Navigation)
        val global = DesktopCommandInputBehavior(DesktopCommandFocusPolicy.Global)
        val activeScreen = DesktopCommandInputBehavior(
            focusPolicy = DesktopCommandFocusPolicy.ActiveScreen,
            scrollPolicy = DesktopCommandScrollPolicy.FocusedScrollable,
        )

        return listOf(
            commandInput(
                id = "nav.back",
                title = "Back",
                command = ChimahonDesktopCommand.Back,
                areas = navigation,
                behavior = global,
                keys = listOf(key(KeyEvent.VK_ESCAPE), key(KeyEvent.VK_LEFT, alt)),
            ),
            commandInput(
                id = "nav.search",
                title = "Search",
                command = ChimahonDesktopCommand.Search,
                areas = navigation,
                behavior = activeScreen,
                keys = listOf(key(KeyEvent.VK_F, menu)),
            ),
            commandInput(
                id = "nav.filters",
                title = "Toggle filters",
                command = ChimahonDesktopCommand.ToggleFilters,
                areas = navigation,
                behavior = activeScreen,
                keys = listOf(key(KeyEvent.VK_F, menuShift)),
            ),
            commandInput(
                id = "nav.refresh",
                title = "Refresh",
                command = ChimahonDesktopCommand.Refresh,
                areas = navigation,
                behavior = activeScreen,
                keys = listOf(key(KeyEvent.VK_F5), key(KeyEvent.VK_R, menu)),
            ),
            commandInput(
                id = "nav.library",
                title = "Library",
                command = ChimahonDesktopCommand.Library,
                areas = navigation,
                behavior = global,
                keys = listOf(key(KeyEvent.VK_1, menu), key(KeyEvent.VK_L, menu)),
            ),
            commandInput(
                id = "nav.novels",
                title = "Novels",
                command = ChimahonDesktopCommand.Novels,
                areas = navigation + DesktopCommandInputArea.LightNovelReader,
                behavior = global,
                keys = listOf(key(KeyEvent.VK_N, menu)),
            ),
            commandInput(
                id = "nav.updates",
                title = "Updates",
                command = ChimahonDesktopCommand.Updates,
                areas = navigation,
                behavior = global,
                keys = listOf(key(KeyEvent.VK_2, menu), key(KeyEvent.VK_U, menu)),
            ),
            commandInput(
                id = "nav.history",
                title = "History",
                command = ChimahonDesktopCommand.History,
                areas = navigation,
                behavior = global,
                keys = listOf(key(KeyEvent.VK_3, menu), key(KeyEvent.VK_H, menu)),
            ),
            commandInput(
                id = "nav.more",
                title = "More",
                command = ChimahonDesktopCommand.More,
                areas = navigation,
                behavior = global,
                keys = listOf(key(KeyEvent.VK_5, menu), key(KeyEvent.VK_M, menu)),
            ),
            commandInput(
                id = "nav.settings",
                title = "Settings",
                command = ChimahonDesktopCommand.Settings,
                areas = navigation,
                behavior = global,
                keys = listOf(key(KeyEvent.VK_COMMA, menu)),
            ),
            commandInput(
                id = "nav.downloads",
                title = "Download queue",
                command = ChimahonDesktopCommand.DownloadQueue,
                areas = navigation,
                behavior = global,
                keys = listOf(key(KeyEvent.VK_D, menu)),
            ),
        )
    }

    private fun browseMappings(): List<DesktopCommandInputMapping> {
        val behavior = DesktopCommandInputBehavior(
            focusPolicy = DesktopCommandFocusPolicy.BrowseContent,
            scrollPolicy = DesktopCommandScrollPolicy.BrowseGrid,
        )
        val browse = setOf(DesktopCommandInputArea.Browse)
        return listOf(
            commandInput(
                id = "browse.sources",
                title = "Browse sources",
                command = ChimahonDesktopCommand.BrowseSources,
                areas = browse,
                behavior = behavior,
                keys = listOf(key(KeyEvent.VK_4, menu), key(KeyEvent.VK_B, menu)),
            ),
            commandInput(
                id = "browse.extensions",
                title = "Browse extensions",
                command = ChimahonDesktopCommand.BrowseExtensions,
                areas = browse,
                behavior = behavior,
                keys = listOf(key(KeyEvent.VK_E, menu)),
            ),
            commandInput(
                id = "browse.feed",
                title = "Browse feed",
                command = ChimahonDesktopCommand.BrowseFeed,
                areas = browse,
                behavior = behavior,
                keys = listOf(key(KeyEvent.VK_B, menuShift)),
            ),
            commandInput(
                id = "browse.migrate",
                title = "Migrate",
                command = ChimahonDesktopCommand.BrowseMigrate,
                areas = browse,
                behavior = behavior,
                keys = listOf(key(KeyEvent.VK_M, menuShift)),
            ),
            commandInput(
                id = "browse.anime.sources",
                title = "Browse anime sources",
                command = ChimahonDesktopCommand.BrowseAnimeSources,
                areas = browse + DesktopCommandInputArea.AnimePlayer,
                behavior = behavior,
                keys = listOf(key(KeyEvent.VK_A, menuAltShift)),
            ),
            commandInput(
                id = "browse.anime.extensions",
                title = "Browse anime extensions",
                command = ChimahonDesktopCommand.BrowseAnimeExtensions,
                areas = browse + DesktopCommandInputArea.AnimePlayer,
                behavior = behavior,
                keys = listOf(key(KeyEvent.VK_E, menuAltShift)),
            ),
        )
    }

    private fun readerMappings(): List<DesktopCommandInputMapping> {
        val readerArea = setOf(DesktopCommandInputArea.Reader)
        val readerBehavior = DesktopCommandInputBehavior(
            focusPolicy = DesktopCommandFocusPolicy.ReaderContent,
            scrollPolicy = DesktopCommandScrollPolicy.ReaderPage,
        )
        val panelBehavior = readerBehavior.copy(consumesWhenPanelsOpen = true)

        return listOf(
            commandInput(
                id = "reader.previous.page",
                title = "Previous page",
                command = ChimahonDesktopCommand.ReaderPreviousPage,
                areas = readerArea,
                behavior = readerBehavior,
                keys = listOf(
                    key(KeyEvent.VK_PAGE_UP),
                    key(KeyEvent.VK_LEFT),
                    key(KeyEvent.VK_UP),
                    key(KeyEvent.VK_SPACE, shift),
                    key(KeyEvent.VK_LEFT, menuAlt),
                ),
                mouseButtons = listOf(mouseButton(DesktopReaderInputDefaults.BackMouseButton)),
                wheels = listOf(wheel("Previous page", "Next page", scroll = DesktopCommandScrollPolicy.ReaderPage)),
            ),
            commandInput(
                id = "reader.next.page",
                title = "Next page",
                command = ChimahonDesktopCommand.ReaderNextPage,
                areas = readerArea,
                behavior = readerBehavior,
                keys = listOf(
                    key(KeyEvent.VK_PAGE_DOWN),
                    key(KeyEvent.VK_RIGHT),
                    key(KeyEvent.VK_DOWN),
                    key(KeyEvent.VK_SPACE),
                    key(KeyEvent.VK_RIGHT, menuAlt),
                ),
                mouseButtons = listOf(mouseButton(DesktopReaderInputDefaults.ForwardMouseButton)),
                wheels = listOf(wheel("Previous page", "Next page", scroll = DesktopCommandScrollPolicy.ReaderPage)),
            ),
            commandInput(
                id = "reader.first.page",
                title = "First page",
                command = ChimahonDesktopCommand.ReaderFirstPage,
                areas = readerArea,
                behavior = readerBehavior,
                keys = listOf(key(KeyEvent.VK_HOME), key(KeyEvent.VK_HOME, menuAlt)),
            ),
            commandInput(
                id = "reader.last.page",
                title = "Last page",
                command = ChimahonDesktopCommand.ReaderLastPage,
                areas = readerArea,
                behavior = readerBehavior,
                keys = listOf(key(KeyEvent.VK_END), key(KeyEvent.VK_END, menuAlt)),
            ),
            commandInput(
                id = "reader.previous.chapter",
                title = "Previous chapter",
                command = ChimahonDesktopCommand.ReaderPreviousChapter,
                areas = readerArea,
                behavior = readerBehavior.copy(scrollPolicy = DesktopCommandScrollPolicy.ReaderChapter),
                keys = listOf(
                    key(KeyEvent.VK_PAGE_UP, shift),
                    key(KeyEvent.VK_LEFT, shift),
                    key(KeyEvent.VK_LEFT, menuAltShift),
                ),
                mouseButtons = listOf(mouseButton(DesktopReaderInputDefaults.BackMouseButton, shift)),
                wheels = listOf(wheel("Previous chapter", "Next chapter", shift, scroll = DesktopCommandScrollPolicy.ReaderChapter)),
            ),
            commandInput(
                id = "reader.next.chapter",
                title = "Next chapter",
                command = ChimahonDesktopCommand.ReaderNextChapter,
                areas = readerArea,
                behavior = readerBehavior.copy(scrollPolicy = DesktopCommandScrollPolicy.ReaderChapter),
                keys = listOf(
                    key(KeyEvent.VK_PAGE_DOWN, shift),
                    key(KeyEvent.VK_RIGHT, shift),
                    key(KeyEvent.VK_RIGHT, menuAltShift),
                ),
                mouseButtons = listOf(mouseButton(DesktopReaderInputDefaults.ForwardMouseButton, shift)),
                wheels = listOf(wheel("Previous chapter", "Next chapter", shift, scroll = DesktopCommandScrollPolicy.ReaderChapter)),
            ),
            commandInput(
                id = "reader.controls",
                title = "Toggle controls",
                command = ChimahonDesktopCommand.ReaderToggleControls,
                areas = readerArea,
                behavior = panelBehavior,
                keys = listOf(key(KeyEvent.VK_ENTER), key(KeyEvent.VK_ENTER, menuAlt)),
                mouseButtons = listOf(mouseButton(MouseEvent.BUTTON2)),
            ),
            commandInput(
                id = "reader.mode",
                title = "Cycle reader mode",
                command = ChimahonDesktopCommand.ReaderCycleMode,
                areas = readerArea,
                behavior = readerBehavior,
                keys = listOf(key(KeyEvent.VK_M), key(KeyEvent.VK_M, menuAlt)),
            ),
            commandInput(
                id = "reader.settings",
                title = "Reader settings",
                command = ChimahonDesktopCommand.ReaderOpenSettings,
                areas = readerArea,
                behavior = panelBehavior,
                keys = listOf(key(KeyEvent.VK_S), key(KeyEvent.VK_COMMA, menuAlt)),
            ),
            commandInput(
                id = "reader.chapters",
                title = "Chapter list",
                command = ChimahonDesktopCommand.ReaderOpenChapters,
                areas = readerArea,
                behavior = panelBehavior,
                keys = listOf(key(KeyEvent.VK_C), key(KeyEvent.VK_C, menuAlt)),
            ),
            commandInput(
                id = "reader.ocr",
                title = "OCR lookup",
                command = ChimahonDesktopCommand.ReaderToggleOcrLookup,
                areas = readerArea,
                behavior = panelBehavior.copy(focusPolicy = DesktopCommandFocusPolicy.TextSelection),
                keys = listOf(key(KeyEvent.VK_G), key(KeyEvent.VK_G, menuAlt)),
            ),
            commandInput(
                id = "reader.zoom.in",
                title = "Zoom in",
                command = ChimahonDesktopCommand.ReaderZoomIn,
                areas = readerArea,
                behavior = readerBehavior.copy(zoomPolicy = DesktopCommandZoomPolicy.ReaderZoomIn),
                keys = listOf(key(KeyEvent.VK_EQUALS, menu), key(KeyEvent.VK_PLUS, menu), key(KeyEvent.VK_ADD, menu)),
                wheels = listOf(wheel("Zoom in", "Zoom out", menu, zoom = DesktopCommandZoomPolicy.ReaderZoomIn)),
            ),
            commandInput(
                id = "reader.zoom.out",
                title = "Zoom out",
                command = ChimahonDesktopCommand.ReaderZoomOut,
                areas = readerArea,
                behavior = readerBehavior.copy(zoomPolicy = DesktopCommandZoomPolicy.ReaderZoomOut),
                keys = listOf(key(KeyEvent.VK_MINUS, menu), key(KeyEvent.VK_SUBTRACT, menu)),
                wheels = listOf(wheel("Zoom in", "Zoom out", menu, zoom = DesktopCommandZoomPolicy.ReaderZoomOut)),
            ),
            commandInput(
                id = "reader.zoom.reset",
                title = "Reset zoom",
                command = ChimahonDesktopCommand.ReaderResetZoom,
                areas = readerArea,
                behavior = readerBehavior.copy(zoomPolicy = DesktopCommandZoomPolicy.ReaderZoomReset),
                keys = listOf(key(KeyEvent.VK_0, menu)),
            ),
            commandInput(
                id = "reader.fit.width",
                title = "Fit width",
                command = ChimahonDesktopCommand.ReaderFitWidth,
                areas = readerArea,
                behavior = readerBehavior.copy(zoomPolicy = DesktopCommandZoomPolicy.ReaderFitWidth),
                keys = listOf(key(KeyEvent.VK_W, menuAlt), key(KeyEvent.VK_1, menuAlt)),
            ),
            commandInput(
                id = "reader.fit.height",
                title = "Fit height",
                command = ChimahonDesktopCommand.ReaderFitHeight,
                areas = readerArea,
                behavior = readerBehavior.copy(zoomPolicy = DesktopCommandZoomPolicy.ReaderFitHeight),
                keys = listOf(key(KeyEvent.VK_H, menuAlt), key(KeyEvent.VK_2, menuAlt)),
            ),
            commandInput(
                id = "reader.fit.screen",
                title = "Fit screen",
                command = ChimahonDesktopCommand.ReaderFitScreen,
                areas = readerArea,
                behavior = readerBehavior.copy(zoomPolicy = DesktopCommandZoomPolicy.ReaderFitScreen),
                keys = listOf(key(KeyEvent.VK_0, menuAlt)),
            ),
            commandInput(
                id = "reader.fit.toggle",
                title = "Toggle fit width/screen",
                command = ChimahonDesktopCommand.ReaderToggleFitWidthOrScreen,
                areas = readerArea,
                behavior = readerBehavior.copy(zoomPolicy = DesktopCommandZoomPolicy.ReaderToggleFit),
                keys = listOf(key(KeyEvent.VK_Z)),
                mouseButtons = listOf(mouseButton(MouseEvent.BUTTON1, clickCount = 2)),
            ),
            commandInput(
                id = "reader.scroll",
                title = "Reader scroll",
                areas = readerArea,
                behavior = readerBehavior.copy(scrollPolicy = DesktopCommandScrollPolicy.ReaderPageScroll),
                keys = listOf(
                    key(KeyEvent.VK_K),
                    key(KeyEvent.VK_J),
                    key(KeyEvent.VK_H),
                    key(KeyEvent.VK_L, shift),
                    key(KeyEvent.VK_UP, shift),
                    key(KeyEvent.VK_DOWN, shift),
                ),
                wheels = listOf(wheel("Page scroll up", "Page scroll down", alt, scroll = DesktopCommandScrollPolicy.ReaderPageScroll)),
            ),
        ) + readerActionMappings(readerArea, panelBehavior)
    }

    private fun readerActionMappings(
        readerArea: Set<DesktopCommandInputArea>,
        behavior: DesktopCommandInputBehavior,
    ): List<DesktopCommandInputMapping> {
        return listOf(
            commandInput("reader.stats", "Reader stats", ChimahonDesktopCommand.ReaderToggleStats, readerArea, behavior, listOf(key(KeyEvent.VK_I), key(KeyEvent.VK_I, menuAlt))),
            commandInput("reader.crop", "Crop borders", ChimahonDesktopCommand.ReaderToggleCrop, readerArea, behavior, listOf(key(KeyEvent.VK_F), key(KeyEvent.VK_F, menuAlt))),
            commandInput("reader.orientation", "Cycle orientation", ChimahonDesktopCommand.ReaderCycleOrientation, readerArea, behavior, listOf(key(KeyEvent.VK_T), key(KeyEvent.VK_T, menuAlt))),
            commandInput("reader.layout", "Cycle page layout", ChimahonDesktopCommand.ReaderCyclePageLayout, readerArea, behavior, listOf(key(KeyEvent.VK_L), key(KeyEvent.VK_L, menuAlt))),
            commandInput("reader.shift.double.pages", "Shift double pages", ChimahonDesktopCommand.ReaderShiftDoublePages, readerArea, behavior, listOf(key(KeyEvent.VK_P), key(KeyEvent.VK_P, menuAlt))),
            commandInput("reader.bookmark", "Bookmark chapter", ChimahonDesktopCommand.ReaderBookmarkChapter, readerArea, behavior, listOf(key(KeyEvent.VK_B), key(KeyEvent.VK_B, menuAlt))),
            commandInput("reader.download", "Download chapter", ChimahonDesktopCommand.ReaderDownloadChapter, readerArea, behavior, listOf(key(KeyEvent.VK_D), key(KeyEvent.VK_D, menuAlt))),
            commandInput("reader.mark.read", "Mark chapter read", ChimahonDesktopCommand.ReaderMarkChapterRead, readerArea, behavior, listOf(key(KeyEvent.VK_R), key(KeyEvent.VK_R, menuAlt))),
            commandInput("reader.open.url", "Open chapter URL", ChimahonDesktopCommand.ReaderOpenChapterUrl, readerArea, behavior, listOf(key(KeyEvent.VK_O), key(KeyEvent.VK_O, menuAlt))),
            commandInput("reader.share", "Share chapter URL", ChimahonDesktopCommand.ReaderShareChapter, readerArea, behavior, listOf(key(KeyEvent.VK_S, menuAltShift))),
        )
    }

    private fun lightNovelReaderMappings(): List<DesktopCommandInputMapping> {
        val area = setOf(DesktopCommandInputArea.LightNovelReader)
        val behavior = DesktopCommandInputBehavior(
            focusPolicy = DesktopCommandFocusPolicy.LightNovelReader,
            scrollPolicy = DesktopCommandScrollPolicy.LightNovelPage,
        )
        return listOf(
            commandInput(
                id = "ln.previous",
                title = "Previous page/chapter",
                areas = area,
                behavior = behavior,
                keys = listOf(key(KeyEvent.VK_LEFT), key(KeyEvent.VK_PAGE_UP), key(KeyEvent.VK_SPACE, shift)),
                wheels = listOf(wheel("Previous page", "Next page", scroll = DesktopCommandScrollPolicy.LightNovelPage)),
            ),
            commandInput(
                id = "ln.next",
                title = "Next page/chapter",
                areas = area,
                behavior = behavior,
                keys = listOf(key(KeyEvent.VK_RIGHT), key(KeyEvent.VK_PAGE_DOWN), key(KeyEvent.VK_SPACE)),
                wheels = listOf(wheel("Previous page", "Next page", scroll = DesktopCommandScrollPolicy.LightNovelPage)),
            ),
            commandInput(
                id = "ln.hud",
                title = "Toggle HUD",
                areas = area,
                behavior = behavior.copy(consumesWhenPanelsOpen = true),
                keys = listOf(key(KeyEvent.VK_ENTER)),
            ),
            commandInput(
                id = "ln.chapters",
                title = "Chapter drawer",
                areas = area,
                behavior = behavior.copy(consumesWhenPanelsOpen = true),
                keys = listOf(key(KeyEvent.VK_C)),
            ),
            commandInput(
                id = "ln.typography",
                title = "Typography",
                areas = area,
                behavior = behavior.copy(
                    zoomPolicy = DesktopCommandZoomPolicy.LightNovelTextScale,
                    consumesWhenPanelsOpen = true,
                ),
                keys = listOf(key(KeyEvent.VK_S), key(KeyEvent.VK_COMMA, menuAlt)),
                wheels = listOf(wheel("Scale text down", "Scale text up", menu, zoom = DesktopCommandZoomPolicy.LightNovelTextScale)),
            ),
            commandInput(
                id = "ln.direction",
                title = "Reading direction",
                areas = area,
                behavior = behavior,
                keys = listOf(key(KeyEvent.VK_T)),
            ),
            commandInput(
                id = "ln.mode",
                title = "Paged/continuous mode",
                areas = area,
                behavior = behavior.copy(scrollPolicy = DesktopCommandScrollPolicy.LightNovelContinuous),
                keys = listOf(key(KeyEvent.VK_M)),
            ),
            commandInput(
                id = "ln.lookup",
                title = "Lookup selected text",
                areas = area,
                behavior = behavior.copy(
                    focusPolicy = DesktopCommandFocusPolicy.TextSelection,
                    consumesWhenPanelsOpen = true,
                ),
                keys = listOf(key(KeyEvent.VK_G)),
            ),
        )
    }

    private fun animePlayerMappings(): List<DesktopCommandInputMapping> {
        val area = setOf(DesktopCommandInputArea.AnimePlayer)
        val global = DesktopCommandInputBehavior(DesktopCommandFocusPolicy.Global)
        val playerBehavior = DesktopCommandInputBehavior(
            focusPolicy = DesktopCommandFocusPolicy.PlayerSurface,
            scrollPolicy = DesktopCommandScrollPolicy.PlayerSeek,
        )
        return listOf(
            commandInput("anime.library", "Anime library", ChimahonDesktopCommand.AnimeLibrary, area, global, listOf(key(KeyEvent.VK_A, menuAlt))),
            commandInput("anime.updates", "Anime updates", ChimahonDesktopCommand.AnimeUpdates, area, global, listOf(key(KeyEvent.VK_U, menuAlt))),
            commandInput("anime.history", "Anime history", ChimahonDesktopCommand.AnimeHistory, area, global, listOf(key(KeyEvent.VK_H, menuAlt))),
            commandInput("anime.downloads", "Anime download queue", ChimahonDesktopCommand.AnimeDownloadQueue, area, global, listOf(key(KeyEvent.VK_Q, menuAltShift))),
            commandInput("anime.settings", "Anime settings", ChimahonDesktopCommand.AnimeSettings, area, global, listOf(key(KeyEvent.VK_COMMA, menuAltShift))),
            commandInput("player.play.pause", "Play/pause", ChimahonDesktopCommand.PlayerTogglePlayback, area, playerBehavior, listOf(key(KeyEvent.VK_SPACE, menuShift))),
            commandInput(
                id = "player.seek.backward",
                title = "Seek backward",
                command = ChimahonDesktopCommand.PlayerSeekBackward,
                areas = area,
                behavior = playerBehavior,
                keys = listOf(key(KeyEvent.VK_J, menuShift), key(KeyEvent.VK_LEFT, menuShift)),
                wheels = listOf(wheel("Seek backward", "Seek forward", shift, scroll = DesktopCommandScrollPolicy.PlayerSeek)),
            ),
            commandInput(
                id = "player.seek.forward",
                title = "Seek forward",
                command = ChimahonDesktopCommand.PlayerSeekForward,
                areas = area,
                behavior = playerBehavior,
                keys = listOf(key(KeyEvent.VK_L, menuShift), key(KeyEvent.VK_RIGHT, menuShift)),
                wheels = listOf(wheel("Seek backward", "Seek forward", shift, scroll = DesktopCommandScrollPolicy.PlayerSeek)),
            ),
            commandInput("player.previous.episode", "Previous episode", ChimahonDesktopCommand.PlayerPreviousEpisode, area, playerBehavior, listOf(key(KeyEvent.VK_OPEN_BRACKET, menuShift))),
            commandInput("player.next.episode", "Next episode", ChimahonDesktopCommand.PlayerNextEpisode, area, playerBehavior, listOf(key(KeyEvent.VK_CLOSE_BRACKET, menuShift))),
            commandInput("player.subtitles", "Subtitle settings", ChimahonDesktopCommand.PlayerSubtitleSettings, area, playerBehavior, listOf(key(KeyEvent.VK_S, menuShift))),
            commandInput("player.audio.delay", "Audio delay", ChimahonDesktopCommand.PlayerAudioDelay, area, playerBehavior, listOf(key(KeyEvent.VK_Y, menuShift))),
            commandInput("player.video.filters", "Video filters", ChimahonDesktopCommand.PlayerVideoFilters, area, playerBehavior, listOf(key(KeyEvent.VK_V, menuShift))),
            commandInput("player.settings", "Player settings", ChimahonDesktopCommand.PlayerSettings, area, playerBehavior, listOf(key(KeyEvent.VK_COMMA, menuShift))),
        )
    }

    private fun commandInput(
        id: String,
        title: String,
        command: ChimahonDesktopCommand? = null,
        areas: Set<DesktopCommandInputArea>,
        behavior: DesktopCommandInputBehavior,
        keys: List<DesktopCommandKeyMapping> = emptyList(),
        mouseButtons: List<DesktopCommandMouseButtonMapping> = emptyList(),
        wheels: List<DesktopCommandWheelMapping> = emptyList(),
    ): DesktopCommandInputMapping {
        return DesktopCommandInputMapping(
            id = id,
            title = title,
            command = command,
            areas = areas,
            keyMappings = keys,
            mouseButtonMappings = mouseButtons,
            wheelMappings = wheels,
            behavior = behavior,
        )
    }

    private fun key(
        keyCode: Int,
        modifiers: DesktopReaderShortcutModifiers = plain,
    ): DesktopCommandKeyMapping {
        return DesktopCommandKeyMapping(keyCode = keyCode, modifiers = modifiers)
    }

    private fun mouseButton(
        button: Int,
        modifiers: DesktopReaderShortcutModifiers = plain,
        clickCount: Int = 1,
    ): DesktopCommandMouseButtonMapping {
        return DesktopCommandMouseButtonMapping(
            button = button,
            modifiers = modifiers,
            clickCount = clickCount,
        )
    }

    private fun wheel(
        negativeActionTitle: String,
        positiveActionTitle: String,
        modifiers: DesktopReaderShortcutModifiers = plain,
        scroll: DesktopCommandScrollPolicy = DesktopCommandScrollPolicy.FocusedScrollable,
        zoom: DesktopCommandZoomPolicy = DesktopCommandZoomPolicy.None,
    ): DesktopCommandWheelMapping {
        return DesktopCommandWheelMapping(
            negativeActionTitle = negativeActionTitle,
            positiveActionTitle = positiveActionTitle,
            modifiers = modifiers,
            scrollPolicy = scroll,
            zoomPolicy = zoom,
        )
    }

    private val plain = DesktopReaderShortcutModifiers()
    private val shift = DesktopReaderShortcutModifiers(shift = true)
    private val alt = DesktopReaderShortcutModifiers(alt = true)
    private val menu = DesktopReaderShortcutModifiers(menuShortcut = true)
    private val menuShift = DesktopReaderShortcutModifiers(menuShortcut = true, shift = true)
    private val menuAlt = DesktopReaderShortcutModifiers(menuShortcut = true, alt = true)
    private val menuAltShift = DesktopReaderShortcutModifiers(menuShortcut = true, alt = true, shift = true)
}

private fun Int.desktopCommandKeyLabel(): String {
    return when (this) {
        KeyEvent.VK_LEFT -> "Left"
        KeyEvent.VK_RIGHT -> "Right"
        KeyEvent.VK_UP -> "Up"
        KeyEvent.VK_DOWN -> "Down"
        KeyEvent.VK_PAGE_UP -> "Page Up"
        KeyEvent.VK_PAGE_DOWN -> "Page Down"
        KeyEvent.VK_OPEN_BRACKET -> "["
        KeyEvent.VK_CLOSE_BRACKET -> "]"
        KeyEvent.VK_COMMA -> ","
        KeyEvent.VK_EQUALS -> "="
        KeyEvent.VK_PLUS -> "+"
        KeyEvent.VK_ADD -> "Num +"
        KeyEvent.VK_MINUS -> "-"
        KeyEvent.VK_SUBTRACT -> "Num -"
        else -> readerKeyLabel()
    }
}
