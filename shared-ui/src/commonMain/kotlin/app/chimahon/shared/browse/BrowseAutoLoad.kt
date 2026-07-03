package app.chimahon.shared.browse

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun BrowseAutoLoadEffect(
    listState: LazyListState,
    state: BrowseAutoLoadState,
    onLoadMore: () -> Unit,
    prefetchDistance: Int = 5,
) {
    LaunchedEffect(
        state.enabled,
        state.hasMore,
        state.loading,
        state.visibleCount,
        state.totalCount,
    ) {
        if (
            state.enabled &&
            state.hasMore &&
            !state.loading &&
            state.visibleCount < minOf(state.totalCount, BrowseExtensionPrefetchItemCount)
        ) {
            onLoadMore()
        }
    }

    LaunchedEffect(listState, state.enabled, state.hasMore, state.loading) {
        if (!state.enabled || !state.hasMore || state.loading) return@LaunchedEffect
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            lastVisibleIndex >= (layoutInfo.totalItemsCount - prefetchDistance).coerceAtLeast(0)
        }
            .distinctUntilChanged()
            .collect { nearEnd ->
                if (nearEnd) {
                    onLoadMore()
                }
            }
    }
}
