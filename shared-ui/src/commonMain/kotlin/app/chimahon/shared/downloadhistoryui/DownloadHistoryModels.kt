package app.chimahon.shared.downloadhistoryui

enum class ChimahonMediaKind(
    val title: String,
    val entryTitle: String,
    val itemTitle: String,
    val unreadTitle: String,
) {
    Manga("Manga", "Manga", "Chapter", "Unread"),
    Anime("Anime", "Anime", "Episode", "Unseen"),
    LightNovel("Light novel", "Novel", "Section", "Unread"),
}

enum class ChimahonDownloadHistoryTab(val title: String) {
    Updates("Updates"),
    History("History"),
    Downloads("Downloads"),
}

enum class ChimahonDownloadHistoryGrouping(val title: String) {
    Date("Date"),
    DateAndSource("Date and source"),
    Source("Source"),
}

enum class ChimahonDownloadHistorySort(val title: String) {
    Newest("Newest"),
    Oldest("Oldest"),
    Title("Title"),
    Source("Source"),
}

enum class ChimahonDownloadHistorySheetRowKind {
    Media,
    Source,
    Language,
    Status,
    Grouping,
    Sort,
}

enum class ChimahonDownloadHistoryLoadState {
    Idle,
    Loading,
    Refreshing,
    Error,
}

enum class ChimahonDownloadHistoryChipKind {
    Source,
    Language,
    Media,
    Status,
    Warning,
    Local,
}

enum class ChimahonDownloadHistoryStatus(val label: String) {
    Queued("Queued"),
    Downloading("Downloading"),
    Paused("Paused"),
    Complete("Complete"),
    Failed("Failed"),
    Canceled("Canceled"),
    Read("Read"),
    Unread("Unread"),
    Seen("Seen"),
    Unseen("Unseen"),
}

enum class ChimahonDownloadHistoryActionId {
    Open,
    OpenSource,
    MarkRead,
    MarkUnread,
    Download,
    Pause,
    Resume,
    Retry,
    Remove,
    Delete,
    MoveTop,
    MoveUp,
    MoveDown,
    MoveBottom,
    Cancel,
    CopyTitle,
}

enum class ChimahonDownloadHistoryActionRole {
    Normal,
    Primary,
    Destructive,
}

enum class ChimahonDownloadHistoryActionIcon {
    More,
    Open,
    Source,
    MarkRead,
    MarkUnread,
    Download,
    Pause,
    Resume,
    Retry,
    Remove,
    Delete,
    MoveTop,
    MoveUp,
    MoveDown,
    MoveBottom,
    Cancel,
    Copy,
}

data class ChimahonDownloadHistoryChip(
    val text: String,
    val kind: ChimahonDownloadHistoryChipKind = ChimahonDownloadHistoryChipKind.Status,
    val selected: Boolean = false,
    val enabled: Boolean = true,
) {
    val stableKey: String
        get() = "${kind.name}:$text"
}

data class ChimahonDownloadHistoryProgress(
    val fraction: Float = 0f,
    val percentLabel: String? = null,
    val detailLabel: String? = null,
) {
    val normalizedFraction: Float
        get() = fraction.coerceIn(0f, 1f)

    val displayPercentLabel: String
        get() = percentLabel ?: "${(normalizedFraction * 100f).toInt()}%"
}

data class ChimahonDownloadHistoryAction(
    val id: ChimahonDownloadHistoryActionId,
    val label: String,
    val icon: ChimahonDownloadHistoryActionIcon,
    val enabled: Boolean = true,
    val role: ChimahonDownloadHistoryActionRole = ChimahonDownloadHistoryActionRole.Normal,
    val onClick: () -> Unit,
) {
    val destructive: Boolean
        get() = role == ChimahonDownloadHistoryActionRole.Destructive
}

data class ChimahonDownloadHistoryBatchAction(
    val id: String,
    val label: String,
    val icon: ChimahonDownloadHistoryActionIcon,
    val enabled: Boolean = true,
    val role: ChimahonDownloadHistoryActionRole = ChimahonDownloadHistoryActionRole.Normal,
    val onClick: () -> Unit,
)

