package app.chimahon.shared.readerinputui

import app.chimahon.shared.ChimahonDualPageMode
import app.chimahon.shared.ChimahonReaderKeyboardScheme
import app.chimahon.shared.ChimahonReaderMode
import app.chimahon.shared.ChimahonReaderMouseWheelAction
import app.chimahon.shared.ChimahonReaderNavigationMode
import app.chimahon.shared.ChimahonReaderScale
import app.chimahon.shared.ChimahonReaderSettings
import app.chimahon.shared.ChimahonReaderTapNavigationLayout
import app.chimahon.shared.ChimahonTapZoneInvert
import kotlin.math.abs
import kotlin.math.min

enum class ChimahonReaderInputActionCategory(val title: String) {
    Navigation("Navigation"),
    Chapter("Chapter"),
    Hud("HUD"),
    Display("Display"),
    Zoom("Zoom"),
    ChapterAction("Chapter action"),
    System("System"),
}

enum class ChimahonReaderInputActionId(
    val title: String,
    val category: ChimahonReaderInputActionCategory,
    val menuLabel: String = title,
    val contentDescription: String = title,
) {
    PreviousPage(
        title = "Previous page",
        category = ChimahonReaderInputActionCategory.Navigation,
    ),
    NextPage(
        title = "Next page",
        category = ChimahonReaderInputActionCategory.Navigation,
    ),
    ScrollUp(
        title = "Scroll up",
        category = ChimahonReaderInputActionCategory.Navigation,
    ),
    ScrollDown(
        title = "Scroll down",
        category = ChimahonReaderInputActionCategory.Navigation,
    ),
    FirstPage(
        title = "First page",
        category = ChimahonReaderInputActionCategory.Navigation,
    ),
    LastPage(
        title = "Last page",
        category = ChimahonReaderInputActionCategory.Navigation,
    ),
    PreviousChapter(
        title = "Previous chapter",
        category = ChimahonReaderInputActionCategory.Chapter,
    ),
    NextChapter(
        title = "Next chapter",
        category = ChimahonReaderInputActionCategory.Chapter,
    ),
    ToggleControls(
        title = "Toggle controls",
        category = ChimahonReaderInputActionCategory.Hud,
        menuLabel = "Controls",
    ),
    DismissPanels(
        title = "Dismiss panels",
        category = ChimahonReaderInputActionCategory.System,
    ),
    Back(
        title = "Back",
        category = ChimahonReaderInputActionCategory.System,
    ),
    CycleMode(
        title = "Reader mode",
        category = ChimahonReaderInputActionCategory.Display,
        menuLabel = "Mode",
        contentDescription = "Change reader mode",
    ),
    OpenSettings(
        title = "Reader settings",
        category = ChimahonReaderInputActionCategory.Hud,
        menuLabel = "Settings",
    ),
    OpenChapters(
        title = "Chapters",
        category = ChimahonReaderInputActionCategory.Hud,
        menuLabel = "Chapters",
    ),
    ToggleStats(
        title = "Reader statistics",
        category = ChimahonReaderInputActionCategory.Hud,
        menuLabel = "Stats",
    ),
    ToggleCrop(
        title = "Crop borders",
        category = ChimahonReaderInputActionCategory.Display,
        menuLabel = "Crop",
    ),
    ToggleOcrLookup(
        title = "OCR lookup",
        category = ChimahonReaderInputActionCategory.Hud,
        menuLabel = "OCR",
        contentDescription = "Toggle GLens OCR lookup",
    ),
    CycleOrientation(
        title = "Rotation",
        category = ChimahonReaderInputActionCategory.Display,
    ),
    CyclePageLayout(
        title = "Page layout",
        category = ChimahonReaderInputActionCategory.Display,
    ),
    ShiftDoublePages(
        title = "Shift double pages",
        category = ChimahonReaderInputActionCategory.Display,
    ),
    FitScreen(
        title = "Fit screen",
        category = ChimahonReaderInputActionCategory.Zoom,
    ),
    FitWidth(
        title = "Fit width",
        category = ChimahonReaderInputActionCategory.Zoom,
    ),
    FitHeight(
        title = "Fit height",
        category = ChimahonReaderInputActionCategory.Zoom,
    ),
    ZoomIn(
        title = "Zoom in",
        category = ChimahonReaderInputActionCategory.Zoom,
    ),
    ZoomOut(
        title = "Zoom out",
        category = ChimahonReaderInputActionCategory.Zoom,
    ),
    ResetZoom(
        title = "Reset zoom",
        category = ChimahonReaderInputActionCategory.Zoom,
    ),
    ToggleFitWidthOrScreen(
        title = "Toggle fit width/screen",
        category = ChimahonReaderInputActionCategory.Zoom,
    ),
    BookmarkChapter(
        title = "Bookmark chapter",
        category = ChimahonReaderInputActionCategory.ChapterAction,
        menuLabel = "Bookmark",
    ),
    DownloadChapter(
        title = "Download chapter",
        category = ChimahonReaderInputActionCategory.ChapterAction,
        menuLabel = "Download",
    ),
    MarkChapterRead(
        title = "Mark chapter read",
        category = ChimahonReaderInputActionCategory.ChapterAction,
        menuLabel = "Read",
    ),
    OpenChapterUrl(
        title = "Open chapter source",
        category = ChimahonReaderInputActionCategory.ChapterAction,
        menuLabel = "Source",
    ),
    ShareChapter(
        title = "Share chapter",
        category = ChimahonReaderInputActionCategory.ChapterAction,
        menuLabel = "Share",
    ),
}

enum class ChimahonReaderInputSource(val title: String) {
    Keyboard("Keyboard"),
    MouseWheel("Mouse wheel"),
    Trackpad("Trackpad"),
    TapZone("Tap zone"),
    DoubleTap("Double tap"),
    LongPress("Long press"),
    Pointer("Pointer"),
    Toolbar("Toolbar"),
}

enum class ChimahonReaderInputKey(val displayLabel: String) {
    Escape("Esc"),
    H("H"),
    J("J"),
    K("K"),
    L("L"),
    N("N"),
    M("M"),
    S("S"),
    C("C"),
    I("I"),
    F("F"),
    T("T"),
    G("G"),
    P("P"),
    B("B"),
    D("D"),
    R("R"),
    O("O"),
    Z("Z"),
    W("W"),
    Zero("0"),
    One("1"),
    Two("2"),
    Plus("+"),
    Equals("="),
    Minus("-"),
    NumPlus("Num +"),
    NumMinus("Num -"),
    DirectionLeft("Left"),
    DirectionRight("Right"),
    DirectionUp("Up"),
    DirectionDown("Down"),
    PageUp("Page Up"),
    PageDown("Page Down"),
    Spacebar("Space"),
    Enter("Enter"),
    MoveHome("Home"),
    MoveEnd("End"),
    VolumeUp("Volume Up"),
    VolumeDown("Volume Down"),
    Unknown("Unknown"),
}

data class ChimahonReaderInputKeyStroke(
    val key: ChimahonReaderInputKey,
    val primary: Boolean = false,
    val shift: Boolean = false,
    val alt: Boolean = false,
    val displayLabel: String = formatChimahonReaderInputKeyStroke(key, primary, shift, alt),
) {
    val chapterModifier: Boolean
        get() = primary || shift
}

