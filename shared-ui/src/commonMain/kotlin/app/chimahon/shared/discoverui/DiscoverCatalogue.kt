package app.chimahon.shared.discoverui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonDiscoverCataloguePane(
    state: ChimahonDiscoverCatalogueState,
    actions: ChimahonDiscoverCatalogueActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
    cover: @Composable (ChimahonDiscoverMediaResult) -> Unit = { media ->
        ChimahonDiscoverCoverPlaceholder(media = media, colors = colors)
    },
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        ChimahonDiscoverSourceDetailTopBar(
            state = state,
            actions = actions,
            colors = colors,
        )
        val source = state.source
        if (source != null && (state.health != ChimahonDiscoverSourceHealth.Ready || !state.healthMessage.isNullOrBlank())) {
            ChimahonDiscoverHealthBanner(
                title = source.name,
                subtitle = state.healthMessage ?: source.healthMessage ?: state.health.title,
                health = state.health,
                action = if (state.health == ChimahonDiscoverSourceHealth.Error) "Retry" else null,
                onAction = if (state.health == ChimahonDiscoverSourceHealth.Error) actions.onRefresh else null,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                colors = colors,
            )
        }
        when {
            state.loading && state.results.isEmpty() -> {
                ChimahonDiscoverHealthBanner(
                    title = "Loading ${source?.contentType?.pluralTitle ?: "catalogue"}",
                    subtitle = "Fetching ${state.mode.title.lowercase()} entries.",
                    health = ChimahonDiscoverSourceHealth.Loading,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    colors = colors,
                )
            }
            state.errorMessage != null && state.results.isEmpty() -> {
                ChimahonDiscoverHealthBanner(
                    title = "Catalogue fetch failed",
                    subtitle = state.errorMessage,
                    health = ChimahonDiscoverSourceHealth.Error,
                    action = "Retry",
                    onAction = actions.onRefresh,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    colors = colors,
                )
            }
            state.visibleResults.isEmpty() -> {
                ChimahonDiscoverEmptyCatalogueState(
                    contentType = source?.contentType ?: ChimahonDiscoverContentType.Manga,
                    mode = state.mode,
                    query = state.query,
                    modifier = Modifier.fillMaxSize(),
                    colors = colors,
                )
            }
            state.displayMode == ChimahonDiscoverDisplayMode.Grid -> {
                ChimahonDiscoverMediaGrid(
                    state = state,
                    actions = actions,
                    colors = colors,
                    cover = cover,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            else -> {
                ChimahonDiscoverMediaList(
                    state = state,
                    compact = state.displayMode == ChimahonDiscoverDisplayMode.CompactList,
                    actions = actions,
                    colors = colors,
                    cover = cover,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun ChimahonDiscoverSourceDetailTopBar(
    state: ChimahonDiscoverCatalogueState,
    actions: ChimahonDiscoverCatalogueActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    val source = state.source
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(1.dp, colors.divider),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp)
                .padding(start = 4.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonDiscoverIconButton(
                icon = ChimahonDiscoverIcon.Back,
                contentDescription = "Back",
                onClick = actions.onBack,
                colors = colors,
            )
            if (source != null) {
                ChimahonDiscoverIconTile(
                    marker = source.primaryMarker,
                    icon = source.contentType.icon,
                    size = 42.dp,
                    selected = source.pinned,
                    colors = colors,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp),
            ) {
                ChimahonDiscoverLabel(
                    text = source?.name ?: "Source",
                    color = colors.content,
                    size = 16,
                    weight = FontWeight.Bold,
                )
                ChimahonDiscoverLabel(
                    text = state.sourceDetailSubtitle(),
                    color = colors.secondaryContent,
                    size = 12,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            if (source != null) {
                ChimahonDiscoverIconButton(
                    icon = ChimahonDiscoverIcon.Star,
                    contentDescription = if (source.pinned) "Unpin source" else "Pin source",
                    selected = source.pinned,
                    onClick = { actions.onToggleSourcePinned(source) },
                    colors = colors,
                )
                ChimahonDiscoverIconButton(
                    icon = ChimahonDiscoverIcon.Settings,
                    contentDescription = "Source settings",
                    onClick = { actions.onOpenSourceSettings(source) },
                    colors = colors,
                )
            }
            ChimahonDiscoverIconButton(
                icon = ChimahonDiscoverIcon.Refresh,
                contentDescription = "Refresh catalogue",
                onClick = actions.onRefresh,
                loading = state.refreshing,
                colors = colors,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonDiscoverSearchField(
                query = state.query,
                onQueryChange = actions.onSearchQueryChange,
                placeholder = "Search ${source?.contentType?.pluralTitle?.lowercase() ?: "catalogue"}",
                modifier = Modifier.weight(1f),
                colors = colors,
            )
        }
        ChimahonDiscoverChipRow(
            chips = state.availableModes().map { it.title },
            selected = state.mode.title,
            onSelect = { title ->
                state.availableModes().firstOrNull { it.title == title }?.let(actions.onModeChange)
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            colors = colors,
        )
        ChimahonDiscoverSectionHeader(
            title = "Results",
            count = state.results.size,
            detail = if (state.hasNextPage) "More available" else null,
            colors = colors,
        )
    }
}

@Composable
fun ChimahonDiscoverMediaGrid(
    state: ChimahonDiscoverCatalogueState,
    actions: ChimahonDiscoverCatalogueActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
    cover: @Composable (ChimahonDiscoverMediaResult) -> Unit = { media ->
        ChimahonDiscoverCoverPlaceholder(media = media, colors = colors)
    },
) {
    val gridState = rememberLazyGridState()
    ChimahonDiscoverAutoLoadEffect(
        gridState = gridState,
        state = state.autoLoadState,
        onLoadMore = actions.onLoadMore,
    )
    LazyVerticalGrid(
        columns = GridCells.Adaptive(142.dp),
        state = gridState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        gridItems(
            items = state.visibleResults.withDiscoverStableKeys(),
            key = { keyed -> keyed.lazyKey },
        ) { keyed ->
            ChimahonDiscoverMediaGridCard(
                media = keyed.media,
                onClick = { actions.onOpenResult(keyed.media) },
                onLongClick = { actions.onLongPressResult(keyed.media) },
                colors = colors,
                cover = cover,
            )
        }
        item(
            key = "discover-grid-footer",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            ChimahonDiscoverLoadFooter(
                state = state.autoLoadState,
                onLoadMore = actions.onLoadMore,
                modifier = Modifier.padding(vertical = 8.dp),
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDiscoverMediaList(
    state: ChimahonDiscoverCatalogueState,
    compact: Boolean,
    actions: ChimahonDiscoverCatalogueActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
    cover: @Composable (ChimahonDiscoverMediaResult) -> Unit = { media ->
        ChimahonDiscoverCoverPlaceholder(media = media, colors = colors)
    },
) {
    val listState = rememberLazyListState()
    ChimahonDiscoverAutoLoadEffect(
        listState = listState,
        state = state.autoLoadState,
        onLoadMore = actions.onLoadMore,
    )
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        items(
            items = state.visibleResults.withDiscoverStableKeys(),
            key = { keyed -> keyed.lazyKey },
        ) { keyed ->
            ChimahonDiscoverMediaListRow(
                media = keyed.media,
                compact = compact,
                onClick = { actions.onOpenResult(keyed.media) },
                onLongClick = { actions.onLongPressResult(keyed.media) },
                colors = colors,
                cover = cover,
            )
        }
        item(key = "discover-list-footer") {
            ChimahonDiscoverLoadFooter(
                state = state.autoLoadState,
                onLoadMore = actions.onLoadMore,
                modifier = Modifier.padding(vertical = 8.dp),
                colors = colors,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonDiscoverMediaGridCard(
    media: ChimahonDiscoverMediaResult,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
    cover: @Composable (ChimahonDiscoverMediaResult) -> Unit = { item ->
        ChimahonDiscoverCoverPlaceholder(media = item, colors = colors)
    },
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface)
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(if (media.contentType == ChimahonDiscoverContentType.Anime) 16f / 10f else 2f / 3f)
                .clip(RoundedCornerShape(6.dp))
                .background(colors.surfaceVariant),
        ) {
            cover(media)
            ChimahonDiscoverMediaCardScrim(
                media = media,
                colors = colors,
                modifier = Modifier.align(Alignment.BottomStart),
            )
        }
        ChimahonDiscoverLabel(
            text = media.title,
            color = colors.content,
            size = 13,
            weight = FontWeight.SemiBold,
            maxLines = 2,
            modifier = Modifier.padding(top = 7.dp),
        )
        media.primaryMetaLine?.let {
            ChimahonDiscoverLabel(
                text = it,
                color = colors.secondaryContent,
                size = 11,
                maxLines = 1,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        ChimahonDiscoverBadgeRow(
            badges = media.effectiveBadges().take(3),
            modifier = Modifier.padding(top = 6.dp),
            colors = colors,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChimahonDiscoverMediaListRow(
    media: ChimahonDiscoverMediaResult,
    compact: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
    cover: @Composable (ChimahonDiscoverMediaResult) -> Unit = { item ->
        ChimahonDiscoverCoverPlaceholder(media = item, colors = colors)
    },
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (compact) 72.dp else 96.dp)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .padding(horizontal = 16.dp, vertical = if (compact) 8.dp else 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(if (compact) 46.dp else 62.dp)
                    .aspectRatio(if (media.contentType == ChimahonDiscoverContentType.Anime) 16f / 10f else 2f / 3f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.surfaceVariant),
            ) {
                cover(media)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp),
            ) {
                ChimahonDiscoverLabel(
                    text = media.title,
                    color = colors.content,
                    size = if (compact) 13 else 14,
                    weight = FontWeight.SemiBold,
                    maxLines = if (compact) 1 else 2,
                )
                media.primaryMetaLine?.let {
                    ChimahonDiscoverLabel(
                        text = it,
                        color = colors.secondaryContent,
                        size = 12,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                media.secondaryMetaLine?.let {
                    ChimahonDiscoverLabel(
                        text = it,
                        color = colors.secondaryContent,
                        size = 11,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                if (!compact) {
                    ChimahonDiscoverBadgeRow(
                        badges = media.effectiveBadges().take(4),
                        modifier = Modifier.padding(top = 6.dp),
                        colors = colors,
                    )
                }
            }
            ChimahonDiscoverIconButton(
                icon = media.contentType.icon,
                contentDescription = "More for ${media.title}",
                onClick = onLongClick,
                colors = colors,
            )
        }
        ChimahonDiscoverDivider(startIndent = if (compact) 74.dp else 90.dp, colors = colors)
    }
}

@Composable
fun ChimahonDiscoverCoverPlaceholder(
    media: ChimahonDiscoverMediaResult,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = media.contentType.icon.imageVector,
                contentDescription = null,
                tint = colors.secondaryContent,
                modifier = Modifier.size(28.dp),
            )
            ChimahonDiscoverLabel(
                text = media.marker,
                color = colors.secondaryContent,
                size = 12,
                weight = FontWeight.Bold,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
fun ChimahonDiscoverLoadFooter(
    state: ChimahonDiscoverAutoLoadState,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    if (!state.hasMore && state.visibleCount > 0) {
        ChimahonDiscoverLabel(
            text = "${state.visibleCount} shown",
            color = colors.secondaryContent,
            size = 11,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        )
        return
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        color = colors.surface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.divider),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = state.hasMore && !state.loading, onClick = onLoadMore)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = colors.primary,
                )
            } else {
                Icon(
                    imageVector = ChimahonDiscoverIcon.Refresh.imageVector,
                    contentDescription = null,
                    tint = colors.secondaryContent,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                ChimahonDiscoverLabel(
                    text = if (state.loading) "Loading more" else "More results",
                    color = colors.content,
                    size = 13,
                    weight = FontWeight.SemiBold,
                )
                ChimahonDiscoverLabel(
                    text = "${state.visibleCount} of ${state.totalCount.coerceAtLeast(state.visibleCount)} visible",
                    color = colors.secondaryContent,
                    size = 11,
                    modifier = Modifier.padding(top = 1.dp),
                )
            }
        }
    }
}

@Composable
fun ChimahonDiscoverEmptyCatalogueState(
    contentType: ChimahonDiscoverContentType,
    mode: ChimahonDiscoverBrowseMode,
    query: String,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ChimahonDiscoverIconTile(
                marker = contentType.shortLabel,
                icon = contentType.icon,
                size = 58.dp,
                selected = true,
                colors = colors,
            )
            ChimahonDiscoverLabel(
                text = if (query.isBlank()) "No ${contentType.pluralTitle.lowercase()} found" else "No matching ${contentType.pluralTitle.lowercase()}",
                color = colors.content,
                size = 17,
                weight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
            )
            ChimahonDiscoverLabel(
                text = "${mode.title} did not return any entries for this source.",
                color = colors.secondaryContent,
                size = 13,
                maxLines = 2,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}

@Composable
private fun ChimahonDiscoverMediaCardScrim(
    media: ChimahonDiscoverMediaResult,
    colors: ChimahonDiscoverColors,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.00f),
                        Color.Black.copy(alpha = 0.58f),
                    ),
                ),
            )
            .padding(6.dp),
    ) {
        ChimahonDiscoverChip(
            text = media.contentType.shortLabel,
            selected = true,
            colors = colors,
        )
    }
}

private fun ChimahonDiscoverCatalogueState.sourceDetailSubtitle(): String {
    val currentSource = this.source
    return listOfNotNull(
        currentSource?.contentType?.title,
        mode.title,
        query.takeIf(String::isNotBlank)?.let { "\"$it\"" },
        "${visibleResults.size} shown",
        if (loadingMore) "loading more" else null,
    ).joinToString(" - ").ifBlank { "Catalogue" }
}

private fun ChimahonDiscoverCatalogueState.availableModes(): List<ChimahonDiscoverBrowseMode> {
    val currentSource = this.source
    return buildList {
        add(ChimahonDiscoverBrowseMode.Popular)
        if (currentSource?.supportsLatest != false) add(ChimahonDiscoverBrowseMode.Latest)
        add(ChimahonDiscoverBrowseMode.Search)
        if (currentSource?.contentType == ChimahonDiscoverContentType.LightNovel) add(ChimahonDiscoverBrowseMode.Library)
    }
}

private data class ChimahonDiscoverKeyedMedia(
    val media: ChimahonDiscoverMediaResult,
    val lazyKey: String,
)

private fun List<ChimahonDiscoverMediaResult>.withDiscoverStableKeys(): List<ChimahonDiscoverKeyedMedia> {
    if (isEmpty()) return emptyList()
    val duplicateKeys = groupingBy { it.stableKey }.eachCount().filterValues { it > 1 }.keys
    if (duplicateKeys.isEmpty()) {
        return map { media -> ChimahonDiscoverKeyedMedia(media = media, lazyKey = media.stableKey) }
    }
    val seenDuplicates = mutableMapOf<String, Int>()
    return map { media ->
        val baseKey = media.stableKey
        val lazyKey = if (baseKey in duplicateKeys) {
            val occurrence = seenDuplicates.getOrPut(baseKey) { 0 }
            seenDuplicates[baseKey] = occurrence + 1
            "$baseKey#${media.title}:${occurrence}"
        } else {
            baseKey
        }
        ChimahonDiscoverKeyedMedia(media = media, lazyKey = lazyKey)
    }
}
