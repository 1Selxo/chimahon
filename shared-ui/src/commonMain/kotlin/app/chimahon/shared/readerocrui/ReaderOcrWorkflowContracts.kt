package app.chimahon.shared.readerocrui

import app.chimahon.shared.lookup.ChimahonLookupMatchedTextAnchor
import app.chimahon.shared.lookup.ChimahonLookupResult

enum class ChimahonReaderOcrScanPhase(
    val label: String,
) {
    Idle("Ready"),
    WaitingForPageImage("Waiting for page"),
    LoadingPageImage("Loading page image"),
    SendingToEngine("Sending to OCR"),
    RecognizingText("Recognizing text"),
    BuildingBoxes("Building boxes"),
    Ready("Ready"),
    Failed("Failed"),
    Cancelled("Cancelled"),
}

data class ChimahonReaderOcrScanRequestUiModel(
    val id: String,
    val pageKey: String,
    val pageIndex: Int,
    val languageId: String = "auto",
    val engine: ChimahonReaderOcrEngine = ChimahonReaderOcrEngine.Glens,
    val forceRefresh: Boolean = false,
    val createdAtMillis: Long = 0L,
)

data class ChimahonReaderOcrScanStatusUiModel(
    val phase: ChimahonReaderOcrScanPhase = ChimahonReaderOcrScanPhase.Idle,
    val request: ChimahonReaderOcrScanRequestUiModel? = null,
    val provider: ChimahonReaderOcrCapabilityProvider = request?.engine?.toCapabilityProvider()
        ?: ChimahonReaderOcrCapabilityProvider.Glens,
    val progress: Float? = null,
    val blockCount: Int = 0,
    val lineCount: Int = 0,
    val wordCount: Int = 0,
    val message: String? = null,
    val errorMessage: String? = null,
    val startedAtMillis: Long = request?.createdAtMillis ?: 0L,
    val finishedAtMillis: Long = 0L,
) {
    val engine: ChimahonReaderOcrEngine
        get() = provider.toReaderOcrEngine()

    val active: Boolean
        get() = phase == ChimahonReaderOcrScanPhase.WaitingForPageImage ||
            phase == ChimahonReaderOcrScanPhase.LoadingPageImage ||
            phase == ChimahonReaderOcrScanPhase.SendingToEngine ||
            phase == ChimahonReaderOcrScanPhase.RecognizingText ||
            phase == ChimahonReaderOcrScanPhase.BuildingBoxes

    val terminal: Boolean
        get() = phase == ChimahonReaderOcrScanPhase.Ready ||
            phase == ChimahonReaderOcrScanPhase.Failed ||
            phase == ChimahonReaderOcrScanPhase.Cancelled

    val failed: Boolean
        get() = phase == ChimahonReaderOcrScanPhase.Failed

    val canRetry: Boolean
        get() = phase == ChimahonReaderOcrScanPhase.Failed ||
            phase == ChimahonReaderOcrScanPhase.Cancelled ||
            phase == ChimahonReaderOcrScanPhase.Ready

    val loadPhase: ChimahonReaderOcrLoadPhase
        get() = when (phase) {
            ChimahonReaderOcrScanPhase.Idle,
            ChimahonReaderOcrScanPhase.Cancelled,
            -> ChimahonReaderOcrLoadPhase.Idle
            ChimahonReaderOcrScanPhase.WaitingForPageImage,
            ChimahonReaderOcrScanPhase.LoadingPageImage,
            ChimahonReaderOcrScanPhase.SendingToEngine,
            ChimahonReaderOcrScanPhase.RecognizingText,
            ChimahonReaderOcrScanPhase.BuildingBoxes,
            -> ChimahonReaderOcrLoadPhase.Loading
            ChimahonReaderOcrScanPhase.Ready -> ChimahonReaderOcrLoadPhase.Ready
            ChimahonReaderOcrScanPhase.Failed -> ChimahonReaderOcrLoadPhase.Failed
        }

    val progressPercent: Int?
        get() = progress
            ?.takeIf { it.isFinite() }
            ?.coerceIn(0f, 1f)
            ?.times(100f)
            ?.toInt()

    val summaryText: String
        get() = when (phase) {
            ChimahonReaderOcrScanPhase.Idle -> "Ready to scan page"
            ChimahonReaderOcrScanPhase.WaitingForPageImage -> "Preparing page image"
            ChimahonReaderOcrScanPhase.LoadingPageImage -> "Loading page image"
            ChimahonReaderOcrScanPhase.SendingToEngine -> "Sending page to ${provider.label}"
            ChimahonReaderOcrScanPhase.RecognizingText -> "Running ${provider.label} OCR"
            ChimahonReaderOcrScanPhase.BuildingBoxes -> "Building OCR boxes"
            ChimahonReaderOcrScanPhase.Ready -> when (blockCount) {
                0 -> "No text boxes found"
                1 -> "1 text block"
                else -> "$blockCount text blocks"
            }
            ChimahonReaderOcrScanPhase.Failed -> "OCR failed"
            ChimahonReaderOcrScanPhase.Cancelled -> "OCR cancelled"
        }

    val detailText: String
        get() = message
            ?: errorMessage
            ?: progressPercent?.let { "$it%" }
            ?: when (phase) {
                ChimahonReaderOcrScanPhase.Ready -> "$lineCount lines, $wordCount words"
                ChimahonReaderOcrScanPhase.Failed -> "Try rescanning this page"
                else -> request?.languageId?.takeIf { it.isNotBlank() }?.let { "Language $it" } ?: provider.label
            }
}

