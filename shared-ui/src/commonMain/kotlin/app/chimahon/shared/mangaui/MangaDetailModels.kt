package app.chimahon.shared.mangaui

data class ChimahonMangaDetailUiState(
    val manga: ChimahonMangaHeaderUiModel? = null,
    val chapters: List<ChimahonMangaChapterUiModel> = emptyList(),
    val sortState: ChimahonMangaChapterSortState = ChimahonMangaChapterSortState(),
    val filterState: ChimahonMangaChapterFilterState = ChimahonMangaChapterFilterState(),
    val displayMode: ChimahonMangaChapterDisplayMode = ChimahonMangaChapterDisplayMode.SourceTitle,
    val chapterQuery: String = "",
    val selectedChapterIds: Set<String> = emptySet(),
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val errorMessage: String? = null,
    val missingChapterCount: Int = 0,
) {
    val visibleChapters: List<ChimahonMangaChapterUiModel>
        get() = chapters
            .filter { it.matches(filterState, chapterQuery) }
            .sortedForMangaDetail(sortState)

    val contentState: ChimahonMangaDetailContentState
        get() = when {
            loading && manga == null -> ChimahonMangaDetailContentState.Loading
            errorMessage != null && manga == null -> ChimahonMangaDetailContentState.Error
            manga == null -> ChimahonMangaDetailContentState.Empty
            else -> ChimahonMangaDetailContentState.Content
        }

    val activeSelectionCount: Int
        get() = selectedChapterIds.size

    val chapterSelectionState: ChimahonMangaChapterSelectionState
        get() {
            val visible = visibleChapters
            return ChimahonMangaChapterSelectionState(
                selectedCount = visible.count { it.id in selectedChapterIds },
                totalCount = visible.size,
            )
        }
}

data class ChimahonMangaHeaderUiModel(
    val id: String,
    val title: String,
    val sourceName: String,
    val thumbnailUrl: String? = null,
    val author: String? = null,
    val artist: String? = null,
    val status: String? = null,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val favorite: Boolean = false,
    val trackedCount: Int = 0,
    val nextUpdateLabel: String? = null,
    val inLibrary: Boolean = favorite,
    val sourceIncognito: Boolean = false,
    val localSource: Boolean = false,
    val webUrl: String? = null,
    val unreadCount: Int = 0,
    val downloadedCount: Int = 0,
    val totalChapterCount: Int = 0,
) {
    val creatorLine: String?
        get() = listOfNotNull(author?.takeIf(String::isNotBlank), artist?.takeIf(String::isNotBlank))
            .distinct()
            .joinToString(" / ")
            .takeIf(String::isNotBlank)

    val sourceLine: String
        get() = buildList {
            add(sourceName)
            if (sourceIncognito) add("Incognito")
            if (localSource) add("Local")
        }.joinToString(" / ")

    val sourceUrl: String?
        get() = webUrl?.takeIf(String::isNotBlank)
}

