package app.chimahon.shared.novelopsui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.ImportExport
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NovelOperationsScreen(
    state: NovelOperationsState,
    actions: NovelOperationsActions,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "novel-ops-imports") {
            NovelImportQueueCard(state.imports, actions)
        }
        item(key = "novel-ops-discover") {
            NovelDiscoverCard(state.discover, actions)
        }
        item(key = "novel-ops-sync") {
            NovelTtuSyncCard(state.sync, actions)
        }
        item(key = "novel-ops-categories") {
            NovelCategoryManagerCard(state.categories, actions)
        }
    }
}

@Composable
fun NovelImportQueueCard(
    state: NovelImportQueueState,
    actions: NovelOperationsActions,
    modifier: Modifier = Modifier,
) {
    NovelOpsCard(modifier = modifier) {
        NovelOpsHeader(
            title = "Novel imports",
            subtitle = "${state.candidates.size} candidate(s), ${state.queue.size} queued",
            icon = Icons.Outlined.ImportExport,
            trailing = {
                TextButton(onClick = { actions.onOperation(NovelOperationAction.PickFolder) }) {
                    Text("Folder")
                }
                Button(onClick = { actions.onOperation(NovelOperationAction.PickEpub) }) {
                    Text("EPUB")
                }
            },
        )
        if (!state.hasWork) {
            NovelOpsStatusRow(
                title = "No import work",
                subtitle = "Pick EPUB files or a folder to import light novels.",
                icon = Icons.Outlined.Book,
            )
        } else {
            state.queue.forEach { item ->
                NovelImportQueueRow(item = item, actions = actions)
            }
            if (state.candidates.isNotEmpty()) {
                NovelOpsSectionLabel("Import candidates")
                state.candidates.forEach { candidate ->
                    NovelImportCandidateRow(
                        candidate = candidate,
                        actions = actions,
                    )
                }
            }
        }
        state.message?.let {
            NovelOpsStatusRow(
                title = it,
                subtitle = state.conflictPolicy.title,
                icon = Icons.Outlined.Settings,
            )
        }
    }
}

@Composable
fun NovelImportQueueRow(
    item: NovelImportQueueItemUiModel,
    actions: NovelOperationsActions,
    modifier: Modifier = Modifier,
) {
    NovelOpsRow(
        modifier = modifier,
        icon = Icons.Outlined.Book,
        title = item.candidate.title,
        subtitle = item.statusLabel,
        selected = item.phase == NovelImportPhase.Complete,
        warning = item.errorMessage != null,
        trailing = {
            if (item.busy) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colors.primary,
                )
                NovelOpsIconButton(
                    icon = Icons.Outlined.DeleteOutline,
                    contentDescription = "Cancel import",
                    onClick = { actions.onQueueAction(NovelOperationAction.CancelImport, item) },
                )
            } else if (item.phase == NovelImportPhase.Failed) {
                NovelOpsIconButton(
                    icon = Icons.Outlined.Refresh,
                    contentDescription = "Retry import",
                    onClick = { actions.onQueueAction(NovelOperationAction.RetryImport, item) },
                )
            } else {
                NovelOpsIconButton(
                    icon = Icons.Outlined.DeleteOutline,
                    contentDescription = "Remove import",
                    onClick = { actions.onQueueAction(NovelOperationAction.RemoveImport, item) },
                )
            }
        },
    )
}

@Composable
fun NovelImportCandidateRow(
    candidate: NovelImportCandidateUiModel,
    actions: NovelOperationsActions,
    modifier: Modifier = Modifier,
) {
    NovelOpsRow(
        modifier = modifier.clickable {
            actions.onCandidateAction(NovelOperationAction.ImportCandidate, candidate)
        },
        icon = when (candidate.source) {
            NovelSourceKind.LocalEpub -> Icons.Outlined.Book
            NovelSourceKind.Folder -> Icons.Outlined.FolderOpen
            NovelSourceKind.TtuSync -> Icons.Outlined.Sync
            NovelSourceKind.Backup -> Icons.Outlined.ImportExport
            NovelSourceKind.RemoteUrl -> Icons.Outlined.Download
        },
        title = candidate.title,
        subtitle = "${candidate.subtitle} - ${candidate.countLabel}",
        selected = candidate.selected,
        warning = candidate.duplicateBookId != null,
        trailing = {
            TextButton(onClick = { actions.onCandidateAction(NovelOperationAction.ImportCandidate, candidate) }) {
                Text(if (candidate.duplicateBookId != null) "Resolve" else "Import")
            }
        },
    )
}

