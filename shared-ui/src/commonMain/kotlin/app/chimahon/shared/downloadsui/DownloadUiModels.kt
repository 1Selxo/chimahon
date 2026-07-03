package app.chimahon.shared.downloadsui

import app.chimahon.shared.ChimahonDownloadQueueData
import app.chimahon.shared.ChimahonDownloadQueueEntry
import app.chimahon.shared.ChimahonDownloadState

enum class ChimahonDownloadQueueFilter(val title: String) {
    All("All"),
    Active("Active"),
    Queued("Queued"),
    Paused("Paused"),
    Complete("Complete"),
    Failed("Failed"),
}

enum class ChimahonDownloadQueueGroupMode(val title: String) {
    Source("Source"),
    Manga("Manga"),
    Status("Status"),
    None("None"),
}

enum class ChimahonDownloadQueueDisplayMode(val title: String) {
    Comfortable("Comfortable"),
    Compact("Compact"),
}

data class ChimahonDownloadsUiState(
    val queue: ChimahonDownloadQueueData? = null,
    val sourceNames: Map<Long, String> = emptyMap(),
    val selectedEntryIds: Set<String> = emptySet(),
    val query: String = "",
    val filter: ChimahonDownloadQueueFilter = ChimahonDownloadQueueFilter.All,
    val groupMode: ChimahonDownloadQueueGroupMode = ChimahonDownloadQueueGroupMode.Source,
    val displayMode: ChimahonDownloadQueueDisplayMode = ChimahonDownloadQueueDisplayMode.Comfortable,
    val loading: Boolean = false,
    val processingEntryId: String? = null,
    val message: String? = null,
) {
    val entries: List<ChimahonDownloadQueueEntry>
        get() = queue?.entries.orEmpty()

    val selectedCount: Int
        get() = selectedEntryIds.size

    val selectionMode: Boolean
        get() = selectedEntryIds.isNotEmpty()

    val filteredEntries: List<ChimahonDownloadQueueEntry>
        get() = entries.filterDownloads(query = query, filter = filter)

    val summary: ChimahonDownloadQueueSummary
        get() = ChimahonDownloadQueueSummary.from(queue)
}

data class ChimahonDownloadQueueSummary(
    val totalCount: Int = 0,
    val queuedCount: Int = 0,
    val activeCount: Int = 0,
    val pausedCount: Int = 0,
    val completedCount: Int = 0,
    val failedCount: Int = 0,
    val downloadedBytes: Long = 0L,
    val totalBytes: Long? = null,
) {
    val hasWork: Boolean
        get() = totalCount > 0

    val hasRecoverableFailures: Boolean
        get() = failedCount > 0

    companion object {
        fun from(queue: ChimahonDownloadQueueData?): ChimahonDownloadQueueSummary {
            val entries = queue?.entries.orEmpty()
            val knownTotalBytes = entries.mapNotNull(ChimahonDownloadQueueEntry::totalBytes)
            return ChimahonDownloadQueueSummary(
                totalCount = entries.size,
                queuedCount = entries.count { it.status == ChimahonDownloadState.Queued },
                activeCount = entries.count { it.status == ChimahonDownloadState.Downloading },
                pausedCount = entries.count { it.status == ChimahonDownloadState.Paused },
                completedCount = entries.count { it.status == ChimahonDownloadState.Downloaded },
                failedCount = entries.count { it.status == ChimahonDownloadState.Error },
                downloadedBytes = entries.sumOf(ChimahonDownloadQueueEntry::downloadedBytes),
                totalBytes = knownTotalBytes.takeIf { it.isNotEmpty() }?.sum(),
            )
        }
    }
}

data class ChimahonDownloadQueueGroup(
    val key: String,
    val title: String,
    val subtitle: String,
    val entries: List<ChimahonDownloadQueueEntry>,
)

