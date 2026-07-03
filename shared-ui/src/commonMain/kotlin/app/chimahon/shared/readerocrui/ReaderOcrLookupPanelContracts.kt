package app.chimahon.shared.readerocrui

import app.chimahon.shared.lookup.ChimahonLookupActionKind
import app.chimahon.shared.lookup.ChimahonLookupActionSlot
import app.chimahon.shared.lookup.ChimahonLookupFrame
import app.chimahon.shared.lookup.ChimahonLookupMatchedTextAnchor
import app.chimahon.shared.lookup.ChimahonLookupPopupActionSlots
import app.chimahon.shared.lookup.ChimahonLookupPopupUiState
import app.chimahon.shared.lookup.ChimahonLookupResult
import app.chimahon.shared.lookup.ChimahonLookupSheetMode
import app.chimahon.shared.lookup.ChimahonLookupSheetUiState
import app.chimahon.shared.lookup.ChimahonLookupStack

enum class ChimahonReaderOcrLookupPanelMode {
    Hidden,
    Peek,
    Expanded,
    FullHeight,
    Floating,
}

data class ChimahonReaderOcrLookupPanelUiState(
    val visible: Boolean = false,
    val mode: ChimahonReaderOcrLookupPanelMode = if (visible) {
        ChimahonReaderOcrLookupPanelMode.Peek
    } else {
        ChimahonReaderOcrLookupPanelMode.Hidden
    },
    val selection: ChimahonReaderOcrMultiSelectionUiModel? = null,
    val anchor: ChimahonLookupMatchedTextAnchor = selection?.anchor ?: ChimahonLookupMatchedTextAnchor(""),
    val lookupText: String = selection?.lookupText ?: anchor.lookupText,
    val sentence: String = selection?.sentence ?: anchor.fullText,
    val stack: ChimahonLookupStack = ChimahonLookupStack.Empty,
    val selectedResultIndex: Int = 0,
    val selectedBoxIndex: Int = selection?.selectedBoxIndex ?: 0,
    val boxCount: Int = selection?.boxCount ?: 0,
    val loading: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null,
    val actions: List<ChimahonReaderOcrBoxActionState> = emptyList(),
) {
    val currentFrame: ChimahonLookupFrame?
        get() = stack.currentFrame

    val results: List<ChimahonLookupResult>
        get() = currentFrame?.results.orEmpty()

    val selectedResult: ChimahonLookupResult?
        get() = results.getOrNull(selectedResultIndex)

    val hasLookupText: Boolean
        get() = lookupText.isNotBlank()

    val canGoPreviousBox: Boolean
        get() = selectedBoxIndex > 0

    val canGoNextBox: Boolean
        get() = selectedBoxIndex + 1 < boxCount

    val positionText: String?
        get() = selection?.positionText
            ?: boxCount
                .takeIf { it > 0 }
                ?.let { "${selectedBoxIndex.coerceIn(0, it - 1) + 1} / $it" }

    val supportMessage: String?
        get() = message ?: errorMessage ?: currentFrame?.errorMessage

    fun withDefaultActions(
        scanStatus: ChimahonReaderOcrScanStatusUiModel = ChimahonReaderOcrScanStatusUiModel(),
        boxesVisible: Boolean = true,
    ): ChimahonReaderOcrLookupPanelUiState {
        return copy(
            actions = defaultReaderOcrBoxActions(
                selection = selection,
                selectedBoxIndex = selectedBoxIndex,
                boxCount = boxCount,
                scanStatus = scanStatus,
                boxesVisible = boxesVisible,
            ),
        )
    }

    fun toSheetUiState(
        modeOverride: ChimahonLookupSheetMode? = null,
    ): ChimahonLookupSheetUiState {
        return ChimahonLookupSheetUiState(
            visible = visible,
            mode = modeOverride ?: mode.toLookupSheetMode(),
            anchor = anchor,
            stack = stack,
            lookupText = lookupText,
            sentence = sentence,
            selectedResultIndex = selectedResultIndex,
            selectedOcrIndex = selectedBoxIndex,
            ocrBlockCount = boxCount,
            isLoading = loading,
            message = message,
            errorMessage = errorMessage ?: currentFrame?.errorMessage,
        )
    }

    fun toPopupUiState(): ChimahonLookupPopupUiState {
        return ChimahonLookupPopupUiState(
            visible = visible,
            anchor = anchor,
            stack = stack,
            lookupText = lookupText,
            selectedResultIndex = selectedResultIndex,
            selectedOcrIndex = selectedBoxIndex,
            ocrBlockCount = boxCount,
            isLoading = loading,
            message = supportMessage,
        )
    }

    fun toLookupCardState(): ChimahonReaderOcrLookupCardState {
        return ChimahonReaderOcrLookupCardState(
            visible = visible,
            lookupText = lookupText,
            sentence = sentence,
            anchor = anchor,
            results = results,
            loading = loading,
            errorMessage = errorMessage ?: currentFrame?.errorMessage,
            selectedResultIndex = selectedResultIndex,
            selectedBoxIndex = selectedBoxIndex,
            boxCount = boxCount,
        )
    }
}

