package app.chimahon.shared.lookup

/**
 * Platform-neutral geometry used by reader OCR, EPUB selection, subtitle, and
 * external screen lookup surfaces.
 */
data class ChimahonLookupRect(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
) {
    val left: Float get() = x
    val top: Float get() = y
    val right: Float get() = x + width
    val bottom: Float get() = y + height
    val centerX: Float get() = x + width / 2f
    val centerY: Float get() = y + height / 2f
    val isEmpty: Boolean get() = width <= 0f || height <= 0f

    fun contains(pointX: Float, pointY: Float): Boolean {
        return pointX >= left && pointX <= right && pointY >= top && pointY <= bottom
    }

    fun scaledFromCenter(
        scaleX: Float,
        scaleY: Float,
        minEdge: Float = MIN_LOOKUP_RECT_EDGE,
        maxEdge: Float = Float.POSITIVE_INFINITY,
    ): ChimahonLookupRect {
        val safeScaleX = scaleX.takeIf { it.isFinite() }?.coerceAtLeast(0f) ?: 1f
        val safeScaleY = scaleY.takeIf { it.isFinite() }?.coerceAtLeast(0f) ?: 1f
        val safeMaxEdge = maxEdge.takeIf { it.isFinite() }?.coerceAtLeast(minEdge) ?: Float.POSITIVE_INFINITY
        val scaledWidth = (width.coerceAtLeast(minEdge) * safeScaleX).coerceIn(minEdge, safeMaxEdge)
        val scaledHeight = (height.coerceAtLeast(minEdge) * safeScaleY).coerceIn(minEdge, safeMaxEdge)
        return ChimahonLookupRect(
            x = centerX - scaledWidth / 2f,
            y = centerY - scaledHeight / 2f,
            width = scaledWidth,
            height = scaledHeight,
        )
    }

    fun scaledWithinUnitFromCenter(
        scaleX: Float,
        scaleY: Float,
        minEdge: Float = MIN_LOOKUP_RECT_EDGE,
        maxEdge: Float = 1f,
    ): ChimahonLookupRect {
        val scaled = scaledFromCenter(
            scaleX = scaleX,
            scaleY = scaleY,
            minEdge = minEdge,
            maxEdge = maxEdge,
        )
        val left = scaled.left.coerceIn(0f, 1f)
        val top = scaled.top.coerceIn(0f, 1f)
        return ChimahonLookupRect(
            x = left,
            y = top,
            width = scaled.right.coerceIn(0f, 1f) - left,
            height = scaled.bottom.coerceIn(0f, 1f) - top,
        )
    }

    companion object {
        val Zero = ChimahonLookupRect(0f, 0f, 0f, 0f)
    }
}

fun chimahonLookupRectFromEdges(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    coerceToUnit: Boolean = true,
): ChimahonLookupRect? {
    if (!left.isFinite() || !top.isFinite() || !right.isFinite() || !bottom.isFinite()) {
        return null
    }
    val rawLeft = minOf(left, right)
    val rawTop = minOf(top, bottom)
    val rawRight = maxOf(left, right)
    val rawBottom = maxOf(top, bottom)
    val safeLeft = if (coerceToUnit) rawLeft.coerceIn(0f, 1f) else rawLeft
    val safeTop = if (coerceToUnit) rawTop.coerceIn(0f, 1f) else rawTop
    val safeRight = if (coerceToUnit) rawRight.coerceIn(0f, 1f) else rawRight
    val safeBottom = if (coerceToUnit) rawBottom.coerceIn(0f, 1f) else rawBottom
    val width = safeRight - safeLeft
    val height = safeBottom - safeTop
    if (width <= MIN_LOOKUP_RECT_EDGE || height <= MIN_LOOKUP_RECT_EDGE) return null
    return ChimahonLookupRect(safeLeft, safeTop, width, height)
}

fun Iterable<ChimahonLookupRect>.unionChimahonLookupRects(): ChimahonLookupRect? {
    val usable = filterNot { it.isEmpty }
    if (usable.isEmpty()) return null
    return chimahonLookupRectFromEdges(
        left = usable.minOf { it.left },
        top = usable.minOf { it.top },
        right = usable.maxOf { it.right },
        bottom = usable.maxOf { it.bottom },
        coerceToUnit = false,
    )
}

enum class ChimahonLookupAnchorCoordinateSpace {
    ScreenPixels,
    NormalizedPage,
}

