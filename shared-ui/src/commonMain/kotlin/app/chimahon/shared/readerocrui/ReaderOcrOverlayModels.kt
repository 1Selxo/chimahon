package app.chimahon.shared.readerocrui

import app.chimahon.shared.lookup.ChimahonLookupAnchorCoordinateSpace
import app.chimahon.shared.lookup.ChimahonLookupMatchedTextAnchor
import app.chimahon.shared.lookup.ChimahonLookupRect
import app.chimahon.shared.lookup.ChimahonLookupSelectionRect
import app.chimahon.shared.lookup.ChimahonLookupWritingDirection

enum class ChimahonReaderOcrBoxLevel(
    val key: String,
    val label: String,
) {
    Block("block", "Block"),
    Line("line", "Line"),
    Word("word", "Word"),
}

enum class ChimahonReaderOcrBoxVisualState {
    Idle,
    Hovered,
    Focused,
    Selected,
    Pressed,
    Disabled,
}

data class ChimahonReaderOcrOverlayInteractionState(
    val selectedBoxId: String? = null,
    val hoveredBoxId: String? = null,
    val focusedBoxId: String? = null,
    val pressedBoxId: String? = null,
    val disabledBoxIds: Set<String> = emptySet(),
    val hiddenBoxIds: Set<String> = emptySet(),
) {
    fun visualStateFor(boxId: String): ChimahonReaderOcrBoxVisualState {
        return when (boxId) {
            in disabledBoxIds -> ChimahonReaderOcrBoxVisualState.Disabled
            pressedBoxId -> ChimahonReaderOcrBoxVisualState.Pressed
            selectedBoxId -> ChimahonReaderOcrBoxVisualState.Selected
            focusedBoxId -> ChimahonReaderOcrBoxVisualState.Focused
            hoveredBoxId -> ChimahonReaderOcrBoxVisualState.Hovered
            else -> ChimahonReaderOcrBoxVisualState.Idle
        }
    }

    fun isVisible(boxId: String): Boolean {
        return boxId !in hiddenBoxIds
    }

    fun select(boxId: String?): ChimahonReaderOcrOverlayInteractionState {
        return copy(selectedBoxId = boxId, focusedBoxId = boxId, pressedBoxId = null)
    }

    fun hover(boxId: String?): ChimahonReaderOcrOverlayInteractionState {
        return copy(hoveredBoxId = boxId)
    }

    fun focus(boxId: String?): ChimahonReaderOcrOverlayInteractionState {
        return copy(focusedBoxId = boxId)
    }

    fun press(boxId: String?): ChimahonReaderOcrOverlayInteractionState {
        return copy(pressedBoxId = boxId)
    }

    fun clearTransient(): ChimahonReaderOcrOverlayInteractionState {
        return copy(hoveredBoxId = null, pressedBoxId = null)
    }
}

data class ChimahonReaderOcrOverlayBoxUiModel(
    val id: String,
    val blockId: String,
    val level: ChimahonReaderOcrBoxLevel,
    val text: String,
    val rect: ChimahonLookupRect,
    val selectionRects: List<ChimahonLookupSelectionRect> = emptyList(),
    val rotationDegrees: Float = 0f,
    val writingDirection: ChimahonLookupWritingDirection = ChimahonLookupWritingDirection.Horizontal,
    val confidence: Float? = null,
    val sourceIndex: Int = 0,
    val lineIndex: Int? = null,
    val wordIndex: Int? = null,
    val visualState: ChimahonReaderOcrBoxVisualState = ChimahonReaderOcrBoxVisualState.Idle,
    val visible: Boolean = true,
    val anchor: ChimahonLookupMatchedTextAnchor = ChimahonLookupMatchedTextAnchor(
        lookupText = text,
        fullText = text,
        rect = rect,
        coordinateSpace = ChimahonLookupAnchorCoordinateSpace.NormalizedPage,
        writingDirection = writingDirection,
        selectionRects = selectionRects,
        sourceId = id,
    ),
) {
    val enabled: Boolean
        get() = visualState != ChimahonReaderOcrBoxVisualState.Disabled

    val selected: Boolean
        get() = visualState == ChimahonReaderOcrBoxVisualState.Selected

    val hovered: Boolean
        get() = visualState == ChimahonReaderOcrBoxVisualState.Hovered

    val focused: Boolean
        get() = visualState == ChimahonReaderOcrBoxVisualState.Focused

    val pressed: Boolean
        get() = visualState == ChimahonReaderOcrBoxVisualState.Pressed

    val active: Boolean
        get() = selected || hovered || focused || pressed
}

