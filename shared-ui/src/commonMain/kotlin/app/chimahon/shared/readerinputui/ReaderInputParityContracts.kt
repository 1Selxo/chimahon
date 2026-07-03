package app.chimahon.shared.readerinputui

import app.chimahon.shared.ChimahonReaderMouseWheelAction

data class ChimahonReaderHardwareKeyEvent(
    val key: ChimahonReaderInputKey,
    val primaryModifier: Boolean = false,
    val shiftModifier: Boolean = false,
    val altModifier: Boolean = false,
    val repeated: Boolean = false,
    val source: ChimahonReaderInputSource = ChimahonReaderInputSource.Keyboard,
    val reservedBySystem: Boolean = false,
) {
    val stroke: ChimahonReaderInputKeyStroke
        get() = ChimahonReaderInputKeyStroke(
            key = key,
            primary = primaryModifier,
            shift = shiftModifier,
            alt = altModifier,
        )
}

enum class ChimahonReaderTrackpadScrollPhase {
    Began,
    Changed,
    Momentum,
    Ended,
    Cancelled,
}

data class ChimahonReaderTrackpadScrollEvent(
    val deltaX: Float,
    val deltaY: Float,
    val primaryModifier: Boolean = false,
    val shiftModifier: Boolean = false,
    val altModifier: Boolean = false,
    val phase: ChimahonReaderTrackpadScrollPhase = ChimahonReaderTrackpadScrollPhase.Changed,
    val fingerCount: Int = 2,
    val requiresTwoFingerScroll: Boolean = true,
) {
    val active: Boolean
        get() = phase != ChimahonReaderTrackpadScrollPhase.Ended &&
            phase != ChimahonReaderTrackpadScrollPhase.Cancelled
}

enum class ChimahonReaderInputActionHintKind {
    KeyboardShortcut,
    TrackpadGesture,
    ReaderChrome,
}

data class ChimahonReaderInputActionHint(
    val action: ChimahonReaderInputActionId,
    val title: String = action.title,
    val inputLabel: String,
    val kind: ChimahonReaderInputActionHintKind,
    val source: ChimahonReaderInputSource,
    val shortcut: ChimahonReaderInputKeyStroke? = null,
    val enabled: Boolean = true,
    val detail: String? = null,
) {
    val category: ChimahonReaderInputActionCategory
        get() = action.category
}

fun ChimahonReaderInputUiState.resolveHardwareKeyboardEvent(
    event: ChimahonReaderHardwareKeyEvent,
): ChimahonReaderInputDispatch {
    if (event.reservedBySystem) {
        return ChimahonReaderInputDispatch.ignored("Key command is reserved by the system.")
    }

    val dispatch = resolveKeyboardAction(event.stroke)
    if (
        event.repeated &&
        dispatch.action != null &&
        dispatch.action !in repeatableReaderHardwareKeyboardActions
    ) {
        return ChimahonReaderInputDispatch.ignored("Repeated key command is ignored for this action.")
    }

    return if (dispatch.source == event.source) {
        dispatch
    } else {
        dispatch.copy(source = event.source)
    }
}

fun ChimahonReaderWheelAccumulator.updateTrackpadScroll(
    event: ChimahonReaderTrackpadScrollEvent,
    state: ChimahonReaderInputUiState,
): ChimahonReaderInputDispatch {
    if (!event.active) {
        reset()
        return ChimahonReaderInputDispatch.ignored()
    }
    if (event.requiresTwoFingerScroll && event.fingerCount != 2) {
        reset()
        return ChimahonReaderInputDispatch.ignored("Only two-finger trackpad scrolling is handled.")
    }

    return update(
        deltaX = event.deltaX,
        deltaY = event.deltaY,
        state = state,
        primaryModifier = event.primaryModifier,
        shiftModifier = event.shiftModifier,
        altModifier = event.altModifier,
        source = ChimahonReaderInputSource.Trackpad,
    )
}

fun ChimahonReaderInputUiState.readerInputActionHints(
    includeKeyboard: Boolean = true,
    includeTrackpad: Boolean = true,
    includeChrome: Boolean = true,
    includeChapterActions: Boolean = true,
): List<ChimahonReaderInputActionHint> {
    return buildList {
        if (includeKeyboard) {
            addAll(readerKeyboardActionHints())
        }
        if (includeTrackpad) {
            addAll(readerTrackpadActionHints())
        }
        if (includeChrome) {
            addAll(readerChromeActionHints(includeChapterActions))
        }
    }.distinctBy { hint ->
        Triple(hint.action, hint.inputLabel, hint.kind)
    }
}

fun ChimahonReaderInputUiState.readerKeyboardActionHints(): List<ChimahonReaderInputActionHint> {
    val state = this
    return buildList {
        add(
            keyboardHint(
                action = escapeKeyAction(),
                stroke = ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.Escape),
            ),
        )
        if (!keyboardInputEnabled) return@buildList
        defaultShortcuts.forEach { shortcut ->
            shortcut.chords.forEach { stroke ->
                add(
                    keyboardHint(
                        action = shortcut.action,
                        stroke = stroke,
                        enabled = shortcut.enabled && shortcut.action.readerInputActionAvailable(state),
                    ),
                )
            }
        }
        add(
            keyboardHint(
                ChimahonReaderInputActionId.FirstPage,
                ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.MoveHome),
            ),
        )
        add(
            keyboardHint(
                ChimahonReaderInputActionId.LastPage,
                ChimahonReaderInputKeyStroke(ChimahonReaderInputKey.MoveEnd),
            ),
        )
    }
}

