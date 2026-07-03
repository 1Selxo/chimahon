package app.chimahon.shared.novelui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonNovelReaderHud(
    state: ChimahonNovelReaderBarState,
    actions: ChimahonNovelReaderBarActions,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface.copy(alpha = 0.92f),
    contentColor: Color = MaterialTheme.colors.onSurface,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ChimahonNovelReaderTopBar(
            state = state,
            actions = actions,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
        )
        Spacer(modifier = Modifier.weight(1f, fill = false))
        ChimahonNovelReaderBottomBar(
            state = state,
            actions = actions,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
        )
    }
}

@Composable
fun ChimahonNovelReaderTopBar(
    state: ChimahonNovelReaderBarState,
    actions: ChimahonNovelReaderBarActions,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface.copy(alpha = 0.92f),
    contentColor: Color = MaterialTheme.colors.onSurface,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        elevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(onClick = actions.onNavigateUp) {
                Text("Back", color = contentColor)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = state.title,
                        color = contentColor,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (state.trackingActive) {
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
                        )
                    }
                }
                Text(
                    text = state.chapterTitle ?: state.progressLabel,
                    color = contentColor.copy(alpha = 0.68f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (state.lookupActive) {
                TextButton(onClick = actions.onDismissLookup) {
                    Text("Close lookup", color = contentColor)
                }
            }
            TextButton(onClick = actions.onToggleHud) {
                Text("Hide", color = contentColor)
            }
        }
    }
}

@Composable
fun ChimahonNovelReaderBottomBar(
    state: ChimahonNovelReaderBarState,
    actions: ChimahonNovelReaderBarActions,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface.copy(alpha = 0.92f),
    contentColor: Color = MaterialTheme.colors.onSurface,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        elevation = 12.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ChimahonNovelReaderChapterNavigator(
                state = state,
                actions = actions,
                contentColor = contentColor,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChimahonNovelReaderAction("Chapters", contentColor, actions.onOpenChapters)
                ChimahonNovelReaderAction("Appearance", contentColor, actions.onOpenAppearance)
                ChimahonNovelReaderAction("Stats", contentColor, actions.onOpenStatistics)
                if (state.sasayakiAvailable) {
                    ChimahonNovelReaderAction("Sasayaki", contentColor, actions.onOpenSasayaki)
                }
                ChimahonNovelReaderAction(
                    text = if (state.focusMode) "Focus on" else "Focus",
                    contentColor = contentColor,
                    onClick = actions.onToggleFocusMode,
                    selected = state.focusMode,
                )
            }
        }
    }
}

@Composable
fun ChimahonNovelReaderChapterNavigator(
    state: ChimahonNovelReaderBarState,
    actions: ChimahonNovelReaderBarActions,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colors.onSurface,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Button(
                onClick = actions.onPreviousChapter,
                enabled = state.canGoPrevious,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = contentColor.copy(alpha = 0.10f),
                    contentColor = contentColor,
                ),
                elevation = null,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
            ) {
                Text("Prev")
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = state.progressLabel,
                        color = contentColor,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                    Text(
                        text = if (state.chapterCount > 0) {
                            "${state.chapterIndex + 1}/${state.chapterCount}"
                        } else {
                            "0/0"
                        },
                        color = contentColor.copy(alpha = 0.62f),
                        fontSize = 12.sp,
                    )
                }
                LinearProgressIndicator(
                    progress = state.progressLabel.removeSuffix("%").toFloatOrNull()
                        ?.div(100f)
                        ?.coerceIn(0f, 1f)
                        ?: 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(100.dp)),
                    color = contentColor,
                    backgroundColor = contentColor.copy(alpha = 0.16f),
                )
            }
            Button(
                onClick = actions.onNextChapter,
                enabled = state.canGoNext,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = contentColor.copy(alpha = 0.10f),
                    contentColor = contentColor,
                ),
                elevation = null,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp),
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun ChimahonNovelReaderProgressSlider(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colors.onSurface,
) {
    Slider(
        modifier = modifier,
        value = progress.coerceIn(0f, 1f),
        onValueChange = { onProgressChange(it.coerceIn(0f, 1f)) },
        valueRange = 0f..1f,
        colors = SliderDefaults.colors(
            thumbColor = contentColor,
            activeTrackColor = contentColor,
            inactiveTrackColor = contentColor.copy(alpha = 0.18f),
        ),
    )
}

@Composable
private fun ChimahonNovelReaderAction(
    text: String,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) contentColor.copy(alpha = 0.16f) else Color.Transparent,
    ) {
        TextButton(onClick = onClick) {
            Text(
                text = text,
                color = contentColor,
                fontSize = 12.sp,
                maxLines = 1,
            )
        }
    }
}
