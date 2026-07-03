package app.chimahon.shared.moreui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonMoreBackupActionRow(
    state: ChimahonMoreBackupActionState,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    ChimahonMoreListItem(
        title = state.title,
        subtitle = state.subtitle,
        icon = state.icon,
        warning = state.warning,
        trailing = {
            ChimahonMoreActionPill(
                action = state.action,
                onClick = onAction,
                colors = colors,
            )
        },
        modifier = modifier,
        colors = colors,
    )
}

@Composable
fun ChimahonMoreBackupScopeGrid(
    items: List<ChimahonMoreBackupScopeItem>,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(220.dp),
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items, key = { it.key }) { item ->
            ChimahonMoreBackupScopeCard(item = item, colors = colors)
        }
    }
}

@Composable
fun ChimahonMoreBackupScopeCard(
    item: ChimahonMoreBackupScopeItem,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    ChimahonMoreCard(modifier = modifier, colors = colors) {
        Column(modifier = Modifier.padding(14.dp)) {
            ChimahonMoreIconTile(
                icon = item.icon,
                active = item.included,
                warning = item.warning,
                colors = colors,
            )
            ChimahonMoreLabel(
                text = item.title,
                color = colors.content,
                size = 14,
                weight = FontWeight.SemiBold,
                maxLines = 2,
                modifier = Modifier.padding(top = 10.dp),
            )
            ChimahonMoreLabel(
                text = item.subtitle,
                color = colors.secondaryContent,
                size = 11,
                lineHeight = 16,
                maxLines = 3,
                modifier = Modifier.padding(top = 3.dp),
            )
            item.countLabel?.let {
                ChimahonMoreStatusPillView(
                    pill = ChimahonMoreStatusPill(it, active = item.included, warning = item.warning),
                    colors = colors,
                    modifier = Modifier.padding(top = 9.dp),
                )
            }
        }
    }
}

@Composable
fun ChimahonMoreBackupCheckpointRow(
    title: String,
    subtitle: String,
    complete: Boolean,
    modifier: Modifier = Modifier,
    colors: ChimahonMoreColors = ChimahonMoreDefaults.colors(),
) {
    ChimahonMoreListItem(
        title = title,
        subtitle = subtitle,
        icon = if (complete) ChimahonMoreIcon.Check else ChimahonMoreIcon.Warning,
        selected = complete,
        warning = !complete,
        value = if (complete) "Ready" else "Missing",
        modifier = modifier,
        colors = colors,
    )
}