fun ChimahonReaderInputUiState.readerTrackpadActionHints(): List<ChimahonReaderInputActionHint> {
    if (!trackpadGesturesEnabled || !wheelNavigationEnabled) return emptyList()

    val directScrollHints = if (mouseWheelActionZoomsByDefault()) {
        listOf(
            trackpadHint(ChimahonReaderInputActionId.ZoomIn, "Two-finger scroll up"),
            trackpadHint(ChimahonReaderInputActionId.ZoomOut, "Two-finger scroll down"),
        )
    } else if (!modePaged) {
        listOf(
            trackpadHint(ChimahonReaderInputActionId.ScrollUp, "Two-finger scroll up"),
            trackpadHint(ChimahonReaderInputActionId.ScrollDown, "Two-finger scroll down"),
        )
    } else {
        listOf(
            trackpadHint(ChimahonReaderInputActionId.PreviousPage, "Two-finger scroll up"),
            trackpadHint(ChimahonReaderInputActionId.NextPage, "Two-finger scroll down"),
        )
    }

    return buildList {
        addAll(directScrollHints)
        add(trackpadHint(ChimahonReaderInputActionId.ZoomIn, "Ctrl/Cmd + two-finger scroll up"))
        add(trackpadHint(ChimahonReaderInputActionId.ZoomOut, "Ctrl/Cmd + two-finger scroll down"))
        add(trackpadHint(ChimahonReaderInputActionId.PreviousChapter, "Shift + two-finger scroll up"))
        add(trackpadHint(ChimahonReaderInputActionId.NextChapter, "Shift + two-finger scroll down"))
    }
}

fun ChimahonReaderInputUiState.readerChromeActionHints(
    includeChapterActions: Boolean = true,
): List<ChimahonReaderInputActionHint> {
    val menuItems = desktopMenuItems().filter { item ->
        includeChapterActions || item.action.category != ChimahonReaderInputActionCategory.ChapterAction
    }
    return menuItems.map { item ->
        ChimahonReaderInputActionHint(
            action = item.action,
            title = item.label,
            inputLabel = item.shortcutLabel ?: item.label,
            kind = ChimahonReaderInputActionHintKind.ReaderChrome,
            source = ChimahonReaderInputSource.Toolbar,
            enabled = item.enabled && item.visible,
        )
    }
}

private val repeatableReaderHardwareKeyboardActions = setOf(
    ChimahonReaderInputActionId.PreviousPage,
    ChimahonReaderInputActionId.NextPage,
    ChimahonReaderInputActionId.ScrollUp,
    ChimahonReaderInputActionId.ScrollDown,
    ChimahonReaderInputActionId.ZoomIn,
    ChimahonReaderInputActionId.ZoomOut,
)

private fun ChimahonReaderInputUiState.mouseWheelActionZoomsByDefault(): Boolean {
    return mouseWheelAction == ChimahonReaderMouseWheelAction.Zoom
}

private fun ChimahonReaderInputUiState.trackpadHint(
    action: ChimahonReaderInputActionId,
    inputLabel: String,
): ChimahonReaderInputActionHint {
    return ChimahonReaderInputActionHint(
        action = action,
        inputLabel = inputLabel,
        kind = ChimahonReaderInputActionHintKind.TrackpadGesture,
        source = ChimahonReaderInputSource.Trackpad,
        enabled = action.readerInputActionAvailable(this) && !inputLocked && !hasOpenPanel,
    )
}

private fun ChimahonReaderInputUiState.keyboardHint(
    action: ChimahonReaderInputActionId,
    stroke: ChimahonReaderInputKeyStroke,
    enabled: Boolean = action.readerInputActionAvailable(this),
): ChimahonReaderInputActionHint {
    return ChimahonReaderInputActionHint(
        action = action,
        inputLabel = stroke.displayLabel,
        kind = ChimahonReaderInputActionHintKind.KeyboardShortcut,
        source = ChimahonReaderInputSource.Keyboard,
        shortcut = stroke,
        enabled = enabled,
    )
}

private fun ChimahonReaderInputUiState.escapeKeyAction(): ChimahonReaderInputActionId {
    return when {
        hasOpenPanel -> ChimahonReaderInputActionId.DismissPanels
        controlsVisible -> ChimahonReaderInputActionId.ToggleControls
        else -> ChimahonReaderInputActionId.Back
    }
}

private fun ChimahonReaderInputActionId.readerInputActionAvailable(
    state: ChimahonReaderInputUiState,
): Boolean {
    return when (this) {
        ChimahonReaderInputActionId.PreviousPage -> state.canPreviousPage
        ChimahonReaderInputActionId.NextPage -> state.canNextPage
        ChimahonReaderInputActionId.FirstPage -> state.pageCount > 0 && state.currentPage > 1
        ChimahonReaderInputActionId.LastPage -> state.pageCount > 0 && state.currentPage < state.pageCount
        ChimahonReaderInputActionId.PreviousChapter -> state.hasPreviousChapter
        ChimahonReaderInputActionId.NextChapter -> state.hasNextChapter
        ChimahonReaderInputActionId.CyclePageLayout -> state.pageLayoutAvailable
        ChimahonReaderInputActionId.ShiftDoublePages -> state.shiftDoublePagesAvailable
        ChimahonReaderInputActionId.BookmarkChapter -> state.canBookmarkChapter
        ChimahonReaderInputActionId.DownloadChapter -> state.canDownloadChapter
        ChimahonReaderInputActionId.MarkChapterRead -> state.canMarkChapterRead
        ChimahonReaderInputActionId.OpenChapterUrl -> state.canOpenChapterUrl
        ChimahonReaderInputActionId.ShareChapter -> state.canShareChapter
        else -> true
    }
}
