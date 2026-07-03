package app.chimahon.shared.library

import app.chimahon.shared.ChimahonFilterMode
import app.chimahon.shared.ChimahonLibraryCategory
import app.chimahon.shared.ChimahonLibraryContinueButtonMode
import app.chimahon.shared.ChimahonLibraryCoverRatio
import app.chimahon.shared.ChimahonLibraryDisplayMode
import app.chimahon.shared.ChimahonLibrarySettings
import app.chimahon.shared.ChimahonLibrarySort
import app.chimahon.shared.ChimahonMangaEntry

data class ChimahonLibraryUiState(
    val title: String = "Library",
    val subtitle: String? = null,
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null,
    val categories: List<ChimahonLibraryCategoryUiModel> = emptyList(),
    val entries: List<ChimahonLibraryMangaUiModel> = emptyList(),
    val selectedMangaIds: Set<Long> = emptySet(),
    val settings: ChimahonLibrarySettings = ChimahonLibrarySettings(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
    val categoryTabStyle: ChimahonLibraryCategoryTabStyle = ChimahonLibraryCategoryTabStyle.ScrollableTabs,
) {
    val isSelectionMode: Boolean
        get() = selectedMangaIds.isNotEmpty()

    val visibleCount: Int
        get() = entries.size

    val categoryTabsState: ChimahonLibraryCategoryTabsState
        get() = ChimahonLibraryCategoryTabsState(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            showItemCount = settings.showCategoryItemCount,
            style = categoryTabStyle,
        )
}

enum class ChimahonLibraryCategoryTabStyle {
    ScrollableTabs,
    Chips,
}

data class ChimahonLibraryCategoryTabsState(
    val categories: List<ChimahonLibraryCategoryUiModel> = emptyList(),
    val selectedCategoryId: Long? = null,
    val showItemCount: Boolean = true,
    val style: ChimahonLibraryCategoryTabStyle = ChimahonLibraryCategoryTabStyle.ScrollableTabs,
    val includeHiddenCategories: Boolean = false,
) {
    val visibleCategories: List<ChimahonLibraryCategoryUiModel>
        get() = if (includeHiddenCategories) {
            categories
        } else {
            categories.filter { !it.hidden || it.selected || it.id == selectedCategoryId }
        }

    val selectedIndex: Int
        get() = visibleCategories.indexOfFirst { it.id == selectedCategoryId }.takeIf { it >= 0 } ?: 0
}

data class ChimahonLibraryCategoryUiModel(
    val id: Long?,
    val title: String,
    val count: Int = 0,
    val selected: Boolean = false,
    val hidden: Boolean = false,
    val isDefault: Boolean = false,
) {
    val displayTitle: String
        get() = if (count > 0) "$title ($count)" else title

    val stableKey: String
        get() = id?.let { "library:category:$it" } ?: "library:category:all"

    val itemCountLabel: String
        get() = when (count) {
            0 -> "No items"
            1 -> "1 item"
            else -> "$count items"
        }

    fun titleForTabs(showItemCount: Boolean): String {
        return if (showItemCount) displayTitle else title
    }
}

enum class ChimahonLibraryCardDensity {
    Compact,
    Comfortable,
}

data class ChimahonLibraryMangaUiModel(
    val id: Long,
    val title: String,
    val subtitle: String? = null,
    val thumbnailUrl: String? = null,
    val sourceName: String? = null,
    val language: String? = null,
    val status: String? = null,
    val unreadCount: Int = 0,
    val downloadedCount: Int = 0,
    val totalChapterCount: Int = 0,
    val latestChapterLabel: String? = null,
    val lastReadLabel: String? = null,
    val lastUpdatedLabel: String? = null,
    val categoryLabels: List<String> = emptyList(),
    val hasLocalSource: Boolean = false,
    val tracked: Boolean = false,
    val favorite: Boolean = true,
    val initialized: Boolean = true,
    val coverRatio: ChimahonLibraryCoverRatio = ChimahonLibraryCoverRatio.Automatic,
    val bookmarkedCount: Int = 0,
) {
    val stableKey: String
        get() = "library:manga:$id"

    val hasUnread: Boolean
        get() = unreadCount > 0

    val hasDownloads: Boolean
        get() = downloadedCount > 0

    val hasBookmarkedChapters: Boolean
        get() = bookmarkedCount > 0

    val progressLabel: String?
        get() = when {
            unreadCount > 0 -> "$unreadCount unread"
            totalChapterCount > 0 -> "$totalChapterCount chapters"
            else -> null
        }

    fun badges(
        settings: ChimahonLibrarySettings,
        showBookmarkedBadges: Boolean = true,
    ): List<ChimahonLibraryBadgeModel> {
        val badges = mutableListOf<ChimahonLibraryBadgeModel>()
        if (settings.showUnreadBadges && unreadCount > 0) {
            badges += ChimahonLibraryBadgeModel(
                text = unreadCount.toString(),
                kind = ChimahonLibraryBadgeKind.Unread,
                contentDescription = "$unreadCount unread chapters",
            )
        }
        if (settings.showDownloadedBadges && downloadedCount > 0) {
            badges += ChimahonLibraryBadgeModel(
                text = downloadedCount.toString(),
                kind = ChimahonLibraryBadgeKind.Downloaded,
                contentDescription = "$downloadedCount downloaded chapters",
            )
        }
        if (showBookmarkedBadges && bookmarkedCount > 0) {
            badges += ChimahonLibraryBadgeModel(
                text = bookmarkedCount.toString(),
                kind = ChimahonLibraryBadgeKind.Bookmarked,
                contentDescription = "$bookmarkedCount bookmarked chapters",
            )
        }
        if (settings.showLocalBadges && hasLocalSource) {
            badges += ChimahonLibraryBadgeModel(
                text = "Local",
                kind = ChimahonLibraryBadgeKind.Local,
                contentDescription = "Local manga",
            )
        }
        if (settings.showLanguageBadges && !language.isNullOrBlank()) {
            badges += ChimahonLibraryBadgeModel(
                text = language.uppercase(),
                kind = ChimahonLibraryBadgeKind.Language,
                contentDescription = "Language ${language.uppercase()}",
            )
        }
        if (settings.showSourceBadges && !sourceName.isNullOrBlank()) {
            badges += ChimahonLibraryBadgeModel(
                text = sourceName,
                kind = ChimahonLibraryBadgeKind.Source,
                contentDescription = "Source $sourceName",
            )
        }
        if (settings.showTrackingBadges && tracked) {
            badges += ChimahonLibraryBadgeModel(
                text = "Tracked",
                kind = ChimahonLibraryBadgeKind.Tracking,
                contentDescription = "Tracked manga",
            )
        }
        return badges
    }

    fun shouldShowContinueButton(settings: ChimahonLibrarySettings): Boolean {
        if (!settings.showContinueButtons) return false
        return when (settings.continueButtonMode) {
            ChimahonLibraryContinueButtonMode.Unread -> unreadCount > 0
            ChimahonLibraryContinueButtonMode.Started -> lastReadLabel != null || unreadCount > 0
            ChimahonLibraryContinueButtonMode.Always -> true
            ChimahonLibraryContinueButtonMode.Never -> false
        }
    }

    fun badges(state: ChimahonLibraryFilterSortState): List<ChimahonLibraryBadgeModel> {
        val settings = ChimahonLibrarySettings(
            displayMode = state.displayMode,
            coverAspectRatio = state.coverRatio,
            sort = state.sort,
            sortAscending = state.sortAscending,
            downloadedFilter = state.downloadedFilter,
            unreadFilter = state.unreadFilter,
            startedFilter = state.startedFilter,
            bookmarkedFilter = state.bookmarkedFilter,
            completedFilter = state.completedFilter,
            trackedFilter = state.trackedFilter,
            showUnreadBadges = state.showUnreadBadges,
            showDownloadedBadges = state.showDownloadedBadges,
            showLocalBadges = state.showLocalBadges,
            showLanguageBadges = state.showLanguageBadges,
            showSourceBadges = state.showSourceBadges,
            showTrackingBadges = state.showTrackingBadges,
            showLatestChapter = state.showLatestChapter,
            showLastReadAt = state.showLastReadAt,
            showContinueButtons = state.showContinueButtons,
        )
        return badges(settings = settings, showBookmarkedBadges = state.showBookmarkedBadges)
    }
}

data class ChimahonLibraryBadgeModel(
    val text: String,
    val kind: ChimahonLibraryBadgeKind,
    val contentDescription: String = text,
)

enum class ChimahonLibraryBadgeKind {
    Unread,
    Downloaded,
    Bookmarked,
    Local,
    Language,
    Source,
    Tracking,
    Status,
    Warning,
}

data class ChimahonLibraryFilterSortState(
    val displayMode: ChimahonLibraryDisplayMode = ChimahonLibraryDisplayMode.ComfortableGrid,
    val coverRatio: ChimahonLibraryCoverRatio = ChimahonLibraryCoverRatio.Automatic,
    val sort: ChimahonLibrarySort = ChimahonLibrarySort.Alphabetical,
    val sortAscending: Boolean = true,
    val downloadedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val unreadFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val startedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val bookmarkedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val completedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val trackedFilter: ChimahonFilterMode = ChimahonFilterMode.Any,
    val showUnreadBadges: Boolean = true,
    val showDownloadedBadges: Boolean = true,
    val showLocalBadges: Boolean = true,
    val showLanguageBadges: Boolean = false,
    val showSourceBadges: Boolean = false,
    val showTrackingBadges: Boolean = false,
    val showLatestChapter: Boolean = true,
    val showLastReadAt: Boolean = false,
    val showContinueButtons: Boolean = true,
    val showBookmarkedBadges: Boolean = true,
)

enum class ChimahonLibraryFilterKind(val title: String) {
    Downloaded("Downloaded"),
    Unread("Unread"),
    Started("Started"),
    Bookmarked("Bookmarked"),
    Completed("Completed"),
    Tracked("Tracked"),
}

enum class ChimahonLibrarySheetRowKind {
    Display,
    CoverRatio,
    Sort,
    SortDirection,
    Filter,
    BadgeSwitch,
}

data class ChimahonLibrarySheetRow(
    val stableKey: String,
    val title: String,
    val kind: ChimahonLibrarySheetRowKind,
    val selected: Boolean = false,
    val enabled: Boolean = true,
    val subtitle: String? = null,
)

enum class ChimahonLibraryUiContentState {
    Loading,
    Empty,
    Error,
    Content,
}

fun ChimahonLibrarySettings.toLibraryFilterSortState(): ChimahonLibraryFilterSortState {
    return ChimahonLibraryFilterSortState(
        displayMode = displayMode,
        coverRatio = coverAspectRatio,
        sort = sort,
        sortAscending = sortAscending,
        downloadedFilter = downloadedFilter,
        unreadFilter = unreadFilter,
        startedFilter = startedFilter,
        bookmarkedFilter = bookmarkedFilter,
        completedFilter = completedFilter,
        trackedFilter = trackedFilter,
        showUnreadBadges = showUnreadBadges,
        showDownloadedBadges = showDownloadedBadges,
        showLocalBadges = showLocalBadges,
        showLanguageBadges = showLanguageBadges,
        showSourceBadges = showSourceBadges,
        showTrackingBadges = showTrackingBadges,
        showLatestChapter = showLatestChapter,
        showLastReadAt = showLastReadAt,
        showContinueButtons = showContinueButtons,
    )
}

fun ChimahonLibrarySettings.withLibraryFilterSortState(
    state: ChimahonLibraryFilterSortState,
): ChimahonLibrarySettings {
    return copy(
        displayMode = state.displayMode,
        coverAspectRatio = state.coverRatio,
        sort = state.sort,
        sortAscending = state.sortAscending,
        downloadedFilter = state.downloadedFilter,
        unreadFilter = state.unreadFilter,
        startedFilter = state.startedFilter,
        bookmarkedFilter = state.bookmarkedFilter,
        completedFilter = state.completedFilter,
        trackedFilter = state.trackedFilter,
        showUnreadBadges = state.showUnreadBadges,
        showDownloadedBadges = state.showDownloadedBadges,
        showLocalBadges = state.showLocalBadges,
        showLanguageBadges = state.showLanguageBadges,
        showSourceBadges = state.showSourceBadges,
        showTrackingBadges = state.showTrackingBadges,
        showLatestChapter = state.showLatestChapter,
        showLastReadAt = state.showLastReadAt,
        showContinueButtons = state.showContinueButtons,
    )
}

fun ChimahonMangaEntry.toLibraryMangaUiModel(
    sourceName: String? = null,
    language: String? = null,
    unreadCount: Int = 0,
    downloadedCount: Int = 0,
    bookmarkedCount: Int = 0,
    totalChapterCount: Int = 0,
    latestChapterLabel: String? = null,
    lastReadLabel: String? = null,
    lastUpdatedLabel: String? = null,
    categoryLabels: List<String> = emptyList(),
    hasLocalSource: Boolean = false,
    tracked: Boolean = false,
    coverRatio: ChimahonLibraryCoverRatio = ChimahonLibraryCoverRatio.Automatic,
): ChimahonLibraryMangaUiModel {
    return ChimahonLibraryMangaUiModel(
        id = id,
        title = title,
        subtitle = author ?: artist ?: sourceName,
        thumbnailUrl = thumbnailUrl,
        sourceName = sourceName,
        language = language,
        status = status,
        unreadCount = unreadCount,
        downloadedCount = downloadedCount,
        bookmarkedCount = bookmarkedCount,
        totalChapterCount = totalChapterCount,
        latestChapterLabel = latestChapterLabel,
        lastReadLabel = lastReadLabel,
        lastUpdatedLabel = lastUpdatedLabel,
        categoryLabels = categoryLabels,
        hasLocalSource = hasLocalSource,
        tracked = tracked,
        favorite = favorite,
        initialized = initialized,
        coverRatio = coverRatio,
    )
}

fun List<ChimahonLibraryCategory>.toLibraryCategoryUiModels(
    selectedCategoryId: Long?,
    countsByCategoryId: Map<Long, Int>,
    allCount: Int,
    includeAllCategory: Boolean = true,
): List<ChimahonLibraryCategoryUiModel> {
    val categoryModels = sortedWith(compareBy<ChimahonLibraryCategory> { it.order }.thenBy { it.name.lowercase() })
        .map { category ->
            ChimahonLibraryCategoryUiModel(
                id = category.id,
                title = category.displayTitle(),
                count = countsByCategoryId[category.id] ?: 0,
                selected = selectedCategoryId == category.id,
                hidden = category.hidden,
                isDefault = category.id == 0L || category.name.equals("Default", ignoreCase = true),
            )
        }
    if (!includeAllCategory) return categoryModels
    return listOf(
        ChimahonLibraryCategoryUiModel(
            id = null,
            title = "All",
            count = allCount,
            selected = selectedCategoryId == null,
        ),
    ) + categoryModels
}

fun ChimahonLibraryDisplayMode.libraryDisplayTitle(): String {
    return when (this) {
        ChimahonLibraryDisplayMode.ComfortableGrid -> "Comfortable grid"
        ChimahonLibraryDisplayMode.ComfortableGridPanorama -> "Panorama grid"
        ChimahonLibraryDisplayMode.CompactGrid -> "Compact grid"
        ChimahonLibraryDisplayMode.CoverOnlyGrid -> "Cover-only grid"
        ChimahonLibraryDisplayMode.List -> "List"
    }
}

fun ChimahonLibraryDisplayMode.libraryCardDensity(): ChimahonLibraryCardDensity {
    return when (this) {
        ChimahonLibraryDisplayMode.CompactGrid,
        ChimahonLibraryDisplayMode.CoverOnlyGrid,
        -> ChimahonLibraryCardDensity.Compact
        ChimahonLibraryDisplayMode.ComfortableGrid,
        ChimahonLibraryDisplayMode.ComfortableGridPanorama,
        ChimahonLibraryDisplayMode.List,
        -> ChimahonLibraryCardDensity.Comfortable
    }
}

fun ChimahonLibraryCoverRatio.libraryCoverRatioTitle(): String {
    return when (this) {
        ChimahonLibraryCoverRatio.Automatic -> "Automatic"
        ChimahonLibraryCoverRatio.Square -> "Square"
        ChimahonLibraryCoverRatio.ThreeToFour -> "3:4"
        ChimahonLibraryCoverRatio.TwoToThree -> "2:3"
        ChimahonLibraryCoverRatio.Original -> "Original"
    }
}

fun ChimahonLibrarySort.librarySortTitle(): String {
    return when (this) {
        ChimahonLibrarySort.Alphabetical -> "Alphabetical"
        ChimahonLibrarySort.LastRead -> "Last read"
        ChimahonLibrarySort.LastUpdate -> "Last updated"
        ChimahonLibrarySort.UnreadCount -> "Unread count"
        ChimahonLibrarySort.TotalChapters -> "Total chapters"
        ChimahonLibrarySort.LatestChapter -> "Latest chapter"
        ChimahonLibrarySort.ChapterFetchDate -> "Chapter fetch date"
        ChimahonLibrarySort.DateAdded -> "Date added"
        ChimahonLibrarySort.Random -> "Random"
    }
}

fun ChimahonFilterMode.libraryFilterTitle(): String {
    return when (this) {
        ChimahonFilterMode.Any -> "Any"
        ChimahonFilterMode.Include -> "Include"
        ChimahonFilterMode.Exclude -> "Exclude"
    }
}

fun ChimahonLibraryFilterSortState.filterFor(kind: ChimahonLibraryFilterKind): ChimahonFilterMode {
    return when (kind) {
        ChimahonLibraryFilterKind.Downloaded -> downloadedFilter
        ChimahonLibraryFilterKind.Unread -> unreadFilter
        ChimahonLibraryFilterKind.Started -> startedFilter
        ChimahonLibraryFilterKind.Bookmarked -> bookmarkedFilter
        ChimahonLibraryFilterKind.Completed -> completedFilter
        ChimahonLibraryFilterKind.Tracked -> trackedFilter
    }
}

fun ChimahonLibraryFilterSortState.withFilter(
    kind: ChimahonLibraryFilterKind,
    value: ChimahonFilterMode,
): ChimahonLibraryFilterSortState {
    return when (kind) {
        ChimahonLibraryFilterKind.Downloaded -> copy(downloadedFilter = value)
        ChimahonLibraryFilterKind.Unread -> copy(unreadFilter = value)
        ChimahonLibraryFilterKind.Started -> copy(startedFilter = value)
        ChimahonLibraryFilterKind.Bookmarked -> copy(bookmarkedFilter = value)
        ChimahonLibraryFilterKind.Completed -> copy(completedFilter = value)
        ChimahonLibraryFilterKind.Tracked -> copy(trackedFilter = value)
    }
}

fun ChimahonLibrarySort.librarySheetRow(selected: Boolean): ChimahonLibrarySheetRow {
    return ChimahonLibrarySheetRow(
        stableKey = "library:sort:${name}",
        title = librarySortTitle(),
        kind = ChimahonLibrarySheetRowKind.Sort,
        selected = selected,
    )
}

fun ChimahonLibraryFilterKind.librarySheetRow(
    value: ChimahonFilterMode,
): ChimahonLibrarySheetRow {
    return ChimahonLibrarySheetRow(
        stableKey = "library:filter:${name}",
        title = title,
        subtitle = value.libraryFilterTitle(),
        kind = ChimahonLibrarySheetRowKind.Filter,
        selected = value != ChimahonFilterMode.Any,
    )
}

fun ChimahonLibraryFilterSortState.sortRows(): List<ChimahonLibrarySheetRow> {
    return ChimahonLibrarySort.entries.map { it.librarySheetRow(selected = sort == it) }
}

fun ChimahonLibraryFilterSortState.filterRows(): List<ChimahonLibrarySheetRow> {
    return ChimahonLibraryFilterKind.entries.map { kind -> kind.librarySheetRow(filterFor(kind)) }
}

fun ChimahonLibraryFilterSortState.activeFilterLabels(): List<String> {
    return ChimahonLibraryFilterKind.entries
        .mapNotNull { kind ->
            val value = filterFor(kind)
            if (value == ChimahonFilterMode.Any) null else "${kind.title}: ${value.libraryFilterTitle()}"
        }
}

private fun ChimahonLibraryCategory.displayTitle(): String {
    return name.takeUnless { it.isBlank() || it.equals("default", ignoreCase = true) } ?: "Default"
}