data class ChimahonMangaChapterUiModel(
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
    val downloadState: ChimahonChapterDownloadUiState = ChimahonChapterDownloadUiState.NotDownloaded,
    val downloadProgress: Int = 0,
    val isOcrReady: Boolean = false,
    val isOcrRunning: Boolean = false,
) {
    val chapterMarker: String
        get() = if (chapterNumber > 0.0) chapterNumber.toDisplayChapter() else "C"

    fun displayTitle(mode: ChimahonMangaChapterDisplayMode): String {
        return when {
            mode == ChimahonMangaChapterDisplayMode.ChapterNumber && chapterNumber > 0.0 ->
                "Chapter ${chapterNumber.toDisplayChapter()}"
            else -> title.ifBlank { "Chapter $chapterMarker" }
        }
    }

    fun metadataParts(showSource: Boolean = true): List<String> {
        return listOfNotNull(
            dateLabel?.takeIf(String::isNotBlank),
            readProgressLabel?.takeIf(String::isNotBlank),
            sourceName?.takeIf { showSource && it.isNotBlank() },
            scanlator?.takeIf(String::isNotBlank),
        )
    }

    fun stableLazyKey(index: Int): String {
        val identity = stableRowIdentity()
        return if (identity.isNotBlank()) {
            "manga-chapter:$identity"
        } else {
            "manga-chapter:index:$index"
        }
    }

    val stableRowKey: String
        get() = stableRowIdentity().takeIf(String::isNotBlank)?.let { "manga-chapter:$it" }
            ?: "manga-chapter:$id"

    fun rowActions(selected: Boolean): List<ChimahonMangaChapterRowActionModel> {
        return listOf(
            ChimahonMangaChapterRowActionModel(
                action = ChimahonMangaChapterRowAction.Select,
                label = if (selected) "Selected" else "Select",
                selected = selected,
            ),
            ChimahonMangaChapterRowActionModel(
                action = if (read) {
                    ChimahonMangaChapterRowAction.MarkUnread
                } else {
                    ChimahonMangaChapterRowAction.MarkRead
                },
                label = if (read) "Mark unread" else "Mark read",
                selected = read,
            ),
            ChimahonMangaChapterRowActionModel(
                action = if (bookmarked) {
                    ChimahonMangaChapterRowAction.RemoveBookmark
                } else {
                    ChimahonMangaChapterRowAction.Bookmark
                },
                label = if (bookmarked) "Bookmarked" else "Bookmark",
                selected = bookmarked,
            ),
            ChimahonMangaChapterRowActionModel(
                action = ChimahonMangaChapterRowAction.Download,
                label = downloadState.title,
                selected = downloaded || downloadState == ChimahonChapterDownloadUiState.Downloaded,
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

data class ChimahonMangaChapterSortState(
    val sort: ChimahonMangaChapterSort = ChimahonMangaChapterSort.SourceOrder,
    val descending: Boolean = true,
) {
    val directionTitle: String
        get() = when (sort) {
            ChimahonMangaChapterSort.SourceOrder -> if (descending) "Source order" else "Reverse source order"
            ChimahonMangaChapterSort.ChapterNumber -> if (descending) "Highest first" else "Lowest first"
            ChimahonMangaChapterSort.UploadDate -> if (descending) "Newest first" else "Oldest first"
            ChimahonMangaChapterSort.Name -> if (descending) "Z-A" else "A-Z"
            ChimahonMangaChapterSort.Scanlator -> if (descending) "Z-A groups" else "A-Z groups"
        }

    val sourceOrderHint: String
        get() = if (descending) {
            "Using the extension's source order"
        } else {
            "Using reverse source order"
        }

    fun withSort(newSort: ChimahonMangaChapterSort): ChimahonMangaChapterSortState {
        return copy(sort = newSort)
    }

    fun toggledDirection(): ChimahonMangaChapterSortState {
        return copy(descending = !descending)
    }
}

enum class ChimahonMangaChapterSort(val title: String) {
    SourceOrder("Source order"),
    ChapterNumber("Chapter number"),
    UploadDate("Upload date"),
    Name("Chapter name"),
    Scanlator("Scanlator"),
}

enum class ChimahonMangaChapterDisplayMode(val title: String) {
    SourceTitle("Source title"),
    ChapterNumber("Chapter number"),
}

data class ChimahonMangaChapterSelectionState(
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

data class ChimahonMangaChapterBulkActionModel(
    val action: ChimahonMangaChapterBulkAction,
    val label: String = action.title,
    val selected: Boolean = false,
    val enabled: Boolean = true,
)

enum class ChimahonMangaChapterBulkAction(val title: String) {
    Download("Download"),
    MarkRead("Mark read"),
    MarkUnread("Mark unread"),
    Bookmark("Bookmark"),
    RemoveBookmark("Remove bookmark"),
    SelectAll("Select all"),
    ClearSelection("Clear"),
}

data class ChimahonMangaChapterRowActionModel(
    val action: ChimahonMangaChapterRowAction,
    val label: String = action.title,
    val selected: Boolean = false,
    val enabled: Boolean = true,
)

enum class ChimahonMangaChapterRowAction(val title: String) {
    Download("Download"),
    MarkRead("Mark read"),
    MarkUnread("Mark unread"),
    Bookmark("Bookmark"),
    RemoveBookmark("Remove bookmark"),
    Select("Select"),
}

data class ChimahonMangaChapterFilterState(
    val read: ChimahonMangaReadFilter = ChimahonMangaReadFilter.Any,
    val downloaded: ChimahonMangaDownloadFilter = ChimahonMangaDownloadFilter.Any,
    val bookmarked: ChimahonMangaBookmarkFilter = ChimahonMangaBookmarkFilter.Any,
    val scanlator: String? = null,
) {
    val activeCount: Int
        get() = listOf(
            read != ChimahonMangaReadFilter.Any,
            downloaded != ChimahonMangaDownloadFilter.Any,
            bookmarked != ChimahonMangaBookmarkFilter.Any,
            !scanlator.isNullOrBlank(),
        ).count { it }

    val hasActiveFilters: Boolean
        get() = activeCount > 0

    val label: String
        get() = activeLabels().takeIf { it.isNotEmpty() }?.joinToString(" / ") ?: "All"

    fun activeLabels(): List<String> {
        return buildList {
            if (read != ChimahonMangaReadFilter.Any) add(read.title)
            if (downloaded != ChimahonMangaDownloadFilter.Any) add(downloaded.title)
            if (bookmarked != ChimahonMangaBookmarkFilter.Any) add(bookmarked.title)
            scanlator?.takeIf(String::isNotBlank)?.let { add(it) }
        }
    }
}

enum class ChimahonMangaReadFilter(val title: String) {
    Any("All"),
    Unread("Unread"),
    Read("Read"),
}

enum class ChimahonMangaDownloadFilter(val title: String) {
    Any("Any download"),
    Downloaded("Downloaded"),
    NotDownloaded("Not downloaded"),
}

enum class ChimahonMangaBookmarkFilter(val title: String) {
    Any("Any bookmark"),
    Bookmarked("Bookmarked"),
    NotBookmarked("Not bookmarked"),
}

enum class ChimahonChapterDownloadUiState(val title: String) {
    NotDownloaded("Not downloaded"),
    Queued("Queued"),
    Downloading("Downloading"),
    Downloaded("Downloaded"),
    Error("Download error"),
}

data class ChimahonMangaInfoChipModel(
    val label: String,
    val kind: ChimahonMangaInfoChipKind = ChimahonMangaInfoChipKind.Neutral,
) {
    val stableKey: String
        get() = "manga-info-chip:$kind:$label"
}

enum class ChimahonMangaInfoChipKind {
    Status,
    Source,
    Creator,
    Library,
    Count,
    Warning,
    Neutral,
}

data class ChimahonMangaSourceActionModel(
    val action: ChimahonMangaSourceAction,
    val label: String = action.title,
    val url: String? = null,
    val enabled: Boolean = true,
    val supportingLabel: String? = null,
)

enum class ChimahonMangaSourceAction(val title: String) {
    BrowseSource("Browse source"),
    OpenInWebView("Open in WebView"),
    OpenExternal("Open externally"),
    CopyUrl("Copy URL"),
}

enum class ChimahonMangaDetailContentState {
    Loading,
    Empty,
    Error,
    Content,
}

data class ChimahonMangaDetailActions(
    val onNavigateUp: () -> Unit = {},
    val onCoverClick: (ChimahonMangaHeaderUiModel) -> Unit = {},
    val onFavoriteClick: (ChimahonMangaHeaderUiModel) -> Unit = {},
    val onTrackingClick: (ChimahonMangaHeaderUiModel) -> Unit = {},
    val onWebViewClick: (ChimahonMangaHeaderUiModel) -> Unit = {},
    val onEditIntervalClick: (ChimahonMangaHeaderUiModel) -> Unit = {},
    val onRecommendClick: (ChimahonMangaHeaderUiModel) -> Unit = {},
    val onMergeClick: (ChimahonMangaHeaderUiModel) -> Unit = {},
    val onTagClick: (String) -> Unit = {},
    val onChapterClick: (ChimahonMangaChapterUiModel) -> Unit = {},
    val onChapterLongClick: (ChimahonMangaChapterUiModel) -> Unit = {},
    val onChapterDownloadClick: (ChimahonMangaChapterUiModel) -> Unit = {},
    val onSortChange: (ChimahonMangaChapterSortState) -> Unit = {},
    val onFilterClick: () -> Unit = {},
    val onRefresh: () -> Unit = {},
    val onRetry: () -> Unit = {},
    val onChapterReadClick: (ChimahonMangaChapterUiModel) -> Unit = {},
    val onChapterBookmarkClick: (ChimahonMangaChapterUiModel) -> Unit = {},
    val onChapterSelectClick: (ChimahonMangaChapterUiModel) -> Unit = {},
    val onDisplayModeChange: (ChimahonMangaChapterDisplayMode) -> Unit = {},
    val onReadFilterChange: (ChimahonMangaReadFilter) -> Unit = {},
    val onDownloadFilterChange: (ChimahonMangaDownloadFilter) -> Unit = {},
    val onBookmarkFilterChange: (ChimahonMangaBookmarkFilter) -> Unit = {},
    val onClearChapterFilters: () -> Unit = {},
    val onChapterBulkActionClick: (ChimahonMangaChapterBulkAction) -> Unit = {},
    val onSourceActionClick: (ChimahonMangaHeaderUiModel, ChimahonMangaSourceActionModel) -> Unit = { _, _ -> },
)

fun ChimahonMangaHeaderUiModel.infoChips(
    refreshing: Boolean = false,
): List<ChimahonMangaInfoChipModel> {
    return buildList {
        status?.takeIf(String::isNotBlank)?.let {
            add(ChimahonMangaInfoChipModel(it, ChimahonMangaInfoChipKind.Status))
        }
        add(ChimahonMangaInfoChipModel(sourceLine, ChimahonMangaInfoChipKind.Source))
        creatorLine?.let { add(ChimahonMangaInfoChipModel(it, ChimahonMangaInfoChipKind.Creator)) }
        if (inLibrary) add(ChimahonMangaInfoChipModel("In library", ChimahonMangaInfoChipKind.Library))
        if (favorite && !inLibrary) add(ChimahonMangaInfoChipModel("Favorite", ChimahonMangaInfoChipKind.Library))
        if (refreshing) add(ChimahonMangaInfoChipModel("Refreshing", ChimahonMangaInfoChipKind.Warning))
        if (totalChapterCount > 0) {
            add(ChimahonMangaInfoChipModel("$totalChapterCount chapters", ChimahonMangaInfoChipKind.Count))
        }
        if (unreadCount > 0) {
            add(ChimahonMangaInfoChipModel("$unreadCount unread", ChimahonMangaInfoChipKind.Count))
        }
        if (downloadedCount > 0) {
            add(ChimahonMangaInfoChipModel("$downloadedCount downloaded", ChimahonMangaInfoChipKind.Count))
        }
    }
}

fun ChimahonMangaHeaderUiModel.sourceActions(
    includeBrowseSource: Boolean = true,
): List<ChimahonMangaSourceActionModel> {
    return buildList {
        if (includeBrowseSource && sourceName.isNotBlank()) {
            add(
                ChimahonMangaSourceActionModel(
                    action = ChimahonMangaSourceAction.BrowseSource,
                    supportingLabel = sourceName,
                ),
            )
        }
        val url = sourceUrl ?: return@buildList
        add(
            ChimahonMangaSourceActionModel(
                action = ChimahonMangaSourceAction.OpenInWebView,
                url = url,
            ),
        )
        add(
            ChimahonMangaSourceActionModel(
                action = ChimahonMangaSourceAction.OpenExternal,
                url = url,
            ),
        )
        add(
            ChimahonMangaSourceActionModel(
                action = ChimahonMangaSourceAction.CopyUrl,
                url = url,
            ),
        )
    }
}

fun defaultMangaChapterBulkActions(
    selectionState: ChimahonMangaChapterSelectionState,
): List<ChimahonMangaChapterBulkActionModel> {
    return buildList {
        if (selectionState.totalCount > 0) {
            add(
                ChimahonMangaChapterBulkActionModel(
                    action = ChimahonMangaChapterBulkAction.SelectAll,
                    selected = selectionState.allSelected,
                    enabled = selectionState.canSelectAll,
                ),
            )
        }
        if (selectionState.hasSelection) {
            add(ChimahonMangaChapterBulkActionModel(ChimahonMangaChapterBulkAction.Download))
            add(ChimahonMangaChapterBulkActionModel(ChimahonMangaChapterBulkAction.MarkRead))
            add(ChimahonMangaChapterBulkActionModel(ChimahonMangaChapterBulkAction.MarkUnread))
            add(ChimahonMangaChapterBulkActionModel(ChimahonMangaChapterBulkAction.Bookmark))
            add(ChimahonMangaChapterBulkActionModel(ChimahonMangaChapterBulkAction.RemoveBookmark))
            add(ChimahonMangaChapterBulkActionModel(ChimahonMangaChapterBulkAction.ClearSelection))
        }
    }
}

fun List<ChimahonMangaChapterUiModel>.sortedForMangaDetail(
    sortState: ChimahonMangaChapterSortState,
): List<ChimahonMangaChapterUiModel> {
    val comparator = Comparator<IndexedValue<ChimahonMangaChapterUiModel>> { left, right ->
        val base = when (sortState.sort) {
            ChimahonMangaChapterSort.SourceOrder ->
                left.value.sourceOrder.compareTo(right.value.sourceOrder)
            ChimahonMangaChapterSort.ChapterNumber ->
                left.value.chapterNumber.compareTo(right.value.chapterNumber)
            ChimahonMangaChapterSort.UploadDate ->
                compareNullableLong(left.value.dateUploadEpochMillis, right.value.dateUploadEpochMillis)
            ChimahonMangaChapterSort.Name ->
                left.value.title.lowercase().compareTo(right.value.title.lowercase())
            ChimahonMangaChapterSort.Scanlator ->
                left.value.scanlator.orEmpty().lowercase().compareTo(right.value.scanlator.orEmpty().lowercase())
        }
        val directed = if (sortState.sort == ChimahonMangaChapterSort.SourceOrder) {
            if (sortState.descending) base else -base
        } else {
            if (sortState.descending) -base else base
        }
        if (directed != 0) directed else left.index.compareTo(right.index)
    }
    return withIndex().sortedWith(comparator).map { it.value }
}

fun ChimahonMangaChapterUiModel.matches(
    filters: ChimahonMangaChapterFilterState,
    query: String,
): Boolean {
    if (query.isNotBlank()) {
        val needle = query.trim().lowercase()
        val haystack = listOf(title, scanlator.orEmpty(), sourceName.orEmpty(), chapterMarker)
            .joinToString(" ")
            .lowercase()
        if (needle !in haystack) return false
    }
    val readMatches = when (filters.read) {
        ChimahonMangaReadFilter.Any -> true
        ChimahonMangaReadFilter.Unread -> !read
        ChimahonMangaReadFilter.Read -> read
    }
    val downloadMatches = when (filters.downloaded) {
        ChimahonMangaDownloadFilter.Any -> true
        ChimahonMangaDownloadFilter.Downloaded -> downloaded ||
            downloadState == ChimahonChapterDownloadUiState.Downloaded
        ChimahonMangaDownloadFilter.NotDownloaded -> !downloaded &&
            downloadState != ChimahonChapterDownloadUiState.Downloaded
    }
    val bookmarkMatches = when (filters.bookmarked) {
        ChimahonMangaBookmarkFilter.Any -> true
        ChimahonMangaBookmarkFilter.Bookmarked -> bookmarked
        ChimahonMangaBookmarkFilter.NotBookmarked -> !bookmarked
    }
    val scanlatorMatches = filters.scanlator.isNullOrBlank() ||
        scanlator.equals(filters.scanlator, ignoreCase = true)
    return readMatches && downloadMatches && bookmarkMatches && scanlatorMatches
}

private fun compareNullableLong(left: Long?, right: Long?): Int {
    return when {
        left == null && right == null -> 0
        left == null -> -1
        right == null -> 1
        else -> left.compareTo(right)
    }
}

private fun Double.toDisplayChapter(): String {
    if (this % 1.0 == 0.0) return toLong().toString()
    return toString().trimEnd('0').trimEnd('.')
}
