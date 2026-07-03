package app.chimahon.shared.novelreaderui

import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import kotlin.math.abs

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.novelReaderDesktopControls(
    state: NovelReaderUiState,
    actions: NovelReaderActions,
): Modifier {
    val controls = state.desktopControls
    if (!controls.enabled) return this

    var wheelAccumulator by remember(
        controls.wheelBehavior,
        controls.invertWheelDirection,
        controls.wheelStepThreshold,
        state.activeChapterId,
        state.pageIndex,
    ) {
        mutableStateOf(0f)
    }

    var chained = this
    if (controls.keyboardEnabled) {
        chained = chained
            .focusable()
            .onPreviewKeyEvent { event ->
                event.handleNovelReaderKeyEvent(
                    state = state,
                    actions = actions,
                    controls = controls,
                )
            }
    }

    if (controls.mouseWheelEnabled) {
        chained = chained.onPointerEvent(PointerEventType.Scroll) { event ->
            val behavior = controls.wheelBehavior.resolveFor(state.readingMode)
            if (
                behavior == NovelReaderWheelBehavior.Disabled ||
                behavior == NovelReaderWheelBehavior.ScrollContent ||
                state.drawerVisible ||
                state.typographyVisible
            ) {
                return@onPointerEvent
            }

            val verticalDelta = event.changes.sumOf { it.scrollDelta.y.toDouble() }.toFloat()
            val horizontalDelta = event.changes.sumOf { it.scrollDelta.x.toDouble() }.toFloat()
            val dominantDelta = if (abs(horizontalDelta) > abs(verticalDelta)) {
                horizontalDelta
            } else {
                verticalDelta
            }
            if (abs(dominantDelta) < 0.5f) return@onPointerEvent

            val adjustedDelta = if (controls.invertWheelDirection) -dominantDelta else dominantDelta
            wheelAccumulator += adjustedDelta
            if (abs(wheelAccumulator) >= controls.wheelStepThreshold.coerceAtLeast(0.1f)) {
                val forward = wheelAccumulator > 0f
                val handled = when (behavior) {
                    NovelReaderWheelBehavior.TurnPage -> actions.performNovelReaderInputIntent(
                        intent = if (forward) NovelReaderInputIntent.NextPage else NovelReaderInputIntent.PreviousPage,
                        state = state,
                    )
                    NovelReaderWheelBehavior.ChangeChapter -> actions.performNovelReaderInputIntent(
                        intent = if (forward) NovelReaderInputIntent.NextChapter else NovelReaderInputIntent.PreviousChapter,
                        state = state,
                    )
                    NovelReaderWheelBehavior.AdjustFontSize -> {
                        val delta = if (forward) -1f else 1f
                        actions.onLayoutChange(
                            state.layout.copy(fontSize = (state.layout.fontSize + delta).coerceIn(10f, 72f)),
                        )
                        true
                    }
                    NovelReaderWheelBehavior.Auto,
                    NovelReaderWheelBehavior.ScrollContent,
                    NovelReaderWheelBehavior.Disabled,
                    -> false
                }
                if (handled && controls.consumeHandledWheel) {
                    event.changes.forEach { it.consume() }
                }
                wheelAccumulator = 0f
            }
        }
    }

    return chained
}

fun NovelReaderActions.performNovelReaderInputIntent(
    intent: NovelReaderInputIntent,
    state: NovelReaderUiState,
): Boolean {
    onInputIntent(intent)
    return when (intent) {
        NovelReaderInputIntent.PreviousPage -> {
            if (state.hud.canGoPreviousPage) onPreviousPage()
            state.hud.canGoPreviousPage
        }
        NovelReaderInputIntent.NextPage -> {
            if (state.hud.canGoNextPage) onNextPage()
            state.hud.canGoNextPage
        }
        NovelReaderInputIntent.PreviousChapter -> {
            if (state.hud.canGoPreviousChapter) onPreviousChapter()
            state.hud.canGoPreviousChapter
        }
        NovelReaderInputIntent.NextChapter -> {
            if (state.hud.canGoNextChapter) onNextChapter()
            state.hud.canGoNextChapter
        }
        NovelReaderInputIntent.FirstPage -> {
            state.chapters.firstOrNull()?.let(onChapterSelected) ?: onProgressChange(0f)
            true
        }
        NovelReaderInputIntent.LastPage -> {
            state.chapters.lastOrNull()?.let(onChapterSelected) ?: onProgressChange(1f)
            true
        }
        NovelReaderInputIntent.ToggleHud -> {
            onToggleHud()
            true
        }
        NovelReaderInputIntent.OpenChapters -> {
            if (state.drawerVisible) onCloseChapters() else onOpenChapters()
            true
        }
        NovelReaderInputIntent.CloseChapters -> {
            onCloseChapters()
            true
        }
        NovelReaderInputIntent.OpenTypography -> {
            if (state.typographyVisible) onCloseTypography() else onOpenTypography()
            true
        }
        NovelReaderInputIntent.CloseTypography -> {
            onCloseTypography()
            true
        }
        NovelReaderInputIntent.LookupSelection -> {
            val selection = state.selection ?: return false
            onSelectionAction(NovelReaderSelectionAction.Lookup, selection)
            true
        }
        NovelReaderInputIntent.ClearSelection -> {
            val selection = state.selection ?: return false
            onSelectionAction(NovelReaderSelectionAction.Clear, selection)
            true
        }
        NovelReaderInputIntent.ToggleFullScreen -> {
            onToggleFullScreen()
            true
        }
    }
}

