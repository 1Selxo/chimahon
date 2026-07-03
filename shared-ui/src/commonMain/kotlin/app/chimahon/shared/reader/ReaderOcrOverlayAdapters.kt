package app.chimahon.shared.reader

import app.chimahon.shared.ChimahonDictionarySettings
import app.chimahon.shared.ChimahonReaderOcrBlock
import app.chimahon.shared.lookup.ChimahonLookupAnchorCoordinateSpace
import app.chimahon.shared.lookup.ChimahonLookupMatchedTextAnchor
import app.chimahon.shared.lookup.ChimahonLookupRect
import app.chimahon.shared.lookup.ChimahonLookupSelectionRect
import app.chimahon.shared.lookup.ChimahonLookupWritingDirection
import app.chimahon.shared.lookup.chimahonLookupRectFromEdges
import app.chimahon.shared.lookup.unionChimahonLookupRects
import app.chimahon.shared.readerocrui.ChimahonReaderOcrBoxLevel
import app.chimahon.shared.readerocrui.ChimahonReaderOcrBoxMode
import app.chimahon.shared.readerocrui.ChimahonReaderOcrBoxVisualState
import app.chimahon.shared.readerocrui.ChimahonReaderOcrOverlayBoxUiModel
import app.chimahon.shared.readerocrui.ChimahonReaderOcrOverlayInteractionState
import app.chimahon.shared.readerocrui.ChimahonReaderOcrSelectionUiModel
import app.chimahon.shared.readerocrui.ChimahonReaderOcrTextBoxUiModel

enum class ReaderOcrOverlaySource(
    val idPrefix: String,
    val label: String,
) {
    Glens("glens", "GLens"),
    Vision("vision", "Vision"),
    Platform("platform", "Platform OCR"),
    Manual("manual", "Manual"),
    Synthetic("lookup", "Lookup"),
    Unknown("ocr", "OCR"),
}

sealed interface ReaderOcrPageTransform {
    data object Normal : ReaderOcrPageTransform
    data class Rotated(val clockwise: Boolean) : ReaderOcrPageTransform
    data class SplitWide(
        val rtl: Boolean,
        val gutterFraction: Float = DEFAULT_SPLIT_WIDE_GUTTER_FRACTION,
    ) : ReaderOcrPageTransform
    data class Cropped(
        val cropLeft: Float,
        val cropTop: Float,
        val cropRight: Float,
        val cropBottom: Float,
    ) : ReaderOcrPageTransform
    data class SplitAndMergeWide(
        val upperIsRight: Boolean = true,
    ) : ReaderOcrPageTransform
    data class MergedPages(
        val pageIndex: Int,
        val pageWidthPx: Int,
        val pageHeightPx: Int,
        val pairedPageWidthPx: Int,
        val pairedPageHeightPx: Int,
        val isLeftToRight: Boolean,
        val centerMarginPx: Int = DEFAULT_MERGED_PAGE_CENTER_MARGIN_PX,
    ) : ReaderOcrPageTransform
}

data class ReaderOcrOverlayGeometrySettings(
    val boxScaleX: Float = 1f,
    val boxScaleY: Float = 1f,
    val boxOpacity: Float = 1f,
) {
    val safeBoxScaleX: Float
        get() = boxScaleX.takeIf { it.isFinite() }?.coerceIn(0.50f, 2.20f) ?: 1f

    val safeBoxScaleY: Float
        get() = boxScaleY.takeIf { it.isFinite() }?.coerceIn(0.50f, 2.20f) ?: 1f

    val safeBoxOpacity: Float
        get() = boxOpacity.takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: 1f
}

data class ReaderOcrOverlayPixelRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    val width: Float get() = right - left
    val height: Float get() = bottom - top

    fun contains(x: Float, y: Float): Boolean {
        return x >= left && x <= right && y >= top && y <= bottom
    }

    fun toNormalizedPagePoint(x: Float, y: Float): ReaderOcrOverlayPoint? {
        if (!contains(x, y) || width <= 0f || height <= 0f) return null
        return ReaderOcrOverlayPoint(
            x = ((x - left) / width).coerceIn(0f, 1f),
            y = ((y - top) / height).coerceIn(0f, 1f),
        )
    }
}

data class ReaderOcrOverlayPoint(
    val x: Float,
    val y: Float,
)

data class ReaderOcrOverlayLine(
    val id: String,
    val text: String,
    val rect: ChimahonLookupRect,
    val rotationDegrees: Float = 0f,
    val lineIndex: Int = 0,
) {
    val centerX: Float get() = rect.centerX
    val centerY: Float get() = rect.centerY

    fun scaledRect(settings: ReaderOcrOverlayGeometrySettings): ChimahonLookupRect {
        return rect.scaledWithinUnitFromCenter(settings.safeBoxScaleX, settings.safeBoxScaleY)
    }
}

