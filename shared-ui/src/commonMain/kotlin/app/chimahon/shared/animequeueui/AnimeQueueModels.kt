package app.chimahon.shared.animequeueui

data class ChimahonAnimeQueueUiState(
    val title: String = "Anime queue",
    val subtitle: String? = null,
    val rows: List<ChimahonAnimeQueueRowUiModel> = emptyList(),
    val selectedEpisodeIds: Set<String> = emptySet(),
    val playlist: ChimahonAnimeAutoNextPlaylistUiState = ChimahonAnimeAutoNextPlaylistUiState(),
    val paused: Boolean = false,
    val activeCount: Int = 0,
    val queuedCount: Int = 0,
    val completedCount: Int = 0,
    val failedCount: Int = 0,
    val localCount: Int = 0,
    val streamCount: Int = 0,
    val loading: Boolean = false,
    val errorMessage: String? = null,
) {
    val selectedRowIds: Set<String>
        get() = selectedEpisodeIds + rows.filter { it.selected }.map { it.id }

    val selectedCount: Int
        get() = selectedRowIds.size

    val isSelectionMode: Boolean
        get() = selectedRowIds.isNotEmpty()

    val hasRows: Boolean
        get() = rows.isNotEmpty()

    val allRowsSelected: Boolean
        get() = rows.isNotEmpty() && rows.all { it.id in selectedRowIds }

    val selectedRows: List<ChimahonAnimeQueueRowUiModel>
        get() = rows.filter { it.id in selectedRowIds }

    val hasRecoverableErrors: Boolean
        get() = rows.any { it.canRetry }

    val summaryLabel: String
        get() = listOfNotNull(
            rows.size.takeIf { it > 0 }?.let { "$it episode(s)" },
            activeCount.takeIf { it > 0 }?.let { "$it active" },
            queuedCount.takeIf { it > 0 }?.let { "$it queued" },
            completedCount.takeIf { it > 0 }?.let { "$it complete" },
            failedCount.takeIf { it > 0 }?.let { "$it failed" },
            localCount.takeIf { it > 0 }?.let { "$it local" },
            streamCount.takeIf { it > 0 }?.let { "$it stream" },
        ).joinToString(" - ").ifBlank { "No queued episodes" }
}

