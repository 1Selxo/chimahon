package app.chimahon.shared.anime

interface ChimahonAnimeLibraryRepositoryBridge {
    suspend fun loadLibrary(
        request: ChimahonAnimeLibraryLoadRequest = ChimahonAnimeLibraryLoadRequest(),
    ): ChimahonAnimeLibraryLoadResult

    suspend fun loadAnime(animeId: Long): ChimahonAnimeEntry?

    suspend fun loadEpisodes(request: ChimahonAnimeEpisodesLoadRequest): List<ChimahonAnimeEpisodeEntry>

    suspend fun loadCategories(): List<ChimahonAnimeCategory>

    suspend fun setAnimeCategories(request: ChimahonAnimeCategoryMutationRequest): ChimahonAnimeMutationResult

    suspend fun removeAnimeFromLibrary(request: ChimahonAnimeLibraryRemovalRequest): ChimahonAnimeMutationResult

    suspend fun markEpisodesSeen(request: ChimahonAnimeSeenMutationRequest): ChimahonAnimeMutationResult
}

data class ChimahonAnimeLibraryRepositoryCallbacks(
    val loadLibrary: suspend (ChimahonAnimeLibraryLoadRequest) -> ChimahonAnimeLibraryLoadResult,
    val loadAnime: suspend (Long) -> ChimahonAnimeEntry?,
    val loadEpisodes: suspend (ChimahonAnimeEpisodesLoadRequest) -> List<ChimahonAnimeEpisodeEntry>,
    val loadCategories: suspend () -> List<ChimahonAnimeCategory>,
    val setAnimeCategories: suspend (ChimahonAnimeCategoryMutationRequest) -> ChimahonAnimeMutationResult,
    val removeAnimeFromLibrary: suspend (ChimahonAnimeLibraryRemovalRequest) -> ChimahonAnimeMutationResult,
    val markEpisodesSeen: suspend (ChimahonAnimeSeenMutationRequest) -> ChimahonAnimeMutationResult,
)

class ChimahonCallbackAnimeLibraryRepositoryBridge(
    private val callbacks: ChimahonAnimeLibraryRepositoryCallbacks,
) : ChimahonAnimeLibraryRepositoryBridge {
    override suspend fun loadLibrary(
        request: ChimahonAnimeLibraryLoadRequest,
    ): ChimahonAnimeLibraryLoadResult {
        return callbacks.loadLibrary(request)
    }

    override suspend fun loadAnime(animeId: Long): ChimahonAnimeEntry? {
        return callbacks.loadAnime(animeId)
    }

    override suspend fun loadEpisodes(
        request: ChimahonAnimeEpisodesLoadRequest,
    ): List<ChimahonAnimeEpisodeEntry> {
        return callbacks.loadEpisodes(request)
    }

    override suspend fun loadCategories(): List<ChimahonAnimeCategory> {
        return callbacks.loadCategories()
    }

    override suspend fun setAnimeCategories(
        request: ChimahonAnimeCategoryMutationRequest,
    ): ChimahonAnimeMutationResult {
        return callbacks.setAnimeCategories(request)
    }

    override suspend fun removeAnimeFromLibrary(
        request: ChimahonAnimeLibraryRemovalRequest,
    ): ChimahonAnimeMutationResult {
        return callbacks.removeAnimeFromLibrary(request)
    }

    override suspend fun markEpisodesSeen(
        request: ChimahonAnimeSeenMutationRequest,
    ): ChimahonAnimeMutationResult {
        return callbacks.markEpisodesSeen(request)
    }
}

