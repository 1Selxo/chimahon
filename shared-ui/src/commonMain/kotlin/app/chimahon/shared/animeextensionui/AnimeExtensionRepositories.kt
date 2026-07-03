package app.chimahon.shared.animeextensionui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AnimeExtensionRepositoryPane(
    state: AnimeExtensionRepoListState,
    actions: AnimeExtensionRepoActions,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AnimeExtensionRepositoryHeader(
            state = state,
            actions = actions,
            colors = colors,
        )
        if (state.repositories.isEmpty()) {
            AnimeExtensionStatusRow(
                title = "No anime extension repositories",
                subtitle = "Add an anime extension repository to browse installable anime sources.",
                icon = AnimeExtensionIcon.Repo,
                action = "Add",
                onAction = actions.onAdd,
                colors = colors,
            )
        } else {
            state.repositories.forEach { repository ->
                AnimeExtensionRepositoryRow(
                    repository = repository.copy(
                        selected = repository.selected || repository.baseUrl == state.selectedBaseUrl,
                    ),
                    actions = actions,
                    colors = colors,
                )
            }
        }
        state.message?.takeIf(String::isNotBlank)?.let { message ->
            AnimeExtensionStatusRow(
                title = message,
                subtitle = state.selectedRepository?.displayName ?: "Anime extension repositories",
                icon = if (message.startsWith("Could not", ignoreCase = true)) {
                    AnimeExtensionIcon.Error
                } else {
                    AnimeExtensionIcon.Info
                },
                error = message.startsWith("Could not", ignoreCase = true),
                colors = colors,
            )
        }
    }
}

