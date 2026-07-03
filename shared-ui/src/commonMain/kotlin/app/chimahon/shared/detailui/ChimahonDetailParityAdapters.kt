package app.chimahon.shared.detailui

import app.chimahon.shared.ChimahonChapterDownloadStatus
import app.chimahon.shared.ChimahonChapterEntry
import app.chimahon.shared.ChimahonDownloadState
import app.chimahon.shared.ChimahonHistoryEntry
import app.chimahon.shared.ChimahonMangaEntry
import app.chimahon.shared.ChimahonRecentUpdateEntry
import app.chimahon.shared.ChimahonRemoteChapterEntry
import app.chimahon.shared.ChimahonRemoteMangaDetail

fun ChimahonDetailHeaderModel.toCollapsingHeaderMetadata(
    summary: ChimahonDetailChapterSummary = ChimahonDetailChapterSummary(),
    actions: List<ChimahonDetailActionModel> = defaultAndroidDetailActions(summary),
    updateLabel: String? = null,
    historyLabel: String? = null,
): ChimahonDetailCollapsingHeaderMetadata {
    return ChimahonDetailCollapsingHeaderMetadata(
        id = id,
        title = title,
        subtitle = creatorLine ?: sourceName,
        sourceName = sourceName,
        thumbnailUrl = thumbnailUrl,
        creatorLine = creatorLine,
        statusLabel = displayStatus,
        description = description,
        genres = genres,
        summaryLabel = summary.androidDetailSummaryLabel(),
        badges = parityBadges(
            summary = summary,
            updateLabel = updateLabel,
            historyLabel = historyLabel,
        ),
        actions = actions,
        inLibrary = inLibrary,
        initialized = initialized,
        sourceUrl = sourceUrl,
    )
}

fun ChimahonMangaEntry.toCollapsingHeaderMetadata(
    sourceName: String,
    summary: ChimahonDetailChapterSummary = ChimahonDetailChapterSummary(),
    updateLabel: String? = null,
    historyLabel: String? = null,
): ChimahonDetailCollapsingHeaderMetadata {
    return toChimahonDetailHeaderModel(sourceName)
        .toCollapsingHeaderMetadata(
            summary = summary,
            updateLabel = updateLabel ?: lastUpdate?.takeIf { it > 0L }?.let { "Updated" },
            historyLabel = historyLabel,
        )
}

fun ChimahonRemoteMangaDetail.toCollapsingHeaderMetadata(
    inLibrary: Boolean = false,
    summary: ChimahonDetailChapterSummary? = null,
): ChimahonDetailCollapsingHeaderMetadata {
    return toChimahonDetailHeaderModel(inLibrary = inLibrary)
        .toCollapsingHeaderMetadata(summary = summary ?: chapters.toChimahonRemoteDetailChapterSummary())
}

fun ChimahonDetailHeaderModel.defaultAndroidDetailActions(
    summary: ChimahonDetailChapterSummary = ChimahonDetailChapterSummary(),
): List<ChimahonDetailActionModel> {
    val libraryAction = if (kind == ChimahonDetailKind.Remote && !inLibrary) {
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.AddToLibrary,
            label = "Add",
            selected = false,
        )
    } else {
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.Favorite,
            label = if (favorite || inLibrary) "Library" else "Add",
            selected = favorite || inLibrary,
        )
    }
    val readAction = ChimahonDetailActionModel(
        action = if (summary.readCount > 0) ChimahonDetailAction.Resume else ChimahonDetailAction.Start,
        label = if (summary.readCount > 0) "Resume" else "Start",
        enabled = summary.hasChapters,
        supportingLabel = summary.nextReadHint(),
    )
    return listOf(
        libraryAction,
        readAction,
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.Download,
            label = "Download",
            selected = summary.downloadedCount > 0 && summary.downloadedCount >= summary.totalCount,
            enabled = summary.hasChapters,
            supportingLabel = if (summary.downloadedCount > 0) "${summary.downloadedCount}" else null,
        ),
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.Tracking,
            label = "Tracking",
        ),
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.Web,
            label = "Web",
            enabled = !url.isNullOrBlank(),
        ),
        ChimahonDetailActionModel(
            action = ChimahonDetailAction.Refresh,
            label = "Refresh",
        ),
    )
}

