package app.chimahon.shared.queueui

data class ChimahonUpdatesUiState(
    val title: String = "Updates",
    val subtitle: String? = null,
    val rows: List<ChimahonUpdateFeedRowUiModel> = emptyList(),
    val selectedRowIds: Set<String> = emptySet(),
    val refreshing: Boolean = false,
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val lastUpdatedLabel: String? = null,
) {
    val selectedCount: Int
        get() = selectedRowIds.size

    val isSelectionMode: Boolean
        get() = selectedRowIds.isNotEmpty()
}

data class ChimahonHistoryUiState(
    val title: String = "History",
    val subtitle: String? = null,
    val rows: List<ChimahonHistoryRowUiModel> = emptyList(),
    val selectedRowIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val loading: Boolean = false,
    val errorMessage: String? = null,
) {
    val selectedCount: Int
        get() = selectedRowIds.size

    val isSelectionMode: Boolean
        get() = selectedRowIds.isNotEmpty()
}

data class ChimahonDownloadsUiState(
    val title: String = "Downloads",
    val subtitle: String? = null,
    val rows: List<ChimahonDownloadQueueRowUiModel> = emptyList(),
    val selectedRowIds: Set<String> = emptySet(),
    val paused: Boolean = false,
    val activeCount: Int = 0,
    val queuedCount: Int = 0,
    val completedCount: Int = 0,
    val loading: Boolean = false,
    val errorMessage: String? = null,
) {
    val selectedCount: Int
        get() = selectedRowIds.size

    val isSelectionMode: Boolean
        get() = selectedRowIds.isNotEmpty()
}

data class ChimahonUpdateFeedRowUiModel(
    val id: String,
    val mangaTitle: String,
    val subtitle: String? = null,
    val sourceName: String? = null,
    val thumbnailUrl: String? = null,
    val updateHeader: String? = null,
    val chapters: List<ChimahonQueueChapterUiModel> = emptyList(),
    val unreadCount: Int = 0,
    val downloadedCount: Int = 0,
    val selected: Boolean = false,
    val overflowActions: List<ChimahonQueueOverflowAction> = emptyList(),
) {
    val chapterCountLabel: String
        get() = when (chapters.size) {
            0 -> "No chapters"
            1 -> "1 chapter"
            else -> "${chapters.size} chapters"
        }
}

data class ChimahonHistoryRowUiModel(
    val id: String,
    val mangaTitle: String,
    val chapterTitle: String,
    val readAtLabel: String,
    val progressLabel: String? = null,
    val sourceName: String? = null,
    val thumbnailUrl: String? = null,
    val selected: Boolean = false,
    val overflowActions: List<ChimahonQueueOverflowAction> = emptyList(),
)

data class ChimahonDownloadQueueRowUiModel(
    val id: String,
    val mangaTitle: String,
    val chapterTitle: String,
    val subtitle: String? = null,
    val sourceName: String? = null,
    val thumbnailUrl: String? = null,
    val progress: ChimahonQueueProgress = ChimahonQueueProgress(),
    val status: ChimahonDownloadStatus = ChimahonDownloadStatus.Queued,
    val sizeLabel: String? = null,
    val speedLabel: String? = null,
    val etaLabel: String? = null,
    val selected: Boolean = false,
    val overflowActions: List<ChimahonQueueOverflowAction> = emptyList(),
) {
    val detailLabel: String?
        get() = listOfNotNull(sizeLabel, speedLabel, etaLabel).joinToString("  |  ").ifBlank { null }
}

data class ChimahonQueueChapterUiModel(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val chapterNumberLabel: String? = null,
    val scanlator: String? = null,
    val dateLabel: String? = null,
    val checked: Boolean = false,
    val checkboxEnabled: Boolean = true,
    val read: Boolean = false,
    val bookmarked: Boolean = false,
    val downloaded: Boolean = false,
    val selected: Boolean = false,
    val status: ChimahonChapterQueueStatus = ChimahonChapterQueueStatus.None,
    val progress: ChimahonQueueProgress? = null,
    val overflowActions: List<ChimahonQueueOverflowAction> = emptyList(),
)