enum class ChimahonReaderOcrBoxActionId(
    val label: String,
    val contentDescription: String,
) {
    Lookup("Lookup", "Look up selected OCR text"),
    CopyText("Copy", "Copy selected OCR text"),
    SearchWeb("Search", "Search selected OCR text on the web"),
    Translate("Translate", "Translate selected OCR text"),
    OpenDictionary("Dictionary", "Open selected OCR text in dictionary"),
    PreviousBox("Previous", "Previous OCR text box"),
    NextBox("Next", "Next OCR text box"),
    AddToSelection("Add", "Add OCR text box to selection"),
    RemoveFromSelection("Remove", "Remove OCR text box from selection"),
    ClearSelection("Clear", "Clear OCR text selection"),
    ToggleBoxes("Boxes", "Show or hide OCR text boxes"),
    Rescan("Rescan", "Scan this page again"),
    Close("Close", "Close OCR lookup"),
}

data class ChimahonReaderOcrBoxActionState(
    val id: ChimahonReaderOcrBoxActionId,
    val enabled: Boolean = true,
    val active: Boolean = false,
    val loading: Boolean = false,
    val label: String = id.label,
    val contentDescription: String = id.contentDescription,
    val shortcutHint: String? = null,
    val badgeText: String? = null,
) {
    val unavailable: Boolean
        get() = !enabled && !loading
}

data class ChimahonReaderOcrBoxActionIntent(
    val id: ChimahonReaderOcrBoxActionId,
    val boxIds: List<String> = emptyList(),
    val lookupText: String = "",
    val sentence: String = lookupText,
    val anchor: ChimahonLookupMatchedTextAnchor = ChimahonLookupMatchedTextAnchor(lookupText),
    val selectedResult: ChimahonLookupResult? = null,
    val openInNewPanel: Boolean = false,
)

