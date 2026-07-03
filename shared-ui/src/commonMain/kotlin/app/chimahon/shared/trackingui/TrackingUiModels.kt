package app.chimahon.shared.trackingui

data class ChimahonTrackingUiState(
    val title: String = "Tracking",
    val subtitle: String? = null,
    val accounts: List<ChimahonTrackerAccountUiModel> = emptyList(),
    val entries: List<ChimahonTrackingEntryUiModel> = emptyList(),
    val selectedMediaKind: ChimahonTrackingMediaKind? = null,
    val query: String = "",
    val loading: Boolean = false,
    val syncing: Boolean = false,
    val errorMessage: String? = null,
) {
    val loggedInAccounts: Int
        get() = accounts.count { it.accountStatus.isSignedIn }

    val filteredEntries: List<ChimahonTrackingEntryUiModel>
        get() = entries
            .filter { selectedMediaKind == null || it.mediaKind == selectedMediaKind }
            .filter { entry ->
                query.isBlank() ||
                    entry.title.contains(query, ignoreCase = true) ||
                    entry.remoteTitle.contains(query, ignoreCase = true) ||
                    entry.trackerName.contains(query, ignoreCase = true)
            }

    val mediaKinds: List<ChimahonTrackingMediaKind>
        get() = entries.map { it.mediaKind }.distinct().ifEmpty {
            listOf(
                ChimahonTrackingMediaKind.Manga,
                ChimahonTrackingMediaKind.Anime,
                ChimahonTrackingMediaKind.LightNovel,
            )
        }
}

data class ChimahonTrackerAccountUiModel(
    val id: String,
    val name: String,
    val username: String? = null,
    val avatarUrl: String? = null,
    val accountStatus: ChimahonTrackerAccountStatus = ChimahonTrackerAccountStatus.LoggedOut,
    val syncStatus: ChimahonTrackerSyncStatus = ChimahonTrackerSyncStatus.Idle,
    val supportedMediaKinds: Set<ChimahonTrackingMediaKind> = ChimahonTrackingMediaKind.entries.toSet(),
    val isDefault: Boolean = false,
    val privateTrackingEnabled: Boolean = false,
    val supportsPrivateTracking: Boolean = false,
    val lastSyncLabel: String? = null,
    val errorMessage: String? = null,
    val pendingCount: Int = 0,
) {
    val displayName: String
        get() = username?.takeIf(String::isNotBlank) ?: accountStatus.label
}

data class ChimahonTrackingEntryUiModel(
    val id: String,
    val trackerId: String,
    val trackerName: String,
    val mediaKind: ChimahonTrackingMediaKind,
    val title: String,
    val remoteTitle: String = title,
    val coverUrl: String? = null,
    val remoteUrl: String? = null,
    val status: ChimahonTrackingStatus = ChimahonTrackingStatus.NotTracked,
    val score: Double? = null,
    val scoreMax: Double = 10.0,
    val scoreStep: Double = 0.5,
    val progress: Int = 0,
    val totalProgress: Int? = null,
    val startedDateLabel: String? = null,
    val completedDateLabel: String? = null,
    val lastSyncedLabel: String? = null,
    val nextSyncLabel: String? = null,
    val privateTracking: Boolean = false,
    val syncStatus: ChimahonTrackerSyncStatus = ChimahonTrackerSyncStatus.Idle,
    val errorMessage: String? = null,
) {
    val safeScoreMax: Double
        get() = scoreMax.coerceAtLeast(1.0)

    val scoreLabel: String
        get() = score?.let { value -> "${value.trimmed()} / ${safeScoreMax.trimmed()}" } ?: "No score"

    val progressLabel: String
        get() {
            val unit = mediaKind.progressUnit
            val total = totalProgress
            return if (total != null && total > 0) {
                "$progress / $total $unit"
            } else {
                "$progress $unit"
            }
        }

    val stableLazyKey: String
        get() = listOf(mediaKind.name, trackerId, id).joinToString(":")
}

