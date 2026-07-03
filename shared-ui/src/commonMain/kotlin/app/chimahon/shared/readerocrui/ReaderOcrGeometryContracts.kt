package app.chimahon.shared.readerocrui

import app.chimahon.shared.lookup.ChimahonLookupAnchorCoordinateSpace
import app.chimahon.shared.lookup.ChimahonLookupMatchedTextAnchor
import app.chimahon.shared.lookup.ChimahonLookupRect
import app.chimahon.shared.lookup.ChimahonLookupSelectionRect
import app.chimahon.shared.lookup.ChimahonLookupWritingDirection
import app.chimahon.shared.lookup.chimahonLookupRectFromEdges

data class ChimahonReaderOcrPoint(
    val x: Float,
    val y: Float,
) {
    fun clampedToUnit(): ChimahonReaderOcrPoint {
        return ChimahonReaderOcrPoint(
            x = x.coerceIn(0f, 1f),
            y = y.coerceIn(0f, 1f),
        )
    }
}

data class ChimahonReaderOcrPagePixelSize(
    val widthPx: Int,
    val heightPx: Int,
) {
    val safeWidthPx: Int
        get() = widthPx.coerceAtLeast(1)

    val safeHeightPx: Int
        get() = heightPx.coerceAtLeast(1)

    val aspectRatio: Float
        get() = safeWidthPx.toFloat() / safeHeightPx.toFloat()
}

sealed interface ChimahonReaderOcrPageTransform {
    data object Normal : ChimahonReaderOcrPageTransform

    data class Rotated(
        val clockwise: Boolean,
    ) : ChimahonReaderOcrPageTransform

    data class Cropped(
        val cropLeft: Float,
        val cropTop: Float,
        val cropRight: Float,
        val cropBottom: Float,
    ) : ChimahonReaderOcrPageTransform

    data class SplitWide(
        val rtl: Boolean,
        val gutterFraction: Float,
    ) : ChimahonReaderOcrPageTransform

    data class SplitAndMergeWide(
        val upperIsRight: Boolean = true,
    ) : ChimahonReaderOcrPageTransform

    data class MergedPages(
        val pageIndex: Int,
        val pageWidthPx: Int,
        val pageHeightPx: Int,
        val pairedPageWidthPx: Int,
        val pairedPageHeightPx: Int,
        val isLeftToRight: Boolean,
        val centerMarginPx: Int = 0,
    ) : ChimahonReaderOcrPageTransform
}

data class ChimahonReaderOcrBoxGeometry(
    val rect: ChimahonLookupRect,
    val selectionRects: List<ChimahonLookupSelectionRect> = emptyList(),
    val coordinateSpace: ChimahonLookupAnchorCoordinateSpace = ChimahonLookupAnchorCoordinateSpace.NormalizedPage,
    val rotationDegrees: Float = 0f,
    val writingDirection: ChimahonLookupWritingDirection = ChimahonLookupWritingDirection.Horizontal,
) {
    val normalized: Boolean
        get() = coordinateSpace == ChimahonLookupAnchorCoordinateSpace.NormalizedPage

    fun scaledForHitTarget(
        scaleX: Float,
        scaleY: Float,
    ): ChimahonReaderOcrBoxGeometry {
        if (!normalized) return this
        val scaledRect = rect.scaledWithinUnitFromCenter(scaleX, scaleY)
        val scaledSelectionRects = selectionRects.map { selection ->
            if (selection.coordinateSpace == ChimahonLookupAnchorCoordinateSpace.NormalizedPage) {
                selection.copy(rect = selection.rect.scaledWithinUnitFromCenter(scaleX, scaleY))
            } else {
                selection
            }
        }
        return copy(rect = scaledRect, selectionRects = scaledSelectionRects)
    }

    fun transformedForPage(
        transform: ChimahonReaderOcrPageTransform,
    ): ChimahonReaderOcrBoxGeometry? {
        if (!normalized || transform == ChimahonReaderOcrPageTransform.Normal) return this
        val transformedRect = rect.transformedForReaderOcrPage(transform) ?: return null
        val transformedSelectionRects = selectionRects.mapNotNull { it.transformedForReaderOcrPage(transform) }
        return copy(
            rect = transformedRect,
            selectionRects = transformedSelectionRects,
            rotationDegrees = rotationDegrees.transformedForReaderOcrPage(transform),
        )
    }

    fun toAnchor(
        lookupText: String,
        fullText: String = lookupText,
        matchedOffset: Int = 0,
        matchedText: String = lookupText,
        sourceId: String? = null,
    ): ChimahonLookupMatchedTextAnchor {
        return ChimahonLookupMatchedTextAnchor(
            lookupText = lookupText,
            fullText = fullText,
            matchedText = matchedText,
            matchedOffset = matchedOffset,
            matchedLength = matchedText.length,
            rect = rect,
            coordinateSpace = coordinateSpace,
            writingDirection = writingDirection,
            selectionRects = selectionRects.ifEmpty {
                listOf(ChimahonLookupSelectionRect(rect, coordinateSpace))
            },
            sourceId = sourceId,
        )
    }
}

fun ChimahonLookupRect.transformedForReaderOcrPage(
    transform: ChimahonReaderOcrPageTransform,
): ChimahonLookupRect? {
    if (isEmpty) return null
    if (transform == ChimahonReaderOcrPageTransform.Normal) return this
    val points = listOf(
        ChimahonReaderOcrPoint(left, top),
        ChimahonReaderOcrPoint(right, top),
        ChimahonReaderOcrPoint(left, bottom),
        ChimahonReaderOcrPoint(right, bottom),
    ).map { it.transformedForReaderOcrPage(transform).clampedToUnit() }

    return chimahonLookupRectFromEdges(
        left = points.minOf { it.x },
        top = points.minOf { it.y },
        right = points.maxOf { it.x },
        bottom = points.maxOf { it.y },
        coerceToUnit = true,
    )
}

