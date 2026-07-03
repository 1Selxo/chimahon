package app.chimahon.shared.novelreaderui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NovelReaderTopHud(
    state: NovelReaderUiState,
    actions: NovelReaderActions,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = palette.surface,
        elevation = 10.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            NovelReaderHudIconButton(
                icon = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                palette = palette,
                onClick = actions.onBack,
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = state.title,
                        color = palette.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (state.hud.trackingActive) {
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(8.dp)
                                .background(color = palette.accent, shape = CircleShape),
                        )
                    }
                }
                Text(
                    text = state.activeChapter?.title ?: state.subtitle ?: state.sourceLabel.orEmpty(),
                    color = palette.muted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (state.selection != null || state.hud.lookupActive) {
                NovelReaderHudIconButton(
                    icon = Icons.Outlined.Search,
                    contentDescription = "Lookup",
                    palette = palette,
                    onClick = {
                        state.selection?.let { selection ->
                            actions.onSelectionAction(NovelReaderSelectionAction.Lookup, selection)
                        }
                    },
                )
            }
            NovelReaderHudIconButton(
                icon = Icons.Outlined.FormatListNumbered,
                contentDescription = "Chapters",
                palette = palette,
                onClick = actions.onOpenChapters,
            )
            NovelReaderHudIconButton(
                icon = Icons.Outlined.Palette,
                contentDescription = "Appearance",
                palette = palette,
                onClick = actions.onOpenTypography,
            )
            NovelReaderHudIconButton(
                icon = Icons.Outlined.CropFree,
                contentDescription = if (state.chrome.fullScreen || state.chrome.requestedFullScreen) {
                    "Exit full screen"
                } else {
                    "Full screen"
                },
                palette = palette,
                onClick = actions.onToggleFullScreen,
            )
            NovelReaderHudIconButton(
                icon = Icons.Outlined.Close,
                contentDescription = "Hide controls",
                palette = palette,
                onClick = actions.onToggleHud,
            )
        }
    }
}

