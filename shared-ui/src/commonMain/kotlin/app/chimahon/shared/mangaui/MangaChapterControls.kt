package app.chimahon.shared.mangaui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonMangaChapterControls(
    chapterCount: Int,
    totalChapterCount: Int,
    missingChapterCount: Int,
    sortState: ChimahonMangaChapterSortState,
    filterState: ChimahonMangaChapterFilterState,
    onSortChange: (ChimahonMangaChapterSortState) -> Unit,
    onFilterClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    displayMode: ChimahonMangaChapterDisplayMode = ChimahonMangaChapterDisplayMode.SourceTitle,
    selectionState: ChimahonMangaChapterSelectionState = ChimahonMangaChapterSelectionState(
        selectedCount = 0,
        totalCount = chapterCount,
    ),
    bulkActions: List<ChimahonMangaChapterBulkActionModel> = defaultMangaChapterBulkActions(selectionState),
    onDisplayModeChange: (ChimahonMangaChapterDisplayMode) -> Unit = {},
    onReadFilterChange: (ChimahonMangaReadFilter) -> Unit = {},
    onDownloadFilterChange: (ChimahonMangaDownloadFilter) -> Unit = {},
    onBookmarkFilterChange: (ChimahonMangaBookmarkFilter) -> Unit = {},
    onClearFilters: () -> Unit = {},
    onBulkActionClick: (ChimahonMangaChapterBulkAction) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
    ) {
        ChimahonMangaChapterHeader(
            chapterCount = chapterCount,
            totalChapterCount = totalChapterCount,
            missingChapterCount = missingChapterCount,
            onClick = onFilterClick,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonChapterSortChip(
                sortState = sortState,
                onSortChange = onSortChange,
                modifier = Modifier.weight(1f),
            )
            ChimahonChapterDirectionChip(
                sortState = sortState,
                onClick = { onSortChange(sortState.toggledDirection()) },
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Refresh chapters",
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonChapterDisplayChip(
                displayMode = displayMode,
                onDisplayModeChange = onDisplayModeChange,
                modifier = Modifier.weight(1f),
            )
            ChimahonChapterFilterMenuChip(
                filterState = filterState,
                onFilterClick = onFilterClick,
                onReadFilterChange = onReadFilterChange,
                onDownloadFilterChange = onDownloadFilterChange,
                onBookmarkFilterChange = onBookmarkFilterChange,
                onClearFilters = onClearFilters,
                modifier = Modifier.weight(1f),
            )
        }
        ChimahonChapterActiveFilterRow(filterState = filterState)
        ChimahonChapterSelectionActionRow(
            actions = bulkActions,
            onActionClick = onBulkActionClick,
        )
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
    }
}

