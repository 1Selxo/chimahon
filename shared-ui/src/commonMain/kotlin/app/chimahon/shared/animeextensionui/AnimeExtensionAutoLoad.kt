package app.chimahon.shared.animeextensionui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun AnimeExtensionAutoLoadEffect(
    listState: LazyListState,
    state: AnimeExtensionAutoLoadState,
    onLoadMore: () -> Unit,
    prefetchItemCount: Int = AnimeExtensionPrefetchItemCount,
) {
    val shouldLoadMissingList = state.enabled &&
        state.hasMore &&
        state.missingList &&
        !state.loading &&
        state.visibleCount == 0 &&
        state.totalCount > 0
    val shouldLoadMore by remember(listState, state) {
        derivedStateOf {
            if (!state.enabled || !state.hasMore || state.loading) return@derivedStateOf false
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            val totalItems = layoutInfo.totalItemsCount
            totalItems == 0 || lastVisibleIndex >= totalItems - prefetchItemCount.coerceAtLeast(1)
        }
    }
    LaunchedEffect(shouldLoadMissingList, state.visibleCount, state.totalCount) {
        if (shouldLoadMissingList) onLoadMore()
    }
    LaunchedEffect(shouldLoadMore, state.visibleCount, state.totalCount) {
        if (shouldLoadMore) onLoadMore()
    }
}
