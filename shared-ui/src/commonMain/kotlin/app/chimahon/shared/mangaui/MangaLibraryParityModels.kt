package app.chimahon.shared.mangaui

data class ChimahonMangaLibraryUiState(
    val title: String = "Library",
    val subtitle: String? = null,
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val categories: List<ChimahonMangaLibraryCategoryModel> = emptyList(),
    val items: List<ChimahonMangaLibraryItemModel> = emptyList(),
    val selectedItemIds: Set<String> = emptySet(),
    val displayMode: ChimahonMangaLibraryDisplayMode = ChimahonMangaLibraryDisplayMode.ComfortableGrid,
    val displayOptions: ChimahonMangaLibraryDisplayOptions = ChimahonMangaLibraryDisplayOptions(),
    val loading: Boolean = false,
    val errorMessage: String? = null,
) {
    val isSelectionMode: Boolean
        get() = selectedItemIds.isNotEmpty()

    val visibleCount: Int
        get() = items.size

    val contentState: ChimahonMangaLibraryContentState
        get() = when {
            loading && items.isEmpty() -> ChimahonMangaLibraryContentState.Loading
            errorMessage != null && items.isEmpty() -> ChimahonMangaLibraryContentState.Error
            items.isEmpty() -> ChimahonMangaLibraryContentState.Empty
            else -> ChimahonMangaLibraryContentState.Content
        }
}

enum class ChimahonMangaLibraryContentState {
    Loading,
    Empty,
    Error,
    Content,
}

data class ChimahonMangaLibraryDisplayOptions(
    val showUnreadBadges: Boolean = true,
    val showDownloadedBadges: Boolean = true,
    val showUpdateBadges: Boolean = true,
    val showHistoryBadges: Boolean = true,
    val showBookmarkBadges: Boolean = false,
    val showLocalBadges: Boolean = true,
    val showLanguageBadges: Boolean = false,
    val showSourceBadges: Boolean = false,
    val showTrackingBadges: Boolean = false,
    val showStatusBadges: Boolean = false,
    val showCategoryBadges: Boolean = true,
    val showLatestChapter: Boolean = true,
    val showLastUpdate: Boolean = true,
    val showLastHistory: Boolean = true,
    val showContinueButton: Boolean = true,
)

enum class ChimahonMangaLibraryDisplayMode(val title: String) {
    CompactGrid("Compact grid"),
    ComfortableGrid("Comfortable grid"),
    DetailedGrid("Detailed grid"),
    CompactList("Compact list"),
    DetailedList("Detailed list"),
    CoverOnlyGrid("Cover-only grid"),
    PanoramaGrid("Panorama grid"),
    ;

    val isList: Boolean
        get() = this == CompactList || this == DetailedList

    val isDetailed: Boolean
        get() = this == DetailedGrid || this == DetailedList
}

enum class ChimahonMangaLibraryCoverRatio {
    Automatic,
    Square,
    ThreeToFour,
    TwoToThree,
    Panorama,
}

data class ChimahonMangaLibraryCategoryModel(
    val id: String?,
    val title: String,
    val count: Int = 0,
    val selected: Boolean = false,
    val hidden: Boolean = false,
    val isDefault: Boolean = false,
    val unreadCount: Int = 0,
    val updateCount: Int = 0,
    val downloadedCount: Int = 0,
) {
    val displayTitle: String
        get() = if (count > 0) "$title ($count)" else title

    val hasActivity: Boolean
        get() = unreadCount > 0 || updateCount > 0 || downloadedCount > 0

    val activityLabel: String?
        get() = buildList {
            if (unreadCount > 0) add("$unreadCount unread")
            if (updateCount > 0) add("$updateCount updates")
            if (downloadedCount > 0) add("$downloadedCount downloaded")
        }.joinToString(" / ").takeIf(String::isNotBlank)
}

