package app.chimahon.shared.novelui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonNovelLibraryGrid(
    entries: List<ChimahonNovelBookUiModel>,
    displayMode: ChimahonNovelLibraryDisplayMode,
    onBookClick: (ChimahonNovelBookUiModel) -> Unit,
    modifier: Modifier = Modifier,
    selectedBookIds: Set<String> = emptySet(),
    showLanguageBadges: Boolean = true,
    showGhostBadges: Boolean = true,
    showProgressBadges: Boolean = true,
    onBookLongClick: (ChimahonNovelBookUiModel) -> Unit = {},
    onContinueClick: (ChimahonNovelBookUiModel) -> Unit = onBookClick,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    coverContent: @Composable BoxScope.(ChimahonNovelBookUiModel) -> Unit = {
        ChimahonNovelCoverPlaceholder(
            title = it.title,
            modifier = Modifier.fillMaxSize(),
        )
    },
) {
    val minCellSize = when (displayMode) {
        ChimahonNovelLibraryDisplayMode.CompactGrid,
        ChimahonNovelLibraryDisplayMode.CoverOnlyGrid,
        -> 112.dp
        ChimahonNovelLibraryDisplayMode.ComfortableGrid,
        ChimahonNovelLibraryDisplayMode.List,
        -> 148.dp
    }
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minCellSize),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = entries.withIndex().toList(),
            key = { indexed -> indexed.value.stableLazyKey(indexed.index) },
        ) { indexed ->
            val novel = indexed.value
            ChimahonNovelGridCard(
                novel = novel,
                displayMode = displayMode,
                selected = novel.id in selectedBookIds,
                showLanguageBadge = showLanguageBadges,
                showGhostBadge = showGhostBadges,
                showProgressBadge = showProgressBadges,
                onClick = { onBookClick(novel) },
                onLongClick = { onBookLongClick(novel) },
                onContinueClick = { onContinueClick(novel) },
                coverContent = { coverContent(novel) },
            )
        }
    }
}

