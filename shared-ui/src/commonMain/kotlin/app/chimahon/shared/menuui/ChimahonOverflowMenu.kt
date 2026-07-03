package app.chimahon.shared.menuui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonOverflowIconButton(
    sections: List<ChimahonMenuSection>,
    onAction: (ChimahonMenuAction) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String = "More options",
    icon: ImageVector = Icons.Outlined.MoreVert,
    colors: ChimahonMenuColors = ChimahonMenuDefaults.colors(),
    compact: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(
        modifier = modifier,
        enabled = enabled && sections.any { it.visibleActions.isNotEmpty() },
        onClick = { expanded = true },
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
    ChimahonOverflowMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        sections = sections,
        onAction = { action ->
            onAction(action)
            if (action.closeMenuOnClick) {
                expanded = false
            }
        },
        colors = colors,
        compact = compact,
    )
}

@Composable
fun ChimahonOverflowMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    sections: List<ChimahonMenuSection>,
    onAction: (ChimahonMenuAction) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMenuColors = ChimahonMenuDefaults.colors(),
    compact: Boolean = true,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier.widthIn(min = 220.dp, max = 360.dp),
    ) {
        sections.withIndex()
            .filter { it.value.visibleActions.isNotEmpty() }
            .forEach { indexed ->
                val section = indexed.value
                if (indexed.index > 0) {
                    Divider(color = colors.divider)
                }
                section.title?.takeIf { it.isNotBlank() }?.let { title ->
                    DropdownMenuSectionLabel(title = title, colors = colors)
                }
                section.visibleActions.forEach { action ->
                    DropdownMenuItem(
                        enabled = action.clickable,
                        onClick = { onAction(action) },
                    ) {
                        ChimahonMenuActionRow(
                            modifier = Modifier.weight(1f),
                            action = action,
                            onAction = onAction,
                            colors = colors,
                            compact = compact,
                            showSupportingText = !compact,
                            interactive = false,
                        )
                    }
                }
            }
    }
}

@Composable
fun ChimahonOverflowColumn(
    sections: List<ChimahonMenuSection>,
    onAction: (ChimahonMenuAction) -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMenuColors = ChimahonMenuDefaults.colors(),
    compact: Boolean = false,
) {
    Column(modifier = modifier) {
        sections.withIndex()
            .filter { it.value.visibleActions.isNotEmpty() }
            .forEach { indexed ->
                val section = indexed.value
                if (indexed.index > 0) {
                    Divider(color = colors.divider)
                }
                section.title?.takeIf { it.isNotBlank() }?.let { title ->
                    ChimahonMenuSectionHeader(title = title, colors = colors)
                }
                section.visibleActions.forEach { action ->
                    ChimahonMenuActionRow(
                        action = action,
                        onAction = onAction,
                        colors = colors,
                        compact = compact,
                    )
                }
            }
    }
}

@Composable
private fun DropdownMenuSectionLabel(
    title: String,
    colors: ChimahonMenuColors,
) {
    Text(
        text = title,
        color = colors.secondaryText,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
}
