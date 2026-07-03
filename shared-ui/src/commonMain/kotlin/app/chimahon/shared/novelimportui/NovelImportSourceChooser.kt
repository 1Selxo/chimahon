package app.chimahon.shared.novelimportui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonNovelImportCompactToolbar(
    state: ChimahonNovelImportToolbarUiState,
    modifier: Modifier = Modifier,
    onNavigateUp: (() -> Unit)? = null,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surface,
        elevation = 0.dp,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (onNavigateUp != null) {
                    TextButton(onClick = onNavigateUp) {
                        Text("Back")
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    NovelImportLabel(
                        text = if (state.selectedCount > 0) {
                            "${state.selectedCount} selected"
                        } else {
                            state.title
                        },
                        color = colors.content,
                        size = 20,
                        weight = FontWeight.SemiBold,
                    )
                    val subtitle = buildList {
                        state.statusLabel?.takeIf(String::isNotBlank)?.let { add(it) }
                        state.subtitle?.takeIf(String::isNotBlank)?.let { add(it) }
                        if (state.totalCount > 0) add("${state.totalCount} file(s)")
                    }.joinToString(" - ")
                    if (subtitle.isNotBlank()) {
                        NovelImportLabel(
                            text = subtitle,
                            color = colors.secondaryContent,
                            size = 12,
                        )
                    }
                }
                if (state.busy) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = colors.primary,
                        strokeWidth = 2.dp,
                    )
                }
                state.primaryActions.forEach { action ->
                    NovelImportToolbarActionButton(action = action, colors = colors)
                }
                ChimahonNovelImportToolbarOverflowMenu(
                    actions = state.overflowActions,
                    colors = colors,
                )
            }
            Divider(color = colors.divider)
        }
    }
}

@Composable
fun ChimahonNovelImportSourceChooser(
    sources: List<ChimahonNovelImportSourceUiModel>,
    selectedSourceId: String?,
    onSourceSelected: (ChimahonNovelImportSourceUiModel) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        sources.forEach { source ->
            ChimahonNovelImportSourceCard(
                source = source,
                selected = source.id == selectedSourceId,
                onClick = { onSourceSelected(source) },
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonNovelImportSourceCard(
    source: ChimahonNovelImportSourceUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    NovelImportCardSurface(
        modifier = modifier.width(220.dp),
        selected = selected,
        fillMaxWidth = false,
        colors = colors,
        onClick = if (source.enabled) onClick else null,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Icon(
                    imageVector = source.icon.imageVector,
                    contentDescription = null,
                    tint = when {
                        !source.enabled -> colors.secondaryContent.copy(alpha = 0.42f)
                        selected -> colors.primary
                        else -> colors.secondaryContent
                    },
                    modifier = Modifier.size(22.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    NovelImportLabel(
                        text = source.title,
                        color = if (source.enabled) colors.content else colors.secondaryContent.copy(alpha = 0.50f),
                        size = 14,
                        weight = FontWeight.SemiBold,
                    )
                    NovelImportLabel(
                        text = source.kind.label,
                        color = colors.secondaryContent,
                        size = 10,
                    )
                }
                if (source.recommended) {
                    NovelImportStatusChip(
                        text = "Default",
                        kind = ChimahonNovelImportStatusKind.Active,
                        colors = colors,
                    )
                } else if (!source.badgeLabel.isNullOrBlank()) {
                    NovelImportStatusChip(
                        text = source.badgeLabel,
                        kind = ChimahonNovelImportStatusKind.Info,
                        colors = colors,
                    )
                }
            }
            source.supportingLabel?.let { label ->
                NovelImportLabel(
                    text = label,
                    color = colors.secondaryContent,
                    size = 12,
                    lineHeight = 17,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
fun ChimahonNovelImportToolbarOverflowMenu(
    actions: List<ChimahonNovelImportToolbarAction>,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    var expanded by remember { mutableStateOf(false) }
    NovelImportIconButton(
        modifier = modifier,
        icon = ChimahonNovelImportUiIcon.More,
        contentDescription = "More actions",
        enabled = actions.isNotEmpty(),
        onClick = { expanded = true },
        colors = colors,
    )
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        actions.forEach { action ->
            DropdownMenuItem(
                enabled = action.enabled,
                onClick = {
                    expanded = false
                    action.onClick()
                },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    val tint = if (action.destructive) colors.error else Color.Unspecified
                    Icon(
                        imageVector = action.icon.imageVector,
                        contentDescription = null,
                        tint = tint,
                    )
                    Text(
                        text = action.label,
                        color = tint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun NovelImportToolbarActionButton(
    action: ChimahonNovelImportToolbarAction,
    modifier: Modifier = Modifier,
    colors: ChimahonNovelImportUiColors = ChimahonNovelImportUiDefaults.colors(),
) {
    NovelImportIconButton(
        modifier = modifier,
        icon = action.icon,
        contentDescription = action.label,
        enabled = action.enabled,
        active = action.active,
        destructive = action.destructive,
        onClick = action.onClick,
        colors = colors,
    )
}
