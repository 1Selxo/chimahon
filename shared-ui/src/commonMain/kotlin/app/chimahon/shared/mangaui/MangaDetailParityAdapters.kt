package app.chimahon.shared.mangaui

import app.chimahon.shared.ChimahonChapterDownloadStatus
import app.chimahon.shared.ChimahonChapterEntry
import app.chimahon.shared.ChimahonDownloadState
import app.chimahon.shared.CHIMAHON_CHAPTER_BOOKMARKED_MASK
import app.chimahon.shared.CHIMAHON_CHAPTER_DISPLAY_MASK
import app.chimahon.shared.CHIMAHON_CHAPTER_DISPLAY_NUMBER
import app.chimahon.shared.CHIMAHON_CHAPTER_DOWNLOADED_MASK
import app.chimahon.shared.ChimahonMangaEntry
import app.chimahon.shared.ChimahonRecentUpdateEntry
import app.chimahon.shared.ChimahonRemoteChapterEntry
import app.chimahon.shared.ChimahonRemoteMangaDetail
import app.chimahon.shared.CHIMAHON_CHAPTER_SHOW_BOOKMARKED
import app.chimahon.shared.CHIMAHON_CHAPTER_SHOW_DOWNLOADED
import app.chimahon.shared.CHIMAHON_CHAPTER_SHOW_NOT_BOOKMARKED
import app.chimahon.shared.CHIMAHON_CHAPTER_SHOW_NOT_DOWNLOADED
import app.chimahon.shared.CHIMAHON_CHAPTER_SHOW_READ
import app.chimahon.shared.CHIMAHON_CHAPTER_SHOW_UNREAD
import app.chimahon.shared.CHIMAHON_CHAPTER_SORTING_ALPHABET
import app.chimahon.shared.CHIMAHON_CHAPTER_SORTING_MASK
import app.chimahon.shared.CHIMAHON_CHAPTER_SORTING_NUMBER
import app.chimahon.shared.CHIMAHON_CHAPTER_SORTING_UPLOAD_DATE
import app.chimahon.shared.CHIMAHON_CHAPTER_SORT_DESC
import app.chimahon.shared.CHIMAHON_CHAPTER_SORT_DIR_MASK
import app.chimahon.shared.CHIMAHON_CHAPTER_UNREAD_MASK

fun ChimahonMangaEntry.toMangaHeaderUiModel(
    sourceName: String,
    sourceIncognito: Boolean = false,
    localSource: Boolean = false,
    trackedCount: Int = 0,
    nextUpdateLabel: String? = null,
    unreadCount: Int = 0,
    downloadedCount: Int = 0,
    totalChapterCount: Int = 0,
): ChimahonMangaHeaderUiModel {
    return ChimahonMangaHeaderUiModel(
        id = id.toString(),
        title = title,
        sourceName = sourceName,
        thumbnailUrl = thumbnailUrl,
        author = author,
        artist = artist,
        status = status,
        description = description,
        tags = genres,
        favorite = favorite,
        trackedCount = trackedCount,
        nextUpdateLabel = nextUpdateLabel,
        inLibrary = favorite,
        sourceIncognito = sourceIncognito,
        localSource = localSource,
        webUrl = url,
        unreadCount = unreadCount,
        downloadedCount = downloadedCount,
        totalChapterCount = totalChapterCount,
    )
}

fun ChimahonRemoteMangaDetail.toMangaHeaderUiModel(
    inLibrary: Boolean = false,
): ChimahonMangaHeaderUiModel {
    return ChimahonMangaHeaderUiModel(
        id = "$sourceId:$url",
        title = title,
        sourceName = sourceName,
        thumbnailUrl = thumbnailUrl,
        author = author,
        artist = artist,
        status = status,
        description = description,
        tags = genres,
        favorite = inLibrary,
        inLibrary = inLibrary,
        webUrl = url,
        totalChapterCount = chapters.size,
    )
}

fun ChimahonChapterEntry.toMangaChapterUiModel(
    sourceName: String? = null,
    dateLabel: String? = null,
    readProgressLabel: String? = null,
    downloadStatus: ChimahonChapterDownloadStatus? = null,
    isOcrReady: Boolean = false,
    isOcrRunning: Boolean = false,
): ChimahonMangaChapterUiModel {
    return ChimahonMangaChapterUiModel(
        id = id.toString(),
        title = name,
        url = url,
        sourceOrder = sourceOrder.toMangaIntOrMax(),
        chapterNumber = chapterNumber,
        dateUploadEpochMillis = dateUpload.takeIf { it > 0L },
        dateLabel = dateLabel,
        readProgressLabel = readProgressLabel,
        scanlator = scanlator,
        sourceName = sourceName,
        read = read,
        bookmarked = bookmarked,
        downloaded = downloadStatus?.status == ChimahonDownloadState.Downloaded,
        downloadState = downloadStatus.toMangaDownloadUiState(),
        downloadProgress = downloadStatus?.progress ?: 0,
        isOcrReady = isOcrReady,
        isOcrRunning = isOcrRunning,
    )
}

