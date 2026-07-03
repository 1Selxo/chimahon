package app.chimahon.shared.library

import app.chimahon.shared.ChimahonLibraryCoverRatio
import app.chimahon.shared.ChimahonLibraryDisplayMode
import app.chimahon.shared.ChimahonLibrarySettings
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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Public
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

@Composable
fun ChimahonLibraryMangaGrid(
    entries: List<ChimahonLibraryMangaUiModel>,
    settings: ChimahonLibrarySettings,
    onMangaClick: (ChimahonLibraryMangaUiModel) -> Unit,
    modifier: Modifier = Modifier,
    selectedMangaIds: Set<Long> = emptySet(),
    onMangaLongClick: (ChimahonLibraryMangaUiModel) -> Unit = {},
    onContinueClick: (ChimahonLibraryMangaUiModel) -> Unit = onMangaClick,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    coverContent: @Composable BoxScope.(ChimahonLibraryMangaUiModel) -> Unit = {
        ChimahonLibraryCoverPlaceholder(title = it.title)
    },
    density: ChimahonLibraryCardDensity = settings.displayMode.libraryCardDensity(),
    showBookmarkedBadges: Boolean = true,
) {
    val minCellSize = when (settings.displayMode) {
        ChimahonLibraryDisplayMode.CompactGrid,
        ChimahonLibraryDisplayMode.CoverOnlyGrid,
        -> if (density == ChimahonLibraryCardDensity.Compact) 104.dp else 120.dp
        ChimahonLibraryDisplayMode.ComfortableGrid,
        ChimahonLibraryDisplayMode.ComfortableGridPanorama,
        -> if (density == ChimahonLibraryCardDensity.Compact) 124.dp else 148.dp
        ChimahonLibraryDisplayMode.List -> if (density == ChimahonLibraryCardDensity.Compact) 124.dp else 148.dp
    }
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minCellSize),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(if (density == ChimahonLibraryCardDensity.Compact) 8.dp else 12.dp),
        verticalArrangement = Arrangement.spacedBy(if (density == ChimahonLibraryCardDensity.Compact) 12.dp else 16.dp),
    ) {
        items(entries, key = { it.stableKey }) { manga ->
            ChimahonLibraryMangaGridCard(
                manga = manga,
                settings = settings,
                density = density,
                selected = manga.id in selectedMangaIds,
                onClick = { onMangaClick(manga) },
                onLongClick = { onMangaLongClick(manga) },
                onContinueClick = { onContinueClick(manga) },
                showBookmarkedBadges = showBookmarkedBadges,
                coverContent = { coverContent(manga) },
            )
        }
    }
}

@Composable
fun ChimahonLibraryMangaList(
    entries: List<ChimahonLibraryMangaUiModel>,
    settings: ChimahonLibrarySettings,
    onMangaClick: (ChimahonLibraryMangaUiModel) -> Unit,
    modifier: Modifier = Modifier,
    selectedMangaIds: Set<Long> = emptySet(),
    onMangaLongClick: (ChimahonLibraryMangaUiModel) -> Unit = {},
    onContinueClick: (ChimahonLibraryMangaUiModel) -> Unit = onMangaClick,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    coverContent: @Composable BoxScope.(ChimahonLibraryMangaUiModel) -> Unit = {
        ChimahonLibraryCoverPlaceholder(title = it.title)
    },
    density: ChimahonLibraryCardDensity = settings.displayMode.libraryCardDensity(),
    showBookmarkedBadges: Boolean = true,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(entries, key = { it.stableKey }) { manga ->
            ChimahonLibraryMangaListCard(
                manga = manga,
                settings = settings,
                density = density,
                selected = manga.id in selectedMangaIds,
                onClick = { onMangaClick(manga) },
                onLongClick = { onMangaLongClick(manga) },
                onContinueClick = { onContinueClick(manga) },
                showBookmarkedBadges = showBookmarkedBadges,
                coverContent = { coverContent(manga) },
            )
        }
    }
}

