package app.chimahon.shared.anime

const val CHIMAHON_ANIME_TRACKING_LOCAL_TRACKER_ID: String = "local-anime"

data class ChimahonAnimeTrackingError(
    val code: ChimahonAnimeTrackingErrorCode,
    val message: String? = null,
    val trackerId: String? = null,
    val animeId: Long? = null,
    val remoteId: String? = null,
)

enum class ChimahonAnimeTrackingErrorCode {
    NotConfigured,
    LoggedOut,
    TokenExpired,
    NotFound,
    Network,
    RateLimited,
    Conflict,
    Validation,
    Storage,
    Unsupported,
    Unknown,
}

data class ChimahonAnimeTrackingEntryKey(
    val trackerId: String,
    val animeId: Long,
    val remoteId: String = "",
) {
    val stableId: String
        get() = listOf(
            "anime-track",
            trackerId.ifBlank { CHIMAHON_ANIME_TRACKING_LOCAL_TRACKER_ID },
            animeId.toString(),
            remoteId.ifBlank { "local" },
        ).joinToString(":")
}

data class ChimahonAnimeTrackerAccount(
    val trackerId: String,
    val trackerName: String,
    val userId: String? = null,
    val username: String? = null,
    val avatarUrl: String? = null,
    val accountStatus: ChimahonAnimeTrackerAccountStatus = ChimahonAnimeTrackerAccountStatus.LoggedOut,
    val syncStatus: ChimahonAnimeTrackingSyncStatus = ChimahonAnimeTrackingSyncStatus.Idle,
    val capabilities: ChimahonAnimeTrackingCapabilities = ChimahonAnimeTrackingCapabilities(),
    val isDefault: Boolean = false,
    val privateTrackingEnabled: Boolean = false,
    val lastSyncAtMillis: Long = 0L,
    val pendingChanges: Int = 0,
    val errorMessage: String? = null,
) {
    val displayName: String
        get() = username?.takeIf(String::isNotBlank) ?: accountStatus.label
}

enum class ChimahonAnimeTrackerAccountStatus(val label: String) {
    LoggedOut("Not logged in"),
    LoggedIn("Logged in"),
    Expired("Login expired"),
    Syncing("Syncing"),
    Error("Error"),
    ;

    val isSignedIn: Boolean
        get() = this == LoggedIn || this == Syncing
}

data class ChimahonAnimeTrackingCapabilities(
    val supportsAnime: Boolean = true,
    val supportsSearch: Boolean = true,
    val supportsStatus: Boolean = true,
    val supportsProgress: Boolean = true,
    val supportsScore: Boolean = true,
    val supportsPrivateTracking: Boolean = false,
    val supportsEpisodeSync: Boolean = true,
    val supportsRewatching: Boolean = true,
    val supportsStartFinishDates: Boolean = true,
    val supportsPlayerProgress: Boolean = true,
    val scoreScale: ChimahonAnimeTrackingScoreScale = ChimahonAnimeTrackingScoreScale(),
    val supportedStatuses: Set<ChimahonAnimeTrackStatus> = ChimahonAnimeTrackStatus.defaultRemoteStatuses,
)

enum class ChimahonAnimeTrackingSyncStatus(val label: String) {
    Idle("Idle"),
    Pending("Pending"),
    Syncing("Syncing"),
    Synced("Synced"),
    Failed("Failed"),
    Disabled("Disabled"),
    Conflict("Conflict"),
}

enum class ChimahonAnimeTrackStatus(val label: String) {
    NotTracked("Not tracked"),
    Watching("Watching"),
    Rewatching("Rewatching"),
    Completed("Completed"),
    OnHold("On hold"),
    Dropped("Dropped"),
    PlanToWatch("Plan to watch"),
    ;

    val active: Boolean
        get() = this == Watching || this == Rewatching

    companion object {
        val defaultRemoteStatuses: Set<ChimahonAnimeTrackStatus> = setOf(
            Watching,
            Completed,
            OnHold,
            Dropped,
            PlanToWatch,
            Rewatching,
        )
    }
}

data class ChimahonAnimeTrackingScoreScale(
    val format: ChimahonAnimeTrackingScoreFormat = ChimahonAnimeTrackingScoreFormat.Point10,
    val max: Double = 10.0,
    val step: Double = 0.5,
) {
    val safeMax: Double
        get() = max.coerceAtLeast(1.0)

    val safeStep: Double
        get() = step.coerceAtLeast(0.1)
}

enum class ChimahonAnimeTrackingScoreFormat {
    Point10,
    Point100,
    FiveStar,
    Percentage,
    Smile,
    Custom,
}

