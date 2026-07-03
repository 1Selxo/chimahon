package app.chimahon.shared.mangaui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonMangaChapterList(
    chapters: List<ChimahonMangaChapterUiModel>,
    displayMode: ChimahonMangaChapterDisplayMode,
    onChapterClick: (ChimahonMangaChapterUiModel) -> Unit,
    modifier: Modifier = Modifier,
    selectedChapterIds: Set<String> = emptySet(),
    onChapterLongClick: (ChimahonMangaChapterUiModel) -> Unit = {},
    onChapterDownloadClick: (ChimahonMangaChapterUiModel) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(vertical = 4.dp),
    onChapterReadClick: (ChimahonMangaChapterUiModel) -> Unit = {},
    onChapterBookmarkClick: (ChimahonMangaChapterUiModel) -> Unit = {},
    onChapterSelectClick: (ChimahonMangaChapterUiModel) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        items(
            items = chapters.withIndex().toList(),
            key = { indexed -> indexed.value.stableLazyKey(indexed.index) },
        ) { indexed ->
            val chapter = indexed.value
            ChimahonMangaChapterRow(
                chapter = chapter,
                displayMode = displayMode,
                selected = chapter.id in selectedChapterIds,
                onClick = { onChapterClick(chapter) },
                onLongClick = { onChapterLongClick(chapter) },
                onDownloadClick = { onChapterDownloadClick(chapter) },
                onReadClick = { onChapterReadClick(chapter) },
                onBookmarkClick = { onChapterBookmarkClick(chapter) },
                onSelectClick = { onChapterSelectClick(chapter) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonMangaChapterRow(
    chapter: ChimahonMangaChapterUiModel,
    displayMode: ChimahonMangaChapterDisplayMode,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
    onReadClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (selected) {
                    MaterialTheme.colors.primary.copy(alpha = 0.12f)
                } else {
                    Color.Transparent
                },
            )
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!chapter.read) {
                    Icon(
                        imageVector = Icons.Filled.Circle,
                        contentDescription = "Unread",
                        modifier = Modifier.size(8.dp),
                        tint = MaterialTheme.colors.primary,
                    )
                }
                if (chapter.bookmarked) {
                    Icon(
                        imageVector = Icons.Filled.Bookmark,
                        contentDescription = "Bookmarked",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colors.primary,
                    )
                }
                Text(
                    text = chapter.displayTitle(displayMode),
                    color = LocalContentColor.current.copy(alpha = if (chapter.read) 0.45f else 1f),
                    style = MaterialTheme.typography.body2,
                    fontWeight = if (chapter.read) FontWeight.Normal else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            ChimahonChapterMetadataLine(chapter = chapter)
            ChimahonChapterBadgeRow(chapter = chapter)
        }
        ChimahonChapterRowActionButtons(
            chapter = chapter,
            selected = selected,
            onDownloadClick = onDownloadClick,
            onReadClick = onReadClick,
            onBookmarkClick = onBookmarkClick,
            onSelectClick = onSelectClick,
        )
    }
}

@Composable
private fun ChimahonChapterMetadataLine(chapter: ChimahonMangaChapterUiModel) {
    val parts = chapter.metadataParts()
    if (parts.isEmpty()) return

    Row(verticalAlignment = Alignment.CenterVertically) {
        parts.forEachIndexed { index, part ->
            if (index > 0) {
                Text(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    text = "/",
                    color = LocalContentColor.current.copy(alpha = if (chapter.read) 0.35f else 0.55f),
                    fontSize = 11.sp,
                )
            }
            Text(
                text = part,
                color = LocalContentColor.current.copy(alpha = if (chapter.read) 0.35f else 0.62f),
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonChapterBadgeRow(chapter: ChimahonMangaChapterUiModel) {
    val badges = buildList {
        if (chapter.sourceOrder != Int.MAX_VALUE) {
            add("Source #${chapter.sourceOrder.coerceAtLeast(0)}")
        }
        if (chapter.downloaded || chapter.downloadState == ChimahonChapterDownloadUiState.Downloaded) {
            add("Downloaded")
        }
        if (chapter.isOcrReady) add("OCR")
        if (chapter.isOcrRunning) add("OCR running")
    }
    if (badges.isEmpty()) return

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        badges.forEach { badge ->
            ChimahonChapterTinyBadge(text = badge)
        }
    }
}

@Composable
private fun ChimahonChapterRowActionButtons(
    chapter: ChimahonMangaChapterUiModel,
    selected: Boolean,
    onDownloadClick: () -> Unit,
    onReadClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onSelectClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        chapter.rowActions(selected).forEach { action ->
            ChimahonChapterCompactActionButton(
                action = action,
                chapter = chapter,
                onClick = when (action.action) {
                    ChimahonMangaChapterRowAction.Download -> onDownloadClick
                    ChimahonMangaChapterRowAction.MarkRead,
                    ChimahonMangaChapterRowAction.MarkUnread,
                    -> onReadClick
                    ChimahonMangaChapterRowAction.Bookmark,
                    ChimahonMangaChapterRowAction.RemoveBookmark,
                    -> onBookmarkClick
                    ChimahonMangaChapterRowAction.Select -> onSelectClick
                },
            )
        }
    }
}

@Composable
private fun ChimahonChapterCompactActionButton(
    action: ChimahonMangaChapterRowActionModel,
    chapter: ChimahonMangaChapterUiModel,
    onClick: () -> Unit,
) {
    val tint = when {
        !action.enabled -> MaterialTheme.colors.onSurface.copy(alpha = 0.32f)
        action.action == ChimahonMangaChapterRowAction.Download -> chapter.downloadState.tint
        action.selected -> MaterialTheme.colors.primary
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.56f)
    }
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (action.selected) {
                    MaterialTheme.colors.primary.copy(alpha = 0.10f)
                } else {
                    Color.Transparent
                },
            )
            .clickable(enabled = action.enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = action.icon(chapter),
            contentDescription = action.label,
            modifier = Modifier.size(19.dp),
            tint = tint,
        )
        if (action.action == ChimahonMangaChapterRowAction.Download &&
            chapter.downloadState == ChimahonChapterDownloadUiState.Downloading
        ) {
            LinearProgressIndicator(
                progress = (chapter.downloadProgress.coerceIn(0, 100) / 100f),
                modifier = Modifier
                    .width(26.dp)
                    .padding(top = 24.dp),
            )
        }
    }
}

