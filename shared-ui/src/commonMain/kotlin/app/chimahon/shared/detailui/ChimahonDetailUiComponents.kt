package app.chimahon.shared.detailui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonDetailHeader(
    manga: ChimahonDetailHeaderModel,
    summary: ChimahonDetailChapterSummary,
    actions: List<ChimahonDetailActionModel> = manga.defaultDetailActions(summary),
    modifier: Modifier = Modifier,
    onCoverClick: () -> Unit = {},
    onActionClick: (ChimahonDetailAction) -> Unit = {},
    onGenreClick: (String) -> Unit = {},
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonDetailCoverPlaceholder(title = manga.title)
    },
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.12f),
                        MaterialTheme.colors.background,
                    ),
                ),
            ),
    ) {
        ChimahonDetailCoverTitleActionRow(
            manga = manga,
            summary = summary,
            actions = actions,
            onCoverClick = onCoverClick,
            onActionClick = onActionClick,
            coverContent = coverContent,
        )
        ChimahonDetailInfoChips(
            chips = manga.detailChips(summary),
            modifier = Modifier.padding(top = 4.dp),
        )
        if (manga.genres.isNotEmpty()) {
            ChimahonDetailInfoChips(
                chips = manga.genreChips(),
                onChipClick = { chip -> onGenreClick(chip.label) },
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
fun ChimahonDetailCoverTitleActionRow(
    manga: ChimahonDetailHeaderModel,
    summary: ChimahonDetailChapterSummary,
    actions: List<ChimahonDetailActionModel>,
    onCoverClick: () -> Unit,
    onActionClick: (ChimahonDetailAction) -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonDetailCoverPlaceholder(title = manga.title)
    },
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val wide = maxWidth >= 560.dp
        if (wide) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChimahonDetailCover(
                    title = manga.title,
                    onClick = onCoverClick,
                    modifier = Modifier.width(150.dp),
                    content = coverContent,
                )
                ChimahonDetailTitleBlock(
                    manga = manga,
                    summary = summary,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ChimahonDetailCover(
                    title = manga.title,
                    onClick = onCoverClick,
                    modifier = Modifier.width(128.dp),
                    content = coverContent,
                )
                ChimahonDetailTitleBlock(
                    manga = manga,
                    summary = summary,
                    centered = true,
                    modifier = Modifier.padding(top = 14.dp),
                )
            }
        }
        ChimahonDetailActionRow(
            actions = actions,
            onActionClick = onActionClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun ChimahonDetailCover(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {
        ChimahonDetailCoverPlaceholder(title = title)
    },
) {
    Box(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            .clickable(onClick = onClick),
        content = content,
    )
}

@Composable
fun BoxScope.ChimahonDetailCoverPlaceholder(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .matchParentSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.28f),
                        MaterialTheme.colors.onSurface.copy(alpha = 0.14f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title.firstOrNull()?.uppercaseChar()?.toString() ?: "M",
            color = MaterialTheme.colors.onPrimary,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun ChimahonDetailActionRow(
    actions: List<ChimahonDetailActionModel>,
    onActionClick: (ChimahonDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.background.copy(alpha = 0.94f),
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions.forEach { action ->
                ChimahonDetailActionButton(
                    model = action,
                    onClick = { onActionClick(action.action) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun ChimahonDetailInfoChips(
    chips: List<ChimahonDetailChipModel>,
    modifier: Modifier = Modifier,
    onChipClick: (ChimahonDetailChipModel) -> Unit = {},
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(chips, key = { it.stableKey }) { chip ->
            ChimahonDetailChip(
                chip = chip,
                onClick = { onChipClick(chip) },
            )
        }
    }
}

@Composable
fun ChimahonDetailDescription(
    title: String = "Description",
    description: String?,
    genres: List<String> = emptyList(),
    notes: String = "",
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = false,
    onGenreClick: (String) -> Unit = {},
) {
    var expanded by remember(description) { mutableStateOf(defaultExpanded) }
    val text = description?.takeIf(String::isNotBlank) ?: "No description available."
    ChimahonDetailCard(modifier = modifier) {
        SectionTitle(title)
        SelectionContainer {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(top = 6.dp),
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.body2,
                lineHeight = 19.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 5,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (description?.isNotBlank() == true) {
            TextButton(
                onClick = { expanded = !expanded },
                contentPadding = PaddingValues(horizontal = 0.dp),
            ) {
                Text(if (expanded) "Show less" else "Show more")
            }
        }
        if (genres.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                genres.forEach { genre ->
                    ChimahonDetailChip(
                        chip = ChimahonDetailChipModel(genre, ChimahonDetailChipKind.Genre),
                        onClick = { onGenreClick(genre) },
                    )
                }
            }
        }
        if (notes.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            SectionTitle("Notes")
            Text(
                text = notes,
                modifier = Modifier.padding(top = 6.dp),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
                style = MaterialTheme.typography.caption,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
fun ChimahonDetailChapterSummaryControls(
    state: ChimahonDetailChapterControlsState,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit = {},
    onSortClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onDisplayModeClick: () -> Unit = {},
    onSortDirectionClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onSortChange: (ChimahonDetailChapterSortState) -> Unit = {},
    onReadFilterChange: (ChimahonDetailReadFilter) -> Unit = {},
    onDownloadFilterChange: (ChimahonDetailDownloadFilter) -> Unit = {},
    onBookmarkFilterChange: (ChimahonDetailBookmarkFilter) -> Unit = {},
    onClearFilters: () -> Unit = {},
    onDisplayModeChange: (ChimahonDetailChapterDisplayMode) -> Unit = {},
    onChapterBulkActionClick: (ChimahonDetailChapterBulkAction) -> Unit = {},
) {
    ChimahonDetailCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (state.selectionState.hasSelection) {
                        "${state.selectionState.selectedCount} selected"
                    } else {
                        "Chapters"
                    },
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.onSurface,
                )
                Text(
                    text = state.summary.summaryLabel(),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
                )
            }
            IconButton(
                onClick = {
                    onSortDirectionClick()
                    onSortChange(state.sortState.toggledDirection())
                },
            ) {
                Icon(
                    imageVector = if (state.sortState.descending) {
                        Icons.Outlined.ArrowDownward
                    } else {
                        Icons.Outlined.ArrowUpward
                    },
                    contentDescription = "Sort direction",
                    tint = MaterialTheme.colors.primary,
                )
            }
            IconButton(onClick = onDownloadClick, enabled = state.summary.hasChapters) {
                Icon(
                    imageVector = Icons.Outlined.CloudDownload,
                    contentDescription = "Download chapters",
                    tint = if (state.summary.hasChapters) {
                        MaterialTheme.colors.primary
                    } else {
                        MaterialTheme.colors.onSurface.copy(alpha = 0.32f)
                    },
                )
            }
        }
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = "Search chapters")
            },
            label = { Text("Search chapters") },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChimahonDetailSortControlChip(
                sortState = state.sortState,
                label = state.sortLabel,
                onSortClick = onSortClick,
                onSortChange = onSortChange,
                modifier = Modifier.weight(1f),
            )
            ChimahonDetailFilterControlChip(
                filterState = state.filterState,
                label = state.filterLabel,
                selected = state.filtersVisible || state.filterState.hasActiveFilters,
                onFilterClick = onFilterClick,
                onReadFilterChange = onReadFilterChange,
                onDownloadFilterChange = onDownloadFilterChange,
                onBookmarkFilterChange = onBookmarkFilterChange,
                onClearFilters = onClearFilters,
                modifier = Modifier.weight(1f),
            )
            ChimahonDetailDisplayControlChip(
                displayMode = state.displayMode,
                label = state.displayLabel,
                onDisplayModeClick = onDisplayModeClick,
                onDisplayModeChange = onDisplayModeChange,
                modifier = Modifier.weight(1f),
            )
        }
        ChimahonDetailActiveFilterRow(
            filterState = state.filterState,
            modifier = Modifier.padding(top = 10.dp),
        )
        ChimahonDetailChapterBulkActionRow(
            actions = state.bulkActions,
            onActionClick = onChapterBulkActionClick,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

@Composable
fun ChimahonDetailRelatedSourceActions(
    actions: List<ChimahonDetailRelatedActionModel>,
    modifier: Modifier = Modifier,
    onActionClick: (ChimahonDetailRelatedAction) -> Unit = {},
    onActionModelClick: (ChimahonDetailRelatedActionModel) -> Unit = { model -> onActionClick(model.action) },
) {
    if (actions.isEmpty()) return
    ChimahonDetailCard(modifier = modifier) {
        SectionTitle("Related")
        Column(
            modifier = Modifier.padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            actions.forEach { action ->
                ChimahonDetailRelatedActionRow(
                    model = action,
                    onClick = { onActionModelClick(action) },
                )
            }
        }
    }
}

@Composable
fun ChimahonDetailEmptyState(
    message: ChimahonDetailStateMessage = ChimahonDetailStateMessage(
        title = "Nothing here yet",
        body = "This manga has no details or chapters to show.",
    ),
    modifier: Modifier = Modifier,
    onPrimaryAction: () -> Unit = {},
) {
    ChimahonDetailStateSurface(
        icon = Icons.Outlined.Explore,
        message = message,
        modifier = modifier,
        onPrimaryAction = onPrimaryAction,
    )
}

@Composable
fun ChimahonDetailErrorState(
    message: ChimahonDetailStateMessage = ChimahonDetailStateMessage(
        title = "Unable to load details",
        body = "Check the source and try again.",
        primaryActionLabel = "Retry",
    ),
    modifier: Modifier = Modifier,
    onPrimaryAction: () -> Unit = {},
) {
    ChimahonDetailStateSurface(
        icon = Icons.Outlined.ErrorOutline,
        message = message,
        modifier = modifier,
        onPrimaryAction = onPrimaryAction,
    )
}

@Composable
fun ChimahonDetailCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colors.surface,
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f)),
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            content = { content() },
        )
    }
}

@Composable
private fun ChimahonDetailTitleBlock(
    manga: ChimahonDetailHeaderModel,
    summary: ChimahonDetailChapterSummary,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
) {
    val textAlign = if (centered) TextAlign.Center else TextAlign.Start
    Column(
        modifier = modifier,
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        Text(
            text = manga.title,
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
            textAlign = textAlign,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = manga.sourceName,
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.68f),
            style = MaterialTheme.typography.body2,
            textAlign = textAlign,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        manga.creatorLine?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 5.dp),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.68f),
                style = MaterialTheme.typography.caption,
                textAlign = textAlign,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = summary.summaryLabel(),
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.64f),
            style = MaterialTheme.typography.caption,
            textAlign = textAlign,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChimahonDetailActionButton(
    model: ChimahonDetailActionModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeColor = MaterialTheme.colors.primary
    val inactiveColor = MaterialTheme.colors.onSurface.copy(alpha = 0.60f)
    val color = if (model.selected) activeColor else inactiveColor
    Column(
        modifier = modifier
            .height(58.dp)
            .alpha(if (model.enabled) 1f else 0.42f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = model.enabled, onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(if (model.selected) activeColor.copy(alpha = 0.12f) else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = model.action.icon(),
                contentDescription = model.label,
                tint = color,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = model.label,
            modifier = Modifier.padding(top = 2.dp),
            color = color,
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChimahonDetailChip(
    chip: ChimahonDetailChipModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = chip.kind in setOf(
        ChimahonDetailChipKind.Status,
        ChimahonDetailChipKind.Library,
        ChimahonDetailChipKind.Genre,
        ChimahonDetailChipKind.Url,
        ChimahonDetailChipKind.Warning,
    )
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = CircleShape,
        color = if (selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.11f)
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = 0.07f)
        },
        contentColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
    ) {
        Text(
            text = chip.label,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChimahonDetailControlChip(
    text: String,
    icon: ImageVector,
    selected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (selected) {
                    MaterialTheme.colors.primary.copy(alpha = 0.12f)
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.06f)
                },
            )
            .border(
                1.dp,
                MaterialTheme.colors.onSurface.copy(alpha = 0.10f),
                RoundedCornerShape(8.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(16.dp),
            tint = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.66f),
        )
        Text(
            text = text,
            color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.78f),
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChimahonDetailSortControlChip(
    sortState: ChimahonDetailChapterSortState,
    label: String,
    onSortClick: () -> Unit,
    onSortChange: (ChimahonDetailChapterSortState) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    BoxWithMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        anchor = {
            ChimahonDetailControlChip(
                text = if (label == "Source order" && sortState.sort != ChimahonDetailChapterSort.SourceOrder) {
                    sortState.sort.title
                } else {
                    label.ifBlank { sortState.sort.title }
                },
                icon = Icons.Outlined.FormatListNumbered,
                onClick = {
                    onSortClick()
                    expanded = true
                },
                modifier = modifier,
            )
        },
    ) {
        ChimahonDetailChapterSort.entries.forEach { sort ->
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
private fun ChimahonDetailDisplayControlChip(
    displayMode: ChimahonDetailChapterDisplayMode,
    label: String,
    onDisplayModeClick: () -> Unit,
    onDisplayModeChange: (ChimahonDetailChapterDisplayMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    BoxWithMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        anchor = {
            ChimahonDetailControlChip(
                text = if (
                    label == "Source title" &&
                    displayMode != ChimahonDetailChapterDisplayMode.SourceTitle
                ) {
                    displayMode.title
                } else {
                    label.ifBlank { displayMode.title }
                },
                icon = Icons.Outlined.BookmarkBorder,
                onClick = {
                    onDisplayModeClick()
                    expanded = true
                },
                modifier = modifier,
            )
        },
    ) {
        ChimahonDetailChapterDisplayMode.entries.forEach { mode ->
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
private fun ChimahonDetailFilterControlChip(
    filterState: ChimahonDetailChapterFilterState,
    label: String,
    selected: Boolean,
    onFilterClick: () -> Unit,
    onReadFilterChange: (ChimahonDetailReadFilter) -> Unit,
    onDownloadFilterChange: (ChimahonDetailDownloadFilter) -> Unit,
    onBookmarkFilterChange: (ChimahonDetailBookmarkFilter) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    BoxWithMenu(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        anchor = {
            ChimahonDetailControlChip(
                text = if (filterState.hasActiveFilters) {
                    filterState.label
                } else {
                    label.ifBlank { filterState.label }
                },
                icon = Icons.Outlined.FilterList,
                selected = selected,
                onClick = {
                    onFilterClick()
                    expanded = true
                },
                modifier = modifier,
            )
        },
    ) {
        ChimahonDetailMenuSectionTitle("Read")
        ChimahonDetailReadFilter.entries.forEach { filter ->
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
        ChimahonDetailMenuSectionTitle("Download")
        ChimahonDetailDownloadFilter.entries.forEach { filter ->
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
        ChimahonDetailMenuSectionTitle("Bookmark")
        ChimahonDetailBookmarkFilter.entries.forEach { filter ->
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
private fun ChimahonDetailChapterBulkActionRow(
    actions: List<ChimahonDetailChapterBulkActionModel>,
    onActionClick: (ChimahonDetailChapterBulkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (actions.isEmpty()) return
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 4.dp),
    ) {
        items(actions, key = { action -> "detail-bulk:${action.action}" }) { action ->
            ChimahonDetailBulkActionChip(
                action = action,
                onClick = { onActionClick(action.action) },
            )
        }
    }
}

@Composable
private fun ChimahonDetailActiveFilterRow(
    filterState: ChimahonDetailChapterFilterState,
    modifier: Modifier = Modifier,
) {
    val labels = filterState.activeLabels()
    if (labels.isEmpty()) return
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 4.dp),
    ) {
        items(labels, key = { label -> "detail-filter:$label" }) { label ->
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colors.primary.copy(alpha = 0.10f),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    text = label,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ChimahonDetailBulkActionChip(
    action: ChimahonDetailChapterBulkActionModel,
    onClick: () -> Unit,
) {
    val tint = when {
        !action.enabled -> MaterialTheme.colors.onSurface.copy(alpha = 0.34f)
        action.selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.72f)
    }
    Row(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (action.selected) {
                    MaterialTheme.colors.primary.copy(alpha = 0.12f)
                } else {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.06f)
                },
            )
            .clickable(enabled = action.enabled, onClick = onClick)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = action.action.icon(),
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

@Composable
private fun ChimahonDetailMenuSectionTitle(text: String) {
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
    Box {
        anchor()
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
        ) {
            content()
        }
    }
}

@Composable
private fun ChimahonDetailRelatedActionRow(
    model: ChimahonDetailRelatedActionModel,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .alpha(if (model.enabled) 1f else 0.44f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = model.enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = model.action.icon(),
            contentDescription = model.label,
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.size(22.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = model.label,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val supportingText = model.supportingLabel?.takeIf(String::isNotBlank)
                ?: model.url?.takeIf(String::isNotBlank)
            supportingText?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun ChimahonDetailStateSurface(
    icon: ImageVector,
    message: ChimahonDetailStateMessage,
    modifier: Modifier = Modifier,
    onPrimaryAction: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = message.title,
            modifier = Modifier.size(42.dp),
            tint = MaterialTheme.colors.onBackground.copy(alpha = 0.56f),
        )
        Text(
            text = message.title,
            modifier = Modifier.padding(top = 14.dp),
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        message.body?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 6.dp),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.68f),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
        }
        message.primaryActionLabel?.let { label ->
            Spacer(Modifier.height(18.dp))
            Button(onClick = onPrimaryAction) {
                Text(label)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colors.onSurface,
        style = MaterialTheme.typography.subtitle2,
        fontWeight = FontWeight.SemiBold,
    )
}

private fun ChimahonDetailAction.icon(): ImageVector {
    return when (this) {
        ChimahonDetailAction.Favorite -> Icons.Filled.Favorite
        ChimahonDetailAction.Start -> Icons.Outlined.PlayArrow
        ChimahonDetailAction.Resume -> Icons.Outlined.PlayArrow
        ChimahonDetailAction.Tracking -> Icons.Outlined.Sync
        ChimahonDetailAction.Web -> Icons.Outlined.Public
        ChimahonDetailAction.Refresh -> Icons.Outlined.Refresh
        ChimahonDetailAction.Download -> Icons.Outlined.CloudDownload
        ChimahonDetailAction.MarkRead -> Icons.Outlined.CheckCircle
        ChimahonDetailAction.MarkUnread -> Icons.Outlined.CheckCircle
        ChimahonDetailAction.Bookmark -> Icons.Outlined.BookmarkBorder
        ChimahonDetailAction.Select -> Icons.Outlined.CheckCircle
        ChimahonDetailAction.AddToLibrary -> Icons.Outlined.LibraryAdd
    }
}

private fun ChimahonDetailRelatedAction.icon(): ImageVector {
    return when (this) {
        ChimahonDetailRelatedAction.BrowseSource -> Icons.Outlined.Explore
        ChimahonDetailRelatedAction.OpenInWebView -> Icons.Outlined.OpenInBrowser
        ChimahonDetailRelatedAction.OpenExternal -> Icons.Outlined.Link
        ChimahonDetailRelatedAction.CopyUrl -> Icons.Outlined.Link
        ChimahonDetailRelatedAction.Recommendations -> Icons.Outlined.Search
        ChimahonDetailRelatedAction.Merge -> Icons.Outlined.Link
        ChimahonDetailRelatedAction.Similar -> Icons.Outlined.FormatListNumbered
    }
}

private fun ChimahonDetailChapterBulkAction.icon(): ImageVector {
    return when (this) {
        ChimahonDetailChapterBulkAction.Download -> Icons.Outlined.CloudDownload
        ChimahonDetailChapterBulkAction.MarkRead -> Icons.Outlined.CheckCircle
        ChimahonDetailChapterBulkAction.MarkUnread -> Icons.Outlined.CheckCircle
        ChimahonDetailChapterBulkAction.Bookmark -> Icons.Outlined.BookmarkBorder
        ChimahonDetailChapterBulkAction.RemoveBookmark -> Icons.Outlined.BookmarkBorder
        ChimahonDetailChapterBulkAction.SelectAll -> Icons.Outlined.CheckCircle
        ChimahonDetailChapterBulkAction.ClearSelection -> Icons.Outlined.Close
    }
}

private fun ChimahonDetailChapterSummary.summaryLabel(): String {
    if (totalCount <= 0) return "No chapters"
    return buildList {
        add("$totalCount chapters")
        if (unreadCount > 0) add("$unreadCount unread")
        if (downloadedCount > 0) add("$downloadedCount downloaded")
        if (bookmarkedCount > 0) add("$bookmarkedCount bookmarked")
    }.joinToString(" / ")
}
