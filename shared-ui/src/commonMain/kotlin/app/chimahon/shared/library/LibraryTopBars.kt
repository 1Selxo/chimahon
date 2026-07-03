package app.chimahon.shared.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChimahonLibraryToolbarAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

@Composable
fun ChimahonLibraryTopAppBar(
    title: String,
    subtitle: String?,
    searchQuery: String,
    searchActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onFilterClick: () -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsClick: (() -> Unit)? = null,
    overflowActions: List<ChimahonLibraryToolbarAction> = emptyList(),
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 4.dp,
        title = {
            if (searchActive) {
                ChimahonLibrarySearchField(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onClose = onSearchToggle,
                )
            } else {
                ChimahonLibraryTopAppBarTitle(title = title, subtitle = subtitle)
            }
        },
        actions = {
            if (!searchActive) {
                ChimahonLibraryIconAction(
                    label = "Search library",
                    icon = Icons.Outlined.Search,
                    onClick = onSearchToggle,
                )
            }
            ChimahonLibraryIconAction(
                label = "Filter and sort",
                icon = Icons.Outlined.FilterList,
                onClick = onFilterClick,
            )
            ChimahonLibraryIconAction(
                label = "Refresh library",
                icon = Icons.Outlined.Refresh,
                onClick = onRefreshClick,
            )
            onSettingsClick?.let {
                ChimahonLibraryIconAction(
                    label = "Library settings",
                    icon = Icons.Outlined.Settings,
                    onClick = it,
                )
            }
            overflowActions.forEach { action ->
                ChimahonLibraryIconAction(
                    label = action.label,
                    icon = action.icon,
                    onClick = action.onClick,
                    enabled = action.enabled,
                )
            }
        },
    )
}

@Composable
fun ChimahonLibrarySelectionTopAppBar(
    selectedCount: Int,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier,
    onSelectAll: (() -> Unit)? = null,
    onMarkRead: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    extraActions: List<ChimahonLibraryToolbarAction> = emptyList(),
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(Icons.Outlined.Close, contentDescription = "Clear selection")
            }
        },
        title = {
            Text(
                text = selectedCount.toString(),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            onSelectAll?.let {
                ChimahonLibraryIconAction("Select all", Icons.Outlined.DoneAll, it)
            }
            onMarkRead?.let {
                ChimahonLibraryIconAction("Mark read", Icons.Outlined.CheckCircle, it)
            }
            onDelete?.let {
                ChimahonLibraryIconAction("Remove from library", Icons.Outlined.DeleteOutline, it)
            }
            extraActions.forEach { action ->
                ChimahonLibraryIconAction(
                    label = action.label,
                    icon = action.icon,
                    onClick = action.onClick,
                    enabled = action.enabled,
                )
            }
        },
    )
}

@Composable
private fun ChimahonLibraryTopAppBarTitle(
    title: String,
    subtitle: String?,
) {
    Column {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonLibrarySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            placeholder = { Text("Search library") },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = onClose) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close search")
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
        )
    }
}

@Composable
internal fun ChimahonLibraryIconAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    IconButton(onClick = onClick, enabled = enabled) {
        Icon(icon, contentDescription = label)
    }
}

@Composable
fun ChimahonLibrarySmallActionRow(
    actions: List<ChimahonLibraryToolbarAction>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        actions.forEach { action ->
            ChimahonLibraryIconAction(
                label = action.label,
                icon = action.icon,
                onClick = action.onClick,
                enabled = action.enabled,
            )
        }
        if (actions.isEmpty()) {
            ChimahonLibraryIconAction(
                label = "More",
                icon = Icons.Outlined.MoreHoriz,
                onClick = {},
                enabled = false,
            )
        }
    }
}