data class ChimahonReaderOcrSelectionUiModel(
    val boxId: String,
    val lookupText: String,
    val sentence: String,
    val sentenceOffset: Int = 0,
    val sentenceEndOffset: Int = sentenceOffset + lookupText.length,
    val anchor: ChimahonLookupMatchedTextAnchor,
    val selectedBoxIndex: Int = 0,
    val boxCount: Int = 0,
) {
    val hasLookupText: Boolean
        get() = lookupText.isNotBlank()

    val positionText: String?
        get() = boxCount
            .takeIf { it > 0 }
            ?.let { "${selectedBoxIndex.coerceIn(0, it - 1) + 1} / $it" }
}

data class ChimahonReaderOcrOverlayUiModel(
    val boxes: List<ChimahonReaderOcrOverlayBoxUiModel> = emptyList(),
    val interaction: ChimahonReaderOcrOverlayInteractionState = ChimahonReaderOcrOverlayInteractionState(),
    val selection: ChimahonReaderOcrSelectionUiModel? = null,
    val boxesVisible: Boolean = true,
    val boxMode: ChimahonReaderOcrBoxMode = ChimahonReaderOcrBoxMode.Blocks,
) {
    val visibleBoxes: List<ChimahonReaderOcrOverlayBoxUiModel>
        get() = boxes.filter { boxesVisible && it.visible }

    val selectedBox: ChimahonReaderOcrOverlayBoxUiModel?
        get() = boxes.firstOrNull { it.id == interaction.selectedBoxId }

    val selectedBoxIndex: Int
        get() = boxes.indexOfFirst { it.id == interaction.selectedBoxId }

    val hasPreviousBox: Boolean
        get() = selectedBoxIndex > 0

    val hasNextBox: Boolean
        get() = selectedBoxIndex >= 0 && selectedBoxIndex < boxes.lastIndex

    fun selectBox(boxId: String?): ChimahonReaderOcrOverlayUiModel {
        val selected = boxes.firstOrNull { it.id == boxId }
        return copy(
            interaction = interaction.select(selected?.id),
            selection = selected?.toReaderOcrSelectionUiModel(
                selectedBoxIndex = boxes.indexOf(selected),
                boxCount = boxes.size,
            ),
        )
    }

    fun selectRelative(delta: Int): ChimahonReaderOcrOverlayUiModel {
        if (boxes.isEmpty()) return this
        val start = selectedBoxIndex.takeIf { it >= 0 } ?: if (delta >= 0) -1 else boxes.size
        val nextIndex = (start + delta).coerceIn(0, boxes.lastIndex)
        return selectBox(boxes[nextIndex].id)
    }
}

fun ChimahonReaderOcrOverlayBoxUiModel.withInteraction(
    interaction: ChimahonReaderOcrOverlayInteractionState,
): ChimahonReaderOcrOverlayBoxUiModel {
    return copy(
        visualState = interaction.visualStateFor(id),
        visible = visible && interaction.isVisible(id),
    )
}

fun ChimahonReaderOcrOverlayBoxUiModel.toReaderOcrSelectionUiModel(
    selectedBoxIndex: Int = sourceIndex,
    boxCount: Int = 0,
): ChimahonReaderOcrSelectionUiModel {
    return ChimahonReaderOcrSelectionUiModel(
        boxId = id,
        lookupText = anchor.lookupText,
        sentence = anchor.fullText.ifBlank { text },
        sentenceOffset = anchor.matchedOffset,
        sentenceEndOffset = anchor.matchedOffset + anchor.matchedLength,
        anchor = anchor,
        selectedBoxIndex = selectedBoxIndex,
        boxCount = boxCount,
    )
}
