package app.chimahon.shared.anime

data class ChimahonAnimeDownloadQueueLoadRequest(
    val animeId: Long? = null,
    val episodeIds: Set<Long> = emptySet(),
    val includeCompleted: Boolean = true,
    val includeCanceled: Boolean = true,
    val includeFailed: Boolean = true,
)

data class ChimahonAnimeDownloadQueueSnapshot(
    val items: List<ChimahonAnimeDownloadQueueItem> = emptyList(),
    val paused: Boolean = false,
    val downloadedEpisodeIds: Set<Long> = emptySet(),
    val queuedEpisodeIds: Set<Long> = emptySet(),
    val downloadingEpisodeProgress: Map<Long, Int> = emptyMap(),
    val erroredEpisodeIds: Set<Long> = emptySet(),
    val downloadedAnimeIds: Set<Long> = emptySet(),
    val localAnimeIds: Set<Long> = emptySet(),
    val maxConcurrentDownloads: Int = 1,
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonAnimeDownloadError> = emptyList(),
) {
    val activeCount: Int
        get() = items.count { it.status.isActive }

    val queuedCount: Int
        get() = items.count { it.status == ChimahonAnimeDownloadStatus.Queued }

    val completedCount: Int
        get() = items.count { it.status == ChimahonAnimeDownloadStatus.Downloaded || it.downloadedOnDisk }

    val failedCount: Int
        get() = items.count { it.status == ChimahonAnimeDownloadStatus.Failed }

    val canceledCount: Int
        get() = items.count { it.status == ChimahonAnimeDownloadStatus.Canceled }

    val pendingCount: Int
        get() = items.count { it.status.isPending }

    val successful: Boolean
        get() = errors.isEmpty()

    fun statusFor(
        episodeId: Long,
        animeId: Long? = null,
    ): ChimahonAnimeDownloadStatus {
        val item = items.firstOrNull { it.episodeId == episodeId }
        if (item != null) return item.status

        return when {
            episodeId in erroredEpisodeIds -> ChimahonAnimeDownloadStatus.Failed
            episodeId in downloadingEpisodeProgress -> ChimahonAnimeDownloadStatus.Downloading
            episodeId in queuedEpisodeIds -> ChimahonAnimeDownloadStatus.Queued
            episodeId in downloadedEpisodeIds -> ChimahonAnimeDownloadStatus.Downloaded
            animeId != null && animeId in localAnimeIds -> ChimahonAnimeDownloadStatus.Downloaded
            animeId != null && animeId in downloadedAnimeIds -> ChimahonAnimeDownloadStatus.Downloaded
            else -> ChimahonAnimeDownloadStatus.NotDownloaded
        }
    }

    fun progressFor(
        episodeId: Long,
        animeId: Long? = null,
    ): ChimahonAnimeDownloadProgress {
        val item = items.firstOrNull { it.episodeId == episodeId }
        if (item != null) return item.progress

        val percent = downloadingEpisodeProgress[episodeId]?.coerceIn(0, 100)
        return ChimahonAnimeDownloadProgress(
            percent = percent ?: if (statusFor(episodeId, animeId) == ChimahonAnimeDownloadStatus.Downloaded) 100 else 0,
        )
    }

    fun containsDownloadedEpisode(
        episodeId: Long,
        animeId: Long? = null,
    ): Boolean {
        return statusFor(episodeId = episodeId, animeId = animeId) == ChimahonAnimeDownloadStatus.Downloaded
    }

    fun containsQueuedOrActiveEpisode(episodeId: Long): Boolean {
        return statusFor(episodeId = episodeId).let { it.isPending || it.isActive }
    }
}

