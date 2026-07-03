package app.chimahon.shared.downloadhistoryui

import app.chimahon.shared.ChimahonHistoryEntry
import app.chimahon.shared.ChimahonMangaEntry
import app.chimahon.shared.ChimahonRecentUpdateEntry
import app.chimahon.shared.timelineui.timelineChapterNumberLabel
import app.chimahon.shared.timelineui.timelineDateBucket
import app.chimahon.shared.timelineui.timelineEpochDay
import app.chimahon.shared.timelineui.timelineEpochMillis
import app.chimahon.shared.timelineui.timelineReadingDurationLabel

fun chimahonUpdateRowId(
    mediaKind: ChimahonMediaKind,
    chapterId: Long,
): String = "update:${mediaKind.name}:$chapterId"

fun chimahonHistoryRowId(
    mediaKind: ChimahonMediaKind,
    historyId: Long,
): String = "history:${mediaKind.name}:$historyId"

fun ChimahonRecentUpdateEntry.toChimahonUpdateRowUiModel(
    mediaKind: ChimahonMediaKind = ChimahonMediaKind.Manga,
    manga: ChimahonMangaEntry? = null,
    selected: Boolean = false,
    downloadedCount: Int = 0,
    sourceLabel: String? = null,
    language: String? = null,
    updateGroupLabel: String? = null,
    nowEpochMillis: Long? = null,
    actions: List<ChimahonDownloadHistoryAction> = emptyList(),
): ChimahonUpdateRowUiModel {
    return ChimahonUpdateRowUiModel(
        id = chimahonUpdateRowId(mediaKind, chapterId),
        mediaKind = mediaKind,
        entryId = mangaId.toString(),
        entryTitle = mangaTitle,
        itemId = chapterId.toString(),
        itemTitle = chapterName,
        updateGroupLabel = updateGroupLabel,
        dateLabel = dateFetch.timelineDateBucket("Fetched", nowEpochMillis),
        newItemCount = 1,
        unreadCount = if (read) 0 else 1,
        downloadedCount = downloadedCount,
        bookmarked = bookmarked,
        thumbnailUrl = manga?.thumbnailUrl,
        sourceName = sourceLabel,
        language = language,
        selected = selected,
        actions = actions,
        sourceId = this.sourceId,
        fetchedAt = dateFetch.timelineEpochMillis(),
    )
}

fun List<ChimahonRecentUpdateEntry>.toChimahonUpdateRowUiModels(
    mediaKind: ChimahonMediaKind = ChimahonMediaKind.Manga,
    mangaById: Map<Long, ChimahonMangaEntry> = emptyMap(),
    selectedIds: Set<String> = emptySet(),
    downloadedChapterIds: Set<Long> = emptySet(),
    sourceLabels: Map<Long, String> = emptyMap(),
    languageLabels: Map<Long, String> = emptyMap(),
    sort: ChimahonDownloadHistorySort = ChimahonDownloadHistorySort.Newest,
    nowEpochMillis: Long? = null,
): List<ChimahonUpdateRowUiModel> {
    return sortedUpdatesForDownloadHistory(sort, sourceLabels)
        .map { update ->
            update.toChimahonUpdateRowUiModel(
                mediaKind = mediaKind,
                manga = mangaById[update.mangaId],
                selected = chimahonUpdateRowId(mediaKind, update.chapterId) in selectedIds,
                downloadedCount = if (update.chapterId in downloadedChapterIds) 1 else 0,
                sourceLabel = sourceLabels[update.sourceId],
                language = languageLabels[update.sourceId],
                nowEpochMillis = nowEpochMillis,
            )
        }
}

