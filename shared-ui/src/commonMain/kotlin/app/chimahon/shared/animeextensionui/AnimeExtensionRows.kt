package app.chimahon.shared.animeextensionui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AnimeExtensionManagementContent(
    state: AnimeExtensionListState,
    actions: AnimeExtensionActions,
    repoActions: AnimeExtensionRepoActions,
    modifier: Modifier = Modifier,
    repoSheet: AnimeExtensionRepoSheetState = AnimeExtensionRepoSheetState.None,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
    contentPadding: PaddingValues = PaddingValues(vertical = 10.dp),
    extensionIcon: @Composable (AnimeExtensionUiModel) -> Unit = {},
) {
    val listState = rememberLazyListState()
    AnimeExtensionAutoLoadEffect(
        listState = listState,
        state = state.autoLoadState(),
        onLoadMore = actions.onLoadMore,
    )
    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            item(key = "anime-extension-search") {
                AnimeExtensionSearchAndFilters(
                    state = state,
                    actions = actions,
                    colors = colors,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
            if (state.installPermissionWarning) {
                item(key = "anime-extension-permission-warning") {
                    AnimeExtensionStatusRow(
                        title = "Install permission required",
                        subtitle = "Allow package installs before installing anime APK extensions.",
                        icon = AnimeExtensionIcon.Security,
                        error = true,
                        colors = colors,
                    )
                }
            }
            item(key = "anime-extension-repositories") {
                AnimeExtensionRepositoryPane(
                    state = state.repositories,
                    actions = repoActions,
                    colors = colors,
                )
            }
            state.repositories.selectedRepository?.let { repository ->
                item(key = "anime-extension-selected-repo:${repository.baseUrl}") {
                    AnimeExtensionSelectedRepoActionStrip(
                        repository = repository,
                        actions = repoActions,
                        colors = colors,
                    )
                }
            }
            if (state.loading) {
                item(key = "anime-extension-loading") {
                    AnimeExtensionStatusRow(
                        title = "Loading anime extensions",
                        subtitle = "Refreshing installed, untrusted, and repository extension metadata.",
                        icon = AnimeExtensionIcon.Sync,
                        loading = true,
                        colors = colors,
                    )
                }
            } else if (state.errorMessage != null) {
                item(key = "anime-extension-error") {
                    AnimeExtensionStatusRow(
                        title = "Anime extensions unavailable",
                        subtitle = state.errorMessage,
                        icon = AnimeExtensionIcon.Error,
                        action = "Retry",
                        onAction = actions.onRetry,
                        error = true,
                        colors = colors,
                    )
                }
            } else {
                val groups = state.filteredGroups()
                if (groups.isEmpty()) {
                    item(key = "anime-extension-empty") {
                        AnimeExtensionEmptyState(
                            state = state,
                            actions = actions,
                            colors = colors,
                        )
                    }
                } else {
                    groups.forEach { group ->
                        item(key = "anime-extension-header:${group.header}") {
                            AnimeExtensionSectionHeader(
                                title = group.header.title,
                                count = group.extensions.size,
                                actionLabel = "Update all".takeIf {
                                    group.header == AnimeExtensionHeader.Updates && group.extensions.isNotEmpty()
                                },
                                onAction = actions.onUpdateAll.takeIf {
                                    group.header == AnimeExtensionHeader.Updates && group.extensions.isNotEmpty()
                                },
                                colors = colors,
                            )
                        }
                        items(
                            items = group.extensions,
                            key = { "anime-extension:${group.header}:${it.stableKey}" },
                        ) { extension ->
                            AnimeExtensionRow(
                                extension = extension,
                                onClick = {
                                    actions.dispatchExtensionAction(
                                        extension.defaultPrimaryAction(state.platform),
                                        extension,
                                    )
                                },
                                onAction = { action, item ->
                                    actions.dispatchExtensionAction(action, item)
                                },
                                platform = state.platform,
                                colors = colors,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                                icon = { extensionIcon(extension) },
                            )
                        }
                    }
                    item(key = "anime-extension-footer") {
                        AnimeExtensionListFooter(
                            state = state.autoLoadState(),
                            onLoadMore = actions.onLoadMore,
                            colors = colors,
                        )
                    }
                }
            }
        }
        AnimeExtensionRepoSheetHost(
            state = repoSheet,
            actions = repoActions,
            knownBaseUrls = state.repositories.repositories.map { it.baseUrl },
            colors = colors,
        )
    }
}

