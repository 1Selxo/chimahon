package app.chimahon.shared.animequeueui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonAnimeQueueStatusChip(
    chip: ChimahonAnimeQueueStatusChipUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = chimahonAnimeQueueChipColors(chip.kind)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = colors.background,
        contentColor = colors.content,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            text = chip.text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ChimahonAnimeQueueBadge(
    badge: ChimahonAnimeQueueBadgeUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = chimahonAnimeQueueBadgeColors(badge.kind)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = colors.background,
        contentColor = colors.content,
        elevation = 0.dp,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            text = badge.text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ChimahonAnimeQueueBadgeRail(
    badges: List<ChimahonAnimeQueueBadgeUiModel>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    if (badges.isEmpty()) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        badges.forEach { badge ->
            ChimahonAnimeQueueBadge(badge = badge)
        }
    }
}

@Composable
fun ChimahonAnimeQueueProgressPanel(
    progress: ChimahonAnimeQueueProgress,
    modifier: Modifier = Modifier,
    status: ChimahonAnimeQueueStatusChipUiModel? = null,
    showBar: Boolean = true,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.05f),
        contentColor = MaterialTheme.colors.onSurface,
    ) {
        Box(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (status != null) {
                    ChimahonAnimeQueueStatusChip(chip = status)
                }
                Text(
                    modifier = Modifier.weight(1f),
                    text = progress.statusLabel ?: progress.displayPercentLabel,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (progress.statusLabel != null) {
                    Text(
                        text = progress.displayPercentLabel,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.56f),
                        fontSize = 12.sp,
                        maxLines = 1,
                    )
                }
            }
            if (showBar) {
                Spacer(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(top = 34.dp)
                        .height(3.dp),
                )
                val progressModifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                if (progress.indeterminate) {
                    LinearProgressIndicator(modifier = progressModifier)
                } else {
                    LinearProgressIndicator(
                        modifier = progressModifier,
                        progress = progress.normalizedFraction,
                    )
                }
            }
        }
    }
}

@Composable
fun ChimahonAnimeQueueEpisodeCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Checkbox(
        modifier = modifier.size(48.dp),
        checked = checked,
        enabled = enabled,
        onCheckedChange = onCheckedChange,
    )
}

@Composable
fun ChimahonAnimeQueueCover(
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
    width: Dp = 54.dp,
    ratio: Float = 2f / 3f,
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonAnimeQueueCoverPlaceholder() },
) {
    Box(
        modifier = modifier
            .width(width)
            .aspectRatio(ratio)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center,
    ) {
        coverContent(thumbnailUrl)
    }
}

@Composable
fun ChimahonAnimeQueueCoverPlaceholder(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Movie,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.32f),
        )
    }
}

@Composable
fun ChimahonAnimeQueueOverflowMenu(
    actions: List<ChimahonAnimeQueueOverflowAction>,
    modifier: Modifier = Modifier,
    contentDescription: String = "More actions",
) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(
        modifier = modifier,
        onClick = { expanded = true },
        enabled = actions.isNotEmpty(),
    ) {
        Icon(Icons.Outlined.MoreVert, contentDescription = contentDescription)
    }
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
                ChimahonAnimeQueueActionMenuContent(action = action)
            }
        }
    }
}

@Composable
fun ChimahonAnimeQueueActionMenuContent(
    action: ChimahonAnimeQueueOverflowAction,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (action.destructive) {
        MaterialTheme.colors.error
    } else {
        Color.Unspecified
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = action.icon.imageVector,
            contentDescription = null,
            tint = contentColor,
        )
        Text(
            text = action.label,
            color = contentColor,
        )
    }
}

@Composable
fun ChimahonAnimeQueueIconButton(
    icon: ChimahonAnimeQueueActionIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    destructive: Boolean = false,
    active: Boolean = false,
) {
    val tint = when {
        destructive -> MaterialTheme.colors.error
        active -> MaterialTheme.colors.primary
        else -> Color.Unspecified
    }
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
    ) {
        Icon(
            imageVector = icon.imageVector,
            contentDescription = contentDescription,
            tint = tint,
        )
    }
}

