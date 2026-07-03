package app.chimahon.shared.novelinputui

enum class NovelInputPlatform(val title: String) {
    Shared("Shared"),
    Mobile("Mobile"),
    Desktop("Desktop"),
}

enum class NovelInputDevice(val title: String) {
    Keyboard("Keyboard"),
    Mouse("Mouse"),
    Touch("Touch"),
    Touchpad("Touchpad"),
}

enum class NovelInputScope(val title: String) {
    Reader("Reader"),
    Lookup("Lookup"),
    Selection("Selection"),
    Appearance("Appearance"),
}

enum class NovelInputActionCategory(val title: String) {
    Navigation("Navigation"),
    ReaderMode("Reader mode"),
    Lookup("Lookup"),
    Selection("Selection"),
    Appearance("Appearance"),
}

enum class NovelInputActionId(
    val title: String,
    val category: NovelInputActionCategory,
) {
    PreviousPage("Previous page", NovelInputActionCategory.Navigation),
    NextPage("Next page", NovelInputActionCategory.Navigation),
    PreviousChapter("Previous chapter", NovelInputActionCategory.Navigation),
    NextChapter("Next chapter", NovelInputActionCategory.Navigation),
    ToggleHud("Toggle controls", NovelInputActionCategory.ReaderMode),
    OpenChapters("Open chapters", NovelInputActionCategory.ReaderMode),
    TogglePageScrollMode("Toggle page/scroll mode", NovelInputActionCategory.ReaderMode),
    ToggleVerticalWriting("Toggle vertical writing", NovelInputActionCategory.ReaderMode),
    OpenInputHelp("Open input help", NovelInputActionCategory.ReaderMode),
    LookupSelection("Lookup selection", NovelInputActionCategory.Lookup),
    LookupClipboard("Lookup clipboard", NovelInputActionCategory.Lookup),
    LookupHoveredText("Lookup hovered text", NovelInputActionCategory.Lookup),
    CopySelection("Copy selection", NovelInputActionCategory.Selection),
    SearchSelection("Search selection", NovelInputActionCategory.Selection),
    AddSelectionToAnki("Add selection to Anki", NovelInputActionCategory.Selection),
    DismissSelection("Dismiss selection", NovelInputActionCategory.Selection),
    IncreaseFontSize("Increase font size", NovelInputActionCategory.Appearance),
    DecreaseFontSize("Decrease font size", NovelInputActionCategory.Appearance),
    FirstPage("First page", NovelInputActionCategory.Navigation),
    LastPage("Last page", NovelInputActionCategory.Navigation),
    OpenTypography("Open appearance", NovelInputActionCategory.Appearance),
    ToggleFullScreen("Toggle full screen", NovelInputActionCategory.ReaderMode),
}

enum class NovelReadingFlow(val title: String) {
    HorizontalLeftToRight("Horizontal left to right"),
    HorizontalRightToLeft("Horizontal right to left"),
    VerticalRightToLeft("Vertical right to left"),
    VerticalLeftToRight("Vertical left to right"),
}

val NovelReadingFlow.vertical: Boolean
    get() = this == NovelReadingFlow.VerticalRightToLeft || this == NovelReadingFlow.VerticalLeftToRight

val NovelReadingFlow.reversesPageEdges: Boolean
    get() = this == NovelReadingFlow.HorizontalRightToLeft || this == NovelReadingFlow.VerticalRightToLeft

enum class NovelReaderViewportMode(val title: String) {
    Paged("Paged"),
    ContinuousScroll("Scroll"),
}

enum class NovelPageTurnBehavior(val title: String) {
    TapZonesAndKeys("Tap zones and keys"),
    KeysOnly("Keys only"),
    GesturesOnly("Gestures only"),
}

data class NovelPageScrollModeState(
    val mode: NovelReaderViewportMode = NovelReaderViewportMode.Paged,
    val readingFlow: NovelReadingFlow = NovelReadingFlow.HorizontalLeftToRight,
    val pageTurnBehavior: NovelPageTurnBehavior = NovelPageTurnBehavior.TapZonesAndKeys,
    val keepTapZonesInScrollMode: Boolean = true,
    val showProgressScrubber: Boolean = true,
) {
    val modeLabel: String
        get() = mode.title

    val flowLabel: String
        get() = readingFlow.title
}

enum class NovelTapZoneId(val title: String) {
    TopEdge("Top"),
    BottomEdge("Bottom"),
    LeadingEdge("Leading"),
    Center("Center"),
    TrailingEdge("Trailing"),
}

data class NovelTapZoneAssignment(
    val zone: NovelTapZoneId,
    val action: NovelInputActionId?,
    val label: String = action?.title ?: "Disabled",
) {
    val enabled: Boolean
        get() = action != null
}

