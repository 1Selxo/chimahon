package app.chimahon.shared.detailui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonDetailCollapsingHeader(
    metadata: ChimahonDetailCollapsingHeaderMetadata,
    collapseState: ChimahonDetailCollapsingHeaderState,
    modifier: Modifier = Modifier,
    onCoverClick: () -> Unit = {},
    onActionClick: (ChimahonDetailAction) -> Unit = {},
    onBadgeClick: (ChimahonDetailParityBadgeModel) -> Unit = {},
    onGenreClick: (String) -> Unit = {},
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonDetailParityCoverPlaceholder(title = metadata.title)
    },
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.background,
        elevation = if (collapseState.isCollapsed) 4.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier.background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.12f * collapseState.normalizedExpandedFraction),
                        MaterialTheme.colors.background,
                    ),
                ),
            ),
        ) {
            if (collapseState.isCollapsed) {
                ChimahonDetailCollapsedHeaderRow(
                    metadata = metadata,
                    onActionClick = onActionClick,
                )
            } else {
                ChimahonDetailExpandedHeader(
                    metadata = metadata,
                    expandedFraction = collapseState.normalizedExpandedFraction,
                    onCoverClick = onCoverClick,
                    onActionClick = onActionClick,
                    coverContent = coverContent,
                )
                ChimahonDetailParityBadgeRow(
                    badges = metadata.badges,
                    onBadgeClick = onBadgeClick,
                    modifier = Modifier.padding(top = 4.dp),
                )
                if (metadata.genres.isNotEmpty()) {
                    ChimahonDetailParityBadgeRow(
                        badges = metadata.genres.map {
                            ChimahonDetailParityBadgeModel(it, ChimahonDetailParityBadgeKind.Genre)
                        },
                        onBadgeClick = { onGenreClick(it.label) },
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ChimahonDetailParityActionRow(
    state: ChimahonDetailActionRowState,
    onActionClick: (ChimahonDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.visibleActions.isEmpty()) return
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.background.copy(alpha = 0.95f),
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            state.visibleActions.take(6).forEach { action ->
                ChimahonDetailParityActionButton(
                    model = action,
                    onClick = { onActionClick(action.action) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun ChimahonDetailChapterBulkActionBar(
    state: ChimahonDetailChapterBulkBarState,
    onActionClick: (ChimahonDetailChapterBulkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.actions.isEmpty()) return
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (state.hasSelection) {
            MaterialTheme.colors.primary.copy(alpha = 0.10f)
        } else {
            MaterialTheme.colors.surface
        },
        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.10f)),
        elevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = state.title,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.subtitle2,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (state.hasSelection) {
                    Text(
                        text = "${state.selectedCount}/${state.totalCount.coerceAtLeast(state.selectedCount)}",
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                items(state.actions, key = { "detail-parity-bulk:${it.action}" }) { action ->
                    ChimahonDetailParityBulkChip(
                        action = action,
                        onClick = { onActionClick(action.action) },
                    )
                }
            }
        }
    }
}

@Composable
fun ChimahonDetailChapterBadgeRow(
    badges: List<ChimahonDetailChapterBadgeModel>,
    modifier: Modifier = Modifier,
) {
    if (badges.isEmpty()) return
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(badges, key = { it.stableKey }) { badge ->
            ChimahonDetailChapterBadge(badge = badge)
        }
    }
}

@Composable
fun ChimahonDetailParityBadgeRow(
    badges: List<ChimahonDetailParityBadgeModel>,
    modifier: Modifier = Modifier,
    onBadgeClick: (ChimahonDetailParityBadgeModel) -> Unit = {},
) {
    if (badges.isEmpty()) return
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(badges, key = { it.stableKey }) { badge ->
            ChimahonDetailParityBadge(
                badge = badge,
                onClick = { onBadgeClick(badge) },
            )
        }
    }
}

@Composable
fun BoxScope.ChimahonDetailParityCoverPlaceholder(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .matchParentSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.32f),
                        MaterialTheme.colors.onSurface.copy(alpha = 0.14f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title.firstOrNull()?.uppercaseChar()?.toString() ?: "M",
            color = MaterialTheme.colors.onPrimary,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ChimahonDetailExpandedHeader(
    metadata: ChimahonDetailCollapsingHeaderMetadata,
    expandedFraction: Float,
    onCoverClick: () -> Unit,
    onActionClick: (ChimahonDetailAction) -> Unit,
    coverContent: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val wide = maxWidth >= 560.dp
        if (wide) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .alpha(expandedFraction.coerceIn(0.35f, 1f)),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChimahonDetailParityCover(
                    title = metadata.title,
                    onClick = onCoverClick,
                    modifier = Modifier.width(148.dp),
                    content = coverContent,
                )
                ChimahonDetailParityTitleBlock(
                    metadata = metadata,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
                    .alpha(expandedFraction.coerceIn(0.35f, 1f)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ChimahonDetailParityCover(
                    title = metadata.title,
                    onClick = onCoverClick,
                    modifier = Modifier.width(126.dp),
                    content = coverContent,
                )
                ChimahonDetailParityTitleBlock(
                    metadata = metadata,
                    centered = true,
                    modifier = Modifier.padding(top = 14.dp),
                )
            }
        }
        ChimahonDetailParityActionRow(
            state = ChimahonDetailActionRowState(
                primaryAction = metadata.actions.firstOrNull {
                    it.action == ChimahonDetailAction.Resume || it.action == ChimahonDetailAction.Start
                },
                actions = metadata.actions,
            ),
            onActionClick = onActionClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp),
        )
    }
}

@Composable
private fun ChimahonDetailCollapsedHeaderRow(
    metadata: ChimahonDetailCollapsingHeaderMetadata,
    onActionClick: (ChimahonDetailAction) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = metadata.title,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = metadata.collapsedSubtitle,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.64f),
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        metadata.actions
            .firstOrNull { it.action == ChimahonDetailAction.Resume || it.action == ChimahonDetailAction.Start }
            ?.let { action ->
                Button(
                    onClick = { onActionClick(action.action) },
                    enabled = action.enabled,
                    contentPadding = PaddingValues(horizontal = 12.dp),
                ) {
                    Icon(
                        imageVector = action.action.parityIcon(),
                        contentDescription = action.label,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(action.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
    }
}

@Composable
private fun ChimahonDetailParityCover(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
            .clickable(onClick = onClick),
        content = content,
    )
}

@Composable
private fun ChimahonDetailParityTitleBlock(
    metadata: ChimahonDetailCollapsingHeaderMetadata,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
) {
    val alignment = if (centered) TextAlign.Center else TextAlign.Start
    Column(
        modifier = modifier,
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        Text(
            text = metadata.title,
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
            textAlign = alignment,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = metadata.subtitle,
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.68f),
            style = MaterialTheme.typography.body2,
            textAlign = alignment,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (metadata.summaryLabel.isNotBlank()) {
            Text(
                text = metadata.summaryLabel,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.64f),
                style = MaterialTheme.typography.caption,
                textAlign = alignment,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonDetailParityActionButton(
    model: ChimahonDetailActionModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeColor = MaterialTheme.colors.primary
    val inactiveColor = MaterialTheme.colors.onSurface.copy(alpha = 0.60f)
    val color = if (model.selected) activeColor else inactiveColor
    Column(
        modifier = modifier
            .height(58.dp)
            .alpha(if (model.enabled) 1f else 0.42f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = model.enabled, onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = model.action.parityIcon(),
            contentDescription = model.label,
            tint = color,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = model.label,
            modifier = Modifier.padding(top = 3.dp),
            color = color,
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        model.supportingLabel?.takeIf(String::isNotBlank)?.let {
            Text(
                text = it,
                color = color.copy(alpha = 0.76f),
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonDetailParityBulkChip(
    action: ChimahonDetailChapterBulkActionModel,
    onClick: () -> Unit,
) {
    val tint = when {
        !action.enabled -> MaterialTheme.colors.onSurface.copy(alpha = 0.34f)
        action.selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.72f)
    }
    Surface(
        modifier = Modifier.clickable(enabled = action.enabled, onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (action.selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = 0.06f)
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = action.action.parityIcon(),
                contentDescription = action.label,
                modifier = Modifier.size(16.dp),
                tint = tint,
            )
            Text(
                text = action.label,
                color = tint,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonDetailParityBadge(
    badge: ChimahonDetailParityBadgeModel,
    onClick: () -> Unit,
) {
    val colors = badge.kind.badgeColors()
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = CircleShape,
        color = colors.first,
        contentColor = colors.second,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            badge.kind.icon()?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp),
                )
            }
            Text(
                text = badge.label,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonDetailChapterBadge(
    badge: ChimahonDetailChapterBadgeModel,
) {
    val colors = badge.kind.badgeColors()
    Surface(
        shape = CircleShape,
        color = colors.first,
        contentColor = colors.second,
    ) {
        Column(modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                badge.kind.icon()?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                    )
                }
                Text(
                    text = badge.label,
                    style = MaterialTheme.typography.caption,
                    fontWeight = if (badge.kind.isStrongState()) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (badge.progressPercent != null) {
                LinearProgressIndicator(
                    progress = badge.progressPercent.coerceIn(0, 100) / 100f,
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .width(42.dp)
                        .height(2.dp),
                )
            }
        }
    }
}

@Composable
private fun ChimahonDetailParityBadgeKind.badgeColors(): Pair<Color, Color> {
    return when (this) {
        ChimahonDetailParityBadgeKind.Status,
        ChimahonDetailParityBadgeKind.Library,
        ChimahonDetailParityBadgeKind.Genre,
        -> MaterialTheme.colors.primary.copy(alpha = 0.11f) to MaterialTheme.colors.primary
        ChimahonDetailParityBadgeKind.Unread,
        ChimahonDetailParityBadgeKind.Update,
        -> MaterialTheme.colors.secondary.copy(alpha = 0.14f) to MaterialTheme.colors.secondary
        ChimahonDetailParityBadgeKind.Downloaded ->
            Color(0xFF1B7F4A).copy(alpha = 0.16f) to Color(0xFF1B7F4A)
        ChimahonDetailParityBadgeKind.Warning ->
            MaterialTheme.colors.error.copy(alpha = 0.12f) to MaterialTheme.colors.error
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.07f) to MaterialTheme.colors.onSurface.copy(alpha = 0.72f)
    }
}

@Composable
private fun ChimahonDetailChapterBadgeKind.badgeColors(): Pair<Color, Color> {
    return when (this) {
        ChimahonDetailChapterBadgeKind.Unread,
        ChimahonDetailChapterBadgeKind.Bookmarked,
        -> MaterialTheme.colors.primary.copy(alpha = 0.11f) to MaterialTheme.colors.primary
        ChimahonDetailChapterBadgeKind.Downloaded ->
            Color(0xFF1B7F4A).copy(alpha = 0.16f) to Color(0xFF1B7F4A)
        ChimahonDetailChapterBadgeKind.DownloadError ->
            MaterialTheme.colors.error.copy(alpha = 0.12f) to MaterialTheme.colors.error
        ChimahonDetailChapterBadgeKind.Downloading,
        ChimahonDetailChapterBadgeKind.DownloadQueued,
        -> MaterialTheme.colors.secondary.copy(alpha = 0.14f) to MaterialTheme.colors.secondary
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.07f) to MaterialTheme.colors.onSurface.copy(alpha = 0.72f)
    }
}

private fun ChimahonDetailParityBadgeKind.icon(): ImageVector? {
    return when (this) {
        ChimahonDetailParityBadgeKind.Library -> Icons.Filled.Favorite
        ChimahonDetailParityBadgeKind.ChapterCount -> Icons.Outlined.FilterList
        ChimahonDetailParityBadgeKind.Unread -> Icons.Outlined.NewReleases
        ChimahonDetailParityBadgeKind.Bookmarked -> Icons.Filled.Bookmark
        ChimahonDetailParityBadgeKind.Downloaded -> Icons.Outlined.CloudDownload
        ChimahonDetailParityBadgeKind.Update -> Icons.Outlined.Schedule
        ChimahonDetailParityBadgeKind.History -> Icons.Outlined.History
        ChimahonDetailParityBadgeKind.Warning -> Icons.Outlined.ErrorOutline
        ChimahonDetailParityBadgeKind.Url -> Icons.Outlined.OpenInBrowser
        else -> null
    }
}

private fun ChimahonDetailChapterBadgeKind.icon(): ImageVector? {
    return when (this) {
        ChimahonDetailChapterBadgeKind.Unread -> Icons.Outlined.NewReleases
        ChimahonDetailChapterBadgeKind.Read -> Icons.Outlined.CheckCircle
        ChimahonDetailChapterBadgeKind.Started -> Icons.Outlined.PlayArrow
        ChimahonDetailChapterBadgeKind.Bookmarked -> Icons.Filled.Bookmark
        ChimahonDetailChapterBadgeKind.DownloadQueued -> Icons.Outlined.HourglassEmpty
        ChimahonDetailChapterBadgeKind.Downloading -> Icons.Outlined.CloudDownload
        ChimahonDetailChapterBadgeKind.Downloaded -> Icons.Outlined.CheckCircle
        ChimahonDetailChapterBadgeKind.DownloadError -> Icons.Outlined.ErrorOutline
        else -> null
    }
}

private fun ChimahonDetailChapterBadgeKind.isStrongState(): Boolean {
    return this in setOf(
        ChimahonDetailChapterBadgeKind.Unread,
        ChimahonDetailChapterBadgeKind.Bookmarked,
        ChimahonDetailChapterBadgeKind.Downloaded,
        ChimahonDetailChapterBadgeKind.DownloadError,
    )
}

private fun ChimahonDetailAction.parityIcon(): ImageVector {
    return when (this) {
        ChimahonDetailAction.Favorite -> Icons.Filled.Favorite
        ChimahonDetailAction.Start -> Icons.Outlined.PlayArrow
        ChimahonDetailAction.Resume -> Icons.Outlined.PlayArrow
        ChimahonDetailAction.Tracking -> Icons.Outlined.Sync
        ChimahonDetailAction.Web -> Icons.Outlined.Public
        ChimahonDetailAction.Refresh -> Icons.Outlined.Refresh
        ChimahonDetailAction.Download -> Icons.Outlined.CloudDownload
        ChimahonDetailAction.MarkRead -> Icons.Outlined.CheckCircle
        ChimahonDetailAction.MarkUnread -> Icons.Outlined.CheckCircle
        ChimahonDetailAction.Bookmark -> Icons.Outlined.BookmarkBorder
        ChimahonDetailAction.Select -> Icons.Outlined.CheckCircle
        ChimahonDetailAction.AddToLibrary -> Icons.Outlined.LibraryAdd
    }
}

private fun ChimahonDetailChapterBulkAction.parityIcon(): ImageVector {
    return when (this) {
        ChimahonDetailChapterBulkAction.Download -> Icons.Outlined.CloudDownload
        ChimahonDetailChapterBulkAction.MarkRead -> Icons.Outlined.CheckCircle
        ChimahonDetailChapterBulkAction.MarkUnread -> Icons.Outlined.CheckCircle
        ChimahonDetailChapterBulkAction.Bookmark -> Icons.Outlined.BookmarkBorder
        ChimahonDetailChapterBulkAction.RemoveBookmark -> Icons.Outlined.BookmarkBorder
        ChimahonDetailChapterBulkAction.SelectAll -> Icons.Outlined.CheckCircle
        ChimahonDetailChapterBulkAction.ClearSelection -> Icons.Outlined.Close
    }
}
