package app.chimahon.shared.readerocrui

enum class ChimahonReaderOcrInputKind {
    Keyboard,
    MouseButton,
    MouseWheel,
    Pointer,
    Touch,
}

data class ChimahonReaderOcrShortcutModifiers(
    val menuShortcut: Boolean = false,
    val ctrl: Boolean = false,
    val alt: Boolean = false,
    val shift: Boolean = false,
    val meta: Boolean = false,
) {
    val hasAny: Boolean
        get() = menuShortcut || ctrl || alt || shift || meta

    fun label(
        menuShortcutLabel: String = "Ctrl",
    ): String {
        return buildList {
            if (menuShortcut) add(menuShortcutLabel)
            if (ctrl) add("Ctrl")
            if (alt) add("Alt")
            if (shift) add("Shift")
            if (meta) add("Meta")
        }.joinToString(separator = "+")
    }
}

data class ChimahonReaderOcrKeyboardShortcut(
    val action: ChimahonReaderOcrBoxActionId,
    val key: String,
    val modifiers: ChimahonReaderOcrShortcutModifiers = ChimahonReaderOcrShortcutModifiers(),
    val repeatable: Boolean = true,
    val consumesWhenLookupPanelOpen: Boolean = true,
) {
    fun label(
        menuShortcutLabel: String = "Ctrl",
    ): String {
        val modifierLabel = modifiers.label(menuShortcutLabel)
        return listOf(modifierLabel, key)
            .filter { it.isNotBlank() }
            .joinToString(separator = "+")
    }
}

enum class ChimahonReaderOcrPointerButton {
    Primary,
    Secondary,
    Middle,
    Back,
    Forward,
}

enum class ChimahonReaderOcrPointerGesture {
    HoverBox,
    PressBox,
    TapBox,
    DoubleTapBox,
    ContextMenu,
    DragSelect,
    WheelNavigateBoxes,
    WheelScrollLookupPanel,
    WheelZoomPage,
}

data class ChimahonReaderOcrPointerBinding(
    val gesture: ChimahonReaderOcrPointerGesture,
    val action: ChimahonReaderOcrBoxActionId? = null,
    val button: ChimahonReaderOcrPointerButton? = null,
    val modifiers: ChimahonReaderOcrShortcutModifiers = ChimahonReaderOcrShortcutModifiers(),
    val description: String,
)

data class ChimahonReaderOcrInputAffordanceState(
    val shortcuts: List<ChimahonReaderOcrKeyboardShortcut> = defaultReaderOcrKeyboardShortcuts(),
    val pointerBindings: List<ChimahonReaderOcrPointerBinding> = defaultReaderOcrPointerBindings(),
    val focusTrappedInLookupPanel: Boolean = false,
    val rangeSelectionEnabled: Boolean = true,
    val hoverPreviewEnabled: Boolean = true,
    val wheelNavigatesBoxesWhenPanelFocused: Boolean = true,
) {
    fun shortcutsFor(action: ChimahonReaderOcrBoxActionId): List<ChimahonReaderOcrKeyboardShortcut> {
        return shortcuts.filter { it.action == action }
    }

    fun shortcutLabelFor(
        action: ChimahonReaderOcrBoxActionId,
        menuShortcutLabel: String = "Ctrl",
    ): String? {
        return shortcutsFor(action).firstOrNull()?.label(menuShortcutLabel)
    }
}

data class ChimahonReaderOcrDesktopHint(
    val inputKind: ChimahonReaderOcrInputKind,
    val label: String,
    val description: String,
    val action: ChimahonReaderOcrBoxActionId? = null,
)

fun defaultReaderOcrDesktopHints(
    affordances: ChimahonReaderOcrInputAffordanceState = ChimahonReaderOcrInputAffordanceState(),
    menuShortcutLabel: String = "Ctrl",
): List<ChimahonReaderOcrDesktopHint> {
    return buildList {
        affordances.shortcuts.forEach { shortcut ->
            add(
                ChimahonReaderOcrDesktopHint(
                    inputKind = ChimahonReaderOcrInputKind.Keyboard,
                    label = shortcut.label(menuShortcutLabel),
                    description = shortcut.action.contentDescription,
                    action = shortcut.action,
                ),
            )
        }
        affordances.pointerBindings.forEach { binding ->
            add(
                ChimahonReaderOcrDesktopHint(
                    inputKind = binding.gesture.inputKind,
                    label = binding.gesture.label,
                    description = binding.description,
                    action = binding.action,
                ),
            )
        }
    }
}

