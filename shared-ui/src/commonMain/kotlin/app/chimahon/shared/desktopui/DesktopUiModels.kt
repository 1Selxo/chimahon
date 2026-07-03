package app.chimahon.shared.desktopui

enum class ChimahonDesktopShortcutScope(val title: String) {
    Navigation("Navigation"),
    App("App"),
    Browse("Browse"),
    Reader("Reader"),
    Player("Player"),
    Anime("Anime"),
    LightNovel("LN"),
}

enum class ChimahonDesktopInputDevice(val title: String) {
    Keyboard("Keyboard"),
    Mouse("Mouse"),
    Touchpad("Touchpad"),
    Gamepad("Gamepad"),
}

enum class ChimahonDesktopActionStyle {
    Neutral,
    Primary,
    Destructive,
    Warning,
}

data class ChimahonDesktopShortcut(
    val title: String,
    val keys: List<String>,
    val scope: ChimahonDesktopShortcutScope,
    val description: String = "",
    val enabled: Boolean = true,
)

data class ChimahonDesktopShortcutGroup(
    val title: String,
    val shortcuts: List<ChimahonDesktopShortcut>,
    val subtitle: String = "",
)

data class ChimahonDesktopInputHint(
    val title: String,
    val detail: String,
    val device: ChimahonDesktopInputDevice,
    val keys: List<String> = emptyList(),
    val enabled: Boolean = true,
)

data class ChimahonDesktopActionItem(
    val id: String,
    val title: String,
    val description: String = "",
    val shortcut: String? = null,
    val icon: ChimahonDesktopIcon = ChimahonDesktopIcon.More,
    val style: ChimahonDesktopActionStyle = ChimahonDesktopActionStyle.Neutral,
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val loading: Boolean = false,
)

data class ChimahonDesktopReaderOverlayState(
    val title: String,
    val subtitle: String = "",
    val pageLabel: String = "",
    val modeLabel: String = "",
    val controlsVisible: Boolean = true,
    val ocrActive: Boolean = false,
    val cropActive: Boolean = false,
    val statsActive: Boolean = false,
    val chapterListActive: Boolean = false,
    val actions: List<ChimahonDesktopActionItem> = emptyList(),
)

data class ChimahonDesktopToolbarState(
    val title: String,
    val subtitle: String = "",
    val leadingActions: List<ChimahonDesktopActionItem> = emptyList(),
    val trailingActions: List<ChimahonDesktopActionItem> = emptyList(),
)

data class ChimahonDesktopHelpPanelState(
    val title: String = "Desktop controls",
    val subtitle: String = "",
    val shortcutGroups: List<ChimahonDesktopShortcutGroup> = ChimahonDefaultDesktopShortcutGroups,
    val inputHints: List<ChimahonDesktopInputHint> = ChimahonDefaultDesktopInputHints,
)

