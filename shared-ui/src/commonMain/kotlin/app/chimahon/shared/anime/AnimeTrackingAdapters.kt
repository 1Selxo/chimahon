package app.chimahon.shared.anime

import kotlin.math.floor
import kotlin.math.max

fun ChimahonAnimeLibraryEntry.toAnimeTrackingLocalSnapshot(
    sourceId: Long = anime.sourceId,
    sourceName: String? = this.sourceName,
    updatedAtMillis: Long = max(anime.lastModifiedAt, lastSeen),
): ChimahonAnimeTrackingLocalSnapshot {
    val localEpisodes = episodes.map { episode -> episode.toAnimeTrackingLocalEpisode() }
    val watchedCount = seenEpisodeCount.takeIf { it > 0 } ?: localEpisodes.count { it.seen }
    val totalCount = totalEpisodeCount.takeIf { it > 0 } ?: localEpisodes.size
    return ChimahonAnimeTrackingLocalSnapshot(
        animeId = id,
        sourceId = sourceId,
        title = anime.title.ifBlank { "Untitled anime" },
        alternateTitles = listOfNotNull(anime.author, anime.artist).filter(String::isNotBlank),
        coverUrl = anime.thumbnailUrl,
        sourceUrl = anime.url.takeIf(String::isNotBlank),
        sourceName = sourceName,
        status = animeTrackingStatusForProgress(
            watchedEpisodes = watchedCount,
            totalEpisodes = totalCount,
            started = hasStarted,
        ),
        progress = ChimahonAnimeTrackingProgress(
            watchedEpisodes = watchedCount,
            totalEpisodes = totalCount.takeIf { it > 0 },
            lastEpisodeId = localEpisodes
                .filter { it.seen || it.positionSeconds > 0L }
                .maxByOrNull { it.watchedAtMillis }?.episodeId,
            lastEpisodeNumber = localEpisodes
                .filter { it.seen || it.positionSeconds > 0L }
                .maxByOrNull { it.watchedAtMillis }?.episodeNumber,
            updatedAtMillis = updatedAtMillis,
        ),
        episodes = localEpisodes,
        categoryIds = categoryIds,
        updatedAtMillis = updatedAtMillis,
    )
}

fun ChimahonAnimeEpisodeEntry.toAnimeTrackingLocalEpisode(): ChimahonAnimeTrackingLocalEpisode {
    return ChimahonAnimeTrackingLocalEpisode(
        episodeId = id,
        title = name,
        episodeNumber = episodeNumber.takeIf { it >= 0.0 },
        seen = seen,
        positionSeconds = lastSecondSeen,
        durationSeconds = totalSeconds,
        watchedAtMillis = lastModifiedAt.takeIf { it > 0L } ?: dateFetch,
    )
}

fun ChimahonAnimeTrackingLocalSnapshot.toAnimeTrackingLocalEntry(
    trackerId: String = CHIMAHON_ANIME_TRACKING_LOCAL_TRACKER_ID,
    trackerName: String = sourceName?.takeIf(String::isNotBlank) ?: "Local anime",
): ChimahonAnimeTrackingEntry {
    return ChimahonAnimeTrackingEntry(
        key = ChimahonAnimeTrackingEntryKey(
            trackerId = trackerId,
            animeId = animeId,
        ),
        trackerName = trackerName,
        title = title,
        remoteTitle = title,
        coverUrl = coverUrl,
        remoteUrl = sourceUrl,
        status = status,
        progress = progress,
        syncStatus = ChimahonAnimeTrackingSyncStatus.Idle,
        episodeSync = toAnimeTrackingEpisodeSyncState(),
        localUpdatedAtMillis = updatedAtMillis,
        lastSyncedAtMillis = updatedAtMillis,
    )
}

fun ChimahonAnimeTrackingLocalSnapshot.toAnimeTrackingSearchRequest(
    trackerId: String,
    query: String = title,
    limit: Int = 25,
): ChimahonAnimeTrackingRemoteSearchRequest {
    return ChimahonAnimeTrackingRemoteSearchRequest(
        trackerId = trackerId,
        query = query,
        anime = this,
        limit = limit,
    )
}

fun ChimahonAnimeTrackingLocalSnapshot.toAnimeTrackingLinkRequest(
    trackerId: String,
    remoteId: String,
    privateTracking: Boolean = false,
    syncEpisodes: Boolean = true,
    requestedAtMillis: Long = updatedAtMillis,
): ChimahonAnimeTrackingLinkRequest {
    return ChimahonAnimeTrackingLinkRequest(
        trackerId = trackerId,
        remoteId = remoteId,
        anime = this,
        initialStatus = status,
        initialProgress = progress,
        privateTracking = privateTracking,
        syncEpisodes = syncEpisodes,
        requestedAtMillis = requestedAtMillis,
    )
}

