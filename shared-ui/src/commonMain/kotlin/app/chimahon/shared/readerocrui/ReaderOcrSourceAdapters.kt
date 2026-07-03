package app.chimahon.shared.readerocrui

import app.chimahon.shared.ChimahonReaderOcrBlock
import app.chimahon.shared.ChimahonReaderOcrLineGeometry
import app.chimahon.shared.lookup.ChimahonLookupAnchorCoordinateSpace
import app.chimahon.shared.lookup.ChimahonLookupMatchedTextAnchor
import app.chimahon.shared.lookup.ChimahonLookupRect
import app.chimahon.shared.lookup.ChimahonLookupSelectionRect
import app.chimahon.shared.lookup.ChimahonLookupWritingDirection
import app.chimahon.shared.lookup.chimahonLookupRectFromEdges

data class ChimahonReaderOcrBlockAdapterOptions(
    val idPrefix: String = "glens",
    val boxMode: ChimahonReaderOcrBoxMode = ChimahonReaderOcrBoxMode.Blocks,
    val pageTransform: ChimahonReaderOcrPageTransform = ChimahonReaderOcrPageTransform.Normal,
    val boxScaleX: Float = 1f,
    val boxScaleY: Float = 1f,
    val includeHiddenText: Boolean = false,
)

fun List<ChimahonReaderOcrBlock>.toReaderOcrOverlayUiModel(
    options: ChimahonReaderOcrBlockAdapterOptions = ChimahonReaderOcrBlockAdapterOptions(),
    interaction: ChimahonReaderOcrOverlayInteractionState = ChimahonReaderOcrOverlayInteractionState(),
    multiSelection: ChimahonReaderOcrMultiSelectionState = ChimahonReaderOcrMultiSelectionState(),
): ChimahonReaderOcrOverlayUiModel {
    val boxes = toReaderOcrOverlayBoxes(
        options = options,
        interaction = interaction,
        multiSelection = multiSelection,
    )
    val overlay = ChimahonReaderOcrOverlayUiModel(
        boxes = boxes,
        interaction = interaction.withMultiSelection(multiSelection),
        boxesVisible = true,
        boxMode = options.boxMode,
    )
    val selection = overlay.toMultiSelectionUiModel(multiSelection)
    return overlay.copy(
        selection = selection.selections.lastOrNull(),
    )
}

fun List<ChimahonReaderOcrBlock>.toReaderOcrTextBoxes(
    options: ChimahonReaderOcrBlockAdapterOptions = ChimahonReaderOcrBlockAdapterOptions(),
    multiSelection: ChimahonReaderOcrMultiSelectionState = ChimahonReaderOcrMultiSelectionState(),
): List<ChimahonReaderOcrTextBoxUiModel> {
    return flatMapIndexed { blockIndex, block ->
        block.toReaderOcrBoxParts(blockIndex, options).map { part ->
            part.toTextBoxUiModel(multiSelection)
        }
    }
}

fun List<ChimahonReaderOcrBlock>.toReaderOcrOverlayBoxes(
    options: ChimahonReaderOcrBlockAdapterOptions = ChimahonReaderOcrBlockAdapterOptions(),
    interaction: ChimahonReaderOcrOverlayInteractionState = ChimahonReaderOcrOverlayInteractionState(),
    multiSelection: ChimahonReaderOcrMultiSelectionState = ChimahonReaderOcrMultiSelectionState(),
): List<ChimahonReaderOcrOverlayBoxUiModel> {
    return flatMapIndexed { blockIndex, block ->
        block.toReaderOcrBoxParts(blockIndex, options).map { part ->
            part.toOverlayBoxUiModel(interaction, multiSelection)
        }
    }
}

fun ChimahonReaderOcrBlock.toReaderOcrTextBoxUiModel(
    index: Int,
    options: ChimahonReaderOcrBlockAdapterOptions = ChimahonReaderOcrBlockAdapterOptions(),
    multiSelection: ChimahonReaderOcrMultiSelectionState = ChimahonReaderOcrMultiSelectionState(),
): ChimahonReaderOcrTextBoxUiModel? {
    return toReaderOcrBoxParts(index, options.copy(boxMode = ChimahonReaderOcrBoxMode.Blocks))
        .firstOrNull { it.level == ChimahonReaderOcrBoxLevel.Block }
        ?.toTextBoxUiModel(multiSelection)
}