@Composable
fun NovelReaderBottomHud(
    state: NovelReaderUiState,
    actions: NovelReaderActions,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = palette.surface,
        elevation = 14.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            NovelReaderPageNavigator(state = state, actions = actions, palette = palette)
            NovelReaderProgressBar(state = state, actions = actions, palette = palette)
            NovelReaderPersistenceStatus(state = state, palette = palette)
            NovelReaderActionStrip(state = state, actions = actions, palette = palette)
            if (state.inputHints.isNotEmpty()) {
                NovelReaderInputHintRow(
                    hints = state.inputHints,
                    palette = palette,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun NovelReaderPageNavigator(
    state: NovelReaderUiState,
    actions: NovelReaderActions,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Button(
            onClick = actions.onPreviousChapter,
            enabled = state.hud.canGoPreviousChapter,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = palette.text.copy(alpha = 0.10f),
                contentColor = palette.onSurface,
                disabledBackgroundColor = palette.text.copy(alpha = 0.05f),
                disabledContentColor = palette.muted,
            ),
            elevation = null,
        ) {
            Icon(Icons.Outlined.SkipPrevious, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Chapter")
        }
        NovelReaderHudIconButton(
            icon = Icons.Outlined.SkipPrevious,
            contentDescription = "Previous page",
            palette = palette,
            enabled = state.hud.canGoPreviousPage,
            onClick = actions.onPreviousPage,
        )
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = state.progressLabel,
                color = palette.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = listOfNotNull(
                    state.readingMode.title,
                    state.readingDirection.takeIf { it != NovelReadingDirection.Default }?.title,
                ).joinToString(" - "),
                color = palette.muted,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        NovelReaderHudIconButton(
            icon = Icons.Outlined.SkipNext,
            contentDescription = "Next page",
            palette = palette,
            enabled = state.hud.canGoNextPage,
            onClick = actions.onNextPage,
        )
        Button(
            onClick = actions.onNextChapter,
            enabled = state.hud.canGoNextChapter,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = palette.text.copy(alpha = 0.10f),
                contentColor = palette.onSurface,
                disabledBackgroundColor = palette.text.copy(alpha = 0.05f),
                disabledContentColor = palette.muted,
            ),
            elevation = null,
        ) {
            Text("Chapter")
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Outlined.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun NovelReaderProgressBar(
    state: NovelReaderUiState,
    actions: NovelReaderActions,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Slider(
            value = state.progress.coerceIn(0f, 1f),
            onValueChange = actions.onProgressChange,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = palette.onSurface,
                activeTrackColor = palette.onSurface,
                inactiveTrackColor = palette.outline,
            ),
        )
        LinearProgressIndicator(
            progress = state.progress.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = palette.onSurface.copy(alpha = 0.74f),
            backgroundColor = palette.outline,
        )
    }
}

@Composable
fun NovelReaderActionStrip(
    state: NovelReaderUiState,
    actions: NovelReaderActions,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NovelReaderHudTextAction("Chapters", Icons.Outlined.FormatListNumbered, palette, actions.onOpenChapters)
        NovelReaderHudTextAction("Appearance", Icons.Outlined.Palette, palette, actions.onOpenTypography)
        NovelReaderHudTextAction("Stats", Icons.Outlined.QueryStats, palette, actions.onOpenStatistics)
        if (state.hud.sasayakiAvailable) {
            NovelReaderHudTextAction("Sasayaki", Icons.Outlined.Search, palette, actions.onOpenSasayaki)
        }
        NovelReaderHudTextAction(
            text = if (state.chrome.fullScreen || state.chrome.requestedFullScreen) "Window" else "Full",
            icon = Icons.Outlined.CropFree,
            palette = palette,
            selected = state.chrome.fullScreen || state.chrome.requestedFullScreen,
            onClick = actions.onToggleFullScreen,
        )
        NovelReaderHudTextAction(
            text = if (state.hud.focusMode) "Focus on" else "Focus",
            icon = if (state.hud.focusMode) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
            palette = palette,
            selected = state.hud.focusMode,
            onClick = actions.onToggleFocusMode,
        )
    }
}

@Composable
fun NovelReaderHudIconButton(
    icon: ImageVector,
    contentDescription: String,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier.size(42.dp),
        enabled = enabled,
        onClick = onClick,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) palette.onSurface else palette.muted,
        )
    }
}

@Composable
fun NovelReaderChromeEdgeHints(
    chrome: NovelReaderChromeState,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (chrome.topHint.isNotBlank()) {
            NovelReaderEdgeHint(
                text = chrome.topHint,
                palette = palette,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
            )
        }
        if (chrome.bottomHint.isNotBlank()) {
            NovelReaderEdgeHint(
                text = chrome.bottomHint,
                palette = palette,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
            )
        }
    }
}

@Composable
private fun BoxScope.NovelReaderEdgeHint(
    text: String,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = palette.surface.copy(alpha = 0.76f),
        border = androidx.compose.foundation.BorderStroke(1.dp, palette.outline),
        elevation = 6.dp,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            text = text,
            color = palette.muted,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun NovelReaderPersistenceStatus(
    state: NovelReaderUiState,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    val persistence = state.progressPersistence
    val status = when {
        persistence.pendingSave -> "Saving progress..."
        persistence.lastErrorMessage != null -> "Progress save failed"
        persistence.savedProgressLabel != null -> "Saved ${persistence.savedProgressLabel}"
        else -> null
    } ?: return

    Text(
        modifier = modifier.fillMaxWidth(),
        text = status,
        color = if (persistence.lastErrorMessage != null) palette.accent else palette.muted,
        fontSize = 11.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun NovelReaderHudTextAction(
    text: String,
    icon: ImageVector,
    palette: NovelReaderPalette,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) palette.text.copy(alpha = 0.14f) else palette.text.copy(alpha = 0.04f),
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = androidx.compose.ui.graphics.Color.Transparent,
                contentColor = palette.onSurface,
            ),
            elevation = null,
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(text = text, maxLines = 1, fontSize = 12.sp)
        }
    }
}
