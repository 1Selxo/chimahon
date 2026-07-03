package app.chimahon.shared.readerocrui

import app.chimahon.shared.lookup.ChimahonLookupAnchorCoordinateSpace
import app.chimahon.shared.lookup.ChimahonLookupMatchedTextAnchor
import app.chimahon.shared.lookup.ChimahonLookupRect
import app.chimahon.shared.lookup.ChimahonLookupSelectionRect
import app.chimahon.shared.lookup.ChimahonLookupWritingDirection
import app.chimahon.shared.lookup.unionChimahonLookupRects

enum class ChimahonReaderOcrSelectionIntentKind {
    Replace,
    Add,
    Toggle,
    Range,
    Clear,
}

data class ChimahonReaderOcrSelectionIntent(
    val kind: ChimahonReaderOcrSelectionIntentKind,
    val boxId: String? = null,
    val pointer: ChimahonReaderOcrPoint? = null,
    val keyboard: Boolean = false,
)

data class ChimahonReaderOcrMultiSelectionState(
    val selectedBoxIds: List<String> = emptyList(),
    val primaryBoxId: String? = selectedBoxIds.lastOrNull(),
    val anchorBoxId: String? = selectedBoxIds.firstOrNull(),
    val focusedBoxId: String? = primaryBoxId,
    val hoveredBoxId: String? = null,
    val dragAnchorBoxId: String? = null,
) {
    val selectedBoxIdSet: Set<String>
        get() = selectedBoxIds.toSet()

    val selectedCount: Int
        get() = selectedBoxIds.size

    val hasSelection: Boolean
        get() = selectedBoxIds.isNotEmpty()

    fun contains(boxId: String): Boolean {
        return boxId in selectedBoxIdSet
    }

    fun selectOnly(boxId: String?): ChimahonReaderOcrMultiSelectionState {
        val ids = boxId?.let(::listOf).orEmpty()
        return copy(
            selectedBoxIds = ids,
            primaryBoxId = boxId,
            anchorBoxId = boxId,
            focusedBoxId = boxId,
            dragAnchorBoxId = null,
        )
    }

    fun add(boxId: String): ChimahonReaderOcrMultiSelectionState {
        val ids = selectedBoxIds.withoutDuplicate(boxId)
        return copy(
            selectedBoxIds = ids,
            primaryBoxId = boxId,
            anchorBoxId = anchorBoxId ?: boxId,
            focusedBoxId = boxId,
        )
    }

    fun remove(boxId: String): ChimahonReaderOcrMultiSelectionState {
        val ids = selectedBoxIds.filterNot { it == boxId }
        val primary = primaryBoxId.takeIf { it in ids } ?: ids.lastOrNull()
        val anchor = anchorBoxId.takeIf { it in ids } ?: ids.firstOrNull()
        return copy(
            selectedBoxIds = ids,
            primaryBoxId = primary,
            anchorBoxId = anchor,
            focusedBoxId = focusedBoxId.takeIf { it in ids } ?: primary,
        )
    }

    fun toggle(boxId: String): ChimahonReaderOcrMultiSelectionState {
        return if (boxId in selectedBoxIdSet) remove(boxId) else add(boxId)
    }

    fun selectRange(
        orderedBoxIds: List<String>,
        toBoxId: String,
    ): ChimahonReaderOcrMultiSelectionState {
        if (orderedBoxIds.isEmpty()) return clear()
        val fromId = anchorBoxId
            ?.takeIf { it in orderedBoxIds }
            ?: primaryBoxId?.takeIf { it in orderedBoxIds }
            ?: toBoxId
        val fromIndex = orderedBoxIds.indexOf(fromId)
        val toIndex = orderedBoxIds.indexOf(toBoxId)
        if (fromIndex < 0 || toIndex < 0) return selectOnly(toBoxId)
        val low = minOf(fromIndex, toIndex)
        val high = maxOf(fromIndex, toIndex)
        val ids = orderedBoxIds.subList(low, high + 1)
        return copy(
            selectedBoxIds = ids,
            primaryBoxId = toBoxId,
            anchorBoxId = fromId,
            focusedBoxId = toBoxId,
        )
    }

    fun focus(boxId: String?): ChimahonReaderOcrMultiSelectionState {
        return copy(focusedBoxId = boxId)
    }

    fun hover(boxId: String?): ChimahonReaderOcrMultiSelectionState {
        return copy(hoveredBoxId = boxId)
    }

    fun clear(): ChimahonReaderOcrMultiSelectionState {
        return ChimahonReaderOcrMultiSelectionState()
    }

    fun applyIntent(
        intent: ChimahonReaderOcrSelectionIntent,
        orderedBoxIds: List<String>,
    ): ChimahonReaderOcrMultiSelectionState {
        val boxId = intent.boxId
        return when (intent.kind) {
            ChimahonReaderOcrSelectionIntentKind.Replace -> selectOnly(boxId)
            ChimahonReaderOcrSelectionIntentKind.Add -> boxId?.let(::add) ?: this
            ChimahonReaderOcrSelectionIntentKind.Toggle -> boxId?.let(::toggle) ?: this
            ChimahonReaderOcrSelectionIntentKind.Range -> boxId?.let { selectRange(orderedBoxIds, it) } ?: this
            ChimahonReaderOcrSelectionIntentKind.Clear -> clear()
        }
    }
}

