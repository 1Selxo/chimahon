package app.chimahon.shared.animeui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.DownloadForOffline
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PictureInPictureAlt
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.Theaters
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonAnimePlayerShell(
    state: ChimahonAnimePlayerUiState,
    onAction: (ChimahonAnimePlayerActionId) -> Unit,
    onSeekFractionChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    callbacks: ChimahonAnimePlayerCallbacks = ChimahonAnimePlayerCallbacks(
        onAction = onAction,
        onSeekFractionChange = onSeekFractionChange,
    ),
    videoContent: @Composable BoxScope.() -> Unit = {
        ChimahonAnimePlayerPlaceholder(state = state)
    },
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        videoContent()
        if (state.subtitleText != null) {
            ChimahonAnimeSubtitleOverlay(
                text = state.subtitleText,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (state.controlsVisible) 132.dp else 38.dp),
            )
        }
        if (state.loading || state.loadingEpisode) {
            ChimahonAnimePlayerLoading(
                text = if (state.loadingEpisode) "Loading episode" else "Buffering",
                modifier = Modifier.align(Alignment.Center),
            )
        }
        if (state.errorMessage != null) {
            ChimahonAnimePlayerError(
                message = state.errorMessage,
                onRetry = callbacks.onRetry,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        if (state.controlsVisible) {
            ChimahonAnimePlayerTopBar(
                state = state,
                callbacks = callbacks,
                modifier = Modifier.align(Alignment.TopCenter),
            )
            ChimahonAnimePlayerCenterControls(
                state = state,
                callbacks = callbacks,
                modifier = Modifier.align(Alignment.Center),
            )
            ChimahonAnimePlayerBottomBar(
                state = state,
                callbacks = callbacks,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
fun ChimahonAnimePlayerTopBar(
    state: ChimahonAnimePlayerUiState,
    callbacks: ChimahonAnimePlayerCallbacks,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.62f),
        elevation = 8.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ChimahonAnimeIconButton(
                icon = ChimahonAnimePlayerActionId.Back.icon,
                contentDescription = "Back",
                onClick = { callbacks.onAction(ChimahonAnimePlayerActionId.Back) },
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.animeTitle.ifBlank { "Anime" },
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = listOfNotNull(
                        state.episodeTitle.takeIf(String::isNotBlank),
                        state.sourceName?.takeIf(String::isNotBlank),
                        state.selectedHosterModel?.title?.takeIf(String::isNotBlank) ?: state.selectedHoster?.takeIf(String::isNotBlank),
                        state.selectedQuality?.takeIf(String::isNotBlank),
                    ).joinToString(" - "),
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            ChimahonAnimeIconButton(
                icon = ChimahonAnimePlayerActionId.LockControls.icon,
                contentDescription = if (state.controlsLocked) "Unlock controls" else "Lock controls",
                onClick = { callbacks.onAction(ChimahonAnimePlayerActionId.LockControls) },
                selected = state.controlsLocked,
            )
            if (state.pictureInPictureAvailable) {
                ChimahonAnimeIconButton(
                    icon = Icons.Outlined.PictureInPictureAlt,
                    contentDescription = if (state.pictureInPicture) "Exit picture in picture" else "Picture in picture",
                    onClick = callbacks.onTogglePictureInPicture,
                    selected = state.pictureInPicture,
                )
            }
            ChimahonAnimeIconButton(
                icon = Icons.Outlined.Theaters,
                contentDescription = if (state.theaterMode) "Exit theater mode" else "Theater mode",
                onClick = callbacks.onToggleTheaterMode,
                selected = state.theaterMode,
            )
            ChimahonAnimeIconButton(
                icon = Icons.Outlined.CropFree,
                contentDescription = if (state.fullscreen) "Exit fullscreen" else "Fullscreen",
                onClick = {
                    callbacks.onAction(ChimahonAnimePlayerActionId.Fullscreen)
                    callbacks.onToggleFullscreen()
                },
                selected = state.fullscreen,
            )
        }
    }
}

@Composable
fun ChimahonAnimePlayerCenterControls(
    state: ChimahonAnimePlayerUiState,
    callbacks: ChimahonAnimePlayerCallbacks,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.34f), RoundedCornerShape(40.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        IconButton(
            enabled = state.hasPreviousEpisode,
            onClick = { callbacks.onAction(ChimahonAnimePlayerActionId.PreviousEpisode) },
        ) {
            Icon(Icons.Outlined.SkipPrevious, contentDescription = "Previous episode", tint = Color.White)
        }
        IconButton(onClick = { callbacks.onAction(ChimahonAnimePlayerActionId.SeekBackward) }) {
            Icon(ChimahonAnimePlayerActionId.SeekBackward.icon, contentDescription = "Seek backward", tint = Color.White)
        }
        IconButton(
            modifier = Modifier.size(58.dp),
            onClick = { callbacks.onAction(ChimahonAnimePlayerActionId.PlayPause) },
        ) {
            Icon(
                imageVector = if (state.paused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                contentDescription = if (state.paused) "Play" else "Pause",
                tint = Color.White,
                modifier = Modifier.size(44.dp),
            )
        }
        IconButton(onClick = { callbacks.onAction(ChimahonAnimePlayerActionId.SeekForward) }) {
            Icon(ChimahonAnimePlayerActionId.SeekForward.icon, contentDescription = "Seek forward", tint = Color.White)
        }
        IconButton(
            enabled = state.hasNextEpisode,
            onClick = { callbacks.onAction(ChimahonAnimePlayerActionId.NextEpisode) },
        ) {
            Icon(Icons.Outlined.SkipNext, contentDescription = "Next episode", tint = Color.White)
        }
    }
}

@Composable
fun ChimahonAnimePlayerBottomBar(
    state: ChimahonAnimePlayerUiState,
    callbacks: ChimahonAnimePlayerCallbacks,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Black.copy(alpha = 0.70f),
        elevation = 12.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(text = state.positionLabel, color = Color.White, fontSize = 12.sp)
                Box(modifier = Modifier.weight(1f)) {
                    LinearProgressIndicator(
                        progress = state.bufferedFraction.coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .align(Alignment.Center),
                        color = Color.White.copy(alpha = 0.26f),
                        backgroundColor = Color.White.copy(alpha = 0.10f),
                    )
                    Slider(
                        value = state.progressFraction,
                        onValueChange = callbacks.onSeekFractionChange,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.Transparent,
                        ),
                    )
                }
                Text(text = state.durationLabel, color = Color.White, fontSize = 12.sp)
            }
            ChimahonAnimePlayerStreamStatusRow(
                state = state,
                callbacks = callbacks,
            )
            ChimahonAnimePlayerSelectionTray(
                state = state,
                callbacks = callbacks,
            )
            ChimahonAnimePlayerActionTray(
                actions = state.actionSurface.primaryActions,
                callbacks = callbacks,
            )
            ChimahonAnimePlayerActionTray(
                actions = state.actionSurface.secondaryActions,
                callbacks = callbacks,
                compact = true,
            )
        }
    }
}

