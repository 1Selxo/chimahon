package app.chimahon.shared.detailui

data class ChimahonDetailCollapsingHeaderMetadata(
    val id: String,
    val title: String,
    val subtitle: String,
    val sourceName: String,
    val thumbnailUrl: String? = null,
    val creatorLine: String? = null,
    val statusLabel: String? = null,
    val description: String? = null,
    val genres: List<String> = emptyList(),
    val summaryLabel: String = "",
    val badges: List<ChimahonDetailParityBadgeModel> = emptyList(),
    val actions: List<ChimahonDetailActionModel> = emptyList(),
    val inLibrary: Boolean = false,
    val initialized: Boolean = true,
    val sourceUrl: String? = null,
) {
    val collapsedSubtitle: String
        get() = listOf(sourceName, statusLabel.orEmpty())
            .filter(String::isNotBlank)
            .distinct()
            .joinToString(" / ")
            .ifBlank { subtitle }
}

data class ChimahonDetailCollapsingHeaderState(
    val expandedFraction: Float = 1f,
    val pinned: Boolean = false,
) {
    val normalizedExpandedFraction: Float
        get() = expandedFraction.coerceIn(0f, 1f)

    val collapsedFraction: Float
        get() = 1f - normalizedExpandedFraction

    val isCollapsed: Boolean
        get() = pinned || normalizedExpandedFraction <= 0.18f
}

data class ChimahonDetailParityBadgeModel(
    val label: String,
    val kind: ChimahonDetailParityBadgeKind = ChimahonDetailParityBadgeKind.Neutral,
    val contentDescription: String = label,
) {
    val stableKey: String
        get() = "detail-parity-badge:$kind:$label"
}

enum class ChimahonDetailParityBadgeKind {
    Status,
    Source,
    Creator,
    Library,
    ChapterCount,
    Unread,
    Bookmarked,
    Downloaded,
    Update,
    History,
    Warning,
    Genre,
    Url,
    Neutral,
}

data class ChimahonDetailChapterBadgeModel(
    val label: String,
    val kind: ChimahonDetailChapterBadgeKind = ChimahonDetailChapterBadgeKind.Neutral,
    val progressPercent: Int? = null,
    val contentDescription: String = label,
) {
    val stableKey: String
        get() = "detail-chapter-badge:$kind:$label:${progressPercent ?: -1}"
}

enum class ChimahonDetailChapterBadgeKind {
    Unread,
    Read,
    Started,
    Bookmarked,
    DownloadQueued,
    Downloading,
    Downloaded,
    DownloadError,
    Scanlator,
    Source,
    UploadDate,
    ChapterNumber,
    Neutral,
}

data class ChimahonDetailChapterStateModel(
    val read: Boolean = false,
    val bookmarked: Boolean = false,
    val downloaded: Boolean = false,
    val downloadState: ChimahonDetailChapterDownloadState = ChimahonDetailChapterDownloadState.NotDownloaded,
    val downloadProgress: Int = 0,
    val started: Boolean = false,
    val readProgressLabel: String? = null,
) {
    val unread: Boolean
        get() = !read

    val activeDownload: Boolean
        get() = downloaded || downloadState == ChimahonDetailChapterDownloadState.Downloaded
}

data class ChimahonDetailChapterBulkBarState(
    val selectedCount: Int = 0,
    val totalCount: Int = 0,
    val actions: List<ChimahonDetailChapterBulkActionModel> =
        defaultChimahonDetailChapterBulkActions(
            ChimahonDetailChapterSelectionState(selectedCount = selectedCount, totalCount = totalCount),
        ),
) {
    val hasSelection: Boolean
        get() = selectedCount > 0

    val title: String
        get() = if (hasSelection) "$selectedCount selected" else "$totalCount chapters"
}

data class ChimahonDetailActionRowState(
    val primaryAction: ChimahonDetailActionModel? = null,
    val actions: List<ChimahonDetailActionModel> = emptyList(),
    val overflowActions: List<ChimahonDetailRelatedActionModel> = emptyList(),
) {
    val visibleActions: List<ChimahonDetailActionModel>
        get() = buildList {
            primaryAction?.let(::add)
            actions.forEach { action ->
                if (primaryAction?.action != action.action) add(action)
            }
        }
}

