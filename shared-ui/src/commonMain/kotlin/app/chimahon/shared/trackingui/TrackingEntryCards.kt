package app.chimahon.shared.trackingui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun ChimahonTrackingEntrySection(
    state: ChimahonTrackingUiState,
    callbacks: ChimahonTrackingCallbacks,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.(ChimahonTrackingEntryUiModel) -> Unit = {
        ChimahonTrackingCoverPlaceholder(it)
    },
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ChimahonTrackingMediaSummarySection(
            summaries = state.mediaSummaries(),
            selectedMediaKind = state.selectedMediaKind,
            onMediaKindSelected = callbacks.onMediaKindSelected,
        )
        ChimahonTrackingEntryHeader(
            state = state,
            callbacks = callbacks,
        )
        when {
            state.loading -> ChimahonTrackingLoadingState(message = "Loading tracked entries...")
            state.errorMessage != null -> ChimahonTrackingErrorState(
                title = "Tracking failed",
                message = state.errorMessage,
                onRetry = callbacks.onRetry,
            )
            state.filteredEntries.isEmpty() -> ChimahonTrackingEmptyState(
                title = "No tracked entries",
                message = "Track manga, anime, or light novels to keep progress in sync.",
            )
            else -> state.filteredEntries.forEach { entry ->
                ChimahonTrackingEntryCard(
                    entry = entry,
                    callbacks = callbacks,
                    coverContent = { coverContent(entry) },
                )
            }
        }
    }
}

@Composable
fun ChimahonTrackingEntryHeader(
    state: ChimahonTrackingUiState,
    callbacks: ChimahonTrackingCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ChimahonTrackingSectionHeader(
            title = "Tracked library",
            subtitle = "${state.filteredEntries.size} entries",
        )
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChimahonTrackingFilterChip(
                text = "All",
                selected = state.selectedMediaKind == null,
                onClick = { callbacks.onMediaKindSelected(null) },
            )
            state.mediaKinds.forEach { kind ->
                ChimahonTrackingFilterChip(
                    text = kind.pluralLabel,
                    selected = state.selectedMediaKind == kind,
                    onClick = { callbacks.onMediaKindSelected(kind) },
                )
            }
        }
    }
}

@Composable
fun ChimahonTrackingEntryCard(
    entry: ChimahonTrackingEntryUiModel,
    callbacks: ChimahonTrackingCallbacks,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonTrackingCoverPlaceholder(entry)
    },
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { callbacks.onOpenTracker(entry) },
        color = MaterialTheme.colors.surface,
        elevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
                    content = coverContent,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = entry.mediaKind.icon(),
                            contentDescription = entry.mediaKind.label,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colors.primary,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = entry.trackerName,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = entry.remoteTitle,
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (entry.remoteTitle != entry.title) {
                        Text(
                            text = entry.title,
                            modifier = Modifier.padding(top = 2.dp),
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        ChimahonTrackingStatusChip(
                            text = entry.status.labelFor(entry.mediaKind),
                            tone = ChimahonTrackingTone.Info,
                        )
                        ChimahonTrackingStatusChip(
                            text = entry.progressLabel,
                            tone = ChimahonTrackingTone.Muted,
                        )
                        ChimahonTrackingStatusChip(
                            text = entry.scoreLabel,
                            tone = ChimahonTrackingTone.Muted,
                        )
                        ChimahonTrackingStatusChip(
                            text = entry.syncStatus.label,
                            tone = entry.syncStatus.statusTone(),
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    IconButton(onClick = { callbacks.onSyncEntry(entry) }) {
                        Icon(
                            imageVector = entry.syncStatus.icon(),
                            contentDescription = "Sync ${entry.remoteTitle}",
                            tint = MaterialTheme.colors.primary,
                        )
                    }
                    IconButton(onClick = { callbacks.onSearchTracker(entry) }) {
                        Icon(
                            imageVector = ChimahonTrackingIcon.Search.imageVector(),
                            contentDescription = "Search tracker",
                        )
                    }
                }
            }
            entry.errorMessage?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.error,
                )
            }
            ChimahonTrackingEditor(
                entry = entry,
                callbacks = callbacks,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
fun ChimahonTrackingEditor(
    entry: ChimahonTrackingEntryUiModel,
    callbacks: ChimahonTrackingCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonTrackingStatusPicker(
                entry = entry,
                onStatusChange = { callbacks.onStatusChange(entry, it) },
                modifier = Modifier.weight(1f),
            )
            OutlinedButton(onClick = { callbacks.onOpenTracker(entry) }) {
                Text("Open")
            }
            TextButton(onClick = { callbacks.onRemoveTracking(entry) }) {
                Text("Remove", color = MaterialTheme.colors.error)
            }
        }
        ChimahonTrackingProgressSlider(
            entry = entry,
            onProgressChange = { callbacks.onProgressChange(entry, it) },
        )
        ChimahonTrackingScoreSlider(
            entry = entry,
            onScoreChange = { callbacks.onScoreChange(entry, it) },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Private tracking",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface,
            )
            Switch(
                checked = entry.privateTracking,
                onCheckedChange = { callbacks.onTogglePrivateTracking(entry, it) },
            )
        }
        val dateLabels = listOfNotNull(
            entry.startedDateLabel?.let { "Started $it" },
            entry.completedDateLabel?.let { "Completed $it" },
            entry.lastSyncedLabel?.let { "Synced $it" },
            entry.nextSyncLabel?.let { "Next $it" },
        )
        if (dateLabels.isNotEmpty()) {
            Text(
                text = dateLabels.joinToString("  |  "),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
            )
        }
    }
}

