package app.chimahon.shared.readerocrui

import app.chimahon.shared.lookup.ChimahonLookupMatchedTextAnchor
import app.chimahon.shared.lookup.ChimahonLookupRect
import app.chimahon.shared.lookup.ChimahonLookupResult
import app.chimahon.shared.lookup.ChimahonLookupWritingDirection

enum class ChimahonReaderOcrEngine {
    Glens,
    Platform,
    Manual,
}

enum class ChimahonReaderOcrLoadPhase {
    Idle,
    Loading,
    Ready,
    Failed,
}

enum class ChimahonReaderOcrBoxMode(
    val key: String,
    val label: String,
    val description: String,
) {
    Blocks(
        key = "blocks",
        label = "Blocks",
        description = "Show detected text blocks",
    ),
    Lines(
        key = "lines",
        label = "Lines",
        description = "Show OCR line boxes",
    ),
    Words(
        key = "words",
        label = "Words",
        description = "Show word lookup boxes",
    ),
}

enum class ChimahonReaderOcrAction(
    val label: String,
    val contentDescription: String,
) {
    ToggleLookup("OCR", "Toggle GLens OCR lookup"),
    Rescan("Rescan", "Scan this page again"),
    ToggleBoxes("Boxes", "Show or hide OCR text boxes"),
    PreviousMatch("Previous", "Previous OCR text box"),
    NextMatch("Next", "Next OCR text box"),
    CopyText("Copy", "Copy OCR lookup text"),
    SearchWeb("Search", "Search OCR lookup text"),
    Settings("Settings", "OCR lookup settings"),
    Close("Close", "Close OCR lookup"),
}

data class ChimahonReaderOcrLanguageOption(
    val id: String,
    val label: String,
    val nativeLabel: String = "",
    val selected: Boolean = false,
    val enabled: Boolean = true,
    val downloaded: Boolean = true,
) {
    val displayLabel: String
        get() = if (nativeLabel.isBlank() || nativeLabel == label) label else "$label - $nativeLabel"
}

data class ChimahonReaderOcrToolbarButtonState(
    val action: ChimahonReaderOcrAction,
    val enabled: Boolean = true,
    val selected: Boolean = false,
    val loading: Boolean = false,
    val badgeText: String? = null,
)

data class ChimahonReaderOcrTextBoxUiModel(
    val id: String,
    val text: String,
    val rect: ChimahonLookupRect,
    val lineRects: List<ChimahonLookupRect> = emptyList(),
    val confidence: Float? = null,
    val writingDirection: ChimahonLookupWritingDirection = ChimahonLookupWritingDirection.Horizontal,
    val selected: Boolean = false,
    val visible: Boolean = true,
    val hovered: Boolean = false,
    val focused: Boolean = false,
    val pressed: Boolean = false,
    val enabled: Boolean = true,
    val rotationDegrees: Float = 0f,
    val boxLevel: ChimahonReaderOcrBoxLevel = ChimahonReaderOcrBoxLevel.Block,
    val sourceIndex: Int = 0,
    val lineIndex: Int? = null,
    val wordIndex: Int? = null,
    val anchor: ChimahonLookupMatchedTextAnchor? = null,
) {
    val hasText: Boolean
        get() = text.isNotBlank()

    val visualState: ChimahonReaderOcrBoxVisualState
        get() = when {
            !enabled -> ChimahonReaderOcrBoxVisualState.Disabled
            pressed -> ChimahonReaderOcrBoxVisualState.Pressed
            selected -> ChimahonReaderOcrBoxVisualState.Selected
            focused -> ChimahonReaderOcrBoxVisualState.Focused
            hovered -> ChimahonReaderOcrBoxVisualState.Hovered
            else -> ChimahonReaderOcrBoxVisualState.Idle
        }

    val active: Boolean
        get() = selected || hovered || focused || pressed
}

