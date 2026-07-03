package app.chimahon.shared.anime

sealed interface ChimahonAnimeTrackingAction {
    data class Load(
        val request: ChimahonAnimeTrackingLoadRequest = ChimahonAnimeTrackingLoadRequest(),
    ) : ChimahonAnimeTrackingAction

    data class SearchRemote(
        val request: ChimahonAnimeTrackingRemoteSearchRequest,
    ) : ChimahonAnimeTrackingAction

    data class LinkRemote(
        val request: ChimahonAnimeTrackingLinkRequest,
    ) : ChimahonAnimeTrackingAction

    data class RemoveTracking(
        val key: ChimahonAnimeTrackingEntryKey,
    ) : ChimahonAnimeTrackingAction

    data class UpdateStatus(
        val key: ChimahonAnimeTrackingEntryKey,
        val status: ChimahonAnimeTrackStatus,
        val changedAtMillis: Long = 0L,
    ) : ChimahonAnimeTrackingAction

    data class UpdateProgress(
        val key: ChimahonAnimeTrackingEntryKey,
        val progress: ChimahonAnimeTrackingProgress,
        val changedAtMillis: Long = progress.updatedAtMillis,
    ) : ChimahonAnimeTrackingAction

    data class UpdateScore(
        val key: ChimahonAnimeTrackingEntryKey,
        val score: ChimahonAnimeTrackingScore,
        val changedAtMillis: Long = score.updatedAtMillis,
    ) : ChimahonAnimeTrackingAction

    data class SetPrivateTracking(
        val key: ChimahonAnimeTrackingEntryKey,
        val privateTracking: Boolean,
        val changedAtMillis: Long = 0L,
    ) : ChimahonAnimeTrackingAction

    data class SyncEpisodes(
        val request: ChimahonAnimeTrackingEpisodeSyncRequest,
    ) : ChimahonAnimeTrackingAction

    data class ResolveConflict(
        val request: ChimahonAnimeTrackingConflictResolutionRequest,
    ) : ChimahonAnimeTrackingAction

    data class RecordPlayerProgress(
        val event: ChimahonAnimeTrackingPlayerProgressEvent,
        val hook: ChimahonAnimeTrackingPlayerProgressHook = ChimahonAnimeTrackingPlayerProgressHook(),
    ) : ChimahonAnimeTrackingAction
}

data class ChimahonAnimeTrackingActionResult(
    val action: ChimahonAnimeTrackingAction,
    val loadResult: ChimahonAnimeTrackingLoadResult? = null,
    val searchResult: ChimahonAnimeTrackingRemoteSearchResult? = null,
    val mutationResult: ChimahonAnimeTrackingMutationResult? = null,
    val playerProgressResult: ChimahonAnimeTrackingPlayerProgressResult? = null,
) {
    val successful: Boolean
        get() = loadResult?.successful != false &&
            searchResult?.successful != false &&
            mutationResult?.successful != false &&
            playerProgressResult?.successful != false

    val errors: List<ChimahonAnimeTrackingError>
        get() = loadResult?.errors.orEmpty() +
            searchResult?.errors.orEmpty() +
            mutationResult?.errors.orEmpty() +
            playerProgressResult?.errors.orEmpty()
}

