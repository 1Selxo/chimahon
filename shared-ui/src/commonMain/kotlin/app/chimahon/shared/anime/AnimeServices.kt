package app.chimahon.shared.anime

interface ChimahonAnimeLibraryService {
    suspend fun getLibrary(): ChimahonAnimeLibraryData

    suspend fun getAnime(animeId: Long): ChimahonAnimeEntry?

    suspend fun getEpisodes(animeId: Long): List<ChimahonAnimeEpisodeEntry>

    suspend fun getCategories(): List<ChimahonAnimeCategory>

    suspend fun setAnimeCategories(animeIds: List<Long>, categoryIds: List<Long>): ChimahonAnimeMutationResult

    suspend fun removeAnimeFromLibrary(animeIds: List<Long>): ChimahonAnimeMutationResult

    suspend fun markEpisodesSeen(
        animeIds: List<Long>,
        seen: Boolean,
    ): ChimahonAnimeMutationResult
}

interface ChimahonAnimeExtensionRepoService {
    suspend fun getRepos(): ChimahonAnimeExtensionRepoData

    suspend fun createRepo(baseUrl: String): ChimahonAnimeExtensionRepoChangeResult

    suspend fun createRepo(request: ChimahonAnimeExtensionRepoSaveRequest): ChimahonAnimeExtensionRepoChangeResult {
        return createRepo(request.normalizedBaseUrl)
    }

    suspend fun replaceRepo(repo: ChimahonAnimeExtensionRepoSummary): ChimahonAnimeExtensionRepoChangeResult

    suspend fun updateRepo(
        currentBaseUrl: String,
        request: ChimahonAnimeExtensionRepoSaveRequest,
    ): ChimahonAnimeExtensionRepoChangeResult {
        return replaceRepo(request.copy(originalBaseUrl = currentBaseUrl).toSummary())
    }

    suspend fun deleteRepo(baseUrl: String): ChimahonAnimeExtensionRepoChangeResult

    suspend fun refreshRepos(): ChimahonAnimeExtensionRepoChangeResult

    suspend fun refreshRepo(baseUrl: String): ChimahonAnimeExtensionRepoChangeResult {
        return ChimahonAnimeExtensionRepoChangeResult(
            errors = listOf("Refreshing a single anime extension repository is not wired."),
        )
    }

    suspend fun setRepoEnabled(
        baseUrl: String,
        enabled: Boolean,
    ): ChimahonAnimeExtensionRepoChangeResult {
        val action = if (enabled) "Enabling" else "Disabling"
        return ChimahonAnimeExtensionRepoChangeResult(
            errors = listOf("$action anime extension repositories is not wired."),
        )
    }
}

data class ChimahonAnimeMutationResult(
    val animeCount: Int = 0,
    val episodeCount: Int = 0,
    val categoryCount: Int = 0,
    val errors: List<String> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonAnimeExtensionRepoChangeResult(
    val repos: List<ChimahonAnimeExtensionRepoSummary> = emptyList(),
    val events: List<ChimahonAnimeExtensionRepoEvent> = emptyList(),
    val errors: List<String> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty() && events.isEmpty()
}
