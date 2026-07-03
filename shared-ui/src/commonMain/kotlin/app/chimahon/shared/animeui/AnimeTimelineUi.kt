package app.chimahon.shared.animeui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonAnimeUpdatesScreen(
    state: ChimahonAnimeUpdatesUiState,
    onRowClick: (ChimahonAnimeUpdateRowUiModel) -> Unit,
    onEpisodeClick: (ChimahonAnimeEpisodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
    onRowLongClick: (ChimahonAnimeUpdateRowUiModel) -> Unit = {},
    onSearch: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onFilter: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(bottom = 24.dp),
    coverContent: @Composable BoxScope.(ChimahonAnimeUpdateRowUiModel) -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = it.animeTitle)
    },
) {
    Column(modifier = modifier.fillMaxSize()) {
        ChimahonAnimeTimelineHeader(
            title = state.title,
            subtitle = state.subtitle ?: state.lastUpdatedLabel ?: "${state.rows.size} update(s)",
            selectedCount = state.selectedCount,
            refreshing = state.refreshing,
            onSearch = onSearch,
            onFilter = onFilter,
            onRefresh = onRefresh,
        )
        when {
            state.loading && state.rows.isEmpty() -> {
                ChimahonAnimeLoadingState("Loading anime updates", modifier = Modifier.weight(1f))
            }
            state.errorMessage != null && state.rows.isEmpty() -> {
                ChimahonAnimeEmptyState(
                    title = "Anime updates failed",
                    subtitle = state.errorMessage,
                    actionLabel = "Retry",
                    onActionClick = onRefresh,
                    modifier = Modifier.weight(1f),
                )
            }
            state.rows.isEmpty() -> {
                ChimahonAnimeEmptyState(
                    title = "No anime updates",
                    subtitle = "Updated episodes will appear here.",
                    modifier = Modifier.weight(1f),
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = contentPadding,
                ) {
                    items(state.rows, key = { it.id }) { row ->
                        ChimahonAnimeUpdateRow(
                            row = row,
                            selected = row.id in state.selectedRowIds || row.selected,
                            onClick = { onRowClick(row) },
                            onLongClick = { onRowLongClick(row) },
                            onEpisodeClick = onEpisodeClick,
                            coverContent = { coverContent(row) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonAnimeHistoryScreen(
    state: ChimahonAnimeHistoryUiState,
    onRowClick: (ChimahonAnimeHistoryRowUiModel) -> Unit,
    modifier: Modifier = Modifier,
    onRowLongClick: (ChimahonAnimeHistoryRowUiModel) -> Unit = {},
    onSearch: () -> Unit = {},
    onClearHistory: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(bottom = 24.dp),
    coverContent: @Composable BoxScope.(ChimahonAnimeHistoryRowUiModel) -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = it.animeTitle)
    },
) {
    Column(modifier = modifier.fillMaxSize()) {
        ChimahonAnimeTimelineHeader(
            title = state.title,
            subtitle = state.subtitle ?: "${state.rows.size} watched episode(s)",
            selectedCount = state.selectedCount,
            refreshing = false,
            onSearch = onSearch,
            onFilter = {},
            onRefresh = onClearHistory,
            refreshIcon = Icons.Outlined.DeleteOutline,
        )
        when {
            state.loading && state.rows.isEmpty() -> {
                ChimahonAnimeLoadingState("Loading anime history", modifier = Modifier.weight(1f))
            }
            state.errorMessage != null && state.rows.isEmpty() -> {
                ChimahonAnimeEmptyState(
                    title = "Anime history failed",
                    subtitle = state.errorMessage,
                    modifier = Modifier.weight(1f),
                )
            }
            state.rows.isEmpty() -> {
                ChimahonAnimeEmptyState(
                    title = "No anime history",
                    subtitle = "Episodes you watch will show up here.",
                    modifier = Modifier.weight(1f),
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = contentPadding,
                ) {
                    items(state.rows, key = { it.id }) { row ->
                        ChimahonAnimeHistoryRow(
                            row = row,
                            selected = row.id in state.selectedRowIds || row.selected,
                            onClick = { onRowClick(row) },
                            onLongClick = { onRowLongClick(row) },
                            coverContent = { coverContent(row) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonAnimeTimelineHeader(
    title: String,
    subtitle: String,
    selectedCount: Int,
    refreshing: Boolean,
    onSearch: () -> Unit,
    onFilter: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshIcon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Outlined.Refresh,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        elevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (selectedCount > 0) "$selectedCount selected" else title,
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (refreshing) "Refreshing" else subtitle,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            ChimahonAnimeIconButton(Icons.Outlined.Search, "Search", onSearch)
            ChimahonAnimeIconButton(Icons.Outlined.FilterList, "Filter", onFilter)
            ChimahonAnimeIconButton(refreshIcon, "Refresh", onRefresh)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonAnimeUpdateRow(
    row: ChimahonAnimeUpdateRowUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onEpisodeClick: (ChimahonAnimeEpisodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = row.animeTitle)
    },
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        color = if (selected) MaterialTheme.colors.primary.copy(alpha = 0.10f) else MaterialTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ChimahonAnimeCoverFrame(
                title = row.animeTitle,
                modifier = Modifier
                    .width(58.dp)
                    .aspectRatio(ChimahonAnimeCoverRatio.Poster.ratio),
                content = coverContent,
            )
            Column(modifier = Modifier.weight(1f)) {
                row.updateHeader?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.overline.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colors.primary,
                    )
                }
                Text(
                    text = row.animeTitle,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = listOfNotNull(row.subtitle, row.sourceName, row.episodeCountLabel).joinToString(" - "),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                row.episodes.take(3).forEach { episode ->
                    ChimahonAnimeTimelineEpisodePill(
                        episode = episode,
                        onClick = { onEpisodeClick(episode) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonAnimeHistoryRow(
    row: ChimahonAnimeHistoryRowUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = row.animeTitle)
    },
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        color = if (selected) MaterialTheme.colors.primary.copy(alpha = 0.10f) else MaterialTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonAnimeCoverFrame(
                title = row.animeTitle,
                modifier = Modifier
                    .width(54.dp)
                    .aspectRatio(ChimahonAnimeCoverRatio.Poster.ratio),
                content = coverContent,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.episodeTitle,
                    style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = row.animeTitle,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.70f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = listOfNotNull(row.watchedAtLabel, row.progressLabel, row.sourceName).joinToString(" - "),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.54f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonAnimeTimelineEpisodePill(
    episode: ChimahonAnimeEpisodeUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.padding(top = 6.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.07f),
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(onClick = onClick, onLongClick = onClick)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.width(16.dp),
            )
            Text(
                text = episode.displayTitle(ChimahonAnimeEpisodeDisplayMode.SourceTitle),
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