data class ChimahonAnimeQueueRowUiModel(
    val id: String,
    val animeTitle: String,
    val episodeTitle: String,
    val episodeId: String = id,
    val animeId: String? = null,
    val episodeNumberLabel: String? = null,
    val subtitle: String? = null,
    val sourceName: String? = null,
    val thumbnailUrl: String? = null,
    val intent: ChimahonAnimeQueueIntent = ChimahonAnimeQueueIntent.Watch,
    val availability: Set<ChimahonAnimeEpisodeAvailability> = setOf(ChimahonAnimeEpisodeAvailability.Stream),
    val status: ChimahonAnimeQueueStatus = ChimahonAnimeQueueStatus.Queued,
    val priority: ChimahonAnimeQueuePriority = ChimahonAnimeQueuePriority.Normal,
    val progress: ChimahonAnimeQueueProgress = ChimahonAnimeQueueProgress(),
    val durationLabel: String? = null,
    val sizeLabel: String? = null,
    val speedLabel: String? = null,
    val etaLabel: String? = null,
    val availabilityNote: String? = null,
    val selected: Boolean = false,
    val seen: Boolean = false,
    val bookmarked: Boolean = false,
    val filler: Boolean = false,
    val reorder: ChimahonAnimeQueueReorderState = ChimahonAnimeQueueReorderState(),
    val error: ChimahonAnimeQueueError? = null,
    val overflowActions: List<ChimahonAnimeQueueOverflowAction> = emptyList(),
) {
    val metadataLabel: String?
        get() = listOfNotNull(
            episodeNumberLabel,
            sourceName,
            subtitle,
            durationLabel,
        ).joinToString(" - ").ifBlank { null }

    val transferLabel: String?
        get() = listOfNotNull(sizeLabel, speedLabel, etaLabel).joinToString(" - ").ifBlank { null }

    val canPlay: Boolean
        get() = !isUnavailable && (
            ChimahonAnimeEpisodeAvailability.Local in availability ||
                ChimahonAnimeEpisodeAvailability.Downloaded in availability ||
                ChimahonAnimeEpisodeAvailability.Stream in availability
            )

    val canStream: Boolean
        get() = !isUnavailable && ChimahonAnimeEpisodeAvailability.Stream in availability

    val canDownload: Boolean
        get() = !isUnavailable &&
            intent == ChimahonAnimeQueueIntent.Download &&
            ChimahonAnimeEpisodeAvailability.Downloaded !in availability &&
            status !in terminalStatuses

    val canPause: Boolean
        get() = status in pauseableStatuses

    val canResume: Boolean
        get() = status == ChimahonAnimeQueueStatus.Paused

    val canRetry: Boolean
        get() = error?.canRetry == true || status == ChimahonAnimeQueueStatus.Failed || status == ChimahonAnimeQueueStatus.Canceled

    val canCancel: Boolean
        get() = status !in terminalStatuses

    val isActive: Boolean
        get() = status in activeStatuses

    val isTerminal: Boolean
        get() = status in terminalStatuses

    val isUnavailable: Boolean
        get() = availability.isEmpty() || ChimahonAnimeEpisodeAvailability.Unavailable in availability

    fun badges(includePriority: Boolean = true): List<ChimahonAnimeQueueBadgeUiModel> {
        val badges = availability
            .sortedBy { it.ordinal }
            .map { it.toBadge() }
            .toMutableList()
        if (availability.isEmpty()) {
            badges += ChimahonAnimeEpisodeAvailability.Unavailable.toBadge()
        }
        if (seen) badges += ChimahonAnimeQueueBadgeUiModel("Seen", ChimahonAnimeQueueBadgeKind.Muted)
        if (bookmarked) badges += ChimahonAnimeQueueBadgeUiModel("Saved", ChimahonAnimeQueueBadgeKind.Info)
        if (filler) badges += ChimahonAnimeQueueBadgeUiModel("Filler", ChimahonAnimeQueueBadgeKind.Warning)
        if (!availabilityNote.isNullOrBlank()) badges += ChimahonAnimeQueueBadgeUiModel(availabilityNote, ChimahonAnimeQueueBadgeKind.Muted)
        if (includePriority && priority != ChimahonAnimeQueuePriority.Normal) badges += priority.toBadge()
        return badges
    }
}

data class ChimahonAnimeQueueProgress(
    val fraction: Float = 0f,
    val percentLabel: String? = null,
    val statusLabel: String? = null,
    val downloadedBytes: Long? = null,
    val totalBytes: Long? = null,
    val indeterminate: Boolean = false,
) {
    val normalizedFraction: Float
        get() {
            val downloaded = downloadedBytes
            val total = totalBytes
            return if (downloaded != null && total != null && total > 0L) {
                (downloaded.toFloat() / total.toFloat()).coerceIn(0f, 1f)
            } else {
                fraction.coerceIn(0f, 1f)
            }
        }

    val displayPercentLabel: String
        get() = percentLabel ?: "${(normalizedFraction * 100f).toInt()}%"

    val hasKnownTotal: Boolean
        get() = totalBytes?.let { it > 0L } == true

    val shouldShow: Boolean
        get() = indeterminate ||
            fraction > 0f ||
            downloadedBytes != null ||
            totalBytes != null ||
            !statusLabel.isNullOrBlank() ||
            !percentLabel.isNullOrBlank()
}

data class ChimahonAnimeQueueError(
    val title: String = "Episode failed",
    val message: String,
    val retryLabel: String = "Retry",
    val canRetry: Boolean = true,
)

data class ChimahonAnimeQueueReorderState(
    val positionLabel: String? = null,
    val canMoveToTop: Boolean = false,
    val canMoveUp: Boolean = false,
    val canMoveDown: Boolean = false,
    val canMoveToBottom: Boolean = false,
) {
    val canMove: Boolean
        get() = canMoveToTop || canMoveUp || canMoveDown || canMoveToBottom
}

data class ChimahonAnimeQueueStatusChipUiModel(
    val text: String,
    val kind: ChimahonAnimeQueueStatusChipKind = ChimahonAnimeQueueStatusChipKind.Info,
)

data class ChimahonAnimeQueueBadgeUiModel(
    val text: String,
    val kind: ChimahonAnimeQueueBadgeKind = ChimahonAnimeQueueBadgeKind.Info,
)

