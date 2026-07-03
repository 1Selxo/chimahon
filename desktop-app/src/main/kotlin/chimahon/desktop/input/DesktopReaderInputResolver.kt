package chimahon.desktop.input

import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

fun KeyEvent.resolveDesktopReaderKeyInput(
    scheme: DesktopReaderInputScheme = DesktopReaderInputDefaults.defaultScheme(),
    menuShortcutUsesMeta: Boolean,
    menuShortcutLabel: String,
): DesktopReaderInputMatch? {
    if (id != KeyEvent.KEY_PRESSED) return null

    val binding = scheme.keyBindings.firstOrNull { candidate ->
        candidate.keyCode == keyCode &&
            candidate.modifiers.matches(this, menuShortcutUsesMeta)
    } ?: return null

    return DesktopReaderInputMatch(
        action = binding.action,
        source = DesktopReaderInputSource.Keyboard,
        bindingLabel = binding.label(menuShortcutLabel),
    )
}

fun MouseEvent.resolveDesktopReaderMouseButtonInput(
    scheme: DesktopReaderInputScheme = DesktopReaderInputDefaults.defaultScheme(),
): DesktopReaderInputMatch? {
    if (id != MouseEvent.MOUSE_PRESSED) return null

    val binding = scheme.mouseButtonBindings.firstOrNull { candidate ->
        candidate.button == button &&
            candidate.clickCount == clickCount.coerceAtLeast(1) &&
            candidate.modifiers.matches(this)
    } ?: return null

    return DesktopReaderInputMatch(
        action = binding.action,
        source = DesktopReaderInputSource.MouseButton,
        bindingLabel = binding.label(),
    )
}

fun MouseWheelEvent.resolveDesktopReaderWheelInput(
    scheme: DesktopReaderInputScheme = DesktopReaderInputDefaults.defaultScheme(),
    menuShortcutUsesMeta: Boolean,
): DesktopReaderInputMatch? {
    val binding = scheme.mouseWheelBinding
    val direction = wheelDirection(binding.inverted)
    if (direction == 0) return null

    val action = when {
        binding.menuShortcutOverridesToZoom && isMenuShortcutDown(menuShortcutUsesMeta) ->
            if (direction < 0) DesktopReaderInputAction.ZoomIn else DesktopReaderInputAction.ZoomOut
        binding.shiftOverridesToChapterNavigation && isShiftDown ->
            if (direction < 0) DesktopReaderInputAction.PreviousChapter else DesktopReaderInputAction.NextChapter
        binding.altOverridesToPageScroll && isAltDown ->
            if (direction < 0) DesktopReaderInputAction.PageScrollUp else DesktopReaderInputAction.PageScrollDown
        binding.mode == DesktopReaderMouseWheelMode.NavigatePages ->
            if (direction < 0) DesktopReaderInputAction.PreviousPage else DesktopReaderInputAction.NextPage
        binding.mode == DesktopReaderMouseWheelMode.ScrollPages ->
            if (direction < 0) DesktopReaderInputAction.ScrollUp else DesktopReaderInputAction.ScrollDown
        binding.mode == DesktopReaderMouseWheelMode.Zoom ->
            if (direction < 0) DesktopReaderInputAction.ZoomIn else DesktopReaderInputAction.ZoomOut
        binding.mode == DesktopReaderMouseWheelMode.Disabled -> null
        else -> null
    } ?: return null

    return DesktopReaderInputMatch(
        action = action,
        source = DesktopReaderInputSource.MouseWheel,
        bindingLabel = "Mouse wheel",
        amount = preciseWheelRotation.toFloat(),
    )
}

fun DesktopReaderShortcutModifiers.matches(
    event: KeyEvent,
    menuShortcutUsesMeta: Boolean,
): Boolean {
    val menuShortcutDown = event.isMenuShortcutDown(menuShortcutUsesMeta)
    val extraCtrl = event.isControlDown && !(menuShortcut && !menuShortcutUsesMeta)
    val extraMeta = event.isMetaDown && !(menuShortcut && menuShortcutUsesMeta)

    return menuShortcut == menuShortcutDown &&
        alt == event.isAltDown &&
        shift == event.isShiftDown &&
        ctrl == extraCtrl &&
        meta == extraMeta
}

fun DesktopReaderShortcutModifiers.matches(event: MouseEvent): Boolean {
    return !menuShortcut &&
        alt == event.isAltDown &&
        shift == event.isShiftDown &&
        ctrl == event.isControlDown &&
        meta == event.isMetaDown
}

fun KeyEvent.isMenuShortcutDown(menuShortcutUsesMeta: Boolean): Boolean {
    return if (menuShortcutUsesMeta) isMetaDown else isControlDown
}

fun MouseWheelEvent.isMenuShortcutDown(menuShortcutUsesMeta: Boolean): Boolean {
    return if (menuShortcutUsesMeta) isMetaDown else isControlDown
}

private fun MouseWheelEvent.wheelDirection(inverted: Boolean): Int {
    val rawDirection = when {
        preciseWheelRotation < 0.0 -> -1
        preciseWheelRotation > 0.0 -> 1
        wheelRotation < 0 -> -1
        wheelRotation > 0 -> 1
        else -> 0
    }
    return if (inverted) -rawDirection else rawDirection
}
