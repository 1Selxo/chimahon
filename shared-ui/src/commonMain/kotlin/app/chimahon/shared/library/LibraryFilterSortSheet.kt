package app.chimahon.shared.library

import app.chimahon.shared.ChimahonFilterMode
import app.chimahon.shared.ChimahonLibrarySort
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonLibraryFilterSortSheet(
    state: ChimahonLibraryFilterSortState,
    onStateChange: (ChimahonLibraryFilterSortState) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onReset: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        elevation = 8.dp,
    ) {
        Column {
            ChimahonLibrarySheetHeader(
                title = "Filter and sort",
                onDismiss = onDismiss,
                onReset = onReset,
            )
            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            LazyColumn(
                modifier = Modifier.heightIn(max = 680.dp),
                content = {
                    item(key = "display-title") {
                        ChimahonLibrarySheetSectionTitle("Display")
                    }
                    item(key = "display-mode") {
                        ChimahonLibraryDisplayModeToggle(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            selected = state.displayMode,
                            onSelected = { onStateChange(state.copy(displayMode = it)) },
                        )
                    }
                    item(key = "cover-ratio-title") {
                        ChimahonLibrarySheetSectionTitle("Cover ratio")
                    }
                    item(key = "cover-ratio") {
                        ChimahonLibraryCoverRatioToggle(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            selected = state.coverRatio,
                            onSelected = { onStateChange(state.copy(coverRatio = it)) },
                        )
                    }
                    item(key = "sort-title") {
                        ChimahonLibrarySheetSectionTitle("Sort")
                    }
                    items(ChimahonLibrarySort.entries, key = { it.librarySheetRow(state.sort == it).stableKey }) { sort ->
                        ChimahonLibrarySortSheetRow(
                            sort = sort,
                            selected = state.sort == sort,
                            onClick = { onStateChange(state.copy(sort = sort)) },
                        )
                    }
                    item(key = "sort-direction") {
                        ChimahonLibrarySortDirectionRow(
                            ascending = state.sortAscending,
                            onAscendingChange = { onStateChange(state.copy(sortAscending = it)) },
                        )
                    }
                    item(key = "filter-title") {
                        ChimahonLibrarySheetSectionTitle("Filter")
                    }
                    items(ChimahonLibraryFilterKind.entries, key = { it.librarySheetRow(state.filterFor(it)).stableKey }) { kind ->
                        ChimahonLibraryFilterSheetRow(
                            kind = kind,
                            value = state.filterFor(kind),
                            onValueChange = { onStateChange(state.withFilter(kind, it)) },
                        )
                    }
                    item(key = "badges-title") {
                        ChimahonLibrarySheetSectionTitle("Badges and labels")
                    }
                    item(key = "badge-switches") {
                        ChimahonLibraryDisplaySwitches(
                            state = state,
                            onStateChange = onStateChange,
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun ChimahonLibrarySheetHeader(
    title: String,
    onDismiss: () -> Unit,
    onReset: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        onReset?.let {
            TextButton(onClick = it) {
                Text("Reset")
            }
        }
        IconButton(onClick = onDismiss) {
            Icon(Icons.Outlined.Close, contentDescription = "Close")
        }
    }
}

@Composable
private fun ChimahonLibrarySheetSectionTitle(title: String) {
    Text(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
        text = title,
        color = MaterialTheme.colors.primary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun ChimahonLibrarySortSheetRow(
    sort: ChimahonLibrarySort,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.RadioButton, onClick = onClick),
        color = MaterialTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RadioButton(selected = selected, onClick = onClick)
            Text(
                text = sort.librarySortTitle(),
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun ChimahonLibraryFilterSheetRow(
    kind: ChimahonLibraryFilterKind,
    value: ChimahonFilterMode,
    onValueChange: (ChimahonFilterMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonLibraryTriStateFilterRow(
        title = kind.title,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
    )
}

@Composable
private fun ChimahonLibrarySortDirectionRow(
    ascending: Boolean,
    onAscendingChange: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = "Direction",
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
            fontSize = 13.sp,
        )
        ChimahonLibrarySegmentedRow(modifier = Modifier.padding(top = 8.dp)) {
            ChimahonLibraryTextSegmentButton(
                label = "Ascending",
                selected = ascending,
                onClick = { onAscendingChange(true) },
                modifier = Modifier.weight(1f),
            )
            ChimahonLibraryTextSegmentButton(
                label = "Descending",
                selected = !ascending,
                onClick = { onAscendingChange(false) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
fun ChimahonLibraryTriStateFilterRow(
    title: String,
    value: ChimahonFilterMode,
    onValueChange: (ChimahonFilterMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text(
            text = title,
            color = MaterialTheme.colors.onSurface,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        ChimahonLibrarySegmentedRow(modifier = Modifier.padding(top = 8.dp)) {
            ChimahonFilterMode.entries.forEach { mode ->
                ChimahonLibraryTextSegmentButton(
                    label = mode.libraryFilterTitle(),
                    selected = value == mode,
                    onClick = { onValueChange(mode) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
