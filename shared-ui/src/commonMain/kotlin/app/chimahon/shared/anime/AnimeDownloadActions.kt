package app.chimahon.shared.anime

sealed interface ChimahonAnimeDownloadAction {
    data class Start(
        val request: ChimahonAnimeDownloadStartRequest,
    ) : ChimahonAnimeDownloadAction

    data class Pause(
        val episodeIds: Set<Long>,
    ) : ChimahonAnimeDownloadAction

    data class Resume(
        val episodeIds: Set<Long>,
    ) : ChimahonAnimeDownloadAction

    data class Cancel(
        val episodeIds: Set<Long>,
        val removeFromQueue: Boolean = true,
    ) : ChimahonAnimeDownloadAction

    data class Retry(
        val episodeIds: Set<Long>,
    ) : ChimahonAnimeDownloadAction

    data class Reorder(
        val episodeIds: List<Long>,
    ) : ChimahonAnimeDownloadAction

    data class Move(
        val episodeId: Long,
        val placement: ChimahonAnimeDownloadMovePlacement,
    ) : ChimahonAnimeDownloadAction

    data class Delete(
        val episodeIds: Set<Long>,
        val deleteFiles: Boolean = true,
        val removeFromQueue: Boolean = true,
    ) : ChimahonAnimeDownloadAction

    data object PauseQueue : ChimahonAnimeDownloadAction

    data object ResumeQueue : ChimahonAnimeDownloadAction
}

data class ChimahonAnimeDownloadActionResult(
    val action: ChimahonAnimeDownloadAction,
    val mutation: ChimahonAnimeDownloadMutationResult,
) {
    val successful: Boolean
        get() = mutation.successful

    val queue: ChimahonAnimeDownloadQueueSnapshot?
        get() = mutation.queue

    val affectedEpisodeIds: Set<Long>
        get() = mutation.affectedEpisodeIds

    val errors: List<ChimahonAnimeDownloadError>
        get() = mutation.errors
}

suspend fun ChimahonAnimeDownloadService.performAction(
    action: ChimahonAnimeDownloadAction,
): ChimahonAnimeDownloadActionResult {
    val mutation = when (action) {
        is ChimahonAnimeDownloadAction.Start -> startDownloads(action.request)
        is ChimahonAnimeDownloadAction.Pause -> pauseDownloads(
            ChimahonAnimeDownloadEpisodeMutationRequest(action.episodeIds),
        )
        is ChimahonAnimeDownloadAction.Resume -> resumeDownloads(
            ChimahonAnimeDownloadEpisodeMutationRequest(action.episodeIds),
        )
        is ChimahonAnimeDownloadAction.Cancel -> cancelDownloads(
            ChimahonAnimeDownloadCancelRequest(
                episodeIds = action.episodeIds,
                removeFromQueue = action.removeFromQueue,
            ),
        )
        is ChimahonAnimeDownloadAction.Retry -> retryDownloads(
            ChimahonAnimeDownloadEpisodeMutationRequest(action.episodeIds),
        )
        is ChimahonAnimeDownloadAction.Reorder -> reorderDownloads(
            ChimahonAnimeDownloadReorderRequest(episodeIds = action.episodeIds),
        )
        is ChimahonAnimeDownloadAction.Move -> reorderDownloads(
            ChimahonAnimeDownloadReorderRequest(
                move = ChimahonAnimeDownloadMoveRequest(
                    episodeId = action.episodeId,
                    placement = action.placement,
                ),
            ),
        )
        is ChimahonAnimeDownloadAction.Delete -> deleteDownloads(
            ChimahonAnimeDownloadDeleteRequest(
                episodeIds = action.episodeIds,
                deleteFiles = action.deleteFiles,
                removeFromQueue = action.removeFromQueue,
            ),
        )
        ChimahonAnimeDownloadAction.PauseQueue -> setQueuePaused(true)
        ChimahonAnimeDownloadAction.ResumeQueue -> setQueuePaused(false)
    }
    return ChimahonAnimeDownloadActionResult(
        action = action,
        mutation = mutation,
    )
}

suspend fun ChimahonAnimeDownloadService.performActions(
    actions: List<ChimahonAnimeDownloadAction>,
): List<ChimahonAnimeDownloadActionResult> {
    return actions.map { action -> performAction(action) }
}

fun ChimahonAnimeEpisodeDownloadDecision.toStartDownloadAction(
    anime: ChimahonAnimeEntry,
    sourceInfo: ChimahonAnimeSourceInfo? = null,
    startNow: Boolean = false,
    priority: ChimahonAnimeDownloadPriority = if (startNow) {
        ChimahonAnimeDownloadPriority.Next
    } else {
        ChimahonAnimeDownloadPriority.Normal
    },
    addedAtMillis: Long = 0L,
): ChimahonAnimeDownloadAction.Start {
    return ChimahonAnimeDownloadAction.Start(
        request = toStartDownloadRequest(
            anime = anime,
            sourceInfo = sourceInfo,
            startNow = startNow,
            priority = priority,
            addedAtMillis = addedAtMillis,
        ),
    )
}

fun Set<Long>.toPauseAnimeDownloadsAction(): ChimahonAnimeDownloadAction.Pause {
    return ChimahonAnimeDownloadAction.Pause(this)
}

fun Set<Long>.toResumeAnimeDownloadsAction(): ChimahonAnimeDownloadAction.Resume {
    return ChimahonAnimeDownloadAction.Resume(this)
}

fun Set<Long>.toCancelAnimeDownloadsAction(
    removeFromQueue: Boolean = true,
): ChimahonAnimeDownloadAction.Cancel {
    return ChimahonAnimeDownloadAction.Cancel(
        episodeIds = this,
        removeFromQueue = removeFromQueue,
    )
}

fun Set<Long>.toRetryAnimeDownloadsAction(): ChimahonAnimeDownloadAction.Retry {
    return ChimahonAnimeDownloadAction.Retry(this)
}

fun Set<Long>.toDeleteAnimeDownloadsAction(
    deleteFiles: Boolean = true,
    removeFromQueue: Boolean = true,
): ChimahonAnimeDownloadAction.Delete {
    return ChimahonAnimeDownloadAction.Delete(
        episodeIds = this,
        deleteFiles = deleteFiles,
        removeFromQueue = removeFromQueue,
    )
}