data class ChimahonReaderInputShortcut(
    val action: ChimahonReaderInputActionId,
    val chords: List<ChimahonReaderInputKeyStroke>,
    val enabled: Boolean = true,
) {
    val label: String
        get() = chords.joinToString(" / ") { it.displayLabel }
}

data class ChimahonReaderInputDispatch(
    val action: ChimahonReaderInputActionId? = null,
    val source: ChimahonReaderInputSource = ChimahonReaderInputSource.Keyboard,
    val tapZone: ChimahonReaderTapZoneHit? = null,
    val wheel: ChimahonReaderWheelSnapshot? = null,
    val consumed: Boolean = action != null,
    val unavailableReason: String? = null,
) {
    val dispatched: Boolean
        get() = action != null

    companion object {
        fun ignored(reason: String? = null): ChimahonReaderInputDispatch {
            return ChimahonReaderInputDispatch(consumed = false, unavailableReason = reason)
        }

        fun consumed(reason: String? = null): ChimahonReaderInputDispatch {
            return ChimahonReaderInputDispatch(consumed = true, unavailableReason = reason)
        }
    }
}

data class ChimahonReaderInputMenuItem(
    val id: String,
    val action: ChimahonReaderInputActionId,
    val label: String = action.menuLabel,
    val contentDescription: String = action.contentDescription,
    val shortcutLabel: String? = null,
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val visible: Boolean = true,
)

enum class ChimahonReaderTapZoneAction(val title: String) {
    Previous("Previous"),
    Menu("HUD"),
    Next("Next"),
    MoveLeft("Move left"),
    MoveRight("Move right"),
    Disabled("Disabled"),
}

enum class ChimahonReaderTapZoneId(val title: String) {
    Top("Top"),
    Bottom("Bottom"),
    Left("Left"),
    Right("Right"),
    TopLeft("Top left"),
    TopCenter("Top center"),
    TopRight("Top right"),
    MiddleLeft("Middle left"),
    MiddleCenter("Center"),
    MiddleRight("Middle right"),
    BottomLeft("Bottom left"),
    BottomCenter("Bottom center"),
    BottomRight("Bottom right"),
    Content("Content"),
}

data class ChimahonReaderTapZoneHit(
    val zone: ChimahonReaderTapZoneId,
    val action: ChimahonReaderTapZoneAction,
    val normalizedX: Float,
    val normalizedY: Float,
) {
    val inputAction: ChimahonReaderInputActionId
        get() = when (action) {
            ChimahonReaderTapZoneAction.Previous -> ChimahonReaderInputActionId.PreviousPage
            ChimahonReaderTapZoneAction.Next -> ChimahonReaderInputActionId.NextPage
            ChimahonReaderTapZoneAction.MoveLeft -> ChimahonReaderInputActionId.ScrollUp
            ChimahonReaderTapZoneAction.MoveRight -> ChimahonReaderInputActionId.ScrollDown
            ChimahonReaderTapZoneAction.Menu,
            ChimahonReaderTapZoneAction.Disabled,
            -> ChimahonReaderInputActionId.ToggleControls
        }
}

data class ChimahonReaderTapZoneSideMap(
    val left: ChimahonReaderTapZoneHit,
    val right: ChimahonReaderTapZoneHit,
    val top: ChimahonReaderTapZoneHit,
    val bottom: ChimahonReaderTapZoneHit,
) {
    val leftLabel: String
        get() = left.action.title

    val rightLabel: String
        get() = right.action.title
}

data class ChimahonReaderPageSideActionMap(
    val leftAction: ChimahonReaderInputActionId,
    val rightAction: ChimahonReaderInputActionId,
    val leftEnabled: Boolean,
    val rightEnabled: Boolean,
) {
    val leftLabel: String
        get() = leftAction.contentDescription

    val rightLabel: String
        get() = rightAction.contentDescription
}

data class ChimahonReaderWheelSnapshot(
    val deltaX: Float,
    val deltaY: Float,
    val dominantDelta: Float,
    val accumulatedDelta: Float,
    val threshold: Float,
    val primaryModifier: Boolean = false,
    val shiftModifier: Boolean = false,
    val altModifier: Boolean = false,
    val source: ChimahonReaderInputSource = ChimahonReaderInputSource.MouseWheel,
) {
    val progress: Float
        get() = if (threshold <= 0f) 0f else (abs(accumulatedDelta) / threshold).coerceIn(0f, 1f)
}

enum class ChimahonReaderPointerCursor {
    Default,
    Click,
    Pan,
    ZoomIn,
    ZoomOut,
    Hidden,
}

enum class ChimahonReaderPointerAffordance(val title: String) {
    Idle("Reader"),
    PreviousPage("Previous page"),
    NextPage("Next page"),
    ToggleControls("Toggle controls"),
    Scroll("Scroll"),
    Pan("Pan"),
    ZoomIn("Zoom in"),
    ZoomOut("Zoom out"),
    Busy("Input locked"),
    Hidden("Cursor hidden"),
}

data class ChimahonReaderPointerAffordanceState(
    val affordance: ChimahonReaderPointerAffordance = ChimahonReaderPointerAffordance.Idle,
    val cursor: ChimahonReaderPointerCursor = ChimahonReaderPointerCursor.Default,
    val tapZone: ChimahonReaderTapZoneHit? = null,
    val normalizedX: Float? = null,
    val normalizedY: Float? = null,
    val cursorAutoHideMillis: Int = 0,
    val toolbarVisible: Boolean = true,
    val label: String = affordance.title,
) {
    val cursorVisible: Boolean
        get() = cursor != ChimahonReaderPointerCursor.Hidden
}

data class ChimahonReaderFullscreenToolbarState(
    val fullscreen: Boolean,
    val controlsVisible: Boolean,
    val topBarVisible: Boolean,
    val bottomBarVisible: Boolean,
    val desktopActionStripVisible: Boolean,
    val edgePageButtonsVisible: Boolean,
    val pageIndicatorVisible: Boolean,
    val modeIndicatorVisible: Boolean,
    val shouldAutoHideControls: Boolean,
    val shouldAutoHidePointer: Boolean,
    val autoHideControlsDelayMillis: Int = 3_500,
    val pointerHideDelayMillis: Int = 1_600,
    val toolbarLabel: String = if (controlsVisible) "Reader controls visible" else "Reader controls hidden",
)