class ChimahonRepositoryAnimeLibraryService(
    private val bridge: ChimahonAnimeLibraryRepositoryBridge,
    private val defaultRequest: ChimahonAnimeLibraryLoadRequest = ChimahonAnimeLibraryLoadRequest(),
) : ChimahonAnimeLibraryService {
    override suspend fun getLibrary(): ChimahonAnimeLibraryData {
        return bridge.loadLibrary(defaultRequest).data
    }

    override suspend fun getAnime(animeId: Long): ChimahonAnimeEntry? {
        return bridge.loadAnime(animeId)
    }

    override suspend fun getEpisodes(animeId: Long): List<ChimahonAnimeEpisodeEntry> {
        return bridge.loadEpisodes(ChimahonAnimeEpisodesLoadRequest(animeId = animeId))
    }

    override suspend fun getCategories(): List<ChimahonAnimeCategory> {
        return bridge.loadCategories()
    }

    override suspend fun setAnimeCategories(
        animeIds: List<Long>,
        categoryIds: List<Long>,
    ): ChimahonAnimeMutationResult {
        return bridge.setAnimeCategories(
            ChimahonAnimeCategoryMutationRequest(
                animeIds = animeIds,
                categoryIds = categoryIds,
            ),
        )
    }

    override suspend fun removeAnimeFromLibrary(animeIds: List<Long>): ChimahonAnimeMutationResult {
        return bridge.removeAnimeFromLibrary(ChimahonAnimeLibraryRemovalRequest(animeIds))
    }

    override suspend fun markEpisodesSeen(
        animeIds: List<Long>,
        seen: Boolean,
    ): ChimahonAnimeMutationResult {
        return bridge.markEpisodesSeen(
            ChimahonAnimeSeenMutationRequest(
                animeIds = animeIds,
                seen = seen,
            ),
        )
    }
}

fun ChimahonAnimeLibraryRepositoryBridge.asAnimeLibraryService(
    defaultRequest: ChimahonAnimeLibraryLoadRequest = ChimahonAnimeLibraryLoadRequest(),
): ChimahonAnimeLibraryService {
    return ChimahonRepositoryAnimeLibraryService(
        bridge = this,
        defaultRequest = defaultRequest,
    )
}

interface ChimahonAnimeDetailService {
    suspend fun loadDetail(request: ChimahonAnimeDetailLoadRequest): ChimahonAnimeDetailLoadResult

    suspend fun refreshEpisodes(request: ChimahonAnimeEpisodesLoadRequest): ChimahonAnimeDetailLoadResult

    suspend fun updateEpisodeFlags(request: ChimahonAnimeEpisodeFlagMutationRequest): ChimahonAnimeMutationResult
}

interface ChimahonAnimeDetailRepositoryBridge {
    suspend fun loadDetail(request: ChimahonAnimeDetailLoadRequest): ChimahonAnimeDetailLoadResult

    suspend fun refreshEpisodes(request: ChimahonAnimeEpisodesLoadRequest): ChimahonAnimeDetailLoadResult

    suspend fun updateEpisodeFlags(request: ChimahonAnimeEpisodeFlagMutationRequest): ChimahonAnimeMutationResult
}

data class ChimahonAnimeDetailRepositoryCallbacks(
    val loadDetail: suspend (ChimahonAnimeDetailLoadRequest) -> ChimahonAnimeDetailLoadResult,
    val refreshEpisodes: suspend (ChimahonAnimeEpisodesLoadRequest) -> ChimahonAnimeDetailLoadResult,
    val updateEpisodeFlags: suspend (ChimahonAnimeEpisodeFlagMutationRequest) -> ChimahonAnimeMutationResult,
)

class ChimahonCallbackAnimeDetailRepositoryBridge(
    private val callbacks: ChimahonAnimeDetailRepositoryCallbacks,
) : ChimahonAnimeDetailRepositoryBridge {
    override suspend fun loadDetail(request: ChimahonAnimeDetailLoadRequest): ChimahonAnimeDetailLoadResult {
        return callbacks.loadDetail(request)
    }

    override suspend fun refreshEpisodes(request: ChimahonAnimeEpisodesLoadRequest): ChimahonAnimeDetailLoadResult {
        return callbacks.refreshEpisodes(request)
    }

    override suspend fun updateEpisodeFlags(
        request: ChimahonAnimeEpisodeFlagMutationRequest,
    ): ChimahonAnimeMutationResult {
        return callbacks.updateEpisodeFlags(request)
    }
}

