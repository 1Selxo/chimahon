package app.chimahon.shared.animeui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonAnimeCoverFrame(
    title: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    ratio: ChimahonAnimeCoverRatio = ChimahonAnimeCoverRatio.Poster,
    content: @Composable BoxScope.() -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = title)
    },
) {
    Box(
        modifier = modifier
            .semantics { contentDescription = title }
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) MaterialTheme.colors.primary else Color.Transparent,
                shape = RoundedCornerShape(6.dp),
            ),
    ) {
        content()
        ChimahonAnimeCoverScrim(modifier = Modifier.align(Alignment.BottomCenter))
        if (selected) {
            ChimahonAnimeSelectedMarker(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp),
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
                .align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun ChimahonAnimeCoverPlaceholder(
    title: String,
    modifier: Modifier = Modifier,
) {
    val initials = title
        .split(' ', '-', '_', '.', ':')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "A" }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.34f),
                        MaterialTheme.colors.secondary.copy(alpha = 0.24f),
                        MaterialTheme.colors.onSurface.copy(alpha = 0.10f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials.take(2),
            color = MaterialTheme.colors.onPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ChimahonAnimeCoverScrim(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.38f)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.46f)),
                ),
            ),
    )
}

@Composable
fun ChimahonAnimeSelectedMarker(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(24.dp),
        shape = CircleShape,
        color = MaterialTheme.colors.primary,
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Selected",
            tint = MaterialTheme.colors.onPrimary,
        )
    }
}

@Composable
fun ChimahonAnimeBadgeStack(
    badges: List<ChimahonAnimeBadgeUiModel>,
    modifier: Modifier = Modifier,
) {
    if (badges.isEmpty()) return
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.End,
    ) {
        badges.take(4).forEach { badge ->
            ChimahonAnimeBadge(badge = badge)
        }
    }
}

@Composable
fun ChimahonAnimeBadge(
    badge: ChimahonAnimeBadgeUiModel,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.semantics { contentDescription = badge.label },
        shape = CircleShape,
        color = badge.backgroundColor,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            text = badge.label,
            color = badge.contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ChimahonAnimeTinyChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val surfaceColor = if (selected) {
        MaterialTheme.colors.primary.copy(alpha = 0.16f)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.07f)
    }
    val textColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.70f)
    Surface(
        modifier = modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = CircleShape,
        color = surfaceColor,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            text = text,
            color = textColor,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ChimahonAnimeActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    valueLabel: String? = null,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = when {
                !enabled -> MaterialTheme.colors.onSurface.copy(alpha = 0.28f)
                selected -> MaterialTheme.colors.primary
                else -> MaterialTheme.colors.onSurface.copy(alpha = 0.70f)
            },
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = valueLabel ?: title,
            color = when {
                !enabled -> MaterialTheme.colors.onSurface.copy(alpha = 0.28f)
                selected -> MaterialTheme.colors.primary
                else -> MaterialTheme.colors.onSurface.copy(alpha = 0.76f)
            },
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ChimahonAnimePrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector = Icons.Filled.PlayArrow,
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ChimahonAnimeIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = when {
                !enabled -> MaterialTheme.colors.onSurface.copy(alpha = 0.28f)
                selected -> MaterialTheme.colors.primary
                else -> LocalContentColor.current.copy(alpha = 0.74f)
            },
        )
    }
}

@Composable
fun ChimahonAnimeLoadingState(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(14.dp))
            Text(
                text = title,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.66f),
                style = MaterialTheme.typography.body2,
            )
        }
    }
}

@Composable
fun ChimahonAnimeEmptyState(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                modifier = Modifier.size(58.dp),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colors.primary.copy(alpha = 0.13f),
            ) {
                Icon(
                    modifier = Modifier.padding(16.dp),
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                )
            }
            Text(
                text = title,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.62f),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
            if (actionLabel != null && onActionClick != null) {
                Spacer(Modifier.height(8.dp))
                ChimahonAnimePrimaryButton(
                    text = actionLabel,
                    onClick = onActionClick,
                    icon = Icons.Outlined.Refresh,
                )
            }
        }
    }
}

