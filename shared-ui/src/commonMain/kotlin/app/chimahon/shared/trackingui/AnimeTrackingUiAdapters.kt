package app.chimahon.shared.trackingui

import app.chimahon.shared.anime.ChimahonAnimeTrackStatus
import app.chimahon.shared.anime.ChimahonAnimeTrackerAccount
import app.chimahon.shared.anime.ChimahonAnimeTrackerAccountStatus
import app.chimahon.shared.anime.ChimahonAnimeTrackingConflictStrategy
import app.chimahon.shared.anime.ChimahonAnimeTrackingDate
import app.chimahon.shared.anime.ChimahonAnimeTrackingEntry
import app.chimahon.shared.anime.ChimahonAnimeTrackingEpisodeSyncStatus
import app.chimahon.shared.anime.ChimahonAnimeTrackingError
import app.chimahon.shared.anime.ChimahonAnimeTrackingLoadResult
import app.chimahon.shared.anime.ChimahonAnimeTrackingSettings
import app.chimahon.shared.anime.ChimahonAnimeTrackingSyncStatus

fun ChimahonAnimeTrackingLoadResult.toAnimeTrackingUiState(
    selectedMediaKind: ChimahonTrackingMediaKind? = ChimahonTrackingMediaKind.Anime,
    query: String = "",
    loading: Boolean = false,
    formatTimestamp: (Long) -> String? = { null },
): ChimahonTrackingUiState {
    return ChimahonTrackingUiState(
        title = "Anime tracking",
        subtitle = if (syncing) "Syncing anime trackers" else "${entries.size} tracked anime",
        accounts = accounts.map { it.toTrackingAccountUiModel(formatTimestamp) },
        entries = entries.map { it.toTrackingEntryUiModel(formatTimestamp) },
        selectedMediaKind = selectedMediaKind,
        query = query,
        loading = loading,
        syncing = syncing,
        errorMessage = errors.toAnimeTrackingErrorMessage(),
    )
}

fun ChimahonAnimeTrackerAccount.toTrackingAccountUiModel(
    formatTimestamp: (Long) -> String? = { null },
): ChimahonTrackerAccountUiModel {
    return ChimahonTrackerAccountUiModel(
        id = trackerId,
        name = trackerName,
        username = username,
        avatarUrl = avatarUrl,
        accountStatus = accountStatus.toTrackingAccountStatus(),
        syncStatus = syncStatus.toTrackerSyncStatus(),
        supportedMediaKinds = setOf(ChimahonTrackingMediaKind.Anime),
        isDefault = isDefault,
        privateTrackingEnabled = privateTrackingEnabled,
        supportsPrivateTracking = capabilities.supportsPrivateTracking,
        lastSyncLabel = lastSyncAtMillis.takeIf { it > 0L }?.let(formatTimestamp),
        errorMessage = errorMessage,
        pendingCount = pendingChanges,
    )
}

fun ChimahonAnimeTrackingEntry.toTrackingEntryUiModel(
    formatTimestamp: (Long) -> String? = { null },
): ChimahonTrackingEntryUiModel {
    val syncMessage = errorMessage ?: episodeSync.errorMessage ?: conflictSummary()
    return ChimahonTrackingEntryUiModel(
        id = key.stableId,
        trackerId = trackerId,
        trackerName = trackerName,
        mediaKind = ChimahonTrackingMediaKind.Anime,
        title = title,
        remoteTitle = remoteTitle,
        coverUrl = coverUrl,
        remoteUrl = remoteUrl,
        status = status.toTrackingStatus(),
        score = score.normalizedValue,
        scoreMax = score.scale.safeMax,
        scoreStep = score.scale.safeStep,
        progress = progress.safeWatchedEpisodes,
        totalProgress = progress.safeTotalEpisodes,
        startedDateLabel = startedDate?.toTrackingDateLabel(),
        completedDateLabel = completedDate?.toTrackingDateLabel(),
        lastSyncedLabel = lastSyncedAtMillis.takeIf { it > 0L }?.let(formatTimestamp),
        nextSyncLabel = nextSyncAtMillis.takeIf { it > 0L }?.let(formatTimestamp) ?: episodeSync.syncLabel,
        privateTracking = privateTracking,
        syncStatus = syncStatus.toTrackerSyncStatus(),
        errorMessage = syncMessage,
    )
}

fun ChimahonAnimeTrackingSettings.toTrackingSettingsUiState(
    lastGlobalSyncLabel: String? = null,
    pendingChanges: Int = 0,
    errorMessage: String? = null,
): ChimahonTrackingSettingsUiState {
    return ChimahonTrackingSettingsUiState(
        trackOnLibraryAdd = trackOnLibraryAdd,
        autoSyncEnabled = autoSyncEnabled,
        syncOnProgressChange = syncOnEpisodeSeen || syncOnPlayerProgress,
        syncOnCompletion = syncCompletedEntries,
        privateTrackingDefault = privateTrackingDefault,
        syncOnlyOnWifi = syncOnlyOnWifi,
        pauseInIncognito = pauseInIncognito,
        includeManga = false,
        includeAnime = true,
        includeLightNovels = false,
        defaultAnimeStatus = defaultStatus.toTrackingStatus(),
        conflictStrategy = conflictStrategy.toTrackingConflictStrategy(),
        lastGlobalSyncLabel = lastGlobalSyncLabel,
        pendingChanges = pendingChanges,
        errorMessage = errorMessage,
    )
}

