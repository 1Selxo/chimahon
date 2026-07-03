package app.chimahon.shared.readerinputui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

@Composable
fun rememberChimahonReaderWheelAccumulator(
    vararg resetKeys: Any?,
): ChimahonReaderWheelAccumulator {
    return remember(*resetKeys) { ChimahonReaderWheelAccumulator() }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.chimahonReaderKeyboardInput(
    state: ChimahonReaderInputUiState,
    enabled: Boolean = true,
    onDispatch: (ChimahonReaderInputDispatch) -> Unit = {},
    onAction: (ChimahonReaderInputActionId) -> Unit,
): Modifier {
    if (!enabled) return this

    return onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        val stroke = event.toReaderInputKeyStroke()
        if (stroke.key == ChimahonReaderInputKey.Unknown) return@onPreviewKeyEvent false
        val dispatch = state.resolveKeyboardAction(stroke)
        if (dispatch.dispatched) {
            dispatch.action?.let(onAction)
        }
        onDispatch(dispatch)
        dispatch.consumed
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.chimahonReaderWheelInput(
    state: ChimahonReaderInputUiState,
    accumulator: ChimahonReaderWheelAccumulator,
    enabled: Boolean = true,
    source: ChimahonReaderInputSource = ChimahonReaderInputSource.MouseWheel,
    primaryModifier: Boolean = false,
    shiftModifier: Boolean = false,
    altModifier: Boolean = false,
    onDispatch: (ChimahonReaderInputDispatch) -> Unit = {},
    onAction: (ChimahonReaderInputActionId) -> Unit,
): Modifier {
    if (!enabled) return this

    return onPointerEvent(PointerEventType.Scroll) { event ->
        val deltaX = event.changes.sumOf { it.scrollDelta.x.toDouble() }.toFloat()
        val deltaY = event.changes.sumOf { it.scrollDelta.y.toDouble() }.toFloat()
        val dispatch = accumulator.update(
            deltaX = deltaX,
            deltaY = deltaY,
            state = state,
            primaryModifier = primaryModifier,
            shiftModifier = shiftModifier,
            altModifier = altModifier,
            source = source,
        )
        if (dispatch.dispatched) {
            dispatch.action?.let(onAction)
            event.changes.forEach { it.consume() }
        } else if (dispatch.consumed && abs(deltaX) + abs(deltaY) > 0f) {
            event.changes.forEach { it.consume() }
        }
        onDispatch(dispatch)
    }
}

fun Modifier.chimahonReaderTapZoneInput(
    state: ChimahonReaderInputUiState,
    enabled: Boolean = true,
    onDispatch: (ChimahonReaderInputDispatch) -> Unit = {},
    onAction: (ChimahonReaderInputActionId) -> Unit,
): Modifier {
    if (!enabled) return this

    return pointerInput(state) {
        detectTapGestures(
            onLongPress = {
                if (!state.inputLocked && state.longTapEnabled) {
                    val action = if (state.readWithLongTap && state.canMarkChapterRead) {
                        ChimahonReaderInputActionId.MarkChapterRead
                    } else {
                        ChimahonReaderInputActionId.ToggleControls
                    }
                    val dispatch = ChimahonReaderInputDispatch(
                        action = action,
                        source = ChimahonReaderInputSource.LongPress,
                        consumed = true,
                    )
                    onDispatch(dispatch)
                    onAction(action)
                }
            },
            onDoubleTap = {
                if (!state.inputLocked && state.doubleTapToZoom) {
                    val dispatch = ChimahonReaderInputDispatch(
                        action = ChimahonReaderInputActionId.ToggleFitWidthOrScreen,
                        source = ChimahonReaderInputSource.DoubleTap,
                        consumed = true,
                    )
                    onDispatch(dispatch)
                    onAction(ChimahonReaderInputActionId.ToggleFitWidthOrScreen)
                }
            },
            onTap = { offset ->
                val dispatch = state.resolveTap(
                    normalizedX = offset.normalizedX(size.width),
                    normalizedY = offset.normalizedY(size.height),
                )
                onDispatch(dispatch)
                dispatch.action?.let(onAction)
            },
        )
    }
}

fun Modifier.chimahonReaderPointerAffordanceInput(
    state: ChimahonReaderInputUiState,
    enabled: Boolean = true,
    onAffordanceChange: (ChimahonReaderPointerAffordanceState) -> Unit,
): Modifier {
    if (!enabled) return this

    return pointerInput(state) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                when (event.type) {
                    PointerEventType.Move -> {
                        event.changes.firstOrNull()?.position?.let { offset ->
                            onAffordanceChange(
                                state.pointerAffordanceAt(
                                    normalizedX = offset.normalizedX(size.width),
                                    normalizedY = offset.normalizedY(size.height),
                                ),
                            )
                        }
                    }
                    PointerEventType.Exit -> {
                        onAffordanceChange(
                            ChimahonReaderPointerAffordanceState(
                                affordance = ChimahonReaderPointerAffordance.Idle,
                                toolbarVisible = state.controlsVisible,
                            ),
                        )
                    }
                    else -> Unit
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun KeyEvent.toReaderInputKeyStroke(): ChimahonReaderInputKeyStroke {
    return ChimahonReaderInputKeyStroke(
        key = key.toReaderInputKey(),
        primary = isCtrlPressed || isMetaPressed,
        shift = isShiftPressed,
        alt = isAltPressed,
    )
}

fun Key.toReaderInputKey(): ChimahonReaderInputKey {
    return when (this) {
        Key.Escape -> ChimahonReaderInputKey.Escape
        Key.H -> ChimahonReaderInputKey.H
        Key.J -> ChimahonReaderInputKey.J
        Key.K -> ChimahonReaderInputKey.K
        Key.L -> ChimahonReaderInputKey.L
        Key.N -> ChimahonReaderInputKey.N
        Key.M -> ChimahonReaderInputKey.M
        Key.S -> ChimahonReaderInputKey.S
        Key.C -> ChimahonReaderInputKey.C
        Key.I -> ChimahonReaderInputKey.I
        Key.F -> ChimahonReaderInputKey.F
        Key.T -> ChimahonReaderInputKey.T
        Key.G -> ChimahonReaderInputKey.G
        Key.P -> ChimahonReaderInputKey.P
        Key.B -> ChimahonReaderInputKey.B
        Key.D -> ChimahonReaderInputKey.D
        Key.R -> ChimahonReaderInputKey.R
        Key.O -> ChimahonReaderInputKey.O
        Key.Z -> ChimahonReaderInputKey.Z
        Key.W -> ChimahonReaderInputKey.W
        Key.Zero -> ChimahonReaderInputKey.Zero
        Key.One -> ChimahonReaderInputKey.One
        Key.Two -> ChimahonReaderInputKey.Two
        Key.Equals -> ChimahonReaderInputKey.Equals
        Key.Minus -> ChimahonReaderInputKey.Minus
        Key.DirectionLeft -> ChimahonReaderInputKey.DirectionLeft
        Key.DirectionRight -> ChimahonReaderInputKey.DirectionRight
        Key.DirectionUp -> ChimahonReaderInputKey.DirectionUp
        Key.DirectionDown -> ChimahonReaderInputKey.DirectionDown
        Key.PageUp -> ChimahonReaderInputKey.PageUp
        Key.PageDown -> ChimahonReaderInputKey.PageDown
        Key.Spacebar -> ChimahonReaderInputKey.Spacebar
        Key.Enter -> ChimahonReaderInputKey.Enter
        Key.MoveHome -> ChimahonReaderInputKey.MoveHome
        Key.MoveEnd -> ChimahonReaderInputKey.MoveEnd
        Key.VolumeUp -> ChimahonReaderInputKey.VolumeUp
        Key.VolumeDown -> ChimahonReaderInputKey.VolumeDown
        else -> ChimahonReaderInputKey.Unknown
    }
}

private fun Offset.normalizedX(width: Int): Float {
    return if (width <= 0) 0f else (x / width.toFloat()).coerceIn(0f, 1f)
}

private fun Offset.normalizedY(height: Int): Float {
    return if (height <= 0) 0f else (y / height.toFloat()).coerceIn(0f, 1f)
}