data class ChimahonDownloadHistoryBatchBarState(
    val selectedCount: Int,
    val totalCount: Int,
    val title: String = "$selectedCount selected",
    val allSelected: Boolean = totalCount > 0 && selectedCount == totalCount,
    val primaryActions: List<ChimahonDownloadHistoryBatchAction> = emptyList(),
    val overflowActions: List<ChimahonDownloadHistoryAction> = emptyList(),
) {
    val visible: Boolean
        get() = selectedCount > 0
}

data class ChimahonDownloadHistorySheetRow(
    val stableKey: String,
    val title: String,
    val kind: ChimahonDownloadHistorySheetRowKind,
    val selected: Boolean = false,
    val enabled: Boolean = true,
    val subtitle: String? = null,
)

sealed interface ChimahonDownloadHistoryRow {
    val id: String
    val mediaKind: ChimahonMediaKind
    val entryId: String
    val entryTitle: String
    val itemId: String?
    val itemTitle: String?
    val thumbnailUrl: String?
    val sourceName: String?
    val language: String?
    val selected: Boolean
    val chips: List<ChimahonDownloadHistoryChip>
    val actions: List<ChimahonDownloadHistoryAction>

    val stableKey: String
        get() = "${mediaKind.name}:$id:$entryId:${itemId.orEmpty()}"

    fun stableLazyKey(index: Int): String
}

data class ChimahonUpdateRowUiModel(
    override val id: String,
    override val mediaKind: ChimahonMediaKind,
    override val entryId: String,
    override val entryTitle: String,
    override val itemId: String? = null,
    override val itemTitle: String? = null,
    val updateGroupLabel: String? = null,
    val dateLabel: String? = null,
    val newItemCount: Int = 1,
    val unreadCount: Int = 0,
    val downloadedCount: Int = 0,
    val bookmarked: Boolean = false,
    override val thumbnailUrl: String? = null,
    override val sourceName: String? = null,
    override val language: String? = null,
    override val selected: Boolean = false,
    override val chips: List<ChimahonDownloadHistoryChip> = emptyList(),
    override val actions: List<ChimahonDownloadHistoryAction> = emptyList(),
    val sourceId: Long? = null,
    val fetchedAt: Long? = null,
) : ChimahonDownloadHistoryRow {
    val countLabel: String
        get() = when (newItemCount) {
            0 -> "No ${mediaKind.itemTitle.lowercase()}s"
            1 -> "1 ${mediaKind.itemTitle.lowercase()}"
            else -> "$newItemCount ${mediaKind.itemTitle.lowercase()}s"
        }

    val statusChips: List<ChimahonDownloadHistoryChip>
        get() = buildList {
            add(ChimahonDownloadHistoryChip(mediaKind.title, ChimahonDownloadHistoryChipKind.Media))
            if (!language.isNullOrBlank()) {
                add(ChimahonDownloadHistoryChip(language.uppercase(), ChimahonDownloadHistoryChipKind.Language))
            }
            if (!sourceName.isNullOrBlank()) {
                add(ChimahonDownloadHistoryChip(sourceName, ChimahonDownloadHistoryChipKind.Source))
            }
            if (unreadCount > 0) {
                add(ChimahonDownloadHistoryChip("$unreadCount ${mediaKind.unreadTitle.lowercase()}", ChimahonDownloadHistoryChipKind.Status))
            }
            if (downloadedCount > 0) {
                add(ChimahonDownloadHistoryChip("$downloadedCount downloaded", ChimahonDownloadHistoryChipKind.Status))
            }
            if (bookmarked) {
                add(ChimahonDownloadHistoryChip("Bookmarked", ChimahonDownloadHistoryChipKind.Status))
            }
            addAll(chips)
        }

    override fun stableLazyKey(index: Int): String {
        return "update:${stableKey.ifBlank { index.toString() }}"
    }
}

