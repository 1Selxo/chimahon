package app.chimahon.shared.iosui

import app.chimahon.shared.readerinputui.ChimahonReaderFullscreenToolbarState
import app.chimahon.shared.readerinputui.ChimahonReaderHardwareKeyEvent
import app.chimahon.shared.readerinputui.ChimahonReaderInputActionHint
import app.chimahon.shared.readerinputui.ChimahonReaderInputActionHintKind
import app.chimahon.shared.readerinputui.ChimahonReaderInputActionId
import app.chimahon.shared.readerinputui.ChimahonReaderInputDispatch
import app.chimahon.shared.readerinputui.ChimahonReaderInputKey
import app.chimahon.shared.readerinputui.ChimahonReaderInputKeyStroke
import app.chimahon.shared.readerinputui.ChimahonReaderInputSource
import app.chimahon.shared.readerinputui.ChimahonReaderInputUiState
import app.chimahon.shared.readerinputui.ChimahonReaderTrackpadScrollEvent
import app.chimahon.shared.readerinputui.ChimahonReaderTrackpadScrollPhase
import app.chimahon.shared.readerinputui.ChimahonReaderWheelAccumulator
import app.chimahon.shared.readerinputui.readerInputActionHints
import app.chimahon.shared.readerinputui.resolveHardwareKeyboardEvent
import app.chimahon.shared.readerinputui.updateTrackpadScroll

private const val IosReaderDefaultTopChromeHeight = 44f
private const val IosReaderDefaultBottomChromeHeight = 52f
private const val IosReaderDefaultActionStripHeight = 44f
private const val IosReaderDefaultIndicatorHeight = 28f

enum class IosReaderUserInterfaceIdiom {
    Phone,
    Pad,
}

enum class IosReaderHardwareKey {
    Escape,
    H,
    J,
    K,
    L,
    N,
    M,
    S,
    C,
    I,
    F,
    T,
    G,
    P,
    B,
    D,
    R,
    O,
    Z,
    W,
    Zero,
    One,
    Two,
    Plus,
    Equals,
    Minus,
    ArrowLeft,
    ArrowRight,
    ArrowUp,
    ArrowDown,
    PageUp,
    PageDown,
    Space,
    Enter,
    Home,
    End,
    Unknown,
}

data class IosReaderKeyboardModifiers(
    val command: Boolean = false,
    val control: Boolean = false,
    val shift: Boolean = false,
    val option: Boolean = false,
) {
    val primary: Boolean
        get() = command || control

    val label: String
        get() = buildList {
            if (command) add("Command")
            if (control) add("Control")
            if (option) add("Option")
            if (shift) add("Shift")
        }.joinToString("+")
}

data class IosReaderHardwareKeyEvent(
    val key: IosReaderHardwareKey,
    val modifiers: IosReaderKeyboardModifiers = IosReaderKeyboardModifiers(),
    val repeated: Boolean = false,
    val reservedBySystem: Boolean = false,
) {
    fun toReaderHardwareKeyEvent(): ChimahonReaderHardwareKeyEvent {
        return ChimahonReaderHardwareKeyEvent(
            key = key.toReaderInputKey(),
            primaryModifier = modifiers.primary,
            shiftModifier = modifiers.shift,
            altModifier = modifiers.option,
            repeated = repeated,
            source = ChimahonReaderInputSource.Keyboard,
            reservedBySystem = reservedBySystem,
        )
    }
}

data class IosReaderTrackpadScroll(
    val deltaX: Float,
    val deltaY: Float,
    val modifiers: IosReaderKeyboardModifiers = IosReaderKeyboardModifiers(),
    val phase: ChimahonReaderTrackpadScrollPhase = ChimahonReaderTrackpadScrollPhase.Changed,
    val fingerCount: Int = 2,
) {
    fun toReaderTrackpadScrollEvent(): ChimahonReaderTrackpadScrollEvent {
        return ChimahonReaderTrackpadScrollEvent(
            deltaX = deltaX,
            deltaY = deltaY,
            primaryModifier = modifiers.primary,
            shiftModifier = modifiers.shift,
            altModifier = modifiers.option,
            phase = phase,
            fingerCount = fingerCount,
            requiresTwoFingerScroll = true,
        )
    }
}