data class ChimahonDownloadQueueActions(
    val onRefresh: () -> Unit = {},
    val onPauseQueue: () -> Unit = {},
    val onResumeQueue: () -> Unit = {},
    val onProcessNext: () -> Unit = {},
    val onClearCompleted: () -> Unit = {},
    val onRetryFailed: () -> Unit = {},
    val onSelectEntry: (ChimahonDownloadQueueEntry) -> Unit = {},
    val onOpenEntry: (ChimahonDownloadQueueEntry) -> Unit = {},
    val onPauseEntry: (ChimahonDownloadQueueEntry) -> Unit = {},
    val onResumeEntry: (ChimahonDownloadQueueEntry) -> Unit = {},
    val onRetryEntry: (ChimahonDownloadQueueEntry) -> Unit = {},
    val onRemoveEntry: (ChimahonDownloadQueueEntry) -> Unit = {},
    val onMoveEntryToTop: (ChimahonDownloadQueueEntry) -> Unit = {},
    val onMoveEntryToBottom: (ChimahonDownloadQueueEntry) -> Unit = {},
)

fun ChimahonDownloadsUiState.groups(): List<ChimahonDownloadQueueGroup> {
    val entries = filteredEntries
    if (entries.isEmpty()) return emptyList()
    return when (groupMode) {
        ChimahonDownloadQueueGroupMode.Source -> entries
            .groupBy(ChimahonDownloadQueueEntry::sourceId)
            .toList()
            .sortedBy { sourceNames[it.first].orEmpty().lowercase() }
            .map { (sourceId, groupEntries) ->
                val sourceName = sourceNames[sourceId] ?: "Source $sourceId"
                ChimahonDownloadQueueGroup(
                    key = "source:$sourceId",
                    title = sourceName,
                    subtitle = groupEntries.downloadGroupSubtitle(),
                    entries = groupEntries,
                )
            }
        ChimahonDownloadQueueGroupMode.Manga -> entries
            .groupBy { "${it.mangaId}:${it.mangaTitle}" }
            .toList()
            .sortedBy { it.second.firstOrNull()?.mangaTitle.orEmpty().lowercase() }
            .map { (key, groupEntries) ->
                ChimahonDownloadQueueGroup(
                    key = "manga:$key",
                    title = groupEntries.firstOrNull()?.mangaTitle ?: "Manga",
                    subtitle = groupEntries.downloadGroupSubtitle(),
                    entries = groupEntries,
                )
            }
        ChimahonDownloadQueueGroupMode.Status -> entries
            .groupBy(ChimahonDownloadQueueEntry::status)
            .toList()
            .sortedBy { it.first.downloadStatusSortOrder() }
            .map { (status, groupEntries) ->
                ChimahonDownloadQueueGroup(
                    key = "status:${status.name}",
                    title = status.downloadTitle(),
                    subtitle = groupEntries.downloadGroupSubtitle(),
                    entries = groupEntries,
                )
            }
        ChimahonDownloadQueueGroupMode.None -> listOf(
            ChimahonDownloadQueueGroup(
                key = "all",
                title = "Queue",
                subtitle = entries.downloadGroupSubtitle(),
                entries = entries,
            ),
        )
    }
}

fun List<ChimahonDownloadQueueEntry>.filterDownloads(
    query: String,
    filter: ChimahonDownloadQueueFilter,
): List<ChimahonDownloadQueueEntry> {
    val normalizedQuery = query.trim()
    return filter { entry ->
        (
            normalizedQuery.isBlank() ||
                entry.mangaTitle.contains(normalizedQuery, ignoreCase = true) ||
                entry.chapterName.contains(normalizedQuery, ignoreCase = true) ||
                entry.chapterUrl.contains(normalizedQuery, ignoreCase = true) ||
                entry.errorMessage.orEmpty().contains(normalizedQuery, ignoreCase = true)
            ) &&
            when (filter) {
                ChimahonDownloadQueueFilter.All -> true
                ChimahonDownloadQueueFilter.Active -> entry.status == ChimahonDownloadState.Downloading
                ChimahonDownloadQueueFilter.Queued -> entry.status == ChimahonDownloadState.Queued
                ChimahonDownloadQueueFilter.Paused -> entry.status == ChimahonDownloadState.Paused
                ChimahonDownloadQueueFilter.Complete -> entry.status == ChimahonDownloadState.Downloaded
                ChimahonDownloadQueueFilter.Failed -> entry.status == ChimahonDownloadState.Error
            }
    }
}