@Composable
fun AnimeExtensionSearchAndFilters(
    state: AnimeExtensionListState,
    actions: AnimeExtensionActions,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = state.query,
            onValueChange = actions.onQueryChange,
            label = { Text("Search anime extensions") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        AnimeExtensionChipRow(
            chips = AnimeExtensionFilter.values().map { it.title },
            selected = state.filter.title,
            onSelect = { title ->
                AnimeExtensionFilter.values()
                    .firstOrNull { it.title == title }
                    ?.let(actions.onFilterChange)
            },
            colors = colors,
            modifier = Modifier.padding(top = 8.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimeExtensionChip(
                text = state.selectedLanguage.animeExtensionLanguageDisplayName(),
                selected = state.selectedLanguage != AnimeExtensionAllLanguages,
                onClick = {
                    val languages = state.languageOptions
                    val current = languages.indexOf(state.selectedLanguage).coerceAtLeast(0)
                    actions.onLanguageChange(languages[(current + 1) % languages.size])
                },
                colors = colors,
            )
            AnimeExtensionChip(
                text = "Sort: ${state.sort.title}",
                selected = state.sort != AnimeExtensionSort.Name,
                onClick = {
                    val options = AnimeExtensionSort.values()
                    val current = options.indexOf(state.sort).coerceAtLeast(0)
                    actions.onSortChange(options[(current + 1) % options.size])
                },
                colors = colors,
            )
            AnimeExtensionChip(
                text = "${state.installedCount} installed",
                selected = false,
                colors = colors,
            )
            if (state.updateCount > 0) {
                AnimeExtensionChip(
                    text = "${state.updateCount} updates",
                    selected = true,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun AnimeExtensionRow(
    extension: AnimeExtensionUiModel,
    onClick: () -> Unit,
    onAction: (AnimeExtensionAction, AnimeExtensionUiModel) -> Unit,
    modifier: Modifier = Modifier,
    platform: AnimeExtensionPlatformState = AnimeExtensionPlatformState(),
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
    icon: @Composable () -> Unit = {},
) {
    AnimeExtensionListSurface(
        modifier = modifier,
        selected = extension.selected,
        warning = extension.visibleWarningLabel != null,
        colors = colors,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(start = 12.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(42.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (extension.installBusy) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(42.dp),
                        strokeWidth = 2.dp,
                        color = colors.primary,
                    )
                }
                AnimeExtensionAvatarTile(
                    marker = extension.name.animeExtensionInitials(),
                    icon = if (extension.kind == AnimeExtensionKind.Apk) {
                        AnimeExtensionIcon.Extension
                    } else {
                        AnimeExtensionIcon.Link
                    },
                    active = extension.installed,
                    warning = extension.isUntrusted || extension.obsolete,
                    image = icon.takeIf { extension.iconUrl != null },
                    colors = colors,
                    modifier = Modifier.padding(if (extension.installBusy) 6.dp else 0.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp),
            ) {
                AnimeExtensionLabel(
                    text = extension.name,
                    color = colors.content,
                    size = 14,
                    weight = FontWeight.SemiBold,
                )
                AnimeExtensionMetadataLine(
                    extension = extension,
                    platform = platform,
                    colors = colors,
                    modifier = Modifier.padding(top = 2.dp),
                )
                AnimeExtensionBadgeRow(
                    extension = extension,
                    platform = platform,
                    colors = colors,
                    modifier = Modifier.padding(top = 7.dp),
                )
            }
            AnimeExtensionRowActions(
                extension = extension,
                onAction = onAction,
                platform = platform,
                colors = colors,
            )
        }
    }
}

@Composable
fun AnimeExtensionMetadataLine(
    extension: AnimeExtensionUiModel,
    modifier: Modifier = Modifier,
    platform: AnimeExtensionPlatformState = AnimeExtensionPlatformState(),
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val supportState = extension.platformSupportState(platform)
    val parts = buildList {
        extension.languageLabel?.let(::add)
        if (extension.versionName.isNotBlank()) add(extension.versionName)
        if (extension.sourceCount > 0) add(extension.sourceCountLabel)
        extension.repositoryName?.takeIf(String::isNotBlank)?.let(::add)
        if (extension.installStep != AnimeExtensionInstallStep.Idle) add(extension.installStep.title)
        if (!supportState.supported) add(supportState.title)
    }
    AnimeExtensionLabel(
        text = parts.joinToString(" • "),
        color = colors.secondaryContent,
        size = 11,
        maxLines = 2,
        lineHeight = 16,
        modifier = modifier,
    )
}

@Composable
fun AnimeExtensionBadgeRow(
    extension: AnimeExtensionUiModel,
    modifier: Modifier = Modifier,
    platform: AnimeExtensionPlatformState = AnimeExtensionPlatformState(),
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val supportState = extension.platformSupportState(platform)
    val badges = buildList {
        add(extension.kind.title)
        extension.visibleWarningLabel?.let(::add)
        if (!supportState.supported) add(supportState.badgeLabel)
        if (extension.hasUpdate) add("UPDATE")
        if (extension.shared) add("SHARED")
        if (extension.incognito) add("INCOGNITO")
    }
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(badges, key = { it }) { badge ->
            AnimeExtensionChip(
                text = badge,
                selected = badge == "UPDATE" || badge == "INCOGNITO",
                error = badge == "UNTRUSTED" || badge == "OBSOLETE" || badge == "NSFW" || badge == "UNSUPPORTED",
                warning = badge == "TORRENT",
                colors = colors,
            )
        }
    }
}

@Composable
fun AnimeExtensionRowActions(
    extension: AnimeExtensionUiModel,
    onAction: (AnimeExtensionAction, AnimeExtensionUiModel) -> Unit,
    modifier: Modifier = Modifier,
    platform: AnimeExtensionPlatformState = AnimeExtensionPlatformState(),
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when {
            extension.installBusy -> {
                val actionState = extension.actionState(AnimeExtensionAction.Cancel, platform)
                AnimeExtensionIconButton(
                    icon = AnimeExtensionIcon.Close,
                    contentDescription = actionState.blockedReason ?: "Cancel ${extension.name}",
                    enabled = actionState.enabled,
                    loading = actionState.loading,
                    onClick = { onAction(AnimeExtensionAction.Cancel, extension) },
                    colors = colors,
                )
            }
            extension.installStep == AnimeExtensionInstallStep.Error -> {
                val actionState = extension.actionState(AnimeExtensionAction.Retry, platform)
                AnimeExtensionIconButton(
                    icon = AnimeExtensionIcon.Refresh,
                    contentDescription = actionState.blockedReason ?: "Retry ${extension.name}",
                    enabled = actionState.enabled,
                    loading = actionState.loading,
                    onClick = { onAction(AnimeExtensionAction.Retry, extension) },
                    colors = colors,
                )
            }
            extension.isUntrusted -> {
                val actionState = extension.actionState(AnimeExtensionAction.Trust, platform)
                AnimeExtensionIconButton(
                    icon = AnimeExtensionIcon.Trust,
                    contentDescription = actionState.blockedReason ?: "Trust ${extension.name}",
                    enabled = actionState.enabled,
                    loading = actionState.loading,
                    onClick = { onAction(AnimeExtensionAction.Trust, extension) },
                    colors = colors,
                )
            }
            extension.installed -> {
                val settingsState = extension.actionState(AnimeExtensionAction.OpenSettings, platform)
                AnimeExtensionIconButton(
                    icon = AnimeExtensionIcon.Settings,
                    contentDescription = settingsState.blockedReason ?: "Open ${extension.name} settings",
                    enabled = settingsState.enabled,
                    loading = settingsState.loading,
                    onClick = { onAction(AnimeExtensionAction.OpenSettings, extension) },
                    colors = colors,
                )
                if (extension.hasUpdate) {
                    val updateState = extension.actionState(AnimeExtensionAction.Update, platform)
                    AnimeExtensionIconButton(
                        icon = AnimeExtensionIcon.Install,
                        contentDescription = updateState.blockedReason ?: "Update ${extension.name}",
                        enabled = updateState.enabled,
                        loading = updateState.loading,
                        active = updateState.active,
                        onClick = { onAction(AnimeExtensionAction.Update, extension) },
                        colors = colors,
                    )
                }
                val uninstallState = extension.actionState(AnimeExtensionAction.Uninstall, platform)
                AnimeExtensionIconButton(
                    icon = AnimeExtensionIcon.Delete,
                    contentDescription = uninstallState.blockedReason ?: "Uninstall ${extension.name}",
                    enabled = uninstallState.enabled,
                    loading = uninstallState.loading,
                    onClick = { onAction(AnimeExtensionAction.Uninstall, extension) },
                    colors = colors,
                )
            }
            else -> {
                if (extension.sources.isNotEmpty()) {
                    AnimeExtensionIconButton(
                        icon = AnimeExtensionIcon.Web,
                        contentDescription = "Open ${extension.name} website",
                        onClick = { onAction(AnimeExtensionAction.OpenWebsite, extension) },
                        colors = colors,
                    )
                }
                val installState = extension.actionState(AnimeExtensionAction.Install, platform)
                AnimeExtensionIconButton(
                    icon = AnimeExtensionIcon.Install,
                    contentDescription = installState.blockedReason ?: "Install ${extension.name}",
                    enabled = installState.enabled,
                    loading = installState.loading,
                    onClick = { onAction(AnimeExtensionAction.Install, extension) },
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun AnimeExtensionListFooter(
    state: AnimeExtensionAutoLoadState,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    when {
        state.loading -> AnimeExtensionStatusRow(
            title = "Loading more anime extensions",
            subtitle = "${state.visibleCount} of ${state.totalCount} visible",
            icon = AnimeExtensionIcon.Sync,
            loading = true,
            colors = colors,
            modifier = modifier,
        )
        state.hasMore -> AnimeExtensionStatusRow(
            title = "More anime extensions available",
            subtitle = "${state.visibleCount} of ${state.totalCount} visible. More load automatically while scrolling.",
            icon = AnimeExtensionIcon.Extension,
            action = "Load now",
            onAction = onLoadMore,
            colors = colors,
            modifier = modifier,
        )
        state.totalCount > 0 -> AnimeExtensionStatusRow(
            title = "End of anime extensions",
            subtitle = "${state.totalCount} extension(s) available from selected repositories.",
            icon = AnimeExtensionIcon.Check,
            colors = colors,
            modifier = modifier,
        )
    }
}

@Composable
fun AnimeExtensionEmptyState(
    state: AnimeExtensionListState,
    actions: AnimeExtensionActions,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val filtered = state.hasActiveFilters()
    AnimeExtensionStatusRow(
        title = if (filtered) "No matching anime extensions" else "No anime extensions",
        subtitle = if (filtered) {
            "Clear search, filters, or language selection to see more results."
        } else {
            "Add or refresh an anime extension repository to browse installable anime sources."
        },
        icon = AnimeExtensionIcon.Extension,
        action = if (filtered) "Clear filter" else "Refresh",
        onAction = if (filtered) {
            { actions.onQueryChange("") }
        } else {
            actions.onRefresh
        },
        colors = colors,
        modifier = modifier,
    )
}

private fun String.animeExtensionInitials(): String {
    val words = replace('-', ' ')
        .replace('_', ' ')
        .split(' ')
        .mapNotNull { word -> word.firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString() }
    return when {
        words.size >= 2 -> (words[0] + words[1]).take(2)
        words.size == 1 -> words[0]
        else -> take(2).uppercase().ifBlank { "A" }
    }
}