suspend fun ChimahonAnimeTrackingService.performAction(
    action: ChimahonAnimeTrackingAction,
): ChimahonAnimeTrackingActionResult {
    return when (action) {
        is ChimahonAnimeTrackingAction.Load -> ChimahonAnimeTrackingActionResult(
            action = action,
            loadResult = loadTracking(action.request),
        )
        is ChimahonAnimeTrackingAction.SearchRemote -> ChimahonAnimeTrackingActionResult(
            action = action,
            searchResult = searchRemote(action.request),
        )
        is ChimahonAnimeTrackingAction.LinkRemote -> ChimahonAnimeTrackingActionResult(
            action = action,
            mutationResult = linkRemote(action.request),
        )
        is ChimahonAnimeTrackingAction.RemoveTracking -> ChimahonAnimeTrackingActionResult(
            action = action,
            mutationResult = removeEntry(action.key),
        )
        is ChimahonAnimeTrackingAction.UpdateStatus -> ChimahonAnimeTrackingActionResult(
            action = action,
            mutationResult = updateEntry(
                ChimahonAnimeTrackingUpdateRequest(
                    key = action.key,
                    status = action.status,
                    changedAtMillis = action.changedAtMillis,
                    reason = ChimahonAnimeTrackingUpdateReason.Manual,
                ),
            ),
        )
        is ChimahonAnimeTrackingAction.UpdateProgress -> ChimahonAnimeTrackingActionResult(
            action = action,
            mutationResult = updateEntry(
                ChimahonAnimeTrackingUpdateRequest(
                    key = action.key,
                    progress = action.progress,
                    changedAtMillis = action.changedAtMillis,
                    reason = ChimahonAnimeTrackingUpdateReason.Manual,
                ),
            ),
        )
        is ChimahonAnimeTrackingAction.UpdateScore -> ChimahonAnimeTrackingActionResult(
            action = action,
            mutationResult = updateEntry(
                ChimahonAnimeTrackingUpdateRequest(
                    key = action.key,
                    score = action.score,
                    changedAtMillis = action.changedAtMillis,
                    reason = ChimahonAnimeTrackingUpdateReason.Manual,
                ),
            ),
        )
        is ChimahonAnimeTrackingAction.SetPrivateTracking -> ChimahonAnimeTrackingActionResult(
            action = action,
            mutationResult = updateEntry(
                ChimahonAnimeTrackingUpdateRequest(
                    key = action.key,
                    privateTracking = action.privateTracking,
                    changedAtMillis = action.changedAtMillis,
                    reason = ChimahonAnimeTrackingUpdateReason.Manual,
                ),
            ),
        )
        is ChimahonAnimeTrackingAction.SyncEpisodes -> ChimahonAnimeTrackingActionResult(
            action = action,
            mutationResult = syncEpisodes(action.request),
        )
        is ChimahonAnimeTrackingAction.ResolveConflict -> ChimahonAnimeTrackingActionResult(
            action = action,
            mutationResult = resolveConflict(action.request),
        )
        is ChimahonAnimeTrackingAction.RecordPlayerProgress -> ChimahonAnimeTrackingActionResult(
            action = action,
            playerProgressResult = recordPlayerProgress(action.event, action.hook),
        )
    }
}

suspend fun ChimahonAnimeTrackingService.performActions(
    actions: List<ChimahonAnimeTrackingAction>,
): List<ChimahonAnimeTrackingActionResult> {
    return actions.map { action -> performAction(action) }
}

fun ChimahonAnimeTrackingEntry.updateStatusAction(
    status: ChimahonAnimeTrackStatus,
    changedAtMillis: Long = 0L,
): ChimahonAnimeTrackingAction.UpdateStatus {
    return ChimahonAnimeTrackingAction.UpdateStatus(
        key = key,
        status = status,
        changedAtMillis = changedAtMillis,
    )
}

fun ChimahonAnimeTrackingEntry.updateProgressAction(
    progress: ChimahonAnimeTrackingProgress,
    changedAtMillis: Long = progress.updatedAtMillis,
): ChimahonAnimeTrackingAction.UpdateProgress {
    return ChimahonAnimeTrackingAction.UpdateProgress(
        key = key,
        progress = progress,
        changedAtMillis = changedAtMillis,
    )
}

fun ChimahonAnimeTrackingEntry.updateScoreAction(
    score: ChimahonAnimeTrackingScore,
    changedAtMillis: Long = score.updatedAtMillis,
): ChimahonAnimeTrackingAction.UpdateScore {
    return ChimahonAnimeTrackingAction.UpdateScore(
        key = key,
        score = score,
        changedAtMillis = changedAtMillis,
    )
}