data class NovelTapZoneLayout(
    val leadingWidthPercent: Float = 24f,
    val trailingWidthPercent: Float = 24f,
    val verticalEdgeHeightPercent: Float = 12f,
    val reverseHorizontalZonesForRtl: Boolean = true,
    val assignments: List<NovelTapZoneAssignment> = NovelInputDefaults.defaultTapZoneAssignments(),
) {
    val centerWidthPercent: Float
        get() = (100f - leadingWidthPercent - trailingWidthPercent).coerceAtLeast(0f)

    fun assignmentFor(zone: NovelTapZoneId): NovelTapZoneAssignment {
        return assignments.firstOrNull { it.zone == zone }
            ?: NovelTapZoneAssignment(zone = zone, action = null)
    }

    fun actionForZone(
        zone: NovelTapZoneId,
        readingFlow: NovelReadingFlow = NovelReadingFlow.HorizontalLeftToRight,
    ): NovelInputActionId? {
        val logicalZone = if (reverseHorizontalZonesForRtl && readingFlow.reversesPageEdges) {
            zone.swapHorizontalEdge()
        } else {
            zone
        }
        return assignmentFor(logicalZone).action
    }

    fun zoneForTap(
        normalizedX: Float,
        normalizedY: Float,
    ): NovelTapZoneId {
        val x = normalizedX.coerceIn(0f, 1f) * 100f
        val y = normalizedY.coerceIn(0f, 1f) * 100f
        val verticalEdge = verticalEdgeHeightPercent.coerceIn(0f, 40f)
        return when {
            y <= verticalEdge -> NovelTapZoneId.TopEdge
            y >= 100f - verticalEdge -> NovelTapZoneId.BottomEdge
            x <= leadingWidthPercent.coerceIn(0f, 50f) -> NovelTapZoneId.LeadingEdge
            x >= 100f - trailingWidthPercent.coerceIn(0f, 50f) -> NovelTapZoneId.TrailingEdge
            else -> NovelTapZoneId.Center
        }
    }

    fun actionForTap(
        normalizedX: Float,
        normalizedY: Float,
        readingFlow: NovelReadingFlow = NovelReadingFlow.HorizontalLeftToRight,
    ): NovelInputActionId? {
        return actionForZone(
            zone = zoneForTap(normalizedX, normalizedY),
            readingFlow = readingFlow,
        )
    }
}

enum class NovelMouseWheelBehavior(val title: String, val detail: String) {
    ScrollContent("Scroll content", "Wheel and touchpad gestures scroll the current text surface."),
    TurnPage("Turn pages", "Wheel steps move to the previous or next page."),
    ChangeChapter("Change chapters", "Wheel steps jump across chapter boundaries."),
    AdjustFontSize("Adjust font size", "Wheel steps change reader text size."),
    Disabled("Disabled", "Wheel input is ignored by the reader."),
}

enum class NovelMouseButtonBehavior(val title: String) {
    Disabled("Disabled"),
    PageNavigation("Page navigation"),
    ChapterNavigation("Chapter navigation"),
    Lookup("Lookup"),
}

data class NovelMouseInputState(
    val wheelBehavior: NovelMouseWheelBehavior = NovelMouseWheelBehavior.ScrollContent,
    val primaryModifiedWheelBehavior: NovelMouseWheelBehavior = NovelMouseWheelBehavior.AdjustFontSize,
    val invertWheelDirection: Boolean = false,
    val sideButtonBehavior: NovelMouseButtonBehavior = NovelMouseButtonBehavior.PageNavigation,
) {
    val wheelSummary: String
        get() = if (invertWheelDirection) "${wheelBehavior.title}, inverted" else wheelBehavior.title
}

enum class NovelSelectionActivation(val title: String) {
    NativeSelection("Native selection"),
    SelectThenLookup("Select then lookup"),
    InstantLookup("Instant lookup"),
    Disabled("Disabled"),
}

data class NovelSelectionBehaviorState(
    val mouseDrag: NovelSelectionActivation = NovelSelectionActivation.NativeSelection,
    val touchLongPress: NovelSelectionActivation = NovelSelectionActivation.SelectThenLookup,
    val doubleClick: NovelSelectionActivation = NovelSelectionActivation.SelectThenLookup,
    val keepSelectionToolbarVisible: Boolean = true,
    val allowTextSelectionInPagedMode: Boolean = true,
) {
    val lookupOnSelection: Boolean
        get() = mouseDrag == NovelSelectionActivation.SelectThenLookup ||
            mouseDrag == NovelSelectionActivation.InstantLookup ||
            touchLongPress == NovelSelectionActivation.SelectThenLookup ||
            touchLongPress == NovelSelectionActivation.InstantLookup
}

enum class NovelLatinGlyphBehavior(val title: String) {
    Auto("Auto"),
    KeepUpright("Keep upright"),
    RotateSideways("Rotate sideways"),
}

