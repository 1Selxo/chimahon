package app.chimahon.shared.animeui

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonAnimeLibraryScreen(
    state: ChimahonAnimeLibraryUiState,
    onAnimeClick: (ChimahonAnimeLibraryEntryUiModel) -> Unit,
    modifier: Modifier = Modifier,
    onAnimeLongClick: (ChimahonAnimeLibraryEntryUiModel) -> Unit = {},
    onContinueWatchingClick: (ChimahonAnimeLibraryEntryUiModel) -> Unit = onAnimeClick,
    onCategoryClick: (ChimahonAnimeCategoryUiModel) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(bottom = 24.dp),
    coverContent: @Composable BoxScope.(ChimahonAnimeLibraryEntryUiModel) -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = it.title)
    },
) {
    Column(modifier = modifier.fillMaxSize()) {
        ChimahonAnimeLibraryHeader(
            state = state,
            onSearchClick = onSearchClick,
            onFilterClick = onFilterClick,
            onRefreshClick = onRefreshClick,
            onSettingsClick = onSettingsClick,
        )
        ChimahonAnimeLibraryCategoryTabs(
            categories = state.categories,
            onCategoryClick = onCategoryClick,
        )
        when {
            state.loading && state.entries.isEmpty() -> {
                ChimahonAnimeLoadingState(
                    title = "Loading anime library",
                    modifier = Modifier.weight(1f),
                )
            }
            state.errorMessage != null && state.entries.isEmpty() -> {
                ChimahonAnimeEmptyState(
                    title = "Anime library failed to load",
                    subtitle = state.errorMessage,
                    actionLabel = "Retry",
                    onActionClick = onRefreshClick,
                    modifier = Modifier.weight(1f),
                )
            }
            state.entries.isEmpty() -> {
                ChimahonAnimeEmptyState(
                    title = "Your anime library is empty",
                    subtitle = "Add anime from Browse to start tracking episodes.",
                    modifier = Modifier.weight(1f),
                )
            }
            state.settings.displayMode == ChimahonAnimeLibraryDisplayMode.List -> {
                ChimahonAnimeLibraryList(
                    entries = state.entries,
                    settings = state.settings,
                    selectedAnimeIds = state.selectedAnimeIds,
                    onAnimeClick = onAnimeClick,
                    onAnimeLongClick = onAnimeLongClick,
                    onContinueWatchingClick = onContinueWatchingClick,
                    contentPadding = contentPadding,
                    coverContent = coverContent,
                    modifier = Modifier.weight(1f),
                )
            }
            else -> {
                ChimahonAnimeLibraryGrid(
                    entries = state.entries,
                    settings = state.settings,
                    selectedAnimeIds = state.selectedAnimeIds,
                    onAnimeClick = onAnimeClick,
                    onAnimeLongClick = onAnimeLongClick,
                    onContinueWatchingClick = onContinueWatchingClick,
                    contentPadding = contentPadding,
                    coverContent = coverContent,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun ChimahonAnimeLibraryHeader(
    state: ChimahonAnimeLibraryUiState,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.background,
        elevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 8.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (state.isSelectionMode) "${state.selectedAnimeIds.size} selected" else state.title,
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.h6,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val subtitle = state.subtitle ?: "${state.visibleCount} anime"
                Text(
                    text = subtitle,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.62f),
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            ChimahonAnimeIconButton(Icons.Outlined.Search, "Search anime", onSearchClick)
            ChimahonAnimeIconButton(Icons.Outlined.FilterList, "Filter anime", onFilterClick)
            ChimahonAnimeIconButton(Icons.Outlined.Refresh, "Refresh anime library", onRefreshClick)
            ChimahonAnimeIconButton(Icons.Outlined.Settings, "Library settings", onSettingsClick)
        }
    }
}

@Composable
fun ChimahonAnimeLibraryCategoryTabs(
    categories: List<ChimahonAnimeCategoryUiModel>,
    onCategoryClick: (ChimahonAnimeCategoryUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (categories.isEmpty()) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        categories.forEach { category ->
            ChimahonAnimeTinyChip(
                text = category.displayTitle,
                selected = category.selected,
                onClick = { onCategoryClick(category) },
            )
        }
    }
}

@Composable
fun ChimahonAnimeLibraryGrid(
    entries: List<ChimahonAnimeLibraryEntryUiModel>,
    settings: ChimahonAnimeLibrarySettings,
    onAnimeClick: (ChimahonAnimeLibraryEntryUiModel) -> Unit,
    modifier: Modifier = Modifier,
    selectedAnimeIds: Set<String> = emptySet(),
    onAnimeLongClick: (ChimahonAnimeLibraryEntryUiModel) -> Unit = {},
    onContinueWatchingClick: (ChimahonAnimeLibraryEntryUiModel) -> Unit = onAnimeClick,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    coverContent: @Composable BoxScope.(ChimahonAnimeLibraryEntryUiModel) -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = it.title)
    },
) {
    val minCellSize = when (settings.displayMode) {
        ChimahonAnimeLibraryDisplayMode.CompactGrid -> 104.dp
        ChimahonAnimeLibraryDisplayMode.ComfortableGrid -> 136.dp
        ChimahonAnimeLibraryDisplayMode.CoverOnlyGrid -> 92.dp
        ChimahonAnimeLibraryDisplayMode.List -> 136.dp
    }
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(minCellSize),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(entries, key = { it.id }) { anime ->
            ChimahonAnimeLibraryGridCard(
                anime = anime,
                settings = settings,
                selected = anime.id in selectedAnimeIds,
                onClick = { onAnimeClick(anime) },
                onLongClick = { onAnimeLongClick(anime) },
                onContinueWatchingClick = { onContinueWatchingClick(anime) },
                coverContent = { coverContent(anime) },
            )
        }
    }
}

