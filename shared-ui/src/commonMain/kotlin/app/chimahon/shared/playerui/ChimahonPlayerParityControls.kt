package app.chimahon.shared.playerui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonPlayerAndroidParityOverlays(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (state.controlsVisible && !state.controlsLocked) {
            state.edgeControls.sliderFor(ChimahonPlayerControlSide.Left)?.let { slider ->
                ChimahonPlayerEdgeSlider(
                    slider = slider,
                    actions = actions,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp),
                )
            }
            state.edgeControls.sliderFor(ChimahonPlayerControlSide.Right)?.let { slider ->
                ChimahonPlayerEdgeSlider(
                    slider = slider,
                    actions = actions,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 20.dp),
                )
            }
            ChimahonPlayerLookupAffordanceRails(
                state = state,
                actions = actions,
                modifier = Modifier.fillMaxSize(),
            )
        }
        ChimahonPlayerDrawerOverlay(
            state = state,
            actions = actions,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun ChimahonPlayerEdgeSlider(
    slider: ChimahonPlayerSideSliderUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    val onValueChange: (Float) -> Unit = when (slider.kind) {
        ChimahonPlayerSideSliderKind.Volume -> actions.onVolumeChange
        ChimahonPlayerSideSliderKind.Brightness -> actions.onBrightnessChange
    }
    Column(
        modifier = modifier
            .width(66.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.66f))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = slider.displayValue,
            color = Color.White,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(154.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(slider.fraction)
                        .background(Color.White.copy(alpha = 0.62f)),
                )
                slider.boostedFraction?.let { boosted ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(boosted)
                            .background(Color(0xFFFFC857.toInt()).copy(alpha = 0.74f)),
                    )
                }
            }
            Slider(
                value = slider.value.coerceIn(slider.min, slider.max),
                onValueChange = onValueChange,
                valueRange = slider.min..slider.max,
                modifier = Modifier
                    .width(154.dp)
                    .rotate(-90f),
            )
        }
        Icon(
            imageVector = when (slider.kind) {
                ChimahonPlayerSideSliderKind.Volume -> ChimahonPlayerIcon.Volume.imageVector
                ChimahonPlayerSideSliderKind.Brightness -> ChimahonPlayerIcon.Brightness.imageVector
            },
            contentDescription = slider.label,
            tint = Color.White,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
fun ChimahonPlayerLookupAffordanceRails(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    val lookup = state.lookup
    Box(modifier = modifier) {
        if (lookup.videoOcrAvailable) {
            ChimahonPlayerFloatingAction(
                icon = ChimahonPlayerIcon.Ocr,
                label = if (lookup.videoOcrBusy) "OCR..." else "OCR",
                selected = state.videoOcrEnabled || lookup.videoOcrActive,
                enabled = !lookup.videoOcrBusy,
                onClick = actions.onCaptureVideoOcr,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 22.dp, top = 220.dp),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 22.dp, top = 220.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (lookup.subtitleCues.isNotEmpty()) {
                ChimahonPlayerFloatingAction(
                    icon = ChimahonPlayerIcon.Subtitles,
                    label = "Lines",
                    selected = state.selectedPanel == ChimahonPlayerPanel.SubtitleCueList,
                    onClick = { actions.onOpenPanel(ChimahonPlayerPanel.SubtitleCueList) },
                )
            }
            if (lookup.subtitleLookupAvailable || lookup.hasSubtitleText) {
                ChimahonPlayerFloatingAction(
                    icon = ChimahonPlayerIcon.Lookup,
                    label = "Lookup",
                    selected = state.subtitleLookupEnabled || lookup.subtitleLookupActive,
                    onClick = actions.onToggleSubtitleLookup,
                )
            }
        }
    }
}

@Composable
fun ChimahonPlayerFloatingAction(
    icon: ChimahonPlayerIcon,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) Color.White.copy(alpha = 0.28f) else Color.Black.copy(alpha = 0.58f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon.imageVector,
            contentDescription = label,
            tint = Color.White.copy(alpha = if (enabled) 0.94f else 0.38f),
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = if (enabled) 0.94f else 0.38f),
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ChimahonPlayerDrawerOverlay(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    val drawer = state.selectedDrawer ?: return
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.42f))
                .clickable(role = Role.Button, onClick = actions.onDismissDrawer),
        )
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .fillMaxWidth(0.88f)
                .widthIn(max = 430.dp),
            shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp),
            color = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface,
            elevation = 16.dp,
        ) {
            when (drawer) {
                ChimahonPlayerDrawer.Episodes -> ChimahonPlayerEpisodeDrawer(state, actions)
            }
        }
    }
}