fun ChimahonDetailHeaderModel.parityBadges(
    summary: ChimahonDetailChapterSummary = ChimahonDetailChapterSummary(),
    updateLabel: String? = null,
    historyLabel: String? = null,
): List<ChimahonDetailParityBadgeModel> {
    return buildList {
        displayStatus.takeIf(String::isNotBlank)?.let {
            add(ChimahonDetailParityBadgeModel(it, ChimahonDetailParityBadgeKind.Status))
        }
        sourceName.takeIf(String::isNotBlank)?.let {
            add(ChimahonDetailParityBadgeModel(it, ChimahonDetailParityBadgeKind.Source))
        }
        creatorLine?.let {
            add(ChimahonDetailParityBadgeModel(it, ChimahonDetailParityBadgeKind.Creator))
        }
        if (inLibrary) add(ChimahonDetailParityBadgeModel("In library", ChimahonDetailParityBadgeKind.Library))
        if (!initialized) add(ChimahonDetailParityBadgeModel("Needs refresh", ChimahonDetailParityBadgeKind.Warning))
        if (summary.totalCount > 0) {
            add(ChimahonDetailParityBadgeModel("${summary.totalCount} chapters", ChimahonDetailParityBadgeKind.ChapterCount))
        }
        if (summary.unreadCount > 0) {
            add(ChimahonDetailParityBadgeModel("${summary.unreadCount} unread", ChimahonDetailParityBadgeKind.Unread))
        }
        if (summary.bookmarkedCount > 0) {
            add(ChimahonDetailParityBadgeModel("${summary.bookmarkedCount} bookmarked", ChimahonDetailParityBadgeKind.Bookmarked))
        }
        if (summary.downloadedCount > 0) {
            add(ChimahonDetailParityBadgeModel("${summary.downloadedCount} downloaded", ChimahonDetailParityBadgeKind.Downloaded))
        }
        updateLabel?.takeIf(String::isNotBlank)?.let {
            add(ChimahonDetailParityBadgeModel(it, ChimahonDetailParityBadgeKind.Update))
        }
        historyLabel?.takeIf(String::isNotBlank)?.let {
            add(ChimahonDetailParityBadgeModel(it, ChimahonDetailParityBadgeKind.History))
        }
        if (!sourceUrl.isNullOrBlank()) add(ChimahonDetailParityBadgeModel("Source URL", ChimahonDetailParityBadgeKind.Url))
    }
}

fun ChimahonChapterEntry.toChimahonDetailChapterRowModel(
    sourceName: String? = null,
    dateLabel: String? = null,
    readProgressLabel: String? = null,
    downloadStatus: ChimahonChapterDownloadStatus? = null,
): ChimahonDetailChapterRowModel {
    return ChimahonDetailChapterRowModel(
        id = id.toString(),
        title = name,
        url = url,
        sourceOrder = sourceOrder.toIntOrMax(),
        chapterNumber = chapterNumber,
        dateUploadEpochMillis = dateUpload.takeIf { it > 0L },
        dateLabel = dateLabel,
        readProgressLabel = readProgressLabel,
        scanlator = scanlator,
        sourceName = sourceName,
        read = read,
        bookmarked = bookmarked,
        downloaded = downloadStatus?.status == ChimahonDownloadState.Downloaded,
        downloadState = downloadStatus.toDetailDownloadState(),
        downloadProgress = downloadStatus?.progress ?: 0,
    )
}