data class ChimahonReaderOcrLookupCardState(
    val visible: Boolean = false,
    val lookupText: String = "",
    val sentence: String = "",
    val anchor: ChimahonLookupMatchedTextAnchor = ChimahonLookupMatchedTextAnchor(""),
    val results: List<ChimahonLookupResult> = emptyList(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val selectedResultIndex: Int = 0,
    val selectedBoxIndex: Int = 0,
    val boxCount: Int = 0,
) {
    val selectedResult: ChimahonLookupResult?
        get() = results.getOrNull(selectedResultIndex)
}

data class ChimahonReaderOcrUiState(
    val enabled: Boolean = true,
    val overlayVisible: Boolean = false,
    val boxesVisible: Boolean = true,
    val phase: ChimahonReaderOcrLoadPhase = ChimahonReaderOcrLoadPhase.Idle,
    val engine: ChimahonReaderOcrEngine = ChimahonReaderOcrEngine.Glens,
    val selectedLanguageId: String = "ja",
    val languages: List<ChimahonReaderOcrLanguageOption> = defaultReaderOcrLanguages(),
    val selectedBoxMode: ChimahonReaderOcrBoxMode = ChimahonReaderOcrBoxMode.Blocks,
    val availableBoxModes: List<ChimahonReaderOcrBoxMode> = ChimahonReaderOcrBoxMode.entries,
    val textBoxes: List<ChimahonReaderOcrTextBoxUiModel> = emptyList(),
    val lookup: ChimahonReaderOcrLookupCardState = ChimahonReaderOcrLookupCardState(),
    val message: String? = null,
    val interaction: ChimahonReaderOcrOverlayInteractionState = ChimahonReaderOcrOverlayInteractionState(),
    val capabilities: ChimahonReaderOcrCapabilityStatusUiState = ChimahonReaderOcrCapabilityStatusUiState(),
) {
    val selectedLanguage: ChimahonReaderOcrLanguageOption?
        get() = languages.firstOrNull { it.id == selectedLanguageId }

    val visibleBoxCount: Int
        get() = textBoxes.count { it.visible }

    val statusText: String
        get() = when (phase) {
            ChimahonReaderOcrLoadPhase.Idle -> if (overlayVisible) "Tap text to look up" else "OCR ready"
            ChimahonReaderOcrLoadPhase.Loading -> "Running GLens OCR"
            ChimahonReaderOcrLoadPhase.Ready -> when (visibleBoxCount) {
                0 -> "No OCR boxes"
                1 -> "1 OCR box"
                else -> "$visibleBoxCount OCR boxes"
            }
            ChimahonReaderOcrLoadPhase.Failed -> "OCR failed"
        }

    val detailText: String
        get() = message ?: when (phase) {
            ChimahonReaderOcrLoadPhase.Idle -> selectedLanguage?.displayLabel ?: "Language auto"
            ChimahonReaderOcrLoadPhase.Loading -> "Recognizing page text"
            ChimahonReaderOcrLoadPhase.Ready -> selectedBoxMode.description
            ChimahonReaderOcrLoadPhase.Failed -> lookup.errorMessage ?: "Try rescanning this page"
        }

    val capabilityText: String
        get() = capabilities.summaryText
}

data class ChimahonReaderOcrUiActions(
    val onAction: (ChimahonReaderOcrAction) -> Unit = {},
    val onLanguageSelected: (ChimahonReaderOcrLanguageOption) -> Unit = {},
    val onBoxModeSelected: (ChimahonReaderOcrBoxMode) -> Unit = {},
    val onLookupTextChange: (String) -> Unit = {},
    val onLookupResultSelected: (ChimahonLookupResult) -> Unit = {},
    val onBoxSelected: (ChimahonReaderOcrTextBoxUiModel) -> Unit = {},
    val onBoxHovered: (String?) -> Unit = {},
    val onBoxFocused: (String?) -> Unit = {},
    val onCapabilityAction: (ChimahonReaderOcrCapabilityUiModel) -> Unit = {},
    val onDismissLookup: () -> Unit = {},
) {
    fun run(action: ChimahonReaderOcrAction) {
        onAction(action)
    }
}

fun defaultReaderOcrLanguages(): List<ChimahonReaderOcrLanguageOption> {
    return listOf(
        ChimahonReaderOcrLanguageOption(id = "auto", label = "Auto", selected = false),
        ChimahonReaderOcrLanguageOption(id = "ja", label = "Japanese", nativeLabel = "Nihongo", selected = true),
        ChimahonReaderOcrLanguageOption(id = "en", label = "English"),
        ChimahonReaderOcrLanguageOption(id = "ko", label = "Korean"),
        ChimahonReaderOcrLanguageOption(id = "zh", label = "Chinese"),
    )
}

fun defaultReaderOcrToolbarButtons(state: ChimahonReaderOcrUiState): List<ChimahonReaderOcrToolbarButtonState> {
    return listOf(
        ChimahonReaderOcrToolbarButtonState(
            action = ChimahonReaderOcrAction.ToggleLookup,
            enabled = state.enabled,
            selected = state.overlayVisible,
            loading = state.phase == ChimahonReaderOcrLoadPhase.Loading,
        ),
        ChimahonReaderOcrToolbarButtonState(
            action = ChimahonReaderOcrAction.ToggleBoxes,
            enabled = state.enabled && state.phase == ChimahonReaderOcrLoadPhase.Ready,
            selected = state.boxesVisible,
            badgeText = state.visibleBoxCount.takeIf { it > 0 }?.toString(),
        ),
        ChimahonReaderOcrToolbarButtonState(
            action = ChimahonReaderOcrAction.PreviousMatch,
            enabled = state.lookup.selectedBoxIndex > 0,
        ),
        ChimahonReaderOcrToolbarButtonState(
            action = ChimahonReaderOcrAction.NextMatch,
            enabled = state.lookup.selectedBoxIndex + 1 < state.lookup.boxCount,
        ),
        ChimahonReaderOcrToolbarButtonState(
            action = ChimahonReaderOcrAction.Rescan,
            enabled = state.enabled && state.phase != ChimahonReaderOcrLoadPhase.Loading,
        ),
        ChimahonReaderOcrToolbarButtonState(
            action = ChimahonReaderOcrAction.Settings,
            enabled = state.enabled,
        ),
    )
}
