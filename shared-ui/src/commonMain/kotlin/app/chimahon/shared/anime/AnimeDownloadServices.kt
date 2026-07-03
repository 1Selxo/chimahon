package app.chimahon.shared.anime

interface ChimahonAnimeDownloadService {
    suspend fun loadQueue(
        request: ChimahonAnimeDownloadQueueLoadRequest = ChimahonAnimeDownloadQueueLoadRequest(),
    ): ChimahonAnimeDownloadQueueSnapshot

    suspend fun startDownloads(request: ChimahonAnimeDownloadStartRequest): ChimahonAnimeDownloadMutationResult

    suspend fun pauseDownloads(request: ChimahonAnimeDownloadEpisodeMutationRequest): ChimahonAnimeDownloadMutationResult

    suspend fun resumeDownloads(request: ChimahonAnimeDownloadEpisodeMutationRequest): ChimahonAnimeDownloadMutationResult

    suspend fun cancelDownloads(request: ChimahonAnimeDownloadCancelRequest): ChimahonAnimeDownloadMutationResult

    suspend fun retryDownloads(request: ChimahonAnimeDownloadEpisodeMutationRequest): ChimahonAnimeDownloadMutationResult

    suspend fun reorderDownloads(request: ChimahonAnimeDownloadReorderRequest): ChimahonAnimeDownloadMutationResult

    suspend fun deleteDownloads(request: ChimahonAnimeDownloadDeleteRequest): ChimahonAnimeDownloadMutationResult

    suspend fun setQueuePaused(paused: Boolean): ChimahonAnimeDownloadMutationResult
}

data class ChimahonAnimeDownloadEpisodeMutationRequest(
    val episodeIds: Set<Long>,
)

data class ChimahonAnimeDownloadCancelRequest(
    val episodeIds: Set<Long>,
    val removeFromQueue: Boolean = true,
)

data class ChimahonAnimeDownloadDeleteRequest(
    val episodeIds: Set<Long>,
    val deleteFiles: Boolean = true,
    val removeFromQueue: Boolean = true,
)

data class ChimahonAnimeDownloadReorderRequest(
    val episodeIds: List<Long> = emptyList(),
    val move: ChimahonAnimeDownloadMoveRequest? = null,
)

data class ChimahonAnimeDownloadMoveRequest(
    val episodeId: Long,
    val placement: ChimahonAnimeDownloadMovePlacement,
)

enum class ChimahonAnimeDownloadMovePlacement {
    Top,
    Up,
    Down,
    Bottom,
}

