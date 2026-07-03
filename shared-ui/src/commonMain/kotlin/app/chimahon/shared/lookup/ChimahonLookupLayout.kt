package app.chimahon.shared.lookup

data class ChimahonLookupSize(
    val width: Float,
    val height: Float,
) {
    companion object {
        val Zero = ChimahonLookupSize(0f, 0f)
    }
}

data class ChimahonLookupPopupLayoutResult(
    val x: Float,
    val y: Float,
    val size: ChimahonLookupSize,
) {
    val rect: ChimahonLookupRect
        get() = ChimahonLookupRect(x = x, y = y, width = size.width, height = size.height)
}

fun resolveChimahonLookupPopupLayout(
    screenSize: ChimahonLookupSize,
    requestedPopupSize: ChimahonLookupSize,
    anchor: ChimahonLookupRect,
    writingDirection: ChimahonLookupWritingDirection,
    mode: ChimahonLookupPopupMode = ChimahonLookupPopupMode.Floating,
    paddingPx: Float = DEFAULT_POPUP_PADDING_PX,
    gapPx: Float = DEFAULT_POPUP_GAP_PX,
    fullHeightWidthFraction: Float = DEFAULT_FULL_HEIGHT_WIDTH_FRACTION,
): ChimahonLookupPopupLayoutResult {
    val screenWidth = screenSize.width.coerceAtLeast(1f)
    val screenHeight = screenSize.height.coerceAtLeast(1f)
    val safePadding = paddingPx.coerceAtLeast(0f)
    val availableWidth = (screenWidth - safePadding * 2f).coerceAtLeast(1f)
    val availableHeight = (screenHeight - safePadding * 2f).coerceAtLeast(1f)
    val requestedWidth = requestedPopupSize.width.coerceAtLeast(1f)
    val requestedHeight = requestedPopupSize.height.coerceAtLeast(1f)

    return when (mode) {
        ChimahonLookupPopupMode.FullWidth -> {
            val width = screenWidth
            val height = requestedHeight.coerceAtMost(screenHeight)
            val bottomY = screenHeight - height
            val overlapsAnchor = anchor.width > 0f &&
                anchor.height > 0f &&
                bottomY < anchor.bottom
            ChimahonLookupPopupLayoutResult(
                x = 0f,
                y = if (overlapsAnchor) 0f else bottomY,
                size = ChimahonLookupSize(width, height),
            )
        }
        ChimahonLookupPopupMode.FullHeight -> {
            val width = minOf(
                requestedWidth,
                (screenWidth * fullHeightWidthFraction.coerceIn(0.25f, 1f)).coerceAtLeast(1f),
                availableWidth,
            )
            val height = availableHeight
            val anchorCenterX = anchor.centerX
            val x = if (anchorCenterX < screenWidth / 2f) {
                screenWidth - width - safePadding
            } else {
                safePadding
            }.coerceWithin(safePadding, screenWidth - width - safePadding)

            ChimahonLookupPopupLayoutResult(
                x = x,
                y = safePadding,
                size = ChimahonLookupSize(width, height),
            )
        }
        ChimahonLookupPopupMode.Floating -> {
            resolveFloatingPopupLayout(
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                popupWidth = requestedWidth.coerceAtMost(availableWidth),
                popupHeight = requestedHeight.coerceAtMost(availableHeight),
                anchor = anchor,
                writingDirection = writingDirection,
                paddingPx = safePadding,
                gapPx = gapPx.coerceAtLeast(0f),
            )
        }
    }
}

private fun resolveFloatingPopupLayout(
    screenWidth: Float,
    screenHeight: Float,
    popupWidth: Float,
    popupHeight: Float,
    anchor: ChimahonLookupRect,
    writingDirection: ChimahonLookupWritingDirection,
    paddingPx: Float,
    gapPx: Float,
): ChimahonLookupPopupLayoutResult {
    data class Candidate(val x: Float, val y: Float)

    val anchorWidth = anchor.width.coerceAtLeast(1f)
    val anchorHeight = anchor.height.coerceAtLeast(1f)
    val anchorCenterX = anchor.x + anchorWidth / 2f
    val anchorCenterY = anchor.y + anchorHeight / 2f
    val right = Candidate(anchor.x + anchorWidth + gapPx, anchorCenterY - popupHeight / 2f)
    val left = Candidate(anchor.x - popupWidth - gapPx, anchorCenterY - popupHeight / 2f)
    val below = Candidate(anchorCenterX - popupWidth / 2f, anchor.y + anchorHeight + gapPx)
    val above = Candidate(anchorCenterX - popupWidth / 2f, anchor.y - popupHeight - gapPx)
    val candidates = listOf(right, left, below, above)
    val order = if (writingDirection == ChimahonLookupWritingDirection.Vertical) {
        if (anchorCenterX < screenWidth / 2f) listOf(0, 1, 2, 3) else listOf(1, 0, 2, 3)
    } else {
        if (anchorCenterY < screenHeight / 2f) listOf(2, 3, 0, 1) else listOf(3, 2, 0, 1)
    }

    for (index in order) {
        val candidate = candidates[index]
        val x = candidate.x.coerceWithin(paddingPx, screenWidth - popupWidth - paddingPx)
        val y = candidate.y.coerceWithin(paddingPx, screenHeight - popupHeight - paddingPx)
        val overlapsAnchor = x < anchor.x + anchorWidth &&
            x + popupWidth > anchor.x &&
            y < anchor.y + anchorHeight &&
            y + popupHeight > anchor.y

        if (!overlapsAnchor) {
            return ChimahonLookupPopupLayoutResult(
                x = x,
                y = y,
                size = ChimahonLookupSize(popupWidth, popupHeight),
            )
        }
    }

    val fallback = candidates[order.first()]
    return ChimahonLookupPopupLayoutResult(
        x = fallback.x.coerceWithin(paddingPx, screenWidth - popupWidth - paddingPx),
        y = fallback.y.coerceWithin(paddingPx, screenHeight - popupHeight - paddingPx),
        size = ChimahonLookupSize(popupWidth, popupHeight),
    )
}

private fun Float.coerceWithin(minimumValue: Float, maximumValue: Float): Float {
    val low = minOf(minimumValue, maximumValue)
    val high = maxOf(minimumValue, maximumValue)
    return coerceIn(low, high)
}

private const val DEFAULT_POPUP_PADDING_PX = 8f
private const val DEFAULT_POPUP_GAP_PX = 8f
private const val DEFAULT_FULL_HEIGHT_WIDTH_FRACTION = 0.5f
