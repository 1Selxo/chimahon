package app.chimahon.shared.downloadhistoryui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonDownloadHistoryBatchActionBar(
    state: ChimahonDownloadHistoryBatchBarState,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    if (!state.visible) return
    Surface(
        color = colors.surface,
        contentColor = colors.content,
        elevation = 2.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = if (state.allSelected) onClearSelection else onSelectAll) {
                Icon(
                    imageVector = Icons.Outlined.SelectAll,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = if (state.allSelected) "Clear" else "All",
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.title,
                    color = colors.content,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${state.selectedCount} of ${state.totalCount}",
                    color = colors.secondaryContent,
                    fontSize = 11.sp,
                )
            }
            state.primaryActions.take(4).forEach { action ->
                ChimahonDownloadHistoryBatchActionButton(
                    action = action,
                    colors = colors,
                )
            }
            ChimahonDownloadHistoryActionButtons(
                actions = state.overflowActions,
                maxVisibleActions = 2,
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDownloadHistoryBatchActionButton(
    action: ChimahonDownloadHistoryBatchAction,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    TextButton(
        onClick = action.onClick,
        enabled = action.enabled,
        modifier = modifier,
    ) {
        Icon(
            imageVector = action.icon.imageVector,
            contentDescription = null,
            tint = when {
                !action.enabled -> colors.secondaryContent.copy(alpha = 0.36f)
                action.role == ChimahonDownloadHistoryActionRole.Destructive -> colors.error
                action.role == ChimahonDownloadHistoryActionRole.Primary -> colors.primary
                else -> colors.secondaryContent
            },
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = action.label,
            modifier = Modifier.padding(start = 4.dp),
            maxLines = 1,
        )
    }
}

@Composable
fun ChimahonDownloadHistoryFilterChips(
    filterState: ChimahonDownloadHistoryFilterState,
    onMediaKindClick: (ChimahonMediaKind) -> Unit,
    onChipClick: (ChimahonDownloadHistoryChip) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(ChimahonMediaKind.entries, key = { "media:${it.name}" }) { mediaKind ->
                ChimahonMediaKindFilterChip(
                    mediaKind = mediaKind,
                    selected = mediaKind in filterState.selectedMediaKinds,
                    onClick = { onMediaKindClick(mediaKind) },
                    colors = colors,
                )
            }
        }
        val chips = filterState.sourceChips + filterState.languageChips + filterState.statusChips
        if (chips.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(chips, key = { it.stableKey }) { chip ->
                    ChimahonDownloadHistoryChip(
                        chip = chip,
                        modifier = Modifier.clickable(enabled = chip.enabled) { onChipClick(chip) },
                        colors = colors,
                    )
                }
            }
        }
    }
}

@Composable
fun ChimahonDownloadHistorySheetChoiceRow(
    row: ChimahonDownloadHistorySheetRow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = row.enabled, onClick = onClick),
        color = if (row.selected) colors.primaryContainer else colors.surface,
        contentColor = colors.content,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.title,
                    color = if (row.enabled) colors.content else colors.secondaryContent.copy(alpha = 0.42f),
                    fontSize = 15.sp,
                    fontWeight = if (row.selected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!row.subtitle.isNullOrBlank()) {
                    Text(
                        text = row.subtitle,
                        color = colors.secondaryContent,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (row.selected) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Selected",
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun ChimahonMediaKindFilterChip(
    mediaKind: ChimahonMediaKind,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    Row(
        modifier = modifier
            .height(34.dp)
            .clip(CircleShape)
            .background(if (selected) colors.primaryContainer else colors.content.copy(alpha = 0.06f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = mediaKind.icon,
            contentDescription = null,
            tint = if (selected) colors.primary else colors.secondaryContent,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = mediaKind.title,
            color = if (selected) colors.primary else colors.content,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
fun ChimahonDownloadHistoryStateCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    loading: Boolean = false,
    error: Boolean = false,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (error) colors.error.copy(alpha = 0.12f) else colors.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (error) colors.error else colors.primary,
                    modifier = Modifier.size(30.dp),
                )
            }
            Text(
                text = title,
                color = colors.content,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = subtitle,
                color = colors.secondaryContent,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            if (loading) {
                LinearProgressIndicator(
                    color = colors.primary,
                    backgroundColor = colors.content.copy(alpha = 0.10f),
                    modifier = Modifier
                        .fillMaxWidth(0.36f)
                        .height(4.dp)
                        .clip(CircleShape),
                )
            }
            if (actionLabel != null && onAction != null) {
                Button(onClick = onAction) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Composable
fun ChimahonDownloadHistoryEmptyState(
    tab: ChimahonDownloadHistoryTab,
    modifier: Modifier = Modifier,
    onRefresh: (() -> Unit)? = null,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    val (title, subtitle, icon) = when (tab) {
        ChimahonDownloadHistoryTab.Updates -> Triple(
            "No recent updates",
            "New manga chapters, anime episodes, and novel sections will appear here.",
            Icons.Outlined.CheckCircle,
        )
        ChimahonDownloadHistoryTab.History -> Triple(
            "No history yet",
            "Read chapters, watched episodes, and novel progress will be listed here.",
            Icons.Outlined.History,
        )
        ChimahonDownloadHistoryTab.Downloads -> Triple(
            "No downloads queued",
            "Offline chapters, episodes, and novels will appear here with queue controls.",
            Icons.Outlined.Download,
        )
    }
    ChimahonDownloadHistoryStateCard(
        title = title,
        subtitle = subtitle,
        icon = icon,
        actionLabel = if (onRefresh != null) "Refresh" else null,
        onAction = onRefresh,
        colors = colors,
        modifier = modifier,
    )
}

@Composable
fun ChimahonDownloadHistoryLoadingState(
    tab: ChimahonDownloadHistoryTab,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    val title = when (tab) {
        ChimahonDownloadHistoryTab.Updates -> "Loading updates"
        ChimahonDownloadHistoryTab.History -> "Loading history"
        ChimahonDownloadHistoryTab.Downloads -> "Loading downloads"
    }
    ChimahonDownloadHistoryStateCard(
        title = title,
        subtitle = "Syncing manga, anime, and light novel state.",
        icon = Icons.Outlined.Refresh,
        loading = true,
        colors = colors,
        modifier = modifier,
    )
}

@Composable
fun ChimahonDownloadHistoryErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    ChimahonDownloadHistoryStateCard(
        title = "Could not load timeline",
        subtitle = message,
        icon = Icons.Outlined.ErrorOutline,
        actionLabel = if (onRetry != null) "Retry" else null,
        onAction = onRetry,
        error = true,
        colors = colors,
        modifier = modifier,
    )
}
