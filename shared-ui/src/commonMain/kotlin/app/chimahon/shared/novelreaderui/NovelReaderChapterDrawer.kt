package app.chimahon.shared.novelreaderui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NovelReaderChapterDrawer(
    state: NovelReaderUiState,
    actions: NovelReaderActions,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 10.dp, horizontal = 8.dp),
        color = palette.surface,
        shape = RoundedCornerShape(18.dp),
        elevation = 18.dp,
        border = BorderStroke(1.dp, palette.outline),
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.FormatListNumbered,
                    contentDescription = null,
                    tint = palette.onSurface,
                    modifier = Modifier.size(22.dp),
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                ) {
                    Text(
                        text = "Chapters",
                        color = palette.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                    )
                    Text(
                        text = "${state.chapters.size} chapter(s)",
                        color = palette.muted,
                        fontSize = 12.sp,
                    )
                }
                IconButton(onClick = actions.onCloseChapters) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close chapters", tint = palette.onSurface)
                }
            }
            Divider(color = palette.outline)

            if (state.chapters.isEmpty()) {
                NovelReaderDrawerEmpty(palette = palette, modifier = Modifier.weight(1f))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(
                        items = state.chapters,
                        key = { index, chapter -> chapter.stableLazyKey(index) },
                    ) { _, chapter ->
                        NovelReaderChapterDrawerRow(
                            chapter = chapter,
                            active = chapter.id == state.activeChapter?.id,
                            palette = palette,
                            onClick = { actions.onChapterSelected(chapter) },
                        )
                        val activeSections = if (chapter.id == state.activeChapter?.id) {
                            state.sections.filter { it.chapterId == chapter.id }
                        } else {
                            emptyList()
                        }
                        activeSections.forEachIndexed { sectionIndex, section ->
                            NovelReaderSectionDrawerRow(
                                section = section,
                                active = section.id == state.activeSection?.id,
                                palette = palette,
                                onClick = { actions.onSectionSelected(section) },
                            )
                            if (sectionIndex != activeSections.lastIndex) {
                                Divider(color = palette.outline.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
            Divider(color = palette.outline)
            NovelReaderChapterDrawerFooter(
                state = state,
                palette = palette,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun NovelReaderChapterDrawerRow(
    chapter: NovelReaderChapterUiModel,
    active: Boolean,
    palette: NovelReaderPalette,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(if (active) palette.selection else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            NovelReaderChapterDot(chapter = chapter, active = active, palette = palette)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
            ) {
                Text(
                    text = chapter.title,
                    color = palette.onSurface,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                val subtitle = listOfNotNull(chapter.subtitle, chapter.progressLabel).joinToString(" - ")
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        color = palette.muted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (chapter.bookmarked) {
                Text(text = "Saved", color = palette.muted, fontSize = 11.sp)
            }
        }
        if (chapter.progress > 0f) {
            LinearProgressIndicator(
                progress = chapter.progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = palette.onSurface.copy(alpha = 0.72f),
                backgroundColor = palette.outline,
            )
        }
    }
}

@Composable
fun NovelReaderSectionDrawerRow(
    section: NovelReaderSectionUiModel,
    active: Boolean,
    palette: NovelReaderPalette,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (active) palette.selection.copy(alpha = 0.72f) else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick)
            .padding(start = 48.dp, end = 16.dp, top = 9.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.MenuBook,
            contentDescription = null,
            tint = if (active) palette.onSurface else palette.muted,
            modifier = Modifier.size(18.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = section.displayTitle,
                color = if (active) palette.onSurface else palette.muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 13.sp,
            )
            section.characterCount?.let { count ->
                Text(text = "$count chars", color = palette.muted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun NovelReaderChapterDot(
    chapter: NovelReaderChapterUiModel,
    active: Boolean,
    palette: NovelReaderPalette,
) {
    val color = when {
        active -> palette.onSurface
        chapter.read -> palette.muted
        else -> palette.accent
    }
    Box(
        modifier = Modifier
            .size(if (active) 14.dp else 10.dp)
            .background(color = color, shape = CircleShape),
    )
}

@Composable
private fun NovelReaderDrawerEmpty(
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.MenuBook,
            contentDescription = null,
            tint = palette.muted,
            modifier = Modifier.size(42.dp),
        )
        Spacer(Modifier.height(12.dp))
        Text(text = "No chapter list", color = palette.onSurface, fontWeight = FontWeight.SemiBold)
        Text(text = "The EPUB spine has not been loaded yet.", color = palette.muted, fontSize = 12.sp)
    }
}

@Composable
private fun NovelReaderChapterDrawerFooter(
    state: NovelReaderUiState,
    palette: NovelReaderPalette,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = state.activeChapter?.title ?: state.title,
                color = palette.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 13.sp,
            )
            Text(
                text = listOfNotNull(
                    state.progressLabel,
                    state.progressPersistence.savedProgressLabel?.let { "saved $it" },
                ).joinToString(" - "),
                color = palette.muted,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (state.progressPersistence.pendingSave) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = palette.accent, shape = CircleShape),
            )
        }
    }
}
