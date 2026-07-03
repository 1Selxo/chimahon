package app.chimahon.shared.animeui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonAnimeDetailScreen(
    state: ChimahonAnimeDetailUiState,
    actions: ChimahonAnimeActions,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 24.dp),
    coverContent: @Composable BoxScope.(ChimahonAnimeHeaderUiModel) -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = it.title)
    },
) {
    when (state.contentState) {
        ChimahonAnimeContentState.Loading -> {
            ChimahonAnimeLoadingState(
                title = "Loading anime",
                modifier = modifier.fillMaxSize(),
            )
        }
        ChimahonAnimeContentState.Error -> {
            ChimahonAnimeEmptyState(
                title = "Anime failed to load",
                subtitle = state.errorMessage ?: "Unknown error",
                actionLabel = "Retry",
                onActionClick = actions.onRetry,
                modifier = modifier.fillMaxSize(),
            )
        }
        ChimahonAnimeContentState.Empty -> {
            ChimahonAnimeEmptyState(
                title = "Anime not found",
                subtitle = "This entry is missing from the shared database.",
                modifier = modifier.fillMaxSize(),
            )
        }
        ChimahonAnimeContentState.Content -> {
            val anime = checkNotNull(state.anime)
            LazyColumn(
                modifier = modifier,
                contentPadding = contentPadding,
            ) {
                item(key = "anime-header") {
                    ChimahonAnimeDetailHeader(
                        anime = anime,
                        refreshing = state.refreshing,
                        coverContent = { coverContent(anime) },
                        actions = actions,
                    )
                }
                if (state.seasons.isNotEmpty()) {
                    item(key = "anime-seasons") {
                        ChimahonAnimeSeasonStrip(
                            seasons = state.seasons,
                            onSeasonClick = actions.onSeasonClick,
                        )
                    }
                }
                if (state.relatedAnime.isNotEmpty()) {
                    item(key = "anime-related") {
                        ChimahonRelatedAnimeStrip(
                            entries = state.relatedAnime,
                            onRelatedAnimeClick = actions.onRelatedAnimeClick,
                        )
                    }
                }
                item(key = "anime-episode-controls") {
                    ChimahonAnimeEpisodeControls(
                        visibleCount = state.visibleEpisodes.size,
                        totalCount = state.episodes.size,
                        missingCount = state.missingEpisodeCount,
                        sortState = state.sortState,
                        filterState = state.filterState,
                        onSortChange = actions.onSortChange,
                        onFilterClick = actions.onFilterClick,
                        onRefresh = actions.onRefresh,
                    )
                }
                if (state.visibleEpisodes.isEmpty()) {
                    item(key = "anime-episodes-empty") {
                        ChimahonAnimeEmptyState(
                            title = "No episodes match",
                            subtitle = if (state.filterState.hasActiveFilters || state.episodeQuery.isNotBlank()) {
                                "Clear filters or search to show more episodes."
                            } else {
                                "Refresh from source to fetch episodes."
                            },
                            actionLabel = "Refresh",
                            onActionClick = actions.onRefresh,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                        )
                    }
                } else {
                    items(
                        items = state.visibleEpisodes.withIndex().toList(),
                        key = { indexed -> indexed.value.stableLazyKey(indexed.index) },
                    ) { indexed ->
                        val episode = indexed.value
                        ChimahonAnimeEpisodeRow(
                            episode = episode,
                            selected = episode.id in state.selectedEpisodeIds,
                            displayMode = state.displayMode,
                            onClick = { actions.onEpisodeClick(episode) },
                            onLongClick = { actions.onEpisodeLongClick(episode) },
                            onDownloadClick = { actions.onEpisodeDownloadClick(episode) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonAnimeDetailHeader(
    anime: ChimahonAnimeHeaderUiModel,
    actions: ChimahonAnimeActions,
    modifier: Modifier = Modifier,
    refreshing: Boolean = false,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonAnimeCoverPlaceholder(title = anime.title)
    },
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colors.primary.copy(alpha = 0.24f),
                                MaterialTheme.colors.surface,
                            ),
                        ),
                    ),
            )
            IconButton(
                onClick = actions.onNavigateUp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colors.onSurface,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 42.dp, end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ChimahonAnimeCoverFrame(
                    title = anime.title,
                    modifier = Modifier
                        .width(112.dp)
                        .aspectRatio(ChimahonAnimeCoverRatio.Poster.ratio),
                    content = coverContent,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = anime.title,
                        color = MaterialTheme.colors.onSurface,
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = anime.sourceLine,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.66f),
                        style = MaterialTheme.typography.caption,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    anime.creatorLine?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.66f),
                            style = MaterialTheme.typography.caption,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    ChimahonAnimeMetaChips(anime = anime, refreshing = refreshing)
                }
            }
        }
        ChimahonAnimeHeaderActionRow(anime = anime, actions = actions)
        ChimahonAnimeDescriptionBlock(anime = anime, actions = actions)
    }
}

