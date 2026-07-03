package app.chimahon.shared.mangaui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Public
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonMangaLibraryContent(
    state: ChimahonMangaLibraryUiState,
    onMangaClick: (ChimahonMangaLibraryItemModel) -> Unit,
    modifier: Modifier = Modifier,
    onMangaLongClick: (ChimahonMangaLibraryItemModel) -> Unit = {},
    onContinueClick: (ChimahonMangaLibraryItemModel) -> Unit = onMangaClick,
    onCategorySelected: (String?) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(16.dp),
    coverContent: @Composable BoxScope.(ChimahonMangaLibraryItemModel) -> Unit = {
        ChimahonMangaLibraryCoverPlaceholder(title = it.title)
    },
) {
    Column(modifier = modifier) {
        ChimahonMangaLibraryCategoryChips(
            categories = state.categories,
            selectedCategoryId = state.selectedCategoryId,
            onCategorySelected = onCategorySelected,
        )
        if (state.displayMode.isList) {
            ChimahonMangaLibraryList(
                items = state.items,
                displayMode = state.displayMode,
                displayOptions = state.displayOptions,
                selectedItemIds = state.selectedItemIds,
                onMangaClick = onMangaClick,
                onMangaLongClick = onMangaLongClick,
                onContinueClick = onContinueClick,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                coverContent = coverContent,
            )
        } else {
            ChimahonMangaLibraryGrid(
                items = state.items,
                displayMode = state.displayMode,
                displayOptions = state.displayOptions,
                selectedItemIds = state.selectedItemIds,
                onMangaClick = onMangaClick,
                onMangaLongClick = onMangaLongClick,
                onContinueClick = onContinueClick,
                modifier = Modifier.weight(1f),
                contentPadding = contentPadding,
                coverContent = coverContent,
            )
        }
    }
}

@Composable
fun ChimahonMangaLibraryCategoryChips(
    categories: List<ChimahonMangaLibraryCategoryModel>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    showItemCount: Boolean = true,
) {
    if (categories.isEmpty()) return
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        items(categories, key = { "manga-library-category:${it.id}:${it.title}" }) { category ->
            ChimahonMangaLibraryCategoryChip(
                category = category,
                selected = category.id == selectedCategoryId,
                showItemCount = showItemCount,
                onClick = { onCategorySelected(category.id) },
            )
        }
    }
}

@Composable
fun ChimahonMangaLibraryGrid(
    items: List<ChimahonMangaLibraryItemModel>,
    displayMode: ChimahonMangaLibraryDisplayMode,
    displayOptions: ChimahonMangaLibraryDisplayOptions,
    onMangaClick: (ChimahonMangaLibraryItemModel) -> Unit,
    modifier: Modifier = Modifier,
    selectedItemIds: Set<String> = emptySet(),
    onMangaLongClick: (ChimahonMangaLibraryItemModel) -> Unit = {},
    onContinueClick: (ChimahonMangaLibraryItemModel) -> Unit = onMangaClick,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    coverContent: @Composable BoxScope.(ChimahonMangaLibraryItemModel) -> Unit = {
        ChimahonMangaLibraryCoverPlaceholder(title = it.title)
    },
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(displayMode.gridMinCellSize()),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items, key = { it.id }) { manga ->
            ChimahonMangaLibraryGridCard(
                manga = manga,
                displayMode = displayMode,
                displayOptions = displayOptions,
                selected = manga.id in selectedItemIds,
                onClick = { onMangaClick(manga) },
                onLongClick = { onMangaLongClick(manga) },
                onContinueClick = { onContinueClick(manga) },
                coverContent = { coverContent(manga) },
            )
        }
    }
}