data class ReaderOcrOverlayBlock(
    val id: String,
    val source: ReaderOcrOverlaySource,
    val sourceIndex: Int,
    val rect: ChimahonLookupRect,
    val lines: List<String>,
    val lineBoxes: List<ReaderOcrOverlayLine> = emptyList(),
    val vertical: Boolean = false,
    val language: String = "",
    val confidence: Float? = null,
) {
    val fullText: String
        get() = lines.joinToString(separator = "")

    val orderedFullText: String
        get() = orderedLines().joinToString(separator = "")

    val writingDirection: ChimahonLookupWritingDirection
        get() = if (vertical) ChimahonLookupWritingDirection.Vertical else ChimahonLookupWritingDirection.Horizontal

    fun scaledRect(settings: ReaderOcrOverlayGeometrySettings): ChimahonLookupRect {
        return rect.scaledWithinUnitFromCenter(settings.safeBoxScaleX, settings.safeBoxScaleY)
    }
}

data class ReaderOcrOverlaySelection(
    val block: ReaderOcrOverlayBlock,
    val lookupText: String,
    val sentence: String,
    val sentenceOffset: Int,
    val sentenceEndOffset: Int,
    val anchorX: Float,
    val anchorY: Float,
    val anchorRect: ChimahonLookupRect,
    val selectionRects: List<ChimahonLookupSelectionRect>,
) {
    fun toLookupAnchor(): ChimahonLookupMatchedTextAnchor {
        return ChimahonLookupMatchedTextAnchor(
            lookupText = lookupText,
            fullText = sentence.ifBlank { lookupText },
            charOffset = sentenceOffset,
            matchedText = lookupText,
            matchedOffset = sentenceOffset,
            matchedLength = lookupText.length,
            rect = anchorRect,
            coordinateSpace = ChimahonLookupAnchorCoordinateSpace.NormalizedPage,
            writingDirection = block.writingDirection,
            selectionRects = selectionRects,
            sourceId = block.id,
        )
    }

    fun toSelectionUiModel(
        selectedBoxIndex: Int = block.sourceIndex,
        boxCount: Int = 0,
    ): ChimahonReaderOcrSelectionUiModel {
        return ChimahonReaderOcrSelectionUiModel(
            boxId = block.id,
            lookupText = lookupText,
            sentence = sentence,
            sentenceOffset = sentenceOffset,
            sentenceEndOffset = sentenceEndOffset,
            anchor = toLookupAnchor(),
            selectedBoxIndex = selectedBoxIndex,
            boxCount = boxCount,
        )
    }
}

fun ChimahonDictionarySettings.toReaderOcrOverlayGeometrySettings(): ReaderOcrOverlayGeometrySettings {
    return ReaderOcrOverlayGeometrySettings(
        boxScaleX = ocrBoxScaleXPercent.coerceIn(50, 220) / 100f,
        boxScaleY = ocrBoxScaleYPercent.coerceIn(50, 220) / 100f,
        boxOpacity = ocrBoxOpacityPercent.coerceIn(0, 100) / 100f,
    )
}

fun ChimahonReaderOcrBlock.toReaderOcrOverlayBlock(
    index: Int,
    source: ReaderOcrOverlaySource = ReaderOcrOverlaySource.Unknown,
    idPrefix: String = source.idPrefix,
): ReaderOcrOverlayBlock? {
    val safeLines = lines
        .mapIndexedNotNull { lineIndex, line ->
            line.trim().takeIf { it.isNotBlank() }?.let { lineIndex to it }
        }
    if (safeLines.isEmpty()) return null

    val sourceRect = chimahonLookupRectFromEdges(xmin, ymin, xmax, ymax)
    val sourceText = safeLines.joinToString(separator = "|") { it.second }
    val blockId = "$idPrefix-$index-${sourceText.hashCode()}"
    val lineRects = lineGeometries
        ?.takeIf { it.isNotEmpty() }
        ?.let { geometries ->
            safeLines.mapNotNull { (originalIndex, text) ->
                val geometry = geometries.getOrNull(originalIndex) ?: return@mapNotNull null
                val rect = chimahonLookupRectFromEdges(
                    geometry.xmin,
                    geometry.ymin,
                    geometry.xmax,
                    geometry.ymax,
                ) ?: return@mapNotNull null
                ReaderOcrOverlayLine(
                    id = "$blockId-line-$originalIndex",
                    text = text,
                    rect = rect,
                    rotationDegrees = geometry.rotation,
                    lineIndex = originalIndex,
                )
            }
        }
        .orEmpty()

    val rect = sourceRect ?: lineRects.map { it.rect }.unionChimahonLookupRects() ?: return null
    return ReaderOcrOverlayBlock(
        id = blockId,
        source = source,
        sourceIndex = index,
        rect = rect,
        lines = safeLines.map { it.second },
        lineBoxes = lineRects,
        vertical = vertical,
        language = language,
    )
}

