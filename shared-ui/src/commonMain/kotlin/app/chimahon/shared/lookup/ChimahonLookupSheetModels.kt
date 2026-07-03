package app.chimahon.shared.lookup

enum class ChimahonLookupSheetMode {
    Hidden,
    Peek,
    Expanded,
    FullHeight,
}

enum class ChimahonLookupSheetActionId {
    PreviousOcr,
    NextOcr,
    CopyText,
    ShareText,
    SearchWeb,
    AddToAnki,
    PlayWordAudio,
    PlaySentenceAudio,
    CaptureScreenshot,
    CropScreenshot,
    Custom,
    Close,
}

data class ChimahonLookupSheetActionState(
    val id: ChimahonLookupSheetActionId,
    val label: String,
    val contentDescription: String = label,
    val enabled: Boolean = true,
    val active: Boolean = false,
    val loading: Boolean = false,
    val badgeText: String? = null,
    val sourceSlot: ChimahonLookupActionSlot? = null,
)

data class ChimahonLookupSheetUiState(
    val visible: Boolean = true,
    val mode: ChimahonLookupSheetMode = ChimahonLookupSheetMode.Peek,
    val anchor: ChimahonLookupMatchedTextAnchor = ChimahonLookupMatchedTextAnchor(""),
    val stack: ChimahonLookupStack = ChimahonLookupStack.Empty,
    val lookupText: String = anchor.lookupText,
    val sentence: String = anchor.fullText,
    val selectedResultIndex: Int = 0,
    val selectedOcrIndex: Int = 0,
    val ocrBlockCount: Int = 0,
    val isLoading: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null,
) {
    val currentFrame: ChimahonLookupFrame?
        get() = stack.currentFrame

    val selectedResult: ChimahonLookupResult?
        get() = currentFrame?.results?.getOrNull(selectedResultIndex)

    val hasLookupText: Boolean
        get() = lookupText.isNotBlank()

    val canGoPreviousOcr: Boolean
        get() = selectedOcrIndex > 0

    val canGoNextOcr: Boolean
        get() = selectedOcrIndex + 1 < ocrBlockCount

    val ocrPositionText: String?
        get() = ocrBlockCount
            .takeIf { it > 0 }
            ?.let { "${selectedOcrIndex.coerceIn(0, it - 1) + 1} / $it" }

    fun toPopupUiState(): ChimahonLookupPopupUiState {
        return ChimahonLookupPopupUiState(
            visible = visible,
            anchor = anchor,
            stack = stack,
            lookupText = lookupText,
            selectedResultIndex = selectedResultIndex,
            selectedOcrIndex = selectedOcrIndex,
            ocrBlockCount = ocrBlockCount,
            isLoading = isLoading,
            message = message ?: errorMessage,
        )
    }
}

data class ChimahonLookupSheetActionHandlers(
    val onAction: (ChimahonLookupSheetActionState) -> Unit = {},
    val onLookupTextChange: (String) -> Unit = {},
    val onResultSelected: (ChimahonLookupResult) -> Unit = {},
    val onRecursiveLookup: (String) -> Unit = {},
    val onDismiss: () -> Unit = {},
)

fun ChimahonLookupPopupUiState.toSheetUiState(
    mode: ChimahonLookupSheetMode? = null,
    sentence: String? = null,
    errorMessage: String? = null,
): ChimahonLookupSheetUiState {
    return ChimahonLookupSheetUiState(
        visible = visible,
        mode = mode ?: if (visible) ChimahonLookupSheetMode.Peek else ChimahonLookupSheetMode.Hidden,
        anchor = anchor,
        stack = stack,
        lookupText = lookupText,
        sentence = sentence ?: anchor.fullText,
        selectedResultIndex = selectedResultIndex,
        selectedOcrIndex = selectedOcrIndex,
        ocrBlockCount = ocrBlockCount,
        isLoading = isLoading,
        message = message,
        errorMessage = errorMessage ?: currentFrame?.errorMessage,
    )
}

fun defaultChimahonLookupSheetActions(
    state: ChimahonLookupSheetUiState,
    actionSlots: ChimahonLookupPopupActionSlots = ChimahonLookupPopupActionSlots(),
): List<ChimahonLookupSheetActionState> {
    val resultSelected = state.selectedResult != null
    return buildList {
        add(
            ChimahonLookupSheetActionState(
                id = ChimahonLookupSheetActionId.PreviousOcr,
                label = "Previous",
                contentDescription = "Previous OCR match",
                enabled = state.canGoPreviousOcr,
            ),
        )
        add(
            ChimahonLookupSheetActionState(
                id = ChimahonLookupSheetActionId.NextOcr,
                label = "Next",
                contentDescription = "Next OCR match",
                enabled = state.canGoNextOcr,
            ),
        )
        add(
            ChimahonLookupSheetActionState(
                id = ChimahonLookupSheetActionId.CopyText,
                label = "Copy",
                contentDescription = "Copy lookup text",
                enabled = state.hasLookupText,
            ),
        )
        add(
            ChimahonLookupSheetActionState(
                id = ChimahonLookupSheetActionId.ShareText,
                label = "Share",
                contentDescription = "Share lookup text",
                enabled = state.hasLookupText,
            ),
        )
        add(
            ChimahonLookupSheetActionState(
                id = ChimahonLookupSheetActionId.SearchWeb,
                label = "Search",
                contentDescription = "Search lookup text",
                enabled = state.hasLookupText,
            ),
        )
        actionSlots.all.mapTo(this) { slot ->
            slot.toLookupSheetActionState(resultSelected = resultSelected)
        }
        add(
            ChimahonLookupSheetActionState(
                id = ChimahonLookupSheetActionId.Close,
                label = "Close",
                contentDescription = "Close lookup",
            ),
        )
    }
}

private fun ChimahonLookupActionSlot.toLookupSheetActionState(
    resultSelected: Boolean,
): ChimahonLookupSheetActionState {
    val actionId = when (kind) {
        ChimahonLookupActionKind.AddToAnki -> ChimahonLookupSheetActionId.AddToAnki
        ChimahonLookupActionKind.WordAudio -> ChimahonLookupSheetActionId.PlayWordAudio
        ChimahonLookupActionKind.SentenceAudio -> ChimahonLookupSheetActionId.PlaySentenceAudio
        ChimahonLookupActionKind.Screenshot -> ChimahonLookupSheetActionId.CaptureScreenshot
        ChimahonLookupActionKind.CropScreenshot -> ChimahonLookupSheetActionId.CropScreenshot
        ChimahonLookupActionKind.Custom -> ChimahonLookupSheetActionId.Custom
    }
    val requiresResult = kind == ChimahonLookupActionKind.AddToAnki || kind == ChimahonLookupActionKind.WordAudio
    return ChimahonLookupSheetActionState(
        id = actionId,
        label = label,
        contentDescription = contentDescription,
        enabled = enabled && (!requiresResult || resultSelected),
        active = active,
        sourceSlot = this,
    )
}
