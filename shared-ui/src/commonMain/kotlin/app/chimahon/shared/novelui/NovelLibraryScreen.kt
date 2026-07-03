package app.chimahon.shared.novelui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonNovelLibraryScreen(
    state: ChimahonNovelLibraryUiState,
    actions: ChimahonNovelLibraryActions,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 24.dp),
    coverContent: @Composable BoxScope.(ChimahonNovelBookUiModel) -> Unit = {
        ChimahonNovelCoverPlaceholder(
            title = it.title,
            modifier = Modifier.fillMaxSize(),
        )
    },
) {
    Column(modifier = modifier.fillMaxSize()) {
        ChimahonNovelLibraryToolbar(
            state = state,
            actions = actions,
        )
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.10f))
        if (state.showCategoryTabs && state.visibleCategories.isNotEmpty()) {
            ChimahonNovelCategoryTabs(
                categories = state.visibleCategories,
                selectedCategoryId = state.activeCategory?.id,
                onCategorySelected = actions.onCategorySelected,
            )
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
        }

        Box(modifier = Modifier.weight(1f)) {
            when (state.contentState) {
                ChimahonNovelContentState.Loading -> {
                    ChimahonNovelLoadingState(modifier = Modifier.fillMaxSize())
                }
                ChimahonNovelContentState.Empty -> {
                    ChimahonNovelEmptyLibraryState(
                        onImportClick = actions.onImportEpub,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                ChimahonNovelContentState.FilteredEmpty -> {
                    ChimahonNovelFilteredEmptyState(modifier = Modifier.fillMaxSize())
                }
                ChimahonNovelContentState.Error -> {
                    ChimahonNovelErrorState(
                        message = state.errorMessage ?: "Unknown error",
                        onRetry = actions.onRefresh,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                ChimahonNovelContentState.Content -> {
                    if (state.displayMode == ChimahonNovelLibraryDisplayMode.List) {
                        ChimahonNovelLibraryList(
                            entries = state.visibleBooks,
                            selectedBookIds = state.selectedBookIds,
                            showLanguageBadges = state.showLanguageBadges,
                            showGhostBadges = state.showGhostBadges,
                            showProgressBadges = state.showProgressBadges,
                            onBookClick = actions.onBookClick,
                            onBookLongClick = actions.onBookLongClick,
                            onContinueClick = actions.onBookClick,
                            contentPadding = contentPadding,
                            modifier = Modifier.fillMaxSize(),
                            coverContent = coverContent,
                        )
                    } else {
                        ChimahonNovelLibraryGrid(
                            entries = state.visibleBooks,
                            displayMode = state.displayMode,
                            selectedBookIds = state.selectedBookIds,
                            showLanguageBadges = state.showLanguageBadges,
                            showGhostBadges = state.showGhostBadges,
                            showProgressBadges = state.showProgressBadges,
                            onBookClick = actions.onBookClick,
                            onBookLongClick = actions.onBookLongClick,
                            onContinueClick = actions.onBookClick,
                            contentPadding = contentPadding,
                            modifier = Modifier.fillMaxSize(),
                            coverContent = coverContent,
                        )
                    }
                }
            }

            ChimahonNovelBottomActionBar(
                visible = state.selectionMode,
                selectedCount = state.selectedBookIds.size,
                actions = actions,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
fun ChimahonNovelLibraryToolbar(
    state: ChimahonNovelLibraryUiState,
    actions: ChimahonNovelLibraryActions,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        elevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            if (state.selectionMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "${state.selectedBookIds.size} selected",
                        color = MaterialTheme.colors.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                    )
                    TextButton(onClick = actions.onSelectAll) { Text("All") }
                    TextButton(onClick = actions.onInvertSelection) { Text("Invert") }
                    TextButton(onClick = actions.onClearSelection) { Text("Close") }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.toolbarTitle,
                            color = MaterialTheme.colors.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 22.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        val subtitle = buildList {
                            if (state.importing) add("Importing")
                            if (state.syncState.syncing) add(state.syncState.statusLabel)
                            state.defaultCategoryLabel?.let { add("Default: $it") }
                            state.storageLabel?.let { add(it) }
                            add("${state.books.size} book(s)")
                            add(state.displayMode.title)
                            add(state.sort.title)
                        }.joinToString(", ")
                        Text(
                            text = subtitle,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    TextButton(onClick = actions.onSortClick) { Text("Sort") }
                    TextButton(onClick = actions.onDisplaySettingsClick) { Text("Display") }
                    TextButton(onClick = actions.onRefresh) { Text("Refresh") }
                    Button(
                        onClick = actions.onImportEpub,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = MaterialTheme.colors.onPrimary,
                        ),
                    ) {
                        Text("Import")
                    }
                }
                if (state.syncState.shouldShow(actions)) {
                    ChimahonNovelLibrarySyncStrip(
                        syncState = state.syncState,
                        actions = actions,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = actions.onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Search novels") },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            TextButton(onClick = { actions.onSearchQueryChange("") }) {
                                Text("Clear")
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun ChimahonNovelLibrarySyncStrip(
    syncState: ChimahonNovelSyncUiState,
    actions: ChimahonNovelLibraryActions,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (syncState.syncing) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primary,
                    strokeWidth = 2.dp,
                )
            }
            Column(modifier = Modifier.width(190.dp)) {
                Text(
                    text = syncState.providerName,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = syncState.errorMessage ?: syncState.summaryLabel ?: syncState.statusLabel,
                    color = if (syncState.errorMessage != null) {
                        MaterialTheme.colors.error
                    } else {
                        MaterialTheme.colors.onSurface.copy(alpha = 0.62f)
                    },
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            actions.onSyncAuto?.let { action ->
                Button(
                    enabled = !syncState.syncing && syncState.enabled && syncState.connected,
                    onClick = action,
                    elevation = null,
                ) {
                    Text("Sync all")
                }
            }
            actions.onSyncImport?.let { action ->
                TextButton(
                    enabled = !syncState.syncing && syncState.enabled && syncState.connected,
                    onClick = action,
                ) {
                    Text("Import")
                }
            }
            actions.onSyncExport?.let { action ->
                TextButton(
                    enabled = !syncState.syncing && syncState.enabled && syncState.connected,
                    onClick = action,
                ) {
                    Text("Export")
                }
            }
        }
    }
}

private fun ChimahonNovelSyncUiState.shouldShow(actions: ChimahonNovelLibraryActions): Boolean {
    return enabled ||
        connected ||
        syncing ||
        errorMessage != null ||
        actions.onSyncAuto != null ||
        actions.onSyncImport != null ||
        actions.onSyncExport != null
}

@Composable
fun ChimahonNovelCategoryTabs(
    categories: List<ChimahonNovelCategoryUiModel>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { category ->
            val selected = category.id == selectedCategoryId
            ChimahonNovelCategoryChip(
                text = category.displayTitle,
                selected = selected,
                onClick = { onCategorySelected(category.id) },
            )
        }
    }
}

@Composable
fun ChimahonNovelCategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.14f)
        } else {
            Color.Transparent
        },
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colors.primary.copy(alpha = 0.35f)
            else MaterialTheme.colors.onSurface.copy(alpha = 0.14f),
        ),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
            text = text,
            color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp,
            maxLines = 1,
        )
    }
}