fun List<ChimahonReaderOcrBlock>.toReaderOcrOverlayBlocks(
    source: ReaderOcrOverlaySource = ReaderOcrOverlaySource.Unknown,
    transform: ReaderOcrPageTransform = ReaderOcrPageTransform.Normal,
): List<ReaderOcrOverlayBlock> {
    return mapIndexedNotNull { index, block ->
        block.toReaderOcrOverlayBlock(index = index, source = source)
            ?.transformedForDisplay(transform)
    }
}

fun resolveReaderOcrDisplayedImageRect(
    canvasWidth: Float,
    canvasHeight: Float,
    imageAspectRatio: Float,
    cropToFill: Boolean,
): ReaderOcrOverlayPixelRect {
    val safeWidth = canvasWidth.coerceAtLeast(1f)
    val safeHeight = canvasHeight.coerceAtLeast(1f)
    val safeAspect = imageAspectRatio.takeIf { it.isFinite() && it > 0f } ?: 1f
    val containerAspect = safeWidth / safeHeight
    val scaleByWidth = if (cropToFill) containerAspect < safeAspect else containerAspect > safeAspect
    val imageWidth: Float
    val imageHeight: Float
    if (scaleByWidth) {
        imageWidth = safeWidth
        imageHeight = safeWidth / safeAspect
    } else {
        imageHeight = safeHeight
        imageWidth = safeHeight * safeAspect
    }
    val left = (safeWidth - imageWidth) / 2f
    val top = (safeHeight - imageHeight) / 2f
    return ReaderOcrOverlayPixelRect(
        left = left,
        top = top,
        right = left + imageWidth,
        bottom = top + imageHeight,
    )
}

fun Iterable<ReaderOcrOverlayBlock>.hitTestReaderOcrOverlayBlock(
    pageX: Float,
    pageY: Float,
    settings: ReaderOcrOverlayGeometrySettings = ReaderOcrOverlayGeometrySettings(),
): ReaderOcrOverlayBlock? {
    return lastOrNull { block -> block.scaledRect(settings).contains(pageX, pageY) }
}

fun ReaderOcrOverlayBlock.localPointForPagePoint(
    pageX: Float,
    pageY: Float,
    settings: ReaderOcrOverlayGeometrySettings = ReaderOcrOverlayGeometrySettings(),
): ReaderOcrOverlayPoint {
    val bounds = scaledRect(settings)
    return ReaderOcrOverlayPoint(
        x = ((pageX - bounds.left) / bounds.width.coerceAtLeast(MIN_NORMALIZED_EDGE)).coerceIn(0f, 1f),
        y = ((pageY - bounds.top) / bounds.height.coerceAtLeast(MIN_NORMALIZED_EDGE)).coerceIn(0f, 1f),
    )
}

fun ReaderOcrOverlayBlock.toReaderOcrOverlaySelection(
    pageX: Float? = null,
    pageY: Float? = null,
    localX: Float = DEFAULT_SELECTION_LOCAL_X,
    localY: Float = DEFAULT_SELECTION_LOCAL_Y,
    settings: ReaderOcrOverlayGeometrySettings = ReaderOcrOverlayGeometrySettings(),
): ReaderOcrOverlaySelection {
    val sentence = orderedFullText.ifBlank { lines.joinToString(separator = " ") }.trim()
    val offset = readerOcrCharOffset(
        block = this,
        pageX = pageX,
        pageY = pageY,
        localX = localX,
        localY = localY,
        settings = settings,
    ).coerceIn(0, sentence.length)
    val lookupString = extractReaderOcrLookupString(sentence, offset)
    val lookupOffset = if (lookupString.isBlank()) {
        offset
    } else {
        sentence.indexOf(lookupString, offset.coerceIn(0, sentence.length))
            .takeIf { it >= 0 }
            ?: sentence.indexOf(lookupString).takeIf { it >= 0 }
            ?: offset
    }
    val lookupEnd = (lookupOffset + lookupString.length).coerceIn(lookupOffset, sentence.length)
    val selectionRects = selectionHighlightRects(
        selectionStart = lookupOffset,
        selectionEnd = lookupEnd,
        settings = settings,
    ).map {
        ChimahonLookupSelectionRect(
            rect = it,
            coordinateSpace = ChimahonLookupAnchorCoordinateSpace.NormalizedPage,
        )
    }
    val anchorRect = selectionRects.firstOrNull()?.rect ?: scaledRect(settings)
    return ReaderOcrOverlaySelection(
        block = this,
        lookupText = lookupString,
        sentence = sentence.ifBlank { lookupString },
        sentenceOffset = lookupOffset,
        sentenceEndOffset = lookupEnd,
        anchorX = localX.coerceIn(0f, 1f),
        anchorY = localY.coerceIn(0f, 1f),
        anchorRect = anchorRect,
        selectionRects = selectionRects,
    )
}

