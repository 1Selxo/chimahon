@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package app.chimahon.shared.desktopui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonDesktopShortcutSheet(
    groups: List<ChimahonDesktopShortcutGroup>,
    modifier: Modifier = Modifier,
    query: String = "",
    scope: ChimahonDesktopShortcutScope? = null,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    val visibleGroups = groups.filterDesktopShortcuts(query, scope)
    ChimahonDesktopFloatingSurface(
        modifier = modifier.widthIn(max = 680.dp),
        colors = colors,
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (visibleGroups.isEmpty()) {
                item {
                    ChimahonDesktopHelpStatusRow(
                        title = "No shortcuts found",
                        subtitle = "Try another search or clear the selected shortcut scope.",
                        icon = ChimahonDesktopIcon.Search,
                        colors = colors,
                    )
                }
            } else {
                visibleGroups.forEach { group ->
                    item(key = "header:${group.title}") {
                        ChimahonDesktopSectionHeader(
                            title = group.title,
                            subtitle = group.subtitle,
                            colors = colors,
                        )
                    }
                    items(group.shortcuts, key = { "${it.scope}:${it.title}:${it.keys.joinToString()}" }) { shortcut ->
                        ChimahonDesktopShortcutRow(
                            shortcut = shortcut,
                            colors = colors,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonDesktopShortcutRow(
    shortcut: ChimahonDesktopShortcut,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            ChimahonDesktopText(
                text = shortcut.title,
                color = if (shortcut.enabled) colors.content else colors.secondaryContent,
                size = 13,
                weight = FontWeight.SemiBold,
            )
            if (shortcut.description.isNotBlank()) {
                ChimahonDesktopText(
                    text = shortcut.description,
                    color = colors.secondaryContent,
                    size = 11,
                    lineHeight = 16,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
        ) {
            shortcut.keys.forEach { key ->
                ChimahonDesktopKeyCap(
                    text = key,
                    colors = colors,
                )
            }
        }
    }
    ChimahonDesktopDivider(indent = 18.dp, colors = colors)
}

@Composable
fun ChimahonDesktopInputHintPanel(
    hints: List<ChimahonDesktopInputHint>,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface),
    ) {
        ChimahonDesktopSectionHeader(
            title = "Mouse and touchpad",
            subtitle = "Desktop-only controls layered on top of Android-style reader gestures",
            colors = colors,
        )
        hints.forEach { hint ->
            ChimahonDesktopInputHintRow(
                hint = hint,
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDesktopInputHintRow(
    hint: ChimahonDesktopInputHint,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 18.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = hint.device.icon().imageVector,
            contentDescription = hint.device.title,
            tint = if (hint.enabled) colors.primary else colors.secondaryContent,
            modifier = Modifier.padding(end = 14.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            ChimahonDesktopText(
                text = hint.title,
                color = if (hint.enabled) colors.content else colors.secondaryContent,
                size = 13,
                weight = FontWeight.SemiBold,
            )
            ChimahonDesktopText(
                text = hint.detail,
                color = colors.secondaryContent,
                size = 11,
                lineHeight = 16,
                maxLines = 3,
                modifier = Modifier.padding(top = 2.dp),
            )
            if (hint.keys.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    hint.keys.forEach { key ->
                        ChimahonDesktopKeyCap(text = key, colors = colors)
                    }
                }
            }
        }
    }
    ChimahonDesktopDivider(indent = 58.dp, colors = colors)
}

@Composable
fun ChimahonDesktopHelpPanel(
    state: ChimahonDesktopHelpPanelState,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    ChimahonDesktopFloatingSurface(
        modifier = modifier.widthIn(max = 720.dp),
        colors = colors,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ChimahonDesktopSectionHeader(
                title = state.title,
                subtitle = state.subtitle.ifBlank { "Keyboard, mouse, and touchpad controls" },
                colors = colors,
            )
            ChimahonDesktopShortcutSheet(
                groups = state.shortcutGroups,
                colors = colors,
                modifier = Modifier.fillMaxWidth(),
            )
            ChimahonDesktopInputHintPanel(
                hints = state.inputHints,
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDesktopSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 18.dp, vertical = 12.dp),
    ) {
        ChimahonDesktopText(
            text = title,
            color = colors.content,
            size = 15,
            weight = FontWeight.Bold,
        )
        if (subtitle.isNotBlank()) {
            ChimahonDesktopText(
                text = subtitle,
                color = colors.secondaryContent,
                size = 11,
                lineHeight = 16,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
    }
}

@Composable
fun ChimahonDesktopHelpStatusRow(
    title: String,
    subtitle: String,
    icon: ChimahonDesktopIcon,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon.imageVector,
            contentDescription = "",
            tint = colors.primary,
            modifier = Modifier.padding(end = 14.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            ChimahonDesktopText(
                text = title,
                color = colors.content,
                size = 14,
                weight = FontWeight.SemiBold,
            )
            ChimahonDesktopText(
                text = subtitle,
                color = colors.secondaryContent,
                size = 11,
                lineHeight = 16,
                maxLines = 3,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