fun ChimahonAnimeTrackStatus.toTrackingStatus(): ChimahonTrackingStatus {
    return when (this) {
        ChimahonAnimeTrackStatus.NotTracked -> ChimahonTrackingStatus.NotTracked
        ChimahonAnimeTrackStatus.Watching -> ChimahonTrackingStatus.Watching
        ChimahonAnimeTrackStatus.Rewatching -> ChimahonTrackingStatus.ReWatching
        ChimahonAnimeTrackStatus.Completed -> ChimahonTrackingStatus.Completed
        ChimahonAnimeTrackStatus.OnHold -> ChimahonTrackingStatus.OnHold
        ChimahonAnimeTrackStatus.Dropped -> ChimahonTrackingStatus.Dropped
        ChimahonAnimeTrackStatus.PlanToWatch -> ChimahonTrackingStatus.PlanToWatch
    }
}

fun ChimahonTrackingStatus.toAnimeTrackStatus(): ChimahonAnimeTrackStatus {
    return when (this) {
        ChimahonTrackingStatus.NotTracked -> ChimahonAnimeTrackStatus.NotTracked
        ChimahonTrackingStatus.Reading,
        ChimahonTrackingStatus.Watching,
        -> ChimahonAnimeTrackStatus.Watching
        ChimahonTrackingStatus.ReReading,
        ChimahonTrackingStatus.ReWatching,
        -> ChimahonAnimeTrackStatus.Rewatching
        ChimahonTrackingStatus.Completed -> ChimahonAnimeTrackStatus.Completed
        ChimahonTrackingStatus.OnHold,
        ChimahonTrackingStatus.Paused,
        -> ChimahonAnimeTrackStatus.OnHold
        ChimahonTrackingStatus.Dropped -> ChimahonAnimeTrackStatus.Dropped
        ChimahonTrackingStatus.PlanToRead,
        ChimahonTrackingStatus.PlanToWatch,
        -> ChimahonAnimeTrackStatus.PlanToWatch
    }
}

fun ChimahonAnimeTrackingSyncStatus.toTrackerSyncStatus(): ChimahonTrackerSyncStatus {
    return when (this) {
        ChimahonAnimeTrackingSyncStatus.Idle -> ChimahonTrackerSyncStatus.Idle
        ChimahonAnimeTrackingSyncStatus.Pending -> ChimahonTrackerSyncStatus.Pending
        ChimahonAnimeTrackingSyncStatus.Syncing -> ChimahonTrackerSyncStatus.Syncing
        ChimahonAnimeTrackingSyncStatus.Synced -> ChimahonTrackerSyncStatus.Synced
        ChimahonAnimeTrackingSyncStatus.Failed -> ChimahonTrackerSyncStatus.Failed
        ChimahonAnimeTrackingSyncStatus.Disabled -> ChimahonTrackerSyncStatus.Disabled
        ChimahonAnimeTrackingSyncStatus.Conflict -> ChimahonTrackerSyncStatus.Failed
    }
}

private fun ChimahonAnimeTrackerAccountStatus.toTrackingAccountStatus(): ChimahonTrackerAccountStatus {
    return when (this) {
        ChimahonAnimeTrackerAccountStatus.LoggedOut -> ChimahonTrackerAccountStatus.LoggedOut
        ChimahonAnimeTrackerAccountStatus.LoggedIn -> ChimahonTrackerAccountStatus.LoggedIn
        ChimahonAnimeTrackerAccountStatus.Expired -> ChimahonTrackerAccountStatus.Expired
        ChimahonAnimeTrackerAccountStatus.Syncing -> ChimahonTrackerAccountStatus.Syncing
        ChimahonAnimeTrackerAccountStatus.Error -> ChimahonTrackerAccountStatus.Error
    }
}

private fun ChimahonAnimeTrackingConflictStrategy.toTrackingConflictStrategy(): ChimahonTrackingConflictStrategy {
    return when (this) {
        ChimahonAnimeTrackingConflictStrategy.NewestChangeWins -> ChimahonTrackingConflictStrategy.NewestChangeWins
        ChimahonAnimeTrackingConflictStrategy.LocalProgressWins -> ChimahonTrackingConflictStrategy.LocalProgressWins
        ChimahonAnimeTrackingConflictStrategy.TrackerProgressWins -> ChimahonTrackingConflictStrategy.TrackerProgressWins
        ChimahonAnimeTrackingConflictStrategy.HighestProgressWins -> ChimahonTrackingConflictStrategy.LocalProgressWins
        ChimahonAnimeTrackingConflictStrategy.AskEveryTime -> ChimahonTrackingConflictStrategy.AskEveryTime
    }
}

private fun ChimahonAnimeTrackingEntry.conflictSummary(): String? {
    return when {
        conflictIds.isNotEmpty() -> "${conflictIds.size} tracking conflict(s) need review"
        episodeSync.status == ChimahonAnimeTrackingEpisodeSyncStatus.Conflict -> "Episode sync conflict needs review"
        else -> null
    }
}

private fun ChimahonAnimeTrackingDate.toTrackingDateLabel(): String {
    return buildString {
        append(year.toString().padStart(4, '0'))
        month?.let { append("-").append(it.toString().padStart(2, '0')) }
        day?.let { append("-").append(it.toString().padStart(2, '0')) }
    }
}

private fun List<ChimahonAnimeTrackingError>.toAnimeTrackingErrorMessage(): String? {
    return firstOrNull()?.let { error -> error.message ?: error.code.name }
}