fun ChimahonAnimeEpisodeProgressUpdate.toAnimeTrackingPlayerProgressEvent(
    episodeNumber: Double? = null,
    episodeTitle: String? = null,
    playbackSource: ChimahonAnimePlaybackSource = ChimahonAnimePlaybackSource.Stream,
    paused: Boolean = false,
    incognito: Boolean = false,
): ChimahonAnimeTrackingPlayerProgressEvent {
    return ChimahonAnimeTrackingPlayerProgressEvent(
        animeId = animeId,
        episodeId = episodeId,
        episodeTitle = episodeTitle,
        episodeNumber = episodeNumber,
        positionSeconds = positionSeconds,
        durationSeconds = durationSeconds,
        watchedAtMillis = watchedAtMillis,
        sessionWatchDurationMillis = sessionWatchDurationMillis,
        playbackSource = playbackSource,
        paused = paused,
        completed = markSeen,
        incognito = incognito,
    )
}

fun ChimahonAnimePlayerSession.toAnimeTrackingPlayerProgressEvent(
    watchedAtMillis: Long,
    sessionWatchDurationMillis: Long = 0L,
    incognito: Boolean = false,
): ChimahonAnimeTrackingPlayerProgressEvent {
    return ChimahonAnimeTrackingPlayerProgressEvent(
        animeId = anime.id,
        episodeId = episode.id,
        episodeTitle = episode.name,
        episodeNumber = episode.episodeNumber.takeIf { episode.isRecognizedNumber },
        positionSeconds = positionSeconds,
        durationSeconds = durationSeconds.takeIf { it > 0L } ?: episode.totalSeconds,
        watchedAtMillis = watchedAtMillis,
        sessionWatchDurationMillis = sessionWatchDurationMillis,
        playbackSource = playbackSource,
        paused = paused,
        completed = episode.seen,
        incognito = incognito,
    )
}

fun ChimahonAnimeTrackingEntry.toAnimeTrackingUpdateRequest(
    event: ChimahonAnimeTrackingPlayerProgressEvent,
    policy: ChimahonAnimeTrackingPlayerProgressPolicy = ChimahonAnimeTrackingPlayerProgressPolicy(),
): ChimahonAnimeTrackingUpdateRequest? {
    if (!event.shouldQueueAnimeTrackingSync(policy)) return null

    val watchedEpisode = event.toRemoteEpisodeNumber()
    val watchedEpisodes = max(progress.safeWatchedEpisodes, watchedEpisode ?: progress.safeWatchedEpisodes)
    val totalEpisodes = progress.safeTotalEpisodes
    val nextStatus = when {
        totalEpisodes != null && watchedEpisodes >= totalEpisodes && policy.updateStatusWhenCompleted ->
            ChimahonAnimeTrackStatus.Completed
        status == ChimahonAnimeTrackStatus.NotTracked -> ChimahonAnimeTrackStatus.Watching
        status == ChimahonAnimeTrackStatus.PlanToWatch && policy.updateStatusWhenStarted -> ChimahonAnimeTrackStatus.Watching
        else -> status
    }
    return ChimahonAnimeTrackingUpdateRequest(
        key = key,
        status = nextStatus.takeIf { it != status },
        progress = progress.copy(
            watchedEpisodes = watchedEpisodes,
            lastEpisodeId = event.episodeId,
            lastEpisodeNumber = event.episodeNumber,
            updatedAtMillis = event.watchedAtMillis,
        ),
        changedAtMillis = event.watchedAtMillis,
        reason = ChimahonAnimeTrackingUpdateReason.PlayerProgress,
    )
}

fun ChimahonAnimeTrackingEntry.toAnimeTrackingEpisodeSyncRequest(
    event: ChimahonAnimeTrackingPlayerProgressEvent,
    policy: ChimahonAnimeTrackingPlayerProgressPolicy = ChimahonAnimeTrackingPlayerProgressPolicy(),
): ChimahonAnimeTrackingEpisodeSyncRequest? {
    if (!event.shouldQueueAnimeTrackingSync(policy)) return null

    val item = event.toAnimeTrackingEpisodeSyncItem(policy) ?: return null
    return ChimahonAnimeTrackingEpisodeSyncRequest(
        key = key,
        items = listOf(item),
        trigger = item.trigger,
        requestedAtMillis = event.watchedAtMillis,
    )
}