data class ChimahonHistoryRowUiModel(
    override val id: String,
    override val mediaKind: ChimahonMediaKind,
    override val entryId: String,
    override val entryTitle: String,
    override val itemId: String? = null,
    override val itemTitle: String? = null,
    val consumedAtLabel: String,
    val progressLabel: String? = null,
    val durationLabel: String? = null,
    val continueLabel: String? = null,
    val inLibrary: Boolean = true,
    val completed: Boolean = false,
    override val thumbnailUrl: String? = null,
    override val sourceName: String? = null,
    override val language: String? = null,
    override val selected: Boolean = false,
    override val chips: List<ChimahonDownloadHistoryChip> = emptyList(),
    override val actions: List<ChimahonDownloadHistoryAction> = emptyList(),
    val sourceId: Long? = null,
    val consumedAt: Long? = null,
) : ChimahonDownloadHistoryRow {
    val detailLabel: String
        get() = listOfNotNull(consumedAtLabel, progressLabel, durationLabel, sourceName)
            .joinToString(" - ")

    val statusChips: List<ChimahonDownloadHistoryChip>
        get() = buildList {
            add(ChimahonDownloadHistoryChip(mediaKind.title, ChimahonDownloadHistoryChipKind.Media))
            if (!language.isNullOrBlank()) {
                add(ChimahonDownloadHistoryChip(language.uppercase(), ChimahonDownloadHistoryChipKind.Language))
            }
            if (!inLibrary) {
                add(ChimahonDownloadHistoryChip("Not in library", ChimahonDownloadHistoryChipKind.Warning))
            }
            if (completed) {
                add(ChimahonDownloadHistoryChip("Complete", ChimahonDownloadHistoryChipKind.Status))
            }
            addAll(chips)
        }

    override fun stableLazyKey(index: Int): String {
        return "history:${stableKey.ifBlank { index.toString() }}"
    }
}

data class ChimahonDownloadQueueRowUiModel(
    override val id: String,
    override val mediaKind: ChimahonMediaKind,
    override val entryId: String,
    override val entryTitle: String,
    override val itemId: String? = null,
    override val itemTitle: String? = null,
    val queueIndex: Int = 0,
    val status: ChimahonDownloadHistoryStatus = ChimahonDownloadHistoryStatus.Queued,
    val progress: ChimahonDownloadHistoryProgress = ChimahonDownloadHistoryProgress(),
    val sizeLabel: String? = null,
    val speedLabel: String? = null,
    val etaLabel: String? = null,
    val errorMessage: String? = null,
    val canMove: Boolean = true,
    override val thumbnailUrl: String? = null,
    override val sourceName: String? = null,
    override val language: String? = null,
    override val selected: Boolean = false,
    override val chips: List<ChimahonDownloadHistoryChip> = emptyList(),
    override val actions: List<ChimahonDownloadHistoryAction> = emptyList(),
    val sourceId: Long? = null,
) : ChimahonDownloadHistoryRow {
    val detailLabel: String?
        get() = listOfNotNull(sizeLabel, speedLabel, etaLabel, errorMessage).joinToString(" - ").ifBlank { null }

    val active: Boolean
        get() = status == ChimahonDownloadHistoryStatus.Downloading

    val failed: Boolean
        get() = status == ChimahonDownloadHistoryStatus.Failed

    val statusChips: List<ChimahonDownloadHistoryChip>
        get() = buildList {
            add(ChimahonDownloadHistoryChip("#${queueIndex + 1}", ChimahonDownloadHistoryChipKind.Status))
            add(ChimahonDownloadHistoryChip(mediaKind.title, ChimahonDownloadHistoryChipKind.Media))
            add(ChimahonDownloadHistoryChip(status.label, status.toChipKind()))
            if (!language.isNullOrBlank()) {
                add(ChimahonDownloadHistoryChip(language.uppercase(), ChimahonDownloadHistoryChipKind.Language))
            }
            if (!sourceName.isNullOrBlank()) {
                add(ChimahonDownloadHistoryChip(sourceName, ChimahonDownloadHistoryChipKind.Source))
            }
            addAll(chips)
        }

    override fun stableLazyKey(index: Int): String {
        return "download:${stableKey.ifBlank { index.toString() }}:$queueIndex"
    }
}