class ChimahonRepositoryAnimeDetailService(
    private val bridge: ChimahonAnimeDetailRepositoryBridge,
) : ChimahonAnimeDetailService {
    override suspend fun loadDetail(request: ChimahonAnimeDetailLoadRequest): ChimahonAnimeDetailLoadResult {
        return bridge.loadDetail(request)
    }

    override suspend fun refreshEpisodes(request: ChimahonAnimeEpisodesLoadRequest): ChimahonAnimeDetailLoadResult {
        return bridge.refreshEpisodes(request)
    }

    override suspend fun updateEpisodeFlags(
        request: ChimahonAnimeEpisodeFlagMutationRequest,
    ): ChimahonAnimeMutationResult {
        return bridge.updateEpisodeFlags(request)
    }
}

fun ChimahonAnimeDetailRepositoryBridge.asAnimeDetailService(): ChimahonAnimeDetailService {
    return ChimahonRepositoryAnimeDetailService(this)
}

interface ChimahonAnimeHistoryService {
    suspend fun loadHistory(request: ChimahonAnimeHistoryLoadRequest = ChimahonAnimeHistoryLoadRequest()): ChimahonAnimeHistoryLoadResult

    suspend fun getLastHistory(): ChimahonAnimeHistoryEntry?

    suspend fun recordProgress(update: ChimahonAnimeEpisodeProgressUpdate): ChimahonAnimeHistoryMutationResult

    suspend fun resetHistoryForAnime(animeIds: List<Long>): ChimahonAnimeHistoryMutationResult

    suspend fun clearHistory(): ChimahonAnimeHistoryMutationResult
}

interface ChimahonAnimeHistoryRepositoryBridge {
    suspend fun loadHistory(request: ChimahonAnimeHistoryLoadRequest = ChimahonAnimeHistoryLoadRequest()): ChimahonAnimeHistoryLoadResult

    suspend fun getLastHistory(): ChimahonAnimeHistoryEntry?

    suspend fun recordProgress(update: ChimahonAnimeEpisodeProgressUpdate): ChimahonAnimeHistoryMutationResult

    suspend fun resetHistoryForAnime(animeIds: List<Long>): ChimahonAnimeHistoryMutationResult

    suspend fun clearHistory(): ChimahonAnimeHistoryMutationResult
}

data class ChimahonAnimeHistoryRepositoryCallbacks(
    val loadHistory: suspend (ChimahonAnimeHistoryLoadRequest) -> ChimahonAnimeHistoryLoadResult,
    val getLastHistory: suspend () -> ChimahonAnimeHistoryEntry?,
    val recordProgress: suspend (ChimahonAnimeEpisodeProgressUpdate) -> ChimahonAnimeHistoryMutationResult,
    val resetHistoryForAnime: suspend (List<Long>) -> ChimahonAnimeHistoryMutationResult,
    val clearHistory: suspend () -> ChimahonAnimeHistoryMutationResult,
)

class ChimahonCallbackAnimeHistoryRepositoryBridge(
    private val callbacks: ChimahonAnimeHistoryRepositoryCallbacks,
) : ChimahonAnimeHistoryRepositoryBridge {
    override suspend fun loadHistory(
        request: ChimahonAnimeHistoryLoadRequest,
    ): ChimahonAnimeHistoryLoadResult {
        return callbacks.loadHistory(request)
    }

    override suspend fun getLastHistory(): ChimahonAnimeHistoryEntry? {
        return callbacks.getLastHistory()
    }

    override suspend fun recordProgress(
        update: ChimahonAnimeEpisodeProgressUpdate,
    ): ChimahonAnimeHistoryMutationResult {
        return callbacks.recordProgress(update)
    }

    override suspend fun resetHistoryForAnime(animeIds: List<Long>): ChimahonAnimeHistoryMutationResult {
        return callbacks.resetHistoryForAnime(animeIds)
    }

    override suspend fun clearHistory(): ChimahonAnimeHistoryMutationResult {
        return callbacks.clearHistory()
    }
}