fun ChimahonRemoteChapterEntry.toChimahonDetailChapterRowModel(
    sourceName: String? = null,
    dateLabel: String? = null,
): ChimahonDetailChapterRowModel {
    return ChimahonDetailChapterRowModel(
        id = url,
        title = name,
        url = url,
        sourceOrder = sourceOrder,
        chapterNumber = chapterNumber,
        dateUploadEpochMillis = dateUpload.takeIf { it > 0L },
        dateLabel = dateLabel,
        scanlator = scanlator,
        sourceName = sourceName,
    )
}

fun List<ChimahonChapterEntry>.toChimahonDetailChapterRows(
    sourceName: String? = null,
    dateLabelsByChapterId: Map<Long, String> = emptyMap(),
    readProgressLabelsByChapterId: Map<Long, String> = emptyMap(),
    downloadStatusesByChapterId: Map<Long, ChimahonChapterDownloadStatus> = emptyMap(),
): List<ChimahonDetailChapterRowModel> {
    return map { chapter ->
        chapter.toChimahonDetailChapterRowModel(
            sourceName = sourceName,
            dateLabel = dateLabelsByChapterId[chapter.id],
            readProgressLabel = readProgressLabelsByChapterId[chapter.id],
            downloadStatus = downloadStatusesByChapterId[chapter.id],
        )
    }
}

fun List<ChimahonDetailChapterRowModel>.filteredSortedForAndroidDetail(
    query: String = "",
    filters: ChimahonDetailChapterFilterState = ChimahonDetailChapterFilterState(),
    sortState: ChimahonDetailChapterSortState = ChimahonDetailChapterSortState(),
): List<ChimahonDetailChapterRowModel> {
    return filter { it.matchesAndroidDetail(query = query, filters = filters) }
        .sortedForAndroidDetail(sortState)
}

fun ChimahonDetailChapterRowModel.matchesAndroidDetail(
    query: String,
    filters: ChimahonDetailChapterFilterState,
): Boolean {
    if (query.isNotBlank()) {
        val needle = query.trim().lowercase()
        val haystack = listOf(
            title,
            scanlator.orEmpty(),
            sourceName.orEmpty(),
            chapterNumber.takeIf { it > 0.0 }?.toAdapterDisplayChapter().orEmpty(),
            dateLabel.orEmpty(),
            readProgressLabel.orEmpty(),
        ).joinToString(" ").lowercase()
        if (needle !in haystack) return false
    }
    val readMatches = when (filters.read) {
        ChimahonDetailReadFilter.Any -> true
        ChimahonDetailReadFilter.Unread -> !read
        ChimahonDetailReadFilter.Read -> read
    }
    val downloadMatches = when (filters.downloaded) {
        ChimahonDetailDownloadFilter.Any -> true
        ChimahonDetailDownloadFilter.Downloaded ->
            downloaded || downloadState == ChimahonDetailChapterDownloadState.Downloaded
        ChimahonDetailDownloadFilter.NotDownloaded ->
            !downloaded && downloadState != ChimahonDetailChapterDownloadState.Downloaded
    }
    val bookmarkMatches = when (filters.bookmarked) {
        ChimahonDetailBookmarkFilter.Any -> true
        ChimahonDetailBookmarkFilter.Bookmarked -> bookmarked
        ChimahonDetailBookmarkFilter.NotBookmarked -> !bookmarked
    }
    val scanlatorMatches = filters.scanlator.isNullOrBlank() ||
        scanlator.equals(filters.scanlator, ignoreCase = true)
    return readMatches && downloadMatches && bookmarkMatches && scanlatorMatches
}