data class NovelVerticalWritingState(
    val enabled: Boolean = false,
    val flow: NovelReadingFlow = NovelReadingFlow.VerticalRightToLeft,
    val tateChuYoko: Boolean = true,
    val latinGlyphBehavior: NovelLatinGlyphBehavior = NovelLatinGlyphBehavior.Auto,
    val showRubyText: Boolean = true,
    val compactPunctuation: Boolean = true,
    val mirrorTapZones: Boolean = true,
) {
    val activeFlow: NovelReadingFlow
        get() = if (enabled) flow else NovelReadingFlow.HorizontalLeftToRight

    val summary: String
        get() = if (enabled) flow.title else "Horizontal"
}

data class NovelShortcutChord(
    val key: String,
    val primary: Boolean = false,
    val shift: Boolean = false,
    val alt: Boolean = false,
    val meta: Boolean = false,
    val displayLabel: String = formatNovelShortcutChord(key, primary, shift, alt, meta),
) {
    val normalizedIdentity: String
        get() = normalizedNovelShortcutIdentity(key, primary, shift, alt, meta)
}

data class NovelKeyboardShortcut(
    val action: NovelInputActionId,
    val id: String = action.name,
    val title: String = action.title,
    val scope: NovelInputScope = action.defaultInputScope(),
    val chords: List<NovelShortcutChord> = action.defaultShortcutChords(),
    val description: String = action.defaultDescription(),
    val enabled: Boolean = true,
    val reservedBySystem: Boolean = false,
) {
    val shortcutLabel: String
        get() = chords.joinToString(" / ") { it.displayLabel }
}

enum class NovelLookupShortcutAction(
    val title: String,
    val inputAction: NovelInputActionId,
) {
    LookupSelection("Lookup selection", NovelInputActionId.LookupSelection),
    LookupClipboard("Lookup clipboard", NovelInputActionId.LookupClipboard),
    LookupHoveredText("Lookup hovered text", NovelInputActionId.LookupHoveredText),
    CopySelection("Copy selection", NovelInputActionId.CopySelection),
    SearchSelection("Search web", NovelInputActionId.SearchSelection),
    AddToAnki("Add to Anki", NovelInputActionId.AddSelectionToAnki),
    Dismiss("Dismiss", NovelInputActionId.DismissSelection),
}

data class NovelLookupShortcutRow(
    val action: NovelLookupShortcutAction,
    val title: String = action.title,
    val detail: String = action.inputAction.defaultDescription(),
    val shortcuts: List<NovelShortcutChord> = action.inputAction.defaultShortcutChords(),
    val enabled: Boolean = true,
) {
    val shortcutLabel: String
        get() = shortcuts.joinToString(" / ") { it.displayLabel }
}

enum class NovelInputHintStatus {
    Neutral,
    Active,
    Warning,
}

data class NovelInputHintRow(
    val title: String,
    val detail: String,
    val device: NovelInputDevice,
    val shortcut: String = "",
    val enabled: Boolean = true,
    val status: NovelInputHintStatus = NovelInputHintStatus.Neutral,
) {
    val hasShortcut: Boolean
        get() = shortcut.isNotBlank()
}

enum class NovelInputConflictSeverity(val title: String) {
    Info("Info"),
    Warning("Warning"),
    Error("Error"),
}

data class NovelInputConflictWarning(
    val id: String,
    val title: String,
    val message: String,
    val severity: NovelInputConflictSeverity = NovelInputConflictSeverity.Warning,
    val shortcutLabel: String? = null,
    val actionIds: List<NovelInputActionId> = emptyList(),
) {
    val blocking: Boolean
        get() = severity == NovelInputConflictSeverity.Error
}

data class NovelInputProfile(
    val platform: NovelInputPlatform = NovelInputPlatform.Shared,
    val pageScrollMode: NovelPageScrollModeState = NovelPageScrollModeState(),
    val tapZones: NovelTapZoneLayout = NovelTapZoneLayout(),
    val keyboardShortcuts: List<NovelKeyboardShortcut> = NovelInputDefaults.defaultKeyboardShortcuts(),
    val mouse: NovelMouseInputState = NovelMouseInputState(),
    val selection: NovelSelectionBehaviorState = NovelSelectionBehaviorState(),
    val verticalWriting: NovelVerticalWritingState = NovelVerticalWritingState(),
    val lookupShortcuts: List<NovelLookupShortcutRow> = NovelInputDefaults.defaultLookupShortcutRows(),
    val extraHints: List<NovelInputHintRow> = emptyList(),
) {
    val conflictWarnings: List<NovelInputConflictWarning>
        get() = detectNovelInputConflicts()

    val inputHints: List<NovelInputHintRow>
        get() = defaultNovelInputHints(this) + extraHints
}

