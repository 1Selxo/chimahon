package app.chimahon.shared.mangaui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChimahonMangaHeader(
    manga: ChimahonMangaHeaderUiModel,
    refreshing: Boolean,
    onCoverClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onTrackingClick: () -> Unit,
    onWebViewClick: () -> Unit,
    onEditIntervalClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonMangaCoverPlaceholder(title = manga.title)
    },
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.13f),
                        MaterialTheme.colors.background,
                    ),
                ),
            ),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val wide = maxWidth >= 600.dp
            if (wide) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ChimahonMangaCover(
                        title = manga.title,
                        onClick = onCoverClick,
                        modifier = Modifier.width(150.dp),
                        coverContent = coverContent,
                    )
                    ChimahonMangaHeaderText(
                        manga = manga,
                        refreshing = refreshing,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ChimahonMangaCover(
                        title = manga.title,
                        onClick = onCoverClick,
                        modifier = Modifier.width(128.dp),
                        coverContent = coverContent,
                    )
                    Spacer(Modifier.height(14.dp))
                    ChimahonMangaHeaderText(
                        manga = manga,
                        refreshing = refreshing,
                        centered = true,
                    )
                }
            }
        }
        ChimahonMangaActionRow(
            manga = manga,
            onFavoriteClick = onFavoriteClick,
            onTrackingClick = onTrackingClick,
            onWebViewClick = onWebViewClick,
            onEditIntervalClick = onEditIntervalClick,
        )
    }
}

@Composable
fun ChimahonMangaActionRow(
    manga: ChimahonMangaHeaderUiModel,
    onFavoriteClick: () -> Unit,
    onTrackingClick: () -> Unit,
    onWebViewClick: () -> Unit,
    onEditIntervalClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colors.background.copy(alpha = 0.92f),
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChimahonMangaActionButton(
                title = if (manga.inLibrary) "In library" else "Add",
                icon = if (manga.favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                selected = manga.favorite,
                onClick = onFavoriteClick,
            )
            ChimahonMangaActionButton(
                title = manga.nextUpdateLabel ?: "Interval",
                icon = Icons.Outlined.Schedule,
                selected = manga.nextUpdateLabel != null,
                onClick = onEditIntervalClick,
            )
            ChimahonMangaActionButton(
                title = if (manga.trackedCount > 0) "${manga.trackedCount} trackers" else "Tracking",
                icon = Icons.Outlined.Sync,
                selected = manga.trackedCount > 0,
                onClick = onTrackingClick,
            )
            if (!manga.webUrl.isNullOrBlank()) {
                ChimahonMangaActionButton(
                    title = "WebView",
                    icon = Icons.Outlined.Public,
                    selected = true,
                    onClick = onWebViewClick,
                )
            }
        }
    }
}

@Composable
fun ChimahonMangaDescription(
    manga: ChimahonMangaHeaderUiModel,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    defaultExpanded: Boolean = false,
) {
    var expanded by remember(manga.id) { mutableStateOf(defaultExpanded) }
    val description = manga.description?.takeIf(String::isNotBlank) ?: "No description available."
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
    ) {
        SelectionContainer {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp),
                text = description,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.body2,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (manga.tags.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            ) {
                itemsIndexed(manga.tags, key = { index, tag -> "manga-tag:$index:$tag" }) { _, tag ->
                    ChimahonMangaTagChip(
                        text = tag,
                        onClick = { onTagClick(tag) },
                    )
                }
            }
        }
    }
}

@Composable
fun ChimahonMangaInfoButtons(
    manga: ChimahonMangaHeaderUiModel,
    showRecommendButton: Boolean,
    showMergeButton: Boolean,
    onRecommendClick: () -> Unit,
    onMergeClick: () -> Unit,
    modifier: Modifier = Modifier,
    sourceActions: List<ChimahonMangaSourceActionModel> = manga.sourceActions(),
    onSourceActionClick: (ChimahonMangaSourceActionModel) -> Unit = {},
) {
    if (!showRecommendButton && !showMergeButton && sourceActions.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (showMergeButton) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onMergeClick,
            ) {
                Text("Merge with another source")
            }
        }
        if (showRecommendButton) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onRecommendClick,
                enabled = manga.title.isNotBlank(),
            ) {
                Text("Recommendations")
            }
        }
        sourceActions.forEach { action ->
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSourceActionClick(action) },
                enabled = action.enabled,
            ) {
                Icon(
                    imageVector = action.action.icon(),
                    contentDescription = action.label,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(action.label)
            }
        }
    }
}

@Composable
fun ChimahonMangaCover(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    coverContent: @Composable BoxScope.() -> Unit = {
        ChimahonMangaCoverPlaceholder(title = title)
    },
) {
    Box(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.08f)),
    ) {
        coverContent()
    }
}

@Composable
fun BoxScope.ChimahonMangaCoverPlaceholder(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .matchParentSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary.copy(alpha = 0.28f),
                        MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title.firstOrNull()?.uppercaseChar()?.toString() ?: "M",
            color = MaterialTheme.colors.onPrimary,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ChimahonMangaHeaderText(
    manga: ChimahonMangaHeaderUiModel,
    refreshing: Boolean,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
) {
    val alignment = if (centered) TextAlign.Center else TextAlign.Start
    Column(
        modifier = modifier,
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        Text(
            text = manga.title,
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
            textAlign = alignment,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = manga.sourceLine,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.68f),
            style = MaterialTheme.typography.body2,
            textAlign = alignment,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        manga.creatorLine?.let {
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = it,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.68f),
                style = MaterialTheme.typography.caption,
                textAlign = alignment,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        ChimahonMangaInfoChipRow(
            chips = manga.infoChips(refreshing),
            modifier = Modifier.padding(top = 10.dp),
        )
    }
}

@Composable
private fun ChimahonMangaActionButton(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = if (selected) MaterialTheme.colors.primary else LocalContentColor.current.copy(alpha = 0.56f)
    Column(
        modifier = Modifier
            .width(84.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = title,
            color = color,
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ChimahonMangaInfoChipRow(
    chips: List<ChimahonMangaInfoChipModel>,
    modifier: Modifier = Modifier,
) {
    if (chips.isEmpty()) return
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(end = 16.dp),
    ) {
        items(chips, key = { chip -> chip.stableKey }) { chip ->
            ChimahonMangaInfoChip(chip = chip)
        }
    }
}

@Composable
private fun ChimahonMangaInfoChip(chip: ChimahonMangaInfoChipModel) {
    val selected = chip.kind in setOf(
        ChimahonMangaInfoChipKind.Status,
        ChimahonMangaInfoChipKind.Library,
        ChimahonMangaInfoChipKind.Warning,
    )
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) {
            MaterialTheme.colors.primary.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colors.onSurface.copy(alpha = 0.07f)
        },
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            text = chip.label,
            color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.68f),
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChimahonMangaTagChip(
    text: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colors.primary.copy(alpha = 0.10f),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            text = text,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun ChimahonMangaSourceAction.icon(): ImageVector {
    return when (this) {
        ChimahonMangaSourceAction.BrowseSource -> Icons.Outlined.Public
        ChimahonMangaSourceAction.OpenInWebView -> Icons.Outlined.OpenInBrowser
        ChimahonMangaSourceAction.OpenExternal -> Icons.Outlined.Link
        ChimahonMangaSourceAction.CopyUrl -> Icons.Outlined.Link
    }
}
