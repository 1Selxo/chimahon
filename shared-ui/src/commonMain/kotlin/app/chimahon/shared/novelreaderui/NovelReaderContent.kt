package app.chimahon.shared.novelreaderui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

@Composable
fun NovelReaderScreen(
    state: NovelReaderUiState,
    actions: NovelReaderActions,
    modifier: Modifier = Modifier,
    systemDark: Boolean = false,
) {
    val palette = state.layout.resolveNovelReaderPalette(systemDark)
    Box(
        modifier = modifier
            .fillMaxSize()
            .novelReaderDesktopControls(state = state, actions = actions)
            .background(palette.background),
    ) {
        NovelReaderContent(
            state = state,
            actions = actions,
            palette = palette,
            modifier = Modifier.fillMaxSize(),
        )

        if (state.hud.visible) {
            NovelReaderTopHud(
                state = state,
                actions = actions,
                palette = palette,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(),
            )
            NovelReaderBottomHud(
                state = state,
                actions = actions,
                palette = palette,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                )
        }

        state.selection?.let { selection ->
            NovelReaderSelectionOverlay(
                selection = selection,
                actions = actions,
                palette = palette,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            )
        }

        if (state.drawerVisible) {
            NovelReaderChapterDrawer(
                state = state,
                actions = actions,
                palette = palette,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth(0.42f),
            )
        }

        if (state.typographyVisible) {
            NovelReaderTypographyEntryPanel(
                layout = state.layout,
                palette = palette,
                actions = actions,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            )
        }

        if (!state.hud.visible && state.chrome.edgeHintsVisible) {
            NovelReaderChromeEdgeHints(
                chrome = state.chrome,
                palette = palette,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun NovelReaderContent(
    state: NovelReaderUiState,
    actions: NovelReaderActions,
    modifier: Modifier = Modifier,
    palette: NovelReaderPalette = state.layout.resolveNovelReaderPalette(),
) {
    when (state.contentState) {
        NovelReaderContentState.Loading -> {
            NovelReaderLoadingState(
                message = state.loadingMessage ?: "Opening novel...",
                palette = palette,
                modifier = modifier,
            )
        }
        NovelReaderContentState.Error -> {
            NovelReaderErrorState(
                title = "Novel reader failed",
                message = state.errorMessage ?: "The selected chapter could not be loaded.",
                onRetry = actions.onRetry,
                palette = palette,
                modifier = modifier,
            )
        }
        NovelReaderContentState.Empty -> {
            NovelReaderEmptyState(
                title = "No text loaded",
                message = "Choose a chapter from the drawer to start reading.",
                palette = palette,
                modifier = modifier,
            )
        }
        NovelReaderContentState.Content -> {
            NovelReaderReadableText(
                state = state,
                actions = actions,
                palette = palette,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun NovelReaderReadableText(
    state: NovelReaderUiState,
    actions: NovelReaderActions,
    modifier: Modifier = Modifier,
    palette: NovelReaderPalette = state.layout.resolveNovelReaderPalette(),
) {
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    val tapZonePercent = state.layout.tapZonePercent
    val direction = state.readingDirection
    val tapModifier = Modifier
        .onSizeChanged { viewportSize = it }
        .pointerInput(tapZonePercent, direction, viewportSize) {
            detectTapGestures { offset ->
                val width = max(viewportSize.width, 1).toFloat()
                val height = max(viewportSize.height, 1).toFloat()
                val edge = width * (tapZonePercent.coerceIn(5f, 45f) / 100f)
                val topBottom = height * 0.12f
                when {
                    offset.y <= topBottom || offset.y >= height - topBottom -> actions.onToggleHud()
                    direction == NovelReadingDirection.RightToLeft ||
                        direction == NovelReadingDirection.VerticalRightToLeft -> {
                        if (offset.x <= edge) actions.onNextPage()
                        else if (offset.x >= width - edge) actions.onPreviousPage()
                        else actions.onToggleHud()
                    }
                    else -> {
                        if (offset.x <= edge) actions.onPreviousPage()
                        else if (offset.x >= width - edge) actions.onNextPage()
                        else actions.onToggleHud()
                    }
                }
            }
        }

    Box(
        modifier = modifier
            .then(tapModifier)
            .background(palette.background),
    ) {
        if (state.readingMode == NovelReadingMode.Continuous) {
            NovelReaderContinuousText(
                paragraphs = state.visibleParagraphs,
                layout = state.layout,
                palette = palette,
                actions = actions,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            NovelReaderPagedText(
                paragraphs = state.visibleParagraphs,
                layout = state.layout,
                palette = palette,
                actions = actions,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun NovelReaderPagedText(
    paragraphs: List<NovelReaderParagraphUiModel>,
    layout: NovelReaderLayoutState,
    palette: NovelReaderPalette,
    actions: NovelReaderActions,
    modifier: Modifier = Modifier,
) {
    SelectionContainer {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(layout.readerPadding())
                .widthIn(max = if (layout.verticalWriting) 720.dp else 880.dp),
            verticalArrangement = Arrangement.spacedBy(layout.paragraphGap()),
        ) {
            paragraphs.forEach { paragraph ->
                NovelReaderParagraph(
                    paragraph = paragraph,
                    layout = layout,
                    palette = palette,
                    actions = actions,
                )
            }
        }
    }
}

@Composable
fun NovelReaderContinuousText(
    paragraphs: List<NovelReaderParagraphUiModel>,
    layout: NovelReaderLayoutState,
    palette: NovelReaderPalette,
    actions: NovelReaderActions,
    modifier: Modifier = Modifier,
) {
    SelectionContainer {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(
                horizontal = layout.horizontalPadding(),
                vertical = layout.verticalPadding(),
            ),
            verticalArrangement = Arrangement.spacedBy(layout.paragraphGap()),
        ) {
            itemsIndexed(
                items = paragraphs,
                key = { index, paragraph -> paragraph.stableLazyKey(index) },
            ) { index, paragraph ->
                NovelReaderParagraph(
                    paragraph = paragraph,
                    layout = layout,
                    palette = palette,
                    actions = actions,
                )
            }
        }
    }
}

@Composable
fun NovelReaderParagraph(
    paragraph: NovelReaderParagraphUiModel,
    layout: NovelReaderLayoutState,
    palette: NovelReaderPalette,
    actions: NovelReaderActions,
    modifier: Modifier = Modifier,
) {
    if (paragraph.type == NovelReaderParagraphType.Divider) {
        Divider(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            color = palette.text.copy(alpha = 0.18f),
        )
        return
    }

    Text(
        modifier = modifier
            .fillMaxWidth()
            .background(if (paragraph.selected) palette.selection else Color.Transparent)
            .clickable { actions.onParagraphSelected(paragraph) }
            .padding(horizontal = 2.dp, vertical = 1.dp),
        text = paragraph.displayText,
        color = palette.text,
        style = paragraph.textStyle(layout),
        textAlign = when {
            paragraph.type == NovelReaderParagraphType.Heading -> TextAlign.Center
            layout.justifyText -> TextAlign.Justify
            else -> TextAlign.Start
        },
        overflow = TextOverflow.Clip,
    )
}

data class NovelReaderPalette(
    val background: Color,
    val text: Color,
    val surface: Color,
    val onSurface: Color,
    val muted: Color,
    val outline: Color,
    val selection: Color,
    val accent: Color,
)

fun NovelReaderLayoutState.resolveNovelReaderPalette(systemDark: Boolean = false): NovelReaderPalette {
    val (background, text) = when (theme) {
        NovelReaderTheme.Light -> 0xFFFFFFFF.toInt() to 0xFF111111.toInt()
        NovelReaderTheme.Dark -> 0xFF121212.toInt() to 0xFFE9E4F0.toInt()
        NovelReaderTheme.Sepia -> 0xFFF3E3CB.toInt() to 0xFF3A2A1B.toInt()
        NovelReaderTheme.PureBlack -> 0xFF000000.toInt() to 0xFFE8E8E8.toInt()
        NovelReaderTheme.Custom -> backgroundColor to textColor
        NovelReaderTheme.System -> {
            if (systemDark) 0xFF121212.toInt() to 0xFFE9E4F0.toInt()
            else 0xFFFFFFFF.toInt() to 0xFF111111.toInt()
        }
    }
    val backgroundColor = Color(background)
    val textColor = Color(text)
    return NovelReaderPalette(
        background = backgroundColor,
        text = textColor,
        surface = backgroundColor.copy(alpha = 0.94f),
        onSurface = textColor,
        muted = textColor.copy(alpha = 0.66f),
        outline = textColor.copy(alpha = 0.16f),
        selection = textColor.copy(alpha = 0.12f),
        accent = Color(0xFF6750A4),
    )
}

private fun NovelReaderParagraphUiModel.textStyle(layout: NovelReaderLayoutState): TextStyle {
    val baseSize = when (type) {
        NovelReaderParagraphType.Heading -> layout.fontSize + 8f
        NovelReaderParagraphType.Subheading -> layout.fontSize + 3f
        NovelReaderParagraphType.Note -> layout.fontSize - 2f
        else -> layout.fontSize
    }.coerceAtLeast(10f)

    return TextStyle(
        fontSize = baseSize.sp,
        lineHeight = (baseSize * layout.lineHeight.coerceIn(1f, 2.8f)).sp,
        letterSpacing = layout.characterSpacing.sp,
        fontFamily = layout.fontFamily(),
        fontWeight = when (type) {
            NovelReaderParagraphType.Heading,
            NovelReaderParagraphType.Subheading -> FontWeight.SemiBold
            else -> FontWeight.Normal
        },
        fontStyle = if (type == NovelReaderParagraphType.Quote || type == NovelReaderParagraphType.Poem) {
            FontStyle.Italic
        } else {
            FontStyle.Normal
        },
    )
}

private fun NovelReaderLayoutState.fontFamily(): FontFamily {
    val normalized = selectedFont.lowercase()
    return when {
        "mono" in normalized -> FontFamily.Monospace
        "sans" in normalized -> FontFamily.SansSerif
        else -> FontFamily.Serif
    }
}

private fun NovelReaderLayoutState.readerPadding(): PaddingValues {
    return PaddingValues(
        horizontal = horizontalPadding(),
        vertical = verticalPadding(),
    )
}

private fun NovelReaderLayoutState.horizontalPadding() = (16f + horizontalPaddingPercent.coerceIn(0f, 50f) * 2f).dp

private fun NovelReaderLayoutState.verticalPadding() = (12f + verticalPaddingPercent.coerceIn(0f, 50f) * 1.6f).dp

private fun NovelReaderLayoutState.paragraphGap() = (4f + paragraphSpacing.coerceIn(0f, 2.5f) * 12f).dp
