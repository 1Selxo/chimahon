package app.chimahon.shared

data class ChimahonLibraryData(
    val entries: List<ChimahonLibraryMangaData>,
    val categories: List<ChimahonCategoryData>,
    val downloadedOnly: Boolean,
    val totalMangaCount: Int,
    val downloadedMangaCount: Int,
)

data class ChimahonLibraryMangaData(
    val manga: ChimahonMangaEntry,
    val chapters: List<ChimahonChapterEntry>,
    val categoryIds: List<Long>,
    val unreadChapterCount: Int,
    val readChapterCount: Int,
    val bookmarkedChapterCount: Int,
    val downloadedChapterCount: Int,
) {
    val hasDownloads: Boolean
        get() = downloadedChapterCount > 0
}

data class ChimahonCategoryData(
    val category: ChimahonLibraryCategory,
    val mangaCount: Int,
    val unreadChapterCount: Int,
    val downloadedChapterCount: Int,
)

data class ChimahonUpdatesFilter(
    val read: Boolean? = null,
    val started: Boolean? = null,
    val bookmarked: Boolean? = null,
    val downloadedOnly: Boolean? = null,
)

data class ChimahonUpdateData(
    val update: ChimahonRecentUpdateEntry,
    val downloaded: Boolean,
)

data class ChimahonUpdatesData(
    val entries: List<ChimahonUpdateData>,
    val after: Long,
    val limit: Int,
    val hasMore: Boolean,
)

data class ChimahonHistoryFilter(
    val query: String = "",
    val unfinishedManga: Boolean? = null,
    val unfinishedChapter: Boolean? = null,
    val includeNonLibrary: Boolean = true,
    val downloadedOnly: Boolean? = null,
)

data class ChimahonHistoryDataEntry(
    val history: ChimahonHistoryEntry,
    val downloaded: Boolean,
)

data class ChimahonHistoryData(
    val entries: List<ChimahonHistoryDataEntry>,
    val limit: Int,
    val hasMore: Boolean,
)

data class ChimahonStatisticsData(
    val libraryMangaCount: Int,
    val startedMangaCount: Int,
    val completedMangaCount: Int,
    val sourceCount: Int,
    val categoryCount: Int,
    val totalChapterCount: Int,
    val readChapterCount: Int,
    val unreadChapterCount: Int,
    val bookmarkedChapterCount: Int,
    val downloadedMangaCount: Int,
    val downloadedChapterCount: Int,
    val historyEntryCount: Int,
    val totalReadDurationMillis: Long,
)

data class ChimahonStorageSection(
    val path: String,
    val exists: Boolean,
    val sizeBytes: Long,
    val fileCount: Int,
    val directoryCount: Int,
)

data class ChimahonStorageData(
    val files: ChimahonStorageSection,
    val cache: ChimahonStorageSection,
    val downloads: ChimahonStorageSection,
    val totalSizeBytes: Long,
    val downloadedMangaCount: Int,
    val downloadedChapterCount: Int,
)

enum class ChimahonDownloadState {
    Queued,
    Downloading,
    Paused,
    Downloaded,
    Error,
}

data class ChimahonDownloadQueueEntry(
    val id: String,
    val mangaId: Long,
    val chapterId: Long,
    val sourceId: Long,
    val mangaTitle: String,
    val chapterName: String,
    val chapterUrl: String,
    val status: ChimahonDownloadState = ChimahonDownloadState.Queued,
    val progress: Int = 0,
    val downloadedBytes: Long = 0L,
    val totalBytes: Long? = null,
    val addedAt: Long,
    val errorMessage: String? = null,
)

data class ChimahonDownloadQueueData(
    val entries: List<ChimahonDownloadQueueEntry>,
    val paused: Boolean,
) {
    val pendingCount: Int
        get() = entries.count { it.status == ChimahonDownloadState.Queued }

    val activeCount: Int
        get() = entries.count { it.status == ChimahonDownloadState.Downloading }

    val failedCount: Int
        get() = entries.count { it.status == ChimahonDownloadState.Error }
}

data class ChimahonUiSettings(
    val keepReaderControlsVisible: Boolean = false,
    val rightToLeftByDefault: Boolean = false,
    val showUnreadBadges: Boolean = true,
    val showCategoryTabs: Boolean = true,
    val showHiddenCategories: Boolean = false,
)

data class ChimahonDownloadSettings(
    val downloadOnlyOverWifi: Boolean = true,
    val autoDownloadNewChapters: Boolean = false,
    val autoDownloadUnreadOnly: Boolean = true,
    val parallelDownloads: Int = 2,
)

data class ChimahonServiceSettings(
    val ui: ChimahonUiSettings = ChimahonUiSettings(),
    val downloads: ChimahonDownloadSettings = ChimahonDownloadSettings(),
)
