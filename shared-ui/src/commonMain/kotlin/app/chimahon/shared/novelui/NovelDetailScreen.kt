package app.chimahon.shared.novelui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonNovelDetailScreen(
    state: ChimahonNovelDetailUiState,
    actions: ChimahonNovelDetailActions,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 24.dp),
    coverContent: @Composable BoxScope.(ChimahonNovelHeaderUiModel) -> Unit = {
        ChimahonNovelCoverPlaceholder(
            title = it.title,
            modifier = Modifier.fillMaxSize(),
        )
    },
) {
    when (state.contentState) {
        ChimahonNovelContentState.Loading -> {
            ChimahonNovelLoadingState(
                message = "Loading novel...",
                modifier = modifier,
            )
        }
        ChimahonNovelContentState.Error -> {
            ChimahonNovelErrorState(
                message = state.errorMessage ?: "Novel failed to load",
                onRetry = actions.onRetry,
                modifier = modifier,
                title = "Novel failed to load",
            )
        }
        ChimahonNovelContentState.Empty -> {
            ChimahonNovelEmptyLibraryState(
                onImportClick = {},
                modifier = modifier,
            )
        }
        ChimahonNovelContentState.FilteredEmpty,
        ChimahonNovelContentState.Content,
        -> {
            val novel = state.novel ?: return
            val visibleChapters = state.visibleChapters
            LazyColumn(
                modifier = modifier,
                contentPadding = contentPadding,
            ) {
                item(key = "novel-header") {
                    ChimahonNovelDetailHeader(
                        novel = novel,
                        refreshing = state.refreshing,
                        actions = actions,
                        coverContent = { coverContent(novel) },
                    )
                }
                item(key = "novel-description") {
                    ChimahonNovelDescription(novel = novel)
                }
                item(key = "novel-actions") {
                    ChimahonNovelDetailActionRow(novel = novel, actions = actions)
                }
                item(key = "novel-chapter-controls") {
                    ChimahonNovelChapterControls(
                        chapterCount = visibleChapters.size,
                        totalChapterCount = state.chapters.size,
                        sortState = state.sortState,
                        filterState = state.filterState,
                        volumes = state.volumes,
                        actions = actions,
                    )
                }
                if (visibleChapters.isEmpty()) {
                    item(key = "novel-chapter-empty") {
                        ChimahonNovelChapterEmptyState(
                            hasFilters = state.filterState.activeCount > 0,
                        )
                    }
                } else {
                    items(
                        items = visibleChapters.withIndex().toList(),
                        key = { indexed -> indexed.value.stableLazyKey(indexed.index) },
                    ) { indexed ->
                        val chapter = indexed.value
                        ChimahonNovelChapterRow(
                            chapter = chapter,
                            selected = chapter.id in state.selectedChapterIds,
                            onClick = { actions.onChapterClick(chapter) },
                            onLongClick = { actions.onChapterLongClick(chapter) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonNovelDetailHeader(
    novel: ChimahonNovelHeaderUiModel,
    refreshing: Boolean,
    actions: ChimahonNovelDetailActions,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonNovelCoverPlaceholder(
            title = novel.title,
            modifier = Modifier.fillMaxSize(),
        )
    },
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(112.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
            ) {
                coverContent()
                if (novel.ghost) {
                    ChimahonNovelHeaderBadge(
                        text = "MISSING",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                        warning = true,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(
                    text = novel.title,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                val creatorLine = novel.creatorLine
                if (!creatorLine.isNullOrBlank()) {
                    Text(
                        text = creatorLine,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.70f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = buildList {
                        add(novel.sourceLabel)
                        novel.language?.takeIf(String::isNotBlank)?.let { add(it.uppercase()) }
                        novel.lastReadLabel?.takeIf(String::isNotBlank)?.let { add(it) }
                    }.joinToString(" - "),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    ChimahonNovelHeaderBadge(text = novel.progressLabel)
                    if (novel.characterCount != null) {
                        ChimahonNovelHeaderBadge(text = "${novel.characterCount} chars")
                    }
                    if (!novel.dictionaryProfileName.isNullOrBlank()) {
                        ChimahonNovelHeaderBadge(text = novel.dictionaryProfileName)
                    }
                    if (refreshing) {
                        ChimahonNovelHeaderBadge(text = "Refreshing")
                    }
                }
                Button(
                    onClick = { actions.onReadClick(novel) },
                    enabled = !novel.ghost,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary,
                    ),
                ) {
                    Text(text = if (novel.progressPercent != null && novel.progressPercent > 0) "Resume" else "Start")
                }
            }
        }
    }
}

@Composable
fun ChimahonNovelDescription(
    novel: ChimahonNovelHeaderUiModel,
    modifier: Modifier = Modifier,
) {
    var expanded by remember(novel.id) { mutableStateOf(false) }
    val description = novel.description?.takeIf(String::isNotBlank)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (description != null) {
            Text(
                text = description,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.78f),
                fontSize = 14.sp,
                maxLines = if (expanded) Int.MAX_VALUE else 4,
                overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
            )
            TextButton(onClick = { expanded = !expanded }) {
                Text(text = if (expanded) "Show less" else "Show more")
            }
        }
        if (novel.categoryLabels.isNotEmpty()) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                novel.categoryLabels.forEach { label ->
                    ChimahonNovelHeaderBadge(text = label)
                }
            }
        }
    }
}

@Composable
fun ChimahonNovelDetailActionRow(
    novel: ChimahonNovelHeaderUiModel,
    actions: ChimahonNovelDetailActions,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ChimahonNovelPillAction("Edit", onClick = { actions.onEditInfoClick(novel) })
        ChimahonNovelPillAction("Category", onClick = { actions.onMoveCategoryClick(novel) })
        ChimahonNovelPillAction("Dictionary", onClick = { actions.onDictionaryProfileClick(novel) })
        ChimahonNovelPillAction("Sync", onClick = { actions.onSyncClick(novel) })
        ChimahonNovelPillAction("Delete", destructive = true, onClick = { actions.onDeleteClick(novel) })
    }
}

@Composable
fun ChimahonNovelChapterControls(
    chapterCount: Int,
    totalChapterCount: Int,
    sortState: ChimahonNovelChapterSortState,
    filterState: ChimahonNovelChapterFilterState,
    volumes: List<ChimahonNovelVolumeUiModel>,
    actions: ChimahonNovelDetailActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
    ) {
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Chapters",
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                )
                Text(
                    text = "$chapterCount shown of $totalChapterCount - ${sortState.directionLabel}",
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    fontSize = 12.sp,
                )
            }
            TextButton(
                onClick = {
                    actions.onSortChange(sortState.copy(descending = !sortState.descending))
                },
            ) {
                Text(if (sortState.descending) "Reverse" else "Source")
            }
            TextButton(onClick = actions.onFilterClick) {
                Text(if (filterState.activeCount > 0) "Filter ${filterState.activeCount}" else "Filter")
            }
            TextButton(onClick = actions.onRefresh) {
                Text("Refresh")
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChimahonNovelHeaderBadge(text = sortState.sort.title)
            ChimahonNovelHeaderBadge(text = sortState.directionLabel)
            if (filterState.read != ChimahonNovelReadFilter.Any) {
                ChimahonNovelHeaderBadge(text = filterState.read.title)
            }
            if (filterState.imageOnly != ChimahonNovelImageOnlyFilter.Any) {
                ChimahonNovelHeaderBadge(text = filterState.imageOnly.title)
            }
            val activeVolume = volumes.firstOrNull { it.id == filterState.volumeId }
            if (activeVolume != null) {
                ChimahonNovelHeaderBadge(text = activeVolume.title)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonNovelChapterRow(
    chapter: ChimahonNovelChapterUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        color = when {
            selected -> MaterialTheme.colors.primary.copy(alpha = 0.10f)
            chapter.current -> MaterialTheme.colors.primary.copy(alpha = 0.06f)
            else -> MaterialTheme.colors.surface
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (chapter.read) {
                            MaterialTheme.colors.primary.copy(alpha = 0.12f)
                        } else {
                            MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = ((chapter.spineIndex ?: chapter.sourceOrder) + 1).toString(),
                    color = if (chapter.read) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chapter.sourceTitle,
                    color = if (chapter.read) {
                        MaterialTheme.colors.onSurface.copy(alpha = 0.60f)
                    } else {
                        MaterialTheme.colors.onSurface
                    },
                    fontWeight = if (chapter.current) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                val metadata = listOfNotNull(
                    chapter.characterRangeLabel,
                    chapter.bookmarkLabel,
                    if (chapter.imageOnly) "Image-only" else null,
                    if (chapter.current) "Current" else null,
                ).joinToString(" - ")
                if (metadata.isNotBlank()) {
                    Text(
                        text = metadata,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (chapter.progress > 0f && chapter.progress < 1f) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = chapter.progress.coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = MaterialTheme.colors.primary,
                        backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                    )
                }
            }
            if (selected) {
                ChimahonNovelHeaderBadge(text = "Selected")
            }
        }
    }
}

@Composable
private fun ChimahonNovelHeaderBadge(
    text: String,
    modifier: Modifier = Modifier,
    warning: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(5.dp),
        color = if (warning) MaterialTheme.colors.error.copy(alpha = 0.14f)
        else MaterialTheme.colors.primary.copy(alpha = 0.11f),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            text = text,
            color = if (warning) MaterialTheme.colors.error else MaterialTheme.colors.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChimahonNovelPillAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
) {
    val content = if (destructive) MaterialTheme.colors.error else MaterialTheme.colors.primary
    Button(
        modifier = modifier.height(36.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = content.copy(alpha = 0.10f),
            contentColor = content,
        ),
        elevation = null,
        contentPadding = PaddingValues(horizontal = 12.dp),
    ) {
        Text(text = text, fontSize = 12.sp, maxLines = 1)
    }
}
