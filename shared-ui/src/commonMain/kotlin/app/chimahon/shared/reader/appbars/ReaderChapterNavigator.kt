package app.chimahon.shared.reader.appbars

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReaderChapterNavigator(
    state: ReaderChapterNavigatorState,
    actions: ReaderChapterNavigatorActions,
    orientation: ReaderNavigatorOrientation,
    modifier: Modifier = Modifier,
    colors: ReaderAppBarColors = ReaderAppBarDefaults.colors(),
) {
    when (orientation) {
        ReaderNavigatorOrientation.Horizontal -> ReaderHorizontalChapterNavigator(
            state = state,
            actions = actions,
            modifier = modifier,
            colors = colors,
        )
        ReaderNavigatorOrientation.Vertical -> ReaderVerticalChapterNavigator(
            state = state,
            actions = actions,
            modifier = modifier,
            colors = colors,
        )
    }
}

@Composable
fun ReaderHorizontalChapterNavigator(
    state: ReaderChapterNavigatorState,
    actions: ReaderChapterNavigatorActions,
    modifier: Modifier = Modifier,
    colors: ReaderAppBarColors = ReaderAppBarDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ReaderFilledIconButton(
            icon = ReaderAppBarIcon.Previous,
            contentDescription = if (state.isRtl) {
                state.nextChapterContentDescription
            } else {
                state.previousChapterContentDescription
            },
            enabled = if (state.isRtl) state.enabledNext else state.enabledPrevious,
            colors = colors,
            onClick = if (state.isRtl) actions.onNextChapter else actions.onPreviousChapter,
        )

        if (state.pageSlider.coercedTotalPages > 1) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.divider, RoundedCornerShape(24.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(contentAlignment = Alignment.CenterEnd) {
                    ReaderPageLabel(
                        text = state.pageSlider.currentPageText,
                        colors = colors,
                    )
                    ReaderPageLabel(
                        text = state.pageSlider.coercedTotalPages.toString(),
                        colors = colors,
                        invisible = true,
                    )
                }
                ReaderPageSlider(
                    state = state.pageSlider.copy(isRtl = state.isRtl),
                    orientation = ReaderNavigatorOrientation.Horizontal,
                    onPageIndexChange = actions.onPageIndexChange,
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp),
                    colors = colors,
                )
                ReaderPageLabel(
                    text = state.pageSlider.coercedTotalPages.toString(),
                    colors = colors,
                )
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        ReaderFilledIconButton(
            icon = ReaderAppBarIcon.Next,
            contentDescription = if (state.isRtl) {
                state.previousChapterContentDescription
            } else {
                state.nextChapterContentDescription
            },
            enabled = if (state.isRtl) state.enabledPrevious else state.enabledNext,
            colors = colors,
            onClick = if (state.isRtl) actions.onPreviousChapter else actions.onNextChapter,
        )
    }
}

@Composable
fun ReaderVerticalChapterNavigator(
    state: ReaderChapterNavigatorState,
    actions: ReaderChapterNavigatorActions,
    modifier: Modifier = Modifier,
    colors: ReaderAppBarColors = ReaderAppBarDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ReaderFilledIconButton(
            icon = ReaderAppBarIcon.Previous,
            contentDescription = state.previousChapterContentDescription,
            enabled = state.enabledPrevious,
            colors = colors,
            rotateDegrees = 90f,
            onClick = actions.onPreviousChapter,
        )

        if (state.pageSlider.coercedTotalPages > 1) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .width(56.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.divider, RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ReaderPageLabel(
                    text = state.pageSlider.currentPageText,
                    colors = colors,
                )
                ReaderPageSlider(
                    state = state.pageSlider,
                    orientation = ReaderNavigatorOrientation.Vertical,
                    onPageIndexChange = actions.onPageIndexChange,
                    modifier = Modifier
                        .weight(1f)
                        .width(32.dp),
                    colors = colors,
                )
                ReaderPageLabel(
                    text = state.pageSlider.coercedTotalPages.toString(),
                    colors = colors,
                )
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        ReaderFilledIconButton(
            icon = ReaderAppBarIcon.Next,
            contentDescription = state.nextChapterContentDescription,
            enabled = state.enabledNext,
            colors = colors,
            rotateDegrees = 90f,
            onClick = actions.onNextChapter,
        )
    }
}