@Composable
fun NovelDiscoverCard(
    state: NovelDiscoverState,
    actions: NovelOperationsActions,
    modifier: Modifier = Modifier,
) {
    NovelOpsCard(modifier = modifier) {
        NovelOpsHeader(
            title = "Find novels",
            subtitle = state.selectedSource?.title ?: "${state.sources.size} source(s)",
            icon = Icons.Outlined.Search,
            trailing = {
                TextButton(
                    onClick = {
                        state.selectedSource?.let { actions.onSourceAction(NovelOperationAction.RefreshSource, it) }
                    },
                    enabled = state.selectedSource != null,
                ) {
                    Text("Refresh")
                }
            },
        )
        OutlinedTextField(
            value = state.query,
            onValueChange = actions.onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            singleLine = true,
            placeholder = { Text("Search imported or remote novels") },
        )
        if (state.sources.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.sources, key = { it.id }) { source ->
                    NovelOpsChip(
                        text = source.title,
                        selected = source.selected || source.id == state.selectedSourceId,
                        warning = source.errorMessage != null,
                        onClick = { actions.onSourceAction(NovelOperationAction.OpenSource, source) },
                    )
                }
            }
        }
        when {
            state.loading -> NovelOpsStatusRow(
                title = "Loading novel sources",
                subtitle = "Scanning local folders, backups, and sync cache.",
                icon = Icons.Outlined.Sync,
                loading = true,
            )
            state.errorMessage != null -> NovelOpsStatusRow(
                title = "Novel sources unavailable",
                subtitle = state.errorMessage,
                icon = Icons.Outlined.ErrorOutline,
                error = true,
            )
            state.visibleCandidates.isEmpty() -> NovelOpsStatusRow(
                title = "No novels found",
                subtitle = "Try another query or refresh the selected source.",
                icon = Icons.Outlined.Book,
            )
            else -> state.visibleCandidates.take(8).forEach { candidate ->
                NovelImportCandidateRow(candidate = candidate, actions = actions)
            }
        }
    }
}

@Composable
fun NovelTtuSyncCard(
    state: NovelTtuSyncState,
    actions: NovelOperationsActions,
    modifier: Modifier = Modifier,
) {
    NovelOpsCard(modifier = modifier) {
        NovelOpsHeader(
            title = "TTSU sync",
            subtitle = state.statusLabel,
            icon = Icons.Outlined.Sync,
            trailing = {
                Switch(
                    checked = state.enabled,
                    onCheckedChange = { actions.onSyncModeChange(if (it) NovelSyncMode.Auto else NovelSyncMode.Off) },
                )
            },
        )
        NovelOpsSegmentedRow(
            values = NovelSyncMode.values().map { it.title },
            selected = state.mode.title,
            onSelect = { title ->
                NovelSyncMode.values().firstOrNull { it.title == title }?.let(actions.onSyncModeChange)
            },
        )
        NovelOpsSegmentedRow(
            values = NovelSyncDirection.values().map { it.title },
            selected = state.direction.title,
            onSelect = { title ->
                NovelSyncDirection.values().firstOrNull { it.title == title }?.let(actions.onSyncDirectionChange)
            },
        )
        state.prompt?.let { prompt ->
            NovelOpsStatusRow(
                title = prompt.userCode,
                subtitle = prompt.instruction,
                icon = Icons.Outlined.ContentCopy,
                action = "Copy",
                onAction = { actions.onOperation(NovelOperationAction.CopyDeviceCode) },
            )
        }
        NovelOpsStatusRow(
            title = state.summary ?: state.authStatus.title,
            subtitle = state.errorMessage ?: state.lastSyncLabel ?: "Google Drive compatible TTSU progress files",
            icon = Icons.Outlined.Sync,
            loading = state.syncing,
            error = state.errorMessage != null || state.authStatus == NovelDriveAuthStatus.Failed,
            action = when {
                state.connected && state.syncing -> "Stop"
                state.connected -> "Sync"
                else -> "Connect"
            },
            onAction = {
                actions.onOperation(
                    when {
                        state.connected && state.syncing -> NovelOperationAction.StopSync
                        state.connected -> NovelOperationAction.RunSync
                        else -> NovelOperationAction.ConnectDrive
                    },
                )
            },
        )
        if (state.connected) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { actions.onOperation(NovelOperationAction.DisconnectDrive) }) {
                    Text("Sign out")
                }
            }
        }
    }
}