data class ChimahonAnimeQueueOverflowAction(
    val id: String,
    val label: String,
    val icon: ChimahonAnimeQueueActionIcon = ChimahonAnimeQueueActionIcon.More,
    val enabled: Boolean = true,
    val destructive: Boolean = false,
    val onClick: () -> Unit,
)

data class ChimahonAnimeEpisodeBatchAction(
    val id: String,
    val label: String,
    val icon: ChimahonAnimeQueueActionIcon,
    val enabled: Boolean = true,
    val destructive: Boolean = false,
    val onClick: () -> Unit,
)

data class ChimahonAnimeEpisodeBatchActionBarState(
    val selectedCount: Int,
    val totalCount: Int,
    val title: String = "$selectedCount selected",
    val allSelected: Boolean = totalCount > 0 && selectedCount == totalCount,
    val primaryActions: List<ChimahonAnimeEpisodeBatchAction> = emptyList(),
    val overflowActions: List<ChimahonAnimeQueueOverflowAction> = emptyList(),
)

data class ChimahonAnimeQueueRowActions(
    val onOpenRow: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onSelectionChange: (ChimahonAnimeQueueRowUiModel, Boolean) -> Unit = { _, _ -> },
    val onPlayEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onStreamEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onDownloadEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onRetryEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onPauseEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onResumeEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onCancelEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onRemoveEpisode: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onPriorityChange: (ChimahonAnimeQueueRowUiModel, ChimahonAnimeQueuePriority) -> Unit = { _, _ -> },
    val onMoveToTop: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onMoveUp: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onMoveDown: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
    val onMoveToBottom: (ChimahonAnimeQueueRowUiModel) -> Unit = {},
)

data class ChimahonAnimeAutoNextPlaylistUiState(
    val title: String = "Up next",
    val subtitle: String? = null,
    val episodes: List<ChimahonAnimePlaylistEpisodeUiModel> = emptyList(),
    val currentEpisodeId: String? = null,
    val autoPlayEnabled: Boolean = true,
    val countdownSeconds: Int? = null,
    val loading: Boolean = false,
    val errorMessage: String? = null,
) {
    val isEmpty: Boolean
        get() = episodes.isEmpty()

    val currentEpisode: ChimahonAnimePlaylistEpisodeUiModel?
        get() = currentEpisodeId?.let { id -> episodes.firstOrNull { it.id == id } }

    val nextEpisode: ChimahonAnimePlaylistEpisodeUiModel?
        get() {
            val currentIndex = episodes.indexOfFirst { it.id == currentEpisodeId }
            return when {
                currentIndex >= 0 -> episodes.drop(currentIndex + 1).firstOrNull()
                else -> episodes.firstOrNull()
            }
        }

    val hasRecoverableErrors: Boolean
        get() = episodes.any { it.canRetry }
}