data class ChimahonMangaLibraryItemModel(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val thumbnailUrl: String? = null,
    val sourceName: String? = null,
    val language: String? = null,
    val status: String? = null,
    val author: String? = null,
    val artist: String? = null,
    val unreadCount: Int = 0,
    val downloadedCount: Int = 0,
    val bookmarkedCount: Int = 0,
    val totalChapterCount: Int = 0,
    val latestChapterLabel: String? = null,
    val lastUpdateLabel: String? = null,
    val lastHistoryLabel: String? = null,
    val progressLabel: String? = null,
    val categoryIds: List<String> = emptyList(),
    val categoryLabels: List<String> = emptyList(),
    val extraBadges: List<ChimahonMangaLibraryBadgeModel> = emptyList(),
    val favorite: Boolean = true,
    val initialized: Boolean = true,
    val localSource: Boolean = false,
    val tracked: Boolean = false,
    val hasNewUpdates: Boolean = false,
    val coverRatio: ChimahonMangaLibraryCoverRatio = ChimahonMangaLibraryCoverRatio.Automatic,
) {
    val hasUnread: Boolean
        get() = unreadCount > 0

    val hasDownloads: Boolean
        get() = downloadedCount > 0

    val hasHistory: Boolean
        get() = !lastHistoryLabel.isNullOrBlank()

    val creatorLine: String?
        get() = listOfNotNull(
            author?.takeIf(String::isNotBlank),
            artist?.takeIf { it.isNotBlank() && it != author },
        ).joinToString(" / ").takeIf(String::isNotBlank)

    val defaultProgressLabel: String?
        get() = progressLabel
            ?: when {
                unreadCount > 0 -> "$unreadCount unread"
                totalChapterCount > 0 -> "$totalChapterCount chapters"
                else -> null
            }

    fun badges(options: ChimahonMangaLibraryDisplayOptions): List<ChimahonMangaLibraryBadgeModel> {
        return buildList {
            if (options.showUnreadBadges && unreadCount > 0) {
                add(
                    ChimahonMangaLibraryBadgeModel(
                        label = unreadCount.toString(),
                        kind = ChimahonMangaLibraryBadgeKind.Unread,
                        contentDescription = "$unreadCount unread chapters",
                    ),
                )
            }
            if (options.showDownloadedBadges && downloadedCount > 0) {
                add(
                    ChimahonMangaLibraryBadgeModel(
                        label = downloadedCount.toString(),
                        kind = ChimahonMangaLibraryBadgeKind.Downloaded,
                        contentDescription = "$downloadedCount downloaded chapters",
                    ),
                )
            }
            if (options.showUpdateBadges && hasNewUpdates) {
                add(
                    ChimahonMangaLibraryBadgeModel(
                        label = lastUpdateLabel ?: "Updated",
                        kind = ChimahonMangaLibraryBadgeKind.Update,
                    ),
                )
            }
            if (options.showHistoryBadges && !lastHistoryLabel.isNullOrBlank()) {
                add(ChimahonMangaLibraryBadgeModel(lastHistoryLabel, ChimahonMangaLibraryBadgeKind.History))
            }
            if (options.showBookmarkBadges && bookmarkedCount > 0) {
                add(ChimahonMangaLibraryBadgeModel("$bookmarkedCount", ChimahonMangaLibraryBadgeKind.Bookmark))
            }
            if (options.showLocalBadges && localSource) {
                add(ChimahonMangaLibraryBadgeModel("Local", ChimahonMangaLibraryBadgeKind.Local))
            }
            if (options.showLanguageBadges && !language.isNullOrBlank()) {
                add(ChimahonMangaLibraryBadgeModel(language.uppercase(), ChimahonMangaLibraryBadgeKind.Language))
            }
            if (options.showSourceBadges && !sourceName.isNullOrBlank()) {
                add(ChimahonMangaLibraryBadgeModel(sourceName, ChimahonMangaLibraryBadgeKind.Source))
            }
            if (options.showTrackingBadges && tracked) {
                add(ChimahonMangaLibraryBadgeModel("Tracked", ChimahonMangaLibraryBadgeKind.Tracking))
            }
            if (options.showStatusBadges && !status.isNullOrBlank()) {
                add(ChimahonMangaLibraryBadgeModel(status, ChimahonMangaLibraryBadgeKind.Status))
            }
            if (options.showCategoryBadges && categoryLabels.isNotEmpty() && extraBadges.none { it.kind == ChimahonMangaLibraryBadgeKind.Category }) {
                categoryLabels.take(2).forEach {
                    add(ChimahonMangaLibraryBadgeModel(it, ChimahonMangaLibraryBadgeKind.Category))
                }
            }
            if (!initialized) {
                add(ChimahonMangaLibraryBadgeModel("Needs refresh", ChimahonMangaLibraryBadgeKind.Warning))
            }
            addAll(extraBadges)
        }
    }

    fun secondaryLines(
        displayMode: ChimahonMangaLibraryDisplayMode,
        options: ChimahonMangaLibraryDisplayOptions,
    ): List<String> {
        return buildList {
            subtitle?.takeIf(String::isNotBlank)?.let(::add)
            if (displayMode.isDetailed) creatorLine?.let(::add)
            if (options.showLatestChapter) latestChapterLabel?.takeIf(String::isNotBlank)?.let(::add)
            if (options.showLastUpdate) lastUpdateLabel?.takeIf(String::isNotBlank)?.let(::add)
            if (options.showLastHistory) lastHistoryLabel?.takeIf(String::isNotBlank)?.let(::add)
            defaultProgressLabel?.let(::add)
        }.distinct()
    }

    fun shouldShowContinueButton(options: ChimahonMangaLibraryDisplayOptions): Boolean {
        return options.showContinueButton && (unreadCount > 0 || hasHistory)
    }
}

data class ChimahonMangaLibraryBadgeModel(
    val label: String,
    val kind: ChimahonMangaLibraryBadgeKind,
    val contentDescription: String = label,
) {
    val stableKey: String
        get() = "manga-library-badge:$kind:$label"
}

enum class ChimahonMangaLibraryBadgeKind {
    Unread,
    Downloaded,
    Update,
    History,
    Bookmark,
    Local,
    Language,
    Source,
    Tracking,
    Status,
    Category,
    Warning,
    Progress,
}
