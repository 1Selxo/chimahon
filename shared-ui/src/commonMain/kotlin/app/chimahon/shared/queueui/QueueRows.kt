package app.chimahon.shared.queueui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonUpdateFeedList(
    rows: List<ChimahonUpdateFeedRowUiModel>,
    onRowClick: (ChimahonUpdateFeedRowUiModel) -> Unit,
    onChapterCheckedChange: (ChimahonQueueChapterUiModel, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonQueueCoverPlaceholder() },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        items(rows, key = { it.id }) { row ->
            ChimahonUpdateFeedRow(
                row = row,
                onClick = { onRowClick(row) },
                onChapterCheckedChange = onChapterCheckedChange,
                coverContent = coverContent,
            )
        }
    }
}

@Composable
fun ChimahonHistoryList(
    rows: List<ChimahonHistoryRowUiModel>,
    onRowClick: (ChimahonHistoryRowUiModel) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonQueueCoverPlaceholder() },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        items(rows, key = { it.id }) { row ->
            ChimahonHistoryRow(
                row = row,
                onClick = { onRowClick(row) },
                coverContent = coverContent,
            )
        }
    }
}

@Composable
fun ChimahonDownloadQueueList(
    rows: List<ChimahonDownloadQueueRowUiModel>,
    onRowClick: (ChimahonDownloadQueueRowUiModel) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonQueueCoverPlaceholder() },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        items(rows, key = { it.id }) { row ->
            ChimahonDownloadQueueRow(
                row = row,
                onClick = { onRowClick(row) },
                coverContent = coverContent,
            )
        }
    }
}

@Composable
fun ChimahonUpdateFeedRow(
    row: ChimahonUpdateFeedRowUiModel,
    onClick: () -> Unit,
    onChapterCheckedChange: (ChimahonQueueChapterUiModel, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonQueueCoverPlaceholder() },
) {
    ChimahonQueueRowSurface(
        modifier = modifier,
        selected = row.selected,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (!row.updateHeader.isNullOrBlank()) {
                Text(
                    text = row.updateHeader,
                    color = MaterialTheme.colors.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChimahonQueueCover(
                    thumbnailUrl = row.thumbnailUrl,
                    coverContent = coverContent,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = row.mangaTitle,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = listOfNotNull(row.subtitle, row.sourceName, row.chapterCountLabel).joinToString("  |  "),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                ChimahonQueueOverflowMenu(actions = row.overflowActions)
            }
            row.chapters.forEachIndexed { index, chapter ->
                if (index > 0) {
                    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f))
                }
                ChimahonQueueChapterRow(
                    chapter = chapter,
                    onCheckedChange = { checked -> onChapterCheckedChange(chapter, checked) },
                )
            }
        }
    }
}

@Composable
fun ChimahonHistoryRow(
    row: ChimahonHistoryRowUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonQueueCoverPlaceholder() },
) {
    ChimahonQueueRowSurface(
        modifier = modifier,
        selected = row.selected,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonQueueCover(
                thumbnailUrl = row.thumbnailUrl,
                coverContent = coverContent,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.mangaTitle,
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = row.chapterTitle,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.80f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.54f),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = listOfNotNull(row.readAtLabel, row.progressLabel, row.sourceName).joinToString("  |  "),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            ChimahonQueueOverflowMenu(actions = row.overflowActions)
        }
    }
}

@Composable
fun ChimahonDownloadQueueRow(
    row: ChimahonDownloadQueueRowUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonQueueCoverPlaceholder() },
) {
    ChimahonQueueRowSurface(
        modifier = modifier,
        selected = row.selected,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChimahonQueueCover(
                    thumbnailUrl = row.thumbnailUrl,
                    coverContent = coverContent,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = row.mangaTitle,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = row.chapterTitle,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.80f),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val detailLabel = row.detailLabel
                    if (!detailLabel.isNullOrBlank()) {
                        Text(
                            text = detailLabel,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                ChimahonQueueOverflowMenu(actions = row.overflowActions)
            }
            ChimahonQueueProgressChip(
                modifier = Modifier.fillMaxWidth(),
                progress = row.progress,
                status = row.status.toStatusChip(),
            )
        }
    }
}

@Composable
fun ChimahonQueueChapterRow(
    chapter: ChimahonQueueChapterUiModel,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChimahonQueueChapterCheckbox(
            checked = chapter.checked,
            enabled = chapter.checkboxEnabled,
            onCheckedChange = onCheckedChange,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chapter.title,
                color = if (chapter.read) {
                    MaterialTheme.colors.onSurface.copy(alpha = 0.58f)
                } else {
                    MaterialTheme.colors.onSurface
                },
                fontSize = 14.sp,
                fontWeight = if (chapter.read) FontWeight.Normal else FontWeight.Medium,
                textDecoration = if (chapter.read) TextDecoration.None else null,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = listOfNotNull(
                    chapter.subtitle,
                    chapter.chapterNumberLabel,
                    chapter.scanlator,
                    chapter.dateLabel,
                ).joinToString("  |  "),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (chapter.bookmarked) {
                Icon(
                    imageVector = Icons.Outlined.Bookmark,
                    contentDescription = "Bookmarked",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
            if (chapter.downloaded) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Downloaded",
                    tint = Color(0xFF00796B),
                    modifier = Modifier.size(18.dp),
                )
            }
            chapter.status.toStatusChip()?.let { chip ->
                ChimahonQueueStatusChip(chip = chip)
            }
            chapter.progress?.let { progress ->
                Text(
                    text = progress.displayPercentLabel,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                    fontSize = 12.sp,
                    maxLines = 1,
                )
            }
            ChimahonQueueOverflowMenu(actions = chapter.overflowActions)
        }
    }
}

@Composable
internal fun ChimahonQueueRowSurface(
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
fun ChimahonQueueCover(
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
    width: Dp = 52.dp,
    coverContent: @Composable BoxScope.(String?) -> Unit = { ChimahonQueueCoverPlaceholder() },
) {
    Box(
        modifier = modifier
            .width(width)
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center,
    ) {
        coverContent(thumbnailUrl)
    }
}

@Composable
fun ChimahonQueueCoverPlaceholder(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(78.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.CollectionsBookmark,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.32f),
        )
    }
}
