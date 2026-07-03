package app.chimahon.shared.anime

sealed interface ChimahonAnimeBackendAction {
    data class SetCategories(
        val animeIds: List<Long>,
        val categoryIds: List<Long>,
    ) : ChimahonAnimeBackendAction

    data class RemoveFromLibrary(
        val animeIds: List<Long>,
    ) : ChimahonAnimeBackendAction

    data class MarkSeen(
        val animeIds: List<Long> = emptyList(),
        val episodeIds: List<Long> = emptyList(),
        val seen: Boolean,
    ) : ChimahonAnimeBackendAction

    data class UpdateEpisodeFlags(
        val episodeIds: List<Long>,
        val seen: Boolean? = null,
        val bookmarked: Boolean? = null,
        val fillermarked: Boolean? = null,
    ) : ChimahonAnimeBackendAction

    data class RecordProgress(
        val update: ChimahonAnimeEpisodeProgressUpdate,
    ) : ChimahonAnimeBackendAction
}

data class ChimahonAnimeBackendActionResult(
    val action: ChimahonAnimeBackendAction,
    val mutation: ChimahonAnimeMutationResult? = null,
    val historyMutation: ChimahonAnimeHistoryMutationResult? = null,
) {
    val successful: Boolean
        get() = mutation?.successful != false && historyMutation?.successful != false

    val errors: List<String>
        get() = mutation?.errors.orEmpty() + historyMutation?.errors.orEmpty().map { it.message ?: it.code.name }
}

suspend fun ChimahonAnimeBackendCoordinator.performAction(
    action: ChimahonAnimeBackendAction,
): ChimahonAnimeBackendActionResult {
    return when (action) {
        is ChimahonAnimeBackendAction.SetCategories -> {
            ChimahonAnimeBackendActionResult(
                action = action,
                mutation = setAnimeCategories(action.animeIds, action.categoryIds),
            )
        }
        is ChimahonAnimeBackendAction.RemoveFromLibrary -> {
            ChimahonAnimeBackendActionResult(
                action = action,
                mutation = removeAnimeFromLibrary(action.animeIds),
            )
        }
        is ChimahonAnimeBackendAction.MarkSeen -> {
            ChimahonAnimeBackendActionResult(
                action = action,
                mutation = markEpisodesSeen(
                    ChimahonAnimeSeenMutationRequest(
                        animeIds = action.animeIds,
                        episodeIds = action.episodeIds,
                        seen = action.seen,
                    ),
                ),
            )
        }
        is ChimahonAnimeBackendAction.UpdateEpisodeFlags -> {
            ChimahonAnimeBackendActionResult(
                action = action,
                mutation = updateEpisodeFlags(
                    ChimahonAnimeEpisodeFlagMutationRequest(
                        episodeIds = action.episodeIds,
                        seen = action.seen,
                        bookmark = action.bookmarked,
                        fillermark = action.fillermarked,
                    ),
                ),
            )
        }
        is ChimahonAnimeBackendAction.RecordProgress -> {
            ChimahonAnimeBackendActionResult(
                action = action,
                historyMutation = recordPlayerProgress(action.update),
            )
        }
    }
}

suspend fun ChimahonAnimeBackendCoordinator.performActions(
    actions: List<ChimahonAnimeBackendAction>,
): List<ChimahonAnimeBackendActionResult> {
    return actions.map { action -> performAction(action) }
}

private suspend fun ChimahonAnimeBackendCoordinator.setAnimeCategories(
    animeIds: List<Long>,
    categoryIds: List<Long>,
): ChimahonAnimeMutationResult {
    return servicesForActions.library.setAnimeCategories(
        animeIds = animeIds,
        categoryIds = categoryIds,
    )
}

private suspend fun ChimahonAnimeBackendCoordinator.removeAnimeFromLibrary(
    animeIds: List<Long>,
): ChimahonAnimeMutationResult {
    return servicesForActions.library.removeAnimeFromLibrary(animeIds)
}

private suspend fun ChimahonAnimeBackendCoordinator.updateEpisodeFlags(
    request: ChimahonAnimeEpisodeFlagMutationRequest,
): ChimahonAnimeMutationResult {
    return servicesForActions.detail.updateEpisodeFlags(request)
}

private val ChimahonAnimeBackendCoordinator.servicesForActions: ChimahonAnimeBackendServices
    get() = services
