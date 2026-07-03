package app.chimahon.shared.downloadsui

import app.chimahon.shared.ChimahonDownloadQueueEntry
import app.chimahon.shared.ChimahonDownloadState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonDownloadsScreen(
    state: ChimahonDownloadsUiState,
    actions: ChimahonDownloadQueueActions,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit = {},
    onFilterChange: (ChimahonDownloadQueueFilter) -> Unit = {},
    onGroupModeChange: (ChimahonDownloadQueueGroupMode) -> Unit = {},
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        ChimahonDownloadQueueToolbar(
            state = state,
            actions = actions,
            colors = colors,
        )
        DownloadSearchField(
            query = state.query,
            onQueryChange = onQueryChange,
            colors = colors,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        )
        ChimahonDownloadQueueFilterRow(
            selected = state.filter,
            selectedGroupMode = state.groupMode,
            onFilterChange = onFilterChange,
            onGroupModeChange = onGroupModeChange,
            colors = colors,
        )
        ChimahonDownloadQueueList(
            state = state,
            actions = actions,
            colors = colors,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun ChimahonDownloadQueueToolbar(
    state: ChimahonDownloadsUiState,
    actions: ChimahonDownloadQueueActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
) {
    val queue = state.queue
    val summary = state.summary
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (queue?.paused == true) ChimahonDownloadState.Paused.downloadBackgroundColor() else colors.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (queue?.paused == true) {
                        ChimahonDownloadUiIcon.Pause.imageVector
                    } else {
                        ChimahonDownloadUiIcon.Download.imageVector
                    },
                    contentDescription = "",
                    tint = if (queue?.paused == true) ChimahonDownloadState.Paused.downloadColor() else colors.primary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                DownloadLabel(
                    text = if (queue?.paused == true) "Download queue paused" else "Download queue",
                    color = colors.content,
                    size = 16,
                    weight = FontWeight.SemiBold,
                )
                DownloadLabel(
                    text = state.message ?: summary.downloadSummaryText(queue?.paused == true),
                    color = if (state.message?.startsWith("Could not") == true) colors.error else colors.secondaryContent,
                    size = 11,
                    lineHeight = 16,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
            DownloadIconButton(
                icon = ChimahonDownloadUiIcon.Refresh,
                contentDescription = "Refresh downloads",
                loading = state.loading,
                onClick = actions.onRefresh,
                colors = colors,
            )
            DownloadIconButton(
                icon = if (queue?.paused == true) ChimahonDownloadUiIcon.Resume else ChimahonDownloadUiIcon.Pause,
                contentDescription = if (queue?.paused == true) "Resume queue" else "Pause queue",
                active = queue?.paused == true,
                enabled = queue != null,
                onClick = { if (queue?.paused == true) actions.onResumeQueue() else actions.onPauseQueue() },
                colors = colors,
            )
        }
        if (summary.hasWork) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DownloadStatPill(
                    label = "Active",
                    value = summary.activeCount.toString(),
                    icon = ChimahonDownloadUiIcon.Download,
                    tint = ChimahonDownloadState.Downloading.downloadColor(),
                    modifier = Modifier.weight(1f),
                    colors = colors,
                )
                DownloadStatPill(
                    label = "Queued",
                    value = summary.queuedCount.toString(),
                    icon = ChimahonDownloadUiIcon.Queue,
                    modifier = Modifier.weight(1f),
                    colors = colors,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DownloadStatPill(
                    label = "Paused",
                    value = summary.pausedCount.toString(),
                    icon = ChimahonDownloadUiIcon.Pause,
                    tint = ChimahonDownloadState.Paused.downloadColor(),
                    modifier = Modifier.weight(1f),
                    colors = colors,
                )
                DownloadStatPill(
                    label = "Failed",
                    value = summary.failedCount.toString(),
                    icon = ChimahonDownloadUiIcon.Error,
                    tint = ChimahonDownloadState.Error.downloadColor(),
                    modifier = Modifier.weight(1f),
                    colors = colors,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                DownloadLabel(
                    text = "Downloaded ${summary.downloadedBytes.toReadableBytes()}",
                    color = colors.secondaryContent,
                    size = 11,
                    modifier = Modifier.weight(1f),
                )
                if (summary.queuedCount > 0 && queue?.paused == false) {
                    androidx.compose.material.TextButton(onClick = actions.onProcessNext) {
                        Text("Start next")
                    }
                }
                if (summary.hasRecoverableFailures) {
                    androidx.compose.material.TextButton(onClick = actions.onRetryFailed) {
                        Text("Retry failed")
                    }
                }
                if (summary.completedCount > 0) {
                    androidx.compose.material.TextButton(onClick = actions.onClearCompleted) {
                        Text("Clear done")
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonDownloadQueueFilterRow(
    selected: ChimahonDownloadQueueFilter,
    selectedGroupMode: ChimahonDownloadQueueGroupMode,
    onFilterChange: (ChimahonDownloadQueueFilter) -> Unit,
    onGroupModeChange: (ChimahonDownloadQueueGroupMode) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(ChimahonDownloadQueueFilter.entries, key = { it.name }) { filter ->
                DownloadStatusChip(
                    text = filter.title,
                    active = filter == selected,
                    error = filter == ChimahonDownloadQueueFilter.Failed && filter == selected,
                    modifier = Modifier.clickable { onFilterChange(filter) },
                    colors = colors,
                )
            }
        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(ChimahonDownloadQueueGroupMode.entries, key = { it.name }) { mode ->
                DownloadStatusChip(
                    text = "Group: ${mode.title}",
                    active = mode == selectedGroupMode,
                    modifier = Modifier.clickable { onGroupModeChange(mode) },
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonDownloadQueueList(
    state: ChimahonDownloadsUiState,
    actions: ChimahonDownloadQueueActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
    contentPadding: PaddingValues = PaddingValues(bottom = 16.dp),
) {
    val groups = state.groups()
    when {
        state.loading && state.queue == null -> {
            DownloadStatusCard(
                title = "Loading downloads",
                subtitle = "Reading saved queue and offline chapter state.",
                icon = ChimahonDownloadUiIcon.Download,
                loading = true,
                colors = colors,
                modifier = modifier,
            )
        }
        state.queue == null -> {
            DownloadStatusCard(
                title = "Download queue unavailable",
                subtitle = state.message ?: "Queue data has not been loaded yet.",
                icon = ChimahonDownloadUiIcon.Error,
                action = "Refresh",
                onAction = actions.onRefresh,
                error = true,
                colors = colors,
                modifier = modifier,
            )
        }
        state.entries.isEmpty() -> {
            DownloadStatusCard(
                title = "No downloads queued",
                subtitle = "New chapter downloads will appear here with pause, resume, reorder, and remove controls.",
                icon = ChimahonDownloadUiIcon.Download,
                action = "Refresh",
                onAction = actions.onRefresh,
                colors = colors,
                modifier = modifier,
            )
        }
        groups.isEmpty() -> {
            DownloadStatusCard(
                title = "No matching downloads",
                subtitle = "Clear search or filters to show queued chapters again.",
                icon = ChimahonDownloadUiIcon.Search,
                colors = colors,
                modifier = modifier,
            )
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = contentPadding,
            ) {
                groups.forEach { group ->
                    item(key = "header:${group.key}") {
                        DownloadSectionHeader(
                            title = group.title,
                            subtitle = group.subtitle,
                            colors = colors,
                        )
                    }
                    items(group.entries, key = { it.id }) { entry ->
                        val sourceName = state.sourceNames[entry.sourceId] ?: "Source ${entry.sourceId}"
                        ChimahonDownloadQueueRow(
                            entry = entry,
                            sourceName = sourceName,
                            position = state.entries.indexOfFirst { it.id == entry.id }.coerceAtLeast(0),
                            selected = entry.id in state.selectedEntryIds,
                            compact = state.displayMode == ChimahonDownloadQueueDisplayMode.Compact,
                            processing = state.processingEntryId == entry.id,
                            actions = actions,
                            colors = colors,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonDownloadQueueRow(
    entry: ChimahonDownloadQueueEntry,
    sourceName: String,
    position: Int,
    selected: Boolean,
    compact: Boolean,
    processing: Boolean,
    actions: ChimahonDownloadQueueActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
) {
    val progress = entry.progressFraction()
    val statusColor = entry.status.downloadColor()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(if (selected) colors.primaryContainer else colors.surface)
            .clickable { actions.onOpenEntry(entry) }
            .padding(horizontal = 16.dp, vertical = if (compact) 8.dp else 12.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = ChimahonDownloadUiIcon.Queue.imageVector,
                contentDescription = "Queue position",
                tint = colors.secondaryContent,
                modifier = Modifier.size(18.dp),
            )
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(if (compact) 40.dp else 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(entry.status.downloadBackgroundColor()),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = entry.status.downloadIcon().imageVector,
                    contentDescription = entry.status.downloadTitle(),
                    tint = statusColor,
                    modifier = Modifier.size(if (compact) 20.dp else 23.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DownloadLabel(
                        text = entry.mangaTitle,
                        color = colors.content,
                        size = if (compact) 13 else 14,
                        weight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    val label = entry.progressLabel()
                    if (label.isNotBlank()) {
                        DownloadLabel(
                            text = label,
                            color = colors.secondaryContent,
                            size = 11,
                            weight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
                DownloadLabel(
                    text = entry.chapterName,
                    color = colors.secondaryContent,
                    size = if (compact) 11 else 12,
                    modifier = Modifier.padding(top = 2.dp),
                )
                if (!compact) {
                    DownloadLabel(
                        text = entry.detailSubtitle(sourceName = sourceName, position = position),
                        color = if (entry.status == ChimahonDownloadState.Error) colors.error else colors.secondaryContent,
                        size = 11,
                        maxLines = 2,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                }
            }
            DownloadIconButton(
                icon = entry.primaryActionIcon(),
                contentDescription = entry.primaryActionTitle(),
                loading = processing,
                destructive = entry.status == ChimahonDownloadState.Downloaded,
                onClick = { entry.runPrimaryAction(actions) },
                colors = colors,
            )
        }
        DownloadProgressBar(
            fraction = progress,
            color = statusColor,
            colors = colors,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            DownloadStatusChip(
                text = entry.status.downloadTitle(),
                active = entry.status == ChimahonDownloadState.Downloading ||
                    entry.status == ChimahonDownloadState.Downloaded,
                error = entry.status == ChimahonDownloadState.Error,
                colors = colors,
            )
            Spacer(modifier = Modifier.width(8.dp))
            DownloadLabel(
                text = entry.sizeLabel(),
                color = colors.secondaryContent,
                size = 11,
                modifier = Modifier.weight(1f),
            )
            DownloadIconButton(
                icon = ChimahonDownloadUiIcon.MoveTop,
                contentDescription = "Move to top",
                enabled = entry.canMove(),
                onClick = { actions.onMoveEntryToTop(entry) },
                colors = colors,
            )
            DownloadIconButton(
                icon = ChimahonDownloadUiIcon.MoveBottom,
                contentDescription = "Move to bottom",
                enabled = entry.canMove(),
                onClick = { actions.onMoveEntryToBottom(entry) },
                colors = colors,
            )
            DownloadIconButton(
                icon = ChimahonDownloadUiIcon.Clear,
                contentDescription = "Remove download",
                destructive = true,
                onClick = { actions.onRemoveEntry(entry) },
                colors = colors,
            )
        }
    }
    DownloadDivider(colors = colors)
}

@Composable
fun ChimahonDownloadCleanupCard(
    summary: ChimahonDownloadQueueSummary,
    onClearCompleted: () -> Unit,
    onRetryFailed: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadUiColors = ChimahonDownloadUiDefaults.colors(),
) {
    DownloadCardSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        colors = colors,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            DownloadLabel(
                text = "Queue cleanup",
                color = colors.content,
                size = 15,
                weight = FontWeight.SemiBold,
            )
            DownloadLabel(
                text = "${summary.completedCount} complete - ${summary.failedCount} failed - ${summary.downloadedBytes.toReadableBytes()} downloaded",
                color = colors.secondaryContent,
                size = 11,
                lineHeight = 16,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                if (summary.failedCount > 0) {
                    androidx.compose.material.TextButton(onClick = onRetryFailed) {
                        Text("Retry failed")
                    }
                }
                if (summary.completedCount > 0) {
                    androidx.compose.material.TextButton(onClick = onClearCompleted) {
                        Text("Clear complete")
                    }
                }
            }
        }
    }
}

private fun ChimahonDownloadQueueSummary.downloadSummaryText(paused: Boolean): String {
    if (totalCount == 0) return "No queued chapters"
    return listOfNotNull(
        "$totalCount chapter(s)",
        activeCount.takeIf { it > 0 }?.let { "$it active" },
        queuedCount.takeIf { it > 0 }?.let { "$it queued" },
        pausedCount.takeIf { it > 0 || paused }?.let { "$it paused" },
        failedCount.takeIf { it > 0 }?.let { "$it failed" },
        completedCount.takeIf { it > 0 }?.let { "$it complete" },
    ).joinToString(" - ")
}

private fun ChimahonDownloadQueueEntry.primaryActionIcon(): ChimahonDownloadUiIcon {
    return when (status) {
        ChimahonDownloadState.Queued -> ChimahonDownloadUiIcon.Pause
        ChimahonDownloadState.Downloading -> ChimahonDownloadUiIcon.Pause
        ChimahonDownloadState.Paused -> ChimahonDownloadUiIcon.Resume
        ChimahonDownloadState.Downloaded -> ChimahonDownloadUiIcon.Clear
        ChimahonDownloadState.Error -> ChimahonDownloadUiIcon.Retry
    }
}

private fun ChimahonDownloadQueueEntry.runPrimaryAction(actions: ChimahonDownloadQueueActions) {
    when (status) {
        ChimahonDownloadState.Queued,
        ChimahonDownloadState.Downloading,
        -> actions.onPauseEntry(this)
        ChimahonDownloadState.Paused -> actions.onResumeEntry(this)
        ChimahonDownloadState.Downloaded -> actions.onRemoveEntry(this)
        ChimahonDownloadState.Error -> actions.onRetryEntry(this)
    }
}