@Composable
fun ChimahonMangaLibraryList(
    items: List<ChimahonMangaLibraryItemModel>,
    displayMode: ChimahonMangaLibraryDisplayMode,
    displayOptions: ChimahonMangaLibraryDisplayOptions,
    onMangaClick: (ChimahonMangaLibraryItemModel) -> Unit,
    modifier: Modifier = Modifier,
    selectedItemIds: Set<String> = emptySet(),
    onMangaLongClick: (ChimahonMangaLibraryItemModel) -> Unit = {},
    onContinueClick: (ChimahonMangaLibraryItemModel) -> Unit = onMangaClick,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    coverContent: @Composable BoxScope.(ChimahonMangaLibraryItemModel) -> Unit = {
        ChimahonMangaLibraryCoverPlaceholder(title = it.title)
    },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(items, key = { it.id }) { manga ->
            ChimahonMangaLibraryListRow(
                manga = manga,
                displayMode = displayMode,
                displayOptions = displayOptions,
                selected = manga.id in selectedItemIds,
                onClick = { onMangaClick(manga) },
                onLongClick = { onMangaLongClick(manga) },
                onContinueClick = { onContinueClick(manga) },
                coverContent = { coverContent(manga) },
            )
        }
    }
}

@Composable
fun ChimahonMangaLibraryCategoryChip(
    category: ChimahonMangaLibraryCategoryModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showItemCount: Boolean = true,
) {
    Surface(
        modifier = modifier
            .alpha(if (category.hidden) 0.42f else 1f)
            .clickable(enabled = !category.hidden, onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.16f)
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = 0.06f)
        },
        contentColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.82f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (showItemCount) category.displayTitle else category.title,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (category.updateCount > 0) {
                ChimahonMangaLibraryMiniCount(category.updateCount, ChimahonMangaLibraryBadgeKind.Update)
            } else if (category.unreadCount > 0) {
                ChimahonMangaLibraryMiniCount(category.unreadCount, ChimahonMangaLibraryBadgeKind.Unread)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonMangaLibraryGridCard(
    manga: ChimahonMangaLibraryItemModel,
    displayMode: ChimahonMangaLibraryDisplayMode,
    displayOptions: ChimahonMangaLibraryDisplayOptions,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonMangaLibraryCoverPlaceholder(title = manga.title)
    },
) {
    Column(
        modifier = modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(manga.coverAspectRatio(displayMode))
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
        ) {
            coverContent()
            ChimahonMangaLibraryCoverScrim(modifier = Modifier.align(Alignment.BottomCenter))
            ChimahonMangaLibraryBadgeStack(
                badges = manga.badges(displayOptions),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
            )
            if (selected) {
                ChimahonMangaLibrarySelectedMarker(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                )
            }
            if (displayMode == ChimahonMangaLibraryDisplayMode.CoverOnlyGrid) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    text = manga.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (displayMode != ChimahonMangaLibraryDisplayMode.CoverOnlyGrid) {
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = manga.title,
                color = MaterialTheme.colors.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = if (displayMode == ChimahonMangaLibraryDisplayMode.CompactGrid) 2 else 3,
                overflow = TextOverflow.Ellipsis,
            )
            ChimahonMangaLibrarySecondaryLines(
                manga = manga,
                displayMode = displayMode,
                displayOptions = displayOptions,
            )
            if (displayMode.isDetailed && displayOptions.showCategoryBadges) {
                ChimahonMangaLibraryInlineBadges(
                    badges = manga.categoryLabels.take(3).map {
                        ChimahonMangaLibraryBadgeModel(it, ChimahonMangaLibraryBadgeKind.Category)
                    },
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
        if (manga.shouldShowContinueButton(displayOptions)) {
            ChimahonMangaLibraryContinueButton(
                modifier = Modifier.padding(top = 8.dp),
                text = manga.defaultProgressLabel ?: "Continue",
                onClick = onContinueClick,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonMangaLibraryListRow(
    manga: ChimahonMangaLibraryItemModel,
    displayMode: ChimahonMangaLibraryDisplayMode,
    displayOptions: ChimahonMangaLibraryDisplayOptions,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonMangaLibraryCoverPlaceholder(title = manga.title)
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = if (displayMode.isDetailed) 10.dp else 7.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(if (displayMode.isDetailed) 64.dp else 52.dp)
                    .height(if (displayMode.isDetailed) 92.dp else 74.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
            ) {
                coverContent()
                if (selected) {
                    ChimahonMangaLibrarySelectedMarker(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = manga.title,
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = if (displayMode.isDetailed) 2 else 1,
                    overflow = TextOverflow.Ellipsis,
                )
                ChimahonMangaLibrarySecondaryLines(
                    manga = manga,
                    displayMode = displayMode,
                    displayOptions = displayOptions,
                    maxLines = if (displayMode.isDetailed) 3 else 1,
                )
                ChimahonMangaLibraryInlineBadges(
                    badges = manga.badges(displayOptions).take(if (displayMode.isDetailed) 5 else 3),
                    modifier = Modifier.padding(top = 6.dp),
                )
                if (displayMode.isDetailed && displayOptions.showCategoryBadges) {
                    ChimahonMangaLibraryInlineBadges(
                        badges = manga.categoryLabels.take(4).map {
                            ChimahonMangaLibraryBadgeModel(it, ChimahonMangaLibraryBadgeKind.Category)
                        },
                        modifier = Modifier.padding(top = 5.dp),
                    )
                }
            }
            if (manga.shouldShowContinueButton(displayOptions)) {
                ChimahonMangaLibraryContinueButton(
                    text = "Read",
                    onClick = onContinueClick,
                )
            }
        }
    }
}

@Composable
fun ChimahonMangaLibraryCoverPlaceholder(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.34f),
                        MaterialTheme.colors.secondary.copy(alpha = 0.18f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.CollectionsBookmark,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.42f),
            modifier = Modifier.size(38.dp),
        )
        Text(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            text = title.libraryInitials(),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChimahonMangaLibrarySecondaryLines(
    manga: ChimahonMangaLibraryItemModel,
    displayMode: ChimahonMangaLibraryDisplayMode,
    displayOptions: ChimahonMangaLibraryDisplayOptions,
    maxLines: Int = 2,
) {
    val lines = manga.secondaryLines(displayMode, displayOptions)
    if (lines.isEmpty()) return
    Text(
        modifier = Modifier.padding(top = 2.dp),
        text = lines.joinToString(" / "),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
        fontSize = 12.sp,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ChimahonMangaLibraryBadgeStack(
    badges: List<ChimahonMangaLibraryBadgeModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        badges.take(3).forEach { badge ->
            ChimahonMangaLibraryBadge(badge = badge)
        }
    }
}

@Composable
private fun ChimahonMangaLibraryInlineBadges(
    badges: List<ChimahonMangaLibraryBadgeModel>,
    modifier: Modifier = Modifier,
) {
    if (badges.isEmpty()) return
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        badges.forEach { badge ->
            ChimahonMangaLibraryBadge(badge = badge, compact = true)
        }
    }
}

@Composable
fun ChimahonMangaLibraryBadge(
    badge: ChimahonMangaLibraryBadgeModel,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val colors = badge.kind.badgeColors()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = colors.first,
        contentColor = colors.second,
        elevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 6.dp else 7.dp,
                vertical = if (compact) 2.dp else 3.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            badge.kind.icon()?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(if (compact) 10.dp else 12.dp),
                )
            }
            Text(
                text = badge.label,
                fontSize = if (compact) 9.sp else 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonMangaLibraryMiniCount(
    count: Int,
    kind: ChimahonMangaLibraryBadgeKind,
) {
    val colors = kind.badgeColors()
    Surface(
        shape = CircleShape,
        color = colors.first,
        contentColor = colors.second,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            text = count.toString(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ChimahonMangaLibrarySelectedMarker(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        shape = CircleShape,
        elevation = 2.dp,
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = "Selected",
            modifier = Modifier
                .padding(2.dp)
                .size(18.dp),
        )
    }
}

@Composable
private fun ChimahonMangaLibraryContinueButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier.heightIn(min = 32.dp),
        onClick = onClick,
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChimahonMangaLibraryCoverScrim(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.58f)),
                ),
            ),
    )
}

@Composable
private fun ChimahonMangaLibraryBadgeKind.badgeColors(): Pair<Color, Color> {
    return when (this) {
        ChimahonMangaLibraryBadgeKind.Unread ->
            MaterialTheme.colors.primary to MaterialTheme.colors.onPrimary
        ChimahonMangaLibraryBadgeKind.Downloaded ->
            Color(0xFF1B7F4A) to Color.White
        ChimahonMangaLibraryBadgeKind.Update ->
            MaterialTheme.colors.secondary to MaterialTheme.colors.onSecondary
        ChimahonMangaLibraryBadgeKind.History ->
            MaterialTheme.colors.onSurface.copy(alpha = 0.78f) to MaterialTheme.colors.surface
        ChimahonMangaLibraryBadgeKind.Warning ->
            MaterialTheme.colors.error to MaterialTheme.colors.onError
        ChimahonMangaLibraryBadgeKind.Local,
        ChimahonMangaLibraryBadgeKind.Language,
        ChimahonMangaLibraryBadgeKind.Source,
        ChimahonMangaLibraryBadgeKind.Tracking,
        ChimahonMangaLibraryBadgeKind.Status,
        ChimahonMangaLibraryBadgeKind.Category,
        ChimahonMangaLibraryBadgeKind.Bookmark,
        ChimahonMangaLibraryBadgeKind.Progress,
        -> MaterialTheme.colors.surface to MaterialTheme.colors.onSurface
    }
}

private fun ChimahonMangaLibraryBadgeKind.icon(): ImageVector? {
    return when (this) {
        ChimahonMangaLibraryBadgeKind.Downloaded -> Icons.Outlined.CloudDownload
        ChimahonMangaLibraryBadgeKind.Update -> Icons.Outlined.NewReleases
        ChimahonMangaLibraryBadgeKind.History -> Icons.Outlined.History
        ChimahonMangaLibraryBadgeKind.Bookmark -> Icons.Filled.Bookmark
        ChimahonMangaLibraryBadgeKind.Local -> Icons.Outlined.CollectionsBookmark
        ChimahonMangaLibraryBadgeKind.Language -> Icons.Outlined.Public
        ChimahonMangaLibraryBadgeKind.Source -> Icons.Outlined.Public
        ChimahonMangaLibraryBadgeKind.Tracking -> Icons.Outlined.Sync
        ChimahonMangaLibraryBadgeKind.Category -> Icons.Outlined.Label
        ChimahonMangaLibraryBadgeKind.Unread,
        ChimahonMangaLibraryBadgeKind.Status,
        ChimahonMangaLibraryBadgeKind.Warning,
        ChimahonMangaLibraryBadgeKind.Progress,
        -> null
    }
}

private fun ChimahonMangaLibraryDisplayMode.gridMinCellSize() = when (this) {
    ChimahonMangaLibraryDisplayMode.CompactGrid,
    ChimahonMangaLibraryDisplayMode.CoverOnlyGrid,
    -> 112.dp
    ChimahonMangaLibraryDisplayMode.ComfortableGrid,
    -> 148.dp
    ChimahonMangaLibraryDisplayMode.DetailedGrid,
    ChimahonMangaLibraryDisplayMode.PanoramaGrid,
    -> 168.dp
    ChimahonMangaLibraryDisplayMode.CompactList,
    ChimahonMangaLibraryDisplayMode.DetailedList,
    -> 148.dp
}

private fun ChimahonMangaLibraryItemModel.coverAspectRatio(
    displayMode: ChimahonMangaLibraryDisplayMode,
): Float {
    return when (coverRatio) {
        ChimahonMangaLibraryCoverRatio.Square -> 1f
        ChimahonMangaLibraryCoverRatio.ThreeToFour -> 3f / 4f
        ChimahonMangaLibraryCoverRatio.TwoToThree -> 2f / 3f
        ChimahonMangaLibraryCoverRatio.Panorama -> 1.45f
        ChimahonMangaLibraryCoverRatio.Automatic -> when (displayMode) {
            ChimahonMangaLibraryDisplayMode.PanoramaGrid -> 1.45f
            else -> 2f / 3f
        }
    }
}

private fun String.libraryInitials(): String {
    val words = trim()
        .split(' ', '-', '_', '.', ':')
        .filter { it.isNotBlank() }
    return words
        .take(2)
        .mapNotNull { word -> word.firstOrNull()?.uppercaseChar()?.toString() }
        .joinToString("")
        .ifBlank { "C" }
}
