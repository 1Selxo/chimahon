package app.chimahon.shared.animequeueui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonAnimeAutoNextPlaylistSheet(
    state: ChimahonAnimeAutoNextPlaylistUiState,
    actions: ChimahonAnimeAutoNextPlaylistActions,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonAnimeQueueCoverPlaceholder() },
) {
    ChimahonAnimeQueueSheetSurface(modifier = modifier) {
        LazyColumn {
            item {
                ChimahonAnimeQueueSheetHeader(
                    title = state.title,
                    subtitle = state.subtitle ?: state.nextEpisode?.episodeTitle,
                    onClose = actions.onDismiss,
                    trailing = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Auto",
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
                                fontSize = 12.sp,
                            )
                            Switch(
                                checked = state.autoPlayEnabled,
                                onCheckedChange = actions.onToggleAutoPlay,
                            )
                        }
                    },
                )
            }
            state.countdownSeconds?.let { seconds ->
                item {
                    ChimahonAnimeAutoNextCountdownRow(
                        seconds = seconds,
                        nextEpisode = state.nextEpisode,
                        onPlayNow = {
                            state.nextEpisode?.let(actions.onPlayNow)
                        },
                    )
                }
            }
            when {
                state.loading -> item {
                    ChimahonAnimeQueueSheetMessage(
                        title = "Loading playlist",
                        subtitle = "Preparing upcoming episodes",
                        loading = true,
                    )
                }
                state.errorMessage != null -> item {
                    ChimahonAnimeQueueSheetMessage(
                        title = "Playlist failed",
                        subtitle = state.errorMessage,
                        actionLabel = "Retry",
                        onActionClick = actions.onRetry,
                        error = true,
                    )
                }
                state.episodes.isEmpty() -> item {
                    ChimahonAnimeQueueSheetMessage(
                        title = "No episodes queued",
                        subtitle = "Upcoming episodes will appear here when auto-next is available.",
                    )
                }
                else -> items(state.episodes, key = { it.id }) { episode ->
                    ChimahonAnimePlaylistEpisodeRow(
                        episode = episode,
                        actions = actions,
                        coverContent = coverContent,
                    )
                }
            }
        }
    }
}

