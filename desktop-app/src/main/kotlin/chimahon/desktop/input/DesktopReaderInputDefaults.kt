package chimahon.desktop.input

import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

object DesktopReaderInputDefaults {
    const val BackMouseButton: Int = 4
    const val ForwardMouseButton: Int = 5

    fun defaultScheme(): DesktopReaderInputScheme {
        return DesktopReaderInputScheme(
            keyBindings = defaultKeyBindings(),
            mouseButtonBindings = defaultMouseButtonBindings(),
            mouseWheelBinding = DesktopReaderMouseWheelBinding(),
        )
    }

    fun defaultKeyBindings(): List<DesktopReaderKeyBinding> {
        val plain = DesktopReaderShortcutModifiers()
        val shift = DesktopReaderShortcutModifiers(shift = true)
        val menu = DesktopReaderShortcutModifiers(menuShortcut = true)
        val menuAlt = DesktopReaderShortcutModifiers(menuShortcut = true, alt = true)
        val menuAltShift = DesktopReaderShortcutModifiers(menuShortcut = true, alt = true, shift = true)
        val ctrlMenu = DesktopReaderShortcutModifiers(menuShortcut = true)
        val macFullScreen = DesktopReaderShortcutModifiers(menuShortcut = true, ctrl = true)

        return listOf(
            DesktopReaderKeyBinding(DesktopReaderInputAction.PreviousPage, KeyEvent.VK_LEFT, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.PreviousPage, KeyEvent.VK_UP, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.PreviousPage, KeyEvent.VK_PAGE_UP, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.PreviousPage, KeyEvent.VK_SPACE, shift),
            DesktopReaderKeyBinding(DesktopReaderInputAction.PreviousPage, KeyEvent.VK_LEFT, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.NextPage, KeyEvent.VK_RIGHT, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.NextPage, KeyEvent.VK_DOWN, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.NextPage, KeyEvent.VK_PAGE_DOWN, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.NextPage, KeyEvent.VK_SPACE, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.NextPage, KeyEvent.VK_RIGHT, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.FirstPage, KeyEvent.VK_HOME, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.FirstPage, KeyEvent.VK_HOME, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.LastPage, KeyEvent.VK_END, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.LastPage, KeyEvent.VK_END, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.PreviousChapter, KeyEvent.VK_PAGE_UP, shift),
            DesktopReaderKeyBinding(DesktopReaderInputAction.PreviousChapter, KeyEvent.VK_LEFT, shift),
            DesktopReaderKeyBinding(DesktopReaderInputAction.PreviousChapter, KeyEvent.VK_LEFT, menuAltShift),
            DesktopReaderKeyBinding(DesktopReaderInputAction.NextChapter, KeyEvent.VK_PAGE_DOWN, shift),
            DesktopReaderKeyBinding(DesktopReaderInputAction.NextChapter, KeyEvent.VK_RIGHT, shift),
            DesktopReaderKeyBinding(DesktopReaderInputAction.NextChapter, KeyEvent.VK_RIGHT, menuAltShift),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ToggleToolbar, KeyEvent.VK_ENTER, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ToggleToolbar, KeyEvent.VK_ENTER, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.CycleReadingMode, KeyEvent.VK_M, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.CycleReadingMode, KeyEvent.VK_M, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.OpenSettings, KeyEvent.VK_S, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.OpenSettings, KeyEvent.VK_COMMA, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.OpenChapterList, KeyEvent.VK_C, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.OpenChapterList, KeyEvent.VK_C, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ToggleStats, KeyEvent.VK_I, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ToggleStats, KeyEvent.VK_I, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ToggleCropBorders, KeyEvent.VK_F, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ToggleCropBorders, KeyEvent.VK_F, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ToggleOcrLookup, KeyEvent.VK_G, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ToggleOcrLookup, KeyEvent.VK_G, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.CycleOrientation, KeyEvent.VK_T, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.CycleOrientation, KeyEvent.VK_T, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.CyclePageLayout, KeyEvent.VK_L, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.CyclePageLayout, KeyEvent.VK_L, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ShiftDoublePages, KeyEvent.VK_P, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ShiftDoublePages, KeyEvent.VK_P, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.BookmarkChapter, KeyEvent.VK_B, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.BookmarkChapter, KeyEvent.VK_B, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.DownloadChapter, KeyEvent.VK_D, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.DownloadChapter, KeyEvent.VK_D, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.MarkChapterRead, KeyEvent.VK_R, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.MarkChapterRead, KeyEvent.VK_R, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.OpenChapterUrl, KeyEvent.VK_O, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.OpenChapterUrl, KeyEvent.VK_O, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ShareChapter, KeyEvent.VK_S, menuAltShift),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ZoomIn, KeyEvent.VK_EQUALS, menu),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ZoomIn, KeyEvent.VK_PLUS, menu),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ZoomIn, KeyEvent.VK_ADD, menu),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ZoomOut, KeyEvent.VK_MINUS, menu),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ZoomOut, KeyEvent.VK_SUBTRACT, menu),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ResetZoom, KeyEvent.VK_0, ctrlMenu),
            DesktopReaderKeyBinding(DesktopReaderInputAction.FitWidth, KeyEvent.VK_W, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.FitHeight, KeyEvent.VK_H, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.FitScreen, KeyEvent.VK_0, menuAlt),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ToggleFitWidthOrScreen, KeyEvent.VK_Z, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ScrollUp, KeyEvent.VK_K, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ScrollDown, KeyEvent.VK_J, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ScrollLeft, KeyEvent.VK_H, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ScrollRight, KeyEvent.VK_L, shift),
            DesktopReaderKeyBinding(DesktopReaderInputAction.PageScrollUp, KeyEvent.VK_UP, shift),
            DesktopReaderKeyBinding(DesktopReaderInputAction.PageScrollDown, KeyEvent.VK_DOWN, shift),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ToggleFullScreen, KeyEvent.VK_F11, plain),
            DesktopReaderKeyBinding(DesktopReaderInputAction.ToggleFullScreen, KeyEvent.VK_F, macFullScreen),
        )
    }

    fun defaultMouseButtonBindings(): List<DesktopReaderMouseButtonBinding> {
        val plain = DesktopReaderShortcutModifiers()
        val shift = DesktopReaderShortcutModifiers(shift = true)

        return listOf(
            DesktopReaderMouseButtonBinding(
                action = DesktopReaderInputAction.PreviousPage,
                button = BackMouseButton,
                modifiers = plain,
            ),
            DesktopReaderMouseButtonBinding(
                action = DesktopReaderInputAction.NextPage,
                button = ForwardMouseButton,
                modifiers = plain,
            ),
            DesktopReaderMouseButtonBinding(
                action = DesktopReaderInputAction.PreviousChapter,
                button = BackMouseButton,
                modifiers = shift,
            ),
            DesktopReaderMouseButtonBinding(
                action = DesktopReaderInputAction.NextChapter,
                button = ForwardMouseButton,
                modifiers = shift,
            ),
            DesktopReaderMouseButtonBinding(
                action = DesktopReaderInputAction.ToggleToolbar,
                button = MouseEvent.BUTTON2,
                modifiers = plain,
            ),
            DesktopReaderMouseButtonBinding(
                action = DesktopReaderInputAction.ToggleFitWidthOrScreen,
                button = MouseEvent.BUTTON1,
                modifiers = plain,
                clickCount = 2,
            ),
        )
    }

    fun shortcutHelpLines(
        scheme: DesktopReaderInputScheme = defaultScheme(),
        menuShortcutLabel: String,
    ): List<DesktopReaderShortcutHelpLine> {
        return DesktopReaderInputAction.entries.mapNotNull { action ->
            val labels = scheme.keyBindings
                .filter { it.action == action }
                .map { it.label(menuShortcutLabel) }
                .distinct()
            if (labels.isEmpty()) {
                null
            } else {
                DesktopReaderShortcutHelpLine(
                    category = action.category,
                    action = action,
                    title = action.defaultTitle,
                    shortcuts = labels,
                )
            }
        }
    }
}

