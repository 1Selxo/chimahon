package app.chimahon.shared.anime

interface ChimahonAnimeTrackingService {
    suspend fun loadTracking(request: ChimahonAnimeTrackingLoadRequest = ChimahonAnimeTrackingLoadRequest()): ChimahonAnimeTrackingLoadResult

    suspend fun searchRemote(request: ChimahonAnimeTrackingRemoteSearchRequest): ChimahonAnimeTrackingRemoteSearchResult

    suspend fun linkRemote(request: ChimahonAnimeTrackingLinkRequest): ChimahonAnimeTrackingMutationResult

    suspend fun updateEntry(request: ChimahonAnimeTrackingUpdateRequest): ChimahonAnimeTrackingMutationResult

    suspend fun removeEntry(key: ChimahonAnimeTrackingEntryKey): ChimahonAnimeTrackingMutationResult

    suspend fun syncEpisodes(request: ChimahonAnimeTrackingEpisodeSyncRequest): ChimahonAnimeTrackingMutationResult

    suspend fun resolveConflict(request: ChimahonAnimeTrackingConflictResolutionRequest): ChimahonAnimeTrackingMutationResult

    suspend fun recordPlayerProgress(
        event: ChimahonAnimeTrackingPlayerProgressEvent,
        hook: ChimahonAnimeTrackingPlayerProgressHook = ChimahonAnimeTrackingPlayerProgressHook(),
    ): ChimahonAnimeTrackingPlayerProgressResult
}

data class ChimahonAnimeTrackingServiceCallbacks(
    val loadTracking: suspend (ChimahonAnimeTrackingLoadRequest) -> ChimahonAnimeTrackingLoadResult = {
        ChimahonAnimeTrackingLoadResult(errors = listOf(chimahonAnimeTrackingUnsupported("load tracking")))
    },
    val searchRemote: suspend (ChimahonAnimeTrackingRemoteSearchRequest) -> ChimahonAnimeTrackingRemoteSearchResult = { request ->
        ChimahonAnimeTrackingRemoteSearchResult(
            trackerId = request.trackerId,
            errors = listOf(chimahonAnimeTrackingUnsupported("search anime trackers")),
        )
    },
    val linkRemote: suspend (ChimahonAnimeTrackingLinkRequest) -> ChimahonAnimeTrackingMutationResult = {
        ChimahonAnimeTrackingMutationResult(errors = listOf(chimahonAnimeTrackingUnsupported("link anime tracking")))
    },
    val updateEntry: suspend (ChimahonAnimeTrackingUpdateRequest) -> ChimahonAnimeTrackingMutationResult = {
        ChimahonAnimeTrackingMutationResult(errors = listOf(chimahonAnimeTrackingUnsupported("update anime tracking")))
    },
    val removeEntry: suspend (ChimahonAnimeTrackingEntryKey) -> ChimahonAnimeTrackingMutationResult = {
        ChimahonAnimeTrackingMutationResult(errors = listOf(chimahonAnimeTrackingUnsupported("remove anime tracking")))
    },
    val syncEpisodes: suspend (ChimahonAnimeTrackingEpisodeSyncRequest) -> ChimahonAnimeTrackingMutationResult = {
        ChimahonAnimeTrackingMutationResult(errors = listOf(chimahonAnimeTrackingUnsupported("sync anime episodes")))
    },
    val resolveConflict: suspend (ChimahonAnimeTrackingConflictResolutionRequest) -> ChimahonAnimeTrackingMutationResult = {
        ChimahonAnimeTrackingMutationResult(errors = listOf(chimahonAnimeTrackingUnsupported("resolve anime tracking conflicts")))
    },
    val recordPlayerProgress: suspend (
        ChimahonAnimeTrackingPlayerProgressEvent,
        ChimahonAnimeTrackingPlayerProgressHook,
    ) -> ChimahonAnimeTrackingPlayerProgressResult = { _, _ ->
        ChimahonAnimeTrackingPlayerProgressResult(
            errors = listOf(chimahonAnimeTrackingUnsupported("record anime player progress")),
        )
    },
)

