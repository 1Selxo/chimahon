package app.chimahon.shared.lookup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun ChimahonLookupPopupOverlay(
    state: ChimahonLookupPopupUiState,
    modifier: Modifier = Modifier,
    layoutSettings: ChimahonLookupPopupLayoutSettings = ChimahonLookupPopupLayoutSettings(),
    actionSlots: ChimahonLookupPopupActionSlots = ChimahonLookupPopupActionSlots(),
    actionHandlers: ChimahonLookupPopupActionHandlers = ChimahonLookupPopupActionHandlers(),
    onLookupTextChange: (String) -> Unit = {},
    onPrevious: () -> Unit = {},
    onNext: () -> Unit = {},
    onCopy: () -> Unit = {},
    onShare: () -> Unit = {},
    onSearch: (String) -> Unit = {},
    onClose: () -> Unit = {},
    onSelectTab: (Int) -> Unit = {},
    onBack: () -> Unit = {},
    onForward: () -> Unit = {},
    onRecursiveLookup: (String) -> Unit = {},
    resultContent: (@Composable (ChimahonLookupPopupUiState) -> Unit)? = null,
) {
    if (!state.visible && !layoutSettings.keepMountedWhenHidden) return

    val density = LocalDensity.current
    val colors = lookupPopupColors(layoutSettings)
    val requestedWidthDp = layoutSettings.widthDp.dp.coerceBetween(
        layoutSettings.minWidthDp.dp,
        layoutSettings.maxWidthDp.dp,
    )
    val requestedHeightDp = layoutSettings.heightDp.dp.coerceBetween(
        layoutSettings.minHeightDp.dp,
        layoutSettings.maxHeightDp.dp,
    )
    val requestedSize = with(density) {
        ChimahonLookupSize(
            width = requestedWidthDp.toPx(),
            height = requestedHeightDp.toPx(),
        )
    }
    val paddingPx = with(density) { layoutSettings.screenPaddingDp.dp.toPx() }
    val gapPx = with(density) { layoutSettings.anchorGapDp.dp.toPx() }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenSize = ChimahonLookupSize(
            width = constraints.maxWidth.toFloat().coerceAtLeast(1f),
            height = constraints.maxHeight.toFloat().coerceAtLeast(1f),
        )
        val layout = remember(
            screenSize,
            requestedSize,
            state.anchor.rect,
            state.anchor.writingDirection,
            layoutSettings.mode,
            paddingPx,
            gapPx,
            layoutSettings.fullHeightWidthFraction,
        ) {
            resolveChimahonLookupPopupLayout(
                screenSize = screenSize,
                requestedPopupSize = requestedSize,
                anchor = state.anchor.rect,
                writingDirection = state.anchor.writingDirection,
                mode = layoutSettings.mode,
                paddingPx = paddingPx,
                gapPx = gapPx,
                fullHeightWidthFraction = layoutSettings.fullHeightWidthFraction,
            )
        }
        val popupWidth = with(density) { layout.size.width.toDp() }
        val popupHeight = with(density) { layout.size.height.toDp() }
        val popupX = with(density) { layout.x.toDp() }
        val popupY = with(density) { layout.y.toDp() }
        val hiddenOffset = (-10_000).dp

        if (state.visible && layoutSettings.dismissOnOutsideClick) {
            val interactionSource = remember { MutableInteractionSource() }
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClose,
                    ),
            )
        }

        ChimahonLookupPopupPanel(
            state = state,
            width = popupWidth,
            height = popupHeight,
            colors = colors,
            layoutSettings = layoutSettings,
            actionSlots = actionSlots,
            actionHandlers = actionHandlers,
            onLookupTextChange = onLookupTextChange,
            onPrevious = onPrevious,
            onNext = onNext,
            onCopy = onCopy,
            onShare = onShare,
            onSearch = onSearch,
            onClose = onClose,
            onSelectTab = onSelectTab,
            onBack = onBack,
            onForward = onForward,
            onRecursiveLookup = onRecursiveLookup,
            resultContent = resultContent,
            modifier = Modifier
                .offset(
                    x = if (state.visible) popupX else hiddenOffset,
                    y = if (state.visible) popupY else hiddenOffset,
                )
                .alpha(if (state.visible) 1f else 0f),
        )
    }
}

