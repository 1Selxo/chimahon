package app.chimahon.shared.trackingui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
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

@Composable
fun ChimahonTrackingSettingsSection(
    state: ChimahonTrackingSettingsUiState,
    callbacks: ChimahonTrackingSettingsCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ChimahonTrackingSectionHeader(
            title = "Tracking settings",
            subtitle = state.lastGlobalSyncLabel ?: "Not synced yet",
        )
        if (state.pendingChanges > 0 || state.errorMessage != null) {
            ChimahonTrackingSyncSummaryStrip(
                pendingChanges = state.pendingChanges,
                errorMessage = state.errorMessage,
                onSyncNow = callbacks.onSyncNow,
            )
        }
        ChimahonTrackingSettingsGroup {
            ChimahonTrackingSwitchRow(
                title = "Track when adding to library",
                subtitle = "Create tracker entries for newly saved manga, anime, and novels.",
                checked = state.trackOnLibraryAdd,
                onCheckedChange = callbacks.onTrackOnLibraryAddChange,
            )
            ChimahonTrackingPreferenceDivider()
            ChimahonTrackingSwitchRow(
                title = "Auto sync",
                subtitle = "Push progress and pull remote status changes in the background.",
                checked = state.autoSyncEnabled,
                onCheckedChange = callbacks.onAutoSyncEnabledChange,
            )
            ChimahonTrackingPreferenceDivider()
            ChimahonTrackingOptionRow(
                title = "Sync interval",
                subtitle = state.syncInterval.description,
                currentLabel = state.syncInterval.label,
            ) { dismiss ->
                ChimahonTrackingSyncIntervalMenu(
                    selected = state.syncInterval,
                    onSelected = callbacks.onSyncIntervalChange,
                    dismiss = dismiss,
                )
            }
            ChimahonTrackingPreferenceDivider()
            ChimahonTrackingOptionRow(
                title = "Conflict handling",
                subtitle = state.conflictStrategy.description,
                currentLabel = state.conflictStrategy.label,
            ) { dismiss ->
                ChimahonTrackingConflictStrategyMenu(
                    selected = state.conflictStrategy,
                    onSelected = callbacks.onConflictStrategyChange,
                    dismiss = dismiss,
                )
            }
        }

        ChimahonTrackingSettingsGroup {
            ChimahonTrackingSwitchRow(
                title = "Sync progress changes",
                subtitle = "Queue updates when chapters, episodes, or pages are completed.",
                checked = state.syncOnProgressChange,
                onCheckedChange = callbacks.onSyncOnProgressChange,
            )
            ChimahonTrackingPreferenceDivider()
            ChimahonTrackingSwitchRow(
                title = "Sync completed entries",
                subtitle = "Mark tracker entries complete when local progress reaches the end.",
                checked = state.syncOnCompletion,
                onCheckedChange = callbacks.onSyncOnCompletionChange,
            )
            ChimahonTrackingPreferenceDivider()
            ChimahonTrackingSwitchRow(
                title = "Private by default",
                subtitle = "Use private tracking where the connected service supports it.",
                checked = state.privateTrackingDefault,
                onCheckedChange = callbacks.onPrivateTrackingDefaultChange,
            )
            ChimahonTrackingPreferenceDivider()
            ChimahonTrackingSwitchRow(
                title = "Pause in incognito",
                subtitle = "Avoid tracker writes while incognito reading is enabled.",
                checked = state.pauseInIncognito,
                onCheckedChange = callbacks.onPauseInIncognitoChange,
            )
            ChimahonTrackingPreferenceDivider()
            ChimahonTrackingSwitchRow(
                title = "Sync on Wi-Fi only",
                subtitle = "Defer background tracker traffic while on metered networks.",
                checked = state.syncOnlyOnWifi,
                onCheckedChange = callbacks.onSyncOnlyOnWifiChange,
            )
        }

        ChimahonTrackingSettingsGroup {
            ChimahonTrackingMediaKindRow(
                mediaKind = ChimahonTrackingMediaKind.Manga,
                enabled = state.includeManga,
                defaultStatus = state.defaultStatusFor(ChimahonTrackingMediaKind.Manga),
                onEnabledChange = callbacks.onMediaKindEnabledChange,
                onDefaultStatusChange = callbacks.onDefaultStatusChange,
            )
            ChimahonTrackingPreferenceDivider()
            ChimahonTrackingMediaKindRow(
                mediaKind = ChimahonTrackingMediaKind.Anime,
                enabled = state.includeAnime,
                defaultStatus = state.defaultStatusFor(ChimahonTrackingMediaKind.Anime),
                onEnabledChange = callbacks.onMediaKindEnabledChange,
                onDefaultStatusChange = callbacks.onDefaultStatusChange,
            )
            ChimahonTrackingPreferenceDivider()
            ChimahonTrackingMediaKindRow(
                mediaKind = ChimahonTrackingMediaKind.LightNovel,
                enabled = state.includeLightNovels,
                defaultStatus = state.defaultStatusFor(ChimahonTrackingMediaKind.LightNovel),
                onEnabledChange = callbacks.onMediaKindEnabledChange,
                onDefaultStatusChange = callbacks.onDefaultStatusChange,
            )
        }

        ChimahonTrackingCategoryScopeRow(
            state = state,
            onOpenCategoryScope = callbacks.onOpenCategoryScope,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = callbacks.onResetDefaults) {
                Text("Reset")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = callbacks.onSyncNow) {
                Text("Sync now")
            }
        }
    }
}