data class ChimahonReaderInputUiState(
    val mode: ChimahonReaderMode = ChimahonReaderMode.Webtoon,
    val scale: ChimahonReaderScale = ChimahonReaderScale.FitWidth,
    val navigationMode: ChimahonReaderNavigationMode = ChimahonReaderNavigationMode.Automatic,
    val tapZonesEnabled: Boolean = true,
    val tapNavigationLayout: ChimahonReaderTapNavigationLayout = ChimahonReaderTapNavigationLayout.Edge,
    val smallerTapZones: Boolean = false,
    val invertTapZones: ChimahonTapZoneInvert = ChimahonTapZoneInvert.None,
    val desktopTapNavigation: Boolean = true,
    val tapZonePercent: Int = 20,
    val swipeNavigationEnabled: Boolean = true,
    val keyboardShortcutsEnabled: Boolean = true,
    val keyboardScheme: ChimahonReaderKeyboardScheme = ChimahonReaderKeyboardScheme.AndroidCompatible,
    val mouseWheelAction: ChimahonReaderMouseWheelAction = ChimahonReaderMouseWheelAction.Scroll,
    val mouseWheelSensitivityPercent: Int = 100,
    val invertMouseWheel: Boolean = false,
    val trackpadGesturesEnabled: Boolean = true,
    val volumeKeysEnabled: Boolean = false,
    val volumeKeysInverted: Boolean = false,
    val desktopPageButtons: Boolean = true,
    val desktopReaderMenu: Boolean = true,
    val hideCursorWhileReading: Boolean = true,
    val cursorHideDelayMillis: Int = 1_600,
    val fullscreen: Boolean = true,
    val keepControlsVisible: Boolean = true,
    val showPageNumber: Boolean = true,
    val showProgressTop: Boolean = true,
    val showReadingMode: Boolean = true,
    val showNavigationOverlayOnStart: Boolean = false,
    val navigateToPan: Boolean = false,
    val longTapEnabled: Boolean = true,
    val readWithLongTap: Boolean = true,
    val doubleTapToZoom: Boolean = true,
    val controlsVisible: Boolean = true,
    val settingsVisible: Boolean = false,
    val chaptersVisible: Boolean = false,
    val statsVisible: Boolean = false,
    val inputLocked: Boolean = false,
    val currentPage: Int = 1,
    val pageCount: Int = 1,
    val canPreviousPage: Boolean = currentPage > 1,
    val canNextPage: Boolean = pageCount <= 0 || currentPage < pageCount,
    val hasPreviousChapter: Boolean = false,
    val hasNextChapter: Boolean = false,
    val dualPageMode: ChimahonDualPageMode = ChimahonDualPageMode.Off,
    val cropActive: Boolean = false,
    val ocrActive: Boolean = false,
    val bookmarked: Boolean = false,
    val canBookmarkChapter: Boolean = true,
    val canDownloadChapter: Boolean = true,
    val canMarkChapterRead: Boolean = true,
    val canOpenChapterUrl: Boolean = true,
    val canShareChapter: Boolean = true,
) {
    val hasOpenPanel: Boolean
        get() = settingsVisible || chaptersVisible || statsVisible

    val modePaged: Boolean
        get() = mode.readerInputPaged

    val modeRightToLeft: Boolean
        get() = mode.readerInputRightToLeft

    val effectiveNavigationMode: ChimahonReaderNavigationMode
        get() = navigationMode.effectiveReaderInputNavigationMode(mode)

    val keyboardInputEnabled: Boolean
        get() = keyboardShortcutsEnabled && keyboardScheme != ChimahonReaderKeyboardScheme.Disabled

    val tapNavigationEnabled: Boolean
        get() = tapZonesEnabled &&
            desktopTapNavigation &&
            tapNavigationLayout != ChimahonReaderTapNavigationLayout.Disabled

    val wheelNavigationEnabled: Boolean
        get() = swipeNavigationEnabled && mouseWheelAction != ChimahonReaderMouseWheelAction.Disabled

    val wheelThreshold: Float
        get() = (48f * 100f / mouseWheelSensitivityPercent.coerceIn(25, 400))
            .coerceIn(12f, 160f)

    val imagePanOwnsDrag: Boolean
        get() = modePaged && navigateToPan && scale != ChimahonReaderScale.FitScreen

    val pageLayoutAvailable: Boolean
        get() = modePaged

    val shiftDoublePagesAvailable: Boolean
        get() = modePaged && dualPageMode != ChimahonDualPageMode.Off

    val defaultShortcuts: List<ChimahonReaderInputShortcut>
        get() = defaultReaderInputShortcuts(keyboardScheme)

    val contextMenuItems: List<ChimahonReaderInputMenuItem>
        get() = desktopMenuItems()

    fun resolveKeyboardAction(stroke: ChimahonReaderInputKeyStroke): ChimahonReaderInputDispatch {
        if (!keyboardInputEnabled && stroke.key != ChimahonReaderInputKey.Escape) {
            return ChimahonReaderInputDispatch.ignored("Keyboard shortcuts are disabled.")
        }
        if (inputLocked && stroke.key != ChimahonReaderInputKey.Escape) {
            return ChimahonReaderInputDispatch.consumed("Reader input is locked.")
        }

        if (stroke.primary) {
            resolvePrimaryShortcut(stroke)?.let { action ->
                return dispatchIfAvailable(action, ChimahonReaderInputSource.Keyboard)
            }
        }

        val chapterShortcut = stroke.chapterModifier
        return when (stroke.key) {
            ChimahonReaderInputKey.H -> if (keyboardScheme == ChimahonReaderKeyboardScheme.Vim) {
                when {
                    chapterShortcut && hasPreviousChapter -> dispatch(ChimahonReaderInputActionId.PreviousChapter)
                    chapterShortcut -> ChimahonReaderInputDispatch.ignored()
                    canPreviousPage -> dispatch(ChimahonReaderInputActionId.PreviousPage)
                    else -> ChimahonReaderInputDispatch.ignored()
                }
            } else {
                ChimahonReaderInputDispatch.ignored()
            }
            ChimahonReaderInputKey.J -> if (keyboardScheme == ChimahonReaderKeyboardScheme.Vim && canNextPage) {
                dispatch(ChimahonReaderInputActionId.NextPage)
            } else {
                ChimahonReaderInputDispatch.ignored()
            }
            ChimahonReaderInputKey.K -> if (keyboardScheme == ChimahonReaderKeyboardScheme.Vim && canPreviousPage) {
                dispatch(ChimahonReaderInputActionId.PreviousPage)
            } else {
                ChimahonReaderInputDispatch.ignored()
            }
            ChimahonReaderInputKey.L -> resolveLKey(chapterShortcut)
            ChimahonReaderInputKey.N -> if (keyboardScheme == ChimahonReaderKeyboardScheme.Vim && hasNextChapter) {
                dispatch(ChimahonReaderInputActionId.NextChapter)
            } else {
                ChimahonReaderInputDispatch.ignored()
            }
            ChimahonReaderInputKey.VolumeUp -> resolveVolumeKey(up = true)
            ChimahonReaderInputKey.VolumeDown -> resolveVolumeKey(up = false)
            ChimahonReaderInputKey.DirectionLeft,
            ChimahonReaderInputKey.PageUp,
            ChimahonReaderInputKey.DirectionUp,
            -> resolvePreviousKey(chapterShortcut)
            ChimahonReaderInputKey.DirectionRight,
            ChimahonReaderInputKey.PageDown,
            ChimahonReaderInputKey.DirectionDown,
            -> resolveNextKey(chapterShortcut)
            ChimahonReaderInputKey.Spacebar -> if (stroke.shift) {
                if (canPreviousPage) dispatch(ChimahonReaderInputActionId.PreviousPage) else ChimahonReaderInputDispatch.ignored()
            } else {
                if (canNextPage) dispatch(ChimahonReaderInputActionId.NextPage) else ChimahonReaderInputDispatch.ignored()
            }
            ChimahonReaderInputKey.Enter -> dispatch(ChimahonReaderInputActionId.ToggleControls)
            ChimahonReaderInputKey.M -> dispatchIfReaderChromeFree(ChimahonReaderInputActionId.CycleMode)
            ChimahonReaderInputKey.S -> if (stroke.primary && canShareChapter) {
                dispatch(ChimahonReaderInputActionId.ShareChapter)
            } else if (!chaptersVisible) {
                dispatch(ChimahonReaderInputActionId.OpenSettings)
            } else {
                ChimahonReaderInputDispatch.ignored()
            }
            ChimahonReaderInputKey.C -> if (!settingsVisible && !chaptersVisible) {
                dispatch(ChimahonReaderInputActionId.OpenChapters)
            } else {
                ChimahonReaderInputDispatch.ignored()
            }
            ChimahonReaderInputKey.I -> dispatchIfReaderChromeFree(ChimahonReaderInputActionId.ToggleStats)
            ChimahonReaderInputKey.F -> dispatchIfReaderChromeFree(ChimahonReaderInputActionId.ToggleCrop)
            ChimahonReaderInputKey.T -> dispatchIfReaderChromeFree(ChimahonReaderInputActionId.CycleOrientation)
            ChimahonReaderInputKey.G -> dispatchIfReaderChromeFree(ChimahonReaderInputActionId.ToggleOcrLookup)
            ChimahonReaderInputKey.P -> if (!hasOpenPanel && shiftDoublePagesAvailable) {
                dispatch(ChimahonReaderInputActionId.ShiftDoublePages)
            } else {
                ChimahonReaderInputDispatch.ignored()
            }
            ChimahonReaderInputKey.B -> dispatchOptionalChapterAction(
                ChimahonReaderInputActionId.BookmarkChapter,
                canBookmarkChapter,
            )
            ChimahonReaderInputKey.D -> dispatchOptionalChapterAction(
                ChimahonReaderInputActionId.DownloadChapter,
                canDownloadChapter,
            )
            ChimahonReaderInputKey.R -> dispatchOptionalChapterAction(
                ChimahonReaderInputActionId.MarkChapterRead,
                canMarkChapterRead,
            )
            ChimahonReaderInputKey.O -> dispatchOptionalChapterAction(
                ChimahonReaderInputActionId.OpenChapterUrl,
                canOpenChapterUrl,
            )
            ChimahonReaderInputKey.MoveHome -> if (pageCount > 0 && currentPage > 1) {
                dispatch(ChimahonReaderInputActionId.FirstPage)
            } else {
                ChimahonReaderInputDispatch.ignored()
            }
            ChimahonReaderInputKey.MoveEnd -> if (pageCount > 0 && currentPage < pageCount) {
                dispatch(ChimahonReaderInputActionId.LastPage)
            } else {
                ChimahonReaderInputDispatch.ignored()
            }
            ChimahonReaderInputKey.Escape -> when {
                hasOpenPanel -> dispatch(ChimahonReaderInputActionId.DismissPanels)
                controlsVisible -> dispatch(ChimahonReaderInputActionId.ToggleControls)
                else -> dispatch(ChimahonReaderInputActionId.Back)
            }
            ChimahonReaderInputKey.Z -> if (!hasOpenPanel) {
                dispatch(ChimahonReaderInputActionId.ToggleFitWidthOrScreen)
            } else {
                ChimahonReaderInputDispatch.ignored()
            }
            else -> ChimahonReaderInputDispatch.ignored()
        }
    }

    fun tapZoneAt(
        normalizedX: Float,
        normalizedY: Float,
    ): ChimahonReaderTapZoneHit {
        val x = normalizedX.coerceIn(0f, 1f)
        val y = normalizedY.coerceIn(0f, 1f)
        if (!tapNavigationEnabled) {
            return ChimahonReaderTapZoneHit(
                zone = zoneIdForPoint(x, y),
                action = ChimahonReaderTapZoneAction.Menu,
                normalizedX = x,
                normalizedY = y,
            )
        }

        var hitX = x
        var hitY = y
        if (invertTapZones == ChimahonTapZoneInvert.Horizontal || invertTapZones == ChimahonTapZoneInvert.Both) {
            hitX = 1f - hitX
        }
        if (invertTapZones == ChimahonTapZoneInvert.Vertical || invertTapZones == ChimahonTapZoneInvert.Both) {
            hitY = 1f - hitY
        }
        if (effectiveNavigationMode == ChimahonReaderNavigationMode.RightToLeft) {
            hitX = 1f - hitX
        }

        val action = tapRegions().firstOrNull { it.contains(hitX, hitY) }?.action
            ?: ChimahonReaderTapZoneAction.Menu
        return ChimahonReaderTapZoneHit(
            zone = zoneIdForPoint(x, y),
            action = action,
            normalizedX = x,
            normalizedY = y,
        )
    }

    fun tapZoneSideMap(): ChimahonReaderTapZoneSideMap {
        return ChimahonReaderTapZoneSideMap(
            left = tapZoneAt(0.01f, 0.50f),
            right = tapZoneAt(0.99f, 0.50f),
            top = tapZoneAt(0.50f, 0.01f),
            bottom = tapZoneAt(0.50f, 0.99f),
        )
    }

    fun pageSideActionMap(): ChimahonReaderPageSideActionMap {
        val leftAction = if (effectiveNavigationMode == ChimahonReaderNavigationMode.RightToLeft) {
            ChimahonReaderInputActionId.NextPage
        } else {
            ChimahonReaderInputActionId.PreviousPage
        }
        val rightAction = if (effectiveNavigationMode == ChimahonReaderNavigationMode.RightToLeft) {
            ChimahonReaderInputActionId.PreviousPage
        } else {
            ChimahonReaderInputActionId.NextPage
        }
        return ChimahonReaderPageSideActionMap(
            leftAction = leftAction,
            rightAction = rightAction,
            leftEnabled = if (leftAction == ChimahonReaderInputActionId.NextPage) canNextPage else canPreviousPage,
            rightEnabled = if (rightAction == ChimahonReaderInputActionId.NextPage) canNextPage else canPreviousPage,
        )
    }

    fun resolveTap(
        normalizedX: Float,
        normalizedY: Float,
    ): ChimahonReaderInputDispatch {
        if (inputLocked) {
            return ChimahonReaderInputDispatch.consumed("Reader input is locked.")
        }
        val hit = tapZoneAt(normalizedX, normalizedY)
        return ChimahonReaderInputDispatch(
            action = hit.inputAction,
            source = ChimahonReaderInputSource.TapZone,
            tapZone = hit,
            consumed = true,
        )
    }

    fun pointerAffordanceAt(
        normalizedX: Float,
        normalizedY: Float,
    ): ChimahonReaderPointerAffordanceState {
        val toolbar = fullscreenToolbarState()
        if (inputLocked) {
            return ChimahonReaderPointerAffordanceState(
                affordance = ChimahonReaderPointerAffordance.Busy,
                cursor = ChimahonReaderPointerCursor.Default,
                normalizedX = normalizedX.coerceIn(0f, 1f),
                normalizedY = normalizedY.coerceIn(0f, 1f),
                toolbarVisible = toolbar.topBarVisible || toolbar.bottomBarVisible,
            )
        }
        if (imagePanOwnsDrag) {
            return ChimahonReaderPointerAffordanceState(
                affordance = ChimahonReaderPointerAffordance.Pan,
                cursor = ChimahonReaderPointerCursor.Pan,
                normalizedX = normalizedX.coerceIn(0f, 1f),
                normalizedY = normalizedY.coerceIn(0f, 1f),
                toolbarVisible = toolbar.topBarVisible || toolbar.bottomBarVisible,
            )
        }

        val hit = tapZoneAt(normalizedX, normalizedY)
        val affordance = when (hit.action) {
            ChimahonReaderTapZoneAction.Previous -> ChimahonReaderPointerAffordance.PreviousPage
            ChimahonReaderTapZoneAction.Next -> ChimahonReaderPointerAffordance.NextPage
            ChimahonReaderTapZoneAction.MoveLeft,
            ChimahonReaderTapZoneAction.MoveRight,
            -> ChimahonReaderPointerAffordance.Scroll
            ChimahonReaderTapZoneAction.Menu,
            ChimahonReaderTapZoneAction.Disabled,
            -> ChimahonReaderPointerAffordance.ToggleControls
        }
        val cursor = if (toolbar.shouldAutoHidePointer) {
            ChimahonReaderPointerCursor.Hidden
        } else {
            ChimahonReaderPointerCursor.Click
        }
        return ChimahonReaderPointerAffordanceState(
            affordance = affordance,
            cursor = cursor,
            tapZone = hit,
            normalizedX = hit.normalizedX,
            normalizedY = hit.normalizedY,
            cursorAutoHideMillis = if (toolbar.shouldAutoHidePointer) cursorHideDelayMillis.coerceAtLeast(0) else 0,
            toolbarVisible = toolbar.topBarVisible || toolbar.bottomBarVisible,
            label = hit.action.title,
        )
    }

    fun fullscreenToolbarState(
        viewportWidthDp: Float = 0f,
    ): ChimahonReaderFullscreenToolbarState {
        val widthKnown = viewportWidthDp > 0f
        val desktopStripVisible = controlsVisible &&
            desktopReaderMenu &&
            (!widthKnown || viewportWidthDp >= 720f)
        return ChimahonReaderFullscreenToolbarState(
            fullscreen = fullscreen,
            controlsVisible = controlsVisible,
            topBarVisible = controlsVisible,
            bottomBarVisible = controlsVisible,
            desktopActionStripVisible = desktopStripVisible,
            edgePageButtonsVisible = !controlsVisible &&
                desktopPageButtons &&
                pageCount > 0 &&
                (!widthKnown || viewportWidthDp >= 840f),
            pageIndicatorVisible = !controlsVisible && pageCount > 0 && showPageNumber,
            modeIndicatorVisible = !controlsVisible && showReadingMode,
            shouldAutoHideControls = controlsVisible && !keepControlsVisible && !hasOpenPanel,
            shouldAutoHidePointer = fullscreen &&
                hideCursorWhileReading &&
                !controlsVisible &&
                !hasOpenPanel,
            pointerHideDelayMillis = cursorHideDelayMillis.coerceAtLeast(0),
        )
    }

    fun desktopMenuItems(): List<ChimahonReaderInputMenuItem> {
        val shortcuts = defaultShortcuts.associateBy { it.action }
        return listOf(
            menuItem(ChimahonReaderInputActionId.CycleMode, shortcuts, selected = mode != ChimahonReaderMode.Webtoon),
            menuItem(ChimahonReaderInputActionId.OpenSettings, shortcuts, selected = settingsVisible),
            menuItem(ChimahonReaderInputActionId.OpenChapters, shortcuts, selected = chaptersVisible),
            menuItem(ChimahonReaderInputActionId.ToggleStats, shortcuts, selected = statsVisible),
            menuItem(ChimahonReaderInputActionId.ToggleCrop, shortcuts, selected = cropActive),
            menuItem(ChimahonReaderInputActionId.ToggleOcrLookup, shortcuts, selected = ocrActive),
            menuItem(ChimahonReaderInputActionId.FitScreen, shortcuts, selected = scale == ChimahonReaderScale.FitScreen),
            menuItem(ChimahonReaderInputActionId.FitWidth, shortcuts, selected = scale == ChimahonReaderScale.FitWidth),
            menuItem(ChimahonReaderInputActionId.FitHeight, shortcuts, selected = scale == ChimahonReaderScale.FitHeight),
            menuItem(ChimahonReaderInputActionId.BookmarkChapter, shortcuts, enabled = canBookmarkChapter, selected = bookmarked),
            menuItem(ChimahonReaderInputActionId.DownloadChapter, shortcuts, enabled = canDownloadChapter),
            menuItem(ChimahonReaderInputActionId.MarkChapterRead, shortcuts, enabled = canMarkChapterRead),
            menuItem(ChimahonReaderInputActionId.OpenChapterUrl, shortcuts, enabled = canOpenChapterUrl),
            menuItem(ChimahonReaderInputActionId.ShareChapter, shortcuts, enabled = canShareChapter),
        )
    }

    private fun resolvePrimaryShortcut(
        stroke: ChimahonReaderInputKeyStroke,
    ): ChimahonReaderInputActionId? {
        return when {
            stroke.alt && stroke.key == ChimahonReaderInputKey.Zero -> ChimahonReaderInputActionId.FitScreen
            stroke.alt && stroke.key == ChimahonReaderInputKey.One -> ChimahonReaderInputActionId.FitWidth
            stroke.alt && (stroke.key == ChimahonReaderInputKey.Two || stroke.key == ChimahonReaderInputKey.H) ->
                ChimahonReaderInputActionId.FitHeight
            stroke.key == ChimahonReaderInputKey.Zero -> ChimahonReaderInputActionId.ResetZoom
            stroke.key == ChimahonReaderInputKey.Plus ||
                stroke.key == ChimahonReaderInputKey.Equals ||
                stroke.key == ChimahonReaderInputKey.NumPlus -> ChimahonReaderInputActionId.ZoomIn
            stroke.key == ChimahonReaderInputKey.Minus ||
                stroke.key == ChimahonReaderInputKey.NumMinus -> ChimahonReaderInputActionId.ZoomOut
            stroke.alt && stroke.key == ChimahonReaderInputKey.W -> ChimahonReaderInputActionId.FitWidth
            stroke.alt && stroke.key == ChimahonReaderInputKey.M -> ChimahonReaderInputActionId.CycleMode
            stroke.alt && stroke.key == ChimahonReaderInputKey.C -> ChimahonReaderInputActionId.OpenChapters
            stroke.alt && stroke.key == ChimahonReaderInputKey.G -> ChimahonReaderInputActionId.ToggleOcrLookup
            stroke.alt && stroke.key == ChimahonReaderInputKey.F -> ChimahonReaderInputActionId.ToggleCrop
            stroke.alt && stroke.key == ChimahonReaderInputKey.I -> ChimahonReaderInputActionId.ToggleStats
            stroke.alt && stroke.key == ChimahonReaderInputKey.O -> ChimahonReaderInputActionId.OpenChapterUrl
            stroke.alt && stroke.shift && stroke.key == ChimahonReaderInputKey.S -> ChimahonReaderInputActionId.ShareChapter
            else -> null
        }
    }

    private fun resolveLKey(chapterShortcut: Boolean): ChimahonReaderInputDispatch {
        return if (keyboardScheme == ChimahonReaderKeyboardScheme.Vim) {
            when {
                chapterShortcut && hasNextChapter -> dispatch(ChimahonReaderInputActionId.NextChapter)
                chapterShortcut -> ChimahonReaderInputDispatch.ignored()
                canNextPage -> dispatch(ChimahonReaderInputActionId.NextPage)
                else -> ChimahonReaderInputDispatch.ignored()
            }
        } else if (!hasOpenPanel && pageLayoutAvailable) {
            dispatch(ChimahonReaderInputActionId.CyclePageLayout)
        } else {
            ChimahonReaderInputDispatch.ignored()
        }
    }

    private fun resolveVolumeKey(up: Boolean): ChimahonReaderInputDispatch {
        if (!volumeKeysEnabled) {
            return ChimahonReaderInputDispatch.ignored("Volume key navigation is disabled.")
        }
        val previous = if (volumeKeysInverted) !up else up
        return if (previous) {
            if (canPreviousPage) dispatch(ChimahonReaderInputActionId.PreviousPage) else ChimahonReaderInputDispatch.ignored()
        } else {
            if (canNextPage) dispatch(ChimahonReaderInputActionId.NextPage) else ChimahonReaderInputDispatch.ignored()
        }
    }

    private fun resolvePreviousKey(chapterShortcut: Boolean): ChimahonReaderInputDispatch {
        return when {
            chapterShortcut && hasPreviousChapter -> dispatch(ChimahonReaderInputActionId.PreviousChapter)
            chapterShortcut -> ChimahonReaderInputDispatch.ignored()
            canPreviousPage -> dispatch(ChimahonReaderInputActionId.PreviousPage)
            else -> ChimahonReaderInputDispatch.ignored()
        }
    }

    private fun resolveNextKey(chapterShortcut: Boolean): ChimahonReaderInputDispatch {
        return when {
            chapterShortcut && hasNextChapter -> dispatch(ChimahonReaderInputActionId.NextChapter)
            chapterShortcut -> ChimahonReaderInputDispatch.ignored()
            canNextPage -> dispatch(ChimahonReaderInputActionId.NextPage)
            else -> ChimahonReaderInputDispatch.ignored()
        }
    }

    private fun dispatchIfReaderChromeFree(action: ChimahonReaderInputActionId): ChimahonReaderInputDispatch {
        return if (!hasOpenPanel) dispatch(action) else ChimahonReaderInputDispatch.ignored()
    }

    private fun dispatchOptionalChapterAction(
        action: ChimahonReaderInputActionId,
        enabled: Boolean,
    ): ChimahonReaderInputDispatch {
        return if (!hasOpenPanel && enabled) dispatch(action) else ChimahonReaderInputDispatch.ignored()
    }

    private fun dispatchIfAvailable(
        action: ChimahonReaderInputActionId,
        source: ChimahonReaderInputSource,
    ): ChimahonReaderInputDispatch {
        return when (action) {
            ChimahonReaderInputActionId.PreviousPage -> if (canPreviousPage) dispatch(action, source) else ChimahonReaderInputDispatch.ignored()
            ChimahonReaderInputActionId.NextPage -> if (canNextPage) dispatch(action, source) else ChimahonReaderInputDispatch.ignored()
            ChimahonReaderInputActionId.PreviousChapter -> if (hasPreviousChapter) dispatch(action, source) else ChimahonReaderInputDispatch.ignored()
            ChimahonReaderInputActionId.NextChapter -> if (hasNextChapter) dispatch(action, source) else ChimahonReaderInputDispatch.ignored()
            ChimahonReaderInputActionId.CyclePageLayout -> if (pageLayoutAvailable) dispatch(action, source) else ChimahonReaderInputDispatch.ignored()
            ChimahonReaderInputActionId.ShiftDoublePages -> if (shiftDoublePagesAvailable) dispatch(action, source) else ChimahonReaderInputDispatch.ignored()
            ChimahonReaderInputActionId.BookmarkChapter -> if (canBookmarkChapter) dispatch(action, source) else ChimahonReaderInputDispatch.ignored()
            ChimahonReaderInputActionId.DownloadChapter -> if (canDownloadChapter) dispatch(action, source) else ChimahonReaderInputDispatch.ignored()
            ChimahonReaderInputActionId.MarkChapterRead -> if (canMarkChapterRead) dispatch(action, source) else ChimahonReaderInputDispatch.ignored()
            ChimahonReaderInputActionId.OpenChapterUrl -> if (canOpenChapterUrl) dispatch(action, source) else ChimahonReaderInputDispatch.ignored()
            ChimahonReaderInputActionId.ShareChapter -> if (canShareChapter) dispatch(action, source) else ChimahonReaderInputDispatch.ignored()
            else -> dispatch(action, source)
        }
    }

    private fun dispatch(
        action: ChimahonReaderInputActionId,
        source: ChimahonReaderInputSource = ChimahonReaderInputSource.Keyboard,
    ): ChimahonReaderInputDispatch {
        return ChimahonReaderInputDispatch(action = action, source = source, consumed = true)
    }

    private fun tapRegions(): List<ReaderInputTapRegion> {
        val edge = tapZoneFraction()
        val farEdge = 1f - edge
        return when {
            effectiveNavigationMode == ChimahonReaderNavigationMode.Vertical -> listOf(
                ReaderInputTapRegion(
                    rect = ReaderInputRect(0f, 0f, 1f, edge),
                    action = ChimahonReaderTapZoneAction.Previous,
                ),
                ReaderInputTapRegion(
                    rect = ReaderInputRect(0f, farEdge, 1f, 1f),
                    action = ChimahonReaderTapZoneAction.Next,
                ),
            )
            tapNavigationLayout == ChimahonReaderTapNavigationLayout.Edge -> listOf(
                ReaderInputTapRegion(
                    rect = ReaderInputRect(0f, 0f, edge, 1f),
                    action = ChimahonReaderTapZoneAction.Next,
                ),
                ReaderInputTapRegion(
                    rect = ReaderInputRect(edge, farEdge, farEdge, 1f),
                    action = ChimahonReaderTapZoneAction.Previous,
                ),
                ReaderInputTapRegion(
                    rect = ReaderInputRect(farEdge, 0f, 1f, 1f),
                    action = ChimahonReaderTapZoneAction.Next,
                ),
            )
            tapNavigationLayout == ChimahonReaderTapNavigationLayout.Kindle -> listOf(
                ReaderInputTapRegion(
                    rect = ReaderInputRect(edge, edge, 1f, 1f),
                    action = ChimahonReaderTapZoneAction.Next,
                ),
                ReaderInputTapRegion(
                    rect = ReaderInputRect(0f, edge, edge, 1f),
                    action = ChimahonReaderTapZoneAction.Previous,
                ),
            )
            else -> listOf(
                ReaderInputTapRegion(
                    rect = ReaderInputRect(0f, edge, edge, farEdge),
                    action = ChimahonReaderTapZoneAction.Previous,
                ),
                ReaderInputTapRegion(
                    rect = ReaderInputRect(0f, 0f, 1f, edge),
                    action = ChimahonReaderTapZoneAction.Previous,
                ),
                ReaderInputTapRegion(
                    rect = ReaderInputRect(farEdge, edge, 1f, farEdge),
                    action = ChimahonReaderTapZoneAction.Next,
                ),
                ReaderInputTapRegion(
                    rect = ReaderInputRect(0f, farEdge, 1f, 1f),
                    action = ChimahonReaderTapZoneAction.Next,
                ),
            )
        }
    }

    private fun tapZoneFraction(): Float {
        val configured = tapZonePercent.coerceIn(0, 45) / 100f
        return if (smallerTapZones) min(configured, 0.25f) else configured
    }

    private fun menuItem(
        action: ChimahonReaderInputActionId,
        shortcuts: Map<ChimahonReaderInputActionId, ChimahonReaderInputShortcut>,
        enabled: Boolean = true,
        selected: Boolean = false,
    ): ChimahonReaderInputMenuItem {
        return ChimahonReaderInputMenuItem(
            id = "reader.${action.name}",
            action = action,
            shortcutLabel = shortcuts[action]?.label,
            enabled = enabled,
            selected = selected,
        )
    }
}

