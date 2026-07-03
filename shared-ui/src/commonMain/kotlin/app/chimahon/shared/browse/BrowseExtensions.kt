package app.chimahon.shared.browse

import app.chimahon.shared.ChimahonExtensionPackageType
import app.chimahon.shared.ChimahonExtensionRegistryPlatformSupportMessage
import app.chimahon.shared.ChimahonExtensionRepoEntry
import app.chimahon.shared.ChimahonInstalledExtensionEntry
import app.chimahon.shared.ChimahonRepoExtensionEntry
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BrowseExtensionReposPane(
    state: BrowseExtensionRepoListState,
    actions: BrowseExtensionRepoActions,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BrowseSectionHeader(
            title = "Extension repositories",
            count = state.repos.size,
            detail = state.selectedRepoBaseUrl?.let { "Selected" },
            colors = colors,
        )
        if (state.repos.isEmpty()) {
            BrowseStatusRow(
                title = "No repositories",
                subtitle = "Add a compatible extension repository to browse installable extensions.",
                icon = BrowseIcon.Repo,
                colors = colors,
            )
        } else {
            state.repos.forEach { repo ->
                val policy = state.policyFor(repo)
                BrowseExtensionRepoRow(
                    repo = repo,
                    selected = repo.baseUrl == state.selectedRepoBaseUrl,
                    disabled = !policy.enabled,
                    loaded = repo.baseUrl == state.loadedRepoBaseUrl,
                    policy = policy,
                    actions = actions,
                    colors = colors,
                )
            }
        }
        state.message?.let { message ->
            BrowseStatusRow(
                title = message,
                subtitle = state.selectedRepoBaseUrl ?: "Extension repository",
                icon = if (message.startsWith("Could not")) BrowseIcon.Info else BrowseIcon.Check,
                error = message.startsWith("Could not"),
                colors = colors,
            )
        }
    }
}

@Composable
fun BrowseExtensionRepoRow(
    repo: ChimahonExtensionRepoEntry,
    selected: Boolean,
    disabled: Boolean,
    loaded: Boolean,
    actions: BrowseExtensionRepoActions,
    modifier: Modifier = Modifier,
    policy: BrowseExtensionRepoPolicyState = BrowseExtensionRepoPolicyState(enabled = !disabled),
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
                .clickable { actions.onSelect(repo) }
                .padding(start = 16.dp, end = 4.dp, top = 9.dp, bottom = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrowseAvatarTile(
                marker = repo.browseRepoInitial(),
                icon = BrowseIcon.Repo,
                active = selected && !disabled,
                colors = colors,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                BrowseLabel(
                    text = repo.name,
                    color = if (disabled) colors.secondaryContent else colors.content,
                    size = 14,
                    weight = FontWeight.SemiBold,
                )
                BrowseLabel(
                    text = repo.baseUrl,
                    color = colors.secondaryContent,
                    size = 11,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 3.dp),
                )
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.padding(top = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    item {
                        BrowseChip(
                            text = repo.browseRepoHost(),
                            selected = false,
                            colors = colors,
                        )
                    }
                    item {
                        BrowseChip(
                            text = if (disabled) "DISABLED" else "ENABLED",
                            selected = !disabled,
                            error = disabled,
                            colors = colors,
                        )
                    }
                    if (loaded) {
                        item {
                            BrowseChip(
                                text = "LOADED",
                                selected = true,
                                colors = colors,
                            )
                        }
                    }
                    if (policy.trustState != BrowseExtensionRepoTrustState.Unsigned) {
                        item {
                            BrowseChip(
                                text = policy.trustState.badgeLabel,
                                selected = policy.trusted,
                                error = !policy.trusted,
                                colors = colors,
                            )
                        }
                    }
                    if (policy.removalState == BrowseExtensionRepoRemovalState.Confirming) {
                        item {
                            BrowseChip(
                                text = "REMOVE?",
                                selected = false,
                                error = true,
                                colors = colors,
                            )
                        }
                    }
                }
            }
            BrowseIconButton(
                icon = BrowseIcon.Refresh,
                contentDescription = "Refresh ${repo.name}",
                enabled = policy.canRefresh,
                onClick = { actions.onRefresh(repo) },
                colors = colors,
            )
            BrowseIconButton(
                icon = if (disabled) BrowseIcon.Visibility else BrowseIcon.VisibilityOff,
                contentDescription = if (disabled) "Enable ${repo.name}" else "Disable ${repo.name}",
                active = disabled,
                onClick = { actions.onSetEnabled(repo, disabled) },
                colors = colors,
            )
            BrowseIconButton(
                icon = BrowseIcon.More,
                contentDescription = "Open ${repo.name}",
                onClick = { actions.onOpenWebsite(repo) },
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
            BrowseIconButton(
                icon = BrowseIcon.Copy,
                contentDescription = "Copy ${repo.name} index URL",
                onClick = { actions.onCopyIndexUrl(repo) },
                colors = colors,
            )
            BrowseIconButton(
                icon = BrowseIcon.Edit,
                contentDescription = "Edit ${repo.name}",
                onClick = { actions.onEdit(repo) },
                colors = colors,
            )
            if (policy.canTrust) {
                BrowseIconButton(
                    icon = BrowseIcon.Check,
                    contentDescription = "Trust ${repo.name}",
                    onClick = { actions.onSetTrusted(repo, true) },
                    colors = colors,
                )
            }
            BrowseIconButton(
                icon = BrowseIcon.Delete,
                contentDescription = "Remove ${repo.name}",
                loading = policy.removalState == BrowseExtensionRepoRemovalState.Removing,
                enabled = policy.canRemove,
                onClick = { actions.onRemove(repo) },
                colors = colors,
            )
        }
        BrowseDivider(startIndent = 72.dp, colors = colors)
    }
}