fun List<ChimahonRecentUpdateEntry>.toChimahonUpdateGroups(
    mediaKind: ChimahonMediaKind = ChimahonMediaKind.Manga,
    mangaById: Map<Long, ChimahonMangaEntry> = emptyMap(),
    selectedIds: Set<String> = emptySet(),
    downloadedChapterIds: Set<Long> = emptySet(),
    sourceLabels: Map<Long, String> = emptyMap(),
    languageLabels: Map<Long, String> = emptyMap(),
    grouping: ChimahonDownloadHistoryGrouping = ChimahonDownloadHistoryGrouping.Date,
    sort: ChimahonDownloadHistorySort = ChimahonDownloadHistorySort.Newest,
    nowEpochMillis: Long? = null,
): List<ChimahonDownloadHistoryGroup<ChimahonUpdateRowUiModel>> {
    return sortedUpdatesForDownloadHistory(sort, sourceLabels)
        .groupBy { it.downloadHistoryGroupKey(grouping, sourceLabels, nowEpochMillis, prefix = "updates") }
        .map { (groupKey, entries) ->
            val rows = entries.map { update ->
                update.toChimahonUpdateRowUiModel(
                    mediaKind = mediaKind,
                    manga = mangaById[update.mangaId],
                    selected = chimahonUpdateRowId(mediaKind, update.chapterId) in selectedIds,
                    downloadedCount = if (update.chapterId in downloadedChapterIds) 1 else 0,
                    sourceLabel = sourceLabels[update.sourceId],
                    language = languageLabels[update.sourceId],
                    updateGroupLabel = groupKey.rowLabel,
                    nowEpochMillis = nowEpochMillis,
                )
            }
            ChimahonDownloadHistoryGroup(
                id = groupKey.id,
                title = groupKey.title,
                subtitle = listOfNotNull(groupKey.subtitle, entries.updateCountLabel(mediaKind)).joinToString(" - "),
                rows = rows,
            )
        }
}

fun ChimahonHistoryEntry.toChimahonHistoryRowUiModel(
    mediaKind: ChimahonMediaKind = ChimahonMediaKind.Manga,
    manga: ChimahonMangaEntry? = null,
    selected: Boolean = false,
    inLibrary: Boolean = manga?.favorite ?: true,
    sourceLabel: String? = null,
    language: String? = null,
    nowEpochMillis: Long? = null,
    actions: List<ChimahonDownloadHistoryAction> = emptyList(),
): ChimahonHistoryRowUiModel {
    val completed = read || (totalCount > 0.0 && readCount >= totalCount)
    return ChimahonHistoryRowUiModel(
        id = chimahonHistoryRowId(mediaKind, id),
        mediaKind = mediaKind,
        entryId = mangaId.toString(),
        entryTitle = title,
        itemId = chapterId.toString(),
        itemTitle = chapterNumber.timelineChapterNumberLabel() ?: "${mediaKind.itemTitle} $chapterId",
        consumedAtLabel = readAt?.timelineDateBucket("Read", nowEpochMillis) ?: "Unknown date",
        progressLabel = historyProgressLabel(mediaKind),
        durationLabel = readDuration.takeIf { it > 0L }?.timelineReadingDurationLabel(),
        continueLabel = historyContinueLabel(completed),
        inLibrary = inLibrary,
        completed = completed,
        thumbnailUrl = manga?.thumbnailUrl,
        sourceName = sourceLabel,
        language = language,
        selected = selected,
        actions = actions,
        sourceId = this.sourceId,
        consumedAt = readAt?.timelineEpochMillis(),
    )
}

fun List<ChimahonHistoryEntry>.toChimahonHistoryRowUiModels(
    mediaKind: ChimahonMediaKind = ChimahonMediaKind.Manga,
    mangaById: Map<Long, ChimahonMangaEntry> = emptyMap(),
    selectedIds: Set<String> = emptySet(),
    libraryEntryIds: Set<Long> = mangaById.values.filter { it.favorite }.mapTo(mutableSetOf()) { it.id },
    sourceLabels: Map<Long, String> = emptyMap(),
    languageLabels: Map<Long, String> = emptyMap(),
    sort: ChimahonDownloadHistorySort = ChimahonDownloadHistorySort.Newest,
    nowEpochMillis: Long? = null,
): List<ChimahonHistoryRowUiModel> {
    return sortedHistoryForDownloadHistory(sort, sourceLabels)
        .map { history ->
            history.toChimahonHistoryRowUiModel(
                mediaKind = mediaKind,
                manga = mangaById[history.mangaId],
                selected = chimahonHistoryRowId(mediaKind, history.id) in selectedIds,
                inLibrary = history.mangaId in libraryEntryIds,
                sourceLabel = sourceLabels[history.sourceId],
                language = languageLabels[history.sourceId],
                nowEpochMillis = nowEpochMillis,
            )
        }
}