data class ChimahonTrackingMediaSummaryUiModel(
    val mediaKind: ChimahonTrackingMediaKind,
    val trackedCount: Int = 0,
    val activeCount: Int = 0,
    val completedCount: Int = 0,
    val pendingSyncCount: Int = 0,
    val errorCount: Int = 0,
    val nextSyncLabel: String? = null,
) {
    val summaryLabel: String
        get() = when {
            trackedCount == 0 -> "No tracked ${mediaKind.pluralLabel.lowercase()}"
            activeCount > 0 -> "$activeCount active of $trackedCount tracked"
            completedCount > 0 -> "$completedCount completed of $trackedCount tracked"
            else -> "$trackedCount tracked"
        }
}

data class ChimahonTrackingCallbacks(
    val onLogin: (ChimahonTrackerAccountUiModel) -> Unit = {},
    val onLogout: (ChimahonTrackerAccountUiModel) -> Unit = {},
    val onOpenAccountSettings: (ChimahonTrackerAccountUiModel) -> Unit = {},
    val onSyncAccount: (ChimahonTrackerAccountUiModel) -> Unit = {},
    val onOpenTracker: (ChimahonTrackingEntryUiModel) -> Unit = {},
    val onSearchTracker: (ChimahonTrackingEntryUiModel) -> Unit = {},
    val onRemoveTracking: (ChimahonTrackingEntryUiModel) -> Unit = {},
    val onSyncEntry: (ChimahonTrackingEntryUiModel) -> Unit = {},
    val onStatusChange: (ChimahonTrackingEntryUiModel, ChimahonTrackingStatus) -> Unit = { _, _ -> },
    val onScoreChange: (ChimahonTrackingEntryUiModel, Double?) -> Unit = { _, _ -> },
    val onProgressChange: (ChimahonTrackingEntryUiModel, Int) -> Unit = { _, _ -> },
    val onTogglePrivateTracking: (ChimahonTrackingEntryUiModel, Boolean) -> Unit = { _, _ -> },
    val onMediaKindSelected: (ChimahonTrackingMediaKind?) -> Unit = {},
    val onRetry: () -> Unit = {},
)

data class ChimahonTrackingSettingsUiState(
    val trackOnLibraryAdd: Boolean = true,
    val autoSyncEnabled: Boolean = true,
    val syncOnProgressChange: Boolean = true,
    val syncOnCompletion: Boolean = true,
    val privateTrackingDefault: Boolean = false,
    val syncOnlyOnWifi: Boolean = false,
    val pauseInIncognito: Boolean = true,
    val includeManga: Boolean = true,
    val includeAnime: Boolean = true,
    val includeLightNovels: Boolean = true,
    val defaultMangaStatus: ChimahonTrackingStatus = ChimahonTrackingStatus.Reading,
    val defaultAnimeStatus: ChimahonTrackingStatus = ChimahonTrackingStatus.Watching,
    val defaultLightNovelStatus: ChimahonTrackingStatus = ChimahonTrackingStatus.Reading,
    val syncInterval: ChimahonTrackingSyncInterval = ChimahonTrackingSyncInterval.EverySixHours,
    val conflictStrategy: ChimahonTrackingConflictStrategy = ChimahonTrackingConflictStrategy.NewestChangeWins,
    val includedCategoryLabels: List<String> = emptyList(),
    val excludedCategoryLabels: List<String> = emptyList(),
    val lastGlobalSyncLabel: String? = null,
    val pendingChanges: Int = 0,
    val errorMessage: String? = null,
) {
    val enabledMediaKinds: List<ChimahonTrackingMediaKind>
        get() = buildList {
            if (includeManga) add(ChimahonTrackingMediaKind.Manga)
            if (includeAnime) add(ChimahonTrackingMediaKind.Anime)
            if (includeLightNovels) add(ChimahonTrackingMediaKind.LightNovel)
        }

    val categoryScopeLabel: String
        get() = when {
            includedCategoryLabels.isEmpty() && excludedCategoryLabels.isEmpty() -> "All library categories"
            includedCategoryLabels.isNotEmpty() && excludedCategoryLabels.isEmpty() ->
                "Only ${includedCategoryLabels.joinToString()}"
            includedCategoryLabels.isEmpty() ->
                "All except ${excludedCategoryLabels.joinToString()}"
            else -> "${includedCategoryLabels.joinToString()} excluding ${excludedCategoryLabels.joinToString()}"
        }

    fun defaultStatusFor(mediaKind: ChimahonTrackingMediaKind): ChimahonTrackingStatus {
        return when (mediaKind) {
            ChimahonTrackingMediaKind.Manga -> defaultMangaStatus
            ChimahonTrackingMediaKind.Anime -> defaultAnimeStatus
            ChimahonTrackingMediaKind.LightNovel -> defaultLightNovelStatus
        }
    }
}