fun defaultReaderOcrKeyboardShortcuts(): List<ChimahonReaderOcrKeyboardShortcut> {
    val plain = ChimahonReaderOcrShortcutModifiers()
    val shift = ChimahonReaderOcrShortcutModifiers(shift = true)
    val menu = ChimahonReaderOcrShortcutModifiers(menuShortcut = true)
    val menuAlt = ChimahonReaderOcrShortcutModifiers(menuShortcut = true, alt = true)
    return listOf(
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.Lookup,
            key = "Enter",
            modifiers = plain,
        ),
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.CopyText,
            key = "C",
            modifiers = menu,
            repeatable = false,
        ),
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.SearchWeb,
            key = "F",
            modifiers = menu,
            repeatable = false,
        ),
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.Translate,
            key = "T",
            modifiers = plain,
            repeatable = false,
        ),
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.OpenDictionary,
            key = "D",
            modifiers = plain,
            repeatable = false,
        ),
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.PreviousBox,
            key = "Tab",
            modifiers = shift,
        ),
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.NextBox,
            key = "Tab",
            modifiers = plain,
        ),
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.PreviousBox,
            key = "Left",
            modifiers = menuAlt,
        ),
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.NextBox,
            key = "Right",
            modifiers = menuAlt,
        ),
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.ToggleBoxes,
            key = "B",
            modifiers = plain,
            repeatable = false,
        ),
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.Rescan,
            key = "R",
            modifiers = plain,
            repeatable = false,
        ),
        ChimahonReaderOcrKeyboardShortcut(
            action = ChimahonReaderOcrBoxActionId.Close,
            key = "Escape",
            modifiers = plain,
            repeatable = false,
        ),
    )
}

fun defaultReaderOcrPointerBindings(): List<ChimahonReaderOcrPointerBinding> {
    val plain = ChimahonReaderOcrShortcutModifiers()
    val shift = ChimahonReaderOcrShortcutModifiers(shift = true)
    val menu = ChimahonReaderOcrShortcutModifiers(menuShortcut = true)
    return listOf(
        ChimahonReaderOcrPointerBinding(
            gesture = ChimahonReaderOcrPointerGesture.HoverBox,
            button = null,
            modifiers = plain,
            description = "Preview OCR box focus",
        ),
        ChimahonReaderOcrPointerBinding(
            gesture = ChimahonReaderOcrPointerGesture.TapBox,
            action = ChimahonReaderOcrBoxActionId.Lookup,
            button = ChimahonReaderOcrPointerButton.Primary,
            modifiers = plain,
            description = "Select and look up OCR text",
        ),
        ChimahonReaderOcrPointerBinding(
            gesture = ChimahonReaderOcrPointerGesture.TapBox,
            action = ChimahonReaderOcrBoxActionId.AddToSelection,
            button = ChimahonReaderOcrPointerButton.Primary,
            modifiers = shift,
            description = "Extend OCR box selection range",
        ),
        ChimahonReaderOcrPointerBinding(
            gesture = ChimahonReaderOcrPointerGesture.TapBox,
            action = ChimahonReaderOcrBoxActionId.AddToSelection,
            button = ChimahonReaderOcrPointerButton.Primary,
            modifiers = menu,
            description = "Toggle OCR box in multi-selection",
        ),
        ChimahonReaderOcrPointerBinding(
            gesture = ChimahonReaderOcrPointerGesture.DoubleTapBox,
            action = ChimahonReaderOcrBoxActionId.OpenDictionary,
            button = ChimahonReaderOcrPointerButton.Primary,
            modifiers = plain,
            description = "Open dictionary for OCR text",
        ),
        ChimahonReaderOcrPointerBinding(
            gesture = ChimahonReaderOcrPointerGesture.ContextMenu,
            button = ChimahonReaderOcrPointerButton.Secondary,
            modifiers = plain,
            description = "Open OCR box action menu",
        ),
        ChimahonReaderOcrPointerBinding(
            gesture = ChimahonReaderOcrPointerGesture.WheelNavigateBoxes,
            action = ChimahonReaderOcrBoxActionId.NextBox,
            modifiers = menu,
            description = "Move between OCR boxes while lookup is focused",
        ),
    )
}

private val ChimahonReaderOcrPointerGesture.inputKind: ChimahonReaderOcrInputKind
    get() = when (this) {
        ChimahonReaderOcrPointerGesture.HoverBox,
        ChimahonReaderOcrPointerGesture.PressBox,
        ChimahonReaderOcrPointerGesture.TapBox,
        ChimahonReaderOcrPointerGesture.DoubleTapBox,
        ChimahonReaderOcrPointerGesture.ContextMenu,
        ChimahonReaderOcrPointerGesture.DragSelect,
        -> ChimahonReaderOcrInputKind.Pointer
        ChimahonReaderOcrPointerGesture.WheelNavigateBoxes,
        ChimahonReaderOcrPointerGesture.WheelScrollLookupPanel,
        ChimahonReaderOcrPointerGesture.WheelZoomPage,
        -> ChimahonReaderOcrInputKind.MouseWheel
    }

private val ChimahonReaderOcrPointerGesture.label: String
    get() = when (this) {
        ChimahonReaderOcrPointerGesture.HoverBox -> "Hover"
        ChimahonReaderOcrPointerGesture.PressBox -> "Press"
        ChimahonReaderOcrPointerGesture.TapBox -> "Click"
        ChimahonReaderOcrPointerGesture.DoubleTapBox -> "Double click"
        ChimahonReaderOcrPointerGesture.ContextMenu -> "Right click"
        ChimahonReaderOcrPointerGesture.DragSelect -> "Drag"
        ChimahonReaderOcrPointerGesture.WheelNavigateBoxes -> "Wheel"
        ChimahonReaderOcrPointerGesture.WheelScrollLookupPanel -> "Wheel"
        ChimahonReaderOcrPointerGesture.WheelZoomPage -> "Wheel"
    }