class ChimahonReaderWheelAccumulator(
    private var accumulatedDelta: Float = 0f,
) {
    fun reset() {
        accumulatedDelta = 0f
    }

    fun update(
        deltaX: Float,
        deltaY: Float,
        state: ChimahonReaderInputUiState,
        primaryModifier: Boolean = false,
        shiftModifier: Boolean = false,
        altModifier: Boolean = false,
        source: ChimahonReaderInputSource = ChimahonReaderInputSource.MouseWheel,
    ): ChimahonReaderInputDispatch {
        if (state.inputLocked) {
            return ChimahonReaderInputDispatch.consumed("Reader input is locked.")
        }
        if (!state.wheelNavigationEnabled) {
            reset()
            return ChimahonReaderInputDispatch.ignored("Wheel navigation is disabled.")
        }
        if (source == ChimahonReaderInputSource.Trackpad && !state.trackpadGesturesEnabled) {
            reset()
            return ChimahonReaderInputDispatch.ignored("Trackpad gestures are disabled.")
        }
        if (
            state.modePaged &&
            !primaryModifier &&
            state.mouseWheelAction != ChimahonReaderMouseWheelAction.Zoom &&
            state.imagePanOwnsDrag
        ) {
            reset()
            return ChimahonReaderInputDispatch.ignored("Pan owns wheel navigation while zoomed.")
        }
        if (state.hasOpenPanel) {
            reset()
            return ChimahonReaderInputDispatch.ignored("Reader panels are open.")
        }

        val dominantDelta = if (abs(deltaX) > abs(deltaY)) deltaX else deltaY
        if (abs(dominantDelta) < 0.5f) {
            return ChimahonReaderInputDispatch.ignored()
        }
        val adjustedDelta = if (state.invertMouseWheel) -dominantDelta else dominantDelta
        accumulatedDelta += adjustedDelta
        val snapshot = ChimahonReaderWheelSnapshot(
            deltaX = deltaX,
            deltaY = deltaY,
            dominantDelta = adjustedDelta,
            accumulatedDelta = accumulatedDelta,
            threshold = state.wheelThreshold,
            primaryModifier = primaryModifier,
            shiftModifier = shiftModifier,
            altModifier = altModifier,
            source = source,
        )
        if (abs(accumulatedDelta) < state.wheelThreshold) {
            return ChimahonReaderInputDispatch(consumed = true, wheel = snapshot)
        }

        val action = resolveWheelAction(state, primaryModifier, shiftModifier, altModifier, accumulatedDelta)
        accumulatedDelta = 0f
        return if (action == null) {
            ChimahonReaderInputDispatch(consumed = false, wheel = snapshot)
        } else {
            ChimahonReaderInputDispatch(
                action = action,
                source = source,
                wheel = snapshot.copy(accumulatedDelta = 0f),
                consumed = true,
            )
        }
    }

    private fun resolveWheelAction(
        state: ChimahonReaderInputUiState,
        primaryModifier: Boolean,
        shiftModifier: Boolean,
        altModifier: Boolean,
        delta: Float,
    ): ChimahonReaderInputActionId? {
        if (shiftModifier) {
            return when {
                delta < 0f && state.hasPreviousChapter -> ChimahonReaderInputActionId.PreviousChapter
                delta > 0f && state.hasNextChapter -> ChimahonReaderInputActionId.NextChapter
                else -> null
            }
        }
        if (primaryModifier || state.mouseWheelAction == ChimahonReaderMouseWheelAction.Zoom) {
            return if (delta < 0f) ChimahonReaderInputActionId.ZoomIn else ChimahonReaderInputActionId.ZoomOut
        }
        if (altModifier || !state.modePaged) {
            return if (delta < 0f) ChimahonReaderInputActionId.ScrollUp else ChimahonReaderInputActionId.ScrollDown
        }
        return when {
            delta > 0f && state.canNextPage -> ChimahonReaderInputActionId.NextPage
            delta < 0f && state.canPreviousPage -> ChimahonReaderInputActionId.PreviousPage
            else -> null
        }
    }
}

