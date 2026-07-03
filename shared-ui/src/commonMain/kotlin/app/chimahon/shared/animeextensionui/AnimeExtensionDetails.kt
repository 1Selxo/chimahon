package app.chimahon.shared.animeextensionui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AnimeExtensionDetailsPanel(
    state: AnimeExtensionDetailsState,
    actions: AnimeExtensionActions,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
    extensionIcon: @Composable (AnimeExtensionUiModel) -> Unit = {},
) {
    val extension = state.extension
    when {
        state.loading -> {
            AnimeExtensionStatusRow(
                title = "Loading anime extension",
                subtitle = "Fetching sources and extension settings.",
                icon = AnimeExtensionIcon.Sync,
                loading = true,
                colors = colors,
                modifier = modifier,
            )
        }
        state.errorMessage != null -> {
            AnimeExtensionStatusRow(
                title = "Anime extension unavailable",
                subtitle = state.errorMessage,
                icon = AnimeExtensionIcon.Error,
                error = true,
                action = "Retry",
                onAction = actions.onRetry,
                colors = colors,
                modifier = modifier,
            )
        }
        extension == null -> {
            AnimeExtensionStatusRow(
                title = "No anime extension selected",
                subtitle = "Choose an installed anime extension to inspect its sources.",
                icon = AnimeExtensionIcon.Extension,
                colors = colors,
                modifier = modifier,
            )
        }
        else -> {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                item(key = "anime-extension-details-header:${extension.stableKey}") {
                    AnimeExtensionDetailsHeader(
                        extension = extension,
                        platform = state.platform,
                        incognito = state.incognito,
                        actions = actions,
                        colors = colors,
                        icon = { extensionIcon(extension) },
                    )
                }
                items(extension.sources, key = { "anime-extension-source:${it.id}:${it.name}" }) { source ->
                    AnimeExtensionSourceSwitchRow(
                        extension = extension,
                        source = source,
                        actions = actions,
                        colors = colors,
                    )
                }
            }
        }
    }
}