fun ChimahonAnimeTrackingLocalSnapshot.toAnimeTrackingEpisodeSyncRequest(
    key: ChimahonAnimeTrackingEntryKey,
    trigger: ChimahonAnimeTrackingEpisodeSyncTrigger = ChimahonAnimeTrackingEpisodeSyncTrigger.BackgroundSync,
    requestedAtMillis: Long = updatedAtMillis,
): ChimahonAnimeTrackingEpisodeSyncRequest {
    val items = episodes
        .filter { it.seen || it.positionSeconds > 0L }
        .map { episode ->
            ChimahonAnimeTrackingEpisodeSyncItem(
                episodeId = episode.episodeId,
                remoteEpisodeNumber = episode.episodeNumber?.toAnimeTrackingEpisodeIndex(),
                title = episode.title,
                seen = episode.seen,
                positionSeconds = episode.positionSeconds,
                durationSeconds = episode.durationSeconds,
                watchedAtMillis = episode.watchedAtMillis,
                trigger = trigger,
            )
        }
    return ChimahonAnimeTrackingEpisodeSyncRequest(
        key = key,
        items = items,
        trigger = trigger,
        requestedAtMillis = requestedAtMillis,
    )
}

fun ChimahonAnimeTrackingPlayerProgressEvent.toAnimeTrackingEpisodeSyncItem(
    policy: ChimahonAnimeTrackingPlayerProgressPolicy = ChimahonAnimeTrackingPlayerProgressPolicy(),
): ChimahonAnimeTrackingEpisodeSyncItem? {
    if (!shouldQueueAnimeTrackingSync(policy)) return null

    return ChimahonAnimeTrackingEpisodeSyncItem(
        episodeId = episodeId,
        remoteEpisodeNumber = toRemoteEpisodeNumber(),
        title = episodeTitle,
        seen = shouldMarkSeen(policy),
        positionSeconds = positionSeconds,
        durationSeconds = durationSeconds,
        watchedAtMillis = watchedAtMillis,
        trigger = if (completed || shouldMarkSeen(policy)) {
            ChimahonAnimeTrackingEpisodeSyncTrigger.PlaybackCompleted
        } else {
            ChimahonAnimeTrackingEpisodeSyncTrigger.PlayerProgress
        },
    )
}

fun ChimahonAnimeTrackingPlayerProgressEvent.shouldQueueAnimeTrackingSync(
    policy: ChimahonAnimeTrackingPlayerProgressPolicy = ChimahonAnimeTrackingPlayerProgressPolicy(),
): Boolean {
    if (!policy.enabled) return false
    if (incognito && !policy.queueWhileIncognito) return false
    if (policy.syncOnlyCompletedEpisodes && !shouldMarkSeen(policy)) return false
    return positionSeconds >= policy.minimumPositionSeconds || completed
}

fun ChimahonAnimeTrackingConflict.resolveUsingStrategy(
    strategy: ChimahonAnimeTrackingConflictStrategy = this.strategy,
    resolvedAtMillis: Long = detectedAtMillis,
): ChimahonAnimeTrackingConflictResolutionRequest {
    val choice = when (strategy) {
        ChimahonAnimeTrackingConflictStrategy.NewestChangeWins -> newestConflictChoice()
        ChimahonAnimeTrackingConflictStrategy.LocalProgressWins -> ChimahonAnimeTrackingConflictChoice.UseLocal
        ChimahonAnimeTrackingConflictStrategy.TrackerProgressWins -> ChimahonAnimeTrackingConflictChoice.UseTracker
        ChimahonAnimeTrackingConflictStrategy.HighestProgressWins -> highestProgressConflictChoice()
        ChimahonAnimeTrackingConflictStrategy.AskEveryTime -> ChimahonAnimeTrackingConflictChoice.Ignore
    }
    return ChimahonAnimeTrackingConflictResolutionRequest(
        conflictId = id,
        key = key,
        choice = choice,
        resolvedAtMillis = resolvedAtMillis,
    )
}