fun ReaderOcrOverlayBlock.toReaderOcrTextBoxUiModel(
    interaction: ChimahonReaderOcrOverlayInteractionState = ChimahonReaderOcrOverlayInteractionState(),
    settings: ReaderOcrOverlayGeometrySettings = ReaderOcrOverlayGeometrySettings(),
): ChimahonReaderOcrTextBoxUiModel {
    val visualState = interaction.visualStateFor(id)
    val visible = interaction.isVisible(id)
    val rect = scaledRect(settings)
    return ChimahonReaderOcrTextBoxUiModel(
        id = id,
        text = orderedFullText.ifBlank { fullText },
        rect = rect,
        lineRects = orderedLineBoxes().map { it.scaledRect(settings) },
        confidence = confidence,
        writingDirection = writingDirection,
        selected = visualState == ChimahonReaderOcrBoxVisualState.Selected,
        visible = visible,
        hovered = visualState == ChimahonReaderOcrBoxVisualState.Hovered,
        focused = visualState == ChimahonReaderOcrBoxVisualState.Focused,
        pressed = visualState == ChimahonReaderOcrBoxVisualState.Pressed,
        enabled = visualState != ChimahonReaderOcrBoxVisualState.Disabled,
        boxLevel = ChimahonReaderOcrBoxLevel.Block,
        sourceIndex = sourceIndex,
        anchor = blockAnchor(
            id = id,
            text = orderedFullText.ifBlank { fullText },
            rect = rect,
            writingDirection = writingDirection,
        ),
    )
}

fun ReaderOcrOverlayBlock.toReaderOcrOverlayBoxes(
    boxMode: ChimahonReaderOcrBoxMode,
    interaction: ChimahonReaderOcrOverlayInteractionState = ChimahonReaderOcrOverlayInteractionState(),
    settings: ReaderOcrOverlayGeometrySettings = ReaderOcrOverlayGeometrySettings(),
): List<ChimahonReaderOcrOverlayBoxUiModel> {
    val boxes = when (boxMode) {
        ChimahonReaderOcrBoxMode.Blocks -> listOf(toBlockOverlayBox(settings))
        ChimahonReaderOcrBoxMode.Lines -> lineOverlayBoxes(settings).ifEmpty { listOf(toBlockOverlayBox(settings)) }
        ChimahonReaderOcrBoxMode.Words -> wordOverlayBoxes(settings).ifEmpty { listOf(toBlockOverlayBox(settings)) }
    }
    return boxes.map { box ->
        box.copy(
            visualState = interaction.visualStateFor(box.id),
            visible = box.visible && interaction.isVisible(box.id),
        )
    }
}

fun ReaderOcrOverlayBlock.transformedForDisplay(
    transform: ReaderOcrPageTransform,
): ReaderOcrOverlayBlock? {
    if (transform == ReaderOcrPageTransform.Normal) return this
    val transformedRect = rect.transformedReaderOcrRect(transform) ?: return null
    val transformedLines = lineBoxes.mapNotNull { line ->
        line.rect.transformedReaderOcrRect(transform)?.let { transformedLineRect ->
            line.copy(
                rect = transformedLineRect,
                rotationDegrees = line.rotationDegrees.transformedReaderOcrRotation(transform),
            )
        }
    }
    return copy(
        rect = transformedRect,
        lineBoxes = transformedLines,
    )
}

fun Iterable<ReaderOcrOverlayBlock>.transformedForDisplay(
    transform: ReaderOcrPageTransform,
): List<ReaderOcrOverlayBlock> {
    return mapNotNull { it.transformedForDisplay(transform) }
}

fun ReaderOcrOverlayBlock.orderedLines(): List<String> {
    return orderedLineIndices().map { lines[it] }
}

fun ReaderOcrOverlayBlock.orderedLineBoxes(): List<ReaderOcrOverlayLine> {
    if (lineBoxes.size != lines.size || lineBoxes.isEmpty()) return lineBoxes
    return orderedLineIndices().mapNotNull { index -> lineBoxes.getOrNull(index) }
}