class ChimahonRepositoryAnimeHistoryService(
    private val bridge: ChimahonAnimeHistoryRepositoryBridge,
) : ChimahonAnimeHistoryService {
    override suspend fun loadHistory(
        request: ChimahonAnimeHistoryLoadRequest,
    ): ChimahonAnimeHistoryLoadResult {
        return bridge.loadHistory(request)
    }

    override suspend fun getLastHistory(): ChimahonAnimeHistoryEntry? {
        return bridge.getLastHistory()
    }

    override suspend fun recordProgress(
        update: ChimahonAnimeEpisodeProgressUpdate,
    ): ChimahonAnimeHistoryMutationResult {
        return bridge.recordProgress(update)
    }

    override suspend fun resetHistoryForAnime(animeIds: List<Long>): ChimahonAnimeHistoryMutationResult {
        return bridge.resetHistoryForAnime(animeIds)
    }

    override suspend fun clearHistory(): ChimahonAnimeHistoryMutationResult {
        return bridge.clearHistory()
    }
}

fun ChimahonAnimeHistoryRepositoryBridge.asAnimeHistoryService(): ChimahonAnimeHistoryService {
    return ChimahonRepositoryAnimeHistoryService(this)
}

interface ChimahonAnimePlayerService {
    suspend fun loadSession(request: ChimahonAnimePlayerLoadRequest): ChimahonAnimePlayerLoadResult

    suspend fun loadHosters(request: ChimahonAnimeHosterLoadRequest): ChimahonAnimeHosterLoadResult

    suspend fun loadVideos(request: ChimahonAnimeVideoLoadRequest): ChimahonAnimeVideoLoadResult

    suspend fun recordProgress(update: ChimahonAnimeEpisodeProgressUpdate): ChimahonAnimeHistoryMutationResult
}

interface ChimahonAnimePlayerRepositoryBridge {
    suspend fun loadSession(request: ChimahonAnimePlayerLoadRequest): ChimahonAnimePlayerLoadResult

    suspend fun loadHosters(request: ChimahonAnimeHosterLoadRequest): ChimahonAnimeHosterLoadResult

    suspend fun loadVideos(request: ChimahonAnimeVideoLoadRequest): ChimahonAnimeVideoLoadResult

    suspend fun recordProgress(update: ChimahonAnimeEpisodeProgressUpdate): ChimahonAnimeHistoryMutationResult
}

data class ChimahonAnimePlayerRepositoryCallbacks(
    val loadSession: suspend (ChimahonAnimePlayerLoadRequest) -> ChimahonAnimePlayerLoadResult,
    val loadHosters: suspend (ChimahonAnimeHosterLoadRequest) -> ChimahonAnimeHosterLoadResult,
    val loadVideos: suspend (ChimahonAnimeVideoLoadRequest) -> ChimahonAnimeVideoLoadResult,
    val recordProgress: suspend (ChimahonAnimeEpisodeProgressUpdate) -> ChimahonAnimeHistoryMutationResult,
)

class ChimahonCallbackAnimePlayerRepositoryBridge(
    private val callbacks: ChimahonAnimePlayerRepositoryCallbacks,
) : ChimahonAnimePlayerRepositoryBridge {
    override suspend fun loadSession(request: ChimahonAnimePlayerLoadRequest): ChimahonAnimePlayerLoadResult {
        return callbacks.loadSession(request)
    }

    override suspend fun loadHosters(request: ChimahonAnimeHosterLoadRequest): ChimahonAnimeHosterLoadResult {
        return callbacks.loadHosters(request)
    }

    override suspend fun loadVideos(request: ChimahonAnimeVideoLoadRequest): ChimahonAnimeVideoLoadResult {
        return callbacks.loadVideos(request)
    }

    override suspend fun recordProgress(
        update: ChimahonAnimeEpisodeProgressUpdate,
    ): ChimahonAnimeHistoryMutationResult {
        return callbacks.recordProgress(update)
    }
}

class ChimahonRepositoryAnimePlayerService(
    private val bridge: ChimahonAnimePlayerRepositoryBridge,
) : ChimahonAnimePlayerService {
    override suspend fun loadSession(request: ChimahonAnimePlayerLoadRequest): ChimahonAnimePlayerLoadResult {
        return bridge.loadSession(request)
    }

    override suspend fun loadHosters(request: ChimahonAnimeHosterLoadRequest): ChimahonAnimeHosterLoadResult {
        return bridge.loadHosters(request)
    }

    override suspend fun loadVideos(request: ChimahonAnimeVideoLoadRequest): ChimahonAnimeVideoLoadResult {
        return bridge.loadVideos(request)
    }

    override suspend fun recordProgress(
        update: ChimahonAnimeEpisodeProgressUpdate,
    ): ChimahonAnimeHistoryMutationResult {
        return bridge.recordProgress(update)
    }
}

