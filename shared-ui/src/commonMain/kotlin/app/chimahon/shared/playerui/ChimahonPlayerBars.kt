package app.chimahon.shared.playerui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonPlayerTopBar(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.78f), Color.Black.copy(alpha = 0.22f)),
                ),
            )
            .padding(horizontal = 8.dp, vertical = 10.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChimahonPlayerRoundButton(
                icon = ChimahonPlayerIcon.Back,
                contentDescription = "Back",
                onClick = actions.onBack,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        enabled = state.episodes.isNotEmpty(),
                        role = Role.Button,
                        onClick = { actions.onOpenDrawer(ChimahonPlayerDrawer.Episodes) },
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp),
            ) {
                Text(
                    text = state.title,
                    color = Color.White,
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val detail = listOfNotNull(state.episodeTitle, state.sourceName, state.subtitle)
                    .distinct()
                    .joinToString(" - ")
                if (detail.isNotBlank()) {
                    Text(
                        text = detail,
                        color = Color.White.copy(alpha = 0.76f),
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (state.castAvailable) {
                ChimahonPlayerRoundButton(
                    icon = ChimahonPlayerIcon.Cast,
                    selected = state.casting,
                    contentDescription = "Cast",
                    onClick = actions.onToggleCast,
                )
            }
            ChimahonPlayerRoundButton(
                icon = ChimahonPlayerIcon.Theater,
                selected = state.theaterMode,
                contentDescription = if (state.theaterMode) "Exit theater mode" else "Theater mode",
                onClick = actions.onToggleTheaterMode,
            )
            if (state.pictureInPictureAvailable) {
                ChimahonPlayerRoundButton(
                    icon = ChimahonPlayerIcon.PictureInPicture,
                    selected = state.pictureInPicture,
                    contentDescription = if (state.pictureInPicture) "Exit picture in picture" else "Picture in picture",
                    onClick = actions.onTogglePictureInPicture,
                )
            }
            ChimahonPlayerRoundButton(
                icon = if (state.controlsLocked) ChimahonPlayerIcon.Lock else ChimahonPlayerIcon.Unlock,
                selected = state.controlsLocked,
                contentDescription = if (state.controlsLocked) "Unlock controls" else "Lock controls",
                onClick = actions.onToggleLock,
            )
            ChimahonPlayerRoundButton(
                icon = if (state.fullscreen) ChimahonPlayerIcon.FullscreenExit else ChimahonPlayerIcon.Fullscreen,
                contentDescription = if (state.fullscreen) "Exit fullscreen" else "Fullscreen",
                onClick = actions.onToggleFullscreen,
            )
        }
        ChimahonPlayerTopChapterBar(
            state = state,
            actions = actions,
            modifier = Modifier.padding(start = 52.dp, top = 6.dp, end = 4.dp),
        )
    }
}

@Composable
fun ChimahonPlayerTopChapterBar(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    val chapter = state.activeChapter
    val episodeLabel = state.currentEpisodeLabel
    if (chapter == null && episodeLabel == null) return
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (episodeLabel != null) {
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.EpisodeList,
                label = episodeLabel,
                onClick = { actions.onOpenDrawer(ChimahonPlayerDrawer.Episodes) },
            )
        }
        if (chapter != null) {
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.Chapters,
                label = chapter.title,
                selected = chapter.isSkipCandidate,
                onClick = {
                    if (chapter.isSkipCandidate) {
                        actions.onSkipSegment(chapter)
                    } else {
                        actions.onOpenSheet(ChimahonPlayerSheet.Chapters)
                    }
                },
            )
            Text(
                text = chimahonFormatPlayerTime(chapter.startSeconds),
                color = Color.White.copy(alpha = 0.62f),
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.weight(1f))
        ChimahonPlayerActionPill(
            icon = ChimahonPlayerIcon.Play,
            label = if (state.settings.autoPlayNextEpisode) "Auto" else "Manual",
            selected = state.settings.autoPlayNextEpisode,
            onClick = { actions.onToggleAutoPlayNextEpisode(!state.settings.autoPlayNextEpisode) },
        )
    }
}