@Composable
fun ChimahonNovelLibraryList(
    entries: List<ChimahonNovelBookUiModel>,
    onBookClick: (ChimahonNovelBookUiModel) -> Unit,
    modifier: Modifier = Modifier,
    selectedBookIds: Set<String> = emptySet(),
    showLanguageBadges: Boolean = true,
    showGhostBadges: Boolean = true,
    showProgressBadges: Boolean = true,
    onBookLongClick: (ChimahonNovelBookUiModel) -> Unit = {},
    onContinueClick: (ChimahonNovelBookUiModel) -> Unit = onBookClick,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    coverContent: @Composable BoxScope.(ChimahonNovelBookUiModel) -> Unit = {
        ChimahonNovelCoverPlaceholder(
            title = it.title,
            modifier = Modifier.fillMaxSize(),
        )
    },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(
            items = entries.withIndex().toList(),
            key = { indexed -> indexed.value.stableLazyKey(indexed.index) },
        ) { indexed ->
            val novel = indexed.value
            ChimahonNovelListRow(
                novel = novel,
                selected = novel.id in selectedBookIds,
                showLanguageBadge = showLanguageBadges,
                showGhostBadge = showGhostBadges,
                showProgressBadge = showProgressBadges,
                onClick = { onBookClick(novel) },
                onLongClick = { onBookLongClick(novel) },
                onContinueClick = { onContinueClick(novel) },
                coverContent = { coverContent(novel) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonNovelGridCard(
    novel: ChimahonNovelBookUiModel,
    displayMode: ChimahonNovelLibraryDisplayMode,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLanguageBadge: Boolean = true,
    showGhostBadge: Boolean = true,
    showProgressBadge: Boolean = true,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonNovelCoverPlaceholder(title = novel.title, modifier = Modifier.fillMaxSize())
    },
) {
    Column(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
        ) {
            coverContent()
            ChimahonNovelCoverScrim(modifier = Modifier.align(Alignment.BottomCenter))
            ChimahonNovelBadgeStack(
                novel = novel,
                showLanguageBadge = showLanguageBadge,
                showGhostBadge = showGhostBadge,
                showProgressBadge = showProgressBadge,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
            )
            if (selected) {
                ChimahonNovelSelectedMarker(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                )
            }
            if (displayMode == ChimahonNovelLibraryDisplayMode.CoverOnlyGrid) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    text = novel.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (displayMode != ChimahonNovelLibraryDisplayMode.CoverOnlyGrid) {
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = novel.title,
                color = MaterialTheme.colors.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = if (displayMode == ChimahonNovelLibraryDisplayMode.CompactGrid) 2 else 3,
                overflow = TextOverflow.Ellipsis,
            )
            ChimahonNovelSecondaryLine(novel = novel)
        }
        val progressLabel = novel.progressLabel
        if (!novel.ghost && progressLabel != null) {
            ChimahonNovelContinueButton(
                modifier = Modifier.padding(top = 8.dp),
                text = progressLabel,
                onClick = onContinueClick,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonNovelListRow(
    novel: ChimahonNovelBookUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLanguageBadge: Boolean = true,
    showGhostBadge: Boolean = true,
    showProgressBadge: Boolean = true,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonNovelCoverPlaceholder(title = novel.title, modifier = Modifier.fillMaxSize())
    },
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        color = if (selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.10f)
        } else {
            MaterialTheme.colors.surface
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .height(78.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
            ) {
                coverContent()
                if (selected) {
                    ChimahonNovelSelectedMarker(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = novel.title,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    ChimahonNovelBadgeStack(
                        novel = novel,
                        showLanguageBadge = showLanguageBadge,
                        showGhostBadge = showGhostBadge,
                        showProgressBadge = showProgressBadge,
                    )
                }
                val subtitle = novel.subtitle
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.66f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (novel.progressFraction > 0f || novel.readCharacterCount > 0) {
                    Spacer(modifier = Modifier.height(7.dp))
                    LinearProgressIndicator(
                        progress = novel.progressFraction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(100.dp)),
                        color = MaterialTheme.colors.primary,
                        backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                    )
                }
            }
            if (!novel.ghost) {
                ChimahonNovelContinueButton(
                    text = "Read",
                    onClick = onContinueClick,
                )
            }
        }
    }
}

@Composable
fun ChimahonNovelBottomActionBar(
    visible: Boolean,
    selectedCount: Int,
    actions: ChimahonNovelLibraryActions,
    modifier: Modifier = Modifier,
    canEdit: Boolean = selectedCount == 1,
    syncAvailable: Boolean = actions.onSyncImport != null || actions.onSyncExport != null || actions.onSyncAuto != null,
) {
    if (!visible) return
    Surface(
        modifier = modifier.fillMaxWidth(),
        elevation = 12.dp,
        color = MaterialTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "$selectedCount selected",
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            ChimahonNovelActionButton(text = "Move", onClick = actions.onMoveCategory)
            ChimahonNovelActionButton(text = "Reset", onClick = actions.onResetProgress)
            if (canEdit) {
                ChimahonNovelActionButton(text = "Edit", onClick = actions.onEditBook)
            }
            if (syncAvailable) {
                ChimahonNovelActionButton(text = "Sync", onClick = actions.onSyncAuto ?: {})
            }
            ChimahonNovelActionButton(
                text = "Delete",
                destructive = true,
                onClick = actions.onDeleteSelected,
            )
        }
    }
}

@Composable
private fun ChimahonNovelSecondaryLine(novel: ChimahonNovelBookUiModel) {
    val text = listOfNotNull(
        novel.author?.takeIf(String::isNotBlank),
        novel.progressLabel,
        novel.lastReadLabel?.takeIf(String::isNotBlank),
    ).joinToString(" - ")
    if (text.isBlank()) return
    Text(
        text = text,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
        fontSize = 12.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ChimahonNovelCoverScrim(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.36f)
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.54f),
                ),
            ),
    )
}

@Composable
private fun ChimahonNovelBadgeStack(
    novel: ChimahonNovelBookUiModel,
    modifier: Modifier = Modifier,
    showLanguageBadge: Boolean,
    showGhostBadge: Boolean,
    showProgressBadge: Boolean,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showGhostBadge && novel.ghost) {
            ChimahonNovelBadge(text = "MISSING", warning = true)
        } else {
            if (showLanguageBadge && !novel.language.isNullOrBlank()) {
                ChimahonNovelBadge(text = novel.language.uppercase())
            }
            val progressLabel = novel.progressLabel
            if (showProgressBadge && progressLabel != null) {
                ChimahonNovelBadge(text = progressLabel)
            }
        }
    }
}

@Composable
private fun ChimahonNovelBadge(
    text: String,
    modifier: Modifier = Modifier,
    warning: Boolean = false,
) {
    val background = if (warning) {
        MaterialTheme.colors.error
    } else {
        Color.Black.copy(alpha = 0.64f)
    }
    val content = if (warning) MaterialTheme.colors.onError else Color.White
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(background)
            .padding(horizontal = 5.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = content,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ChimahonNovelSelectedMarker(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colors.primary)
            .border(2.dp, MaterialTheme.colors.surface, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "OK",
            color = MaterialTheme.colors.onPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ChimahonNovelContinueButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier.height(34.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.12f),
            contentColor = MaterialTheme.colors.primary,
        ),
        elevation = null,
        contentPadding = PaddingValues(horizontal = 12.dp),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ChimahonNovelActionButton(
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

@Composable
fun ChimahonNovelSelectionCheckBox(
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    Checkbox(
        modifier = modifier,
        checked = selected,
        onCheckedChange = null,
    )
}