data class ChimahonAnimePlaylistEpisodeUiModel(
    val id: String,
    val animeTitle: String,
    val episodeTitle: String,
    val episodeNumberLabel: String? = null,
    val sourceName: String? = null,
    val thumbnailUrl: String? = null,
    val availability: Set<ChimahonAnimeEpisodeAvailability> = setOf(ChimahonAnimeEpisodeAvailability.Stream),
    val status: ChimahonAnimeQueueStatus = ChimahonAnimeQueueStatus.Queued,
    val priority: ChimahonAnimeQueuePriority = ChimahonAnimeQueuePriority.Normal,
    val progress: ChimahonAnimeQueueProgress? = null,
    val durationLabel: String? = null,
    val selected: Boolean = false,
    val current: Boolean = false,
    val seen: Boolean = false,
    val reorder: ChimahonAnimeQueueReorderState = ChimahonAnimeQueueReorderState(),
    val error: ChimahonAnimeQueueError? = null,
) {
    val metadataLabel: String?
        get() = listOfNotNull(episodeNumberLabel, sourceName, durationLabel).joinToString(" - ").ifBlank { null }

    val canPlay: Boolean
        get() = !isUnavailable && (
            ChimahonAnimeEpisodeAvailability.Local in availability ||
                ChimahonAnimeEpisodeAvailability.Downloaded in availability ||
                ChimahonAnimeEpisodeAvailability.Stream in availability
            )

    val canDownload: Boolean
        get() = !isUnavailable &&
            ChimahonAnimeEpisodeAvailability.Downloaded !in availability &&
            status !in terminalStatuses

    val canPause: Boolean
        get() = status in pauseableStatuses

    val canResume: Boolean
        get() = status == ChimahonAnimeQueueStatus.Paused

    val canRetry: Boolean
        get() = error?.canRetry == true || status == ChimahonAnimeQueueStatus.Failed || status == ChimahonAnimeQueueStatus.Canceled

    val canCancel: Boolean
        get() = status !in terminalStatuses

    val isUnavailable: Boolean
        get() = availability.isEmpty() || ChimahonAnimeEpisodeAvailability.Unavailable in availability

    fun badges(): List<ChimahonAnimeQueueBadgeUiModel> {
        val badges = availability
            .sortedBy { it.ordinal }
            .map { it.toBadge() }
            .toMutableList()
        if (availability.isEmpty()) {
            badges += ChimahonAnimeEpisodeAvailability.Unavailable.toBadge()
        }
        if (current) badges += ChimahonAnimeQueueBadgeUiModel("Now", ChimahonAnimeQueueBadgeKind.Active)
        if (seen) badges += ChimahonAnimeQueueBadgeUiModel("Seen", ChimahonAnimeQueueBadgeKind.Muted)
        if (priority != ChimahonAnimeQueuePriority.Normal) badges += priority.toBadge()
        return badges
    }
}

data class ChimahonAnimeAutoNextPlaylistActions(
    val onDismiss: () -> Unit = {},
    val onRetry: () -> Unit = {},
    val onToggleAutoPlay: (Boolean) -> Unit = {},
    val onPlayNow: (ChimahonAnimePlaylistEpisodeUiModel) -> Unit = {},
    val onEpisodeClick: (ChimahonAnimePlaylistEpisodeUiModel) -> Unit = {},
    val onDownloadEpisode: (ChimahonAnimePlaylistEpisodeUiModel) -> Unit = {},
    val onRetryEpisode: (ChimahonAnimePlaylistEpisodeUiModel) -> Unit = {},
    val onPauseEpisode: (ChimahonAnimePlaylistEpisodeUiModel) -> Unit = {},
    val onResumeEpisode: (ChimahonAnimePlaylistEpisodeUiModel) -> Unit = {},
    val onCancelEpisode: (ChimahonAnimePlaylistEpisodeUiModel) -> Unit = {},
    val onRemoveEpisode: (ChimahonAnimePlaylistEpisodeUiModel) -> Unit = {},
    val onMoveToTop: (ChimahonAnimePlaylistEpisodeUiModel) -> Unit = {},
    val onMoveToBottom: (ChimahonAnimePlaylistEpisodeUiModel) -> Unit = {},
)

enum class ChimahonAnimeQueueIntent {
    Watch,
    Download,
}

enum class ChimahonAnimeEpisodeAvailability {
    Local,
    Stream,
    Downloaded,
    Unavailable,
}

enum class ChimahonAnimeQueueStatus(val label: String) {
    Queued("Queued"),
    Preparing("Preparing"),
    Downloading("Downloading"),
    Ready("Ready"),
    Playing("Playing"),
    Paused("Paused"),
    Complete("Complete"),
    Failed("Failed"),
    Canceled("Canceled"),
}

enum class ChimahonAnimeQueuePriority(val label: String) {
    Low("Low"),
    Normal("Normal"),
    High("High"),
    Next("Play next"),
}

enum class ChimahonAnimeQueueSurfaceKind {
    WatchQueue,
    DownloadQueue,
    AutoNext,
}

enum class ChimahonAnimeQueueStatusChipKind {
    Info,
    Active,
    Success,
    Warning,
    Error,
    Muted,
}

enum class ChimahonAnimeQueueBadgeKind {
    Info,
    Active,
    Success,
    Warning,
    Error,
    Muted,
    Local,
    Stream,
}

enum class ChimahonAnimeQueueActionIcon {
    More,
    SelectAll,
    ClearSelection,
    MarkSeen,
    MarkUnseen,
    Download,
    Play,
    Pause,
    Resume,
    Retry,
    Cancel,
    Delete,
    Open,
    Remove,
    MoveTop,
    MoveUp,
    MoveDown,
    MoveBottom,
    Priority,
    Playlist,
    Stream,
    Local,
    Error,
}