fun ChimahonReaderSettings.toReaderInputUiState(
    mode: ChimahonReaderMode = this.mode,
    scale: ChimahonReaderScale = this.scale,
    controlsVisible: Boolean = true,
    settingsVisible: Boolean = false,
    chaptersVisible: Boolean = false,
    statsVisible: Boolean = false,
    inputLocked: Boolean = false,
    currentPage: Int = 1,
    pageCount: Int = 1,
    canPreviousPage: Boolean = currentPage > 1,
    canNextPage: Boolean = pageCount <= 0 || currentPage < pageCount,
    hasPreviousChapter: Boolean = false,
    hasNextChapter: Boolean = false,
    bookmarked: Boolean = false,
    canBookmarkChapter: Boolean = true,
    canDownloadChapter: Boolean = true,
    canMarkChapterRead: Boolean = true,
    canOpenChapterUrl: Boolean = true,
    canShareChapter: Boolean = true,
): ChimahonReaderInputUiState {
    return ChimahonReaderInputUiState(
        mode = mode,
        scale = scale,
        navigationMode = navigationMode,
        tapZonesEnabled = tapZonesEnabled,
        tapNavigationLayout = tapNavigationLayout,
        smallerTapZones = smallerTapZones,
        invertTapZones = invertTapZones,
        desktopTapNavigation = desktopTapNavigation,
        tapZonePercent = tapZonePercent,
        swipeNavigationEnabled = swipeNavigationEnabled,
        keyboardShortcutsEnabled = keyboardShortcutsEnabled,
        keyboardScheme = keyboardScheme,
        mouseWheelAction = mouseWheelAction,
        mouseWheelSensitivityPercent = mouseWheelSensitivityPercent,
        invertMouseWheel = invertMouseWheel,
        trackpadGesturesEnabled = trackpadGesturesEnabled,
        volumeKeysEnabled = volumeKeysEnabled,
        volumeKeysInverted = volumeKeysInverted,
        desktopPageButtons = desktopPageButtons,
        desktopReaderMenu = desktopReaderMenu,
        hideCursorWhileReading = hideCursorWhileReading,
        cursorHideDelayMillis = cursorHideDelayMillis,
        fullscreen = fullscreen,
        keepControlsVisible = keepControlsVisible,
        showPageNumber = showPageNumber,
        showProgressTop = showProgressTop,
        showReadingMode = showReadingMode,
        showNavigationOverlayOnStart = showNavigationOverlayOnStart,
        navigateToPan = navigateToPan,
        longTapEnabled = longTapEnabled,
        readWithLongTap = readWithLongTap,
        doubleTapToZoom = doubleTapToZoom,
        controlsVisible = controlsVisible,
        settingsVisible = settingsVisible,
        chaptersVisible = chaptersVisible,
        statsVisible = statsVisible,
        inputLocked = inputLocked,
        currentPage = currentPage,
        pageCount = pageCount,
        canPreviousPage = canPreviousPage,
        canNextPage = canNextPage,
        hasPreviousChapter = hasPreviousChapter,
        hasNextChapter = hasNextChapter,
        dualPageMode = dualPageMode,
        cropActive = cropBorders,
        bookmarked = bookmarked,
        canBookmarkChapter = canBookmarkChapter,
        canDownloadChapter = canDownloadChapter,
        canMarkChapterRead = canMarkChapterRead,
        canOpenChapterUrl = canOpenChapterUrl,
        canShareChapter = canShareChapter,
    )
}

