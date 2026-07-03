package app.chimahon.shared.animequeueui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonAnimeEpisodeBatchTopAppBar(
    state: ChimahonAnimeEpisodeBatchActionBarState,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier,
    onSelectAll: (() -> Unit)? = null,
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 4.dp,
        navigationIcon = {
            IconButton(onClick = onClearSelection) {
                Icon(
                    imageVector = ChimahonAnimeQueueActionIcon.ClearSelection.imageVector,
                    contentDescription = "Clear selection",
                )
            }
        },
        title = {
            Text(
                text = state.title,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            onSelectAll?.let {
                IconButton(onClick = it, enabled = state.totalCount > 0) {
                    Icon(
                        imageVector = ChimahonAnimeQueueActionIcon.SelectAll.imageVector,
                        contentDescription = if (state.allSelected) "Deselect all" else "Select all",
                    )
                }
            }
            state.primaryActions.forEach { action ->
                ChimahonAnimeEpisodeBatchIconAction(action = action)
            }
            ChimahonAnimeQueueOverflowMenu(actions = state.overflowActions)
        },
    )
}

@Composable
fun ChimahonAnimeEpisodeBottomBatchActionBar(
    state: ChimahonAnimeEpisodeBatchActionBarState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.title,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.weight(1f))
            state.primaryActions.forEach { action ->
                ChimahonAnimeEpisodeBatchIconAction(action = action)
            }
            ChimahonAnimeQueueOverflowMenu(actions = state.overflowActions)
        }
    }
}

@Composable
fun ChimahonAnimeEpisodeBatchActionRail(
    actions: List<ChimahonAnimeEpisodeBatchAction>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        actions.forEach { action ->
            ChimahonAnimeEpisodeBatchIconAction(action = action)
        }
    }
}

@Composable
fun ChimahonAnimeEpisodeBatchIconAction(
    action: ChimahonAnimeEpisodeBatchAction,
    modifier: Modifier = Modifier,
) {
    val tint = if (action.destructive) MaterialTheme.colors.error else Color.Unspecified
    IconButton(
        modifier = modifier,
        onClick = action.onClick,
        enabled = action.enabled,
    ) {
        Icon(
            imageVector = action.icon.imageVector,
            contentDescription = action.label,
            tint = tint,
        )
    }
}
