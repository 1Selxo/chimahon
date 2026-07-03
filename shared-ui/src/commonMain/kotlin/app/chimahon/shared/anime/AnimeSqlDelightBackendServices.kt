package app.chimahon.shared.anime

import dataanime.GetCategories
import dataanime.GetCategoriesByAnimeId
import tachiyomi.mi.data.AnimeDatabase

class ChimahonSqlDelightAnimeBackend(
    private val database: AnimeDatabase,
    private val currentTimeMillis: () -> Long = { 0L },
    private val sourceInfos: () -> Map<Long, ChimahonAnimeSourceInfo> = { emptyMap() },
    private val downloads: () -> ChimahonAnimeDownloadSnapshot = { ChimahonAnimeDownloadSnapshot() },
) : ChimahonAnimeLibraryRepositoryBridge,
    ChimahonAnimeDetailRepositoryBridge,
    ChimahonAnimeHistoryRepositoryBridge,
    ChimahonAnimePlayerRepositoryBridge {

    override suspend fun loadLibrary(
        request: ChimahonAnimeLibraryLoadRequest,
    ): ChimahonAnimeLibraryLoadResult {
        val animeRows = if (request.favoriteOnly) {
            database.animesQueries.getFavorites().executeAsList()
        } else {
            database.animesQueries.getAll().executeAsList()
        }
        val episodeMap = if (request.includeEpisodes) {
            animeRows.associate { anime ->
                anime._id to loadEpisodesForAnime(anime._id)
            }
        } else {
            emptyMap()
        }
        val categoryIdsByAnimeId = if (request.includeCategories) {
            animeRows.associate { anime ->
                anime._id to database.categoriesQueries.getCategoriesByAnimeId(anime._id)
                    .executeAsList()
                    .map(GetCategoriesByAnimeId::id)
                    .ifEmpty { listOf(CHIMAHON_ANIME_UNCATEGORIZED_ID) }
            }
        } else {
            emptyMap()
        }
        val sourceInfoMap = if (request.includeSourceInfo) sourceInfos() else emptyMap()
        val downloadSnapshot = if (request.includeDownloads) downloads() else ChimahonAnimeDownloadSnapshot()
        val records = animeRows.toChimahonAnimeLibraryRecords(
            episodesByAnimeId = episodeMap,
            sourceInfos = sourceInfoMap,
            categoryIdsByAnimeId = categoryIdsByAnimeId,
            downloadedEpisodeIds = downloadSnapshot.downloadedEpisodeIds,
        )
        return ChimahonAnimeLibraryLoadResult(
            data = records.toAnimeLibraryData(
                categories = if (request.includeCategories) loadCategories() else emptyList(),
                episodesByAnimeId = episodeMap,
            ),
            sourceInfos = sourceInfoMap,
            downloads = downloadSnapshot,
            loadedAtMillis = currentTimeMillis(),
        )
    }

    override suspend fun loadAnime(animeId: Long): ChimahonAnimeEntry? {
        return database.animesQueries.getAnimeById(animeId)
            .executeAsOneOrNull()
            ?.toChimahonAnimeEntry()
    }

    override suspend fun loadEpisodes(request: ChimahonAnimeEpisodesLoadRequest): List<ChimahonAnimeEpisodeEntry> {
        return loadEpisodesForAnime(request.animeId)
    }

    override suspend fun loadCategories(): List<ChimahonAnimeCategory> {
        return database.categoriesQueries.getCategories()
            .executeAsList()
            .map(GetCategories::toChimahonAnimeCategory)
            .ifEmpty { listOf(defaultAnimeCategory()) }
    }

    override suspend fun setAnimeCategories(
        request: ChimahonAnimeCategoryMutationRequest,
    ): ChimahonAnimeMutationResult {
        if (request.animeIds.isEmpty()) return ChimahonAnimeMutationResult()
        val storedCategoryIds = request.categoryIds
            .filter { it != CHIMAHON_ANIME_UNCATEGORIZED_ID }
            .distinct()
        database.transaction {
            request.animeIds.distinct().forEach { animeId ->
                database.animes_categoriesQueries.deleteAnimeCategoryByAnimeId(animeId)
                storedCategoryIds.forEach { categoryId ->
                    database.animes_categoriesQueries.insert(animeId, categoryId)
                }
            }
        }
        return ChimahonAnimeMutationResult(
            animeCount = request.animeIds.distinct().size,
            categoryCount = storedCategoryIds.size,
        )
    }

    override suspend fun removeAnimeFromLibrary(
        request: ChimahonAnimeLibraryRemovalRequest,
    ): ChimahonAnimeMutationResult {
        val animeIds = request.animeIds.distinct()
        animeIds.forEach { animeId ->
            database.animesQueries.update(
                source = null,
                url = null,
                artist = null,
                author = null,
                description = null,
                genre = null,
                title = null,
                status = null,
                thumbnailUrl = null,
                favorite = false,
                lastUpdate = null,
                nextUpdate = null,
                initialized = null,
                viewer = null,
                episodeFlags = null,
                coverLastModified = null,
                dateAdded = null,
                updateStrategy = null,
                calculateInterval = null,
                version = null,
                isSyncing = null,
                fetchType = null,
                parentId = null,
                seasonFlags = null,
                seasonNumber = null,
                seasonSourceOrder = null,
                backgroundUrl = null,
                backgroundLastModified = null,
                animeId = animeId,
            )
        }
        return ChimahonAnimeMutationResult(animeCount = animeIds.size)
    }

    override suspend fun markEpisodesSeen(
        request: ChimahonAnimeSeenMutationRequest,
    ): ChimahonAnimeMutationResult {
        val episodeIds = when {
            request.episodeIds.isNotEmpty() -> request.episodeIds.distinct()
            request.animeIds.isNotEmpty() -> request.animeIds.distinct()
                .flatMap { animeId -> database.episodesQueries.getEpisodesByAnimeId(animeId).executeAsList() }
                .map { it._id }
                .distinct()
            else -> emptyList()
        }
        updateEpisodeSeenState(episodeIds, request.seen)
        return ChimahonAnimeMutationResult(episodeCount = episodeIds.size)
    }

    override suspend fun loadDetail(
        request: ChimahonAnimeDetailLoadRequest,
    ): ChimahonAnimeDetailLoadResult {
        val anime = loadAnime(request.animeId)
            ?: return ChimahonAnimeDetailLoadResult(
                errors = listOf(
                    ChimahonAnimeBackendError(
                        code = ChimahonAnimeBackendErrorCode.NotFound,
                        subjectId = request.animeId,
                    ),
                ),
            )
        val episodes = if (request.includeMergedEpisodes) {
            loadEpisodesForAnime(request.animeId)
        } else {
            emptyList()
        }
        val sourceInfo = sourceInfos()[anime.sourceId]
        val categories = database.categoriesQueries.getCategoriesByAnimeId(anime.id)
            .executeAsList()
            .map(GetCategoriesByAnimeId::id)
            .ifEmpty { listOf(CHIMAHON_ANIME_UNCATEGORIZED_ID) }
        val entry = ChimahonAnimeLibraryRecord(
            anime = anime,
            categoryIds = categories,
            sourceInfo = sourceInfo,
        ).toLibraryEntry(episodes)
        return ChimahonAnimeDetailLoadResult(
            detail = ChimahonAnimeDetailData(
                entry = entry,
                libraryData = if (request.includeLibraryContext) {
                    loadLibrary(ChimahonAnimeLibraryLoadRequest()).data
                } else {
                    null
                },
                sourceInfo = sourceInfo,
                downloads = if (request.includeDownloads) downloads() else ChimahonAnimeDownloadSnapshot(),
            ),
            loadedAtMillis = currentTimeMillis(),
        )
    }

    override suspend fun refreshEpisodes(
        request: ChimahonAnimeEpisodesLoadRequest,
    ): ChimahonAnimeDetailLoadResult {
        return loadDetail(
            ChimahonAnimeDetailLoadRequest(
                animeId = request.animeId,
                refreshFromSource = request.refreshFromSource,
                applyScanlatorFilter = request.applyScanlatorFilter,
            ),
        )
    }

    override suspend fun updateEpisodeFlags(
        request: ChimahonAnimeEpisodeFlagMutationRequest,
    ): ChimahonAnimeMutationResult {
        val episodeIds = request.episodeIds.distinct()
        episodeIds.forEach { episodeId ->
            val episode = database.episodesQueries.getEpisodeById(episodeId).executeAsOneOrNull()
            val lastSecondSeen = request.seen?.let { seen ->
                if (seen) {
                    episode?.total_seconds?.takeIf { it > 0L }
                } else {
                    0L
                }
            }
            database.episodesQueries.update(
                animeId = null,
                url = null,
                name = null,
                scanlator = null,
                seen = request.seen,
                bookmark = request.bookmark,
                lastSecondSeen = lastSecondSeen,
                totalSeconds = null,
                episodeNumber = null,
                sourceOrder = null,
                dateFetch = null,
                dateUpload = null,
                version = null,
                isSyncing = null,
                summary = null,
                previewUrl = null,
                fillermark = request.fillermark,
                episodeId = episodeId,
            )
        }
        return ChimahonAnimeMutationResult(episodeCount = episodeIds.size)
    }

    override suspend fun loadHistory(request: ChimahonAnimeHistoryLoadRequest): ChimahonAnimeHistoryLoadResult {
        return ChimahonAnimeHistoryLoadResult()
    }

    override suspend fun getLastHistory(): ChimahonAnimeHistoryEntry? {
        return null
    }

    override suspend fun recordProgress(update: ChimahonAnimeEpisodeProgressUpdate): ChimahonAnimeHistoryMutationResult {
        database.episodesQueries.update(
            animeId = null,
            url = null,
            name = null,
            scanlator = null,
            seen = if (update.markSeen) true else null,
            bookmark = null,
            lastSecondSeen = update.positionSeconds.coerceAtLeast(0L),
            totalSeconds = update.durationSeconds.coerceAtLeast(0L),
            episodeNumber = null,
            sourceOrder = null,
            dateFetch = null,
            dateUpload = null,
            version = null,
            isSyncing = null,
            summary = null,
            previewUrl = null,
            fillermark = null,
            episodeId = update.episodeId,
        )
        return ChimahonAnimeHistoryMutationResult(
            affectedCount = 1,
            totalWatchDurationMillis = update.sessionWatchDurationMillis,
        )
    }

    override suspend fun resetHistoryForAnime(animeIds: List<Long>): ChimahonAnimeHistoryMutationResult {
        val episodes = animeIds.distinct()
            .flatMap { animeId -> database.episodesQueries.getEpisodesByAnimeId(animeId).executeAsList() }
        episodes.forEach { episode ->
            database.episodesQueries.update(
                animeId = null,
                url = null,
                name = null,
                scanlator = null,
                seen = false,
                bookmark = null,
                lastSecondSeen = 0L,
                totalSeconds = null,
                episodeNumber = null,
                sourceOrder = null,
                dateFetch = null,
                dateUpload = null,
                version = null,
                isSyncing = null,
                summary = null,
                previewUrl = null,
                fillermark = null,
                episodeId = episode._id,
            )
        }
        return ChimahonAnimeHistoryMutationResult(affectedCount = episodes.size)
    }

    override suspend fun clearHistory(): ChimahonAnimeHistoryMutationResult {
        val animeRows = database.animesQueries.getAll().executeAsList()
        return resetHistoryForAnime(animeRows.map { it._id })
    }

    override suspend fun loadSession(
        request: ChimahonAnimePlayerLoadRequest,
    ): ChimahonAnimePlayerLoadResult {
        return ChimahonAnimePlayerLoadResult(
            errors = listOf(
                ChimahonAnimeBackendError(
                    code = ChimahonAnimeBackendErrorCode.Unsupported,
                    message = "Video hoster playback is not wired to SQLDelight alone.",
                    subjectId = request.episodeId,
                ),
            ),
        )
    }

    override suspend fun loadHosters(
        request: ChimahonAnimeHosterLoadRequest,
    ): ChimahonAnimeHosterLoadResult {
        return ChimahonAnimeHosterLoadResult(errors = unsupportedVideoErrors(request.animeId))
    }

    override suspend fun loadVideos(
        request: ChimahonAnimeVideoLoadRequest,
    ): ChimahonAnimeVideoLoadResult {
        return ChimahonAnimeVideoLoadResult(
            hosterKey = request.hosterKey,
            errors = unsupportedVideoErrors(request.animeId),
        )
    }

    private fun loadEpisodesForAnime(animeId: Long): List<ChimahonAnimeEpisodeEntry> {
        return database.episodesQueries.getEpisodesByAnimeId(animeId)
            .executeAsList()
            .map { it.toChimahonAnimeEpisodeEntry() }
            .sortedWith(
                compareBy<ChimahonAnimeEpisodeEntry> { it.sourceOrder }
                    .thenBy { it.id },
            )
    }

    private fun updateEpisodeSeenState(
        episodeIds: List<Long>,
        seen: Boolean,
    ) {
        episodeIds.forEach { episodeId ->
            val episode = database.episodesQueries.getEpisodeById(episodeId).executeAsOneOrNull()
            database.episodesQueries.update(
                animeId = null,
                url = null,
                name = null,
                scanlator = null,
                seen = seen,
                bookmark = null,
                lastSecondSeen = if (seen) episode?.total_seconds?.takeIf { it > 0L } else 0L,
                totalSeconds = null,
                episodeNumber = null,
                sourceOrder = null,
                dateFetch = null,
                dateUpload = null,
                version = null,
                isSyncing = null,
                summary = null,
                previewUrl = null,
                fillermark = null,
                episodeId = episodeId,
            )
        }
    }

    private fun unsupportedVideoErrors(subjectId: Long): List<ChimahonAnimeBackendError> {
        return listOf(
            ChimahonAnimeBackendError(
                code = ChimahonAnimeBackendErrorCode.Unsupported,
                message = "Video stream resolution still requires an anime source engine.",
                subjectId = subjectId,
            ),
        )
    }
}

fun ChimahonSqlDelightAnimeBackend.toRepositoryBridges(): ChimahonAnimeRepositoryBridges {
    return ChimahonAnimeRepositoryBridges(
        library = this,
        detail = this,
        history = this,
        player = this,
    )
}

fun ChimahonSqlDelightAnimeBackend.toBackendServices(
    libraryRequest: ChimahonAnimeLibraryLoadRequest = ChimahonAnimeLibraryLoadRequest(),
): ChimahonAnimeBackendServices {
    return toRepositoryBridges()
        .toServices(libraryRequest)
        .copy(
            capabilities = ChimahonAnimeBackendCapabilities(
                library = true,
                detail = true,
                history = true,
                player = false,
            ),
        )
}

private fun GetCategories.toChimahonAnimeCategory(): ChimahonAnimeCategory {
    return ChimahonAnimeCategory(
        id = id,
        name = name,
        order = order,
        hidden = hidden != 0L,
    )
}