data class ChimahonDownloadHistoryGroup<T : ChimahonDownloadHistoryRow>(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val rows: List<T>,
) {
    val stableKey: String
        get() = id

    fun stableLazyKey(index: Int): String {
        return "group:${stableKey.ifBlank { index.toString() }}"
    }
}

data class ChimahonDownloadHistoryFilterState(
    val selectedMediaKinds: Set<ChimahonMediaKind> = emptySet(),
    val sourceChips: List<ChimahonDownloadHistoryChip> = emptyList(),
    val languageChips: List<ChimahonDownloadHistoryChip> = emptyList(),
    val statusChips: List<ChimahonDownloadHistoryChip> = emptyList(),
    val query: String = "",
    val grouping: ChimahonDownloadHistoryGrouping = ChimahonDownloadHistoryGrouping.Date,
    val sort: ChimahonDownloadHistorySort = ChimahonDownloadHistorySort.Newest,
) {
    val hasActiveFilters: Boolean
        get() = selectedMediaKinds.isNotEmpty() ||
            sourceChips.any { it.selected } ||
            languageChips.any { it.selected } ||
            statusChips.any { it.selected } ||
            query.isNotBlank()
}

data class ChimahonDownloadHistoryUiState(
    val selectedTab: ChimahonDownloadHistoryTab = ChimahonDownloadHistoryTab.Updates,
    val updates: List<ChimahonUpdateRowUiModel> = emptyList(),
    val history: List<ChimahonHistoryRowUiModel> = emptyList(),
    val downloads: List<ChimahonDownloadQueueRowUiModel> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val filterState: ChimahonDownloadHistoryFilterState = ChimahonDownloadHistoryFilterState(),
    val loadState: ChimahonDownloadHistoryLoadState = ChimahonDownloadHistoryLoadState.Idle,
    val errorMessage: String? = null,
    val refreshingLabel: String? = null,
) {
    val selectedCount: Int
        get() = selectedIds.size

    val selectionMode: Boolean
        get() = selectedIds.isNotEmpty()

    val visibleRowCount: Int
        get() = when (selectedTab) {
            ChimahonDownloadHistoryTab.Updates -> updates.size
            ChimahonDownloadHistoryTab.History -> history.size
            ChimahonDownloadHistoryTab.Downloads -> downloads.size
        }
}

fun ChimahonDownloadHistoryStatus.toChipKind(): ChimahonDownloadHistoryChipKind {
    return when (this) {
        ChimahonDownloadHistoryStatus.Downloading,
        ChimahonDownloadHistoryStatus.Complete,
        ChimahonDownloadHistoryStatus.Read,
        ChimahonDownloadHistoryStatus.Seen -> ChimahonDownloadHistoryChipKind.Status
        ChimahonDownloadHistoryStatus.Failed,
        ChimahonDownloadHistoryStatus.Canceled -> ChimahonDownloadHistoryChipKind.Warning
        ChimahonDownloadHistoryStatus.Paused -> ChimahonDownloadHistoryChipKind.Warning
        ChimahonDownloadHistoryStatus.Queued,
        ChimahonDownloadHistoryStatus.Unread,
        ChimahonDownloadHistoryStatus.Unseen -> ChimahonDownloadHistoryChipKind.Status
    }
}

fun ChimahonDownloadHistoryFilterState.groupingRows(): List<ChimahonDownloadHistorySheetRow> {
    return ChimahonDownloadHistoryGrouping.entries.map {
        ChimahonDownloadHistorySheetRow(
            stableKey = "download-history:grouping:${it.name}",
            title = it.title,
            kind = ChimahonDownloadHistorySheetRowKind.Grouping,
            selected = grouping == it,
        )
    }
}

fun ChimahonDownloadHistoryFilterState.sortRows(): List<ChimahonDownloadHistorySheetRow> {
    return ChimahonDownloadHistorySort.entries.map {
        ChimahonDownloadHistorySheetRow(
            stableKey = "download-history:sort:${it.name}",
            title = it.title,
            kind = ChimahonDownloadHistorySheetRowKind.Sort,
            selected = sort == it,
        )
    }
}