fun ChimahonReaderOcrTextBoxUiModel.toOverlayBoxUiModel(): ChimahonReaderOcrOverlayBoxUiModel {
    val sourceAnchor = anchor ?: ChimahonLookupMatchedTextAnchor(
        lookupText = text,
        fullText = text,
        rect = rect,
        coordinateSpace = ChimahonLookupAnchorCoordinateSpace.NormalizedPage,
        writingDirection = writingDirection,
        selectionRects = lineRects.map {
            ChimahonLookupSelectionRect(it, ChimahonLookupAnchorCoordinateSpace.NormalizedPage)
        },
        sourceId = id,
    )
    return ChimahonReaderOcrOverlayBoxUiModel(
        id = id,
        blockId = sourceAnchor.sourceId ?: id,
        level = boxLevel,
        text = text,
        rect = rect,
        selectionRects = sourceAnchor.selectionRects,
        rotationDegrees = rotationDegrees,
        writingDirection = writingDirection,
        confidence = confidence,
        sourceIndex = sourceIndex,
        lineIndex = lineIndex,
        wordIndex = wordIndex,
        visualState = visualState,
        visible = visible,
        anchor = sourceAnchor,
    )
}

fun ChimahonReaderOcrOverlayBoxUiModel.toTextBoxUiModel(): ChimahonReaderOcrTextBoxUiModel {
    return ChimahonReaderOcrTextBoxUiModel(
        id = id,
        text = text,
        rect = rect,
        lineRects = selectionRects.map { it.rect },
        confidence = confidence,
        writingDirection = writingDirection,
        selected = selected,
        visible = visible,
        hovered = hovered,
        focused = focused,
        pressed = pressed,
        enabled = enabled,
        rotationDegrees = rotationDegrees,
        boxLevel = level,
        sourceIndex = sourceIndex,
        lineIndex = lineIndex,
        wordIndex = wordIndex,
        anchor = anchor,
    )
}

fun String.readerOcrLookupCandidates(): List<String> {
    val normalized = trim()
    if (normalized.isBlank()) return emptyList()
    val candidates = mutableListOf<String>()
    if (normalized.length <= MAX_READER_OCR_LOOKUP_CANDIDATE_LENGTH &&
        normalized.count { it.isReaderOcrLookupChar() } >= 2
    ) {
        candidates += normalized
    }

    var tokenStart = -1
    normalized.forEachIndexed { index, char ->
        if (char.isReaderOcrLookupChar()) {
            if (tokenStart < 0) tokenStart = index
        } else if (tokenStart >= 0) {
            normalized.substring(tokenStart, index).takeIf { it.length >= 2 }?.let(candidates::add)
            tokenStart = -1
        }
    }
    if (tokenStart >= 0) {
        normalized.substring(tokenStart).takeIf { it.length >= 2 }?.let(candidates::add)
    }
    return candidates.distinct()
}

fun String.readerOcrLookupTextAt(
    charOffset: Int,
): String {
    if (isBlank()) return ""
    val start = charOffset.coerceIn(0, length)
    val builder = StringBuilder()
    var index = start
    while (index < length && this[index].isReaderOcrLookupChar()) {
        builder.append(this[index])
        index++
    }
    return builder.toString().trim()
}

private data class ReaderOcrBoxPart(
    val id: String,
    val blockId: String,
    val level: ChimahonReaderOcrBoxLevel,
    val text: String,
    val fullText: String,
    val rect: ChimahonLookupRect,
    val lineRects: List<ChimahonLookupRect>,
    val rotationDegrees: Float,
    val writingDirection: ChimahonLookupWritingDirection,
    val sourceIndex: Int,
    val lineIndex: Int? = null,
    val wordIndex: Int? = null,
    val matchedOffset: Int = 0,
)

private data class ReaderOcrLinePart(
    val sourceLineIndex: Int,
    val text: String,
    val textOffset: Int,
    val rect: ChimahonLookupRect,
    val rotationDegrees: Float,
)

private data class ReaderOcrTokenRange(
    val text: String,
    val start: Int,
    val end: Int,
)

