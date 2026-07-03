@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package app.chimahon.shared.desktopshortcutsui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonDesktopShortcutCheatsheet(
    sections: List<ChimahonDesktopShortcutSectionModel>,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    selectedArea: ChimahonDesktopShortcutArea? = null,
    platform: ChimahonDesktopShortcutPlatform = ChimahonDesktopShortcutPlatform.Unknown,
    modifierLabels: ChimahonDesktopShortcutModifierLabels =
        ChimahonDesktopShortcutModifierLabels.forPlatform(platform),
    title: String = "Keyboard shortcuts",
    subtitle: String? = "Reader, player, browse, library, anime, and LN controls",
    onAreaSelected: ((ChimahonDesktopShortcutArea?) -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = null,
) {
    ChimahonDesktopShortcutSheet(
        state = ChimahonDesktopShortcutCheatsheetState(
            title = title,
            subtitle = subtitle,
            sections = sections,
            query = query,
            selectedArea = selectedArea,
            platform = platform,
            modifierLabels = modifierLabels,
        ),
        onQueryChange = onQueryChange,
        modifier = modifier,
        onAreaSelected = onAreaSelected,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
fun ChimahonDesktopShortcutSheet(
    state: ChimahonDesktopShortcutCheatsheetState,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onAreaSelected: ((ChimahonDesktopShortcutArea?) -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(bottom = 10.dp),
) {
    val visibleSections = state.sections.filterChimahonDesktopShortcutSections(
        query = state.query,
        area = state.selectedArea,
        platform = state.platform,
        modifierLabels = state.modifierLabels,
    )
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 720.dp)
            .heightIn(max = 560.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 12.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ChimahonDesktopShortcutSheetHeader(
                title = state.title,
                subtitle = state.subtitle,
                onDismissRequest = onDismissRequest,
            )
            ChimahonDesktopShortcutSearchField(
                query = state.query,
                onQueryChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )
            val areas = state.availableAreas()
            if (onAreaSelected != null && areas.isNotEmpty()) {
                ChimahonDesktopShortcutAreaFilterRow(
                    areas = areas,
                    selectedArea = state.selectedArea,
                    onAreaSelected = onAreaSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                )
            }
            ChimahonDesktopShortcutSectionList(
                sections = visibleSections,
                platform = state.platform,
                modifierLabels = state.modifierLabels,
                emptyTitle = state.emptyTitle,
                emptySubtitle = state.emptySubtitle,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
fun ChimahonDesktopShortcutSectionList(
    sections: List<ChimahonDesktopShortcutSectionModel>,
    platform: ChimahonDesktopShortcutPlatform,
    modifierLabels: ChimahonDesktopShortcutModifierLabels,
    modifier: Modifier = Modifier,
    emptyTitle: String = "No shortcuts found",
    emptySubtitle: String = "Try another search or clear the selected area.",
    contentPadding: PaddingValues = PaddingValues(bottom = 10.dp),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        if (sections.isEmpty()) {
            item(key = "empty") {
                ChimahonDesktopShortcutEmptyRow(
                    title = emptyTitle,
                    subtitle = emptySubtitle,
                )
            }
        } else {
            sections.forEach { section ->
                item(key = "section:${section.id}") {
                    ChimahonDesktopShortcutSectionHeader(section = section)
                }
                items(section.rows, key = { row -> row.id }) { row ->
                    ChimahonDesktopShortcutRow(
                        row = row,
                        platform = platform,
                        modifierLabels = modifierLabels,
                    )
                }
            }
        }
    }
}

@Composable
fun ChimahonDesktopShortcutRow(
    row: ChimahonDesktopShortcutRowModel,
    platform: ChimahonDesktopShortcutPlatform,
    modifierLabels: ChimahonDesktopShortcutModifierLabels,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (row.enabled) {
        MaterialTheme.colors.onSurface
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.44f)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = row.title,
                color = contentColor,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!row.description.isNullOrBlank()) {
                Text(
                    text = row.description,
                    color = MaterialTheme.colors.onSurface.copy(alpha = if (row.enabled) 0.62f else 0.38f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            if (row.conflictBadges.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    row.conflictBadges.forEach { badge ->
                        ChimahonDesktopShortcutConflictBadgeChip(badge = badge)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        ChimahonDesktopShortcutKeySequence(
            shortcut = row.shortcut,
            alternateShortcuts = row.alternateShortcuts,
            platform = platform,
            modifierLabels = modifierLabels,
            enabled = row.enabled,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun ChimahonDesktopShortcutKeySequence(
    shortcut: ChimahonDesktopShortcutChord?,
    alternateShortcuts: List<ChimahonDesktopShortcutChord>,
    platform: ChimahonDesktopShortcutPlatform,
    modifierLabels: ChimahonDesktopShortcutModifierLabels,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shortcuts = listOfNotNull(shortcut) + alternateShortcuts
    if (shortcuts.isEmpty()) {
        Text(
            text = "Unassigned",
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.44f),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
        )
        return
    }
    val sequenceDescription = shortcuts.joinToString(" or ") {
        it.displayLabel(labels = modifierLabels, platform = platform)
    }
    FlowRow(
        modifier = modifier.semantics { contentDescription = sequenceDescription },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        shortcuts.forEachIndexed { index, chord ->
            if (index > 0) {
                Text(
                    text = "or",
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.48f),
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            ChimahonDesktopShortcutKeycap(
                text = chord.displayLabel(labels = modifierLabels, platform = platform),
                enabled = enabled,
            )
        }
    }
}

@Composable
fun ChimahonDesktopShortcutSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.heightIn(min = 44.dp),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
            )
        },
        trailingIcon = if (query.isNotBlank()) {
            {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Clear search",
                    )
                }
            }
        } else {
            null
        },
        placeholder = {
            Text(
                text = "Search shortcuts",
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.50f),
            )
        },
        shape = RoundedCornerShape(6.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
            cursorColor = MaterialTheme.colors.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
fun ChimahonDesktopShortcutAreaFilterRow(
    areas: List<ChimahonDesktopShortcutArea>,
    selectedArea: ChimahonDesktopShortcutArea?,
    onAreaSelected: (ChimahonDesktopShortcutArea?) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        ChimahonDesktopShortcutAreaChip(
            label = "All",
            selected = selectedArea == null,
            onClick = { onAreaSelected(null) },
        )
        areas.forEach { area ->
            ChimahonDesktopShortcutAreaChip(
                label = area.title,
                selected = selectedArea == area,
                onClick = { onAreaSelected(area) },
            )
        }
    }
}

@Composable
fun ChimahonDesktopShortcutConflictBadgeChip(
    badge: ChimahonDesktopShortcutConflictBadge,
    modifier: Modifier = Modifier,
) {
    val accent = when (badge.severity) {
        ChimahonDesktopShortcutConflictSeverity.Info -> MaterialTheme.colors.primary
        ChimahonDesktopShortcutConflictSeverity.Warning -> MaterialTheme.colors.secondary
        ChimahonDesktopShortcutConflictSeverity.Error -> MaterialTheme.colors.error
    }
    Surface(
        modifier = modifier.semantics {
            contentDescription = listOfNotNull(badge.severity.title, badge.label, badge.detail)
                .joinToString(": ")
        },
        shape = RoundedCornerShape(5.dp),
        color = accent.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.22f)),
    ) {
        Text(
            text = badge.label,
            color = accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun ChimahonDesktopShortcutSheetHeader(
    title: String,
    subtitle: String?,
    onDismissRequest: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 14.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        if (onDismissRequest != null) {
            IconButton(onClick = onDismissRequest) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Close shortcuts",
                )
            }
        }
    }
}

@Composable
private fun ChimahonDesktopShortcutSectionHeader(
    section: ChimahonDesktopShortcutSectionModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 9.dp),
    ) {
        Text(
            text = section.title,
            color = MaterialTheme.colors.onSurface,
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (!section.subtitle.isNullOrBlank()) {
            Text(
                text = section.subtitle,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun ChimahonDesktopShortcutKeycap(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(5.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.06f else 0.03f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.14f else 0.06f),
        ),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colors.onSurface.copy(alpha = if (enabled) 0.82f else 0.38f),
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun ChimahonDesktopShortcutAreaChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = if (selected) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface
    }
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = if (selected) 0.14f else 0.06f),
        border = BorderStroke(1.dp, color.copy(alpha = if (selected) 0.28f else 0.10f)),
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
        )
    }
}

@Composable
private fun ChimahonDesktopShortcutEmptyRow(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = MaterialTheme.colors.primary,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