data class ChimahonQueueProgress(
    val fraction: Float = 0f,
    val percentLabel: String? = null,
    val statusLabel: String? = null,
) {
    val normalizedFraction: Float
        get() = fraction.coerceIn(0f, 1f)

    val displayPercentLabel: String
        get() = percentLabel ?: "${(normalizedFraction * 100f).toInt()}%"
}

data class ChimahonQueueStatusChipUiModel(
    val text: String,
    val kind: ChimahonQueueStatusChipKind = ChimahonQueueStatusChipKind.Info,
)

data class ChimahonQueueOverflowAction(
    val id: String,
    val label: String,
    val icon: ChimahonQueueActionIcon = ChimahonQueueActionIcon.More,
    val enabled: Boolean = true,
    val destructive: Boolean = false,
    val onClick: () -> Unit,
)

data class ChimahonQueueBatchAction(
    val id: String,
    val label: String,
    val icon: ChimahonQueueActionIcon,
    val enabled: Boolean = true,
    val destructive: Boolean = false,
    val onClick: () -> Unit,
)

data class ChimahonQueueBatchActionBarState(
    val selectedCount: Int,
    val totalCount: Int,
    val title: String = "$selectedCount selected",
    val allSelected: Boolean = totalCount > 0 && selectedCount == totalCount,
    val primaryActions: List<ChimahonQueueBatchAction> = emptyList(),
    val overflowActions: List<ChimahonQueueOverflowAction> = emptyList(),
)

enum class ChimahonQueueSurfaceKind {
    Updates,
    History,
    Downloads,
}

enum class ChimahonDownloadStatus(val label: String) {
    Queued("Queued"),
    Downloading("Downloading"),
    Paused("Paused"),
    Complete("Complete"),
    Failed("Failed"),
    Canceled("Canceled"),
}

enum class ChimahonChapterQueueStatus(val label: String) {
    None(""),
    Queued("Queued"),
    Downloading("Downloading"),
    Downloaded("Downloaded"),
    Read("Read"),
    Unread("Unread"),
    Failed("Failed"),
}

enum class ChimahonQueueStatusChipKind {
    Info,
    Active,
    Success,
    Warning,
    Error,
    Muted,
}

enum class ChimahonQueueActionIcon {
    More,
    SelectAll,
    ClearSelection,
    MarkRead,
    MarkUnread,
    Download,
    Pause,
    Resume,
    Retry,
    Cancel,
    Delete,
    Open,
    Remove,
}

fun ChimahonDownloadStatus.toStatusChip(): ChimahonQueueStatusChipUiModel {
    val kind = when (this) {
        ChimahonDownloadStatus.Queued -> ChimahonQueueStatusChipKind.Info
        ChimahonDownloadStatus.Downloading -> ChimahonQueueStatusChipKind.Active
        ChimahonDownloadStatus.Paused -> ChimahonQueueStatusChipKind.Warning
        ChimahonDownloadStatus.Complete -> ChimahonQueueStatusChipKind.Success
        ChimahonDownloadStatus.Failed -> ChimahonQueueStatusChipKind.Error
        ChimahonDownloadStatus.Canceled -> ChimahonQueueStatusChipKind.Muted
    }
    return ChimahonQueueStatusChipUiModel(label, kind)
}

fun ChimahonChapterQueueStatus.toStatusChip(): ChimahonQueueStatusChipUiModel? {
    if (this == ChimahonChapterQueueStatus.None) return null
    val kind = when (this) {
        ChimahonChapterQueueStatus.None -> ChimahonQueueStatusChipKind.Muted
        ChimahonChapterQueueStatus.Queued -> ChimahonQueueStatusChipKind.Info
        ChimahonChapterQueueStatus.Downloading -> ChimahonQueueStatusChipKind.Active
        ChimahonChapterQueueStatus.Downloaded -> ChimahonQueueStatusChipKind.Success
        ChimahonChapterQueueStatus.Read -> ChimahonQueueStatusChipKind.Muted
        ChimahonChapterQueueStatus.Unread -> ChimahonQueueStatusChipKind.Info
        ChimahonChapterQueueStatus.Failed -> ChimahonQueueStatusChipKind.Error
    }
    return ChimahonQueueStatusChipUiModel(label, kind)
}