fun ChimahonReaderOcrSelectionUiModel.toLookupPanelUiState(
    results: List<ChimahonLookupResult> = emptyList(),
    loading: Boolean = false,
    message: String? = null,
    errorMessage: String? = null,
): ChimahonReaderOcrLookupPanelUiState {
    val stack = ChimahonLookupStack(
        frames = listOf(
            ChimahonLookupFrame(
                query = lookupText,
                results = results,
                errorMessage = errorMessage,
            ),
        ),
    )
    val selection = ChimahonReaderOcrMultiSelectionUiModel(selections = listOf(this))
    return ChimahonReaderOcrLookupPanelUiState(
        visible = true,
        selection = selection,
        anchor = anchor,
        lookupText = lookupText,
        sentence = sentence,
        stack = stack,
        selectedBoxIndex = selectedBoxIndex,
        boxCount = boxCount,
        loading = loading,
        message = message,
        errorMessage = errorMessage,
    )
}

fun ChimahonReaderOcrMultiSelectionUiModel.toLookupPanelUiState(
    results: List<ChimahonLookupResult> = emptyList(),
    loading: Boolean = false,
    message: String? = null,
    errorMessage: String? = null,
): ChimahonReaderOcrLookupPanelUiState {
    val stack = ChimahonLookupStack(
        frames = listOf(
            ChimahonLookupFrame(
                query = lookupText,
                results = results,
                errorMessage = errorMessage,
            ),
        ),
    )
    return ChimahonReaderOcrLookupPanelUiState(
        visible = hasSelection,
        selection = this,
        anchor = anchor,
        lookupText = lookupText,
        sentence = sentence,
        stack = stack,
        selectedBoxIndex = selectedBoxIndex,
        boxCount = boxCount,
        loading = loading,
        message = message,
        errorMessage = errorMessage,
    )
}

fun ChimahonReaderOcrLookupCardState.toLookupPanelUiState(
    mode: ChimahonReaderOcrLookupPanelMode = ChimahonReaderOcrLookupPanelMode.Peek,
): ChimahonReaderOcrLookupPanelUiState {
    val stack = ChimahonLookupStack(
        frames = listOf(
            ChimahonLookupFrame(
                query = lookupText,
                results = results,
                errorMessage = errorMessage,
            ),
        ),
    )
    return ChimahonReaderOcrLookupPanelUiState(
        visible = visible,
        mode = if (visible) mode else ChimahonReaderOcrLookupPanelMode.Hidden,
        anchor = anchor,
        lookupText = lookupText,
        sentence = sentence,
        stack = stack,
        selectedResultIndex = selectedResultIndex,
        selectedBoxIndex = selectedBoxIndex,
        boxCount = boxCount,
        loading = loading,
        errorMessage = errorMessage,
    )
}

