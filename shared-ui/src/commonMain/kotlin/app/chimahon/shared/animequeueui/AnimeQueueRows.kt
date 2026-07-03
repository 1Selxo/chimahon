package app.chimahon.shared.animequeueui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonAnimeQueueList(
    rows: List<ChimahonAnimeQueueRowUiModel>,
    actions: ChimahonAnimeQueueRowActions,
    modifier: Modifier = Modifier,
    selectionMode: Boolean = rows.any { it.selected },
    showPriorityControl: Boolean = true,
    showQueueControls: Boolean = true,
    showReorderControls: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonAnimeQueueCoverPlaceholder() },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        items(rows, key = { it.id }) { row ->
            ChimahonAnimeQueueRow(
                row = row,
                actions = actions,
                selectionMode = selectionMode,
                showPriorityControl = showPriorityControl,
                showQueueControls = showQueueControls,
                showReorderControls = showReorderControls,
                coverContent = coverContent,
            )
        }
    }
}

@Composable
fun ChimahonAnimeQueueRow(
    row: ChimahonAnimeQueueRowUiModel,
    actions: ChimahonAnimeQueueRowActions,
    modifier: Modifier = Modifier,
    selectionMode: Boolean = row.selected,
    showPriorityControl: Boolean = true,
    showQueueControls: Boolean = true,
    showReorderControls: Boolean = true,
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonAnimeQueueCoverPlaceholder() },
) {
    ChimahonAnimeQueueRowSurface(
        modifier = modifier,
        selected = row.selected,
        onClick = { actions.onOpenRow(row) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectionMode) {
                    ChimahonAnimeQueueEpisodeCheckbox(
                        checked = row.selected,
                        onCheckedChange = { actions.onSelectionChange(row, it) },
                    )
                }
                ChimahonAnimeQueueCover(
                    thumbnailUrl = row.thumbnailUrl,
                    coverContent = coverContent,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = row.animeTitle,
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        ChimahonAnimeQueueStatusChip(chip = row.status.toStatusChip())
                    }
                    Text(
                        text = row.episodeTitle,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.80f),
                        fontSize = 14.sp,
                        fontWeight = if (row.seen) FontWeight.Normal else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val metadataLabel = row.metadataLabel
                    if (!metadataLabel.isNullOrBlank()) {
                        Text(
                            text = metadataLabel,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                ChimahonAnimeQueueOverflowMenu(actions = row.overflowActions)
            }
            ChimahonAnimeQueueBadgeRail(badges = row.badges())
            if (row.progress.shouldShow || row.status == ChimahonAnimeQueueStatus.Downloading) {
                ChimahonAnimeQueueProgressPanel(
                    modifier = Modifier.fillMaxWidth(),
                    progress = row.progress,
                    status = row.status.toStatusChip(),
                )
            }
            val transferLabel = row.transferLabel
            if (!transferLabel.isNullOrBlank()) {
                Text(
                    text = transferLabel,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            row.error?.let { error ->
                ChimahonAnimeQueueInlineError(
                    error = error,
                    onRetry = { actions.onRetryEpisode(row) },
                )
            }
            ChimahonAnimeQueueRowActionStrip(
                row = row,
                actions = actions,
                showPriorityControl = showPriorityControl,
                showQueueControls = showQueueControls,
                showReorderControls = showReorderControls,
            )
        }
    }
}

@Composable
fun ChimahonAnimeQueueRowActionStrip(
    row: ChimahonAnimeQueueRowUiModel,
    actions: ChimahonAnimeQueueRowActions,
    modifier: Modifier = Modifier,
    showPriorityControl: Boolean = true,
    showQueueControls: Boolean = true,
    showReorderControls: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChimahonAnimeQueuePrimaryAction(row = row, actions = actions)
        if (showQueueControls) {
            ChimahonAnimeQueueSecondaryActionRail(row = row, actions = actions)
        }
        if (showPriorityControl) {
            ChimahonAnimeQueuePrioritySelector(
                priority = row.priority,
                onPriorityChange = { actions.onPriorityChange(row, it) },
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (showReorderControls && row.reorder.canMove) {
            ChimahonAnimeQueueReorderControls(
                reorder = row.reorder,
                onMoveToTop = { actions.onMoveToTop(row) },
                onMoveUp = { actions.onMoveUp(row) },
                onMoveDown = { actions.onMoveDown(row) },
                onMoveToBottom = { actions.onMoveToBottom(row) },
            )
        }
    }
}

@Composable
fun ChimahonAnimeQueuePrimaryAction(
    row: ChimahonAnimeQueueRowUiModel,
    actions: ChimahonAnimeQueueRowActions,
    modifier: Modifier = Modifier,
) {
    when {
        row.canRetry && (row.status == ChimahonAnimeQueueStatus.Failed || row.status == ChimahonAnimeQueueStatus.Canceled) -> {
            TextButton(
                modifier = modifier,
                enabled = row.error?.canRetry != false,
                onClick = { actions.onRetryEpisode(row) },
            ) {
                Icon(
                    imageVector = ChimahonAnimeQueueActionIcon.Retry.imageVector,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(row.error?.retryLabel ?: "Retry")
            }
        }
        row.canResume -> {
            TextButton(
                modifier = modifier,
                onClick = { actions.onResumeEpisode(row) },
            ) {
                Icon(
                    imageVector = ChimahonAnimeQueueActionIcon.Resume.imageVector,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text("Resume")
            }
        }
        row.canPause && (row.status == ChimahonAnimeQueueStatus.Downloading || row.status == ChimahonAnimeQueueStatus.Playing) -> {
            TextButton(
                modifier = modifier,
                onClick = { actions.onPauseEpisode(row) },
            ) {
                Icon(
                    imageVector = ChimahonAnimeQueueActionIcon.Pause.imageVector,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text("Pause")
            }
        }
        row.canDownload -> {
            TextButton(
                modifier = modifier,
                onClick = { actions.onDownloadEpisode(row) },
            ) {
                Icon(
                    imageVector = ChimahonAnimeQueueActionIcon.Download.imageVector,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text("Download")
            }
        }
        row.canStream -> {
            TextButton(
                modifier = modifier,
                onClick = { actions.onStreamEpisode(row) },
            ) {
                Icon(
                    imageVector = ChimahonAnimeQueueActionIcon.Stream.imageVector,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text("Stream")
            }
        }
        row.canPlay -> {
            TextButton(
                modifier = modifier,
                onClick = { actions.onPlayEpisode(row) },
            ) {
                Icon(
                    imageVector = ChimahonAnimeQueueActionIcon.Play.imageVector,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text("Play")
            }
        }
        else -> {
            TextButton(
                modifier = modifier,
                enabled = false,
                onClick = {},
            ) {
                Icon(
                    imageVector = ChimahonAnimeQueueActionIcon.Error.imageVector,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text("Unavailable")
            }
        }
    }
}

@Composable
fun ChimahonAnimeQueueSecondaryActionRail(
    row: ChimahonAnimeQueueRowUiModel,
    actions: ChimahonAnimeQueueRowActions,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (row.canPause && row.status != ChimahonAnimeQueueStatus.Downloading && row.status != ChimahonAnimeQueueStatus.Playing) {
            ChimahonAnimeQueueIconButton(
                icon = ChimahonAnimeQueueActionIcon.Pause,
                contentDescription = "Pause episode",
                onClick = { actions.onPauseEpisode(row) },
            )
        }
        if (row.canRetry && row.status != ChimahonAnimeQueueStatus.Failed && row.status != ChimahonAnimeQueueStatus.Canceled) {
            ChimahonAnimeQueueIconButton(
                icon = ChimahonAnimeQueueActionIcon.Retry,
                contentDescription = "Retry episode",
                onClick = { actions.onRetryEpisode(row) },
            )
        }
        if (row.canCancel) {
            ChimahonAnimeQueueIconButton(
                icon = ChimahonAnimeQueueActionIcon.Cancel,
                contentDescription = "Cancel episode",
                destructive = true,
                onClick = { actions.onCancelEpisode(row) },
            )
        }
    }
}

@Composable
fun ChimahonAnimeQueuePrioritySelector(
    priority: ChimahonAnimeQueuePriority,
    onPriorityChange: (ChimahonAnimeQueuePriority) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    TextButton(
        modifier = modifier,
        enabled = enabled,
        onClick = { expanded = true },
    ) {
        Icon(
            imageVector = ChimahonAnimeQueueActionIcon.Priority.imageVector,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp),
        )
        Text(priority.label)
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        ChimahonAnimeQueuePriority.entries.forEach { item ->
            DropdownMenuItem(
                enabled = enabled,
                onClick = {
                    expanded = false
                    onPriorityChange(item)
                },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = item.label,
                        fontWeight = if (item == priority) FontWeight.SemiBold else FontWeight.Normal,
                    )
                    if (item != ChimahonAnimeQueuePriority.Normal) {
                        ChimahonAnimeQueueBadge(badge = item.toBadge())
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonAnimeQueueReorderControls(
    reorder: ChimahonAnimeQueueReorderState,
    onMoveToTop: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onMoveToBottom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!reorder.positionLabel.isNullOrBlank()) {
            Text(
                text = reorder.positionLabel,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
            )
        }
        ChimahonAnimeQueueIconButton(
            icon = ChimahonAnimeQueueActionIcon.MoveTop,
            contentDescription = "Move to top",
            enabled = reorder.canMoveToTop,
            onClick = onMoveToTop,
        )
        ChimahonAnimeQueueIconButton(
            icon = ChimahonAnimeQueueActionIcon.MoveUp,
            contentDescription = "Move up",
            enabled = reorder.canMoveUp,
            onClick = onMoveUp,
        )
        ChimahonAnimeQueueIconButton(
            icon = ChimahonAnimeQueueActionIcon.MoveDown,
            contentDescription = "Move down",
            enabled = reorder.canMoveDown,
            onClick = onMoveDown,
        )
        ChimahonAnimeQueueIconButton(
            icon = ChimahonAnimeQueueActionIcon.MoveBottom,
            contentDescription = "Move to bottom",
            enabled = reorder.canMoveToBottom,
            onClick = onMoveToBottom,
        )
    }
}

@Composable
fun ChimahonAnimeQueueDivider(
    modifier: Modifier = Modifier,
) {
    Divider(
        modifier = modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
    )
}