fun ChimahonDownloadQueueEntry.progressFraction(): Float {
    return when {
        status == ChimahonDownloadState.Downloaded -> 1f
        progress > 0 -> progress.coerceIn(0, 100) / 100f
        totalBytes != null && totalBytes > 0L -> downloadedBytes.toFloat() / totalBytes.toFloat()
        else -> 0f
    }.coerceIn(0f, 1f)
}

fun ChimahonDownloadQueueEntry.progressLabel(): String {
    return when {
        status == ChimahonDownloadState.Downloaded -> "100%"
        progress > 0 -> "${progress.coerceIn(0, 100)}%"
        totalBytes != null && totalBytes > 0L -> "${(progressFraction() * 100f).toInt()}%"
        status == ChimahonDownloadState.Downloading -> "..."
        else -> ""
    }
}

fun ChimahonDownloadQueueEntry.sizeLabel(): String {
    val total = totalBytes
    return when {
        total != null && total > 0L -> "${downloadedBytes.toReadableBytes()} / ${total.toReadableBytes()}"
        downloadedBytes > 0L -> downloadedBytes.toReadableBytes()
        else -> "Size unknown"
    }
}

fun ChimahonDownloadQueueEntry.detailSubtitle(sourceName: String, position: Int): String {
    return listOfNotNull(
        "#${position + 1}",
        sourceName,
        status.downloadTitle(),
        errorMessage?.takeIf(String::isNotBlank),
    ).joinToString(" - ")
}

fun ChimahonDownloadQueueEntry.primaryActionTitle(): String {
    return when (status) {
        ChimahonDownloadState.Queued -> "Pause"
        ChimahonDownloadState.Downloading -> "Pause"
        ChimahonDownloadState.Paused -> "Resume"
        ChimahonDownloadState.Downloaded -> "Remove"
        ChimahonDownloadState.Error -> "Retry"
    }
}

fun ChimahonDownloadQueueEntry.canMove(): Boolean {
    return status == ChimahonDownloadState.Queued || status == ChimahonDownloadState.Paused
}

fun ChimahonDownloadState.downloadTitle(): String {
    return when (this) {
        ChimahonDownloadState.Queued -> "Queued"
        ChimahonDownloadState.Downloading -> "Downloading"
        ChimahonDownloadState.Paused -> "Paused"
        ChimahonDownloadState.Downloaded -> "Complete"
        ChimahonDownloadState.Error -> "Failed"
    }
}

fun Long.toReadableBytes(): String {
    if (this <= 0L) return "0 B"
    val units = listOf("B", "KB", "MB", "GB", "TB")
    var size = toDouble()
    var unitIndex = 0
    while (size >= 1024.0 && unitIndex < units.lastIndex) {
        size /= 1024.0
        unitIndex++
    }
    return if (unitIndex == 0) {
        "${size.toLong()} ${units[unitIndex]}"
    } else {
        "${(size * 10.0).toInt() / 10.0} ${units[unitIndex]}"
    }
}

private fun List<ChimahonDownloadQueueEntry>.downloadGroupSubtitle(): String {
    val active = count { it.status == ChimahonDownloadState.Downloading }
    val queued = count { it.status == ChimahonDownloadState.Queued }
    val paused = count { it.status == ChimahonDownloadState.Paused }
    val failed = count { it.status == ChimahonDownloadState.Error }
    val complete = count { it.status == ChimahonDownloadState.Downloaded }
    return listOfNotNull(
        "$size chapter(s)",
        active.takeIf { it > 0 }?.let { "$it active" },
        queued.takeIf { it > 0 }?.let { "$it queued" },
        paused.takeIf { it > 0 }?.let { "$it paused" },
        failed.takeIf { it > 0 }?.let { "$it failed" },
        complete.takeIf { it > 0 }?.let { "$it done" },
    ).joinToString(" - ")
}

private fun ChimahonDownloadState.downloadStatusSortOrder(): Int {
    return when (this) {
        ChimahonDownloadState.Downloading -> 0
        ChimahonDownloadState.Queued -> 1
        ChimahonDownloadState.Paused -> 2
        ChimahonDownloadState.Error -> 3
        ChimahonDownloadState.Downloaded -> 4
    }
}
