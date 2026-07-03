package app.chimahon.shared.anime

interface ChimahonAnimeSourceBrowseService {
    suspend fun loadSources(
        request: ChimahonAnimeSourceListLoadRequest = ChimahonAnimeSourceListLoadRequest(),
    ): ChimahonAnimeSourceListLoadResult

    suspend fun loadFilters(
        request: ChimahonAnimeSourceFiltersLoadRequest,
    ): ChimahonAnimeSourceFiltersLoadResult

    suspend fun browse(
        request: ChimahonAnimeSourceBrowseRequest,
    ): ChimahonAnimeSourceBrowseResult

    suspend fun search(
        request: ChimahonAnimeSourceSearchRequest,
    ): ChimahonAnimeSourceSearchResult

    suspend fun loadDetail(
        request: ChimahonAnimeSourceDetailLoadRequest,
    ): ChimahonAnimeSourceDetailLoadResult

    suspend fun loadPopular(
        sourceId: Long,
        page: Int = 1,
        filters: List<ChimahonAnimeSourceFilterSelection> = emptyList(),
        refresh: Boolean = false,
    ): ChimahonAnimeSourceBrowseResult {
        return browse(
            ChimahonAnimeSourceBrowseRequest(
                sourceId = sourceId,
                mode = ChimahonAnimeSourceBrowseMode.Popular,
                page = page,
                filters = filters,
                refresh = refresh,
            ),
        )
    }

    suspend fun loadLatest(
        sourceId: Long,
        page: Int = 1,
        filters: List<ChimahonAnimeSourceFilterSelection> = emptyList(),
        refresh: Boolean = false,
    ): ChimahonAnimeSourceBrowseResult {
        return browse(
            ChimahonAnimeSourceBrowseRequest(
                sourceId = sourceId,
                mode = ChimahonAnimeSourceBrowseMode.Latest,
                page = page,
                filters = filters,
                refresh = refresh,
            ),
        )
    }

    suspend fun searchSource(
        sourceId: Long,
        query: String,
        page: Int = 1,
        filters: List<ChimahonAnimeSourceFilterSelection> = emptyList(),
        refresh: Boolean = false,
    ): ChimahonAnimeSourceBrowseResult {
        return browse(
            ChimahonAnimeSourceBrowseRequest(
                sourceId = sourceId,
                mode = ChimahonAnimeSourceBrowseMode.Search,
                page = page,
                query = query,
                filters = filters,
                refresh = refresh,
            ),
        )
    }
}

interface ChimahonAnimeSourceBrowseRepositoryBridge {
    suspend fun loadSources(
        request: ChimahonAnimeSourceListLoadRequest = ChimahonAnimeSourceListLoadRequest(),
    ): ChimahonAnimeSourceListLoadResult

    suspend fun loadFilters(
        request: ChimahonAnimeSourceFiltersLoadRequest,
    ): ChimahonAnimeSourceFiltersLoadResult

    suspend fun browse(
        request: ChimahonAnimeSourceBrowseRequest,
    ): ChimahonAnimeSourceBrowseResult

    suspend fun search(
        request: ChimahonAnimeSourceSearchRequest,
    ): ChimahonAnimeSourceSearchResult

    suspend fun loadDetail(
        request: ChimahonAnimeSourceDetailLoadRequest,
    ): ChimahonAnimeSourceDetailLoadResult
}

data class ChimahonAnimeSourceBrowseRepositoryCallbacks(
    val loadSources: suspend (ChimahonAnimeSourceListLoadRequest) -> ChimahonAnimeSourceListLoadResult,
    val loadFilters: suspend (ChimahonAnimeSourceFiltersLoadRequest) -> ChimahonAnimeSourceFiltersLoadResult,
    val browse: suspend (ChimahonAnimeSourceBrowseRequest) -> ChimahonAnimeSourceBrowseResult,
    val search: suspend (ChimahonAnimeSourceSearchRequest) -> ChimahonAnimeSourceSearchResult,
    val loadDetail: suspend (ChimahonAnimeSourceDetailLoadRequest) -> ChimahonAnimeSourceDetailLoadResult,
)

class ChimahonCallbackAnimeSourceBrowseRepositoryBridge(
    private val callbacks: ChimahonAnimeSourceBrowseRepositoryCallbacks,
) : ChimahonAnimeSourceBrowseRepositoryBridge {
    override suspend fun loadSources(
        request: ChimahonAnimeSourceListLoadRequest,
    ): ChimahonAnimeSourceListLoadResult {
        return callbacks.loadSources(request)
    }

    override suspend fun loadFilters(
        request: ChimahonAnimeSourceFiltersLoadRequest,
    ): ChimahonAnimeSourceFiltersLoadResult {
        return callbacks.loadFilters(request)
    }

    override suspend fun browse(
        request: ChimahonAnimeSourceBrowseRequest,
    ): ChimahonAnimeSourceBrowseResult {
        return callbacks.browse(request)
    }

    override suspend fun search(
        request: ChimahonAnimeSourceSearchRequest,
    ): ChimahonAnimeSourceSearchResult {
        return callbacks.search(request)
    }

    override suspend fun loadDetail(
        request: ChimahonAnimeSourceDetailLoadRequest,
    ): ChimahonAnimeSourceDetailLoadResult {
        return callbacks.loadDetail(request)
    }
}

class ChimahonRepositoryAnimeSourceBrowseService(
    private val bridge: ChimahonAnimeSourceBrowseRepositoryBridge,
) : ChimahonAnimeSourceBrowseService {
    override suspend fun loadSources(
        request: ChimahonAnimeSourceListLoadRequest,
    ): ChimahonAnimeSourceListLoadResult {
        return bridge.loadSources(request)
    }

    override suspend fun loadFilters(
        request: ChimahonAnimeSourceFiltersLoadRequest,
    ): ChimahonAnimeSourceFiltersLoadResult {
        return bridge.loadFilters(request)
    }

    override suspend fun browse(
        request: ChimahonAnimeSourceBrowseRequest,
    ): ChimahonAnimeSourceBrowseResult {
        return bridge.browse(request)
    }

    override suspend fun search(
        request: ChimahonAnimeSourceSearchRequest,
    ): ChimahonAnimeSourceSearchResult {
        return bridge.search(request)
    }

    override suspend fun loadDetail(
        request: ChimahonAnimeSourceDetailLoadRequest,
    ): ChimahonAnimeSourceDetailLoadResult {
        return bridge.loadDetail(request)
    }
}

fun ChimahonAnimeSourceBrowseRepositoryBridge.asAnimeSourceBrowseService(): ChimahonAnimeSourceBrowseService {
    return ChimahonRepositoryAnimeSourceBrowseService(this)
}

data class ChimahonAnimeSourceBrowseServices(
    val browse: ChimahonAnimeSourceBrowseService,
)