val ChimahonReaderMode.readerInputPaged: Boolean
    get() = this == ChimahonReaderMode.LeftToRight || this == ChimahonReaderMode.RightToLeft

val ChimahonReaderMode.readerInputRightToLeft: Boolean
    get() = this == ChimahonReaderMode.RightToLeft

fun ChimahonReaderNavigationMode.effectiveReaderInputNavigationMode(
    mode: ChimahonReaderMode,
): ChimahonReaderNavigationMode {
    return when (this) {
        ChimahonReaderNavigationMode.Automatic -> when {
            mode.readerInputRightToLeft -> ChimahonReaderNavigationMode.RightToLeft
            mode == ChimahonReaderMode.Webtoon || mode == ChimahonReaderMode.Vertical ->
                ChimahonReaderNavigationMode.Vertical
            else -> ChimahonReaderNavigationMode.LeftToRight
        }
        else -> this
    }
}

fun defaultReaderInputShortcuts(
    scheme: ChimahonReaderKeyboardScheme = ChimahonReaderKeyboardScheme.Desktop,
): List<ChimahonReaderInputShortcut> {
    if (scheme == ChimahonReaderKeyboardScheme.Disabled) return emptyList()
    val vim = scheme == ChimahonReaderKeyboardScheme.Vim
    return buildList {
        add(
            ChimahonReaderInputShortcut(
                action = ChimahonReaderInputActionId.PreviousPage,
                chords = buildList {
                    add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.DirectionLeft))
                    add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.PageUp))
                    add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.DirectionUp))
                    add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.Spacebar, shift = true))
                    if (vim) {
                        add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.H))
                        add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.K))
                    }
                },
            ),
        )
        add(
            ChimahonReaderInputShortcut(
                action = ChimahonReaderInputActionId.NextPage,
                chords = buildList {
                    add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.DirectionRight))
                    add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.PageDown))
                    add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.DirectionDown))
                    add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.Spacebar))
                    if (vim) {
                        add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.J))
                        add(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.L))
                    }
                },
            ),
        )
        add(
            ChimahonReaderInputShortcut(
                action = ChimahonReaderInputActionId.PreviousChapter,
                chords = listOf(
                    ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.DirectionLeft, shift = true),
                    ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.PageUp, shift = true),
                ),
            ),
        )
        add(
            ChimahonReaderInputShortcut(
                action = ChimahonReaderInputActionId.NextChapter,
                chords = listOf(
                    ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.DirectionRight, shift = true),
                    ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.PageDown, shift = true),
                ),
            ),
        )
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.ToggleControls, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.Enter))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.CycleMode, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.M))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.OpenSettings, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.S))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.OpenChapters, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.C))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.ToggleStats, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.I))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.ToggleCrop, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.F))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.ToggleOcrLookup, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.G))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.CycleOrientation, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.T))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.CyclePageLayout, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.L))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.ShiftDoublePages, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.P))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.ZoomIn, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.Plus, primary = true))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.ZoomOut, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.Minus, primary = true))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.ResetZoom, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.Zero, primary = true))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.FitScreen, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.Zero, primary = true, alt = true))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.FitWidth, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.One, primary = true, alt = true))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.FitHeight, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.Two, primary = true, alt = true))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.BookmarkChapter, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.B))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.DownloadChapter, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.D))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.MarkChapterRead, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.R))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.OpenChapterUrl, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.O))))
        add(ChimahonReaderInputShortcut(ChimahonReaderInputActionId.ShareChapter, listOf(ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.S, primary = true, alt = true, shift = true))))
    }
}

