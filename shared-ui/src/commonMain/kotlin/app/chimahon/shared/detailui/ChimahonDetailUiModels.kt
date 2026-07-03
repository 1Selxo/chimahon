package app.chimahon.shared.detailui

enum class ChimahonDetailKind {
    Library,
    Remote,
}

data class ChimahonDetailHeaderModel(
    val id: String,
    val kind: ChimahonDetailKind,
    val title: String,
    val sourceName: String,
    val thumbnailUrl: String? = null,
    val author: String? = null,
    val artist: String? = null,
    val description: String? = null,
    val genres: List<String> = emptyList(),
    val status: String = "",
    val url: String? = null,
    val favorite: Boolean = false,
    val initialized: Boolean = true,
    val inLibrary: Boolean = favorite,
    val notes: String = "",
) {
    val creatorLine: String?
        get() = listOfNotNull(
            author?.takeIf(String::isNotBlank),
            artist?.takeIf { it.isNotBlank() && it != author },
        ).joinToString(" / ").takeIf(String::isNotBlank)

    val displayStatus: String
        get() = status.ifBlank { "Unknown status" }

    val sourceUrl: String?
        get() = url?.takeIf(String::isNotBlank)
}

data class ChimahonDetailChapterSummary(
    val totalCount: Int = 0,
    val readCount: Int = 0,
    val unreadCount: Int = 0,
    val downloadedCount: Int = 0,
    val bookmarkedCount: Int = 0,
    val selectedCount: Int = 0,
    val latestUploadEpochMillis: Long? = null,
) {
    val hasChapters: Boolean
        get() = totalCount > 0
}

data class ChimahonDetailActionModel(
    val action: ChimahonDetailAction,
    val label: String,
    val selected: Boolean = false,
    val enabled: Boolean = true,
    val supportingLabel: String? = null,
)

enum class ChimahonDetailAction {
    Favorite,
    Start,
    Resume,
    Tracking,
    Web,
    Refresh,
    Download,
    MarkRead,
    AddToLibrary,
    MarkUnread,
    Bookmark,
    Select,
}

data class ChimahonDetailChipModel(
    val label: String,
    val kind: ChimahonDetailChipKind = ChimahonDetailChipKind.Neutral,
) {
    val stableKey: String
        get() = "detail-chip:$kind:$label"
}

enum class ChimahonDetailChipKind {
    Status,
    Source,
    Creator,
    Genre,
    Library,
    Count,
    Neutral,
    Url,
    Warning,
}

data class ChimahonDetailChapterControlsState(
    val summary: ChimahonDetailChapterSummary,
    val query: String = "",
    val sortLabel: String = "Source order",
    val filterLabel: String = "All",
    val displayLabel: String = "Source title",
    val descending: Boolean = true,
    val filtersVisible: Boolean = false,
    val sortState: ChimahonDetailChapterSortState = ChimahonDetailChapterSortState(descending = descending),
    val filterState: ChimahonDetailChapterFilterState = ChimahonDetailChapterFilterState(),
    val displayMode: ChimahonDetailChapterDisplayMode = ChimahonDetailChapterDisplayMode.SourceTitle,
    val selectionState: ChimahonDetailChapterSelectionState = ChimahonDetailChapterSelectionState(
        selectedCount = summary.selectedCount,
        totalCount = summary.totalCount,
    ),
    val bulkActions: List<ChimahonDetailChapterBulkActionModel> =
        defaultChimahonDetailChapterBulkActions(selectionState),
)

data class ChimahonDetailChapterSortState(
    val sort: ChimahonDetailChapterSort = ChimahonDetailChapterSort.SourceOrder,
    val descending: Boolean = true,
) {
    val directionTitle: String
        get() = when (sort) {
            ChimahonDetailChapterSort.SourceOrder -> if (descending) "Source order" else "Reverse source order"
            ChimahonDetailChapterSort.ChapterNumber -> if (descending) "Highest first" else "Lowest first"
            ChimahonDetailChapterSort.UploadDate -> if (descending) "Newest first" else "Oldest first"
            ChimahonDetailChapterSort.Name -> if (descending) "Z-A" else "A-Z"
            ChimahonDetailChapterSort.Scanlator -> if (descending) "Z-A groups" else "A-Z groups"
        }

    fun withSort(newSort: ChimahonDetailChapterSort): ChimahonDetailChapterSortState {
        return copy(sort = newSort)
    }

    fun toggledDirection(): ChimahonDetailChapterSortState {
        return copy(descending = !descending)
    }
}