data class NovelInputUiState(
    val title: String = "Light novel input",
    val subtitle: String = "Desktop and mobile parity",
    val profile: NovelInputProfile = NovelInputDefaults.desktopProfile(),
    val showConflictWarnings: Boolean = true,
) {
    val conflictWarnings: List<NovelInputConflictWarning>
        get() = profile.conflictWarnings
}

data class NovelInputUiActions(
    val onProfileChange: (NovelInputProfile) -> Unit = {},
    val onPageScrollModeChange: (NovelPageScrollModeState) -> Unit = {},
    val onTapZoneLayoutChange: (NovelTapZoneLayout) -> Unit = {},
    val onTapZoneSelected: (NovelTapZoneId) -> Unit = {},
    val onKeyboardShortcutSelected: (NovelKeyboardShortcut) -> Unit = {},
    val onMouseInputChange: (NovelMouseInputState) -> Unit = {},
    val onSelectionBehaviorChange: (NovelSelectionBehaviorState) -> Unit = {},
    val onVerticalWritingChange: (NovelVerticalWritingState) -> Unit = {},
    val onLookupShortcutSelected: (NovelLookupShortcutRow) -> Unit = {},
    val onConflictSelected: (NovelInputConflictWarning) -> Unit = {},
)

object NovelInputDefaults {
    fun desktopProfile(): NovelInputProfile {
        return NovelInputProfile(
            platform = NovelInputPlatform.Desktop,
            pageScrollMode = NovelPageScrollModeState(
                mode = NovelReaderViewportMode.Paged,
                readingFlow = NovelReadingFlow.HorizontalLeftToRight,
            ),
            keyboardShortcuts = defaultKeyboardShortcuts(),
            mouse = NovelMouseInputState(
                wheelBehavior = NovelMouseWheelBehavior.TurnPage,
                primaryModifiedWheelBehavior = NovelMouseWheelBehavior.AdjustFontSize,
                sideButtonBehavior = NovelMouseButtonBehavior.PageNavigation,
            ),
            selection = NovelSelectionBehaviorState(
                mouseDrag = NovelSelectionActivation.NativeSelection,
                touchLongPress = NovelSelectionActivation.SelectThenLookup,
            ),
        )
    }

    fun mobileProfile(): NovelInputProfile {
        return NovelInputProfile(
            platform = NovelInputPlatform.Mobile,
            pageScrollMode = NovelPageScrollModeState(
                mode = NovelReaderViewportMode.Paged,
                readingFlow = NovelReadingFlow.HorizontalLeftToRight,
                pageTurnBehavior = NovelPageTurnBehavior.GesturesOnly,
            ),
            keyboardShortcuts = emptyList(),
            mouse = NovelMouseInputState(
                wheelBehavior = NovelMouseWheelBehavior.Disabled,
                primaryModifiedWheelBehavior = NovelMouseWheelBehavior.Disabled,
                sideButtonBehavior = NovelMouseButtonBehavior.Disabled,
            ),
            selection = NovelSelectionBehaviorState(
                mouseDrag = NovelSelectionActivation.Disabled,
                touchLongPress = NovelSelectionActivation.SelectThenLookup,
                doubleClick = NovelSelectionActivation.Disabled,
            ),
        )
    }

    fun defaultKeyboardShortcuts(): List<NovelKeyboardShortcut> {
        return listOf(
            NovelKeyboardShortcut(NovelInputActionId.PreviousPage),
            NovelKeyboardShortcut(NovelInputActionId.NextPage),
            NovelKeyboardShortcut(NovelInputActionId.FirstPage),
            NovelKeyboardShortcut(NovelInputActionId.LastPage),
            NovelKeyboardShortcut(NovelInputActionId.PreviousChapter),
            NovelKeyboardShortcut(NovelInputActionId.NextChapter),
            NovelKeyboardShortcut(NovelInputActionId.ToggleHud),
            NovelKeyboardShortcut(NovelInputActionId.OpenChapters),
            NovelKeyboardShortcut(NovelInputActionId.OpenTypography),
            NovelKeyboardShortcut(NovelInputActionId.TogglePageScrollMode),
            NovelKeyboardShortcut(NovelInputActionId.ToggleVerticalWriting),
            NovelKeyboardShortcut(NovelInputActionId.ToggleFullScreen),
            NovelKeyboardShortcut(NovelInputActionId.OpenInputHelp),
            NovelKeyboardShortcut(NovelInputActionId.IncreaseFontSize),
            NovelKeyboardShortcut(NovelInputActionId.DecreaseFontSize),
        )
    }

    fun defaultLookupShortcutRows(): List<NovelLookupShortcutRow> {
        return NovelLookupShortcutAction.entries.map { action ->
            NovelLookupShortcutRow(action = action)
        }
    }