fun ChimahonAnimeTrackingEntry.findConflictWith(
    remote: ChimahonAnimeTrackingEntry,
    detectedAtMillis: Long = max(localUpdatedAtMillis, remote.remoteUpdatedAtMillis),
    strategy: ChimahonAnimeTrackingConflictStrategy = ChimahonAnimeTrackingConflictStrategy.NewestChangeWins,
): ChimahonAnimeTrackingConflict? {
    val changes = buildList {
        if (status != remote.status) {
            add(
                ChimahonAnimeTrackingConflictFieldChange(
                    field = ChimahonAnimeTrackingConflictField.Status,
                    localValueLabel = status.label,
                    remoteValueLabel = remote.status.label,
                    localUpdatedAtMillis = localUpdatedAtMillis,
                    remoteUpdatedAtMillis = remote.remoteUpdatedAtMillis,
                ),
            )
        }
        if (progress.safeWatchedEpisodes != remote.progress.safeWatchedEpisodes) {
            add(
                ChimahonAnimeTrackingConflictFieldChange(
                    field = ChimahonAnimeTrackingConflictField.Progress,
                    localValueLabel = progress.label,
                    remoteValueLabel = remote.progress.label,
                    localUpdatedAtMillis = progress.updatedAtMillis,
                    remoteUpdatedAtMillis = remote.progress.updatedAtMillis,
                ),
            )
        }
        if (score.normalizedValue != remote.score.normalizedValue) {
            add(
                ChimahonAnimeTrackingConflictFieldChange(
                    field = ChimahonAnimeTrackingConflictField.Score,
                    localValueLabel = score.label,
                    remoteValueLabel = remote.score.label,
                    localUpdatedAtMillis = score.updatedAtMillis,
                    remoteUpdatedAtMillis = remote.score.updatedAtMillis,
                ),
            )
        }
    }
    if (changes.isEmpty()) return null

    return ChimahonAnimeTrackingConflict(
        id = listOf("anime-conflict", key.trackerId, key.animeId.toString(), key.remoteId).joinToString(":"),
        key = key,
        localEntry = this,
        remoteEntry = remote,
        fields = changes,
        detectedAtMillis = detectedAtMillis,
        strategy = strategy,
    )
}

fun animeTrackingStatusForProgress(
    watchedEpisodes: Int,
    totalEpisodes: Int,
    started: Boolean = watchedEpisodes > 0,
): ChimahonAnimeTrackStatus {
    return when {
        totalEpisodes > 0 && watchedEpisodes >= totalEpisodes -> ChimahonAnimeTrackStatus.Completed
        started || watchedEpisodes > 0 -> ChimahonAnimeTrackStatus.Watching
        else -> ChimahonAnimeTrackStatus.PlanToWatch
    }
}

private fun ChimahonAnimeTrackingLocalSnapshot.toAnimeTrackingEpisodeSyncState(): ChimahonAnimeTrackingEpisodeSyncState {
    val syncedIds = episodes.filter { it.seen }.map { it.episodeId }.toSet()
    val lastSyncedEpisode = episodes
        .filter { it.seen }
        .mapNotNull { it.episodeNumber?.toAnimeTrackingEpisodeIndex() }
        .maxOrNull()
        ?: 0
    return ChimahonAnimeTrackingEpisodeSyncState(
        status = if (syncedIds.isEmpty()) {
            ChimahonAnimeTrackingEpisodeSyncStatus.Idle
        } else {
            ChimahonAnimeTrackingEpisodeSyncStatus.Synced
        },
        syncedEpisodeIds = syncedIds,
        lastSyncedEpisodeNumber = lastSyncedEpisode,
        lastSyncedAtMillis = episodes.filter { it.seen }.maxOfOrNull { it.watchedAtMillis } ?: 0L,
    )
}

private fun ChimahonAnimeTrackingPlayerProgressEvent.toRemoteEpisodeNumber(): Int? {
    return episodeNumber?.toAnimeTrackingEpisodeIndex()
}

private fun Double.toAnimeTrackingEpisodeIndex(): Int? {
    if (this < 0.0) return null
    val floored = floor(this).toInt()
    return floored.takeIf { it > 0 }
}

private fun ChimahonAnimeTrackingConflict.newestConflictChoice(): ChimahonAnimeTrackingConflictChoice {
    val newestLocal = fields.maxOfOrNull { it.localUpdatedAtMillis } ?: 0L
    val newestRemote = fields.maxOfOrNull { it.remoteUpdatedAtMillis } ?: 0L
    return if (newestLocal >= newestRemote) {
        ChimahonAnimeTrackingConflictChoice.UseLocal
    } else {
        ChimahonAnimeTrackingConflictChoice.UseTracker
    }
}

private fun ChimahonAnimeTrackingConflict.highestProgressConflictChoice(): ChimahonAnimeTrackingConflictChoice {
    val localProgress = localEntry?.progress?.safeWatchedEpisodes ?: 0
    val remoteProgress = remoteEntry?.progress?.safeWatchedEpisodes ?: 0
    return if (localProgress >= remoteProgress) {
        ChimahonAnimeTrackingConflictChoice.UseLocal
    } else {
        ChimahonAnimeTrackingConflictChoice.UseTracker
    }
}