data class IosReaderRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float
        get() = (right - left).coerceAtLeast(0f)

    val height: Float
        get() = (bottom - top).coerceAtLeast(0f)

    val empty: Boolean
        get() = width == 0f || height == 0f

    fun normalized(): IosReaderRect {
        return IosReaderRect(
            left = minOf(left, right),
            top = minOf(top, bottom),
            right = maxOf(left, right),
            bottom = maxOf(top, bottom),
        )
    }

    fun inset(insets: IosUiEdgeInsets): IosReaderRect {
        val normalizedInsets = insets.normalized()
        return IosReaderRect(
            left = left + normalizedInsets.leading,
            top = top + normalizedInsets.top,
            right = (right - normalizedInsets.trailing).coerceAtLeast(left + normalizedInsets.leading),
            bottom = (bottom - normalizedInsets.bottom).coerceAtLeast(top + normalizedInsets.top),
        )
    }

    fun intersection(other: IosReaderRect): IosReaderRect? {
        val a = normalized()
        val b = other.normalized()
        val intersection = IosReaderRect(
            left = maxOf(a.left, b.left),
            top = maxOf(a.top, b.top),
            right = minOf(a.right, b.right),
            bottom = minOf(a.bottom, b.bottom),
        )
        return if (intersection.empty) null else intersection
    }

    companion object {
        fun fromFrame(
            x: Float,
            y: Float,
            width: Float,
            height: Float,
        ): IosReaderRect {
            return IosReaderRect(
                left = x,
                top = y,
                right = x + width.coerceAtLeast(0f),
                bottom = y + height.coerceAtLeast(0f),
            )
        }
    }
}

data class IosReaderChromeMetrics(
    val topBarHeight: Float = IosReaderDefaultTopChromeHeight,
    val bottomBarHeight: Float = IosReaderDefaultBottomChromeHeight,
    val actionStripHeight: Float = IosReaderDefaultActionStripHeight,
    val pageIndicatorHeight: Float = IosReaderDefaultIndicatorHeight,
    val modeIndicatorHeight: Float = IosReaderDefaultIndicatorHeight,
) {
    fun normalized(): IosReaderChromeMetrics {
        return copy(
            topBarHeight = topBarHeight.coerceAtLeast(0f),
            bottomBarHeight = bottomBarHeight.coerceAtLeast(0f),
            actionStripHeight = actionStripHeight.coerceAtLeast(0f),
            pageIndicatorHeight = pageIndicatorHeight.coerceAtLeast(0f),
            modeIndicatorHeight = modeIndicatorHeight.coerceAtLeast(0f),
        )
    }
}

data class IosReaderKeyboardOverlap(
    val dockState: IosKeyboardDockState = IosKeyboardDockState.Hidden,
    val frame: IosReaderRect? = null,
    val contentOverlapHeight: Float = 0f,
    val bottomChromeOverlapHeight: Float = 0f,
    val bottomAvoidanceInset: Float = 0f,
) {
    val visible: Boolean
        get() = dockState != IosKeyboardDockState.Hidden &&
            ((frame?.empty == false) || bottomAvoidanceInset > 0f)

    val overlapsFloatingKeyboard: Boolean
        get() = dockState == IosKeyboardDockState.Floating || dockState == IosKeyboardDockState.Split
}

data class IosReaderChromeLayoutRequest(
    val viewportWidth: Float,
    val viewportHeight: Float,
    val safeArea: IosUiEdgeInsets = IosUiEdgeInsets.Zero,
    val statusBarTopInset: Float = safeArea.top,
    val homeIndicatorBottomInset: Float = safeArea.bottom,
    val keyboard: IosKeyboardObstruction = IosKeyboardObstruction.Hidden,
    val floatingKeyboardFrame: IosReaderRect? = null,
    val metrics: IosReaderChromeMetrics = IosReaderChromeMetrics(),
) {
    val viewport: IosReaderRect
        get() = IosReaderRect(0f, 0f, viewportWidth.coerceAtLeast(0f), viewportHeight.coerceAtLeast(0f))
}

