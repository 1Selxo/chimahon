package app.chimahon.shared.reader.actions

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

fun ChimahonReaderPageActionsState.shortcutActionDescriptors(
    autoScrollState: ChimahonReaderAutoScrollState? = null,
    includePageActions: Boolean = true,
    includeExtraActions: Boolean = true,
): List<ChimahonReaderActionDescriptor> = buildList {
    if (includePageActions) {
        addAll(pageActionDescriptors())
    }
    if (includeExtraActions && autoScrollState != null) {
        addAll(extraActionDescriptors(autoScrollState, includeHelp = false))
    } else if (includeExtraActions) {
        add(retryAllDescriptor())
    }
}.distinctBy { it.id }

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.chimahonReaderActionShortcuts(
    actions: List<ChimahonReaderActionDescriptor>,
    enabled: Boolean = true,
    onAction: (ChimahonReaderActionId) -> Unit,
): Modifier {
    if (!enabled) return this

    return onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        val action = actions.firstOrNull { action ->
            action.enabled && action.shortcut?.let(event::matchesShortcut) == true
        } ?: return@onPreviewKeyEvent false

        onAction(action.id)
        true
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.chimahonReaderActionShortcuts(
    state: ChimahonReaderPageActionsState,
    autoScrollState: ChimahonReaderAutoScrollState? = null,
    enabled: Boolean = true,
    includePageActions: Boolean = true,
    includeExtraActions: Boolean = true,
    onAction: (ChimahonReaderActionId) -> Unit,
): Modifier {
    return chimahonReaderActionShortcuts(
        actions = state.shortcutActionDescriptors(
            autoScrollState = autoScrollState,
            includePageActions = includePageActions,
            includeExtraActions = includeExtraActions,
        ),
        enabled = enabled,
        onAction = onAction,
    )
}

private fun KeyEvent.matchesShortcut(shortcut: ChimahonReaderActionShortcut): Boolean {
    val primaryPressed = isCtrlPressed || isMetaPressed
    return key == shortcut.key.toComposeKey() &&
        primaryPressed == shortcut.primary &&
        isShiftPressed == shortcut.shift &&
        isAltPressed == shortcut.alt
}

private fun ChimahonReaderShortcutKey.toComposeKey(): Key {
    return when (this) {
        ChimahonReaderShortcutKey.A -> Key.A
        ChimahonReaderShortcutKey.B -> Key.B
        ChimahonReaderShortcutKey.C -> Key.C
        ChimahonReaderShortcutKey.G -> Key.G
        ChimahonReaderShortcutKey.I -> Key.I
        ChimahonReaderShortcutKey.L -> Key.L
        ChimahonReaderShortcutKey.O -> Key.O
        ChimahonReaderShortcutKey.R -> Key.R
        ChimahonReaderShortcutKey.S -> Key.S
    }
}