@Composable
fun ChimahonTrackingStatusPicker(
    entry: ChimahonTrackingEntryUiModel,
    onStatusChange: (ChimahonTrackingStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = entry.status.labelFor(entry.mediaKind),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            entry.mediaKind.defaultStatusOptions().forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onStatusChange(option.status)
                    },
                ) {
                    Text(option.label)
                }
            }
        }
    }
}

@Composable
fun ChimahonTrackingProgressSlider(
    entry: ChimahonTrackingEntryUiModel,
    onProgressChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val max = (entry.totalProgress ?: (entry.progress + 25)).coerceAtLeast(1)
    val value = entry.progress.coerceIn(0, max).toFloat()
    Column(modifier = modifier.fillMaxWidth()) {
        ChimahonTrackingControlLabel(
            label = entry.mediaKind.progressUnit.replaceFirstChar { it.uppercase() },
            value = entry.progressLabel,
        )
        Slider(
            value = value,
            onValueChange = { onProgressChange(it.roundToInt()) },
            valueRange = 0f..max.toFloat(),
            steps = (max - 1).coerceIn(0, 100),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            OutlinedButton(
                enabled = entry.progress > 0,
                onClick = { onProgressChange((entry.progress - 1).coerceAtLeast(0)) },
            ) {
                Text("-1")
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(
                enabled = entry.progress < max,
                onClick = { onProgressChange((entry.progress + 1).coerceAtMost(max)) },
            ) {
                Text("+1")
            }
        }
    }
}

@Composable
fun ChimahonTrackingScoreSlider(
    entry: ChimahonTrackingEntryUiModel,
    onScoreChange: (Double?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val max = entry.safeScoreMax.toFloat()
    val value = entry.score?.coerceIn(0.0, entry.safeScoreMax)?.toFloat() ?: 0f
    val step = entry.scoreStep.coerceAtLeast(0.1)
    val stepCount = ((entry.safeScoreMax / step).roundToInt() - 1).coerceIn(0, 100)
    val currentScore = entry.score ?: 0.0
    Column(modifier = modifier.fillMaxWidth()) {
        ChimahonTrackingControlLabel(
            label = "Score",
            value = entry.scoreLabel,
        )
        Slider(
            value = value,
            onValueChange = {
                val rounded = (it / step.toFloat()).roundToInt() * step
                onScoreChange(rounded.coerceIn(0.0, entry.safeScoreMax))
            },
            valueRange = 0f..max,
            steps = stepCount,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                enabled = entry.score != null,
                onClick = { onScoreChange(null) },
            ) {
                Text("Clear")
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(
                enabled = currentScore > 0.0,
                onClick = { onScoreChange((currentScore - step).coerceAtLeast(0.0)) },
            ) {
                Text("-${step.trimmed()}")
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(
                enabled = currentScore < entry.safeScoreMax,
                onClick = { onScoreChange((currentScore + step).coerceAtMost(entry.safeScoreMax)) },
            ) {
                Text("+${step.trimmed()}")
            }
        }
    }
}

@Composable
private fun ChimahonTrackingControlLabel(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun BoxScope.ChimahonTrackingCoverPlaceholder(
    entry: ChimahonTrackingEntryUiModel,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .matchParentSize()
            .background(MaterialTheme.colors.primary.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = entry.mediaKind.icon(),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colors.primary,
        )
    }
}