fun ChimahonDetailChapterRowModel.stateModel(): ChimahonDetailChapterStateModel {
    return ChimahonDetailChapterStateModel(
        read = read,
        bookmarked = bookmarked,
        downloaded = downloaded,
        downloadState = downloadState,
        downloadProgress = downloadProgress,
        started = !read && !readProgressLabel.isNullOrBlank(),
        readProgressLabel = readProgressLabel,
    )
}

fun ChimahonDetailChapterRowModel.stateBadges(
    showReadState: Boolean = true,
    showDownloadState: Boolean = true,
    showBookmarkState: Boolean = true,
    showMetadata: Boolean = true,
): List<ChimahonDetailChapterBadgeModel> {
    val progressLabel = readProgressLabel?.takeIf(String::isNotBlank)
    return buildList {
        if (showReadState) {
            when {
                read -> add(ChimahonDetailChapterBadgeModel("Read", ChimahonDetailChapterBadgeKind.Read))
                progressLabel != null -> add(
                    ChimahonDetailChapterBadgeModel(
                        progressLabel,
                        ChimahonDetailChapterBadgeKind.Started,
                    ),
                )
                else -> add(ChimahonDetailChapterBadgeModel("Unread", ChimahonDetailChapterBadgeKind.Unread))
            }
        }
        if (showBookmarkState && bookmarked) {
            add(ChimahonDetailChapterBadgeModel("Bookmarked", ChimahonDetailChapterBadgeKind.Bookmarked))
        }
        if (showDownloadState) {
            downloadBadge()?.let(::add)
        }
        if (showMetadata) {
            if (chapterNumber > 0.0) {
                add(
                    ChimahonDetailChapterBadgeModel(
                        "Ch. ${chapterNumber.toParityDisplayChapter()}",
                        ChimahonDetailChapterBadgeKind.ChapterNumber,
                    ),
                )
            }
            dateLabel?.takeIf(String::isNotBlank)?.let {
                add(ChimahonDetailChapterBadgeModel(it, ChimahonDetailChapterBadgeKind.UploadDate))
            }
            scanlator?.takeIf(String::isNotBlank)?.let {
                add(ChimahonDetailChapterBadgeModel(it, ChimahonDetailChapterBadgeKind.Scanlator))
            }
            sourceName?.takeIf(String::isNotBlank)?.let {
                add(ChimahonDetailChapterBadgeModel(it, ChimahonDetailChapterBadgeKind.Source))
            }
        }
    }
}

fun ChimahonDetailChapterRowModel.downloadBadge(): ChimahonDetailChapterBadgeModel? {
    val downloadedNow = downloaded || downloadState == ChimahonDetailChapterDownloadState.Downloaded
    return when {
        downloadedNow -> ChimahonDetailChapterBadgeModel("Downloaded", ChimahonDetailChapterBadgeKind.Downloaded)
        downloadState == ChimahonDetailChapterDownloadState.Queued ->
            ChimahonDetailChapterBadgeModel("Queued", ChimahonDetailChapterBadgeKind.DownloadQueued)
        downloadState == ChimahonDetailChapterDownloadState.Downloading ->
            ChimahonDetailChapterBadgeModel(
                "${downloadProgress.coerceIn(0, 100)}%",
                ChimahonDetailChapterBadgeKind.Downloading,
                progressPercent = downloadProgress.coerceIn(0, 100),
                contentDescription = "Downloading ${downloadProgress.coerceIn(0, 100)} percent",
            )
        downloadState == ChimahonDetailChapterDownloadState.Error ->
            ChimahonDetailChapterBadgeModel("Download error", ChimahonDetailChapterBadgeKind.DownloadError)
        else -> null
    }
}

private fun Double.toParityDisplayChapter(): String {
    if (this % 1.0 == 0.0) return toLong().toString()
    return toString().trimEnd('0').trimEnd('.')
}