data class ChimahonAnimeDownloadQueueItem(
    val id: String,
    val animeId: Long,
    val episodeId: Long,
    val sourceId: Long = 0L,
    val animeTitle: String,
    val episodeTitle: String,
    val episodeUrl: String = "",
    val episodeNumber: Double = -1.0,
    val sourceName: String? = null,
    val thumbnailUrl: String? = null,
    val summary: String? = null,
    val durationSeconds: Long = 0L,
    val status: ChimahonAnimeDownloadStatus = ChimahonAnimeDownloadStatus.Queued,
    val priority: ChimahonAnimeDownloadPriority = ChimahonAnimeDownloadPriority.Normal,
    val progress: ChimahonAnimeDownloadProgress = ChimahonAnimeDownloadProgress(),
    val queuePosition: Int = 0,
    val addedAtMillis: Long = 0L,
    val updatedAtMillis: Long = 0L,
    val preferredVideoKey: String = "",
    val downloadPath: String? = null,
    val streamable: Boolean = true,
    val local: Boolean = false,
    val downloadedOnDisk: Boolean = status == ChimahonAnimeDownloadStatus.Downloaded,
    val seen: Boolean = false,
    val bookmarked: Boolean = false,
    val filler: Boolean = false,
    val error: ChimahonAnimeDownloadError? = null,
) {
    val isRecognizedEpisodeNumber: Boolean
        get() = episodeNumber >= 0.0
}

