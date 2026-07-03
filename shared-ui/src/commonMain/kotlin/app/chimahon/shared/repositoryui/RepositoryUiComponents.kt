package app.chimahon.shared.repositoryui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChimahonRepositoryUiColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val content: Color,
    val secondaryContent: Color,
    val primary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val divider: Color,
    val warning: Color,
    val error: Color,
    val success: Color,
)

object ChimahonRepositoryUiDefaults {
    @Composable
    fun colors(
        background: Color = MaterialTheme.colors.background,
        surface: Color = MaterialTheme.colors.surface,
        surfaceVariant: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
        content: Color = MaterialTheme.colors.onSurface,
        secondaryContent: Color = content.copy(alpha = 0.64f),
        primary: Color = MaterialTheme.colors.primary,
        primaryContainer: Color = MaterialTheme.colors.primary.copy(alpha = 0.14f),
        onPrimaryContainer: Color = MaterialTheme.colors.primary,
        divider: Color = content.copy(alpha = 0.10f),
        warning: Color = Color(0xFF9A6500),
        error: Color = MaterialTheme.colors.error,
        success: Color = Color(0xFF2E7D32),
    ): ChimahonRepositoryUiColors {
        return ChimahonRepositoryUiColors(
            background = background,
            surface = surface,
            surfaceVariant = surfaceVariant,
            content = content,
            secondaryContent = secondaryContent,
            primary = primary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            divider = divider,
            warning = warning,
            error = error,
            success = success,
        )
    }
}