@Composable
fun ChimahonNovelSortDisplaySheet(
    state: ChimahonNovelLibraryUiState,
    onSortSelected: (ChimahonNovelLibrarySort, Boolean) -> Unit,
    onDisplayModeSelected: (ChimahonNovelLibraryDisplayMode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        elevation = 16.dp,
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Sort and display",
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                )
                TextButton(onClick = onDismiss) { Text("Close") }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Sort",
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
                ChimahonNovelLibrarySort.entries.forEach { sort ->
                    ChimahonNovelSheetRow(
                        title = sort.title,
                        subtitle = if (state.sort == sort) {
                            if (state.sortDescending) "Descending" else "Ascending"
                        } else {
                            null
                        },
                        selected = state.sort == sort,
                        onClick = {
                            val nextDescending = if (state.sort == sort) !state.sortDescending else state.sortDescending
                            onSortSelected(sort, nextDescending)
                        },
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Display",
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ChimahonNovelLibraryDisplayMode.entries.forEach { mode ->
                        ChimahonNovelCategoryChip(
                            text = mode.title,
                            selected = state.displayMode == mode,
                            onClick = { onDisplayModeSelected(mode) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChimahonNovelSheetRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colors.primary.copy(alpha = 0.10f) else Color.Transparent,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                color = MaterialTheme.colors.onSurface,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    fontSize = 12.sp,
                )
            }
        }
    }
}
