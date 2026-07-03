package app.chimahon.shared.mangaui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonMangaDetailScreen(
    state: ChimahonMangaDetailUiState,
    actions: ChimahonMangaDetailActions,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 24.dp),
    coverContent: @Composable BoxScope.(ChimahonMangaHeaderUiModel) -> Unit = {
        ChimahonMangaCoverPlaceholder(title = it.title)
    },
) {
    when (state.contentState) {
        ChimahonMangaDetailContentState.Loading -> {
            ChimahonMangaDetailLoadingState(modifier = modifier)
        }
        ChimahonMangaDetailContentState.Error -> {
            ChimahonMangaDetailErrorState(
                message = state.errorMessage ?: "Manga failed to load",
                onRetry = actions.onRetry,
                modifier = modifier,
            )
        }
        ChimahonMangaDetailContentState.Empty -> {
            ChimahonMangaDetailEmptyState(modifier = modifier)
        }
        ChimahonMangaDetailContentState.Content -> {
            val manga = checkNotNull(state.manga)
            val visibleChapters = state.visibleChapters
            LazyColumn(
                modifier = modifier,
                contentPadding = contentPadding,
            ) {
                item(key = "manga-header") {
                    ChimahonMangaHeader(
                        manga = manga,
                        refreshing = state.refreshing,
                        onCoverClick = { actions.onCoverClick(manga) },
                        onFavoriteClick = { actions.onFavoriteClick(manga) },
                        onTrackingClick = { actions.onTrackingClick(manga) },
                        onWebViewClick = { actions.onWebViewClick(manga) },
                        onEditIntervalClick = { actions.onEditIntervalClick(manga) },
                        coverContent = { coverContent(manga) },
                    )
                }
                item(key = "manga-description") {
                    ChimahonMangaDescription(
                        manga = manga,
                        onTagClick = actions.onTagClick,
                    )
                }
                item(key = "manga-extra-buttons") {
                    ChimahonMangaInfoButtons(
                        manga = manga,
                        showRecommendButton = true,
                        showMergeButton = false,
                        onRecommendClick = { actions.onRecommendClick(manga) },
                        onMergeClick = { actions.onMergeClick(manga) },
                        onSourceActionClick = { sourceAction ->
                            if (sourceAction.action == ChimahonMangaSourceAction.OpenInWebView) {
                                actions.onWebViewClick(manga)
                            } else {
                                actions.onSourceActionClick(manga, sourceAction)
                            }
                        },
                    )
                }
                item(key = "chapter-controls") {
                    ChimahonMangaChapterControls(
                        chapterCount = visibleChapters.size,
                        totalChapterCount = state.chapters.size,
                        missingChapterCount = state.missingChapterCount,
                        sortState = state.sortState,
                        filterState = state.filterState,
                        onSortChange = actions.onSortChange,
                        onFilterClick = actions.onFilterClick,
                        onRefresh = actions.onRefresh,
                        displayMode = state.displayMode,
                        selectionState = state.chapterSelectionState,
                        onDisplayModeChange = actions.onDisplayModeChange,
                        onReadFilterChange = actions.onReadFilterChange,
                        onDownloadFilterChange = actions.onDownloadFilterChange,
                        onBookmarkFilterChange = actions.onBookmarkFilterChange,
                        onClearFilters = actions.onClearChapterFilters,
                        onBulkActionClick = actions.onChapterBulkActionClick,
                    )
                }
                if (visibleChapters.isEmpty()) {
                    item(key = "chapter-empty") {
                        ChimahonMangaChapterEmptyState(
                            hasFilters = state.filterState.hasActiveFilters || state.chapterQuery.isNotBlank(),
                        )
                    }
                } else {
                    items(
                        items = visibleChapters.withIndex().toList(),
                        key = { indexed -> indexed.value.stableLazyKey(indexed.index) },
                    ) { indexed ->
                        val chapter = indexed.value
                        ChimahonMangaChapterRow(
                            chapter = chapter,
                            displayMode = state.displayMode,
                            selected = chapter.id in state.selectedChapterIds,
                            onClick = { actions.onChapterClick(chapter) },
                            onLongClick = { actions.onChapterLongClick(chapter) },
                            onDownloadClick = { actions.onChapterDownloadClick(chapter) },
                            onReadClick = { actions.onChapterReadClick(chapter) },
                            onBookmarkClick = { actions.onChapterBookmarkClick(chapter) },
                            onSelectClick = { actions.onChapterSelectClick(chapter) },
                        )
                    }
                }
            }
        }
    }
}