fun List<ChimahonHistoryEntry>.toChimahonHistoryGroups(
    mediaKind: ChimahonMediaKind = ChimahonMediaKind.Manga,
    mangaById: Map<Long, ChimahonMangaEntry> = emptyMap(),
    selectedIds: Set<String> = emptySet(),
    libraryEntryIds: Set<Long> = mangaById.values.filter { it.favorite }.mapTo(mutableSetOf()) { it.id },
    sourceLabels: Map<Long, String> = emptyMap(),
    languageLabels: Map<Long, String> = emptyMap(),
    grouping: ChimahonDownloadHistoryGrouping = ChimahonDownloadHistoryGrouping.Date,
    sort: ChimahonDownloadHistorySort = ChimahonDownloadHistorySort.Newest,
    nowEpochMillis: Long? = null,
): List<ChimahonDownloadHistoryGroup<ChimahonHistoryRowUiModel>> {
    return sortedHistoryForDownloadHistory(sort, sourceLabels)
        .groupBy { it.downloadHistoryGroupKey(grouping, sourceLabels, nowEpochMillis, prefix = "history") }
        .map { (groupKey, entries) ->
            val rows = entries.map { history ->
                history.toChimahonHistoryRowUiModel(
                    mediaKind = mediaKind,
                    manga = mangaById[history.mangaId],
                    selected = chimahonHistoryRowId(mediaKind, history.id) in selectedIds,
                    inLibrary = history.mangaId in libraryEntryIds,
                    sourceLabel = sourceLabels[history.sourceId],
                    language = languageLabels[history.sourceId],
                    nowEpochMillis = nowEpochMillis,
                )
            }
            ChimahonDownloadHistoryGroup(
                id = groupKey.id,
                title = groupKey.title,
                subtitle = listOfNotNull(groupKey.subtitle, entries.historyCountLabel(mediaKind)).joinToString(" - "),
                rows = rows,
            )
        }
}

private data class ChimahonDownloadHistoryGroupKey(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val rowLabel: String? = null,
)

private fun ChimahonRecentUpdateEntry.downloadHistoryGroupKey(
    grouping: ChimahonDownloadHistoryGrouping,
    sourceLabels: Map<Long, String>,
    nowEpochMillis: Long?,
    prefix: String,
): ChimahonDownloadHistoryGroupKey {
    val day = dateFetch.timelineEpochDay()
    val dateTitle = dateFetch.timelineDateBucket("Fetched", nowEpochMillis)
    val sourceTitle = sourceLabels[sourceId]?.takeIf { it.isNotBlank() } ?: "Source $sourceId"
    return downloadHistoryGroupKey(prefix, grouping, day, dateTitle, sourceId, sourceTitle)
}

private fun ChimahonHistoryEntry.downloadHistoryGroupKey(
    grouping: ChimahonDownloadHistoryGrouping,
    sourceLabels: Map<Long, String>,
    nowEpochMillis: Long?,
    prefix: String,
): ChimahonDownloadHistoryGroupKey {
    val day = readAt?.timelineEpochDay() ?: 0L
    val dateTitle = readAt?.timelineDateBucket("Read", nowEpochMillis) ?: "Unknown date"
    val sourceTitle = sourceLabels[sourceId]?.takeIf { it.isNotBlank() } ?: "Source $sourceId"
    return downloadHistoryGroupKey(prefix, grouping, day, dateTitle, sourceId, sourceTitle)
}

private fun downloadHistoryGroupKey(
    prefix: String,
    grouping: ChimahonDownloadHistoryGrouping,
    day: Long,
    dateTitle: String,
    sourceId: Long,
    sourceTitle: String,
): ChimahonDownloadHistoryGroupKey {
    return when (grouping) {
        ChimahonDownloadHistoryGrouping.Date -> ChimahonDownloadHistoryGroupKey(
            id = "$prefix:date:$day",
            title = dateTitle,
        )
        ChimahonDownloadHistoryGrouping.DateAndSource -> ChimahonDownloadHistoryGroupKey(
            id = "$prefix:date:$day:source:$sourceId",
            title = dateTitle,
            subtitle = sourceTitle,
            rowLabel = sourceTitle,
        )
        ChimahonDownloadHistoryGrouping.Source -> ChimahonDownloadHistoryGroupKey(
            id = "$prefix:source:$sourceId",
            title = sourceTitle,
            rowLabel = dateTitle,
        )
    }
}

