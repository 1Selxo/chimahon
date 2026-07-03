package app.chimahon.shared.reader.appbars

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ReaderTopBar(
    state: ReaderTopBarState,
    actions: ReaderTopBarActions,
    modifier: Modifier = Modifier,
    colors: ReaderAppBarColors = ReaderAppBarDefaults.colors(),
) {
    val overflowActions = listOfNotNull(
        actions.onOpenInWebView?.let {
            state.openInWebViewContentDescription to it
        },
        actions.onOpenInBrowser?.let {
            state.openInBrowserContentDescription to it
        },
        actions.onShare?.let {
            state.shareContentDescription to it
        },
    )
    var overflowExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier.clickable(onClick = actions.onClickTopAppBar),
        backgroundColor = Color.Transparent,
        contentColor = colors.content,
        navigationIcon = {
            ReaderIconButton(
                icon = ReaderAppBarIcon.Back,
                contentDescription = state.navigateUpContentDescription,
                onClick = actions.navigateUp,
                tint = colors.content,
            )
        },
        title = {
            Column {
                Text(
                    text = state.mangaTitle.orEmpty(),
                    color = colors.content,
                    style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                state.chapterTitle?.takeIf { it.isNotBlank() }?.let { chapterTitle ->
                    Text(
                        text = chapterTitle,
                        color = colors.secondaryContent,
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        actions = {
            if (state.ocr.visible && actions.onToggleOcr != null) {
                ReaderIconButton(
                    icon = ReaderAppBarIcon.Ocr,
                    contentDescription = state.ocr.contentDescription,
                    onClick = actions.onToggleOcr,
                    tint = colors.content,
                    active = state.ocr.enabled,
                    enabled = state.ocr.canToggle,
                    loading = state.ocr.loading,
                    activeTint = colors.primary,
                )
            }
            if (state.showBookmark) {
                ReaderIconButton(
                    icon = if (state.bookmarked) ReaderAppBarIcon.Bookmark else ReaderAppBarIcon.BookmarkBorder,
                    contentDescription = if (state.bookmarked) {
                        state.removeBookmarkContentDescription
                    } else {
                        state.bookmarkContentDescription
                    },
                    onClick = actions.onToggleBookmarked,
                    tint = colors.content,
                    active = state.bookmarked,
                    activeTint = colors.primary,
                )
            }
            if (overflowActions.isNotEmpty()) {
                ReaderIconButton(
                    icon = ReaderAppBarIcon.More,
                    contentDescription = state.overflowContentDescription,
                    onClick = { overflowExpanded = true },
                    tint = colors.content,
                )
                DropdownMenu(
                    expanded = overflowExpanded,
                    onDismissRequest = { overflowExpanded = false },
                ) {
                    overflowActions.forEach { (title, onClick) ->
                        DropdownMenuItem(
                            onClick = {
                                overflowExpanded = false
                                onClick()
                            },
                        ) {
                            Text(title)
                        }
                    }
                }
            }
        },
    )
}