fun ChimahonAnimeQueueStatus.toStatusChip(): ChimahonAnimeQueueStatusChipUiModel {
    val kind = when (this) {
        ChimahonAnimeQueueStatus.Queued -> ChimahonAnimeQueueStatusChipKind.Info
        ChimahonAnimeQueueStatus.Preparing -> ChimahonAnimeQueueStatusChipKind.Info
        ChimahonAnimeQueueStatus.Downloading -> ChimahonAnimeQueueStatusChipKind.Active
        ChimahonAnimeQueueStatus.Ready -> ChimahonAnimeQueueStatusChipKind.Success
        ChimahonAnimeQueueStatus.Playing -> ChimahonAnimeQueueStatusChipKind.Active
        ChimahonAnimeQueueStatus.Paused -> ChimahonAnimeQueueStatusChipKind.Warning
        ChimahonAnimeQueueStatus.Complete -> ChimahonAnimeQueueStatusChipKind.Success
        ChimahonAnimeQueueStatus.Failed -> ChimahonAnimeQueueStatusChipKind.Error
        ChimahonAnimeQueueStatus.Canceled -> ChimahonAnimeQueueStatusChipKind.Muted
    }
    return ChimahonAnimeQueueStatusChipUiModel(label, kind)
}

fun ChimahonAnimeEpisodeAvailability.toBadge(): ChimahonAnimeQueueBadgeUiModel {
    return when (this) {
        ChimahonAnimeEpisodeAvailability.Local -> ChimahonAnimeQueueBadgeUiModel("Local", ChimahonAnimeQueueBadgeKind.Local)
        ChimahonAnimeEpisodeAvailability.Stream -> ChimahonAnimeQueueBadgeUiModel("Stream", ChimahonAnimeQueueBadgeKind.Stream)
        ChimahonAnimeEpisodeAvailability.Downloaded -> ChimahonAnimeQueueBadgeUiModel("Offline", ChimahonAnimeQueueBadgeKind.Success)
        ChimahonAnimeEpisodeAvailability.Unavailable -> ChimahonAnimeQueueBadgeUiModel("Unavailable", ChimahonAnimeQueueBadgeKind.Error)
    }
}

fun ChimahonAnimeQueuePriority.toBadge(): ChimahonAnimeQueueBadgeUiModel {
    val kind = when (this) {
        ChimahonAnimeQueuePriority.Low -> ChimahonAnimeQueueBadgeKind.Muted
        ChimahonAnimeQueuePriority.Normal -> ChimahonAnimeQueueBadgeKind.Info
        ChimahonAnimeQueuePriority.High -> ChimahonAnimeQueueBadgeKind.Warning
        ChimahonAnimeQueuePriority.Next -> ChimahonAnimeQueueBadgeKind.Active
    }
    return ChimahonAnimeQueueBadgeUiModel(label, kind)
}

val ChimahonAnimeQueueStatus.isActive: Boolean
    get() = this in activeStatuses

val ChimahonAnimeQueueStatus.isTerminal: Boolean
    get() = this in terminalStatuses

val ChimahonAnimeQueueStatus.canPause: Boolean
    get() = this in pauseableStatuses

val ChimahonAnimeQueueStatus.canResume: Boolean
    get() = this == ChimahonAnimeQueueStatus.Paused

val ChimahonAnimeQueueStatus.canRetry: Boolean
    get() = this == ChimahonAnimeQueueStatus.Failed || this == ChimahonAnimeQueueStatus.Canceled

private val activeStatuses = setOf(
    ChimahonAnimeQueueStatus.Preparing,
    ChimahonAnimeQueueStatus.Downloading,
    ChimahonAnimeQueueStatus.Ready,
    ChimahonAnimeQueueStatus.Playing,
)

private val pauseableStatuses = setOf(
    ChimahonAnimeQueueStatus.Queued,
    ChimahonAnimeQueueStatus.Preparing,
    ChimahonAnimeQueueStatus.Downloading,
    ChimahonAnimeQueueStatus.Playing,
)

private val terminalStatuses = setOf(
    ChimahonAnimeQueueStatus.Complete,
    ChimahonAnimeQueueStatus.Canceled,
)