fun DesktopReaderKeyBinding.label(menuShortcutLabel: String): String {
    return buildList {
        if (modifiers.menuShortcut) add(menuShortcutLabel)
        if (modifiers.ctrl) add("Ctrl")
        if (modifiers.meta) add("Meta")
        if (modifiers.alt) add("Alt")
        if (modifiers.shift) add("Shift")
        add(keyCode.readerKeyLabel())
    }.joinToString("+")
}

fun DesktopReaderMouseButtonBinding.label(): String {
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
        if (modifiers.ctrl) add("Ctrl")
        if (modifiers.meta) add("Meta")
        if (modifiers.alt) add("Alt")
        if (modifiers.shift) add("Shift")
        add(clickLabel)
    }.joinToString("+")
}

fun Int.readerKeyLabel(): String {
    return when (this) {
        KeyEvent.VK_LEFT -> "Left"
        KeyEvent.VK_RIGHT -> "Right"
        KeyEvent.VK_UP -> "Up"
        KeyEvent.VK_DOWN -> "Down"
        KeyEvent.VK_PAGE_UP -> "Page Up"
        KeyEvent.VK_PAGE_DOWN -> "Page Down"
        KeyEvent.VK_HOME -> "Home"
        KeyEvent.VK_END -> "End"
        KeyEvent.VK_ENTER -> "Enter"
        KeyEvent.VK_SPACE -> "Space"
        KeyEvent.VK_COMMA -> "Comma"
        KeyEvent.VK_F11 -> "F11"
        KeyEvent.VK_EQUALS -> "+"
        KeyEvent.VK_PLUS -> "+"
        KeyEvent.VK_ADD -> "Num +"
        KeyEvent.VK_MINUS -> "-"
        KeyEvent.VK_SUBTRACT -> "Num -"
        else -> KeyEvent.getKeyText(this)
    }
}