val ChimahonAnimeDownloadUiState.icon: ImageVector
    get() = when (this) {
        ChimahonAnimeDownloadUiState.NotDownloaded -> Icons.Outlined.Download
        ChimahonAnimeDownloadUiState.Queued -> Icons.Outlined.HourglassEmpty
        ChimahonAnimeDownloadUiState.Downloading -> Icons.Outlined.Download
        ChimahonAnimeDownloadUiState.Downloaded -> Icons.Outlined.CheckCircle
        ChimahonAnimeDownloadUiState.Error -> Icons.Outlined.ErrorOutline
    }

val ChimahonAnimePlayerActionId.icon: ImageVector
    get() = when (this) {
        ChimahonAnimePlayerActionId.Back -> Icons.Outlined.SkipPrevious
        ChimahonAnimePlayerActionId.PlayPause -> Icons.Outlined.PlayArrow
        ChimahonAnimePlayerActionId.SeekBackward -> Icons.Outlined.SkipPrevious
        ChimahonAnimePlayerActionId.SeekForward -> Icons.Outlined.SkipNext
        ChimahonAnimePlayerActionId.PreviousEpisode -> Icons.Outlined.SkipPrevious
        ChimahonAnimePlayerActionId.NextEpisode -> Icons.Outlined.SkipNext
        ChimahonAnimePlayerActionId.Playlist -> Icons.Outlined.MoreVert
        ChimahonAnimePlayerActionId.Subtitles -> Icons.Outlined.Subtitles
        ChimahonAnimePlayerActionId.Audio -> Icons.Outlined.VolumeUp
        ChimahonAnimePlayerActionId.Quality -> Icons.Outlined.Public
        ChimahonAnimePlayerActionId.Speed -> Icons.Outlined.Schedule
        ChimahonAnimePlayerActionId.VideoFilters -> Icons.Outlined.FilterList
        ChimahonAnimePlayerActionId.SleepTimer -> Icons.Outlined.Schedule
        ChimahonAnimePlayerActionId.OcrLookup -> Icons.Outlined.Search
        ChimahonAnimePlayerActionId.Screenshot -> Icons.Outlined.Download
        ChimahonAnimePlayerActionId.Cast -> Icons.Outlined.Public
        ChimahonAnimePlayerActionId.LockControls -> Icons.Outlined.Pause
        ChimahonAnimePlayerActionId.Settings -> Icons.Outlined.Settings
        ChimahonAnimePlayerActionId.Fullscreen -> Icons.Outlined.MoreVert
    }

val ChimahonAnimeHeaderUiModel.favoriteIcon: ImageVector
    get() = if (favorite) Icons.Filled.Favorite else Icons.Outlined.CheckCircle

val ChimahonAnimeHeaderUiModel.trackingIcon: ImageVector
    get() = if (trackedCount > 0) Icons.Outlined.CheckCircle else Icons.Outlined.Sync

val ChimahonAnimeHeaderUiModel.intervalIcon: ImageVector
    get() = Icons.Outlined.Schedule

val ChimahonAnimeHeaderUiModel.webIcon: ImageVector
    get() = Icons.Outlined.Public

val ChimahonAnimeEpisodeSort.icon: ImageVector
    get() = Icons.Outlined.Sort

val ChimahonAnimeBadgeUiModel.backgroundColor: Color
    @Composable
    get() = when (kind) {
        ChimahonAnimeBadgeKind.Unseen -> MaterialTheme.colors.primary
        ChimahonAnimeBadgeKind.Downloaded -> MaterialTheme.colors.secondary
        ChimahonAnimeBadgeKind.Local -> MaterialTheme.colors.onSurface.copy(alpha = 0.76f)
        ChimahonAnimeBadgeKind.Warning -> MaterialTheme.colors.error
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.72f)
    }

val ChimahonAnimeBadgeUiModel.contentColor: Color
    @Composable
    get() = when (kind) {
        ChimahonAnimeBadgeKind.Unseen -> MaterialTheme.colors.onPrimary
        ChimahonAnimeBadgeKind.Downloaded -> MaterialTheme.colors.onSecondary
        else -> Color.White
    }

@Composable
fun ChimahonAnimeDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
    )
}

@Composable
fun ChimahonAnimeSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.SemiBold,
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.58f),
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        trailing?.invoke()
    }
}