@Composable
fun ChimahonRepositoryList(
    state: ChimahonRepositoryListState,
    actions: ChimahonRepositoryUiActions,
    modifier: Modifier = Modifier,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(key = "repository-header") {
            ChimahonRepositoryListHeader(
                state = state,
                actions = actions,
                colors = colors,
            )
        }
        if (state.repositories.isEmpty()) {
            item(key = "repository-empty") {
                ChimahonRepositoryEmptyState(
                    onAdd = actions.onAdd,
                    colors = colors,
                )
            }
        } else {
            items(state.repositories, key = { it.baseUrl }) { repository ->
                ChimahonRepositoryCard(
                    repository = repository,
                    actions = actions,
                    colors = colors,
                )
            }
        }
        state.message?.takeIf { it.isNotBlank() }?.let { message ->
            item(key = "repository-message") {
                ChimahonRepositoryStatusMessage(
                    title = message,
                    subtitle = state.selectedRepository?.displayName ?: "Extension repositories",
                    error = message.startsWith("Could not", ignoreCase = true),
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonRepositoryListHeader(
    state: ChimahonRepositoryListState,
    actions: ChimahonRepositoryUiActions,
    modifier: Modifier = Modifier,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            ChimahonRepositoryLabel(
                text = "Extension repositories",
                color = colors.content,
                size = 18,
                weight = FontWeight.SemiBold,
            )
            val subtitle = buildString {
                append("${state.repositories.size} repo(s)")
                if (state.disabledCount > 0) append(", ${state.disabledCount} disabled")
                append(if (state.autoSyncEnabled) ", auto sync on" else ", auto sync off")
            }
            ChimahonRepositoryLabel(
                text = subtitle,
                color = colors.secondaryContent,
                size = 12,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
        IconButton(
            onClick = actions.onRefreshAll,
            enabled = state.repositories.isNotEmpty() && !state.syncing,
            modifier = Modifier.size(40.dp),
        ) {
            if (state.syncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = colors.primary,
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "Refresh all repositories",
                    tint = colors.secondaryContent,
                )
            }
        }
        IconButton(
            onClick = actions.onAdd,
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add repository",
                tint = colors.primary,
            )
        }
    }
}

@Composable
fun ChimahonRepositoryCard(
    repository: ChimahonRepositoryUiModel,
    actions: ChimahonRepositoryUiActions,
    modifier: Modifier = Modifier,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
    showSelectedActions: Boolean = repository.selected,
) {
    val borderColor = when {
        repository.selected -> colors.primary.copy(alpha = 0.42f)
        repository.sync.isError -> colors.error.copy(alpha = 0.42f)
        else -> colors.divider
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = repository.displayName }
            .alpha(if (repository.enabled) 1f else 0.62f),
        color = if (repository.selected) colors.primary.copy(alpha = 0.035f) else colors.surface,
        contentColor = colors.content,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        elevation = 0.dp,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { actions.perform(ChimahonRepositoryAction.Select, repository) }
                    .padding(start = 14.dp, end = 6.dp, top = 12.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ChimahonRepositoryAvatar(
                    text = repository.initial,
                    active = repository.selected && repository.enabled,
                    trusted = repository.trusted,
                    colors = colors,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ChimahonRepositoryLabel(
                            text = repository.displayName,
                            color = colors.content,
                            size = 15,
                            weight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
                        if (repository.selected) {
                            ChimahonRepositoryStatusPill(
                                text = "Selected",
                                selected = true,
                                colors = colors,
                            )
                        }
                    }
                    ChimahonRepositoryLabel(
                        text = repository.baseUrl,
                        color = colors.secondaryContent,
                        size = 12,
                    )
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ChimahonRepositoryStatusPill(
                            text = repository.host,
                            selected = false,
                            colors = colors,
                        )
                        ChimahonRepositoryStatusPill(
                            text = if (repository.enabled) "Enabled" else "Disabled",
                            selected = repository.enabled,
                            error = !repository.enabled,
                            colors = colors,
                        )
                        if (repository.trusted) {
                            ChimahonRepositoryStatusPill(
                                text = "Signed",
                                selected = true,
                                colors = colors,
                            )
                        }
                    }
                }
                ChimahonRepositorySyncIndicator(
                    sync = repository.sync,
                    colors = colors,
                )
            }
            ChimahonRepositoryExtensionBadges(
                counts = repository.counts,
                modifier = Modifier.padding(start = 68.dp, end = 12.dp, bottom = 9.dp),
                colors = colors,
            )
            ChimahonRepositorySyncStatusRow(
                sync = repository.sync,
                enabled = repository.enabled,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                colors = colors,
            )
            if (showSelectedActions) {
                ChimahonRepositoryActionStrip(
                    repository = repository,
                    actions = actions,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 10.dp),
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun ChimahonRepositoryExtensionBadges(
    counts: ChimahonRepositoryExtensionCounts,
    modifier: Modifier = Modifier,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    val badges = counts.badgeSpecs()
    if (badges.isEmpty()) return
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(badges, key = { it.label }) { badge ->
            ChimahonRepositoryCountBadge(
                label = badge.label,
                value = badge.value,
                active = badge.active,
                warning = badge.warning,
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonRepositoryCountBadge(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    warning: Boolean = false,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    val background = when {
        warning -> colors.warning.copy(alpha = 0.14f)
        active -> colors.primaryContainer
        else -> colors.surfaceVariant
    }
    val foreground = when {
        warning -> colors.warning
        active -> colors.primary
        else -> colors.secondaryContent
    }
    Row(
        modifier = modifier
            .heightIn(min = 28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChimahonRepositoryLabel(
            text = value.toString(),
            color = foreground,
            size = 12,
            weight = FontWeight.Bold,
            maxLines = 1,
        )
        Spacer(Modifier.width(5.dp))
        ChimahonRepositoryLabel(
            text = label,
            color = foreground,
            size = 11,
            weight = FontWeight.Medium,
            maxLines = 1,
        )
    }
}

@Composable
fun ChimahonRepositorySyncStatusRow(
    sync: ChimahonRepositorySyncState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    val status = if (enabled) sync.status else ChimahonRepositorySyncStatus.Disabled
    val statusColor = status.repositoryStatusColor(colors)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(statusColor.copy(alpha = 0.08f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (sync.isLoading && enabled) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = colors.primary,
            )
        } else {
            Icon(
                imageVector = status.repositoryStatusIcon(),
                contentDescription = status.title,
                tint = statusColor,
                modifier = Modifier.size(19.dp),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        ) {
            ChimahonRepositoryLabel(
                text = sync.progressLabel ?: status.title,
                color = statusColor,
                size = 12,
                weight = FontWeight.SemiBold,
            )
            val subtitle = sync.message ?: sync.lastSyncedLabel?.let { "Last synced $it" }
            if (!subtitle.isNullOrBlank()) {
                ChimahonRepositoryLabel(
                    text = subtitle,
                    color = colors.secondaryContent,
                    size = 11,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
fun ChimahonRepositoryActionStrip(
    repository: ChimahonRepositoryUiModel,
    actions: ChimahonRepositoryUiActions,
    modifier: Modifier = Modifier,
    actionSpecs: List<ChimahonRepositoryActionSpec> = repository.defaultActionSpecs(),
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
    ) {
        items(actionSpecs, key = { it.action.name }) { spec ->
            ChimahonRepositoryActionButton(
                spec = spec,
                onClick = { actions.perform(spec.action, repository) },
                colors = colors,
            )
        }
    }
}

@Composable
fun ChimahonRepositoryActionButton(
    spec: ChimahonRepositoryActionSpec,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    val foreground = when {
        !spec.enabled -> colors.secondaryContent.copy(alpha = 0.42f)
        spec.destructive -> colors.error
        else -> colors.secondaryContent
    }
    Column(
        modifier = modifier
            .width(76.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = spec.enabled, onClick = onClick)
            .padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (spec.destructive) {
                        colors.error.copy(alpha = 0.10f)
                    } else {
                        colors.surfaceVariant
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = spec.action.repositoryActionIcon(),
                contentDescription = spec.title,
                tint = foreground,
                modifier = Modifier.size(20.dp),
            )
        }
        ChimahonRepositoryLabel(
            text = spec.title,
            color = foreground,
            size = 10,
            weight = FontWeight.Medium,
            maxLines = 1,
        )
    }
}

@Composable
fun ChimahonRepositoryValidationMessages(
    validation: ChimahonRepositoryValidationResult,
    modifier: Modifier = Modifier,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    if (validation.messages.isEmpty()) return
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        validation.messages.forEach { message ->
            val messageColor = message.severity.repositoryValidationColor(colors)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = message.severity.repositoryValidationIcon(),
                    contentDescription = message.severity.name,
                    tint = messageColor,
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .size(17.dp),
                )
                ChimahonRepositoryLabel(
                    text = message.text,
                    color = messageColor,
                    size = 12,
                    lineHeight = 16,
                    maxLines = 3,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
fun ChimahonRepositoryEmptyState(
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surface,
        contentColor = colors.content,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, colors.divider),
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChimahonRepositoryAvatar(
                text = "R",
                size = 56.dp,
                active = true,
                colors = colors,
            )
            ChimahonRepositoryLabel(
                text = "No repositories",
                color = colors.content,
                size = 16,
                weight = FontWeight.SemiBold,
            )
            ChimahonRepositoryLabel(
                text = "Add a compatible extension repository to browse installable extensions.",
                color = colors.secondaryContent,
                size = 12,
                lineHeight = 17,
                maxLines = 3,
            )
            TextButton(onClick = onAdd) {
                Text("Add repository")
            }
        }
    }
}

@Composable
fun ChimahonRepositoryStatusMessage(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    error: Boolean = false,
    colors: ChimahonRepositoryUiColors = ChimahonRepositoryUiDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (error) colors.error.copy(alpha = 0.08f) else colors.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (error) Icons.Outlined.ErrorOutline else Icons.Outlined.Info,
            contentDescription = title,
            tint = if (error) colors.error else colors.secondaryContent,
            modifier = Modifier.size(21.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            ChimahonRepositoryLabel(
                text = title,
                color = if (error) colors.error else colors.content,
                size = 13,
                weight = FontWeight.SemiBold,
                maxLines = 2,
            )
            ChimahonRepositoryLabel(
                text = subtitle,
                color = colors.secondaryContent,
                size = 11,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun ChimahonRepositorySyncIndicator(
    sync: ChimahonRepositorySyncState,
    colors: ChimahonRepositoryUiColors,
) {
    Box(
        modifier = Modifier.size(40.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (sync.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(19.dp),
                strokeWidth = 2.dp,
                color = colors.primary,
            )
        } else {
            Icon(
                imageVector = sync.status.repositoryStatusIcon(),
                contentDescription = sync.status.title,
                tint = sync.status.repositoryStatusColor(colors),
                modifier = Modifier.size(21.dp),
            )
        }
    }
}

@Composable
private fun ChimahonRepositoryStatusPill(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    error: Boolean = false,
    colors: ChimahonRepositoryUiColors,
) {
    val background = when {
        error -> colors.error.copy(alpha = 0.12f)
        selected -> colors.primaryContainer
        else -> colors.surfaceVariant
    }
    val foreground = when {
        error -> colors.error
        selected -> colors.primary
        else -> colors.secondaryContent
    }
    Box(
        modifier = modifier
            .heightIn(min = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        ChimahonRepositoryLabel(
            text = text,
            color = foreground,
            size = 10,
            weight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun ChimahonRepositoryAvatar(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    active: Boolean = false,
    trusted: Boolean = false,
    colors: ChimahonRepositoryUiColors,
) {
    val background = if (active) colors.primaryContainer else colors.surfaceVariant
    val foreground = if (active) colors.primary else colors.secondaryContent
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape((size.value * 0.22f).dp))
            .background(background)
            .border(1.dp, colors.divider, RoundedCornerShape((size.value * 0.22f).dp)),
        contentAlignment = Alignment.Center,
    ) {
        ChimahonRepositoryLabel(
            text = text.take(3).uppercase(),
            color = foreground,
            size = if (text.length > 2) 10 else 14,
            weight = FontWeight.Bold,
            maxLines = 1,
        )
        if (trusted) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size((size.value * 0.36f).dp)
                    .clip(CircleShape)
                    .background(colors.surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Security,
                    contentDescription = "Signed repository",
                    tint = foreground,
                    modifier = Modifier.size((size.value * 0.22f).dp),
                )
            }
        }
    }
}

@Composable
private fun ChimahonRepositoryLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color,
    size: Int,
    weight: FontWeight = FontWeight.Normal,
    maxLines: Int = 1,
    lineHeight: Int = size + 4,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = size.sp,
        fontWeight = weight,
        lineHeight = lineHeight.sp,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

private data class RepositoryBadgeSpec(
    val label: String,
    val value: Int,
    val active: Boolean = false,
    val warning: Boolean = false,
)

private fun ChimahonRepositoryExtensionCounts.badgeSpecs(): List<RepositoryBadgeSpec> {
    val badges = mutableListOf<RepositoryBadgeSpec>()
    badges += RepositoryBadgeSpec("Available", available, active = available > 0)
    if (installed > 0) badges += RepositoryBadgeSpec("Installed", installed, active = true)
    if (updates > 0) badges += RepositoryBadgeSpec("Updates", updates, warning = true)
    if (sources > 0) badges += RepositoryBadgeSpec("Sources", sources)
    if (nsfw > 0) badges += RepositoryBadgeSpec("NSFW", nsfw, warning = true)
    if (javascript > 0) badges += RepositoryBadgeSpec("JS", javascript)
    if (androidApk > 0) badges += RepositoryBadgeSpec("APK", androidApk)
    return badges
}

private fun ChimahonRepositorySyncStatus.repositoryStatusIcon(): ImageVector {
    return when (this) {
        ChimahonRepositorySyncStatus.Idle -> Icons.Outlined.Info
        ChimahonRepositorySyncStatus.Queued,
        ChimahonRepositorySyncStatus.Syncing,
        -> Icons.Outlined.Sync
        ChimahonRepositorySyncStatus.Synced -> Icons.Outlined.CheckCircle
        ChimahonRepositorySyncStatus.Failed -> Icons.Outlined.ErrorOutline
        ChimahonRepositorySyncStatus.Disabled -> Icons.Outlined.Block
    }
}

private fun ChimahonRepositorySyncStatus.repositoryStatusColor(
    colors: ChimahonRepositoryUiColors,
): Color {
    return when (this) {
        ChimahonRepositorySyncStatus.Idle -> colors.secondaryContent
        ChimahonRepositorySyncStatus.Queued,
        ChimahonRepositorySyncStatus.Syncing,
        -> colors.primary
        ChimahonRepositorySyncStatus.Synced -> colors.success
        ChimahonRepositorySyncStatus.Failed -> colors.error
        ChimahonRepositorySyncStatus.Disabled -> colors.secondaryContent
    }
}

private fun ChimahonRepositoryValidationSeverity.repositoryValidationIcon(): ImageVector {
    return when (this) {
        ChimahonRepositoryValidationSeverity.Info -> Icons.Outlined.Info
        ChimahonRepositoryValidationSeverity.Warning -> Icons.Outlined.ErrorOutline
        ChimahonRepositoryValidationSeverity.Error -> Icons.Outlined.ErrorOutline
    }
}

private fun ChimahonRepositoryValidationSeverity.repositoryValidationColor(
    colors: ChimahonRepositoryUiColors,
): Color {
    return when (this) {
        ChimahonRepositoryValidationSeverity.Info -> colors.secondaryContent
        ChimahonRepositoryValidationSeverity.Warning -> colors.warning
        ChimahonRepositoryValidationSeverity.Error -> colors.error
    }
}

private fun ChimahonRepositoryAction.repositoryActionIcon(): ImageVector {
    return when (this) {
        ChimahonRepositoryAction.Select -> Icons.Outlined.CheckCircle
        ChimahonRepositoryAction.Refresh -> Icons.Outlined.Refresh
        ChimahonRepositoryAction.Edit -> Icons.Outlined.Edit
        ChimahonRepositoryAction.CopyIndexUrl -> Icons.Outlined.ContentCopy
        ChimahonRepositoryAction.OpenWebsite -> Icons.Outlined.OpenInNew
        ChimahonRepositoryAction.Enable -> Icons.Outlined.Visibility
        ChimahonRepositoryAction.Disable -> Icons.Outlined.VisibilityOff
        ChimahonRepositoryAction.Trust -> Icons.Outlined.Security
        ChimahonRepositoryAction.Remove -> Icons.Outlined.Delete
    }
}
