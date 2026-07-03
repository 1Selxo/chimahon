package app.chimahon.shared.anime

data class ChimahonAnimeBackendError(
    val code: ChimahonAnimeBackendErrorCode,
    val message: String? = null,
    val subjectId: Long? = null,
    val sourceId: Long? = null,
)

enum class ChimahonAnimeBackendErrorCode {
    NotFound,
    SourceUnavailable,
    Network,
    Storage,
    Unsupported,
    PermissionDenied,
    Unknown,
}

data class ChimahonAnimeSourceInfo(
    val id: Long,
    val name: String,
    val language: String = "",
    val iconUrl: String? = null,
    val baseUrl: String? = null,
    val isLocal: Boolean = false,
    val supportsSearch: Boolean = false,
    val supportsLatest: Boolean = false,
) {
    val displayName: String
        get() = name.ifBlank {
            if (isLocal) {
                "Local anime"
            } else {
                "Source $id"
            }
        }
}

data class ChimahonAnimeLibraryRecord(
    val anime: ChimahonAnimeEntry,
    val categoryIds: List<Long> = emptyList(),
    val totalEpisodeCount: Int = 0,
    val seenEpisodeCount: Int = 0,
    val bookmarkedEpisodeCount: Int = 0,
    val fillermarkedEpisodeCount: Int = 0,
    val downloadedEpisodeCount: Int = 0,
    val latestUpload: Long = 0L,
    val episodeFetchedAt: Long = 0L,
    val lastSeen: Long = 0L,
    val sourceInfo: ChimahonAnimeSourceInfo? = null,
    val isLocal: Boolean = sourceInfo?.isLocal == true,
) {
    val id: Long
        get() = anime.id

    fun toLibraryEntry(
        episodes: List<ChimahonAnimeEpisodeEntry> = emptyList(),
    ): ChimahonAnimeLibraryEntry {
        return ChimahonAnimeLibraryEntry(
            anime = anime,
            episodes = episodes,
            categoryIds = categoryIds,
            totalEpisodeCount = totalEpisodeCount.takeIf { it > 0 } ?: episodes.size,
            seenEpisodeCount = seenEpisodeCount.takeIf { it > 0 } ?: episodes.count { it.seen },
            bookmarkedEpisodeCount = bookmarkedEpisodeCount.takeIf { it > 0 } ?: episodes.count { it.bookmark },
            fillermarkedEpisodeCount = fillermarkedEpisodeCount.takeIf { it > 0 } ?: episodes.count { it.fillermark },
            downloadedEpisodeCount = downloadedEpisodeCount,
            latestUpload = latestUpload.takeIf { it > 0L } ?: (episodes.maxOfOrNull { it.dateUpload } ?: 0L),
            episodeFetchedAt = episodeFetchedAt.takeIf { it > 0L } ?: (episodes.maxOfOrNull { it.dateFetch } ?: 0L),
            lastSeen = lastSeen,
            sourceName = sourceInfo?.displayName,
            sourceLanguage = sourceInfo?.language.orEmpty(),
            isLocal = isLocal,
        )
    }
}

fun List<ChimahonAnimeLibraryRecord>.toAnimeLibraryData(
    categories: List<ChimahonAnimeCategory> = emptyList(),
    episodesByAnimeId: Map<Long, List<ChimahonAnimeEpisodeEntry>> = emptyMap(),
    downloadedOnly: Boolean = false,
): ChimahonAnimeLibraryData {
    val entries = map { record ->
        record.toLibraryEntry(episodes = episodesByAnimeId[record.id].orEmpty())
    }
    return ChimahonAnimeLibraryData(
        entries = entries,
        categories = categories,
        downloadedOnly = downloadedOnly,
    )
}

data class ChimahonAnimeLibraryLoadRequest(
    val includeEpisodes: Boolean = true,
    val includeCategories: Boolean = true,
    val includeDownloads: Boolean = true,
    val includeSourceInfo: Boolean = true,
    val includeHistory: Boolean = true,
    val favoriteOnly: Boolean = true,
    val applyScanlatorFilter: Boolean = false,
)