private fun ChimahonReaderOcrBlock.toReaderOcrBoxParts(
    index: Int,
    options: ChimahonReaderOcrBlockAdapterOptions,
): List<ReaderOcrBoxPart> {
    val fullText = orderedFullText().ifBlank { lines.joinToString(separator = "") }
    if (!options.includeHiddenText && fullText.isBlank()) return emptyList()
    val blockId = "${options.idPrefix}-block-$index-${fullText.hashCode()}"
    val writingDirection = if (vertical) {
        ChimahonLookupWritingDirection.Vertical
    } else {
        ChimahonLookupWritingDirection.Horizontal
    }
    val rawBlockRect = normalizedLookupRect(xmin, ymin, xmax, ymax) ?: return emptyList()
    val blockGeometry = ChimahonReaderOcrBoxGeometry(
        rect = rawBlockRect,
        writingDirection = writingDirection,
    )
        .transformedForPage(options.pageTransform)
        ?.scaledForHitTarget(options.boxScaleX, options.boxScaleY)
        ?: return emptyList()
    val lineParts = toReaderOcrLineParts(
        rawBlockRect = rawBlockRect,
        fullText = fullText,
        options = options,
    )
    return buildList {
        if (options.boxMode == ChimahonReaderOcrBoxMode.Blocks) {
            add(
                ReaderOcrBoxPart(
                    id = blockId,
                    blockId = blockId,
                    level = ChimahonReaderOcrBoxLevel.Block,
                    text = fullText,
                    fullText = fullText,
                    rect = blockGeometry.rect,
                    lineRects = lineParts.map { it.rect },
                    rotationDegrees = blockGeometry.rotationDegrees,
                    writingDirection = writingDirection,
                    sourceIndex = index,
                ),
            )
        }
        if (options.boxMode == ChimahonReaderOcrBoxMode.Lines) {
            lineParts.forEach { line ->
                add(
                    ReaderOcrBoxPart(
                        id = "$blockId-line-${line.sourceLineIndex}",
                        blockId = blockId,
                        level = ChimahonReaderOcrBoxLevel.Line,
                        text = line.text,
                        fullText = fullText,
                        rect = line.rect,
                        lineRects = listOf(line.rect),
                        rotationDegrees = line.rotationDegrees,
                        writingDirection = writingDirection,
                        sourceIndex = index,
                        lineIndex = line.sourceLineIndex,
                        matchedOffset = line.textOffset,
                    ),
                )
            }
        }
        if (options.boxMode == ChimahonReaderOcrBoxMode.Words) {
            var wordIndex = 0
            lineParts.forEach { line ->
                line.text.readerOcrTokenRanges().forEach { token ->
                    token.toTokenRect(line.rect, line.text.length, vertical)?.let { tokenRect ->
                        add(
                            ReaderOcrBoxPart(
                                id = "$blockId-line-${line.sourceLineIndex}-word-$wordIndex",
                                blockId = blockId,
                                level = ChimahonReaderOcrBoxLevel.Word,
                                text = token.text,
                                fullText = fullText,
                                rect = tokenRect,
                                lineRects = listOf(tokenRect),
                                rotationDegrees = line.rotationDegrees,
                                writingDirection = writingDirection,
                                sourceIndex = index,
                                lineIndex = line.sourceLineIndex,
                                wordIndex = wordIndex,
                                matchedOffset = line.textOffset + token.start,
                            ),
                        )
                        wordIndex += 1
                    }
                }
            }
        }
    }
}

private fun ChimahonReaderOcrBlock.toReaderOcrLineParts(
    rawBlockRect: ChimahonLookupRect,
    fullText: String,
    options: ChimahonReaderOcrBlockAdapterOptions,
): List<ReaderOcrLinePart> {
    val orderedIndices = orderedLineIndices()
    var textOffset = 0
    return orderedIndices.mapNotNull { sourceIndex ->
        val lineText = lines.getOrNull(sourceIndex).orEmpty()
        val currentTextOffset = textOffset
        textOffset += lineText.length
        val geometry = lineGeometries?.getOrNull(sourceIndex)
        val rawRect = geometry?.let { normalizedLookupRect(it.xmin, it.ymin, it.xmax, it.ymax) }
            ?: fallbackLineRect(
                blockRect = rawBlockRect,
                orderedIndex = orderedIndices.indexOf(sourceIndex),
                lineCount = orderedIndices.size,
                vertical = vertical,
            )
        val transformedGeometry = ChimahonReaderOcrBoxGeometry(
            rect = rawRect,
            rotationDegrees = geometry?.rotation ?: 0f,
            writingDirection = if (vertical) {
                ChimahonLookupWritingDirection.Vertical
            } else {
                ChimahonLookupWritingDirection.Horizontal
            },
        )
            .transformedForPage(options.pageTransform)
            ?.scaledForHitTarget(options.boxScaleX, options.boxScaleY)
            ?: return@mapNotNull null
        val part = ReaderOcrLinePart(
            sourceLineIndex = sourceIndex,
            text = lineText,
            textOffset = currentTextOffset.coerceIn(0, fullText.length),
            rect = transformedGeometry.rect,
            rotationDegrees = transformedGeometry.rotationDegrees,
        )
        part
    }
}