data class ChimahonAnimeDownloadMutationResult(
    val queue: ChimahonAnimeDownloadQueueSnapshot? = null,
    val affectedEpisodeIds: Set<Long> = emptySet(),
    val errors: List<ChimahonAnimeDownloadError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonAnimeDownloadServiceCallbacks(
    val loadQueue: suspend (ChimahonAnimeDownloadQueueLoadRequest) -> ChimahonAnimeDownloadQueueSnapshot = {
        ChimahonAnimeDownloadQueueSnapshot(errors = listOf(unsupportedAnimeDownloadError("load queue")))
    },
    val startDownloads: suspend (ChimahonAnimeDownloadStartRequest) -> ChimahonAnimeDownloadMutationResult = {
        unsupportedAnimeDownloadMutation("start downloads", it.episodeIds.toSet())
    },
    val pauseDownloads: suspend (ChimahonAnimeDownloadEpisodeMutationRequest) -> ChimahonAnimeDownloadMutationResult = {
        unsupportedAnimeDownloadMutation("pause downloads", it.episodeIds)
    },
    val resumeDownloads: suspend (ChimahonAnimeDownloadEpisodeMutationRequest) -> ChimahonAnimeDownloadMutationResult = {
        unsupportedAnimeDownloadMutation("resume downloads", it.episodeIds)
    },
    val cancelDownloads: suspend (ChimahonAnimeDownloadCancelRequest) -> ChimahonAnimeDownloadMutationResult = {
        unsupportedAnimeDownloadMutation("cancel downloads", it.episodeIds)
    },
    val retryDownloads: suspend (ChimahonAnimeDownloadEpisodeMutationRequest) -> ChimahonAnimeDownloadMutationResult = {
        unsupportedAnimeDownloadMutation("retry downloads", it.episodeIds)
    },
    val reorderDownloads: suspend (ChimahonAnimeDownloadReorderRequest) -> ChimahonAnimeDownloadMutationResult = {
        val movedEpisodeId = it.move?.episodeId
        unsupportedAnimeDownloadMutation(
            action = "reorder downloads",
            episodeIds = it.episodeIds.toSet() + listOfNotNull(movedEpisodeId),
        )
    },
    val deleteDownloads: suspend (ChimahonAnimeDownloadDeleteRequest) -> ChimahonAnimeDownloadMutationResult = {
        unsupportedAnimeDownloadMutation("delete downloads", it.episodeIds)
    },
    val setQueuePaused: suspend (Boolean) -> ChimahonAnimeDownloadMutationResult = {
        unsupportedAnimeDownloadMutation(if (it) "pause queue" else "resume queue")
    },
)

class CallbackChimahonAnimeDownloadService(
    private val callbacks: ChimahonAnimeDownloadServiceCallbacks,
) : ChimahonAnimeDownloadService {
    override suspend fun loadQueue(
        request: ChimahonAnimeDownloadQueueLoadRequest,
    ): ChimahonAnimeDownloadQueueSnapshot {
        return callbacks.loadQueue(request)
    }

    override suspend fun startDownloads(
        request: ChimahonAnimeDownloadStartRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return callbacks.startDownloads(request)
    }

    override suspend fun pauseDownloads(
        request: ChimahonAnimeDownloadEpisodeMutationRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return callbacks.pauseDownloads(request)
    }

    override suspend fun resumeDownloads(
        request: ChimahonAnimeDownloadEpisodeMutationRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return callbacks.resumeDownloads(request)
    }

    override suspend fun cancelDownloads(
        request: ChimahonAnimeDownloadCancelRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return callbacks.cancelDownloads(request)
    }

    override suspend fun retryDownloads(
        request: ChimahonAnimeDownloadEpisodeMutationRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return callbacks.retryDownloads(request)
    }

    override suspend fun reorderDownloads(
        request: ChimahonAnimeDownloadReorderRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return callbacks.reorderDownloads(request)
    }

    override suspend fun deleteDownloads(
        request: ChimahonAnimeDownloadDeleteRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return callbacks.deleteDownloads(request)
    }

    override suspend fun setQueuePaused(paused: Boolean): ChimahonAnimeDownloadMutationResult {
        return callbacks.setQueuePaused(paused)
    }
}

object EmptyChimahonAnimeDownloadService : ChimahonAnimeDownloadService {
    override suspend fun loadQueue(
        request: ChimahonAnimeDownloadQueueLoadRequest,
    ): ChimahonAnimeDownloadQueueSnapshot {
        return ChimahonAnimeDownloadQueueSnapshot(errors = listOf(unsupportedAnimeDownloadError("load queue")))
    }

    override suspend fun startDownloads(
        request: ChimahonAnimeDownloadStartRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return unsupportedAnimeDownloadMutation("start downloads", request.episodeIds.toSet())
    }

    override suspend fun pauseDownloads(
        request: ChimahonAnimeDownloadEpisodeMutationRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return unsupportedAnimeDownloadMutation("pause downloads", request.episodeIds)
    }

    override suspend fun resumeDownloads(
        request: ChimahonAnimeDownloadEpisodeMutationRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return unsupportedAnimeDownloadMutation("resume downloads", request.episodeIds)
    }

    override suspend fun cancelDownloads(
        request: ChimahonAnimeDownloadCancelRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return unsupportedAnimeDownloadMutation("cancel downloads", request.episodeIds)
    }

    override suspend fun retryDownloads(
        request: ChimahonAnimeDownloadEpisodeMutationRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return unsupportedAnimeDownloadMutation("retry downloads", request.episodeIds)
    }

    override suspend fun reorderDownloads(
        request: ChimahonAnimeDownloadReorderRequest,
    ): ChimahonAnimeDownloadMutationResult {
        val movedEpisodeId = request.move?.episodeId
        return unsupportedAnimeDownloadMutation(
            action = "reorder downloads",
            episodeIds = request.episodeIds.toSet() + listOfNotNull(movedEpisodeId),
        )
    }

    override suspend fun deleteDownloads(
        request: ChimahonAnimeDownloadDeleteRequest,
    ): ChimahonAnimeDownloadMutationResult {
        return unsupportedAnimeDownloadMutation("delete downloads", request.episodeIds)
    }

    override suspend fun setQueuePaused(paused: Boolean): ChimahonAnimeDownloadMutationResult {
        return unsupportedAnimeDownloadMutation(if (paused) "pause queue" else "resume queue")
    }
}

private fun unsupportedAnimeDownloadMutation(
    action: String,
    episodeIds: Set<Long> = emptySet(),
): ChimahonAnimeDownloadMutationResult {
    return ChimahonAnimeDownloadMutationResult(
        affectedEpisodeIds = episodeIds,
        errors = listOf(unsupportedAnimeDownloadError(action)),
    )
}

private fun unsupportedAnimeDownloadError(action: String): ChimahonAnimeDownloadError {
    return ChimahonAnimeDownloadError(
        code = ChimahonAnimeDownloadErrorCode.Unsupported,
        message = "Anime download service cannot $action.",
        canRetry = false,
    )
}