fun ChimahonTrackingUiState.mediaSummaries(): List<ChimahonTrackingMediaSummaryUiModel> {
    return mediaKinds.map { mediaKind ->
        val mediaEntries = entries.filter { it.mediaKind == mediaKind }
        ChimahonTrackingMediaSummaryUiModel(
            mediaKind = mediaKind,
            trackedCount = mediaEntries.size,
            activeCount = mediaEntries.count { it.status.isActive() },
            completedCount = mediaEntries.count { it.status == ChimahonTrackingStatus.Completed },
            pendingSyncCount = mediaEntries.count {
                it.syncStatus == ChimahonTrackerSyncStatus.Pending ||
                    it.syncStatus == ChimahonTrackerSyncStatus.Syncing
            },
            errorCount = mediaEntries.count {
                it.syncStatus == ChimahonTrackerSyncStatus.Failed || it.errorMessage != null
            },
            nextSyncLabel = mediaEntries.mapNotNull { it.nextSyncLabel }.firstOrNull(),
        )
    }
}

data class ChimahonTrackingSettingsCallbacks(
    val onTrackOnLibraryAddChange: (Boolean) -> Unit = {},
    val onAutoSyncEnabledChange: (Boolean) -> Unit = {},
    val onSyncOnProgressChange: (Boolean) -> Unit = {},
    val onSyncOnCompletionChange: (Boolean) -> Unit = {},
    val onPrivateTrackingDefaultChange: (Boolean) -> Unit = {},
    val onSyncOnlyOnWifiChange: (Boolean) -> Unit = {},
    val onPauseInIncognitoChange: (Boolean) -> Unit = {},
    val onMediaKindEnabledChange: (ChimahonTrackingMediaKind, Boolean) -> Unit = { _, _ -> },
    val onDefaultStatusChange: (ChimahonTrackingMediaKind, ChimahonTrackingStatus) -> Unit = { _, _ -> },
    val onSyncIntervalChange: (ChimahonTrackingSyncInterval) -> Unit = {},
    val onConflictStrategyChange: (ChimahonTrackingConflictStrategy) -> Unit = {},
    val onOpenCategoryScope: () -> Unit = {},
    val onSyncNow: () -> Unit = {},
    val onResetDefaults: () -> Unit = {},
)

enum class ChimahonTrackingMediaKind(
    val label: String,
    val pluralLabel: String,
    val progressUnit: String,
    val startVerb: String,
) {
    Manga("Manga", "Manga", "chapters", "Reading"),
    Anime("Anime", "Anime", "episodes", "Watching"),
    LightNovel("Light novel", "Light novels", "chapters", "Reading"),
}

enum class ChimahonTrackingSyncInterval(
    val label: String,
    val description: String,
) {
    Manual("Manual", "Only sync when requested"),
    EveryHour("Hourly", "Keep active trackers close to real time"),
    EverySixHours("Every 6 hours", "Balanced background sync"),
    Daily("Daily", "Light background traffic"),
}

enum class ChimahonTrackingConflictStrategy(
    val label: String,
    val description: String,
) {
    NewestChangeWins("Newest change wins", "Use the most recent local or tracker edit"),
    LocalProgressWins("Local progress wins", "Prefer Chimahon progress when values differ"),
    TrackerProgressWins("Tracker progress wins", "Prefer remote tracker values"),
    AskEveryTime("Ask every time", "Hold conflicts until the user resolves them"),
}