    fun defaultTapZoneAssignments(): List<NovelTapZoneAssignment> {
        return listOf(
            NovelTapZoneAssignment(NovelTapZoneId.TopEdge, NovelInputActionId.ToggleHud),
            NovelTapZoneAssignment(NovelTapZoneId.BottomEdge, NovelInputActionId.ToggleHud),
            NovelTapZoneAssignment(NovelTapZoneId.LeadingEdge, NovelInputActionId.PreviousPage),
            NovelTapZoneAssignment(NovelTapZoneId.Center, NovelInputActionId.ToggleHud),
            NovelTapZoneAssignment(NovelTapZoneId.TrailingEdge, NovelInputActionId.NextPage),
        )
    }
}

fun NovelInputProfile.withPageScrollMode(state: NovelPageScrollModeState): NovelInputProfile {
    return copy(pageScrollMode = state)
}

fun NovelInputProfile.withTapZones(layout: NovelTapZoneLayout): NovelInputProfile {
    return copy(tapZones = layout)
}

fun NovelInputProfile.withMouseInput(state: NovelMouseInputState): NovelInputProfile {
    return copy(mouse = state)
}

fun NovelInputProfile.withSelectionBehavior(state: NovelSelectionBehaviorState): NovelInputProfile {
    return copy(selection = state)
}

fun NovelInputProfile.withVerticalWriting(state: NovelVerticalWritingState): NovelInputProfile {
    val pageScroll = if (state.enabled) {
        pageScrollMode.copy(readingFlow = state.flow)
    } else if (pageScrollMode.readingFlow.vertical) {
        pageScrollMode.copy(readingFlow = NovelReadingFlow.HorizontalLeftToRight)
    } else {
        pageScrollMode
    }
    return copy(
        verticalWriting = state,
        pageScrollMode = pageScroll,
        tapZones = tapZones.copy(reverseHorizontalZonesForRtl = state.mirrorTapZones),
    )
}

fun NovelInputProfile.detectNovelInputConflicts(): List<NovelInputConflictWarning> {
    return buildList {
        addShortcutConflicts(keyboardShortcuts, lookupShortcuts)
        addTapZoneWarnings(tapZones, pageScrollMode)
        addMouseWarnings(mouse, pageScrollMode)
        addSelectionWarnings(selection, pageScrollMode)
        addVerticalWritingWarnings(verticalWriting, pageScrollMode)
    }
}

fun defaultNovelInputHints(profile: NovelInputProfile): List<NovelInputHintRow> {
    return buildList {
        add(
            NovelInputHintRow(
                title = "Tap zones",
                detail = "${profile.tapZones.leadingWidthPercent.asPercent()} / ${profile.tapZones.centerWidthPercent.asPercent()} / ${profile.tapZones.trailingWidthPercent.asPercent()}",
                device = NovelInputDevice.Touch,
                shortcut = profile.pageScrollMode.pageTurnBehavior.title,
                enabled = profile.pageScrollMode.keepTapZonesInScrollMode ||
                    profile.pageScrollMode.mode == NovelReaderViewportMode.Paged,
            ),
        )
        add(
            NovelInputHintRow(
                title = "Keyboard pages",
                detail = "Previous and next page keys stay aligned with tap zones.",
                device = NovelInputDevice.Keyboard,
                shortcut = shortcutLabelFor(profile.keyboardShortcuts, NovelInputActionId.PreviousPage, NovelInputActionId.NextPage),
                enabled = profile.keyboardShortcuts.isNotEmpty(),
            ),
        )
        add(
            NovelInputHintRow(
                title = "Jump keys",
                detail = "Home and End jump to the beginning or end of the loaded novel.",
                device = NovelInputDevice.Keyboard,
                shortcut = shortcutLabelFor(profile.keyboardShortcuts, NovelInputActionId.FirstPage, NovelInputActionId.LastPage),
                enabled = profile.keyboardShortcuts.isNotEmpty(),
            ),
        )
        add(
            NovelInputHintRow(
                title = "Mouse wheel",
                detail = profile.mouse.wheelBehavior.detail,
                device = NovelInputDevice.Mouse,
                shortcut = profile.mouse.wheelSummary,
                enabled = profile.mouse.wheelBehavior != NovelMouseWheelBehavior.Disabled,
            ),
        )
        add(
            NovelInputHintRow(
                title = "Text selection",
                detail = "Mouse: ${profile.selection.mouseDrag.title}; touch: ${profile.selection.touchLongPress.title}",
                device = if (profile.platform == NovelInputPlatform.Mobile) NovelInputDevice.Touch else NovelInputDevice.Mouse,
                shortcut = if (profile.selection.lookupOnSelection) "Lookup ready" else "Selection only",
                enabled = profile.selection.mouseDrag != NovelSelectionActivation.Disabled ||
                    profile.selection.touchLongPress != NovelSelectionActivation.Disabled,
            ),
        )
        add(
            NovelInputHintRow(
                title = "Vertical writing",
                detail = profile.verticalWriting.summary,
                device = NovelInputDevice.Touch,
                shortcut = shortcutLabelFor(profile.keyboardShortcuts, NovelInputActionId.ToggleVerticalWriting),
                enabled = profile.verticalWriting.enabled,
                status = if (profile.verticalWriting.enabled) NovelInputHintStatus.Active else NovelInputHintStatus.Neutral,
            ),
        )
    }
}