data class ChimahonAnimeDownloadProgress(
    val percent: Int? = null,
    val downloadedBytes: Long? = null,
    val totalBytes: Long? = null,
    val speedBytesPerSecond: Long? = null,
    val estimatedRemainingSeconds: Long? = null,
    val message: String? = null,
    val indeterminate: Boolean = false,
) {
    val normalizedPercent: Int?
        get() = percent?.coerceIn(0, 100)

    val fraction: Float
        get() {
            val downloaded = downloadedBytes
            val total = totalBytes
            return when {
                downloaded != null && total != null && total > 0L -> {
                    (downloaded.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                }
                normalizedPercent != null -> normalizedPercent!!.toFloat() / 100f
                else -> 0f
            }
        }

    val hasKnownTotal: Boolean
        get() = totalBytes?.let { it > 0L } == true
}

data class ChimahonAnimeDownloadError(
    val code: ChimahonAnimeDownloadErrorCode = ChimahonAnimeDownloadErrorCode.Unknown,
    val message: String? = null,
    val episodeId: Long? = null,
    val animeId: Long? = null,
    val canRetry: Boolean = true,
)

enum class ChimahonAnimeDownloadErrorCode {
    NotFound,
    SourceUnavailable,
    Network,
    Storage,
    Unsupported,
    PermissionDenied,
    Canceled,
    Unknown,
}

enum class ChimahonAnimeDownloadStatus(val label: String) {
    NotDownloaded("Not downloaded"),
    Queued("Queued"),
    Preparing("Preparing"),
    Downloading("Downloading"),
    Paused("Paused"),
    Downloaded("Downloaded"),
    Failed("Failed"),
    Canceled("Canceled"),
    Deleting("Deleting"),
}

enum class ChimahonAnimeDownloadPriority(val label: String) {
    Low("Low"),
    Normal("Normal"),
    High("High"),
    Next("Play next"),
}

enum class ChimahonAnimeEpisodeDownloadSelection(val limit: Int?) {
    Next1(1),
    Next5(5),
    Next10(10),
    Next25(25),
    Unseen(null),
}

data class ChimahonAnimeDownloadEpisodeRequest(
    val animeId: Long,
    val episodeId: Long,
    val sourceId: Long = 0L,
    val animeTitle: String = "",
    val episodeTitle: String = "",
    val episodeUrl: String = "",
    val episodeNumber: Double = -1.0,
    val thumbnailUrl: String? = null,
    val sourceName: String? = null,
    val durationSeconds: Long = 0L,
    val seen: Boolean = false,
    val bookmarked: Boolean = false,
    val filler: Boolean = false,
    val streamable: Boolean = true,
)

data class ChimahonAnimeDownloadStartRequest(
    val episodes: List<ChimahonAnimeDownloadEpisodeRequest>,
    val startNow: Boolean = false,
    val priority: ChimahonAnimeDownloadPriority = if (startNow) {
        ChimahonAnimeDownloadPriority.Next
    } else {
        ChimahonAnimeDownloadPriority.Normal
    },
    val preferredVideoKey: String = "",
    val addedAtMillis: Long = 0L,
) {
    val episodeIds: List<Long>
        get() = episodes.map { it.episodeId }
}

data class ChimahonAnimeEpisodeDownloadDecisionRequest(
    val anime: ChimahonAnimeEntry,
    val episodes: List<ChimahonAnimeEpisodeEntry>,
    val selection: ChimahonAnimeEpisodeDownloadSelection,
    val downloads: ChimahonAnimeDownloadQueueSnapshot = ChimahonAnimeDownloadQueueSnapshot(),
    val sourceInfo: ChimahonAnimeSourceInfo? = null,
    val sortDescending: Boolean = anime.episodeSortDescending,
    val includeSeen: Boolean = false,
)

data class ChimahonAnimeEpisodeDownloadDecision(
    val animeId: Long,
    val selection: ChimahonAnimeEpisodeDownloadSelection,
    val episodes: List<ChimahonAnimeEpisodeEntry>,
    val skippedEpisodeIds: Set<Long> = emptySet(),
    val reason: ChimahonAnimeEpisodeDownloadDecisionReason = if (episodes.isEmpty()) {
        ChimahonAnimeEpisodeDownloadDecisionReason.NoEpisodes
    } else {
        ChimahonAnimeEpisodeDownloadDecisionReason.Selected
    },
) {
    val episodeIds: List<Long>
        get() = episodes.map { it.id }

    val hasEpisodes: Boolean
        get() = episodes.isNotEmpty()
}

enum class ChimahonAnimeEpisodeDownloadDecisionReason {
    Selected,
    NoEpisodes,
    AlreadyDownloadedOrQueued,
}

fun ChimahonAnimeDownloadSnapshot.toAnimeDownloadQueueSnapshot(): ChimahonAnimeDownloadQueueSnapshot {
    return ChimahonAnimeDownloadQueueSnapshot(
        downloadedEpisodeIds = downloadedEpisodeIds,
        queuedEpisodeIds = queuedEpisodeIds,
        downloadingEpisodeProgress = downloadingEpisodeProgress,
        erroredEpisodeIds = erroredEpisodeIds,
        downloadedAnimeIds = downloadedAnimeIds,
        localAnimeIds = localAnimeIds,
    )
}

fun ChimahonAnimeEpisodeDownloadDecisionRequest.decideEpisodesForDownload(): ChimahonAnimeEpisodeDownloadDecision {
    val sortedEpisodes = episodes.sortedForAnimeDownload(sortDescending = sortDescending)
    val candidateEpisodes = sortedEpisodes.filter { episode ->
        (includeSeen || !episode.seen) &&
            !downloads.containsDownloadedEpisode(episodeId = episode.id, animeId = anime.id) &&
            !downloads.containsQueuedOrActiveEpisode(episode.id)
    }
    val selectedEpisodes = selection.limit
        ?.let { limit -> candidateEpisodes.take(limit) }
        ?: candidateEpisodes
    val skippedIds = sortedEpisodes.mapTo(mutableSetOf()) { it.id } - selectedEpisodes.mapTo(mutableSetOf()) { it.id }
    val reason = when {
        selectedEpisodes.isNotEmpty() -> ChimahonAnimeEpisodeDownloadDecisionReason.Selected
        sortedEpisodes.isEmpty() -> ChimahonAnimeEpisodeDownloadDecisionReason.NoEpisodes
        else -> ChimahonAnimeEpisodeDownloadDecisionReason.AlreadyDownloadedOrQueued
    }
    return ChimahonAnimeEpisodeDownloadDecision(
        animeId = anime.id,
        selection = selection,
        episodes = selectedEpisodes,
        skippedEpisodeIds = skippedIds,
        reason = reason,
    )
}

fun ChimahonAnimeEpisodeDownloadDecision.toStartDownloadRequest(
    anime: ChimahonAnimeEntry,
    sourceInfo: ChimahonAnimeSourceInfo? = null,
    startNow: Boolean = false,
    priority: ChimahonAnimeDownloadPriority = if (startNow) {
        ChimahonAnimeDownloadPriority.Next
    } else {
        ChimahonAnimeDownloadPriority.Normal
    },
    addedAtMillis: Long = 0L,
): ChimahonAnimeDownloadStartRequest {
    return ChimahonAnimeDownloadStartRequest(
        episodes = episodes.map { episode ->
            episode.toAnimeDownloadEpisodeRequest(
                anime = anime,
                sourceInfo = sourceInfo,
            )
        },
        startNow = startNow,
        priority = priority,
        addedAtMillis = addedAtMillis,
    )
}

fun ChimahonAnimeEpisodeEntry.toAnimeDownloadEpisodeRequest(
    anime: ChimahonAnimeEntry,
    sourceInfo: ChimahonAnimeSourceInfo? = null,
): ChimahonAnimeDownloadEpisodeRequest {
    return ChimahonAnimeDownloadEpisodeRequest(
        animeId = anime.id,
        episodeId = id,
        sourceId = anime.sourceId,
        animeTitle = anime.title,
        episodeTitle = name,
        episodeUrl = url,
        episodeNumber = episodeNumber,
        thumbnailUrl = previewUrl ?: anime.thumbnailUrl,
        sourceName = sourceInfo?.displayName,
        durationSeconds = totalSeconds,
        seen = seen,
        bookmarked = bookmark,
        filler = fillermark,
        streamable = !sourceInfo.isLocalSource(),
    )
}

fun ChimahonAnimeDownloadEpisodeRequest.toQueueItem(
    id: String? = null,
    status: ChimahonAnimeDownloadStatus = ChimahonAnimeDownloadStatus.Queued,
    priority: ChimahonAnimeDownloadPriority = ChimahonAnimeDownloadPriority.Normal,
    progress: ChimahonAnimeDownloadProgress = ChimahonAnimeDownloadProgress(),
    queuePosition: Int = 0,
    addedAtMillis: Long = 0L,
): ChimahonAnimeDownloadQueueItem {
    return ChimahonAnimeDownloadQueueItem(
        id = id ?: "anime-$animeId-episode-$episodeId",
        animeId = animeId,
        episodeId = episodeId,
        sourceId = sourceId,
        animeTitle = animeTitle,
        episodeTitle = episodeTitle,
        episodeUrl = episodeUrl,
        episodeNumber = episodeNumber,
        sourceName = sourceName,
        thumbnailUrl = thumbnailUrl,
        durationSeconds = durationSeconds,
        status = status,
        priority = priority,
        progress = progress,
        queuePosition = queuePosition,
        addedAtMillis = addedAtMillis,
        streamable = streamable,
        seen = seen,
        bookmarked = bookmarked,
        filler = filler,
    )
}

val ChimahonAnimeDownloadStatus.isActive: Boolean
    get() = this == ChimahonAnimeDownloadStatus.Preparing ||
        this == ChimahonAnimeDownloadStatus.Downloading ||
        this == ChimahonAnimeDownloadStatus.Deleting

val ChimahonAnimeDownloadStatus.isPending: Boolean
    get() = this == ChimahonAnimeDownloadStatus.Queued ||
        this == ChimahonAnimeDownloadStatus.Preparing ||
        this == ChimahonAnimeDownloadStatus.Paused

val ChimahonAnimeDownloadStatus.isTerminal: Boolean
    get() = this == ChimahonAnimeDownloadStatus.Downloaded ||
        this == ChimahonAnimeDownloadStatus.Canceled

private fun List<ChimahonAnimeEpisodeEntry>.sortedForAnimeDownload(
    sortDescending: Boolean,
): List<ChimahonAnimeEpisodeEntry> {
    val sorted = sortedWith(
        compareBy<ChimahonAnimeEpisodeEntry> { it.sourceOrder }
            .thenBy { if (it.isRecognizedNumber) it.episodeNumber else Double.MAX_VALUE }
            .thenBy { it.dateUpload }
            .thenBy { it.name.lowercase() },
    )
    return if (sortDescending) sorted.asReversed() else sorted
}

private fun ChimahonAnimeSourceInfo?.isLocalSource(): Boolean {
    return this?.isLocal == true
}