private fun ReaderOcrBoxPart.toTextBoxUiModel(
    multiSelection: ChimahonReaderOcrMultiSelectionState,
): ChimahonReaderOcrTextBoxUiModel {
    val anchor = toAnchor()
    return ChimahonReaderOcrTextBoxUiModel(
        id = id,
        text = text,
        rect = rect,
        lineRects = lineRects,
        writingDirection = writingDirection,
        selected = id in multiSelection.selectedBoxIdSet,
        hovered = id == multiSelection.hoveredBoxId,
        focused = id == multiSelection.focusedBoxId,
        rotationDegrees = rotationDegrees,
        boxLevel = level,
        sourceIndex = sourceIndex,
        lineIndex = lineIndex,
        wordIndex = wordIndex,
        anchor = anchor,
    )
}

private fun ReaderOcrBoxPart.toOverlayBoxUiModel(
    interaction: ChimahonReaderOcrOverlayInteractionState,
    multiSelection: ChimahonReaderOcrMultiSelectionState,
): ChimahonReaderOcrOverlayBoxUiModel {
    val visualState = when {
        id in interaction.disabledBoxIds -> ChimahonReaderOcrBoxVisualState.Disabled
        id == interaction.pressedBoxId -> ChimahonReaderOcrBoxVisualState.Pressed
        id in multiSelection.selectedBoxIdSet -> ChimahonReaderOcrBoxVisualState.Selected
        id == multiSelection.focusedBoxId || id == interaction.focusedBoxId -> ChimahonReaderOcrBoxVisualState.Focused
        id == multiSelection.hoveredBoxId || id == interaction.hoveredBoxId -> ChimahonReaderOcrBoxVisualState.Hovered
        else -> interaction.visualStateFor(id)
    }
    return ChimahonReaderOcrOverlayBoxUiModel(
        id = id,
        blockId = blockId,
        level = level,
        text = text,
        rect = rect,
        selectionRects = lineRects.map {
            ChimahonLookupSelectionRect(it, ChimahonLookupAnchorCoordinateSpace.NormalizedPage)
        },
        rotationDegrees = rotationDegrees,
        writingDirection = writingDirection,
        sourceIndex = sourceIndex,
        lineIndex = lineIndex,
        wordIndex = wordIndex,
        visualState = visualState,
        visible = interaction.isVisible(id),
        anchor = toAnchor(),
    )
}

private fun ReaderOcrBoxPart.toAnchor(): ChimahonLookupMatchedTextAnchor {
    return ChimahonLookupMatchedTextAnchor(
        lookupText = text,
        fullText = fullText.ifBlank { text },
        matchedText = text,
        matchedOffset = matchedOffset.coerceIn(0, fullText.length.coerceAtLeast(0)),
        matchedLength = text.length,
        rect = rect,
        coordinateSpace = ChimahonLookupAnchorCoordinateSpace.NormalizedPage,
        writingDirection = writingDirection,
        selectionRects = lineRects.map {
            ChimahonLookupSelectionRect(it, ChimahonLookupAnchorCoordinateSpace.NormalizedPage)
        },
        sourceId = id,
    )
}

private fun ChimahonReaderOcrBlock.orderedFullText(): String {
    return orderedLineIndices().joinToString(separator = "") { index -> lines.getOrNull(index).orEmpty() }
}