data class IosReaderChromeLayout(
    val viewport: IosReaderRect,
    val safeArea: IosUiEdgeInsets,
    val statusBarTopInset: Float,
    val homeIndicatorBottomInset: Float,
    val keyboardBottomInset: Float,
    val keyboardOverlap: IosReaderKeyboardOverlap,
    val topChromeHeight: Float,
    val bottomChromeHeight: Float,
    val contentInsets: IosUiEdgeInsets,
    val chromeInsets: IosUiEdgeInsets,
    val toolbar: ChimahonReaderFullscreenToolbarState,
) {
    val contentFrame: IosReaderRect
        get() = viewport.inset(contentInsets)

    val topChromeInset: Float
        get() = chromeInsets.top

    val bottomChromeInset: Float
        get() = chromeInsets.bottom
}

data class IosReaderActionHint(
    val action: ChimahonReaderInputActionId,
    val title: String,
    val discoverabilityTitle: String,
    val inputLabel: String,
    val kind: ChimahonReaderInputActionHintKind,
    val source: ChimahonReaderInputSource,
    val key: IosReaderHardwareKey? = null,
    val modifiers: IosReaderKeyboardModifiers = IosReaderKeyboardModifiers(),
    val enabled: Boolean = true,
)

fun ChimahonReaderInputUiState.resolveIosHardwareKeyboardEvent(
    event: IosReaderHardwareKeyEvent,
): ChimahonReaderInputDispatch {
    return resolveHardwareKeyboardEvent(event.toReaderHardwareKeyEvent())
}

fun ChimahonReaderWheelAccumulator.updateIosTrackpadScroll(
    event: IosReaderTrackpadScroll,
    state: ChimahonReaderInputUiState,
): ChimahonReaderInputDispatch {
    return updateTrackpadScroll(event.toReaderTrackpadScrollEvent(), state)
}

fun ChimahonReaderInputUiState.resolveIosReaderChromeLayout(
    request: IosReaderChromeLayoutRequest,
): IosReaderChromeLayout {
    val viewport = request.viewport
    val safeArea = request.safeArea.normalized()
    val metrics = request.metrics.normalized()
    val toolbar = fullscreenToolbarState(request.viewportWidth)
    val statusTop = maxOf(
        safeArea.top,
        request.statusBarTopInset.coerceAtLeast(0f),
    )
    val homeBottom = maxOf(
        safeArea.bottom,
        request.homeIndicatorBottomInset.coerceAtLeast(0f),
    )
    val keyboardBottom = request.keyboard.scrollInset.coerceAtLeast(0f)
    val topChromeHeight = if (toolbar.topBarVisible) metrics.topBarHeight else 0f
    val visibleBottomBars =
        (if (toolbar.bottomBarVisible) metrics.bottomBarHeight else 0f) +
            (if (toolbar.desktopActionStripVisible) metrics.actionStripHeight else 0f)
    val bottomChromeHeight = maxOf(
        visibleBottomBars,
        if (toolbar.pageIndicatorVisible) metrics.pageIndicatorHeight else 0f,
        if (toolbar.modeIndicatorVisible) metrics.modeIndicatorHeight else 0f,
    )
    val baseBottomAvoidance = maxOf(homeBottom, keyboardBottom)
    val provisionalChromeInsets = IosUiEdgeInsets(
        top = statusTop + topChromeHeight,
        bottom = baseBottomAvoidance + bottomChromeHeight,
        leading = safeArea.leading,
        trailing = safeArea.trailing,
    )
    val provisionalContentFrame = viewport.inset(provisionalChromeInsets)
    val bottomChromeFrame = IosReaderRect(
        left = safeArea.leading,
        top = (viewport.bottom - baseBottomAvoidance - bottomChromeHeight).coerceAtLeast(0f),
        right = (viewport.right - safeArea.trailing).coerceAtLeast(safeArea.leading),
        bottom = (viewport.bottom - baseBottomAvoidance).coerceAtLeast(0f),
    )
    val overlap = resolveIosReaderKeyboardOverlap(
        keyboard = request.keyboard,
        viewport = viewport,
        floatingKeyboardFrame = request.floatingKeyboardFrame,
        contentFrame = provisionalContentFrame,
        bottomChromeFrame = bottomChromeFrame,
    )
    val bottomAvoidance = maxOf(baseBottomAvoidance, overlap.bottomAvoidanceInset)
    val chromeInsets = IosUiEdgeInsets(
        top = statusTop + topChromeHeight,
        bottom = bottomAvoidance + bottomChromeHeight,
        leading = safeArea.leading,
        trailing = safeArea.trailing,
    )
    val contentInsets = IosUiEdgeInsets(
        top = statusTop,
        bottom = bottomAvoidance,
        leading = safeArea.leading,
        trailing = safeArea.trailing,
    )
    return IosReaderChromeLayout(
        viewport = viewport,
        safeArea = safeArea,
        statusBarTopInset = statusTop,
        homeIndicatorBottomInset = homeBottom,
        keyboardBottomInset = keyboardBottom,
        keyboardOverlap = overlap,
        topChromeHeight = topChromeHeight,
        bottomChromeHeight = bottomChromeHeight,
        contentInsets = contentInsets,
        chromeInsets = chromeInsets,
        toolbar = toolbar,
    )
}