val ChimahonDefaultDesktopShortcutGroups: List<ChimahonDesktopShortcutGroup> = listOf(
    ChimahonDesktopShortcutGroup(
        title = "Navigation",
        subtitle = "Global app commands and focus-aware screen navigation",
        shortcuts = listOf(
            ChimahonDesktopShortcut("Back / close panel", listOf("Esc", "Alt+Left"), ChimahonDesktopShortcutScope.Navigation, "Routes to the active panel, reader/player surface, or previous screen."),
            ChimahonDesktopShortcut("Search focus", listOf("Ctrl+F", "Cmd+F"), ChimahonDesktopShortcutScope.Navigation, "Moves focus to search for the current screen."),
            ChimahonDesktopShortcut("Toggle filters", listOf("Ctrl+Shift+F", "Cmd+Shift+F"), ChimahonDesktopShortcutScope.Navigation, "Shows or hides current screen filters."),
            ChimahonDesktopShortcut("Refresh", listOf("F5", "Ctrl+R", "Cmd+R"), ChimahonDesktopShortcutScope.Navigation, "Refreshes the active source, library, history, or settings surface."),
            ChimahonDesktopShortcut("Library", listOf("Ctrl+1", "Ctrl+L", "Cmd+1", "Cmd+L"), ChimahonDesktopShortcutScope.Navigation),
            ChimahonDesktopShortcut("Novels", listOf("Ctrl+N", "Cmd+N"), ChimahonDesktopShortcutScope.LightNovel),
            ChimahonDesktopShortcut("Updates", listOf("Ctrl+2", "Ctrl+U", "Cmd+2", "Cmd+U"), ChimahonDesktopShortcutScope.Navigation),
            ChimahonDesktopShortcut("History", listOf("Ctrl+3", "Ctrl+H", "Cmd+3", "Cmd+H"), ChimahonDesktopShortcutScope.Navigation),
            ChimahonDesktopShortcut("More", listOf("Ctrl+5", "Ctrl+M", "Cmd+5", "Cmd+M"), ChimahonDesktopShortcutScope.Navigation),
            ChimahonDesktopShortcut("Settings", listOf("Ctrl+,", "Cmd+,"), ChimahonDesktopShortcutScope.Navigation),
            ChimahonDesktopShortcut("Download queue", listOf("Ctrl+D", "Cmd+D"), ChimahonDesktopShortcutScope.Navigation),
        ),
    ),
    ChimahonDesktopShortcutGroup(
        title = "Browse",
        subtitle = "Source, extension, feed, and migration commands",
        shortcuts = listOf(
            ChimahonDesktopShortcut("Browse sources", listOf("Ctrl+4", "Ctrl+B", "Cmd+4", "Cmd+B"), ChimahonDesktopShortcutScope.Browse, "Opens manga source browsing and keeps browse content scrollable."),
            ChimahonDesktopShortcut("Browse extensions", listOf("Ctrl+E", "Cmd+E"), ChimahonDesktopShortcutScope.Browse),
            ChimahonDesktopShortcut("Browse feed", listOf("Ctrl+Shift+B", "Cmd+Shift+B"), ChimahonDesktopShortcutScope.Browse),
            ChimahonDesktopShortcut("Migrate", listOf("Ctrl+Shift+M", "Cmd+Shift+M"), ChimahonDesktopShortcutScope.Browse),
            ChimahonDesktopShortcut("Browse anime sources", listOf("Ctrl+Alt+Shift+A", "Cmd+Opt+Shift+A"), ChimahonDesktopShortcutScope.Anime),
            ChimahonDesktopShortcut("Anime extensions", listOf("Ctrl+Alt+Shift+E", "Cmd+Opt+Shift+E"), ChimahonDesktopShortcutScope.Anime),
        ),
    ),
    ChimahonDesktopShortcutGroup(
        title = "Reader",
        subtitle = "Manga reader keys, mouse buttons, wheel, zoom, and panel focus",
        shortcuts = listOf(
            ChimahonDesktopShortcut("Previous / next page", listOf("Left", "Right", "Up", "Down", "PageUp", "PageDown", "Space"), ChimahonDesktopShortcutScope.Reader, "Wheel and side mouse buttons route only while the reader is active."),
            ChimahonDesktopShortcut("First / last page", listOf("Home", "End"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("Previous / next chapter", listOf("Shift+Left", "Shift+Right", "Shift+PageUp", "Shift+PageDown"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("Toggle controls", listOf("Enter", "Ctrl+Alt+Enter", "Cmd+Alt+Enter"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("Reading mode", listOf("M", "Ctrl+Alt+M", "Cmd+Alt+M"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("Reader settings", listOf("S", "Ctrl+Alt+,", "Cmd+Alt+,"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("Chapter list", listOf("C", "Ctrl+Alt+C", "Cmd+Alt+C"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("OCR lookup", listOf("G", "Ctrl+Alt+G", "Cmd+Alt+G"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("Crop borders", listOf("F", "Ctrl+Alt+F", "Cmd+Alt+F"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("Stats", listOf("I", "Ctrl+Alt+I", "Cmd+Alt+I"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("Orientation / layout", listOf("T", "L", "P", "Ctrl+Alt+T/L/P", "Cmd+Alt+T/L/P"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("Zoom", listOf("Ctrl++", "Ctrl+-", "Ctrl+0", "Cmd++", "Cmd+-", "Cmd+0"), ChimahonDesktopShortcutScope.Reader, "Ctrl/Cmd + wheel zooms around the reader anchor."),
            ChimahonDesktopShortcut("Fit page", listOf("Ctrl+Alt+0", "Ctrl+Alt+1", "Ctrl+Alt+2", "Cmd+Alt+0", "Cmd+Alt+1", "Cmd+Alt+2"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("Bookmark / download / read", listOf("B", "D", "R"), ChimahonDesktopShortcutScope.Reader),
            ChimahonDesktopShortcut("Open / share chapter URL", listOf("O", "Ctrl+Alt+O", "Ctrl+Alt+Shift+S", "Cmd+Alt+O", "Cmd+Alt+Shift+S"), ChimahonDesktopShortcutScope.Reader),
        ),
    ),
    ChimahonDesktopShortcutGroup(
        title = "Light novel reader",
        subtitle = "Paged and continuous LN reader controls",
        shortcuts = listOf(
            ChimahonDesktopShortcut("Previous / next", listOf("Left", "Right", "PageUp", "PageDown", "Space"), ChimahonDesktopShortcutScope.LightNovel, "Routes to page or continuous scroll behavior for the active LN mode."),
            ChimahonDesktopShortcut("Toggle HUD", listOf("Enter"), ChimahonDesktopShortcutScope.LightNovel),
            ChimahonDesktopShortcut("Chapter drawer", listOf("C"), ChimahonDesktopShortcutScope.LightNovel),
            ChimahonDesktopShortcut("Typography", listOf("S", "Ctrl+Alt+,", "Cmd+Alt+,"), ChimahonDesktopShortcutScope.LightNovel, "Ctrl/Cmd + wheel adjusts text scale where supported."),
            ChimahonDesktopShortcut("Direction / mode", listOf("T", "M"), ChimahonDesktopShortcutScope.LightNovel),
            ChimahonDesktopShortcut("Lookup selected text", listOf("G"), ChimahonDesktopShortcutScope.LightNovel),
        ),
    ),
    ChimahonDesktopShortcutGroup(
        title = "Anime",
        subtitle = "Anime library, browse, queue, and settings navigation",
        shortcuts = listOf(
            ChimahonDesktopShortcut("Anime library", listOf("Ctrl+Alt+A", "Cmd+Opt+A"), ChimahonDesktopShortcutScope.Anime),
            ChimahonDesktopShortcut("Anime updates", listOf("Ctrl+Alt+U", "Cmd+Opt+U"), ChimahonDesktopShortcutScope.Anime),
            ChimahonDesktopShortcut("Anime history", listOf("Ctrl+Alt+H", "Cmd+Opt+H"), ChimahonDesktopShortcutScope.Anime),
            ChimahonDesktopShortcut("Anime download queue", listOf("Ctrl+Alt+Shift+Q", "Cmd+Opt+Shift+Q"), ChimahonDesktopShortcutScope.Anime),
            ChimahonDesktopShortcut("Anime settings", listOf("Ctrl+Alt+Shift+,", "Cmd+Opt+Shift+,"), ChimahonDesktopShortcutScope.Anime),
        ),
    ),
    ChimahonDesktopShortcutGroup(
        title = "Player",
        subtitle = "Anime playback controls and seek behavior",
        shortcuts = listOf(
            ChimahonDesktopShortcut("Play / pause", listOf("Ctrl+Shift+Space", "Cmd+Shift+Space"), ChimahonDesktopShortcutScope.Player, "Routes only while player surface is active."),
            ChimahonDesktopShortcut("Seek", listOf("Ctrl+Shift+J", "Ctrl+Shift+L", "Cmd+Shift+J", "Cmd+Shift+L"), ChimahonDesktopShortcutScope.Player, "Shift + wheel can map to seek steps."),
            ChimahonDesktopShortcut("Previous / next episode", listOf("Ctrl+Shift+[", "Ctrl+Shift+]", "Cmd+Shift+[", "Cmd+Shift+]"), ChimahonDesktopShortcutScope.Player),
            ChimahonDesktopShortcut("Subtitles", listOf("Ctrl+Shift+S", "Cmd+Shift+S"), ChimahonDesktopShortcutScope.Player),
            ChimahonDesktopShortcut("Audio delay", listOf("Ctrl+Shift+Y", "Cmd+Shift+Y"), ChimahonDesktopShortcutScope.Player),
            ChimahonDesktopShortcut("Video filters", listOf("Ctrl+Shift+V", "Cmd+Shift+V"), ChimahonDesktopShortcutScope.Player),
            ChimahonDesktopShortcut("Player settings", listOf("Ctrl+Shift+,", "Cmd+Shift+,"), ChimahonDesktopShortcutScope.Player),
        ),
    ),
)

val ChimahonDefaultDesktopInputHints: List<ChimahonDesktopInputHint> = listOf(
    ChimahonDesktopInputHint(
        title = "Mouse side buttons",
        detail = "Use back and forward mouse buttons for reader page navigation; hold Shift for chapter navigation.",
        device = ChimahonDesktopInputDevice.Mouse,
        keys = listOf("Mouse 4", "Mouse 5", "Shift"),
    ),
    ChimahonDesktopInputHint(
        title = "Mouse wheel",
        detail = "Reader wheel advances pages by default; Ctrl/Cmd zooms, Shift changes chapters, and Alt scrolls page segments.",
        device = ChimahonDesktopInputDevice.Mouse,
        keys = listOf("Wheel", "Ctrl/Cmd", "Shift", "Alt"),
    ),
    ChimahonDesktopInputHint(
        title = "Touchpad",
        detail = "Two-finger scroll follows the active reader mode: page turns, continuous scrolling, LN scroll, or zoom modifier behavior.",
        device = ChimahonDesktopInputDevice.Touchpad,
        keys = listOf("Two-finger scroll", "Tap zones"),
    ),
    ChimahonDesktopInputHint(
        title = "Focus routing",
        detail = "Global navigation works across the app; reader, LN, browse, and player commands only capture while their surface is active.",
        device = ChimahonDesktopInputDevice.Keyboard,
        keys = listOf("Esc", "Ctrl/Cmd", "Reader", "Player"),
    ),
    ChimahonDesktopInputHint(
        title = "Reader zoom",
        detail = "Zoom and fit commands keep the reader content focused and preserve the pointer or viewport anchor where possible.",
        device = ChimahonDesktopInputDevice.Mouse,
        keys = listOf("Ctrl/Cmd+Wheel", "Ctrl/Cmd+0", "Ctrl/Cmd+Alt+0/1/2"),
    ),
    ChimahonDesktopInputHint(
        title = "LN reading",
        detail = "Paged LN controls use arrows, Page Up/Down, Space, and Enter; continuous mode treats wheel input as scroll.",
        device = ChimahonDesktopInputDevice.Keyboard,
        keys = listOf("Left/Right", "Space", "Enter", "Wheel"),
    ),
    ChimahonDesktopInputHint(
        title = "Player seek",
        detail = "Player shortcuts are scoped to the active video surface, with Shift + wheel reserved for seek-step behavior.",
        device = ChimahonDesktopInputDevice.Mouse,
        keys = listOf("Ctrl/Cmd+Shift+J/L", "Shift+Wheel"),
    ),
)

fun List<ChimahonDesktopShortcutGroup>.filterDesktopShortcuts(
    query: String,
    scope: ChimahonDesktopShortcutScope? = null,
): List<ChimahonDesktopShortcutGroup> {
    val normalizedQuery = query.trim()
    return mapNotNull { group ->
        val shortcuts = group.shortcuts.filter { shortcut ->
            (scope == null || shortcut.scope == scope) &&
                (
                    normalizedQuery.isBlank() ||
                        shortcut.title.contains(normalizedQuery, ignoreCase = true) ||
                        shortcut.description.contains(normalizedQuery, ignoreCase = true) ||
                        shortcut.keys.any { it.contains(normalizedQuery, ignoreCase = true) }
                    )
        }
        if (shortcuts.isEmpty()) null else group.copy(shortcuts = shortcuts)
    }
}

fun ChimahonDesktopActionItem.isDangerous(): Boolean {
    return style == ChimahonDesktopActionStyle.Destructive
}