enum class ChimahonLookupWritingDirection {
    Horizontal,
    Vertical,
}

data class ChimahonLookupSelectionRect(
    val rect: ChimahonLookupRect,
    val coordinateSpace: ChimahonLookupAnchorCoordinateSpace = ChimahonLookupAnchorCoordinateSpace.ScreenPixels,
)

data class ChimahonLookupMatchedTextAnchor(
    val lookupText: String,
    val fullText: String = lookupText,
    val charOffset: Int = 0,
    val matchedText: String = lookupText,
    val matchedOffset: Int = charOffset,
    val matchedLength: Int = matchedText.length,
    val rect: ChimahonLookupRect = ChimahonLookupRect.Zero,
    val coordinateSpace: ChimahonLookupAnchorCoordinateSpace = ChimahonLookupAnchorCoordinateSpace.ScreenPixels,
    val writingDirection: ChimahonLookupWritingDirection = ChimahonLookupWritingDirection.Horizontal,
    val selectionRects: List<ChimahonLookupSelectionRect> = emptyList(),
    val sourceId: String? = null,
)

data class ChimahonLookupTerm(
    val expression: String,
    val reading: String = "",
    val sequence: Long? = null,
)

data class ChimahonLookupDefinition(
    val dictionary: String,
    val text: String,
    val tags: List<String> = emptyList(),
)

data class ChimahonLookupFrequency(
    val source: String,
    val value: String,
    val rank: Int? = null,
)

data class ChimahonLookupPitchAccent(
    val reading: String,
    val positions: List<Int> = emptyList(),
    val displayText: String = "",
)

data class ChimahonLookupAudioReference(
    val id: String,
    val label: String,
    val source: String = "",
    val mediaKey: String? = null,
)

data class ChimahonLookupMediaAsset(
    val key: String,
    val mimeType: String,
    val displayName: String = key,
    val dataUri: String? = null,
)

data class ChimahonLookupStyle(
    val dictionary: String,
    val css: String = "",
)

data class ChimahonLookupResult(
    val term: ChimahonLookupTerm,
    val matchedText: String = term.expression,
    val definitions: List<ChimahonLookupDefinition> = emptyList(),
    val frequencies: List<ChimahonLookupFrequency> = emptyList(),
    val pitches: List<ChimahonLookupPitchAccent> = emptyList(),
    val audio: List<ChimahonLookupAudioReference> = emptyList(),
    val tags: List<String> = emptyList(),
    val score: Double? = null,
    val id: String = "${term.expression}|${term.reading}|$matchedText",
)

data class ChimahonLookupFrame(
    val query: String,
    val results: List<ChimahonLookupResult> = emptyList(),
    val id: String = query,
    val styles: List<ChimahonLookupStyle> = emptyList(),
    val mediaAssets: Map<String, ChimahonLookupMediaAsset> = emptyMap(),
    val existingAnkiExpressions: Set<String> = emptySet(),
    val errorMessage: String? = null,
)

data class ChimahonLookupTab(
    val index: Int,
    val label: String,
    val active: Boolean,
)

data class ChimahonLookupStack(
    val frames: List<ChimahonLookupFrame> = emptyList(),
    val activeIndex: Int = 0,
) {
    val currentFrame: ChimahonLookupFrame?
        get() = frames.getOrNull(activeIndex)

    val canGoBack: Boolean
        get() = activeIndex > 0

    val canGoForward: Boolean
        get() = activeIndex < frames.lastIndex

    val tabs: List<ChimahonLookupTab>
        get() = frames.mapIndexed { index, frame ->
            ChimahonLookupTab(
                index = index,
                label = frame.query.take(MAX_TAB_LABEL_LENGTH),
                active = index == activeIndex,
            )
        }

    fun push(frame: ChimahonLookupFrame): ChimahonLookupStack {
        val retained = frames.take(activeIndex + 1)
        return copy(
            frames = retained + frame,
            activeIndex = retained.size,
        )
    }

    fun replaceCurrent(frame: ChimahonLookupFrame): ChimahonLookupStack {
        if (frames.isEmpty()) return copy(frames = listOf(frame), activeIndex = 0)
        return copy(
            frames = frames.mapIndexed { index, existing ->
                if (index == activeIndex) frame else existing
            },
        )
    }

    fun select(index: Int): ChimahonLookupStack {
        return if (index in frames.indices) copy(activeIndex = index) else this
    }

    fun back(): ChimahonLookupStack {
        return if (canGoBack) copy(activeIndex = activeIndex - 1) else this
    }

    fun forward(): ChimahonLookupStack {
        return if (canGoForward) copy(activeIndex = activeIndex + 1) else this
    }

    companion object {
        val Empty = ChimahonLookupStack()
    }
}