fun IosViewportObstructionState.toIosReaderChromeLayoutRequest(
    homeIndicatorBottomInset: Float = safeArea.bottom,
    floatingKeyboardFrame: IosReaderRect? = null,
    metrics: IosReaderChromeMetrics = IosReaderChromeMetrics(),
): IosReaderChromeLayoutRequest {
    return IosReaderChromeLayoutRequest(
        viewportWidth = width,
        viewportHeight = height,
        safeArea = safeArea,
        statusBarTopInset = topStatusBar.statusProtectedTop,
        homeIndicatorBottomInset = homeIndicatorBottomInset,
        keyboard = keyboard,
        floatingKeyboardFrame = floatingKeyboardFrame,
        metrics = metrics,
    )
}

fun ChimahonReaderInputUiState.iosReaderActionHints(
    includeKeyboard: Boolean = true,
    includeTrackpad: Boolean = true,
    includeChrome: Boolean = true,
    includeChapterActions: Boolean = true,
): List<IosReaderActionHint> {
    return readerInputActionHints(
        includeKeyboard = includeKeyboard,
        includeTrackpad = includeTrackpad,
        includeChrome = includeChrome,
        includeChapterActions = includeChapterActions,
    ).map { hint ->
        hint.toIosReaderActionHint()
    }
}

fun IosReaderHardwareKey.toReaderInputKey(): ChimahonReaderInputKey {
    return when (this) {
        IosReaderHardwareKey.Escape -> ChimahonReaderInputKey.Escape
        IosReaderHardwareKey.H -> ChimahonReaderInputKey.H
        IosReaderHardwareKey.J -> ChimahonReaderInputKey.J
        IosReaderHardwareKey.K -> ChimahonReaderInputKey.K
        IosReaderHardwareKey.L -> ChimahonReaderInputKey.L
        IosReaderHardwareKey.N -> ChimahonReaderInputKey.N
        IosReaderHardwareKey.M -> ChimahonReaderInputKey.M
        IosReaderHardwareKey.S -> ChimahonReaderInputKey.S
        IosReaderHardwareKey.C -> ChimahonReaderInputKey.C
        IosReaderHardwareKey.I -> ChimahonReaderInputKey.I
        IosReaderHardwareKey.F -> ChimahonReaderInputKey.F
        IosReaderHardwareKey.T -> ChimahonReaderInputKey.T
        IosReaderHardwareKey.G -> ChimahonReaderInputKey.G
        IosReaderHardwareKey.P -> ChimahonReaderInputKey.P
        IosReaderHardwareKey.B -> ChimahonReaderInputKey.B
        IosReaderHardwareKey.D -> ChimahonReaderInputKey.D
        IosReaderHardwareKey.R -> ChimahonReaderInputKey.R
        IosReaderHardwareKey.O -> ChimahonReaderInputKey.O
        IosReaderHardwareKey.Z -> ChimahonReaderInputKey.Z
        IosReaderHardwareKey.W -> ChimahonReaderInputKey.W
        IosReaderHardwareKey.Zero -> ChimahonReaderInputKey.Zero
        IosReaderHardwareKey.One -> ChimahonReaderInputKey.One
        IosReaderHardwareKey.Two -> ChimahonReaderInputKey.Two
        IosReaderHardwareKey.Plus -> ChimahonReaderInputKey.Plus
        IosReaderHardwareKey.Equals -> ChimahonReaderInputKey.Equals
        IosReaderHardwareKey.Minus -> ChimahonReaderInputKey.Minus
        IosReaderHardwareKey.ArrowLeft -> ChimahonReaderInputKey.DirectionLeft
        IosReaderHardwareKey.ArrowRight -> ChimahonReaderInputKey.DirectionRight
        IosReaderHardwareKey.ArrowUp -> ChimahonReaderInputKey.DirectionUp
        IosReaderHardwareKey.ArrowDown -> ChimahonReaderInputKey.DirectionDown
        IosReaderHardwareKey.PageUp -> ChimahonReaderInputKey.PageUp
        IosReaderHardwareKey.PageDown -> ChimahonReaderInputKey.PageDown
        IosReaderHardwareKey.Space -> ChimahonReaderInputKey.Spacebar
        IosReaderHardwareKey.Enter -> ChimahonReaderInputKey.Enter
        IosReaderHardwareKey.Home -> ChimahonReaderInputKey.MoveHome
        IosReaderHardwareKey.End -> ChimahonReaderInputKey.MoveEnd
        IosReaderHardwareKey.Unknown -> ChimahonReaderInputKey.Unknown
    }
}

