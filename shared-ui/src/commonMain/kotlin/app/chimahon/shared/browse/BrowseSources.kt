package app.chimahon.shared.browse

import app.chimahon.shared.ChimahonBrowseSourceDisplayMode
import app.chimahon.shared.ChimahonSourceBrowseMode
import app.chimahon.shared.ChimahonSourceEntry
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
import androidx.compose.foundation.lazy.grid.items
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
fun BrowseSourcesPane(
    state: BrowseSourceListState,
    actions: BrowseSourceActions,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    val groups = state.sourceGroups()
    val filteredCount = groups.sumOf { it.sources.size }
    if (state.sources.isEmpty()) {
        Column(modifier = modifier.fillMaxSize()) {
            BrowseStatusRow(
                title = "No sources installed",
                subtitle = "Install an extension to expose catalog sources here.",
                icon = BrowseIcon.Source,
                action = actions.onInstallExtension?.let { "Install" },
                onAction = actions.onInstallExtension,
                colors = colors,
            )
            actions.onOpenLocalImport?.let { onOpenLocalImport ->
                BrowseStatusRow(
                    title = "Local source",
                    subtitle = "Open local import status to stage folders and archives.",
                    icon = BrowseIcon.Local,
                    action = "Open",
                    onAction = onOpenLocalImport,
                    colors = colors,
                )
            }
        }
        return
    }
    if (filteredCount == 0) {
        BrowseStatusRow(
            title = if (state.enabledLanguages.isNotEmpty() && state.query.isBlank()) {
                "No enabled sources"
            } else {
                "No matching sources"
            },
            subtitle = if (state.hasActiveFilters()) {
                "Clear source search or filters to show installed sources again."
            } else {
                "Install another extension or refresh your repositories."
            },
            icon = BrowseIcon.Search,
            action = actions.onInstallExtension?.let { "Extensions" },
            onAction = actions.onInstallExtension,
            colors = colors,
            modifier = modifier,
        )
        return
    }

    when (state.displayMode) {
        ChimahonBrowseSourceDisplayMode.Grid -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(154.dp),
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                groups.forEach { group ->
                    if (group.title != null) {
                        item(
                            key = "header:${group.key}",
                            span = { GridItemSpan(maxLineSpan) },
                        ) {
                            BrowseSectionHeader(
                                title = group.title,
                                count = group.sources.size,
                                colors = colors,
                            )
                        }
                    }
                    items(group.sources, key = { it.id }) { source ->
                        BrowseSourceGridCard(
                            source = source,
                            pinned = source.id in state.pinnedSourceIds,
                            showLanguage = state.showLanguageBadges,
                            actions = actions,
                            colors = colors,
                        )
                    }
                }
            }
        }
        ChimahonBrowseSourceDisplayMode.List,
        ChimahonBrowseSourceDisplayMode.CompactList,
        -> {
            val compact = state.displayMode == ChimahonBrowseSourceDisplayMode.CompactList
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 12.dp),
            ) {
                groups.forEach { group ->
                    if (group.title != null) {
                        item(key = "header:${group.key}") {
                            BrowseSectionHeader(
                                title = group.title,
                                count = group.sources.size,
                                colors = colors,
                            )
                        }
                    }
                    items(group.sources, key = { it.id }) { source ->
                        BrowseSourceRow(
                            source = source,
                            compact = compact,
                            pinned = source.id in state.pinnedSourceIds,
                            showLanguage = state.showLanguageBadges,
                            actions = actions,
                            colors = colors,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BrowseSourceSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onClear: () -> Unit = { onQueryChange("") },
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    BrowseSearchField(
        query = query,
        onQueryChange = onQueryChange,
        placeholder = "Search sources",
        onClear = onClear,
        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        colors = colors,
    )
}

@Composable
fun BrowseSourceFilterSheet(
    state: BrowseSourceListState,
    onLanguageSelected: (String) -> Unit,
    onLanguageEnabledChange: (String, Boolean) -> Unit,
    onQuickFilterSelected: (BrowseSourceQuickFilter) -> Unit,
    onSortSelected: (BrowseSourceSort) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    val allLanguages = state.sources
        .map { it.language.browseLanguageCode() }
        .distinct()
        .sorted()
    val enabledLanguages = state.enabledLanguages.map(String::browseLanguageCode).toSet()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(colors.surface)
            .border(1.dp, colors.divider, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(top = 8.dp, bottom = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp)
                .size(width = 38.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.divider),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                BrowseLabel(
                    text = "Sources",
                    color = colors.content,
                    size = 14,
                    weight = FontWeight.SemiBold,
                )
                BrowseLabel(
                    text = "${state.filteredSources().size} of ${state.sources.size} shown",
                    color = colors.secondaryContent,
                    size = 11,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            if (state.hasActiveFilters()) {
                androidx.compose.material.TextButton(onClick = onReset) {
                    androidx.compose.material.Text("Reset")
                }
            }
        }
        BrowseSectionHeader(title = "Language", colors = colors)
        BrowseChipRow(
            chips = state.languageOptions(),
            selected = state.selectedLanguage,
            onSelect = onLanguageSelected,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            colors = colors,
        )
        if (allLanguages.isNotEmpty()) {
            BrowseSectionHeader(
                title = "Enabled languages",
                detail = if (state.enabledLanguages.isEmpty()) "All enabled" else "${enabledLanguages.size} of ${allLanguages.size}",
                colors = colors,
            )
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(allLanguages, key = { it }) { language ->
                    val selected = state.enabledLanguages.isEmpty() || language in enabledLanguages
                    BrowseChip(
                        text = language,
                        selected = selected,
                        onClick = { onLanguageEnabledChange(language, !selected) },
                        colors = colors,
                    )
                }
            }
        }
        BrowseSectionHeader(title = "Capability", colors = colors)
        BrowseChipRow(
            chips = BrowseSourceQuickFilter.entries.map { it.title },
            selected = state.quickFilter.title,
            onSelect = { title ->
                BrowseSourceQuickFilter.entries.firstOrNull { it.title == title }?.let(onQuickFilterSelected)
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            colors = colors,
        )
        BrowseSectionHeader(title = "Sort", colors = colors)
        BrowseChipRow(
            chips = BrowseSourceSort.entries.map { it.title },
            selected = state.sort.title,
            onSelect = { title ->
                BrowseSourceSort.entries.firstOrNull { it.title == title }?.let(onSortSelected)
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            colors = colors,
        )
    }
}

@Composable
fun BrowseSourceRow(
    source: ChimahonSourceEntry,
    compact: Boolean,
    pinned: Boolean,
    showLanguage: Boolean,
    actions: BrowseSourceActions,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (compact) 50.dp else 58.dp)
                .clickable { actions.onOpen(source, ChimahonSourceBrowseMode.Popular) }
                .padding(start = 16.dp, end = 6.dp, top = if (compact) 5.dp else 8.dp, bottom = if (compact) 5.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrowseSourceIcon(source = source, compact = compact, colors = colors)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                BrowseLabel(
                    text = source.name,
                    color = colors.content,
                    size = 14,
                    weight = FontWeight.SemiBold,
                )
                BrowseSourceBadges(
                    source = source,
                    pinned = pinned,
                    showLanguage = showLanguage || !compact,
                    compact = compact,
                    colors = colors,
                )
            }
            BrowseIconButton(
                icon = BrowseIcon.Star,
                contentDescription = if (pinned) "Unpin ${source.name}" else "Pin ${source.name}",
                active = pinned,
                onClick = { actions.onTogglePinned(source) },
                colors = colors,
            )
            BrowseIconButton(
                icon = BrowseIcon.Settings,
                contentDescription = "Source settings for ${source.name}",
                onClick = { actions.onOpenSettings(source) },
                colors = colors,
            )
            if (source.supportsLatest) {
                BrowseIconButton(
                    icon = BrowseIcon.Latest,
                    contentDescription = "Open latest from ${source.name}",
                    onClick = { actions.onOpen(source, ChimahonSourceBrowseMode.Latest) },
                    colors = colors,
                )
            } else {
                Icon(
                    imageVector = BrowseIcon.Forward.imageVector,
                    contentDescription = "Open ${source.name}",
                    tint = colors.secondaryContent,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .size(19.dp),
                )
            }
        }
        BrowseDivider(startIndent = if (compact) 58.dp else 66.dp, colors = colors)
    }
}

@Composable
fun BrowseSourceGridCard(
    source: ChimahonSourceEntry,
    pinned: Boolean,
    showLanguage: Boolean,
    actions: BrowseSourceActions,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    BrowseCardSurface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { actions.onOpen(source, ChimahonSourceBrowseMode.Popular) },
        colors = colors,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                BrowseSourceIcon(source = source, compact = false, colors = colors)
                Spacer(modifier = Modifier.weight(1f))
                BrowseIconButton(
                    icon = BrowseIcon.Star,
                    contentDescription = if (pinned) "Unpin ${source.name}" else "Pin ${source.name}",
                    active = pinned,
                    onClick = { actions.onTogglePinned(source) },
                    colors = colors,
                )
                BrowseIconButton(
                    icon = BrowseIcon.Settings,
                    contentDescription = "Source settings for ${source.name}",
                    onClick = { actions.onOpenSettings(source) },
                    colors = colors,
                )
            }
            BrowseLabel(
                text = source.name,
                color = colors.content,
                size = 14,
                weight = FontWeight.SemiBold,
                maxLines = 2,
                modifier = Modifier.padding(top = 10.dp),
            )
            BrowseSourceBadges(
                source = source,
                pinned = pinned,
                showLanguage = showLanguage,
                compact = false,
                colors = colors,
                modifier = Modifier.padding(top = 8.dp),
            )
            if (source.supportsLatest) {
                BrowseChip(
                    text = "Latest",
                    selected = true,
                    onClick = { actions.onOpen(source, ChimahonSourceBrowseMode.Latest) },
                    colors = colors,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
fun BrowseSourceIcon(
    source: ChimahonSourceEntry,
    compact: Boolean,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    BrowseAvatarTile(
        marker = source.browseSourceInitials(),
        icon = if (source.isBrowseLocalSource()) BrowseIcon.Local else BrowseIcon.Web,
        size = if (compact) 34.dp else 42.dp,
        active = source.supportsLatest,
        modifier = modifier,
        colors = colors,
    )
}

@Composable
private fun BrowseSourceBadges(
    source: ChimahonSourceEntry,
    pinned: Boolean,
    showLanguage: Boolean,
    compact: Boolean,
    modifier: Modifier = Modifier,
    colors: BrowseColors,
) {
    val badges = listOfNotNull(
        if (showLanguage) source.language.browseLanguageCode().uppercase() to false else null,
        if (source.isBrowseLocalSource()) "LOCAL" to true else null,
        "POPULAR" to !source.supportsLatest,
        if (source.supportsLatest) "LATEST" to true else null,
        if (!compact) "SEARCH" to false else null,
        if (pinned) "PINNED" to true else null,
    )
    if (badges.isEmpty()) return
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = if (compact) 3.dp else 5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        items(badges, key = { it.first }) { badge ->
            BrowseChip(
                text = badge.first,
                selected = badge.second,
                colors = colors,
            )
        }
    }
}