private fun ChimahonReaderOcrBlock.orderedLineIndices(): List<Int> {
    val geometries = lineGeometries
    if (lines.size <= 1 || geometries == null || geometries.size != lines.size) {
        return lines.indices.toList()
    }
    return if (vertical) {
        lines.indices.sortedWith(
            compareByDescending<Int> { geometries[it].centerX }
                .thenBy { geometries[it].ymin },
        )
    } else {
        lines.indices.sortedWith(
            compareBy<Int> { geometries[it].centerY }
                .thenBy { geometries[it].xmin },
        )
    }
}

private fun String.readerOcrTokenRanges(): List<ReaderOcrTokenRange> {
    val ranges = mutableListOf<ReaderOcrTokenRange>()
    var tokenStart = -1
    forEachIndexed { index, char ->
        if (char.isReaderOcrLookupChar()) {
            if (tokenStart < 0) tokenStart = index
        } else if (tokenStart >= 0) {
            ranges += ReaderOcrTokenRange(
                text = substring(tokenStart, index),
                start = tokenStart,
                end = index,
            )
            tokenStart = -1
        }
    }
    if (tokenStart >= 0) {
        ranges += ReaderOcrTokenRange(
            text = substring(tokenStart),
            start = tokenStart,
            end = length,
        )
    }
    return ranges.filter { it.text.length >= 1 }
}

private fun ReaderOcrTokenRange.toTokenRect(
    lineRect: ChimahonLookupRect,
    lineLength: Int,
    vertical: Boolean,
): ChimahonLookupRect? {
    val safeLineLength = lineLength.coerceAtLeast(end).coerceAtLeast(1)
    val startFraction = (start.toFloat() / safeLineLength.toFloat()).coerceIn(0f, 1f)
    val endFraction = (end.toFloat() / safeLineLength.toFloat())
        .coerceAtLeast(startFraction + MIN_READER_OCR_TOKEN_FRACTION)
        .coerceIn(0f, 1f)
    return if (vertical) {
        chimahonLookupRectFromEdges(
            left = lineRect.left,
            top = lineRect.top + lineRect.height * startFraction,
            right = lineRect.right,
            bottom = lineRect.top + lineRect.height * endFraction,
            coerceToUnit = true,
        )
    } else {
        chimahonLookupRectFromEdges(
            left = lineRect.left + lineRect.width * startFraction,
            top = lineRect.top,
            right = lineRect.left + lineRect.width * endFraction,
            bottom = lineRect.bottom,
            coerceToUnit = true,
        )
    }
}

private fun fallbackLineRect(
    blockRect: ChimahonLookupRect,
    orderedIndex: Int,
    lineCount: Int,
    vertical: Boolean,
): ChimahonLookupRect {
    val safeCount = lineCount.coerceAtLeast(1)
    return if (vertical) {
        val columnWidth = blockRect.width / safeCount
        val right = blockRect.right - orderedIndex * columnWidth
        ChimahonLookupRect(
            x = (right - columnWidth).coerceIn(blockRect.left, blockRect.right),
            y = blockRect.top,
            width = columnWidth,
            height = blockRect.height,
        )
    } else {
        val rowHeight = blockRect.height / safeCount
        ChimahonLookupRect(
            x = blockRect.left,
            y = blockRect.top + orderedIndex * rowHeight,
            width = blockRect.width,
            height = rowHeight,
        )
    }
}

private fun normalizedLookupRect(
    xmin: Float,
    ymin: Float,
    xmax: Float,
    ymax: Float,
): ChimahonLookupRect? {
    return chimahonLookupRectFromEdges(
        left = xmin,
        top = ymin,
        right = xmax,
        bottom = ymax,
        coerceToUnit = true,
    )
}

private val ChimahonReaderOcrLineGeometry.centerX: Float
    get() = (xmin + xmax) / 2f

private val ChimahonReaderOcrLineGeometry.centerY: Float
    get() = (ymin + ymax) / 2f

private fun Char.isReaderOcrLookupChar(): Boolean {
    return !isWhitespace() && (isLetterOrDigit() || isReaderOcrCjkOrKana())
}

private fun Char.isReaderOcrCjkOrKana(): Boolean {
    return code in 0x3040..0x30FF ||
        code in 0x3400..0x4DBF ||
        code in 0x4E00..0x9FFF ||
        code in 0xAC00..0xD7AF
}

private const val MAX_READER_OCR_LOOKUP_CANDIDATE_LENGTH = 48
private const val MIN_READER_OCR_TOKEN_FRACTION = 0.035f