enum class ChimahonDetailChapterSort(val title: String) {
    SourceOrder("Source order"),
    ChapterNumber("Chapter number"),
    UploadDate("Upload date"),
    Name("Chapter name"),
    Scanlator("Scanlator"),
}

enum class ChimahonDetailChapterDisplayMode(val title: String) {
    SourceTitle("Source title"),
    ChapterNumber("Chapter number"),
}

data class ChimahonDetailChapterFilterState(
    val read: ChimahonDetailReadFilter = ChimahonDetailReadFilter.Any,
    val downloaded: ChimahonDetailDownloadFilter = ChimahonDetailDownloadFilter.Any,
    val bookmarked: ChimahonDetailBookmarkFilter = ChimahonDetailBookmarkFilter.Any,
    val scanlator: String? = null,
) {
    val activeCount: Int
        get() = activeLabels().size

    val hasActiveFilters: Boolean
        get() = activeCount > 0

    val label: String
        get() = activeLabels().takeIf { it.isNotEmpty() }?.joinToString(" / ") ?: "All"

    fun activeLabels(): List<String> {
        return buildList {
            if (read != ChimahonDetailReadFilter.Any) add(read.title)
            if (downloaded != ChimahonDetailDownloadFilter.Any) add(downloaded.title)
            if (bookmarked != ChimahonDetailBookmarkFilter.Any) add(bookmarked.title)
            scanlator?.takeIf(String::isNotBlank)?.let { add(it) }
        }
    }
}

enum class ChimahonDetailReadFilter(val title: String) {
    Any("All"),
    Unread("Unread"),
    Read("Read"),
}

enum class ChimahonDetailDownloadFilter(val title: String) {
    Any("Any download"),
    Downloaded("Downloaded"),
    NotDownloaded("Not downloaded"),
}

enum class ChimahonDetailBookmarkFilter(val title: String) {
    Any("Any bookmark"),
    Bookmarked("Bookmarked"),
    NotBookmarked("Not bookmarked"),
}

data class ChimahonDetailChapterSelectionState(
    val selectedCount: Int = 0,
    val totalCount: Int = 0,
) {
    val hasSelection: Boolean
        get() = selectedCount > 0

    val canSelectAll: Boolean
        get() = totalCount > 0 && selectedCount < totalCount

    val allSelected: Boolean
        get() = totalCount > 0 && selectedCount >= totalCount
}

data class ChimahonDetailChapterBulkActionModel(
    val action: ChimahonDetailChapterBulkAction,
    val label: String = action.title,
    val selected: Boolean = false,
    val enabled: Boolean = true,
)

enum class ChimahonDetailChapterBulkAction(val title: String) {
    Download("Download"),
    MarkRead("Mark read"),
    MarkUnread("Mark unread"),
    Bookmark("Bookmark"),
    RemoveBookmark("Remove bookmark"),
    SelectAll("Select all"),
    ClearSelection("Clear"),
}