private fun List<ChimahonRecentUpdateEntry>.sortedUpdatesForDownloadHistory(
    sort: ChimahonDownloadHistorySort,
    sourceLabels: Map<Long, String>,
): List<ChimahonRecentUpdateEntry> {
    return when (sort) {
        ChimahonDownloadHistorySort.Newest -> sortedWith(
            compareByDescending<ChimahonRecentUpdateEntry> { it.dateFetch }
                .thenBy { it.mangaTitle.lowercase() }
                .thenBy { it.chapterName.lowercase() },
        )
        ChimahonDownloadHistorySort.Oldest -> sortedWith(
            compareBy<ChimahonRecentUpdateEntry> { it.dateFetch }
                .thenBy { it.mangaTitle.lowercase() }
                .thenBy { it.chapterName.lowercase() },
        )
        ChimahonDownloadHistorySort.Title -> sortedWith(
            compareBy<ChimahonRecentUpdateEntry> { it.mangaTitle.lowercase() }
                .thenByDescending { it.dateFetch },
        )
        ChimahonDownloadHistorySort.Source -> sortedWith(
            compareBy<ChimahonRecentUpdateEntry> { sourceLabels[it.sourceId]?.lowercase() ?: it.sourceId.toString() }
                .thenByDescending { it.dateFetch },
        )
    }
}

private fun List<ChimahonHistoryEntry>.sortedHistoryForDownloadHistory(
    sort: ChimahonDownloadHistorySort,
    sourceLabels: Map<Long, String>,
): List<ChimahonHistoryEntry> {
    return when (sort) {
        ChimahonDownloadHistorySort.Newest -> sortedByDescending { it.readAt ?: 0L }
        ChimahonDownloadHistorySort.Oldest -> sortedBy { it.readAt ?: Long.MAX_VALUE }
        ChimahonDownloadHistorySort.Title -> sortedWith(
            compareBy<ChimahonHistoryEntry> { it.title.lowercase() }
                .thenByDescending { it.readAt ?: 0L },
        )
        ChimahonDownloadHistorySort.Source -> sortedWith(
            compareBy<ChimahonHistoryEntry> { sourceLabels[it.sourceId]?.lowercase() ?: it.sourceId.toString() }
                .thenByDescending { it.readAt ?: 0L },
        )
    }
}

private fun List<ChimahonRecentUpdateEntry>.updateCountLabel(mediaKind: ChimahonMediaKind): String {
    val entryCount = map { it.mangaId }.distinct().size
    val itemCount = size
    val entryLabel = if (entryCount == 1) "1 ${mediaKind.entryTitle.lowercase()}" else "$entryCount ${mediaKind.entryTitle.lowercase()}s"
    val itemLabel = if (itemCount == 1) "1 ${mediaKind.itemTitle.lowercase()}" else "$itemCount ${mediaKind.itemTitle.lowercase()}s"
    return "$entryLabel - $itemLabel"
}

private fun List<ChimahonHistoryEntry>.historyCountLabel(mediaKind: ChimahonMediaKind): String {
    val entryCount = map { it.mangaId }.distinct().size
    val itemCount = size
    val entryLabel = if (entryCount == 1) "1 ${mediaKind.entryTitle.lowercase()}" else "$entryCount ${mediaKind.entryTitle.lowercase()}s"
    val itemLabel = if (itemCount == 1) "1 item" else "$itemCount items"
    return "$entryLabel - $itemLabel"
}

private fun ChimahonHistoryEntry.historyProgressLabel(mediaKind: ChimahonMediaKind): String? {
    return when {
        totalCount > 0.0 -> "${readCount.toLong()}/${totalCount.toLong()} ${mediaKind.itemTitle.lowercase()}s"
        lastPageRead > 0L -> "Page ${lastPageRead + 1L}"
        else -> null
    }
}

private fun ChimahonHistoryEntry.historyContinueLabel(completed: Boolean): String {
    return when {
        completed -> "Read again"
        lastPageRead > 0L -> "Resume page ${lastPageRead + 1L}"
        readCount > 0.0 && totalCount > 0.0 -> "Resume ${readCount.toLong()}/${totalCount.toLong()}"
        else -> "Start"
    }
}