private fun ReaderOcrOverlayBlock.toBlockOverlayBox(
    settings: ReaderOcrOverlayGeometrySettings,
): ChimahonReaderOcrOverlayBoxUiModel {
    val text = orderedFullText.ifBlank { fullText }
    val rect = scaledRect(settings)
    return ChimahonReaderOcrOverlayBoxUiModel(
        id = id,
        blockId = id,
        level = ChimahonReaderOcrBoxLevel.Block,
        text = text,
        rect = rect,
        selectionRects = orderedLineBoxes().map {
            ChimahonLookupSelectionRect(
                rect = it.scaledRect(settings),
                coordinateSpace = ChimahonLookupAnchorCoordinateSpace.NormalizedPage,
            )
        },
        writingDirection = writingDirection,
        confidence = confidence,
        sourceIndex = sourceIndex,
        anchor = blockAnchor(
            id = id,
            text = text,
            rect = rect,
            writingDirection = writingDirection,
        ),
    )
}

private fun ReaderOcrOverlayBlock.lineOverlayBoxes(
    settings: ReaderOcrOverlayGeometrySettings,
): List<ChimahonReaderOcrOverlayBoxUiModel> {
    return orderedLineBoxes().map { line ->
        val rect = line.scaledRect(settings)
        ChimahonReaderOcrOverlayBoxUiModel(
            id = line.id,
            blockId = id,
            level = ChimahonReaderOcrBoxLevel.Line,
            text = line.text,
            rect = rect,
            rotationDegrees = line.rotationDegrees,
            writingDirection = writingDirection,
            confidence = confidence,
            sourceIndex = sourceIndex,
            lineIndex = line.lineIndex,
            anchor = blockAnchor(
                id = line.id,
                text = line.text,
                rect = rect,
                writingDirection = writingDirection,
            ),
        )
    }
}

private fun ReaderOcrOverlayBlock.wordOverlayBoxes(
    settings: ReaderOcrOverlayGeometrySettings,
): List<ChimahonReaderOcrOverlayBoxUiModel> {
    val lines = orderedLineBoxes().ifEmpty {
        listOf(
            ReaderOcrOverlayLine(
                id = "$id-line-fallback",
                text = orderedFullText.ifBlank { fullText },
                rect = rect,
            ),
        )
    }
    return lines.flatMap { line ->
        line.text.readerOcrLookupSegments().map { segment ->
            val rect = line.segmentRect(segment.start, segment.end).scaledWithinUnitFromCenter(
                settings.safeBoxScaleX,
                settings.safeBoxScaleY,
            )
            val wordId = "${line.id}-word-${segment.ordinal}"
            ChimahonReaderOcrOverlayBoxUiModel(
                id = wordId,
                blockId = id,
                level = ChimahonReaderOcrBoxLevel.Word,
                text = segment.text,
                rect = rect,
                rotationDegrees = line.rotationDegrees,
                writingDirection = writingDirection,
                confidence = confidence,
                sourceIndex = sourceIndex,
                lineIndex = line.lineIndex,
                wordIndex = segment.ordinal,
                anchor = blockAnchor(
                    id = wordId,
                    text = segment.text,
                    rect = rect,
                    writingDirection = writingDirection,
                ),
            )
        }
    }
}

private fun blockAnchor(
    id: String,
    text: String,
    rect: ChimahonLookupRect,
    writingDirection: ChimahonLookupWritingDirection,
): ChimahonLookupMatchedTextAnchor {
    return ChimahonLookupMatchedTextAnchor(
        lookupText = text,
        fullText = text,
        rect = rect,
        coordinateSpace = ChimahonLookupAnchorCoordinateSpace.NormalizedPage,
        writingDirection = writingDirection,
        selectionRects = listOf(
            ChimahonLookupSelectionRect(
                rect = rect,
                coordinateSpace = ChimahonLookupAnchorCoordinateSpace.NormalizedPage,
            ),
        ),
        sourceId = id,
    )
}

private fun ReaderOcrOverlayBlock.orderedLineIndices(): List<Int> {
    if (lines.size <= 1 || lineBoxes.size != lines.size) {
        return lines.indices.toList()
    }
    return if (vertical) {
        lines.indices.sortedWith(
            compareByDescending<Int> { lineBoxes[it].centerX }
                .thenBy { lineBoxes[it].rect.top },
        )
    } else {
        lines.indices.sortedWith(
            compareBy<Int> { lineBoxes[it].centerY }
                .thenBy { lineBoxes[it].rect.left },
        )
    }
}