@Composable
private fun ChimahonChapterTinyBadge(text: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.07f),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            text = text,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val ChimahonChapterDownloadUiState.icon: ImageVector
    get() = when (this) {
        ChimahonChapterDownloadUiState.NotDownloaded -> Icons.Outlined.Download
        ChimahonChapterDownloadUiState.Queued -> Icons.Outlined.HourglassEmpty
        ChimahonChapterDownloadUiState.Downloading -> Icons.Outlined.Download
        ChimahonChapterDownloadUiState.Downloaded -> Icons.Outlined.CheckCircle
        ChimahonChapterDownloadUiState.Error -> Icons.Outlined.ErrorOutline
    }

private val ChimahonChapterDownloadUiState.tint: Color
    @Composable
    get() = when (this) {
        ChimahonChapterDownloadUiState.Downloaded -> MaterialTheme.colors.primary
        ChimahonChapterDownloadUiState.Error -> MaterialTheme.colors.error
        else -> MaterialTheme.colors.onSurface.copy(alpha = 0.62f)
    }

private fun ChimahonMangaChapterRowActionModel.icon(
    chapter: ChimahonMangaChapterUiModel,
): ImageVector {
    return when (action) {
        ChimahonMangaChapterRowAction.Download -> chapter.downloadState.icon
        ChimahonMangaChapterRowAction.MarkRead -> Icons.Outlined.CheckCircle
        ChimahonMangaChapterRowAction.MarkUnread -> Icons.Outlined.CheckCircle
        ChimahonMangaChapterRowAction.Bookmark -> Icons.Outlined.BookmarkBorder
        ChimahonMangaChapterRowAction.RemoveBookmark -> Icons.Filled.Bookmark
        ChimahonMangaChapterRowAction.Select -> Icons.Outlined.CheckCircle
    }
}