@Composable
fun NovelCategoryManagerCard(
    state: NovelCategoryManagerState,
    actions: NovelOperationsActions,
    modifier: Modifier = Modifier,
) {
    NovelOpsCard(modifier = modifier) {
        NovelOpsHeader(
            title = "Novel categories",
            subtitle = state.defaultCategory?.title?.let { "Default: $it" } ?: "${state.categories.size} category(s)",
            icon = Icons.Outlined.Category,
            trailing = {
                Button(onClick = { actions.onOperation(NovelOperationAction.CreateCategory) }) {
                    Text("Add")
                }
            },
        )
        if (state.categories.isEmpty()) {
            NovelOpsStatusRow(
                title = "No custom categories",
                subtitle = "Create categories to organize imported light novels.",
                icon = Icons.Outlined.Category,
            )
        } else {
            state.categories.forEach { category ->
                NovelCategoryRow(
                    category = category,
                    selected = category.id == state.defaultCategoryId,
                    actions = actions,
                )
            }
        }
        state.message?.let {
            NovelOpsStatusRow(
                title = it,
                subtitle = "Novel category manager",
                icon = Icons.Outlined.Category,
            )
        }
    }
}

@Composable
fun NovelCategoryRow(
    category: app.chimahon.shared.novelui.ChimahonNovelCategoryUiModel,
    selected: Boolean,
    actions: NovelOperationsActions,
    modifier: Modifier = Modifier,
) {
    NovelOpsRow(
        modifier = modifier.clickable { actions.onCategoryAction(NovelOperationAction.SetDefaultCategory, category) },
        icon = Icons.Outlined.Category,
        title = category.title,
        subtitle = if (selected) "Default category" else "${category.itemCount} book(s)",
        selected = selected,
        trailing = {
            NovelOpsIconButton(
                icon = Icons.Outlined.KeyboardArrowUp,
                contentDescription = "Move ${category.title} up",
                onClick = { actions.onCategoryAction(NovelOperationAction.MoveCategoryUp, category) },
            )
            NovelOpsIconButton(
                icon = Icons.Outlined.KeyboardArrowDown,
                contentDescription = "Move ${category.title} down",
                onClick = { actions.onCategoryAction(NovelOperationAction.MoveCategoryDown, category) },
            )
            NovelOpsIconButton(
                icon = Icons.Outlined.Settings,
                contentDescription = "Rename ${category.title}",
                onClick = { actions.onCategoryAction(NovelOperationAction.RenameCategory, category) },
            )
            NovelOpsIconButton(
                icon = Icons.Outlined.DeleteOutline,
                contentDescription = "Delete ${category.title}",
                onClick = { actions.onCategoryAction(NovelOperationAction.DeleteCategory, category) },
            )
        },
    )
}

@Composable
fun NovelOpsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.10f)),
        elevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
fun NovelOpsHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NovelOpsTile(icon = icon, selected = true)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            NovelOpsLabel(text = title, size = 16, weight = FontWeight.SemiBold)
            NovelOpsLabel(
                text = subtitle,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                size = 12,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            trailing()
        }
    }
}

@Composable
fun NovelOpsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    warning: Boolean = false,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                when {
                    warning -> MaterialTheme.colors.error.copy(alpha = 0.06f)
                    selected -> MaterialTheme.colors.primary.copy(alpha = 0.06f)
                    else -> Color.Transparent
                },
            )
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NovelOpsTile(icon = icon, selected = selected, warning = warning)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            NovelOpsLabel(text = title, size = 14, weight = FontWeight.SemiBold)
            NovelOpsLabel(
                text = subtitle,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                size = 11,
                maxLines = 2,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
            trailing()
        }
    }
}

@Composable
fun NovelOpsStatusRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    error: Boolean = false,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    NovelOpsRow(
        modifier = modifier,
        icon = icon,
        title = title,
        subtitle = subtitle,
        warning = error,
        trailing = {
            when {
                loading -> CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colors.primary,
                )
                action != null && onAction != null -> TextButton(onClick = onAction) { Text(action) }
            }
        },
    )
}

@Composable
fun NovelOpsTile(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    warning: Boolean = false,
) {
    val color = when {
        warning -> MaterialTheme.colors.error
        selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.62f)
    }
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(21.dp),
        )
    }
}

@Composable
fun NovelOpsIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
fun NovelOpsChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    warning: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val color = when {
        warning -> MaterialTheme.colors.error
        selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.62f)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.12f))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        NovelOpsLabel(text = text, color = color, size = 11, weight = FontWeight.SemiBold)
    }
}

@Composable
fun NovelOpsSegmentedRow(
    values: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        values.forEach { value ->
            NovelOpsChip(text = value, selected = value == selected, onClick = { onSelect(value) })
        }
    }
}

@Composable
fun NovelOpsSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    NovelOpsLabel(
        text = text.uppercase(),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
        size = 11,
        weight = FontWeight.Bold,
        modifier = modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 4.dp),
    )
}

@Composable
fun NovelOpsLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface,
    size: Int = 13,
    weight: FontWeight = FontWeight.Normal,
    maxLines: Int = 1,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = size.sp,
        fontWeight = weight,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}
