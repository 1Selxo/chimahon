package app.chimahon.shared.downloadhistoryui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Update
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonUpdatesList(
    rows: List<ChimahonUpdateRowUiModel>,
    onRowClick: (ChimahonUpdateRowUiModel) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
    coverContent: @Composable BoxScope.(ChimahonMediaKind, String?) -> Unit = { kind, _ ->
        ChimahonDownloadHistoryCoverPlaceholder(mediaKind = kind, colors = colors)
    },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        itemsIndexed(rows, key = { index, row -> row.stableLazyKey(index) }) { _, row ->
            ChimahonUpdateRow(
                row = row,
                onClick = { onRowClick(row) },
                colors = colors,
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
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
    coverContent: @Composable BoxScope.(ChimahonMediaKind, String?) -> Unit = { kind, _ ->
        ChimahonDownloadHistoryCoverPlaceholder(mediaKind = kind, colors = colors)
    },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        itemsIndexed(rows, key = { index, row -> row.stableLazyKey(index) }) { _, row ->
            ChimahonHistoryRow(
                row = row,
                onClick = { onRowClick(row) },
                colors = colors,
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
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
    coverContent: @Composable BoxScope.(ChimahonMediaKind, String?) -> Unit = { kind, _ ->
        ChimahonDownloadHistoryCoverPlaceholder(mediaKind = kind, colors = colors)
    },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        itemsIndexed(rows, key = { index, row -> row.stableLazyKey(index) }) { _, row ->
            ChimahonDownloadQueueRow(
                row = row,
                onClick = { onRowClick(row) },
                colors = colors,
                coverContent = coverContent,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonGroupedUpdatesList(
    groups: List<ChimahonDownloadHistoryGroup<ChimahonUpdateRowUiModel>>,
    onRowClick: (ChimahonUpdateRowUiModel) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
    coverContent: @Composable BoxScope.(ChimahonMediaKind, String?) -> Unit = { kind, _ ->
        ChimahonDownloadHistoryCoverPlaceholder(mediaKind = kind, colors = colors)
    },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        groups.forEach { group ->
            stickyHeader {
                ChimahonDownloadHistoryGroupHeader(
                    title = group.title,
                    subtitle = group.subtitle,
                    colors = colors,
                )
            }
            itemsIndexed(group.rows, key = { index, row -> row.stableLazyKey(index) }) { _, row ->
                ChimahonUpdateRow(
                    row = row,
                    onClick = { onRowClick(row) },
                    colors = colors,
                    coverContent = coverContent,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonGroupedHistoryList(
    groups: List<ChimahonDownloadHistoryGroup<ChimahonHistoryRowUiModel>>,
    onRowClick: (ChimahonHistoryRowUiModel) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
    coverContent: @Composable BoxScope.(ChimahonMediaKind, String?) -> Unit = { kind, _ ->
        ChimahonDownloadHistoryCoverPlaceholder(mediaKind = kind, colors = colors)
    },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        groups.forEach { group ->
            stickyHeader {
                ChimahonDownloadHistoryGroupHeader(
                    title = group.title,
                    subtitle = group.subtitle,
                    colors = colors,
                )
            }
            itemsIndexed(group.rows, key = { index, row -> row.stableLazyKey(index) }) { _, row ->
                ChimahonHistoryRow(
                    row = row,
                    onClick = { onRowClick(row) },
                    colors = colors,
                    coverContent = coverContent,
                )
            }
        }
    }
}

@Composable
fun ChimahonUpdateRow(
    row: ChimahonUpdateRowUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
    coverContent: @Composable BoxScope.(ChimahonMediaKind, String?) -> Unit = { kind, _ ->
        ChimahonDownloadHistoryCoverPlaceholder(mediaKind = kind, colors = colors)
    },
) {
    ChimahonDownloadHistoryRowSurface(
        selected = row.selected,
        onClick = onClick,
        modifier = modifier,
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!row.updateGroupLabel.isNullOrBlank()) {
                TimelineLabel(
                    text = row.updateGroupLabel,
                    icon = Icons.Outlined.Update,
                    colors = colors,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChimahonDownloadHistoryCover(
                    mediaKind = row.mediaKind,
                    thumbnailUrl = row.thumbnailUrl,
                    colors = colors,
                ) {
                    coverContent(row.mediaKind, row.thumbnailUrl)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = row.entryTitle,
                            color = colors.content,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = row.countLabel,
                            color = colors.secondaryContent,
                            fontSize = 11.sp,
                            maxLines = 1,
                        )
                    }
                    Text(
                        text = listOfNotNull(row.itemTitle, row.dateLabel).joinToString(" - ")
                            .ifBlank { row.mediaKind.itemTitle },
                        color = colors.secondaryContent,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                ChimahonDownloadHistoryActionButtons(actions = row.actions, colors = colors)
            }
            ChimahonDownloadHistoryChipRow(chips = row.statusChips, colors = colors)
        }
    }
}

@Composable
fun ChimahonHistoryRow(
    row: ChimahonHistoryRowUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
    coverContent: @Composable BoxScope.(ChimahonMediaKind, String?) -> Unit = { kind, _ ->
        ChimahonDownloadHistoryCoverPlaceholder(mediaKind = kind, colors = colors)
    },
) {
    ChimahonDownloadHistoryRowSurface(
        selected = row.selected,
        onClick = onClick,
        modifier = modifier,
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChimahonDownloadHistoryCover(
                    mediaKind = row.mediaKind,
                    thumbnailUrl = row.thumbnailUrl,
                    colors = colors,
                ) {
                    coverContent(row.mediaKind, row.thumbnailUrl)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = row.entryTitle,
                        color = colors.content,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = row.itemTitle ?: row.mediaKind.itemTitle,
                        color = colors.content.copy(alpha = 0.78f),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    TimelineLabel(
                        text = row.detailLabel,
                        icon = Icons.Outlined.History,
                        colors = colors,
                        modifier = Modifier.padding(top = 3.dp),
                    )
                }
                ChimahonDownloadHistoryActionButtons(actions = row.actions, colors = colors)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChimahonDownloadHistoryChipRow(
                    chips = row.statusChips,
                    colors = colors,
                    modifier = Modifier.weight(1f),
                )
                if (!row.continueLabel.isNullOrBlank()) {
                    Text(
                        text = row.continueLabel,
                        color = colors.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
fun ChimahonDownloadQueueRow(
    row: ChimahonDownloadQueueRowUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
    coverContent: @Composable BoxScope.(ChimahonMediaKind, String?) -> Unit = { kind, _ ->
        ChimahonDownloadHistoryCoverPlaceholder(mediaKind = kind, colors = colors)
    },
) {
    ChimahonDownloadHistoryRowSurface(
        selected = row.selected,
        onClick = onClick,
        modifier = modifier,
        colors = colors,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChimahonDownloadHistoryCover(
                    mediaKind = row.mediaKind,
                    thumbnailUrl = row.thumbnailUrl,
                    colors = colors,
                ) {
                    coverContent(row.mediaKind, row.thumbnailUrl)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = row.status.imageVector,
                            contentDescription = row.status.label,
                            tint = if (row.failed) colors.error else colors.primary,
                            modifier = Modifier.padding(end = 6.dp),
                        )
                        Text(
                            text = row.entryTitle,
                            color = colors.content,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Text(
                        text = row.itemTitle ?: row.mediaKind.itemTitle,
                        color = colors.content.copy(alpha = 0.78f),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val detailLabel = row.detailLabel
                    if (!detailLabel.isNullOrBlank()) {
                        Text(
                            text = detailLabel,
                            color = if (row.failed) colors.error else colors.secondaryContent,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 3.dp),
                        )
                    }
                }
                ChimahonDownloadHistoryActionButtons(actions = row.actions, colors = colors)
            }
            ChimahonDownloadHistoryProgressBar(
                progress = row.progress,
                status = row.status,
                colors = colors,
            )
            ChimahonDownloadHistoryChipRow(chips = row.statusChips, colors = colors)
        }
    }
}

@Composable
fun ChimahonDownloadHistoryGroupHeader(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    colors: ChimahonDownloadHistoryColors = ChimahonDownloadHistoryDefaults.colors(),
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.background,
        contentColor = colors.content,
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title.uppercase(),
                color = colors.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = colors.secondaryContent,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun TimelineLabel(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colors: ChimahonDownloadHistoryColors,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.secondaryContent,
        )
        Text(
            text = text,
            color = colors.secondaryContent,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
