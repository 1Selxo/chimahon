package app.chimahon.shared.novelui

interface ChimahonNovelLibraryRepositoryBridge {
    suspend fun loadLibrary(
        request: ChimahonNovelLibraryLoadRequest = ChimahonNovelLibraryLoadRequest(),
    ): ChimahonNovelLibraryLoadResult

    suspend fun loadNovel(novelId: String): ChimahonNovelBookRecord?

    suspend fun loadCategories(): List<ChimahonNovelCategoryRecord>

    suspend fun setNovelCategories(request: ChimahonNovelCategoryMutationRequest): ChimahonNovelMutationResult

    suspend fun updateNovelMetadata(request: ChimahonNovelMetadataMutationRequest): ChimahonNovelMutationResult

    suspend fun removeNovels(request: ChimahonNovelRemovalRequest): ChimahonNovelMutationResult

    suspend fun resetProgress(request: ChimahonNovelProgressResetRequest): ChimahonNovelMutationResult
}

data class ChimahonNovelLibraryRepositoryCallbacks(
    val loadLibrary: suspend (ChimahonNovelLibraryLoadRequest) -> ChimahonNovelLibraryLoadResult,
    val loadNovel: suspend (String) -> ChimahonNovelBookRecord?,
    val loadCategories: suspend () -> List<ChimahonNovelCategoryRecord>,
    val setNovelCategories: suspend (ChimahonNovelCategoryMutationRequest) -> ChimahonNovelMutationResult,
    val updateNovelMetadata: suspend (ChimahonNovelMetadataMutationRequest) -> ChimahonNovelMutationResult,
    val removeNovels: suspend (ChimahonNovelRemovalRequest) -> ChimahonNovelMutationResult,
    val resetProgress: suspend (ChimahonNovelProgressResetRequest) -> ChimahonNovelMutationResult,
)

class ChimahonCallbackNovelLibraryRepositoryBridge(
    private val callbacks: ChimahonNovelLibraryRepositoryCallbacks,
) : ChimahonNovelLibraryRepositoryBridge {
    override suspend fun loadLibrary(
        request: ChimahonNovelLibraryLoadRequest,
    ): ChimahonNovelLibraryLoadResult {
        return callbacks.loadLibrary(request)
    }

    override suspend fun loadNovel(novelId: String): ChimahonNovelBookRecord? {
        return callbacks.loadNovel(novelId)
    }

    override suspend fun loadCategories(): List<ChimahonNovelCategoryRecord> {
        return callbacks.loadCategories()
    }

    override suspend fun setNovelCategories(
        request: ChimahonNovelCategoryMutationRequest,
    ): ChimahonNovelMutationResult {
        return callbacks.setNovelCategories(request)
    }

    override suspend fun updateNovelMetadata(
        request: ChimahonNovelMetadataMutationRequest,
    ): ChimahonNovelMutationResult {
        return callbacks.updateNovelMetadata(request)
    }

    override suspend fun removeNovels(request: ChimahonNovelRemovalRequest): ChimahonNovelMutationResult {
        return callbacks.removeNovels(request)
    }

    override suspend fun resetProgress(request: ChimahonNovelProgressResetRequest): ChimahonNovelMutationResult {
        return callbacks.resetProgress(request)
    }
}

interface ChimahonNovelLibraryService {
    suspend fun getLibrary(): ChimahonNovelLibraryData

    suspend fun getNovel(novelId: String): ChimahonNovelBookRecord?

    suspend fun getCategories(): List<ChimahonNovelCategoryRecord>

    suspend fun setNovelCategories(
        novelIds: List<String>,
        categoryIds: List<String>,
        mode: ChimahonNovelCategoryMutationMode = ChimahonNovelCategoryMutationMode.Replace,
    ): ChimahonNovelMutationResult

    suspend fun updateNovelMetadata(request: ChimahonNovelMetadataMutationRequest): ChimahonNovelMutationResult

    suspend fun removeNovels(novelIds: List<String>, deleteLocalFiles: Boolean = true): ChimahonNovelMutationResult

    suspend fun resetProgress(novelIds: List<String>): ChimahonNovelMutationResult
}

class ChimahonRepositoryNovelLibraryService(
    private val bridge: ChimahonNovelLibraryRepositoryBridge,
    private val defaultRequest: ChimahonNovelLibraryLoadRequest = ChimahonNovelLibraryLoadRequest(),
) : ChimahonNovelLibraryService {
    override suspend fun getLibrary(): ChimahonNovelLibraryData {
        return bridge.loadLibrary(defaultRequest).data
    }

    override suspend fun getNovel(novelId: String): ChimahonNovelBookRecord? {
        return bridge.loadNovel(novelId)
    }

    override suspend fun getCategories(): List<ChimahonNovelCategoryRecord> {
        return bridge.loadCategories()
    }

    override suspend fun setNovelCategories(
        novelIds: List<String>,
        categoryIds: List<String>,
        mode: ChimahonNovelCategoryMutationMode,
    ): ChimahonNovelMutationResult {
        return bridge.setNovelCategories(
            ChimahonNovelCategoryMutationRequest(
                novelIds = novelIds,
                categoryIds = categoryIds,
                mode = mode,
            ),
        )
    }

    override suspend fun updateNovelMetadata(
        request: ChimahonNovelMetadataMutationRequest,
    ): ChimahonNovelMutationResult {
        return bridge.updateNovelMetadata(request)
    }

    override suspend fun removeNovels(
        novelIds: List<String>,
        deleteLocalFiles: Boolean,
    ): ChimahonNovelMutationResult {
        return bridge.removeNovels(
            ChimahonNovelRemovalRequest(
                novelIds = novelIds,
                deleteLocalFiles = deleteLocalFiles,
            ),
        )
    }

    override suspend fun resetProgress(novelIds: List<String>): ChimahonNovelMutationResult {
        return bridge.resetProgress(ChimahonNovelProgressResetRequest(novelIds = novelIds))
    }
}