fun formatChimahonReaderInputKeyStroke(
    key: ChimahonReaderInputKey,
    primary: Boolean = false,
    shift: Boolean = false,
    alt: Boolean = false,
): String {
    return buildList {
        if (primary) add("Ctrl/Cmd")
        if (alt) add("Alt")
        if (shift) add("Shift")
        add(key.displayLabel)
    }.joinToString("+")
}

private data class ReaderInputRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    fun contains(x: Float, y: Float): Boolean {
        return x >= left && x <= right && y >= top && y <= bottom
    }
}

private data class ReaderInputTapRegion(
    val rect: ReaderInputRect,
    val action: ChimahonReaderTapZoneAction,
) {
    fun contains(x: Float, y: Float): Boolean {
        return rect.contains(x, y)
    }
}

private fun zoneIdForPoint(x: Float, y: Float): ChimahonReaderTapZoneId {
    val column = when {
        x < 1f / 3f -> 0
        x > 2f / 3f -> 2
        else -> 1
    }
    val row = when {
        y < 1f / 3f -> 0
        y > 2f / 3f -> 2
        else -> 1
    }
    return when (row to column) {
        0 to 0 -> ChimahonReaderTapZoneId.TopLeft
        0 to 1 -> ChimahonReaderTapZoneId.TopCenter
        0 to 2 -> ChimahonReaderTapZoneId.TopRight
        1 to 0 -> ChimahonReaderTapZoneId.MiddleLeft
        1 to 1 -> ChimahonReaderTapZoneId.MiddleCenter
        1 to 2 -> ChimahonReaderTapZoneId.MiddleRight
        2 to 0 -> ChimahonReaderTapZoneId.BottomLeft
        2 to 1 -> ChimahonReaderTapZoneId.BottomCenter
        else -> ChimahonReaderTapZoneId.BottomRight
    }
}