enum class ChimahonTrackerAccountStatus(val label: String) {
    LoggedOut("Not logged in"),
    LoggedIn("Logged in"),
    Expired("Login expired"),
    Syncing("Syncing"),
    Error("Error"),
    ;

    val isSignedIn: Boolean
        get() = this == LoggedIn || this == Syncing
}

enum class ChimahonTrackerSyncStatus(val label: String) {
    Idle("Idle"),
    Pending("Pending"),
    Syncing("Syncing"),
    Synced("Synced"),
    Failed("Failed"),
    Disabled("Disabled"),
}

enum class ChimahonTrackingStatus {
    NotTracked,
    Reading,
    Watching,
    ReReading,
    ReWatching,
    Completed,
    OnHold,
    Paused,
    Dropped,
    PlanToRead,
    PlanToWatch,
}

data class ChimahonTrackingStatusOption(
    val status: ChimahonTrackingStatus,
    val label: String,
)

fun ChimahonTrackingStatus.labelFor(mediaKind: ChimahonTrackingMediaKind): String {
    return when (this) {
        ChimahonTrackingStatus.NotTracked -> "Not tracked"
        ChimahonTrackingStatus.Reading -> if (mediaKind == ChimahonTrackingMediaKind.Anime) "Watching" else "Reading"
        ChimahonTrackingStatus.Watching -> "Watching"
        ChimahonTrackingStatus.ReReading -> if (mediaKind == ChimahonTrackingMediaKind.Anime) "Rewatching" else "Rereading"
        ChimahonTrackingStatus.ReWatching -> "Rewatching"
        ChimahonTrackingStatus.Completed -> "Completed"
        ChimahonTrackingStatus.OnHold -> "On hold"
        ChimahonTrackingStatus.Paused -> "Paused"
        ChimahonTrackingStatus.Dropped -> "Dropped"
        ChimahonTrackingStatus.PlanToRead -> if (mediaKind == ChimahonTrackingMediaKind.Anime) "Plan to watch" else "Plan to read"
        ChimahonTrackingStatus.PlanToWatch -> "Plan to watch"
    }
}

fun ChimahonTrackingStatus.isActive(): Boolean {
    return when (this) {
        ChimahonTrackingStatus.Reading,
        ChimahonTrackingStatus.Watching,
        ChimahonTrackingStatus.ReReading,
        ChimahonTrackingStatus.ReWatching,
        -> true
        ChimahonTrackingStatus.NotTracked,
        ChimahonTrackingStatus.Completed,
        ChimahonTrackingStatus.OnHold,
        ChimahonTrackingStatus.Paused,
        ChimahonTrackingStatus.Dropped,
        ChimahonTrackingStatus.PlanToRead,
        ChimahonTrackingStatus.PlanToWatch,
        -> false
    }
}

fun ChimahonTrackingMediaKind.defaultStatusOptions(): List<ChimahonTrackingStatusOption> {
    val statuses = when (this) {
        ChimahonTrackingMediaKind.Anime -> listOf(
            ChimahonTrackingStatus.Watching,
            ChimahonTrackingStatus.Completed,
            ChimahonTrackingStatus.OnHold,
            ChimahonTrackingStatus.Dropped,
            ChimahonTrackingStatus.PlanToWatch,
            ChimahonTrackingStatus.ReWatching,
        )
        ChimahonTrackingMediaKind.Manga,
        ChimahonTrackingMediaKind.LightNovel,
        -> listOf(
            ChimahonTrackingStatus.Reading,
            ChimahonTrackingStatus.Completed,
            ChimahonTrackingStatus.OnHold,
            ChimahonTrackingStatus.Dropped,
            ChimahonTrackingStatus.PlanToRead,
            ChimahonTrackingStatus.ReReading,
        )
    }
    return statuses.map { ChimahonTrackingStatusOption(it, it.labelFor(this)) }
}

fun Double.trimmed(): String {
    val asInt = toInt()
    return if (this == asInt.toDouble()) asInt.toString() else toString().trimEnd('0').trimEnd('.')
}