private fun ReaderOcrOverlayBlock.selectionHighlightRects(
    selectionStart: Int,
    selectionEnd: Int,
    settings: ReaderOcrOverlayGeometrySettings,
): List<ChimahonLookupRect> {
    if (lines.isEmpty() || lineBoxes.size != lines.size) return emptyList()
    val orderedIndices = orderedLineIndices()
    val totalLength = orderedFullText.length.coerceAtLeast(1)
    val safeStart = selectionStart.coerceIn(0, totalLength)
    val safeEnd = selectionEnd
        .coerceIn(safeStart, totalLength)
        .let { if (it == safeStart) (it + 1).coerceAtMost(totalLength) else it }
    var cursor = 0
    val rects = mutableListOf<ChimahonLookupRect>()
    orderedIndices.forEach { lineIndex ->
        val line = lines[lineIndex]
        val lineLength = line.length
        val lineStart = cursor
        val lineEnd = cursor + lineLength
        if (lineLength > 0 && safeStart < lineEnd && safeEnd > lineStart) {
            val overlapStart = maxOf(safeStart, lineStart)
            val overlapEnd = minOf(safeEnd, lineEnd)
            val startFraction = (overlapStart - lineStart).toFloat() / lineLength.toFloat()
            val endFraction = (overlapEnd - lineStart)
                .toFloat()
                .div(lineLength.toFloat())
                .coerceAtLeast(startFraction + MIN_SELECTION_FRACTION)
                .coerceAtMost(1f)
            val bounds = lineBoxes[lineIndex].scaledRect(settings)
            rects += if (vertical) {
                ChimahonLookupRect(
                    x = bounds.left,
                    y = bounds.top + bounds.height * startFraction,
                    width = bounds.width,
                    height = bounds.height * (endFraction - startFraction),
                )
            } else {
                ChimahonLookupRect(
                    x = bounds.left + bounds.width * startFraction,
                    y = bounds.top,
                    width = bounds.width * (endFraction - startFraction),
                    height = bounds.height,
                )
            }
        }
        cursor = lineEnd
    }
    return rects
}

private fun readerOcrCharOffset(
    block: ReaderOcrOverlayBlock,
    pageX: Float?,
    pageY: Float?,
    localX: Float,
    localY: Float,
    settings: ReaderOcrOverlayGeometrySettings,
): Int {
    if (
        pageX != null &&
        pageY != null &&
        block.lineBoxes.size == block.lines.size &&
        block.lines.isNotEmpty()
    ) {
        val orderedIndices = block.orderedLineIndices()
        var cursor = 0
        orderedIndices.forEach { lineIndex ->
            val line = block.lines[lineIndex]
            val bounds = block.lineBoxes[lineIndex].scaledRect(settings)
            if (line.isNotEmpty() && bounds.contains(pageX, pageY)) {
                val lineVertical = block.vertical ||
                    bounds.height / bounds.width.coerceAtLeast(MIN_NORMALIZED_EDGE) > VERTICAL_LINE_RATIO
                val charIndex = if (lineVertical) {
                    ((pageY - bounds.top) / bounds.height.coerceAtLeast(MIN_NORMALIZED_EDGE) * line.length).toInt()
                } else {
                    ((pageX - bounds.left) / bounds.width.coerceAtLeast(MIN_NORMALIZED_EDGE) * line.length).toInt()
                }.coerceIn(0, line.length - 1)
                return cursor + charIndex
            }
            cursor += line.length
        }
    }
    return readerUniformCharOffset(block, localX, localY)
}

private fun readerUniformCharOffset(
    block: ReaderOcrOverlayBlock,
    localX: Float,
    localY: Float,
): Int {
    val lines = block.orderedLines().ifEmpty { listOf(block.fullText) }
    if (lines.isEmpty()) return 0
    if (block.vertical) {
        val columnWidth = 1f / lines.size.coerceAtLeast(1)
        val maxChars = lines.maxOfOrNull { it.length }?.coerceAtLeast(1) ?: 1
        val rowHeight = 1f / maxChars.toFloat()
        val fromRight = (1f - localX).coerceIn(0f, 1f)
        val columnIndex = (fromRight / columnWidth)
            .toInt()
            .coerceIn(0, lines.lastIndex)
        val column = lines[columnIndex]
        val charIndex = (localY.coerceIn(0f, 0.999f) / rowHeight)
            .toInt()
            .coerceIn(0, column.length.coerceAtLeast(1) - 1)
        return lines.take(columnIndex).sumOf { it.length } + charIndex
    }

    val lineHeight = 1f / lines.size.coerceAtLeast(1)
    val lineIndex = (localY.coerceIn(0f, 0.999f) / lineHeight)
        .toInt()
        .coerceIn(0, lines.lastIndex)
    val line = lines[lineIndex]
    val charIndex = (localX.coerceIn(0f, 0.999f) * line.length.coerceAtLeast(1))
        .toInt()
        .coerceIn(0, line.length.coerceAtLeast(1) - 1)
    return lines.take(lineIndex).sumOf { it.length } + charIndex
}