fun NovelInputActionId.defaultInputScope(): NovelInputScope {
    return when (category) {
        NovelInputActionCategory.Lookup -> NovelInputScope.Lookup
        NovelInputActionCategory.Selection -> NovelInputScope.Selection
        NovelInputActionCategory.Appearance -> NovelInputScope.Appearance
        NovelInputActionCategory.Navigation,
        NovelInputActionCategory.ReaderMode,
        -> NovelInputScope.Reader
    }
}

fun NovelInputActionId.defaultDescription(): String {
    return when (this) {
        NovelInputActionId.PreviousPage -> "Move to the previous reader page."
        NovelInputActionId.NextPage -> "Move to the next reader page."
        NovelInputActionId.FirstPage -> "Jump to the first loaded reader position."
        NovelInputActionId.LastPage -> "Jump to the last loaded reader position."
        NovelInputActionId.PreviousChapter -> "Move to the previous chapter."
        NovelInputActionId.NextChapter -> "Move to the next chapter."
        NovelInputActionId.ToggleHud -> "Show or hide reader controls."
        NovelInputActionId.OpenChapters -> "Open the chapter list."
        NovelInputActionId.OpenTypography -> "Open reader font, theme, and spacing controls."
        NovelInputActionId.TogglePageScrollMode -> "Switch between paged and continuous reading."
        NovelInputActionId.ToggleVerticalWriting -> "Enable or disable vertical text layout."
        NovelInputActionId.ToggleFullScreen -> "Request immersive full-screen reader chrome."
        NovelInputActionId.OpenInputHelp -> "Open the reader input reference."
        NovelInputActionId.LookupSelection -> "Look up the selected word or phrase."
        NovelInputActionId.LookupClipboard -> "Look up text currently on the clipboard."
        NovelInputActionId.LookupHoveredText -> "Look up the word under the pointer."
        NovelInputActionId.CopySelection -> "Copy selected reader text."
        NovelInputActionId.SearchSelection -> "Search selected text on the web."
        NovelInputActionId.AddSelectionToAnki -> "Create a card from the selected text."
        NovelInputActionId.DismissSelection -> "Close lookup or selection controls."
        NovelInputActionId.IncreaseFontSize -> "Increase reader text size."
        NovelInputActionId.DecreaseFontSize -> "Decrease reader text size."
    }
}

fun NovelInputActionId.defaultShortcutChords(): List<NovelShortcutChord> {
    return when (this) {
        NovelInputActionId.PreviousPage -> listOf(
            NovelShortcutChord("Left"),
            NovelShortcutChord("PageUp"),
        )
        NovelInputActionId.NextPage -> listOf(
            NovelShortcutChord("Right"),
            NovelShortcutChord("PageDown"),
            NovelShortcutChord("Space"),
        )
        NovelInputActionId.FirstPage -> listOf(NovelShortcutChord("Home"))
        NovelInputActionId.LastPage -> listOf(NovelShortcutChord("End"))
        NovelInputActionId.PreviousChapter -> listOf(
            NovelShortcutChord("Left", shift = true),
            NovelShortcutChord("PageUp", shift = true),
        )
        NovelInputActionId.NextChapter -> listOf(
            NovelShortcutChord("Right", shift = true),
            NovelShortcutChord("PageDown", shift = true),
        )
        NovelInputActionId.ToggleHud -> listOf(NovelShortcutChord("Enter"))
        NovelInputActionId.OpenChapters -> listOf(
            NovelShortcutChord("C"),
            NovelShortcutChord("C", primary = true, alt = true),
        )
        NovelInputActionId.OpenTypography -> listOf(
            NovelShortcutChord("S"),
            NovelShortcutChord("S", primary = true, alt = true),
        )
        NovelInputActionId.TogglePageScrollMode -> listOf(
            NovelShortcutChord("M"),
            NovelShortcutChord("M", primary = true, alt = true),
        )
        NovelInputActionId.ToggleVerticalWriting -> listOf(
            NovelShortcutChord("V"),
            NovelShortcutChord("V", primary = true, alt = true),
        )
        NovelInputActionId.ToggleFullScreen -> listOf(NovelShortcutChord("Enter", alt = true))
        NovelInputActionId.OpenInputHelp -> listOf(NovelShortcutChord("H"))
        NovelInputActionId.LookupSelection -> listOf(
            NovelShortcutChord("G"),
            NovelShortcutChord("G", primary = true, alt = true),
        )
        NovelInputActionId.LookupClipboard -> listOf(NovelShortcutChord("G", primary = true, shift = true))
        NovelInputActionId.LookupHoveredText -> listOf(NovelShortcutChord("G", alt = true))
        NovelInputActionId.CopySelection -> listOf(NovelShortcutChord("C", primary = true))
        NovelInputActionId.SearchSelection -> listOf(NovelShortcutChord("S", primary = true, shift = true))
        NovelInputActionId.AddSelectionToAnki -> listOf(NovelShortcutChord("A"))
        NovelInputActionId.DismissSelection -> listOf(NovelShortcutChord("Escape"))
        NovelInputActionId.IncreaseFontSize -> listOf(NovelShortcutChord("+", primary = true))
        NovelInputActionId.DecreaseFontSize -> listOf(NovelShortcutChord("-", primary = true))
    }
}