fun ChimahonLookupSelectionRect.transformedForReaderOcrPage(
    transform: ChimahonReaderOcrPageTransform,
): ChimahonLookupSelectionRect? {
    if (coordinateSpace != ChimahonLookupAnchorCoordinateSpace.NormalizedPage) return this
    return rect.transformedForReaderOcrPage(transform)?.let { copy(rect = it) }
}

fun ChimahonReaderOcrPoint.transformedForReaderOcrPage(
    transform: ChimahonReaderOcrPageTransform,
): ChimahonReaderOcrPoint {
    val mapped = when (transform) {
        ChimahonReaderOcrPageTransform.Normal -> this
        is ChimahonReaderOcrPageTransform.Rotated -> {
            if (transform.clockwise) {
                ChimahonReaderOcrPoint(1f - y, x)
            } else {
                ChimahonReaderOcrPoint(y, 1f - x)
            }
        }
        is ChimahonReaderOcrPageTransform.Cropped -> {
            val cropLeft = transform.cropLeft.coerceIn(0f, 1f - MIN_READER_OCR_EDGE)
            val cropTop = transform.cropTop.coerceIn(0f, 1f - MIN_READER_OCR_EDGE)
            val cropRight = transform.cropRight.coerceIn(cropLeft + MIN_READER_OCR_EDGE, 1f)
            val cropBottom = transform.cropBottom.coerceIn(cropTop + MIN_READER_OCR_EDGE, 1f)
            ChimahonReaderOcrPoint(
                x = (x - cropLeft) / (cropRight - cropLeft).coerceAtLeast(MIN_READER_OCR_EDGE),
                y = (y - cropTop) / (cropBottom - cropTop).coerceAtLeast(MIN_READER_OCR_EDGE),
            )
        }
        is ChimahonReaderOcrPageTransform.SplitWide -> {
            val gutter = transform.gutterFraction.coerceIn(0f, MAX_READER_OCR_SPLIT_GUTTER)
            val panelWidth = ((1f - gutter) / 2f).coerceAtLeast(MIN_READER_OCR_EDGE)
            val panelHeight = (1f - gutter).coerceAtLeast(MIN_READER_OCR_EDGE)
            val sourceLeftHalf = x <= 0.5f
            val sourceOffset = if (sourceLeftHalf) 0f else 0.5f
            val displayLeftPanel = if (transform.rtl) !sourceLeftHalf else sourceLeftHalf
            val panelStart = if (displayLeftPanel) 0f else panelWidth + gutter
            ChimahonReaderOcrPoint(
                x = panelStart + ((x - sourceOffset) * 2f).coerceIn(0f, 1f) * panelWidth,
                y = ((1f - panelHeight) / 2f) + y.coerceIn(0f, 1f) * panelHeight,
            )
        }
        is ChimahonReaderOcrPageTransform.SplitAndMergeWide -> {
            val sourceRightHalf = x >= 0.5f
            val sourceOffset = if (sourceRightHalf) 0.5f else 0f
            val upperHalf = if (transform.upperIsRight) sourceRightHalf else !sourceRightHalf
            ChimahonReaderOcrPoint(
                x = ((x - sourceOffset) * 2f).coerceIn(0f, 1f),
                y = (if (upperHalf) 0f else 0.5f) + y.coerceIn(0f, 1f) * 0.5f,
            )
        }
        is ChimahonReaderOcrPageTransform.MergedPages -> {
            val pageWidth = transform.pageWidthPx.coerceAtLeast(1).toFloat()
            val pageHeight = transform.pageHeightPx.coerceAtLeast(1).toFloat()
            val pairedWidth = transform.pairedPageWidthPx.coerceAtLeast(1).toFloat()
            val pairedHeight = transform.pairedPageHeightPx.coerceAtLeast(1).toFloat()
            val margin = transform.centerMarginPx.coerceAtLeast(0).toFloat()
            val totalWidth = pageWidth + margin + pairedWidth
            val totalHeight = maxOf(pageHeight, pairedHeight)
            val pageIsLeft = if (transform.isLeftToRight) {
                transform.pageIndex == 0
            } else {
                transform.pageIndex != 0
            }
            val xOffset = if (pageIsLeft) 0f else pairedWidth + margin
            val yOffset = (totalHeight - pageHeight) / 2f
            ChimahonReaderOcrPoint(
                x = (x.coerceIn(0f, 1f) * pageWidth + xOffset) / totalWidth.coerceAtLeast(MIN_READER_OCR_EDGE),
                y = (y.coerceIn(0f, 1f) * pageHeight + yOffset) / totalHeight.coerceAtLeast(MIN_READER_OCR_EDGE),
            )
        }
    }
    return mapped.clampedToUnit()
}

fun Float.transformedForReaderOcrPage(
    transform: ChimahonReaderOcrPageTransform,
): Float {
    return when (transform) {
        ChimahonReaderOcrPageTransform.Normal,
        is ChimahonReaderOcrPageTransform.Cropped,
        is ChimahonReaderOcrPageTransform.SplitWide,
        is ChimahonReaderOcrPageTransform.SplitAndMergeWide,
        is ChimahonReaderOcrPageTransform.MergedPages,
        -> this
        is ChimahonReaderOcrPageTransform.Rotated -> this + if (transform.clockwise) 90f else -90f
    }
}

private const val MIN_READER_OCR_EDGE = 0.001f
private const val MAX_READER_OCR_SPLIT_GUTTER = 0.12f