private fun ReaderOcrOverlayLine.segmentRect(
    start: Int,
    end: Int,
): ChimahonLookupRect {
    val length = text.length.coerceAtLeast(1)
    val safeStart = start.coerceIn(0, (length - 1).coerceAtLeast(0))
    val safeEnd = end.coerceIn(safeStart + 1, length)
    val startFraction = safeStart.toFloat() / length.toFloat()
    val endFraction = safeEnd.toFloat() / length.toFloat()
    val vertical = rect.height / rect.width.coerceAtLeast(MIN_NORMALIZED_EDGE) > VERTICAL_LINE_RATIO
    return if (vertical) {
        ChimahonLookupRect(
            x = rect.left,
            y = rect.top + rect.height * startFraction,
            width = rect.width,
            height = rect.height * (endFraction - startFraction),
        )
    } else {
        ChimahonLookupRect(
            x = rect.left + rect.width * startFraction,
            y = rect.top,
            width = rect.width * (endFraction - startFraction),
            height = rect.height,
        )
    }
}

private fun ChimahonLookupRect.transformedReaderOcrRect(
    transform: ReaderOcrPageTransform,
): ChimahonLookupRect? {
    if (isEmpty) return null
    val points = listOf(
        ReaderOcrOverlayPoint(left, top),
        ReaderOcrOverlayPoint(right, top),
        ReaderOcrOverlayPoint(left, bottom),
        ReaderOcrOverlayPoint(right, bottom),
    ).map { it.transformedReaderOcrPoint(transform) }
    return chimahonLookupRectFromEdges(
        left = points.minOf { it.x },
        top = points.minOf { it.y },
        right = points.maxOf { it.x },
        bottom = points.maxOf { it.y },
    )
}

private fun ReaderOcrOverlayPoint.transformedReaderOcrPoint(
    transform: ReaderOcrPageTransform,
): ReaderOcrOverlayPoint {
    val mapped = when (transform) {
        ReaderOcrPageTransform.Normal -> this
        is ReaderOcrPageTransform.Rotated -> {
            if (transform.clockwise) {
                ReaderOcrOverlayPoint(1f - y, x)
            } else {
                ReaderOcrOverlayPoint(y, 1f - x)
            }
        }
        is ReaderOcrPageTransform.SplitWide -> {
            val gutter = transform.gutterFraction.coerceIn(0f, MAX_SPLIT_WIDE_GUTTER_FRACTION)
            val panelWidth = ((1f - gutter) / 2f).coerceAtLeast(MIN_NORMALIZED_EDGE)
            val panelHeight = (1f - gutter).coerceAtLeast(MIN_NORMALIZED_EDGE)
            val sourceLeftHalf = x <= 0.5f
            val sourceOffset = if (sourceLeftHalf) 0f else 0.5f
            val displayLeftPanel = if (transform.rtl) !sourceLeftHalf else sourceLeftHalf
            val panelStart = if (displayLeftPanel) 0f else panelWidth + gutter
            ReaderOcrOverlayPoint(
                x = panelStart + ((x - sourceOffset) * 2f).coerceIn(0f, 1f) * panelWidth,
                y = ((1f - panelHeight) / 2f) + y.coerceIn(0f, 1f) * panelHeight,
            )
        }
        is ReaderOcrPageTransform.Cropped -> {
            val cropLeft = transform.cropLeft.coerceIn(0f, 1f)
            val cropTop = transform.cropTop.coerceIn(0f, 1f)
            val cropRight = transform.cropRight.coerceIn(cropLeft + MIN_NORMALIZED_EDGE, 1f)
            val cropBottom = transform.cropBottom.coerceIn(cropTop + MIN_NORMALIZED_EDGE, 1f)
            ReaderOcrOverlayPoint(
                x = (x - cropLeft) / (cropRight - cropLeft).coerceAtLeast(MIN_NORMALIZED_EDGE),
                y = (y - cropTop) / (cropBottom - cropTop).coerceAtLeast(MIN_NORMALIZED_EDGE),
            )
        }
        is ReaderOcrPageTransform.SplitAndMergeWide -> {
            val isRightHalf = x >= 0.5f
            val xOffset = if (isRightHalf) 0.5f else 0f
            val upperHalf = if (transform.upperIsRight) isRightHalf else !isRightHalf
            ReaderOcrOverlayPoint(
                x = ((x - xOffset) * 2f).coerceIn(0f, 1f),
                y = (if (upperHalf) 0f else 0.5f) + y.coerceIn(0f, 1f) * 0.5f,
            )
        }
        is ReaderOcrPageTransform.MergedPages -> {
            val pageWidth = transform.pageWidthPx.coerceAtLeast(1).toFloat()
            val pageHeight = transform.pageHeightPx.coerceAtLeast(1).toFloat()
            val pairWidth = transform.pairedPageWidthPx.coerceAtLeast(1).toFloat()
            val pairHeight = transform.pairedPageHeightPx.coerceAtLeast(1).toFloat()
            val margin = transform.centerMarginPx.coerceAtLeast(0).toFloat()
            val totalWidth = pageWidth + margin + pairWidth
            val totalHeight = maxOf(pageHeight, pairHeight)
            val pageIsLeft = if (transform.isLeftToRight) {
                transform.pageIndex == 0
            } else {
                transform.pageIndex != 0
            }
            val xOffset = if (pageIsLeft) 0f else pairWidth + margin
            val yOffset = (totalHeight - pageHeight) / 2f
            ReaderOcrOverlayPoint(
                x = (x.coerceIn(0f, 1f) * pageWidth + xOffset) / totalWidth.coerceAtLeast(MIN_NORMALIZED_EDGE),
                y = (y.coerceIn(0f, 1f) * pageHeight + yOffset) / totalHeight.coerceAtLeast(MIN_NORMALIZED_EDGE),
            )
        }
    }
    return ReaderOcrOverlayPoint(
        x = mapped.x.coerceIn(0f, 1f),
        y = mapped.y.coerceIn(0f, 1f),
    )
}