fun formatNovelShortcutChord(
    key: String,
    primary: Boolean = false,
    shift: Boolean = false,
    alt: Boolean = false,
    meta: Boolean = false,
): String {
    return buildList {
        if (primary) add("Ctrl/Cmd")
        if (meta) add("Meta")
        if (alt) add("Alt")
        if (shift) add("Shift")
        add(key)
    }.joinToString("+")
}

fun normalizedNovelShortcutIdentity(
    key: String,
    primary: Boolean = false,
    shift: Boolean = false,
    alt: Boolean = false,
    meta: Boolean = false,
): String {
    return listOf(
        key.trim().lowercase(),
        primary.asIdentityBit(),
        shift.asIdentityBit(),
        alt.asIdentityBit(),
        meta.asIdentityBit(),
    ).joinToString(":")
}

private fun MutableList<NovelInputConflictWarning>.addShortcutConflicts(
    keyboardShortcuts: List<NovelKeyboardShortcut>,
    lookupRows: List<NovelLookupShortcutRow>,
) {
    val bindings = mutableMapOf<String, MutableList<Pair<NovelInputActionId, String>>>()
    keyboardShortcuts
        .filter { it.enabled }
        .forEach { shortcut ->
            shortcut.chords.forEach { chord ->
                val key = "${shortcut.scope.name}:${chord.normalizedIdentity}"
                bindings.getOrPut(key) { mutableListOf() }
                    .add(shortcut.action to chord.displayLabel)
            }
            if (shortcut.reservedBySystem) {
                add(
                    NovelInputConflictWarning(
                        id = "reserved:${shortcut.id}",
                        title = "System shortcut",
                        message = "${shortcut.title} uses a shortcut that may be intercepted by the platform.",
                        severity = NovelInputConflictSeverity.Warning,
                        shortcutLabel = shortcut.shortcutLabel,
                        actionIds = listOf(shortcut.action),
                    ),
                )
            }
        }
    lookupRows
        .filter { it.enabled }
        .forEach { row ->
            row.shortcuts.forEach { chord ->
                val key = "${NovelInputScope.Lookup.name}:${chord.normalizedIdentity}"
                bindings.getOrPut(key) { mutableListOf() }
                    .add(row.action.inputAction to chord.displayLabel)
            }
        }
    bindings.values
        .filter { rows -> rows.map { it.first }.distinct().size > 1 }
        .forEach { rows ->
            val actions = rows.map { it.first }.distinct()
            add(
                NovelInputConflictWarning(
                    id = "shortcut:${rows.first().second}:${actions.joinToString { it.name }}",
                    title = "Shortcut conflict",
                    message = rows.first().second + " is assigned to " + actions.joinToString { it.title } + ".",
                    severity = NovelInputConflictSeverity.Error,
                    shortcutLabel = rows.first().second,
                    actionIds = actions,
                ),
            )
        }
}

private fun MutableList<NovelInputConflictWarning>.addTapZoneWarnings(
    tapZones: NovelTapZoneLayout,
    pageScrollMode: NovelPageScrollModeState,
) {
    if (tapZones.leadingWidthPercent + tapZones.trailingWidthPercent > 82f) {
        add(
            NovelInputConflictWarning(
                id = "tap-zone:center-too-small",
                title = "Small center tap zone",
                message = "The center tap zone is narrow, so toggling controls may be hard on mobile.",
                severity = NovelInputConflictSeverity.Warning,
            ),
        )
    }
    if (tapZones.verticalEdgeHeightPercent > 24f) {
        add(
            NovelInputConflictWarning(
                id = "tap-zone:edge-too-tall",
                title = "Large top/bottom zones",
                message = "Top and bottom tap zones may steal taps from text selection.",
                severity = NovelInputConflictSeverity.Warning,
            ),
        )
    }
    if (pageScrollMode.mode == NovelReaderViewportMode.Paged) {
        val actions = NovelTapZoneId.entries.mapNotNull { tapZones.assignmentFor(it).action }.toSet()
        if (NovelInputActionId.PreviousPage !in actions || NovelInputActionId.NextPage !in actions) {
            add(
                NovelInputConflictWarning(
                    id = "tap-zone:paged-navigation-missing",
                    title = "Paged tap navigation incomplete",
                    message = "Paged mode should expose both previous and next page tap actions.",
                    severity = NovelInputConflictSeverity.Warning,
                    actionIds = listOf(NovelInputActionId.PreviousPage, NovelInputActionId.NextPage),
                ),
            )
        }
    }
}