@Composable
fun ChimahonAnimeLibraryList(
    entries: List<ChimahonAnimeLibraryEntryUiModel>,
    settings: ChimahonAnimeLibrarySettings,
    onAnimeClick: (ChimahonAnimeLibraryEntryUiModel) -> Unit,
    modifier: Modifier = Modifier,
    selectedAnimeIds: Set<String> = emptySet(),
    onAnimeLongClick: (ChimahonAnimeLibraryEntryUiModel) -> Unit = {},
    onContinueWatchingClick: (ChimahonAnimeLibraryEntryUiModel) -> Unit = onAnimeClick,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    coverContent: @Composable BoxScope.(ChimahonAnimeLibraryEntryUiModel) -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = it.title)
    },
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        items(entries, key = { it.id }) { anime ->
            ChimahonAnimeLibraryListRow(
                anime = anime,
                settings = settings,
                selected = anime.id in selectedAnimeIds,
                onClick = { onAnimeClick(anime) },
                onLongClick = { onAnimeLongClick(anime) },
                onContinueWatchingClick = { onContinueWatchingClick(anime) },
                coverContent = { coverContent(anime) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonAnimeLibraryGridCard(
    anime: ChimahonAnimeLibraryEntryUiModel,
    settings: ChimahonAnimeLibrarySettings,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onContinueWatchingClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = anime.title)
    },
) {
    Column(
        modifier = modifier
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(settings.coverRatio.ratio)
                .clip(RoundedCornerShape(6.dp)),
        ) {
            ChimahonAnimeCoverFrame(
                title = anime.title,
                selected = selected,
                ratio = settings.coverRatio,
                content = coverContent,
                modifier = Modifier.fillMaxSize(),
            )
            ChimahonAnimeBadgeStack(
                badges = anime.badges(settings),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(5.dp),
            )
            if (settings.displayMode == ChimahonAnimeLibraryDisplayMode.CoverOnlyGrid) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 7.dp, vertical = 6.dp),
                    text = anime.title,
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (settings.displayMode != ChimahonAnimeLibraryDisplayMode.CoverOnlyGrid) {
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = anime.title,
                color = MaterialTheme.colors.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = if (settings.displayMode == ChimahonAnimeLibraryDisplayMode.CompactGrid) 2 else 3,
                overflow = TextOverflow.Ellipsis,
            )
            ChimahonAnimeLibrarySecondaryLine(anime = anime, settings = settings)
        }
        if (anime.shouldShowContinueButton(settings)) {
            ChimahonAnimeContinueButton(
                modifier = Modifier.padding(top = 8.dp),
                text = anime.progressLabel ?: "Continue",
                onClick = onContinueWatchingClick,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonAnimeLibraryListRow(
    anime: ChimahonAnimeLibraryEntryUiModel,
    settings: ChimahonAnimeLibrarySettings,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onContinueWatchingClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = anime.title)
    },
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        color = if (selected) MaterialTheme.colors.primary.copy(alpha = 0.10f) else MaterialTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(58.dp)
                    .aspectRatio(settings.coverRatio.ratio)
                    .clip(RoundedCornerShape(5.dp)),
            ) {
                ChimahonAnimeCoverFrame(
                    title = anime.title,
                    selected = selected,
                    ratio = settings.coverRatio,
                    content = coverContent,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = anime.title,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                ChimahonAnimeLibrarySecondaryLine(anime = anime, settings = settings)
                val tertiary = listOfNotNull(
                    anime.latestEpisodeLabel?.takeIf(String::isNotBlank),
                    anime.lastWatchedLabel?.takeIf(String::isNotBlank),
                    anime.nextAiringLabel?.takeIf(String::isNotBlank),
                ).joinToString(" | ")
                if (tertiary.isNotBlank()) {
                    Text(
                        text = tertiary,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.54f),
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            ChimahonAnimeBadgeStack(badges = anime.badges(settings))
            if (anime.shouldShowContinueButton(settings)) {
                ChimahonAnimeIconButton(
                    icon = Icons.Filled.PlayArrow,
                    contentDescription = "Continue watching",
                    onClick = onContinueWatchingClick,
                    selected = true,
                )
            }
        }
    }
}

@Composable
private fun ChimahonAnimeLibrarySecondaryLine(
    anime: ChimahonAnimeLibraryEntryUiModel,
    settings: ChimahonAnimeLibrarySettings,
) {
    val parts = buildList {
        anime.progressLabel?.let { add(it) }
        anime.status?.takeIf(String::isNotBlank)?.let { add(it) }
        anime.sourceName?.takeIf { settings.showSourceBadges && it.isNotBlank() }?.let { add(it) }
        anime.language?.takeIf { settings.showLanguageBadges && it.isNotBlank() }?.uppercase()?.let { add(it) }
    }
    if (parts.isEmpty()) return
    Text(
        modifier = Modifier.padding(top = 2.dp),
        text = parts.joinToString(" | "),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
        fontSize = 12.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ChimahonAnimeContinueButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.94f),
            contentColor = MaterialTheme.colors.onPrimary,
        ),
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = null,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