private fun KeyEvent.handleNovelReaderKeyEvent(
    state: NovelReaderUiState,
    actions: NovelReaderActions,
    controls: NovelReaderDesktopControlsState,
): Boolean {
    if (type != KeyEventType.KeyDown) return false

    val primaryPressed = isCtrlPressed || isMetaPressed
    val chapterShortcut = primaryPressed || isShiftPressed
    return when {
        controls.escapeClosesChrome && key == Key.Escape -> actions.dismissNovelReaderChrome(state)
        isAltPressed && key == Key.Enter -> actions.performNovelReaderInputIntent(
            intent = NovelReaderInputIntent.ToggleFullScreen,
            state = state,
        )
        controls.homeEndEnabled && key == Key.MoveHome -> actions.performNovelReaderInputIntent(
            intent = NovelReaderInputIntent.FirstPage,
            state = state,
        )
        controls.homeEndEnabled && key == Key.MoveEnd -> actions.performNovelReaderInputIntent(
            intent = NovelReaderInputIntent.LastPage,
            state = state,
        )
        controls.pageUpDownEnabled && key == Key.PageUp -> actions.performNovelReaderInputIntent(
            intent = if (chapterShortcut) {
                NovelReaderInputIntent.PreviousChapter
            } else {
                NovelReaderInputIntent.PreviousPage
            },
            state = state,
        )
        controls.pageUpDownEnabled && key == Key.PageDown -> actions.performNovelReaderInputIntent(
            intent = if (chapterShortcut) {
                NovelReaderInputIntent.NextChapter
            } else {
                NovelReaderInputIntent.NextPage
            },
            state = state,
        )
        controls.arrowKeysEnabled && key == Key.DirectionLeft -> actions.performNovelReaderInputIntent(
            intent = state.horizontalReaderIntent(leftEdge = true, chapter = chapterShortcut),
            state = state,
        )
        controls.arrowKeysEnabled && key == Key.DirectionRight -> actions.performNovelReaderInputIntent(
            intent = state.horizontalReaderIntent(leftEdge = false, chapter = chapterShortcut),
            state = state,
        )
        controls.arrowKeysEnabled && key == Key.DirectionUp -> actions.performNovelReaderInputIntent(
            intent = if (chapterShortcut) {
                NovelReaderInputIntent.PreviousChapter
            } else {
                NovelReaderInputIntent.PreviousPage
            },
            state = state,
        )
        controls.arrowKeysEnabled && key == Key.DirectionDown -> actions.performNovelReaderInputIntent(
            intent = if (chapterShortcut) {
                NovelReaderInputIntent.NextChapter
            } else {
                NovelReaderInputIntent.NextPage
            },
            state = state,
        )
        controls.vimKeysEnabled && (key == Key.H || key == Key.K) -> actions.performNovelReaderInputIntent(
            intent = if (chapterShortcut) {
                NovelReaderInputIntent.PreviousChapter
            } else {
                NovelReaderInputIntent.PreviousPage
            },
            state = state,
        )
        controls.vimKeysEnabled && (key == Key.L || key == Key.J) -> actions.performNovelReaderInputIntent(
            intent = if (chapterShortcut) {
                NovelReaderInputIntent.NextChapter
            } else {
                NovelReaderInputIntent.NextPage
            },
            state = state,
        )
        !primaryPressed && !isAltPressed && !isShiftPressed && (key == Key.Enter || key == Key.Spacebar) -> {
            actions.performNovelReaderInputIntent(NovelReaderInputIntent.ToggleHud, state)
        }
        !primaryPressed && !isAltPressed && !isShiftPressed && key == Key.C -> {
            actions.performNovelReaderInputIntent(NovelReaderInputIntent.OpenChapters, state)
        }
        !primaryPressed && !isAltPressed && !isShiftPressed && key == Key.S -> {
            actions.performNovelReaderInputIntent(NovelReaderInputIntent.OpenTypography, state)
        }
        (isAltPressed || primaryPressed) && key == Key.G && state.selection != null -> {
            actions.performNovelReaderInputIntent(NovelReaderInputIntent.LookupSelection, state)
        }
        else -> false
    }
}

private fun NovelReaderActions.dismissNovelReaderChrome(state: NovelReaderUiState): Boolean {
    return when {
        state.selection != null -> performNovelReaderInputIntent(NovelReaderInputIntent.ClearSelection, state)
        state.typographyVisible -> performNovelReaderInputIntent(NovelReaderInputIntent.CloseTypography, state)
        state.drawerVisible -> performNovelReaderInputIntent(NovelReaderInputIntent.CloseChapters, state)
        !state.hud.visible -> performNovelReaderInputIntent(NovelReaderInputIntent.ToggleHud, state)
        else -> {
            onBack()
            true
        }
    }
}

private fun NovelReaderUiState.horizontalReaderIntent(
    leftEdge: Boolean,
    chapter: Boolean,
): NovelReaderInputIntent {
    val reversed = readingDirection == NovelReadingDirection.RightToLeft ||
        readingDirection == NovelReadingDirection.VerticalRightToLeft
    val previous = if (reversed) !leftEdge else leftEdge
    return when {
        previous && chapter -> NovelReaderInputIntent.PreviousChapter
        !previous && chapter -> NovelReaderInputIntent.NextChapter
        previous -> NovelReaderInputIntent.PreviousPage
        else -> NovelReaderInputIntent.NextPage
    }
}

private fun NovelReaderWheelBehavior.resolveFor(readingMode: NovelReadingMode): NovelReaderWheelBehavior {
    return when (this) {
        NovelReaderWheelBehavior.Auto -> {
            if (readingMode == NovelReadingMode.Continuous) {
                NovelReaderWheelBehavior.ScrollContent
            } else {
                NovelReaderWheelBehavior.TurnPage
            }
        }
        else -> this
    }
}