data class ChimahonDetailChapterRowModel(
    val id: String,
    val title: String,
    val url: String = id,
    val sourceOrder: Int = Int.MAX_VALUE,
    val chapterNumber: Double = -1.0,
    val dateUploadEpochMillis: Long? = null,
    val dateLabel: String? = null,
    val readProgressLabel: String? = null,
    val scanlator: String? = null,
    val sourceName: String? = null,
    val read: Boolean = false,
    val bookmarked: Boolean = false,
    val downloaded: Boolean = false,
    val downloadState: ChimahonDetailChapterDownloadState = ChimahonDetailChapterDownloadState.NotDownloaded,
    val downloadProgress: Int = 0,
) {
    val stableRowKey: String
        get() = stableRowIdentity().takeIf(String::isNotBlank)?.let { "detail-chapter:$it" }
            ?: "detail-chapter:$id"

    fun stableLazyKey(index: Int): String {
        val identity = stableRowIdentity()
        return if (identity.isNotBlank()) {
            "detail-chapter:$identity"
        } else {
            "detail-chapter:index:$index"
        }
    }

    fun displayTitle(mode: ChimahonDetailChapterDisplayMode): String {
        return when {
            mode == ChimahonDetailChapterDisplayMode.ChapterNumber && chapterNumber > 0.0 ->
                "Chapter ${chapterNumber.toDisplayChapter()}"
            else -> title.ifBlank { "Chapter" }
        }
    }

    fun rowActions(selected: Boolean): List<ChimahonDetailChapterRowActionModel> {
        return listOf(
            ChimahonDetailChapterRowActionModel(
                action = ChimahonDetailChapterRowAction.Select,
                label = if (selected) "Selected" else "Select",
                selected = selected,
            ),
            ChimahonDetailChapterRowActionModel(
                action = if (read) {
                    ChimahonDetailChapterRowAction.MarkUnread
                } else {
                    ChimahonDetailChapterRowAction.MarkRead
                },
                label = if (read) "Mark unread" else "Mark read",
                selected = read,
            ),
            ChimahonDetailChapterRowActionModel(
                action = if (bookmarked) {
                    ChimahonDetailChapterRowAction.RemoveBookmark
                } else {
                    ChimahonDetailChapterRowAction.Bookmark
                },
                label = if (bookmarked) "Bookmarked" else "Bookmark",
                selected = bookmarked,
            ),
            ChimahonDetailChapterRowActionModel(
                action = ChimahonDetailChapterRowAction.Download,
                label = downloadState.title,
                selected = downloaded || downloadState == ChimahonDetailChapterDownloadState.Downloaded,
            ),
        )
    }

    private fun stableRowIdentity(): String {
        return buildList {
            id.takeIf(String::isNotBlank)?.let { add("id=$it") }
            url.takeIf { it.isNotBlank() && it != id }?.let { add("url=$it") }
            if (sourceOrder != Int.MAX_VALUE) add("sourceOrder=$sourceOrder")
            if (chapterNumber > 0.0) add("chapter=${chapterNumber.toDisplayChapter()}")
            title.takeIf(String::isNotBlank)?.let { add("title=$it") }
        }.joinToString("|")
    }
}

data class ChimahonDetailChapterRowActionModel(
    val action: ChimahonDetailChapterRowAction,
    val label: String = action.title,
    val selected: Boolean = false,
    val enabled: Boolean = true,
)

enum class ChimahonDetailChapterRowAction(val title: String) {
    Download("Download"),
    MarkRead("Mark read"),
    MarkUnread("Mark unread"),
    Bookmark("Bookmark"),
    RemoveBookmark("Remove bookmark"),
    Select("Select"),
}

enum class ChimahonDetailChapterDownloadState(val title: String) {
    NotDownloaded("Not downloaded"),
    Queued("Queued"),
    Downloading("Downloading"),
    Downloaded("Downloaded"),
    Error("Download error"),
}

data class ChimahonDetailRelatedActionModel(
    val action: ChimahonDetailRelatedAction,
    val label: String,
    val enabled: Boolean = true,
    val supportingLabel: String? = null,
    val url: String? = null,
)

enum class ChimahonDetailRelatedAction {
    BrowseSource,
    OpenInWebView,
    OpenExternal,
    Recommendations,
    Merge,
    Similar,
    CopyUrl,
}

data class ChimahonDetailStateMessage(
    val title: String,
    val body: String? = null,
    val primaryActionLabel: String? = null,
)

fun defaultChimahonDetailChapterBulkActions(
    selectionState: ChimahonDetailChapterSelectionState,
): List<ChimahonDetailChapterBulkActionModel> {
    return buildList {
        if (selectionState.totalCount > 0) {
            add(
                ChimahonDetailChapterBulkActionModel(
                    action = ChimahonDetailChapterBulkAction.SelectAll,
                    selected = selectionState.allSelected,
                    enabled = selectionState.canSelectAll,
                ),
            )
        }
        if (selectionState.hasSelection) {
            add(ChimahonDetailChapterBulkActionModel(ChimahonDetailChapterBulkAction.Download))
            add(ChimahonDetailChapterBulkActionModel(ChimahonDetailChapterBulkAction.MarkRead))
            add(ChimahonDetailChapterBulkActionModel(ChimahonDetailChapterBulkAction.MarkUnread))
            add(ChimahonDetailChapterBulkActionModel(ChimahonDetailChapterBulkAction.Bookmark))
            add(ChimahonDetailChapterBulkActionModel(ChimahonDetailChapterBulkAction.RemoveBookmark))
            add(ChimahonDetailChapterBulkActionModel(ChimahonDetailChapterBulkAction.ClearSelection))
        }
    }
}

private fun Double.toDisplayChapter(): String {
    if (this % 1.0 == 0.0) return toLong().toString()
    return toString().trimEnd('0').trimEnd('.')
}