private fun MutableList<NovelInputConflictWarning>.addMouseWarnings(
    mouse: NovelMouseInputState,
    pageScrollMode: NovelPageScrollModeState,
) {
    if (
        pageScrollMode.mode == NovelReaderViewportMode.ContinuousScroll &&
        mouse.wheelBehavior == NovelMouseWheelBehavior.TurnPage
    ) {
        add(
            NovelInputConflictWarning(
                id = "mouse:wheel-page-in-scroll",
                title = "Wheel turns pages in scroll mode",
                message = "Continuous scroll mode usually expects the wheel to scroll content.",
                severity = NovelInputConflictSeverity.Info,
            ),
        )
    }
    if (
        pageScrollMode.mode == NovelReaderViewportMode.ContinuousScroll &&
        mouse.wheelBehavior == NovelMouseWheelBehavior.ChangeChapter
    ) {
        add(
            NovelInputConflictWarning(
                id = "mouse:wheel-chapter-in-scroll",
                title = "Wheel changes chapters",
                message = "Chapter wheel navigation can skip content in continuous scroll mode.",
                severity = NovelInputConflictSeverity.Warning,
            ),
        )
    }
}

private fun MutableList<NovelInputConflictWarning>.addSelectionWarnings(
    selection: NovelSelectionBehaviorState,
    pageScrollMode: NovelPageScrollModeState,
) {
    if (
        pageScrollMode.mode == NovelReaderViewportMode.Paged &&
        !selection.allowTextSelectionInPagedMode
    ) {
        add(
            NovelInputConflictWarning(
                id = "selection:paged-disabled",
                title = "Selection disabled in paged mode",
                message = "Lookup shortcuts can still work, but direct text selection is unavailable.",
                severity = NovelInputConflictSeverity.Info,
                actionIds = listOf(NovelInputActionId.LookupSelection),
            ),
        )
    }
    if (
        selection.mouseDrag == NovelSelectionActivation.InstantLookup &&
        selection.keepSelectionToolbarVisible
    ) {
        add(
            NovelInputConflictWarning(
                id = "selection:instant-lookup-toolbar",
                title = "Instant lookup keeps toolbar open",
                message = "Mouse drag lookup may leave selection controls visible after every drag.",
                severity = NovelInputConflictSeverity.Warning,
                actionIds = listOf(NovelInputActionId.LookupSelection),
            ),
        )
    }
}

private fun MutableList<NovelInputConflictWarning>.addVerticalWritingWarnings(
    verticalWriting: NovelVerticalWritingState,
    pageScrollMode: NovelPageScrollModeState,
) {
    if (verticalWriting.enabled && !pageScrollMode.readingFlow.vertical) {
        add(
            NovelInputConflictWarning(
                id = "vertical:flow-horizontal",
                title = "Vertical writing uses horizontal flow",
                message = "Set the reading flow to a vertical direction when vertical writing is enabled.",
                severity = NovelInputConflictSeverity.Warning,
                actionIds = listOf(NovelInputActionId.ToggleVerticalWriting),
            ),
        )
    }
    if (!verticalWriting.enabled && pageScrollMode.readingFlow.vertical) {
        add(
            NovelInputConflictWarning(
                id = "vertical:flow-without-layout",
                title = "Vertical flow without vertical writing",
                message = "Tap zones and page edges are mirrored, but text layout remains horizontal.",
                severity = NovelInputConflictSeverity.Info,
                actionIds = listOf(NovelInputActionId.ToggleVerticalWriting),
            ),
        )
    }
}

private fun NovelTapZoneId.swapHorizontalEdge(): NovelTapZoneId {
    return when (this) {
        NovelTapZoneId.LeadingEdge -> NovelTapZoneId.TrailingEdge
        NovelTapZoneId.TrailingEdge -> NovelTapZoneId.LeadingEdge
        NovelTapZoneId.TopEdge,
        NovelTapZoneId.BottomEdge,
        NovelTapZoneId.Center,
        -> this
    }
}

private fun shortcutLabelFor(
    shortcuts: List<NovelKeyboardShortcut>,
    vararg actions: NovelInputActionId,
): String {
    return actions
        .mapNotNull { action -> shortcuts.firstOrNull { it.action == action }?.shortcutLabel }
        .joinToString(" / ")
}

private fun Float.asPercent(): String {
    return "${toInt()}%"
}

private fun Boolean.asIdentityBit(): String {
    return if (this) "1" else "0"
}