@Composable
fun ChimahonPlayerBottomControls(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.86f)),
                ),
            )
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ChimahonPlayerSeekRow(state = state, actions = actions)
        ChimahonPlayerStreamStatusRow(state = state, actions = actions)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChimahonPlayerRoundButton(
                icon = ChimahonPlayerIcon.Previous,
                contentDescription = "Previous episode",
                onClick = actions.onPreviousEpisode,
            )
            ChimahonPlayerRoundButton(
                icon = ChimahonPlayerIcon.SeekBack,
                contentDescription = "Seek back",
                onClick = { actions.onSeekBy(-10L) },
            )
            ChimahonPlayerPrimaryPlayButton(
                paused = state.paused,
                loading = state.loading,
                onClick = actions.onTogglePlayback,
            )
            ChimahonPlayerRoundButton(
                icon = ChimahonPlayerIcon.SeekForward,
                contentDescription = "Seek forward",
                onClick = { actions.onSeekBy(10L) },
            )
            ChimahonPlayerRoundButton(
                icon = ChimahonPlayerIcon.Next,
                contentDescription = "Next episode",
                onClick = actions.onNextEpisode,
            )
            state.activeSkipSegment?.let { chapter ->
                ChimahonPlayerActionPill(
                    icon = ChimahonPlayerIcon.Chapters,
                    label = chapter.skipActionTitle,
                    selected = true,
                    onClick = { actions.onSkipSegment(chapter) },
                )
            }
            Spacer(Modifier.weight(1f))
            if (state.episodes.isNotEmpty()) {
                ChimahonPlayerActionPill(
                    icon = ChimahonPlayerIcon.EpisodeList,
                    label = state.currentEpisodeLabel ?: "Episodes",
                    onClick = { actions.onOpenDrawer(ChimahonPlayerDrawer.Episodes) },
                )
            }
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.Speed,
                label = formatPlayerSpeed(state.settings.playbackSpeed),
                selected = state.settings.playbackSpeed != 1f,
                onClick = { actions.onOpenSheet(ChimahonPlayerSheet.PlaybackSpeed) },
            )
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.Fit,
                label = state.videoFit.title,
                selected = state.videoFit != ChimahonPlayerVideoFit.Fit,
                onClick = actions.onCycleVideoFit,
            )
            if (state.tracks.hosters.isNotEmpty()) {
                ChimahonPlayerActionPill(
                    icon = ChimahonPlayerIcon.Hoster,
                    label = state.tracks.selectedHoster?.title ?: "Hoster",
                    selected = state.tracks.selectedHosterId != null,
                    onClick = { actions.onOpenSheet(ChimahonPlayerSheet.Hosters) },
                )
            }
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.Subtitles,
                label = "Subs",
                selected = state.tracks.selectedSubtitleTrackIds.isNotEmpty(),
                onClick = { actions.onOpenSheet(ChimahonPlayerSheet.SubtitleTracks) },
            )
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.Audio,
                label = "Audio",
                selected = state.tracks.selectedAudioTrackId != null,
                onClick = { actions.onOpenSheet(ChimahonPlayerSheet.AudioTracks) },
            )
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.Quality,
                label = "Quality",
                selected = state.tracks.selectedQualityId != null,
                onClick = { actions.onOpenSheet(ChimahonPlayerSheet.QualityTracks) },
            )
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.More,
                label = "More",
                onClick = { actions.onOpenSheet(ChimahonPlayerSheet.More) },
            )
        }
    }
}

@Composable
fun ChimahonPlayerStreamStatusRow(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    val statusLabel = listOfNotNull(
        state.stream.statusLabel,
        state.stream.networkLabel,
        state.stream.downloadState.takeIf { it != ChimahonPlayerDownloadState.NotDownloaded }?.title,
        state.stream.downloadProgress.takeIf { it > 0 }?.let { "$it%" },
    ).joinToString(" - ")
    if (statusLabel.isBlank()) return
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = statusLabel,
            color = Color.White.copy(alpha = 0.72f),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (state.stream.retryable) {
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.Refresh,
                label = "Retry",
                onClick = actions.onRetry,
            )
        }
        if (state.stream.downloadState != ChimahonPlayerDownloadState.Downloaded) {
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.Download,
                label = state.stream.downloadState.title,
                selected = state.stream.downloadState == ChimahonPlayerDownloadState.Downloading,
                onClick = actions.onDownloadEpisode,
            )
        } else {
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.DownloadOffline,
                label = "Downloaded",
                selected = state.stream.playbackSource == ChimahonPlayerPlaybackSource.Download,
                onClick = actions.onToggleDownloadPlayback,
            )
        }
    }
}

