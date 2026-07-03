package app.chimahon.shared.timelineui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimelineList(
    groups: List<TimelineDateGroup>,
    onUpdateClick: (TimelineUpdateRow) -> Unit,
    onHistoryClick: (TimelineHistoryRow) -> Unit,
    modifier: Modifier = Modifier,
    density: TimelineRowDensity = TimelineRowDensity.Comfortable,
    colors: TimelineColors = TimelineDefaults.colors(),
    contentPadding: PaddingValues = PaddingValues(bottom = 16.dp),
    coverContent: @Composable BoxScope.(TimelineRow, String?) -> Unit = { row, _ ->
        TimelineCoverPlaceholder(title = row.mangaTitle, colors = colors)
    },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        groups.forEach { group ->
            stickyHeader {
                TimelineDateHeader(
                    title = group.title,
                    detail = group.detail,
                    colors = colors,
                )
            }
            items(group.rows, key = { it.stableKey }) { row ->
                when (row) {
                    is TimelineUpdateRow -> TimelineUpdateListRow(
                        row = row,
                        onClick = { onUpdateClick(row) },
                        density = density,
                        colors = colors,
                        coverContent = { thumbnail -> coverContent(row, thumbnail) },
                    )
                    is TimelineHistoryRow -> TimelineHistoryListRow(
                        row = row,
                        onClick = { onHistoryClick(row) },
                        density = density,
                        colors = colors,
                        coverContent = { thumbnail -> coverContent(row, thumbnail) },
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineScreen(
    state: TimelineUiState,
    onSearchQueryChange: (String) -> Unit,
    onFilterChipClick: (TimelineFilterChip) -> Unit,
    onSortChipClick: (TimelineFilterChip) -> Unit,
    onUpdateClick: (TimelineUpdateRow) -> Unit,
    onHistoryClick: (TimelineHistoryRow) -> Unit,
    modifier: Modifier = Modifier,
    colors: TimelineColors = TimelineDefaults.colors(),
    coverContent: @Composable BoxScope.(TimelineRow, String?) -> Unit = { row, _ ->
        TimelineCoverPlaceholder(title = row.mangaTitle, colors = colors)
    },
) {
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        TimelineFilterBar(
            state = state.filterBar,
            onSearchQueryChange = onSearchQueryChange,
            onFilterChipClick = onFilterChipClick,
            onSortChipClick = onSortChipClick,
            colors = colors,
        )
        when {
            state.loading -> TimelineLoadingState(
                title = "Loading ${state.kind.title.lowercase()}",
                colors = colors,
            )
            state.errorMessage != null -> TimelineErrorState(
                message = state.errorMessage,
                colors = colors,
            )
            state.groups.isEmpty() -> TimelineEmptyState(
                title = state.emptyTitle,
                detail = state.emptyDetail,
                icon = when (state.kind) {
                    TimelineSurfaceKind.Updates -> TimelineQuickActionIcon.Updates
                    TimelineSurfaceKind.History -> TimelineQuickActionIcon.History
                },
                colors = colors,
            )
            else -> TimelineList(
                groups = state.groups,
                onUpdateClick = onUpdateClick,
                onHistoryClick = onHistoryClick,
                density = state.density,
                colors = colors,
                contentPadding = PaddingValues(bottom = if (state.selectionActive) 128.dp else 16.dp),
                coverContent = coverContent,
            )
        }
    }
}

fun timelineUpdatesFilterChips(selected: TimelineUpdatesFilter): List<TimelineFilterChip> {
    return TimelineUpdatesFilter.entries.map {
        TimelineFilterChip(
            id = it.name,
            label = it.title,
            selected = it == selected,
        )
    }
}

fun timelineUpdatesSortChips(selected: TimelineUpdatesSort): List<TimelineFilterChip> {
    return TimelineUpdatesSort.entries.map {
        TimelineFilterChip(
            id = it.name,
            label = it.title,
            selected = it == selected,
        )
    }
}

fun timelineHistoryFilterChips(selected: TimelineHistoryFilter): List<TimelineFilterChip> {
    return TimelineHistoryFilter.entries.map {
        TimelineFilterChip(
            id = it.name,
            label = it.title,
            selected = it == selected,
        )
    }
}

fun timelineHistorySortChips(selected: TimelineHistorySort): List<TimelineFilterChip> {
    return TimelineHistorySort.entries.map {
        TimelineFilterChip(
            id = it.name,
            label = it.title,
            selected = it == selected,
        )
    }
}
