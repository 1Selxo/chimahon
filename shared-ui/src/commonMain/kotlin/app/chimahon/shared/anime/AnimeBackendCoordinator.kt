package app.chimahon.shared.anime

data class ChimahonAnimeHomeLoadRequest(
    val libraryRequest: ChimahonAnimeLibraryLoadRequest = ChimahonAnimeLibraryLoadRequest(),
    val historyRequest: ChimahonAnimeHistoryLoadRequest = ChimahonAnimeHistoryLoadRequest(limit = 50),
    val includeLibrary: Boolean = true,
    val includeHistory: Boolean = true,
)

data class ChimahonAnimeHomeData(
    val library: ChimahonAnimeLibraryLoadResult = ChimahonAnimeLibraryLoadResult(),
    val history: ChimahonAnimeHistoryLoadResult = ChimahonAnimeHistoryLoadResult(),
    val capabilities: ChimahonAnimeBackendCapabilities = ChimahonAnimeBackendCapabilities(),
) {
    val errors: List<ChimahonAnimeBackendError>
        get() = library.errors + history.errors

    val hasContent: Boolean
        get() = library.data.entries.isNotEmpty() || history.data.entries.isNotEmpty()

    val libraryEntryCount: Int
        get() = library.data.entries.size

    val historyEntryCount: Int
        get() = history.data.entries.size
}

data class ChimahonAnimeDetailSessionRequest(
    val detailRequest: ChimahonAnimeDetailLoadRequest,
    val playerRequest: ChimahonAnimePlayerLoadRequest? = null,
)

data class ChimahonAnimeDetailSessionData(
    val detail: ChimahonAnimeDetailLoadResult = ChimahonAnimeDetailLoadResult(),
    val player: ChimahonAnimePlayerLoadResult? = null,
    val capabilities: ChimahonAnimeBackendCapabilities = ChimahonAnimeBackendCapabilities(),
) {
    val errors: List<ChimahonAnimeBackendError>
        get() = detail.errors + player?.errors.orEmpty()

    val found: Boolean
        get() = detail.found
}

class ChimahonAnimeBackendCoordinator(
    val services: ChimahonAnimeBackendServices,
) {
    suspend fun loadHome(
        request: ChimahonAnimeHomeLoadRequest = ChimahonAnimeHomeLoadRequest(),
    ): ChimahonAnimeHomeData {
        val libraryResult = if (request.includeLibrary) {
            ChimahonAnimeLibraryLoadResult(
                data = services.library.getLibrary(),
            )
        } else {
            ChimahonAnimeLibraryLoadResult()
        }
        val historyResult = if (request.includeHistory) {
            services.history.loadHistory(request.historyRequest)
        } else {
            ChimahonAnimeHistoryLoadResult()
        }
        return ChimahonAnimeHomeData(
            library = libraryResult,
            history = historyResult,
            capabilities = services.capabilities,
        )
    }

    suspend fun loadDetailSession(
        request: ChimahonAnimeDetailSessionRequest,
    ): ChimahonAnimeDetailSessionData {
        val detailResult = services.detail.loadDetail(request.detailRequest)
        val playerResult = request.playerRequest?.let { services.player.loadSession(it) }
        return ChimahonAnimeDetailSessionData(
            detail = detailResult,
            player = playerResult,
            capabilities = services.capabilities,
        )
    }

    suspend fun markEpisodesSeen(
        request: ChimahonAnimeSeenMutationRequest,
    ): ChimahonAnimeMutationResult {
        return if (request.episodeIds.isNotEmpty()) {
            services.detail.updateEpisodeFlags(
                ChimahonAnimeEpisodeFlagMutationRequest(
                    episodeIds = request.episodeIds,
                    seen = request.seen,
                ),
            )
        } else {
            services.library.markEpisodesSeen(
                animeIds = request.animeIds,
                seen = request.seen,
            )
        }
    }

    suspend fun recordPlayerProgress(
        update: ChimahonAnimeEpisodeProgressUpdate,
    ): ChimahonAnimeHistoryMutationResult {
        return services.player.recordProgress(update)
    }
}

fun ChimahonAnimeBackendServices.coordinator(): ChimahonAnimeBackendCoordinator {
    return ChimahonAnimeBackendCoordinator(this)
}