@Composable
fun ChimahonMangaChapterHeader(
    chapterCount: Int,
    totalChapterCount: Int,
    missingChapterCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = if (chapterCount == totalChapterCount) {
                    "$chapterCount chapters"
                } else {
                    "$chapterCount of $totalChapterCount chapters"
                },
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (missingChapterCount > 0) {
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = "$missingChapterCount missing chapter gaps",
                    color = MaterialTheme.colors.error.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ChimahonChapterSortChip(
    sortState: ChimahonMangaChapterSortState,
    onSortChange: (ChimahonMangaChapterSortState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    BoxWithMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        anchor = {
            ChimahonChapterControlChip(
                text = sortState.sort.title,
                icon = Icons.Outlined.Sort,
                onClick = { expanded = true },
                modifier = modifier,
            )
        },
    ) {
        ChimahonMangaChapterSort.entries.forEach { sort ->
            DropdownMenuItem(
                onClick = {
                    onSortChange(sortState.withSort(sort))
                    expanded = false
                },
            ) {
                Text(sort.title)
            }
        }
    }
}

@Composable
private fun ChimahonChapterDirectionChip(
    sortState: ChimahonMangaChapterSortState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonChapterControlChip(
        text = sortState.directionTitle,
        icon = if (sortState.descending) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward,
        supportingText = if (sortState.sort == ChimahonMangaChapterSort.SourceOrder) {
            sortState.sourceOrderHint
        } else {
            null
        },
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun ChimahonChapterDisplayChip(
    displayMode: ChimahonMangaChapterDisplayMode,
    onDisplayModeChange: (ChimahonMangaChapterDisplayMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    BoxWithMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        anchor = {
            ChimahonChapterControlChip(
                text = displayMode.title,
                icon = Icons.Outlined.FormatListNumbered,
                onClick = { expanded = true },
                modifier = modifier,
            )
        },
    ) {
        ChimahonMangaChapterDisplayMode.entries.forEach { mode ->
            DropdownMenuItem(
                onClick = {
                    onDisplayModeChange(mode)
                    expanded = false
                },
            ) {
                Text(mode.title)
            }
        }
    }
}

@Composable
private fun ChimahonChapterFilterMenuChip(
    filterState: ChimahonMangaChapterFilterState,
    onFilterClick: () -> Unit,
    onReadFilterChange: (ChimahonMangaReadFilter) -> Unit,
    onDownloadFilterChange: (ChimahonMangaDownloadFilter) -> Unit,
    onBookmarkFilterChange: (ChimahonMangaBookmarkFilter) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    BoxWithMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        anchor = {
            ChimahonChapterControlChip(
                text = filterState.label,
                icon = Icons.Outlined.FilterList,
                selected = filterState.hasActiveFilters,
                onClick = {
                    onFilterClick()
                    expanded = true
                },
                modifier = modifier,
            )
        },
    ) {
        ChimahonChapterMenuSectionTitle("Read")
        ChimahonMangaReadFilter.entries.forEach { filter ->
            DropdownMenuItem(
                onClick = {
                    onReadFilterChange(filter)
                    expanded = false
                },
            ) {
                Text(filter.title)
            }
        }
        Divider()
        ChimahonChapterMenuSectionTitle("Download")
        ChimahonMangaDownloadFilter.entries.forEach { filter ->
            DropdownMenuItem(
                onClick = {
                    onDownloadFilterChange(filter)
                    expanded = false
                },
            ) {
                Text(filter.title)
            }
        }
        Divider()
        ChimahonChapterMenuSectionTitle("Bookmark")
        ChimahonMangaBookmarkFilter.entries.forEach { filter ->
            DropdownMenuItem(
                onClick = {
                    onBookmarkFilterChange(filter)
                    expanded = false
                },
            ) {
                Text(filter.title)
            }
        }
        if (filterState.hasActiveFilters) {
            Divider()
            DropdownMenuItem(
                onClick = {
                    onClearFilters()
                    expanded = false
                },
            ) {
                Text("Clear filters")
            }
        }
    }
}

@Composable
private fun ChimahonChapterActiveFilterRow(filterState: ChimahonMangaChapterFilterState) {
    if (!filterState.hasActiveFilters) return

    val labels = filterState.activeLabels()
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
    ) {
        items(labels, key = { label -> "chapter-filter:$label" }) { label ->
            ChimahonChapterFilterChip(text = label)
        }
    }
}

@Composable
private fun ChimahonChapterSelectionActionRow(
    actions: List<ChimahonMangaChapterBulkActionModel>,
    onActionClick: (ChimahonMangaChapterBulkAction) -> Unit,
) {
    if (actions.isEmpty()) return

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
    ) {
        items(actions, key = { action -> "chapter-bulk:${action.action}" }) { action ->
            ChimahonChapterBulkActionChip(
                action = action,
                onClick = { onActionClick(action.action) },
            )
        }
    }
}

@Composable
private fun ChimahonChapterControlChip(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    selected: Boolean = false,
) {
    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = 0.06f)
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colors.primary,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (supportingText != null) {
                    Text(
                        text = supportingText,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChimahonChapterBulkActionChip(
    action: ChimahonMangaChapterBulkActionModel,
    onClick: () -> Unit,
) {
    val tint = when {
        !action.enabled -> MaterialTheme.colors.onSurface.copy(alpha = 0.34f)
        action.selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.72f)
    }
    Surface(
        modifier = Modifier.clickable(enabled = action.enabled, onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (action.selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = 0.06f)
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = action.action.icon,
                contentDescription = action.label,
                modifier = Modifier.size(16.dp),
                tint = tint,
            )
            Text(
                text = action.label,
                color = tint,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonChapterFilterChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colors.primary.copy(alpha = 0.10f),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            text = text,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChimahonChapterMenuSectionTitle(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        text = text,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
        style = MaterialTheme.typography.caption,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun BoxWithMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    anchor: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.Box {
        anchor()
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
        ) {
            content()
        }
    }
}

private val ChimahonMangaChapterBulkAction.icon: ImageVector
    get() = when (this) {
        ChimahonMangaChapterBulkAction.Download -> Icons.Outlined.CloudDownload
        ChimahonMangaChapterBulkAction.MarkRead -> Icons.Outlined.CheckCircle
        ChimahonMangaChapterBulkAction.MarkUnread -> Icons.Outlined.CheckCircle
        ChimahonMangaChapterBulkAction.Bookmark -> Icons.Outlined.BookmarkBorder
        ChimahonMangaChapterBulkAction.RemoveBookmark -> Icons.Outlined.BookmarkBorder
        ChimahonMangaChapterBulkAction.SelectAll -> Icons.Outlined.CheckCircle
        ChimahonMangaChapterBulkAction.ClearSelection -> Icons.Outlined.Close
    }