@Composable
fun AnimeExtensionDetailsHeader(
    extension: AnimeExtensionUiModel,
    incognito: Boolean,
    actions: AnimeExtensionActions,
    modifier: Modifier = Modifier,
    platform: AnimeExtensionPlatformState = AnimeExtensionPlatformState(),
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
    icon: @Composable () -> Unit = {},
) {
    val supportState = extension.platformSupportState(platform)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { actions.dispatchExtensionAction(AnimeExtensionAction.CopyDebugInfo, extension) }
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimeExtensionAvatarTile(
                marker = extension.name.take(2).uppercase(),
                icon = AnimeExtensionIcon.Extension,
                image = icon.takeIf { extension.iconUrl != null },
                size = 112.dp,
                active = extension.installed,
                warning = extension.visibleWarningLabel != null,
                colors = colors,
            )
            AnimeExtensionLabel(
                text = extension.name,
                color = colors.content,
                size = 22,
                weight = FontWeight.SemiBold,
                maxLines = 2,
                lineHeight = 28,
                modifier = Modifier.padding(top = 12.dp),
            )
            AnimeExtensionLabel(
                text = extension.packageName.substringAfter("eu.kanade.tachiyomi.animeextension."),
                color = colors.secondaryContent,
                size = 12,
                maxLines = 2,
                lineHeight = 17,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimeExtensionInfoText(
                primaryText = extension.versionName.ifBlank { "-" },
                secondaryText = "Version",
                colors = colors,
                modifier = Modifier.weight(1f),
            )
            AnimeExtensionInfoDivider(colors = colors)
            AnimeExtensionInfoText(
                primaryText = extension.languageLabel ?: "-",
                secondaryText = "Language",
                colors = colors,
                modifier = Modifier.weight(1f),
            )
            if (extension.nsfw || extension.torrent) {
                AnimeExtensionInfoDivider(colors = colors)
                AnimeExtensionInfoText(
                    primaryText = if (extension.nsfw) "NSFW" else "Torrent",
                    secondaryText = "Age rating",
                    error = extension.nsfw,
                    colors = colors,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val uninstallState = extension.actionState(AnimeExtensionAction.Uninstall, platform)
            AnimeExtensionActionTextButton(
                text = "Uninstall",
                onClick = { actions.dispatchExtensionAction(AnimeExtensionAction.Uninstall, extension) },
                modifier = Modifier.weight(1f),
                enabled = uninstallState.enabled,
                loading = uninstallState.loading,
                icon = AnimeExtensionIcon.Delete.imageVector,
                colors = colors,
            )
            val secondaryAction = if (extension.hasUpdate) AnimeExtensionAction.Update else AnimeExtensionAction.AppInfo
            val secondaryState = extension.actionState(secondaryAction, platform)
            AnimeExtensionActionTextButton(
                text = if (extension.hasUpdate) "Update" else "App info",
                onClick = {
                    actions.dispatchExtensionAction(secondaryAction, extension)
                },
                modifier = Modifier.weight(1f),
                enabled = secondaryState.enabled,
                loading = secondaryState.loading,
                icon = if (extension.hasUpdate) {
                    AnimeExtensionIcon.Install.imageVector
                } else {
                    AnimeExtensionIcon.Info.imageVector
                },
                colors = colors,
            )
        }
        AnimeExtensionIncognitoRow(
            extension = extension,
            incognito = incognito,
            actions = actions,
            colors = colors,
        )
        if (!supportState.supported) {
            AnimeExtensionStatusRow(
                title = supportState.title,
                subtitle = supportState.unsupportedDetail(extension, platform),
                icon = AnimeExtensionIcon.Block,
                error = true,
                colors = colors,
            )
        }
        if (extension.obsolete) {
            AnimeExtensionStatusRow(
                title = "Obsolete extension",
                subtitle = "This anime extension is no longer supported by its repository.",
                icon = AnimeExtensionIcon.Error,
                error = true,
                colors = colors,
            )
        }
        Divider(color = colors.divider)
    }
}

@Composable
fun AnimeExtensionInfoText(
    primaryText: String,
    secondaryText: String,
    modifier: Modifier = Modifier,
    error: Boolean = false,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AnimeExtensionLabel(
            text = primaryText,
            color = if (error) colors.error else colors.content,
            size = 15,
            weight = if (error) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 2,
            lineHeight = 19,
            modifier = Modifier.fillMaxWidth(),
        )
        AnimeExtensionLabel(
            text = secondaryText,
            color = colors.secondaryContent,
            size = 11,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun AnimeExtensionInfoDivider(
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Box(
        modifier = modifier
            .height(24.dp)
            .background(colors.divider)
            .size(width = 1.dp, height = 24.dp),
    )
}

@Composable
fun AnimeExtensionIncognitoRow(
    extension: AnimeExtensionUiModel,
    incognito: Boolean,
    actions: AnimeExtensionActions,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { actions.dispatchExtensionAction(AnimeExtensionAction.ToggleIncognito, extension) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(colors.surfaceVariant, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = AnimeExtensionIcon.Security.imageVector,
                contentDescription = null,
                tint = if (incognito) colors.primary else colors.secondaryContent,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            AnimeExtensionLabel(
                text = "Incognito mode",
                color = colors.content,
                size = 14,
                weight = FontWeight.SemiBold,
            )
            AnimeExtensionLabel(
                text = "Hide anime browsing and playback activity for this extension.",
                color = colors.secondaryContent,
                size = 11,
                lineHeight = 16,
                maxLines = 2,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        Switch(
            checked = incognito,
            onCheckedChange = { actions.dispatchExtensionAction(AnimeExtensionAction.ToggleIncognito, extension) },
        )
    }
}

@Composable
fun AnimeExtensionSourceSwitchRow(
    extension: AnimeExtensionUiModel,
    source: AnimeExtensionSourceUiModel,
    actions: AnimeExtensionActions,
    modifier: Modifier = Modifier,
    colors: AnimeExtensionUiColors = AnimeExtensionUiDefaults.colors(),
) {
    val actionState = source.actionState(extension)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .clickable(enabled = actionState.enabled && !actionState.loading) {
                actions.onToggleSource(extension, source)
            }
            .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimeExtensionAvatarTile(
            marker = source.language.take(2).uppercase().ifBlank { "S" },
            icon = AnimeExtensionIcon.Web,
            active = source.enabled,
            colors = colors,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
        ) {
            AnimeExtensionLabel(
                text = source.displayName,
                color = colors.content,
                size = 14,
                weight = FontWeight.SemiBold,
            )
            AnimeExtensionLabel(
                text = actionState.message ?: source.subtitle.ifBlank { source.language.animeExtensionLanguageDisplayName() },
                color = if (actionState.status == AnimeExtensionSourceToggleStatus.Error) {
                    colors.error
                } else {
                    colors.secondaryContent
                },
                size = 11,
                lineHeight = 16,
                maxLines = 2,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        if (source.configurable) {
            AnimeExtensionIconButton(
                icon = AnimeExtensionIcon.Settings,
                contentDescription = "Open ${source.name} settings",
                enabled = extension.installed,
                onClick = { actions.onOpenSourceSettings(extension, source) },
                colors = colors,
            )
        }
        Switch(
            checked = source.enabled,
            onCheckedChange = { actions.onToggleSource(extension, source) },
            enabled = actionState.enabled && !actionState.loading,
        )
    }
}