@Composable
fun ChimahonAnimeMetaChips(
    anime: ChimahonAnimeHeaderUiModel,
    refreshing: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        listOfNotNull(
            anime.status,
            anime.totalEpisodeCount.takeIf { it > 0 }?.let { "$it episodes" },
            anime.unseenCount.takeIf { it > 0 }?.let { "$it unseen" },
            anime.downloadedCount.takeIf { it > 0 }?.let { "$it downloaded" },
            anime.nextAiringLabel,
            anime.totalDurationLabel,
            if (refreshing) "Refreshing" else null,
        ).forEach { label ->
            ChimahonAnimeTinyChip(text = label)
        }
    }
}

@Composable
fun ChimahonAnimeHeaderActionRow(
    anime: ChimahonAnimeHeaderUiModel,
    actions: ChimahonAnimeActions,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top,
    ) {
        ChimahonAnimeActionButton(
            title = if (anime.favorite) "Library" else "Add",
            icon = if (anime.favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            selected = anime.favorite,
            onClick = { actions.onFavoriteClick(anime) },
        )
        ChimahonAnimeActionButton(
            title = "Tracking",
            icon = Icons.Outlined.CheckCircle,
            valueLabel = anime.trackedCount.takeIf { it > 0 }?.toString(),
            onClick = { actions.onTrackingClick(anime) },
        )
        ChimahonAnimeActionButton(
            title = "WebView",
            icon = Icons.Outlined.OpenInBrowser,
            enabled = anime.webUrl != null,
            onClick = { actions.onWebViewClick(anime) },
        )
        ChimahonAnimeActionButton(
            title = "Interval",
            icon = Icons.Outlined.Schedule,
            valueLabel = anime.nextUpdateLabel,
            enabled = anime.favorite,
            onClick = { actions.onEditIntervalClick(anime) },
        )
        ChimahonAnimeActionButton(
            title = "Skip intro",
            icon = Icons.Outlined.Timer,
            selected = anime.skipIntroEnabled,
            enabled = anime.favorite,
            onClick = { actions.onSkipIntroClick(anime) },
        )
    }
    ChimahonAnimePrimaryButton(
        text = "Continue watching",
        icon = Icons.Filled.PlayArrow,
        onClick = { actions.onContinueWatchingClick(anime) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    )
}

@Composable
fun ChimahonAnimeDescriptionBlock(
    anime: ChimahonAnimeHeaderUiModel,
    actions: ChimahonAnimeActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (!anime.description.isNullOrBlank()) {
            Text(
                text = anime.description,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.82f),
                style = MaterialTheme.typography.body2,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (anime.tags.isNotEmpty()) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                anime.tags.forEach { tag ->
                    ChimahonAnimeTinyChip(
                        text = tag,
                        onClick = { actions.onTagClick(tag) },
                    )
                }
            }
        }
        if (anime.categoryLabels.isNotEmpty()) {
            Text(
                text = anime.categoryLabels.joinToString(" / "),
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

@Composable
fun ChimahonAnimeSeasonStrip(
    seasons: List<ChimahonAnimeSeasonUiModel>,
    onSeasonClick: (ChimahonAnimeSeasonUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ChimahonAnimeSectionHeader(title = "Seasons")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(seasons, key = { it.id }) { season ->
                ChimahonAnimeSeasonCard(
                    season = season,
                    onClick = { onSeasonClick(season) },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonAnimeSeasonCard(
    season: ChimahonAnimeSeasonUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .width(184.dp)
            .clip(RoundedCornerShape(8.dp))
            .semantics { contentDescription = season.title },
        color = if (season.selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colors.surface
        },
        elevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(onClick = onClick, onLongClick = onClick)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonAnimeCoverFrame(
                title = season.title,
                modifier = Modifier
                    .size(width = 48.dp, height = 68.dp)
                    .clip(RoundedCornerShape(5.dp)),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = season.title,
                    style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = season.subtitle ?: season.countLabel,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (season.unseenCount > 0) {
                    ChimahonAnimeTinyChip(text = "${season.unseenCount} unseen", selected = true)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonRelatedAnimeStrip(
    entries: List<ChimahonRelatedAnimeUiModel>,
    onRelatedAnimeClick: (ChimahonRelatedAnimeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ChimahonAnimeSectionHeader(title = "Related anime")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(entries, key = { it.id }) { entry ->
                Column(
                    modifier = Modifier
                        .width(118.dp)
                        .combinedClickable(
                            onClick = { onRelatedAnimeClick(entry) },
                            onLongClick = { onRelatedAnimeClick(entry) },
                        ),
                ) {
                    ChimahonAnimeCoverFrame(
                        title = entry.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(ChimahonAnimeCoverRatio.Poster.ratio),
                    )
                    Text(
                        modifier = Modifier.padding(top = 6.dp),
                        text = entry.title,
                        style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    entry.subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.overline,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChimahonAnimeEpisodeControls(
    visibleCount: Int,
    totalCount: Int,
    missingCount: Int,
    sortState: ChimahonAnimeEpisodeSortState,
    filterState: ChimahonAnimeEpisodeFilterState,
    onSortChange: (ChimahonAnimeEpisodeSortState) -> Unit,
    onFilterClick: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Episodes",
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = buildList {
                        add("$visibleCount / $totalCount shown")
                        if (missingCount > 0) add("$missingCount missing")
                        if (filterState.activeCount > 0) add("${filterState.activeCount} filter(s)")
                    }.joinToString(" - "),
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
                )
            }
            ChimahonAnimeIconButton(Icons.Outlined.FilterList, "Filter episodes", onFilterClick)
            ChimahonAnimeIconButton(Icons.Outlined.Refresh, "Refresh episodes", onRefresh)
            ChimahonAnimeIconButton(
                icon = sortState.sort.icon,
                contentDescription = "Toggle episode sort",
                onClick = { onSortChange(sortState.toggledDirection()) },
            )
        }
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ChimahonAnimeTinyChip(text = sortState.sort.title, selected = true)
            ChimahonAnimeTinyChip(text = sortState.directionTitle, selected = true)
            ChimahonAnimeEpisodeSort.entries.forEach { sort ->
                if (sort != sortState.sort) {
                    ChimahonAnimeTinyChip(
                        text = sort.title,
                        onClick = { onSortChange(sortState.withSort(sort)) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonAnimeEpisodeRow(
    episode: ChimahonAnimeEpisodeUiModel,
    displayMode: ChimahonAnimeEpisodeDisplayMode,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (episode.seen) {
                            MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
                        } else {
                            MaterialTheme.colors.primary.copy(alpha = 0.16f)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = episode.episodeMarker.take(4),
                    color = if (episode.seen) {
                        MaterialTheme.colors.onSurface.copy(alpha = 0.58f)
                    } else {
                        MaterialTheme.colors.primary
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.displayTitle(displayMode),
                    color = MaterialTheme.colors.onSurface.copy(alpha = if (episode.seen) 0.56f else 0.92f),
                    style = MaterialTheme.typography.body2.copy(
                        fontWeight = if (episode.seen) FontWeight.Normal else FontWeight.SemiBold,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                val metadata = episode.metadataParts()
                if (metadata.isNotEmpty()) {
                    Text(
                        text = metadata.joinToString(" - "),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.58f),
                        style = MaterialTheme.typography.caption,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (episode.downloadState == ChimahonAnimeDownloadUiState.Downloading) {
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = (episode.downloadProgress / 100f).coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (episode.bookmarked) {
                Icon(
                    imageVector = Icons.Outlined.Bookmark,
                    contentDescription = "Bookmarked",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
            if (episode.filler) {
                ChimahonAnimeTinyChip(text = "Filler")
            }
            ChimahonAnimeEpisodeDownloadButton(
                state = episode.downloadState,
                onClick = onDownloadClick,
            )
        }
    }
}

@Composable
fun ChimahonAnimeEpisodeDownloadButton(
    state: ChimahonAnimeDownloadUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = when (state) {
        ChimahonAnimeDownloadUiState.Downloaded -> Icons.Outlined.DeleteOutline
        ChimahonAnimeDownloadUiState.Downloading,
        ChimahonAnimeDownloadUiState.Queued,
        -> Icons.Outlined.MoreVert
        ChimahonAnimeDownloadUiState.Error -> Icons.Outlined.Refresh
        ChimahonAnimeDownloadUiState.NotDownloaded -> Icons.Outlined.Download
    }
    IconButton(
        onClick = onClick,
        modifier = modifier.size(42.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = state.title,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
        )
    }
}