private fun Float.transformedReaderOcrRotation(transform: ReaderOcrPageTransform): Float {
    return when (transform) {
        ReaderOcrPageTransform.Normal,
        is ReaderOcrPageTransform.SplitWide,
        is ReaderOcrPageTransform.Cropped,
        is ReaderOcrPageTransform.SplitAndMergeWide,
        is ReaderOcrPageTransform.MergedPages,
        -> this
        is ReaderOcrPageTransform.Rotated -> this + if (transform.clockwise) 90f else -90f
    }
}

private fun String.readerOcrLookupSegments(): List<ReaderOcrTextSegment> {
    if (isBlank()) return emptyList()
    val segments = mutableListOf<ReaderOcrTextSegment>()
    var tokenStart: Int? = null
    forEachIndexed { index, char ->
        if (isReaderOcrLookupStartChar(char)) {
            if (tokenStart == null) tokenStart = index
        } else {
            tokenStart?.let { start ->
                this.addReaderOcrSegment(segments, start, index)
                tokenStart = null
            }
        }
    }
    tokenStart?.let { start -> this.addReaderOcrSegment(segments, start, length) }
    return segments.ifEmpty {
        listOf(ReaderOcrTextSegment(text = trim(), start = 0, end = length, ordinal = 0))
    }
}

private fun String.addReaderOcrSegment(
    segments: MutableList<ReaderOcrTextSegment>,
    start: Int,
    end: Int,
) {
    val token = substring(start, end).trim()
    if (token.length >= 2) {
        segments += ReaderOcrTextSegment(
            text = token,
            start = start,
            end = end,
            ordinal = segments.size,
        )
    }
}

private fun extractReaderOcrLookupString(text: String, start: Int): String {
    if (text.isBlank()) return ""
    val result = StringBuilder()
    var index = start.coerceIn(0, text.length)
    while (index < text.length && isReaderOcrLookupStartChar(text[index])) {
        result.append(text[index])
        index++
    }
    return result.toString().trim()
}

private fun isReaderOcrLookupStartChar(char: Char): Boolean {
    return !char.isWhitespace() && (char.isLetterOrDigit() || char.isReaderCjkOrKana())
}

private fun Char.isReaderCjkOrKana(): Boolean {
    return code in 0x3040..0x30FF ||
        code in 0x3400..0x4DBF ||
        code in 0x4E00..0x9FFF ||
        code in 0xAC00..0xD7AF
}

private data class ReaderOcrTextSegment(
    val text: String,
    val start: Int,
    val end: Int,
    val ordinal: Int,
)

private const val DEFAULT_SPLIT_WIDE_GUTTER_FRACTION = 0.018f
private const val DEFAULT_MERGED_PAGE_CENTER_MARGIN_PX = 0
private const val MAX_SPLIT_WIDE_GUTTER_FRACTION = 0.12f
private const val DEFAULT_SELECTION_LOCAL_X = 0.08f
private const val DEFAULT_SELECTION_LOCAL_Y = 0.50f
private const val MIN_NORMALIZED_EDGE = 0.004f
private const val MIN_SELECTION_FRACTION = 0.04f
private const val VERTICAL_LINE_RATIO = 1.20f
