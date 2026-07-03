package app.chimahon.shared.animeui

import app.chimahon.shared.ChimahonAnimeLibrarySettings as SavedAnimeLibrarySettings
import app.chimahon.shared.anime.ChimahonAnimeBackendCapabilities
import app.chimahon.shared.anime.ChimahonAnimeDetailSessionData
import app.chimahon.shared.anime.ChimahonAnimeHomeData

data class ChimahonAnimeHomeUiBundle(
    val library: ChimahonAnimeLibraryUiState = ChimahonAnimeLibraryUiState(),
    val history: ChimahonAnimeHistoryUiState = ChimahonAnimeHistoryUiState(),
    val capabilityBadges: List<ChimahonAnimeBadgeUiModel> = emptyList(),
    val errorMessage: String? = null,
) {
    val hasContent: Boolean
        get() = library.entries.isNotEmpty() || history.rows.isNotEmpty()
}

data class ChimahonAnimeDetailSessionUiBundle(
    val detail: ChimahonAnimeDetailUiState = ChimahonAnimeDetailUiState(),
    val player: ChimahonAnimePlayerUiState? = null,
    val capabilityBadges: List<ChimahonAnimeBadgeUiModel> = emptyList(),
    val errorMessage: String? = null,
)

fun ChimahonAnimeHomeData.toAnimeHomeUiBundle(
    settings: SavedAnimeLibrarySettings = SavedAnimeLibrarySettings(),
    query: String = "",
    selectedCategoryId: String? = null,
    selectedAnimeIds: Set<String> = emptySet(),
    trackedAnimeIds: Set<Long> = emptySet(),
    loading: Boolean = false,
    refreshing: Boolean = false,
    formatWatchedAt: (Long) -> String? = { null },
): ChimahonAnimeHomeUiBundle {
    return ChimahonAnimeHomeUiBundle(
        library = library.toAnimeLibraryUiState(
            settings = settings,
            query = query,
            selectedCategoryId = selectedCategoryId,
            selectedAnimeIds = selectedAnimeIds,
            trackedAnimeIds = trackedAnimeIds,
            loading = loading,
            refreshing = refreshing,
        ),
        history = history.toAnimeHistoryUiState(
            loading = loading,
            formatWatchedAt = formatWatchedAt,
        ),
        capabilityBadges = capabilities.toAnimeCapabilityBadges(),
        errorMessage = errors.firstOrNull()?.let { it.message ?: it.code.name },
    )
}

fun ChimahonAnimeDetailSessionData.toAnimeDetailSessionUiBundle(
    selectedEpisodeIds: Set<String> = emptySet(),
    selectedSeasonId: String? = null,
    episodeQuery: String = "",
    refreshing: Boolean = false,
): ChimahonAnimeDetailSessionUiBundle {
    return ChimahonAnimeDetailSessionUiBundle(
        detail = detail.toAnimeDetailUiState(
            selectedEpisodeIds = selectedEpisodeIds,
            selectedSeasonId = selectedSeasonId,
            episodeQuery = episodeQuery,
            refreshing = refreshing,
        ),
        player = player?.toAnimePlayerUiState(),
        capabilityBadges = capabilities.toAnimeCapabilityBadges(),
        errorMessage = errors.firstOrNull()?.let { it.message ?: it.code.name },
    )
}

private fun ChimahonAnimeBackendCapabilities.toAnimeCapabilityBadges(): List<ChimahonAnimeBadgeUiModel> {
    return listOfNotNull(
        if (library) ChimahonAnimeBadgeUiModel("Library", ChimahonAnimeBadgeKind.Source) else null,
        if (sourceBrowse) ChimahonAnimeBadgeUiModel("Sources", ChimahonAnimeBadgeKind.Source) else null,
        if (extensionManagement) ChimahonAnimeBadgeUiModel("Extensions", ChimahonAnimeBadgeKind.Source) else null,
        if (history) ChimahonAnimeBadgeUiModel("History", ChimahonAnimeBadgeKind.Tracking) else null,
        if (player) ChimahonAnimeBadgeUiModel("Player", ChimahonAnimeBadgeKind.Downloaded) else null,
        if (downloads) ChimahonAnimeBadgeUiModel("Downloads", ChimahonAnimeBadgeKind.Downloaded) else null,
        if (localAnime) ChimahonAnimeBadgeUiModel("Local", ChimahonAnimeBadgeKind.Local) else null,
        if (tracking) ChimahonAnimeBadgeUiModel("Tracking", ChimahonAnimeBadgeKind.Tracking) else null,
    )
}