@Composable
fun AnimeExtensionRepositoryHeader(
    state: AnimeExtensionRepoListState,
    actions: AnimeExtensionRepoActions,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(start = 16.dp, end = 8.dp, top = 13.dp, bottom = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            AnimeExtensionLabel(
                text = "Anime extension repositories",
                color = colors.content,
                size = 16,
                weight = FontWeight.SemiBold,
            )
            AnimeExtensionLabel(
                text = buildString {
                    append("${state.repositories.size} repo(s)")
                    append(", ${state.installedCount} installed")
                    append(", ${state.availableCount} available")
                    if (state.disabledCount > 0) append(", ${state.disabledCount} disabled")
                    if (state.unavailableCount > 0) append(", ${state.unavailableCount} unavailable")
                    append(if (state.autoSyncEnabled) ", auto sync on" else ", auto sync off")
                },
                color = colors.secondaryContent,
                size = 11,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        AnimeExtensionIconButton(
            icon = AnimeExtensionIcon.Refresh,
            contentDescription = "Refresh anime extension repositories",
            loading = state.refreshing,
            enabled = state.hasRepositories,
            onClick = actions.onRefreshAll,
            colors = colors,
        )
        AnimeExtensionIconButton(
            icon = AnimeExtensionIcon.Install,
            contentDescription = "Add anime extension repository",
            onClick = actions.onAdd,
            colors = colors,
        )
    }
}

@Composable
fun AnimeExtensionRepositoryRow(
    repository: AnimeExtensionRepoUiModel,
    actions: AnimeExtensionRepoActions,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val status = repository.repoStatus()
    AnimeExtensionListSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp),
        selected = repository.selected,
        warning = status.severity == AnimeExtensionRepoStatusSeverity.Error ||
            status.severity == AnimeExtensionRepoStatusSeverity.Warning,
        enabled = repository.enabled,
        colors = colors,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        actions.onSelectRepository(repository)
                    }
                    .padding(start = 14.dp, end = 6.dp, top = 10.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimeExtensionAvatarTile(
                    marker = repository.repoMarker(),
                    icon = AnimeExtensionIcon.Repo,
                    active = repository.selected,
                    warning = status.severity == AnimeExtensionRepoStatusSeverity.Error ||
                        status.severity == AnimeExtensionRepoStatusSeverity.Warning,
                    colors = colors,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                ) {
                    AnimeExtensionLabel(
                        text = repository.displayName,
                        color = colors.content,
                        size = 14,
                        weight = FontWeight.SemiBold,
                    )
                    AnimeExtensionLabel(
                        text = repository.normalizedBaseUrl,
                        color = colors.secondaryContent,
                        size = 11,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    AnimeExtensionLabel(
                        text = repository.countSummary,
                        color = colors.secondaryContent,
                        size = 11,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    repository.stateDetailLabel?.let { detail ->
                        AnimeExtensionLabel(
                            text = detail,
                            color = if (repository.isUnavailable) colors.error else colors.secondaryContent,
                            size = 11,
                            maxLines = 2,
                            lineHeight = 15,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    AnimeExtensionRepositoryBadges(
                        repository = repository,
                        colors = colors,
                        modifier = Modifier.padding(top = 7.dp),
                    )
                }
                val refreshAction = repository.rowActionState(AnimeExtensionRepoAction.Refresh)
                AnimeExtensionIconButton(
                    icon = refreshAction.icon,
                    contentDescription = "${refreshAction.label} ${repository.displayName}",
                    loading = refreshAction.loading,
                    enabled = refreshAction.enabled,
                    onClick = {
                        actions.onRefreshRepository(repository)
                    },
                    colors = colors,
                )
                val toggleAction = repository.rowActionState(
                    if (repository.enabled) AnimeExtensionRepoAction.Disable else AnimeExtensionRepoAction.Enable,
                )
                AnimeExtensionIconButton(
                    icon = toggleAction.icon,
                    contentDescription = "${toggleAction.label} ${repository.displayName}",
                    active = toggleAction.active,
                    enabled = toggleAction.enabled,
                    onClick = {
                        if (repository.enabled) {
                            actions.onDisableRepository(repository)
                        } else {
                            actions.onEnableRepository(repository)
                        }
                    },
                    colors = colors,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background)
                    .padding(start = 72.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                buildList {
                    add(AnimeExtensionRepoAction.OpenWebsite)
                    add(AnimeExtensionRepoAction.CopyIndexUrl)
                    add(AnimeExtensionRepoAction.Edit)
                    if (!repository.trusted) add(AnimeExtensionRepoAction.Trust)
                    add(AnimeExtensionRepoAction.Remove)
                }.forEach { action ->
                    AnimeExtensionRepoActionIconButton(
                        action = action,
                        repository = repository,
                        actions = actions,
                        colors = colors,
                    )
                }
            }
        }
    }
}

@Composable
fun AnimeExtensionRepositoryBadges(
    repository: AnimeExtensionRepoUiModel,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val status = repository.repoStatus()
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item(key = "host") {
            AnimeExtensionChip(
                text = repository.normalizedBaseUrl.animeExtensionRepoHost(),
                selected = false,
                colors = colors,
            )
        }
        item(key = "status") {
            AnimeExtensionChip(
                text = status.label.uppercase(),
                selected = status.severity == AnimeExtensionRepoStatusSeverity.Ready,
                error = status.severity == AnimeExtensionRepoStatusSeverity.Error,
                warning = status.severity == AnimeExtensionRepoStatusSeverity.Warning ||
                    status.severity == AnimeExtensionRepoStatusSeverity.Disabled,
                colors = colors,
            )
        }
        item(key = "installed") {
            AnimeExtensionChip(
                text = "${repository.installedCount} installed",
                selected = repository.installedCount > 0,
                colors = colors,
            )
        }
        item(key = "available") {
            AnimeExtensionChip(
                text = "${repository.availableCount} available",
                selected = repository.availableCount > 0,
                colors = colors,
            )
        }
        if (repository.updateCount > 0) {
            item(key = "updates") {
                AnimeExtensionChip(
                    text = "${repository.updateCount} updates",
                    selected = true,
                    colors = colors,
                )
            }
        }
        if (repository.sourceCount > 0) {
            item(key = "sources") {
                AnimeExtensionChip(
                    text = "${repository.sourceCount} sources",
                    selected = false,
                    colors = colors,
                )
            }
        }
        items(repository.languages, key = { "lang:$it" }) { language ->
            AnimeExtensionChip(
                text = language.animeExtensionLanguageDisplayName(),
                selected = false,
                colors = colors,
            )
        }
    }
}

@Composable
fun AnimeExtensionSelectedRepoActionStrip(
    repository: AnimeExtensionRepoUiModel,
    actions: AnimeExtensionRepoActions,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val status = repository.repoStatus()
    AnimeExtensionListSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        selected = true,
        warning = status.severity == AnimeExtensionRepoStatusSeverity.Error ||
            status.severity == AnimeExtensionRepoStatusSeverity.Warning,
        colors = colors,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimeExtensionAvatarTile(
                marker = repository.repoMarker(),
                icon = AnimeExtensionIcon.Repo,
                active = true,
                colors = colors,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                AnimeExtensionLabel(
                    text = repository.displayName,
                    color = colors.content,
                    size = 14,
                    weight = FontWeight.SemiBold,
                )
                AnimeExtensionLabel(
                    text = "Selected anime repository",
                    color = colors.secondaryContent,
                    size = 11,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
            repository.defaultActionOrder().forEach { action ->
                AnimeExtensionRepoActionIconButton(
                    action = action,
                    repository = repository,
                    actions = actions,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun AnimeExtensionRepoActionIconButton(
    action: AnimeExtensionRepoAction,
    repository: AnimeExtensionRepoUiModel,
    actions: AnimeExtensionRepoActions,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val actionState = repository.rowActionState(action)
    AnimeExtensionIconButton(
        icon = actionState.icon,
        contentDescription = "${actionState.label} ${repository.displayName}",
        onClick = {
            when (action) {
                AnimeExtensionRepoAction.Select -> actions.onSelectRepository(repository)
                AnimeExtensionRepoAction.OpenWebsite -> actions.onOpenRepositoryWebsite(repository)
                AnimeExtensionRepoAction.CopyIndexUrl -> actions.onCopyRepositoryIndexUrl(repository)
                AnimeExtensionRepoAction.Refresh -> actions.onRefreshRepository(repository)
                AnimeExtensionRepoAction.Edit -> actions.onEditRepository(repository)
                AnimeExtensionRepoAction.Enable -> actions.onEnableRepository(repository)
                AnimeExtensionRepoAction.Disable -> actions.onDisableRepository(repository)
                AnimeExtensionRepoAction.Trust -> actions.onTrustRepository(repository)
                AnimeExtensionRepoAction.Remove -> actions.onRemoveRepository(repository)
            }
        },
        modifier = modifier,
        active = actionState.active,
        enabled = actionState.enabled,
        loading = actionState.loading,
        colors = colors,
    )
}