fun ChimahonAnimePlayerRepositoryBridge.asAnimePlayerService(): ChimahonAnimePlayerService {
    return ChimahonRepositoryAnimePlayerService(this)
}

data class ChimahonAnimeRepositoryBridges(
    val library: ChimahonAnimeLibraryRepositoryBridge,
    val detail: ChimahonAnimeDetailRepositoryBridge,
    val history: ChimahonAnimeHistoryRepositoryBridge,
    val player: ChimahonAnimePlayerRepositoryBridge,
) {
    fun toServices(
        libraryRequest: ChimahonAnimeLibraryLoadRequest = ChimahonAnimeLibraryLoadRequest(),
    ): ChimahonAnimeBackendServices {
        return ChimahonAnimeBackendServices(
            library = library.asAnimeLibraryService(libraryRequest),
            detail = detail.asAnimeDetailService(),
            history = history.asAnimeHistoryService(),
            player = player.asAnimePlayerService(),
            capabilities = ChimahonAnimeBackendCapabilities(
                library = true,
                detail = true,
                history = true,
                player = true,
            ),
        )
    }
}

data class ChimahonAnimeBackendServices(
    val library: ChimahonAnimeLibraryService,
    val detail: ChimahonAnimeDetailService,
    val history: ChimahonAnimeHistoryService,
    val player: ChimahonAnimePlayerService,
    val capabilities: ChimahonAnimeBackendCapabilities = ChimahonAnimeBackendCapabilities(),
) {
    val enabledFeatureCount: Int
        get() = capabilities.enabledCount

    companion object {
        fun unavailable(platformName: String = "this platform"): ChimahonAnimeBackendServices {
            val errorFactory = ChimahonUnavailableAnimeBackendErrorFactory(platformName)
            return ChimahonAnimeBackendServices(
                library = ChimahonUnavailableAnimeLibraryService(errorFactory),
                detail = ChimahonUnavailableAnimeDetailService(errorFactory),
                history = ChimahonUnavailableAnimeHistoryService(errorFactory),
                player = ChimahonUnavailableAnimePlayerService(errorFactory),
                capabilities = ChimahonAnimeBackendCapabilities(),
            )
        }
    }

}

data class ChimahonAnimeBackendCapabilities(
    val library: Boolean = false,
    val detail: Boolean = false,
    val sourceBrowse: Boolean = false,
    val extensionManagement: Boolean = false,
    val history: Boolean = false,
    val player: Boolean = false,
    val downloads: Boolean = false,
    val localAnime: Boolean = false,
    val tracking: Boolean = false,
) {
    val enabledCount: Int
        get() = listOf(
            library,
            detail,
            sourceBrowse,
            extensionManagement,
            history,
            player,
            downloads,
            localAnime,
            tracking,
        ).count { it }

    val hasAnyBackend: Boolean
        get() = enabledCount > 0
}

private class ChimahonUnavailableAnimeBackendErrorFactory(
    private val platformName: String,
) {
    fun error(feature: String): ChimahonAnimeBackendError {
        return ChimahonAnimeBackendError(
            code = ChimahonAnimeBackendErrorCode.Unsupported,
            message = "$feature is not wired on $platformName yet.",
        )
    }
}

private class ChimahonUnavailableAnimeLibraryService(
    private val errors: ChimahonUnavailableAnimeBackendErrorFactory,
) : ChimahonAnimeLibraryService {
    override suspend fun getLibrary(): ChimahonAnimeLibraryData {
        return ChimahonAnimeLibraryData(entries = emptyList(), categories = emptyList())
    }

    override suspend fun getAnime(animeId: Long): ChimahonAnimeEntry? {
        return null
    }

    override suspend fun getEpisodes(animeId: Long): List<ChimahonAnimeEpisodeEntry> {
        return emptyList()
    }

    override suspend fun getCategories(): List<ChimahonAnimeCategory> {
        return emptyList()
    }

    override suspend fun setAnimeCategories(
        animeIds: List<Long>,
        categoryIds: List<Long>,
    ): ChimahonAnimeMutationResult {
        return ChimahonAnimeMutationResult(errors = listOf(errors.error("Anime categories").message.orEmpty()))
    }

    override suspend fun removeAnimeFromLibrary(animeIds: List<Long>): ChimahonAnimeMutationResult {
        return ChimahonAnimeMutationResult(errors = listOf(errors.error("Anime library removal").message.orEmpty()))
    }

    override suspend fun markEpisodesSeen(
        animeIds: List<Long>,
        seen: Boolean,
    ): ChimahonAnimeMutationResult {
        return ChimahonAnimeMutationResult(errors = listOf(errors.error("Anime episode progress").message.orEmpty()))
    }
}

