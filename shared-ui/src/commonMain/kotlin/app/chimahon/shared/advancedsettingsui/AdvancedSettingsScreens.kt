package app.chimahon.shared.advancedsettingsui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonAdvancedSettingsScreen(
    state: ChimahonAdvancedSettingsState,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = if (state.query.isBlank() && state.activeCategories.isEmpty()) {
        listOfNotNull(state.selectedPage)
    } else {
        state.visiblePages
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item(key = "advanced:header") {
            ChimahonAdvancedSettingsHeader(
                title = state.selectedPage?.title ?: ChimahonAdvancedSettingsRoute.Overview.title,
                subtitle = state.selectedPage?.subtitle ?: ChimahonAdvancedSettingsRoute.Overview.subtitle,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }
        item(key = "advanced:search") {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ChimahonAdvancedSettingsSearchBar(
                    query = state.query,
                    onQueryChange = { onEvent(ChimahonAdvancedSettingsEvent.SearchChanged(it)) },
                )
                ChimahonAdvancedCategoryFilterRow(
                    selected = state.activeCategories,
                    onToggle = { category, selected ->
                        onEvent(ChimahonAdvancedSettingsEvent.CategoryToggled(category, selected))
                    },
                )
            }
        }
        if (pages.isEmpty()) {
            item(key = "advanced:empty") {
                ChimahonAdvancedEmptyState(
                    title = "No matching settings",
                    subtitle = "Try another search term or remove a filter.",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
        pages.forEach { page ->
            if (state.query.isNotBlank() || state.activeCategories.isNotEmpty()) {
                item(key = "advanced:${page.route.key}:title") {
                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                }
            }
            items(page.groups, key = { "${page.route.key}:${it.key}" }) { group ->
                ChimahonAdvancedSettingsGroupCard(
                    group = group,
                    onEvent = onEvent,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
        item(key = "advanced:bottom") {
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun ChimahonBackupRestoreScreen(
    state: ChimahonBackupRestoreState,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item(key = "backup:header") {
            ChimahonAdvancedSettingsHeader(
                title = "Backup and restore",
                subtitle = "Export, preview and restore Chimahon data.",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }
        item(key = "backup:summary") {
            ChimahonAdvancedPanel(Modifier.padding(horizontal = 16.dp)) {
                ChimahonBackupSummaryRow(
                    label = "Last backup",
                    value = state.lastBackupLabel ?: "Never",
                    icon = ChimahonAdvancedSettingIcon.Backup,
                )
                Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.07f))
                ChimahonBackupSummaryRow(
                    label = "Next backup",
                    value = state.nextBackupLabel ?: "Manual",
                    icon = ChimahonAdvancedSettingIcon.Sync,
                )
                if (state.inProgress != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = state.inProgress.label,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.70f),
                    )
                    Spacer(Modifier.height(6.dp))
                    if (state.inProgress.fraction == null) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    } else {
                        LinearProgressIndicator(
                            progress = state.inProgress.fraction.coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
        item(key = "backup:scopes:title") {
            ChimahonAdvancedSectionTitle(
                title = "Backup contents",
                subtitle = "Choose the Android-compatible sections to export or restore.",
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        items(state.scopes, key = { "scope:${it.key}" }) { scope ->
            ChimahonBackupScopeRow(
                scope = scope,
                onChange = { selected ->
                    onEvent(ChimahonAdvancedSettingsEvent.BackupScopeChanged(scope.key, selected))
                },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        item(key = "backup:actions") {
            ChimahonAdvancedPanel(Modifier.padding(horizontal = 16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = { onEvent(ChimahonAdvancedSettingsEvent.ActionClicked("create_backup")) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.primary),
                    ) {
                        Text("Create backup")
                    }
                    TextButton(
                        onClick = { onEvent(ChimahonAdvancedSettingsEvent.ActionClicked("restore_backup")) },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.primary),
                    ) {
                        Text("Restore")
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonMaintenanceScreen(
    rows: List<ChimahonMaintenanceRow>,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item(key = "maintenance:header") {
            ChimahonAdvancedSettingsHeader(
                title = "Data and storage",
                subtitle = "Cache cleanup, database repair, download maintenance.",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }
        items(rows, key = { it.key }) { row ->
            ChimahonMaintenanceListRow(
                row = row,
                onClick = { onEvent(ChimahonAdvancedSettingsEvent.ActionClicked(row.key)) },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
fun ChimahonAppearanceThemeScreen(
    swatches: List<ChimahonThemeSwatch>,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item(key = "appearance:header") {
            ChimahonAdvancedSettingsHeader(
                title = "Appearance",
                subtitle = "Theme presets and display style.",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }
        item(key = "appearance:themes") {
            ChimahonAdvancedSectionTitle(
                title = "Themes",
                subtitle = "Matches the Android theme list with desktop-safe swatches.",
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            ) {
                items(swatches, key = { it.key }) { swatch ->
                    ChimahonThemeSwatchCard(
                        swatch = swatch,
                        onClick = { onEvent(ChimahonAdvancedSettingsEvent.ThemeSelected(swatch.key)) },
                    )
                }
            }
        }
    }
}

@Composable
fun ChimahonNavigationEditorScreen(
    tabs: List<ChimahonNavigationTabItem>,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(key = "navigation:header") {
            ChimahonAdvancedSettingsHeader(
                title = "Navigation editor",
                subtitle = "Pick where manga, anime, novels and app tools appear.",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }
        items(tabs, key = { it.key }) { tab ->
            ChimahonNavigationTabRow(
                tab = tab,
                onEvent = onEvent,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
fun ChimahonSecurityPrivacyScreen(
    toggles: List<ChimahonSecurityToggle>,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(key = "security:header") {
            ChimahonAdvancedSettingsHeader(
                title = "Security and privacy",
                subtitle = "Incognito, offline mode, extension checks and backup safety.",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }
        items(toggles, key = { it.key }) { toggle ->
            ChimahonSecurityToggleRow(
                toggle = toggle,
                onChange = {
                    onEvent(ChimahonAdvancedSettingsEvent.SecurityToggleChanged(toggle.key, it))
                },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
fun ChimahonDiagnosticsScreen(
    rows: List<ChimahonDiagnosticRow>,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(key = "diagnostics:header") {
            ChimahonAdvancedSettingsHeader(
                title = "Diagnostics",
                subtitle = "Runtime health checks for the KMP port.",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }
        items(rows, key = { it.key }) { row ->
            ChimahonDiagnosticListRow(
                row = row,
                onAction = {
                    onEvent(ChimahonAdvancedSettingsEvent.DiagnosticActionClicked(row.key))
                },
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
fun ChimahonAdvancedSettingsHeader(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.68f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun ChimahonAdvancedSettingsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = ChimahonAdvancedSettingIcon.Search.imageVector,
                contentDescription = null,
            )
        },
        label = { Text("Search settings") },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChimahonAdvancedCategoryFilterRow(
    selected: Set<ChimahonAdvancedSettingCategory>,
    onToggle: (ChimahonAdvancedSettingCategory, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<ChimahonAdvancedSettingCategory> = ChimahonAdvancedSettingCategory.entries,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { category ->
            val active = category in selected
            ChimahonAdvancedFilterChip(
                text = category.label,
                selected = active,
                onClick = { onToggle(category, !active) },
            )
        }
    }
}

@Composable
fun ChimahonAdvancedFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.58f)
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colors.primary.copy(alpha = 0.13f) else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colors.primary.copy(alpha = 0.46f) else MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                shape = CircleShape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.caption,
            color = color,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ChimahonAdvancedSectionTitle(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.SemiBold,
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.60f),
            )
        }
    }
}

@Composable
private fun ChimahonAdvancedPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content,
        )
    }
}

@Composable
private fun ChimahonBackupSummaryRow(
    label: String,
    value: String,
    icon: ChimahonAdvancedSettingIcon,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AdvancedIconTile(icon = icon, tone = ChimahonAdvancedSettingTone.Info, enabled = true)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f))
            Text(value, style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ChimahonBackupScopeRow(
    scope: ChimahonBackupScopeItem,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonAdvancedPanel(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = scope.selected,
                onCheckedChange = if (scope.required) null else onChange,
                enabled = !scope.required,
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(scope.title, style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold)
                if (!scope.subtitle.isNullOrBlank()) {
                    Text(
                        scope.subtitle,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f),
                    )
                }
            }
            if (scope.required) {
                ChimahonAdvancedStatusPill(
                    text = "Required",
                    tone = ChimahonAdvancedSettingTone.Info,
                )
            }
        }
    }
}

@Composable
private fun ChimahonMaintenanceListRow(
    row: ChimahonMaintenanceRow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonAdvancedPanel(
        modifier = modifier.clickable(enabled = row.enabled, onClick = onClick),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AdvancedIconTile(row.icon, row.tone, row.enabled)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(row.title, style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold)
                if (!row.subtitle.isNullOrBlank()) {
                    Text(
                        row.subtitle,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f),
                    )
                }
            }
            if (!row.sizeLabel.isNullOrBlank()) {
                ChimahonAdvancedStatusPill(row.sizeLabel, row.tone, enabled = row.enabled)
                Spacer(Modifier.width(8.dp))
            }
            TextButton(onClick = onClick, enabled = row.enabled) {
                Text(row.actionLabel)
            }
        }
    }
}

@Composable
private fun ChimahonThemeSwatchCard(
    swatch: ChimahonThemeSwatch,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(144.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface)
            .border(
                width = if (swatch.selected) 2.dp else 1.dp,
                color = if (swatch.selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.10f),
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(enabled = swatch.enabled, onClick = onClick)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(swatch.background),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(swatch.primary),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(width = 54.dp, height = 12.dp)
                    .clip(CircleShape)
                    .background(swatch.secondary),
            )
        }
        Text(
            swatch.title,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (!swatch.subtitle.isNullOrBlank()) {
            Text(
                swatch.subtitle,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonNavigationTabRow(
    tab: ChimahonNavigationTabItem,
    onEvent: (ChimahonAdvancedSettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonAdvancedPanel(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AdvancedIconTile(tab.icon, ChimahonAdvancedSettingTone.Info, tab.enabled)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tab.title, style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold)
                if (!tab.subtitle.isNullOrBlank()) {
                    Text(
                        tab.subtitle,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f),
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    ChimahonNavigationDestination.entries.forEach { destination ->
                        ChimahonAdvancedFilterChip(
                            text = destination.title,
                            selected = destination == tab.destination,
                            onClick = {
                                onEvent(
                                    ChimahonAdvancedSettingsEvent.NavigationDestinationChanged(
                                        tab.key,
                                        destination,
                                    ),
                                )
                            },
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { onEvent(ChimahonAdvancedSettingsEvent.NavigationMoveRequested(tab.key, true)) },
                    enabled = tab.canMoveUp,
                ) {
                    Text("^", style = MaterialTheme.typography.h6)
                }
                IconButton(
                    onClick = { onEvent(ChimahonAdvancedSettingsEvent.NavigationMoveRequested(tab.key, false)) },
                    enabled = tab.canMoveDown,
                ) {
                    Text("v", style = MaterialTheme.typography.h6)
                }
            }
        }
    }
}

@Composable
private fun ChimahonSecurityToggleRow(
    toggle: ChimahonSecurityToggle,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    ChimahonAdvancedPanel(
        modifier = modifier.clickable(enabled = toggle.enabled) { onChange(!toggle.checked) },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AdvancedIconTile(toggle.icon, toggle.tone, toggle.enabled)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(toggle.title, style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold)
                if (!toggle.subtitle.isNullOrBlank()) {
                    Text(
                        toggle.subtitle,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f),
                    )
                }
            }
            Switch(
                checked = toggle.checked,
                onCheckedChange = onChange,
                enabled = toggle.enabled,
            )
        }
    }
}

@Composable
private fun ChimahonDiagnosticListRow(
    row: ChimahonDiagnosticRow,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tone = when (row.status) {
        ChimahonDiagnosticStatus.Healthy -> ChimahonAdvancedSettingTone.Success
        ChimahonDiagnosticStatus.Warning -> ChimahonAdvancedSettingTone.Warning
        ChimahonDiagnosticStatus.Error -> ChimahonAdvancedSettingTone.Error
        ChimahonDiagnosticStatus.Unknown -> ChimahonAdvancedSettingTone.Info
    }
    ChimahonAdvancedPanel(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AdvancedIconTile(ChimahonAdvancedSettingIcon.Diagnostics, tone, enabled = true)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(row.title, style = MaterialTheme.typography.body1, fontWeight = FontWeight.SemiBold)
                if (!row.subtitle.isNullOrBlank()) {
                    Text(
                        row.subtitle,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f),
                    )
                }
            }
            ChimahonAdvancedStatusPill(row.value, tone)
            if (!row.actionLabel.isNullOrBlank()) {
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onAction) {
                    Text(row.actionLabel)
                }
            }
        }
    }
}

@Composable
fun ChimahonAdvancedEmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp),
        color = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = ChimahonAdvancedSettingIcon.Search.imageVector,
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(42.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
            )
        }
    }
}

private val ChimahonNavigationDestination.title: String
    get() = when (this) {
        ChimahonNavigationDestination.Hidden -> "Hidden"
        ChimahonNavigationDestination.BottomBar -> "Bottom"
        ChimahonNavigationDestination.Rail -> "Rail"
        ChimahonNavigationDestination.More -> "More"
    }