fun ChimahonRemoteChapterEntry.toMangaChapterUiModel(
    sourceName: String? = null,
    dateLabel: String? = null,
): ChimahonMangaChapterUiModel {
    return ChimahonMangaChapterUiModel(
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

fun List<ChimahonChapterEntry>.toMangaChapterUiModels(
    sourceName: String? = null,
    dateLabelsByChapterId: Map<Long, String> = emptyMap(),
    readProgressLabelsByChapterId: Map<Long, String> = emptyMap(),
    downloadStatusesByChapterId: Map<Long, ChimahonChapterDownloadStatus> = emptyMap(),
    ocrReadyChapterIds: Set<Long> = emptySet(),
    ocrRunningChapterIds: Set<Long> = emptySet(),
): List<ChimahonMangaChapterUiModel> {
    return map { chapter ->
        chapter.toMangaChapterUiModel(
            sourceName = sourceName,
            dateLabel = dateLabelsByChapterId[chapter.id],
            readProgressLabel = readProgressLabelsByChapterId[chapter.id],
            downloadStatus = downloadStatusesByChapterId[chapter.id],
            isOcrReady = chapter.id in ocrReadyChapterIds,
            isOcrRunning = chapter.id in ocrRunningChapterIds,
        )
    }
}

fun ChimahonMangaEntry.toMangaDetailUiState(
    sourceName: String,
    chapters: List<ChimahonChapterEntry>,
    downloadStatusesByChapterId: Map<Long, ChimahonChapterDownloadStatus> = emptyMap(),
    selectedChapterIds: Set<Long> = emptySet(),
    sortState: ChimahonMangaChapterSortState? = null,
    filterState: ChimahonMangaChapterFilterState? = null,
    displayMode: ChimahonMangaChapterDisplayMode? = null,
    chapterQuery: String = "",
    refreshing: Boolean = false,
    missingChapterCount: Int = 0,
    sourceIncognito: Boolean = false,
    localSource: Boolean = false,
    trackedCount: Int = 0,
    nextUpdateLabel: String? = null,
): ChimahonMangaDetailUiState {
    val downloadedCount = chapters.count { downloadStatusesByChapterId[it.id]?.status == ChimahonDownloadState.Downloaded }
    val unreadCount = chapters.count { !it.read }
    return ChimahonMangaDetailUiState(
        manga = toMangaHeaderUiModel(
            sourceName = sourceName,
            sourceIncognito = sourceIncognito,
            localSource = localSource,
            trackedCount = trackedCount,
            nextUpdateLabel = nextUpdateLabel,
            unreadCount = unreadCount,
            downloadedCount = downloadedCount,
            totalChapterCount = chapters.size,
        ),
        chapters = chapters.toMangaChapterUiModels(
            sourceName = sourceName,
            downloadStatusesByChapterId = downloadStatusesByChapterId,
        ),
        sortState = sortState ?: defaultMangaSortState(),
        filterState = filterState ?: defaultMangaFilterState(),
        displayMode = displayMode ?: defaultMangaDisplayMode(),
        chapterQuery = chapterQuery,
        selectedChapterIds = selectedChapterIds.map { it.toString() }.toSet(),
        refreshing = refreshing,
        missingChapterCount = missingChapterCount,
    )
}

fun ChimahonRemoteMangaDetail.toMangaDetailUiState(
    inLibrary: Boolean = false,
    sortState: ChimahonMangaChapterSortState = ChimahonMangaChapterSortState(),
    filterState: ChimahonMangaChapterFilterState = ChimahonMangaChapterFilterState(),
    displayMode: ChimahonMangaChapterDisplayMode = ChimahonMangaChapterDisplayMode.SourceTitle,
    chapterQuery: String = "",
    refreshing: Boolean = false,
): ChimahonMangaDetailUiState {
    return ChimahonMangaDetailUiState(
        manga = toMangaHeaderUiModel(inLibrary = inLibrary),
        chapters = chapters.map { it.toMangaChapterUiModel(sourceName = sourceName) },
        sortState = sortState,
        filterState = filterState,
        displayMode = displayMode,
        chapterQuery = chapterQuery,
        refreshing = refreshing,
    )
}

fun ChimahonMangaEntry.defaultMangaSortState(): ChimahonMangaChapterSortState {
    return ChimahonMangaChapterSortState(
        sort = defaultMangaChapterSort(),
        descending = chapterFlags and CHIMAHON_CHAPTER_SORT_DIR_MASK == CHIMAHON_CHAPTER_SORT_DESC,
    )
}

fun ChimahonMangaEntry.defaultMangaFilterState(): ChimahonMangaChapterFilterState {
    return ChimahonMangaChapterFilterState(
        read = when {
            chapterFlags and CHIMAHON_CHAPTER_UNREAD_MASK == CHIMAHON_CHAPTER_SHOW_UNREAD ->
                ChimahonMangaReadFilter.Unread
            chapterFlags and CHIMAHON_CHAPTER_UNREAD_MASK == CHIMAHON_CHAPTER_SHOW_READ ->
                ChimahonMangaReadFilter.Read
            else -> ChimahonMangaReadFilter.Any
        },
        downloaded = when {
            chapterFlags and CHIMAHON_CHAPTER_DOWNLOADED_MASK == CHIMAHON_CHAPTER_SHOW_DOWNLOADED ->
                ChimahonMangaDownloadFilter.Downloaded
            chapterFlags and CHIMAHON_CHAPTER_DOWNLOADED_MASK == CHIMAHON_CHAPTER_SHOW_NOT_DOWNLOADED ->
                ChimahonMangaDownloadFilter.NotDownloaded
            else -> ChimahonMangaDownloadFilter.Any
        },
        bookmarked = when {
            chapterFlags and CHIMAHON_CHAPTER_BOOKMARKED_MASK == CHIMAHON_CHAPTER_SHOW_BOOKMARKED ->
                ChimahonMangaBookmarkFilter.Bookmarked
            chapterFlags and CHIMAHON_CHAPTER_BOOKMARKED_MASK == CHIMAHON_CHAPTER_SHOW_NOT_BOOKMARKED ->
                ChimahonMangaBookmarkFilter.NotBookmarked
            else -> ChimahonMangaBookmarkFilter.Any
        },
    )
}

fun ChimahonRecentUpdateEntry.toMangaUpdateInfoChip(
    label: String = "Updated",
): ChimahonMangaInfoChipModel {
    return ChimahonMangaInfoChipModel(
        label = label,
        kind = ChimahonMangaInfoChipKind.Warning,
    )
}

private fun ChimahonMangaEntry.defaultMangaChapterSort(): ChimahonMangaChapterSort {
    return when (chapterFlags and CHIMAHON_CHAPTER_SORTING_MASK) {
        CHIMAHON_CHAPTER_SORTING_NUMBER -> ChimahonMangaChapterSort.ChapterNumber
        CHIMAHON_CHAPTER_SORTING_UPLOAD_DATE -> ChimahonMangaChapterSort.UploadDate
        CHIMAHON_CHAPTER_SORTING_ALPHABET -> ChimahonMangaChapterSort.Name
        else -> ChimahonMangaChapterSort.SourceOrder
    }
}

private fun ChimahonMangaEntry.defaultMangaDisplayMode(): ChimahonMangaChapterDisplayMode {
    return if (chapterFlags and CHIMAHON_CHAPTER_DISPLAY_MASK == CHIMAHON_CHAPTER_DISPLAY_NUMBER) {
        ChimahonMangaChapterDisplayMode.ChapterNumber
    } else {
        ChimahonMangaChapterDisplayMode.SourceTitle
    }
}

private fun ChimahonChapterDownloadStatus?.toMangaDownloadUiState(): ChimahonChapterDownloadUiState {
    return when (this?.status) {
        ChimahonDownloadState.Queued -> ChimahonChapterDownloadUiState.Queued
        ChimahonDownloadState.Downloading -> ChimahonChapterDownloadUiState.Downloading
        ChimahonDownloadState.Paused -> ChimahonChapterDownloadUiState.Queued
        ChimahonDownloadState.Downloaded -> ChimahonChapterDownloadUiState.Downloaded
        ChimahonDownloadState.Error -> ChimahonChapterDownloadUiState.Error
        null -> ChimahonChapterDownloadUiState.NotDownloaded
    }
}

private fun Long.toMangaIntOrMax(): Int {
    return when {
        this > Int.MAX_VALUE -> Int.MAX_VALUE
        this < Int.MIN_VALUE -> Int.MIN_VALUE
        else -> toInt()
    }
}