@Composable
fun ChimahonAnimePlaylistEpisodeRow(
    episode: ChimahonAnimePlaylistEpisodeUiModel,
    actions: ChimahonAnimeAutoNextPlaylistActions,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonAnimeQueueCoverPlaceholder() },
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { actions.onEpisodeClick(episode) },
        color = if (episode.selected || episode.current) {
            MaterialTheme.colors.primary.copy(alpha = 0.10f)
        } else {
            MaterialTheme.colors.surface
        },
        contentColor = MaterialTheme.colors.onSurface,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChimahonAnimeQueueCover(
                    thumbnailUrl = episode.thumbnailUrl,
                    width = 50.dp,
                    coverContent = coverContent,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = episode.animeTitle,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = episode.episodeTitle,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.80f),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val metadataLabel = episode.metadataLabel
                    if (!metadataLabel.isNullOrBlank()) {
                        Text(
                            text = metadataLabel,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                ChimahonAnimeQueueStatusChip(chip = episode.status.toStatusChip())
            }
            ChimahonAnimeQueueBadgeRail(badges = episode.badges())
            episode.progress?.let { progress ->
                if (progress.shouldShow) {
                    ChimahonAnimeQueueProgressPanel(
                        modifier = Modifier.fillMaxWidth(),
                        progress = progress,
                        showBar = true,
                    )
                }
            }
            episode.error?.let { error ->
                ChimahonAnimeQueueInlineError(
                    error = error,
                    onRetry = { actions.onRetryEpisode(episode) },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when {
                    episode.canRetry -> TextButton(onClick = { actions.onRetryEpisode(episode) }) {
                        Icon(
                            imageVector = ChimahonAnimeQueueActionIcon.Retry.imageVector,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(episode.error?.retryLabel ?: "Retry")
                    }
                    episode.canResume -> TextButton(onClick = { actions.onResumeEpisode(episode) }) {
                        Icon(
                            imageVector = ChimahonAnimeQueueActionIcon.Resume.imageVector,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text("Resume")
                    }
                    episode.canPause -> TextButton(onClick = { actions.onPauseEpisode(episode) }) {
                        Icon(
                            imageVector = ChimahonAnimeQueueActionIcon.Pause.imageVector,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text("Pause")
                    }
                    else -> TextButton(
                        enabled = episode.canPlay,
                        onClick = { actions.onPlayNow(episode) },
                    ) {
                        Icon(
                            imageVector = ChimahonAnimeQueueActionIcon.Play.imageVector,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text("Play")
                    }
                }
                if (episode.canDownload) {
                    TextButton(onClick = { actions.onDownloadEpisode(episode) }) {
                        Icon(
                            imageVector = ChimahonAnimeQueueActionIcon.Download.imageVector,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text("Download")
                    }
                }
                if (episode.reorder.canMoveToTop) {
                    ChimahonAnimeQueueIconButton(
                        icon = ChimahonAnimeQueueActionIcon.MoveTop,
                        contentDescription = "Move to top",
                        onClick = { actions.onMoveToTop(episode) },
                    )
                }
                if (episode.reorder.canMoveToBottom) {
                    ChimahonAnimeQueueIconButton(
                        icon = ChimahonAnimeQueueActionIcon.MoveBottom,
                        contentDescription = "Move to bottom",
                        onClick = { actions.onMoveToBottom(episode) },
                    )
                }
                if (episode.canCancel) {
                    ChimahonAnimeQueueIconButton(
                        icon = ChimahonAnimeQueueActionIcon.Cancel,
                        contentDescription = "Cancel episode",
                        destructive = true,
                        onClick = { actions.onCancelEpisode(episode) },
                    )
                }
                ChimahonAnimeQueueIconButton(
                    icon = ChimahonAnimeQueueActionIcon.Remove,
                    contentDescription = "Remove from playlist",
                    onClick = { actions.onRemoveEpisode(episode) },
                )
            }
        }
    }
}

@Composable
fun ChimahonAnimeAutoNextCountdownRow(
    seconds: Int,
    nextEpisode: ChimahonAnimePlaylistEpisodeUiModel?,
    onPlayNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colors.primary.copy(alpha = 0.10f),
        contentColor = MaterialTheme.colors.primary,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ChimahonAnimeQueueActionIcon.Playlist.imageVector,
                contentDescription = null,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Next in ${seconds.coerceAtLeast(0)}s",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                if (nextEpisode != null) {
                    Text(
                        text = nextEpisode.episodeTitle,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            TextButton(onClick = onPlayNow) {
                Text("Play now")
            }
        }
    }
}

@Composable
fun ChimahonAnimeQueueSheetSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 12.dp,
    ) {
        content()
    }
}

@Composable
fun ChimahonAnimeQueueSheetHeader(
    title: String,
    subtitle: String?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 68.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colors.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        trailing?.invoke()
        IconButton(onClick = onClose) {
            Icon(
                imageVector = ChimahonAnimeQueueActionIcon.ClearSelection.imageVector,
                contentDescription = "Close",
            )
        }
    }
}

@Composable
fun ChimahonAnimeQueueSheetMessage(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    error: Boolean = false,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        when {
            loading -> CircularProgressIndicator()
            error -> Icon(
                imageVector = ChimahonAnimeQueueActionIcon.Error.imageVector,
                contentDescription = null,
                tint = MaterialTheme.colors.error,
            )
            else -> Icon(
                imageVector = ChimahonAnimeQueueActionIcon.Playlist.imageVector,
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
            )
        }
        Text(
            text = title,
            color = MaterialTheme.colors.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = subtitle,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        if (actionLabel != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(actionLabel)
            }
        }
    }
}