data class ChimahonReaderOcrWorkflowActions(
    val onScanRequested: (ChimahonReaderOcrScanRequestUiModel) -> Unit = {},
    val onScanCancelled: (ChimahonReaderOcrScanRequestUiModel?) -> Unit = {},
    val onSelectionIntent: (ChimahonReaderOcrSelectionIntent) -> Unit = {},
    val onBoxAction: (ChimahonReaderOcrBoxActionIntent) -> Unit = {},
    val onLookupTextChange: (String) -> Unit = {},
    val onLookupResultSelected: (ChimahonLookupResult) -> Unit = {},
) {
    fun runBoxAction(
        id: ChimahonReaderOcrBoxActionId,
        selection: ChimahonReaderOcrMultiSelectionUiModel?,
        selectedResult: ChimahonLookupResult? = null,
        openInNewPanel: Boolean = false,
    ) {
        val safeSelection = selection ?: ChimahonReaderOcrMultiSelectionUiModel()
        onBoxAction(
            ChimahonReaderOcrBoxActionIntent(
                id = id,
                boxIds = safeSelection.boxIds,
                lookupText = safeSelection.lookupText,
                sentence = safeSelection.sentence,
                anchor = safeSelection.anchor,
                selectedResult = selectedResult,
                openInNewPanel = openInNewPanel,
            ),
        )
    }
}

fun defaultReaderOcrBoxActions(
    selection: ChimahonReaderOcrMultiSelectionUiModel?,
    selectedBoxIndex: Int = selection?.selectedBoxIndex ?: 0,
    boxCount: Int = selection?.boxCount ?: 0,
    scanStatus: ChimahonReaderOcrScanStatusUiModel = ChimahonReaderOcrScanStatusUiModel(),
    boxesVisible: Boolean = true,
): List<ChimahonReaderOcrBoxActionState> {
    val hasText = !selection?.lookupText.isNullOrBlank()
    return listOf(
        ChimahonReaderOcrBoxActionState(
            id = ChimahonReaderOcrBoxActionId.Lookup,
            enabled = hasText,
            active = hasText,
            shortcutHint = "Enter",
        ),
        ChimahonReaderOcrBoxActionState(
            id = ChimahonReaderOcrBoxActionId.CopyText,
            enabled = hasText,
            shortcutHint = "Ctrl+C",
        ),
        ChimahonReaderOcrBoxActionState(
            id = ChimahonReaderOcrBoxActionId.SearchWeb,
            enabled = hasText,
            shortcutHint = "Ctrl+F",
        ),
        ChimahonReaderOcrBoxActionState(
            id = ChimahonReaderOcrBoxActionId.Translate,
            enabled = hasText,
            shortcutHint = "T",
        ),
        ChimahonReaderOcrBoxActionState(
            id = ChimahonReaderOcrBoxActionId.OpenDictionary,
            enabled = hasText,
            shortcutHint = "D",
        ),
        ChimahonReaderOcrBoxActionState(
            id = ChimahonReaderOcrBoxActionId.PreviousBox,
            enabled = selectedBoxIndex > 0,
            shortcutHint = "Shift+Tab",
        ),
        ChimahonReaderOcrBoxActionState(
            id = ChimahonReaderOcrBoxActionId.NextBox,
            enabled = selectedBoxIndex + 1 < boxCount,
            shortcutHint = "Tab",
        ),
        ChimahonReaderOcrBoxActionState(
            id = ChimahonReaderOcrBoxActionId.ClearSelection,
            enabled = selection?.hasSelection == true,
            shortcutHint = "Esc",
        ),
        ChimahonReaderOcrBoxActionState(
            id = ChimahonReaderOcrBoxActionId.ToggleBoxes,
            active = boxesVisible,
        ),
        ChimahonReaderOcrBoxActionState(
            id = ChimahonReaderOcrBoxActionId.Rescan,
            enabled = !scanStatus.active,
            loading = scanStatus.active,
        ),
        ChimahonReaderOcrBoxActionState(
            id = ChimahonReaderOcrBoxActionId.Close,
        ),
    )
}

fun ChimahonReaderOcrScanStatusUiModel.toReaderOcrUiState(
    previous: ChimahonReaderOcrUiState = ChimahonReaderOcrUiState(),
): ChimahonReaderOcrUiState {
    return previous.copy(
        phase = loadPhase,
        engine = engine,
        message = detailText,
    )
}