@Composable
private fun ChimahonTrackingSettingsGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp),
        elevation = 0.dp,
    ) {
        Column(content = content)
    }
}

@Composable
private fun ChimahonTrackingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                modifier = Modifier.padding(top = 2.dp),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun ChimahonTrackingOptionRow(
    title: String,
    subtitle: String,
    currentLabel: String,
    modifier: Modifier = Modifier,
    menuContent: @Composable (dismiss: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
            )
            Text(
                text = subtitle,
                modifier = Modifier.padding(top = 2.dp),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
            )
        }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(
                    text = currentLabel,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                menuContent { expanded = false }
            }
        }
    }
}

@Composable
private fun ChimahonTrackingSyncIntervalMenu(
    selected: ChimahonTrackingSyncInterval,
    onSelected: (ChimahonTrackingSyncInterval) -> Unit,
    dismiss: () -> Unit,
) {
    ChimahonTrackingSyncInterval.entries.forEach { interval ->
        DropdownMenuItem(
            onClick = {
                dismiss()
                onSelected(interval)
            },
        ) {
            ChimahonTrackingMenuItemText(
                title = interval.label,
                subtitle = interval.description,
                selected = interval == selected,
            )
        }
    }
}

@Composable
private fun ChimahonTrackingConflictStrategyMenu(
    selected: ChimahonTrackingConflictStrategy,
    onSelected: (ChimahonTrackingConflictStrategy) -> Unit,
    dismiss: () -> Unit,
) {
    ChimahonTrackingConflictStrategy.entries.forEach { strategy ->
        DropdownMenuItem(
            onClick = {
                dismiss()
                onSelected(strategy)
            },
        ) {
            ChimahonTrackingMenuItemText(
                title = strategy.label,
                subtitle = strategy.description,
                selected = strategy == selected,
            )
        }
    }
}

@Composable
private fun ChimahonTrackingMediaKindRow(
    mediaKind: ChimahonTrackingMediaKind,
    enabled: Boolean,
    defaultStatus: ChimahonTrackingStatus,
    onEnabledChange: (ChimahonTrackingMediaKind, Boolean) -> Unit,
    onDefaultStatusChange: (ChimahonTrackingMediaKind, ChimahonTrackingStatus) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = mediaKind.icon(),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (enabled) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.38f),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mediaKind.pluralLabel,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Default: ${defaultStatus.labelFor(mediaKind)}",
                modifier = Modifier.padding(top = 2.dp),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
            )
        }
        ChimahonTrackingDefaultStatusMenu(
            mediaKind = mediaKind,
            selected = defaultStatus,
            enabled = enabled,
            onSelected = { onDefaultStatusChange(mediaKind, it) },
        )
        Switch(
            checked = enabled,
            onCheckedChange = { onEnabledChange(mediaKind, it) },
        )
    }
}

@Composable
private fun ChimahonTrackingDefaultStatusMenu(
    mediaKind: ChimahonTrackingMediaKind,
    selected: ChimahonTrackingStatus,
    enabled: Boolean,
    onSelected: (ChimahonTrackingStatus) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            enabled = enabled,
            onClick = { expanded = true },
        ) {
            Text(
                text = selected.labelFor(mediaKind),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            mediaKind.defaultStatusOptions().forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onSelected(option.status)
                    },
                ) {
                    Text(option.label)
                }
            }
        }
    }
}

@Composable
private fun ChimahonTrackingCategoryScopeRow(
    state: ChimahonTrackingSettingsUiState,
    onOpenCategoryScope: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp),
        elevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Category scope",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = state.categoryScopeLabel,
                        modifier = Modifier.padding(top = 2.dp),
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    )
                }
                OutlinedButton(onClick = onOpenCategoryScope) {
                    Text("Edit")
                }
            }
            val chips = buildList {
                addAll(state.enabledMediaKinds.map { it.label })
                addAll(state.includedCategoryLabels.map { "Include $it" })
                addAll(state.excludedCategoryLabels.map { "Exclude $it" })
            }
            if (chips.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    chips.forEach { label ->
                        ChimahonTrackingStatusChip(
                            text = label,
                            tone = ChimahonTrackingTone.Muted,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChimahonTrackingSyncSummaryStrip(
    pendingChanges: Int,
    errorMessage: String?,
    onSyncNow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasError = errorMessage != null
    val tone = if (hasError) ChimahonTrackingTone.Error else ChimahonTrackingTone.Info
    val colors = tone.colors()
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.background,
        shape = RoundedCornerShape(8.dp),
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.content.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (hasError) {
                        ChimahonTrackingIcon.Error.imageVector()
                    } else {
                        ChimahonTrackingIcon.Sync.imageVector()
                    },
                    contentDescription = null,
                    tint = colors.content,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (hasError) "Tracking sync needs attention" else "$pendingChanges pending changes",
                    style = MaterialTheme.typography.subtitle2,
                    color = colors.content,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = errorMessage ?: "Queued changes will sync with connected tracker accounts.",
                    modifier = Modifier.padding(top = 2.dp),
                    style = MaterialTheme.typography.caption,
                    color = colors.content.copy(alpha = 0.78f),
                )
            }
            TextButton(onClick = onSyncNow) {
                Text("Sync")
            }
        }
    }
}

@Composable
private fun ChimahonTrackingMenuItemText(
    title: String,
    subtitle: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.body2,
            color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
        Text(
            text = subtitle,
            modifier = Modifier.padding(top = 2.dp),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
        )
    }
}

@Composable
private fun ChimahonTrackingPreferenceDivider() {
    Divider(
        modifier = Modifier.padding(start = 16.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
    )
}