enum class ChimahonReaderOcrSelectionJoinMode {
    NaturalOrder,
    NewLineSeparated,
    SpaceSeparated,
}

data class ChimahonReaderOcrMultiSelectionUiModel(
    val selections: List<ChimahonReaderOcrSelectionUiModel> = emptyList(),
    val primaryBoxId: String? = selections.lastOrNull()?.boxId,
    val joinMode: ChimahonReaderOcrSelectionJoinMode = ChimahonReaderOcrSelectionJoinMode.NaturalOrder,
) {
    val boxIds: List<String>
        get() = selections.map { it.boxId }

    val selectedCount: Int
        get() = selections.size

    val hasSelection: Boolean
        get() = selections.isNotEmpty()

    val lookupText: String
        get() = selections.joinToString(separator = joinSeparator) { it.lookupText }.trim()

    val sentence: String
        get() = selections.joinToString(separator = joinSeparator) { it.sentence.ifBlank { it.lookupText } }.trim()

    val selectedBoxIndex: Int
        get() = selections.lastOrNull()?.selectedBoxIndex ?: 0

    val boxCount: Int
        get() = selections.lastOrNull()?.boxCount ?: selections.size

    val positionText: String?
        get() = when {
            selections.size > 1 -> "${selections.size} boxes"
            selections.size == 1 -> selections.first().positionText
            else -> null
        }

    val anchor: ChimahonLookupMatchedTextAnchor
        get() {
            val anchors = selections.map { it.anchor }
            val rect = anchors
                .map { it.rect }
                .unionChimahonLookupRects()
                ?: ChimahonLookupRect.Zero
            val selectionRects = anchors.flatMap { anchor ->
                anchor.selectionRects.ifEmpty {
                    listOf(ChimahonLookupSelectionRect(anchor.rect, anchor.coordinateSpace))
                }
            }
            val writingDirection = when {
                anchors.any { it.writingDirection == ChimahonLookupWritingDirection.Vertical } ->
                    ChimahonLookupWritingDirection.Vertical
                else -> ChimahonLookupWritingDirection.Horizontal
            }
            return ChimahonLookupMatchedTextAnchor(
                lookupText = lookupText,
                fullText = sentence.ifBlank { lookupText },
                matchedText = lookupText,
                matchedOffset = 0,
                matchedLength = lookupText.length,
                rect = rect,
                coordinateSpace = ChimahonLookupAnchorCoordinateSpace.NormalizedPage,
                writingDirection = writingDirection,
                selectionRects = selectionRects,
                sourceId = boxIds.joinToString(separator = ","),
            )
        }

    private val joinSeparator: String
        get() = when (joinMode) {
            ChimahonReaderOcrSelectionJoinMode.NaturalOrder -> ""
            ChimahonReaderOcrSelectionJoinMode.NewLineSeparated -> "\n"
            ChimahonReaderOcrSelectionJoinMode.SpaceSeparated -> " "
        }
}

fun ChimahonReaderOcrOverlayUiModel.toMultiSelectionUiModel(
    selectionState: ChimahonReaderOcrMultiSelectionState,
    joinMode: ChimahonReaderOcrSelectionJoinMode = ChimahonReaderOcrSelectionJoinMode.NaturalOrder,
): ChimahonReaderOcrMultiSelectionUiModel {
    val selectedIds = selectionState.selectedBoxIdSet
    val selectedBoxes = boxes.filter { it.id in selectedIds }
    return ChimahonReaderOcrMultiSelectionUiModel(
        selections = selectedBoxes.map { box ->
            box.toReaderOcrSelectionUiModel(
                selectedBoxIndex = boxes.indexOfFirst { it.id == box.id }.coerceAtLeast(0),
                boxCount = boxes.size,
            )
        },
        primaryBoxId = selectionState.primaryBoxId,
        joinMode = joinMode,
    )
}

fun ChimahonReaderOcrOverlayInteractionState.withMultiSelection(
    selectionState: ChimahonReaderOcrMultiSelectionState,
): ChimahonReaderOcrOverlayInteractionState {
    return copy(
        selectedBoxId = selectionState.primaryBoxId,
        focusedBoxId = selectionState.focusedBoxId,
        hoveredBoxId = selectionState.hoveredBoxId ?: hoveredBoxId,
    )
}

fun ChimahonReaderOcrTextBoxUiModel.withMultiSelection(
    selectionState: ChimahonReaderOcrMultiSelectionState,
): ChimahonReaderOcrTextBoxUiModel {
    return copy(
        selected = id in selectionState.selectedBoxIdSet,
        hovered = id == selectionState.hoveredBoxId || hovered,
        focused = id == selectionState.focusedBoxId || focused,
    )
}

private fun List<String>.withoutDuplicate(value: String): List<String> {
    return if (value in this) this else this + value
}