enum class ChimahonLookupRecursiveNavigationMode {
    Tabs,
    BackStack,
}

enum class ChimahonLookupPopupMode {
    Floating,
    FullWidth,
    FullHeight,
}

enum class ChimahonLookupPopupTheme {
    System,
    Light,
    Dark,
    PureBlack,
}

data class ChimahonLookupPopupLayoutSettings(
    val mode: ChimahonLookupPopupMode = ChimahonLookupPopupMode.Floating,
    val widthDp: Int = 320,
    val heightDp: Int = 360,
    val minWidthDp: Int = 280,
    val minHeightDp: Int = 172,
    val maxWidthDp: Int = 760,
    val maxHeightDp: Int = 680,
    val screenPaddingDp: Int = 8,
    val anchorGapDp: Int = 8,
    val cornerRadiusDp: Int = 8,
    val fullHeightWidthFraction: Float = 0.5f,
    val dismissOnOutsideClick: Boolean = true,
    val swipeToDismiss: Boolean = true,
    val swipeThresholdDp: Int = 56,
    val keepMountedWhenHidden: Boolean = true,
    val showNavigationButtons: Boolean = true,
    val recursiveNavigationMode: ChimahonLookupRecursiveNavigationMode = ChimahonLookupRecursiveNavigationMode.Tabs,
    val theme: ChimahonLookupPopupTheme = ChimahonLookupPopupTheme.System,
    val eInkMode: Boolean = false,
    val fontSizeSp: Int = 16,
)

enum class ChimahonLookupActionKind {
    AddToAnki,
    WordAudio,
    SentenceAudio,
    Screenshot,
    CropScreenshot,
    Custom,
}

data class ChimahonLookupActionSlot(
    val id: String,
    val kind: ChimahonLookupActionKind,
    val label: String,
    val contentDescription: String = label,
    val enabled: Boolean = true,
    val active: Boolean = false,
)

data class ChimahonLookupPopupActionSlots(
    val addToAnki: ChimahonLookupActionSlot? = null,
    val wordAudio: ChimahonLookupActionSlot? = null,
    val sentenceAudio: ChimahonLookupActionSlot? = null,
    val screenshot: ChimahonLookupActionSlot? = null,
    val cropScreenshot: ChimahonLookupActionSlot? = null,
    val extra: List<ChimahonLookupActionSlot> = emptyList(),
) {
    val all: List<ChimahonLookupActionSlot>
        get() = listOfNotNull(addToAnki, wordAudio, sentenceAudio, screenshot, cropScreenshot) + extra
}

data class ChimahonLookupPopupActionHandlers(
    val onAddToAnki: ((ChimahonLookupResult) -> Unit)? = null,
    val onPlayWordAudio: ((ChimahonLookupResult) -> Unit)? = null,
    val onPlaySentenceAudio: (() -> Unit)? = null,
    val onCaptureScreenshot: (() -> Unit)? = null,
    val onCropScreenshot: (() -> Unit)? = null,
    val onExtraAction: ((ChimahonLookupActionSlot) -> Unit)? = null,
)

data class ChimahonLookupPopupUiState(
    val visible: Boolean = true,
    val anchor: ChimahonLookupMatchedTextAnchor = ChimahonLookupMatchedTextAnchor(""),
    val stack: ChimahonLookupStack = ChimahonLookupStack.Empty,
    val lookupText: String = anchor.lookupText,
    val selectedResultIndex: Int = 0,
    val selectedOcrIndex: Int = 0,
    val ocrBlockCount: Int = 0,
    val isLoading: Boolean = false,
    val message: String? = null,
) {
    val currentFrame: ChimahonLookupFrame?
        get() = stack.currentFrame

    val selectedResult: ChimahonLookupResult?
        get() = currentFrame?.results?.getOrNull(selectedResultIndex)
}

private const val MAX_TAB_LABEL_LENGTH = 16
private const val MIN_LOOKUP_RECT_EDGE = 0.0001f