fun ChimahonReaderInputKey.toIosReaderHardwareKey(): IosReaderHardwareKey? {
    return when (this) {
        ChimahonReaderInputKey.Escape -> IosReaderHardwareKey.Escape
        ChimahonReaderInputKey.H -> IosReaderHardwareKey.H
        ChimahonReaderInputKey.J -> IosReaderHardwareKey.J
        ChimahonReaderInputKey.K -> IosReaderHardwareKey.K
        ChimahonReaderInputKey.L -> IosReaderHardwareKey.L
        ChimahonReaderInputKey.N -> IosReaderHardwareKey.N
        ChimahonReaderInputKey.M -> IosReaderHardwareKey.M
        ChimahonReaderInputKey.S -> IosReaderHardwareKey.S
        ChimahonReaderInputKey.C -> IosReaderHardwareKey.C
        ChimahonReaderInputKey.I -> IosReaderHardwareKey.I
        ChimahonReaderInputKey.F -> IosReaderHardwareKey.F
        ChimahonReaderInputKey.T -> IosReaderHardwareKey.T
        ChimahonReaderInputKey.G -> IosReaderHardwareKey.G
        ChimahonReaderInputKey.P -> IosReaderHardwareKey.P
        ChimahonReaderInputKey.B -> IosReaderHardwareKey.B
        ChimahonReaderInputKey.D -> IosReaderHardwareKey.D
        ChimahonReaderInputKey.R -> IosReaderHardwareKey.R
        ChimahonReaderInputKey.O -> IosReaderHardwareKey.O
        ChimahonReaderInputKey.Z -> IosReaderHardwareKey.Z
        ChimahonReaderInputKey.W -> IosReaderHardwareKey.W
        ChimahonReaderInputKey.Zero -> IosReaderHardwareKey.Zero
        ChimahonReaderInputKey.One -> IosReaderHardwareKey.One
        ChimahonReaderInputKey.Two -> IosReaderHardwareKey.Two
        ChimahonReaderInputKey.Plus -> IosReaderHardwareKey.Plus
        ChimahonReaderInputKey.Equals -> IosReaderHardwareKey.Equals
        ChimahonReaderInputKey.Minus -> IosReaderHardwareKey.Minus
        ChimahonReaderInputKey.DirectionLeft -> IosReaderHardwareKey.ArrowLeft
        ChimahonReaderInputKey.DirectionRight -> IosReaderHardwareKey.ArrowRight
        ChimahonReaderInputKey.DirectionUp -> IosReaderHardwareKey.ArrowUp
        ChimahonReaderInputKey.DirectionDown -> IosReaderHardwareKey.ArrowDown
        ChimahonReaderInputKey.PageUp -> IosReaderHardwareKey.PageUp
        ChimahonReaderInputKey.PageDown -> IosReaderHardwareKey.PageDown
        ChimahonReaderInputKey.Spacebar -> IosReaderHardwareKey.Space
        ChimahonReaderInputKey.Enter -> IosReaderHardwareKey.Enter
        ChimahonReaderInputKey.MoveHome -> IosReaderHardwareKey.Home
        ChimahonReaderInputKey.MoveEnd -> IosReaderHardwareKey.End
        ChimahonReaderInputKey.NumPlus -> IosReaderHardwareKey.Plus
        ChimahonReaderInputKey.NumMinus -> IosReaderHardwareKey.Minus
        ChimahonReaderInputKey.VolumeUp,
        ChimahonReaderInputKey.VolumeDown,
        ChimahonReaderInputKey.Unknown,
        -> null
    }
}

