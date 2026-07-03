package app.chimahon.shared.anime

interface ChimahonLocalAnimeMediaSource {
    suspend fun reportPlatformCapabilities(): ChimahonLocalAnimeMediaPlatformReport

    suspend fun listRootFolders(): ChimahonLocalAnimeRootFoldersResult

    suspend fun scanLocalAnime(request: ChimahonLocalAnimeScanRequest = ChimahonLocalAnimeScanRequest()): ChimahonLocalAnimeScanResult

    suspend fun loadLocalAnime(animeId: String): ChimahonLocalAnimeMediaEntry?

    suspend fun loadLocalSeason(seasonId: String): ChimahonLocalAnimeSeason?

    suspend fun loadLocalEpisode(episodeId: String): ChimahonLocalAnimeEpisode?

    suspend fun readMediaMetadata(request: ChimahonLocalAnimeMediaMetadataRequest): ChimahonLocalAnimeMediaMetadataResult

    suspend fun resolveThumbnail(request: ChimahonLocalAnimeThumbnailRequest): ChimahonLocalAnimeThumbnailResult
}

data class ChimahonLocalAnimeMediaSourceCallbacks(
    val reportPlatformCapabilities: suspend () -> ChimahonLocalAnimeMediaPlatformReport = {
        ChimahonLocalAnimeMediaPlatformReport()
    },
    val listRootFolders: suspend () -> ChimahonLocalAnimeRootFoldersResult = {
        ChimahonLocalAnimeRootFoldersResult()
    },
    val scanLocalAnime: suspend (ChimahonLocalAnimeScanRequest) -> ChimahonLocalAnimeScanResult = {
        ChimahonLocalAnimeScanResult()
    },
    val loadLocalAnime: suspend (String) -> ChimahonLocalAnimeMediaEntry? = {
        null
    },
    val loadLocalSeason: suspend (String) -> ChimahonLocalAnimeSeason? = {
        null
    },
    val loadLocalEpisode: suspend (String) -> ChimahonLocalAnimeEpisode? = {
        null
    },
    val readMediaMetadata: suspend (ChimahonLocalAnimeMediaMetadataRequest) -> ChimahonLocalAnimeMediaMetadataResult = {
        ChimahonLocalAnimeMediaMetadataResult()
    },
    val resolveThumbnail: suspend (ChimahonLocalAnimeThumbnailRequest) -> ChimahonLocalAnimeThumbnailResult = {
        ChimahonLocalAnimeThumbnailResult()
    },
)

class ChimahonCallbackLocalAnimeMediaSource(
    private val callbacks: ChimahonLocalAnimeMediaSourceCallbacks,
) : ChimahonLocalAnimeMediaSource {
    override suspend fun reportPlatformCapabilities(): ChimahonLocalAnimeMediaPlatformReport {
        return callbacks.reportPlatformCapabilities()
    }

    override suspend fun listRootFolders(): ChimahonLocalAnimeRootFoldersResult {
        return callbacks.listRootFolders()
    }

    override suspend fun scanLocalAnime(request: ChimahonLocalAnimeScanRequest): ChimahonLocalAnimeScanResult {
        return callbacks.scanLocalAnime(request)
    }

    override suspend fun loadLocalAnime(animeId: String): ChimahonLocalAnimeMediaEntry? {
        return callbacks.loadLocalAnime(animeId)
    }

    override suspend fun loadLocalSeason(seasonId: String): ChimahonLocalAnimeSeason? {
        return callbacks.loadLocalSeason(seasonId)
    }

    override suspend fun loadLocalEpisode(episodeId: String): ChimahonLocalAnimeEpisode? {
        return callbacks.loadLocalEpisode(episodeId)
    }

    override suspend fun readMediaMetadata(
        request: ChimahonLocalAnimeMediaMetadataRequest,
    ): ChimahonLocalAnimeMediaMetadataResult {
        return callbacks.readMediaMetadata(request)
    }

    override suspend fun resolveThumbnail(
        request: ChimahonLocalAnimeThumbnailRequest,
    ): ChimahonLocalAnimeThumbnailResult {
        return callbacks.resolveThumbnail(request)
    }
}

fun ChimahonLocalAnimeMediaSourceCallbacks.asLocalAnimeMediaSource(): ChimahonLocalAnimeMediaSource {
    return ChimahonCallbackLocalAnimeMediaSource(this)
}