@Composable
fun ChimahonAnimeQueueInlineError(
    error: ChimahonAnimeQueueError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colors.error.copy(alpha = 0.10f),
        contentColor = MaterialTheme.colors.error,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ChimahonAnimeQueueActionIcon.Error.imageVector,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = error.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = error.message,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            TextButton(
                enabled = error.canRetry,
                onClick = onRetry,
            ) {
                Text(error.retryLabel)
            }
        }
    }
}

@Composable
internal fun ChimahonAnimeQueueRowSurface(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colors.surface
        },
        elevation = if (selected) 1.dp else 0.dp,
    ) {
        content()
    }
}

@Composable
internal fun chimahonAnimeQueueChipColors(kind: ChimahonAnimeQueueStatusChipKind): ChimahonAnimeQueueColors {
    return when (kind) {
        ChimahonAnimeQueueStatusChipKind.Info -> ChimahonAnimeQueueColors(
            background = MaterialTheme.colors.primary.copy(alpha = 0.12f),
            content = MaterialTheme.colors.primary,
        )
        ChimahonAnimeQueueStatusChipKind.Active -> ChimahonAnimeQueueColors(
            background = Color(0xFF2E7D32).copy(alpha = 0.14f),
            content = Color(0xFF2E7D32),
        )
        ChimahonAnimeQueueStatusChipKind.Success -> ChimahonAnimeQueueColors(
            background = Color(0xFF00897B).copy(alpha = 0.14f),
            content = Color(0xFF00796B),
        )
        ChimahonAnimeQueueStatusChipKind.Warning -> ChimahonAnimeQueueColors(
            background = Color(0xFFF9A825).copy(alpha = 0.18f),
            content = Color(0xFF8A6500),
        )
        ChimahonAnimeQueueStatusChipKind.Error -> ChimahonAnimeQueueColors(
            background = MaterialTheme.colors.error.copy(alpha = 0.14f),
            content = MaterialTheme.colors.error,
        )
        ChimahonAnimeQueueStatusChipKind.Muted -> ChimahonAnimeQueueColors(
            background = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
            content = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
        )
    }
}

@Composable
internal fun chimahonAnimeQueueBadgeColors(kind: ChimahonAnimeQueueBadgeKind): ChimahonAnimeQueueColors {
    return when (kind) {
        ChimahonAnimeQueueBadgeKind.Info -> ChimahonAnimeQueueColors(
            background = MaterialTheme.colors.primary.copy(alpha = 0.10f),
            content = MaterialTheme.colors.primary,
        )
        ChimahonAnimeQueueBadgeKind.Active -> ChimahonAnimeQueueColors(
            background = Color(0xFF2E7D32).copy(alpha = 0.12f),
            content = Color(0xFF2E7D32),
        )
        ChimahonAnimeQueueBadgeKind.Success -> ChimahonAnimeQueueColors(
            background = Color(0xFF00897B).copy(alpha = 0.12f),
            content = Color(0xFF00796B),
        )
        ChimahonAnimeQueueBadgeKind.Warning -> ChimahonAnimeQueueColors(
            background = Color(0xFFF9A825).copy(alpha = 0.16f),
            content = Color(0xFF8A6500),
        )
        ChimahonAnimeQueueBadgeKind.Error -> ChimahonAnimeQueueColors(
            background = MaterialTheme.colors.error.copy(alpha = 0.12f),
            content = MaterialTheme.colors.error,
        )
        ChimahonAnimeQueueBadgeKind.Muted -> ChimahonAnimeQueueColors(
            background = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
            content = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
        )
        ChimahonAnimeQueueBadgeKind.Local -> ChimahonAnimeQueueColors(
            background = Color(0xFF455A64).copy(alpha = 0.14f),
            content = Color(0xFF455A64),
        )
        ChimahonAnimeQueueBadgeKind.Stream -> ChimahonAnimeQueueColors(
            background = Color(0xFF1565C0).copy(alpha = 0.12f),
            content = Color(0xFF1565C0),
        )
    }
}

internal data class ChimahonAnimeQueueColors(
    val background: Color,
    val content: Color,
)