data class ChimahonAnimeLibraryLoadResult(
    val data: ChimahonAnimeLibraryData = ChimahonAnimeLibraryData(
        entries = emptyList(),
        categories = emptyList(),
    ),
    val sourceInfos: Map<Long, ChimahonAnimeSourceInfo> = emptyMap(),
    val downloads: ChimahonAnimeDownloadSnapshot = ChimahonAnimeDownloadSnapshot(),
    val history: ChimahonAnimeHistoryData? = null,
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonAnimeEpisodesLoadRequest(
    val animeId: Long,
    val includeMergedEpisodes: Boolean = true,
    val applyScanlatorFilter: Boolean = false,
    val refreshFromSource: Boolean = false,
)

data class ChimahonAnimeDetailLoadRequest(
    val animeId: Long,
    val includeMergedEpisodes: Boolean = true,
    val includeLibraryContext: Boolean = true,
    val includeDownloads: Boolean = true,
    val includeHistory: Boolean = true,
    val refreshFromSource: Boolean = false,
    val applyScanlatorFilter: Boolean = false,
)

data class ChimahonAnimeDetailData(
    val entry: ChimahonAnimeLibraryEntry,
    val libraryData: ChimahonAnimeLibraryData? = null,
    val sourceInfo: ChimahonAnimeSourceInfo? = null,
    val downloads: ChimahonAnimeDownloadSnapshot = ChimahonAnimeDownloadSnapshot(),
    val trackedAnimeIds: Set<Long> = emptySet(),
    val history: ChimahonAnimeHistoryData? = null,
) {
    val anime: ChimahonAnimeEntry
        get() = entry.anime

    val episodes: List<ChimahonAnimeEpisodeEntry>
        get() = entry.episodes
}

data class ChimahonAnimeDetailLoadResult(
    val detail: ChimahonAnimeDetailData? = null,
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()

    val found: Boolean
        get() = detail != null
}

data class ChimahonAnimeCategoryMutationRequest(
    val animeIds: List<Long>,
    val categoryIds: List<Long>,
)

data class ChimahonAnimeLibraryRemovalRequest(
    val animeIds: List<Long>,
)

data class ChimahonAnimeSeenMutationRequest(
    val animeIds: List<Long> = emptyList(),
    val episodeIds: List<Long> = emptyList(),
    val seen: Boolean,
    val seenAtMillis: Long? = null,
)

data class ChimahonAnimeEpisodeFlagMutationRequest(
    val episodeIds: List<Long>,
    val seen: Boolean? = null,
    val bookmark: Boolean? = null,
    val fillermark: Boolean? = null,
)

data class ChimahonAnimeEpisodeProgressUpdate(
    val animeId: Long,
    val episodeId: Long,
    val positionSeconds: Long,
    val durationSeconds: Long,
    val watchedAtMillis: Long,
    val sessionWatchDurationMillis: Long = 0L,
    val markSeen: Boolean = false,
) {
    val progressFraction: Float
        get() = if (durationSeconds > 0L) {
            (positionSeconds.toFloat() / durationSeconds.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
}

data class ChimahonAnimeDownloadSnapshot(
    val downloadedEpisodeIds: Set<Long> = emptySet(),
    val queuedEpisodeIds: Set<Long> = emptySet(),
    val downloadingEpisodeProgress: Map<Long, Int> = emptyMap(),
    val erroredEpisodeIds: Set<Long> = emptySet(),
    val downloadedAnimeIds: Set<Long> = emptySet(),
    val localAnimeIds: Set<Long> = emptySet(),
) {
    fun stateFor(
        episodeId: Long,
        animeId: Long? = null,
    ): ChimahonAnimeEpisodeDownloadState {
        return when {
            episodeId in erroredEpisodeIds -> ChimahonAnimeEpisodeDownloadState.Error
            episodeId in downloadingEpisodeProgress -> ChimahonAnimeEpisodeDownloadState.Downloading
            episodeId in queuedEpisodeIds -> ChimahonAnimeEpisodeDownloadState.Queued
            episodeId in downloadedEpisodeIds -> ChimahonAnimeEpisodeDownloadState.Downloaded
            animeId != null && animeId in localAnimeIds -> ChimahonAnimeEpisodeDownloadState.Downloaded
            animeId != null && animeId in downloadedAnimeIds -> ChimahonAnimeEpisodeDownloadState.Downloaded
            else -> ChimahonAnimeEpisodeDownloadState.NotDownloaded
        }
    }

    fun progressFor(
        episodeId: Long,
        animeId: Long? = null,
    ): Int {
        return when {
            episodeId in downloadingEpisodeProgress -> downloadingEpisodeProgress[episodeId]?.coerceIn(0, 100) ?: 0
            stateFor(episodeId, animeId) == ChimahonAnimeEpisodeDownloadState.Downloaded -> 100
            else -> 0
        }
    }
}

enum class ChimahonAnimeEpisodeDownloadState {
    NotDownloaded,
    Queued,
    Downloading,
    Downloaded,
    Error,
}

data class ChimahonAnimeHistoryLoadRequest(
    val query: String = "",
    val animeId: Long? = null,
    val episodeId: Long? = null,
    val limit: Int? = null,
    val includeRelations: Boolean = true,
)

data class ChimahonAnimeHistoryEntry(
    val id: Long,
    val animeId: Long,
    val episodeId: Long,
    val animeTitle: String,
    val episodeTitle: String? = null,
    val sourceId: Long = 0L,
    val sourceName: String? = null,
    val thumbnailUrl: String? = null,
    val episodeNumber: Double = -1.0,
    val seen: Boolean = false,
    val lastSecondSeen: Long = 0L,
    val totalSeconds: Long = 0L,
    val watchedAtMillis: Long? = null,
    val watchDurationMillis: Long = 0L,
) {
    val isRecognizedEpisodeNumber: Boolean
        get() = episodeNumber >= 0.0

    val progressFraction: Float
        get() = if (totalSeconds > 0L) {
            (lastSecondSeen.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
}

data class ChimahonAnimeHistoryData(
    val entries: List<ChimahonAnimeHistoryEntry> = emptyList(),
    val query: String = "",
    val totalWatchDurationMillis: Long = 0L,
    val loadedAtMillis: Long = 0L,
) {
    val lastEntry: ChimahonAnimeHistoryEntry?
        get() = entries.maxByOrNull { it.watchedAtMillis ?: Long.MIN_VALUE }
}

data class ChimahonAnimeHistoryLoadResult(
    val data: ChimahonAnimeHistoryData = ChimahonAnimeHistoryData(),
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonAnimeHistoryMutationResult(
    val affectedCount: Int = 0,
    val totalWatchDurationMillis: Long = 0L,
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonAnimePlayerLoadRequest(
    val animeId: Long,
    val episodeId: Long,
    val preferredHosterKey: String = "",
    val preferredVideoKey: String = "",
    val preferDownload: Boolean = false,
    val allowExternalPlayer: Boolean = false,
    val includeAdjacentEpisodes: Boolean = true,
    val includeHistory: Boolean = true,
)

data class ChimahonAnimeHosterLoadRequest(
    val animeId: Long,
    val episodeId: Long,
    val forceRefresh: Boolean = false,
)

data class ChimahonAnimeVideoLoadRequest(
    val animeId: Long,
    val episodeId: Long,
    val hosterKey: String,
    val forceRefresh: Boolean = false,
)

data class ChimahonAnimePlayerSession(
    val anime: ChimahonAnimeEntry,
    val episode: ChimahonAnimeEpisodeEntry,
    val sourceInfo: ChimahonAnimeSourceInfo? = null,
    val previousEpisode: ChimahonAnimeEpisodeEntry? = null,
    val nextEpisode: ChimahonAnimeEpisodeEntry? = null,
    val hosters: List<ChimahonAnimeHoster> = emptyList(),
    val videos: List<ChimahonAnimeVideoStream> = emptyList(),
    val selectedHosterKey: String? = null,
    val selectedVideoKey: String? = null,
    val subtitleTracks: List<ChimahonAnimeMediaTrack> = emptyList(),
    val selectedSubtitleTrackKeys: Set<String> = emptySet(),
    val audioTracks: List<ChimahonAnimeMediaTrack> = emptyList(),
    val selectedAudioTrackKey: String? = null,
    val skipSegments: List<ChimahonAnimeSkipSegment> = emptyList(),
    val historyEntry: ChimahonAnimeHistoryEntry? = null,
    val playbackSource: ChimahonAnimePlaybackSource = ChimahonAnimePlaybackSource.Stream,
    val positionSeconds: Long = historyEntry?.lastSecondSeen ?: episode.lastSecondSeen,
    val durationSeconds: Long = episode.totalSeconds,
    val bufferedSeconds: Long = 0L,
    val playbackSpeed: Float = 1f,
    val paused: Boolean = true,
    val loading: Boolean = false,
    val error: ChimahonAnimeBackendError? = null,
)

data class ChimahonAnimePlayerLoadResult(
    val session: ChimahonAnimePlayerSession? = null,
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonAnimeHosterLoadResult(
    val hosters: List<ChimahonAnimeHoster> = emptyList(),
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonAnimeVideoLoadResult(
    val hosterKey: String,
    val videos: List<ChimahonAnimeVideoStream> = emptyList(),
    val errors: List<ChimahonAnimeBackendError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonAnimeHoster(
    val key: String,
    val title: String,
    val url: String? = null,
    val status: ChimahonAnimeHosterStatus = ChimahonAnimeHosterStatus.Idle,
    val videoCount: Int = 0,
    val preferred: Boolean = false,
    val errorMessage: String? = null,
    val internalData: String = key,
)

enum class ChimahonAnimeHosterStatus {
    Idle,
    Loading,
    Ready,
    Empty,
    Error,
}

data class ChimahonAnimeVideoStream(
    val key: String,
    val title: String,
    val streamUrl: String,
    val sourceUrl: String? = null,
    val hosterKey: String? = null,
    val hosterTitle: String? = null,
    val resolution: Int? = null,
    val bitrate: Int? = null,
    val headers: List<ChimahonAnimeHttpHeader> = emptyList(),
    val preferred: Boolean = false,
    val initialized: Boolean = true,
    val status: ChimahonAnimeVideoStatus = ChimahonAnimeVideoStatus.Ready,
    val errorMessage: String? = null,
    val subtitleTracks: List<ChimahonAnimeMediaTrack> = emptyList(),
    val audioTracks: List<ChimahonAnimeMediaTrack> = emptyList(),
    val skipSegments: List<ChimahonAnimeSkipSegment> = emptyList(),
    val ffmpegStreamArgs: List<ChimahonAnimeCommandArgument> = emptyList(),
    val ffmpegVideoArgs: List<ChimahonAnimeCommandArgument> = emptyList(),
    val internalData: String = key,
)

enum class ChimahonAnimeVideoStatus {
    Queued,
    Loading,
    Ready,
    Error,
}

data class ChimahonAnimeHttpHeader(
    val name: String,
    val value: String,
)

data class ChimahonAnimeCommandArgument(
    val name: String,
    val value: String,
)

data class ChimahonAnimeMediaTrack(
    val key: String,
    val title: String,
    val language: String? = null,
    val url: String? = null,
    val external: Boolean = false,
    val delaySeconds: Double = 0.0,
    val selectedByDefault: Boolean = false,
    val internalData: String = key,
)

data class ChimahonAnimeSkipSegment(
    val key: String,
    val title: String,
    val startSeconds: Long,
    val endSeconds: Long? = null,
    val kind: ChimahonAnimeSkipKind = ChimahonAnimeSkipKind.Other,
    val source: ChimahonAnimeSkipSource = ChimahonAnimeSkipSource.Source,
    val autoSkip: Boolean = false,
)

enum class ChimahonAnimeSkipKind {
    Opening,
    Ending,
    Recap,
    MixedOpening,
    Other,
}

enum class ChimahonAnimeSkipSource {
    Source,
    AniSkip,
    Local,
    Manual,
}

enum class ChimahonAnimePlaybackSource {
    Stream,
    Download,
    ExternalPlayer,
}
