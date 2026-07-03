package chimahon.desktop.input

import app.chimahon.shared.ChimahonDesktopCommand

enum class DesktopReaderInputCategory {
    PageNavigation,
    ChapterNavigation,
    ReaderChrome,
    ReadingMode,
    ZoomAndFit,
    Scroll,
    Ocr,
    ChapterAction,
    Window,
}

enum class DesktopReaderInputAction(
    val category: DesktopReaderInputCategory,
    val command: ChimahonDesktopCommand? = null,
    val defaultTitle: String,
) {
    PreviousPage(
        DesktopReaderInputCategory.PageNavigation,
        ChimahonDesktopCommand.ReaderPreviousPage,
        "Previous page",
    ),
    NextPage(
        DesktopReaderInputCategory.PageNavigation,
        ChimahonDesktopCommand.ReaderNextPage,
        "Next page",
    ),
    FirstPage(
        DesktopReaderInputCategory.PageNavigation,
        ChimahonDesktopCommand.ReaderFirstPage,
        "First page",
    ),
    LastPage(
        DesktopReaderInputCategory.PageNavigation,
        ChimahonDesktopCommand.ReaderLastPage,
        "Last page",
    ),
    PreviousChapter(
        DesktopReaderInputCategory.ChapterNavigation,
        ChimahonDesktopCommand.ReaderPreviousChapter,
        "Previous chapter",
    ),
    NextChapter(
        DesktopReaderInputCategory.ChapterNavigation,
        ChimahonDesktopCommand.ReaderNextChapter,
        "Next chapter",
    ),
    ToggleToolbar(
        DesktopReaderInputCategory.ReaderChrome,
        ChimahonDesktopCommand.ReaderToggleControls,
        "Toggle toolbar",
    ),
    OpenSettings(
        DesktopReaderInputCategory.ReaderChrome,
        ChimahonDesktopCommand.ReaderOpenSettings,
        "Reader settings",
    ),
    OpenChapterList(
        DesktopReaderInputCategory.ReaderChrome,
        ChimahonDesktopCommand.ReaderOpenChapters,
        "Chapter list",
    ),
    ToggleStats(
        DesktopReaderInputCategory.ReaderChrome,
        ChimahonDesktopCommand.ReaderToggleStats,
        "Reader statistics",
    ),
    CycleReadingMode(
        DesktopReaderInputCategory.ReadingMode,
        ChimahonDesktopCommand.ReaderCycleMode,
        "Cycle reading mode",
    ),
    CycleOrientation(
        DesktopReaderInputCategory.ReadingMode,
        ChimahonDesktopCommand.ReaderCycleOrientation,
        "Cycle orientation",
    ),
    CyclePageLayout(
        DesktopReaderInputCategory.ReadingMode,
        ChimahonDesktopCommand.ReaderCyclePageLayout,
        "Cycle page layout",
    ),
    ShiftDoublePages(
        DesktopReaderInputCategory.ReadingMode,
        ChimahonDesktopCommand.ReaderShiftDoublePages,
        "Shift double pages",
    ),
    ToggleCropBorders(
        DesktopReaderInputCategory.ReadingMode,
        ChimahonDesktopCommand.ReaderToggleCrop,
        "Crop borders",
    ),
    ToggleOcrLookup(
        DesktopReaderInputCategory.Ocr,
        ChimahonDesktopCommand.ReaderToggleOcrLookup,
        "OCR lookup",
    ),
    BookmarkChapter(
        DesktopReaderInputCategory.ChapterAction,
        ChimahonDesktopCommand.ReaderBookmarkChapter,
        "Bookmark chapter",
    ),
    DownloadChapter(
        DesktopReaderInputCategory.ChapterAction,
        ChimahonDesktopCommand.ReaderDownloadChapter,
        "Download chapter",
    ),
    MarkChapterRead(
        DesktopReaderInputCategory.ChapterAction,
        ChimahonDesktopCommand.ReaderMarkChapterRead,
        "Mark chapter read",
    ),
    OpenChapterUrl(
        DesktopReaderInputCategory.ChapterAction,
        ChimahonDesktopCommand.ReaderOpenChapterUrl,
        "Open chapter URL",
    ),
    ShareChapter(
        DesktopReaderInputCategory.ChapterAction,
        ChimahonDesktopCommand.ReaderShareChapter,
        "Share chapter URL",
    ),
    ZoomIn(
        DesktopReaderInputCategory.ZoomAndFit,
        ChimahonDesktopCommand.ReaderZoomIn,
        "Zoom in",
    ),
    ZoomOut(
        DesktopReaderInputCategory.ZoomAndFit,
        ChimahonDesktopCommand.ReaderZoomOut,
        "Zoom out",
    ),
    ResetZoom(
        DesktopReaderInputCategory.ZoomAndFit,
        ChimahonDesktopCommand.ReaderResetZoom,
        "Reset zoom",
    ),
    FitWidth(
        DesktopReaderInputCategory.ZoomAndFit,
        ChimahonDesktopCommand.ReaderFitWidth,
        "Fit width",
    ),
    FitHeight(
        DesktopReaderInputCategory.ZoomAndFit,
        ChimahonDesktopCommand.ReaderFitHeight,
        "Fit height",
    ),
    FitScreen(
        DesktopReaderInputCategory.ZoomAndFit,
        ChimahonDesktopCommand.ReaderFitScreen,
        "Fit screen",
    ),
    ToggleFitWidthOrScreen(
        DesktopReaderInputCategory.ZoomAndFit,
        ChimahonDesktopCommand.ReaderToggleFitWidthOrScreen,
        defaultTitle = "Toggle fit width or screen",
    ),
    ScrollUp(DesktopReaderInputCategory.Scroll, defaultTitle = "Scroll up"),
    ScrollDown(DesktopReaderInputCategory.Scroll, defaultTitle = "Scroll down"),
    ScrollLeft(DesktopReaderInputCategory.Scroll, defaultTitle = "Scroll left"),
    ScrollRight(DesktopReaderInputCategory.Scroll, defaultTitle = "Scroll right"),
    PageScrollUp(DesktopReaderInputCategory.Scroll, defaultTitle = "Page scroll up"),
    PageScrollDown(DesktopReaderInputCategory.Scroll, defaultTitle = "Page scroll down"),
    ToggleFullScreen(DesktopReaderInputCategory.Window, defaultTitle = "Full screen"),
}

