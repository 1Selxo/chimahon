package app.chimahon.shared.discoverui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonDiscoverSourcesPane(
    state: ChimahonDiscoverSourceListState,
    actions: ChimahonDiscoverSourceActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        ChimahonDiscoverSourceFilterStrip(
            state = state,
            actions = actions,
            colors = colors,
        )
        val errorMessage = state.errorMessage
        if (errorMessage != null) {
            ChimahonDiscoverHealthBanner(
                title = "Source catalogue unavailable",
                subtitle = errorMessage,
                health = ChimahonDiscoverSourceHealth.Error,
                action = "Refresh",
                onAction = actions.onRefresh,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                colors = colors,
            )
        }
        when {
            state.loading && state.sources.isEmpty() -> {
                ChimahonDiscoverHealthBanner(
                    title = "Loading sources",
                    subtitle = "Preparing manga, anime, and light novel catalogues.",
                    health = ChimahonDiscoverSourceHealth.Loading,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    colors = colors,
                )
            }
            state.sources.isEmpty() -> {
                ChimahonDiscoverHealthBanner(
                    title = "No sources installed",
                    subtitle = "Install manga, anime, or light novel extensions to browse catalogues.",
                    health = ChimahonDiscoverSourceHealth.Warning,
                    action = "Refresh",
                    onAction = actions.onRefresh,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    colors = colors,
                )
            }
            state.visibleSources.isEmpty() -> {
                ChimahonDiscoverHealthBanner(
                    title = "No matching sources",
                    subtitle = "Clear search or filters to show installed catalogues again.",
                    health = ChimahonDiscoverSourceHealth.Warning,
                    action = "Reset",
                    onAction = actions.onResetFilters,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    colors = colors,
                )
            }
            state.displayMode == ChimahonDiscoverDisplayMode.Grid -> {
                ChimahonDiscoverSourceGrid(
                    sources = state.visibleSources,
                    selectedSourceId = state.selectedSourceId,
                    actions = actions,
                    colors = colors,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            else -> {
                ChimahonDiscoverSourceList(
                    sources = state.visibleSources,
                    compact = state.displayMode == ChimahonDiscoverDisplayMode.CompactList,
                    selectedSourceId = state.selectedSourceId,
                    actions = actions,
                    colors = colors,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun ChimahonDiscoverSourceFilterStrip(
    state: ChimahonDiscoverSourceListState,
    actions: ChimahonDiscoverSourceActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonDiscoverSearchField(
                query = state.filters.query,
                onQueryChange = { query -> actions.onFiltersChange(state.filters.copy(query = query)) },
                placeholder = "Search sources",
                modifier = Modifier.weight(1f),
                colors = colors,
            )
            ChimahonDiscoverIconButton(
                icon = ChimahonDiscoverIcon.Refresh,
                contentDescription = "Refresh sources",
                onClick = actions.onRefresh,
                loading = state.refreshing,
                colors = colors,
            )
        }
        ChimahonDiscoverChipRow(
            chips = listOf("All") + state.contentTypeOptions.map { it.title },
            selected = state.filters.contentType?.title ?: "All",
            onSelect = { title ->
                val nextType = ChimahonDiscoverContentType.entries.firstOrNull { it.title == title }
                actions.onFiltersChange(state.filters.copy(contentType = nextType))
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            colors = colors,
        )
        val languages = state.languageOptions
        if (languages.isNotEmpty()) {
            ChimahonDiscoverChipRow(
                chips = listOf("All languages") + languages,
                selected = state.filters.language?.uppercase() ?: "All languages",
                onSelect = { title ->
                    actions.onFiltersChange(
                        state.filters.copy(language = title.takeIf { it != "All languages" }),
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                colors = colors,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonDiscoverChip(
                text = "Pinned",
                selected = state.filters.pinnedOnly,
                onClick = { actions.onFiltersChange(state.filters.copy(pinnedOnly = !state.filters.pinnedOnly)) },
                colors = colors,
            )
            ChimahonDiscoverChip(
                text = "Latest",
                selected = state.filters.latestOnly,
                onClick = { actions.onFiltersChange(state.filters.copy(latestOnly = !state.filters.latestOnly)) },
                colors = colors,
            )
            ChimahonDiscoverChip(
                text = "Installed",
                selected = state.filters.installedOnly,
                onClick = { actions.onFiltersChange(state.filters.copy(installedOnly = !state.filters.installedOnly)) },
                colors = colors,
            )
            Spacer(modifier = Modifier.weight(1f))
            ChimahonDiscoverIconButton(
                icon = ChimahonDiscoverIcon.Manga,
                contentDescription = "Grid display",
                selected = state.displayMode == ChimahonDiscoverDisplayMode.Grid,
                onClick = { actions.onDisplayModeChange(ChimahonDiscoverDisplayMode.Grid) },
                colors = colors,
            )
            ChimahonDiscoverIconButton(
                icon = ChimahonDiscoverIcon.Filter,
                contentDescription = "List display",
                selected = state.displayMode != ChimahonDiscoverDisplayMode.Grid,
                onClick = {
                    actions.onDisplayModeChange(
                        if (state.displayMode == ChimahonDiscoverDisplayMode.CompactList) {
                            ChimahonDiscoverDisplayMode.ComfortableList
                        } else {
                            ChimahonDiscoverDisplayMode.CompactList
                        },
                    )
                },
                colors = colors,
            )
        }
        ChimahonDiscoverSectionHeader(
            title = "Sources",
            count = state.visibleSources.size,
            detail = if (state.filters.hasActiveFilters) "${state.sources.size} total" else null,
            colors = colors,
        )
    }
}

@Composable
fun ChimahonDiscoverSourceList(
    sources: List<ChimahonDiscoverSourceUiModel>,
    compact: Boolean,
    selectedSourceId: String?,
    actions: ChimahonDiscoverSourceActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        items(sources, key = { it.lazyKey }) { source ->
            ChimahonDiscoverSourceRow(
                source = source,
                compact = compact,
                selected = source.id == selectedSourceId,
                actions = actions,
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDiscoverSourceGrid(
    sources: List<ChimahonDiscoverSourceUiModel>,
    selectedSourceId: String?,
    actions: ChimahonDiscoverSourceActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(156.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(
            key = "grid-header",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            ChimahonDiscoverSectionHeader(
                title = "Catalogue sources",
                count = sources.size,
                colors = colors,
            )
        }
        gridItems(sources, key = { it.lazyKey }) { source ->
            ChimahonDiscoverSourceGridCard(
                source = source,
                selected = source.id == selectedSourceId,
                actions = actions,
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonDiscoverSourceRow(
    source: ChimahonDiscoverSourceUiModel,
    compact: Boolean,
    selected: Boolean,
    actions: ChimahonDiscoverSourceActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(if (selected) colors.primaryContainer.copy(alpha = 0.38f) else colors.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (compact) 54.dp else 64.dp)
                .clickable { actions.onOpen(source, ChimahonDiscoverBrowseMode.Popular) }
                .padding(start = 16.dp, end = 6.dp, top = if (compact) 6.dp else 9.dp, bottom = if (compact) 6.dp else 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonDiscoverIconTile(
                marker = source.primaryMarker,
                icon = source.contentType.icon,
                size = if (compact) 40.dp else 46.dp,
                selected = selected,
                colors = colors,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                ChimahonDiscoverLabel(
                    text = source.name,
                    color = colors.content,
                    size = 14,
                    weight = FontWeight.SemiBold,
                )
                ChimahonDiscoverLabel(
                    text = source.sourceSubtitle(),
                    color = colors.secondaryContent,
                    size = 12,
                    modifier = Modifier.padding(top = 2.dp),
                )
                if (!compact) {
                    ChimahonDiscoverBadgeRow(
                        badges = source.effectiveBadges.take(4),
                        modifier = Modifier.padding(top = 6.dp),
                        colors = colors,
                    )
                }
            }
            ChimahonDiscoverIconButton(
                icon = ChimahonDiscoverIcon.Star,
                contentDescription = if (source.pinned) "Unpin ${source.name}" else "Pin ${source.name}",
                selected = source.pinned,
                onClick = { actions.onTogglePinned(source) },
                colors = colors,
            )
            ChimahonDiscoverIconButton(
                icon = ChimahonDiscoverIcon.Settings,
                contentDescription = "Source settings for ${source.name}",
                onClick = { actions.onOpenSettings(source) },
                colors = colors,
            )
            if (source.supportsLatest) {
                ChimahonDiscoverIconButton(
                    icon = ChimahonDiscoverIcon.Latest,
                    contentDescription = "Open latest from ${source.name}",
                    onClick = { actions.onOpen(source, ChimahonDiscoverBrowseMode.Latest) },
                    colors = colors,
                )
            }
        }
        ChimahonDiscoverDivider(startIndent = if (compact) 64.dp else 74.dp, colors = colors)
    }
}

@Composable
fun ChimahonDiscoverSourceGridCard(
    source: ChimahonDiscoverSourceUiModel,
    selected: Boolean,
    actions: ChimahonDiscoverSourceActions,
    modifier: Modifier = Modifier,
    colors: ChimahonDiscoverColors = ChimahonDiscoverDefaults.colors(),
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) colors.primaryContainer.copy(alpha = 0.40f) else colors.surface)
            .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
            .clickable { actions.onOpen(source, ChimahonDiscoverBrowseMode.Popular) }
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ChimahonDiscoverIconTile(
                marker = source.primaryMarker,
                icon = source.contentType.icon,
                selected = selected,
                colors = colors,
            )
            Spacer(modifier = Modifier.weight(1f))
            ChimahonDiscoverIconButton(
                icon = ChimahonDiscoverIcon.Star,
                contentDescription = if (source.pinned) "Unpin ${source.name}" else "Pin ${source.name}",
                selected = source.pinned,
                onClick = { actions.onTogglePinned(source) },
                colors = colors,
            )
        }
        ChimahonDiscoverLabel(
            text = source.name,
            color = colors.content,
            size = 14,
            weight = FontWeight.SemiBold,
            maxLines = 2,
            modifier = Modifier.padding(top = 10.dp),
        )
        ChimahonDiscoverLabel(
            text = source.sourceSubtitle(),
            color = colors.secondaryContent,
            size = 12,
            maxLines = 2,
            modifier = Modifier.padding(top = 3.dp),
        )
        ChimahonDiscoverBadgeRow(
            badges = source.effectiveBadges.take(3),
            modifier = Modifier.padding(top = 8.dp),
            colors = colors,
        )
        if (source.supportsLatest) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChimahonDiscoverChip(
                    text = "Latest",
                    selected = false,
                    onClick = { actions.onOpen(source, ChimahonDiscoverBrowseMode.Latest) },
                    colors = colors,
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = source.health.icon.imageVector,
                    contentDescription = source.health.title,
                    tint = source.healthTint(colors),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

private fun ChimahonDiscoverSourceUiModel.sourceSubtitle(): String {
    return listOfNotNull(
        contentType.title,
        extensionName?.takeIf(String::isNotBlank),
        displayLanguage,
        resultCount?.let { "$it results" },
        healthMessage?.takeIf(String::isNotBlank),
    ).joinToString(" - ").ifBlank { contentType.pluralTitle }
}

private fun ChimahonDiscoverSourceUiModel.healthTint(colors: ChimahonDiscoverColors) = when (health) {
    ChimahonDiscoverSourceHealth.Ready -> colors.success
    ChimahonDiscoverSourceHealth.Loading -> colors.primary
    ChimahonDiscoverSourceHealth.Warning -> colors.warning
    ChimahonDiscoverSourceHealth.Error -> colors.error
    ChimahonDiscoverSourceHealth.Disabled -> colors.secondaryContent
}