private fun resolveIosReaderKeyboardOverlap(
    keyboard: IosKeyboardObstruction,
    viewport: IosReaderRect,
    floatingKeyboardFrame: IosReaderRect?,
    contentFrame: IosReaderRect,
    bottomChromeFrame: IosReaderRect,
): IosReaderKeyboardOverlap {
    val suppliedFloatingFrame = floatingKeyboardFrame
        ?.normalized()
        ?.takeUnless { it.empty }
    if (!keyboard.visible && suppliedFloatingFrame == null) {
        return IosReaderKeyboardOverlap()
    }
    if (keyboard.dockState == IosKeyboardDockState.Docked) {
        val bottomInset = keyboard.scrollInset.coerceAtLeast(0f)
        return IosReaderKeyboardOverlap(
            dockState = keyboard.dockState,
            frame = IosReaderRect(
                left = viewport.left,
                top = (viewport.bottom - keyboard.bottomOverlap).coerceIn(viewport.top, viewport.bottom),
                right = viewport.right,
                bottom = viewport.bottom,
            ),
            contentOverlapHeight = bottomInset,
            bottomChromeOverlapHeight = bottomInset,
            bottomAvoidanceInset = bottomInset,
        )
    }

    val dockState = if (keyboard.dockState == IosKeyboardDockState.Hidden) {
        IosKeyboardDockState.Floating
    } else {
        keyboard.dockState
    }
    val frame = suppliedFloatingFrame
        ?: keyboard.toApproximateReaderFrame(viewport)
        ?: return IosReaderKeyboardOverlap(dockState = dockState)
    val normalizedFrame = frame.normalized()
    val contentOverlapHeight = normalizedFrame.intersection(contentFrame)?.height ?: 0f
    val bottomChromeOverlapHeight = normalizedFrame.intersection(bottomChromeFrame)?.height ?: 0f
    val bottomAvoidanceInset = if (bottomChromeOverlapHeight > 0f) {
        (viewport.bottom - normalizedFrame.top).coerceAtLeast(0f)
    } else {
        0f
    }
    return IosReaderKeyboardOverlap(
        dockState = dockState,
        frame = normalizedFrame,
        contentOverlapHeight = contentOverlapHeight,
        bottomChromeOverlapHeight = bottomChromeOverlapHeight,
        bottomAvoidanceInset = bottomAvoidanceInset,
    )
}

private fun IosKeyboardObstruction.toApproximateReaderFrame(
    viewport: IosReaderRect,
): IosReaderRect? {
    val top = keyboardTop ?: return null
    if (keyboardHeight <= 0f) return null
    return IosReaderRect(
        left = viewport.left,
        top = top.coerceIn(viewport.top, viewport.bottom),
        right = viewport.right,
        bottom = (top + keyboardHeight).coerceIn(viewport.top, viewport.bottom),
    )
}

private fun ChimahonReaderInputActionHint.toIosReaderActionHint(): IosReaderActionHint {
    val key = shortcut?.key?.toIosReaderHardwareKey()
    val modifiers = shortcut?.toIosReaderKeyboardModifiers() ?: IosReaderKeyboardModifiers()
    return IosReaderActionHint(
        action = action,
        title = title,
        discoverabilityTitle = title,
        inputLabel = inputLabel.toIosReaderInputLabel(),
        kind = kind,
        source = source,
        key = key,
        modifiers = modifiers,
        enabled = enabled,
    )
}

private fun ChimahonReaderInputKeyStroke.toIosReaderKeyboardModifiers(): IosReaderKeyboardModifiers {
    return IosReaderKeyboardModifiers(
        command = primary,
        shift = shift,
        option = alt,
    )
}

private fun String.toIosReaderInputLabel(): String {
    return replace("Ctrl/Cmd", "Command")
        .replace("Alt", "Option")
}
