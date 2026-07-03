package app.chimahon.shared.browse

import app.chimahon.shared.ChimahonMangaEntry
import app.chimahon.shared.ChimahonRemoteMangaEntry
import app.chimahon.shared.ChimahonSourceEntry
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BrowseMigrationStepHeader(
    state: BrowseMigrationState,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            BrowseIconButton(
                icon = BrowseIcon.Back,
                contentDescription = "Back",
                onClick = onBack,
                colors = colors,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = if (onBack == null) 4.dp else 10.dp),
        ) {
            BrowseLabel(
                text = state.step.title,
                color = colors.content,
                size = 17,
                weight = FontWeight.SemiBold,
            )
            BrowseLabel(
                text = state.browseMigrationBreadcrumb(),
                color = colors.secondaryContent,
                size = 11,
                maxLines = 1,
                modifier = Modifier.padding(top = 3.dp),
            )
        }
    }
}

@Composable
fun BrowseMigrationSourceCard(
    source: ChimahonSourceEntry,
    mangaCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    BrowseMigrationRow(
        title = source.name,
        subtitle = "${source.language.browseLanguageCode()} - $mangaCount title(s)",
        marker = source.browseSourceInitials(),
        icon = if (source.isBrowseLocalSource()) BrowseIcon.Local else BrowseIcon.Source,
        badges = listOfNotNull(
            source.language.browseLanguageCode().uppercase() to false,
            if (source.supportsLatest) "LATEST" to true else null,
            "$mangaCount" to (mangaCount > 0),
        ),
        onClick = onClick,
        modifier = modifier,
        colors = colors,
    )
}

@Composable
fun BrowseMigrationMangaCard(
    manga: ChimahonMangaEntry,
    sourceName: String,
    onOpen: () -> Unit,
    onMigrate: () -> Unit,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    BrowseMigrationRow(
        title = manga.title,
        subtitle = manga.browseMigrationSubtitle(sourceName),
        marker = manga.title.firstOrNull()?.uppercaseChar()?.toString() ?: "M",
        icon = BrowseIcon.Source,
        badges = listOfNotNull(
            manga.status.takeIf(String::isNotBlank)?.uppercase()?.let { it to false },
            if (manga.favorite) "LIBRARY" to true else null,
            if (!manga.initialized) "PARTIAL" to false else null,
        ),
        onClick = onOpen,
        modifier = modifier,
        colors = colors,
    ) {
        BrowseIconButton(
            icon = BrowseIcon.Swap,
            contentDescription = "Migrate ${manga.title}",
            onClick = onMigrate,
            colors = colors,
        )
    }
}

@Composable
fun BrowseMigrationCandidateCard(
    candidate: ChimahonRemoteMangaEntry,
    sourceName: String,
    migrating: Boolean,
    onOpen: () -> Unit,
    onMigrate: () -> Unit,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    BrowseMigrationRow(
        title = candidate.title,
        subtitle = candidate.browseCandidateSubtitle(sourceName),
        marker = candidate.title.firstOrNull()?.uppercaseChar()?.toString() ?: "R",
        icon = BrowseIcon.Source,
        badges = listOfNotNull(
            candidate.status.takeIf(String::isNotBlank)?.uppercase()?.let { it to false },
            if (candidate.initialized) "READY" to true else "PARTIAL" to false,
        ),
        onClick = onOpen,
        modifier = modifier,
        colors = colors,
    ) {
        BrowseIconButton(
            icon = BrowseIcon.Check,
            contentDescription = "Migrate to ${candidate.title}",
            loading = migrating,
            onClick = onMigrate,
            colors = colors,
        )
    }
}

@Composable
fun BrowseMigrationSearchCard(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    targetSourceName: String? = null,
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
                text = targetSourceName?.let { "Search $it" } ?: "Search target source",
                color = colors.content,
                size = 15,
                weight = FontWeight.SemiBold,
            )
            BrowseSearchField(
                query = query,
                onQueryChange = onQueryChange,
                placeholder = "Manga title",
                onSearch = onSearch,
                colors = colors,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
fun BrowseMigrationSummaryCard(
    state: BrowseMigrationState,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    colors: BrowseColors = BrowseDefaults.colors(),
) {
    val message = state.message
    BrowseStatusRow(
        title = when {
            message == null -> "${state.resultCount} result(s)"
            message.startsWith("Migrated") -> "Migration complete"
            else -> "Migration status"
        },
        subtitle = message ?: state.browseMigrationBreadcrumb(),
        icon = if (message?.startsWith("Migrated") == true) BrowseIcon.Check else BrowseIcon.Info,
        action = onRetry?.let { "Retry" },
        onAction = onRetry,
        error = message?.startsWith("Could not") == true || message?.startsWith("Migration failed") == true,
        modifier = modifier,
        colors = colors,
    )
}

@Composable
private fun BrowseMigrationRow(
    title: String,
    subtitle: String,
    marker: String,
    icon: BrowseIcon,
    badges: List<Pair<String, Boolean>>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: BrowseColors,
    trailing: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .clickable(onClick = onClick)
                .padding(start = 16.dp, end = 8.dp, top = 9.dp, bottom = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrowseAvatarTile(
                marker = marker,
                icon = icon,
                active = true,
                colors = colors,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                BrowseLabel(
                    text = title,
                    color = colors.content,
                    size = 14,
                    weight = FontWeight.SemiBold,
                    maxLines = 2,
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
            if (trailing != null) {
                trailing()
            } else {
                BrowseIconButton(
                    icon = BrowseIcon.Forward,
                    contentDescription = "Open $title",
                    onClick = onClick,
                    colors = colors,
                )
            }
        }
        BrowseDivider(startIndent = 72.dp, colors = colors)
    }
}

@Composable
fun BrowseMigrationPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    ) {
        Text(text)
    }
}

private fun BrowseMigrationState.browseMigrationBreadcrumb(): String {
    return listOfNotNull(
        source?.name,
        manga?.title,
        targetSource?.name,
        query.takeIf(String::isNotBlank),
    ).joinToString(" / ").ifBlank { "Choose a source, title, and replacement." }
}