fun ChimahonLookupSheetUiState.toReaderOcrLookupPanelUiState(
    mode: ChimahonReaderOcrLookupPanelMode? = null,
): ChimahonReaderOcrLookupPanelUiState {
    return ChimahonReaderOcrLookupPanelUiState(
        visible = visible,
        mode = mode ?: this.mode.toReaderOcrLookupPanelMode(),
        anchor = anchor,
        lookupText = lookupText,
        sentence = sentence,
        stack = stack,
        selectedResultIndex = selectedResultIndex,
        selectedBoxIndex = selectedOcrIndex,
        boxCount = ocrBlockCount,
        loading = isLoading,
        message = message,
        errorMessage = errorMessage,
    )
}

fun readerOcrLookupActionSlots(
    includeAddToAnki: Boolean = true,
    includeWordAudio: Boolean = true,
    includeSentenceAudio: Boolean = true,
    includeScreenshot: Boolean = true,
    includeTranslate: Boolean = true,
    includeOpenDictionary: Boolean = true,
): ChimahonLookupPopupActionSlots {
    return ChimahonLookupPopupActionSlots(
        addToAnki = ChimahonLookupActionSlot(
            id = "reader-ocr-add-to-anki",
            kind = ChimahonLookupActionKind.AddToAnki,
            label = "Anki",
            contentDescription = "Add OCR lookup result to Anki",
        ).takeIf { includeAddToAnki },
        wordAudio = ChimahonLookupActionSlot(
            id = "reader-ocr-word-audio",
            kind = ChimahonLookupActionKind.WordAudio,
            label = "Audio",
            contentDescription = "Play OCR lookup word audio",
        ).takeIf { includeWordAudio },
        sentenceAudio = ChimahonLookupActionSlot(
            id = "reader-ocr-sentence-audio",
            kind = ChimahonLookupActionKind.SentenceAudio,
            label = "Sentence",
            contentDescription = "Play OCR sentence audio",
        ).takeIf { includeSentenceAudio },
        screenshot = ChimahonLookupActionSlot(
            id = "reader-ocr-screenshot",
            kind = ChimahonLookupActionKind.Screenshot,
            label = "Shot",
            contentDescription = "Capture OCR lookup screenshot",
        ).takeIf { includeScreenshot },
        extra = buildList {
            if (includeTranslate) {
                add(
                    ChimahonLookupActionSlot(
                        id = "reader-ocr-translate",
                        kind = ChimahonLookupActionKind.Custom,
                        label = "Translate",
                        contentDescription = "Translate OCR lookup text",
                    ),
                )
            }
            if (includeOpenDictionary) {
                add(
                    ChimahonLookupActionSlot(
                        id = "reader-ocr-open-dictionary",
                        kind = ChimahonLookupActionKind.Custom,
                        label = "Dictionary",
                        contentDescription = "Open OCR lookup text in dictionary",
                    ),
                )
            }
        },
    )
}

private fun ChimahonReaderOcrLookupPanelMode.toLookupSheetMode(): ChimahonLookupSheetMode {
    return when (this) {
        ChimahonReaderOcrLookupPanelMode.Hidden -> ChimahonLookupSheetMode.Hidden
        ChimahonReaderOcrLookupPanelMode.Peek,
        ChimahonReaderOcrLookupPanelMode.Floating,
        -> ChimahonLookupSheetMode.Peek
        ChimahonReaderOcrLookupPanelMode.Expanded -> ChimahonLookupSheetMode.Expanded
        ChimahonReaderOcrLookupPanelMode.FullHeight -> ChimahonLookupSheetMode.FullHeight
    }
}

private fun ChimahonLookupSheetMode.toReaderOcrLookupPanelMode(): ChimahonReaderOcrLookupPanelMode {
    return when (this) {
        ChimahonLookupSheetMode.Hidden -> ChimahonReaderOcrLookupPanelMode.Hidden
        ChimahonLookupSheetMode.Peek -> ChimahonReaderOcrLookupPanelMode.Peek
        ChimahonLookupSheetMode.Expanded -> ChimahonReaderOcrLookupPanelMode.Expanded
        ChimahonLookupSheetMode.FullHeight -> ChimahonReaderOcrLookupPanelMode.FullHeight
    }
}
