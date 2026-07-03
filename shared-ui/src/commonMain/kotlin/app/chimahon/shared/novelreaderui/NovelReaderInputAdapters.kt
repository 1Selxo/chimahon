package app.chimahon.shared.novelreaderui

import app.chimahon.shared.novelinputui.NovelInputActionId
import app.chimahon.shared.novelinputui.NovelInputDevice
import app.chimahon.shared.novelinputui.NovelInputPlatform
import app.chimahon.shared.novelinputui.NovelInputProfile
import app.chimahon.shared.novelinputui.NovelMouseWheelBehavior
import app.chimahon.shared.novelinputui.NovelReaderViewportMode
import app.chimahon.shared.novelinputui.NovelReadingFlow

fun NovelInputProfile.toNovelReaderDesktopControlsState(
    base: NovelReaderDesktopControlsState = NovelReaderDesktopControlsState(),
): NovelReaderDesktopControlsState {
    return base.copy(
        enabled = platform != NovelInputPlatform.Mobile,
        keyboardEnabled = keyboardShortcuts.isNotEmpty(),
        mouseWheelEnabled = mouse.wheelBehavior != NovelMouseWheelBehavior.Disabled,
        wheelBehavior = mouse.wheelBehavior.toNovelReaderWheelBehavior(),
        invertWheelDirection = mouse.invertWheelDirection,
    )
}

fun NovelInputProfile.toNovelReaderInputHints(): List<NovelReaderInputHint> {
    return inputHints
        .filter { it.enabled }
        .map { hint ->
            NovelReaderInputHint(
                label = hint.title,
                shortcut = listOf(hint.shortcut, hint.detail)
                    .filter { it.isNotBlank() }
                    .joinToString(" - "),
                kind = hint.device.toNovelReaderInputKind(),
            )
        }
}

fun NovelInputProfile.applyToNovelReaderState(
    state: NovelReaderUiState,
): NovelReaderUiState {
    return state.copy(
        readingMode = pageScrollMode.mode.toNovelReadingMode(),
        readingDirection = pageScrollMode.readingFlow.toNovelReadingDirection(),
        layout = state.layout.copy(
            verticalWriting = verticalWriting.enabled,
            hideFurigana = !verticalWriting.showRubyText,
        ),
        desktopControls = toNovelReaderDesktopControlsState(state.desktopControls),
        inputHints = toNovelReaderInputHints(),
    )
}

fun NovelInputActionId.toNovelReaderInputIntent(): NovelReaderInputIntent? {
    return when (this) {
        NovelInputActionId.PreviousPage -> NovelReaderInputIntent.PreviousPage
        NovelInputActionId.NextPage -> NovelReaderInputIntent.NextPage
        NovelInputActionId.FirstPage -> NovelReaderInputIntent.FirstPage
        NovelInputActionId.LastPage -> NovelReaderInputIntent.LastPage
        NovelInputActionId.PreviousChapter -> NovelReaderInputIntent.PreviousChapter
        NovelInputActionId.NextChapter -> NovelReaderInputIntent.NextChapter
        NovelInputActionId.ToggleHud -> NovelReaderInputIntent.ToggleHud
        NovelInputActionId.OpenChapters -> NovelReaderInputIntent.OpenChapters
        NovelInputActionId.OpenTypography -> NovelReaderInputIntent.OpenTypography
        NovelInputActionId.LookupSelection -> NovelReaderInputIntent.LookupSelection
        NovelInputActionId.DismissSelection -> NovelReaderInputIntent.ClearSelection
        NovelInputActionId.ToggleFullScreen -> NovelReaderInputIntent.ToggleFullScreen
        NovelInputActionId.TogglePageScrollMode,
        NovelInputActionId.ToggleVerticalWriting,
        NovelInputActionId.OpenInputHelp,
        NovelInputActionId.LookupClipboard,
        NovelInputActionId.LookupHoveredText,
        NovelInputActionId.CopySelection,
        NovelInputActionId.SearchSelection,
        NovelInputActionId.AddSelectionToAnki,
        NovelInputActionId.IncreaseFontSize,
        NovelInputActionId.DecreaseFontSize,
        -> null
    }
}

fun NovelReaderInputIntent.toNovelInputActionId(): NovelInputActionId? {
    return when (this) {
        NovelReaderInputIntent.PreviousPage -> NovelInputActionId.PreviousPage
        NovelReaderInputIntent.NextPage -> NovelInputActionId.NextPage
        NovelReaderInputIntent.FirstPage -> NovelInputActionId.FirstPage
        NovelReaderInputIntent.LastPage -> NovelInputActionId.LastPage
        NovelReaderInputIntent.PreviousChapter -> NovelInputActionId.PreviousChapter
        NovelReaderInputIntent.NextChapter -> NovelInputActionId.NextChapter
        NovelReaderInputIntent.ToggleHud -> NovelInputActionId.ToggleHud
        NovelReaderInputIntent.OpenChapters,
        NovelReaderInputIntent.CloseChapters,
        -> NovelInputActionId.OpenChapters
        NovelReaderInputIntent.OpenTypography,
        NovelReaderInputIntent.CloseTypography,
        -> NovelInputActionId.OpenTypography
        NovelReaderInputIntent.LookupSelection -> NovelInputActionId.LookupSelection
        NovelReaderInputIntent.ClearSelection -> NovelInputActionId.DismissSelection
        NovelReaderInputIntent.ToggleFullScreen -> NovelInputActionId.ToggleFullScreen
    }
}

private fun NovelReaderViewportMode.toNovelReadingMode(): NovelReadingMode {
    return when (this) {
        NovelReaderViewportMode.Paged -> NovelReadingMode.Paged
        NovelReaderViewportMode.ContinuousScroll -> NovelReadingMode.Continuous
    }
}

private fun NovelReadingFlow.toNovelReadingDirection(): NovelReadingDirection {
    return when (this) {
        NovelReadingFlow.HorizontalLeftToRight -> NovelReadingDirection.LeftToRight
        NovelReadingFlow.HorizontalRightToLeft -> NovelReadingDirection.RightToLeft
        NovelReadingFlow.VerticalRightToLeft -> NovelReadingDirection.VerticalRightToLeft
        NovelReadingFlow.VerticalLeftToRight -> NovelReadingDirection.LeftToRight
    }
}

private fun NovelMouseWheelBehavior.toNovelReaderWheelBehavior(): NovelReaderWheelBehavior {
    return when (this) {
        NovelMouseWheelBehavior.ScrollContent -> NovelReaderWheelBehavior.ScrollContent
        NovelMouseWheelBehavior.TurnPage -> NovelReaderWheelBehavior.TurnPage
        NovelMouseWheelBehavior.ChangeChapter -> NovelReaderWheelBehavior.ChangeChapter
        NovelMouseWheelBehavior.AdjustFontSize -> NovelReaderWheelBehavior.AdjustFontSize
        NovelMouseWheelBehavior.Disabled -> NovelReaderWheelBehavior.Disabled
    }
}

private fun NovelInputDevice.toNovelReaderInputKind(): NovelReaderInputKind {
    return when (this) {
        NovelInputDevice.Keyboard -> NovelReaderInputKind.Keyboard
        NovelInputDevice.Mouse,
        NovelInputDevice.Touchpad,
        -> NovelReaderInputKind.Mouse
        NovelInputDevice.Touch -> NovelReaderInputKind.Touch
    }
}