data class ChimahonAnimeTrackingScore(
    val value: Double? = null,
    val scale: ChimahonAnimeTrackingScoreScale = ChimahonAnimeTrackingScoreScale(),
    val updatedAtMillis: Long = 0L,
) {
    val normalizedValue: Double?
        get() = value?.coerceIn(0.0, scale.safeMax)

    val label: String
        get() = normalizedValue?.let { "${it.toAnimeTrackingTrimmed()} / ${scale.safeMax.toAnimeTrackingTrimmed()}" }
            ?: "No score"
}

data class ChimahonAnimeTrackingProgress(
    val watchedEpisodes: Int = 0,
    val totalEpisodes: Int? = null,
    val lastEpisodeId: Long? = null,
    val lastEpisodeNumber: Double? = null,
    val rewatchCount: Int = 0,
    val updatedAtMillis: Long = 0L,
) {
    val safeWatchedEpisodes: Int
        get() = watchedEpisodes.coerceAtLeast(0)

    val safeTotalEpisodes: Int?
        get() = totalEpisodes?.takeIf { it > 0 }

    val completed: Boolean
        get() = safeTotalEpisodes?.let { safeWatchedEpisodes >= it } == true

    val label: String
        get() = safeTotalEpisodes?.let { "$safeWatchedEpisodes / $it episodes" } ?: "$safeWatchedEpisodes episodes"
}

data class ChimahonAnimeTrackingDate(
    val year: Int,
    val month: Int? = null,
    val day: Int? = null,
) {
    val sortableKey: Int
        get() = year * 10_000 + (month ?: 0).coerceIn(0, 12) * 100 + (day ?: 0).coerceIn(0, 31)
}

data class ChimahonAnimeTrackingEntry(
    val key: ChimahonAnimeTrackingEntryKey,
    val trackerName: String,
    val title: String,
    val remoteTitle: String = title,
    val coverUrl: String? = null,
    val remoteUrl: String? = null,
    val status: ChimahonAnimeTrackStatus = ChimahonAnimeTrackStatus.NotTracked,
    val progress: ChimahonAnimeTrackingProgress = ChimahonAnimeTrackingProgress(),
    val score: ChimahonAnimeTrackingScore = ChimahonAnimeTrackingScore(),
    val startedDate: ChimahonAnimeTrackingDate? = null,
    val completedDate: ChimahonAnimeTrackingDate? = null,
    val privateTracking: Boolean = false,
    val syncStatus: ChimahonAnimeTrackingSyncStatus = ChimahonAnimeTrackingSyncStatus.Idle,
    val episodeSync: ChimahonAnimeTrackingEpisodeSyncState = ChimahonAnimeTrackingEpisodeSyncState(),
    val conflictIds: List<String> = emptyList(),
    val lastSyncedAtMillis: Long = 0L,
    val nextSyncAtMillis: Long = 0L,
    val localUpdatedAtMillis: Long = progress.updatedAtMillis,
    val remoteUpdatedAtMillis: Long = 0L,
    val errorMessage: String? = null,
) {
    val animeId: Long
        get() = key.animeId

    val trackerId: String
        get() = key.trackerId

    val remoteId: String
        get() = key.remoteId

    val hasConflicts: Boolean
        get() = conflictIds.isNotEmpty() || syncStatus == ChimahonAnimeTrackingSyncStatus.Conflict

    val pendingEpisodeSyncCount: Int
        get() = episodeSync.pendingCount
}

data class ChimahonAnimeTrackingLocalSnapshot(
    val animeId: Long,
    val sourceId: Long = 0L,
    val title: String,
    val alternateTitles: List<String> = emptyList(),
    val coverUrl: String? = null,
    val sourceUrl: String? = null,
    val sourceName: String? = null,
    val status: ChimahonAnimeTrackStatus = ChimahonAnimeTrackStatus.PlanToWatch,
    val progress: ChimahonAnimeTrackingProgress = ChimahonAnimeTrackingProgress(),
    val episodes: List<ChimahonAnimeTrackingLocalEpisode> = emptyList(),
    val categoryIds: List<Long> = emptyList(),
    val updatedAtMillis: Long = 0L,
) {
    val watchedEpisodeCount: Int
        get() = progress.safeWatchedEpisodes

    val totalEpisodeCount: Int
        get() = progress.safeTotalEpisodes ?: episodes.size
}