@Composable
fun BrowseExtensionRepoInputCard(
    url: String,
    onUrlChange: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    editingRepoName: String? = null,
    saving: Boolean = false,
    message: String? = null,
    onCancel: (() -> Unit)? = null,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    BrowseCardSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        colors = colors,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            BrowseLabel(
                text = editingRepoName?.let { "Edit $it" } ?: "Add extension repo",
                color = colors.content,
                size = 15,
                weight = FontWeight.SemiBold,
            )
            BrowseLabel(
                text = "Repository metadata is checked before the URL is saved.",
                color = colors.secondaryContent,
                size = 11,
                lineHeight = 16,
                maxLines = 2,
                modifier = Modifier.padding(top = 3.dp),
            )
            OutlinedTextField(
                value = url,
                onValueChange = onUrlChange,
                label = { Text("Repository URL") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
            if (!message.isNullOrBlank()) {
                BrowseLabel(
                    text = message,
                    color = if (message.startsWith("Could not")) colors.error else colors.secondaryContent,
                    size = 11,
                    lineHeight = 16,
                    maxLines = 3,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                onCancel?.let {
                    androidx.compose.material.TextButton(onClick = it, enabled = !saving) {
                        Text("Cancel")
                    }
                }
                Button(
                    onClick = onSave,
                    enabled = url.isNotBlank() && !saving,
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text(if (saving) "Saving" else "Save")
                }
            }
        }
    }
}

@Composable
fun BrowseExtensionFilterRow(
    selected: BrowseExtensionFilter,
    onSelect: (BrowseExtensionFilter) -> Unit,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    BrowseChipRow(
        chips = BrowseExtensionFilter.entries.map { it.title },
        selected = selected.title,
        onSelect = { title ->
            BrowseExtensionFilter.entries.firstOrNull { it.title == title }?.let(onSelect)
        },
        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        colors = colors,
    )
}

@Composable
fun BrowseExtensionList(
    state: BrowseExtensionListState,
    actions: BrowseExtensionActions,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    val listState = rememberLazyListState()
    val installed = state.filteredInstalled()
    val available = state.visibleAvailable()
    val updates = state.availableUpdatesByKey()
    val platformSupport = state.extensionRegistryPlatformSupport()
    BrowseAutoLoadEffect(
        listState = listState,
        state = state.autoLoadState(),
        onLoadMore = actions.onLoadMore,
    )
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        if (state.filter != BrowseExtensionFilter.Available && state.filter != BrowseExtensionFilter.Nsfw) {
            item {
                BrowseSectionHeader(
                    title = "Installed extensions",
                    count = installed.size,
                    colors = colors,
                )
            }
            if (installed.isEmpty()) {
                item {
                    BrowseStatusRow(
                        title = "No extensions installed",
                        subtitle = "Select a repository and install an extension below.",
                        icon = BrowseIcon.Extension,
                        colors = colors,
                    )
                }
            } else {
                items(installed, key = { it.browseInstallKey() }) { extension ->
                    BrowseInstalledExtensionRow(
                        extension = extension,
                        availableUpdate = updates[extension.browseInstallKey()],
                        updating = updates[extension.browseInstallKey()]?.browseInstallKey() == state.installingKey,
                        uninstalling = extension.browseInstallKey() == state.uninstallingKey,
                        updateSupported = updates[extension.browseInstallKey()]?.isSupportedByBrowsePlatform(
                            platformSupport,
                        ) != false,
                        onUpdate = { update -> actions.onUpdate(update) },
                        onUninstall = { actions.onUninstall(extension) },
                        colors = colors,
                    )
                }
            }
        }

        when (val catalog = state.catalogState) {
            BrowseExtensionCatalogState.Idle -> Unit
            BrowseExtensionCatalogState.Loading -> item {
                BrowseStatusRow(
                    title = "Loading repository",
                    subtitle = "Fetching extension metadata.",
                    icon = BrowseIcon.Refresh,
                    loading = true,
                    colors = colors,
                )
            }
            is BrowseExtensionCatalogState.Disabled -> item {
                BrowseStatusRow(
                    title = "${catalog.repo.name} disabled",
                    subtitle = "Enable this repository to browse installable extensions.",
                    icon = BrowseIcon.VisibilityOff,
                    action = "Enable",
                    onAction = { actions.onEnableRepo(catalog.repo) },
                    colors = colors,
                )
            }
            is BrowseExtensionCatalogState.Failed -> item {
                BrowseStatusRow(
                    title = "Repository unavailable",
                    subtitle = catalog.message,
                    icon = BrowseIcon.Info,
                    action = "Retry",
                    error = true,
                    onAction = actions.onRetryCatalog,
                    colors = colors,
                )
            }
            is BrowseExtensionCatalogState.Ready -> {
                if (state.filter != BrowseExtensionFilter.Installed) {
                    item {
                        BrowseSectionHeader(
                            title = "Available extensions",
                            count = state.filteredAvailable().size,
                            detail = catalog.catalog.repo.name,
                            colors = colors,
                        )
                    }
                    if (catalog.catalog.extensions.isEmpty()) {
                        item {
                            BrowseStatusRow(
                                title = "No compatible extensions",
                                subtitle = "This repository did not expose supported extension packages.",
                                icon = BrowseIcon.Extension,
                                colors = colors,
                            )
                        }
                    } else if (available.isEmpty()) {
                        item {
                            BrowseStatusRow(
                                title = "No results found",
                                subtitle = "Try another name, package, language, or filter.",
                                icon = BrowseIcon.Search,
                                colors = colors,
                            )
                        }
                    } else {
                        items(available, key = { it.browseInstallKey() }) { extension ->
                            val installedCopy = state.installed.firstOrNull { it.browseInstallKey() == extension.browseInstallKey() }
                            val supportMessage = extension.browsePlatformSupportMessage(platformSupport)
                            val installSupported = supportMessage.supported
                            BrowseAvailableExtensionRow(
                                extension = extension,
                                installedVersion = installedCopy?.version,
                                installing = extension.browseInstallKey() == state.installingKey,
                                installSupported = installSupported,
                                platformSupportMessage = supportMessage,
                                onInstall = {
                                    if (installSupported) {
                                        if (installedCopy == null) actions.onInstall(extension) else actions.onUpdate(extension)
                                    }
                                },
                                colors = colors,
                            )
                        }
                        if (state.autoLoadState().hasMore) {
                            item {
                                BrowseExtensionAutoLoadFooter(
                                    state = state.autoLoadState(),
                                    onLoadMore = actions.onLoadMore,
                                    colors = colors,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BrowseInstalledExtensionRow(
    extension: ChimahonInstalledExtensionEntry,
    availableUpdate: ChimahonRepoExtensionEntry?,
    updating: Boolean,
    uninstalling: Boolean,
    updateSupported: Boolean,
    onUpdate: (ChimahonRepoExtensionEntry) -> Unit,
    onUninstall: () -> Unit,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    BrowseExtensionRowShell(
        name = extension.name,
        subtitle = "${extension.id} - ${extension.sourceCount} source(s)",
        marker = extension.packageType.browseMarker(),
        badges = listOfNotNull(
            "INSTALLED" to true,
            extension.version to false,
            if (availableUpdate != null) "UPDATE" to true else null,
        ),
        modifier = modifier,
        colors = colors,
    ) {
        if (availableUpdate != null) {
            BrowseIconButton(
                icon = BrowseIcon.Download,
                contentDescription = if (updateSupported) {
                    "Update ${extension.name}"
                } else {
                    "${extension.name} cannot be updated on this platform"
                },
                loading = updating,
                enabled = updateSupported,
                onClick = { if (updateSupported) onUpdate(availableUpdate) },
                colors = colors,
            )
        }
        BrowseIconButton(
            icon = BrowseIcon.Delete,
            contentDescription = "Uninstall ${extension.name}",
            loading = uninstalling,
            onClick = onUninstall,
            colors = colors,
        )
    }
}

@Composable
fun BrowseAvailableExtensionRow(
    extension: ChimahonRepoExtensionEntry,
    installedVersion: String?,
    installing: Boolean,
    installSupported: Boolean,
    onInstall: () -> Unit,
    modifier: Modifier = Modifier,
    platformSupportMessage: ChimahonExtensionRegistryPlatformSupportMessage? = null,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    val updateAvailable = installedVersion != null && compareBrowseVersions(extension.version, installedVersion) > 0
    BrowseExtensionRowShell(
        name = extension.name,
        subtitle = "${extension.id} - ${extension.sourceCount} source(s)",
        marker = extension.packageType.browseMarker(),
        badges = listOfNotNull(
            extension.language.browseLanguageCode().uppercase() to false,
            extension.version to false,
            if (extension.isNsfw) "NSFW" to true else null,
            when {
                !installSupported -> (platformSupportMessage?.badgeLabel ?: "UNSUPPORTED") to true
                updateAvailable -> "UPDATE" to true
                installedVersion != null -> "INSTALLED" to true
                else -> null
            },
            if (extension.packageType == ChimahonExtensionPackageType.AndroidApk) "APK" to true else null,
        ),
        modifier = modifier,
        colors = colors,
    ) {
        BrowseIconButton(
            icon = BrowseIcon.Download,
            contentDescription = when {
                !installSupported -> platformSupportMessage?.detail
                    ?: "${extension.name} is unavailable on this platform"
                installedVersion == null -> "Install ${extension.name}"
                else -> "Update ${extension.name}"
            },
            loading = installing,
            active = installedVersion != null,
            enabled = installSupported && (installedVersion == null || updateAvailable),
            onClick = {
                if (installSupported && (installedVersion == null || updateAvailable)) {
                    onInstall()
                }
            },
            colors = colors,
        )
    }
}

@Composable
private fun BrowseExtensionRowShell(
    name: String,
    subtitle: String,
    marker: String,
    badges: List<Pair<String, Boolean>>,
    colors: BrowseColors,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 62.dp)
                .padding(start = 16.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrowseAvatarTile(
                marker = marker,
                icon = BrowseIcon.Extension,
                active = true,
                colors = colors,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                BrowseLabel(
                    text = name,
                    color = colors.content,
                    size = 14,
                    weight = FontWeight.SemiBold,
                    maxLines = 1,
                )
                BrowseLabel(
                    text = subtitle,
                    color = colors.secondaryContent,
                    size = 11,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 3.dp),
                )
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.padding(top = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
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
            actions()
        }
        BrowseDivider(startIndent = 72.dp, colors = colors)
    }
}

@Composable
fun BrowseExtensionAutoLoadFooter(
    state: BrowseAutoLoadState,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    BrowseStatusRow(
        title = state.continuationTitle,
        subtitle = state.continuationSubtitle,
        icon = if (state.enabled) BrowseIcon.Refresh else BrowseIcon.Add,
        loading = state.loading,
        action = if (!state.enabled) "Load more" else null,
        onAction = if (!state.enabled) onLoadMore else null,
        modifier = modifier,
        colors = colors,
    )
}