class ChimahonAnimeTrackingCallbackService(
    private val callbacks: ChimahonAnimeTrackingServiceCallbacks = ChimahonAnimeTrackingServiceCallbacks(),
) : ChimahonAnimeTrackingService {
    override suspend fun loadTracking(
        request: ChimahonAnimeTrackingLoadRequest,
    ): ChimahonAnimeTrackingLoadResult {
        return callbacks.loadTracking(request)
    }

    override suspend fun searchRemote(
        request: ChimahonAnimeTrackingRemoteSearchRequest,
    ): ChimahonAnimeTrackingRemoteSearchResult {
        return callbacks.searchRemote(request)
    }

    override suspend fun linkRemote(
        request: ChimahonAnimeTrackingLinkRequest,
    ): ChimahonAnimeTrackingMutationResult {
        return callbacks.linkRemote(request)
    }

    override suspend fun updateEntry(
        request: ChimahonAnimeTrackingUpdateRequest,
    ): ChimahonAnimeTrackingMutationResult {
        return callbacks.updateEntry(request)
    }

    override suspend fun removeEntry(
        key: ChimahonAnimeTrackingEntryKey,
    ): ChimahonAnimeTrackingMutationResult {
        return callbacks.removeEntry(key)
    }

    override suspend fun syncEpisodes(
        request: ChimahonAnimeTrackingEpisodeSyncRequest,
    ): ChimahonAnimeTrackingMutationResult {
        return callbacks.syncEpisodes(request)
    }

    override suspend fun resolveConflict(
        request: ChimahonAnimeTrackingConflictResolutionRequest,
    ): ChimahonAnimeTrackingMutationResult {
        return callbacks.resolveConflict(request)
    }

    override suspend fun recordPlayerProgress(
        event: ChimahonAnimeTrackingPlayerProgressEvent,
        hook: ChimahonAnimeTrackingPlayerProgressHook,
    ): ChimahonAnimeTrackingPlayerProgressResult {
        return callbacks.recordPlayerProgress(event, hook)
    }
}

object ChimahonAnimeTrackingUnavailableService : ChimahonAnimeTrackingService {
    override suspend fun loadTracking(
        request: ChimahonAnimeTrackingLoadRequest,
    ): ChimahonAnimeTrackingLoadResult {
        return ChimahonAnimeTrackingLoadResult(errors = listOf(chimahonAnimeTrackingUnsupported("load tracking")))
    }

    override suspend fun searchRemote(
        request: ChimahonAnimeTrackingRemoteSearchRequest,
    ): ChimahonAnimeTrackingRemoteSearchResult {
        return ChimahonAnimeTrackingRemoteSearchResult(
            trackerId = request.trackerId,
            errors = listOf(chimahonAnimeTrackingUnsupported("search anime trackers")),
        )
    }

    override suspend fun linkRemote(
        request: ChimahonAnimeTrackingLinkRequest,
    ): ChimahonAnimeTrackingMutationResult {
        return ChimahonAnimeTrackingMutationResult(errors = listOf(chimahonAnimeTrackingUnsupported("link anime tracking")))
    }

    override suspend fun updateEntry(
        request: ChimahonAnimeTrackingUpdateRequest,
    ): ChimahonAnimeTrackingMutationResult {
        return ChimahonAnimeTrackingMutationResult(errors = listOf(chimahonAnimeTrackingUnsupported("update anime tracking")))
    }

    override suspend fun removeEntry(
        key: ChimahonAnimeTrackingEntryKey,
    ): ChimahonAnimeTrackingMutationResult {
        return ChimahonAnimeTrackingMutationResult(errors = listOf(chimahonAnimeTrackingUnsupported("remove anime tracking")))
    }

    override suspend fun syncEpisodes(
        request: ChimahonAnimeTrackingEpisodeSyncRequest,
    ): ChimahonAnimeTrackingMutationResult {
        return ChimahonAnimeTrackingMutationResult(errors = listOf(chimahonAnimeTrackingUnsupported("sync anime episodes")))
    }

    override suspend fun resolveConflict(
        request: ChimahonAnimeTrackingConflictResolutionRequest,
    ): ChimahonAnimeTrackingMutationResult {
        return ChimahonAnimeTrackingMutationResult(
            errors = listOf(chimahonAnimeTrackingUnsupported("resolve anime tracking conflicts")),
        )
    }

    override suspend fun recordPlayerProgress(
        event: ChimahonAnimeTrackingPlayerProgressEvent,
        hook: ChimahonAnimeTrackingPlayerProgressHook,
    ): ChimahonAnimeTrackingPlayerProgressResult {
        return ChimahonAnimeTrackingPlayerProgressResult(
            errors = listOf(chimahonAnimeTrackingUnsupported("record anime player progress")),
        )
    }
}

fun chimahonAnimeTrackingUnsupported(action: String): ChimahonAnimeTrackingError {
    return ChimahonAnimeTrackingError(
        code = ChimahonAnimeTrackingErrorCode.Unsupported,
        message = "Anime tracking cannot $action because no tracker service is wired.",
    )
}