data class ChimahonAnimeTrackingLocalEpisode(
    val episodeId: Long,
    val title: String,
    val episodeNumber: Double? = null,
    val seen: Boolean = false,
    val positionSeconds: Long = 0L,
    val durationSeconds: Long = 0L,
    val watchedAtMillis: Long = 0L,
) {
    val progressFraction: Float
        get() = if (durationSeconds > 0L) {
            (positionSeconds.toFloat() / durationSeconds.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
}

data class ChimahonAnimeTrackingRemoteSearchRequest(
    val trackerId: String,
    val query: String,
    val anime: ChimahonAnimeTrackingLocalSnapshot? = null,
    val limit: Int = 25,
)

data class ChimahonAnimeTrackingRemoteSearchResult(
    val trackerId: String,
    val results: List<ChimahonAnimeTrackingRemoteMatch> = emptyList(),
    val errors: List<ChimahonAnimeTrackingError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonAnimeTrackingRemoteMatch(
    val trackerId: String,
    val remoteId: String,
    val title: String,
    val alternativeTitles: List<String> = emptyList(),
    val coverUrl: String? = null,
    val remoteUrl: String? = null,
    val totalEpisodes: Int? = null,
    val status: ChimahonAnimeTrackStatus? = null,
    val startDate: ChimahonAnimeTrackingDate? = null,
    val score: Double? = null,
    val confidence: Float = 0f,
) {
    val displayTitle: String
        get() = title.ifBlank { alternativeTitles.firstOrNull(String::isNotBlank) ?: remoteId }
}

data class ChimahonAnimeTrackingLinkRequest(
    val trackerId: String,
    val remoteId: String,
    val anime: ChimahonAnimeTrackingLocalSnapshot,
    val initialStatus: ChimahonAnimeTrackStatus = anime.status,
    val initialProgress: ChimahonAnimeTrackingProgress = anime.progress,
    val initialScore: ChimahonAnimeTrackingScore = ChimahonAnimeTrackingScore(),
    val privateTracking: Boolean = false,
    val syncEpisodes: Boolean = true,
    val requestedAtMillis: Long = 0L,
)

data class ChimahonAnimeTrackingUpdateRequest(
    val key: ChimahonAnimeTrackingEntryKey,
    val status: ChimahonAnimeTrackStatus? = null,
    val progress: ChimahonAnimeTrackingProgress? = null,
    val score: ChimahonAnimeTrackingScore? = null,
    val privateTracking: Boolean? = null,
    val startedDate: ChimahonAnimeTrackingDate? = null,
    val completedDate: ChimahonAnimeTrackingDate? = null,
    val changedAtMillis: Long = 0L,
    val reason: ChimahonAnimeTrackingUpdateReason = ChimahonAnimeTrackingUpdateReason.Manual,
)

enum class ChimahonAnimeTrackingUpdateReason {
    Manual,
    LibraryAdd,
    EpisodeSeen,
    PlayerProgress,
    BackgroundSync,
    RemotePull,
    ConflictResolution,
}

data class ChimahonAnimeTrackingEpisodeSyncState(
    val status: ChimahonAnimeTrackingEpisodeSyncStatus = ChimahonAnimeTrackingEpisodeSyncStatus.Idle,
    val pendingEpisodeIds: Set<Long> = emptySet(),
    val syncedEpisodeIds: Set<Long> = emptySet(),
    val lastSyncedEpisodeNumber: Int = 0,
    val lastQueuedAtMillis: Long = 0L,
    val lastSyncedAtMillis: Long = 0L,
    val errorMessage: String? = null,
) {
    val pendingCount: Int
        get() = pendingEpisodeIds.size

    val syncLabel: String
        get() = when {
            errorMessage != null -> errorMessage
            pendingCount > 0 -> "$pendingCount episode(s) pending"
            lastSyncedEpisodeNumber > 0 -> "Synced through episode $lastSyncedEpisodeNumber"
            else -> status.label
        }
}

enum class ChimahonAnimeTrackingEpisodeSyncStatus(val label: String) {
    Idle("Idle"),
    Pending("Pending"),
    Syncing("Syncing"),
    Synced("Synced"),
    Failed("Failed"),
    Disabled("Disabled"),
    Conflict("Conflict"),
}

data class ChimahonAnimeTrackingEpisodeSyncRequest(
    val key: ChimahonAnimeTrackingEntryKey,
    val items: List<ChimahonAnimeTrackingEpisodeSyncItem>,
    val trigger: ChimahonAnimeTrackingEpisodeSyncTrigger = ChimahonAnimeTrackingEpisodeSyncTrigger.Manual,
    val requestedAtMillis: Long = 0L,
) {
    val pendingItems: List<ChimahonAnimeTrackingEpisodeSyncItem>
        get() = items.filter { it.status == ChimahonAnimeTrackingEpisodeSyncItemStatus.Pending }

    val maxWatchedEpisode: Int
        get() = pendingItems.maxOfOrNull { it.remoteEpisodeNumber ?: 0 } ?: 0
}

data class ChimahonAnimeTrackingEpisodeSyncItem(
    val episodeId: Long,
    val remoteEpisodeNumber: Int? = null,
    val title: String? = null,
    val seen: Boolean = false,
    val positionSeconds: Long = 0L,
    val durationSeconds: Long = 0L,
    val watchedAtMillis: Long = 0L,
    val trigger: ChimahonAnimeTrackingEpisodeSyncTrigger = ChimahonAnimeTrackingEpisodeSyncTrigger.Manual,
    val status: ChimahonAnimeTrackingEpisodeSyncItemStatus = ChimahonAnimeTrackingEpisodeSyncItemStatus.Pending,
    val errorMessage: String? = null,
) {
    val progressFraction: Float
        get() = if (durationSeconds > 0L) {
            (positionSeconds.toFloat() / durationSeconds.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
}

enum class ChimahonAnimeTrackingEpisodeSyncTrigger {
    LibraryMarkSeen,
    PlayerProgress,
    PlaybackCompleted,
    Manual,
    BackgroundSync,
    RemotePull,
    ConflictResolution,
}

enum class ChimahonAnimeTrackingEpisodeSyncItemStatus {
    Pending,
    Skipped,
    Synced,
    Failed,
    Conflict,
}

data class ChimahonAnimeTrackingConflict(
    val id: String,
    val key: ChimahonAnimeTrackingEntryKey,
    val localEntry: ChimahonAnimeTrackingEntry? = null,
    val remoteEntry: ChimahonAnimeTrackingEntry? = null,
    val fields: List<ChimahonAnimeTrackingConflictFieldChange> = emptyList(),
    val detectedAtMillis: Long = 0L,
    val strategy: ChimahonAnimeTrackingConflictStrategy = ChimahonAnimeTrackingConflictStrategy.AskEveryTime,
    val resolved: Boolean = false,
) {
    val hasFieldChanges: Boolean
        get() = fields.isNotEmpty()
}

data class ChimahonAnimeTrackingConflictFieldChange(
    val field: ChimahonAnimeTrackingConflictField,
    val localValueLabel: String,
    val remoteValueLabel: String,
    val localUpdatedAtMillis: Long = 0L,
    val remoteUpdatedAtMillis: Long = 0L,
)

enum class ChimahonAnimeTrackingConflictField {
    Status,
    Progress,
    Score,
    StartedDate,
    CompletedDate,
    PrivateTracking,
    EpisodeSync,
}

enum class ChimahonAnimeTrackingConflictStrategy(val label: String) {
    NewestChangeWins("Newest change wins"),
    LocalProgressWins("Local progress wins"),
    TrackerProgressWins("Tracker progress wins"),
    HighestProgressWins("Highest progress wins"),
    AskEveryTime("Ask every time"),
}

data class ChimahonAnimeTrackingConflictResolutionRequest(
    val conflictId: String,
    val key: ChimahonAnimeTrackingEntryKey,
    val choice: ChimahonAnimeTrackingConflictChoice,
    val fieldChoices: Map<ChimahonAnimeTrackingConflictField, ChimahonAnimeTrackingConflictChoice> = emptyMap(),
    val mergedEntry: ChimahonAnimeTrackingEntry? = null,
    val resolvedAtMillis: Long = 0L,
)

enum class ChimahonAnimeTrackingConflictChoice {
    UseLocal,
    UseTracker,
    UseMerged,
    Ignore,
}

data class ChimahonAnimeTrackingPlayerProgressPolicy(
    val enabled: Boolean = true,
    val syncOnlyCompletedEpisodes: Boolean = true,
    val markSeenAtFraction: Float = 0.9f,
    val markSeenWithRemainingSeconds: Long = 90L,
    val minimumPositionSeconds: Long = 60L,
    val queueWhileIncognito: Boolean = false,
    val updateStatusWhenStarted: Boolean = true,
    val updateStatusWhenCompleted: Boolean = true,
) {
    val safeMarkSeenAtFraction: Float
        get() = markSeenAtFraction.coerceIn(0.01f, 1f)
}

data class ChimahonAnimeTrackingPlayerProgressHook(
    val enabled: Boolean = true,
    val targetTrackerIds: Set<String> = emptySet(),
    val policy: ChimahonAnimeTrackingPlayerProgressPolicy = ChimahonAnimeTrackingPlayerProgressPolicy(),
)

data class ChimahonAnimeTrackingPlayerProgressEvent(
    val animeId: Long,
    val episodeId: Long,
    val episodeTitle: String? = null,
    val episodeNumber: Double? = null,
    val positionSeconds: Long,
    val durationSeconds: Long,
    val watchedAtMillis: Long,
    val sessionWatchDurationMillis: Long = 0L,
    val playbackSource: ChimahonAnimePlaybackSource = ChimahonAnimePlaybackSource.Stream,
    val paused: Boolean = false,
    val completed: Boolean = false,
    val incognito: Boolean = false,
) {
    val progressFraction: Float
        get() = if (durationSeconds > 0L) {
            (positionSeconds.toFloat() / durationSeconds.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    fun shouldMarkSeen(policy: ChimahonAnimeTrackingPlayerProgressPolicy): Boolean {
        if (!policy.enabled) return false
        if (positionSeconds < policy.minimumPositionSeconds && !completed) return false

        val withinEndingWindow = durationSeconds > 0L &&
            (durationSeconds - positionSeconds).coerceAtLeast(0L) <= policy.markSeenWithRemainingSeconds
        return completed || progressFraction >= policy.safeMarkSeenAtFraction || withinEndingWindow
    }
}

data class ChimahonAnimeTrackingSettings(
    val trackOnLibraryAdd: Boolean = true,
    val autoSyncEnabled: Boolean = true,
    val syncOnEpisodeSeen: Boolean = true,
    val syncOnPlayerProgress: Boolean = true,
    val syncCompletedEntries: Boolean = true,
    val pullRemoteProgress: Boolean = false,
    val privateTrackingDefault: Boolean = false,
    val syncOnlyOnWifi: Boolean = false,
    val pauseInIncognito: Boolean = true,
    val syncLibraryEntriesOnly: Boolean = true,
    val defaultStatus: ChimahonAnimeTrackStatus = ChimahonAnimeTrackStatus.Watching,
    val conflictStrategy: ChimahonAnimeTrackingConflictStrategy = ChimahonAnimeTrackingConflictStrategy.NewestChangeWins,
    val playerProgressHook: ChimahonAnimeTrackingPlayerProgressHook = ChimahonAnimeTrackingPlayerProgressHook(),
)

data class ChimahonAnimeTrackingLoadRequest(
    val animeIds: List<Long> = emptyList(),
    val trackerIds: List<String> = emptyList(),
    val includeAccounts: Boolean = true,
    val includeConflicts: Boolean = true,
    val includeEpisodeSync: Boolean = true,
)

data class ChimahonAnimeTrackingLoadResult(
    val accounts: List<ChimahonAnimeTrackerAccount> = emptyList(),
    val entries: List<ChimahonAnimeTrackingEntry> = emptyList(),
    val conflicts: List<ChimahonAnimeTrackingConflict> = emptyList(),
    val loadedAtMillis: Long = 0L,
    val errors: List<ChimahonAnimeTrackingError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()

    val syncing: Boolean
        get() = accounts.any { it.syncStatus == ChimahonAnimeTrackingSyncStatus.Syncing } ||
            entries.any { it.syncStatus == ChimahonAnimeTrackingSyncStatus.Syncing }
}

data class ChimahonAnimeTrackingMutationResult(
    val changedCount: Int = 0,
    val entries: List<ChimahonAnimeTrackingEntry> = emptyList(),
    val episodeSync: List<ChimahonAnimeTrackingEpisodeSyncItem> = emptyList(),
    val conflicts: List<ChimahonAnimeTrackingConflict> = emptyList(),
    val errors: List<ChimahonAnimeTrackingError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty()
}

data class ChimahonAnimeTrackingPlayerProgressResult(
    val queued: Boolean = false,
    val updateRequest: ChimahonAnimeTrackingUpdateRequest? = null,
    val episodeSyncRequest: ChimahonAnimeTrackingEpisodeSyncRequest? = null,
    val mutation: ChimahonAnimeTrackingMutationResult = ChimahonAnimeTrackingMutationResult(),
    val errors: List<ChimahonAnimeTrackingError> = emptyList(),
) {
    val successful: Boolean
        get() = errors.isEmpty() && mutation.successful
}

private fun Double.toAnimeTrackingTrimmed(): String {
    val asInt = toInt()
    return if (this == asInt.toDouble()) asInt.toString() else toString().trimEnd('0').trimEnd('.')
}