fun List<ChimahonDetailChapterRowModel>.sortedForAndroidDetail(
    sortState: ChimahonDetailChapterSortState,
): List<ChimahonDetailChapterRowModel> {
    val comparator = Comparator<IndexedValue<ChimahonDetailChapterRowModel>> { left, right ->
        val base = when (sortState.sort) {
            ChimahonDetailChapterSort.SourceOrder -> left.value.sourceOrder.compareTo(right.value.sourceOrder)
            ChimahonDetailChapterSort.ChapterNumber -> left.value.chapterNumber.compareTo(right.value.chapterNumber)
            ChimahonDetailChapterSort.UploadDate ->
                compareAdapterNullableLong(left.value.dateUploadEpochMillis, right.value.dateUploadEpochMillis)
            ChimahonDetailChapterSort.Name -> left.value.title.lowercase().compareTo(right.value.title.lowercase())
            ChimahonDetailChapterSort.Scanlator ->
                left.value.scanlator.orEmpty().lowercase().compareTo(right.value.scanlator.orEmpty().lowercase())
        }
        val directed = if (sortState.sort == ChimahonDetailChapterSort.SourceOrder) {
            if (sortState.descending) base else -base
        } else {
            if (sortState.descending) -base else base
        }
        if (directed != 0) directed else left.index.compareTo(right.index)
    }
    return withIndex().sortedWith(comparator).map { it.value }
}

fun ChimahonRecentUpdateEntry.toDetailUpdateBadge(
    label: String = "Updated",
): ChimahonDetailParityBadgeModel {
    return ChimahonDetailParityBadgeModel(
        label = label,
        kind = ChimahonDetailParityBadgeKind.Update,
        contentDescription = "$mangaTitle ${chapterName.takeIf(String::isNotBlank) ?: "updated"}",
    )
}

fun ChimahonHistoryEntry.toDetailHistoryBadge(
    label: String? = null,
): ChimahonDetailParityBadgeModel {
    return ChimahonDetailParityBadgeModel(
        label = label ?: readAt?.takeIf { it > 0L }?.let { "History" } ?: "Started",
        kind = ChimahonDetailParityBadgeKind.History,
        contentDescription = "$title reading history",
    )
}

fun ChimahonDetailChapterSummary.androidDetailSummaryLabel(): String {
    if (totalCount <= 0) return "No chapters"
    return buildList {
        add("$totalCount chapters")
        if (unreadCount > 0) add("$unreadCount unread")
        if (downloadedCount > 0) add("$downloadedCount downloaded")
        if (bookmarkedCount > 0) add("$bookmarkedCount bookmarked")
        if (selectedCount > 0) add("$selectedCount selected")
    }.joinToString(" / ")
}

private fun ChimahonDetailChapterSummary.nextReadHint(): String? {
    return when {
        unreadCount > 0 -> "$unreadCount unread"
        totalCount > 0 -> "$totalCount chapters"
        else -> null
    }
}

private fun ChimahonChapterDownloadStatus?.toDetailDownloadState(): ChimahonDetailChapterDownloadState {
    return when (this?.status) {
        ChimahonDownloadState.Queued -> ChimahonDetailChapterDownloadState.Queued
        ChimahonDownloadState.Downloading -> ChimahonDetailChapterDownloadState.Downloading
        ChimahonDownloadState.Paused -> ChimahonDetailChapterDownloadState.Queued
        ChimahonDownloadState.Downloaded -> ChimahonDetailChapterDownloadState.Downloaded
        ChimahonDownloadState.Error -> ChimahonDetailChapterDownloadState.Error
        null -> ChimahonDetailChapterDownloadState.NotDownloaded
    }
}

private fun Long.toIntOrMax(): Int {
    return when {
        this > Int.MAX_VALUE -> Int.MAX_VALUE
        this < Int.MIN_VALUE -> Int.MIN_VALUE
        else -> toInt()
    }
}

private fun compareAdapterNullableLong(left: Long?, right: Long?): Int {
    return when {
        left == null && right == null -> 0
        left == null -> -1
        right == null -> 1
        else -> left.compareTo(right)
    }
}

private fun Double.toAdapterDisplayChapter(): String {
    if (this % 1.0 == 0.0) return toLong().toString()
    return toString().trimEnd('0').trimEnd('.')
}