@Composable
fun ChimahonLookupResultList(
    state: ChimahonLookupPopupUiState,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colors.onSurface,
    onRecursiveLookup: (String) -> Unit = {},
) {
    val frame = state.currentFrame
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when {
            state.isLoading -> item("loading") {
                LookupSupportText(
                    text = "Looking up...",
                    color = contentColor.copy(alpha = 0.66f),
                )
            }
            frame == null -> item("empty") {
                LookupSupportText(
                    text = "No lookup loaded",
                    color = contentColor.copy(alpha = 0.66f),
                )
            }
            frame.results.isEmpty() -> item("no-results") {
                LookupSupportText(
                    text = frame.errorMessage ?: "No results found",
                    color = if (frame.errorMessage != null) {
                        MaterialTheme.colors.error
                    } else {
                        contentColor.copy(alpha = 0.66f)
                    },
                )
            }
            else -> itemsIndexed(frame.results, key = { index, result -> "${result.id}#$index" }) { _, result ->
                LookupResultRow(
                    result = result,
                    existingInAnki = result.term.expression in frame.existingAnkiExpressions,
                    contentColor = contentColor,
                    onRecursiveLookup = onRecursiveLookup,
                )
            }
        }
    }
}

@Composable
private fun ChimahonLookupPopupPanel(
    state: ChimahonLookupPopupUiState,
    width: Dp,
    height: Dp,
    colors: LookupPopupColors,
    layoutSettings: ChimahonLookupPopupLayoutSettings,
    actionSlots: ChimahonLookupPopupActionSlots,
    actionHandlers: ChimahonLookupPopupActionHandlers,
    onLookupTextChange: (String) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSearch: (String) -> Unit,
    onClose: () -> Unit,
    onSelectTab: (Int) -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onRecursiveLookup: (String) -> Unit,
    resultContent: (@Composable (ChimahonLookupPopupUiState) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var horizontalDrag by remember(state.visible) { mutableStateOf(0f) }
    val swipeThresholdPx = with(LocalDensity.current) { layoutSettings.swipeThresholdDp.dp.toPx() }
    val swipeModifier = if (layoutSettings.swipeToDismiss) {
        Modifier.pointerInput(swipeThresholdPx) {
            detectHorizontalDragGestures(
                onDragEnd = {
                    if (abs(horizontalDrag) > swipeThresholdPx) onClose()
                    horizontalDrag = 0f
                },
                onDragCancel = { horizontalDrag = 0f },
                onHorizontalDrag = { _, dragAmount ->
                    horizontalDrag += dragAmount
                },
            )
        }
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .width(width)
            .height(height)
            .then(swipeModifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
        shape = RoundedCornerShape(if (layoutSettings.eInkMode) 0.dp else layoutSettings.cornerRadiusDp.dp),
        color = colors.background,
        contentColor = colors.content,
        elevation = if (layoutSettings.eInkMode) 0.dp else 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = colors.border,
                    shape = RoundedCornerShape(if (layoutSettings.eInkMode) 0.dp else layoutSettings.cornerRadiusDp.dp),
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LookupHeader(
                title = state.currentFrame?.query?.ifBlank { state.lookupText }
                    ?: state.lookupText.ifBlank { "Lookup" },
                subtitle = state.anchor.fullText.takeIf { it.isNotBlank() && it != state.lookupText },
                colors = colors,
                onClose = onClose,
            )

            LookupTextField(
                value = state.lookupText,
                contentColor = colors.content,
                backgroundColor = colors.control,
                fontSizeSp = layoutSettings.fontSizeSp,
                onValueChange = onLookupTextChange,
            )

            LookupRecursiveNavigation(
                stack = state.stack,
                mode = layoutSettings.recursiveNavigationMode,
                colors = colors,
                onSelectTab = onSelectTab,
                onBack = onBack,
                onForward = onForward,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                if (resultContent != null) {
                    resultContent(state)
                } else {
                    ChimahonLookupResultList(
                        state = state,
                        contentColor = colors.content,
                        onRecursiveLookup = onRecursiveLookup,
                    )
                }
            }

            LookupFooterActions(
                state = state,
                colors = colors,
                layoutSettings = layoutSettings,
                actionSlots = actionSlots,
                actionHandlers = actionHandlers,
                onPrevious = onPrevious,
                onNext = onNext,
                onCopy = onCopy,
                onShare = onShare,
                onSearch = onSearch,
            )

            state.message?.takeIf { it.isNotBlank() }?.let { message ->
                Text(
                    text = message,
                    color = colors.subdued,
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun LookupHeader(
    title: String,
    subtitle: String?,
    colors: LookupPopupColors,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.accent),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "OCR",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = colors.content,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            subtitle?.let {
                Text(
                    text = it,
                    color = colors.subdued,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        LookupIconAction(
            icon = Icons.Outlined.Close,
            contentDescription = "Close lookup",
            colors = colors,
            onClick = onClose,
        )
    }
}

@Composable
private fun LookupTextField(
    value: String,
    contentColor: Color,
    backgroundColor: Color,
    fontSizeSp: Int,
    onValueChange: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp, max = 88.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isBlank()) {
            Text(
                text = "Lookup text",
                color = contentColor.copy(alpha = 0.46f),
                fontSize = fontSizeSp.coerceIn(12, 24).sp,
                maxLines = 1,
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = contentColor,
                fontSize = fontSizeSp.coerceIn(12, 24).sp,
                lineHeight = (fontSizeSp.coerceIn(12, 24) + 5).sp,
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LookupRecursiveNavigation(
    stack: ChimahonLookupStack,
    mode: ChimahonLookupRecursiveNavigationMode,
    colors: LookupPopupColors,
    onSelectTab: (Int) -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit,
) {
    if (stack.frames.size <= 1) return

    when (mode) {
        ChimahonLookupRecursiveNavigationMode.Tabs -> {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(stack.tabs, key = { it.index }) { tab ->
                    LookupTabChip(
                        tab = tab,
                        colors = colors,
                        onClick = { onSelectTab(tab.index) },
                    )
                }
            }
        }
        ChimahonLookupRecursiveNavigationMode.BackStack -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LookupIconAction(
                    icon = Icons.Outlined.ArrowBack,
                    contentDescription = "Previous lookup",
                    colors = colors,
                    enabled = stack.canGoBack,
                    onClick = onBack,
                )
                Text(
                    text = "${stack.activeIndex + 1} / ${stack.frames.size}",
                    color = colors.subdued,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                LookupIconAction(
                    icon = Icons.Outlined.ArrowForward,
                    contentDescription = "Next lookup",
                    colors = colors,
                    enabled = stack.canGoForward,
                    onClick = onForward,
                )
            }
        }
    }
}

@Composable
private fun LookupFooterActions(
    state: ChimahonLookupPopupUiState,
    colors: LookupPopupColors,
    layoutSettings: ChimahonLookupPopupLayoutSettings,
    actionSlots: ChimahonLookupPopupActionSlots,
    actionHandlers: ChimahonLookupPopupActionHandlers,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSearch: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (layoutSettings.showNavigationButtons && state.ocrBlockCount > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LookupIconAction(
                    icon = Icons.Outlined.SkipPrevious,
                    contentDescription = "Previous OCR match",
                    colors = colors,
                    enabled = state.selectedOcrIndex > 0,
                    onClick = onPrevious,
                )
                Text(
                    text = "${state.selectedOcrIndex + 1} / ${state.ocrBlockCount}",
                    color = colors.subdued,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                LookupIconAction(
                    icon = Icons.Outlined.SkipNext,
                    contentDescription = "Next OCR match",
                    colors = colors,
                    enabled = state.selectedOcrIndex + 1 < state.ocrBlockCount,
                    onClick = onNext,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LookupIconAction(
                icon = Icons.Outlined.ContentCopy,
                contentDescription = "Copy lookup text",
                colors = colors,
                onClick = onCopy,
            )
            LookupIconAction(
                icon = Icons.Outlined.Share,
                contentDescription = "Share lookup text",
                colors = colors,
                onClick = onShare,
            )
            LookupIconAction(
                icon = Icons.Outlined.Search,
                contentDescription = "Search lookup text",
                colors = colors,
                onClick = { onSearch(state.lookupText) },
            )
            Spacer(modifier = Modifier.weight(1f))
            actionSlots.all.forEach { slot ->
                LookupActionSlotButton(
                    slot = slot,
                    selectedResult = state.selectedResult,
                    colors = colors,
                    handlers = actionHandlers,
                )
            }
        }
    }
}

@Composable
private fun LookupActionSlotButton(
    slot: ChimahonLookupActionSlot,
    selectedResult: ChimahonLookupResult?,
    colors: LookupPopupColors,
    handlers: ChimahonLookupPopupActionHandlers,
) {
    val enabled = slot.enabled && when (slot.kind) {
        ChimahonLookupActionKind.AddToAnki -> handlers.onAddToAnki != null && selectedResult != null
        ChimahonLookupActionKind.WordAudio -> handlers.onPlayWordAudio != null && selectedResult != null
        ChimahonLookupActionKind.SentenceAudio -> handlers.onPlaySentenceAudio != null
        ChimahonLookupActionKind.Screenshot -> handlers.onCaptureScreenshot != null
        ChimahonLookupActionKind.CropScreenshot -> handlers.onCropScreenshot != null
        ChimahonLookupActionKind.Custom -> handlers.onExtraAction != null
    }
    LookupIconAction(
        icon = slot.icon,
        contentDescription = slot.contentDescription,
        colors = colors,
        active = slot.active,
        enabled = enabled,
        onClick = {
            when (slot.kind) {
                ChimahonLookupActionKind.AddToAnki -> selectedResult?.let { handlers.onAddToAnki?.invoke(it) }
                ChimahonLookupActionKind.WordAudio -> selectedResult?.let { handlers.onPlayWordAudio?.invoke(it) }
                ChimahonLookupActionKind.SentenceAudio -> handlers.onPlaySentenceAudio?.invoke()
                ChimahonLookupActionKind.Screenshot -> handlers.onCaptureScreenshot?.invoke()
                ChimahonLookupActionKind.CropScreenshot -> handlers.onCropScreenshot?.invoke()
                ChimahonLookupActionKind.Custom -> handlers.onExtraAction?.invoke(slot)
            }
        },
    )
}

@Composable
private fun LookupIconAction(
    icon: ImageVector,
    contentDescription: String,
    colors: LookupPopupColors,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val background = when {
        active -> colors.accent
        enabled -> colors.control
        else -> colors.control.copy(alpha = 0.45f)
    }
    val tint = when {
        active -> Color.White
        enabled -> colors.content.copy(alpha = 0.82f)
        else -> colors.content.copy(alpha = 0.30f)
    }
    Box(
        modifier = modifier
            .size(34.dp)
            .clip(RoundedCornerShape(50))
            .background(background)
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
            }
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun LookupTabChip(
    tab: ChimahonLookupTab,
    colors: LookupPopupColors,
    onClick: () -> Unit,
) {
    val background = if (tab.active) colors.accent else colors.control
    val content = if (tab.active) Color.White else colors.content
    Box(
        modifier = Modifier
            .heightIn(min = 30.dp)
            .widthIn(min = 52.dp, max = 132.dp)
            .clip(RoundedCornerShape(50))
            .background(background)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = tab.label.ifBlank { "Lookup" },
            color = content,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun LookupResultRow(
    result: ChimahonLookupResult,
    existingInAnki: Boolean,
    contentColor: Color,
    onRecursiveLookup: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(contentColor.copy(alpha = 0.06f))
            .clickable { onRecursiveLookup(result.term.expression) }
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = result.term.expression,
                color = contentColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (existingInAnki) {
                Text(
                    text = "Anki",
                    color = contentColor.copy(alpha = 0.62f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        if (result.term.reading.isNotBlank()) {
            Text(
                text = result.term.reading,
                color = contentColor.copy(alpha = 0.70f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        result.definitions.take(3).forEach { definition ->
            Text(
                text = listOf(definition.dictionary, definition.text)
                    .filter { it.isNotBlank() }
                    .joinToString(separator = " - "),
                color = contentColor.copy(alpha = 0.82f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (result.definitions.isEmpty()) {
            Text(
                text = "Definition placeholder",
                color = contentColor.copy(alpha = 0.52f),
                fontSize = 13.sp,
                maxLines = 2,
            )
        }
    }
}

@Composable
private fun LookupSupportText(
    text: String,
    color: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 13.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun lookupPopupColors(settings: ChimahonLookupPopupLayoutSettings): LookupPopupColors {
    val systemDark = isSystemInDarkTheme()
    val dark = when (settings.theme) {
        ChimahonLookupPopupTheme.System -> systemDark
        ChimahonLookupPopupTheme.Light -> false
        ChimahonLookupPopupTheme.Dark,
        ChimahonLookupPopupTheme.PureBlack,
        -> true
    }
    val background = when (settings.theme) {
        ChimahonLookupPopupTheme.PureBlack -> Color.Black
        else -> if (dark) Color(0xFF202226) else Color.White
    }
    val content = if (dark) Color(0xFFF3F4F6) else Color(0xFF22242A)
    val accent = if (dark) Color(0xFF4C7DFF) else Color(0xFF315FD6)
    return LookupPopupColors(
        background = background,
        content = content,
        subdued = content.copy(alpha = 0.64f),
        border = content.copy(alpha = if (settings.eInkMode) 0.22f else 0.12f),
        control = content.copy(alpha = if (settings.eInkMode) 0.08f else 0.10f),
        accent = accent,
    )
}

private data class LookupPopupColors(
    val background: Color,
    val content: Color,
    val subdued: Color,
    val border: Color,
    val control: Color,
    val accent: Color,
)

private val ChimahonLookupActionSlot.icon: ImageVector
    get() = when (kind) {
        ChimahonLookupActionKind.AddToAnki -> Icons.Outlined.Add
        ChimahonLookupActionKind.WordAudio -> Icons.Outlined.VolumeUp
        ChimahonLookupActionKind.SentenceAudio -> Icons.Outlined.VolumeUp
        ChimahonLookupActionKind.Screenshot -> Icons.Outlined.CropFree
        ChimahonLookupActionKind.CropScreenshot -> Icons.Outlined.CropFree
        ChimahonLookupActionKind.Custom -> Icons.Outlined.Add
    }

private fun Dp.coerceBetween(minimumValue: Dp, maximumValue: Dp): Dp {
    val low = minOf(minimumValue, maximumValue)
    val high = maxOf(minimumValue, maximumValue)
    return coerceIn(low, high)
}