enum class DesktopReaderInputSource {
    Keyboard,
    MouseButton,
    MouseWheel,
}

enum class DesktopReaderMouseWheelMode {
    NavigatePages,
    ScrollPages,
    Zoom,
    Disabled,
}

data class DesktopReaderShortcutModifiers(
    val menuShortcut: Boolean = false,
    val alt: Boolean = false,
    val shift: Boolean = false,
    val ctrl: Boolean = false,
    val meta: Boolean = false,
) {
    val hasAny: Boolean
        get() = menuShortcut || alt || shift || ctrl || meta
}

data class DesktopReaderKeyBinding(
    val action: DesktopReaderInputAction,
    val keyCode: Int,
    val modifiers: DesktopReaderShortcutModifiers = DesktopReaderShortcutModifiers(),
    val repeatable: Boolean = true,
    val consumesWhenPanelsAreOpen: Boolean = false,
)

data class DesktopReaderMouseButtonBinding(
    val action: DesktopReaderInputAction,
    val button: Int,
    val modifiers: DesktopReaderShortcutModifiers = DesktopReaderShortcutModifiers(),
    val clickCount: Int = 1,
    val repeatable: Boolean = false,
)

data class DesktopReaderMouseWheelBinding(
    val mode: DesktopReaderMouseWheelMode = DesktopReaderMouseWheelMode.NavigatePages,
    val inverted: Boolean = false,
    val menuShortcutOverridesToZoom: Boolean = true,
    val shiftOverridesToChapterNavigation: Boolean = true,
    val altOverridesToPageScroll: Boolean = true,
)

data class DesktopReaderInputScheme(
    val keyBindings: List<DesktopReaderKeyBinding>,
    val mouseButtonBindings: List<DesktopReaderMouseButtonBinding>,
    val mouseWheelBinding: DesktopReaderMouseWheelBinding = DesktopReaderMouseWheelBinding(),
) {
    fun bindingsFor(action: DesktopReaderInputAction): List<DesktopReaderKeyBinding> {
        return keyBindings.filter { it.action == action }
    }

    fun commandBindings(): List<DesktopReaderKeyBinding> {
        return keyBindings.filter { it.action.command != null }
    }
}

data class DesktopReaderInputMatch(
    val action: DesktopReaderInputAction,
    val source: DesktopReaderInputSource,
    val bindingLabel: String,
    val amount: Float = 1f,
) {
    val command: ChimahonDesktopCommand?
        get() = action.command

    val requestsFullScreen: Boolean
        get() = action == DesktopReaderInputAction.ToggleFullScreen

    val isDesktopOnlyReaderIntent: Boolean
        get() = command == null && !requestsFullScreen
}

data class DesktopReaderShortcutHelpLine(
    val category: DesktopReaderInputCategory,
    val action: DesktopReaderInputAction,
    val title: String,
    val shortcuts: List<String>,
)
