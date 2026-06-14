package app.chimahon.shared

data class ChimahonThumbnailCacheData(
    val entryCount: Int,
    val sizeBytes: Long,
)

data class ChimahonUpdateIssueAggregate(
    val messageId: Long,
    val message: String,
    val issueCount: Int,
    val affectedMangaCount: Int,
    val latestUpdateEpochSeconds: Long,
)

data class ChimahonUpdateIssueSummary(
    val totalIssueCount: Int,
    val affectedMangaCount: Int,
    val staleIssueCount: Int,
    val groups: List<ChimahonUpdateIssueAggregate>,
)

data class ChimahonDatabaseMaintenanceData(
    val historyEntryCount: Int,
    val updateIssues: ChimahonUpdateIssueSummary,
)

data class ChimahonDataMaintenanceSnapshot(
    val storage: ChimahonStorageData,
    val thumbnailCache: ChimahonThumbnailCacheData,
    val database: ChimahonDatabaseMaintenanceData,
)

enum class ChimahonCacheClearTarget {
    Thumbnails,
    ApplicationCache,
}

data class ChimahonMaintenanceFailure(
    val path: String,
    val reason: String,
)

data class ChimahonCacheClearResult(
    val target: ChimahonCacheClearTarget,
    val diskBytesRemoved: Long,
    val diskFilesRemoved: Int,
    val diskDirectoriesRemoved: Int,
    val memoryBytesRemoved: Long,
    val memoryEntriesRemoved: Int,
    val failures: List<ChimahonMaintenanceFailure> = emptyList(),
) {
    val successful: Boolean
        get() = failures.isEmpty()
}

data class ChimahonDatabaseMaintenanceResult(
    val historyEntriesRemoved: Int,
    val updateIssuesRemoved: Int,
    val remainingHistoryEntryCount: Int,
    val remainingUpdateIssueCount: Int,
)