@Composable
fun ReaderPageSlider(
    state: ReaderPageSliderState,
    orientation: ReaderNavigatorOrientation,
    onPageIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: ReaderAppBarColors = ReaderAppBarDefaults.colors(),
) {
    val dragOrTapModifier = when (orientation) {
        ReaderNavigatorOrientation.Horizontal -> Modifier
            .pointerInput(state.coercedTotalPages, state.isRtl) {
                detectTapGestures { offset ->
                    onPageIndexChange(
                        readerPageIndexAt(
                            x = offset.x,
                            width = size.width,
                            pageCount = state.coercedTotalPages,
                            isRtl = state.isRtl,
                        ),
                    )
                }
            }
            .pointerInput(state.coercedTotalPages, state.isRtl) {
                detectDragGestures { change, _ ->
                    onPageIndexChange(
                        readerPageIndexAt(
                            x = change.position.x,
                            width = size.width,
                            pageCount = state.coercedTotalPages,
                            isRtl = state.isRtl,
                        ),
                    )
                    change.consume()
                }
            }
        ReaderNavigatorOrientation.Vertical -> Modifier
            .pointerInput(state.coercedTotalPages) {
                detectTapGestures { offset ->
                    onPageIndexChange(
                        readerVerticalPageIndexAt(
                            y = offset.y,
                            height = size.height,
                            pageCount = state.coercedTotalPages,
                        ),
                    )
                }
            }
            .pointerInput(state.coercedTotalPages) {
                detectDragGestures { change, _ ->
                    onPageIndexChange(
                        readerVerticalPageIndexAt(
                            y = change.position.y,
                            height = size.height,
                            pageCount = state.coercedTotalPages,
                        ),
                    )
                    change.consume()
                }
            }
    }

    Canvas(
        modifier = modifier
            .semantics {
                contentDescription = state.contentDescription
            }
            .then(dragOrTapModifier),
    ) {
        val progress = state.progress
        if (orientation == ReaderNavigatorOrientation.Vertical) {
            val x = size.width / 2f
            val thumbY = size.height * progress
            drawLine(
                color = colors.content.copy(alpha = 0.22f),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 4f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = colors.selectedControl,
                start = Offset(x, 0f),
                end = Offset(x, thumbY),
                strokeWidth = 4f,
                cap = StrokeCap.Round,
            )
            drawCircle(
                color = colors.content,
                radius = 6f,
                center = Offset(x, thumbY),
            )
        } else {
            val y = size.height / 2f
            val visualProgress = if (state.isRtl) 1f - progress else progress
            val thumbX = size.width * visualProgress
            drawLine(
                color = colors.content.copy(alpha = 0.22f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 4f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = colors.selectedControl,
                start = if (state.isRtl) Offset(size.width, y) else Offset(0f, y),
                end = Offset(thumbX, y),
                strokeWidth = 4f,
                cap = StrokeCap.Round,
            )
            drawCircle(
                color = colors.content,
                radius = 6f,
                center = Offset(thumbX, y),
            )
        }
    }
}

fun readerPageIndexAt(
    x: Float,
    width: Int,
    pageCount: Int,
    isRtl: Boolean,
): Int {
    if (width <= 0 || pageCount <= 1) return 0
    val visualFraction = (x / width.toFloat()).coerceIn(0f, 1f)
    val logicalFraction = if (isRtl) 1f - visualFraction else visualFraction
    return (logicalFraction * (pageCount - 1).toFloat() + 0.5f)
        .toInt()
        .coerceIn(0, pageCount - 1)
}

fun readerVerticalPageIndexAt(
    y: Float,
    height: Int,
    pageCount: Int,
): Int {
    if (height <= 0 || pageCount <= 1) return 0
    val fraction = (y / height.toFloat()).coerceIn(0f, 1f)
    return (fraction * (pageCount - 1).toFloat() + 0.5f)
        .toInt()
        .coerceIn(0, pageCount - 1)
}

@Composable
private fun ReaderPageLabel(
    text: String,
    colors: ReaderAppBarColors,
    invisible: Boolean = false,
) {
    Text(
        text = text,
        color = if (invisible) colors.content.copy(alpha = 0f) else colors.content,
        style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
        maxLines = 1,
        modifier = Modifier.widthIn(min = 10.dp),
    )
}
