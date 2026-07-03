package app.chimahon.shared.readerinputui

import app.chimahon.shared.reader.actions.ChimahonReaderActionDescriptor
import app.chimahon.shared.reader.actions.ChimahonReaderActionId
import app.chimahon.shared.reader.actions.ChimahonReaderActionShortcut
import app.chimahon.shared.reader.actions.ChimahonReaderShortcutKey

enum class ChimahonReaderInputBridgeTarget {
    ReaderCore,
    PageActions,
    Chrome,
    Display,
    Zoom,
    Chapter,
    External,
}

data class ChimahonReaderInputResolvedAction(
    val inputAction: ChimahonReaderInputActionId,
    val target: ChimahonReaderInputBridgeTarget,
    val readerAction: ChimahonReaderActionId? = null,
    val title: String = inputAction.title,
    val shortcutLabel: String? = null,
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val unavailableReason: String? = null,
) {
    val canDispatch: Boolean
        get() = enabled && unavailableReason == null
}

data class ChimahonReaderInputBridgeState(
    val inputState: ChimahonReaderInputUiState,
    val pageActions: List<ChimahonReaderActionDescriptor> = emptyList(),
    val menuItems: List<ChimahonReaderInputMenuItem> = inputState.contextMenuItems,
) {
    private val descriptorsById: Map<ChimahonReaderActionId, ChimahonReaderActionDescriptor> =
        pageActions.associateBy(ChimahonReaderActionDescriptor::id)
    private val menuItemsByAction: Map<ChimahonReaderInputActionId, ChimahonReaderInputMenuItem> =
        menuItems.associateBy(ChimahonReaderInputMenuItem::action)

    fun resolve(dispatch: ChimahonReaderInputDispatch): ChimahonReaderInputResolvedAction? {
        val action = dispatch.action ?: return null
        return resolve(action)
    }

    fun resolve(action: ChimahonReaderInputActionId): ChimahonReaderInputResolvedAction {
        val bridgeTarget = action.bridgeTarget
        val readerAction = action.toReaderActionIdOrNull()
        val pageDescriptor = readerAction?.let(descriptorsById::get)
        val menuItem = menuItemsByAction[action]
        return ChimahonReaderInputResolvedAction(
            inputAction = action,
            target = bridgeTarget,
            readerAction = readerAction,
            title = pageDescriptor?.title ?: menuItem?.label ?: action.title,
            shortcutLabel = menuItem?.shortcutLabel ?: pageDescriptor?.shortcut?.label,
            enabled = pageDescriptor?.enabled ?: menuItem?.enabled ?: true,
            selected = pageDescriptor?.active ?: menuItem?.selected ?: false,
            unavailableReason = pageDescriptor?.unavailableReason,
        )
    }

    fun resolvedMenuItems(): List<ChimahonReaderInputResolvedAction> {
        return menuItems
            .filter(ChimahonReaderInputMenuItem::visible)
            .map { item -> resolve(item.action) }
    }
}

val ChimahonReaderInputActionId.bridgeTarget: ChimahonReaderInputBridgeTarget
    get() = when (category) {
        ChimahonReaderInputActionCategory.Navigation -> ChimahonReaderInputBridgeTarget.ReaderCore
        ChimahonReaderInputActionCategory.Chapter -> ChimahonReaderInputBridgeTarget.Chapter
        ChimahonReaderInputActionCategory.Hud -> ChimahonReaderInputBridgeTarget.Chrome
        ChimahonReaderInputActionCategory.Display -> ChimahonReaderInputBridgeTarget.Display
        ChimahonReaderInputActionCategory.Zoom -> ChimahonReaderInputBridgeTarget.Zoom
        ChimahonReaderInputActionCategory.ChapterAction -> ChimahonReaderInputBridgeTarget.PageActions
        ChimahonReaderInputActionCategory.System -> ChimahonReaderInputBridgeTarget.ReaderCore
    }

fun ChimahonReaderInputActionId.toReaderActionIdOrNull(): ChimahonReaderActionId? {
    return when (this) {
        ChimahonReaderInputActionId.ToggleOcrLookup -> ChimahonReaderActionId.ToggleOcrLookup
        ChimahonReaderInputActionId.ResetZoom -> ChimahonReaderActionId.ResetView
        else -> null
    }
}

fun ChimahonReaderActionDescriptor.toReaderInputMenuItem(
    idPrefix: String = "reader.page",
): ChimahonReaderInputMenuItem {
    val inputAction = id.toReaderInputActionIdOrNull()
        ?: ChimahonReaderInputActionId.OpenSettings
    return ChimahonReaderInputMenuItem(
        id = "$idPrefix.${id.name}",
        action = inputAction,
        label = title,
        contentDescription = subtitle ?: title,
        shortcutLabel = shortcut?.label,
        enabled = enabled,
        selected = active,
        visible = true,
    )
}

fun ChimahonReaderActionId.toReaderInputActionIdOrNull(): ChimahonReaderInputActionId? {
    return when (this) {
        ChimahonReaderActionId.ToggleOcrLookup -> ChimahonReaderInputActionId.ToggleOcrLookup
        ChimahonReaderActionId.ResetView -> ChimahonReaderInputActionId.ResetZoom
        else -> null
    }
}

private val ChimahonReaderActionShortcut.label: String
    get() = buildList {
        if (primary) add("Ctrl/Cmd")
        if (alt) add("Alt")
        if (shift) add("Shift")
        add(key.toReaderInputShortcutLabel())
    }.joinToString("+")

private fun ChimahonReaderShortcutKey.toReaderInputShortcutLabel(): String {
    return when (this) {
        ChimahonReaderShortcutKey.A -> "A"
        ChimahonReaderShortcutKey.B -> "B"
        ChimahonReaderShortcutKey.C -> "C"
        ChimahonReaderShortcutKey.G -> "G"
        ChimahonReaderShortcutKey.I -> "I"
        ChimahonReaderShortcutKey.L -> "L"
        ChimahonReaderShortcutKey.O -> "O"
        ChimahonReaderShortcutKey.R -> "R"
        ChimahonReaderShortcutKey.S -> "S"
    }
}
