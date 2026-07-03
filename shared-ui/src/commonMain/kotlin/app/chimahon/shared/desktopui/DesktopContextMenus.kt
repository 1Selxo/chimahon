package app.chimahon.shared.desktopui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonDesktopContextMenuPanel(
    actions: List<ChimahonDesktopActionItem>,
    onAction: (ChimahonDesktopActionItem) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    ChimahonDesktopFloatingSurface(
        modifier = modifier,
        colors = colors,
        maxWidth = 420.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (!title.isNullOrBlank()) {
                ChimahonDesktopSectionHeader(
                    title = title,
                    colors = colors,
                )
            }
            actions.forEach { action ->
                ChimahonDesktopContextActionRow(
                    action = action,
                    onClick = { onAction(action) },
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonDesktopContextActionRow(
    action: ChimahonDesktopActionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (action.selected) colors.primaryContainer else colors.surface)
            .clickable(enabled = action.enabled && !action.loading, onClick = onClick)
            .padding(start = 14.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChimahonDesktopIconButton(
            icon = action.icon,
            contentDescription = action.title,
            onClick = onClick,
            selected = action.selected,
            enabled = action.enabled,
            loading = action.loading,
            style = action.style,
            colors = colors,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, end = 10.dp),
        ) {
            ChimahonDesktopText(
                text = action.title,
                color = when {
                    !action.enabled -> colors.secondaryContent
                    action.isDangerous() -> colors.error
                    action.selected -> colors.primary
                    else -> colors.content
                },
                size = 13,
                weight = FontWeight.SemiBold,
            )
            if (action.description.isNotBlank()) {
                ChimahonDesktopText(
                    text = action.description,
                    color = colors.secondaryContent,
                    size = 11,
                    lineHeight = 16,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        action.shortcut?.let {
            ChimahonDesktopKeyCap(
                text = it,
                colors = colors,
            )
        }
    }
    ChimahonDesktopDivider(indent = 64.dp, colors = colors)
}

@Composable
fun ChimahonDesktopToolbarPanel(
    state: ChimahonDesktopToolbarState,
    onAction: (ChimahonDesktopActionItem) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDesktopColors = ChimahonDesktopDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        state.leadingActions.forEach { action ->
            ChimahonDesktopIconButton(
                icon = action.icon,
                contentDescription = action.title,
                selected = action.selected,
                enabled = action.enabled,
                loading = action.loading,
                style = action.style,
                onClick = { onAction(action) },
                colors = colors,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            ChimahonDesktopText(
                text = state.title,
                color = colors.content,
                size = 15,
                weight = FontWeight.Bold,
            )
            if (state.subtitle.isNotBlank()) {
                ChimahonDesktopText(
                    text = state.subtitle,
                    color = colors.secondaryContent,
                    size = 11,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        state.trailingActions.forEach { action ->
            ChimahonDesktopIconButton(
                icon = action.icon,
                contentDescription = action.title,
                selected = action.selected,
                enabled = action.enabled,
                loading = action.loading,
                style = action.style,
                onClick = { onAction(action) },
                colors = colors,
            )
        }
    }
}