@Composable
fun ChimahonLibraryMangaCard(
    manga: ChimahonLibraryMangaUiModel,
    settings: ChimahonLibrarySettings,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    density: ChimahonLibraryCardDensity = settings.displayMode.libraryCardDensity(),
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonLibraryCoverPlaceholder(title = manga.title)
    },
    showBookmarkedBadges: Boolean = true,
) {
    if (settings.displayMode == ChimahonLibraryDisplayMode.List) {
        ChimahonLibraryMangaListCard(
            manga = manga,
            settings = settings,
            density = density,
            selected = selected,
            onClick = onClick,
            onLongClick = onLongClick,
            onContinueClick = onContinueClick,
            modifier = modifier,
            coverContent = coverContent,
            showBookmarkedBadges = showBookmarkedBadges,
        )
    } else {
        ChimahonLibraryMangaGridCard(
            manga = manga,
            settings = settings,
            density = density,
            selected = selected,
            onClick = onClick,
            onLongClick = onLongClick,
            onContinueClick = onContinueClick,
            modifier = modifier,
            coverContent = coverContent,
            showBookmarkedBadges = showBookmarkedBadges,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonLibraryMangaGridCard(
    manga: ChimahonLibraryMangaUiModel,
    settings: ChimahonLibrarySettings,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonLibraryCoverPlaceholder(title = manga.title)
    },
    density: ChimahonLibraryCardDensity = settings.displayMode.libraryCardDensity(),
    showBookmarkedBadges: Boolean = true,
) {
    val compact = density == ChimahonLibraryCardDensity.Compact
    Column(
        modifier = modifier
            .semantics { contentDescription = manga.title }
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(manga.coverAspectRatio(settings))
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
        ) {
            coverContent()
            ChimahonLibraryCoverScrim(modifier = Modifier.align(Alignment.BottomCenter))
            ChimahonLibraryBadgeStack(
                badges = manga.badges(settings, showBookmarkedBadges),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
            )
            if (selected) {
                ChimahonLibrarySelectedMarker(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                )
            }
            if (settings.displayMode == ChimahonLibraryDisplayMode.CoverOnlyGrid) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 8.dp, vertical = 7.dp),
                    text = manga.title,
                    color = Color.White,
                    fontSize = if (compact) 12.sp else 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (settings.displayMode != ChimahonLibraryDisplayMode.CoverOnlyGrid) {
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = manga.title,
                color = MaterialTheme.colors.onSurface,
                fontSize = if (compact) 13.sp else 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = if (settings.displayMode == ChimahonLibraryDisplayMode.CompactGrid) 2 else 3,
                overflow = TextOverflow.Ellipsis,
            )
            ChimahonLibraryMangaSecondaryLine(manga = manga, settings = settings, density = density)
        }
        if (!compact && manga.shouldShowContinueButton(settings)) {
            ChimahonLibraryContinueButton(
                modifier = Modifier.padding(top = 8.dp),
                text = manga.progressLabel ?: "Continue",
                onClick = onContinueClick,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonLibraryMangaListCard(
    manga: ChimahonLibraryMangaUiModel,
    settings: ChimahonLibrarySettings,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonLibraryCoverPlaceholder(title = manga.title)
    },
    density: ChimahonLibraryCardDensity = settings.displayMode.libraryCardDensity(),
    showBookmarkedBadges: Boolean = true,
) {
    val compact = density == ChimahonLibraryCardDensity.Compact
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
            modifier = Modifier.padding(
                horizontal = if (compact) 12.dp else 16.dp,
                vertical = if (compact) 6.dp else 8.dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 12.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(if (compact) 44.dp else 56.dp)
                    .height(if (compact) 64.dp else 80.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
            ) {
                coverContent()
                if (selected) {
                    ChimahonLibrarySelectedMarker(
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
                    fontSize = if (compact) 14.sp else 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                manga.subtitle?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        modifier = Modifier.padding(top = 2.dp),
                        text = it,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                ChimahonLibraryMangaSecondaryLine(manga = manga, settings = settings, density = density)
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    manga.badges(settings, showBookmarkedBadges).take(if (compact) 3 else 4).forEach { badge ->
                        ChimahonLibraryBadge(badge = badge)
                    }
                }
            }
            if (!compact && manga.shouldShowContinueButton(settings)) {
                ChimahonLibraryContinueButton(
                    text = "Read",
                    onClick = onContinueClick,
                )
            }
        }
    }
}

@Composable
fun ChimahonLibraryCoverPlaceholder(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.38f),
                        MaterialTheme.colors.secondary.copy(alpha = 0.22f),
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
private fun ChimahonLibraryMangaSecondaryLine(
    manga: ChimahonLibraryMangaUiModel,
    settings: ChimahonLibrarySettings,
    density: ChimahonLibraryCardDensity = settings.displayMode.libraryCardDensity(),
) {
    val parts = buildList {
        if (settings.showLatestChapter) manga.latestChapterLabel?.takeIf { it.isNotBlank() }?.let(::add)
        if (settings.showLastReadAt) manga.lastReadLabel?.takeIf { it.isNotBlank() }?.let(::add)
        manga.progressLabel?.let(::add)
    }.distinct()
    if (parts.isEmpty()) return
    Text(
        modifier = Modifier.padding(top = 2.dp),
        text = parts.joinToString(" - "),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
        fontSize = if (density == ChimahonLibraryCardDensity.Compact) 11.sp else 12.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ChimahonLibraryBadgeStack(
    badges: List<ChimahonLibraryBadgeModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        badges.take(3).forEach { badge ->
            ChimahonLibraryBadge(badge = badge)
        }
    }
}

@Composable
fun ChimahonLibraryBadge(
    badge: ChimahonLibraryBadgeModel,
    modifier: Modifier = Modifier,
) {
    val colors = badge.badgeColors()
    Surface(
        modifier = modifier.semantics { contentDescription = badge.contentDescription },
        shape = RoundedCornerShape(50),
        color = colors.first,
        contentColor = colors.second,
        elevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            badge.icon()?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                )
            }
            Text(
                text = badge.text,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChimahonLibrarySelectedMarker(modifier: Modifier = Modifier) {
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
private fun ChimahonLibraryContinueButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier.height(32.dp),
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
private fun ChimahonLibraryCoverScrim(modifier: Modifier = Modifier) {
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
private fun ChimahonLibraryBadgeModel.badgeColors(): Pair<Color, Color> {
    return when (kind) {
        ChimahonLibraryBadgeKind.Unread -> MaterialTheme.colors.primary to MaterialTheme.colors.onPrimary
        ChimahonLibraryBadgeKind.Downloaded -> Color(0xFF1B7F4A) to Color.White
        ChimahonLibraryBadgeKind.Bookmarked -> Color(0xFF7357C8) to Color.White
        ChimahonLibraryBadgeKind.Local -> MaterialTheme.colors.secondary to MaterialTheme.colors.onSecondary
        ChimahonLibraryBadgeKind.Language -> MaterialTheme.colors.surface to MaterialTheme.colors.onSurface
        ChimahonLibraryBadgeKind.Source -> MaterialTheme.colors.surface to MaterialTheme.colors.onSurface
        ChimahonLibraryBadgeKind.Tracking -> Color(0xFF5B5FC7) to Color.White
        ChimahonLibraryBadgeKind.Status -> MaterialTheme.colors.onSurface.copy(alpha = 0.12f) to MaterialTheme.colors.onSurface
        ChimahonLibraryBadgeKind.Warning -> MaterialTheme.colors.error to MaterialTheme.colors.onError
    }
}

private fun ChimahonLibraryBadgeModel.icon(): ImageVector? {
    return when (kind) {
        ChimahonLibraryBadgeKind.Downloaded -> Icons.Outlined.Download
        ChimahonLibraryBadgeKind.Bookmarked -> Icons.Outlined.Bookmark
        ChimahonLibraryBadgeKind.Local -> Icons.Outlined.CollectionsBookmark
        ChimahonLibraryBadgeKind.Language -> Icons.Outlined.Public
        ChimahonLibraryBadgeKind.Tracking -> Icons.Outlined.Bookmark
        ChimahonLibraryBadgeKind.Unread,
        ChimahonLibraryBadgeKind.Source,
        ChimahonLibraryBadgeKind.Status,
        ChimahonLibraryBadgeKind.Warning,
        -> null
    }
}

private fun ChimahonLibraryMangaUiModel.coverAspectRatio(settings: ChimahonLibrarySettings): Float {
    val ratio = if (coverRatio == ChimahonLibraryCoverRatio.Automatic) settings.coverAspectRatio else coverRatio
    return when (ratio) {
        ChimahonLibraryCoverRatio.Automatic -> when (settings.displayMode) {
            ChimahonLibraryDisplayMode.ComfortableGridPanorama -> 1.45f
            else -> 2f / 3f
        }
        ChimahonLibraryCoverRatio.Square -> 1f
        ChimahonLibraryCoverRatio.ThreeToFour -> 3f / 4f
        ChimahonLibraryCoverRatio.TwoToThree -> 2f / 3f
        ChimahonLibraryCoverRatio.Original -> 2f / 3f
    }.let { max(0.5f, it) }
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
