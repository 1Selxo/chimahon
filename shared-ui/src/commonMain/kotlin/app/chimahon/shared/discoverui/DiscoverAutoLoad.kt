package app.chimahon.shared.discoverui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ChimahonDiscoverAutoLoadEffect(
    listState: LazyListState,
    state: ChimahonDiscoverAutoLoadState,
    onLoadMore: () -> Unit,
    prefetchDistance: Int = 6,
) {
    ChimahonDiscoverInitialAutoLoadEffect(state, onLoadMore)

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

@Composable
fun ChimahonDiscoverAutoLoadEffect(
    gridState: LazyGridState,
    state: ChimahonDiscoverAutoLoadState,
    onLoadMore: () -> Unit,
    prefetchDistance: Int = 10,
) {
    ChimahonDiscoverInitialAutoLoadEffect(state, onLoadMore)

    LaunchedEffect(gridState, state.enabled, state.hasMore, state.loading) {
        if (!state.enabled || !state.hasMore || state.loading) return@LaunchedEffect
        snapshotFlow {
            val layoutInfo = gridState.layoutInfo
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.maxOfOrNull { it.index } ?: -1
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

@Composable
private fun ChimahonDiscoverInitialAutoLoadEffect(
    state: ChimahonDiscoverAutoLoadState,
    onLoadMore: () -> Unit,
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
            state.visibleCount < minOf(state.totalCount, ChimahonDiscoverPrefetchItemCount)
        ) {
            onLoadMore()
        }
    }
}