@Composable
fun ChimahonPlayerEpisodeDrawer(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        item {
            ChimahonPlayerSheetHeader(
                title = "Episodes",
                subtitle = state.currentEpisodeLabel ?: "${state.episodes.size} episode(s)",
                onClose = actions.onDismissDrawer,
            )
        }
        if (state.episodes.isEmpty()) {
            item {
                ChimahonPlayerEmptyNote("Episode list will appear once the player receives a playlist.")
            }
        } else {
            items(state.episodes, key = { it.id }) { episode ->
                ChimahonPlayerEpisodeDrawerRow(
                    episode = episode,
                    selected = episode.id == state.currentEpisodeId,
                    onClick = {
                        actions.onEpisodeClick(episode)
                        actions.onDismissDrawer()
                    },
                )
            }
        }
    }
}

@Composable
fun ChimahonPlayerEpisodeDrawerRow(
    episode: ChimahonPlayerEpisodeUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val metadata = listOfNotNull(
        episode.subtitle?.takeIf(String::isNotBlank),
        episode.durationSeconds.takeIf { it > 0L }?.let(::chimahonFormatPlayerTime),
        episode.progressSeconds.takeIf { it > 0L && !episode.seen }?.let { "${chimahonFormatPlayerTime(it)} watched" },
        if (episode.downloaded) "Downloaded" else null,
        if (episode.filler) "Filler" else null,
    ).joinToString(" - ")
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    if (selected) {
                        MaterialTheme.colors.primary.copy(alpha = 0.18f)
                    } else {
                        MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = episode.displayNumber ?: "Ep",
                color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.74f),
                style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = episode.title,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    fontStyle = if (selected) FontStyle.Italic else FontStyle.Normal,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (metadata.isNotBlank()) {
                Text(
                    text = metadata,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (episode.progressFraction > 0f && !episode.seen) {
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colors.onSurface.copy(alpha = 0.10f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(episode.progressFraction)
                            .height(3.dp)
                            .background(MaterialTheme.colors.primary),
                    )
                }
            }
        }
        if (episode.bookmarked) {
            Text(
                text = "Bookmarked",
                style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colors.primary,
            )
        } else if (episode.seen) {
            Text(
                text = "Seen",
                style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.52f),
            )
        }
    }
}

@Composable
fun ChimahonSubtitleCueListPanel(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
) {
    LazyColumn {
        item {
            ChimahonPlayerSheetHeader(
                title = "Subtitle lines",
                subtitle = state.lookup.selectedText ?: state.lookup.hint ?: "Tap a cue to seek or lookup",
                onClose = actions.onDismissOverlay,
            )
        }
        if (state.lookup.subtitleCues.isEmpty()) {
            item {
                ChimahonPlayerEmptyNote("Subtitle lines will appear here while a track is active.")
            }
        } else {
            items(state.lookup.subtitleCues, key = { it.id }) { cue ->
                ChimahonPlayerTrackRow(
                    title = cue.text,
                    subtitle = cue.timeLabel,
                    selected = cue.selected,
                    icon = ChimahonPlayerIcon.Subtitles,
                    onClick = { actions.onSubtitleCueClick(cue) },
                )
            }
        }
    }
}

@Composable
fun ChimahonPlayerActionHintRow(
    hint: ChimahonPlayerActionHint,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = when (hint.kind) {
                ChimahonPlayerInputKind.Keyboard -> ChimahonPlayerIcon.Keyboard.imageVector
                ChimahonPlayerInputKind.Controller,
                ChimahonPlayerInputKind.Remote -> ChimahonPlayerIcon.Controller.imageVector
            },
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
            modifier = Modifier.size(20.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = hint.action,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!hint.description.isNullOrBlank()) {
                Text(
                    text = hint.description,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Text(
            text = hint.input,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