@Composable
fun ChimahonAnimePlayerActionTray(
    actions: List<ChimahonAnimePlayerAction>,
    callbacks: ChimahonAnimePlayerCallbacks,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        actions.forEach { action ->
            ChimahonAnimePlayerActionButton(
                action = action,
                compact = compact,
                onClick = { callbacks.onAction(action.id) },
            )
        }
    }
}

@Composable
fun ChimahonAnimePlayerActionButton(
    action: ChimahonAnimePlayerAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (action.selected) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.08f),
    ) {
        TextButton(
            enabled = action.enabled,
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = if (compact) 9.dp else 12.dp, vertical = 6.dp),
        ) {
            Icon(
                imageVector = action.id.icon,
                contentDescription = null,
                tint = if (action.enabled) Color.White else Color.White.copy(alpha = 0.35f),
                modifier = Modifier.size(if (compact) 16.dp else 18.dp),
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = action.valueLabel ?: action.label,
                color = if (action.enabled) Color.White else Color.White.copy(alpha = 0.35f),
                fontSize = if (compact) 11.sp else 12.sp,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun ChimahonAnimePlayerStreamStatusRow(
    state: ChimahonAnimePlayerUiState,
    callbacks: ChimahonAnimePlayerCallbacks,
    modifier: Modifier = Modifier,
) {
    val sourceUrl = state.resolvedSourceUrl()
    val statusLabel = listOfNotNull(
        state.errorMessage,
        state.stream.statusLabel,
        state.stream.networkLabel,
        state.stream.downloadState.takeIf { it != ChimahonAnimeDownloadUiState.NotDownloaded }?.title,
        state.stream.downloadProgress.takeIf { it > 0 }?.let { "$it%" },
    ).joinToString(" - ")
    if (
        statusLabel.isBlank() &&
        sourceUrl.isNullOrBlank() &&
        state.stream.downloadState == ChimahonAnimeDownloadUiState.NotDownloaded
    ) {
        return
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (statusLabel.isNotBlank()) {
            Text(
                text = statusLabel,
                color = Color.White.copy(alpha = 0.74f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (state.stream.retryable || state.errorMessage != null) {
            ChimahonAnimePlayerMiniButton(
                icon = Icons.Outlined.Refresh,
                label = "Retry",
                onClick = callbacks.onRetry,
            )
        }
        if (state.stream.downloadState != ChimahonAnimeDownloadUiState.Downloaded) {
            ChimahonAnimePlayerMiniButton(
                icon = Icons.Outlined.Download,
                label = state.stream.downloadState.title,
                selected = state.stream.downloadState == ChimahonAnimeDownloadUiState.Downloading,
                onClick = callbacks.onDownload,
            )
        } else {
            ChimahonAnimePlayerMiniButton(
                icon = Icons.Outlined.DownloadForOffline,
                label = "Downloaded",
                selected = state.stream.playbackSource == ChimahonAnimePlayerPlaybackSource.Download,
                onClick = callbacks.onToggleDownloadPlayback,
            )
        }
        if (!sourceUrl.isNullOrBlank()) {
            ChimahonAnimePlayerMiniButton(
                icon = Icons.Outlined.OpenInBrowser,
                label = "Open",
                onClick = callbacks.onOpenSource,
            )
            ChimahonAnimePlayerMiniButton(
                icon = Icons.Outlined.ContentCopy,
                label = "Copy",
                onClick = callbacks.onCopySource,
            )
            ChimahonAnimePlayerMiniButton(
                icon = Icons.Outlined.IosShare,
                label = "Share",
                onClick = callbacks.onShareSource,
            )
        }
    }
}

@Composable
fun ChimahonAnimePlayerSelectionTray(
    state: ChimahonAnimePlayerUiState,
    callbacks: ChimahonAnimePlayerCallbacks,
    modifier: Modifier = Modifier,
) {
    val hasOptions = state.hosters.isNotEmpty() ||
        state.qualities.isNotEmpty() ||
        state.subtitleTracks.isNotEmpty() ||
        state.audioTracks.isNotEmpty() ||
        state.skipSegments.isNotEmpty()
    if (!hasOptions) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        state.activeSkipSegment?.let { segment ->
            ChimahonAnimePlayerMiniButton(
                icon = ChimahonAnimePlayerActionId.SeekForward.icon,
                label = segment.actionLabel,
                selected = true,
                onClick = { callbacks.onSkipSegment(segment) },
            )
        }
        state.hosters.forEach { hoster ->
            ChimahonAnimePlayerMiniButton(
                icon = Icons.Outlined.Public,
                label = hoster.title,
                valueLabel = hoster.statusLabel,
                selected = hoster.id == state.selectedHosterId,
                onClick = { callbacks.onSelectHoster(hoster) },
            )
        }
        state.qualities.forEach { quality ->
            ChimahonAnimePlayerMiniButton(
                icon = ChimahonAnimePlayerActionId.Quality.icon,
                label = quality.title,
                valueLabel = quality.statusLabel ?: quality.resolution?.let { "${it}p" },
                selected = quality.id == state.selectedQualityId,
                onClick = { callbacks.onSelectQuality(quality) },
            )
        }
        state.subtitleTracks.forEach { track ->
            ChimahonAnimePlayerMiniButton(
                icon = ChimahonAnimePlayerActionId.Subtitles.icon,
                label = track.displayChipLabel("Sub"),
                valueLabel = formatSignedAnimeSeconds(track.delaySeconds),
                selected = track.id in state.selectedSubtitleTrackIds,
                onClick = { callbacks.onSelectSubtitleTrack(track) },
            )
        }
        state.audioTracks.forEach { track ->
            ChimahonAnimePlayerMiniButton(
                icon = Icons.Outlined.VolumeUp,
                label = track.displayChipLabel("Audio"),
                valueLabel = formatSignedAnimeSeconds(track.delaySeconds),
                selected = track.id == state.selectedAudioTrackId,
                onClick = { callbacks.onSelectAudioTrack(track) },
            )
        }
        listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f).forEach { speed ->
            ChimahonAnimePlayerMiniButton(
                icon = ChimahonAnimePlayerActionId.Speed.icon,
                label = "${speed}x",
                selected = state.playbackSpeed == speed,
                onClick = { callbacks.onPlaybackSpeedChange(speed) },
            )
        }
    }
}

@Composable
fun ChimahonAnimePlayerMiniButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    valueLabel: String? = null,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.08f),
    ) {
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 5.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = if (selected) 1f else 0.78f),
                modifier = Modifier.size(15.dp),
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = valueLabel?.let { "$label $it" } ?: label,
                color = Color.White.copy(alpha = if (selected) 1f else 0.78f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun ChimahonAnimeSubtitleOverlay(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.padding(horizontal = 28.dp),
        color = Color.Black.copy(alpha = 0.64f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChimahonAnimePlayerError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.68f), RoundedCornerShape(12.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Video failed",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 12.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        ChimahonAnimePlayerMiniButton(
            icon = Icons.Outlined.Refresh,
            label = "Retry",
            onClick = onRetry,
        )
    }
}

@Composable
private fun ChimahonAnimePlayerLoading(
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.54f), RoundedCornerShape(12.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CircularProgressIndicator(color = Color.White)
        Text(text = text, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

private fun ChimahonAnimePlayerUiState.resolvedSourceUrl(): String? {
    return stream.sourceUrl
        ?: sourceUrl
        ?: selectedQualityModel?.sourceUrl
        ?: streamUrl
        ?: stream.streamUrl
        ?: selectedQualityModel?.streamUrl
}

private fun ChimahonAnimePlayerTrackUiModel.displayChipLabel(prefix: String): String {
    return listOf(prefix, language, title)
        .filterNot { it.isNullOrBlank() }
        .joinToString(" ")
}

private fun formatSignedAnimeSeconds(seconds: Double): String? {
    if (seconds == 0.0) return null
    val prefix = if (seconds > 0.0) "+" else ""
    return "$prefix${seconds}s"
}

@Composable
private fun ChimahonAnimePlayerPlaceholder(
    state: ChimahonAnimePlayerUiState,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(74.dp),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colors.primary.copy(alpha = 0.26f),
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(18.dp),
                )
            }
            Text(
                modifier = Modifier.padding(top = 14.dp),
                text = state.episodeTitle.ifBlank { "No episode loaded" },
                color = Color.White.copy(alpha = 0.78f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