@Composable
fun ChimahonPlayerSeekRow(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = chimahonFormatPlayerTime(state.positionSeconds),
            color = Color.White,
            style = MaterialTheme.typography.caption,
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 32.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.22f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(state.bufferedFraction)
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.32f)),
                )
            }
            Slider(
                value = state.progressFraction,
                onValueChange = { fraction ->
                    actions.onSeekTo((state.durationSeconds * fraction).toLong())
                },
                enabled = state.hasDuration,
            )
        }
        Text(
            text = chimahonFormatPlayerTime(state.durationSeconds),
            color = Color.White.copy(alpha = 0.76f),
            style = MaterialTheme.typography.caption,
        )
    }
}

@Composable
fun ChimahonPlayerEpisodeStrip(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    if (state.episodes.isEmpty()) return
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.68f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item {
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.EpisodeList,
                label = state.currentEpisodeLabel ?: "Episodes",
                selected = true,
                onClick = { actions.onOpenDrawer(ChimahonPlayerDrawer.Episodes) },
            )
        }
        items(state.episodes, key = { it.id }) { episode ->
            val selected = episode.id == state.currentEpisodeId
            ChimahonPlayerEpisodeChip(
                episode = episode,
                selected = selected,
                onClick = { actions.onEpisodeClick(episode) },
            )
        }
    }
}

@Composable
fun ChimahonPlayerChapterStrip(
    state: ChimahonPlayerUiState,
    actions: ChimahonPlayerUiActions,
    modifier: Modifier = Modifier,
) {
    if (state.chapters.isEmpty()) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.54f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        state.chapters.forEach { chapter ->
            val selected = chapter.contains(state.positionSeconds)
            ChimahonPlayerActionPill(
                icon = ChimahonPlayerIcon.Chapters,
                label = if (chapter.isSkipCandidate) chapter.skipActionTitle else chapter.title,
                selected = selected,
                onClick = {
                    if (chapter.isSkipCandidate) {
                        actions.onSkipSegment(chapter)
                    } else {
                        actions.onChapterClick(chapter)
                    }
                },
            )
        }
    }
}

@Composable
fun ChimahonPlayerRoundButton(
    icon: ChimahonPlayerIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (selected) Color.White.copy(alpha = 0.24f) else Color.Black.copy(alpha = 0.34f)),
    ) {
        Icon(
            imageVector = icon.imageVector,
            contentDescription = contentDescription,
            tint = if (enabled) Color.White else Color.White.copy(alpha = 0.38f),
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
fun ChimahonPlayerPrimaryPlayButton(
    paused: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(56.dp),
        shape = CircleShape,
        color = Color.White,
        contentColor = Color.Black,
        elevation = 8.dp,
    ) {
        Box(
            modifier = Modifier.clickable(role = Role.Button, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (paused || loading) {
                    ChimahonPlayerIcon.Play.imageVector
                } else {
                    ChimahonPlayerIcon.Pause.imageVector
                },
                contentDescription = if (paused) "Play" else "Pause",
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Composable
fun ChimahonPlayerActionPill(
    icon: ChimahonPlayerIcon,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) Color.White.copy(alpha = 0.24f) else Color.White.copy(alpha = 0.12f))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = icon.imageVector,
            contentDescription = null,
            tint = Color.White.copy(alpha = if (enabled) 0.92f else 0.38f),
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = if (enabled) 0.92f else 0.38f),
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ChimahonPlayerEpisodeChip(
    episode: ChimahonPlayerEpisodeUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) Color.White.copy(alpha = 0.26f) else Color.White.copy(alpha = 0.10f))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = episode.displayNumber ?: episode.title,
            color = Color.White,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (episode.progressFraction > 0f && !episode.seen) {
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.20f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(episode.progressFraction)
                        .height(3.dp)
                        .background(Color.White),
                )
            }
        }
    }
}

private fun formatPlayerSpeed(speed: Float): String {
    val raw = speed.toString()
    val trimmed = raw.trimEnd('0').trimEnd('.')
    return "${trimmed.ifBlank { "1" }}x"
}