fun ChimahonNovelLibraryRepositoryBridge.asNovelLibraryService(
    defaultRequest: ChimahonNovelLibraryLoadRequest = ChimahonNovelLibraryLoadRequest(),
): ChimahonNovelLibraryService {
    return ChimahonRepositoryNovelLibraryService(
        bridge = this,
        defaultRequest = defaultRequest,
    )
}

interface ChimahonNovelDetailRepositoryBridge {
    suspend fun loadDetail(request: ChimahonNovelDetailLoadRequest): ChimahonNovelDetailLoadResult

    suspend fun refreshDetail(request: ChimahonNovelDetailLoadRequest): ChimahonNovelDetailLoadResult

    suspend fun updateChapterFlags(request: ChimahonNovelChapterFlagMutationRequest): ChimahonNovelMutationResult

    suspend fun updateMetadata(request: ChimahonNovelMetadataMutationRequest): ChimahonNovelMutationResult

    suspend fun resetProgress(request: ChimahonNovelProgressResetRequest): ChimahonNovelMutationResult
}

data class ChimahonNovelDetailRepositoryCallbacks(
    val loadDetail: suspend (ChimahonNovelDetailLoadRequest) -> ChimahonNovelDetailLoadResult,
    val refreshDetail: suspend (ChimahonNovelDetailLoadRequest) -> ChimahonNovelDetailLoadResult,
    val updateChapterFlags: suspend (ChimahonNovelChapterFlagMutationRequest) -> ChimahonNovelMutationResult,
    val updateMetadata: suspend (ChimahonNovelMetadataMutationRequest) -> ChimahonNovelMutationResult,
    val resetProgress: suspend (ChimahonNovelProgressResetRequest) -> ChimahonNovelMutationResult,
)

class ChimahonCallbackNovelDetailRepositoryBridge(
    private val callbacks: ChimahonNovelDetailRepositoryCallbacks,
) : ChimahonNovelDetailRepositoryBridge {
    override suspend fun loadDetail(request: ChimahonNovelDetailLoadRequest): ChimahonNovelDetailLoadResult {
        return callbacks.loadDetail(request)
    }

    override suspend fun refreshDetail(request: ChimahonNovelDetailLoadRequest): ChimahonNovelDetailLoadResult {
        return callbacks.refreshDetail(request)
    }

    override suspend fun updateChapterFlags(
        request: ChimahonNovelChapterFlagMutationRequest,
    ): ChimahonNovelMutationResult {
        return callbacks.updateChapterFlags(request)
    }

    override suspend fun updateMetadata(request: ChimahonNovelMetadataMutationRequest): ChimahonNovelMutationResult {
        return callbacks.updateMetadata(request)
    }

    override suspend fun resetProgress(request: ChimahonNovelProgressResetRequest): ChimahonNovelMutationResult {
        return callbacks.resetProgress(request)
    }
}

interface ChimahonNovelDetailService {
    suspend fun loadDetail(request: ChimahonNovelDetailLoadRequest): ChimahonNovelDetailLoadResult

    suspend fun refreshDetail(request: ChimahonNovelDetailLoadRequest): ChimahonNovelDetailLoadResult

    suspend fun updateChapterFlags(request: ChimahonNovelChapterFlagMutationRequest): ChimahonNovelMutationResult

    suspend fun updateMetadata(request: ChimahonNovelMetadataMutationRequest): ChimahonNovelMutationResult

    suspend fun resetProgress(novelId: String): ChimahonNovelMutationResult
}

class ChimahonRepositoryNovelDetailService(
    private val bridge: ChimahonNovelDetailRepositoryBridge,
) : ChimahonNovelDetailService {
    override suspend fun loadDetail(request: ChimahonNovelDetailLoadRequest): ChimahonNovelDetailLoadResult {
        return bridge.loadDetail(request)
    }

    override suspend fun refreshDetail(request: ChimahonNovelDetailLoadRequest): ChimahonNovelDetailLoadResult {
        return bridge.refreshDetail(request)
    }

    override suspend fun updateChapterFlags(
        request: ChimahonNovelChapterFlagMutationRequest,
    ): ChimahonNovelMutationResult {
        return bridge.updateChapterFlags(request)
    }

    override suspend fun updateMetadata(request: ChimahonNovelMetadataMutationRequest): ChimahonNovelMutationResult {
        return bridge.updateMetadata(request)
    }

    override suspend fun resetProgress(novelId: String): ChimahonNovelMutationResult {
        return bridge.resetProgress(ChimahonNovelProgressResetRequest(novelIds = listOf(novelId)))
    }
}

fun ChimahonNovelDetailRepositoryBridge.asNovelDetailService(): ChimahonNovelDetailService {
    return ChimahonRepositoryNovelDetailService(this)
}

data class ChimahonNovelRepositoryBridges(
    val library: ChimahonNovelLibraryRepositoryBridge,
    val detail: ChimahonNovelDetailRepositoryBridge,
) {
    fun toServices(
        libraryRequest: ChimahonNovelLibraryLoadRequest = ChimahonNovelLibraryLoadRequest(),
    ): ChimahonNovelBackendServices {
        return ChimahonNovelBackendServices(
            library = library.asNovelLibraryService(libraryRequest),
            detail = detail.asNovelDetailService(),
        )
    }
}

data class ChimahonNovelBackendServices(
    val library: ChimahonNovelLibraryService,
    val detail: ChimahonNovelDetailService,
)