private class ChimahonUnavailableAnimeDetailService(
    private val errors: ChimahonUnavailableAnimeBackendErrorFactory,
) : ChimahonAnimeDetailService {
    override suspend fun loadDetail(request: ChimahonAnimeDetailLoadRequest): ChimahonAnimeDetailLoadResult {
        return ChimahonAnimeDetailLoadResult(errors = listOf(errors.error("Anime details")))
    }

    override suspend fun refreshEpisodes(request: ChimahonAnimeEpisodesLoadRequest): ChimahonAnimeDetailLoadResult {
        return ChimahonAnimeDetailLoadResult(errors = listOf(errors.error("Anime episode refresh")))
    }

    override suspend fun updateEpisodeFlags(
        request: ChimahonAnimeEpisodeFlagMutationRequest,
    ): ChimahonAnimeMutationResult {
        return ChimahonAnimeMutationResult(errors = listOf(errors.error("Anime episode flags").message.orEmpty()))
    }
}

private class ChimahonUnavailableAnimeHistoryService(
    private val errors: ChimahonUnavailableAnimeBackendErrorFactory,
) : ChimahonAnimeHistoryService {
    override suspend fun loadHistory(request: ChimahonAnimeHistoryLoadRequest): ChimahonAnimeHistoryLoadResult {
        return ChimahonAnimeHistoryLoadResult(errors = listOf(errors.error("Anime watch history")))
    }

    override suspend fun getLastHistory(): ChimahonAnimeHistoryEntry? {
        return null
    }

    override suspend fun recordProgress(update: ChimahonAnimeEpisodeProgressUpdate): ChimahonAnimeHistoryMutationResult {
        return ChimahonAnimeHistoryMutationResult(errors = listOf(errors.error("Anime watch progress")))
    }

    override suspend fun resetHistoryForAnime(animeIds: List<Long>): ChimahonAnimeHistoryMutationResult {
        return ChimahonAnimeHistoryMutationResult(errors = listOf(errors.error("Anime history reset")))
    }

    override suspend fun clearHistory(): ChimahonAnimeHistoryMutationResult {
        return ChimahonAnimeHistoryMutationResult(errors = listOf(errors.error("Anime history clearing")))
    }
}

private class ChimahonUnavailableAnimePlayerService(
    private val errors: ChimahonUnavailableAnimeBackendErrorFactory,
) : ChimahonAnimePlayerService {
    override suspend fun loadSession(request: ChimahonAnimePlayerLoadRequest): ChimahonAnimePlayerLoadResult {
        return ChimahonAnimePlayerLoadResult(errors = listOf(errors.error("Anime player")))
    }

    override suspend fun loadHosters(request: ChimahonAnimeHosterLoadRequest): ChimahonAnimeHosterLoadResult {
        return ChimahonAnimeHosterLoadResult(errors = listOf(errors.error("Anime hosters")))
    }

    override suspend fun loadVideos(request: ChimahonAnimeVideoLoadRequest): ChimahonAnimeVideoLoadResult {
        return ChimahonAnimeVideoLoadResult(
            hosterKey = request.hosterKey,
            errors = listOf(errors.error("Anime video streams")),
        )
    }

    override suspend fun recordProgress(update: ChimahonAnimeEpisodeProgressUpdate): ChimahonAnimeHistoryMutationResult {
        return ChimahonAnimeHistoryMutationResult(errors = listOf(errors.error("Anime player progress")))
    }
}
